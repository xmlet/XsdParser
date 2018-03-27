package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.elementswrapper.NamedConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAttributeGroupVisitor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class XsdAttributeGroup extends XsdReferenceElement {

    public static final String XSD_TAG = "xsd:attributeGroup";
    public static final String XS_TAG = "xs:attributeGroup";

    private final XsdAttributeGroupVisitor visitor = new XsdAttributeGroupVisitor(this);

    private List<XsdAttributeGroup> attributeGroups = new ArrayList<>();
    private List<ReferenceBase> attributes = new ArrayList<>();

    private XsdAttributeGroup(@NotNull Map<String, String> elementFieldsMapParam) {
        super(elementFieldsMapParam);
    }

    @Override
    public void accept(XsdAbstractElementVisitor visitorParam) {
        super.accept(visitorParam);
        visitorParam.visit(this);
    }

    @Override
    public XsdAttributeGroupVisitor getVisitor() {
        return visitor;
    }

    @Override
    public List<ReferenceBase> getElements() {
        List<ReferenceBase> allAttributes = new ArrayList<>();

        attributeGroups.forEach(attributeGroup -> allAttributes.addAll(attributeGroup.getElements()));

        allAttributes.addAll(attributes);

        return allAttributes;
    }

    @Override
    public XsdReferenceElement clone(@NotNull Map<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(elementFieldsMap);
        placeHolderAttributes.remove(REF_TAG);

        XsdAttributeGroup elementCopy = new XsdAttributeGroup(placeHolderAttributes);
        elementCopy.setParent(parent);

        elementCopy.attributes.addAll(this.attributes);
        elementCopy.attributeGroups.addAll(this.attributeGroups);

        return elementCopy;
    }

    @Override
    public void replaceUnsolvedElements(NamedConcreteElement element) {
        if (element.getElement() instanceof  XsdAttributeGroup){
            XsdAttributeGroup attributeGroup = (XsdAttributeGroup) element.getElement();

            attributeGroup.attributes.forEach(attribute -> attribute.getElement().setParent(attributeGroup));

            this.attributeGroups.add(attributeGroup);
        }
    }

    public List<XsdAttributeGroup> getAttributeGroups() {
        return attributeGroups;
    }

    public static ReferenceBase parse(Node node) {
        return xsdParseSkeleton(node, new XsdAttributeGroup(convertNodeMap(node.getAttributes())));
    }


    public void addAttribute(ReferenceBase attribute) {
        attributes.add(attribute);
    }
}
