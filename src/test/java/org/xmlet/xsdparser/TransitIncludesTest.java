package org.xmlet.xsdparser;

import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.core.utils.UnsolvedReferenceItem;
import org.xmlet.xsdparser.xsdelements.XsdSchema;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;

public class TransitIncludesTest {

    private static final XsdParser parser;

    static {
        parser = new XsdParser(getFilePath());
    }

    @Test
    public void transitive_includes() {
        List<UnsolvedReferenceItem> unsolvedReferenceItemList = parser.getUnsolvedReferences();
        List<XsdSchema> schemas = parser.getResultXsdSchemas().collect(Collectors.toList());
        XsdSchema schema = schemas.stream().filter(sch -> sch.getFilePath().endsWith("BBK_RIAD_V2.4-SDMX.xsd")).findFirst().orElse(null);
        schema.getXsdElements().forEach(xsdAbstractElement -> xsdAbstractElement.getElements());
        XsdSchema structureCategory = schemas.stream().filter(sch -> sch.getFilePath().endsWith("SDMXStructureCategory.xsd")).findFirst().orElse(null);




        List<UnsolvedReferenceItem> unsolved = parser.getUnsolvedReferences();
        assertTrue(unsolvedReferenceItemList.isEmpty());
    }

    private List<UnsolvedReference> getUnsolvedReference(XsdSchema xsdSchema) {
        List<UnsolvedReference> unsolvedReferences = new ArrayList<>();
        if(xsdSchema != null) {

        }
        return unsolvedReferences;
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
