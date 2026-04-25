package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.exceptions.ParsingException;

import java.net.URL;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Schema-level constraint checks that can only run after parsing and reference resolution
 * have completed. Gathers state across the whole {@code parseElements} map.
 *
 * <p>Currently enforces:
 * <ul>
 *   <li>{@code xs:notation} {name} uniqueness within each {target namespace}
 *       (W3C XSD 1.0 Part 1 §3.12.6).</li>
 *   <li>Identity-constraint ({@code xs:unique}/{@code xs:key}/{@code xs:keyref}) name
 *       uniqueness within each {target namespace} (Part 1 §3.11.6 — Identity-constraint
 *       Definitions OK, clause 3).</li>
 *   <li>{@code xs:keyref}'s referenced key is in scope: the resolved {@code xs:key} or
 *       {@code xs:unique} must be declared on an ancestor (or the same) {@code xs:element}
 *       as the keyref (Part 1 §3.11.6, clause 5).</li>
 * </ul>
 */
public final class SchemaConstraintValidation {

    private SchemaConstraintValidation() {}

    public static void validate(Map<URL, List<ReferenceBase>> parseElements) {
        validateNotationUniqueness(parseElements);
        validateIdentityConstraintsAndScope(parseElements);
    }

    // ---------- xs:notation name uniqueness ----------

    private static void validateNotationUniqueness(Map<URL, List<ReferenceBase>> parseElements) {
        // Bucket by (target namespace, local name). For schemas without targetNamespace, the
        // bucket key is "" — still spec-correct: those notations share the "no namespace".
        Map<String, Map<String, XsdNotation>> byNs = new HashMap<>();
        for (List<ReferenceBase> refs : parseElements.values()) {
            for (ReferenceBase ref : refs) {
                XsdAbstractElement el = ref.getElement();
                if (el instanceof XsdNotation) {
                    XsdNotation notation = (XsdNotation) el;
                    String ns = targetNamespaceOf(notation);
                    String name = notation.getName();
                    if (name == null) continue;
                    Map<String, XsdNotation> bucket = byNs.computeIfAbsent(ns, k -> new HashMap<>());
                    XsdNotation existing = bucket.put(name, notation);
                    if (existing != null && existing != notation) {
                        throw new ParsingException(XsdNotation.XSD_TAG
                                + " element: duplicate name \"" + name
                                + "\" in target namespace \"" + ns + "\".");
                    }
                }
            }
        }
    }

    // ---------- identity-constraint uniqueness + keyref scope ----------

    private static void validateIdentityConstraintsAndScope(Map<URL, List<ReferenceBase>> parseElements) {
        // Walk the element tree under each schema, tracking the chain of enclosing XsdElement
        // instances so keyrefs can be scope-checked. Identity constraints found along the way
        // are bucketed for uniqueness checking.
        Map<String, Map<String, XsdIdentityConstraint>> byNs = new HashMap<>();
        Set<XsdAbstractElement> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        Deque<XsdElement> elementStack = new ArrayDeque<>();

        for (List<ReferenceBase> refs : parseElements.values()) {
            for (ReferenceBase ref : refs) {
                walk(ref.getElement(), elementStack, byNs, visited);
            }
        }
    }

