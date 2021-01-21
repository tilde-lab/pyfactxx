package conformancetests;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static org.junit.Assert.*;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLFacet;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

import testbase.TestBase;

@SuppressWarnings("javadoc")
public class FixedTestCase extends TestBase {

    @Test
    public void testConsistent_owl_real_range_with_DataOneOf() {
        String premise = "Prefix(:=<http://example.org/>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
                + "Ontology(\n"
                + "  Declaration(NamedIndividual(:a))\n"
                + "  Declaration(DataProperty(:dp))\n"
                + "  Declaration(Class(:A))\n"
                + "  SubClassOf(:A DataAllValuesFrom(:dp owl:real)) \n"
                + "  SubClassOf(:A DataSomeValuesFrom(:dp DataOneOf(\"-INF\"^^xsd:float \"-0\"^^xsd:integer))\n)\n  ClassAssertion(:A :a)\n)";
        String conclusion = "";
        String id = "Consistent_owl_real_range_with_DataOneOf";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "The individual a must have either negative Infinity or 0 (-0 as integer is 0) as dp fillers and all dp successors must be from owl:real, which excludes negative infinity, but allows 0.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_oneOf_004() {
        String premise = "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\nPrefix(xml:=<http://www.w3.org/XML/1998/namespace>)\nPrefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\nPrefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n"
                + "Ontology(\nDeclaration(DataProperty(<urn:t:p#p>))\n"
                + "DataPropertyRange(<urn:t:p#p> DataOneOf(\"1\"^^xsd:integer \"2\"^^xsd:integer \"3\"^^xsd:integer \"4\"^^xsd:integer))\n"
                + "DataPropertyRange(<urn:t:p#p> DataOneOf(\"4\"^^xsd:integer \"5\"^^xsd:integer \"6\"^^xsd:integer))\n"
                + "ClassAssertion(owl:Thing <urn:t:p#i>)\n"
                + "ClassAssertion(DataMinCardinality(1 <urn:t:p#p>) <urn:t:p#i>)\n"
                // +"DataPropertyAssertion(<urn:t:p#p> <urn:t:p#i> \"4\"^^xsd:integer)"
                + ')';
        String conclusion = "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\nPrefix(xml:=<http://www.w3.org/XML/1998/namespace>)\nPrefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\nPrefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n"
                + "Ontology(\nDeclaration(DataProperty(<urn:t:p#p>))\n"
                + "ClassAssertion(owl:Thing <urn:t:p#i>)\n"
                + "DataPropertyAssertion(<urn:t:p#p> <urn:t:p#i> \"4\"^^xsd:integer))";
        String id = "WebOnt_oneOf_004";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "This test illustrates the use of dataRange in OWL DL. This test combines some of the ugliest features of XML, RDF and OWL.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_oneOf_004_1() throws OWLOntologyCreationException {
        String premise = "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\nPrefix(xml:=<http://www.w3.org/XML/1998/namespace>)\nPrefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\nPrefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n"
                + "Ontology(\nDeclaration(DataProperty(<urn:t:p#p>))\n"
                + "DataPropertyRange(<urn:t:p#p> DataOneOf(\"1\"^^xsd:integer \"2\"^^xsd:integer \"3\"^^xsd:integer \"4\"^^xsd:integer))\n"
                + "DataPropertyRange(<urn:t:p#p> DataOneOf(\"4\"^^xsd:integer \"5\"^^xsd:integer \"6\"^^xsd:integer))\n"
                + "ClassAssertion(owl:Thing <urn:t:p#i>)\n"
                + "ClassAssertion(DataMinCardinality(1 <urn:t:p#p>) <urn:t:p#i>)\n"
                // +
                // "DataPropertyAssertion(<urn:t:p#p> <urn:t:p#i> \"4\"^^xsd:integer)"
                + ')';
        OWLOntology o = OWLManager.createOWLOntologyManager()
                .loadOntologyFromOntologyDocument(
                        new StringDocumentSource(premise));
        OWLReasoner r = factory().createReasoner(o);
        r.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        assertTrue(r.isConsistent());
        OWLDataFactory df = o.getOWLOntologyManager().getOWLDataFactory();
        OWLDataProperty p = df.getOWLDataProperty(IRI.create("urn:t:p#p"));
        OWLNamedIndividual i = df
                .getOWLNamedIndividual(IRI.create("urn:t:p#i"));
        OWLLiteral l = df.getOWLLiteral("4",
                df.getOWLDatatype(OWL2Datatype.XSD_INTEGER.getIRI()));
        assertTrue(r.isEntailed(df.getOWLDataPropertyAssertionAxiom(p, i, l)));
    }

