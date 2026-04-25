package org.xmlet.xsdparser;

import org.junit.Assert;
import org.junit.Test;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.core.XsdParserCore;
import org.xmlet.xsdparser.xsdelements.XsdAnyAttribute;
import org.xmlet.xsdparser.xsdelements.XsdAttributeGroup;
import org.xmlet.xsdparser.xsdelements.XsdComplexType;
import org.xmlet.xsdparser.xsdelements.XsdElement;
import org.xmlet.xsdparser.xsdelements.XsdField;
import org.xmlet.xsdparser.xsdelements.XsdIdentityConstraint;
import org.xmlet.xsdparser.xsdelements.XsdKey;
import org.xmlet.xsdparser.xsdelements.XsdKeyref;
import org.xmlet.xsdparser.xsdelements.XsdNotation;
import org.xmlet.xsdparser.xsdelements.XsdSchema;
import org.xmlet.xsdparser.xsdelements.XsdSelector;
import org.xmlet.xsdparser.xsdelements.XsdUnique;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * End-to-end smoke test that exercises the seven XSD 1.0 elements added in this change set:
 * {@code xs:anyAttribute}, {@code xs:notation}, {@code xs:unique}, {@code xs:key},
 * {@code xs:keyref}, {@code xs:selector}, {@code xs:field}. Asserts that each parses,
 * surfaces through its parent's accessor, and that {@code keyref}'s {@code refer} attribute
 * resolves to the matching {@code key}.
 */
public class NewElementsTest {

    private static final XsdParserCore parser;
    private static final XsdSchema schema;

    static {
        URL url = NewElementsTest.class.getResource("/new_elements_smoke.xsd");
        parser = XsdParser.fromURL(url, null);
        schema = parser.getResultXsdSchemas().findFirst().orElseThrow(() -> new AssertionError("schema missing"));
    }

    @Test
    public void notationParsedAndAccessibleFromSchema() {
        List<XsdNotation> notations = schema.getChildrenNotations().collect(Collectors.toList());
        Assert.assertEquals(1, notations.size());
        XsdNotation gif = notations.get(0);
        Assert.assertEquals("gif", gif.getName());
        Assert.assertEquals("-//IETF//NONSGML Media Type image/gif//EN", gif.getPublicId());
        Assert.assertEquals("http://example.com/gif", gif.getSystem());
    }

    @Test
    public void anyAttributeOnComplexType() {
        XsdComplexType wild = schema.getChildrenComplexTypes()
                .filter(t -> "WildAttrType".equals(t.getName()))
                .findFirst().orElseThrow(() -> new AssertionError("WildAttrType missing"));
        XsdAnyAttribute any = wild.getAnyAttribute();
        Assert.assertNotNull(any);
        Assert.assertEquals("##other", any.getNamespace());
        Assert.assertEquals("lax", any.getProcessContents());
    }

    @Test
    public void anyAttributeOnAttributeGroup() {
        XsdAttributeGroup extras = schema.getChildrenAttributeGroups()
                .filter(ag -> "extras".equals(ag.getName()))
                .findFirst().orElseThrow(() -> new AssertionError("extras attributeGroup missing"));
        XsdAnyAttribute any = extras.getAnyAttribute();
        Assert.assertNotNull(any);
        Assert.assertEquals("##any", any.getNamespace());
        Assert.assertEquals("skip", any.getProcessContents());
    }

    @Test
    public void identityConstraintsOnElement() {
        XsdElement catalog = parser.getResultXsdElements()
                .filter(e -> "catalog".equals(e.getName()))
                .findFirst().orElseThrow(() -> new AssertionError("catalog element missing"));

        List<XsdIdentityConstraint> constraints = catalog.getIdentityConstraints();
        Assert.assertEquals(3, constraints.size());

        XsdUnique unique = catalog.getUniques().findFirst().orElseThrow(() -> new AssertionError("unique missing"));
        Assert.assertEquals("uniqueItemName", unique.getName());

        XsdKey key = catalog.getKeys().findFirst().orElseThrow(() -> new AssertionError("key missing"));
        Assert.assertEquals("itemKey", key.getName());

        XsdKeyref keyref = catalog.getKeyrefs().findFirst().orElseThrow(() -> new AssertionError("keyref missing"));
        Assert.assertEquals("itemKeyRef", keyref.getName());
        Assert.assertEquals("itemKey", keyref.getRefer());
    }

