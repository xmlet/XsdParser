package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.core.XsdParserCore;
import org.xmlet.xsdparser.core.utils.ConfigEntryData;
import org.xmlet.xsdparser.core.utils.ParseData;
import org.xmlet.xsdparser.xsdelements.elementswrapper.NamedConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.exceptions.ParsingException;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdExtensionVisitor;

import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A class representing the xsd:extension element.
 *
 * @see <a href="https://www.w3schools.com/xml/el_extension.asp">xsd:extension description and usage at w3c</a>
 */
public class XsdExtension extends XsdAnnotatedElements {

    public static final String XSD_TAG = "xsd:extension";
    public static final String XS_TAG = "xs:extension";
    public static final String TAG = "extension";

    /**
     * The child element of the {@link XsdExtension} instance. Either a {@link XsdGroup}, {@link XsdAll},
     * {@link XsdSequence} or a {@link XsdChoice} instance wrapped in a {@link ReferenceBase} object.
     */
    private ReferenceBase childElement;

    /**
     * A {@link XsdElement} instance wrapped in a {@link ReferenceBase} object from which this {@link XsdExtension}
     * instance extends.
     */
    private ReferenceBase base;

    private XsdExtension(@NotNull XsdParserCore parser, @NotNull Map<String, String> attributesMap, @NotNull Function<XsdAbstractElement, XsdAbstractElementVisitor> visitorFunction) {
        super(parser, attributesMap, visitorFunction);

        String baseValue = attributesMap.getOrDefault(BASE_TAG, null);

        if (baseValue != null){
            if (XsdParserCore.isXsdTypeToJava(baseValue)){
                HashMap<String, String> attributes = new HashMap<>();
                attributes.put(NAME_TAG, baseValue);
                this.base = ReferenceBase.createFromXsd(new XsdBuiltInDataType(parser, attributes, this));
            } else {
                ConfigEntryData config = XsdParserCore.getParseMappers(XsdElement.TAG);

                if (config == null){
                    throw new ParsingException("Invalid Parsing Configuration for XsdElement.");
                }

                this.base = new UnsolvedReference(baseValue, new XsdElement(this, this.parser, new HashMap<>(), config.visitorFunction));
                parser.addUnsolvedReference((UnsolvedReference) this.base);
            }
        }
    }

