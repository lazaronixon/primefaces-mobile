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
import org.primefaces.component.dialog.Dialog;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class DialogRenderer extends CoreRenderer {
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Dialog dialog = (Dialog) component;
        
        encodeMarkup(context, dialog);
        encodeScript(context, dialog);
    }

    protected void encodeScript(FacesContext context, Dialog dialog) throws IOException {
        String clientId = dialog.getClientId(context);        
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("Dialog", dialog.resolveWidgetVar(), clientId);
        
        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, Dialog dialog) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = dialog.getClientId(context);
        String style = dialog.getStyle();
        String styleClass = dialog.getStyleClass();
        String header = dialog.getHeader();
        
        Map<String, Object> attrs = dialog.getAttributes();
        String contentSwatch = (String) attrs.get("contentSwatch");
        String headerSwatch = (String) attrs.get("headerSwatch");
        
        writer.startElement("div", dialog);
        writer.writeAttribute("id", clientId, "id");
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }
        if(styleClass != null) {
            writer.writeAttribute("class", styleClass, null);
        }
        
        writer.writeAttribute("data-role", "popup", null);
        
        if(dialog.isModal()) {
            writer.writeAttribute("data-overlay-theme", "a", null);
            writer.writeAttribute("data-dismissible", "false", null);
        }
        
        if(dialog.getPosition() != null) {
            writer.writeAttribute("data-position-to", dialog.getPosition(), null);
        }
        
        if(dialog.getShowEffect() != null) {
            writer.writeAttribute("data-transition", dialog.getShowEffect(), null);
        }
        
        if(header != null) {
            writer.startElement("div", null);
            writer.writeAttribute("data-role", "header", null);
            writer.writeAttribute("class", "ui-corner-top", null);
            writer.startElement("h1", null);
            if(headerSwatch != null) {
                writer.writeAttribute("data-theme", headerSwatch, null);
            }
            writer.writeText(header, null);
            writer.endElement("h1");
            
            if(dialog.isClosable()) {
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
        
        writer.startElement("div", null);
        writer.writeAttribute("data-role", "content", null);
        writer.writeAttribute("class", "ui-corner-bottom ui-content", null);
        if(contentSwatch != null) {
            writer.writeAttribute("data-theme", contentSwatch, null);
        }
        renderChildren(context, dialog);
        writer.endElement("div");
        
        writer.endElement("div");
    }
    
    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }    
}
