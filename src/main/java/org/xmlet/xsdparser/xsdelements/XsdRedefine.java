package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.core.XsdParserCore;
import org.xmlet.xsdparser.core.utils.ParseData;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.exceptions.ParsingException;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;

import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A class representing the xsd:redefine element.
 *
 * The xsd:redefine element allows you to redefine simple and complex types, groups, and attribute groups
 * from an external schema file. It is similar to xsd:include but allows modifications to the included components.
 *
 * @see <a href="https://www.w3.org/TR/xmlschema-1/#modify-schema">xsd:redefine description at W3C</a>
 */
public class XsdRedefine extends XsdAnnotatedElements {

    public static final String XSD_TAG = "xsd:redefine";
    public static final String XS_TAG = "xs:redefine";
    public static final String TAG = "redefine";

    /**
     * Specifies the URI to the schema to be redefined.
     * This attribute is used to specify another file location that contains element definitions
     * that will be redefined in this element.
     */
    private String schemaLocation;

    /**
     * The redefinition elements contained in this {@link XsdRedefine} element.
     * These can be simpleType, complexType, group, or attributeGroup definitions that
     * override or extend the definitions in the included schema.
     */
    private List<XsdAbstractElement> redefinitions = new ArrayList<>();

    private XsdRedefine(@NotNull XsdParserCore parser, @NotNull Map<String, String> attributesMap, @NotNull Function<XsdAbstractElement, XsdAbstractElementVisitor> visitorFunction) {
        super(parser, attributesMap, visitorFunction);

        this.schemaLocation = attributesMap.getOrDefault(SCHEMA_LOCATION, schemaLocation);

        if (this.schemaLocation == null){
            throw new ParsingException(XSD_TAG + " element: The " + SCHEMA_LOCATION + " attribute is required.");
        }

        parser.addFileToParse(this.schemaLocation);
    }

    public static ReferenceBase parse(@NotNull ParseData parseData){
        return xsdParseSkeleton(parseData.node, new XsdRedefine(parseData.parserInstance, convertNodeMap(parseData.node.getAttributes()), parseData.visitorFunction));
    }

    @Override
    public void accept(XsdAbstractElementVisitor visitorParam) {
        super.accept(visitorParam);
        visitorParam.visit(this);
    }

    @Override
    public Stream<XsdAbstractElement> getXsdElements() {
        return redefinitions.stream();
    }

    public void add(XsdSimpleType element) {
        redefinitions.add(element);
    }

    public void add(XsdComplexType element) {
        redefinitions.add(element);
    }

    public void add(XsdGroup element) {
        redefinitions.add(element);
    }

    public void add(XsdAttributeGroup element) {
        redefinitions.add(element);
    }

    public void add(XsdAnnotation element) {
        redefinitions.add(element);
    }

    @SuppressWarnings("unused")
    public String getSchemaLocation() {
        return schemaLocation;
    }

    /**
     * @return The redefined simple types.
     */
    @SuppressWarnings("unused")
    public Stream<XsdSimpleType> getChildrenSimpleTypes(){
        return getXsdElements()
                .filter(element -> element instanceof XsdSimpleType)
                .map(element -> (XsdSimpleType) element);
    }

    /**
     * @return The redefined complex types.
     */
    @SuppressWarnings("unused")
    public Stream<XsdComplexType> getChildrenComplexTypes(){
        return getXsdElements()
                .filter(element -> element instanceof XsdComplexType)
                .map(element -> (XsdComplexType) element);
    }

    /**
     * @return The redefined groups.
     */
    @SuppressWarnings("unused")
    public Stream<XsdGroup> getChildrenGroups(){
        return getXsdElements()
                .filter(element -> element instanceof XsdGroup)
                .map(element -> (XsdGroup) element);
    }

    /**
     * @return The redefined attribute groups.
     */
    @SuppressWarnings("unused")
    public Stream<XsdAttributeGroup> getChildrenAttributeGroups(){
        return getXsdElements()
                .filter(element -> element instanceof XsdAttributeGroup)
                .map(element -> (XsdAttributeGroup) element);
    }

    /**
     * @return The annotations within this redefine element.
     */
    @SuppressWarnings("unused")
    public Stream<XsdAnnotation> getChildrenAnnotations(){
        return getXsdElements()
                .filter(element -> element instanceof XsdAnnotation)
                .map(element -> (XsdAnnotation) element);
    }
}
