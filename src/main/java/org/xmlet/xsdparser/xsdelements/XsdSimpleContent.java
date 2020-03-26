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
 * A class representing the xsd:simpleContent element.
 *
 * @see <a href="https://www.w3schools.com/xml/el_simpleContent.asp">xsd:simpleContent description and usage at w3c</a>
 */
public class XsdSimpleContent extends XsdAnnotatedElements {

    public static final String XSD_TAG = "xsd:simpleContent";
    public static final String XS_TAG = "xs:simpleContent";

    /**
     * The {@link XsdRestriction} instance that should be applied to the {@link XsdSimpleContent} instance.
     */
    private ReferenceBase restriction;

    /**
     * The {@link XsdExtension} instance that is present in the {@link XsdSimpleContent} instance.
     */
    private ReferenceBase extension;

    private XsdSimpleContent(@NotNull XsdParserCore parser, @NotNull Map<String, String> elementFieldsMapParam, @NotNull Function<XsdAbstractElement, XsdAbstractElementVisitor> visitorFunction) {
        super(parser, elementFieldsMapParam, visitorFunction);
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

    public static ReferenceBase parse(@NotNull ParseData parseData){
        return xsdParseSkeleton(parseData.node, new XsdSimpleContent(parseData.parserInstance, convertNodeMap(parseData.node.getAttributes()), parseData.visitorFunction));
    }

    public void setRestriction(ReferenceBase restriction) {
        this.restriction = restriction;
    }

    public void setExtension(ReferenceBase extension) {
        this.extension = extension;
    }
}
