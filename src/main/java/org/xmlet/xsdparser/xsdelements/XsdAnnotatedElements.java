package org.xmlet.xsdparser.xsdelements;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Serves as a base to every {@link XsdAbstractElement} concrete type which can have xsd:annotation as children. Extends
 * from {@link XsdIdentifierElements} because every concrete type that can contain xsd:annotation has children can also
 * have an {@link XsdIdentifierElements#id} field.
 */
public abstract class XsdAnnotatedElements extends XsdIdentifierElements {

    /**
     * The {@link XsdAnnotation} that is annotating the concrete instances of this class.
     */
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
