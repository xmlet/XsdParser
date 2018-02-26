package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.xsdelements.elementswrapper.ConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public abstract class XsdMultipleElements extends XsdAnnotatedElements {

    private List<ReferenceBase> elements = new ArrayList<>();

    private String maxOccurs;
    private Integer minOccurs;

    XsdMultipleElements(XsdAbstractElement parent, Map<String, String> nodeAttributes) {
        super(parent, nodeAttributes);
    }

    XsdMultipleElements(Map<String, String> nodeAttributes) {
        super(nodeAttributes);
    }

    @Override
    public void setFields(Map<String, String> elementFieldsMap) {
        super.setFields(elementFieldsMap);

        if (elementFieldsMap != null){
            this.minOccurs = Integer.parseInt(elementFieldsMap.getOrDefault(MIN_OCCURS, "1"));
            this.maxOccurs = elementFieldsMap.getOrDefault(MAX_OCCURS, "1");
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

    void addElement(XsdAbstractElement element){
        this.elements.add(ReferenceBase.createFromXsd(element));
    }

    void addElements(List<ReferenceBase> elements){
        this.elements.addAll(elements);
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
