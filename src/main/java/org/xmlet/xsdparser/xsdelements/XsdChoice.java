package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class XsdChoice extends XsdMultipleElements{

    public static final String XSD_TAG = "xsd:choice";
    public static final String XS_TAG = "xs:choice";

    private ChoiceXsdElementVisitor visitor = new ChoiceXsdElementVisitor();

    private XsdChoice(@NotNull Map<String, String> elementFieldsMapParam) {
        super(elementFieldsMapParam);
    }

    @Override
    public void accept(XsdElementVisitor xsdElementVisitor) {
        super.accept(xsdElementVisitor);
        xsdElementVisitor.visit(this);
    }

    @Override
    public ChoiceXsdElementVisitor getVisitor() {
        return visitor;
    }

    public static ReferenceBase parse(Node node){
        return xsdParseSkeleton(node, new XsdChoice(convertNodeMap(node.getAttributes())));
    }

    class ChoiceXsdElementVisitor extends AnnotatedXsdElementVisitor {

        @Override
        public XsdAbstractElement getOwner() {
            return XsdChoice.this;
        }

        @Override
        public void visit(XsdElement element) {
            super.visit(element);
            XsdChoice.this.addElement(element);
        }

        @Override
        public void visit(XsdGroup element) {
            super.visit(element);
            XsdChoice.this.addElement(element);
        }

        @Override
        public void visit(XsdChoice element) {
            super.visit(element);
            XsdChoice.this.addElement(element);
        }

        @Override
        public void visit(XsdSequence element) {
            super.visit(element);
            XsdChoice.this.addElement(element);
        }
    }

}
