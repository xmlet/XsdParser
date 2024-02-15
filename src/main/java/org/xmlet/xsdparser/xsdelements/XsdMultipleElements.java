package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.core.XsdParserCore;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.NamedConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
    protected List<ReferenceBase> elements = new ArrayList<>();

    XsdMultipleElements(@NotNull XsdParserCore parser, @NotNull Map<String, String> elementFieldsMapParam, @NotNull Function<XsdAbstractElement, XsdAbstractElementVisitor> visitorFunction) {
        super(parser, elementFieldsMapParam, visitorFunction);
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

    /**
     * @return The children elements that are of the type {@link XsdElement}.
     */
    @SuppressWarnings("unused")
    public Stream<XsdElement> getChildrenElements() {
        return getXsdElements().filter(element -> element instanceof XsdElement).map(element -> (XsdElement) element);
    }

    public void addElement(XsdAbstractElement element){
        this.elements.add(ReferenceBase.createFromXsd(element));
    }

    /**
     * @param element The element containing the child to return.
     * @return The childElement as a {@link XsdAll} object or null if childElement isn't a {@link XsdAll} instance.
     */
    @SuppressWarnings("unused")
    public static XsdAll getChildAsdAll(XsdMultipleElements element) {
        return element instanceof XsdAll ? (XsdAll) element : null;
    }

    /**
     * @param element The element containing the child to return.
     * @return The childElement as a {@link XsdChoice} object or null if childElement isn't a {@link XsdChoice} instance.
     */
    @SuppressWarnings("unused")
    public static XsdChoice getChildAsChoice(XsdMultipleElements element) {
        return element instanceof XsdChoice ? (XsdChoice) element : null;
    }

    /**
     * @param element The element containing the child to return.
     * @return The childElement as a {@link XsdSequence} object or null if childElement isn't a {@link XsdSequence} instance.
     */
    @SuppressWarnings("unused")
    public static XsdSequence getChildAsSequence(XsdMultipleElements element) {
        return element instanceof XsdSequence ? (XsdSequence) element : null;
    }
}
