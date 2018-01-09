package XsdElements;

import XsdElements.ElementsWrapper.ConcreteElement;
import XsdElements.ElementsWrapper.ReferenceBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public abstract class XsdMultipleElements extends XsdAbstractElement {

    /**
     * This Map contains the separated elements from XsdGroups. This way this class's elements
     * are divided into direct children, represented by the List elements and shared children,
     * represented by this Map. This way it simplifies the division of methods that will belong
     * to an interface and those that will be contained in the elements class.
     */
    private Map<String, List<ReferenceBase>> groupElements = new HashMap<>();

    /**
     * The elements List is a flattened list of all the children of a given XsdMultipleElement object
     * that aren't a xsd:group.
     */
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
            groupElements.put(elementWrapper.getName(), elementWrapper.getElement().getElements());
            addElements(elementWrapper.getElement().getElements());
        }
    }

    public String getMaxOccurs() {
        return maxOccurs;
    }

    public String getMinOccurs() {
        return minOccurs;
    }

    @Override
    protected List<ReferenceBase> getElements(){
        return elements;
    }

    void addElement(ReferenceBase element){
        this.elements.add(element);
    }

    void addElements(List<ReferenceBase> elements){
        this.elements.addAll(elements);
    }

    public Map<String, Stream<XsdElement>> getGroupElements(){
        Map<String, Stream<XsdElement>> concreteGroupElements = new HashMap<>();

        groupElements.keySet().forEach(groupElementName -> {
            concreteGroupElements.put(
                    groupElementName,
                    groupElements.get(groupElementName)
                            .stream()
                            .filter(groupElement -> groupElement instanceof ConcreteElement)
                            .map(groupElement -> (XsdElement) groupElement.getElement()));
        });

        return concreteGroupElements;
    }

}
