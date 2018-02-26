package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;

import java.util.Map;

public abstract class XsdAnnotatedElements extends XsdIdentifierElements {

    private XsdAnnotation annotation;

    protected XsdAnnotatedElements(Map<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    protected XsdAnnotatedElements(XsdAbstractElement parent, Map<String, String> elementFieldsMap) {
        super(parent, elementFieldsMap);
    }

    protected void setAnnotation(XsdAnnotation annotation){
        this.annotation = annotation;
    }

    @SuppressWarnings("unused")
    public XsdAnnotation getAnnotation() {
        return annotation;
    }

    protected class AnnotatedXsdElementVisitor extends XsdElementVisitor {
        @Override
        public XsdAbstractElement getOwner() {
            return XsdAnnotatedElements.this;
        }

        @Override
        public void visit(XsdAnnotation element) {
            super.visit(element);

            setAnnotation(element);
        }
    }
}
