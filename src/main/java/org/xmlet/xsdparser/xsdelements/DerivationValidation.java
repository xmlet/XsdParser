package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.xsdelements.exceptions.ParsingException;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Helpers for the {@code final} guard on type derivations (W3C XSD 1.0 Part 1 §3.4.6
 * "Derivation Valid (Restriction, Complex)" and §3.14.6 / Part 2 §4.x for simple types) and
 * for the attribute-wildcard subset/union rules (§3.10.6 / §3.4.6 clause 4).
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

    /**
     * True if the space-separated {@code tokenList} contains {@code method} or {@code "#all"}.
     * @param tokenList space-separated final/block attribute value to inspect; may be {@code null}.
     * @param method derivation method to check for ({@link #EXTENSION}, {@link #RESTRICTION},
     *               {@link #LIST}, {@link #UNION}).
     * @return {@code true} when {@code method} or {@code "#all"} appears in {@code tokenList}.
     */
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
     * @param type the candidate type whose {@code final} attribute should be returned.
     * @return the {@code final} value or {@code null} for non user-defined types.
     */
    public static String typeFinal(XsdAbstractElement type) {
        if (type instanceof XsdComplexType) return ((XsdComplexType) type).getFinal();
        if (type instanceof XsdSimpleType) return ((XsdSimpleType) type).getFinalObj();
        return null;
    }

    // ---------- Attribute-wildcard subset / intersection (§3.10.6) ----------

    /**
     * Maximum derivation chain depth before the walker bails out. Real-world XSDs rarely exceed
     * 5–10 levels; 64 is generous and cheap to enforce, providing cycle protection without an
     * allocated visited set.
     */
    private static final int MAX_DERIVATION_DEPTH = 64;

    /**
     * Returns the effective {@code xs:anyAttribute} of {@code ct}: its local wildcard if
     * present, otherwise the effective wildcard of its derivation base, walked iteratively up
     * the {@code complexContent}/{@code simpleContent} → {@code restriction}/{@code extension}
     * chain. A {@code null} return means no local wildcard was found anywhere along the chain;
     * the caller should treat that as the implicit wildcard inherited from {@code xs:anyType}
     * ({@code namespace="##any"}, {@code processContents="lax"}).
     *
     * <p>This is an approximation in one specific case: when an extension somewhere along the
     * chain widens the wildcard via union, the spec's "effective wildcard" is the union of the
     * base's effective and the local. The walker returns the nearest local instead of computing
     * the union, which is correct for restriction-only chains (the common case) and an
     * under-approximation (potentially narrower than the true effective) for chains containing
     * extensions. The under-approximation is safe in the sense that it never accepts a schema
     * the spec rejects.
     *
     * <p>Implementation: iterative loop, no allocation. Bounded by {@code MAX_DERIVATION_DEPTH}
     * which doubles as cycle protection.
     *
     * @param ct the complex type whose effective wildcard should be computed.
     * @return the nearest declared {@code xs:anyAttribute} along the derivation chain, or
     *         {@code null} if none was found within the depth bound.
     */
    public static XsdAnyAttribute effectiveLocalWildcard(XsdComplexType ct) {
        XsdComplexType current = ct;
        for (int depth = 0; current != null && depth < MAX_DERIVATION_DEPTH; depth++) {
            if (current.getAnyAttribute() != null) return current.getAnyAttribute();
            XsdAbstractElement base = derivationBase(current);
            if (!(base instanceof XsdComplexType)) return null;
            current = (XsdComplexType) base;
        }
        return null;
    }

    /** Returns the immediate base type of {@code ct} along its complexContent/simpleContent derivation, or {@code null}. */
    private static XsdAbstractElement derivationBase(XsdComplexType ct) {
        XsdComplexContent cc = ct.getComplexContent();
        if (cc != null) {
            if (cc.getXsdRestriction() != null) return cc.getXsdRestriction().getBaseAsComplexType();
            if (cc.getXsdExtension()   != null) return cc.getXsdExtension().getBase();
            return null;
        }
        XsdSimpleContent sc = ct.getSimpleContent();
        if (sc != null) {
            if (sc.getXsdRestriction() != null) return sc.getXsdRestriction().getBaseAsComplexType();
            if (sc.getXsdExtension()   != null) return sc.getXsdExtension().getBase();
        }
        return null;
    }


    /**
     * Validates that {@code derivedWildcard}'s {namespace constraint} and {process contents}
     * form a valid subset/strengthening of {@code baseWildcard}'s, per Schema Component
     * Constraint "Wildcard Subset (3.10.6.1)":
     * <ol>
     *   <li>D's namespace constraint must be a subset of B's;</li>
     *   <li>D's processContents must be the same as or more restrictive than B's,
     *       in the order {@code skip < lax < strict}.</li>
     * </ol>
     *
     * <p>If D has no wildcard, the rule is trivially satisfied. If D has a wildcard but B
     * does not declare one explicitly, the comparison runs against the wildcard inherited
     * from {@code xs:anyType} ({@code namespace="##any"}, {@code processContents="lax"}) —
     * every complex type ultimately derives from {@code xs:anyType}, so a "missing" base
     * wildcard is really the inherited one.
     *
     * @param derivedWildcard the wildcard declared on the derived (restricting) type, or {@code null}.
     * @param baseWildcard the effective wildcard of the base type, or {@code null} to fall through
     *                     to {@code xs:anyType}'s implicit {@code ##any/lax}.
     * @param elementTag the XSD tag (e.g. {@code "xsd:restriction"}) used in error messages.
     */
    public static void requireWildcardSubset(XsdAnyAttribute derivedWildcard,
                                             XsdAnyAttribute baseWildcard,
                                             String elementTag) {
        if (derivedWildcard == null) return;

        // 1) Namespace constraint subset.
        NamespaceConstraint sub = NamespaceConstraint.from(derivedWildcard);
        NamespaceConstraint sup = baseWildcard != null
                ? NamespaceConstraint.from(baseWildcard)
                : new NamespaceConstraint(NamespaceConstraint.Kind.ANY, Collections.emptySet());
        if (!sub.isSubsetOf(sup)) {
            throw new ParsingException(elementTag + " element: derived "
                    + XsdAnyAttribute.XSD_TAG + " namespace=\"" + derivedWildcard.getNamespace()
                    + "\" is not a subset of the base's namespace=\""
                    + (baseWildcard != null ? baseWildcard.getNamespace() : "##any (xs:anyType)")
                    + "\" (W3C XSD 1.0 §3.10.6.1 — Wildcard Subset).");
        }

        // 2) processContents strictness: derived must be ≥ base's in the order skip<lax<strict.
        int derivedRank = processContentsRank(derivedWildcard.getProcessContents());
        int baseRank    = baseWildcard != null
                ? processContentsRank(baseWildcard.getProcessContents())
                : processContentsRank("lax"); // xs:anyType's wildcard is processContents="lax".
        if (derivedRank < baseRank) {
            String basePc = baseWildcard != null
                    ? baseWildcard.getProcessContents()
                    : "lax (xs:anyType)";
            throw new ParsingException(elementTag + " element: derived "
                    + XsdAnyAttribute.XSD_TAG + " " + XsdAbstractElement.PROCESS_CONTENTS
                    + "=\"" + derivedWildcard.getProcessContents()
                    + "\" is weaker than the base's " + XsdAbstractElement.PROCESS_CONTENTS
                    + "=\"" + basePc
                    + "\" (W3C XSD 1.0 §3.10.6.1 — Wildcard Subset, processContents must be"
                    + " the same as or more restrictive than the base's; skip < lax < strict).");
        }
    }

    /** Strictness rank for a {@code processContents} value: skip=0, lax=1, strict=2 (default). */
    private static int processContentsRank(String processContents) {
        if ("skip".equals(processContents)) return 0;
        if ("lax".equals(processContents))  return 1;
        return 2; // "strict" or unset (default per spec)
    }

    /**
     * Validates that the union of two attribute wildcards is expressible as a single wildcard,
     * per Schema Component Constraint "Attribute Wildcard Union (3.10.6.2)". Used by
     * {@code xs:complexContent/xs:extension}: the derived complexType's effective wildcard is
     * {@code base.wildcard ∪ local.wildcard}.
     *
     * <p>The XSD 1.0 lattice of namespace constraints is closed under union (case analysis in
     * §3.10.6.2 covers every combination), so this check is theoretically vacuous — but we
     * compute the union explicitly and throw if the result isn't a valid wildcard form, so any
     * future spec extension that introduces a new constraint kind would surface here.
     *
     * @param localWildcard the wildcard declared on the extending type, or {@code null}.
     * @param baseWildcard the base type's wildcard, or {@code null}.
     * @param elementTag the XSD tag (e.g. {@code "xsd:extension"}) used in error messages.
     */
    public static void requireWildcardUnionExpressible(XsdAnyAttribute localWildcard,
                                                       XsdAnyAttribute baseWildcard,
                                                       String elementTag) {
        if (localWildcard == null || baseWildcard == null) return;
        NamespaceConstraint a = NamespaceConstraint.from(localWildcard);
        NamespaceConstraint b = NamespaceConstraint.from(baseWildcard);
        if (a.unionWith(b) == null) {
            throw new ParsingException(elementTag + " element: union of "
                    + XsdAnyAttribute.XSD_TAG + " namespace=\"" + localWildcard.getNamespace()
                    + "\" with the base's namespace=\"" + baseWildcard.getNamespace()
                    + "\" is not expressible as a single wildcard (W3C XSD 1.0 §3.10.6.2).");
        }
    }

    /**
     * Models the value space of an {@code xs:anyAttribute} {@code namespace} attribute as one
     * of three forms (per spec):
     * <ul>
     *   <li>{@code ANY} — the universal set ({@code namespace="##any"});</li>
     *   <li>{@code NOT(excluded)} — everything except the namespaces in {@code excluded}
     *       ({@code namespace="##other"} expands to {@code NOT({targetNamespace, ""})});</li>
     *   <li>{@code ENUM(set)} — only the listed namespaces. The {@code ##targetNamespace}
     *       sentinel resolves to the enclosing schema's targetNamespace; {@code ##local}
     *       resolves to the empty namespace (no namespace).</li>
     * </ul>
     */
    private static final class NamespaceConstraint {
        enum Kind { ANY, NOT, ENUM }

        final Kind kind;
        final Set<String> tokens;

        private NamespaceConstraint(Kind kind, Set<String> tokens) {
            this.kind = kind;
            this.tokens = tokens;
        }

        /** Build a constraint from a wildcard, resolving {@code ##targetNamespace}/{@code ##local}/{@code ##other} against the wildcard's enclosing schema. */
        static NamespaceConstraint from(XsdAnyAttribute wildcard) {
            String value = wildcard.getNamespace();
            String targetNs = enclosingTargetNamespace(wildcard);
            return parse(value, targetNs);
        }

        static NamespaceConstraint parse(String value, String targetNs) {
            if (value == null || value.isEmpty()) {
                return new NamespaceConstraint(Kind.ANY, Collections.emptySet());
            }
            String trimmed = value.trim();
            if (trimmed.equals("##any")) {
                return new NamespaceConstraint(Kind.ANY, Collections.emptySet());
            }
            if (trimmed.equals("##other")) {
                Set<String> excluded = new HashSet<>();
                excluded.add(targetNs == null ? "" : targetNs);
                excluded.add(""); // ##other also excludes 'absent' (no-namespace) per §3.10.1
                return new NamespaceConstraint(Kind.NOT, excluded);
            }
            Set<String> set = new HashSet<>();
            for (String token : trimmed.split("\\s+")) {
                if (token.equals("##targetNamespace")) {
                    set.add(targetNs == null ? "" : targetNs);
                } else if (token.equals("##local")) {
                    set.add("");
                } else {
                    set.add(token);
                }
            }
            return new NamespaceConstraint(Kind.ENUM, set);
        }

        boolean isSubsetOf(NamespaceConstraint sup) {
            if (sup.kind == Kind.ANY) return true;
            if (this.kind == Kind.ANY) return false;
            if (this.kind == Kind.ENUM && sup.kind == Kind.ENUM) {
                return sup.tokens.containsAll(this.tokens);
            }
            if (this.kind == Kind.ENUM && sup.kind == Kind.NOT) {
                for (String token : this.tokens) {
                    if (sup.tokens.contains(token)) return false;
                }
                return true;
            }
            if (this.kind == Kind.NOT && sup.kind == Kind.NOT) {
                return this.tokens.containsAll(sup.tokens);
            }
            // NOT in ENUM: NOT admits an infinite set; ENUM is finite. Subset only if NOT
            // admits no namespaces (the universe is empty), which doesn't occur in practice.
            return false;
        }

        /**
         * Returns a representable union, or {@code null} if no single wildcard can express it.
         * Per §3.10.6.2 case analysis, the result is always non-null for the three kinds we
         * model — but we keep the method total so a future {@code Kind} addition would surface.
         */
        NamespaceConstraint unionWith(NamespaceConstraint other) {
            if (this.kind == Kind.ANY || other.kind == Kind.ANY) {
                return new NamespaceConstraint(Kind.ANY, Collections.emptySet());
            }
            if (this.kind == Kind.ENUM && other.kind == Kind.ENUM) {
                Set<String> set = new HashSet<>(this.tokens);
                set.addAll(other.tokens);
                return new NamespaceConstraint(Kind.ENUM, set);
            }
            if (this.kind == Kind.NOT && other.kind == Kind.NOT) {
                // NOT(A) ∪ NOT(B) admits everything except A ∩ B.
                Set<String> intersection = new HashSet<>(this.tokens);
                intersection.retainAll(other.tokens);
                if (intersection.isEmpty()) {
                    return new NamespaceConstraint(Kind.ANY, Collections.emptySet());
                }
                return new NamespaceConstraint(Kind.NOT, intersection);
            }
            // ENUM ∪ NOT: admits NOT's exclusions minus those that are in ENUM.
            NamespaceConstraint enumC = this.kind == Kind.ENUM ? this : other;
            NamespaceConstraint notC  = this.kind == Kind.NOT  ? this : other;
            Set<String> remaining = new HashSet<>(notC.tokens);
            remaining.removeAll(enumC.tokens);
            if (remaining.isEmpty()) {
                return new NamespaceConstraint(Kind.ANY, Collections.emptySet());
            }
            return new NamespaceConstraint(Kind.NOT, remaining);
        }

        private static String enclosingTargetNamespace(XsdAbstractElement el) {
            XsdAbstractElement cur = el;
            while (cur != null) {
                if (cur instanceof XsdSchema) {
                    String ns = ((XsdSchema) cur).getTargetNamespace();
                    return ns == null ? "" : ns;
                }
                cur = cur.getParent();
            }
            return "";
        }
    }
}
