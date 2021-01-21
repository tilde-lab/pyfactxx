package bugs.debug;

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
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLFacet;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

import testbase.TestBase;
import conformancetests.Changed;
import conformancetests.JUnitRunner;
import conformancetests.TestClasses;

@Ignore("disabling for release")
@SuppressWarnings("javadoc")
public class FppBrokenTestCase extends TestBase {

	// A number of test cases from VerifyComplianceOWLSNewFeatures:
	// -- disjoint data/object properties queries are not supported
	// A number of test cases from VerifyCompliance People/University:
	// -- bug in bottom-up traversing taxonomy in presence of individuals

    @Test
    // FaCT++ datatype problems
    // from FixedTestCase
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
    @Changed(reason = "old test appears to use the wrong value")
    // FaCT++ datatype problems
    // from FixedTestCase
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
    // FaCT++ datatype problems
    // from FixedTestCase
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
    // FaCT++ datatype problems
    // from FixedTestCase
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
    // FaCT++ datatype problems
    // from FixedTestCase
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
    // @Ignore("FaCT++ vs OWL API mismatch")
    // from WebOnt_AnnotationProperty_002_TestCase
    public void testWebOnt_AnnotationProperty_002() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/AnnotationProperty/premises002#\"\n"
                + "xml:base=\"http://www.w3.org/2002/03owlt/AnnotationProperty/premises002\" >\n"
                + "<owl:Ontology/>\n"
                + "<owl:Class rdf:ID=\"A\">"
                + "  <first:ap>"
                + "     <owl:Class rdf:ID=\"B\"/>"
                + "  </first:ap>"
                + "</owl:Class>"
                + "<owl:AnnotationProperty rdf:ID=\"ap\"/>" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\""
                + "    xmlns:first=\"http://www.w3.org/2002/03owlt/AnnotationProperty/premises002#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/AnnotationProperty/conclusions002\" >\n"
                + " <owl:Ontology/>\n"
                + " <owl:Class rdf:about=\"premises002#A\">"
                + "    <first:ap>"
                + "       <owl:Thing />"
                + "    </first:ap>"
                + "  </owl:Class>"
                + "  <owl:AnnotationProperty rdf:about=\"premises002#ap\"/>"
                + "</rdf:RDF>";
        String id = "WebOnt_AnnotationProperty_002";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "In OWL 1, this test was used to expose differences between the RDF Based and Direct semantics.  In OWL 2, the entailment ontology holds under both semantics.  Under the OWL 2 Direct Semantics, annotations in the conclusion ontology are ignored, so the only axiom evaluated in ClassAssertion(owl:Thing _:x).  Under the OWL 2 RDF Based semantics, annotations are relevant, and in this test, the entailment holds.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    // @Ignore("FaCT++ datatype problems")
    // from TestDateTime
    public void testDifferent() throws OWLOntologyCreationException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = m.createOntology();
        OWLNamedIndividual x = NamedIndividual(IRI("urn:test:x"));
        OWLNamedIndividual y = NamedIndividual(IRI("urn:test:y"));
        OWLDataProperty p = DataProperty(IRI("urn:test:p"));
        OWLLiteral date1 = Literal("2008-07-08T20:44:11.656+01:00",
                OWL2Datatype.XSD_DATE_TIME);
        OWLLiteral date2 = Literal("2008-07-10T20:44:11.656+01:00",
                OWL2Datatype.XSD_DATE_TIME);
        m.addAxiom(o, DataPropertyAssertion(p, x, date1));
        m.addAxiom(o, DataPropertyAssertion(p, y, date2));
        m.addAxiom(o, FunctionalDataProperty(p));
        m.addAxiom(o, SameIndividual(x, y));
        OWLReasoner r = factory().createReasoner(o);
        assertFalse(
                "Ontology was supposed to be inconsistent!\n"
                        + o.getLogicalAxioms(), r.isConsistent());
    }

    @Test
    // @Ignore("FaCT++ datatype problems")
    // from TestDateTime
    public void testBetween() throws OWLOntologyCreationException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = m.createOntology();
        OWLNamedIndividual x = NamedIndividual(IRI("urn:test:x"));
        OWLClass c = Class(IRI("urn:test:c"));
        OWLDataProperty p = DataProperty(IRI("urn:test:p"));
        OWLLiteral date1 = Literal("2008-07-08T20:44:11.656+01:00",
                OWL2Datatype.XSD_DATE_TIME);
        OWLLiteral date3 = Literal("2008-07-09T20:44:11.656+01:00",
                OWL2Datatype.XSD_DATE_TIME);
        OWLLiteral date2 = Literal("2008-07-10T20:44:11.656+01:00",
                OWL2Datatype.XSD_DATE_TIME);
        OWLDataRange range = DatatypeRestriction(
                Datatype(OWL2Datatype.XSD_DATE_TIME.getIRI()),
                FacetRestriction(OWLFacet.MIN_INCLUSIVE, date1),
                FacetRestriction(OWLFacet.MAX_INCLUSIVE, date2));
        OWLClassExpression psome = DataSomeValuesFrom(p, range);
        m.addAxiom(o, EquivalentClasses(c, psome));
        m.addAxiom(o, DataPropertyAssertion(p, x, date3));
        m.addAxiom(o, FunctionalDataProperty(p));
        OWLReasoner r = factory().createReasoner(o);
        assertTrue(r.isConsistent());
        assertTrue(
                "x was supposed to be an instance of c!\n"
                        + o.getLogicalAxioms(),
                r.isEntailed(ClassAssertion(c, x)));
    }

    @Test
    // @Ignore("FaCT++ unsipported datatype")
    // from TestDateTime
    public void testEqualDate() throws OWLOntologyCreationException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = m.createOntology();
        OWLDataFactory f = m.getOWLDataFactory();
        OWLNamedIndividual x = f
                .getOWLNamedIndividual(IRI.create("urn:test:x"));
        OWLNamedIndividual y = f
                .getOWLNamedIndividual(IRI.create("urn:test:y"));
        OWLDataProperty p = f.getOWLDataProperty(IRI.create("urn:test:p"));
        OWLLiteral date = f.getOWLLiteral("2008-07-08",
                f.getOWLDatatype(XSDVocabulary.DATE.getIRI()));
        m.addAxiom(o, f.getOWLDataPropertyAssertionAxiom(p, x, date));
        m.addAxiom(o, f.getOWLDataPropertyAssertionAxiom(p, y, date));
        m.addAxiom(o, f.getOWLFunctionalDataPropertyAxiom(p));
        m.addAxiom(o, f.getOWLSameIndividualAxiom(x, y));
        OWLReasoner r = factory().createReasoner(o);
        assertTrue(
                "Ontology was supposed to be consistent!\n"
                        + o.getLogicalAxioms(), r.isConsistent());
    }

    @Test
    // @Ignore("FaCT++ unsipported datatype")
    // from TestDateTime
    public void testDifferentDate() throws OWLOntologyCreationException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = m.createOntology();
        OWLDataFactory f = m.getOWLDataFactory();
        OWLNamedIndividual x = f
                .getOWLNamedIndividual(IRI.create("urn:test:x"));
        OWLNamedIndividual y = f
                .getOWLNamedIndividual(IRI.create("urn:test:y"));
        OWLDataProperty p = f.getOWLDataProperty(IRI.create("urn:test:p"));
        OWLLiteral date1 = f.getOWLLiteral("2008-07-08",
                f.getOWLDatatype(XSDVocabulary.DATE.getIRI()));
        OWLLiteral date2 = f.getOWLLiteral("2008-07-10",
                f.getOWLDatatype(XSDVocabulary.DATE.getIRI()));
        m.addAxiom(o, f.getOWLDataPropertyAssertionAxiom(p, x, date1));
        m.addAxiom(o, f.getOWLDataPropertyAssertionAxiom(p, y, date2));
        m.addAxiom(o, f.getOWLFunctionalDataPropertyAxiom(p));
        m.addAxiom(o, f.getOWLSameIndividualAxiom(x, y));
        OWLReasoner r = factory().createReasoner(o);
        assertFalse(
                "Ontology was supposed to be inconsistent!\n"
                        + o.getLogicalAxioms(), r.isConsistent());
    }

    @Test
    // @Ignore("FaCT++ unsipported datatype")
    // from TestDateTime
    public void testBetweenDate() throws OWLOntologyCreationException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = m.createOntology();
        OWLDataFactory f = m.getOWLDataFactory();
        OWLNamedIndividual x = f
                .getOWLNamedIndividual(IRI.create("urn:test:x"));
        OWLClass c = f.getOWLClass(IRI.create("urn:test:c"));
        OWLDataProperty p = f.getOWLDataProperty(IRI.create("urn:test:p"));
        OWLLiteral date1 = f.getOWLLiteral("2008-07-08",
                f.getOWLDatatype(XSDVocabulary.DATE.getIRI()));
        OWLLiteral date3 = f.getOWLLiteral("2008-07-09",
                f.getOWLDatatype(XSDVocabulary.DATE.getIRI()));
        OWLLiteral date2 = f.getOWLLiteral("2008-07-10",
                f.getOWLDatatype(XSDVocabulary.DATE.getIRI()));
        OWLDataRange range = f.getOWLDatatypeRestriction(
                f.getOWLDatatype(OWL2Datatype.XSD_DATE_TIME.getIRI()),
                f.getOWLFacetRestriction(OWLFacet.MIN_INCLUSIVE, date1),
                f.getOWLFacetRestriction(OWLFacet.MAX_INCLUSIVE, date2));
        OWLClassExpression psome = f.getOWLDataSomeValuesFrom(p, range);
        m.addAxiom(o, f.getOWLEquivalentClassesAxiom(c, psome));
        m.addAxiom(o, f.getOWLDataPropertyAssertionAxiom(p, x, date3));
        m.addAxiom(o, f.getOWLFunctionalDataPropertyAxiom(p));
        OWLReasoner r = factory().createReasoner(o);
        assertTrue(
                "x was supposed to be an instance of c!\n"
                        + o.getLogicalAxioms(),
                r.isEntailed(f.getOWLClassAssertionAxiom(c, x)));
    }

	@Test
	// @Ignore("FaCT++ datatype problems")
	// from OWL2TestCase
	public void testDatatype_DataComplementOf_001() {
		String premise = "<?xml version=\"1.0\"?>\n" + "<rdf:RDF\n"
				+ "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs= \"http://www.w3.org/2000/01/rdf-schema#\" >\n"
				+ '\n' + "<owl:Ontology/>\n" + '\n' + "<owl:DatatypeProperty rdf:about=\"p\" />\n" + '\n'
				+ "<rdf:Description rdf:about=\"p\">\n" + "  <rdfs:range>\n" + "    <rdfs:Datatype>\n"
				+ "      <owl:datatypeComplementOf rdf:resource=\"http://www.w3.org/2001/XMLSchema#positiveInteger\" /></rdfs:Datatype></rdfs:range></rdf:Description>\n"
				+ '\n' + "<rdf:Description rdf:about=\"i\">\n"
				+ "  <p rdf:datatype=\"http://www.w3.org/2001/XMLSchema#negativeInteger\">-1</p>\n"
				+ "  <p rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">A string</p></rdf:Description>\n"
				+ '\n' + "</rdf:RDF>";
		String conclusion = "";
		String id = "Datatype_DataComplementOf_001";
		TestClasses tc = TestClasses.valueOf("CONSISTENCY");
		String d = "Demonstrates that the complement of a datatype contains literals from other datatypes.";
		JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
		r.setReasonerFactory(factory());
		r.run();
	}

	@Test
    // @Ignore("FaCT++ unsupported datatype")
    // from WebOntTestCase
	public void testWebOnt_I5_8_007() throws OWLOntologyCreationException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLDataFactory f = m.getOWLDataFactory();
        OWLOntology o = m.createOntology();
        OWLDataProperty p = f.getOWLDataProperty(IRI.create("urn:p"));
        m.addAxiom(
                o,
                f.getOWLDataPropertyRangeAxiom(p,
                        f.getOWLDatatype(OWL2Datatype.XSD_SHORT.getIRI())));
        OWLReasoner r = factory().createReasoner(o);
        assertFalse("unsigned byte should not be inferred", r.isEntailed(f
                .getOWLDataPropertyRangeAxiom(p,
                        f.getOWLDatatype(OWL2Datatype.XSD_UNSIGNED_BYTE
                                .getIRI()))));
    }

    @Test
    // @Ignore("FaCT++ datatype problems")
    // from WebOnt_allValuesFrom_001_TestCase
    public void testWebOnt_miscellaneous_202() {
        String premise =
        "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
                + "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n"
                + "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n"
                + "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n"
                + "Ontology(\n" + "Declaration(DataProperty(<urn:fp>))\n"
                + "FunctionalDataProperty(<urn:fp>)\n"
                + "ClassAssertion(owl:Thing <urn:id2>)\n"
                + "DataPropertyAssertion(<urn:fp> <urn:id2> \"<br></br>\n\n"
                + "<img></img>\"^^rdf:XMLLiteral)\n"
                + "DataPropertyAssertion(<urn:fp> <urn:id2> \"<br></br>\n"
                + "<img></img>\"^^rdf:XMLLiteral)\n" + ')';
        // TODO this is silly, to pass this test the reasoner needs a lot of
        // extra processing for literals... maybe the OWL API should do this, it
        // can be useful for users in general and it's actually down to the
        // equals method for XML Literals.
        String conclusion = "";
        String id = "WebOnt_miscellaneous_202";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "This shows that insignificant whitespace in an rdf:XMLLiteral is not significant within OWL.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    // @Ignore("FaCT++ does not accept new individuals in queries")
    // from WebOnt_allValuesFrom_001_TestCase
    public void testWebOnt_I4_6_004() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I4.6/premises004\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:about=\"nonconclusions004#C1\">\n"
                + "       <owl:equivalentClass>\n"
                + "           <owl:Class rdf:about=\"nonconclusions004#C2\"/></owl:equivalentClass></owl:Class>\n"
                + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I4.6/nonconclusions004\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:ID=\"C1\">\n"
                + "       <owl:sameAs>\n"
                + "           <owl:Class rdf:ID=\"C2\"/></owl:sameAs></owl:Class>\n"
                + "</rdf:RDF>";
        String id = "WebOnt_I4_6_004";
        TestClasses tc = TestClasses.valueOf("NEGATIVE_IMPL");
        String d = "owl:sameAs is stronger than owl:equivalentClass.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    // @Ignore("FaCT++ datatype problems")
    public void testQualified_cardinality_boolean() {
        String premise = "Prefix( : = <http://example.org/test#> )\n"
                + "Prefix( xsd: = <http://www.w3.org/2001/XMLSchema#> )\n"
                + '\n'
                + "Ontology(<http://owl.semanticweb.org/page/Special:GetOntology/Qualified-cardinality-boolean?m=p>\n"
                + "  Declaration(NamedIndividual(:a))\n"
                + "  Declaration(Class(:A))\n"
                + "  Declaration(DataProperty(:dp))\n" + '\n'
                + "  SubClassOf(:A DataExactCardinality(2 :dp xsd:boolean))\n"
                + '\n' + "  ClassAssertion(:A :a)\n" + ')';
        String conclusion = "Prefix( : = <http://example.org/test#> )\n"
                + "Prefix( xsd: = <http://www.w3.org/2001/XMLSchema#> )\n"
                + '\n'
                + "Ontology(<http://owl.semanticweb.org/page/Special:GetOntology/Qualified-cardinality-boolean?m=c>\n"
                + "  Declaration(DataProperty(:dp))\n" + '\n'
                + "  DataPropertyAssertion(:dp :a \"true\"^^xsd:boolean)\n"
                + "  DataPropertyAssertion(:dp :a \"false\"^^xsd:boolean)\n"
                + ')';
        String id = "Qualified_cardinality_boolean";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "According to qualified cardinality restriction individual a should have two boolean values. Since there are only two boolean values, the data property assertions can be entailed.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    // @Ignore("FaCT++ unsupported datatype")
    // This test is actually passes, but by coincidence
    public void testConsistent_Datatype_restrictions_with_Different_Types() {
        String premise = "Prefix(:=<http://example.org/>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Ontology(\n"
                + "  Declaration(NamedIndividual(:a))\n"
                + "  Declaration(DataProperty(:dp))\n"
                + "  Declaration(Class(:A))\n"
                + "  SubClassOf(:A "
                + "DataAllValuesFrom(:dp DataOneOf(\"3\"^^xsd:integer \"4\"^^xsd:int))) \n"
                + "  SubClassOf(:A "
                + "DataAllValuesFrom(:dp DataOneOf(\"2\"^^xsd:short \"3\"^^xsd:int)))\n"
                + "  ClassAssertion(:A :a)\n"
                + "  ClassAssertion(DataSomeValuesFrom(:dp DataOneOf(\"3\"^^xsd:integer)) :a\n"
                + "  )\n" + ')';
        String conclusion = "";
        String id = "Consistent_Datatype_restrictions_with_Different_Types";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "The individual a must have dp fillers that are in the sets {3, 4} and {2, 3} (different types are used, but shorts and ints are integers). Furthermore, the dp filler must be 3, but since 3 is in both sets, the ontology is consistent.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    // @Ignore("FaCT++ does not classifies properties")
    public void testUnsatisfiableClasses() throws OWLOntologyCreationException {
        OWLOntologyManager mngr = OWLManager.createOWLOntologyManager();
        OWLOntology ont = mngr.createOntology();
        OWLDataFactory df = OWLManager.getOWLDataFactory();
        OWLDataProperty dp = df.getOWLDataProperty(IRI
                .create("urn:test:datap1"));
        mngr.addAxiom(ont,
                df.getOWLDataPropertyDomainAxiom(dp, df.getOWLNothing()));
        OWLReasonerFactory fac = factory();
        OWLReasoner r = fac.createNonBufferingReasoner(ont);
        assertEquals(r.getBottomDataPropertyNode().toString(), 2, r
                .getBottomDataPropertyNode().getEntities().size());
    }

    @Test
    // @Ignore("FaCT++ unsupported datatype")
    public void testWebOnt_I5_8_009() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.8/premises009\" >\n"
                + "  <owl:Ontology/>\n"
                + "  <owl:DatatypeProperty rdf:ID=\"p\">\n"
                + "    <rdfs:range rdf:resource=\n"
                + "  \"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\" />\n"
                + "    <rdfs:range rdf:resource=\n"
                + "  \"http://www.w3.org/2001/XMLSchema#nonPositiveInteger\" /></owl:DatatypeProperty></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.8/conclusions009\" >\n"
                + "  <owl:Ontology/>\n"
                + "  <owl:DatatypeProperty rdf:about=\"premises009#p\">\n"
                + "    <rdfs:range rdf:resource=\n"
                + "  \"http://www.w3.org/2001/XMLSchema#short\" /></owl:DatatypeProperty>\n"
                + '\n' + "</rdf:RDF>";
        String id = "WebOnt_I5_8_009";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "0 is the only xsd:nonNegativeInteger which is\n"
                + "also an xsd:nonPositiveInteger. 0 is an\n" + "xsd:short.";
        // XXX while it is true, I don't see why the zero should be a short
        // instead of a oneof from int or integer or any of the types in the
        // middle.
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    // @Ignore("FaCT++ unsupported datatype")
    public void testWebOnt_I5_8_010() {
        String premise = "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Ontology(\n"
                + "Declaration(DataProperty(<urn:t#p>))\n"
                + "DataPropertyRange(<urn:t#p> xsd:nonNegativeInteger)\n"
                + "ClassAssertion(DataSomeValuesFrom(<urn:t#p> xsd:nonPositiveInteger) <urn:t#john>)\n)";
        String conclusion = "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
                + "Ontology(\nDeclaration(DataProperty(<urn:t#p>))\n"
                + "ClassAssertion(owl:Thing <urn:t#john>)\n"
                + "DataPropertyAssertion(<urn:t#p> <urn:t#john> \"0\"^^xsd:int)\n)";
        String id = "WebOnt_I5_8_010";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "0 is the only xsd:nonNegativeInteger which is also an xsd:nonPositiveInteger.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        // r.getConfiguration().setLoggingActive(true);
        r.run();
    }

    @Test
    // @Ignore("FaCT++ unsupported datatype")
    public void testWebOnt_I5_8_008() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.8/premises008\" >\n"
                + "  <owl:Ontology/>\n"
                + "  <owl:DatatypeProperty rdf:ID=\"p\">\n"
                + "    <rdfs:range rdf:resource=\"http://www.w3.org/2001/XMLSchema#short\" />\n"
                + "    <rdfs:range rdf:resource=\"http://www.w3.org/2001/XMLSchema#unsignedInt\" />"
                + "</owl:DatatypeProperty></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.8/conclusions008\" >\n<owl:Ontology/>\n"
                + "  <owl:DatatypeProperty rdf:about=\"premises008#p\">\n"
                + "    <rdfs:range rdf:resource=\"http://www.w3.org/2001/XMLSchema#unsignedShort\" /></owl:DatatypeProperty></rdf:RDF>";
        String id = "WebOnt_I5_8_008";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "-1 is an xsd:short that is not an xsd:unsignedShort; 100000 is an "
                + "xsd:unsignedInt that is not an xsd:unsignedShort; but there are no\n"
                + "xsd:unsignedShort which are neither xsd:short nor xsd:unsignedInt";
        // TODO to make this work, the datatype reasoner must be able to infer
        // short and unsigned int equivalent unsigned short
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    // @Ignore("FaCT++ does not like BNodes/anonymous individuals")
    public void testWebOnt_someValuesFrom_003() {
        String premise = "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
                + "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n"
                + "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n"
                + "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n"
                + "Ontology(\n"
                + "Declaration(Class(<urn:person>))\n"
                + "EquivalentClasses(<urn:person> ObjectSomeValuesFrom(<urn:parent> <urn:person>))\n"
                + "Declaration(ObjectProperty(<urn:parent>))\n"
                + "ClassAssertion(<urn:person> <urn:fred>))";
        String conclusion = "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
                + "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n"
                + "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n"
                + "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n"
                + "Ontology(\n"
                + "Declaration(ObjectProperty(<urn:parent>))\n"
                +
                // "Declaration(ObjectProperty(<urn:parent>))\n"+
                // "ClassAssertion(owl:Thing <urn:fred>)\n"+
                "ObjectPropertyAssertion(<urn:parent> <urn:fred> _:genid2)\n"
                + "ClassAssertion(owl:Thing _:genid3)\n"
                + "ClassAssertion(owl:Thing _:genid2)\n"
                + "ObjectPropertyAssertion(<urn:parent> _:genid2 _:genid3))";
        // XXX I do not understand these blank nodes used as existential
        // variables
        String id = "WebOnt_someValuesFrom_003";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "A simple infinite loop for implementors to avoid.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    // @Ignore("FaCT++ does not like BNodes/anonymous individuals")
    public void testsomevaluesfrom2bnode() throws OWLOntologyCreationException {
        // XXX I do not understand these blank nodes used as existential
        // variables
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLDataFactory f = m.getOWLDataFactory();
        OWLOntology o = m.createOntology();
        OWLObjectProperty p = f.getOWLObjectProperty(IRI.create("urn:p"));
        OWLNamedIndividual a = f.getOWLNamedIndividual(IRI.create("urn:a"));
        OWLObjectSomeValuesFrom c = f.getOWLObjectSomeValuesFrom(p,
                f.getOWLThing());
        m.addAxiom(o, f.getOWLClassAssertionAxiom(c, a));
        OWLReasoner r = factory().createReasoner(o);
        assertTrue(r.isEntailed(f.getOWLObjectPropertyAssertionAxiom(p, a,
                f.getOWLAnonymousIndividual())));
    }
}
