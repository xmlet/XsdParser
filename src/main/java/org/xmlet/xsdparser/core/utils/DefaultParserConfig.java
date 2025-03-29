package org.xmlet.xsdparser.core.utils;

import org.xmlet.xsdparser.xsdelements.*;
import org.xmlet.xsdparser.xsdelements.visitors.*;
import org.xmlet.xsdparser.xsdelements.xsdrestrictions.*;

import java.util.HashMap;
import java.util.Map;

public class DefaultParserConfig implements ParserConfig {
    @Override
    public Map<String, String> getXsdTypesToJava() {
        HashMap<String, String> xsdTypesToJava = new HashMap<>();

        String string = "String";
        String xmlGregorianCalendar = "XMLGregorianCalendar";
        String duration = "Duration";
        String bigInteger = "BigInteger";
        String integer = "Integer";
        String shortString = "Short";
        String qName = "QName";
        String longString = "Long";
        String byteString = "Byte";

        xsdTypesToJava.put("ENTITY", string);
        xsdTypesToJava.put("ID", string);
        xsdTypesToJava.put("IDREF", string);
        xsdTypesToJava.put("NCName", string);
        xsdTypesToJava.put("NMTOKEN", string);
        xsdTypesToJava.put("NOTATION", qName);
        xsdTypesToJava.put("Name", string);
        xsdTypesToJava.put("QName", qName);
        xsdTypesToJava.put("anyURI", string);
        xsdTypesToJava.put("boolean", "Boolean");
        xsdTypesToJava.put("byte", byteString);
        xsdTypesToJava.put("date", xmlGregorianCalendar);
        xsdTypesToJava.put("dateTime", xmlGregorianCalendar);
        xsdTypesToJava.put("dayTimeDuration", duration);
        xsdTypesToJava.put("decimal", "BigDecimal");
        xsdTypesToJava.put("double", "Double");
        xsdTypesToJava.put("duration", duration);
        xsdTypesToJava.put("float", "Float");
        xsdTypesToJava.put("gDay", xmlGregorianCalendar);
        xsdTypesToJava.put("gMonth", xmlGregorianCalendar);
        xsdTypesToJava.put("gMonthDay", xmlGregorianCalendar);
        xsdTypesToJava.put("gYear", xmlGregorianCalendar);
        xsdTypesToJava.put("gYearMonth", xmlGregorianCalendar);
        xsdTypesToJava.put("int", integer);
        xsdTypesToJava.put("integer", bigInteger);
        xsdTypesToJava.put("language", string);
        xsdTypesToJava.put("long", longString);
        xsdTypesToJava.put("negativeInteger", bigInteger);
        xsdTypesToJava.put("nonNegativeInteger", bigInteger);
        xsdTypesToJava.put("nonPositiveInteger", bigInteger);
        xsdTypesToJava.put("normalizedString", string);
        xsdTypesToJava.put("positiveInteger", bigInteger);
        xsdTypesToJava.put("short", shortString);
        xsdTypesToJava.put("string", string);
        xsdTypesToJava.put("time", xmlGregorianCalendar);
        xsdTypesToJava.put("token", string);
        xsdTypesToJava.put("unsignedByte", shortString);
        xsdTypesToJava.put("unsignedInt", longString);
        xsdTypesToJava.put("unsignedLong", bigInteger);
        xsdTypesToJava.put("unsignedShort", integer);
        xsdTypesToJava.put("untypedAtomic", string);
        xsdTypesToJava.put("yearMonthDuration", duration);

        xsdTypesToJava.put("xs:ENTITY", string);
        xsdTypesToJava.put("xs:ID", string);
        xsdTypesToJava.put("xs:IDREF", string);
        xsdTypesToJava.put("xs:NCName", string);
        xsdTypesToJava.put("xs:NMTOKEN", string);
        xsdTypesToJava.put("xs:NOTATION", qName);
        xsdTypesToJava.put("xs:Name", string);
        xsdTypesToJava.put("xs:QName", qName);
        xsdTypesToJava.put("xs:anyURI", string);
        xsdTypesToJava.put("xs:boolean", "Boolean");
        xsdTypesToJava.put("xs:byte", byteString);
        xsdTypesToJava.put("xs:date", xmlGregorianCalendar);
        xsdTypesToJava.put("xs:dateTime", xmlGregorianCalendar);
        xsdTypesToJava.put("xs:dayTimeDuration", duration);
        xsdTypesToJava.put("xs:decimal", "BigDecimal");
        xsdTypesToJava.put("xs:double", "Double");
        xsdTypesToJava.put("xs:duration", duration);
        xsdTypesToJava.put("xs:float", "Float");
        xsdTypesToJava.put("xs:gDay", xmlGregorianCalendar);
        xsdTypesToJava.put("xs:gMonth", xmlGregorianCalendar);
        xsdTypesToJava.put("xs:gMonthDay", xmlGregorianCalendar);
        xsdTypesToJava.put("xs:gYear", xmlGregorianCalendar);
        xsdTypesToJava.put("xs:gYearMonth", xmlGregorianCalendar);
        xsdTypesToJava.put("xs:int", integer);
        xsdTypesToJava.put("xs:integer", bigInteger);
        xsdTypesToJava.put("xs:language", string);
        xsdTypesToJava.put("xs:long", longString);
        xsdTypesToJava.put("xs:negativeInteger", bigInteger);
        xsdTypesToJava.put("xs:nonNegativeInteger", bigInteger);
        xsdTypesToJava.put("xs:nonPositiveInteger", bigInteger);
        xsdTypesToJava.put("xs:normalizedString", string);
        xsdTypesToJava.put("xs:positiveInteger", bigInteger);
        xsdTypesToJava.put("xs:short", shortString);
        xsdTypesToJava.put("xs:string", string);
        xsdTypesToJava.put("xs:time", xmlGregorianCalendar);
        xsdTypesToJava.put("xs:token", string);
        xsdTypesToJava.put("xs:unsignedByte", shortString);
        xsdTypesToJava.put("xs:unsignedInt", longString);
        xsdTypesToJava.put("xs:unsignedLong", bigInteger);
        xsdTypesToJava.put("xs:unsignedShort", integer);
        xsdTypesToJava.put("xs:untypedAtomic", string);
        xsdTypesToJava.put("xs:yearMonthDuration", duration);

        xsdTypesToJava.put("xsd:ENTITY", string);
        xsdTypesToJava.put("xsd:ID", string);
        xsdTypesToJava.put("xsd:IDREF", string);
        xsdTypesToJava.put("xsd:NCName", string);
        xsdTypesToJava.put("xsd:NMTOKEN", string);
        xsdTypesToJava.put("xsd:NOTATION", qName);
        xsdTypesToJava.put("xsd:Name", string);
        xsdTypesToJava.put("xsd:QName", qName);
        xsdTypesToJava.put("xsd:anyURI", string);
        xsdTypesToJava.put("xsd:boolean", "Boolean");
        xsdTypesToJava.put("xsd:byte", byteString);
        xsdTypesToJava.put("xsd:date", xmlGregorianCalendar);
        xsdTypesToJava.put("xsd:dateTime", xmlGregorianCalendar);
        xsdTypesToJava.put("xsd:dayTimeDuration", duration);
        xsdTypesToJava.put("xsd:decimal", "BigDecimal");
        xsdTypesToJava.put("xsd:double", "Double");
        xsdTypesToJava.put("xsd:duration", duration);
        xsdTypesToJava.put("xsd:float", "Float");
        xsdTypesToJava.put("xsd:gDay", xmlGregorianCalendar);
        xsdTypesToJava.put("xsd:gMonth", xmlGregorianCalendar);
        xsdTypesToJava.put("xsd:gMonthDay", xmlGregorianCalendar);
        xsdTypesToJava.put("xsd:gYear", xmlGregorianCalendar);
        xsdTypesToJava.put("xsd:gYearMonth", xmlGregorianCalendar);
        xsdTypesToJava.put("xsd:int", integer);
        xsdTypesToJava.put("xsd:integer", bigInteger);
        xsdTypesToJava.put("xsd:language", string);
        xsdTypesToJava.put("xsd:long", longString);
        xsdTypesToJava.put("xsd:negativeInteger", bigInteger);
        xsdTypesToJava.put("xsd:nonNegativeInteger", bigInteger);
        xsdTypesToJava.put("xsd:nonPositiveInteger", bigInteger);
        xsdTypesToJava.put("xsd:normalizedString", string);
        xsdTypesToJava.put("xsd:positiveInteger", bigInteger);
        xsdTypesToJava.put("xsd:short", shortString);
        xsdTypesToJava.put("xsd:string", string);
        xsdTypesToJava.put("xsd:time", xmlGregorianCalendar);
        xsdTypesToJava.put("xsd:token", string);
        xsdTypesToJava.put("xsd:unsignedByte", shortString);
        xsdTypesToJava.put("xsd:unsignedInt", longString);
        xsdTypesToJava.put("xsd:unsignedLong", bigInteger);
        xsdTypesToJava.put("xsd:unsignedShort", integer);
        xsdTypesToJava.put("xsd:untypedAtomic", string);
        xsdTypesToJava.put("xsd:yearMonthDuration", duration);

        return xsdTypesToJava;
    }

