package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.VisitorNotFoundException;
import org.w3c.dom.Node;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class XsdAppInfo extends XsdAbstractElement {

    public static final String XSD_TAG = "xsd:appInfo";
    public static final String XS_TAG = "xs:appInfo";

    private String source;
    private String content;

    private XsdAppInfo(XsdAbstractElement parent, Map<String, String> elementFieldsMap) {
        super(parent, elementFieldsMap);
    }

    private XsdAppInfo(Map<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    @Override
    public void setFields(Map<String, String> elementFieldsMap) {
        super.setFields(elementFieldsMap);

        if (elementFieldsMap != null){
            this.source = elementFieldsMap.getOrDefault(SOURCE, source);
        }
    }

    @Override
    public XsdElementVisitor getXsdElementVisitor() {
        throw new VisitorNotFoundException("AppInfo can't have children.");
    }

    @Override
    public void accept(XsdElementVisitor xsdElementVisitor) {
        xsdElementVisitor.visit(this);
        this.setParent(xsdElementVisitor.getOwner());
    }

    @Override
    public XsdAppInfo clone(Map<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(this.getElementFieldsMap());
        XsdAppInfo elementCopy = new XsdAppInfo(this.getParent(), placeHolderAttributes);

        elementCopy.source = this.source;
        elementCopy.content = this.content;

        return elementCopy;
    }

    @Override
    protected List<ReferenceBase> getElements() {
        return Collections.emptyList();
    }

    public static ReferenceBase parse(Node node){
        XsdAppInfo appInfo = new XsdAppInfo(convertNodeMap(node.getAttributes()));

        appInfo.content = xsdRawContentParse(node);

        return ReferenceBase.createFromXsd(appInfo);
    }
}
