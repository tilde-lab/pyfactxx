package conformancetests;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;

import javax.annotation.Nonnull;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import testbase.TestBase;

@SuppressWarnings("javadoc")
public class ProcessesTestCase extends TestBase {

    private OWLReasoner jfact1;
    private OWLReasoner jfact2;
    private OWLDataFactory df = OWLManager.getOWLDataFactory();
    private OWLOntology o1;
    private OWLOntology o2;
    @Nonnull
    private OWLObjectProperty topObject = df.getOWLTopObjectProperty();
    @Nonnull
    private OWLClass composition = df.getOWLClass(IRI
            .create("urn:mereology#Composition"));
    @Nonnull
    private OWLClass abstractEntity = df.getOWLClass(IRI
            .create("urn:mereology#Abstract_Entity"));
    @Nonnull
    private OWLClass whole = df.getOWLClass(IRI.create("urn:mereology#Whole"));
    @Nonnull
    private OWLClass pair = df.getOWLClass(IRI.create("urn:mereology#Pair"));
    @Nonnull
    private OWLClass part = df.getOWLClass(IRI.create("urn:mereology#Part"));
    @Nonnull
    private OWLClass serviceModel = df.getOWLClass(IRI
            .create("urn:Service#ServiceModel"));
    @Nonnull
    private OWLClass compositeProcess = df.getOWLClass(IRI
            .create("urn:Process#CompositeProcess"));
    @Nonnull
    private OWLObjectProperty composedOf = df.getOWLObjectProperty(IRI
            .create("urn:Process#composedOf"));
    @Nonnull
    private OWLClass simpleProcess = df.getOWLClass(IRI
            .create("urn:Process#SimpleProcess"));
    @Nonnull
    private OWLClass atomicProcess = df.getOWLClass(IRI
            .create("urn:Process#AtomicProcess"));
    @Nonnull
    private OWLClass process = df
            .getOWLClass(IRI.create("urn:Process#Process"));
    @Nonnull
    private String input1 = "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
            + "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n"
            + "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n"
            + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
            + "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n"
            + "Ontology(\n"
            + "Declaration(Class(<urn:mereology#Abstract_Entity>))\n"
            + "Declaration(Class(<urn:mereology#Composition>))\n"
            + "Declaration(Class(<urn:mereology#Pair>))\n"
            + "Declaration(Class(<urn:mereology#Part>))\n"
            + "Declaration(Class(<urn:mereology#Whole>))\n"
            + "Declaration(Class(owl:Thing))\n"
            + "Declaration(ObjectProperty(<urn:mereology#strict_part>))\n"
            + "Declaration(ObjectProperty(<urn:mereology#strict_part_of>))\n"
            + "Declaration(AnnotationProperty(rdfs:comment))\n"
            + "Declaration(AnnotationProperty(owl:versionInfo))\n"
            + "SubClassOf(<urn:mereology#Abstract_Entity> owl:Thing)\n"
            + "SubClassOf(<urn:mereology#Pair> <urn:mereology#Abstract_Entity>)\n"
            + "SubClassOf(<urn:mereology#Pair> ObjectExactCardinality(2 <urn:mereology#strict_part> <urn:mereology#Part>))\n"
            + "EquivalentClasses(<urn:mereology#Part> ObjectSomeValuesFrom(<urn:mereology#strict_part_of> <urn:mereology#Whole>))\n"
            + "SubClassOf(<urn:mereology#Part> <urn:mereology#Abstract_Entity>)\n"
            + "SubClassOf(<urn:mereology#Whole> <urn:mereology#Abstract_Entity>)\n"
            + "SubObjectPropertyOf(<urn:mereology#strict_part> owl:topObjectProperty)\n"
            + "InverseObjectProperties(<urn:mereology#strict_part_of> <urn:mereology#strict_part>)\n"
            + "SubObjectPropertyOf(<urn:mereology#strict_part_of> owl:topObjectProperty))";
    private String input2 = "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
            + "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n"
            + "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n"
            + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
            + "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n"
            + "Ontology(\n"
            + "Declaration(Class(<urn:Process#AtomicProcess>))\n"
            + "Declaration(Class(<urn:Process#CompositeProcess>))\n"
            + "Declaration(Class(<urn:Process#Process>))\n"
            + "Declaration(Class(<urn:Process#SimpleProcess>))\n"
            + "Declaration(Class(<urn:Service#ServiceModel>))\n"
            + "Declaration(ObjectProperty(<urn:Process#composedOf>))\n"
            + "SubClassOf(<urn:Process#AtomicProcess> <urn:Process#Process>)\n"
            + "EquivalentClasses(<urn:Process#CompositeProcess> ObjectIntersectionOf(ObjectExactCardinality(1 <urn:Process#composedOf>) <urn:Process#Process>))\n"
            + "SubClassOf(<urn:Process#CompositeProcess> <urn:Process#Process>)\n"
            + "EquivalentClasses(<urn:Process#Process> ObjectUnionOf(<urn:Process#CompositeProcess> <urn:Process#AtomicProcess>))\n"
            + "SubClassOf(<urn:Process#Process> <urn:Service#ServiceModel>)\n"
            + "SubClassOf(<urn:Process#SimpleProcess> <urn:Process#Process>)\n"
            + "ObjectPropertyDomain(<urn:Process#composedOf> <urn:Process#CompositeProcess>))";