    @Override
    public Map<String, ConfigEntryData> getParseMappers() {
        Map<String, ConfigEntryData> parseMappers = new HashMap<>();

        parseMappers.put(XsdSchema.TAG, new ConfigEntryData(XsdSchema::parse, elem -> new XsdSchemaVisitor((XsdSchema) elem)));
        parseMappers.put(XsdAll.TAG, new ConfigEntryData(XsdAll::parse, elem -> new XsdAllVisitor((XsdAll) elem)));
        parseMappers.put(XsdAny.TAG, new ConfigEntryData(XsdAny::parse, elem -> new XsdAnyVisitor((XsdAny) elem)));
        parseMappers.put(XsdAttribute.TAG, new ConfigEntryData(XsdAttribute::parse, elem -> new XsdAttributeVisitor((XsdAttribute) elem)));
        parseMappers.put(XsdAttributeGroup.TAG, new ConfigEntryData(XsdAttributeGroup::parse, elem -> new XsdAttributeGroupVisitor((XsdAttributeGroup) elem)));
        parseMappers.put(XsdChoice.TAG, new ConfigEntryData(XsdChoice::parse, elem -> new XsdChoiceVisitor((XsdChoice) elem)));
        parseMappers.put(XsdComplexType.TAG, new ConfigEntryData(XsdComplexType::parse, elem -> new XsdComplexTypeVisitor((XsdComplexType) elem)));
        parseMappers.put(XsdElement.TAG, new ConfigEntryData(XsdElement::parse, elem -> new XsdElementVisitor((XsdElement) elem)));
        parseMappers.put(XsdGroup.TAG, new ConfigEntryData(XsdGroup::parse, elem -> new XsdGroupVisitor((XsdGroup) elem)));
        parseMappers.put(XsdInclude.TAG, new ConfigEntryData(XsdInclude::parse, elem -> new XsdAnnotatedElementsVisitor((XsdInclude) elem)));
        parseMappers.put(XsdImport.TAG, new ConfigEntryData(XsdImport::parse, elem -> new XsdAnnotatedElementsVisitor((XsdImport) elem)));
        parseMappers.put(XsdSequence.TAG, new ConfigEntryData(XsdSequence::parse, elem -> new XsdSequenceVisitor((XsdSequence) elem)));
        parseMappers.put(XsdSimpleType.TAG, new ConfigEntryData(XsdSimpleType::parse, elem -> new XsdSimpleTypeVisitor((XsdSimpleType) elem)));
        parseMappers.put(XsdList.TAG, new ConfigEntryData(XsdList::parse, elem -> new XsdListVisitor((XsdList) elem)));
        parseMappers.put(XsdRestriction.TAG, new ConfigEntryData(XsdRestriction::parse, elem -> new XsdRestrictionsVisitor((XsdRestriction) elem)));
        parseMappers.put(XsdUnion.TAG, new ConfigEntryData(XsdUnion::parse, elem -> new XsdUnionVisitor((XsdUnion) elem)));

        parseMappers.put(XsdAnnotation.TAG, new ConfigEntryData(XsdAnnotation::parse, elem -> new XsdAnnotationVisitor((XsdAnnotation) elem)));
        parseMappers.put(XsdAppInfo.TAG, new ConfigEntryData(XsdAppInfo::parse, null));
        parseMappers.put(XsdComplexContent.TAG, new ConfigEntryData(XsdComplexContent::parse, elem -> new XsdComplexContentVisitor((XsdComplexContent) elem)));
        parseMappers.put(XsdDocumentation.TAG, new ConfigEntryData(XsdDocumentation::parse, null));
        parseMappers.put(XsdExtension.TAG, new ConfigEntryData(XsdExtension::parse, elem -> new XsdExtensionVisitor((XsdExtension) elem)));
        parseMappers.put(XsdSimpleContent.TAG, new ConfigEntryData(XsdSimpleContent::parse, elem -> new XsdSimpleContentVisitor((XsdSimpleContent) elem)));

        parseMappers.put(XsdEnumeration.TAG, new ConfigEntryData(XsdEnumeration::parse, elem -> new XsdAnnotatedElementsVisitor((XsdEnumeration) elem)));
        parseMappers.put(XsdFractionDigits.TAG, new ConfigEntryData(XsdFractionDigits::parse, elem -> new XsdAnnotatedElementsVisitor((XsdFractionDigits) elem)));
        parseMappers.put(XsdLength.TAG, new ConfigEntryData(XsdLength::parse, elem -> new XsdAnnotatedElementsVisitor((XsdLength) elem)));
        parseMappers.put(XsdMaxExclusive.TAG, new ConfigEntryData(XsdMaxExclusive::parse, elem -> new XsdAnnotatedElementsVisitor((XsdMaxExclusive) elem)));
        parseMappers.put(XsdMaxInclusive.TAG, new ConfigEntryData(XsdMaxInclusive::parse, elem -> new XsdAnnotatedElementsVisitor((XsdMaxInclusive) elem)));
        parseMappers.put(XsdMaxLength.TAG, new ConfigEntryData(XsdMaxLength::parse, elem -> new XsdAnnotatedElementsVisitor((XsdMaxLength) elem)));
        parseMappers.put(XsdMinExclusive.TAG, new ConfigEntryData(XsdMinExclusive::parse, elem -> new XsdAnnotatedElementsVisitor((XsdMinExclusive) elem)));
        parseMappers.put(XsdMinInclusive.TAG, new ConfigEntryData(XsdMinInclusive::parse, elem -> new XsdAnnotatedElementsVisitor((XsdMinInclusive) elem)));
        parseMappers.put(XsdMinLength.TAG, new ConfigEntryData(XsdMinLength::parse, elem -> new XsdAnnotatedElementsVisitor((XsdMinLength) elem)));
        parseMappers.put(XsdPattern.TAG, new ConfigEntryData(XsdPattern::parse, elem -> new XsdAnnotatedElementsVisitor((XsdPattern) elem)));
        parseMappers.put(XsdTotalDigits.TAG, new ConfigEntryData(XsdTotalDigits::parse, elem -> new XsdAnnotatedElementsVisitor((XsdTotalDigits) elem)));
        parseMappers.put(XsdWhiteSpace.TAG, new ConfigEntryData(XsdWhiteSpace::parse, elem -> new XsdAnnotatedElementsVisitor((XsdWhiteSpace) elem)));


        //XS
        parseMappers.put(XsdSchema.XS_TAG, new ConfigEntryData(XsdSchema::parse, elem -> new XsdSchemaVisitor((XsdSchema) elem)));
        parseMappers.put(XsdAll.XS_TAG, new ConfigEntryData(XsdAll::parse, elem -> new XsdAllVisitor((XsdAll) elem)));
        parseMappers.put(XsdAny.XS_TAG, new ConfigEntryData(XsdAny::parse, elem -> new XsdAnyVisitor((XsdAny) elem)));
        parseMappers.put(XsdAttribute.XS_TAG, new ConfigEntryData(XsdAttribute::parse, elem -> new XsdAttributeVisitor((XsdAttribute) elem)));
        parseMappers.put(XsdAttributeGroup.XS_TAG, new ConfigEntryData(XsdAttributeGroup::parse, elem -> new XsdAttributeGroupVisitor((XsdAttributeGroup) elem)));
        parseMappers.put(XsdChoice.XS_TAG, new ConfigEntryData(XsdChoice::parse, elem -> new XsdChoiceVisitor((XsdChoice) elem)));
        parseMappers.put(XsdComplexType.XS_TAG, new ConfigEntryData(XsdComplexType::parse, elem -> new XsdComplexTypeVisitor((XsdComplexType) elem)));
        parseMappers.put(XsdElement.XS_TAG, new ConfigEntryData(XsdElement::parse, elem -> new XsdElementVisitor((XsdElement) elem)));
        parseMappers.put(XsdGroup.XS_TAG, new ConfigEntryData(XsdGroup::parse, elem -> new XsdGroupVisitor((XsdGroup) elem)));
        parseMappers.put(XsdInclude.XS_TAG, new ConfigEntryData(XsdInclude::parse, elem -> new XsdAnnotatedElementsVisitor((XsdInclude) elem)));
        parseMappers.put(XsdImport.XS_TAG, new ConfigEntryData(XsdImport::parse, elem -> new XsdAnnotatedElementsVisitor((XsdImport) elem)));
        parseMappers.put(XsdSequence.XS_TAG, new ConfigEntryData(XsdSequence::parse, elem -> new XsdSequenceVisitor((XsdSequence) elem)));
        parseMappers.put(XsdSimpleType.XS_TAG, new ConfigEntryData(XsdSimpleType::parse, elem -> new XsdSimpleTypeVisitor((XsdSimpleType) elem)));
        parseMappers.put(XsdList.XS_TAG, new ConfigEntryData(XsdList::parse, elem -> new XsdListVisitor((XsdList) elem)));
        parseMappers.put(XsdRestriction.XS_TAG, new ConfigEntryData(XsdRestriction::parse, elem -> new XsdRestrictionsVisitor((XsdRestriction) elem)));
        parseMappers.put(XsdUnion.XS_TAG, new ConfigEntryData(XsdUnion::parse, elem -> new XsdUnionVisitor((XsdUnion) elem)));

        parseMappers.put(XsdAnnotation.XS_TAG, new ConfigEntryData(XsdAnnotation::parse, elem -> new XsdAnnotationVisitor((XsdAnnotation) elem)));
        parseMappers.put(XsdAppInfo.XS_TAG, new ConfigEntryData(XsdAppInfo::parse, null));
        parseMappers.put(XsdComplexContent.XS_TAG, new ConfigEntryData(XsdComplexContent::parse, elem -> new XsdComplexContentVisitor((XsdComplexContent) elem)));
        parseMappers.put(XsdDocumentation.XS_TAG, new ConfigEntryData(XsdDocumentation::parse, null));
        parseMappers.put(XsdExtension.XS_TAG, new ConfigEntryData(XsdExtension::parse, elem -> new XsdExtensionVisitor((XsdExtension) elem)));
        parseMappers.put(XsdSimpleContent.XS_TAG, new ConfigEntryData(XsdSimpleContent::parse, elem -> new XsdSimpleContentVisitor((XsdSimpleContent) elem)));

        parseMappers.put(XsdEnumeration.XS_TAG, new ConfigEntryData(XsdEnumeration::parse, elem -> new XsdAnnotatedElementsVisitor((XsdEnumeration) elem)));
        parseMappers.put(XsdFractionDigits.XS_TAG, new ConfigEntryData(XsdFractionDigits::parse, elem -> new XsdAnnotatedElementsVisitor((XsdFractionDigits) elem)));
        parseMappers.put(XsdLength.XS_TAG, new ConfigEntryData(XsdLength::parse, elem -> new XsdAnnotatedElementsVisitor((XsdLength) elem)));
        parseMappers.put(XsdMaxExclusive.XS_TAG, new ConfigEntryData(XsdMaxExclusive::parse, elem -> new XsdAnnotatedElementsVisitor((XsdMaxExclusive) elem)));
        parseMappers.put(XsdMaxInclusive.XS_TAG, new ConfigEntryData(XsdMaxInclusive::parse, elem -> new XsdAnnotatedElementsVisitor((XsdMaxInclusive) elem)));
        parseMappers.put(XsdMaxLength.XS_TAG, new ConfigEntryData(XsdMaxLength::parse, elem -> new XsdAnnotatedElementsVisitor((XsdMaxLength) elem)));
        parseMappers.put(XsdMinExclusive.XS_TAG, new ConfigEntryData(XsdMinExclusive::parse, elem -> new XsdAnnotatedElementsVisitor((XsdMinExclusive) elem)));
        parseMappers.put(XsdMinInclusive.XS_TAG, new ConfigEntryData(XsdMinInclusive::parse, elem -> new XsdAnnotatedElementsVisitor((XsdMinInclusive) elem)));
        parseMappers.put(XsdMinLength.XS_TAG, new ConfigEntryData(XsdMinLength::parse, elem -> new XsdAnnotatedElementsVisitor((XsdMinLength) elem)));
        parseMappers.put(XsdPattern.XS_TAG, new ConfigEntryData(XsdPattern::parse, elem -> new XsdAnnotatedElementsVisitor((XsdPattern) elem)));
        parseMappers.put(XsdTotalDigits.XS_TAG, new ConfigEntryData(XsdTotalDigits::parse, elem -> new XsdAnnotatedElementsVisitor((XsdTotalDigits) elem)));
        parseMappers.put(XsdWhiteSpace.XS_TAG, new ConfigEntryData(XsdWhiteSpace::parse, elem -> new XsdAnnotatedElementsVisitor((XsdWhiteSpace) elem)));


        //XSD
        parseMappers.put(XsdSchema.XSD_TAG, new ConfigEntryData(XsdSchema::parse, elem -> new XsdSchemaVisitor((XsdSchema) elem)));
        parseMappers.put(XsdAll.XSD_TAG, new ConfigEntryData(XsdAll::parse, elem -> new XsdAllVisitor((XsdAll) elem)));
        parseMappers.put(XsdAny.XSD_TAG, new ConfigEntryData(XsdAny::parse, elem -> new XsdAnyVisitor((XsdAny) elem)));
        parseMappers.put(XsdAttribute.XSD_TAG, new ConfigEntryData(XsdAttribute::parse, elem -> new XsdAttributeVisitor((XsdAttribute) elem)));
        parseMappers.put(XsdAttributeGroup.XSD_TAG, new ConfigEntryData(XsdAttributeGroup::parse, elem -> new XsdAttributeGroupVisitor((XsdAttributeGroup) elem)));
        parseMappers.put(XsdChoice.XSD_TAG, new ConfigEntryData(XsdChoice::parse, elem -> new XsdChoiceVisitor((XsdChoice) elem)));
        parseMappers.put(XsdComplexType.XSD_TAG, new ConfigEntryData(XsdComplexType::parse, elem -> new XsdComplexTypeVisitor((XsdComplexType) elem)));
        parseMappers.put(XsdElement.XSD_TAG, new ConfigEntryData(XsdElement::parse, elem -> new XsdElementVisitor((XsdElement) elem)));
        parseMappers.put(XsdGroup.XSD_TAG, new ConfigEntryData(XsdGroup::parse, elem -> new XsdGroupVisitor((XsdGroup) elem)));
        parseMappers.put(XsdInclude.XSD_TAG, new ConfigEntryData(XsdInclude::parse, elem -> new XsdAnnotatedElementsVisitor((XsdInclude) elem)));
        parseMappers.put(XsdImport.XSD_TAG, new ConfigEntryData(XsdImport::parse, elem -> new XsdAnnotatedElementsVisitor((XsdImport) elem)));
        parseMappers.put(XsdSequence.XSD_TAG, new ConfigEntryData(XsdSequence::parse, elem -> new XsdSequenceVisitor((XsdSequence) elem)));
        parseMappers.put(XsdSimpleType.XSD_TAG, new ConfigEntryData(XsdSimpleType::parse, elem -> new XsdSimpleTypeVisitor((XsdSimpleType) elem)));
        parseMappers.put(XsdList.XSD_TAG, new ConfigEntryData(XsdList::parse, elem -> new XsdListVisitor((XsdList) elem)));
        parseMappers.put(XsdRestriction.XSD_TAG, new ConfigEntryData(XsdRestriction::parse, elem -> new XsdRestrictionsVisitor((XsdRestriction) elem)));
        parseMappers.put(XsdUnion.XSD_TAG, new ConfigEntryData(XsdUnion::parse, elem -> new XsdUnionVisitor((XsdUnion) elem)));

        parseMappers.put(XsdAnnotation.XSD_TAG, new ConfigEntryData(XsdAnnotation::parse, elem -> new XsdAnnotationVisitor((XsdAnnotation) elem)));
        parseMappers.put(XsdAppInfo.XSD_TAG, new ConfigEntryData(XsdAppInfo::parse, null));
        parseMappers.put(XsdComplexContent.XSD_TAG, new ConfigEntryData(XsdComplexContent::parse, elem -> new XsdComplexContentVisitor((XsdComplexContent) elem)));
        parseMappers.put(XsdDocumentation.XSD_TAG, new ConfigEntryData(XsdDocumentation::parse, null));
        parseMappers.put(XsdExtension.XSD_TAG, new ConfigEntryData(XsdExtension::parse, elem -> new XsdExtensionVisitor((XsdExtension) elem)));
        parseMappers.put(XsdSimpleContent.XSD_TAG, new ConfigEntryData(XsdSimpleContent::parse, elem -> new XsdSimpleContentVisitor((XsdSimpleContent) elem)));

        parseMappers.put(XsdEnumeration.XSD_TAG, new ConfigEntryData(XsdEnumeration::parse, elem -> new XsdAnnotatedElementsVisitor((XsdEnumeration) elem)));
        parseMappers.put(XsdFractionDigits.XSD_TAG, new ConfigEntryData(XsdFractionDigits::parse, elem -> new XsdAnnotatedElementsVisitor((XsdFractionDigits) elem)));
        parseMappers.put(XsdLength.XSD_TAG, new ConfigEntryData(XsdLength::parse, elem -> new XsdAnnotatedElementsVisitor((XsdLength) elem)));
        parseMappers.put(XsdMaxExclusive.XSD_TAG, new ConfigEntryData(XsdMaxExclusive::parse, elem -> new XsdAnnotatedElementsVisitor((XsdMaxExclusive) elem)));
        parseMappers.put(XsdMaxInclusive.XSD_TAG, new ConfigEntryData(XsdMaxInclusive::parse, elem -> new XsdAnnotatedElementsVisitor((XsdMaxInclusive) elem)));
        parseMappers.put(XsdMaxLength.XSD_TAG, new ConfigEntryData(XsdMaxLength::parse, elem -> new XsdAnnotatedElementsVisitor((XsdMaxLength) elem)));
        parseMappers.put(XsdMinExclusive.XSD_TAG, new ConfigEntryData(XsdMinExclusive::parse, elem -> new XsdAnnotatedElementsVisitor((XsdMinExclusive) elem)));
        parseMappers.put(XsdMinInclusive.XSD_TAG, new ConfigEntryData(XsdMinInclusive::parse, elem -> new XsdAnnotatedElementsVisitor((XsdMinInclusive) elem)));
        parseMappers.put(XsdMinLength.XSD_TAG, new ConfigEntryData(XsdMinLength::parse, elem -> new XsdAnnotatedElementsVisitor((XsdMinLength) elem)));
        parseMappers.put(XsdPattern.XSD_TAG, new ConfigEntryData(XsdPattern::parse, elem -> new XsdAnnotatedElementsVisitor((XsdPattern) elem)));
        parseMappers.put(XsdTotalDigits.XSD_TAG, new ConfigEntryData(XsdTotalDigits::parse, elem -> new XsdAnnotatedElementsVisitor((XsdTotalDigits) elem)));
        parseMappers.put(XsdWhiteSpace.XSD_TAG, new ConfigEntryData(XsdWhiteSpace::parse, elem -> new XsdAnnotatedElementsVisitor((XsdWhiteSpace) elem)));

        return parseMappers;
    }
}
