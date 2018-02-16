package XsdElements;

import XsdElements.ElementsWrapper.ConcreteElement;
import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.ElementsWrapper.UnsolvedReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public abstract class XsdMultipleElements extends XsdAbstractElement {

    private List<ReferenceBase> elements = new ArrayList<>();

    private String maxOccurs;
    private String minOccurs;

    XsdMultipleElements(XsdAbstractElement parent, HashMap<String, String> nodeAttributes) {
        super(parent, nodeAttributes);
    }

    XsdMultipleElements(HashMap<String, String> nodeAttributes) {
        super(nodeAttributes);
    }

    @Override
    public void setFields(HashMap<String, String> elementFieldsMap) {
        super.setFields(elementFieldsMap);

        if (elementFieldsMap != null){
            this.minOccurs = elementFieldsMap.getOrDefault(MIN_OCCURS, minOccurs);
            this.maxOccurs = elementFieldsMap.getOrDefault(MAX_OCCURS, maxOccurs);
        }
    }

    @Override
    public void replaceUnsolvedElements(ConcreteElement elementWrapper) {
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

    @Override
    protected List<ReferenceBase> getElements(){
        return elements;
    }

    @Override
    public Stream<XsdAbstractElement> getXsdElements() {
        return elements.stream()
                .filter(element -> element instanceof ConcreteElement)
                .map(ReferenceBase::getElement);
    }

    void addElement(ReferenceBase element){
        this.elements.add(element);
    }

    void addElements(List<ReferenceBase> elements){
        this.elements.addAll(elements);
    }

}
