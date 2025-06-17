package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.core.XsdParserCore;
import org.xmlet.xsdparser.core.utils.NamespaceInfo;
import org.xmlet.xsdparser.core.utils.ParseData;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.enums.BlockDefaultEnum;
import org.xmlet.xsdparser.xsdelements.enums.FinalDefaultEnum;
import org.xmlet.xsdparser.xsdelements.enums.FormEnum;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;

import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class XsdSchema extends XsdAnnotatedElements {

    public static final String XSD_TAG = "xsd:schema";
    public static final String XS_TAG = "xs:schema";
    public static final String TAG = "schema";

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
    private BlockDefaultEnum blockDefault;

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

    private String filePath;

    private Map<String, NamespaceInfo> namespaces = new HashMap<>();

    /**
     * The children elements contained in this {@link XsdSchema} element.
     */
    private List<XsdAbstractElement> elements = new ArrayList<>();

    private XsdSchema(@NotNull XsdParserCore parser, @NotNull Map<String, String> attributesMap, @NotNull Function<XsdAbstractElement, XsdAbstractElementVisitor> visitorFunction){
        super(parser, attributesMap, visitorFunction);

        this.attributeFormDefault = AttributeValidations.belongsToEnum(FormEnum.UNQUALIFIED, attributesMap.getOrDefault(ATTRIBUTE_FORM_DEFAULT, FormEnum.UNQUALIFIED.getValue()));
        this.elementFormDefault = AttributeValidations.belongsToEnum(FormEnum.UNQUALIFIED, attributesMap.getOrDefault(ELEMENT_FORM_DEFAULT, FormEnum.UNQUALIFIED.getValue()));
        this.blockDefault = AttributeValidations.belongsToEnum(BlockDefaultEnum.DEFAULT, attributesMap.getOrDefault(BLOCK_DEFAULT, BlockDefaultEnum.DEFAULT.getValue()));
        this.finalDefault = AttributeValidations.belongsToEnum(FinalDefaultEnum.instance, attributesMap.getOrDefault(FINAL_DEFAULT, FinalDefaultEnum.DEFAULT.getValue()));
        this.targetNamespace = attributesMap.getOrDefault(TARGET_NAMESPACE, targetNamespace);
        this.version = attributesMap.getOrDefault(VERSION, version);
        this.xmlns = attributesMap.getOrDefault(XMLNS, xmlns);

        for (String key : attributesMap.keySet()){
            if (key.startsWith(XMLNS) && !key.equals("xmlns:xs") && !key.equals("xmlns:xsd")/*&& !attributesMap.get(key).contains("http")*/){
                String namespaceId = key.replace(XMLNS + ":", "");
                namespaces.put(namespaceId, new NamespaceInfo(attributesMap.get(key)));
            }
        }
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

    public static ReferenceBase parse(@NotNull ParseData parseData) {
        ReferenceBase xsdSchemaRef = xsdParseSkeleton(parseData.node, new XsdSchema(parseData.parserInstance, convertNodeMap(parseData.node.getAttributes()), parseData.visitorFunction));
        XsdSchema xsdSchema = (XsdSchema) xsdSchemaRef.getElement();

        List<XsdImport> importsList = xsdSchema.getChildrenImports().collect(Collectors.toList());

        Map<String, String> prefixLocations = new HashMap<>();

        xsdSchema.getNamespaces()
                 .forEach((prefix, namespaceInfo) -> importsList.stream()
                         .filter(xsdImportObj -> xsdImportObj.getNamespace().equals(namespaceInfo.getName()))
                         .findFirst()
                         .ifPresent(anImport -> prefixLocations.put(prefix, anImport.getSchemaLocation())));

        xsdSchema.updatePrefixLocations(prefixLocations);

        return xsdSchemaRef;
    }

    private void updatePrefixLocations(Map<String, String> prefixLocations) {
        prefixLocations.forEach((prefix, fileLocation) ->
            namespaces.get(prefix).setFile(fileLocation)
        );
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

    public void resolveNameSpace(String namespace, String schemaLocation){
        if (namespaces.containsKey(namespace)){
            NamespaceInfo namespaceInfo = namespaces.get(namespace);

            if (namespaceInfo.getFile() == null){
                namespaceInfo.setFile(schemaLocation);
            }
        }
    }

    public Map<String, NamespaceInfo> getNamespaces() {
        return namespaces;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
