package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAnnotatedElementsVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdSequenceVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * A class representing the xsd:sequence element. Since it shares the same attributes as {@link XsdAll} or
 * {@link XsdChoice} it extends {@link XsdMultipleElements}. For more information check {@link XsdMultipleElements}.
 *
 * @see <a href="https://www.w3schools.com/xml/el_sequence.asp">xsd:sequence description and usage at w3c</a>
 */
public class XsdSequence extends XsdMultipleElements{

    public static final String XSD_TAG = "xsd:sequence";
    public static final String XS_TAG = "xs:sequence";

    /**
     * {@link XsdSequenceVisitor} instance which restricts the children type to {@link XsdElement}, {@link XsdGroup},
     * {@link XsdSequence} or {@link XsdChoice}.
     * Can also have {@link XsdAnnotation} as children as per inheritance of {@link XsdAnnotatedElementsVisitor}.
     */
    private XsdSequenceVisitor visitor = new XsdSequenceVisitor(this);

    private XsdSequence(@NotNull Map<String, String> elementFieldsMapParam) {
        super(elementFieldsMapParam);
    }

    @Override
    public void accept(XsdAbstractElementVisitor visitorParam) {
        super.accept(visitorParam);
        visitorParam.visit(this);
    }

    @Override
    public XsdSequenceVisitor getVisitor() {
        return visitor;
    }

    public static ReferenceBase parse(Node node){
        return xsdParseSkeleton(node, new XsdSequence(convertNodeMap(node.getAttributes())));
    }
}
