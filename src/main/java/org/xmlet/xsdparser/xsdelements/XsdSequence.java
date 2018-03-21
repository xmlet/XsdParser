package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class XsdSequence extends XsdMultipleElements{

    public static final String XSD_TAG = "xsd:sequence";
    public static final String XS_TAG = "xs:sequence";

    private SequenceXsdElementVisitor visitor = new SequenceXsdElementVisitor();

    private XsdSequence(@NotNull Map<String, String> elementFieldsMapParam) {
        super(elementFieldsMapParam);
    }

    @Override
    public void accept(XsdElementVisitor xsdElementVisitor) {
        super.accept(xsdElementVisitor);
        xsdElementVisitor.visit(this);
    }

    @Override
    public SequenceXsdElementVisitor getVisitor() {
        return visitor;
    }

    public static ReferenceBase parse(Node node){
        return xsdParseSkeleton(node, new XsdSequence(convertNodeMap(node.getAttributes())));
    }

    class SequenceXsdElementVisitor extends AnnotatedXsdElementVisitor {

        @Override
        public XsdAbstractElement getOwner() {
            return XsdSequence.this;
        }

        @Override
        public void visit(XsdElement element) {
            super.visit(element);
            XsdSequence.this.addElement(element);
        }

        @Override
        public void visit(XsdGroup element) {
            super.visit(element);
            XsdSequence.this.addElement(element);
        }

        @Override
        public void visit(XsdChoice element) {
            super.visit(element);
            XsdSequence.this.addElement(element);
        }

        @Override
        public void visit(XsdSequence element) {
            super.visit(element);
            XsdSequence.this.addElement(element);
        }
    }
}