    /**
     * Walks the schema tree looking for nested {@code xs:element} declarations (which is where
     * identity constraints live) and tracks the ancestor element stack for keyref-scope checks.
     * The recursion is exhaustive over exactly the element types that can transitively contain
     * an {@code xs:element}; everything else is a leaf.
     */
    private static void walk(XsdAbstractElement el,
                             Deque<XsdElement> elementStack,
                             Map<String, Map<String, XsdIdentityConstraint>> byNs,
                             Set<XsdAbstractElement> visited) {
        if (el == null || !visited.add(el)) return;

        if (el instanceof XsdElement) {
            XsdElement element = (XsdElement) el;
            elementStack.push(element);
            try {
                for (XsdIdentityConstraint constraint : element.getIdentityConstraints()) {
                    recordAndCheck(constraint, byNs, elementStack);
                }
                walk(element.getXsdComplexType(), elementStack, byNs, visited);
                walk(element.getXsdSimpleType(), elementStack, byNs, visited);
            } finally {
                elementStack.pop();
            }
        } else if (el instanceof XsdComplexType) {
            XsdComplexType ct = (XsdComplexType) el;
            walk(ct.getXsdChildElement(), elementStack, byNs, visited);
            walk(ct.getComplexContent(), elementStack, byNs, visited);
            walk(ct.getSimpleContent(), elementStack, byNs, visited);
        } else if (el instanceof XsdComplexContent) {
            XsdComplexContent cc = (XsdComplexContent) el;
            walk(cc.getXsdExtension(), elementStack, byNs, visited);
            walk(cc.getXsdRestriction(), elementStack, byNs, visited);
        } else if (el instanceof XsdSimpleContent) {
            XsdSimpleContent sc = (XsdSimpleContent) el;
            walk(sc.getXsdExtension(), elementStack, byNs, visited);
            walk(sc.getXsdRestriction(), elementStack, byNs, visited);
        } else if (el instanceof XsdMultipleElements
                || el instanceof XsdGroup
                || el instanceof XsdRestriction
                || el instanceof XsdExtension
                || el instanceof XsdRedefine) {
            // These are the container element types whose children may transitively include
            // xs:element. Use the standard child-stream API; reference resolution has already
            // populated everything by the time this pass runs.
            el.getXsdElements().forEach(child -> walk(child, elementStack, byNs, visited));
        }
        // No other element type can transitively contain xs:element children:
        //   xs:simpleType / xs:list / xs:union / facets   — value-space machinery only
        //   xs:attribute / xs:attributeGroup / xs:anyAttribute  — attributes, no elements
        //   xs:annotation / xs:appinfo / xs:documentation        — leaf annotations
        //   xs:notation                                          — top-level leaf
        //   xs:key / xs:keyref / xs:unique / xs:selector / xs:field — identity-constraint leaves
        //   xs:any                                               — wildcard leaf
        //   xs:import / xs:include                               — references to other schemas
    }

    private static void recordAndCheck(XsdIdentityConstraint constraint,
                                       Map<String, Map<String, XsdIdentityConstraint>> byNs,
                                       Deque<XsdElement> elementStack) {
        String name = constraint.getName();
        if (name == null) return;
        // Skip clones produced by reference resolution (e.g. when an element ref or include/redefine
        // duplicates the same logical declaration). Only the original counts for uniqueness.
        if (constraint.getCloneOf() != null) return;
        String ns = targetNamespaceOf(constraint);
        Map<String, XsdIdentityConstraint> bucket = byNs.computeIfAbsent(ns, k -> new HashMap<>());
        XsdIdentityConstraint existing = bucket.put(name, constraint);
        if (existing != null && existing != constraint && existing.getCloneOf() != constraint && constraint.getCloneOf() != existing) {
            throw new ParsingException("Identity constraint: duplicate name \"" + name
                    + "\" in target namespace \"" + ns + "\" (existing: "
                    + existing.getClass().getSimpleName() + ", new: "
                    + constraint.getClass().getSimpleName() + ").");
        }

        if (constraint instanceof XsdKeyref) {
            XsdKeyref keyref = (XsdKeyref) constraint;
            XsdIdentityConstraint target = keyref.getReferElement();
            if (target == null) return; // unresolved — separate concern
            // Spec: target must be declared on an ancestor of (or the same as) the element
            // containing this keyref. The elementStack holds the ancestor chain top-down.
            XsdElement targetEnclosing = enclosingElementOf(target);
            if (targetEnclosing == null) return;
            if (!elementStack.contains(targetEnclosing)) {
                throw new ParsingException(XsdKeyref.XSD_TAG + " element \"" + name + "\": "
                        + XsdAbstractElement.REFER_TAG + " target \"" + target.getName()
                        + "\" is not in scope (its enclosing " + XsdElement.XSD_TAG
                        + " is not an ancestor of this " + XsdKeyref.XSD_TAG + ").");
            }
        }
    }

    private static XsdElement enclosingElementOf(XsdIdentityConstraint constraint) {
        XsdAbstractElement parent = constraint.getParent();
        return parent instanceof XsdElement ? (XsdElement) parent : null;
    }

    private static String targetNamespaceOf(XsdAbstractElement el) {
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
