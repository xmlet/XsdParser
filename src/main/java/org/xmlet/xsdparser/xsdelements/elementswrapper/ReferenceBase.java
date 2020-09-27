package org.xmlet.xsdparser.xsdelements.elementswrapper;

import org.xmlet.xsdparser.core.XsdParserCore;
import org.xmlet.xsdparser.xsdelements.XsdAbstractElement;
import org.xmlet.xsdparser.xsdelements.XsdNamedElements;

import java.util.HashMap;
import java.util.Map;

import static org.xmlet.xsdparser.xsdelements.XsdAbstractElement.REF_TAG;
import static org.xmlet.xsdparser.xsdelements.XsdNamedElements.NAME_TAG;

/**
 * An abstract class that is meant to wrap all the {@link XsdAbstractElement} objects. Its hierarchy is meant to help
 * in the reference solving process.
 */
public abstract class ReferenceBase {

    protected XsdAbstractElement element;

    ReferenceBase(XsdAbstractElement element){
        this.element = element;
    }

    public XsdAbstractElement getElement(){
        return element;
    }

    /**
     * This method creates a ReferenceBase object that serves as a wrapper to {@link XsdAbstractElement} objects.
     * If a {@link XsdAbstractElement} has a ref attribute it results in a {@link UnsolvedReference} object. If it
     * doesn't have a ref attribute and has a name attribute it's a {@link NamedConcreteElement}. If it isn't a
     * {@link UnsolvedReference} or a {@link NamedConcreteElement} then it's a {@link ConcreteElement}.
     * @param element The element which will be "wrapped".
     * @return A wrapper object for the element received.
     */
    public static ReferenceBase createFromXsd(XsdAbstractElement element) {
        String ref = getRef(element);
        String name = getName(element);

        if (!(element instanceof XsdNamedElements)){
            return new ConcreteElement(element);
        }

        if (ref == null){
            if (name == null){
                return new ConcreteElement(element);
            } else {
                return new NamedConcreteElement((XsdNamedElements) element, name);
            }
        } else {
            return new UnsolvedReference((XsdNamedElements) element);
        }
    }

    public static ReferenceBase clone(XsdParserCore parser, ReferenceBase originalReference, XsdAbstractElement parent){
        if (originalReference == null){
            return null;
        }

        XsdAbstractElement originalElement = originalReference.getElement();

        if (originalReference instanceof UnsolvedReference){
            Map<String, String> originalElementAttributesMap = originalElement.getAttributesMap();
            HashMap<String, String> clonedOriginalElementAttributesMap = new HashMap<>(originalElementAttributesMap);
            clonedOriginalElementAttributesMap.put(REF_TAG, ((UnsolvedReference) originalReference).getRef());

            XsdNamedElements clonedElement = (XsdNamedElements) originalElement.clone(clonedOriginalElementAttributesMap);
            clonedElement.getAttributesMap().put(REF_TAG, ((UnsolvedReference) originalReference).getRef());
            clonedElement.setCloneOf(originalElement);
            clonedElement.setParent(parent);

            UnsolvedReference unsolvedClonedElement = new UnsolvedReference(clonedElement);

            parser.addUnsolvedReference(unsolvedClonedElement);

            return unsolvedClonedElement;
        }
        else {
            originalReference.getElement().setParentAvailable(false);

            return  originalReference;

//            XsdAbstractElement cloneElement = originalElement.clone(originalElement.getAttributesMap());
//            cloneElement.setParent(parent);
//
//            return createFromXsd(cloneElement);
        }
    }

    private static String getName(XsdAbstractElement element){
        return getNodeValue(element, NAME_TAG);
    }

    static String getRef(XsdAbstractElement element){
        return getNodeValue(element, REF_TAG);
    }

    /**
     * @param element The element that contains the attributes.
     * @param nodeName The attribute name that will be searched.
     * @return The value of the attribute contained in element with the name nodeName.
     */
    private static String getNodeValue(XsdAbstractElement element, String nodeName){
        return element.getAttributesMap().getOrDefault(nodeName, null);
    }
}
