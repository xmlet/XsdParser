package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.core.XsdParserCore;
import org.xmlet.xsdparser.core.utils.ParseData;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A class representing the xsd:union element.
 *
 * @see <a href="https://www.w3schools.com/xml/el_union.asp">xsd:union description and usage at w3c</a>
 */
public class XsdUnion extends XsdAnnotatedElements {

    public static final String XSD_TAG = "xsd:union";
    public static final String XS_TAG = "xs:union";

    /**
     * A List of {@link XsdSimpleType} instances that represent the {@link XsdUnion}.
     */
    private List<XsdSimpleType> simpleTypeList = new ArrayList<>();

    /**
     * Specifies a list of built-in data types or {@link XsdSimpleType} instance names defined in a XsdSchema.
     */
    private String memberTypes;

    private XsdUnion(@NotNull XsdParserCore parser, @NotNull Map<String, String> attributesMap, @NotNull Function<XsdAbstractElement, XsdAbstractElementVisitor> visitorFunction) {
        super(parser, attributesMap, visitorFunction);

        this.memberTypes = attributesMap.getOrDefault(MEMBER_TYPES_TAG, memberTypes);
    }

    @Override
    public void accept(XsdAbstractElementVisitor visitorParam) {
        super.accept(visitorParam);
        visitorParam.visit(this);
    }

    /**
     * Performs a copy of the current object for replacing purposes. The cloned objects are used to replace
     * {@link UnsolvedReference} objects in the reference solving process.
     * @param placeHolderAttributes The additional attributes to add to the clone.
     * @return A copy of the object from which is called upon.
     */
    @Override
    public XsdUnion clone(@NotNull Map<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(attributesMap);

        XsdUnion elementCopy = new XsdUnion(this.parser, placeHolderAttributes, visitorFunction);

        if (this.simpleTypeList != null){
            elementCopy.simpleTypeList = this.simpleTypeList.stream().map(simpleType -> (XsdSimpleType) simpleType.clone(simpleType.getAttributesMap(), elementCopy)).collect(Collectors.toList());
        }

        elementCopy.parent = null;

        return elementCopy;
    }

    public List<XsdSimpleType> getUnionElements(){
        return simpleTypeList;
    }

    @SuppressWarnings("unused")
    public List<String> getMemberTypesList() {
        return Arrays.asList(memberTypes.split(" "));
    }

    public static ReferenceBase parse(@NotNull  ParseData parseData){
        return xsdParseSkeleton(parseData.node, new XsdUnion(parseData.parserInstance, convertNodeMap(parseData.node.getAttributes()), parseData.visitorFunction));
    }

    public void add(XsdSimpleType simpleType) {
        simpleTypeList.add(simpleType);
    }
}
