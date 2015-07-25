/*
 * Copyright 2009-2013 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.mobile.renderkit;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;
import javax.servlet.ServletRequestWrapper;
import org.apache.commons.fileupload.FileItem;
import org.primefaces.component.fileupload.FileUpload;
import org.primefaces.model.DefaultUploadedFile;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.webapp.MultipartRequest;

public class FileUploadRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        FileUpload fileUpload = (FileUpload) component;
        String clientId = fileUpload.getClientId(context);
        MultipartRequest multipartRequest = getMultiPartRequestInChain(context);

        if (multipartRequest != null) {
            FileItem file = multipartRequest.getFileItem(clientId);

            if (fileUpload.getMode().equals("simple")) {
                decodeSimple(context, fileUpload, file);
            }
        }
    }

    public void decodeSimple(FacesContext context, FileUpload fileUpload, FileItem file) {
        if (file.getName().equals("")) {
            fileUpload.setSubmittedValue("");
        } else {
            fileUpload.setSubmittedValue(new DefaultUploadedFile(file));
        }
    }

    /**
     * Finds our MultipartRequestServletWrapper in case application contains
     * other RequestWrappers
     */
    private MultipartRequest getMultiPartRequestInChain(FacesContext facesContext) {
        Object request = facesContext.getExternalContext().getRequest();

        while (request instanceof ServletRequestWrapper) {
            if (request instanceof MultipartRequest) {
                return (MultipartRequest) request;
            } else {
                request = ((ServletRequestWrapper) request).getRequest();
            }
        }

        return null;
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        FileUpload fileUpload = (FileUpload) component;

        encodeMarkup(context, fileUpload);
    }

    protected void encodeMarkup(FacesContext context, FileUpload fileUpload) throws IOException {
        if (fileUpload.getMode().equals("simple")) {
            ResponseWriter writer = context.getResponseWriter();
            String clientId = fileUpload.getClientId(context);

            writer.startElement("input", null);
            writer.writeAttribute("type", "file", null);
            writer.writeAttribute("id", clientId, null);
            writer.writeAttribute("name", clientId, null);

            if (fileUpload.getStyle() != null) {
                writer.writeAttribute("style", fileUpload.getStyle(), "style");
            }
            if (fileUpload.getStyleClass() != null) {
                writer.writeAttribute("class", fileUpload.getStyleClass(), "styleClass");
            }
            if (fileUpload.isDisabled()) {
                writer.writeAttribute("disabled", "disabled", "disabled");
            }

            writer.endElement("input");
        }
    }

    /**
     * Return null if no file is submitted in simple mode
     *
     * @param context
     * @param component
     * @param submittedValue
     * @return
     * @throws ConverterException
     */
    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        FileUpload fileUpload = (FileUpload) component;

        if (fileUpload.getMode().equals("simple") && submittedValue != null && submittedValue.equals("")) {
            return null;
        } else {
            return submittedValue;
        }
    }
}