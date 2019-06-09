package org.xmlet.xsdparser.core.utils;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParserCore;
import org.xmlet.xsdparser.xsdelements.*;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.xsdrestrictions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

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

        xsdTypesToJava.put("xsd:anyURI", string);
        xsdTypesToJava.put("xs:anyURI", string);
        xsdTypesToJava.put("xsd:boolean", "Boolean");
        xsdTypesToJava.put("xs:boolean", "Boolean");
        xsdTypesToJava.put("xsd:date", xmlGregorianCalendar);
        xsdTypesToJava.put("xs:date", xmlGregorianCalendar);
        xsdTypesToJava.put("xsd:dateTime", xmlGregorianCalendar);
        xsdTypesToJava.put("xs:dateTime", xmlGregorianCalendar);
        xsdTypesToJava.put("xsd:time", xmlGregorianCalendar);
        xsdTypesToJava.put("xs:time", xmlGregorianCalendar);
        xsdTypesToJava.put("xsd:duration", duration);
        xsdTypesToJava.put("xs:duration", duration);
        xsdTypesToJava.put("xsd:dayTimeDuration", duration);
        xsdTypesToJava.put("xs:dayTimeDuration", duration);
        xsdTypesToJava.put("xsd:yearMonthDuration", duration);
        xsdTypesToJava.put("xs:yearMonthDuration", duration);
        xsdTypesToJava.put("xsd:gDay", xmlGregorianCalendar);
        xsdTypesToJava.put("xs:gDay", xmlGregorianCalendar);
        xsdTypesToJava.put("xsd:gMonth", xmlGregorianCalendar);
        xsdTypesToJava.put("xs:gMonth", xmlGregorianCalendar);
        xsdTypesToJava.put("xsd:gMonthDay", xmlGregorianCalendar);
        xsdTypesToJava.put("xs:gMonthDay", xmlGregorianCalendar);
        xsdTypesToJava.put("xsd:gYear", xmlGregorianCalendar);
        xsdTypesToJava.put("xs:gYear", xmlGregorianCalendar);
        xsdTypesToJava.put("xsd:gYearMonth", xmlGregorianCalendar);
        xsdTypesToJava.put("xs:gYearMonth", xmlGregorianCalendar);
        xsdTypesToJava.put("xsd:decimal", "BigDecimal");
        xsdTypesToJava.put("xs:decimal", "BigDecimal");
        xsdTypesToJava.put("xsd:integer", bigInteger);
        xsdTypesToJava.put("xs:integer", bigInteger);
        xsdTypesToJava.put("xsd:nonPositiveInteger", bigInteger);
        xsdTypesToJava.put("xs:nonPositiveInteger", bigInteger);
        xsdTypesToJava.put("xsd:negativeInteger", bigInteger);
        xsdTypesToJava.put("xs:negativeInteger", bigInteger);
        xsdTypesToJava.put("xsd:long", longString);
        xsdTypesToJava.put("xs:long", longString);
        xsdTypesToJava.put("xsd:int", integer);
        xsdTypesToJava.put("xs:int", integer);
        xsdTypesToJava.put("xsd:short", shortString);
        xsdTypesToJava.put("xs:short", shortString);
        xsdTypesToJava.put("xsd:byte", byteString);
        xsdTypesToJava.put("xs:byte", byteString);
        xsdTypesToJava.put("xsd:nonNegativeInteger", bigInteger);
        xsdTypesToJava.put("xs:nonNegativeInteger", bigInteger);
        xsdTypesToJava.put("xsd:unsignedLong", bigInteger);
        xsdTypesToJava.put("xs:unsignedLong", bigInteger);
        xsdTypesToJava.put("xsd:unsignedInt", longString);
        xsdTypesToJava.put("xs:unsignedInt", longString);
        xsdTypesToJava.put("xsd:unsignedShort", integer);
        xsdTypesToJava.put("xs:unsignedShort", integer);
        xsdTypesToJava.put("xsd:unsignedByte", shortString);
        xsdTypesToJava.put("xs:unsignedByte", shortString);
        xsdTypesToJava.put("xsd:positiveInteger", bigInteger);
        xsdTypesToJava.put("xs:positiveInteger", bigInteger);
        xsdTypesToJava.put("xsd:double", "Double");
        xsdTypesToJava.put("xs:double", "Double");
        xsdTypesToJava.put("xsd:float", "Float");
        xsdTypesToJava.put("xs:float", "Float");
        xsdTypesToJava.put("xsd:QName", qName);
        xsdTypesToJava.put("xs:QName", qName);
        xsdTypesToJava.put("xsd:NOTATION", qName);
        xsdTypesToJava.put("xs:NOTATION", qName);
        xsdTypesToJava.put("xsd:string", string);
        xsdTypesToJava.put("xs:string", string);
        xsdTypesToJava.put("xsd:normalizedString", string);
        xsdTypesToJava.put("xs:normalizedString", string);
        xsdTypesToJava.put("xsd:token", string);
        xsdTypesToJava.put("xs:token", string);
        xsdTypesToJava.put("xsd:language", string);
        xsdTypesToJava.put("xs:language", string);
        xsdTypesToJava.put("xsd:NMTOKEN", string);
        xsdTypesToJava.put("xs:NMTOKEN", string);
        xsdTypesToJava.put("xsd:Name", string);
        xsdTypesToJava.put("xs:Name", string);
        xsdTypesToJava.put("xsd:NCName", string);
        xsdTypesToJava.put("xs:NCName", string);
        xsdTypesToJava.put("xsd:ID", string);
        xsdTypesToJava.put("xs:ID", string);
        xsdTypesToJava.put("xsd:IDREF", string);
        xsdTypesToJava.put("xs:IDREF", string);
        xsdTypesToJava.put("xsd:ENTITY", string);
        xsdTypesToJava.put("xs:ENTITY", string);
        xsdTypesToJava.put("xsd:untypedAtomic", string);
        xsdTypesToJava.put("xs:untypedAtomic", string);

        return xsdTypesToJava;
    }

    @Override
    public Map<String, BiFunction<XsdParserCore, Node, ReferenceBase>> getParseMappers() {
        Map<String, BiFunction<XsdParserCore, Node, ReferenceBase>> parseMappers = new HashMap<>();

        parseMappers.put(XsdAll.XSD_TAG, XsdAll::parse);
        parseMappers.put(XsdAll.XS_TAG, XsdAll::parse);
        parseMappers.put(XsdAttribute.XSD_TAG, XsdAttribute::parse);
        parseMappers.put(XsdAttribute.XS_TAG, XsdAttribute::parse);
        parseMappers.put(XsdAttributeGroup.XSD_TAG, XsdAttributeGroup::parse);
        parseMappers.put(XsdAttributeGroup.XS_TAG, XsdAttributeGroup::parse);
        parseMappers.put(XsdChoice.XSD_TAG, XsdChoice::parse);
        parseMappers.put(XsdChoice.XS_TAG, XsdChoice::parse);
        parseMappers.put(XsdComplexType.XSD_TAG, XsdComplexType::parse);
        parseMappers.put(XsdComplexType.XS_TAG, XsdComplexType::parse);
        parseMappers.put(XsdElement.XSD_TAG, XsdElement::parse);
        parseMappers.put(XsdElement.XS_TAG, XsdElement::parse);
        parseMappers.put(XsdGroup.XSD_TAG, XsdGroup::parse);
        parseMappers.put(XsdGroup.XS_TAG, XsdGroup::parse);
        parseMappers.put(XsdInclude.XSD_TAG, XsdInclude::parse);
        parseMappers.put(XsdInclude.XS_TAG, XsdInclude::parse);
        parseMappers.put(XsdImport.XSD_TAG, XsdImport::parse);
        parseMappers.put(XsdImport.XS_TAG, XsdImport::parse);
        parseMappers.put(XsdSequence.XSD_TAG, XsdSequence::parse);
        parseMappers.put(XsdSequence.XS_TAG, XsdSequence::parse);
        parseMappers.put(XsdSimpleType.XSD_TAG, XsdSimpleType::parse);
        parseMappers.put(XsdSimpleType.XS_TAG, XsdSimpleType::parse);
        parseMappers.put(XsdList.XSD_TAG, XsdList::parse);
        parseMappers.put(XsdList.XS_TAG, XsdList::parse);
        parseMappers.put(XsdRestriction.XSD_TAG, XsdRestriction::parse);
        parseMappers.put(XsdRestriction.XS_TAG, XsdRestriction::parse);
        parseMappers.put(XsdUnion.XSD_TAG, XsdUnion::parse);
        parseMappers.put(XsdUnion.XS_TAG, XsdUnion::parse);

        parseMappers.put(XsdAnnotation.XSD_TAG, XsdAnnotation::parse);
        parseMappers.put(XsdAnnotation.XS_TAG, XsdAnnotation::parse);
        parseMappers.put(XsdAppInfo.XSD_TAG, XsdAppInfo::parse);
        parseMappers.put(XsdAppInfo.XS_TAG, XsdAppInfo::parse);
        parseMappers.put(XsdComplexContent.XSD_TAG, XsdComplexContent::parse);
        parseMappers.put(XsdComplexContent.XS_TAG, XsdComplexContent::parse);
        parseMappers.put(XsdDocumentation.XSD_TAG, XsdDocumentation::parse);
        parseMappers.put(XsdDocumentation.XS_TAG, XsdDocumentation::parse);
        parseMappers.put(XsdExtension.XSD_TAG, XsdExtension::parse);
        parseMappers.put(XsdExtension.XS_TAG, XsdExtension::parse);
        parseMappers.put(XsdSimpleContent.XSD_TAG, XsdSimpleContent::parse);
        parseMappers.put(XsdSimpleContent.XS_TAG, XsdSimpleContent::parse);

        parseMappers.put(XsdEnumeration.XSD_TAG, XsdEnumeration::parse);
        parseMappers.put(XsdEnumeration.XS_TAG, XsdEnumeration::parse);
        parseMappers.put(XsdFractionDigits.XSD_TAG, XsdFractionDigits::parse);
        parseMappers.put(XsdFractionDigits.XS_TAG, XsdFractionDigits::parse);
        parseMappers.put(XsdLength.XSD_TAG, XsdLength::parse);
        parseMappers.put(XsdLength.XS_TAG, XsdLength::parse);
        parseMappers.put(XsdMaxExclusive.XSD_TAG, XsdMaxExclusive::parse);
        parseMappers.put(XsdMaxExclusive.XS_TAG, XsdMaxExclusive::parse);
        parseMappers.put(XsdMaxInclusive.XSD_TAG, XsdMaxInclusive::parse);
        parseMappers.put(XsdMaxInclusive.XS_TAG, XsdMaxInclusive::parse);
        parseMappers.put(XsdMaxLength.XSD_TAG, XsdMaxLength::parse);
        parseMappers.put(XsdMaxLength.XS_TAG, XsdMaxLength::parse);
        parseMappers.put(XsdMinExclusive.XSD_TAG, XsdMinExclusive::parse);
        parseMappers.put(XsdMinExclusive.XS_TAG, XsdMinExclusive::parse);
        parseMappers.put(XsdMinInclusive.XSD_TAG, XsdMinInclusive::parse);
        parseMappers.put(XsdMinInclusive.XS_TAG, XsdMinInclusive::parse);
        parseMappers.put(XsdMinLength.XSD_TAG, XsdMinLength::parse);
        parseMappers.put(XsdMinLength.XS_TAG, XsdMinLength::parse);
        parseMappers.put(XsdPattern.XSD_TAG, XsdPattern::parse);
        parseMappers.put(XsdPattern.XS_TAG, XsdPattern::parse);
        parseMappers.put(XsdTotalDigits.XSD_TAG, XsdTotalDigits::parse);
        parseMappers.put(XsdTotalDigits.XS_TAG, XsdTotalDigits::parse);
        parseMappers.put(XsdWhiteSpace.XSD_TAG, XsdWhiteSpace::parse);
        parseMappers.put(XsdWhiteSpace.XS_TAG, XsdWhiteSpace::parse);

        return parseMappers;
    }
}
