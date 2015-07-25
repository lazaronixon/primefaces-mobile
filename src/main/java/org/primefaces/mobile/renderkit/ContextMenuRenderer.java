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

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.contextmenu.ContextMenu;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.renderkit.CoreRenderer;

import org.primefaces.util.WidgetBuilder;

public class ContextMenuRenderer extends CoreRenderer {        

    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ContextMenu menu = (ContextMenu) component;
        
        encodeMarkup(context, menu);
        encodeScript(context, menu);
    }    
    
    protected void encodeScript(FacesContext context, ContextMenu menu) throws IOException {
        String clientId = menu.getClientId(context);        
        UIComponent target = SearchExpressionFacade.resolveComponent(context, menu, menu.getFor());

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("ContextMenu", menu.resolveWidgetVar(), clientId);

        if (target != null) {
            wb.attr("target", target.getClientId(context))
                    .attr("type", target.getClass().getSimpleName());
        }

        wb.attr("event", menu.getEvent(), null)
                .attr("hasContent", (menu.getChildCount() > 0))
                .callback("beforeShow", "function()", menu.getBeforeShow());

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, ContextMenu menu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();       
        String clientId = menu.getClientId(context);
        String style = menu.getStyle();
        String styleClass = menu.getStyleClass();        

        writer.startElement("div", menu);
        writer.writeAttribute("id", clientId, "id");
        
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }
        if(styleClass != null) {
            writer.writeAttribute("class", styleClass, null);
        }        

        writer.writeAttribute("data-role", "popup", null);
        writer.writeAttribute("data-transition", "fade", null);
                
        renderChildren(context, menu);
        
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
