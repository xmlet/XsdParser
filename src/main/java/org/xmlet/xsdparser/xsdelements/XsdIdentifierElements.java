package org.xmlet.xsdparser.xsdelements;

import java.util.Map;

public abstract class XsdIdentifierElements extends XsdAbstractElement {

    private String id;

    XsdIdentifierElements(Map<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    XsdIdentifierElements(XsdAbstractElement parent, Map<String, String> elementFieldsMap) {
        super(parent, elementFieldsMap);
    }

    @Override
    public void setFields(Map<String, String> elementFieldsMap) {
        super.setFields(elementFieldsMap);

        if (elementFieldsMap != null){
            this.id = elementFieldsMap.getOrDefault(ID, id);
        }
    }

    @SuppressWarnings("unused")
    public String getId() {
        return id;
    }
}
