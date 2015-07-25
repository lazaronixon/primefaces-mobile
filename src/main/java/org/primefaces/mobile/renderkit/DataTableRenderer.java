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
import org.primefaces.component.column.Column;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.row.Row;
import org.primefaces.renderkit.CoreRenderer;

public class DataTableRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        DataTable dataTable = (DataTable) component;
        Map<String, Object> attrs = dataTable.getAttributes();
        String mode = (String) attrs.get("columnToggle");
        String btnText = (String) attrs.get("btnText");
        String defaultStyleClass = "ui-body-d ui-responsive table-stripe ";


        writer.startElement("table", dataTable);
        writer.writeAttribute("id", dataTable.getClientId(context), "id");
        writer.writeAttribute("data-role", "table", null);

        if (mode != null && Boolean.valueOf(mode)) {
            writer.writeAttribute("data-mode", "columntoggle", null);
        }

        if (btnText != null) {
            writer.writeAttribute("data-column-btn-text", btnText, null);
        }



        if (dataTable.getStyle() != null) {
            writer.writeAttribute("style", dataTable.getStyle(), null);
        }
        if (dataTable.getStyleClass() != null) {
            writer.writeAttribute("class", defaultStyleClass + dataTable.getStyleClass(), null);
        } else {
            writer.writeAttribute("class", defaultStyleClass, null);
        }

        encodeHeaders(context, dataTable);        
        encodeContent(context, dataTable);

        writer.endElement("table");

        dataTable.setRowIndex(-1);
    }

    protected void encodeHeaders(FacesContext context, DataTable dataTable) throws IOException {
        ResponseWriter writer = context.getResponseWriter();        
        ColumnGroup group = dataTable.getColumnGroup("header");
        writer.startElement("thead", null);

        if (group != null && group.isRendered()) {
            writer.startElement("tr", null);
            writer.writeAttribute("class", "th-groups", null);
            for (UIComponent child : group.getChildren()) {
                if (child.isRendered() && child instanceof Row) {
                    Row headerRow = (Row) child;
                    for (UIComponent headerRowChild : headerRow.getChildren()) {
                        if (headerRowChild.isRendered() && headerRowChild instanceof Column) {
                            Column column = (Column) headerRowChild;
                            Map<String, Object> attrs = column.getAttributes();
                            String priority = (String) attrs.get("priority");

                            writer.startElement("th", null);
                            writer.writeAttribute("colspan", column.getColspan(), null);                            
                            if (priority != null) {
                                writer.writeAttribute("data-priority", priority, null);
                            }
                            writer.write(column.getHeaderText());
                            writer.endElement("th");
                        }

                    }

                }
                writer.endElement("tr");
            }

        }

        writer.startElement("tr", null);
        writer.writeAttribute("class", "ui-bar-d", null);

        for (UIComponent kid : dataTable.getChildren()) {
            if (kid.isRendered() && kid instanceof Column) {
                Column column = (Column) kid;
                Map<String, Object> attrs = column.getAttributes();
                String priority = (String) attrs.get("priority");
                writer.startElement("th", null);

                if (priority != null) {
                    writer.writeAttribute("data-priority", priority, null);
                }
                writer.write(column.getHeaderText());
                writer.endElement("th");
            }
        }
        writer.endElement("tr");
        writer.endElement("thead");
    }

    protected void encodeContent(FacesContext context, DataTable dataTable) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement("tbody", null);

        if (dataTable.getVar() != null) {
            int rowCount = dataTable.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                dataTable.setRowIndex(i);
                writer.startElement("tr", null);

                int indexColumn = 0;
                for (UIComponent kid : dataTable.getChildren()) {
                    if (kid.isRendered() && kid instanceof Column) {
                        Column column = (Column) kid;

                        String element = "td";
                        if (indexColumn == 0) {
                            element = "th";
                        }

                        writer.startElement(element, null);
                        column.encodeChildren(context);
                        writer.endElement(element);
                        indexColumn++;
                    }
                }
                writer.endElement("tr");
            }
        }
        writer.endElement("tbody");
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

