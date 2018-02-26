package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.VisitorNotFoundException;
import org.w3c.dom.Node;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class XsdDocumentation extends XsdAbstractElement {

    public static final String XSD_TAG = "xsd:documentation";
    public static final String XS_TAG = "xs:documentation";

    private String source;
    private String xmlLang;
    private String content;

    private XsdDocumentation(XsdAbstractElement parent, Map<String, String> elementFieldsMap) {
        super(parent, elementFieldsMap);
    }

    private XsdDocumentation(Map<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    @Override
    public void setFields(Map<String, String> elementFieldsMap) {
        super.setFields(elementFieldsMap);

        if (elementFieldsMap != null){
            this.source = elementFieldsMap.getOrDefault(SOURCE, source);
            this.xmlLang = elementFieldsMap.getOrDefault(XML_LANG, xmlLang);
        }
    }

    @Override
    public XsdElementVisitor getXsdElementVisitor() {
        throw new VisitorNotFoundException("Documentation can't have children.");
    }

    @Override
    public void accept(XsdElementVisitor xsdElementVisitor) {
        xsdElementVisitor.visit(this);
        this.setParent(xsdElementVisitor.getOwner());
    }

    @Override
    public XsdDocumentation clone(Map<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(this.getElementFieldsMap());
        XsdDocumentation elementCopy = new XsdDocumentation(this.getParent(), placeHolderAttributes);

        elementCopy.source = this.source;
        elementCopy.xmlLang = this.xmlLang;
        elementCopy.content = this.content;

        return elementCopy;
    }

    public static ReferenceBase parse(Node node){
        XsdDocumentation documentation = new XsdDocumentation(convertNodeMap(node.getAttributes()));

        documentation.content = xsdRawContentParse(node);

        return ReferenceBase.createFromXsd(documentation);
    }

    @Override
    protected List<ReferenceBase> getElements() {
        return Collections.emptyList();
    }
}
