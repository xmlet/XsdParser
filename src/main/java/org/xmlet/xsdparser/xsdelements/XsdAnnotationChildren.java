package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.xsdelements.visitors.VisitorNotFoundException;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;

public abstract class XsdAnnotationChildren extends XsdAbstractElement {

    private String source;
    String content;

    XsdAnnotationChildren(@NotNull Map<String, String> elementFieldsMapParam) {
        super(elementFieldsMapParam);
    }

    @Override
    public XsdAbstractElementVisitor getVisitor() {
        throw new VisitorNotFoundException("AppInfo/Documentation can't have children.");
    }

    @Override
    public void setFields(@NotNull Map<String, String> elementFieldsMapParam) {
        super.setFields(elementFieldsMapParam);

        this.source = elementFieldsMap.getOrDefault(SOURCE_TAG, source);
    }

    public String getSource() {
        return source;
    }

    public String getContent() {
        return content;
    }

}
