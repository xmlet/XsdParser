package XsdElements.ElementsWrapper;

import XsdElements.XsdAbstractElement;

/**
 * ConcreteElement is a wrapper class for XsdAbstractElement which is fully resolved
 */
public class ConcreteElement extends ReferenceBase {

    private String name;
    private XsdAbstractElement element;

    ConcreteElement(XsdAbstractElement element){
        this.name = getName(element);
        this.element = element;
    }

    public String getName() {
        return name;
    }

    public XsdAbstractElement getElement() {
        return element;
    }

}
