package org.xmlet.xsdparser.xsdelements;

/**
 * Helpers for the {@code final} guard on type derivations (W3C XSD 1.0 Part 1 §3.4.6
 * "Derivation Valid (Restriction, Complex)" and §3.14.6 / Part 2 §4.x for simple types).
 *
 * <p>{@code final} on a parsed type forbids further derivation by the listed methods. The
 * full set of tokens is {@code extension}, {@code restriction}, {@code list}, {@code union},
 * {@code #all} — corresponding to {@code xs:extension}, {@code xs:restriction},
 * {@code xs:list itemType=...}, and {@code xs:union memberTypes=...} respectively. Each
 * derivation site (constructor or {@code replaceUnsolvedElements} hook) calls
 * {@link #tokenListBlocks(String, String)} against {@link #typeFinal(XsdAbstractElement)} of
 * the resolved base.
 *
 * <p>Built-in datatypes don't carry a {@code final} attribute, so this guard never spuriously
 * fires for them; {@link #typeFinal} returns {@code null} for non user-defined types.
 */
public final class DerivationValidation {

    public static final String EXTENSION = "extension";
    public static final String RESTRICTION = "restriction";
    public static final String LIST = "list";
    public static final String UNION = "union";
    public static final String ALL = "#all";

    private DerivationValidation() {}

    /** True if the space-separated {@code tokenList} contains {@code method} or {@code "#all"}. */
    public static boolean tokenListBlocks(String tokenList, String method) {
        if (tokenList == null || tokenList.isEmpty()) return false;
        for (String token : tokenList.trim().split("\\s+")) {
            if (token.equals(ALL) || token.equals(method)) return true;
        }
        return false;
    }

    /**
     * Returns the {@code final} attribute of a resolved type, or {@code null} if the argument
     * is not a parsed user-defined complex/simple type. The returned value already includes any
     * {@code finalDefault} inherited from the enclosing schema (parsing handles that merge).
     */
    public static String typeFinal(XsdAbstractElement type) {
        if (type instanceof XsdComplexType) return ((XsdComplexType) type).getFinal();
        if (type instanceof XsdSimpleType) return ((XsdSimpleType) type).getFinalObj();
        return null;
    }
}
