package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAnnotatedElementsVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdComplexContentVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * A class representing the xsd:complexContent element.
 *
 * @see <a href="https://www.w3schools.com/xml/el_complexcontent.asp">xsd:complexContent element description and usage at w3c</a>
 */
public class XsdComplexContent extends XsdAnnotatedElements {

    public static final String XSD_TAG = "xsd:complexContent";
    public static final String XS_TAG = "xs:complexContent";

    /**
     * {@link XsdComplexContentVisitor} instance which restricts its children to {@link XsdExtension} and
     * {@link XsdRestriction}.
     * Can also have {@link XsdAnnotation} as children as per inheritance of {@link XsdAnnotatedElementsVisitor}.
     * elements.
     */
    private XsdComplexContentVisitor visitor = new XsdComplexContentVisitor(this);

    /**
     * A {@link XsdRestriction} object wrapped in a {@link ReferenceBase} object.
     */
    private ReferenceBase restriction;

    /**
     * A {@link XsdExtension} object wrapped in a {@link ReferenceBase} object.
     */
    private ReferenceBase extension;

    /**
     * Specifies whether character data is allowed to appear between the child elements of this element.
     */
    private boolean mixed;

    private XsdComplexContent(@NotNull XsdParser parser, @NotNull Map<String, String> elementFieldsMapParam) {
        super(parser, elementFieldsMapParam);
    }

    @Override
    public void setFields(@NotNull Map<String, String> elementFieldsMapParam) {
        super.setFields(elementFieldsMapParam);

        this.mixed = Boolean.parseBoolean(elementFieldsMap.getOrDefault(MIXED_TAG, "false"));
    }

    @Override
    public XsdComplexContentVisitor getVisitor() {
        return visitor;
    }

    @Override
    public void accept(XsdAbstractElementVisitor visitorParam) {
        super.accept(visitorParam);
        visitorParam.visit(this);
    }

    @SuppressWarnings("unused")
    public boolean isMixed() {
        return mixed;
    }

    public XsdExtension getXsdExtension() {
        return extension instanceof ConcreteElement ? (XsdExtension) extension.getElement() : null;
    }

    @SuppressWarnings("unused")
    public XsdRestriction getXsdRestriction(){
        return restriction instanceof ConcreteElement ? (XsdRestriction) restriction.getElement() : null;
    }

    public static ReferenceBase parse(@NotNull XsdParser parser, Node node){
        return xsdParseSkeleton(node, new XsdComplexContent(parser, convertNodeMap(node.getAttributes())));
    }

    public void setExtension(ReferenceBase extension) {
        this.extension = extension;
    }

    public void setRestriction(ReferenceBase restriction) {
        this.restriction = restriction;
    }
}
