/*
 * Copyright 2009-2012 Prime Teknoloji.
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
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.overlaypanel.OverlayPanel;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class OverlayPanelRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        OverlayPanel panel = (OverlayPanel) component;

        encodeMarkup(context, panel);
        encodeScript(context, panel);
    }

    protected void encodeMarkup(FacesContext context, OverlayPanel panel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = panel.getClientId(context);     
        Map<String, Object> attrs = panel.getAttributes();
        String swatch = (String) attrs.get("swatch");
        Object swipeClose = (String) attrs.get("swipeClose");        
        
        writer.startElement("div", panel);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("data-role", "panel", null);       

        if (panel.getMy() != null)writer.writeAttribute("data-position", panel.getMy(), null);                
        if (swatch != null) writer.writeAttribute("data-theme", swatch, null);            
        if (swipeClose != null && Boolean.valueOf(swipeClose.toString())) writer.writeAttribute("data-swipe-close", "true", null);                
        if (!panel.isDismissable()) writer.writeAttribute("data-dismissible", "false", null);                

        
        if (panel.getStyleClass() != null) {
            writer.writeAttribute("class", panel.getStyleClass(), "styleClass");
        }

        if (panel.getStyle() != null) {
            writer.writeAttribute("style", panel.getStyle(), "style");
        }

        renderChildren(context, panel);


        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, OverlayPanel panel) throws IOException {        
        UIComponent target = SearchExpressionFacade.resolveComponent(context, panel, panel.getFor());
        if (target == null) {
            throw new FacesException("Cannot find component '" + panel.getFor() + "' in view.");
        }

        String clientId = panel.getClientId(context);
        String targetClientId = target.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("OverlayPanel", panel.resolveWidgetVar(), clientId);

        wb.attr("target", targetClientId)
                .attr("showEvent", panel.getShowEvent(), null)                
                .callback("onShow", "function()", panel.getOnShow())
                .callback("onHide", "function()", panel.getOnHide());      

        wb.finish();
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
