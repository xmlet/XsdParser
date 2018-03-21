package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;

import javax.validation.constraints.NotNull;
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

    private XsdGroup(@NotNull Map<String, String> elementFieldsMapParam) {
        super(elementFieldsMapParam);
    }

    @Override
    public void setFields(@NotNull Map<String, String> elementFieldsMapParam) {
        super.setFields(elementFieldsMapParam);

        this.minOccurs = Integer.parseInt(elementFieldsMap.getOrDefault(MIN_OCCURS_TAG, "1"));
        this.maxOccurs = elementFieldsMap.getOrDefault(MAX_OCCURS_TAG, "1");
    }

    @Override
    public void accept(XsdElementVisitor xsdElementVisitor) {
        super.accept(xsdElementVisitor);
        xsdElementVisitor.visit(this);
    }

    @Override
    public GroupXsdElementVisitor getVisitor() {
        return visitor;
    }

    @Override
    protected List<ReferenceBase> getElements() {
        List<ReferenceBase> list = new ArrayList<>();

        if (childElement != null){
            list.add(ReferenceBase.createFromXsd(childElement));
        }

        return list;
    }

    @Override
    public XsdReferenceElement clone(@NotNull Map<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(elementFieldsMap);
        placeHolderAttributes.remove(REF_TAG);

        XsdGroup elementCopy = new XsdGroup(placeHolderAttributes);
        elementCopy.setParent(this.parent);

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

    public static ReferenceBase parse(Node node){
        return xsdParseSkeleton(node, new XsdGroup(convertNodeMap(node.getAttributes())));
    }

    public Integer getMinOccurs() {
        return minOccurs;
    }

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
