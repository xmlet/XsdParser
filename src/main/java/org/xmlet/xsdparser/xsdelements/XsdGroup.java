package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdGroupVisitor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class XsdGroup extends XsdReferenceElement {

    public static final String XSD_TAG = "xsd:group";
    public static final String XS_TAG = "xs:group";

    private XsdGroupVisitor visitor = new XsdGroupVisitor(this);

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
    public void accept(XsdAbstractElementVisitor visitorParam) {
        super.accept(visitorParam);
        visitorParam.visit(this);
    }

    @Override
    public XsdGroupVisitor getVisitor() {
        return visitor;
    }

    @Override
    public List<ReferenceBase> getElements() {
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

    public void setChildElement(XsdMultipleElements childElement) {
        this.childElement = childElement;
        childElement.getElements().forEach(childElementObj -> childElementObj.getElement().setParent(childElement));
        this.childElement.setParent(this);
    }

    public XsdMultipleElements getChildElement() {
        return childElement;
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

}
