package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class XsdAnnotation extends XsdIdentifierElements {

    public static final String XSD_TAG = "xsd:annotation";
    public static final String XS_TAG = "xs:annotation";

    private XsdElementVisitor xsdElementVisitor = new AnnotationXsdElementVisitor();

    private List<XsdAppInfo> appInfoList = new ArrayList<>();
    private List<XsdDocumentation> documentations = new ArrayList<>();

    private XsdAnnotation(@NotNull Map<String, String> elementFieldsMapParam) {
        super(elementFieldsMapParam);
    }

    @Override
    public XsdElementVisitor getVisitor() {
        return xsdElementVisitor;
    }

    @Override
    public void accept(XsdElementVisitor xsdElementVisitor) {
        super.accept(xsdElementVisitor);
        xsdElementVisitor.visit(this);
    }

    public List<XsdAppInfo> getAppInfoList() {
        return appInfoList;
    }

    public List<XsdDocumentation> getDocumentations() {
        return documentations;
    }

    public static ReferenceBase parse(Node node){
        return xsdParseSkeleton(node, new XsdAnnotation(convertNodeMap(node.getAttributes())));
    }

    class AnnotationXsdElementVisitor implements XsdElementVisitor {

        public XsdAbstractElement getOwner() {
            return XsdAnnotation.this;
        }

        @Override
        public void visit(XsdAppInfo element) {
            XsdElementVisitor.super.visit(element);

            XsdAnnotation.this.appInfoList.add(element);
        }

        @Override
        public void visit(XsdDocumentation element) {
            XsdElementVisitor.super.visit(element);

            XsdAnnotation.this.documentations.add(element);
        }
    }
}
