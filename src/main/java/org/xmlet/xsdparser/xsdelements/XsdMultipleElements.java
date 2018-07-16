package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.xsdelements.elementswrapper.ConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.NamedConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * A class that serves as a base class to three classes that share similarities, {@link XsdAll}, {@link XsdChoice} and
 * {@link XsdSequence}. Those three classes share the {@link XsdMultipleElements#maxOccurs} and
 * {@link XsdMultipleElements#minOccurs} fields and also the {@link XsdMultipleElements#elements} which is a list of
 * {@link XsdAbstractElement} objects contained in each of these types. The types of the instances present in the
 * {@link XsdMultipleElements#elements} list depends on the concrete type, {@link XsdAll}, {@link XsdChoice} or
 * {@link XsdSequence}.
 */
public abstract class XsdMultipleElements extends XsdAnnotatedElements {

    /**
     * A list of elements that are contained in the concrete implementation of the {@link XsdMultipleElements} instance.
     */
    private List<ReferenceBase> elements = new ArrayList<>();

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

    XsdMultipleElements(@NotNull Map<String, String> elementFieldsMapParam) {
        super(elementFieldsMapParam);
    }

    /**
     * Sets the occurs fields with the information provided in the Map object or with their default values.
     * @param elementFieldsMapParam The Map object containing the information previously contained in the Node object.
     */
    @Override
    public void setFields(@NotNull Map<String, String> elementFieldsMapParam) {
        super.setFields(elementFieldsMapParam);

        this.minOccurs = Integer.parseInt(elementFieldsMap.getOrDefault(MIN_OCCURS_TAG, "1"));
        this.maxOccurs = elementFieldsMap.getOrDefault(MAX_OCCURS_TAG, "1");
    }

    /**
     * Replaces possible {@link UnsolvedReference} objects in the {@link XsdMultipleElements#elements} if any of their
     * {@link UnsolvedReference#ref} field matches the {@link NamedConcreteElement#name} field.
     * @param elementWrapper A {@link NamedConcreteElement} with a name that will replace an {@link UnsolvedReference}
     *                       object, if a match between the {@link NamedConcreteElement#name} attribute and the
     *                       {@link UnsolvedReference#ref} attribute.
     */
    @Override
    public void replaceUnsolvedElements(NamedConcreteElement elementWrapper) {
        if (elementWrapper.getElement() instanceof XsdElement){
            super.replaceUnsolvedElements(elementWrapper);
        }

        if (elementWrapper.getElement() instanceof XsdGroup){
            elements.add(elementWrapper);

            this.elements.removeIf(element ->
               element instanceof UnsolvedReference && ((UnsolvedReference) element).getRef().equals(elementWrapper.getName())
            );
        }
    }

    /**
     * @return All the elements received in the parsing process.
     */
    @Override
    public List<ReferenceBase> getElements(){
        return elements;
    }

    /**
     * @return The elements that are fully resolved. The {@link UnsolvedReference} objects aren't returned.
     */
    @Override
    public Stream<XsdAbstractElement> getXsdElements() {
        return elements.stream()
                .filter(element -> element instanceof ConcreteElement)
                .map(ReferenceBase::getElement);
    }

    public void addElement(XsdAbstractElement element){
        this.elements.add(ReferenceBase.createFromXsd(element));
    }

    @SuppressWarnings("unused")
    public Integer getMinOccurs() {
        return minOccurs;
    }

    @SuppressWarnings("unused")
    public String getMaxOccurs() {
        return maxOccurs;
    }

}
