package org.xmlet.xsdparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.core.utils.UnsolvedReferenceItem;
import org.xmlet.xsdparser.xsdelements.XsdAttribute;
import org.xmlet.xsdparser.xsdelements.XsdChoice;
import org.xmlet.xsdparser.xsdelements.XsdComplexType;
import org.xmlet.xsdparser.xsdelements.XsdElement;
import org.xmlet.xsdparser.xsdelements.XsdNamedElements;
import org.xmlet.xsdparser.xsdelements.XsdSchema;
import org.xmlet.xsdparser.xsdelements.xsdrestrictions.XsdPattern;

public class TransitIncludesTest {

    private static final XsdParser parser;

    static {
        parser = new XsdParser(getFilePath());
    }

    @Test
    public void transitive_includes() {
        List<UnsolvedReferenceItem> unsolvedReferenceItemList = parser.getUnsolvedReferences();
        assertTrue(unsolvedReferenceItemList.isEmpty());
        List<XsdSchema> schemas = parser.getResultXsdSchemas().collect(Collectors.toList());
        XsdSchema schema = schemas.stream().filter(sch -> sch.getFilePath().endsWith("BBK_RIAD_V2.4-SDMX.xsd")).findFirst().orElse(null);
        assertNotNull(schema);
        XsdComplexType xsdComplexType = schema.getXsdElements().filter(XsdNamedElements.class::isInstance).filter(xsdAbstractElement -> ((XsdNamedElements)xsdAbstractElement).getName().equals("BBK_RIAD_HDR_C")).map(xsdAbstractElement -> (XsdComplexType) xsdAbstractElement).findFirst().orElse(null);
        assertNotNull(xsdComplexType);
        XsdChoice choice = xsdComplexType.getComplexContent().getXsdRestriction().getSequence().getChildrenChoices().findFirst().orElse(null);
        assertNotNull(choice);
        XsdElement element = choice.getChildrenElements().findFirst().orElse(null);
        assertNotNull(element);
        XsdComplexType elementAsType = element.getTypeAsComplexType();
        assertNotNull(elementAsType);
        List<XsdAttribute> attributes = elementAsType.getComplexContent().getXsdRestriction().getXsdAttributes().collect(Collectors.toList());
        assertFalse(attributes.isEmpty());
        XsdAttribute attribute = attributes.get(0);
        assertEquals("RPRTNG_AGNT_CD", attribute.getName());
        List<XsdPattern> patterns = attribute.getXsdSimpleType().getRestriction().getPattern();
        assertFalse(patterns.isEmpty());
        assertEquals("\\d{8}", patterns.get(0).getValue());
    }
    /**
     * @return Obtains the filePath of the file associated with this test class.
     */
    private static String getFilePath(){
        URL resource = HtmlParseTest.class.getClassLoader().getResource("sdmx_schema/BBK_RIAD_V2.4-SDMX.xsd");

        if (resource != null){
            return resource.getPath();
        } else {
            throw new RuntimeException("The test.xsd file is missing from the XsdParser resource folder.");
        }
    }
}
