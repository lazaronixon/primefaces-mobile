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
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectMany;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import org.primefaces.component.selectmanymenu.SelectManyMenu;
import org.primefaces.mobile.util.MobileUtils;
import org.primefaces.renderkit.SelectManyRenderer;

public class SelectManyMenuRenderer extends SelectManyRenderer {        
    
    @Override
	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        return context.getRenderKit().getRenderer("javax.faces.SelectMany", "javax.faces.Menu").getConvertedValue(context, component, submittedValue);
	}

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        SelectManyMenu menu = (SelectManyMenu) component;

        encodeMarkup(context, menu);        
    }

    protected void encodeMarkup(FacesContext context, SelectManyMenu menu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = menu.getClientId(context);
        String label = menu.getLabel();
        String inputId = clientId + "_input";        
        
        if (label == null) {
            encodeInput(context, menu, clientId);
        } else {
            writer.startElement("div", menu);
            writer.writeAttribute("id", clientId, null);
            writer.writeAttribute("data-role", "fieldcontain", null);       
                        
            writer.startElement("label", null);
            writer.writeAttribute("for", inputId, null);
            writer.writeText(label, "label");
            writer.endElement("label");
            
            encodeInput(context, menu, inputId);
            
            writer.endElement("div");                        
        }
             
    }   
    
    protected void encodeInput(FacesContext context, SelectManyMenu menu, String inputId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();  
        List<SelectItem> selectItems = getSelectItems(context, menu);       
        String style = menu.getStyle();       
        writer.startElement("select", null);
        writer.writeAttribute("id", inputId, "id");
        writer.writeAttribute("name", inputId, null);       
        writer.writeAttribute("multiple", "multiple", null);
        writer.writeAttribute("data-native-menu", "false", null);
        if(MobileUtils.isMini(context)) writer.writeAttribute("data-mini", "true", null);        
        if(style != null) writer.writeAttribute("style", style, "style");
        writer.writeAttribute("class", createStyleClass(menu), "styleClass");                 
        
        if(menu.isDisabled()) writer.writeAttribute("disabled", "disabled", null);        
        if(menu.getTabindex() != null) writer.writeAttribute("tabindex", menu.getTabindex(), null);
        if(menu.getOnchange() != null) writer.writeAttribute("onchange", menu.getOnchange(), null);

        encodeSelectItems(context, menu, selectItems);

        writer.endElement("select");           
    }

    protected void encodeSelectItems(FacesContext context, SelectManyMenu menu, List<SelectItem> selectItems) throws IOException {
        Converter converter = menu.getConverter();
        Object values = getValues(menu);
        Object submittedValues = getSubmittedValues(menu);
        
        for(SelectItem selectItem : selectItems) {
            encodeOption(context, menu, selectItem, values, submittedValues, converter);
        }
    }
    
    protected void encodeOption(FacesContext context, SelectManyMenu menu, SelectItem option, Object values, Object submittedValues, Converter converter) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        if (option instanceof SelectItemGroup) {
            SelectItemGroup group = (SelectItemGroup) option;
            writer.startElement("optgroup", null);
            writer.writeAttribute("label", group.getLabel(), null);
            for (SelectItem groupItem : group.getSelectItems()) {                
                encodeOption(context, menu, groupItem, values, submittedValues, converter);
            }
            writer.endElement("optgroup");
        } else {
            String itemValueAsString = getOptionAsString(context, menu, converter, option.getValue());
            boolean disabled = option.isDisabled() || menu.isDisabled();

            Object valuesArray;
            Object itemValue;
            if (submittedValues != null) {
                valuesArray = submittedValues;
                itemValue = itemValueAsString;
            } else {
                valuesArray = values;
                itemValue = option.getValue();
            }

            boolean selected = isSelected(context, menu, itemValue, valuesArray, converter);

            writer.startElement("option", null);
            writer.writeAttribute("value", itemValueAsString, null);
            if (disabled) writer.writeAttribute("disabled", "disabled", null);
            if (selected) writer.writeAttribute("selected", "selected", null);
            writer.write(option.getLabel());

            writer.endElement("option");
        }
    }
    
    protected String createStyleClass(SelectManyMenu menu) {
        String defaultClass = "";
        defaultClass = menu.isValid() ? defaultClass : defaultClass + " ui-focus";
        
        String styleClass = menu.getStyleClass();
        styleClass = styleClass == null ? defaultClass : defaultClass + " " + styleClass;
        
        return styleClass;
    }        

    @Override
    protected String getSubmitParam(FacesContext context, UISelectMany selectMany) {
        SelectManyMenu menu = (SelectManyMenu) selectMany;
        String clientId = menu.getClientId(context);
        String label = menu.getLabel();
        String inputId = clientId + "_input";         
        return (label == null) ? clientId : inputId;
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
