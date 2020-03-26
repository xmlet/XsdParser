package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.core.XsdParserCore;
import org.xmlet.xsdparser.core.utils.ParseData;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.function.Function;

/**
 * A class representing the xsd:complexContent element.
 *
 * @see <a href="https://www.w3schools.com/xml/el_complexcontent.asp">xsd:complexContent element description and usage at w3c</a>
 */
public class XsdComplexContent extends XsdAnnotatedElements {

    public static final String XSD_TAG = "xsd:complexContent";
    public static final String XS_TAG = "xs:complexContent";

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

    private XsdComplexContent(@NotNull XsdParserCore parser, @NotNull Map<String, String> attributesMap, @NotNull Function<XsdAbstractElement, XsdAbstractElementVisitor> visitorFunction) {
        super(parser, attributesMap, visitorFunction);

        this.mixed = AttributeValidations.validateBoolean(attributesMap.getOrDefault(MIXED_TAG, "false"));
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

    public static ReferenceBase parse(@NotNull ParseData parseData){
        return xsdParseSkeleton(parseData.node, new XsdComplexContent(parseData.parserInstance, convertNodeMap(parseData.node.getAttributes()), parseData.visitorFunction));
    }

    public void setExtension(ReferenceBase extension) {
        this.extension = extension;
    }

    public void setRestriction(ReferenceBase restriction) {
        this.restriction = restriction;
    }
}
