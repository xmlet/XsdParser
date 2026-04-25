package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.xsdelements.exceptions.ParsingException;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Helpers that enforce lexical-form constraints from the XML 1.0 / Namespaces 1.0 / XSD 1.0
 * specs. Used by element constructors to validate attribute values that are typed as
 * {@code xs:NCName}, {@code xs:QName}, {@code xs:token}, or that come from the schema-for-
 * schemas' fixed attribute set.
 *
 * <p>The {@code id} attribute on every XSD element is also typed as {@code xs:ID} (an NCName);
 * those checks are surfaced via {@link #requireNCNameOrNull(String, String, String)} when the
 * element opts in.
 */
final class LexicalValidator {

    /** XML 1.0 NCName: starts with letter or '_', followed by letters/digits/{@code . - _}. */
    private static final Pattern NCNAME =
            Pattern.compile("[\\p{L}_][\\p{L}\\p{Nd}._\\-]*");

    /** Namespaces 1.0 QName: optional {@code prefix:} followed by NCName. */
    private static final Pattern QNAME =
            Pattern.compile("[\\p{L}_][\\p{L}\\p{Nd}._\\-]*(?::[\\p{L}_][\\p{L}\\p{Nd}._\\-]*)?");

    /**
     * XSD Part 2 §3.3.2 token: no carriage-return / line-feed / tab, no leading or trailing
     * space, and no internal sequences of two or more spaces. Empty string accepted (canonical
     * form of a fully-collapsed empty token).
     */
    private static final Pattern TOKEN = Pattern.compile("|\\S+(?: \\S+)*");

    private LexicalValidator() {}

    /** Throws if {@code value} is non-null and not a valid xs:NCName. */
    static void requireNCNameOrNull(String value, String elementTag, String attribute) {
        if (value == null) return;
        if (!NCNAME.matcher(value).matches()) {
            throw new ParsingException(elementTag + " element: " + attribute
                    + " attribute \"" + value + "\" is not a valid " + "xs:NCName.");
        }
    }

    /** Throws if {@code value} is non-null and not a valid xs:QName. */
    static void requireQNameOrNull(String value, String elementTag, String attribute) {
        if (value == null) return;
        if (!QNAME.matcher(value).matches()) {
            throw new ParsingException(elementTag + " element: " + attribute
                    + " attribute \"" + value + "\" is not a valid " + "xs:QName.");
        }
    }

    /** Throws if {@code value} is non-null and not a valid xs:token. */
    static void requireTokenOrNull(String value, String elementTag, String attribute) {
        if (value == null) return;
        if (!TOKEN.matcher(value).matches()) {
            throw new ParsingException(elementTag + " element: " + attribute
                    + " attribute \"" + value + "\" is not a valid " + "xs:token "
                    + "(no leading/trailing whitespace, no internal multi-space, no tab/CR/LF).");
        }
    }

    /**
     * Rejects unprefixed attributes that aren't in {@code allowed}. Namespace declarations
     * ({@code xmlns}, {@code xmlns:*}) and prefixed (other-namespace) attributes are passed
     * through, matching the {@code <xs:anyAttribute namespace="##other"/>} wildcard on the
     * schema-for-schemas' {@code xs:openAttrs} base.
     */
    static void rejectUnknownAttributes(Map<String, String> attrs, Set<String> allowed,
                                        String elementTag) {
        for (String name : attrs.keySet()) {
            if (name.equals("xmlns") || name.startsWith("xmlns:")) continue;
            if (name.indexOf(':') >= 0) continue;
            if (allowed.contains(name)) continue;
            throw new ParsingException(elementTag + " element: attribute \"" + name
                    + "\" is not allowed (per the schema-for-schemas).");
        }
    }
}