    @SuppressWarnings("null")
    @Before
    public void setUp() throws OWLOntologyCreationException {
        o1 = OWLManager.createOWLOntologyManager()
                .loadOntologyFromOntologyDocument(
                        new StringDocumentSource(input1));
        o2 = OWLManager.createOWLOntologyManager()
                .loadOntologyFromOntologyDocument(
                        new StringDocumentSource(input2));
        jfact1 = factory().createReasoner(o1);
        jfact1.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        jfact2 = factory().createReasoner(o2);
        jfact2.precomputeInferences(InferenceType.CLASS_HIERARCHY);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static void equal(NodeSet<?> node, Object... objects) {
        assertEquals(new HashSet(Arrays.asList(objects)), node.getFlattened());
    }

    @Test
    public void shouldTestgetgetSuperClasses_Part_true() {
        NodeSet<OWLClass> result = jfact1.getSuperClasses(part, true);
        equal(result, abstractEntity);
    }

    @Test
    public void shouldTestgetgetSuperClasses_Part_true2() {
        NodeSet<OWLClass> result = jfact2.getSuperClasses(simpleProcess, true);
        equal(result, process);
        result = jfact2.getSuperClasses(atomicProcess, true);
        equal(result, process);
        result = jfact2.getSuperClasses(process, true);
        equal(result, serviceModel);
    }

    @Test
    public void shouldTestgetgetSubClasses_top_true() {
        NodeSet<OWLClass> result = jfact1.getSubClasses(df.getOWLThing(), true);
        equal(result, abstractEntity, composition);
        result = jfact2.getSubClasses(df.getOWLThing(), true);
        equal(result, serviceModel);
    }

    @Test
    public void shouldTestgetObjectPropertyDomains_top_false() {
        NodeSet<OWLClass> result = jfact1.getObjectPropertyDomains(topObject,
                false);
        equal(result, df.getOWLThing());
    }

    @Test
    public void shouldTestgetObjectPropertyDomains_top_false2() {
        NodeSet<OWLClass> result = jfact2.getObjectPropertyDomains(topObject,
                false);
        equal(result, df.getOWLThing());
    }

    @Test
    public void shouldTestgetObjectPropertyDomains_top_false3() {
        NodeSet<OWLClass> result = jfact2.getObjectPropertyDomains(composedOf,
                true);
        equal(result, compositeProcess);
    }

    @Test
    public void shouldTestgetObjectPropertyRanges_top_false() {
        NodeSet<OWLClass> result = jfact1.getObjectPropertyRanges(topObject,
                false);
        equal(result, df.getOWLThing());
    }

    @Test
    public void shouldTestgetObjectPropertyRanges_top_false2() {
        NodeSet<OWLClass> result = jfact2.getObjectPropertyRanges(topObject,
                false);
        equal(result, df.getOWLThing());
    }

    @Test
    public void shouldTestgetObjectPropertyDomains_top_true() {
        NodeSet<OWLClass> result = jfact1.getObjectPropertyDomains(topObject,
                true);
        equal(result, df.getOWLThing());
    }

    @Test
    public void shouldTestgetObjectPropertyDomains_top_true2() {
        NodeSet<OWLClass> result = jfact2.getObjectPropertyDomains(topObject,
                true);
        equal(result, df.getOWLThing());
    }

    @Test
    public void shouldTestgetObjectPropertyRanges_top_true() {
        NodeSet<OWLClass> result = jfact1.getObjectPropertyRanges(topObject,
                true);
        equal(result, df.getOWLThing());
    }

    @Test
    public void shouldTestgetObjectPropertyRanges_top_true2() {
        NodeSet<OWLClass> result = jfact2.getObjectPropertyRanges(topObject,
                true);
        equal(result, df.getOWLThing());
    }

    @Test
    public void shouldTestgetgetSubClasses_AbstractEntity_false() {
        NodeSet<OWLClass> result = jfact1.getSubClasses(abstractEntity, false);
        equal(result, whole, pair, df.getOWLNothing(), part);
        result = jfact2.getSubClasses(serviceModel, false);
        equal(result, simpleProcess, atomicProcess, compositeProcess,
                df.getOWLNothing(), process);
    }

    @Test
    public void shouldTestgetgetSubClasses_AbstractEntity_true() {
        NodeSet<OWLClass> result = jfact1.getSubClasses(abstractEntity, true);
        equal(result, whole, pair, part);
        result = jfact2.getSubClasses(serviceModel, true);
        equal(result, process);
    }

    @Test
    public void shouldTestgetgetSuperClasses_Part_false() {
        NodeSet<OWLClass> result = jfact1.getSuperClasses(part, false);
        equal(result, df.getOWLThing(), abstractEntity);
    }

    @Test
    public void shouldTestgetgetSuperClasses_Part_false3() {
        NodeSet<OWLClass> result = jfact2.getSuperClasses(process, false);
        equal(result, df.getOWLThing(), serviceModel);
    }

    @Test
    public void shouldTestgetgetSuperClasses_Part_false4() {
        NodeSet<OWLClass> result = jfact2.getSuperClasses(compositeProcess,
                false);
        equal(result, df.getOWLThing(), serviceModel, process);
    }
}
