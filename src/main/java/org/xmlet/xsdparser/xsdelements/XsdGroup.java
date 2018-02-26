package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class XsdGroup extends XsdReferenceElement {

    public static final String XSD_TAG = "xsd:group";
    public static final String XS_TAG = "xs:group";

    private GroupXsdElementVisitor visitor = new GroupXsdElementVisitor();

    private XsdMultipleElements childElement;

    private Integer minOccurs;
    private String maxOccurs;

    private XsdGroup(XsdAbstractElement parent, Map<String, String> elementFieldsMap) {
        super(parent, elementFieldsMap);
    }

    private XsdGroup(Map<String, String> elementFieldsMap) {
        super(elementFieldsMap);
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
    public void accept(XsdElementVisitor xsdElementVisitor) {
        xsdElementVisitor.visit(this);
        this.setParent(xsdElementVisitor.getOwner());
    }

    @Override
    public GroupXsdElementVisitor getXsdElementVisitor() {
        return visitor;
    }

    @Override
    protected List<ReferenceBase> getElements() {
        List<ReferenceBase> list = new ArrayList<>();

        list.add(ReferenceBase.createFromXsd(childElement));

        return list;
    }

    @Override
    public XsdAbstractElement clone(Map<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(this.getElementFieldsMap());
        XsdGroup elementCopy = new XsdGroup(this.getParent(), placeHolderAttributes);

        if (childElement != null){
            elementCopy.setChildElement(this.childElement);
        }

        return elementCopy;
    }

    private void setChildElement(XsdMultipleElements childElement) {
        this.childElement = childElement;
        childElement.getElements().forEach(childElementObj -> childElementObj.getElement().setParent(childElement));
        this.childElement.setParent(this);
    }

    private XsdMultipleElements getChildElement() {
        return childElement;
    }

    public static ReferenceBase parse(Node node){
        return xsdParseSkeleton(node, new XsdGroup(convertNodeMap(node.getAttributes())));
    }

    @SuppressWarnings("unused")
    public Integer getMinOccurs() {
        return minOccurs;
    }

    @SuppressWarnings("unused")
    public String getMaxOccurs() {
        return maxOccurs;
    }

    class GroupXsdElementVisitor extends AnnotatedXsdElementVisitor {

        @Override
        public XsdAbstractElement getOwner() {
            return XsdGroup.this;
        }

        @Override
        public void visit(XsdMultipleElements element) {
            super.visit(element);

            XsdGroup.this.setChildElement(element);
        }
    }
}
