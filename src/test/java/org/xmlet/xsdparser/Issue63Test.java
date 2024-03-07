package org.xmlet.xsdparser;

import org.junit.Test;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.XsdComplexType;
import org.xmlet.xsdparser.xsdelements.XsdElement;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/*
 * Sample code to test issue https://github.com/xmlet/XsdParser/issues/63
 */
public class Issue63Test {

    @Test
    public void testIssue63() {
        XsdParser xsdParser = new XsdParser( "./src/test/resources/issue_63/a.xsd" );
        List<XsdElement> elements = xsdParser.getResultXsdElements().collect(Collectors.toList());
        for (XsdElement currentElement : elements) {
            XsdComplexType complexType = currentElement.getXsdComplexType();
            if ( complexType != null ) {
                Logger.getAnonymousLogger().log(Level.INFO, "current type : "+complexType );
                // exception raised here
                complexType.getXsdAttributes().forEach( a -> a.getAllRestrictions() );
            }
        }
    }

}
