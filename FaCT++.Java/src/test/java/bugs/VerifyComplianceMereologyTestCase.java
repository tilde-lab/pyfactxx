package bugs;

import static org.junit.Assert.assertTrue;

import javax.annotation.Nonnull;

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

@SuppressWarnings("javadoc")
public class VerifyComplianceMereologyTestCase extends VerifyComplianceBase {

    @Nonnull
    protected String input = "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
            + "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n"
            + "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n"
            + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
            + "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n"
            + "Ontology(\n"
            + "Declaration(Class(<urn:mereology#Abstract_Entity>))\n"
            + "Declaration(Class(<urn:mereology#Mental_Entity>))\n"
            + "Declaration(Class(<urn:mereology#Mental_Object>))\n"
            + "Declaration(Class(<urn:mereology#Occurrence>))\n"
            + "Declaration(Class(<urn:mereology#Physical_Entity>))\n"
            + "Declaration(Class(<urn:mereology#Spatio_Temporal_Occurrence>))\n"
            + "Declaration(Class(<urn:mereology#Atom>))\n"
            + "Declaration(Class(<urn:mereology#Composition>))\n"
            + "Declaration(Class(<urn:mereology#Pair>))\n"
            + "Declaration(Class(<urn:mereology#Part>))\n"
            + "Declaration(Class(<urn:mereology#Whole>))\n"
            + "Declaration(ObjectProperty(<urn:mereology#component>))\n"
            + "Declaration(ObjectProperty(<urn:mereology#component_of>))\n"
            + "Declaration(ObjectProperty(<urn:mereology#composed_of>))\n"
            + "Declaration(ObjectProperty(<urn:mereology#composes>))\n"
            + "Declaration(ObjectProperty(<urn:mereology#contained_in>))\n"
            + "Declaration(ObjectProperty(<urn:mereology#contains>))\n"
            + "Declaration(ObjectProperty(<urn:mereology#direct_part>))\n"
            + "Declaration(ObjectProperty(<urn:mereology#direct_part_of>))\n"
            + "Declaration(ObjectProperty(<urn:mereology#member>))\n"
            + "Declaration(ObjectProperty(<urn:mereology#member_of>))\n"
            + "Declaration(ObjectProperty(<urn:mereology#part>))\n"
            + "Declaration(ObjectProperty(<urn:mereology#part_of>))\n"
            + "Declaration(ObjectProperty(<urn:mereology#strict_part>))\n"
            + "Declaration(ObjectProperty(<urn:mereology#strict_part_of>))\n"
            + "SubClassOf(<urn:mereology#Mental_Object> <urn:mereology#Mental_Entity>)\n"
            + "SubClassOf(<urn:mereology#Spatio_Temporal_Occurrence> <urn:mereology#Occurrence>)\n"
            + "SubClassOf(<urn:mereology#Atom> <urn:mereology#Abstract_Entity>)\n"
            + "DisjointClasses(<urn:mereology#Atom> <urn:mereology#Whole>)\n"
            + "SubClassOf(<urn:mereology#Composition> <urn:mereology#Whole>)\n"
            + "SubClassOf(<urn:mereology#Pair> <urn:mereology#Composition>)\n"
            + "SubClassOf(<urn:mereology#Pair> ObjectExactCardinality(2 <urn:mereology#strict_part> <urn:mereology#Part>))\n"
            + "EquivalentClasses(<urn:mereology#Part> ObjectSomeValuesFrom(<urn:mereology#strict_part_of> <urn:mereology#Whole>))\n"
            + "SubClassOf(<urn:mereology#Part> <urn:mereology#Abstract_Entity>)\n"
            + "SubClassOf(<urn:mereology#Part> ObjectAllValuesFrom(<urn:mereology#strict_part_of> <urn:mereology#Whole>))\n"
            + "EquivalentClasses(<urn:mereology#Whole> ObjectSomeValuesFrom(<urn:mereology#strict_part> <urn:mereology#Part>))\n"
            + "SubClassOf(<urn:mereology#Whole> <urn:mereology#Abstract_Entity>)\n"
            + "SubClassOf(<urn:mereology#Whole> ObjectAllValuesFrom(<urn:mereology#strict_part> <urn:mereology#Part>))\n"
            + "SubObjectPropertyOf(<urn:mereology#component> <urn:mereology#strict_part>)\n"
            + "InverseObjectProperties(<urn:mereology#component_of> <urn:mereology#component>)\n"
            + "SubObjectPropertyOf(<urn:mereology#component_of> <urn:mereology#strict_part_of>)\n"
            + "SubObjectPropertyOf(<urn:mereology#composed_of> <urn:mereology#part>)\n"
            + "InverseObjectProperties(<urn:mereology#composes> <urn:mereology#composed_of>)\n"
            + "TransitiveObjectProperty(<urn:mereology#composed_of>)\n"
            + "SubObjectPropertyOf(<urn:mereology#composes> <urn:mereology#part_of>)\n"
            + "TransitiveObjectProperty(<urn:mereology#composes>)\n"
            + "SubObjectPropertyOf(<urn:mereology#contained_in> <urn:mereology#part_of>)\n"
            + "InverseObjectProperties(<urn:mereology#contained_in> <urn:mereology#contains>)\n"
            + "TransitiveObjectProperty(<urn:mereology#contained_in>)\n"
            + "SubObjectPropertyOf(<urn:mereology#contains> <urn:mereology#part>)\n"
            + "TransitiveObjectProperty(<urn:mereology#contains>)\n"
            + "EquivalentObjectProperties(<urn:mereology#direct_part> <urn:mereology#strict_part>)\n"
            + "SubObjectPropertyOf(<urn:mereology#direct_part> <urn:mereology#part>)\n"
            + "InverseObjectProperties(<urn:mereology#direct_part_of> <urn:mereology#direct_part>)\n"
            + "EquivalentObjectProperties(<urn:mereology#direct_part_of> <urn:mereology#strict_part_of>)\n"
            + "SubObjectPropertyOf(<urn:mereology#direct_part_of> <urn:mereology#part_of>)\n"
            + "SubObjectPropertyOf(<urn:mereology#member> <urn:mereology#strict_part>)\n"
            + "InverseObjectProperties(<urn:mereology#member_of> <urn:mereology#member>)\n"
            + "SubObjectPropertyOf(<urn:mereology#member_of> <urn:mereology#strict_part_of>)\n"
            + "InverseObjectProperties(<urn:mereology#part> <urn:mereology#part_of>)\n"
            + "TransitiveObjectProperty(<urn:mereology#part>)\n"
            + "TransitiveObjectProperty(<urn:mereology#part_of>)\n"
            + "SubObjectPropertyOf(<urn:mereology#strict_part> <urn:mereology#part>)\n"
            + "InverseObjectProperties(<urn:mereology#strict_part_of> <urn:mereology#strict_part>)\n"
            + "SubObjectPropertyOf(<urn:mereology#strict_part_of> <urn:mereology#part_of>))";

