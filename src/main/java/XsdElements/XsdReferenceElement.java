package XsdElements;

import java.util.HashMap;

/**
 * This class is an abstraction of all classes that can have a ref/name attribute, which helps
 * distinguish those from the other XsdElements
 */
public abstract class XsdReferenceElement extends XsdAbstractElement {

    private String name;
    private String maxOccurs;
    private String minOccurs;

    XsdReferenceElement(XsdAbstractElement parent, HashMap<String, String> elementFieldsMap) {
        super(parent);

        setFields(elementFieldsMap);
    }

    XsdReferenceElement(HashMap<String, String> elementFieldsMap) {
        setFields(elementFieldsMap);
    }

    XsdReferenceElement(XsdAbstractElement parent) {
        super(parent);
    }

    @Override
    public void setFields(HashMap<String, String> elementFieldsMap) {
        super.setFields(elementFieldsMap);

        if (elementFieldsMap != null){
            this.name = elementFieldsMap.getOrDefault(NAME, name);
            this.minOccurs = elementFieldsMap.getOrDefault(MIN_OCCURS, minOccurs);
            this.maxOccurs = elementFieldsMap.getOrDefault(MAX_OCCURS, maxOccurs);
        }
    }

    /**
     * @return The name of the element, with all the special characters replaced with the '_' char.
     */
    public String getName() {
        return name == null ? null : name.replaceAll("[^a-zA-Z0-9]", "_");
    }

    public void setName(String name) {
        this.name = name;
    }
}
