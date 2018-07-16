package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAllVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAnnotatedElementsVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * A class representing the xsd:all element. Since it shares the same attributes as {@link XsdChoice} or {@link XsdSequence}
 * it extends {@link XsdMultipleElements}. For more information check {@link XsdMultipleElements}.
 *
 * @see <a href="https://www.w3schools.com/xml/el_all.asp">xsd:all element definition and usage</a>
 */
public class XsdAll extends XsdMultipleElements {

    public static final String XSD_TAG = "xsd:all";
    public static final String XS_TAG = "xs:all";

    /**
     * {@link XsdAllVisitor} instance, which restricts his children elements to {@link XsdElement} instances.
     * Can also have {@link XsdAnnotation} children elements as per inheritance of {@link XsdAnnotatedElementsVisitor}
     */
    private final XsdAllVisitor visitor = new XsdAllVisitor(this);

    private XsdAll(@NotNull Map<String, String> elementFieldsMapParam){
        super(elementFieldsMapParam);
    }

    @Override
    public void accept(XsdAbstractElementVisitor visitorParam) {
        super.accept(visitorParam);
        visitorParam.visit(this);
    }

    @Override
    public XsdAllVisitor getVisitor() {
        return visitor;
    }

    public static ReferenceBase parse(Node node) {
        return xsdParseSkeleton(node, new XsdAll(convertNodeMap(node.getAttributes())));
    }

}
