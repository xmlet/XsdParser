package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class XsdAnnotation extends XsdIdentifierElements {

    public static final String XSD_TAG = "xsd:annotation";
    public static final String XS_TAG = "xs:annotation";

    private XsdElementVisitor xsdElementVisitor = new AnnotationXsdElementVisitor();

    private List<XsdAppInfo> appInfos = new ArrayList<>();
    private List<XsdDocumentation> documentations = new ArrayList<>();

    private XsdAnnotation(XsdAbstractElement parent, Map<String, String> elementFieldsMap) {
        super(parent, elementFieldsMap);
    }

    private XsdAnnotation(Map<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    @Override
    public XsdElementVisitor getXsdElementVisitor() {
        return xsdElementVisitor;
    }

    @Override
    public void accept(XsdElementVisitor xsdElementVisitor) {
        xsdElementVisitor.visit(this);
        this.setParent(xsdElementVisitor.getOwner());
    }

    @Override
    public XsdAnnotation clone(Map<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(this.getElementFieldsMap());
        XsdAnnotation elementCopy = new XsdAnnotation(this.getParent(), placeHolderAttributes);

        elementCopy.appInfos.addAll(this.getAppInfos());
        elementCopy.documentations.addAll(this.getDocumentations());

        return elementCopy;
    }

    @Override
    protected List<ReferenceBase> getElements() {
        return Collections.emptyList();
    }

    private List<XsdAppInfo> getAppInfos() {
        return appInfos;
    }

    private List<XsdDocumentation> getDocumentations() {
        return documentations;
    }

    public static ReferenceBase parse(Node node){
        return xsdParseSkeleton(node, new XsdAnnotation(convertNodeMap(node.getAttributes())));
    }

    class AnnotationXsdElementVisitor extends XsdElementVisitor {

        @Override
        public XsdAbstractElement getOwner() {
            return XsdAnnotation.this;
        }

        @Override
        public void visit(XsdAppInfo element) {
            super.visit(element);

            XsdAnnotation.this.appInfos.add(element);
        }

        @Override
        public void visit(XsdDocumentation element) {
            super.visit(element);

            XsdAnnotation.this.documentations.add(element);
        }
    }
}
