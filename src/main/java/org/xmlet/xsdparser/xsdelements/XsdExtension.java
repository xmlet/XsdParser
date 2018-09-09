package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.NamedConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.visitors.AttributesVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAnnotatedElementsVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdExtensionVisitor;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * A class representing the xsd:extension element.
 *
 * @see <a href="https://www.w3schools.com/xml/el_extension.asp">xsd:extension description and usage at w3c</a>
 */
public class XsdExtension extends XsdAnnotatedElements {

    public static final String XSD_TAG = "xsd:extension";
    public static final String XS_TAG = "xs:extension";

    /**
     * {@link XsdExtensionVisitor} instance which restricts the children to {@link XsdGroup} and
     * {@link XsdMultipleElements} instances.
     * Can also have {@link XsdAttribute} and {@link XsdAttributeGroup} elements as children as per inheritance of
     * {@link AttributesVisitor}.
     * Can also have {@link XsdAnnotation} as children as per inheritance of {@link XsdAnnotatedElementsVisitor}.
     */
    private XsdExtensionVisitor visitor = new XsdExtensionVisitor(this);

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

    private XsdExtension(@NotNull XsdParser parser, @NotNull Map<String, String> elementFieldsMapParam) {
        super(parser, elementFieldsMapParam);
    }

    /**
     * Uses the base attribute value to add an {@link UnsolvedReference} to resolve further in the parsing process.
     * @param elementFieldsMapParam The Map object containing the information previously present in the Node object.
     */
    @Override
    public void setFields(@NotNull Map<String, String> elementFieldsMapParam) {
        super.setFields(elementFieldsMapParam);

        String baseValue = elementFieldsMap.getOrDefault(BASE_TAG, null);

        if (baseValue != null){
            this.base = new UnsolvedReference(baseValue, new XsdElement(this, this.parser, new HashMap<>()));
            parser.addUnsolvedReference((UnsolvedReference) this.base);
        }
    }

    /**
     * This method should always receive two elements, one to replace the {@link UnsolvedReference} created due to
     * the value present in the base attribute and another if it has an {@link UnsolvedReference} as a child element.
     * @param element A concrete element with a name that will replace the {@link UnsolvedReference} object created in the
     *                {@link XsdExtension#setFields(Map)} method. The {@link UnsolvedReference} is only replaced if there
     *                is a match between the {@link UnsolvedReference#ref} and the {@link NamedConcreteElement#name}.
     */
    @Override
    public void replaceUnsolvedElements(NamedConcreteElement element) {
        super.replaceUnsolvedElements(element);

        XsdNamedElements elem = element.getElement();
        String elemName = elem.getRawName();

        if (this.base != null && this.base instanceof UnsolvedReference && elem instanceof XsdElement && ((UnsolvedReference) this.base).getRef().equals(elemName)){
            this.base = element;
        }

        if (this.childElement != null && this.childElement instanceof UnsolvedReference &&
                elem instanceof XsdGroup && ((UnsolvedReference) this.childElement).getRef().equals(elemName)){
            this.childElement = element;
        }

        visitor.replaceUnsolvedAttributes(element);
    }

    @Override
    public XsdExtensionVisitor getVisitor() {
        return visitor;
    }

    @Override
    public void accept(XsdAbstractElementVisitor visitorParam) {
        super.accept(visitorParam);
        visitorParam.visit(this);
    }

    /**
     * @return Its children elements as his own.
     */
    @Override
    public List<ReferenceBase> getElements() {
        return childElement == null ? Collections.emptyList() : childElement.getElement().getElements();
    }

    /**
     * @return The {@link XsdElement} from which it extends or null if the {@link XsdParser} wasn't able to replace
     * the {@link UnsolvedReference} created by the base attribute value.
     */
    public XsdElement getBase() {
        return base instanceof ConcreteElement ? (XsdElement) base.getElement() : null;
    }

    public static ReferenceBase parse(@NotNull XsdParser parser, Node node){
        return xsdParseSkeleton(node, new XsdExtension(parser, convertNodeMap(node.getAttributes())));
    }

    @SuppressWarnings("unused")
    public Stream<XsdAttribute> getXsdAttributes() {
        return visitor.getXsdAttributes();
    }

    @SuppressWarnings("unused")
    public Stream<XsdAttributeGroup> getXsdAttributeGroup() {
        return visitor.getXsdAttributeGroup();
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
     * @return The childElement as a XsdGroup object or null if childElement isn't a XsdGroup instance.
     */
    @SuppressWarnings("unused")
    public XsdGroup getChildAsGroup() {
        return childElement.getElement() instanceof XsdGroup ? (XsdGroup) childElement.getElement() : null;
    }

    /**
     * @return The childElement as a XsdAll object or null if childElement isn't a XsdAll instance.
     */
    @SuppressWarnings("unused")
    public XsdAll getChildAsdAll() {
        return childrenIsMultipleElement() ? XsdMultipleElements.getChildAsdAll((XsdMultipleElements) childElement.getElement()) : null;
    }

    /**
     * @return The childElement as a XsdChoice object or null if childElement isn't a XsdChoice instance.
     */
    @SuppressWarnings("unused")
    public XsdChoice getChildAsChoice() {
        return childrenIsMultipleElement() ? XsdMultipleElements.getChildAsChoice((XsdMultipleElements) childElement.getElement()) : null;
    }

    /**
     * @return The childElement as a XsdSequence object or null if childElement isn't a XsdSequence instance.
     */
    @SuppressWarnings("unused")
    public XsdSequence getChildAsSequence() {
        return childrenIsMultipleElement() ? XsdMultipleElements.getChildAsSequence((XsdMultipleElements) childElement.getElement()) : null;
    }

    private boolean childrenIsMultipleElement(){
        return childElement.getElement() instanceof XsdMultipleElements;
    }
}
