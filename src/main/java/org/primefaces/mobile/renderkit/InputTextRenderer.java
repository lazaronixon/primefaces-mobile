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
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.mobile.util.MobileUtils;
import org.primefaces.util.WidgetBuilder;

public class InputTextRenderer extends InputRenderer {

    @Override
	public void decode(FacesContext context, UIComponent component) {
		InputText inputText = (InputText) component;
        String clientId = inputText.getClientId(context);
        String inputId = inputText.getLabel() == null ? clientId : clientId + "_input";

        if(inputText.isDisabled() || inputText.isReadonly()) {
            return;
        }

        decodeBehaviors(context, inputText);

		String submittedValue = (String) context.getExternalContext().getRequestParameterMap().get(inputId);

        if(submittedValue != null) {
            inputText.setSubmittedValue(submittedValue);
        }
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		InputText inputText = (InputText) component;

		encodeMarkup(context, inputText);
        encodeScript(context, inputText);
	}

    protected void encodeScript(FacesContext context, InputText inputText) throws IOException {
        String clientId = inputText.getClientId(context);

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("InputText", inputText.resolveWidgetVar(), clientId);
        
        encodeClientBehaviors(context, inputText);        

        wb.finish();
    }

	protected void encodeMarkup(FacesContext context, InputText inputText) throws IOException {
	ResponseWriter writer = context.getResponseWriter();
	String clientId = inputText.getClientId(context);
        String label = inputText.getLabel();
        String inputId = inputText.getLabel() == null ? clientId : clientId + "_input";

        if(label == null) {
            encodeInput(context, inputText, clientId);            
        } 
        else {
            writer.startElement("div", inputText);
            writer.writeAttribute("id", clientId, null);
            writer.writeAttribute("data-role", "fieldcontain", null);       
                        
            writer.startElement("label", null);
            writer.writeAttribute("for", inputId, null);
            writer.writeText(label, "label");
            writer.endElement("label");
            
            encodeInput(context, inputText, inputId);
            
            writer.endElement("div");
        }
	}
     
    protected void encodeInput(FacesContext context, InputText inputText, String inputId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Map<String, Object> attrs = inputText.getAttributes();
        String placeholder = (String) attrs.get("placeholder");
        String type = inputText.getType();        

        writer.startElement("input", null);
		writer.writeAttribute("id", inputId, null);
		writer.writeAttribute("name", inputId, null);
		writer.writeAttribute("type", type, null);
                if (placeholder != null){
                    writer.writeAttribute("placeholder", placeholder, null);
                }                
        
		String valueToRender = ComponentUtils.getValueToRender(context, inputText);
		if(valueToRender != null) {
			writer.writeAttribute("value", valueToRender , null);
		}
                
		renderPassThruAttributes(context, inputText, HTML.INPUT_TEXT_ATTRS);

        if(MobileUtils.isMini(context)) writer.writeAttribute("data-mini", "true", null);
        if(inputText.isDisabled()) writer.writeAttribute("disabled", "disabled", null);
        if(inputText.isReadonly()) writer.writeAttribute("readonly", "readonly", null);
        if(inputText.getStyle() != null) writer.writeAttribute("style", inputText.getStyle(), null);
        writer.writeAttribute("class", createStyleClass(inputText), "styleClass");      
        
        writer.endElement("input");
    }
    
    protected String createStyleClass(InputText inputText) {
        String defaultClass = "";
        defaultClass = inputText.isValid() ? defaultClass : defaultClass + " ui-focus";
        
        String styleClass = inputText.getStyleClass();
        styleClass = styleClass == null ? defaultClass : defaultClass + " " + styleClass;
        
        return styleClass;
    }    
    
}
