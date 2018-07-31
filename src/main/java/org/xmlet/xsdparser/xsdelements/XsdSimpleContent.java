package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAnnotatedElementsVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdSimpleContentVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * A class representing the xsd:simpleContent element.
 *
 * @see <a href="https://www.w3schools.com/xml/el_simpleContent.asp">xsd:simpleContent description and usage at w3c</a>
 */
public class XsdSimpleContent extends XsdAnnotatedElements {

    public static final String XSD_TAG = "xsd:simpleContent";
    public static final String XS_TAG = "xs:simpleContent";

    /**
     * {@link XsdSimpleContentVisitor} instance which restrict its children to {@link XsdRestriction} and
     * {@link XsdExtension} instances.
     * Can also have {@link XsdAnnotation} children as per inheritance of {@link XsdAnnotatedElementsVisitor}.
     */
    private XsdSimpleContentVisitor visitor = new XsdSimpleContentVisitor(this);

    /**
     * The {@link XsdRestriction} instance that should be applied to the {@link XsdSimpleContent} instance.
     */
    private ReferenceBase restriction;

    /**
     * The {@link XsdExtension} instance that is present in the {@link XsdSimpleContent} instance.
     */
    private ReferenceBase extension;

    private XsdSimpleContent(@NotNull XsdParser parser, @NotNull Map<String, String> elementFieldsMapParam) {
        super(parser, elementFieldsMapParam);
    }

    @Override
    public XsdSimpleContentVisitor getVisitor() {
        return visitor;
    }

    @Override
    public void accept(XsdAbstractElementVisitor visitorParam) {
        super.accept(visitorParam);
        visitorParam.visit(this);
    }

    @SuppressWarnings("unused")
    public XsdExtension getXsdExtension() {
        return extension instanceof ConcreteElement ? (XsdExtension) extension.getElement() : null;
    }

    @SuppressWarnings("unused")
    public XsdRestriction getXsdRestriction(){
        return restriction instanceof ConcreteElement ? (XsdRestriction) restriction.getElement() : null;
    }

    public static ReferenceBase parse(@NotNull XsdParser parser, Node node){
        return xsdParseSkeleton(node, new XsdSimpleContent(parser, convertNodeMap(node.getAttributes())));
    }

    public void setRestriction(ReferenceBase restriction) {
        this.restriction = restriction;
    }

    public void setExtension(ReferenceBase extension) {
        this.extension = extension;
    }
}