    @Test
    public void selectorAndFieldXpathParsed() {
        XsdElement catalog = parser.getResultXsdElements()
                .filter(e -> "catalog".equals(e.getName()))
                .findFirst().orElseThrow(() -> new AssertionError("catalog element missing"));
        XsdKey key = catalog.getKeys().findFirst().orElseThrow(() -> new AssertionError("key missing"));

        XsdSelector selector = key.getSelector();
        Assert.assertNotNull(selector);
        Assert.assertEquals("item", selector.getXpath());

        List<XsdField> fields = key.getFields();
        Assert.assertEquals(1, fields.size());
        Assert.assertEquals("@id", fields.get(0).getXpath());
    }

    @Test
    public void keyrefResolvesToReferencedKey() {
        XsdElement catalog = parser.getResultXsdElements()
                .filter(e -> "catalog".equals(e.getName()))
                .findFirst().orElseThrow(() -> new AssertionError("catalog element missing"));
        XsdKeyref keyref = catalog.getKeyrefs().findFirst().orElseThrow(() -> new AssertionError("keyref missing"));

        XsdIdentityConstraint resolved = keyref.getReferElement();
        Assert.assertNotNull("keyref refer should resolve to the named key", resolved);
        Assert.assertTrue("expected XsdKey, got " + resolved.getClass().getSimpleName(), resolved instanceof XsdKey);
        Assert.assertEquals("itemKey", resolved.getName());
    }

    @Test
    public void noUnsolvedReferences() {
        Assert.assertEquals(
                "All references in the smoke fixture should resolve",
                0,
                parser.getUnsolvedReferences().size());
    }

    // ----- Spec-compliance edge cases -----

