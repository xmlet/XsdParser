package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.core.XsdParserCore;
import org.xmlet.xsdparser.core.utils.ParseData;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.function.Function;

public class XsdAny extends XsdAnnotatedElements {

    public static final String XSD_TAG = "xsd:any";
    public static final String XS_TAG = "xs:any";
    public static final String TAG = "any";

    /**
     * Specifies the minimum number of times this element can occur in the parent element. The value can be any
     * number bigger or equal to 0. Default value is 1. This attribute cannot be used if the parent element is the
     * XsdSchema element.
     */
    private Integer minOccurs;

    /**
     * Specifies the maximum number of times this element can occur in the parent element. The value can be any
     * number bigger or equal to 0. Default value is 1. This attribute cannot be used if the parent element is the
     * XsdSchema element.
     */
    private String maxOccurs;

    private String namespace;

    private String processContents;



    private XsdAny(@NotNull XsdParserCore parser, @NotNull Map<String, String> attributesMap, @NotNull Function<XsdAbstractElement, XsdAbstractElementVisitor> visitorFunction){
        super(parser, attributesMap, visitorFunction);

        this.minOccurs = AttributeValidations.validateNonNegativeInteger(XSD_TAG, MIN_OCCURS_TAG, attributesMap.getOrDefault(MIN_OCCURS_TAG, "1"));
        this.maxOccurs = AttributeValidations.maxOccursValidation(XSD_TAG, attributesMap.getOrDefault(MAX_OCCURS_TAG, "1"));
        this.namespace = attributesMap.getOrDefault(NAMESPACE, "##any");
        this.processContents = attributesMap.getOrDefault(PROCESS_CONTENTS, "strict");
    }

    @Override
    public void accept(XsdAbstractElementVisitor visitorParam) {
        super.accept(visitorParam);
        visitorParam.visit(this);
    }

    public static ReferenceBase parse(@NotNull ParseData parseData){
        return xsdParseSkeleton(parseData.node, new XsdAny(parseData.parserInstance, convertNodeMap(parseData.node.getAttributes()), parseData.visitorFunction));
    }

    /**
     * Performs a copy of the current object for replacing purposes. The cloned objects are used to replace
     * {@link UnsolvedReference} objects in the reference solving process.
     * @param placeHolderAttributes The additional attributes to add to the clone.
     * @return A copy of the object from which is called upon.
     */
    @Override
    public XsdAny clone(@NotNull Map<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(attributesMap);

        XsdAny elementCopy = new XsdAny(this.getParser(), placeHolderAttributes, visitorFunction);

        elementCopy.cloneOf = this;
        elementCopy.setParent(null);

        return elementCopy;
    }

    @SuppressWarnings("unused")
    public Integer getMinOccurs() {
        return minOccurs;
    }

    @SuppressWarnings("unused")
    public String getMaxOccurs() {
        return maxOccurs;
    }

    @SuppressWarnings("unused")
    public String getNamespace() {
        return namespace;
    }

    @SuppressWarnings("unused")
    public String getProcessContents() {
        return processContents;
    }
}