    /**
     * This method should always receive two elements, one to replace the {@link UnsolvedReference} created due to
     * the value present in the base attribute and another if it has an {@link UnsolvedReference} as a child element.
     * @param element A concrete element with a name that will replace the {@link UnsolvedReference} object created in the
     *                {@link XsdExtension} constructor. The {@link UnsolvedReference} is only replaced if there
     *                is a match between the {@link UnsolvedReference#ref} and the {@link NamedConcreteElement#name}.
     */
    @Override
    public void replaceUnsolvedElements(NamedConcreteElement element) {
        super.replaceUnsolvedElements(element);

        XsdNamedElements elem = element.getElement();

        boolean isComplexOrSimpleType = elem instanceof XsdComplexType || elem instanceof XsdSimpleType;

        if (this.base instanceof UnsolvedReference && isComplexOrSimpleType && compareReference(element, (UnsolvedReference) this.base)){
            this.base = element;
        }

        if (this.childElement instanceof UnsolvedReference && elem instanceof XsdGroup && compareReference(element, (UnsolvedReference) this.childElement)){
            this.childElement = element;
        }

        ((XsdExtensionVisitor)visitor).replaceUnsolvedAttributes(parser, element, this);
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
    public XsdExtension clone(@NotNull Map<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(attributesMap);
        placeHolderAttributes.remove(BASE_TAG);

        XsdExtension elementCopy = new XsdExtension(this.parser, placeHolderAttributes, visitorFunction);

        for(XsdAttribute attribute: getXsdAttributes().collect(Collectors.toList()))
        {
            elementCopy.visitor.visit((XsdAttribute) attribute.clone(attribute.attributesMap, elementCopy));
        }

        for(XsdAttributeGroup attributeGroup: getXsdAttributeGroup().collect(Collectors.toList()))
        {
            elementCopy.visitor.visit((XsdAttributeGroup) attributeGroup.clone(attributeGroup.attributesMap, elementCopy));
        }

        elementCopy.childElement = ReferenceBase.clone(parser, this.childElement, elementCopy);
        elementCopy.base = this.base;
        elementCopy.cloneOf = this;
        elementCopy.parent = null;

        return elementCopy;
    }

    /**
     * @return Its children elements as his own.
     */
    @Override
    public List<ReferenceBase> getElements() {
        return childElement == null ? Collections.emptyList() : childElement.getElement().getElements();
    }

    /**
     * @return Either a {@link XsdComplexType} or a {@link XsdSimpleType} from which this extension extends or null if
     * the {@link XsdParserCore} wasn't able to replace the {@link UnsolvedReference} created by the base attribute value.
     */
    public XsdNamedElements getBase() {
        if (base instanceof NamedConcreteElement){
            return ((NamedConcreteElement)base).getElement();
        }

        return null;
    }

    /**
     * @return The {@link XsdComplexType} from which this extension extends or null if the {@link XsdParserCore} wasn't
     * able to replace the {@link UnsolvedReference} created by the base attribute value.
     */
    public XsdComplexType getBaseAsComplexType() {
        if (base instanceof NamedConcreteElement){
            XsdAbstractElement baseType = base.getElement();

            if (baseType instanceof XsdComplexType){
                return (XsdComplexType) baseType;
            }
        }

        return null;
    }

    /**
     * @return The {@link XsdSimpleType} from which this extension extends or null if the {@link XsdParserCore} wasn't
     * able to replace the {@link UnsolvedReference} created by the base attribute value.
     */
    @SuppressWarnings("unused")
    public XsdSimpleType getBaseAsSimpleType() {
        if (base instanceof NamedConcreteElement){
            XsdAbstractElement baseType = base.getElement();

            if (baseType instanceof XsdSimpleType){
                return (XsdSimpleType) baseType;
            }
        }

        return null;
    }

    /**
     * @return The {@link XsdBuiltInDataType} from which this extension extends.
     */
    @SuppressWarnings("unused")
    public XsdBuiltInDataType getBaseAsBuiltInDataType() {
        if (base instanceof NamedConcreteElement){
            XsdAbstractElement baseType = base.getElement();

            if (baseType instanceof XsdBuiltInDataType){
                return (XsdBuiltInDataType) baseType;
            }
        }

        return null;
    }

    public static ReferenceBase parse(@NotNull ParseData parseData){
        return xsdParseSkeleton(parseData.node, new XsdExtension(parseData.parserInstance, convertNodeMap(parseData.node.getAttributes()), parseData.visitorFunction));
    }

    @SuppressWarnings("unused")
    public Stream<XsdAttribute> getXsdAttributes() {
        return ((XsdExtensionVisitor)visitor).getXsdAttributes();
    }

    @SuppressWarnings("unused")
    public Stream<XsdAttributeGroup> getXsdAttributeGroup() {
        return ((XsdExtensionVisitor)visitor).getXsdAttributeGroups();
    }

    @SuppressWarnings("unused")
    public XsdAbstractElement getXsdChildElement(){
        if (childElement == null) {
            return null;
        }

        return childElement instanceof UnsolvedReference ? null : childElement.getElement();
    }

    public void setChildElement(ReferenceBase childElement) {
        this.childElement = childElement;
    }

    /**
     * @return The childElement as a {@link XsdGroup} object or null if childElement isn't a {@link XsdGroup} instance.
     */
    @SuppressWarnings("unused")
    public XsdGroup getChildAsGroup() {
        return childElement.getElement() instanceof XsdGroup ? (XsdGroup) childElement.getElement() : null;
    }

    /**
     * @return The childElement as a {@link XsdAll} object or null if childElement isn't a {@link XsdAll} instance.
     */
    @SuppressWarnings("unused")
    public XsdAll getChildAsAll() {
        return childrenIsMultipleElement() ? XsdMultipleElements.getChildAsdAll((XsdMultipleElements) childElement.getElement()) : null;
    }

    /**
     * @return The childElement as a {@link XsdChoice} object or null if childElement isn't a {@link XsdChoice} instance.
     */
    @SuppressWarnings("unused")
    public XsdChoice getChildAsChoice() {
        return childrenIsMultipleElement() ? XsdMultipleElements.getChildAsChoice((XsdMultipleElements) childElement.getElement()) : null;
    }

    /**
     * @return The childElement as a {@link XsdSequence} object or null if childElement isn't a {@link XsdSequence} instance.
     */
    @SuppressWarnings("unused")
    public XsdSequence getChildAsSequence() {
        return childrenIsMultipleElement() ? XsdMultipleElements.getChildAsSequence((XsdMultipleElements) childElement.getElement()) : null;
    }

    private boolean childrenIsMultipleElement(){
        return childElement.getElement() instanceof XsdMultipleElements;
    }
}
