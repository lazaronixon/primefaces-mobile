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
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.confirmdialog.ConfirmDialog;
import org.primefaces.component.dialog.Dialog;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class ConfirmDialogRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ConfirmDialog dialog = (ConfirmDialog) component;

        encodeScript(context, dialog);
        encodeMarkup(context, dialog);
    }

    protected void encodeMarkup(FacesContext context, ConfirmDialog dialog) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = dialog.getClientId(context);
        String style = dialog.getStyle();
        String styleClass = dialog.getStyleClass();

        Map<String, Object> attrs = dialog.getAttributes();
        String contentSwatch = (String) attrs.get("contentSwatch");
        String headerSwatch = (String) attrs.get("headerSwatch");

        writer.startElement("div", dialog);
        writer.writeAttribute("id", clientId, "id");
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }
        if (styleClass != null) {
            writer.writeAttribute("class", styleClass, null);
        }

        writer.writeAttribute("data-role", "popup", null);
        writer.writeAttribute("data-overlay-theme", "a", null);
        writer.writeAttribute("data-dismissible", "false", null);

        if (dialog.getShowEffect() != null) {
            writer.writeAttribute("data-transition", dialog.getShowEffect(), null);
        }

        encodeHeader(context, dialog, headerSwatch);
        encodeContent(context, dialog, contentSwatch);

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, ConfirmDialog dialog) throws IOException {
        String clientId = dialog.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("ConfirmDialog", dialog.resolveWidgetVar(), clientId)
        .attr("global", dialog.isGlobal(), false);

        wb.finish();
    }

    protected void encodeHeader(FacesContext context, ConfirmDialog dialog, String headerSwatch) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String header = dialog.getHeader();

        writer.startElement("div", null);
        writer.writeAttribute("data-role", "header", null);
        writer.writeAttribute("class", "ui-corner-top "+Dialog.TITLE_BAR_CLASS, null);
        
        writer.startElement("h1", null);
        writer.writeAttribute("class", Dialog.TITLE_CLASS, null); 
        if (headerSwatch != null) {
            writer.writeAttribute("data-theme", headerSwatch, null);
        }       
        if (header != null) {
            writer.writeText(header, null);
        }
        writer.endElement("h1");

        if (dialog.isClosable()) {
            writer.startElement("a", dialog);
            writer.writeAttribute("href", "#", null);
            writer.writeAttribute("data-rel", "back", null);
            writer.writeAttribute("data-role", "button", null);
            writer.writeAttribute("data-theme", "a", null);
            writer.writeAttribute("data-icon", "delete", null);
            writer.writeAttribute("data-iconpos", "notext", null);
            writer.writeAttribute("data-class", "ui-btn-left", null);
            writer.endElement("a");
        }

        writer.endElement("div");
    }

    protected void encodeContent(FacesContext context, ConfirmDialog dialog, String contentSwatch) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String messageText = dialog.getMessage();
        UIComponent messageFacet = dialog.getFacet("message");
        String severityIcon = dialog.getSeverity();


        writer.startElement("div", null);
        writer.writeAttribute("data-role", "content", null);
        writer.writeAttribute("class", "ui-corner-bottom ui-content "+Dialog.CONTENT_CLASS, null);
        if (contentSwatch != null) {
            writer.writeAttribute("data-theme", contentSwatch, null);
        }

        //severity
        String icon = null;

        if (severityIcon.equals("info")) icon = "info";
        if (severityIcon.equals("alert")) icon = "alert";            
        if (severityIcon.equals("error")) icon = "delete";            
        if (severityIcon.equals("fatal")) icon = "minus";        
        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-icon "+ConfirmDialog.SEVERITY_ICON_CLASS+" ui-icon-" + icon, null);
        writer.writeAttribute("style", "float: left;margin: 7px 5px;", null);
        writer.endElement("span");

        writer.startElement("p", null);
        writer.writeAttribute("class", ConfirmDialog.MESSAGE_CLASS, null);       
        if (messageFacet != null) {
            messageFacet.encodeAll(context);
        } else if (messageText != null) {
            writer.writeText(messageText, null);
        }
        writer.endElement("p");        
        
        renderChildren(context, dialog);

        writer.endElement("div");
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Do Nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
