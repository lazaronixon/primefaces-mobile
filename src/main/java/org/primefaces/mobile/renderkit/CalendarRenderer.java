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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import org.primefaces.component.calendar.Calendar;
import org.primefaces.component.calendar.CalendarUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;
import org.primefaces.renderkit.InputRenderer;

public class CalendarRenderer extends InputRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Calendar calendar = (Calendar) component;

        if (calendar.isDisabled() || calendar.isReadonly()) {
            return;
        }

        decodeBehaviors(context, calendar);

        String param = calendar.getClientId(context);
        String submittedValue = (String) context.getExternalContext().getRequestParameterMap().get(param);

        if (submittedValue != null) {
            calendar.setSubmittedValue(submittedValue);
        }


    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Calendar calendar = (Calendar) component;
        String markupValue = CalendarUtils.getValueAsString(context, calendar);
        String widgetValue = calendar.isTimeOnly() ? CalendarUtils.getTimeOnlyValueAsString(context, calendar) : markupValue;

        encodeMarkup(context, calendar, markupValue);
        encodeScript(context, calendar, widgetValue);
    }

    protected void encodeScript(FacesContext context, Calendar calendar, String value) throws IOException {
        String clientId = calendar.getClientId(context);
        Map<String, Object> attrs = calendar.getAttributes();
        String showOnFocus = (String) attrs.get("showOnFocus");

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("Calendar", calendar.resolveWidgetVar(), clientId);        
        Locale locale = calendar.calculateLocale(context);
        String pattern = calendar.isTimeOnly() ? calendar.calculateTimeOnlyPattern() : calendar.calculatePattern();

        String mode = calendar.getMode();

        if (mode.equals("popup")) {
            mode = "modal";
        }

        //default date        
        String defaultDate = null;

        if (calendar.isConversionFailed()) {
            defaultDate = CalendarUtils.getValueAsString(context, calendar, new Date());
        } else if (!isValueBlank(value)) {
            defaultDate = value;
        }

        String patternMobDateTime = convertPatternMobile(pattern);
        String patternMobDate = truncDatePattern(patternMobDateTime);

        wb.attr("defaultDate", defaultDate)
                .attr("display", mode)
                .attr("lang", locale.toString())
                .attr("pattern", patternMobDateTime)
                .attr("dateFormat", patternMobDate);
                
        if (mode.equals("modal") && (showOnFocus != null && !Boolean.valueOf(showOnFocus))) {
            wb.attr("showOnFocus", false);
            wb.attr("showOnTap", false);            
        }

        if (!calendar.isTimeOnly()) {
            String dateOrder = formatDateOrder(patternMobDate);
            wb.attr("dateOrder", dateOrder);
        }

        if (calendar.isNavigator()) {
            wb.attr("mode", "mixed");
        }

        if (calendar.getEffect() != null) {
            wb.attr("animate", calendar.getEffect().toLowerCase());
        }

        if (calendar.getMindate() != null) {
            wb.append(",minDate: $.scroller.parseDate('m/d/yy','" + calendar.getMindate() + "')");
        }

        if (calendar.getMaxdate() != null) {
            wb.append(",maxDate: $.scroller.parseDate('m/d/yy','" + calendar.getMaxdate() + "')");
        }

        if (calendar.isShowButtonPanel()) {
            wb.attr("showNow", calendar.isShowButtonPanel());
        }

        if (calendar.isDisabledWeekends()) {
            wb.append(",invalid: { daysOfWeek:[0, 6] }");
        }

        //time
        if (calendar.hasTime()) {
            wb.attr("hasTime", "true");

            String patternMobTime = truncTimePattern(patternMobDateTime);
            String timeOrder = truncTimePattern(patternMobTime);

            wb.attr("timeFormat", patternMobTime);
            wb.attr("timeWheels", timeOrder);


            wb.attr("timeOnly", calendar.isTimeOnly())
                    //step
                    .attr("stepHour", calendar.getStepHour())
                    .attr("stepMinute", calendar.getStepMinute())
                    .attr("stepSecond", calendar.getStepSecond());
        }
        
        encodeClientBehaviors(context, calendar);                        

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, Calendar calendar, String value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = calendar.getClientId(context);
        String type = "text";

        writer.startElement("input", calendar);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("name", clientId, null);


        if (calendar.getMode().equals("inline")) {
            type = "hidden";
        }

        writer.writeAttribute("type", type, null);


        if (!isValueBlank(value)) {
            writer.writeAttribute("value", value, null);
        }

        renderPassThruAttributes(context, calendar, HTML.INPUT_TEXT_ATTRS);

        if (calendar.isDisabled()) {
            writer.writeAttribute("disabled", "disabled", null);
        }
        if (calendar.isReadonlyInput()) {
            writer.writeAttribute("readonly", "readonly", null);
        }
        if (calendar.getStyle() != null) {
            writer.writeAttribute("style", calendar.getStyle(), null);
        }
        writer.writeAttribute("class", createStyleClass(calendar), "styleClass");               

        writer.endElement("input");
    }

    protected String formatDateOrder(String pattern) {
        if (pattern != null) {
            pattern = pattern.replaceAll("/", "");
            pattern = pattern.replaceAll("-", "");
            pattern = pattern.replaceAll(",", "");
            pattern = pattern.replaceAll(" ", "");            
        }

        return pattern;
    }

    protected String formatTimeOrder(String pattern) {
        if (pattern != null) {
            pattern = pattern.replaceAll(":", "");
            pattern = pattern.replaceAll(" ", "");
            pattern = pattern.toLowerCase();
        }

        return pattern;
    }

    protected String truncDatePattern(String pattern) {
        int index1 = pattern.indexOf("H");
        int index2 = pattern.indexOf("h");

        if (index1 != -1 || index2 != -1) {
            int index;
            if (index1 != -1) {
                index = index1;
            } else {
                index = index2;
            }
            pattern = pattern.substring(0, index);
            pattern = pattern.replaceAll(" ", "");
        }

        return pattern;
    }

    protected String truncTimePattern(String pattern) {
        int index1 = pattern.indexOf("H");
        int index2 = pattern.indexOf("h");

        if (index1 != -1 || index2 != -1) {
            int index;
            if (index1 != -1) {
                index = index1;
            } else {
                index = index2;
            }
            pattern = pattern.substring(index, pattern.length());
            pattern = pattern.replace("a", "A");
        }

        return pattern;
    }

    protected String convertPatternMobile(String pattern) {
        if (pattern == null) {
            return null;
        } else {
            //year
            pattern = pattern.replaceAll("yy", "y");

            //time
            if (pattern.indexOf("H") != -1 || pattern.indexOf("h") != -1) {

                //24Hours
                if (pattern.indexOf("A") != -1 || pattern.indexOf("a") != -1) {
                    pattern = pattern.replaceAll("H", "h");
                } else {
                    pattern = pattern.replaceAll("h", "H");
                }

                pattern = pattern.replaceAll("m", "i");
            }

            //month
            if (pattern.indexOf("MMM") != -1) {
                pattern = pattern.replaceAll("MMM", "M");
            } else {
                pattern = pattern.replaceAll("M", "m");
            }

            //day of week
            pattern = pattern.replaceAll("EEE", "D");



            return pattern;
        }
    }
    
    protected String createStyleClass(Calendar calendar) {
        String defaultClass = "";
        defaultClass = calendar.isValid() ? defaultClass : defaultClass + " ui-focus";
        
        String styleClass = calendar.getStyleClass();
        styleClass = styleClass == null ? defaultClass : defaultClass + " " + styleClass;
        
        return styleClass;
    }        

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object value) throws ConverterException {
        Calendar calendar = (Calendar) component;
        String submittedValue = (String) value;
        Converter converter = calendar.getConverter();

        if (isValueBlank(submittedValue)) {
            return null;
        }

        //Delegate to user supplied converter if defined
        if (converter != null) {
            return converter.getAsObject(context, calendar, submittedValue);
        }

        //Use built-in converter
        try {
            SimpleDateFormat format = new SimpleDateFormat(calendar.calculatePattern(), calendar.calculateLocale(context));
            format.setTimeZone(calendar.calculateTimeZone());

            Object date = format.parse(submittedValue);

            return date;
        } catch (ParseException e) {
            throw new ConverterException(e);
        }
    }
}
