package org.xmlet.xsdparser.xsdelements;

import javax.validation.constraints.NotNull;
import java.util.Map;

public abstract class XsdAnnotatedElements extends XsdIdentifierElements {

    private XsdAnnotation annotation;

    protected XsdAnnotatedElements(@NotNull Map<String, String> elementFieldsMapParam) {
        super(elementFieldsMapParam);
    }

    public void setAnnotation(XsdAnnotation annotation){
        this.annotation = annotation;
    }

    public XsdAnnotation getAnnotation() {
        return annotation;
    }

}
