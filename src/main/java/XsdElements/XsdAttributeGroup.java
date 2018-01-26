package XsdElements;

import XsdElements.ElementsWrapper.ConcreteElement;
import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.Visitors.Visitor;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class XsdAttributeGroup extends XsdReferenceElement {

    public static final String XSD_TAG = "xsd:attributeGroup";
    public static final String XS_TAG = "xs:attributeGroup";

    private final AttributeGroupVisitor visitor = new AttributeGroupVisitor();

    private List<XsdAttributeGroup> attributeGroups = new ArrayList<>();
    private List<ReferenceBase> attributes = new ArrayList<>();

    private XsdAttributeGroup(XsdAbstractElement parent, HashMap<String, String> elementFieldsMap) {
        super(parent, elementFieldsMap);
    }

    private XsdAttributeGroup(HashMap<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
        this.setParent(visitor.getOwner());
    }

    @Override
    public Visitor getVisitor() {
        return visitor;
    }

    @Override
    protected List<ReferenceBase> getElements() {
        List<ReferenceBase> allAttributes = new ArrayList<>();

        attributeGroups.forEach(attributeGroup -> allAttributes.addAll(attributeGroup.getElements()));

        allAttributes.addAll(attributes);

        return allAttributes;
    }

    @Override
    public XsdAbstractElement createCopyWithAttributes(HashMap<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(this.getElementFieldsMap());
        XsdAttributeGroup elementCopy = new XsdAttributeGroup(this.getParent(), placeHolderAttributes);

        elementCopy.attributes.addAll(this.attributes);
        elementCopy.attributeGroups.addAll(this.getAttributeGroups());

        return elementCopy;
    }

    @Override
    public void replaceUnsolvedElements(ConcreteElement element) {
        if (element.getElement() instanceof  XsdAttributeGroup){
            XsdAttributeGroup attributeGroup = (XsdAttributeGroup) element.getElement();

            attributeGroup.attributes.forEach(attribute -> attribute.getElement().setParent(attributeGroup));

            this.attributeGroups.add(attributeGroup);
        }
    }

    private void addAttributes(ReferenceBase attribute) {
        this.attributes.add(attribute);
    }

    private void addAttributes(List<ReferenceBase> attributes) {
        this.attributes.addAll(attributes);
    }

    public static ReferenceBase parse(Node node) {
        return xsdParseSkeleton(node, new XsdAttributeGroup(convertNodeMap(node.getAttributes())));
    }

    public List<XsdAttributeGroup> getAttributeGroups() {
        return attributeGroups;
    }

    class AttributeGroupVisitor extends Visitor {

        @Override
        public XsdAbstractElement getOwner() {
            return XsdAttributeGroup.this;
        }

        @Override
        public void visit(XsdAttribute element) {
            super.visit(element);
            XsdAttributeGroup.this.addAttributes(ReferenceBase.createFromXsd(element));
        }
    }

}
