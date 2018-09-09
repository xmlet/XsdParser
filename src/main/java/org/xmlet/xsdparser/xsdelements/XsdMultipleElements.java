package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.core.XsdParser;
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
 * {@link XsdSequence}. Those three classes share {@link XsdMultipleElements#elements} which is a list of
 * {@link XsdAbstractElement} objects contained in each of these types. The types of the instances present in the
 * {@link XsdMultipleElements#elements} list depends on the concrete type, {@link XsdAll}, {@link XsdChoice} or
 * {@link XsdSequence}.
 */
public abstract class XsdMultipleElements extends XsdAnnotatedElements {

    /**
     * A list of elements that are contained in the concrete implementation of the {@link XsdMultipleElements} instance.
     */
    private List<ReferenceBase> elements = new ArrayList<>();

    XsdMultipleElements(@NotNull XsdParser parser, @NotNull Map<String, String> elementFieldsMapParam) {
        super(parser, elementFieldsMapParam);
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

    /**
     * @return The childElement as a XsdAll object or null if childElement isn't a XsdAll instance.
     */
    @SuppressWarnings("unused")
    public static XsdAll getChildAsdAll(XsdMultipleElements element) {
        return element instanceof XsdAll ? (XsdAll) element : null;
    }

    /**
     * @return The childElement as a XsdChoice object or null if childElement isn't a XsdChoice instance.
     */
    @SuppressWarnings("unused")
    public static XsdChoice getChildAsChoice(XsdMultipleElements element) {
        return element instanceof XsdChoice ? (XsdChoice) element : null;
    }

    /**
     * @return The childElement as a XsdSequence object or null if childElement isn't a XsdSequence instance.
     */
    @SuppressWarnings("unused")
    public static XsdSequence getChildAsSequence(XsdMultipleElements element) {
        return element instanceof XsdSequence ? (XsdSequence) element : null;
    }
}
