package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.core.XsdParserCore;
import org.xmlet.xsdparser.core.utils.ParseData;
import org.xmlet.xsdparser.xsdelements.elementswrapper.NamedConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.exceptions.ParsingException;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdSimpleTypeVisitor;

import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * A class representing the xsd:list element.
 *
 * @see <a href="https://www.w3schools.com/xml/el_list.asp">xsd:list description and usage at w3c</a>
 */
public class XsdList extends XsdAnnotatedElements {

    public static final String XSD_TAG = "xsd:list";
    public static final String XS_TAG = "xs:list";
    public static final String TAG = "list";

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

    /**
     * Resolved reference for {@link #itemType}. Wraps a {@link XsdBuiltInDataType} for built-in names or an
     * {@link UnsolvedReference} that will be resolved to a {@link XsdSimpleType} at the end of the parsing
     * process. A {@link XsdComplexType} target is not a valid itemType.
     */
    private ReferenceBase itemTypeReference;

    private XsdList(@NotNull XsdParserCore parser, @NotNull Map<String, String> attributesMap, @NotNull Function<XsdAbstractElement, XsdAbstractElementVisitor> visitorFunction) {
        super(parser, attributesMap, visitorFunction);

        this.itemType = attributesMap.getOrDefault(ITEM_TYPE_TAG, itemType);

        if (this.itemType != null){
            if (XsdParserCore.isXsdTypeToJava(this.itemType)){
                HashMap<String, String> attributes = new HashMap<>();
                attributes.put(NAME_TAG, this.itemType);
                this.itemTypeReference = ReferenceBase.createFromXsd(new XsdBuiltInDataType(parser, attributes, this));
            } else {
                this.itemTypeReference = new UnsolvedReference(this.itemType, new XsdSimpleType(this, parser, new HashMap<>(), elem -> new XsdSimpleTypeVisitor((XsdSimpleType) elem)));
                parser.addUnsolvedReference((UnsolvedReference) this.itemTypeReference);
            }
        }
    }

    @Override
    public void accept(XsdAbstractElementVisitor visitorParam) {
        super.accept(visitorParam);
        visitorParam.visit(this);
    }

    @Override
    public void validateSchemaRules() {
        super.validateSchemaRules();

        if (simpleType == null && itemType == null){
            throw new ParsingException(XSD_TAG + " element: one of " + ITEM_TYPE_TAG + " attribute or " + XsdSimpleType.XSD_TAG + " child is required.");
        }
        if (simpleType != null && itemType != null){
            throw new ParsingException(XSD_TAG + " element: " + ITEM_TYPE_TAG + " attribute and " + XsdSimpleType.XSD_TAG + " child are mutually exclusive.");
        }
    }

    /**
     * Rejects a {@link XsdComplexType} resolution for {@link #itemType} (the spec restricts the target to a
     * simpleType or built-in) and captures a successful {@link XsdSimpleType} resolution.
     */
    @Override
    public boolean replaceUnsolvedElements(NamedConcreteElement elementWrapper) {
        boolean replaced = super.replaceUnsolvedElements(elementWrapper);

        XsdAbstractElement element = elementWrapper.getElement();

        if (itemType != null && itemTypeReference instanceof UnsolvedReference && compareReferenceName(elementWrapper, itemType)){
            if (element instanceof XsdComplexType){
                throw new ParsingException(XSD_TAG + " element: " + ITEM_TYPE_TAG + " attribute must reference a simpleType or built-in type, not a complexType: \"" + itemType + "\".");
            }
            if (element instanceof XsdSimpleType){
                String baseFinal = DerivationValidation.typeFinal(element);
                if (DerivationValidation.tokenListBlocks(baseFinal, DerivationValidation.LIST)){
                    throw new ParsingException(XSD_TAG + " element: " + ITEM_TYPE_TAG + " \"" + itemType + "\" has " + FINAL_TAG + "=\"" + baseFinal + "\", which forbids derivation by list.");
                }
                this.itemTypeReference = elementWrapper;
                replaced = true;
            }
        }
        return replaced;
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
        placeHolderAttributes.remove(ITEM_TYPE_TAG);

        XsdList elementCopy = new XsdList(this.parser, placeHolderAttributes, visitorFunction);

        if (this.simpleType != null){
            elementCopy.simpleType = (XsdSimpleType) this.simpleType.clone(simpleType.getAttributesMap(), elementCopy);
        }

        elementCopy.itemType = this.itemType;
        elementCopy.itemTypeReference = ReferenceBase.clone(parser, this.itemTypeReference, elementCopy);
        elementCopy.parent = null;
        elementCopy.cloneOf = this;

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
