/*
 * Copyright 2009-2011 Prime Technology.
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
import org.primefaces.component.accordionpanel.AccordionPanel;
import org.primefaces.component.tabview.Tab;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class AccordionPanelRenderer extends CoreRenderer {
    
    @Override
    public void decode(FacesContext context, UIComponent component) {        
        decodeBehaviors(context, component);
    }   

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        AccordionPanel acco = (AccordionPanel) component;
        encodeMarkup(context, acco);
        encodeScript(context, acco);
    }
           
    protected void encodeMarkup(FacesContext context, AccordionPanel acco) throws IOException {
        ResponseWriter writer = context.getResponseWriter();        
        Map<String, Object> attrs = acco.getAttributes();
        Object swatch = (String) attrs.get("swatch");
        String contentSwatch = (String) attrs.get("contentSwatch");
        Boolean inset = (acco.getAttributes().get("inset") == null ? true : Boolean.valueOf(acco.getAttributes().get("inset").toString()));
        String var = acco.getVar();
        boolean multiple = acco.isMultiple();
        String activeIndex = acco.getActiveIndex();

        writer.startElement("div", acco);
        writer.writeAttribute("id", acco.getClientId(context), null);
        if (!multiple) writer.writeAttribute("data-role", "collapsible-set", null);
        if(acco.getStyle() != null) writer.writeAttribute("style", acco.getStyle(), null);
        if(acco.getStyleClass() != null) writer.writeAttribute("class", acco.getStyleClass(), null);        
        if(swatch != null) writer.writeAttribute("data-theme", swatch, null); 
        if(contentSwatch != null) writer.writeAttribute("data-content-theme", contentSwatch, null);                 
        if (var == null) {
            int i = 0;
            for(UIComponent child : acco.getChildren()) {
                if(child.isRendered() && child instanceof Tab) {
                    boolean active = multiple ? activeIndex.indexOf(String.valueOf(i)) != -1 : activeIndex.equals(String.valueOf(i));

                    encodeTab(context, (Tab) child, active, inset);

                    i++;
                }
            }
        } else {
            int dataCount = acco.getRowCount();
            Tab tab = (Tab) acco.getChildren().get(0);

            for (int i = 0; i < dataCount; i++) {
                acco.setRowIndex(i);
                boolean active = multiple ? activeIndex.indexOf(String.valueOf(i)) != -1 : activeIndex.equals(String.valueOf(i));

                encodeTab(context, tab, active, inset);
            }

            acco.setRowIndex(-1);
        }

        writer.endElement("div");
    }
    
    protected void encodeScript(FacesContext context, AccordionPanel acco) throws IOException {
        String clientId = acco.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("AccordionPanel", acco.resolveWidgetVar(), clientId);

        wb.callback("onTabChange", "function(panel)", acco.getOnTabChange());

        encodeClientBehaviors(context, acco);

        wb.finish();
    }  
    
    protected void encodeTab(FacesContext context, Tab tab, boolean active,boolean inset) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        String title = tab.getTitle();

        writer.startElement("div", null);
        writer.writeAttribute("id", tab.getClientId(context), null);
        writer.writeAttribute("data-role", "collapsible", null);
        writer.writeAttribute("data-inset", Boolean.toString(inset), null);

        if (active) {
            writer.writeAttribute("data-collapsed", "false", null);
        }

        //header
        writer.startElement("h3", null);
        if (title != null) {
            writer.writeText(title, null);
        }
        writer.endElement("h3");

        //content
        if (inset) writer.startElement("p", null);
        tab.encodeAll(context);
        if (inset) writer.endElement("p");

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