    @Test
    public void testBugFix() throws OWLOntologyCreationException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = m.createOntology();
        OWLDataProperty p = DataProperty(IRI.create("urn:t:t#p"));
        OWLNamedIndividual i = NamedIndividual(IRI.create("urn:t:t#i"));
        m.addAxiom(o, Declaration(p));
        m.addAxiom(o, Declaration(i));
        OWLDataOneOf owlDataOneOf = DataOneOf(Literal(1), Literal(2),
                Literal(3), Literal(4));
        OWLDataOneOf owlDataOneOf2 = DataOneOf(Literal(4), Literal(5),
                Literal(6));
        m.addAxiom(o, DataPropertyRange(p, owlDataOneOf));
        m.addAxiom(o, DataPropertyRange(p, owlDataOneOf2));
        m.addAxiom(o,
                ClassAssertion(DataMinCardinality(1, p, TopDatatype()), i));
        OWLReasoner r = factory().createReasoner(o);
        OWLDataPropertyAssertionAxiom ass = DataPropertyAssertion(p, i,
                Literal(4));
        assertTrue(r.isConsistent());
        boolean entailed = r.isEntailed(ass);
        assertTrue(entailed);
    }

    @Test
    @Changed(reason = "original test had unreliable iris, e.g., http://example.com/2a")
    public
            void testConsistent_but_all_unsat() throws Exception {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        IRI ontoIRI = IRI("urn:SpecialGetOntology:Consistentbutallunsat");
        OWLOntology o = m.createOntology(ontoIRI);
        String ns = "http://example.com/";
        OWLNamedIndividual i1 = NamedIndividual(IRI(ns + "i1"));
        OWLNamedIndividual i2 = NamedIndividual(IRI(ns + "i2"));
        OWLNamedIndividual i3 = NamedIndividual(IRI(ns + "i3"));
        OWLClass a = Class(IRI(ns + 'a'));
        OWLClass b = Class(IRI(ns + 'b'));
        OWLClass c = Class(IRI(ns + 'c'));
        OWLClass d = Class(IRI(ns + 'd'));
        OWLClass e = Class(IRI(ns + 'e'));
        OWLObjectProperty q = ObjectProperty(IRI(ns + 'q'));
        OWLObjectProperty p = ObjectProperty(IRI(ns + 'p'));
        OWLObjectProperty s = ObjectProperty(IRI(ns + 's'));
        OWLObjectProperty t = ObjectProperty(IRI(ns + 't'));
        OWLObjectProperty v = ObjectProperty(IRI(ns + 'v'));
        OWLObjectProperty w = ObjectProperty(IRI(ns + 'w'));
        OWLObjectProperty z = ObjectProperty(IRI(ns + 'z'));
        OWLObjectProperty r = ObjectProperty(IRI(ns + 'r'));
        OWLObjectUnionOf domain = ObjectUnionOf(ObjectOneOf(i1),
                ObjectOneOf(i2), ObjectOneOf(i3));
        m.addAxiom(o, Declaration(e));
        m.addAxiom(o, SubClassOf(e, ObjectSomeValuesFrom(p, a)));
        m.addAxiom(o, SubClassOf(e, ObjectSomeValuesFrom(q, d)));
        m.addAxiom(o, DisjointClasses(e, a));
        m.addAxiom(o, DisjointClasses(e, b));
        m.addAxiom(o, DisjointClasses(e, d));
        m.addAxiom(o, DisjointClasses(e, c));
        m.addAxiom(o, Declaration(a));
        m.addAxiom(o, SubClassOf(a, domain));
        m.addAxiom(o, SubClassOf(a, ObjectSomeValuesFrom(s, e)));
        m.addAxiom(o, SubClassOf(a, ObjectSomeValuesFrom(t, b)));
        m.addAxiom(o, DisjointClasses(a, e));
        m.addAxiom(o, DisjointClasses(a, b));
        m.addAxiom(o, DisjointClasses(a, c));
        m.addAxiom(o, Declaration(b));
        m.addAxiom(o, SubClassOf(b, ObjectSomeValuesFrom(v, a)));
        m.addAxiom(o, SubClassOf(b, ObjectSomeValuesFrom(w, c)));
        m.addAxiom(o, DisjointClasses(b, e));
        m.addAxiom(o, DisjointClasses(b, a));
        m.addAxiom(o, DisjointClasses(b, c));
        m.addAxiom(o, Declaration(d));
        m.addAxiom(o, EquivalentClasses(d, ObjectUnionOf(c, b)));
        m.addAxiom(o, SubClassOf(d, ObjectSomeValuesFrom(z, e)));
        m.addAxiom(o, DisjointClasses(d, e));
        m.addAxiom(o, Declaration(c));
        m.addAxiom(o, SubClassOf(c, ObjectSomeValuesFrom(r, b)));
        m.addAxiom(o, DisjointClasses(c, e));
        m.addAxiom(o, DisjointClasses(c, a));
        m.addAxiom(o, DisjointClasses(c, b));
        m.addAxiom(o, Declaration(p));
        m.addAxiom(o, InverseObjectProperties(s, p));
        m.addAxiom(o, FunctionalObjectProperty(p));
        m.addAxiom(o, InverseFunctionalObjectProperty(p));
        m.addAxiom(o, Declaration(q));
        m.addAxiom(o, InverseObjectProperties(z, q));
        m.addAxiom(o, FunctionalObjectProperty(q));
        m.addAxiom(o, InverseFunctionalObjectProperty(q));
        m.addAxiom(o, Declaration(s));
        m.addAxiom(o, InverseObjectProperties(s, p));
        m.addAxiom(o, FunctionalObjectProperty(s));
        m.addAxiom(o, InverseFunctionalObjectProperty(s));
        m.addAxiom(o, Declaration(t));
        m.addAxiom(o, InverseObjectProperties(v, t));
        m.addAxiom(o, FunctionalObjectProperty(t));
        m.addAxiom(o, InverseFunctionalObjectProperty(t));
        m.addAxiom(o, Declaration(v));
        m.addAxiom(o, InverseObjectProperties(v, t));
        m.addAxiom(o, FunctionalObjectProperty(v));
        m.addAxiom(o, InverseFunctionalObjectProperty(v));
        m.addAxiom(o, Declaration(w));
        m.addAxiom(o, InverseObjectProperties(r, w));
        m.addAxiom(o, FunctionalObjectProperty(w));
        m.addAxiom(o, InverseFunctionalObjectProperty(w));
        m.addAxiom(o, Declaration(z));
        m.addAxiom(o, InverseObjectProperties(z, q));
        m.addAxiom(o, FunctionalObjectProperty(z));
        m.addAxiom(o, InverseFunctionalObjectProperty(z));
        m.addAxiom(o, Declaration(r));
        m.addAxiom(o, InverseObjectProperties(r, w));
        m.addAxiom(o, FunctionalObjectProperty(r));
        m.addAxiom(o, InverseFunctionalObjectProperty(r));
        OWLReasoner reasoner = factory().createReasoner(o);
        reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        assertTrue(reasoner.isEntailed(SubClassOf(a, OWLNothing())));
        assertTrue(reasoner.isEntailed(SubClassOf(b, OWLNothing())));
        assertTrue(reasoner.isEntailed(SubClassOf(c, OWLNothing())));
        assertTrue(reasoner.isEntailed(SubClassOf(e, OWLNothing())));
        assertTrue(reasoner.isEntailed(SubClassOf(d, OWLNothing())));
    }

    @Test
    public void testPlus_and_Minus_Zero_Integer() {
        String premise = "Prefix(:=<http://example.org/>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Ontology(\n"
                + "  Declaration(NamedIndividual(:a))\n"
                + "  Declaration(DataProperty(:dp))\nDeclaration(Class(:A))\n"
                + "  SubClassOf(:A DataAllValuesFrom(:dp DataOneOf(\"0\"^^xsd:integer))\n  ) \n"
                + "  ClassAssertion(:A :a)\n  ClassAssertion( DataSomeValuesFrom(:dp DataOneOf(\"-0\"^^xsd:integer)) :a\n  )\n)";
        String conclusion = "";
        String id = "Plus_and_Minus_Zero_Integer";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "For integers 0 and -0 are the same value, so the ontology is consistent.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        // r.getConfiguration().setLoggingActive(true);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testPlus_and_Minus_Zero_Integer_FORTEST() {
        String premise = "Prefix(:=<http://example.org/>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Ontology(\n"
                + "  Declaration(NamedIndividual(:a))\n"
                + "  Declaration(DataProperty(:dp))\nDeclaration(Class(:A))\n"
                + "  SubClassOf(:A DataAllValuesFrom(:dp DataOneOf(\"0\"^^xsd:integer))\n  ) \n"
                + "  ClassAssertion(:A :a)\n  ClassAssertion( DataSomeValuesFrom(:dp DataOneOf(\"0\"^^xsd:integer)) :a\n  )\n)";
        String conclusion = "";
        String id = "Plus_and_Minus_Zero_Integer";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "For integers 0 and -0 are the same value, so the ontology is consistent.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        // r.getConfiguration().setLoggingActive(true);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testEqual() throws OWLOntologyCreationException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = m.createOntology();
        OWLNamedIndividual x = NamedIndividual(IRI("urn:test:x"));
        OWLNamedIndividual y = NamedIndividual(IRI("urn:test:y"));
        OWLDataProperty p = DataProperty(IRI("urn:test:p"));
        OWLLiteral date = Literal("2008-07-08T20:44:11.656+01:00",
                OWL2Datatype.XSD_DATE_TIME);
        m.addAxiom(o, DataPropertyAssertion(p, x, date));
        m.addAxiom(o, DataPropertyAssertion(p, y, date));
        m.addAxiom(o, FunctionalDataProperty(p));
        m.addAxiom(o, SameIndividual(x, y));
        OWLReasoner r = factory().createReasoner(o);
        assertTrue(
                "Ontology was supposed to be consistent!\n"
                        + o.getLogicalAxioms(), r.isConsistent());
    }

    @Test
    public void testReasoner6() throws OWLOntologyCreationException {
        OWLOntologyManager mngr = OWLManager.createOWLOntologyManager();
        OWLOntology ont = mngr.createOntology();
        OWLReasonerFactory fac = factory();
        OWLReasoner r = fac.createReasoner(ont);
        assertEquals(1, r.getBottomDataPropertyNode().getEntities().size());
    }

    @Test
    public void testContradicting_datatype_Restrictions() {
        String premise = "Prefix(:=<http://example.org/>)\nPrefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Ontology(\n"
                + "  Declaration(NamedIndividual(:a))\n"
                + "  Declaration(DataProperty(:dp))\n"
                + "  Declaration(Class(:A))\n"
                + "  SubClassOf(:A DataAllValuesFrom(:dp DataOneOf(\"3\"^^xsd:integer \"4\"^^xsd:integer))) \n"
                + "  SubClassOf(:A DataAllValuesFrom(:dp DataOneOf(\"2\"^^xsd:integer \"3\"^^xsd:integer)))\n"
                + "  SubClassOf(:A DataSomeValuesFrom(:dp DatatypeRestriction(xsd:integer xsd:minInclusive \"4\"^^xsd:integer)))\n"
                + "  ClassAssertion(:A :a))";
        String conclusion = "";
        String id = "Contradicting_datatype_Restrictions";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "The individual a is in A and thus must have a dp filler that is an integer >= 4. Furthermore the dp fillers must be in the set {3, 4} and in the set {2, 3}. Although 3 is in both sets, 3 is not >= 4, which causes the inconsistency.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Ignore("FaCT++ datatype problems")
    @Changed(reason = "old test appears to use the wrong value")
    public void testDatatype_Float_Discrete_001()
            throws OWLOntologyCreationException {
        OWLDataProperty dp = DataProperty(IRI("http://example.org/ontology/dp"));
        OWLDatatype f = Datatype(OWL2Datatype.XSD_FLOAT.getIRI());
        OWLAxiom ax1 = Declaration(dp);
        OWLLiteral f0 = Literal(0F);
        OWLLiteral f1 = Literal(Float.MIN_NORMAL);
        OWLFacetRestriction min = FacetRestriction(OWLFacet.MIN_EXCLUSIVE, f0);
        OWLFacetRestriction max = FacetRestriction(OWLFacet.MAX_EXCLUSIVE, f1);
        OWLDataSomeValuesFrom superClass = DataSomeValuesFrom(dp,
                DatatypeRestriction(f, min, max));
        OWLClass c = Class(IRI("http://example.org/ontology/c"));
        OWLAxiom ax3 = SubClassOf(c, superClass);
        OWLAxiom ax2 = ClassAssertion(c,
                NamedIndividual(IRI("http://example.org/ontology/a")));
        OWLOntology o = OWLManager.createOWLOntologyManager().createOntology(
                new HashSet<>(Arrays.asList(ax1, ax2, ax3)));
        OWLReasoner r = factory().createReasoner(o);
        assertFalse(r.isConsistent());
    }

    @Test
    public void testdatatype_restriction_min_max_inconsistency() {
        String premise = "Prefix(:=<http://example.org/>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Ontology(\n"
                + "  Declaration(NamedIndividual(:a))\n"
                + "  Declaration(DataProperty(:dp))\n"
                + "  Declaration(Class(:A))\n"
                + "  SubClassOf(:A DataSomeValuesFrom(:dp DatatypeRestriction(xsd:integer xsd:minInclusive \"18\"^^xsd:integer))) \n"
                + "  SubClassOf(:A DataAllValuesFrom(:dp DatatypeRestriction(xsd:integer xsd:maxInclusive \"10\"^^xsd:integer))\n)\n"
                + "  ClassAssertion(:A :a))";
        String conclusion = "";
        String id = "datatype_restriction_min_max_inconsistency";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "The individual a is supposed to have an integer dp-successor >= 18, but all dp-successors must be <= 10, which is impossible.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        // r.getConfiguration().setLoggingActive(true);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testfunctionality_clash() {
        String premise = "Prefix(:=<http://example.org/>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Ontology(\n"
                + "  Declaration(NamedIndividual(:a))\n"
                + "  Declaration(DataProperty(:hasAge))\n"
                + "  FunctionalDataProperty(:hasAge) \n"
                + "  ClassAssertion(DataHasValue(:hasAge \"18\"^^xsd:integer) :a) \n"
                + "  ClassAssertion(DataHasValue(:hasAge \"19\"^^xsd:integer) :a))";
        String conclusion = "";
        String id = "functionality_clash";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "The property hasAge is functional, but the individual a has two distinct hasAge fillers.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testinconsistent_datatypes() {
        String premise = "Prefix(:=<http://example.org/>)\nPrefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\nOntology(\n"
                + "  Declaration(NamedIndividual(:a))\nDeclaration(DataProperty(:dp))\n"
                + "  Declaration(Class(:A))\n"
                + "  SubClassOf(:A DataAllValuesFrom(:dp xsd:string)) \n"
                + "  SubClassOf(:A DataSomeValuesFrom(:dp xsd:integer)) \n"
                + "  ClassAssertion(:A :a))";
        String conclusion = "";
        String id = "inconsistent_datatypes";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "The individual a is in the extension of the class A and is thus required to have a dp-successor that is an integer and at the same time all dp-successors are required to be strings, which causes the inconsistency.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_bool_intersection_inst_comp() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#y\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <ex:x rdf:about=\"http://www.example.org#z\"><rdf:type rdf:resource=\"http://www.example.org#y\"/></ex:x>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#c\">\n"
                + "<owl:equivalentClass><owl:Class>"
                + "    <owl:intersectionOf rdf:parseType=\"Collection\"><rdf:Description rdf:about=\"http://www.example.org#x\"/><rdf:Description rdf:about=\"http://www.example.org#y\"/></owl:intersectionOf>\n"
                + "</owl:Class></owl:equivalentClass>"
                + "  </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <ex:c rdf:about=\"http://www.example.org#z\"/>\n"
                + "</rdf:RDF>";
        String id = "rdfbased_sem_bool_intersection_inst_comp";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "An individual, which is an instance of every component class of an intersection, is an instance of the intersection class expression.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_bool_intersection_term() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#y\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#c\">\n"
                + "<rdfs:subClassOf><owl:Class>"
                + "    <owl:intersectionOf rdf:parseType=\"Collection\"><rdf:Description rdf:about=\"http://www.example.org#x\"/><rdf:Description rdf:about=\"http://www.example.org#y\"/></owl:intersectionOf>\n"
                + "</owl:Class></rdfs:subClassOf>"
                + "  </rdf:Description>\n"
                + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#y\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#c\"><rdfs:subClassOf rdf:resource=\"http://www.example.org#x\"/><rdfs:subClassOf rdf:resource=\"http://www.example.org#y\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_bool_intersection_term";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "If a class is an intersection of other classes, then the original class is a subclass of each of the other classes.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_bool_union_inst_comp() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#y\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "  <ex:x rdf:about=\"http://www.example.org#z\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#c\">\n"
                + "<owl:equivalentClass><owl:Class>"
                + "    <owl:unionOf rdf:parseType=\"Collection\"><rdf:Description rdf:about=\"http://www.example.org#x\"/><rdf:Description rdf:about=\"http://www.example.org#y\"/></owl:unionOf>\n"
                + "</owl:Class></owl:equivalentClass>"
                + "  </rdf:Description>\n</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <ex:c rdf:about=\"http://www.example.org#z\"/>\n"
                + "</rdf:RDF>";
        String id = "rdfbased_sem_bool_union_inst_comp";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "An individual, which is an instance of one of the component classes of a union, is an instance of the union class expression.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_bool_union_term() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#y\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#c\">\n"
                + "<owl:equivalentClass><owl:Class>"
                + "    <owl:unionOf rdf:parseType=\"Collection\"><rdf:Description rdf:about=\"http://www.example.org#x\"/><rdf:Description rdf:about=\"http://www.example.org#y\"/></owl:unionOf>\n"
                + "</owl:Class></owl:equivalentClass>"
                + "  </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#y\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x\"><rdfs:subClassOf rdf:resource=\"http://www.example.org#c\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#y\"><rdfs:subClassOf rdf:resource=\"http://www.example.org#c\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_bool_union_term";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "If a class is a union of other classes, then each of the other classes are subclasses of the original class.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_char_functional_inst() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "  <owl:FunctionalProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x\"><ex:p rdf:resource=\"http://www.example.org#y1\"/><ex:p rdf:resource=\"http://www.example.org#y2\"/></rdf:Description></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#y1\"><owl:sameAs rdf:resource=\"http://www.example.org#y2\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_char_functional_inst";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "For two triples with the same functional property as their predicates and with the same subject, the objects are the same.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_enum_inst_included() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#e\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#e\"><owl:oneOf rdf:parseType=\"Collection\"><rdf:Description rdf:about=\"http://www.example.org#x\"/><rdf:Description rdf:about=\"http://www.example.org#y\"/></owl:oneOf></rdf:Description></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#e\"/>\n"
                + "  <ex:e rdf:about=\"http://www.example.org#x\"/>\n"
                + "  <ex:e rdf:about=\"http://www.example.org#y\"/>\n"
                + "</rdf:RDF>";
        String id = "rdfbased_sem_enum_inst_included";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "If a class defines an enumeration class expression from two individuals, than both individuals are instances of the class.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_eqdis_different_irrflxv() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:Thing rdf:about=\"http://www.example.org#x\"/>\n"
                + "<owl:Thing rdf:about=\"http://www.example.org#z\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#z\"><owl:sameAs rdf:resource=\"http://www.example.org#x\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x\"><owl:differentFrom rdf:resource=\"http://www.example.org#z\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "rdfbased_sem_eqdis_different_irrflxv";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "Diversity of two individuals is irreflexive.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_eqdis_disclass_irrflxv() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#fake\"/>\n"
                + "  <owl:Thing rdf:about=\"http://www.example.org#x\"/>\n"
                + "  <ex:c rdf:about=\"http://www.example.org#x\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#c\"><owl:disjointWith rdf:resource=\"http://www.example.org#fake\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#fake\"><owl:equivalentClass rdf:resource=\"http://www.example.org#c\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "rdfbased_sem_eqdis_disclass_irrflxv";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "Disjointness of two non-empty classes is irreflexive.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_eqdis_disprop_eqprop() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#s2\"><ex:p2 rdf:resource=\"http://www.example.org#o2\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#s1\"><ex:p1 rdf:resource=\"http://www.example.org#o1\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p1\"><owl:equivalentProperty rdf:resource=\"http://www.example.org#p2\"/><owl:propertyDisjointWith rdf:resource=\"http://www.example.org#p2\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "rdfbased_sem_eqdis_disprop_eqprop";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "Two non-empty properties cannot both be equivalent and disjoint.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_eqdis_disprop_inst() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#s\"><ex:p1 rdf:resource=\"http://www.example.org#o\"/><ex:p2 rdf:resource=\"http://www.example.org#o\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p1\"><owl:propertyDisjointWith rdf:resource=\"http://www.example.org#p2\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "rdfbased_sem_eqdis_disprop_inst";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "Triples with disjoint properties as their predicates have different subjects or different objects.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_eqdis_disprop_irrflxv() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#q\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#s\"><ex:p rdf:resource=\"http://www.example.org#o\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#q\"><owl:equivalentProperty rdf:resource=\"http://www.example.org#p\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p\"><owl:propertyDisjointWith rdf:resource=\"http://www.example.org#q\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "rdfbased_sem_eqdis_disprop_irrflxv";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "Disjointness of two non-empty properties is irreflexive.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_eqdis_eqclass_subclass_1() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c1\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c2\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#c1\"><owl:equivalentClass rdf:resource=\"http://www.example.org#c2\"/></rdf:Description></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c1\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c2\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#c2\"><rdfs:subClassOf><rdf:Description rdf:about=\"http://www.example.org#c1\"><rdfs:subClassOf rdf:resource=\"http://www.example.org#c2\"/></rdf:Description></rdfs:subClassOf></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_eqdis_eqclass_subclass_1";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Two equivalent classes are sub classes of each other.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_eqdis_sameas_subst() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#s2\"><owl:sameAs><rdf:Description rdf:about=\"http://www.example.org#s1\"><ex:p1 rdf:resource=\"http://www.example.org#o1\"/></rdf:Description></owl:sameAs></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#o2\"><owl:sameAs rdf:resource=\"http://www.example.org#o1\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p2\"><owl:sameAs rdf:resource=\"http://www.example.org#p1\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p2\"><owl:equivalentProperty rdf:resource=\"http://www.example.org#p1\"/></rdf:Description>\n"
                + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#s2\"><ex:p1 rdf:resource=\"http://www.example.org#o1\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#s1\"><ex:p2 rdf:resource=\"http://www.example.org#o1\"/><ex:p1 rdf:resource=\"http://www.example.org#o2\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_eqdis_sameas_subst";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Equality of two individuals allows for substituting the subject, predicate and object of an RDF triple by an equal individual.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_eqdis_sameas_sym() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:Thing rdf:about=\"http://www.example.org#x\"/>\n"
                + "<owl:Thing rdf:about=\"http://www.example.org#y\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x\"><owl:sameAs rdf:resource=\"http://www.example.org#y\"/></rdf:Description></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:Thing rdf:about=\"http://www.example.org#x\"/>\n"
                + "<owl:Thing rdf:about=\"http://www.example.org#y\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#y\"><owl:sameAs rdf:resource=\"http://www.example.org#x\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_eqdis_sameas_sym";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Equality of two individuals is symmetrical.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_eqdis_sameas_trans() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x\"><owl:sameAs><rdf:Description rdf:about=\"http://www.example.org#y\"><owl:sameAs rdf:resource=\"http://www.example.org#z\"/></rdf:Description></owl:sameAs></rdf:Description></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x\"><owl:sameAs rdf:resource=\"http://www.example.org#z\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_eqdis_sameas_trans";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Equality of two individuals is transitive.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed(reason = "without declarations, some properties default to datatype properties and some to annotation properties")
    public
            void testrdfbased_sem_ndis_alldisjointproperties_fw() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p3\"/>\n"
                + "  <owl:AllDisjointProperties rdf:about=\"http://www.example.org#z\"><owl:members rdf:parseType=\"Collection\"><rdf:Description rdf:about=\"http://www.example.org#p1\"/><rdf:Description rdf:about=\"http://www.example.org#p2\"/><rdf:Description rdf:about=\"http://www.example.org#p3\"/></owl:members></owl:AllDisjointProperties>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#s\"><ex:p1 rdf:resource=\"http://www.example.org#o\"/><ex:p2 rdf:resource=\"http://www.example.org#o\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "rdfbased_sem_ndis_alldisjointproperties_fw";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "All the members of an owl:AllDisjointProperties construct are mutually disjoint properties.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_npa_dat_fw() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#z\"/>\n"
                + "  <owl:Thing rdf:about=\"http://www.example.org#s\"/>\n"
                + "  <owl:DatatypeProperty rdf:about=\"http://www.example.org#p\"/><rdf:Description rdf:about=\"http://www.example.org#s\"><ex:p>data</ex:p></rdf:Description>\n"
                + "  <owl:NegativePropertyAssertion><owl:sourceIndividual rdf:resource=\"http://www.example.org#s\"/><owl:assertionProperty rdf:resource=\"http://www.example.org#p\"/><owl:targetValue>data</owl:targetValue></owl:NegativePropertyAssertion></rdf:RDF>";
        String conclusion = "";
        String id = "rdfbased_sem_npa_dat_fw";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "A negative data property assertion DNPA(s p \"data\") must not occur together with the corresponding positive data property assertion s p \"data\".";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_rdfs_domain_cond() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#u\"><ex:p rdf:resource=\"http://www.example.org#v\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p\"><rdfs:domain rdf:resource=\"http://www.example.org#c\"/></rdf:Description></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <ex:c rdf:about=\"http://www.example.org#u\"/>\n"
                + "</rdf:RDF>";
        String id = "rdfbased_sem_rdfs_domain_cond";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "The left hand side individual in a given triple is entailed to be an instance of the domain of the predicate.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_rdfs_range_cond() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#u\"><ex:p rdf:resource=\"http://www.example.org#v\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p\"><rdfs:range rdf:resource=\"http://www.example.org#c\"/></rdf:Description></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <ex:c rdf:about=\"http://www.example.org#v\"/>\n"
                + "</rdf:RDF>";
        String id = "rdfbased_sem_rdfs_range_cond";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "The right hand side individual in a given triple is entailed to be an instance of the range of the predicate.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_restrict_allvalues_cmp_class() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c1\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c2\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x2\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x1\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x1\">\n"
                + "<owl:equivalentClass><owl:Restriction>"
                + "    <owl:allValuesFrom><rdf:Description rdf:about=\"http://www.example.org#c1\"><rdfs:subClassOf rdf:resource=\"http://www.example.org#c2\"/></rdf:Description></owl:allValuesFrom><owl:onProperty rdf:resource=\"http://www.example.org#p\"/>\n"
                + "</owl:Restriction></owl:equivalentClass>"
                + "  </rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x2\">\n"
                + "<owl:equivalentClass><owl:Restriction>"
                + "    <owl:allValuesFrom rdf:resource=\"http://www.example.org#c2\"/><owl:onProperty rdf:resource=\"http://www.example.org#p\"/>\n"
                + "</owl:Restriction></owl:equivalentClass>"
                + "  </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x1\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x2\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x1\"><rdfs:subClassOf rdf:resource=\"http://www.example.org#x2\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_restrict_allvalues_cmp_class";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "A universal restriction on some property and some class is a sub class of another universal restriction on the same property but on a super class.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_restrict_allvalues_cmp_prop() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x1\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x2\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x1\">\n"
                + "<owl:equivalentClass><owl:Restriction>"
                + "    <owl:allValuesFrom rdf:resource=\"http://www.example.org#c\"/><owl:onProperty><rdf:Description rdf:about=\"http://www.example.org#p1\"><rdfs:subPropertyOf rdf:resource=\"http://www.example.org#p2\"/></rdf:Description></owl:onProperty>\n"
                + "</owl:Restriction></owl:equivalentClass>"
                + "  </rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x2\">\n"
                + "<owl:equivalentClass><owl:Restriction>"
                + "    <owl:allValuesFrom rdf:resource=\"http://www.example.org#c\"/><owl:onProperty rdf:resource=\"http://www.example.org#p2\"/>\n"
                + "</owl:Restriction></owl:equivalentClass>"
                + "  </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x1\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x2\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x2\"><rdfs:subClassOf rdf:resource=\"http://www.example.org#x1\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_restrict_allvalues_cmp_prop";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "A universal restriction on some property and some class is a sub class of another universal restriction on the same class but on a sub property.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_restrict_allvalues_inst_obj() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:Class rdf:about=\"http://www.example.org#z\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "  <ex:z rdf:about=\"http://www.example.org#w\"><ex:p rdf:resource=\"http://www.example.org#x\"/></ex:z>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#z\">\n"
                + "<rdfs:subClassOf><owl:Restriction>"
                + "    <owl:allValuesFrom rdf:resource=\"http://www.example.org#c\"/><owl:onProperty rdf:resource=\"http://www.example.org#p\"/>\n"
                + "</owl:Restriction></rdfs:subClassOf>"
                + "  </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <ex:c rdf:about=\"http://www.example.org#x\"/>\n"
                + "</rdf:RDF>";
        String id = "rdfbased_sem_restrict_allvalues_inst_obj";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "If an individual w is an instance of the universal restriction on property p and class c, then for any triple w p x follows that x is an instance of c.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_restrict_hasvalue_cmp_prop() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x1\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x2\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x1\">\n"
                + "<owl:equivalentClass><owl:Restriction>"
                + "    <owl:hasValue rdf:resource=\"http://www.example.org#v\"/><owl:onProperty><rdf:Description rdf:about=\"http://www.example.org#p1\"><rdfs:subPropertyOf rdf:resource=\"http://www.example.org#p2\"/></rdf:Description></owl:onProperty>\n"
                + "</owl:Restriction></owl:equivalentClass>"
                + "  </rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x2\">\n"
                + "<owl:equivalentClass><owl:Restriction>"
                + "    <owl:hasValue rdf:resource=\"http://www.example.org#v\"/><owl:onProperty rdf:resource=\"http://www.example.org#p2\"/>\n"
                + "</owl:Restriction></owl:equivalentClass>"
                + "  </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x1\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x2\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x1\"><rdfs:subClassOf rdf:resource=\"http://www.example.org#x2\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_restrict_hasvalue_cmp_prop";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "A has-value restriction on some property and some value is a sub class of another has-value restriction on the same value but on a super property.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_restrict_hasvalue_inst_obj() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:Class rdf:about=\"http://www.example.org#z\"/>\n"
                // TODO this is a bug, should not be needed by the reasoner
                + "<owl:Thing rdf:about=\"http://www.example.org#u\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "  <ex:z rdf:about=\"http://www.example.org#w\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#z\">\n"
                + "<owl:equivalentClass><owl:Restriction>"
                + "    <owl:hasValue rdf:resource=\"http://www.example.org#u\"/><owl:onProperty rdf:resource=\"http://www.example.org#p\"/>\n"
                + "</owl:Restriction></owl:equivalentClass>"
                + "  </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#w\"><ex:p rdf:resource=\"http://www.example.org#u\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_restrict_hasvalue_inst_obj";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "If an individual w is an instance of the has-value restriction on property p to value u, then the triple w p u can be entailed.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_restrict_hasvalue_inst_subj() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#z\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#z\">\n"
                + "<owl:equivalentClass><owl:Restriction>"
                + "    <owl:hasValue rdf:resource=\"http://www.example.org#u\"/><owl:onProperty rdf:resource=\"http://www.example.org#p\"/>\n"
                + "</owl:Restriction></owl:equivalentClass>"
                + "  </rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#w\"><ex:p rdf:resource=\"http://www.example.org#u\"/></rdf:Description></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#z\"/>\n"
                + "  <ex:z rdf:about=\"http://www.example.org#w\"/>\n"
                + "</rdf:RDF>";
        String id = "rdfbased_sem_restrict_hasvalue_inst_subj";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "For a triple w p u, the individual w is an instance of the has-value restriction on p to u.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_restrict_maxcard_inst_obj_one() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#z\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "  <ex:z rdf:about=\"http://www.example.org#w\"><ex:p rdf:resource=\"http://www.example.org#x1\"/><ex:p rdf:resource=\"http://www.example.org#x2\"/></ex:z>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#z\">\n"
                + "  <rdfs:subClassOf>\n"
                + "  <owl:Restriction>\n"
                + "    <owl:maxCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality><owl:onProperty rdf:resource=\"http://www.example.org#p\"/>\n"
                + "  </owl:Restriction>\n"
                + "  </rdfs:subClassOf>\n"
                + "  </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x1\"><owl:sameAs rdf:resource=\"http://www.example.org#x2\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_restrict_maxcard_inst_obj_one";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "If an individual w is an instance of the max-1-cardinality restriction on property p, and if there are triples w p x1 and w p x2, then x1 equals x2.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_restrict_maxcard_inst_obj_zero() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:Class rdf:about=\"http://www.example.org#z\"/>"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>"
                + "  <ex:z rdf:about=\"http://www.example.org#w\"><ex:p rdf:resource=\"http://www.example.org#x\"/></ex:z>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#z\">\n"
                + "<rdfs:subClassOf><rdf:Description>"
                + "<rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#Restriction\"/>"
                + "    <owl:maxCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">0</owl:maxCardinality><owl:onProperty rdf:resource=\"http://www.example.org#p\"/></rdf:Description>\n</rdfs:subClassOf></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "rdfbased_sem_restrict_maxcard_inst_obj_zero";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "If an individual w is an instance of the max-0-cardinality restriction on property p, then there cannot be any triple w p x.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_restrict_maxqcr_inst_obj_one() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#z\"/>\n"
                + "  <owl:Thing rdf:about=\"http://www.example.org#x1\"/>\n"
                + "  <owl:Thing rdf:about=\"http://www.example.org#x2\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "  <ex:z rdf:about=\"http://www.example.org#w\"><ex:p><ex:c rdf:about=\"http://www.example.org#x1\"/></ex:p><ex:p><ex:c rdf:about=\"http://www.example.org#x2\"/></ex:p></ex:z>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#z\">\n"
                + "<owl:equivalentClass><owl:Restriction>"
                + "    <owl:maxQualifiedCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:maxQualifiedCardinality><owl:onProperty rdf:resource=\"http://www.example.org#p\"/><owl:onClass rdf:resource=\"http://www.example.org#c\"/>\n"
                + "</owl:Restriction></owl:equivalentClass>"
                + "  </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x1\"><owl:sameAs rdf:resource=\"http://www.example.org#x2\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_restrict_maxqcr_inst_obj_one";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "If an individual w is an instance of the max-1-QCR on property p to class c, and if there are triples w p x1 and w p x2, with x1 and x2 being in c, then x1 equals x2.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_restrict_maxqcr_inst_obj_zero() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#z\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "  <ex:z rdf:about=\"http://www.example.org#w\"><ex:p><ex:c rdf:about=\"http://www.example.org#x\"/></ex:p></ex:z>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#z\">\n"
                + "<owl:equivalentClass><owl:Restriction>"
                + "    <owl:maxQualifiedCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">0</owl:maxQualifiedCardinality><owl:onProperty rdf:resource=\"http://www.example.org#p\"/><owl:onClass rdf:resource=\"http://www.example.org#c\"/>\n"
                + "</owl:Restriction></owl:equivalentClass>"
                + "  </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "rdfbased_sem_restrict_maxqcr_inst_obj_zero";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "If an individual w is an instance of the max-0-QCR on property p to class c, then there cannot be any triple w p x with x in c.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_restrict_somevalues_cmp_class() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c1\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c2\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x1\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x2\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x1\">\n"
                + "<rdfs:subClassOf><owl:Restriction>"
                + "    <owl:someValuesFrom><rdf:Description rdf:about=\"http://www.example.org#c1\"><rdfs:subClassOf rdf:resource=\"http://www.example.org#c2\"/></rdf:Description></owl:someValuesFrom><owl:onProperty rdf:resource=\"http://www.example.org#p\"/>\n"
                + "</owl:Restriction></rdfs:subClassOf>"
                + "  </rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x2\">\n"
                + "<owl:equivalentClass><owl:Restriction>"
                + "    <owl:someValuesFrom rdf:resource=\"http://www.example.org#c2\"/><owl:onProperty rdf:resource=\"http://www.example.org#p\"/>\n"
                + "</owl:Restriction></owl:equivalentClass>"
                + "  </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x1\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x2\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x1\"><rdfs:subClassOf rdf:resource=\"http://www.example.org#x2\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_restrict_somevalues_cmp_class";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "An existential restriction on some property and some class is a sub class of another existential restriction on the same property but on a super class.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_restrict_somevalues_cmp_prop() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x1\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x2\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x1\">\n"
                + "<owl:equivalentClass><owl:Restriction>"
                + "    <owl:someValuesFrom rdf:resource=\"http://www.example.org#c\"/><owl:onProperty><rdf:Description rdf:about=\"http://www.example.org#p1\"><rdfs:subPropertyOf rdf:resource=\"http://www.example.org#p2\"/></rdf:Description></owl:onProperty>\n"
                + "</owl:Restriction></owl:equivalentClass>"
                + "  </rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x2\">\n"
                + "<owl:equivalentClass><owl:Restriction>"
                + "    <owl:someValuesFrom rdf:resource=\"http://www.example.org#c\"/><owl:onProperty rdf:resource=\"http://www.example.org#p2\"/>\n"
                + "</owl:Restriction></owl:equivalentClass>"
                + "  </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x1\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x2\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x1\"><rdfs:subClassOf rdf:resource=\"http://www.example.org#x2\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_restrict_somevalues_cmp_prop";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "An existential restriction on some property and some class is a sub class of another existential restriction on the same class but on a super property.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_restrict_somevalues_inst_subj() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Thing rdf:about=\"http://www.example.org#x\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#z\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#z\">\n"
                + "<owl:equivalentClass><owl:Restriction>"
                + "    <owl:someValuesFrom rdf:resource=\"http://www.example.org#c\"/><owl:onProperty rdf:resource=\"http://www.example.org#p\"/>\n"
                + "</owl:Restriction></owl:equivalentClass>\n"
                + "  </rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#w\"><ex:p><ex:c rdf:about=\"http://www.example.org#x\"/></ex:p></rdf:Description></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Thing rdf:about=\"http://www.example.org#w\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#z\"/>\n"
                + "  <ex:z rdf:about=\"http://www.example.org#w\"/>\n"
                + "</rdf:RDF>";
        String id = "rdfbased_sem_restrict_somevalues_inst_subj";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "For a triple w p x, with x being an instance of a class c, the individual w is an instance of the existential restriction on p to c.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void teststring_integer_clash() {
        String premise = "Prefix(:=<http://example.org/>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Ontology(\n"
                + "  Declaration(NamedIndividual(:a))\n"
                + "  Declaration(DataProperty(:hasAge))\n"
                + "  DataPropertyRange(:hasAge xsd:integer)\n"
                + "  ClassAssertion(DataHasValue(:hasAge \"aString\"^^xsd:string) :a)\n"
                + ')';
        String conclusion = "";
        String id = "string_integer_clash";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "The range of hasAge is integer, but a has an asserted string hasAge filler.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        // r.getConfiguration().setLoggingActive(true);
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_501() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/consistent501\" >\n"
                + "<owl:Ontology/>\n"
                + "<owl:Class rdf:ID='TorF'> \n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:ID='T'><owl:differentFrom rdf:resource='#F'/></owl:Thing><owl:Thing rdf:ID='F'/></owl:oneOf>\n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:about='#plus1'/><owl:Thing rdf:about='#minus1'/></owl:oneOf>\n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:about='#plus2'/><owl:Thing rdf:about='#minus2'/></owl:oneOf>\n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:about='#plus3'/><owl:Thing rdf:about='#minus3'/></owl:oneOf>\n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:about='#plus4'/><owl:Thing rdf:about='#minus4'/></owl:oneOf>\n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:about='#plus5'/><owl:Thing rdf:about='#minus5'/></owl:oneOf>\n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:about='#plus6'/><owl:Thing rdf:about='#minus6'/></owl:oneOf>\n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:about='#plus7'/><owl:Thing rdf:about='#minus7'/></owl:oneOf>\n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:about='#plus8'/><owl:Thing rdf:about='#minus8'/></owl:oneOf>\n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:about='#plus9'/><owl:Thing rdf:about='#minus9'/></owl:oneOf></owl:Class>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus7'/><rdf:Description rdf:about='#minus9'/><rdf:Description rdf:about='#minus8'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus1'/><rdf:Description rdf:about='#plus2'/><rdf:Description rdf:about='#minus8'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus4'/><rdf:Description rdf:about='#plus7'/><rdf:Description rdf:about='#minus5'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus2'/><rdf:Description rdf:about='#plus3'/><rdf:Description rdf:about='#minus1'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus1'/><rdf:Description rdf:about='#plus5'/><rdf:Description rdf:about='#plus8'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus8'/><rdf:Description rdf:about='#minus6'/><rdf:Description rdf:about='#minus3'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus3'/><rdf:Description rdf:about='#minus8'/><rdf:Description rdf:about='#plus7'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus3'/><rdf:Description rdf:about='#plus6'/><rdf:Description rdf:about='#plus8'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus4'/><rdf:Description rdf:about='#minus6'/><rdf:Description rdf:about='#plus8'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus6'/><rdf:Description rdf:about='#plus7'/><rdf:Description rdf:about='#plus3'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus3'/><rdf:Description rdf:about='#plus6'/><rdf:Description rdf:about='#minus9'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus5'/><rdf:Description rdf:about='#minus2'/><rdf:Description rdf:about='#plus3'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus5'/><rdf:Description rdf:about='#plus8'/><rdf:Description rdf:about='#plus2'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus2'/><rdf:Description rdf:about='#minus7'/><rdf:Description rdf:about='#minus3'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus6'/><rdf:Description rdf:about='#minus8'/><rdf:Description rdf:about='#minus5'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus2'/><rdf:Description rdf:about='#plus7'/><rdf:Description rdf:about='#minus3'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus9'/><rdf:Description rdf:about='#minus1'/><rdf:Description rdf:about='#minus2'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus1'/><rdf:Description rdf:about='#plus7'/><rdf:Description rdf:about='#minus6'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus1'/><rdf:Description rdf:about='#plus9'/><rdf:Description rdf:about='#minus3'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus8'/><rdf:Description rdf:about='#minus9'/><rdf:Description rdf:about='#minus2'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus9'/><rdf:Description rdf:about='#minus8'/><rdf:Description rdf:about='#plus2'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus5'/><rdf:Description rdf:about='#plus8'/><rdf:Description rdf:about='#plus4'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus7'/><rdf:Description rdf:about='#plus2'/><rdf:Description rdf:about='#plus5'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus1'/><rdf:Description rdf:about='#plus7'/><rdf:Description rdf:about='#minus4'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus7'/><rdf:Description rdf:about='#minus8'/><rdf:Description rdf:about='#plus4'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus3'/><rdf:Description rdf:about='#plus2'/><rdf:Description rdf:about='#minus6'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus1'/><rdf:Description rdf:about='#minus2'/><rdf:Description rdf:about='#minus9'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus7'/><rdf:Description rdf:about='#plus3'/><rdf:Description rdf:about='#minus2'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus7'/><rdf:Description rdf:about='#plus8'/><rdf:Description rdf:about='#plus4'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus1'/><rdf:Description rdf:about='#minus7'/><rdf:Description rdf:about='#minus5'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus5'/><rdf:Description rdf:about='#plus4'/><rdf:Description rdf:about='#minus3'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus6'/><rdf:Description rdf:about='#plus7'/><rdf:Description rdf:about='#minus1'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus1'/><rdf:Description rdf:about='#plus7'/><rdf:Description rdf:about='#minus9'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus3'/><rdf:Description rdf:about='#plus2'/><rdf:Description rdf:about='#plus6'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus8'/><rdf:Description rdf:about='#plus3'/><rdf:Description rdf:about='#minus7'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus1'/><rdf:Description rdf:about='#plus9'/><rdf:Description rdf:about='#minus8'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus5'/><rdf:Description rdf:about='#minus9'/><rdf:Description rdf:about='#minus7'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus7'/><rdf:Description rdf:about='#plus3'/><rdf:Description rdf:about='#minus9'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus3'/><rdf:Description rdf:about='#minus1'/><rdf:Description rdf:about='#minus2'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus6'/><rdf:Description rdf:about='#plus1'/><rdf:Description rdf:about='#plus4'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus6'/><rdf:Description rdf:about='#minus7'/><rdf:Description rdf:about='#plus5'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus8'/><rdf:Description rdf:about='#minus6'/><rdf:Description rdf:about='#plus3'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus5'/><rdf:Description rdf:about='#minus2'/><rdf:Description rdf:about='#plus6'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus8'/><rdf:Description rdf:about='#plus3'/><rdf:Description rdf:about='#minus5'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus2'/><rdf:Description rdf:about='#minus4'/><rdf:Description rdf:about='#minus9'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_501";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "This is the classic 3 SAT problem.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_502() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent502#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent502\" >\n"
                + "<owl:Ontology/>\n"
                + "<owl:Class rdf:ID='TorF'> \n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:ID='T'><owl:differentFrom rdf:resource='#F'/></owl:Thing><owl:Thing rdf:ID='F'/></owl:oneOf>\n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:about='#plus1'/><owl:Thing rdf:about='#minus1'/></owl:oneOf>\n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:about='#plus2'/><owl:Thing rdf:about='#minus2'/></owl:oneOf>\n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:about='#plus3'/><owl:Thing rdf:about='#minus3'/></owl:oneOf>\n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:about='#plus4'/><owl:Thing rdf:about='#minus4'/></owl:oneOf>\n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:about='#plus5'/><owl:Thing rdf:about='#minus5'/></owl:oneOf>\n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:about='#plus6'/><owl:Thing rdf:about='#minus6'/></owl:oneOf>\n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:about='#plus7'/><owl:Thing rdf:about='#minus7'/></owl:oneOf>\n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:about='#plus8'/><owl:Thing rdf:about='#minus8'/></owl:oneOf>\n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:about='#plus9'/><owl:Thing rdf:about='#minus9'/></owl:oneOf></owl:Class>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus1'/><rdf:Description rdf:about='#plus2'/><rdf:Description rdf:about='#minus4'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus3'/><rdf:Description rdf:about='#plus6'/><rdf:Description rdf:about='#minus4'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus9'/><rdf:Description rdf:about='#minus4'/><rdf:Description rdf:about='#plus5'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus4'/><rdf:Description rdf:about='#minus6'/><rdf:Description rdf:about='#minus2'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus2'/><rdf:Description rdf:about='#minus3'/><rdf:Description rdf:about='#plus1'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus3'/><rdf:Description rdf:about='#plus8'/><rdf:Description rdf:about='#plus7'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus8'/><rdf:Description rdf:about='#minus2'/><rdf:Description rdf:about='#plus3'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus7'/><rdf:Description rdf:about='#minus6'/><rdf:Description rdf:about='#plus9'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus1'/><rdf:Description rdf:about='#minus4'/><rdf:Description rdf:about='#minus6'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus8'/><rdf:Description rdf:about='#minus5'/><rdf:Description rdf:about='#minus3'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus4'/><rdf:Description rdf:about='#plus3'/><rdf:Description rdf:about='#plus6'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus2'/><rdf:Description rdf:about='#minus1'/><rdf:Description rdf:about='#plus4'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus3'/><rdf:Description rdf:about='#plus8'/><rdf:Description rdf:about='#plus2'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus6'/><rdf:Description rdf:about='#minus2'/><rdf:Description rdf:about='#plus9'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus7'/><rdf:Description rdf:about='#minus9'/><rdf:Description rdf:about='#minus2'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus2'/><rdf:Description rdf:about='#minus5'/><rdf:Description rdf:about='#minus7'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus5'/><rdf:Description rdf:about='#plus2'/><rdf:Description rdf:about='#plus9'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus6'/><rdf:Description rdf:about='#minus2'/><rdf:Description rdf:about='#minus7'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus9'/><rdf:Description rdf:about='#plus3'/><rdf:Description rdf:about='#minus2'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus1'/><rdf:Description rdf:about='#plus7'/><rdf:Description rdf:about='#plus4'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus4'/><rdf:Description rdf:about='#plus1'/><rdf:Description rdf:about='#plus9'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus2'/><rdf:Description rdf:about='#plus1'/><rdf:Description rdf:about='#minus6'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus7'/><rdf:Description rdf:about='#minus4'/><rdf:Description rdf:about='#plus9'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus5'/><rdf:Description rdf:about='#plus3'/><rdf:Description rdf:about='#minus9'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus4'/><rdf:Description rdf:about='#plus9'/><rdf:Description rdf:about='#minus8'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus4'/><rdf:Description rdf:about='#plus3'/><rdf:Description rdf:about='#plus9'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus7'/><rdf:Description rdf:about='#plus9'/><rdf:Description rdf:about='#plus5'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus4'/><rdf:Description rdf:about='#plus1'/><rdf:Description rdf:about='#plus3'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus5'/><rdf:Description rdf:about='#plus8'/><rdf:Description rdf:about='#plus7'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus8'/><rdf:Description rdf:about='#minus7'/><rdf:Description rdf:about='#plus3'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus4'/><rdf:Description rdf:about='#minus8'/><rdf:Description rdf:about='#plus6'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus4'/><rdf:Description rdf:about='#plus6'/><rdf:Description rdf:about='#minus5'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus6'/><rdf:Description rdf:about='#plus1'/><rdf:Description rdf:about='#minus9'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus1'/><rdf:Description rdf:about='#plus9'/><rdf:Description rdf:about='#minus6'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus9'/><rdf:Description rdf:about='#minus8'/><rdf:Description rdf:about='#plus3'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus6'/><rdf:Description rdf:about='#plus3'/><rdf:Description rdf:about='#minus4'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus8'/><rdf:Description rdf:about='#minus4'/><rdf:Description rdf:about='#plus6'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus3'/><rdf:Description rdf:about='#plus5'/><rdf:Description rdf:about='#minus8'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus9'/><rdf:Description rdf:about='#plus4'/><rdf:Description rdf:about='#plus3'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus8'/><rdf:Description rdf:about='#minus4'/><rdf:Description rdf:about='#plus2'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus5'/><rdf:Description rdf:about='#minus2'/><rdf:Description rdf:about='#minus9'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus7'/><rdf:Description rdf:about='#minus3'/><rdf:Description rdf:about='#minus4'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus9'/><rdf:Description rdf:about='#minus4'/><rdf:Description rdf:about='#minus8'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus6'/><rdf:Description rdf:about='#minus4'/><rdf:Description rdf:about='#minus1'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus6'/><rdf:Description rdf:about='#minus7'/><rdf:Description rdf:about='#minus8'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_502";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "This is the classic 3 SAT problem.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testBetweenNumbers() throws OWLOntologyCreationException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = m.createOntology();
        OWLNamedIndividual x = NamedIndividual(IRI("urn:test:x"));
        OWLClass c = Class(IRI("urn:test:c"));
        OWLDataProperty p = DataProperty(IRI("urn:test:p"));
        OWLDatatype type = Datatype(XSDVocabulary.INT.getIRI());
        OWLLiteral date1 = Literal("2007", type);
        // Literal("2008-07-08", type);
        OWLLiteral date2 = Literal("2009", type);
        // Literal("2008-07-10", type);
        OWLLiteral date3 = Literal("2008", type);
        // Literal("2008-07-09", type);
        OWLDataRange range = DatatypeRestriction(type,
                FacetRestriction(OWLFacet.MIN_INCLUSIVE, date1),
                FacetRestriction(OWLFacet.MAX_INCLUSIVE, date2));
        OWLClassExpression psome = DataSomeValuesFrom(p, range);
        m.addAxiom(o, EquivalentClasses(c, psome));
        m.addAxiom(o, DataPropertyAssertion(p, x, date3));
        m.addAxiom(o, FunctionalDataProperty(p));
        OWLReasoner r = factory().createReasoner(o);
        assertTrue(r.isEntailed(ClassAssertion(c, x)));
    }

    @Test
    @Ignore("FaCT++ datatype problems")
    public void testContradicting_dateTime_restrictions_programmatic()
            throws OWLOntologyCreationException {
        Set<OWLAxiom> axioms = new HashSet<>();
        OWLClass A = Class(IRI("http://example.org/A"));
        OWLNamedIndividual a = NamedIndividual(IRI("http://example.org/a"));
        OWLDataProperty dp = DataProperty(IRI("http://example.org/dp"));
        OWLDatatype type = Datatype(OWL2Datatype.XSD_DATE_TIME.getIRI());
        OWLLiteral lit1 = Literal("2007-10-08T20:44:11.656+01:00", type);
        OWLLiteral lit2 = Literal("2008-10-08T20:44:11.656+01:00", type);
        OWLLiteral lit3 = Literal("2008-07-08T20:44:11.656+01:00", type);
        OWLFacetRestriction min = FacetRestriction(OWLFacet.MIN_INCLUSIVE, lit3);
        OWLFacetRestriction max = FacetRestriction(OWLFacet.MAX_INCLUSIVE, lit2);
        axioms.add(Declaration(A));
        axioms.add(SubClassOf(A,
                DataAllValuesFrom(dp, DatatypeRestriction(type, max, min))));
        axioms.add(SubClassOf(A, DataHasValue(dp, lit1)));
        axioms.add(Declaration(dp));
        axioms.add(Declaration(a));
        axioms.add(ClassAssertion(A, a));
        OWLOntology o = OWLManager.createOWLOntologyManager().createOntology(
                axioms);
        assertFalse(factory().createReasoner(o).isConsistent());
    }

    @Test
    public void testContradicting_int_restrictions()
            throws OWLOntologyCreationException {
        Set<OWLAxiom> axioms = new HashSet<>();
        OWLClass A = Class(IRI("http://example.org/A"));
        OWLNamedIndividual a = NamedIndividual(IRI("http://example.org/a"));
        OWLDataProperty dp = DataProperty(IRI("http://example.org/dp"));
        OWLDatatype type = Datatype(OWL2Datatype.XSD_INT.getIRI());
        OWLLiteral lit1 = Literal("2007", type);
        OWLLiteral lit2 = Literal("2009", type);
        OWLLiteral lit3 = Literal("2008", type);
        OWLFacetRestriction min = FacetRestriction(OWLFacet.MIN_INCLUSIVE, lit3);
        OWLFacetRestriction max = FacetRestriction(OWLFacet.MAX_INCLUSIVE, lit2);
        axioms.add(Declaration(A));
        axioms.add(SubClassOf(A,
                DataAllValuesFrom(dp, DatatypeRestriction(type, max, min))));
        axioms.add(SubClassOf(A, DataHasValue(dp, lit1)));
        axioms.add(Declaration(dp));
        axioms.add(Declaration(a));
        axioms.add(ClassAssertion(A, a));
        OWLOntology o = OWLManager.createOWLOntologyManager().createOntology(
                axioms);
        assertFalse(factory().createReasoner(o).isConsistent());
    }

    @Test
    @Ignore("FaCT++ datatype problems")
    public void testContradicting_dateTime_restrictions() {
        String premise = "Prefix(:=<http://example.org/>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Ontology(\n"
                + "  Declaration(NamedIndividual(:a))\n"
                + "  Declaration(DataProperty(:dp))\n"
                + "  Declaration(Class(:A))\n"
                + "  SubClassOf(:A \n"
                + "    DataHasValue(:dp \"2007-10-08T20:44:11.656+01:00\"^^xsd:dateTime)) \n"
                + "  SubClassOf(:A \n"
                + "    DataAllValuesFrom(:dp DatatypeRestriction(\n"
                + "      xsd:dateTime \n"
                + "      xsd:minInclusive \"2008-07-08T20:44:11.656+01:00\"^^xsd:dateTime \n"
                + "      xsd:maxInclusive \"2008-10-08T20:44:11.656+01:00\"^^xsd:dateTime)\n"
                + "    )\n" + "  ) \n" + "  ClassAssertion(:A :a)\n" + ')';
        String conclusion = "";
        String id = "Contradicting_dateTime_restrictions";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "The individual a must have a dp filler that is a date from 2007, but the restrictions on dp allow only values from 2008, which makes the ontology inconsistent.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testInconsistent_Data_Complement_with_the_Restrictions() {
        String premise = "Prefix(:=<http://example.org/>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Ontology(\n" + "  Declaration(NamedIndividual(:a))\n"
                + "  Declaration(DataProperty(:dp))\n"
                + "  Declaration(Class(:A))\n"
                + "  SubClassOf(:A DataAllValuesFrom(:dp \n"
                + "    DataOneOf(\"3\"^^xsd:integer \"4\"^^xsd:integer))\n"
                + "  ) \n" + "  SubClassOf(:A DataAllValuesFrom(:dp \n"
                + "    DataOneOf(\"2\"^^xsd:integer \"3\"^^xsd:integer))\n"
                + "  )\n" + "  ClassAssertion(:A :a)\n"
                + "  ClassAssertion(DataSomeValuesFrom(:dp\n"
                + "  DataComplementOf(DataOneOf(\"3\"^^xsd:integer))) :a)\n"
                + ')';
        String conclusion = "";
        String id = "Inconsistent_Data_Complement_with_the_Restrictions";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "The individual a must have dp fillers that are in the sets {3, 4} and {2, 3}, but at the same time 3 is not allowed as a dp filler for a, which causes the inconsistency.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Ignore("FaCT++ datatype problems")
    public void testPlus_and_Minus_Zero_are_Distinct() {
        String premise = "Prefix(:=<http://example.org/>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Ontology(\n"
                + "  Declaration(NamedIndividual(:Meg))\n"
                + "  Declaration(DataProperty(:numberOfChildren))\n"
                + "  DataPropertyAssertion(:numberOfChildren :Meg \"+0.0\"^^xsd:float) \n"
                + "  DataPropertyAssertion(:numberOfChildren :Meg \"-0.0\"^^xsd:float) \n"
                + "  FunctionalDataProperty(:numberOfChildren)\n" + ')';
        String conclusion = "";
        String id = "Plus_and_Minus_Zero_are_Distinct";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "For floats and double, +0.0 and -0.0 are distinct values, which contradicts the functionality for numberOfChildren.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Ignore("FaCT++ datatype problems")
    public void testInconsistent_Byte_Filler() {
        String premise = "Prefix(:=<http://example.org/>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Ontology(\n"
                + "  Declaration(NamedIndividual(:a))\n"
                + "  Declaration(DataProperty(:dp))\n"
                + "  Declaration(Class(:A))\n"
                + "  SubClassOf(:A DataAllValuesFrom(:dp xsd:byte))  \n"
                + "  ClassAssertion(:A :a)\n"
                + "  ClassAssertion(\n"
                + "    DataSomeValuesFrom(:dp DataOneOf(\"6542145\"^^xsd:integer)) :a\n"
                + "  )\n" + ')';
        String conclusion = "";
        String id = "Inconsistent_Byte_Filler";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "The individual a must have the integer 6542145 as dp filler, but all fillers must also be bytes. Since 6542145 is not byte, the ontology is inconsistent.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed(reason = "changed to fix unreliable iris")
    public void testone_two() throws OWLException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        IRI ontoIRI = IRI("urn:onetwo");
        OWLOntology o = m.createOntology(ontoIRI);
        String ns = "http://example.com/";
        OWLClass twoa = Class(IRI(ns + "twoa"));
        OWLObjectProperty atotwoaprime = ObjectProperty(IRI(ns + "atotwoaprime"));
        OWLObjectProperty atob = ObjectProperty(IRI(ns + "atob"));
        OWLClass bandc = Class(IRI(ns + "bandc"));
        OWLClass b = Class(IRI(ns + 'b'));
        OWLClass c = Class(IRI(ns + 'c'));
        OWLClass a = Class(IRI(ns + 'a'));
        OWLObjectProperty twoatobandc = ObjectProperty(IRI(ns + "twoatobandc"));
        OWLNamedIndividual j = NamedIndividual(IRI(ns + 'j'));
        OWLNamedIndividual i = NamedIndividual(IRI(ns + 'i'));
        OWLNamedIndividual k = NamedIndividual(IRI(ns + 'k'));
        OWLObjectProperty bandctotwoaprime = ObjectProperty(IRI(ns
                + "bandctotwoaprime"));
        OWLObjectProperty btoaprime = ObjectProperty(IRI(ns + "btoaprime"));
        OWLObjectProperty btoc = ObjectProperty(IRI(ns + "btoc"));
        OWLObjectProperty ctobprime = ObjectProperty(IRI(ns + "ctobprime"));
        OWLObjectProperty twoatoa = ObjectProperty(IRI(ns + "twoatoa"));
        m.addAxiom(o, Declaration(a));
        m.addAxiom(o, EquivalentClasses(a, ObjectOneOf(j, i, k)));
        m.addAxiom(o, SubClassOf(a, ObjectSomeValuesFrom(atob, b)));
        m.addAxiom(o, SubClassOf(a, ObjectSomeValuesFrom(atotwoaprime, twoa)));
        m.addAxiom(o, DisjointClasses(a, b));
        m.addAxiom(o, DisjointClasses(a, c));
        m.addAxiom(o, DisjointClasses(a, twoa));
        m.addAxiom(o, Declaration(b));
        m.addAxiom(o, SubClassOf(b, ObjectSomeValuesFrom(btoaprime, a)));
        m.addAxiom(o, SubClassOf(b, ObjectSomeValuesFrom(btoc, c)));
        m.addAxiom(o, DisjointClasses(b, a));
        m.addAxiom(o, DisjointClasses(b, c));
        m.addAxiom(o, DisjointClasses(b, twoa));
        m.addAxiom(o, Declaration(bandc));
        m.addAxiom(o, EquivalentClasses(bandc, ObjectUnionOf(c, b)));
        m.addAxiom(o,
                SubClassOf(bandc, ObjectSomeValuesFrom(bandctotwoaprime, twoa)));
        m.addAxiom(o, DisjointClasses(bandc, twoa));
        m.addAxiom(o, Declaration(c));
        m.addAxiom(o, SubClassOf(c, ObjectSomeValuesFrom(ctobprime, b)));
        m.addAxiom(o, DisjointClasses(c, a));
        m.addAxiom(o, DisjointClasses(c, b));
        m.addAxiom(o, DisjointClasses(c, twoa));
        m.addAxiom(o, Declaration(twoa));
        m.addAxiom(o, SubClassOf(twoa, ObjectSomeValuesFrom(twoatoa, a)));
        m.addAxiom(o,
                SubClassOf(twoa, ObjectSomeValuesFrom(twoatobandc, bandc)));
        m.addAxiom(o, DisjointClasses(twoa, a));
        m.addAxiom(o, DisjointClasses(twoa, b));
        m.addAxiom(o, DisjointClasses(twoa, bandc));
        m.addAxiom(o, DisjointClasses(twoa, c));
        m.addAxiom(o, Declaration(atob));
        m.addAxiom(o, InverseObjectProperties(btoaprime, atob));
        m.addAxiom(o, FunctionalObjectProperty(atob));
        m.addAxiom(o, InverseFunctionalObjectProperty(atob));
        m.addAxiom(o, Declaration(atotwoaprime));
        m.addAxiom(o, InverseObjectProperties(atotwoaprime, twoatoa));
        m.addAxiom(o, FunctionalObjectProperty(atotwoaprime));
        m.addAxiom(o, InverseFunctionalObjectProperty(atotwoaprime));
        m.addAxiom(o, Declaration(bandctotwoaprime));
        m.addAxiom(o, InverseObjectProperties(bandctotwoaprime, twoatobandc));
        m.addAxiom(o, FunctionalObjectProperty(bandctotwoaprime));
        m.addAxiom(o, InverseFunctionalObjectProperty(bandctotwoaprime));
        m.addAxiom(o, Declaration(btoaprime));
        m.addAxiom(o, InverseObjectProperties(btoaprime, atob));
        m.addAxiom(o, FunctionalObjectProperty(btoaprime));
        m.addAxiom(o, InverseFunctionalObjectProperty(btoaprime));
        m.addAxiom(o, Declaration(btoc));
        m.addAxiom(o, InverseObjectProperties(ctobprime, btoc));
        m.addAxiom(o, FunctionalObjectProperty(btoc));
        m.addAxiom(o, InverseFunctionalObjectProperty(btoc));
        m.addAxiom(o, Declaration(ctobprime));
        m.addAxiom(o, InverseObjectProperties(ctobprime, btoc));
        m.addAxiom(o, FunctionalObjectProperty(ctobprime));
        m.addAxiom(o, InverseFunctionalObjectProperty(ctobprime));
        m.addAxiom(o, Declaration(twoatoa));
        m.addAxiom(o, InverseObjectProperties(atotwoaprime, twoatoa));
        m.addAxiom(o, FunctionalObjectProperty(twoatoa));
        m.addAxiom(o, InverseFunctionalObjectProperty(twoatoa));
        m.addAxiom(o, Declaration(twoatobandc));
        m.addAxiom(o, InverseObjectProperties(bandctotwoaprime, twoatobandc));
        m.addAxiom(o, FunctionalObjectProperty(twoatobandc));
        m.addAxiom(o, InverseFunctionalObjectProperty(twoatobandc));
        m.addAxiom(o, DifferentIndividuals(i, j, k));
        OWLReasoner reasoner = factory().createReasoner(o);
        assertFalse(
                "Start with 3 classes, a,b,c and relate them so instances have to be in a 1:1 relationship with each other.\n"
                        + "The class b-and-c is the union of b and c. Therefore there have to be 2 instances of b-and-c for every instance of a.\n"
                        + "Relate the class 2a to b-and-c so that *their* instances are in 1:1 relationship.\n"
                        + "Now relate 2a to a so that *their* instances are in a 1:1 relationship. This should lead to a situation in which every instance\n"
                        + "of 2a is 1:1 with an instance of a, and at the same time 2:1 with an instance of a.\n"
                        + "Unless all the classes have an infinite number of members or are empty this doesn't work. This example has a is the enumerated class {i,j,k} (i,j,k all different individuals). So it should be inconsistent.",
                reasoner.isConsistent());
    }
}
