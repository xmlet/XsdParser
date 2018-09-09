package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.enums.BlockFinalEnum;
import org.xmlet.xsdparser.xsdelements.enums.EnumUtils;
import org.xmlet.xsdparser.xsdelements.enums.FinalDefaultEnum;
import org.xmlet.xsdparser.xsdelements.enums.FormEnum;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdSchemaVisitor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class XsdSchema extends XsdAnnotatedElements {

    public static final String XSD_TAG = "xsd:schema";
    public static final String XS_TAG = "xs:schema";

    /**
     * {@link XsdSchemaVisitor} which restricts its children to {@link XsdInclude}, {@link XsdImport},
     * {@link XsdAnnotation}, {@link XsdSimpleType}, {@link XsdComplexType}, {@link XsdGroup}, {@link XsdAttribute},
     * {@link XsdAttributeGroup} and {@link XsdElement} instances.
     */
    private XsdSchemaVisitor visitor = new XsdSchemaVisitor(this);

    /**
     * Specifies if the form attribute for the current {@link XsdSchema} children attributes. The default value is
     * "unqualified". Other possible value is "qualified".
     */
    private FormEnum attributeFormDefault;

    /**
     * Specifies if the form attribute for the current {@link XsdSchema} children elements. The default value is
     * "unqualified". Other possible value is "qualified".
     */
    private FormEnum elementFormDefault;

    /**
     * Specifies if the block attribute for the current {@link XsdSchema} children such as {@link XsdElement} and
     * {@link XsdComplexType}. The default value is "". Other possible value are "extension", "restriction",
     * "substitution" and "#all".
     */
    private BlockFinalEnum blockDefault;

    /**
     * Specifies if the final attribute for the current {@link XsdSchema} children such as {@link XsdElement},
     * {@link XsdSimpleType} and {@link XsdComplexType}. The default value is "". Other possible value are "extension",
     * "restriction", "list", "union" and "#all".
     */
    private FinalDefaultEnum finalDefault;

    /**
     * A URI reference of the namespace of this {@link XsdSchema} element.
     */
    private String targetNamespace;

    /**
     * The version of this {@link XsdSchema} element.
     */
    private String version;

    /**
     * A URI reference that specifies one or more namespaces for use in this {@link XsdSchema}. If no prefix is assigned,
     * the schema components of the namespace can be used with unqualified references.
     */
    private String xmlns;

    /**
     * The children elements contained in this {@link XsdSchema} element.
     */
    private List<XsdAbstractElement> elements = new ArrayList<>();

    private XsdSchema(@NotNull XsdParser parser, @NotNull Map<String, String> elementFieldsMapParam){
        super(parser, elementFieldsMapParam);
    }

    /**
     * Sets all the fields of the {@link XsdSchema} instance. Most values don't have default values except the
     * {@link XsdAttribute#use} field. Regarding the {@link XsdAttribute#type} field we check if the value present is a
     * built-in type, if not we create an {@link UnsolvedReference} object to be resolved at a later time in the
     * parsing process.
     * @param elementFieldsMapParam The Map object containing all the information previously contained in the parsed Node.
     */
    @Override
    public void setFields(@NotNull Map<String, String> elementFieldsMapParam) {
        super.setFields(elementFieldsMapParam);

        this.attributeFormDefault = EnumUtils.belongsToEnum(FormEnum.UNQUALIFIED, elementFieldsMap.getOrDefault(ATTRIBUTE_FORM_DEFAULT, FormEnum.UNQUALIFIED.getValue()));
        this.elementFormDefault = EnumUtils.belongsToEnum(FormEnum.UNQUALIFIED, elementFieldsMap.getOrDefault(ELEMENT_FORM_DEFAULT, FormEnum.UNQUALIFIED.getValue()));
        this.blockDefault = EnumUtils.belongsToEnum(BlockFinalEnum.DEFAULT, elementFieldsMap.getOrDefault(BLOCK_DEFAULT, BlockFinalEnum.DEFAULT.getValue()));
        this.finalDefault = EnumUtils.belongsToEnum(FinalDefaultEnum.DEFAULT, elementFieldsMap.getOrDefault(FINAL_DEFAULT, FinalDefaultEnum.DEFAULT.getValue()));
        this.targetNamespace = elementFieldsMap.getOrDefault(TARGET_NAMESPACE, targetNamespace);
        this.version = elementFieldsMap.getOrDefault(VERSION, version);
        this.xmlns = elementFieldsMap.getOrDefault(XMLNS, xmlns);
    }

    @Override
    public XsdAbstractElementVisitor getVisitor() {
        return visitor;
    }

    @Override
    public Stream<XsdAbstractElement> getXsdElements() {
        return elements.stream();
    }

    @Override
    public List<ReferenceBase> getElements() {
        return elements.stream().map(ReferenceBase::createFromXsd).collect(Collectors.toList());
    }

    public static ReferenceBase parse(@NotNull XsdParser parser, Node node) {
        return xsdParseSkeleton(node, new XsdSchema(parser, convertNodeMap(node.getAttributes())));
    }

    public void add(XsdInclude element) {
        elements.add(element);
    }

    public void add(XsdImport element) {
        elements.add(element);
    }

    public void add(XsdAnnotation element) {
        elements.add(element);
    }

    public void add(XsdSimpleType element) {
        elements.add(element);
    }

    public void add(XsdComplexType element) {
        elements.add(element);
    }

    public void add(XsdGroup element) {
        elements.add(element);
    }

    public void add(XsdAttributeGroup element) {
        elements.add(element);
    }

    public void add(XsdElement element) {
        elements.add(element);
    }

    public void add(XsdAttribute element) {
        elements.add(element);
    }

    @SuppressWarnings("unused")
    public String getAttributeFormDefault() {
        return attributeFormDefault.getValue();
    }

    @SuppressWarnings("unused")
    public String getElementFormDefault() {
        return elementFormDefault.getValue();
    }

    @SuppressWarnings("unused")
    public String getBlockDefault() {
        return blockDefault.getValue();
    }

    @SuppressWarnings("unused")
    public String getFinalDefault() {
        return finalDefault.getValue();
    }

    @SuppressWarnings("unused")
    public String getTargetNamespace() {
        return targetNamespace;
    }

    public String getVersion() {
        return version;
    }

    public String getXmlns() {
        return xmlns;
    }

    /**
     * @return The children elements that are of the type {@link XsdInclude}.
     */
    @SuppressWarnings("unused")
    public Stream<XsdInclude> getChildrenIncludes(){
        return getXsdElements()
                .filter(element -> element instanceof XsdInclude)
                .map(element -> (XsdInclude) element);
    }

    /**
     * @return The children elements that are of the type {@link XsdImport}.
     */
    @SuppressWarnings("unused")
    public Stream<XsdImport> getChildrenImports(){
        return getXsdElements()
                .filter(element -> element instanceof XsdImport)
                .map(element -> (XsdImport) element);
    }

    /**
     * @return The children elements that are of the type {@link XsdAnnotation}.
     */
    @SuppressWarnings("unused")
    public Stream<XsdAnnotation> getChildrenAnnotations(){
        return getXsdElements()
                .filter(element -> element instanceof XsdAnnotation)
                .map(element -> (XsdAnnotation) element);
    }

    /**
     * @return The children elements that are of the type {@link XsdSimpleType}.
     */
    @SuppressWarnings("unused")
    public Stream<XsdSimpleType> getChildrenSimpleTypes(){
        return getXsdElements()
                .filter(element -> element instanceof XsdSimpleType)
                .map(element -> (XsdSimpleType) element);
    }

    /**
     * @return The children elements that are of the type {@link XsdComplexType}.
     */
    @SuppressWarnings("unused")
    public Stream<XsdComplexType> getChildrenComplexTypes(){
        return getXsdElements()
                .filter(element -> element instanceof XsdComplexType)
                .map(element -> (XsdComplexType) element);
    }

    /**
     * @return The children elements that are of the type {@link XsdGroup}.
     */
    @SuppressWarnings("unused")
    public Stream<XsdGroup> getChildrenGroups(){
        return getXsdElements()
                .filter(element -> element instanceof XsdGroup)
                .map(element -> (XsdGroup) element);
    }

    /**
     * @return The children elements that are of the type {@link XsdAttributeGroup}.
     */
    @SuppressWarnings("unused")
    public Stream<XsdAttributeGroup> getChildrenAttributeGroups(){
        return getXsdElements()
                .filter(element -> element instanceof XsdAttributeGroup)
                .map(element -> (XsdAttributeGroup) element);
    }

    /**
     * @return The children elements that are of the type {@link XsdElement}.
     */
    @SuppressWarnings("unused")
    public Stream<XsdElement> getChildrenElements(){
        return getXsdElements()
                .filter(element -> element instanceof XsdElement)
                .map(element -> (XsdElement) element);
    }

    /**
     * @return The children elements that are of the type {@link XsdAttribute}.
     */
    @SuppressWarnings("unused")
    public Stream<XsdAttribute> getChildrenAttributes(){
        return getXsdElements()
                .filter(element -> element instanceof XsdAttribute)
                .map(element -> (XsdAttribute) element);
    }
}
