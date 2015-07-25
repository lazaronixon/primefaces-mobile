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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.growl.Growl;
import org.primefaces.context.RequestContext;
import org.primefaces.renderkit.UINotificationRenderer;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class GrowlRenderer extends UINotificationRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Growl uiGrowl = (Growl) component;
              
        encodeMarkup(context, uiGrowl);                              
    }
    
    protected void encodeMarkup(FacesContext context, Growl uiGrowl) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = uiGrowl.getClientId(context);
        String widgetVar = uiGrowl.resolveWidgetVar();
        Map<String, List<FacesMessage>> messagesMap = new HashMap<String, List<FacesMessage>>();

        String _for = uiGrowl.getFor();
        Iterator<FacesMessage> messages;
        if (_for != null) {
            messages = context.getMessages(_for);
        } else {
            messages = uiGrowl.isGlobalOnly() ? context.getMessages(null) : context.getMessages();
        }

        while (messages.hasNext()) {
            FacesMessage message = messages.next();
            FacesMessage.Severity severity = message.getSeverity();

            if (severity.equals(FacesMessage.SEVERITY_INFO)) {
                addMessage(uiGrowl, message, messagesMap, "info");
            } else if (severity.equals(FacesMessage.SEVERITY_WARN)) {
                addMessage(uiGrowl, message, messagesMap, "warn");
            } else if (severity.equals(FacesMessage.SEVERITY_ERROR)) {
                addMessage(uiGrowl, message, messagesMap, "error");
            } else if (severity.equals(FacesMessage.SEVERITY_FATAL)) {
                addMessage(uiGrowl, message, messagesMap, "fatal");
            }
        }

        writer.startElement("div", uiGrowl);
        writer.writeAttribute("id", clientId, "id");
        if(RequestContext.getCurrentInstance().getApplicationContext().getConfig().isClientSideValidationEnabled()) {
            writer.writeAttribute("class", "ui-growl-pl", null);
            writer.writeAttribute(HTML.WIDGET_VAR, widgetVar, null);
            writer.writeAttribute("data-global", uiGrowl.isGlobalOnly(), null);
            writer.writeAttribute("data-summary", uiGrowl.isShowSummary(), null);
            writer.writeAttribute("data-detail", uiGrowl.isShowDetail(), null);
        }        
        writer.startElement("div", null);
        writer.writeAttribute("id", clientId + "_popup", "id");
        writer.writeAttribute("data-role", "popup", null);
        writer.writeAttribute("data-transition", "fade", null);
        writer.writeAttribute("data-theme", "a", null);               
        writer.endElement("div");
        writer.endElement("div");
        
        encodeScript(context, uiGrowl);

    }
    
    protected void encodeScript(FacesContext context, Growl uiGrowl) throws IOException {
        String clientId = uiGrowl.getClientId(context);        
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("Growl", uiGrowl.resolveWidgetVar(), clientId+"_popup");
        wb.attr("life", uiGrowl.getLife())
                .attr("sticky", uiGrowl.isSticky());
        
        context.getResponseWriter().write(",msgs:");
        encodeMessages(context, uiGrowl);        
        wb.finish();
    }

    protected void addMessage(Growl uiGrowl, FacesMessage message, Map<String, List<FacesMessage>> messagesMap, String severity) {
        if (shouldRender(uiGrowl, message, severity)) {
            List<FacesMessage> severityMessages = messagesMap.get(severity);

            if (severityMessages == null) {
                severityMessages = new ArrayList<FacesMessage>();
                messagesMap.put(severity, severityMessages);
            }

            severityMessages.add(message);
        }
    }
    
    protected void encodeMessages(FacesContext context, Growl growl) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String _for = growl.getFor();
        Iterator<FacesMessage> messages;
        if(_for != null) {
            messages = context.getMessages(_for);
        }
        else {
            messages = growl.isGlobalOnly() ? context.getMessages(null) : context.getMessages();
        }
        
        writer.write("[");

		while(messages.hasNext()) {
			FacesMessage message = messages.next();      
            String severityName = getSeverityName(message);                                     
            
            if(shouldRender(growl, message, severityName)) {
                String summary = escapeText(message.getSummary());
                String detail = escapeText(message.getDetail());
            
                writer.write("{");

                if(growl.isShowSummary() && growl.isShowDetail())
                    writer.writeText("summary:\"" + summary + "\",detail:\"" + detail + "\"", null);
                else if(growl.isShowSummary() && !growl.isShowDetail())
                    writer.writeText("summary:\"" + summary + "\",detail:\"\"", null);
                else if(!growl.isShowSummary() && growl.isShowDetail())
                    writer.writeText("summary:\"\",detail:\"" + detail + "\"", null);

                writer.write(",severity:'" + severityName + "'");

                writer.write("}");

                if(messages.hasNext())
                    writer.write(",");

                message.rendered();
            }
		}

        writer.write("]");
    }    
}