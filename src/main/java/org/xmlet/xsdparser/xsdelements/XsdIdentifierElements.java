package org.xmlet.xsdparser.xsdelements;

import javax.validation.constraints.NotNull;
import java.util.Map;

public abstract class XsdIdentifierElements extends XsdAbstractElement {

    private String id;

    XsdIdentifierElements(@NotNull Map<String, String> elementFieldsMapParam) {
        super(elementFieldsMapParam);
    }

    @Override
    public void setFields(@NotNull Map<String, String> elementFieldsMapParam) {
        super.setFields(elementFieldsMapParam);

        this.id = elementFieldsMap.getOrDefault(ID_TAG, id);
    }

    public String getId() {
        return id;
    }
}
