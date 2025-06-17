package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.core.XsdParserCore;
import org.xmlet.xsdparser.core.utils.ParseData;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;

import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A class representing the xsd:choice element. Since it shares the same attributes as {@link XsdChoice} or
 * {@link XsdSequence} it extends {@link XsdMultipleElements}. For more information check {@link XsdMultipleElements}.
 *
 * @see <a href="https://www.w3schools.com/xml/el_choice.asp">xsd:choice element description and usage at w3c</a>
 */
public class XsdChoice extends XsdMultipleElements {

    public static final String XSD_TAG = "xsd:choice";
    public static final String XS_TAG = "xs:choice";
    public static final String TAG = "choice";

    /**
     * Specifies the minimum number of times this element can occur in the parent element. The value can be any
     * number bigger or equal to 0. Default value is 1. This attribute cannot be used if the parent element is the
     * XsdSchema element.
     */
    private Integer minOccurs;

    /**
     * Specifies the maximum number of times this element can occur in the parent element. The value can be any
     * number bigger or equal to 0, or if you want to set no limit on the maximum number, use the value "unbounded".
     * Default value is 1. This attribute cannot be used if the parent element is the XsdSchema element.
     */
    private String maxOccurs;

    private XsdChoice(@NotNull XsdParserCore parser, @NotNull Map<String, String> attributesMap, @NotNull Function<XsdAbstractElement, XsdAbstractElementVisitor> visitorFunction) {
        super(parser, attributesMap, visitorFunction);

        this.minOccurs = AttributeValidations.validateNonNegativeInteger(XSD_TAG, MIN_OCCURS_TAG, attributesMap.getOrDefault(MIN_OCCURS_TAG, "1"));
        this.maxOccurs = AttributeValidations.maxOccursValidation(XSD_TAG, attributesMap.getOrDefault(MAX_OCCURS_TAG, "1"));
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
    public XsdChoice clone(@NotNull Map<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(attributesMap);

        XsdChoice elementCopy = new XsdChoice(this.getParser(), placeHolderAttributes, visitorFunction);

        for(ReferenceBase element: getElements()){
            elementCopy.elements.add(ReferenceBase.clone(parser, element, elementCopy));
        }

        elementCopy.setParent(null);
        elementCopy.cloneOf = this;

        return elementCopy;
    }

    public static ReferenceBase parse(@NotNull ParseData parseData){
        return xsdParseSkeleton(parseData.node, new XsdChoice(parseData.parserInstance, convertNodeMap(parseData.node.getAttributes()), parseData.visitorFunction));
    }

    @SuppressWarnings("unused")
    public Integer getMinOccurs() {
        return minOccurs;
    }

    @SuppressWarnings("unused")
    public String getMaxOccurs() {
        return maxOccurs;
    }

    /**
     * @return The children elements that are of the type {@link XsdChoice}.
     */
    @SuppressWarnings("unused")
    public Stream<XsdChoice> getChildrenChoices(){
        return getXsdElements().filter(element -> element instanceof XsdChoice).map(element -> (XsdChoice) element);
    }

    /**
     * @return The children elements that are of the type {@link XsdSequence}.
     */
    @SuppressWarnings("unused")
    public Stream<XsdSequence> getChildrenSequences(){
        return getXsdElements().filter(element -> element instanceof XsdSequence).map(element -> (XsdSequence) element);
    }

    /**
     * @return The children elements that are of the type {@link XsdGroup}.
     */
    @SuppressWarnings("unused")
    public Stream<XsdGroup> getChildrenGroups(){
        return getXsdElements().filter(element -> element instanceof XsdGroup).map(element -> (XsdGroup) element);
    }

    /**
     * @return The children elements that are of the type {@link XsdAny}.
     */
    @SuppressWarnings("unused")
    public Stream<XsdAny> getChildrenAny(){
        return getXsdElements().filter(element -> element instanceof XsdAny).map(element -> (XsdAny) element);
    }
}
