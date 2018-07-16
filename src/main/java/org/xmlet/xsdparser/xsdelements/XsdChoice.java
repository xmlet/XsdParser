package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAnnotatedElementsVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdChoiceVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * A class representing the xsd:choice element. Since it shares the same attributes as {@link XsdChoice} or
 * {@link XsdSequence} it extends {@link XsdMultipleElements}. For more information check {@link XsdMultipleElements}.
 *
 * @see <a href="https://www.w3schools.com/xml/el_choice.asp">xsd:choice element description and usage at w3c</a>
 */
public class XsdChoice extends XsdMultipleElements {

    public static final String XSD_TAG = "xsd:choice";
    public static final String XS_TAG = "xs:choice";

    /**
     * {@link XsdChoiceVisitor} instance which restricts the children elements to {@link XsdElement}, {@link XsdGroup},
     * {@link XsdChoice}, {@link XsdSequence}.
     * Can also have {@link XsdAnnotation} as children as per inheritance of {@link XsdAnnotatedElementsVisitor}.
     */
    private XsdChoiceVisitor visitor = new XsdChoiceVisitor(this);

    private XsdChoice(@NotNull Map<String, String> elementFieldsMapParam) {
        super(elementFieldsMapParam);
    }

    @Override
    public void accept(XsdAbstractElementVisitor visitorParam) {
        super.accept(visitorParam);
        visitorParam.visit(this);
    }

    @Override
    public XsdChoiceVisitor getVisitor() {
        return visitor;
    }

    public static ReferenceBase parse(Node node){
        return xsdParseSkeleton(node, new XsdChoice(convertNodeMap(node.getAttributes())));
    }

}
