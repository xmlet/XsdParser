package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.core.XsdParserCore;
import org.xmlet.xsdparser.core.utils.ParseData;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A class representing the xsd:list element.
 *
 * @see <a href="https://www.w3schools.com/xml/el_list.asp">xsd:list description and usage at w3c</a>
 */
public class XsdList extends XsdAnnotatedElements {

    public static final String XSD_TAG = "xsd:list";
    public static final String XS_TAG = "xs:list";

    /**
     * The {@link XsdSimpleType} instance that states the type of the elements that belong to this {@link XsdList}
     * instance. This value shouldn't be present if there is a {@link XsdList#itemType} present.
     */
    private XsdSimpleType simpleType;

    /**
     * The itemType defines the built-it type or the name of a present {@link XsdSimpleType} instance that represent
     * the type of the elements that belong to this {@link XsdList}. This value shouldn't be present if there is a
     * {@link XsdList#simpleType} is present.
     */
    private String itemType;

    private XsdList(@NotNull XsdParserCore parser, @NotNull Map<String, String> attributesMap, @NotNull Function<XsdAbstractElement, XsdAbstractElementVisitor> visitorFunction) {
        super(parser, attributesMap, visitorFunction);

        this.itemType = attributesMap.getOrDefault(ITEM_TYPE_TAG, itemType);
    }

    @Override
    public void accept(XsdAbstractElementVisitor visitorParam) {
        super.accept(visitorParam);
        visitorParam.visit(this);
    }

    /**
     * Performs a copy of the current object for replacing purposes. The cloned objects are used to replace
     * {@link UnsolvedReference} objects in the reference solving process.
     * @param placeHolderAttributes The additional attributes to add to the clone.
     * @return A copy of the object from which is called upon.
     */
    @Override
    public XsdList clone(@NotNull Map<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(attributesMap);

        XsdList elementCopy = new XsdList(this.parser, placeHolderAttributes, visitorFunction);

        if (this.simpleType != null){
            elementCopy.simpleType = (XsdSimpleType) this.simpleType.clone(simpleType.getAttributesMap(), elementCopy);
        }

        elementCopy.parent = null;

        return elementCopy;
    }

    public static ReferenceBase parse(@NotNull ParseData parseData){
        return xsdParseSkeleton(parseData.node, new XsdList(parseData.parserInstance, convertNodeMap(parseData.node.getAttributes()), parseData.visitorFunction));
    }

    public XsdSimpleType getXsdSimpleType() {
        return simpleType;
    }

    public String getItemType() {
        return itemType;
    }

    public void setSimpleType(XsdSimpleType simpleType) {
        this.simpleType = simpleType;
    }
}