    @Override
    protected OWLOntology load(String in) throws OWLOntologyCreationException {
        return loadFromString(input);
    }

    @Override
    protected String input() {
        return "/AF_mereology.owl.xml";
    }

    @Nonnull
    protected OWLClass Abstract_Entity = C("urn:mereology#Abstract_Entity");
    @Nonnull
    protected OWLClass Composition = C("urn:mereology#Composition");
    @Nonnull
    protected OWLClass Whole = C("urn:mereology#Whole");
    @Nonnull
    protected OWLClass Pair = C("urn:mereology#Pair");
    @Nonnull
    protected OWLClass Atom = C("urn:mereology#Atom");
    @Nonnull
    protected OWLClass Nothing = C("http://www.w3.org/2002/07/owl#Nothing");
    @Nonnull
    protected OWLClass Part = C("urn:mereology#Part");
    @Nonnull
    protected OWLClass Physical_Entity = C("urn:mereology#Physical_Entity");
    @Nonnull
    protected OWLClass Occurrence = C("urn:mereology#Occurrence");
    @Nonnull
    protected OWLClass Mental_Entity = C("urn:mereology#Mental_Entity");
    @Nonnull
    protected OWLClass Thing = C("http://www.w3.org/2002/07/owl#Thing");

    @Test
    public void shouldPassgetSuperClassesWholefalse() {
        equal(reasoner.getSuperClasses(Whole, false), Thing, Abstract_Entity);
    }

    @Test
    public void shouldPassgetSubClassesAbstract_Entityfalse() {
        equal(reasoner.getSubClasses(Abstract_Entity, false), Whole, Pair,
                Composition, Atom, Nothing, Part);
    }

    @Test
    public void shouldPassgetSubClassesAbstract_Entitytrue() {
        equal(reasoner.getSubClasses(Abstract_Entity, true), Whole, Atom, Part);
    }

    @Test
    public void shouldPassgetSuperClassesPairfalse() {
        equal(reasoner.getSuperClasses(Pair, false), Thing, Whole,
                Abstract_Entity, Composition);
    }

    @Test
    public void shouldPassgetSuperClassesCompositionfalse() {
        equal(reasoner.getSuperClasses(Composition, false), Thing, Whole,
                Abstract_Entity);
    }

    @Test
    public void shouldPassgetSuperClassesPartfalse() {
        equal(reasoner.getSuperClasses(Part, false), Thing, Abstract_Entity);
    }

    @Test
    public void shouldPassgetSubClassesThingtrue() {
        equal(reasoner.getSubClasses(Thing, true), Physical_Entity,
                Abstract_Entity, Occurrence, Mental_Entity);
    }

    @Test
    public void shouldPassgetSuperClassesWholetrue() {
        equal(reasoner.getSuperClasses(Whole, true), Abstract_Entity);
    }

    @Test
    public void shouldPassgetSuperClassesParttrue() {
        equal(reasoner.getSuperClasses(Part, true), Abstract_Entity);
    }

    @Test
    public void shouldPassisEntailedSubClassOfCompositionAbstract_Entity() {
        assertTrue(reasoner.isEntailed(df.getOWLSubClassOfAxiom(Composition,
                Abstract_Entity)));
    }

    @Test
    public void shouldPassisEntailedSubClassOfPairAbstract_Entity() {
        assertTrue(reasoner.isEntailed(df.getOWLSubClassOfAxiom(Pair,
                Abstract_Entity)));
    }
}
