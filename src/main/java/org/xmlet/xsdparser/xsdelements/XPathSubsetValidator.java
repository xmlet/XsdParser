package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.xsdelements.exceptions.ParsingException;

import java.util.regex.Pattern;

/**
 * Validates the {@code xpath} attribute value of {@link XsdSelector} and {@link XsdField} against
 * the restricted XPath subsets defined by W3C XSD 1.0 Part 1 §3.11.6.3 and the regular-expression
 * patterns on {@code xs:selector}/{@code xs:field} in the schema-for-schemas.
 *
 * <p>Selector pattern (schema-for-schemas):
 * <pre>
 *   (\.//)? ( ((child::)? NameTest) | '.' ) ( '/' (((child::)? NameTest) | '.') )*
 *   ( '|' that whole path again )*
 * </pre>
 *
 * <p>Field pattern (schema-for-schemas) — adds an optional attribute step (using either
 * {@code @} or the explicit {@code attribute::} axis) as the final step:
 * <pre>
 *   (\.//)? ( (((child::)? NameTest) | '.') '/' )*
 *   ( ((child::)? NameTest) | '.' | (('attribute::'|'@') NameTest) )
 *   ( '|' that whole path again )*
 * </pre>
 *
 * <p>{@code NameTest ::= QName | '*' | NCName ':' '*'}
 *
 * <p>Whitespace handling: per the schema-for-schemas, the xpath attribute is typed
 * {@code xs:token}, which {@code collapse}-normalises whitespace before pattern matching. This
 * validator collapses internal multi-space + tabs/CR/LF to single spaces and strips the
 * leading/trailing space, then matches. The resulting pattern admits no whitespace at all
 * (matching the contiguous schema-for-schemas regex).
 */
final class XPathSubsetValidator {

    /** XML 1.0 NCName: starts with letter or '_', followed by letters/digits/{@code . - _}. */
    private static final String NCNAME = "[\\p{L}_][\\p{L}\\p{Nd}._\\-]*";
    /** Namespaces 1.0 QName: optional {@code prefix:} followed by NCName. */
    private static final String QNAME = NCNAME + "(?::" + NCNAME + ")?";
    /** NameTest: a QName, a bare {@code *}, or {@code NCName:*}. */
    private static final String NAME_TEST = "(?:" + QNAME + "|\\*|" + NCNAME + ":\\*)";
    /** Step: a literal {@code .} or an optional {@code child::} prefix on a NameTest. */
    private static final String STEP = "(?:\\.|(?:child::)?" + NAME_TEST + ")";
    /** Final step on a Field path: either a regular Step or an attribute step. */
    private static final String FIELD_FINAL_STEP =
            "(?:" + STEP + "|(?:attribute::|@)" + NAME_TEST + ")";

    private static final Pattern SELECTOR_PATH = Pattern.compile(
            "^(?:\\.//)?" + STEP + "(?:/" + STEP + ")*$");

    private static final Pattern FIELD_PATH = Pattern.compile(
            "^(?:\\.//)?(?:" + STEP + "/)*" + FIELD_FINAL_STEP + "$");

    private XPathSubsetValidator() {}

    static void validateSelector(String xpath, String elementTag) {
        validate(xpath, elementTag, SELECTOR_PATH, "selector");
    }

    static void validateField(String xpath, String elementTag) {
        validate(xpath, elementTag, FIELD_PATH, "field");
    }

    private static void validate(String xpath, String elementTag, Pattern pathPattern, String role) {
        if (xpath == null) {
            throw new ParsingException(elementTag + " element: " + XsdAbstractElement.XPATH_TAG + " is required.");
        }
        // xs:token whitespace="collapse": tabs/CR/LF and runs of spaces -> single space, trimmed.
        String collapsed = xpath.replaceAll("[\\t\\n\\r ]+", " ").trim();
        if (collapsed.isEmpty()) {
            throw new ParsingException(elementTag + " element: " + XsdAbstractElement.XPATH_TAG + " must not be empty.");
        }
        // The SfS pattern is contiguous, but XPath 1.0's token-whitespace conventions admit
        // whitespace around the '|' alternation operator (and every conformant validator
        // accepts it). Strip it before splitting; everything inside a path must remain
        // whitespace-free, matching the pattern exactly.
        for (String path : collapsed.split("\\s*\\|\\s*", -1)) {
            if (!pathPattern.matcher(path).matches()) {
                throw new ParsingException(elementTag + " element: " + XsdAbstractElement.XPATH_TAG
                        + " \"" + xpath + "\" is not in the restricted XPath subset for " + role + "s"
                        + " (W3C XSD 1.0 §3.11.6.3 / schema-for-schemas pattern).");
            }
        }
    }
}