    private static void parseSnippet(String body) {
        // Wrap a schema body in a schema document and parse it via a temp file.
        String xml = "<?xml version=\"1.0\"?>\n"
                + "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n"
                + body + "\n</xs:schema>\n";
        try {
            java.io.File tmp = java.io.File.createTempFile("xsdparser-test-", ".xsd");
            tmp.deleteOnExit();
            java.nio.file.Files.write(tmp.toPath(), xml.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            XsdParser.fromURL(tmp.toURI().toURL(), null);
        } catch (java.io.IOException e) {
            throw new AssertionError(e);
        }
    }

    @Test
    public void notationWithoutPublicOrSystemIsAccepted() {
        // Per spec, both public and system are optional on xs:notation.
        parseSnippet("<xs:notation name=\"x\"/>");
    }

    @Test(expected = org.xmlet.xsdparser.xsdelements.exceptions.ParsingException.class)
    public void selectorWithFullXPathIsRejected() {
        // text() is not in the restricted XPath subset for selectors.
        parseSnippet(
                "<xs:element name=\"r\" type=\"xs:string\">"
              + "  <xs:unique name=\"u\">"
              + "    <xs:selector xpath=\"item/text()\"/>"
              + "    <xs:field xpath=\"@id\"/>"
              + "  </xs:unique>"
              + "</xs:element>");
    }

    @Test(expected = org.xmlet.xsdparser.xsdelements.exceptions.ParsingException.class)
    public void selectorWithAttributeStepIsRejected() {
        // @attr is allowed in xs:field but NOT in xs:selector per the spec subset.
        parseSnippet(
                "<xs:element name=\"r\" type=\"xs:string\">"
              + "  <xs:unique name=\"u\">"
              + "    <xs:selector xpath=\"@id\"/>"
              + "    <xs:field xpath=\"@id\"/>"
              + "  </xs:unique>"
              + "</xs:element>");
    }

    @Test
    public void fieldWithAttributeStepIsAccepted() {
        parseSnippet(
                "<xs:element name=\"r\" type=\"xs:string\">"
              + "  <xs:unique name=\"u\">"
              + "    <xs:selector xpath=\"item\"/>"
              + "    <xs:field xpath=\"@id\"/>"
              + "  </xs:unique>"
              + "</xs:element>");
    }

    @Test(expected = org.xmlet.xsdparser.xsdelements.exceptions.ParsingException.class)
    public void multipleSelectorsAreRejected() {
        parseSnippet(
                "<xs:element name=\"r\" type=\"xs:string\">"
              + "  <xs:unique name=\"u\">"
              + "    <xs:selector xpath=\"a\"/>"
              + "    <xs:selector xpath=\"b\"/>"
              + "    <xs:field xpath=\"@id\"/>"
              + "  </xs:unique>"
              + "</xs:element>");
    }

    @Test(expected = org.xmlet.xsdparser.xsdelements.exceptions.ParsingException.class)
    public void fieldBeforeSelectorIsRejected() {
        parseSnippet(
                "<xs:element name=\"r\" type=\"xs:string\">"
              + "  <xs:unique name=\"u\">"
              + "    <xs:field xpath=\"@id\"/>"
              + "    <xs:selector xpath=\"a\"/>"
              + "  </xs:unique>"
              + "</xs:element>");
    }

    @Test(expected = org.xmlet.xsdparser.xsdelements.exceptions.ParsingException.class)
    public void keyrefWithMismatchedFieldCountIsRejected() {
        parseSnippet(
                "<xs:element name=\"r\" type=\"xs:string\">"
              + "  <xs:key name=\"k\">"
              + "    <xs:selector xpath=\"a\"/>"
              + "    <xs:field xpath=\"@x\"/>"
              + "  </xs:key>"
              + "  <xs:keyref name=\"kr\" refer=\"k\">"
              + "    <xs:selector xpath=\"a\"/>"
              + "    <xs:field xpath=\"@x\"/>"
              + "    <xs:field xpath=\"@y\"/>"
              + "  </xs:keyref>"
              + "</xs:element>");
    }

    @Test(expected = org.xmlet.xsdparser.xsdelements.exceptions.ParsingException.class)
    public void anyAttributeUnderSimpleTypeRestrictionIsRejected() {
        parseSnippet(
                "<xs:simpleType name=\"bad\">"
              + "  <xs:restriction base=\"xs:string\">"
              + "    <xs:anyAttribute/>"
              + "  </xs:restriction>"
              + "</xs:simpleType>");
    }

    // ----- Lexical attribute validations -----

    @Test(expected = org.xmlet.xsdparser.xsdelements.exceptions.ParsingException.class)
    public void notationNameMustBeNCName() {
        parseSnippet("<xs:notation name=\"bad:name\"/>");
    }

    @Test(expected = org.xmlet.xsdparser.xsdelements.exceptions.ParsingException.class)
    public void keyrefReferMustBeQName() {
        parseSnippet(
                "<xs:element name=\"r\" type=\"xs:string\">"
              + "  <xs:key name=\"k\">"
              + "    <xs:selector xpath=\"a\"/>"
              + "    <xs:field xpath=\"@x\"/>"
              + "  </xs:key>"
              + "  <xs:keyref name=\"kr\" refer=\"a:b:c\">"
              + "    <xs:selector xpath=\"a\"/>"
              + "    <xs:field xpath=\"@x\"/>"
              + "  </xs:keyref>"
              + "</xs:element>");
    }

    @Test(expected = org.xmlet.xsdparser.xsdelements.exceptions.ParsingException.class)
    public void notationPublicMustBeToken() {
        // public with consecutive spaces violates xs:token. DOM parsing for CDATA-typed
        // attributes preserves them — the schema-for-schemas declares public as xs:public,
        // not as a tokenized attribute, so the DOM doesn't pre-collapse for us.
        parseSnippet("<xs:notation name=\"x\" public=\"foo  bar\" system=\"http://example.com\"/>");
    }

    @Test(expected = org.xmlet.xsdparser.xsdelements.exceptions.ParsingException.class)
    public void unknownAttributeIsRejected() {
        parseSnippet("<xs:notation name=\"x\" public=\"p\" wat=\"y\"/>");
    }

    @Test
    public void prefixedForeignAttributeIsAllowed() {
        // Prefixed attributes from other namespaces are admitted by xs:openAttrs' wildcard.
        parseSnippet("<xs:notation xmlns:foo=\"http://example.com/foo\" foo:tag=\"v\" name=\"x\" public=\"p\"/>");
    }

    // ----- Annotation cardinality -----

    @Test(expected = org.xmlet.xsdparser.xsdelements.exceptions.ParsingException.class)
    public void multipleAnnotationsOnNotationRejected() {
        parseSnippet(
                "<xs:notation name=\"x\" public=\"p\">"
              + "  <xs:annotation><xs:documentation>one</xs:documentation></xs:annotation>"
              + "  <xs:annotation><xs:documentation>two</xs:documentation></xs:annotation>"
              + "</xs:notation>");
    }

    @Test(expected = org.xmlet.xsdparser.xsdelements.exceptions.ParsingException.class)
    public void multipleAnnotationsOnKeyrefRejected() {
        parseSnippet(
                "<xs:element name=\"r\" type=\"xs:string\">"
              + "  <xs:key name=\"k\">"
              + "    <xs:annotation><xs:documentation>one</xs:documentation></xs:annotation>"
              + "    <xs:annotation><xs:documentation>two</xs:documentation></xs:annotation>"
              + "    <xs:selector xpath=\"a\"/>"
              + "    <xs:field xpath=\"@x\"/>"
              + "  </xs:key>"
              + "</xs:element>");
    }

    // ----- Multiple anyAttribute -----

    @Test(expected = org.xmlet.xsdparser.xsdelements.exceptions.ParsingException.class)
    public void multipleAnyAttributeOnComplexTypeRejected() {
        parseSnippet(
                "<xs:complexType name=\"t\">"
              + "  <xs:anyAttribute namespace=\"##any\"/>"
              + "  <xs:anyAttribute namespace=\"##other\"/>"
              + "</xs:complexType>");
    }

    // ----- Schema-level uniqueness -----

    @Test(expected = org.xmlet.xsdparser.xsdelements.exceptions.ParsingException.class)
    public void duplicateNotationNamesRejected() {
        parseSnippet(
                "<xs:notation name=\"dup\" public=\"a\"/>"
              + "<xs:notation name=\"dup\" public=\"b\"/>");
    }

    @Test(expected = org.xmlet.xsdparser.xsdelements.exceptions.ParsingException.class)
    public void duplicateIdentityConstraintNamesRejected() {
        parseSnippet(
                "<xs:element name=\"a\" type=\"xs:string\">"
              + "  <xs:key name=\"dup\"><xs:selector xpath=\"x\"/><xs:field xpath=\"@y\"/></xs:key>"
              + "</xs:element>"
              + "<xs:element name=\"b\" type=\"xs:string\">"
              + "  <xs:key name=\"dup\"><xs:selector xpath=\"x\"/><xs:field xpath=\"@y\"/></xs:key>"
              + "</xs:element>");
    }

    // ----- Wildcard subset on restriction -----

    @Test(expected = org.xmlet.xsdparser.xsdelements.exceptions.ParsingException.class)
    public void restrictionWideningWildcardRejected() {
        parseSnippet(
                "<xs:complexType name=\"Base\">"
              + "  <xs:sequence/>"
              + "  <xs:anyAttribute namespace=\"http://example.com/ns1\"/>"
              + "</xs:complexType>"
              + "<xs:complexType name=\"Derived\">"
              + "  <xs:complexContent>"
              + "    <xs:restriction base=\"Base\">"
              + "      <xs:sequence/>"
              + "      <xs:anyAttribute namespace=\"##any\"/>"
              + "    </xs:restriction>"
              + "  </xs:complexContent>"
              + "</xs:complexType>");
    }

    @Test
    public void restrictionNarrowingWildcardAccepted() {
        parseSnippet(
                "<xs:complexType name=\"Base\">"
              + "  <xs:sequence/>"
              + "  <xs:anyAttribute namespace=\"##any\"/>"
              + "</xs:complexType>"
              + "<xs:complexType name=\"Derived\">"
              + "  <xs:complexContent>"
              + "    <xs:restriction base=\"Base\">"
              + "      <xs:sequence/>"
              + "      <xs:anyAttribute namespace=\"http://example.com/ns1\"/>"
              + "    </xs:restriction>"
              + "  </xs:complexContent>"
              + "</xs:complexType>");
    }

    // ----- Round-2 spec fixes: child::/attribute:: axes, ##other targetNs, anyURI permissiveness -----

    @Test
    public void selectorWithChildAxisAccepted() {
        // Per the schema-for-schemas pattern, 'child::' is an allowed prefix on each step.
        parseSnippet(
                "<xs:element name=\"r\" type=\"xs:string\">"
              + "  <xs:unique name=\"u\">"
              + "    <xs:selector xpath=\"child::item/child::sub\"/>"
              + "    <xs:field xpath=\"@id\"/>"
              + "  </xs:unique>"
              + "</xs:element>");
    }

    @Test
    public void fieldWithAttributeAxisAccepted() {
        // 'attribute::' is an allowed synonym for '@' in field paths.
        parseSnippet(
                "<xs:element name=\"r\" type=\"xs:string\">"
              + "  <xs:unique name=\"u\">"
              + "    <xs:selector xpath=\"item\"/>"
              + "    <xs:field xpath=\"attribute::id\"/>"
              + "  </xs:unique>"
              + "</xs:element>");
    }

    @Test
    public void notationSystemAcceptsLooseUriLikeStrings() {
        // anyURI is intentionally permissive per Part 2 §3.2.17. Strings that Java's URI
        // class would reject must still be accepted.
        parseSnippet("<xs:notation name=\"x\" public=\"p\" system=\"not a strict URI but ok per anyURI\"/>");
    }

    @Test(expected = org.xmlet.xsdparser.xsdelements.exceptions.ParsingException.class)
    public void otherWildcardForbidsTargetNamespaceMember() {
        // Inside a schema with targetNamespace="http://example.com/ns", a derived restriction
        // declaring an enumerated wildcard {http://example.com/ns} is NOT a subset of ##other,
        // because ##other excludes the targetNamespace.
        try {
            java.io.File tmp = java.io.File.createTempFile("xsdparser-test-", ".xsd");
            tmp.deleteOnExit();
            String xml = "<?xml version=\"1.0\"?>\n"
                    + "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\""
                    + " targetNamespace=\"http://example.com/ns\""
                    + " xmlns:t=\"http://example.com/ns\">"
                    + "<xs:complexType name=\"Base\">"
                    + "  <xs:sequence/>"
                    + "  <xs:anyAttribute namespace=\"##other\"/>"
                    + "</xs:complexType>"
                    + "<xs:complexType name=\"Derived\">"
                    + "  <xs:complexContent>"
                    + "    <xs:restriction base=\"t:Base\">"
                    + "      <xs:sequence/>"
                    + "      <xs:anyAttribute namespace=\"http://example.com/ns\"/>"
                    + "    </xs:restriction>"
                    + "  </xs:complexContent>"
                    + "</xs:complexType>"
                    + "</xs:schema>";
            java.nio.file.Files.write(tmp.toPath(), xml.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            XsdParser.fromURL(tmp.toURI().toURL(), null);
        } catch (java.io.IOException e) {
            throw new AssertionError(e);
        }
    }

    @Test
    public void targetNamespaceSentinelExpandsInWildcard() {
        // ##targetNamespace in an enumerated wildcard resolves to the schema's targetNamespace.
        // The same enumerated form on a derived restriction should be accepted as a subset.
        try {
            java.io.File tmp = java.io.File.createTempFile("xsdparser-test-", ".xsd");
            tmp.deleteOnExit();
            String xml = "<?xml version=\"1.0\"?>\n"
                    + "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\""
                    + " targetNamespace=\"http://example.com/ns\""
                    + " xmlns:t=\"http://example.com/ns\">"
                    + "<xs:complexType name=\"Base\">"
                    + "  <xs:sequence/>"
                    + "  <xs:anyAttribute namespace=\"##targetNamespace ##local\"/>"
                    + "</xs:complexType>"
                    + "<xs:complexType name=\"Derived\">"
                    + "  <xs:complexContent>"
                    + "    <xs:restriction base=\"t:Base\">"
                    + "      <xs:sequence/>"
                    + "      <xs:anyAttribute namespace=\"http://example.com/ns\"/>"
                    + "    </xs:restriction>"
                    + "  </xs:complexContent>"
                    + "</xs:complexType>"
                    + "</xs:schema>";
            java.nio.file.Files.write(tmp.toPath(), xml.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            XsdParser.fromURL(tmp.toURI().toURL(), null);
        } catch (java.io.IOException e) {
            throw new AssertionError(e);
        }
    }

    @Test(expected = org.xmlet.xsdparser.xsdelements.exceptions.ParsingException.class)
    public void restrictionWeakerProcessContentsRejected() {
        // §3.10.6.1: derived processContents must be ≥ base's (skip<lax<strict). skip < strict.
        parseSnippet(
                "<xs:complexType name=\"Base\">"
              + "  <xs:sequence/>"
              + "  <xs:anyAttribute namespace=\"##any\" processContents=\"strict\"/>"
              + "</xs:complexType>"
              + "<xs:complexType name=\"Derived\">"
              + "  <xs:complexContent>"
              + "    <xs:restriction base=\"Base\">"
              + "      <xs:sequence/>"
              + "      <xs:anyAttribute namespace=\"##any\" processContents=\"skip\"/>"
              + "    </xs:restriction>"
              + "  </xs:complexContent>"
              + "</xs:complexType>");
    }

    @Test
    public void restrictionStrongerProcessContentsAccepted() {
        // strict ≥ lax — strengthening is fine.
        parseSnippet(
                "<xs:complexType name=\"Base\">"
              + "  <xs:sequence/>"
              + "  <xs:anyAttribute namespace=\"##any\" processContents=\"lax\"/>"
              + "</xs:complexType>"
              + "<xs:complexType name=\"Derived\">"
              + "  <xs:complexContent>"
              + "    <xs:restriction base=\"Base\">"
              + "      <xs:sequence/>"
              + "      <xs:anyAttribute namespace=\"##any\" processContents=\"strict\"/>"
              + "    </xs:restriction>"
              + "  </xs:complexContent>"
              + "</xs:complexType>");
    }

    @Test(expected = org.xmlet.xsdparser.xsdelements.exceptions.ParsingException.class)
    public void restrictionSkipAgainstImplicitAnyTypeRejected() {
        // Base has no explicit wildcard, but inherits xs:anyType's ##any/lax. processContents
        // skip < lax, so this restriction is invalid.
        parseSnippet(
                "<xs:complexType name=\"Base\">"
              + "  <xs:sequence/>"
              + "</xs:complexType>"
              + "<xs:complexType name=\"Derived\">"
              + "  <xs:complexContent>"
              + "    <xs:restriction base=\"Base\">"
              + "      <xs:sequence/>"
              + "      <xs:anyAttribute namespace=\"##any\" processContents=\"skip\"/>"
              + "    </xs:restriction>"
              + "  </xs:complexContent>"
              + "</xs:complexType>");
    }

    @Test
    public void restrictionLaxAgainstImplicitAnyTypeAccepted() {
        // Adding a wildcard to a restriction with no explicit base wildcard is OK as long as
        // the derived wildcard is at-least-as-strict as xs:anyType's inherited ##any/lax.
        parseSnippet(
                "<xs:complexType name=\"Base\">"
              + "  <xs:sequence/>"
              + "</xs:complexType>"
              + "<xs:complexType name=\"Derived\">"
              + "  <xs:complexContent>"
              + "    <xs:restriction base=\"Base\">"
              + "      <xs:sequence/>"
              + "      <xs:anyAttribute namespace=\"##any\" processContents=\"lax\"/>"
              + "    </xs:restriction>"
              + "  </xs:complexContent>"
              + "</xs:complexType>");
    }

    @Test
    public void extensionWildcardUnionAccepted() {
        // §3.10.6.2 — base + locally-declared wildcards must be expressible as a single union.
        // For the three forms (any/other/enum), the union is always representable; this test
        // confirms a benign extension still parses.
        parseSnippet(
                "<xs:complexType name=\"Base\">"
              + "  <xs:sequence/>"
              + "  <xs:anyAttribute namespace=\"http://example.com/ns1\"/>"
              + "</xs:complexType>"
              + "<xs:complexType name=\"Derived\">"
              + "  <xs:complexContent>"
              + "    <xs:extension base=\"Base\">"
              + "      <xs:sequence/>"
              + "      <xs:anyAttribute namespace=\"http://example.com/ns2\"/>"
              + "    </xs:extension>"
              + "  </xs:complexContent>"
              + "</xs:complexType>");
    }
}
