package conformancetests;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import testbase.TestBase;

@SuppressWarnings("javadoc")
public class WebOntTestCase extends TestBase {

    @Test
    public void testWebOnt_AnnotationProperty_003() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/AnnotationProperty/consistent003#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/AnnotationProperty/consistent003\" >\n"
                + "  <owl:Ontology/>\n"
                + "  <owl:AnnotationProperty rdf:ID=\"ap\"/>\n"
                + "  <owl:Class rdf:ID=\"A\"><first:ap><rdf:Description rdf:ID=\"B\"/></first:ap></owl:Class>\n"
                + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_AnnotationProperty_003";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "URI references used in annotations don't need to be typed.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_AnnotationProperty_004() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\n"
                + "\"http://www.w3.org/2002/03owlt/AnnotationProperty/consistent004\" >\n"
                + "   <owl:Ontology />\n"
                + "   <owl:AnnotationProperty rdf:ID=\"ap\">\n"
                + "     <rdfs:range rdf:resource=\"http://www.w3.org/2001/XMLSchema#string\"/></owl:AnnotationProperty></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_AnnotationProperty_004";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "AnnotationProperty's in OWL Lite and OWL DL, may not have range constraints.  They are permitted in OWL 2 DL.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_I4_5_001() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/I4.5/premises001#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I4.5/premises001\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:ID=\"EuropeanCountry\" />\n"
                + "    <owl:Class rdf:ID=\"Person\" />\n"
                + "    <owl:Class rdf:ID=\"EUCountry\"><owl:oneOf rdf:parseType=\"Collection\"><first:EuropeanCountry rdf:ID=\"UK\"/><first:EuropeanCountry rdf:ID=\"BE\"/><first:EuropeanCountry rdf:ID=\"ES\"/><first:EuropeanCountry rdf:ID=\"FR\"/><first:EuropeanCountry rdf:ID=\"NL\"/><first:EuropeanCountry rdf:ID=\"PT\"/></owl:oneOf></owl:Class>\n"
                + "    <owl:ObjectProperty rdf:ID=\"hasEuroMP\" ><rdfs:domain rdf:resource=\"#EUCountry\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:ID=\"isEuroMPFrom\" ><owl:inverseOf rdf:resource=\"#hasEuroMP\"/></owl:ObjectProperty>\n"
                + "    <owl:Class rdf:ID=\"EuroMP\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#isEuroMPFrom\" /><owl:someValuesFrom rdf:resource=\"http://www.w3.org/2002/07/owl#Thing\" /></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + "    <first:Person rdf:ID=\"Kinnock\" />\n"
                + "    <first:EuropeanCountry rdf:about=\"#UK\"><first:hasEuroMP rdf:resource=\"#Kinnock\" /></first:EuropeanCountry></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:first=\"http://www.w3.org/2002/03owlt/I4.5/premises001#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I4.5/conclusions001\" >\n"
                + "  <owl:Ontology/>\n"
                + "  <first:EuroMP rdf:about=\"premises001#Kinnock\" />\n"
                + "  <owl:Class rdf:about=\"premises001#EuroMP\"/>\n"
                + "</rdf:RDF>";
        String id = "WebOnt_I4_5_001";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "An example combinging owl:oneOf and owl:inverseOf.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_I4_5_002() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/I4.5/inconsistent002#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I4.5/inconsistent002\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:ID=\"EuropeanCountry\" />\n"
                + "    <owl:Class rdf:ID=\"Person\" />\n"
                + "    <owl:Class rdf:ID=\"EUCountry\"><owl:oneOf rdf:parseType=\"Collection\"><first:EuropeanCountry rdf:ID=\"UK\"/><first:EuropeanCountry rdf:ID=\"BE\"/><first:EuropeanCountry rdf:ID=\"ES\"/><first:EuropeanCountry rdf:ID=\"FR\"/><first:EuropeanCountry rdf:ID=\"NL\"/><first:EuropeanCountry rdf:ID=\"PT\"/></owl:oneOf></owl:Class>\n"
                + "    <owl:ObjectProperty rdf:ID=\"hasEuroMP\" ><rdfs:domain rdf:resource=\"#EUCountry\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:ID=\"isEuroMPFrom\" ><owl:inverseOf rdf:resource=\"#hasEuroMP\"/></owl:ObjectProperty>\n"
                + "    <owl:Class rdf:ID=\"EuroMP\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#isEuroMPFrom\" /><owl:someValuesFrom rdf:resource=\"http://www.w3.org/2002/07/owl#Thing\" /></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + "    <first:Person rdf:ID=\"Kinnock\" ><rdf:type><owl:Class><owl:complementOf rdf:resource=\"#EuroMP\"/></owl:Class></rdf:type></first:Person>\n"
                + "    <first:EuropeanCountry rdf:about=\"#UK\"><first:hasEuroMP rdf:resource=\"#Kinnock\" /></first:EuropeanCountry></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_I4_5_002";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "An example combining owl:oneOf and owl:inverseOf.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_I4_6_005_Direct() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I4.6/premises005\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:about=\"nonconclusions005#C1\"><rdfs:comment>An example class.</rdfs:comment><owl:equivalentClass><owl:Class rdf:about=\"nonconclusions005#C2\"/></owl:equivalentClass></owl:Class></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I4.6/nonconclusions005\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:ID=\"C2\"><rdfs:comment>An example class.</rdfs:comment></owl:Class></rdf:RDF>";
        String id = "WebOnt_I4_6_005_Direct";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Under the direct semantics, test WebOnt-I4.6-005 must be treated as a positive entailment test because the direct semantics ignore annotations in the conclusion ontology.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_I5_24_003() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.24/premises003\" >\n"
                + "  <owl:Ontology/>\n"
                + "  <owl:ObjectProperty rdf:ID=\"prop\">\n"
                + "     <rdfs:range>\n"
                + "        <owl:Class rdf:about=\"#A\"/></rdfs:range></owl:ObjectProperty>\n"
                + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.24/conclusions003\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:Class rdf:about=\"http://www.w3.org/2002/07/owl#Thing\"><rdfs:subClassOf><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"premises003#prop\"/></owl:onProperty><owl:allValuesFrom><owl:Class rdf:about=\"premises003#A\"/></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf></owl:Class></rdf:RDF>";
        String id = "WebOnt_I5_24_003";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "This is a typical definition of range from description logic.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_I5_24_004() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.24/premises004\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:Class rdf:about=\"http://www.w3.org/2002/07/owl#Thing\"><rdfs:subClassOf><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"conclusions004#prop\"/></owl:onProperty><owl:allValuesFrom><owl:Class rdf:about=\"conclusions004#A\"/></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf></owl:Class></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.24/conclusions004\" >\n"
                + "  <owl:Ontology/>\n"
                + "  <owl:ObjectProperty rdf:ID=\"prop\">\n"
                + "     <rdfs:range>\n"
                + "        <owl:Class rdf:about=\"#A\"/></rdfs:range></owl:ObjectProperty>\n"
                + "</rdf:RDF>";
        String id = "WebOnt_I5_24_004";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "This is a typical definition of range from description logic.\n"
                + "It works both ways.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_I5_26_001() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.26/consistent001\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:nodeID=\"B\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"A\"/><owl:Class rdf:ID=\"B\"/></owl:intersectionOf></owl:Class>\n"
                + "    <rdf:Description><rdf:type rdf:nodeID=\"B\"/></rdf:Description>\n"
                + "    <owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"C\"/><rdf:Description rdf:nodeID=\"B\"/></owl:intersectionOf></owl:Class></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_I5_26_001";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "Structure sharing was not permitted in OWL DL, between a class description\n"
                + "and a type triple, but is permitted in OWL 2 DL.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_I5_26_002() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.26/consistent002\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:nodeID=\"B\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"J\"/><owl:Class rdf:ID=\"B\"/></owl:intersectionOf><owl:equivalentClass><owl:Class rdf:ID=\"A\"/><owl:Class rdf:ID=\"K\"/></owl:equivalentClass></owl:Class>\n"
                + "    <rdf:Description><rdf:type rdf:nodeID=\"B\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_I5_26_002";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "Structure sharing was not permitted in OWL DL, between an\n"
                + "owl:equivalentClass triple\n"
                + "and a type triple, but is permitted in OWL 2 DL.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_I5_26_003() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.26/consistent003\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:nodeID=\"B\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"B\"/><owl:Class rdf:ID=\"K\"/></owl:intersectionOf></owl:Class>\n"
                + "    <owl:Class rdf:ID=\"notB\"><owl:complementOf rdf:nodeID=\"B\"/></owl:Class>\n"
                + "    <owl:Class rdf:ID=\"u\"><owl:unionOf rdf:parseType=\"Collection\"><rdf:Description rdf:nodeID=\"B\"/><owl:Class rdf:ID=\"A\"/></owl:unionOf></owl:Class></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_I5_26_003";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "Structure sharing was not permitted in OWL DL, between two class descriptions, but is permitted in OWL 2 DL.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_I5_26_004() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.26/consistent004\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:nodeID=\"B\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"A\"/><owl:Class rdf:ID=\"B\"/></owl:intersectionOf><owl:disjointWith><owl:Class rdf:ID=\"C\"/></owl:disjointWith></owl:Class>\n"
                + "    <owl:Class rdf:ID=\"notB\"><owl:complementOf rdf:nodeID=\"B\"/></owl:Class></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_I5_26_004";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "Structure sharing was not permitted in OWL DL, between a class description and an\n"
                + "owl:disjointWith triple, but is permitted in OWL 2 DL.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testWebOnt_I5_26_005() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xml:base=\"http://www.w3.org/2002/03owlt/I5.26/consistent005\" ><owl:Ontology/>\n"
                + "   <owl:Class rdf:nodeID=\"B\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"A\"/><owl:Class rdf:ID=\"B\"/>"
                + "</owl:intersectionOf><owl:disjointWith><owl:Class rdf:ID=\"C\"/></owl:disjointWith><owl:equivalentClass><owl:Class rdf:ID=\"D\"/></owl:equivalentClass></owl:Class></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_I5_26_005";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "Structure sharing was not permitted in OWL DL, between an owl:equivalentClass triple and an\n"
                + "owl:disjointWith triple, but is permitted in OWL 2 DL.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed(reason = "test doesn't make sense; This code does the test in a meaningful way")
    public
            void testWebOnt_I5_26_009() throws OWLOntologyCreationException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = m.createOntology();
        OWLDataFactory f = m.getOWLDataFactory();
        OWLObjectProperty p = f.getOWLObjectProperty(IRI
                .create("urn:test:test#p"));
        m.addAxiom(o, f.getOWLDeclarationAxiom(p));
        OWLReasoner r = factory().createReasoner(o);
        assertTrue(r.isConsistent());
        OWLObjectMinCardinality c = f.getOWLObjectMinCardinality(1, p);
        assertTrue(r.isEntailed(f.getOWLSubClassOfAxiom(c, c)));
    }

    @Test
    @Ignore("Conclusion does not contain axioms")
    public void testWebOnt_I5_26_010() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/I5.26/premises010#\" xml:base=\"http://www.w3.org/2002/03owlt/I5.26/premises010\" ><owl:Ontology/><owl:ObjectProperty rdf:ID=\"p\" /></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xml:base=\"http://www.w3.org/2002/03owlt/I5.26/conclusions010\" >\n"
                + "  <owl:Ontology/>\n"
                + "  <owl:Restriction rdf:nodeID=\"n\">\n"
                + "     <owl:onProperty><owl:ObjectProperty rdf:about=\"premises010#p\" /></owl:onProperty>\n"
                + "     <owl:minCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></rdf:RDF>";
        String id = "WebOnt_I5_26_010";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "The abstract syntax form of the conclusions is:\n"
                + " EquivalentClasses( restriction( first:p, minCardinality(1) ) )\n"
                + " ObjectProperty( first:p )\n"
                + "This is trivially true given that first:p is an \n"
                + "individualvaluedPropertyID.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_I5_2_001() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/I5.2/consistent001#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.2/consistent001\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:Class rdf:ID=\"Nothing\"><rdfs:subClassOf><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#p\"/></owl:onProperty><owl:minCardinality rdf:datatype=\n"
                + "\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:minCardinality></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#p\"/></owl:onProperty><owl:maxCardinality rdf:datatype=\n"
                + "\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">0</owl:maxCardinality></owl:Restriction></rdfs:subClassOf></owl:Class></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_I5_2_001";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "A class like owl:Nothing can be defined using OWL Lite restrictions.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_I5_2_002() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.2/premises002\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:Class rdf:ID=\"Nothing\"><rdfs:subClassOf><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#p\"/></owl:onProperty><owl:minCardinality rdf:datatype=\n"
                + "\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:minCardinality></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#p\"/></owl:onProperty><owl:maxCardinality rdf:datatype=\n"
                + "\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">0</owl:maxCardinality></owl:Restriction></rdfs:subClassOf></owl:Class></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.2/conclusions002\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:Class rdf:about=\"premises002#Nothing\"><owl:equivalentClass><owl:Class rdf:about=\"http://www.w3.org/2002/07/owl#Nothing\" /></owl:equivalentClass></owl:Class></rdf:RDF>";
        String id = "WebOnt_I5_2_002";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "A class like owl:Nothing can be defined using OWL Lite restrictions.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_I5_2_003() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/I5.2/consistent003#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.2/consistent003\" >\n"
                + "  <owl:Ontology/>\n"
                + "  <owl:Class rdf:ID=\"Nothing\"><rdfs:subClassOf><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#p\"/></owl:onProperty><owl:minCardinality rdf:datatype=\n"
                + "\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:minCardinality></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#p\"/></owl:onProperty><owl:maxCardinality rdf:datatype=\n"
                + "\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">0</owl:maxCardinality></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:ID=\"A\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#q\"/></owl:onProperty><owl:someValuesFrom><owl:Class rdf:about=\"http://www.w3.org/2002/07/owl#Thing\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"notA\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#q\"/></owl:onProperty><owl:allValuesFrom><owl:Class rdf:about=\"#Nothing\"/></owl:allValuesFrom></owl:Restriction></owl:equivalentClass>\n"
                + " </owl:Class>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_I5_2_003";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "The complement of a class can be defined using OWL Lite restrictions.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_I5_2_004() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/I5.2/premises004#\" xmlns:second=\"http://www.w3.org/2002/03owlt/I5.2/conclusions004#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.2/premises004\" >\n"
                + "  <owl:Ontology/>\n"
                + "  <owl:Class rdf:ID=\"Nothing\"><rdfs:subClassOf><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#p\"/></owl:onProperty><owl:minCardinality rdf:datatype=\n"
                + "\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:minCardinality></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#p\"/></owl:onProperty><owl:maxCardinality rdf:datatype=\n"
                + "\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">0</owl:maxCardinality></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:ID=\"A\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#q\"/></owl:onProperty><owl:someValuesFrom><owl:Class rdf:about=\"http://www.w3.org/2002/07/owl#Thing\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"notA\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#q\"/></owl:onProperty><owl:allValuesFrom><owl:Class rdf:about=\"#Nothing\"/></owl:allValuesFrom></owl:Restriction></owl:equivalentClass>\n"
                + " </owl:Class>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.2/conclusions004\" >\n"
                + "  <owl:Ontology/>\n"
                + "  <owl:Class rdf:about=\"premises004#notA\">\n"
                + "     <owl:complementOf><owl:Class rdf:about=\"premises004#A\"/></owl:complementOf></owl:Class>\n"
                + "</rdf:RDF>";
        String id = "WebOnt_I5_2_004";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "The complement of a class can be defined using OWL Lite restrictions.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_I5_2_005() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/I5.2/consistent005#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.2/consistent005\" >\n"
                + "  <owl:Ontology/>\n"
                + "  <owl:Class rdf:ID=\"Nothing\"><rdfs:subClassOf><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#p\"/></owl:onProperty><owl:minCardinality rdf:datatype=\n"
                + "\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:minCardinality></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#p\"/></owl:onProperty><owl:maxCardinality rdf:datatype=\n"
                + "\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">0</owl:maxCardinality></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:ID=\"A\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#q\"/></owl:onProperty><owl:someValuesFrom><owl:Class rdf:about=\"http://www.w3.org/2002/07/owl#Thing\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"notA\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#q\"/></owl:onProperty><owl:allValuesFrom><owl:Class rdf:about=\"#Nothing\"/></owl:allValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"B\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#r\"/></owl:onProperty><owl:someValuesFrom><owl:Class rdf:about=\"http://www.w3.org/2002/07/owl#Thing\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"notB\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#r\"/></owl:onProperty><owl:allValuesFrom><owl:Class rdf:about=\"#Nothing\"/></owl:allValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"notAorB\"><owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "    <owl:Class rdf:about=\"#notA\"/>\n"
                + "    <owl:Class rdf:about=\"#notB\"/></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:ID=\"AorB\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#s\"/></owl:onProperty><owl:someValuesFrom><owl:Class rdf:about=\"http://www.w3.org/2002/07/owl#Thing\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"#notAorB\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#s\"/></owl:onProperty><owl:allValuesFrom><owl:Class rdf:about=\"#Nothing\"/></owl:allValuesFrom></owl:Restriction></owl:equivalentClass>\n"
                + " </owl:Class>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_I5_2_005";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "The union of two classes can be defined using OWL Lite restrictions, and owl:intersectionOf.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_I5_2_006() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.2/premises006\" >\n"
                + "  <owl:Ontology/>\n"
                + "  <owl:Class rdf:ID=\"Nothing\"><rdfs:subClassOf><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#p\"/></owl:onProperty><owl:minCardinality rdf:datatype=\n"
                + "\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:minCardinality></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#p\"/></owl:onProperty><owl:maxCardinality rdf:datatype=\n"
                + "\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">0</owl:maxCardinality></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:ID=\"A\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#q\"/></owl:onProperty><owl:someValuesFrom><owl:Class rdf:about=\"http://www.w3.org/2002/07/owl#Thing\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"notA\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#q\"/></owl:onProperty><owl:allValuesFrom><owl:Class rdf:about=\"#Nothing\"/></owl:allValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"B\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#r\"/></owl:onProperty><owl:someValuesFrom><owl:Class rdf:about=\"http://www.w3.org/2002/07/owl#Thing\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"notB\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#r\"/></owl:onProperty><owl:allValuesFrom><owl:Class rdf:about=\"#Nothing\"/></owl:allValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"notAorB\">\n"
                + "   <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "    <owl:Class rdf:about=\"#notA\"/>\n"
                + "    <owl:Class rdf:about=\"#notB\"/></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:ID=\"AorB\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#s\"/></owl:onProperty><owl:someValuesFrom><owl:Class rdf:about=\"http://www.w3.org/2002/07/owl#Thing\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"#notAorB\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#s\"/></owl:onProperty><owl:allValuesFrom><owl:Class rdf:about=\"#Nothing\"/></owl:allValuesFrom></owl:Restriction></owl:equivalentClass>\n"
                + " </owl:Class>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.2/conclusions006\" >\n"
                + "  <owl:Ontology/>\n"
                + "  <owl:Class rdf:about=\"premises006#AorB\">\n"
                + "    <owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"premises006#A\"/><owl:Class rdf:about=\"premises006#B\"/></owl:unionOf></owl:Class>\n"
                + "</rdf:RDF>";
        String id = "WebOnt_I5_2_006";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "The union of two classes can be defined using OWL Lite restrictions, and owl:intersectionOf.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_I5_3_006() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/I5.3/consistent006#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.3/consistent006\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:Thing><first:p><owl:Thing/></first:p>\n"
                + "   </owl:Thing>\n"
                + "   <owl:ObjectProperty rdf:ID=\"p\" />\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_I5_3_006";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "A minimal OWL Lite version of <a xmlns=\"http://www.w3.org/1999/xhtml\" href=\"#I5.3-005\">test 005</a>.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_I5_3_008() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/I5.3/consistent008#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.3/consistent008\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Thing><first:dp>value</first:dp></owl:Thing>\n"
                + "    <owl:DatatypeProperty rdf:ID=\"dp\" />\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_I5_3_008";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "An OWL Lite version of <a xmlns=\"http://www.w3.org/1999/xhtml\" href=\"#I5.3-007\">test 007</a>.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_I5_3_010() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/I5.3/consistent010#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.3/consistent010\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:ObjectProperty rdf:ID=\"p\"/>   \n"
                + "   <owl:Thing><first:p><owl:Class rdf:ID=\"c\"/></first:p></owl:Thing></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_I5_3_010";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "Classes could not be the object of regular properties in OWL DL.  This ontology is permissible in OWL 2 DL due to class / individual punning.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_I5_3_011() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/I5.3/consistent011#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.3/consistent011\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:AnnotationProperty rdf:ID=\"p\"/>   \n"
                + "   <owl:Thing><first:p><owl:Class rdf:ID=\"c\"/></first:p></owl:Thing></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_I5_3_011";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "Classes can be the object of annotation properties in OWL Lite and DL.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_I5_5_005() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.5/premises005\" >\n"
                + "    <owl:Class rdf:ID=\"a\" />\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.5/conclusions005\" >\n"
                + "   <owl:Class>\n"
                + "     <owl:unionOf><rdf:List><rdf:first><owl:Class rdf:about=\"premises005#a\"/></rdf:first><rdf:rest rdf:resource = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#nil\"/></rdf:List></owl:unionOf></owl:Class>\n"
                + "</rdf:RDF>";
        String id = "WebOnt_I5_5_005";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "This test exhibits the effect of the comprehension principles in OWL Full.  The conclusion ontology only contains a class declaration, ObjectUnionOf class expression does not appear in an axiom.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Ignore("FaCT++ unsupported datatype")
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
    public void testWebOnt_I5_8_011() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\">\n"
                + "  <owl:Ontology/>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.8/conclusions011\" >\n"
                + "  <owl:Ontology/>\n"
                + "  <rdfs:Datatype rdf:about=\"http://www.w3.org/2001/XMLSchema#integer\"/>\n"
                + "  <rdfs:Datatype rdf:about=\"http://www.w3.org/2001/XMLSchema#string\"/></rdf:RDF>";
        String id = "WebOnt_I5_8_011";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "The empty graph entails that xsd:integer and xsd:string\n"
                + "are a rdfs:Datatype";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_InverseFunctionalProperty_001() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl =\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/InverseFunctionalProperty/premises001#\" \n"
                + "  xml:base=\"http://www.w3.org/2002/03owlt/InverseFunctionalProperty/premises001\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:InverseFunctionalProperty rdf:ID=\"prop\"/>\n"
                + "    <rdf:Description rdf:ID=\"subject1\"><first:prop rdf:resource=\"#object\" /></rdf:Description>\n"
                + "    <rdf:Description rdf:ID=\"subject2\"><first:prop rdf:resource=\"#object\" /></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.w3.org/2002/03owlt/InverseFunctionalProperty/premises001#object\">\n"
                + "    <rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#Thing\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.w3.org/2002/03owlt/InverseFunctionalProperty/premises001#subject2\">\n"
                + "    <rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#Thing\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.w3.org/2002/03owlt/InverseFunctionalProperty/premises001#subject1\">\n"
                + "    <rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#Thing\"/></rdf:Description></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl =\"http://www.w3.org/2002/07/owl#\"\n"
                + "  xml:base=\"http://www.w3.org/2002/03owlt/InverseFunctionalProperty/conclusions001\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <rdf:Description rdf:about=\"premises001#subject1\"><owl:sameAs rdf:resource=\"premises001#subject2\" /></rdf:Description></rdf:RDF>";
        String id = "WebOnt_InverseFunctionalProperty_001";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "If prop belongs to owl:InverseFunctionalProperty,\n"
                + "and object denotes a resource\n"
                + "which is the object of two prop triples, then the subjects\n"
                + "of these triples have the same denotation.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_Nothing_001() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/Nothing/inconsistent001\" >\n"
                + "  <owl:Ontology/>\n" + "  <owl:Nothing/>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_Nothing_001";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "The triple asserts something of type owl:Nothing, however\n"
                + "that is the empty class.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_Ontology_001() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/Ontology/premises001#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/Ontology/premises001\" >\n"
                + "   <owl:Ontology rdf:about=\"\" />\n"
                + "   <owl:Class rdf:ID=\"Car\">\n"
                + "     <owl:equivalentClass><owl:Class rdf:ID=\"Automobile\"/></owl:equivalentClass></owl:Class>\n"
                + "  <first:Car rdf:ID=\"car\">\n"
                + "     <rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#Thing\" /></first:Car>\n"
                + "  <first:Automobile rdf:ID=\"auto\">\n"
                + "     <rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#Thing\" /></first:Automobile></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/Ontology/premises001#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/Ontology/conclusions001\" >\n"
                + "  <owl:Ontology />\n"
                + "  <first:Car rdf:about=\"premises001#auto\">\n"
                + "     <rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#Thing\" /></first:Car>\n"
                + "  <first:Automobile rdf:about=\"premises001#car\">\n"
                + "     <rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#Thing\" /></first:Automobile>\n"
                + "   <owl:Class rdf:about=\"premises001#Car\"/>\n"
                + "   <owl:Class rdf:about=\"premises001#Automobile\"/>\n"
                + "</rdf:RDF>";
        String id = "WebOnt_Ontology_001";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "This is a variation of <a xmlns=\"http://www.w3.org/1999/xhtml\" href=\"#equivalentClass-001\">equivalentClass-001</a>,\n"
                + "showing the use of owl:Ontology triples in the premises and conclusions.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_Restriction_001() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/Restriction/inconsistent001#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/Restriction/inconsistent001\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:ObjectProperty rdf:ID=\"op\"/>\n"
                + "   <rdf:Description rdf:ID=\"a\">\n"
                + "     <rdf:type><owl:Restriction><owl:onProperty rdf:resource=\"#op\"/><owl:someValuesFrom rdf:resource=\n"
                + "     \"http://www.w3.org/2002/07/owl#Nothing\" /></owl:Restriction></rdf:type></rdf:Description>\n"
                + "   <rdf:Description rdf:ID=\"b\">\n"
                + "     <rdf:type><owl:Restriction><owl:onProperty rdf:resource=\"#op\"/><owl:someValuesFrom rdf:resource=\n"
                + "     \"http://www.w3.org/2002/07/owl#Nothing\" /></owl:Restriction></rdf:type></rdf:Description>\n"
                + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_Restriction_001";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "This test shows the syntax for using the same restriction twice in OWL Lite.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_Restriction_002() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/Restriction/inconsistent002\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:ObjectProperty rdf:ID=\"op\"/>\n"
                + "   <rdf:Description rdf:ID=\"a\">\n"
                + "     <rdf:type><owl:Restriction rdf:nodeID=\"r\"><owl:onProperty rdf:resource=\"#op\"/><owl:someValuesFrom rdf:resource=\n"
                + "     \"http://www.w3.org/2002/07/owl#Nothing\" /></owl:Restriction></rdf:type></rdf:Description>\n"
                + "   <rdf:Description rdf:ID=\"b\">\n"
                + "     <rdf:type rdf:nodeID=\"r\"/>\n"
                + "   </rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_Restriction_002";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "This test shows syntax that was not permitted in OWL Lite or OWL DL for using the same restriction twice, but is permitted in OWL 2 DL.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_Restriction_003() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/Restriction/consistent003#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/Restriction/consistent003\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:DatatypeProperty rdf:ID=\"dp\"/>\n"
                + "   <owl:Class rdf:ID=\"C\">\n"
                + "     <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"superC\"/><owl:Restriction rdf:nodeID=\"r\"><owl:onProperty rdf:resource=\"#dp\"/><owl:someValuesFrom rdf:resource=\n"
                + "     \"http://www.w3.org/2001/XMLSchema#byte\" /></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + "   <owl:Class rdf:ID=\"D\">\n"
                + "     <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"superD\"/><rdf:Description rdf:nodeID=\"r\"/></owl:intersectionOf></owl:Class>\n"
                + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_Restriction_003";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "This test shows syntax that was not permitted in OWL Lite or OWL DL for using the same restriction twice, but is permitted in OWL 2 DL.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_Restriction_004() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/Restriction/consistent004\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:DatatypeProperty rdf:ID=\"dp\"/>\n"
                + "   <owl:Class rdf:ID=\"C\">\n"
                + "     <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"superC\"/><owl:Restriction><owl:onProperty rdf:resource=\"#dp\"/><owl:someValuesFrom rdf:resource=\n"
                + "     \"http://www.w3.org/2001/XMLSchema#byte\" /></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + "   <owl:Class rdf:ID=\"D\">\n"
                + "     <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"superD\"/><owl:Restriction><owl:onProperty rdf:resource=\"#dp\"/><owl:someValuesFrom rdf:resource=\n"
                + "     \"http://www.w3.org/2001/XMLSchema#byte\" /></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_Restriction_004";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "This test shows OWL Lite syntax for using two equivalent restrictions.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_SymmetricProperty_002() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/SymmetricProperty/premises002#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/SymmetricProperty/premises002\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:InverseFunctionalProperty rdf:about=\"#equalityOnA\"><rdfs:range><owl:Class rdf:ID=\"A\"><owl:oneOf rdf:parseType=\"Collection\"><owl:Thing rdf:ID=\"a\"/><owl:Thing rdf:ID=\"b\"/></owl:oneOf></owl:Class></rdfs:range></owl:InverseFunctionalProperty>\n"
                + "    <owl:Thing rdf:about=\"#a\"><first:equalityOnA rdf:resource=\"#a\"/></owl:Thing>\n"
                + "    <owl:Thing rdf:about=\"#b\"><first:equalityOnA rdf:resource=\"#b\"/></owl:Thing>\n"
                + "    <owl:Thing rdf:ID=\"c\"/>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/SymmetricProperty/premises002#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/SymmetricProperty/conclusions002\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:SymmetricProperty rdf:about=\"premises002#equalityOnA\">\n"
                + "     <rdfs:domain><owl:Class><owl:oneOf rdf:parseType=\"Collection\"><owl:Thing rdf:about=\"premises002#a\"/><owl:Thing rdf:about=\"premises002#b\"/><owl:Thing rdf:about=\"premises002#c\"/></owl:oneOf></owl:Class></rdfs:domain></owl:SymmetricProperty>\n"
                + "   <owl:Thing rdf:about=\"premises002#a\">\n"
                + "     <first:equalityOnA rdf:resource=\"premises002#a\"/></owl:Thing></rdf:RDF>";
        String id = "WebOnt_SymmetricProperty_002";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Test illustrating extensional semantics of owl:SymmetricProperty.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_SymmetricProperty_003() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/SymmetricProperty/premises003#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/SymmetricProperty/premises003\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Thing rdf:about=\"premises003#Ghent\"><first:path><owl:Thing rdf:about=\"premises003#Antwerp\"/></first:path></owl:Thing>\n"
                + "    <owl:SymmetricProperty rdf:about=\"premises003#path\"/></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/SymmetricProperty/premises003#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/SymmetricProperty/conclusions003\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Thing rdf:about=\"premises003#Antwerp\"><first:path><owl:Thing rdf:about=\"premises003#Ghent\"/></first:path></owl:Thing>\n"
                + "    <owl:ObjectProperty rdf:about=\"premises003#path\"/></rdf:RDF>";
        String id = "WebOnt_SymmetricProperty_003";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "A Lite version of test <a xmlns=\"http://www.w3.org/1999/xhtml\" href=\"#SymmetricProperty-001\">001</a>.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_Thing_003() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/Thing/inconsistent003\" >\n"
                + "  <owl:Ontology/>\n"
                + "  <owl:Class rdf:about=\"http://www.w3.org/2002/07/owl#Thing\">\n"
                + "    <owl:equivalentClass rdf:resource\n"
                + "       =\"http://www.w3.org/2002/07/owl#Nothing\"/></owl:Class></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_Thing_003";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "The extension of OWL Thing may not be empty.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_Thing_004() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/Thing/consistent004\" >\n"
                + "  <owl:Ontology/>\n"
                + "  <owl:Class rdf:about=\"http://www.w3.org/2002/07/owl#Thing\"><owl:oneOf rdf:parseType=\"Collection\"><owl:Thing rdf:about=\"#s\"/></owl:oneOf></owl:Class></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_Thing_004";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "The extension of OWL Thing may be a singleton in OWL DL.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_TransitiveProperty_002() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/TransitiveProperty/premises002#\" xmlns:second=\"http://www.w3.org/2002/03owlt/TransitiveProperty/conclusions002#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/TransitiveProperty/premises002\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:SymmetricProperty rdf:ID=\"symProp\">\n"
                + "     <rdfs:range><owl:Class><owl:oneOf rdf:parseType=\"Collection\"><owl:Thing rdf:ID=\"a\"/><owl:Thing rdf:ID=\"b\"/></owl:oneOf></owl:Class></rdfs:range></owl:SymmetricProperty>\n"
                + "   <owl:Thing rdf:about=\"#a\">\n"
                + "     <first:symProp rdf:resource=\"#a\"/>\n"
                + "   </owl:Thing>\n"
                + "   <owl:Thing rdf:about=\"#b\">\n"
                + "     <first:symProp rdf:resource=\"#b\"/>\n"
                + "   </owl:Thing></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/TransitiveProperty/premises002#\" xmlns:second=\"http://www.w3.org/2002/03owlt/TransitiveProperty/conclusions002#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/TransitiveProperty/conclusions002\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:TransitiveProperty rdf:about=\"premises002#symProp\"/>\n"
                + "   <rdf:Description rdf:about=\"premises002#a\">\n"
                + "     <rdf:type><owl:Restriction><rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#Class\"/><owl:onProperty rdf:resource=\"premises002#symProp\"/><owl:someValuesFrom  rdf:resource=\"http://www.w3.org/2002/07/owl#Thing\"/></owl:Restriction></rdf:type></rdf:Description>\n"
                + "</rdf:RDF>";
        String id = "WebOnt_TransitiveProperty_002";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Test illustrating extensional semantics of owl:TransitiveProperty.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_backwardCompatibleWith_002() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/backwardCompatibleWith/consistent002#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/backwardCompatibleWith/consistent002\" >\n"
                + "   <rdf:Description><owl:backwardCompatibleWith><owl:Ontology rdf:about=\"http://www.example.org/\"/></owl:backwardCompatibleWith></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_backwardCompatibleWith_002";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "In OWL Lite and DL the subject and object of a triple with predicate owl:backwardCompatibleWith must both be explicitly typed as owl:Ontology.  In OWL 2, this RDF graph parses to a single ontology with URI http://www.example.org/ and an annotation assertion between a blank node and that URI.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_cardinality_001() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/cardinality/premises001\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:ID=\"c\"><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#p\"/><owl:cardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:cardinality></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:ObjectProperty rdf:ID=\"p\"/>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/cardinality/conclusions001\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:about=\"premises001#c\"><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"premises001#p\"/><owl:maxCardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"premises001#p\"/><owl:minCardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:minCardinality></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:ObjectProperty rdf:about=\"premises001#p\"/></rdf:RDF>";
        String id = "WebOnt_cardinality_001";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "An owl:cardinality constraint is simply shorthand for a pair of owl:minCardinality and owl:maxCardinality constraints.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_cardinality_002() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/cardinality/premises002\" >    \n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:about=\"conclusions002#c\"><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"conclusions002#p\"/><owl:maxCardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"conclusions002#p\"/><owl:minCardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:minCardinality></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + "     <owl:ObjectProperty rdf:about=\"conclusions002#p\"/></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/cardinality/conclusions002\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:ID=\"c\"><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#p\"/><owl:cardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:cardinality></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:ObjectProperty rdf:ID=\"p\"/>\n" + "</rdf:RDF>";
        String id = "WebOnt_cardinality_002";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "An owl:cardinality constraint is simply shorthand for a pair of owl:minCardinality and owl:maxCardinality constraints.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_cardinality_003() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/cardinality/premises003\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:ID=\"c\"><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#p\"/><owl:cardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">2</owl:cardinality></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:ObjectProperty rdf:ID=\"p\"/>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/cardinality/conclusions003\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:about=\"premises003#c\"><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"premises003#p\"/><owl:maxCardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">2</owl:maxCardinality></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"premises003#p\"/><owl:minCardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">2</owl:minCardinality></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:ObjectProperty rdf:about=\"premises003#p\"/></rdf:RDF>";
        String id = "WebOnt_cardinality_003";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "An owl:cardinality constraint is simply shorthand for a pair of owl:minCardinality and owl:maxCardinality constraints.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_cardinality_004() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/cardinality/premises004\" >    \n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:about=\"conclusions004#c\"><rdfs:subClassOf><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty rdf:resource=\"conclusions004#p\"/><owl:maxCardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">2</owl:maxCardinality></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"conclusions004#p\"/><owl:minCardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">2</owl:minCardinality></owl:Restriction></owl:intersectionOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "     <owl:ObjectProperty rdf:about=\"conclusions004#p\"/></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/cardinality/conclusions004\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:ID=\"c\"><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#p\"/><owl:cardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">2</owl:cardinality></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + "     <owl:ObjectProperty rdf:ID=\"p\"/>\n" + "</rdf:RDF>";
        String id = "WebOnt_cardinality_004";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "An owl:cardinality constraint is simply shorthand for a pair of owl:minCardinality and owl:maxCardinality constraints.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_001() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent001\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\"><owl:equivalentClass><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#a\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#b\"/></owl:intersectionOf></owl:Class><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#a\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/></owl:intersectionOf></owl:Class><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#b\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/></owl:intersectionOf></owl:Class></owl:unionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#a\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#b\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/></owl:unionOf></owl:Class></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#b\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Unsatisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_001";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: fact1.1\n" + "If a, b and c are disjoint, then:\n"
                + "(a and b) or (b and c) or (c and a)\n" + "is unsatisfiable.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_002() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent002\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\"><rdfs:subClassOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/></rdfs:subClassOf><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:allValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:allValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/></owl:allValuesFrom><rdfs:subClassOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></rdfs:subClassOf></owl:Restriction>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Unsatisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_002";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: fact2.1";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_003() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent003\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f1\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f2\"/><owl:someValuesFrom><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/></owl:complementOf></owl:Class></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f3\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"/></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f1\"/>\n"
                + "    <owl:FunctionalProperty rdf:about=\"http://oiled.man.example.net/test#f1\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f2\"/>\n"
                + "    <owl:FunctionalProperty rdf:about=\"http://oiled.man.example.net/test#f2\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f3\"><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#f2\"/><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#f1\"/></owl:ObjectProperty>\n"
                + "    <owl:FunctionalProperty rdf:about=\"http://oiled.man.example.net/test#f3\"/>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Unsatisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_003";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: fact3.1";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_004() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent004\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#rx3\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#c1\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#c2\"/></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction></owl:complementOf></owl:Class><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#rx3\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#c1\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#rx4\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#c2\"/></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#c1\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#c2\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx1\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx2\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx3\"><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#rx1\"/><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#rx\"/></owl:ObjectProperty>\n"
                + "    <owl:FunctionalProperty rdf:about=\"http://oiled.man.example.net/test#rx3\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx4\"><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#rx2\"/><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#rx\"/></owl:ObjectProperty>\n"
                + "    <owl:FunctionalProperty rdf:about=\"http://oiled.man.example.net/test#rx4\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rxa\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx1a\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx2a\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx3a\"><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#rx1a\"/><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#rxa\"/></owl:ObjectProperty>\n"
                + "    <owl:FunctionalProperty rdf:about=\"http://oiled.man.example.net/test#rx3a\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx4a\"><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#rx2a\"/><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#rxa\"/></owl:ObjectProperty>\n"
                + "    <owl:FunctionalProperty rdf:about=\"http://oiled.man.example.net/test#rx4a\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx\"/>\n"
                + "    <owl:FunctionalProperty rdf:about=\"http://oiled.man.example.net/test#rx\"/>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Unsatisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_004";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: fact4.1";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_005() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/consistent005\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Satisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#rx3a\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#c1\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#c2\"/></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction></owl:complementOf></owl:Class><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#rx3a\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#c1\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#rx4a\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#c2\"/></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#c1\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#c2\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx1\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx2\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx3\"><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#rx1\"/><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#rx\"/></owl:ObjectProperty>\n"
                + "    <owl:FunctionalProperty rdf:about=\"http://oiled.man.example.net/test#rx3\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx4\"><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#rx2\"/><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#rx\"/></owl:ObjectProperty>\n"
                + "    <owl:FunctionalProperty rdf:about=\"http://oiled.man.example.net/test#rx4\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rxa\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx1a\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx2a\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx3a\"><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#rx1a\"/><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#rxa\"/></owl:ObjectProperty>\n"
                + "    <owl:FunctionalProperty rdf:about=\"http://oiled.man.example.net/test#rx3a\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx4a\"><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#rx2a\"/><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#rxa\"/></owl:ObjectProperty>\n"
                + "    <owl:FunctionalProperty rdf:about=\"http://oiled.man.example.net/test#rx4a\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx\"/>\n"
                + "    <owl:FunctionalProperty rdf:about=\"http://oiled.man.example.net/test#rx\"/>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Satisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_005";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "DL Test: fact4.2";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_006() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/consistent006\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Satisfiable\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#invR\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:maxCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p3\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/></owl:unionOf></owl:Class></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p3\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/></owl:unionOf></owl:Class></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p3\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/></owl:unionOf></owl:Class></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Satisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_006";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "DL Test: t1.1";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_007() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent007\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p3\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:maxCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">2</owl:maxCardinality></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p3\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/></owl:unionOf></owl:Class></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p3\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/></owl:unionOf></owl:Class></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p3\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/></owl:unionOf></owl:Class></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Unsatisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_007";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: t1.2";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_008() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent008\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"/><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#invR\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:maxCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p3\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/></owl:unionOf></owl:Class></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p3\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/></owl:unionOf></owl:Class></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p3\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/></owl:unionOf></owl:Class></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Unsatisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_008";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: t1.3";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_009() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/consistent009\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Satisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f1\"/><owl:someValuesFrom><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:complementOf></owl:Class></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#f\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invS\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#s\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF1\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#f1\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\"/>\n"
                + "    <owl:FunctionalProperty rdf:about=\"http://oiled.man.example.net/test#f\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#s\"><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#f\"/><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#f1\"/></owl:ObjectProperty>\n"
                + "    <owl:FunctionalProperty rdf:about=\"http://oiled.man.example.net/test#s\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f1\"/>\n"
                + "    <owl:FunctionalProperty rdf:about=\"http://oiled.man.example.net/test#f1\"/>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Satisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_009";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "DL Test: t10.1";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_010() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent010\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:complementOf></owl:Class><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#invS\"/><owl:allValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:allValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#invF\"/><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#s\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:someValuesFrom></owl:Restriction></owl:allValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#f\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invS\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#s\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF1\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#f1\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\"/>\n"
                + "    <owl:FunctionalProperty rdf:about=\"http://oiled.man.example.net/test#f\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#s\"><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#f\"/><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#f1\"/></owl:ObjectProperty>\n"
                + "    <owl:FunctionalProperty rdf:about=\"http://oiled.man.example.net/test#s\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f1\"/>\n"
                + "    <owl:FunctionalProperty rdf:about=\"http://oiled.man.example.net/test#f1\"/>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Unsatisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_010";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: t10.2";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_011() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent011\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#s\"/><owl:allValuesFrom><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:complementOf></owl:Class></owl:allValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#s\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#invS\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#f\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invS\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#s\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF1\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#f1\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\"/>\n"
                + "    <owl:FunctionalProperty rdf:about=\"http://oiled.man.example.net/test#f\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#s\"><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#f\"/><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#f1\"/></owl:ObjectProperty>\n"
                + "    <owl:FunctionalProperty rdf:about=\"http://oiled.man.example.net/test#s\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f1\"/>\n"
                + "    <owl:FunctionalProperty rdf:about=\"http://oiled.man.example.net/test#f1\"/>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Unsatisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_011";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: t10.3";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_012() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent012\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#s\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://www.w3.org/2002/07/owl#Thing\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f1\"/><owl:someValuesFrom><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:complementOf></owl:Class></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#f\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invS\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#s\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF1\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#f1\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\"/>\n"
                + "    <owl:FunctionalProperty rdf:about=\"http://oiled.man.example.net/test#f\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#s\"><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#f\"/><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#f1\"/></owl:ObjectProperty>\n"
                + "    <owl:FunctionalProperty rdf:about=\"http://oiled.man.example.net/test#s\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f1\"/>\n"
                + "    <owl:FunctionalProperty rdf:about=\"http://oiled.man.example.net/test#f1\"/>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Unsatisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_012";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: t10.4";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_013() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent013\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f1\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:complementOf></owl:Class><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#invF1\"/><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#s\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://www.w3.org/2002/07/owl#Thing\"/></owl:someValuesFrom></owl:Restriction></owl:allValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#f\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invS\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#s\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF1\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#f1\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\"/>\n"
                + "    <owl:FunctionalProperty rdf:about=\"http://oiled.man.example.net/test#f\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#s\"><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#f\"/><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#f1\"/></owl:ObjectProperty>\n"
                + "    <owl:FunctionalProperty rdf:about=\"http://oiled.man.example.net/test#s\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f1\"/>\n"
                + "    <owl:FunctionalProperty rdf:about=\"http://oiled.man.example.net/test#f1\"/>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Unsatisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_013";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: t10.5";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_014() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent014\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:complementOf></owl:Class><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:maxCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#invS\"/><owl:allValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:allValuesFrom></owl:Restriction></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#s\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invS\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#s\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#s\"><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Unsatisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_014";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: t11.1";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_015() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent015\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#s\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:complementOf></owl:Class><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#q\"/></owl:complementOf></owl:Class></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#invR\"/><owl:maxCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#invR\"/><owl:someValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#s\"/><owl:allValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:allValuesFrom></owl:Restriction></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#q\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#s\"/>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Unsatisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_015";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: t12.1";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_016() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/consistent016\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Satisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f1\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f2\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"/></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"/></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#f2\"/><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#f1\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f1\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f2\"/>\n"
                + "<rdf:Description rdf:about='http://www.w3.org/2002/07/owl#Thing' ><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f1\"/><owl:maxCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction></rdfs:subClassOf></rdf:Description>\n"
                + "<rdf:Description rdf:about='http://www.w3.org/2002/07/owl#Thing' ><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f2\"/><owl:maxCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction></rdfs:subClassOf></rdf:Description>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Satisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_016";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "DL Test: t2.1";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_017() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent017\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f1\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f2\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://www.w3.org/2002/07/owl#Thing\"/></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"/></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#f2\"/><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#f1\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f1\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f2\"/>\n"
                + "<rdf:Description rdf:about='http://www.w3.org/2002/07/owl#Thing' ><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f1\"/><owl:maxCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction></rdfs:subClassOf></rdf:Description>\n"
                + "<rdf:Description rdf:about='http://www.w3.org/2002/07/owl#Thing' ><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f2\"/><owl:maxCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction></rdfs:subClassOf></rdf:Description>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Unsatisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_017";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: t2.2";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_018() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/consistent018\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Satisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p3\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p3\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:maxCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">3</owl:maxCardinality></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p3\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/></owl:unionOf></owl:Class></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p3\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/></owl:unionOf></owl:Class></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p3\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/></owl:unionOf></owl:Class></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Satisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_018";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "DL Test: t3.1\n"
                + "There are 90 possible partitions in the satisfiable case";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_019() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent019\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p3\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p3\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:maxCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">3</owl:maxCardinality></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"/></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p3\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/></owl:unionOf></owl:Class></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p3\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/></owl:unionOf></owl:Class></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p3\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/></owl:unionOf></owl:Class></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Unsatisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_019";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: t3.2\n"
                + "There are 301 possible partitions in the unsatisfiable case";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_020() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/consistent020\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Satisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p3\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p3\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:maxCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">4</owl:maxCardinality></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p3\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/></owl:unionOf></owl:Class></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p3\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/></owl:unionOf></owl:Class></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p3\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/></owl:unionOf></owl:Class></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Satisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_020";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "DL Test: t3a.1\n"
                + "there are 1,701 possible partitions in the satisfiable case";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_021() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/consistent021\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Satisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p3\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p3\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:maxCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">5</owl:maxCardinality></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p3\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/></owl:unionOf></owl:Class></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p3\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/></owl:unionOf></owl:Class></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p3\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/></owl:unionOf></owl:Class></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Satisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_021";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "DL Test: t3a.2\n"
                + "There are 7,770 possible partitions in the unsatisfiable case";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_022() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent022\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p3\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p3\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:maxCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">4</owl:maxCardinality></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p3\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/></owl:unionOf></owl:Class></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p3\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/></owl:unionOf></owl:Class></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p3\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/></owl:unionOf></owl:Class></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Unsatisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_022";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: t3a.3\n"
                + "There are 42,525 possible partitions in the satisfiable case";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_023() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent023\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\"><rdfs:subClassOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#a\"/></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#s\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://www.w3.org/2002/07/owl#Thing\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#p\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://www.w3.org/2002/07/owl#Thing\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:allValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/></owl:allValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#p\"/><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://www.w3.org/2002/07/owl#Thing\"/></owl:someValuesFrom></owl:Restriction></owl:allValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#p\"/><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#p\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://www.w3.org/2002/07/owl#Thing\"/></owl:someValuesFrom></owl:Restriction></owl:allValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#p\"/><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:allValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/></owl:allValuesFrom></owl:Restriction></owl:allValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#a\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#invR\"/><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#invP\"/><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#invS\"/><owl:allValuesFrom><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#a\"/></owl:complementOf></owl:Class></owl:allValuesFrom></owl:Restriction></owl:allValuesFrom></owl:Restriction></owl:allValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invP\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#p\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invS\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#s\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#p\"/>\n"
                + "    <owl:TransitiveProperty rdf:about=\"http://oiled.man.example.net/test#p\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#s\"/>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Unsatisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_023";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: t4.1\n" + "Dynamic blocking example";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_024() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/consistent024\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Satisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#a\"/></owl:complementOf></owl:Class><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#invF\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#a\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#invR\"/><owl:someValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#invF\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#a\"/></owl:someValuesFrom></owl:Restriction></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#a\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#f\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\"><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <owl:TransitiveProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "<rdf:Description rdf:about='http://www.w3.org/2002/07/owl#Thing' ><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f\"/><owl:maxCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction></rdfs:subClassOf></rdf:Description>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Satisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_024";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "DL Test: t5.1\n" + "Non-finite model example from paper\n"
                + "The concept should be coherent but has no finite model";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_025() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/consistent025\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Satisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#a\"/></owl:complementOf></owl:Class><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#invF\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#a\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#invR\"/><owl:someValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#invF\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#a\"/></owl:someValuesFrom></owl:Restriction></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#a\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#f\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\"><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + "    <owl:FunctionalProperty rdf:about=\"http://oiled.man.example.net/test#f\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <owl:TransitiveProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Satisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_025";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "DL Test: t5f.1\n" + "Non-finite model example from paper\n"
                + "The concept should be coherent but has no finite model";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_026() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent026\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/></owl:complementOf></owl:Class><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#invF\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#invR\"/><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#invF\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></owl:someValuesFrom></owl:Restriction></owl:allValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f\"/><owl:someValuesFrom><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/></owl:complementOf></owl:Class></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#f\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\"><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <owl:TransitiveProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "<rdf:Description rdf:about='http://www.w3.org/2002/07/owl#Thing' ><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f\"/><owl:maxCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction></rdfs:subClassOf></rdf:Description>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Unsatisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_026";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: t6.1\n" + "Double blocking example.\n"
                + "The concept should be incoherent but needs double blocking";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_027() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent027\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/></owl:complementOf></owl:Class><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#invF\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#invR\"/><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#invF\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></owl:someValuesFrom></owl:Restriction></owl:allValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f\"/><owl:someValuesFrom><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/></owl:complementOf></owl:Class></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#f\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\"><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + "    <owl:FunctionalProperty rdf:about=\"http://oiled.man.example.net/test#f\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <owl:TransitiveProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Unsatisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_027";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: t6f.1\n" + "Double blocking example.\n"
                + "The concept should be incoherent but needs double blocking";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_028() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/consistent028\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Satisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#invR\"/><owl:allValuesFrom><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/></owl:complementOf></owl:Class><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:allValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/></owl:allValuesFrom></owl:Restriction></owl:unionOf></owl:Class></owl:allValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#f\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <owl:TransitiveProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "<rdf:Description rdf:about='http://www.w3.org/2002/07/owl#Thing' ><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f\"/><owl:maxCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction></rdfs:subClassOf></rdf:Description>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Satisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_028";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "DL Test: t7.1";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_029() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent029\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#invR\"/><owl:allValuesFrom><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/></owl:complementOf></owl:Class></owl:allValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#f\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <owl:TransitiveProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "<rdf:Description rdf:about='http://www.w3.org/2002/07/owl#Thing' ><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f\"/><owl:maxCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction></rdfs:subClassOf></rdf:Description>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Unsatisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_029";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: t7.2";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_030() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent030\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#invF\"/><owl:someValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f\"/><owl:someValuesFrom><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/></owl:complementOf></owl:Class></owl:someValuesFrom></owl:Restriction></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#f\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <owl:TransitiveProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "<rdf:Description rdf:about='http://www.w3.org/2002/07/owl#Thing' ><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f\"/><owl:maxCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction></rdfs:subClassOf></rdf:Description>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Unsatisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_030";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: t7.3";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_031() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/consistent031\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Satisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#invR\"/><owl:allValuesFrom><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/></owl:complementOf></owl:Class><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:allValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/></owl:allValuesFrom></owl:Restriction></owl:unionOf></owl:Class></owl:allValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#f\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\"/>\n"
                + "    <owl:FunctionalProperty rdf:about=\"http://oiled.man.example.net/test#f\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <owl:TransitiveProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Satisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_031";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "DL Test: t7f.1";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_032() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent032\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#invR\"/><owl:allValuesFrom><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/></owl:complementOf></owl:Class></owl:allValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#f\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\"/>\n"
                + "    <owl:FunctionalProperty rdf:about=\"http://oiled.man.example.net/test#f\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <owl:TransitiveProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Unsatisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_032";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: t7f.2";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_033() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent033\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#invF\"/><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f\"/><owl:someValuesFrom><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/></owl:complementOf></owl:Class></owl:someValuesFrom></owl:Restriction></owl:allValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#f\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\"/>\n"
                + "    <owl:FunctionalProperty rdf:about=\"http://oiled.man.example.net/test#f\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <owl:TransitiveProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Unsatisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_033";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: t7f.3";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_034() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/consistent034\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Satisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#invR\"/><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r1\"/><owl:allValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:allValuesFrom></owl:Restriction></owl:allValuesFrom></owl:Restriction></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#invR\"/><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r1\"/><owl:allValuesFrom><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:complementOf></owl:Class></owl:allValuesFrom></owl:Restriction></owl:allValuesFrom></owl:Restriction></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\"><owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r1\"/>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Satisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_034";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "DL Test: t8.1";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_035() {
        String premise = "<rdf:RDF  xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" \n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent035\" xmlns:oiled=\"http://oiled.man.example.net/test#\">\n"
                + "<owl:Ontology rdf:about=\"\">\n"
                + "  <rdfs:comment>An ontology illustrating the use of a spy point that\n"
                + "limits the cardinality of the interpretation domain to having only two\n"
                + "objects.</rdfs:comment> </owl:Ontology>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\">\n"
                + " <rdfs:subClassOf>\n"
                + "  <owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <owl:minCardinality\n"
                + "rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">3</owl:minCardinality></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + "<owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#p\">\n"
                + " <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#invP\"/></owl:ObjectProperty>\n"
                + "<owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invP\"/>\n"
                + "<owl:Class rdf:about=\"http://www.w3.org/2002/07/owl#Thing\">\n"
                + " <rdfs:subClassOf> \n"
                + "  <owl:Restriction>\n"
                + "   <owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#p\"/>\n"
                + "   <owl:someValuesFrom>\n"
                + "    <owl:Class>\n"
                + "     <owl:oneOf rdf:parseType=\"Collection\"><owl:Thing rdf:about=\"http://oiled.man.example.net/test#spy\"/></owl:oneOf></owl:Class></owl:someValuesFrom></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + "<rdf:Description rdf:about=\"http://oiled.man.example.net/test#spy\">\n"
                + "  <rdf:type>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#invP\"/>\n"
                + "    <owl:maxCardinality\n"
                + "rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">2</owl:maxCardinality></owl:Restriction></rdf:type>\n"
                + "</rdf:Description>\n" + "<oiled:Unsatisfiable/></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_035";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "A test for the interaction of one-of and inverse using the idea of a spy point.\n"
                + "Everything is related to the spy via the property p and we know that the spy \n"
                + "has at most two invP successors, thus limiting the cardinality of the domain \n"
                + "to being at most 2.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_040() {
        String premise = "<rdf:RDF xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:eg=\"http://example.org/factkb#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" \n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent040\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#A\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#B\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#A0\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#A1\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#A2\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#A3\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#A4\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#A5\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#A6\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#A7\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#A8\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#A9\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#B0\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#B1\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#B2\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#B3\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#B4\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#B5\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#B6\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#B7\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#B8\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#B9\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#C1\"><rdfs:subClassOf><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://example.org/factkb#A0\"/><owl:Class rdf:about=\"http://example.org/factkb#B0\"/></owl:unionOf></owl:Class><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://example.org/factkb#A1\"/><owl:Class rdf:about=\"http://example.org/factkb#B1\"/></owl:unionOf></owl:Class><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://example.org/factkb#A2\"/><owl:Class rdf:about=\"http://example.org/factkb#B2\"/></owl:unionOf></owl:Class><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://example.org/factkb#A3\"/><owl:Class rdf:about=\"http://example.org/factkb#B3\"/></owl:unionOf></owl:Class><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://example.org/factkb#A4\"/><owl:Class rdf:about=\"http://example.org/factkb#B4\"/></owl:unionOf></owl:Class><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://example.org/factkb#A5\"/><owl:Class rdf:about=\"http://example.org/factkb#B5\"/></owl:unionOf></owl:Class><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://example.org/factkb#A6\"/><owl:Class rdf:about=\"http://example.org/factkb#B6\"/></owl:unionOf></owl:Class><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://example.org/factkb#A7\"/><owl:Class rdf:about=\"http://example.org/factkb#B7\"/></owl:unionOf></owl:Class><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://example.org/factkb#A8\"/><owl:Class rdf:about=\"http://example.org/factkb#B8\"/></owl:unionOf></owl:Class><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://example.org/factkb#A9\"/><owl:Class rdf:about=\"http://example.org/factkb#B9\"/></owl:unionOf></owl:Class><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://example.org/factkb#A10\"/><owl:Class rdf:about=\"http://example.org/factkb#B10\"/></owl:unionOf></owl:Class><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://example.org/factkb#A11\"/><owl:Class rdf:about=\"http://example.org/factkb#B11\"/></owl:unionOf></owl:Class><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://example.org/factkb#A12\"/><owl:Class rdf:about=\"http://example.org/factkb#B12\"/></owl:unionOf></owl:Class><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://example.org/factkb#A13\"/><owl:Class rdf:about=\"http://example.org/factkb#B13\"/></owl:unionOf></owl:Class><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://example.org/factkb#A14\"/><owl:Class rdf:about=\"http://example.org/factkb#B14\"/></owl:unionOf></owl:Class><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://example.org/factkb#A15\"/><owl:Class rdf:about=\"http://example.org/factkb#B15\"/></owl:unionOf></owl:Class><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://example.org/factkb#A16\"/><owl:Class rdf:about=\"http://example.org/factkb#B16\"/></owl:unionOf></owl:Class><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://example.org/factkb#A17\"/><owl:Class rdf:about=\"http://example.org/factkb#B17\"/></owl:unionOf></owl:Class><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://example.org/factkb#A18\"/><owl:Class rdf:about=\"http://example.org/factkb#B18\"/></owl:unionOf></owl:Class><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://example.org/factkb#A19\"/><owl:Class rdf:about=\"http://example.org/factkb#B19\"/></owl:unionOf></owl:Class><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://example.org/factkb#A20\"/><owl:Class rdf:about=\"http://example.org/factkb#B20\"/></owl:unionOf></owl:Class><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://example.org/factkb#A21\"/><owl:Class rdf:about=\"http://example.org/factkb#B21\"/></owl:unionOf></owl:Class><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://example.org/factkb#A22\"/><owl:Class rdf:about=\"http://example.org/factkb#B22\"/></owl:unionOf></owl:Class><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://example.org/factkb#A23\"/><owl:Class rdf:about=\"http://example.org/factkb#B23\"/></owl:unionOf></owl:Class><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://example.org/factkb#A24\"/><owl:Class rdf:about=\"http://example.org/factkb#B24\"/></owl:unionOf></owl:Class><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://example.org/factkb#A25\"/><owl:Class rdf:about=\"http://example.org/factkb#B25\"/></owl:unionOf></owl:Class><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://example.org/factkb#A26\"/><owl:Class rdf:about=\"http://example.org/factkb#B26\"/></owl:unionOf></owl:Class><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://example.org/factkb#A27\"/><owl:Class rdf:about=\"http://example.org/factkb#B27\"/></owl:unionOf></owl:Class><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://example.org/factkb#A28\"/><owl:Class rdf:about=\"http://example.org/factkb#B28\"/></owl:unionOf></owl:Class><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://example.org/factkb#A29\"/><owl:Class rdf:about=\"http://example.org/factkb#B29\"/></owl:unionOf></owl:Class><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://example.org/factkb#A30\"/><owl:Class rdf:about=\"http://example.org/factkb#B30\"/></owl:unionOf></owl:Class><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://example.org/factkb#A31\"/><owl:Class rdf:about=\"http://example.org/factkb#B31\"/></owl:unionOf></owl:Class></owl:intersectionOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#C2\"><rdfs:subClassOf><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://example.org/factkb#A\"/><owl:Class rdf:about=\"http://example.org/factkb#B\"/></owl:unionOf></owl:Class><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://example.org/factkb#A\"/><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://example.org/factkb#B\"/></owl:complementOf></owl:Class></owl:unionOf></owl:Class></owl:intersectionOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#C3\"><rdfs:subClassOf><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://example.org/factkb#A\"/></owl:complementOf></owl:Class><owl:Class rdf:about=\"http://example.org/factkb#B\"/></owl:unionOf></owl:Class><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://example.org/factkb#A\"/></owl:complementOf></owl:Class><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://example.org/factkb#B\"/></owl:complementOf></owl:Class></owl:unionOf></owl:Class></owl:intersectionOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#C4\"><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"http://example.org/factkb#R\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://example.org/factkb#C2\"/></owl:someValuesFrom></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#C5\"><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"http://example.org/factkb#R\"/><owl:allValuesFrom><owl:Class rdf:about=\"http://example.org/factkb#C3\"/></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#A10\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#A11\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#A12\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#A13\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#A14\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#A15\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#A16\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#A17\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#A18\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#A19\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#A20\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#A21\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#A22\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#A23\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#A24\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#A25\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#A26\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#A27\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#A28\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#A29\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#A30\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#A31\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#B10\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#B11\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#B12\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#B13\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#B14\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#B15\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#B16\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#B17\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#B18\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#B19\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#B20\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#B21\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#B22\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#B23\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#B24\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#B25\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#B26\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#B27\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#B28\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#B29\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#B30\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#B31\"/>\n"
                + "    <owl:Class rdf:about=\"http://example.org/factkb#TEST\"><rdfs:subClassOf><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://example.org/factkb#C1\"/><owl:Class rdf:about=\"http://example.org/factkb#C4\"/><owl:Class rdf:about=\"http://example.org/factkb#C5\"/></owl:intersectionOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://example.org/factkb#R\"/>\n"
                + "    <eg:TEST/>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_040";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "This kind of pattern comes up a lot in more complex ontologies. \n"
                + "Failure to cope with this kind of pattern is one\n"
                + "of the reasons that many reasoners have been unable to \n"
                + "cope with such ontologies.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_101() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent101\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#f\"><rdfs:subClassOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#c1\"><rdfs:subClassOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#d1\"/></rdfs:subClassOf><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#d1\"/></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#d1\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#e3\"><rdfs:subClassOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Unsatisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_101";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: heinsohn1.1\n"
                + "Tbox tests from Heinsohn et al.\n"
                + "Tests incoherency caused by disjoint concept";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_102() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent102\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:allValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></owl:intersectionOf></owl:Class></owl:allValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://www.w3.org/2002/07/owl#Thing\"/></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#f\"><rdfs:subClassOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#c1\"><rdfs:subClassOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#d1\"/></rdfs:subClassOf><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#d1\"/></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#d1\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#e3\"><rdfs:subClassOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Unsatisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_102";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: heinsohn1.2\n"
                + "Tbox tests from Heinsohn et al.\n"
                + "Tests incoherency caused by disjoint concept";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_103() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent103\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#e3\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#f\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#f\"><rdfs:subClassOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#c1\"><rdfs:subClassOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#d1\"/></rdfs:subClassOf><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#d1\"/></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#d1\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#e3\"><rdfs:subClassOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Unsatisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_103";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: heinsohn1.3\n"
                + "Tbox tests from Heinsohn et al.\n"
                + "Tests incoherency caused by disjoint concept";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_104() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent104\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\"><owl:equivalentClass><owl:Class rdf:about=\"http://oiled.man.example.net/test#c1\"/></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#f\"><rdfs:subClassOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#c1\"><rdfs:subClassOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#d1\"/></rdfs:subClassOf><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#d1\"/></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#d1\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#e3\"><rdfs:subClassOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Unsatisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_104";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: heinsohn1.4\n"
                + "Tbox tests from Heinsohn et al.\n"
                + "Tests incoherency caused by disjoint concept";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_105() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent105\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:minCardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">2</owl:minCardinality></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:maxCardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Unsatisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_105";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: heinsohn2.1\n"
                + "Tbox tests from Heinsohn et al.\n"
                + "Tests incoherency caused by number restrictions";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_106() {
        String premise = "<rdf:RDF \n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent106\"  xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:maxCardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Unsatisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_106";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: heinsohn2.2\n"
                + "Tbox tests from Heinsohn et al.\n"
                + "Tests incoherency caused by number restrictions";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_107() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent107\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:minCardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">2</owl:minCardinality></owl:Restriction></owl:complementOf></owl:Class><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:minCardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:minCardinality></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#a\"><owl:equivalentClass><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></owl:unionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#e\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r1\"><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r2\"><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r3\"><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#t1\"><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#tt\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#t2\"><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#tt\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#t3\"><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#tt\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#tt\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"><owl:disjointWith><owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></owl:disjointWith></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"><owl:disjointWith><owl:Class rdf:about=\"http://oiled.man.example.net/test#e\"/></owl:disjointWith></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"><owl:disjointWith><owl:Class rdf:about=\"http://oiled.man.example.net/test#e\"/></owl:disjointWith></owl:Class>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Unsatisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_107";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: heinsohn3.1\n"
                + "Tbox tests from Heinsohn et al.\n"
                + "Tests incoherency caused by number restrictions and role hierarchy";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_108() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent108\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:minCardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">2</owl:minCardinality></owl:Restriction></owl:complementOf></owl:Class><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r1\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#tt\"/><owl:maxCardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#t1\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r2\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#tt\"/><owl:maxCardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#t2\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r3\"/><owl:someValuesFrom><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#tt\"/><owl:maxCardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#t3\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#e\"/></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#a\"><owl:equivalentClass><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></owl:unionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#e\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r1\"><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r2\"><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r3\"><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#t1\"><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#tt\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#t2\"><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#tt\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#t3\"><rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#tt\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#tt\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"><owl:disjointWith><owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></owl:disjointWith></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"><owl:disjointWith><owl:Class rdf:about=\"http://oiled.man.example.net/test#e\"/></owl:disjointWith></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"><owl:disjointWith><owl:Class rdf:about=\"http://oiled.man.example.net/test#e\"/></owl:disjointWith></owl:Class>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Unsatisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_108";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: heinsohn3.2\n"
                + "Tbox tests from Heinsohn et al.\n"
                + "Tests incoherency caused by number restrictions and role hierarchy";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_109() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent109\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#tt\"/><owl:allValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#a\"/></owl:allValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#tt\"/><owl:minCardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">3</owl:minCardinality></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#tt\"/><owl:maxCardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#tt\"/><owl:maxCardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#a\"><rdfs:subClassOf><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></owl:unionOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#tt\"/>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Unsatisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_109";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: heinsohn3c.1\n"
                + "Tbox tests from Heinsohn et al.\n"
                + "Tests incoherency caused by number restrictions and role hierarchy";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_110() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent110\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#e\"/></owl:complementOf></owl:Class></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:allValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></owl:allValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:allValuesFrom><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#e\"/><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></owl:complementOf></owl:Class></owl:unionOf></owl:Class></owl:allValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#e\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#s\"/>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Unsatisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_110";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: heinsohn4.1\n"
                + "Tbox tests from Heinsohn et al.\n"
                + "Tests role restrictions";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_111() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent111\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:someValuesFrom><owl:Class><owl:complementOf><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#s\"/><owl:maxCardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction></owl:complementOf></owl:Class></owl:someValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:allValuesFrom><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/><owl:Class><owl:complementOf><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#s\"/><owl:minCardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">2</owl:minCardinality></owl:Restriction></owl:complementOf></owl:Class></owl:unionOf></owl:Class></owl:allValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/><owl:allValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></owl:allValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"><rdfs:subClassOf><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></owl:complementOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#e\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#s\"/>\n"
                + "    <rdf:Description><rdf:type rdf:resource=\"http://oiled.man.example.net/test#Unsatisfiable\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_111";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: heinsohn4.2\n"
                + "Tbox tests from Heinsohn et al.\n"
                + "Tests role restrictions";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_201() {
        String premise = "<rdf:RDF\n"
                + "  xml:base=\"http://www.w3.org/2002/03owlt/description-logic/premises201\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "  <owl:Ontology rdf:about=\"\"/>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C10\"/>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C12\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/></owl:complementOf></owl:Class><owl:Class rdf:about=\"http://oiled.man.example.net/test#C10\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C14\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C8\"/></owl:complementOf></owl:Class><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C12\"/></owl:complementOf></owl:Class></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C16\"/>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C18\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C16\"/></owl:complementOf></owl:Class><owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C20\">\n"
                + "    <owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#C18\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C22\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C20\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C16\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C24\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C16\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C26\">\n"
                + "    <owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#C24\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C28\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C26\"/><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C16\"/></owl:complementOf></owl:Class></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C30\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C22\"/></owl:complementOf></owl:Class><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C28\"/></owl:complementOf></owl:Class></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C32\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C30\"/></owl:complementOf></owl:Class><owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C34\"/>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C36\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C34\"/></owl:complementOf></owl:Class><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C38\">\n"
                + "    <owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#C36\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C40\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C38\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C34\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C42\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C34\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C44\">\n"
                + "    <owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#C42\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C46\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C44\"/><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C34\"/></owl:complementOf></owl:Class></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C48\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C40\"/></owl:complementOf></owl:Class><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C46\"/></owl:complementOf></owl:Class></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C50\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C48\"/></owl:complementOf></owl:Class><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C52\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C32\"/></owl:complementOf></owl:Class><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C50\"/></owl:complementOf></owl:Class></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C54\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C14\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C52\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C56\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C10\"/></owl:complementOf></owl:Class></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C58\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C56\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C34\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C60\">\n"
                + "    <owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#C58\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C62\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C10\"/></owl:complementOf></owl:Class></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C64\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C62\"/><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C34\"/></owl:complementOf></owl:Class></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C66\">\n"
                + "    <owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#C64\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C68\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C60\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C66\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C70\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"/><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/></owl:complementOf></owl:Class></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C72\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C68\"/></owl:complementOf></owl:Class><owl:Class rdf:about=\"http://oiled.man.example.net/test#C70\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C74\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C54\"/><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C72\"/></owl:complementOf></owl:Class></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C76\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"/></owl:complementOf></owl:Class><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C78\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/></owl:complementOf></owl:Class><owl:Class rdf:about=\"http://oiled.man.example.net/test#C10\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C80\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C76\"/></owl:complementOf></owl:Class><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C78\"/></owl:complementOf></owl:Class></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C82\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C16\"/></owl:complementOf></owl:Class><owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C84\">\n"
                + "    <owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#C82\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C86\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C84\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C16\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C88\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C16\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C90\">\n"
                + "    <owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#C88\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C92\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C90\"/><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C16\"/></owl:complementOf></owl:Class></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C94\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C86\"/></owl:complementOf></owl:Class><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C92\"/></owl:complementOf></owl:Class></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C96\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C94\"/></owl:complementOf></owl:Class><owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C98\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C34\"/></owl:complementOf></owl:Class><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"/>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C6\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"/><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/></owl:complementOf></owl:Class></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C8\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"/></owl:complementOf></owl:Class><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C100\">\n"
                + "    <owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#C98\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C102\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C100\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C34\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C104\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C34\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C106\">\n"
                + "    <owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#C104\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C108\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C106\"/><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C34\"/></owl:complementOf></owl:Class></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C110\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C102\"/></owl:complementOf></owl:Class><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C108\"/></owl:complementOf></owl:Class></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C112\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C110\"/></owl:complementOf></owl:Class><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C114\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C96\"/></owl:complementOf></owl:Class><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C112\"/></owl:complementOf></owl:Class></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C116\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C80\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C114\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C118\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C10\"/></owl:complementOf></owl:Class></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C120\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C118\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C34\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C122\">\n"
                + "    <owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#C120\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C124\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C10\"/></owl:complementOf></owl:Class></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C126\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C124\"/><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C34\"/></owl:complementOf></owl:Class></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C128\">\n"
                + "    <owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#C126\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C130\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C122\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C128\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C132\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"/><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/></owl:complementOf></owl:Class></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C134\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C130\"/></owl:complementOf></owl:Class><owl:Class rdf:about=\"http://oiled.man.example.net/test#C132\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C136\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C116\"/><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C134\"/></owl:complementOf></owl:Class></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C138\">\n"
                + "    <owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:someValuesFrom><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C136\"/></owl:complementOf></owl:Class></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#C140\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C74\"/><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C138\"/></owl:complementOf></owl:Class></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://oiled.man.example.net/test#TEST\">\n"
                + "    <owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C6\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C140\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#R1\"/>\n"
                + "  <owl:Thing rdf:about=\"http://oiled.man.example.net/test#V822576\">\n"
                + "    <rdf:type><owl:Class rdf:about=\"http://oiled.man.example.net/test#C16\"/></rdf:type>\n"
                + "    <rdf:type><owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"/></rdf:type>\n"
                + "    <rdf:type><owl:Class rdf:about=\"http://oiled.man.example.net/test#C34\"/></rdf:type>\n"
                + "    <rdf:type><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/></rdf:type>\n"
                + "    <rdf:type><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C132\"/></owl:complementOf></owl:Class></rdf:type>\n"
                + "    <rdf:type><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C100\"/></owl:complementOf></owl:Class></rdf:type>\n"
                + "    <rdf:type><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C108\"/></owl:complementOf></owl:Class></rdf:type>\n"
                + "    <rdf:type><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C102\"/></owl:complementOf></owl:Class></rdf:type>\n"
                + "    <rdf:type><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C90\"/></owl:complementOf></owl:Class></rdf:type>\n"
                + "    <rdf:type><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C84\"/></owl:complementOf></owl:Class></rdf:type>\n"
                + "    <rdf:type><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C92\"/></owl:complementOf></owl:Class></rdf:type>\n"
                + "    <rdf:type><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C86\"/></owl:complementOf></owl:Class></rdf:type>\n"
                + "    <rdf:type><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C18\"/></owl:complementOf></owl:Class></rdf:type>\n"
                + "    <rdf:type><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C78\"/></owl:complementOf></owl:Class></rdf:type>\n"
                + "    <rdf:type><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C96\"/></owl:complementOf></owl:Class></rdf:type>\n"
                + "    <rdf:type><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C76\"/></owl:complementOf></owl:Class></rdf:type>\n"
                + "    <rdf:type><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C134\"/></owl:complementOf></owl:Class></rdf:type>\n"
                + "    <rdf:type><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C10\"/></owl:complementOf></owl:Class></rdf:type>\n"
                + "    <rdf:type><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C112\"/></owl:complementOf></owl:Class></rdf:type>\n"
                + "    <rdf:type><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:allValuesFrom><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C98\"/></owl:complementOf></owl:Class></owl:allValuesFrom></owl:Restriction></rdf:type>\n"
                + "    <rdf:type><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:allValuesFrom><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C88\"/></owl:complementOf></owl:Class></owl:allValuesFrom></owl:Restriction></rdf:type>\n"
                + "    <rdf:type><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:allValuesFrom><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C82\"/></owl:complementOf></owl:Class></owl:allValuesFrom></owl:Restriction></rdf:type></owl:Thing>\n"
                + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "      xml:base=\"http://www.w3.org/2002/03owlt/description-logic/conclusions201\"\n"
                + ">\n"
                + "<owl:Ontology/>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V822576\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C110\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V822576\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C94\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V822576\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C136\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V822576\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C58\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V822576\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C80\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V822576\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C56\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V822576\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C116\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V822576\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C114\"/></rdf:type>\n"
                + "</owl:Thing>\n" + "</rdf:RDF>";
        String id = "WebOnt_description_logic_201";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "DL Test: \n" + "ABox test from DL98 systems comparison.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_205() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/premises205\" xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#C10\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:someValuesFrom><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"/></owl:complementOf></owl:Class></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#C12\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"/></owl:complementOf></owl:Class><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C10\"/></owl:complementOf></owl:Class></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#C14\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#C12\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#C16\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C8\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C14\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#C18\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#TOP\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C16\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#TOP\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:someValuesFrom><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"/></owl:complementOf></owl:Class></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#C6\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"/></owl:complementOf></owl:Class><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/></owl:complementOf></owl:Class></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#C8\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#C6\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#TEST\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C18\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#TOP\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#R1\"/>\n"
                + "    <owl:Thing rdf:about=\"http://oiled.man.example.net/test#V16560\"><rdf:type><owl:Class rdf:about=\"http://oiled.man.example.net/test#TEST\"/></rdf:type><rdf:type><owl:Class rdf:about=\"http://oiled.man.example.net/test#TOP\"/></rdf:type><oiled:R1 rdf:resource=\"http://oiled.man.example.net/test#V16562\"/><oiled:R1 rdf:resource=\"http://oiled.man.example.net/test#V16561\"/></owl:Thing>\n"
                + "    <owl:Thing rdf:about=\"http://oiled.man.example.net/test#V16561\"><rdf:type><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/></owl:complementOf></owl:Class></rdf:type><rdf:type><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"/></owl:complementOf></owl:Class></rdf:type><rdf:type><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:allValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"/></owl:allValuesFrom></owl:Restriction></rdf:type></owl:Thing>\n"
                + "    <owl:Thing rdf:about=\"http://oiled.man.example.net/test#V16562\"><rdf:type><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C10\"/></owl:complementOf></owl:Class></rdf:type><rdf:type><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"/></owl:complementOf></owl:Class></rdf:type><rdf:type><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:allValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"/></owl:allValuesFrom></owl:Restriction></rdf:type></owl:Thing></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "      xml:base=\"http://www.w3.org/2002/03owlt/description-logic/conclusions205\"\n"
                + ">\n"
                + "<owl:Ontology/>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V16560\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C18\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V16560\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C8\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V16560\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C16\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V16560\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C14\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V16561\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C6\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V16562\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C12\"/></rdf:type>\n"
                + "</owl:Thing>\n" + "</rdf:RDF>";
        String id = "WebOnt_description_logic_205";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "DL Test: k_lin\n"
                + "ABox test from DL98 systems comparison.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_207() {
        String premise = "<rdf:RDF\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/premises207\" xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n"
                + "    <owl:Ontology rdf:about=\"\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#C10\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#C12\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#C10\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#C6\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"/></owl:complementOf></owl:Class><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#C8\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#C6\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"http://oiled.man.example.net/test#TEST\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C8\"/></owl:complementOf></owl:Class><owl:Class rdf:about=\"http://oiled.man.example.net/test#C12\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#R1\"/>\n"
                + "    <owl:Thing rdf:about=\"http://oiled.man.example.net/test#V21080\"><rdf:type><owl:Class rdf:about=\"http://oiled.man.example.net/test#TEST\"/></rdf:type><rdf:type><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C8\"/></owl:complementOf></owl:Class></rdf:type><rdf:type><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:allValuesFrom><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C6\"/></owl:complementOf></owl:Class></owl:allValuesFrom></owl:Restriction></rdf:type><oiled:R1 rdf:resource=\"http://oiled.man.example.net/test#V21081\"/></owl:Thing>\n"
                + "    <owl:Thing rdf:about=\"http://oiled.man.example.net/test#V21081\"><rdf:type><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/></rdf:type><rdf:type><owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"/></rdf:type><rdf:type><owl:Class><owl:complementOf><owl:Class rdf:about=\"http://oiled.man.example.net/test#C6\"/></owl:complementOf></owl:Class></rdf:type></owl:Thing></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "      xml:base=\"http://www.w3.org/2002/03owlt/description-logic/conclusions207\"\n"
                + ">\n"
                + "<owl:Ontology/>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V21080\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C12\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V21081\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C10\"/></rdf:type>\n"
                + "</owl:Thing>\n" + "</rdf:RDF>";
        String id = "WebOnt_description_logic_207";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "DL Test: k_ph\n"
                + "ABox test from DL98 systems comparison.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_503() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/description-logic/consistent503#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/consistent503\" >\n"
                + " <owl:Ontology/>\n"
                + " <first:Test />\n"
                + " <owl:Class rdf:about='#plus1'>\n"
                + "  <owl:disjointWith>\n"
                + "   <owl:Class rdf:about='#minus1'/>\n"
                + "  </owl:disjointWith></owl:Class>\n"
                + " <owl:Class rdf:about='#plus2'>\n"
                + "  <owl:disjointWith>\n"
                + "   <owl:Class rdf:about='#minus2'/></owl:disjointWith></owl:Class>\n"
                + " <owl:Class rdf:about='#plus3'>\n"
                + "  <owl:disjointWith>\n"
                + "   <owl:Class rdf:about='#minus3'/></owl:disjointWith>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#plus4'>\n"
                + "  <owl:disjointWith>\n"
                + "   <owl:Class rdf:about='#minus4'/></owl:disjointWith></owl:Class>\n"
                + " <owl:Class rdf:about='#plus5'>\n"
                + "  <owl:disjointWith>\n"
                + "   <owl:Class rdf:about='#minus5'/></owl:disjointWith>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#plus6'>\n"
                + "  <owl:disjointWith>\n"
                + "   <owl:Class rdf:about='#minus6'/></owl:disjointWith></owl:Class>\n"
                + " <owl:Class rdf:about='#plus7'>\n"
                + "  <owl:disjointWith>\n"
                + "   <owl:Class rdf:about='#minus7'/></owl:disjointWith>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#plus8'>\n"
                + "  <owl:disjointWith>\n"
                + "   <owl:Class rdf:about='#minus8'/></owl:disjointWith></owl:Class>\n"
                + " <owl:Class rdf:about='#plus9'>\n"
                + "  <owl:disjointWith>\n"
                + "   <owl:Class rdf:about='#minus9'/></owl:disjointWith>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus7'/>\n"
                + "     <rdf:Description rdf:about='#minus9'/>\n"
                + "     <rdf:Description rdf:about='#minus8'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus1'/>\n"
                + "     <rdf:Description rdf:about='#plus2'/>\n"
                + "     <rdf:Description rdf:about='#minus8'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus4'/>\n"
                + "     <rdf:Description rdf:about='#plus7'/>\n"
                + "     <rdf:Description rdf:about='#minus5'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus2'/>\n"
                + "     <rdf:Description rdf:about='#plus3'/>\n"
                + "     <rdf:Description rdf:about='#minus1'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus1'/>\n"
                + "     <rdf:Description rdf:about='#plus5'/>\n"
                + "     <rdf:Description rdf:about='#plus8'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus8'/>\n"
                + "     <rdf:Description rdf:about='#minus6'/>\n"
                + "     <rdf:Description rdf:about='#minus3'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus3'/>\n"
                + "     <rdf:Description rdf:about='#minus8'/>\n"
                + "     <rdf:Description rdf:about='#plus7'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus3'/>\n"
                + "     <rdf:Description rdf:about='#plus6'/>\n"
                + "     <rdf:Description rdf:about='#plus8'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus4'/>\n"
                + "     <rdf:Description rdf:about='#minus6'/>\n"
                + "     <rdf:Description rdf:about='#plus8'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus6'/>\n"
                + "     <rdf:Description rdf:about='#plus7'/>\n"
                + "     <rdf:Description rdf:about='#plus3'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus3'/>\n"
                + "     <rdf:Description rdf:about='#plus6'/>\n"
                + "     <rdf:Description rdf:about='#minus9'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus5'/>\n"
                + "     <rdf:Description rdf:about='#minus2'/>\n"
                + "     <rdf:Description rdf:about='#plus3'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus5'/>\n"
                + "     <rdf:Description rdf:about='#plus8'/>\n"
                + "     <rdf:Description rdf:about='#plus2'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus2'/>\n"
                + "     <rdf:Description rdf:about='#minus7'/>\n"
                + "     <rdf:Description rdf:about='#minus3'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus6'/>\n"
                + "     <rdf:Description rdf:about='#minus8'/>\n"
                + "     <rdf:Description rdf:about='#minus5'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus2'/>\n"
                + "     <rdf:Description rdf:about='#plus7'/>\n"
                + "     <rdf:Description rdf:about='#minus3'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus9'/>\n"
                + "     <rdf:Description rdf:about='#minus1'/>\n"
                + "     <rdf:Description rdf:about='#minus2'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus1'/>\n"
                + "     <rdf:Description rdf:about='#plus7'/>\n"
                + "     <rdf:Description rdf:about='#minus6'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus1'/>\n"
                + "     <rdf:Description rdf:about='#plus9'/>\n"
                + "     <rdf:Description rdf:about='#minus3'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus8'/>\n"
                + "     <rdf:Description rdf:about='#minus9'/>\n"
                + "     <rdf:Description rdf:about='#minus2'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus9'/>\n"
                + "     <rdf:Description rdf:about='#minus8'/>\n"
                + "     <rdf:Description rdf:about='#plus2'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus5'/>\n"
                + "     <rdf:Description rdf:about='#plus8'/>\n"
                + "     <rdf:Description rdf:about='#plus4'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus7'/>\n"
                + "     <rdf:Description rdf:about='#plus2'/>\n"
                + "     <rdf:Description rdf:about='#plus5'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus1'/>\n"
                + "     <rdf:Description rdf:about='#plus7'/>\n"
                + "     <rdf:Description rdf:about='#minus4'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus7'/>\n"
                + "     <rdf:Description rdf:about='#minus8'/>\n"
                + "     <rdf:Description rdf:about='#plus4'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus3'/>\n"
                + "     <rdf:Description rdf:about='#plus2'/>\n"
                + "     <rdf:Description rdf:about='#minus6'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus1'/>\n"
                + "     <rdf:Description rdf:about='#minus2'/>\n"
                + "     <rdf:Description rdf:about='#minus9'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus7'/>\n"
                + "     <rdf:Description rdf:about='#plus3'/>\n"
                + "     <rdf:Description rdf:about='#minus2'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus7'/>\n"
                + "     <rdf:Description rdf:about='#plus8'/>\n"
                + "     <rdf:Description rdf:about='#plus4'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus1'/>\n"
                + "     <rdf:Description rdf:about='#minus7'/>\n"
                + "     <rdf:Description rdf:about='#minus5'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus5'/>\n"
                + "     <rdf:Description rdf:about='#plus4'/>\n"
                + "     <rdf:Description rdf:about='#minus3'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus6'/>\n"
                + "     <rdf:Description rdf:about='#plus7'/>\n"
                + "     <rdf:Description rdf:about='#minus1'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus1'/>\n"
                + "     <rdf:Description rdf:about='#plus7'/>\n"
                + "     <rdf:Description rdf:about='#minus9'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus3'/>\n"
                + "     <rdf:Description rdf:about='#plus2'/>\n"
                + "     <rdf:Description rdf:about='#plus6'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus8'/>\n"
                + "     <rdf:Description rdf:about='#plus3'/>\n"
                + "     <rdf:Description rdf:about='#minus7'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus1'/>\n"
                + "     <rdf:Description rdf:about='#plus9'/>\n"
                + "     <rdf:Description rdf:about='#minus8'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus5'/>\n"
                + "     <rdf:Description rdf:about='#minus9'/>\n"
                + "     <rdf:Description rdf:about='#minus7'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus7'/>\n"
                + "     <rdf:Description rdf:about='#plus3'/>\n"
                + "     <rdf:Description rdf:about='#minus9'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus3'/>\n"
                + "     <rdf:Description rdf:about='#minus1'/>\n"
                + "     <rdf:Description rdf:about='#minus2'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus6'/>\n"
                + "     <rdf:Description rdf:about='#plus1'/>\n"
                + "     <rdf:Description rdf:about='#plus4'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus6'/>\n"
                + "     <rdf:Description rdf:about='#minus7'/>\n"
                + "     <rdf:Description rdf:about='#plus5'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus8'/>\n"
                + "     <rdf:Description rdf:about='#minus6'/>\n"
                + "     <rdf:Description rdf:about='#plus3'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus5'/>\n"
                + "     <rdf:Description rdf:about='#minus2'/>\n"
                + "     <rdf:Description rdf:about='#plus6'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus8'/>\n"
                + "     <rdf:Description rdf:about='#plus3'/>\n"
                + "     <rdf:Description rdf:about='#minus5'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus2'/>\n"
                + "     <rdf:Description rdf:about='#minus4'/>\n"
                + "     <rdf:Description rdf:about='#minus9'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n" + " </owl:Class></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_503";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "This is a different encoding of test 501.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_504() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent504#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent504\" >\n"
                + " <owl:Ontology/>\n"
                + " <first:Test />\n"
                + " <owl:Class rdf:about='#plus1'>\n"
                + "  <owl:disjointWith>\n"
                + "   <owl:Class rdf:about='#minus1'/>\n"
                + "  </owl:disjointWith></owl:Class>\n"
                + " <owl:Class rdf:about='#plus2'>\n"
                + "  <owl:disjointWith>\n"
                + "   <owl:Class rdf:about='#minus2'/></owl:disjointWith></owl:Class>\n"
                + " <owl:Class rdf:about='#plus3'>\n"
                + "  <owl:disjointWith>\n"
                + "   <owl:Class rdf:about='#minus3'/></owl:disjointWith>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#plus4'>\n"
                + "  <owl:disjointWith>\n"
                + "   <owl:Class rdf:about='#minus4'/></owl:disjointWith></owl:Class>\n"
                + " <owl:Class rdf:about='#plus5'>\n"
                + "  <owl:disjointWith>\n"
                + "   <owl:Class rdf:about='#minus5'/></owl:disjointWith>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#plus6'>\n"
                + "  <owl:disjointWith>\n"
                + "   <owl:Class rdf:about='#minus6'/></owl:disjointWith></owl:Class>\n"
                + " <owl:Class rdf:about='#plus7'>\n"
                + "  <owl:disjointWith>\n"
                + "   <owl:Class rdf:about='#minus7'/></owl:disjointWith>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#plus8'>\n"
                + "  <owl:disjointWith>\n"
                + "   <owl:Class rdf:about='#minus8'/></owl:disjointWith></owl:Class>\n"
                + " <owl:Class rdf:about='#plus9'>\n"
                + "  <owl:disjointWith>\n"
                + "   <owl:Class rdf:about='#minus9'/></owl:disjointWith>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus1'/>\n"
                + "     <rdf:Description rdf:about='#plus2'/>\n"
                + "     <rdf:Description rdf:about='#minus4'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus3'/>\n"
                + "     <rdf:Description rdf:about='#plus6'/>\n"
                + "     <rdf:Description rdf:about='#minus4'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus9'/>\n"
                + "     <rdf:Description rdf:about='#minus4'/>\n"
                + "     <rdf:Description rdf:about='#plus5'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus4'/>\n"
                + "     <rdf:Description rdf:about='#minus6'/>\n"
                + "     <rdf:Description rdf:about='#minus2'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus2'/>\n"
                + "     <rdf:Description rdf:about='#minus3'/>\n"
                + "     <rdf:Description rdf:about='#plus1'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus3'/>\n"
                + "     <rdf:Description rdf:about='#plus8'/>\n"
                + "     <rdf:Description rdf:about='#plus7'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus8'/>\n"
                + "     <rdf:Description rdf:about='#minus2'/>\n"
                + "     <rdf:Description rdf:about='#plus3'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus7'/>\n"
                + "     <rdf:Description rdf:about='#minus6'/>\n"
                + "     <rdf:Description rdf:about='#plus9'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus1'/>\n"
                + "     <rdf:Description rdf:about='#minus4'/>\n"
                + "     <rdf:Description rdf:about='#minus6'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus8'/>\n"
                + "     <rdf:Description rdf:about='#minus5'/>\n"
                + "     <rdf:Description rdf:about='#minus3'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus4'/>\n"
                + "     <rdf:Description rdf:about='#plus3'/>\n"
                + "     <rdf:Description rdf:about='#plus6'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus2'/>\n"
                + "     <rdf:Description rdf:about='#minus1'/>\n"
                + "     <rdf:Description rdf:about='#plus4'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus3'/>\n"
                + "     <rdf:Description rdf:about='#plus8'/>\n"
                + "     <rdf:Description rdf:about='#plus2'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus6'/>\n"
                + "     <rdf:Description rdf:about='#minus2'/>\n"
                + "     <rdf:Description rdf:about='#plus9'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus7'/>\n"
                + "     <rdf:Description rdf:about='#minus9'/>\n"
                + "     <rdf:Description rdf:about='#minus2'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus2'/>\n"
                + "     <rdf:Description rdf:about='#minus5'/>\n"
                + "     <rdf:Description rdf:about='#minus7'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus5'/>\n"
                + "     <rdf:Description rdf:about='#plus2'/>\n"
                + "     <rdf:Description rdf:about='#plus9'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus6'/>\n"
                + "     <rdf:Description rdf:about='#minus2'/>\n"
                + "     <rdf:Description rdf:about='#minus7'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus9'/>\n"
                + "     <rdf:Description rdf:about='#plus3'/>\n"
                + "     <rdf:Description rdf:about='#minus2'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus1'/>\n"
                + "     <rdf:Description rdf:about='#plus7'/>\n"
                + "     <rdf:Description rdf:about='#plus4'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus4'/>\n"
                + "     <rdf:Description rdf:about='#plus1'/>\n"
                + "     <rdf:Description rdf:about='#plus9'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus2'/>\n"
                + "     <rdf:Description rdf:about='#plus1'/>\n"
                + "     <rdf:Description rdf:about='#minus6'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus7'/>\n"
                + "     <rdf:Description rdf:about='#minus4'/>\n"
                + "     <rdf:Description rdf:about='#plus9'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus5'/>\n"
                + "     <rdf:Description rdf:about='#plus3'/>\n"
                + "     <rdf:Description rdf:about='#minus9'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus4'/>\n"
                + "     <rdf:Description rdf:about='#plus9'/>\n"
                + "     <rdf:Description rdf:about='#minus8'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus4'/>\n"
                + "     <rdf:Description rdf:about='#plus3'/>\n"
                + "     <rdf:Description rdf:about='#plus9'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus7'/>\n"
                + "     <rdf:Description rdf:about='#plus9'/>\n"
                + "     <rdf:Description rdf:about='#plus5'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus4'/>\n"
                + "     <rdf:Description rdf:about='#plus1'/>\n"
                + "     <rdf:Description rdf:about='#plus3'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus5'/>\n"
                + "     <rdf:Description rdf:about='#plus8'/>\n"
                + "     <rdf:Description rdf:about='#plus7'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus8'/>\n"
                + "     <rdf:Description rdf:about='#minus7'/>\n"
                + "     <rdf:Description rdf:about='#plus3'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus4'/>\n"
                + "     <rdf:Description rdf:about='#minus8'/>\n"
                + "     <rdf:Description rdf:about='#plus6'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus4'/>\n"
                + "     <rdf:Description rdf:about='#plus6'/>\n"
                + "     <rdf:Description rdf:about='#minus5'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus6'/>\n"
                + "     <rdf:Description rdf:about='#plus1'/>\n"
                + "     <rdf:Description rdf:about='#minus9'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus1'/>\n"
                + "     <rdf:Description rdf:about='#plus9'/>\n"
                + "     <rdf:Description rdf:about='#minus6'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus9'/>\n"
                + "     <rdf:Description rdf:about='#minus8'/>\n"
                + "     <rdf:Description rdf:about='#plus3'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus6'/>\n"
                + "     <rdf:Description rdf:about='#plus3'/>\n"
                + "     <rdf:Description rdf:about='#minus4'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus8'/>\n"
                + "     <rdf:Description rdf:about='#minus4'/>\n"
                + "     <rdf:Description rdf:about='#plus6'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus3'/>\n"
                + "     <rdf:Description rdf:about='#plus5'/>\n"
                + "     <rdf:Description rdf:about='#minus8'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus9'/>\n"
                + "     <rdf:Description rdf:about='#plus4'/>\n"
                + "     <rdf:Description rdf:about='#plus3'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus8'/>\n"
                + "     <rdf:Description rdf:about='#minus4'/>\n"
                + "     <rdf:Description rdf:about='#plus2'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus5'/>\n"
                + "     <rdf:Description rdf:about='#minus2'/>\n"
                + "     <rdf:Description rdf:about='#minus9'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus7'/>\n"
                + "     <rdf:Description rdf:about='#minus3'/>\n"
                + "     <rdf:Description rdf:about='#minus4'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#minus9'/>\n"
                + "     <rdf:Description rdf:about='#minus4'/>\n"
                + "     <rdf:Description rdf:about='#minus8'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus6'/>\n"
                + "     <rdf:Description rdf:about='#minus4'/>\n"
                + "     <rdf:Description rdf:about='#minus1'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n"
                + " </owl:Class>\n"
                + " <owl:Class rdf:about='#Test'>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class>\n"
                + "    <owl:unionOf rdf:parseType='Collection'>\n"
                + "     <rdf:Description rdf:about='#plus6'/>\n"
                + "     <rdf:Description rdf:about='#minus7'/>\n"
                + "     <rdf:Description rdf:about='#minus8'/></owl:unionOf></owl:Class>\n"
                + "  </rdfs:subClassOf>\n" + " </owl:Class></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_504";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "This is a different encoding of test 502.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_601() {
        String premise = "<rdf:RDF xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent601\">\n"
                + " <owl:Ontology rdf:about=\"\"/>\n"
                + " <owl:Class rdf:ID=\"C.1.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.1\"/></owl:onProperty>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#b.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.3\"/></owl:onProperty>\n"
                + "    <owl:cardinality rdf:datatype=\"/2001/XMLSchema#unsignedByte\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#c.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:ID=\"P.2\"/></owl:onProperty>\n"
                + "    <owl:cardinality rdf:datatype=\"/2001/XMLSchema#short\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.5\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#decimal\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass>\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"#C.6\"/>\n"
                + "   <owl:Class rdf:about=\"#C.7\"/>\n"
                + "   <owl:Class rdf:about=\"#C.8\"/></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:ID=\"C.6.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:ID=\"P.6\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#byte\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass>\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#a\"/>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#b\"/></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:ID=\"C.8\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:ID=\"P.8\"/></owl:onProperty>\n"
                + "    <owl:cardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"C.7.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.7\"/></owl:onProperty>\n"
                + "    <owl:cardinality rdf:datatype=\"/2001/XMLSchema#integer\">0</owl:cardinality></owl:Restriction></owl:equivalentClass>\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#a\"/>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:ID=\"C.7\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.7\"/>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#integer\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"C.8.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.8\"/>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass>\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#b\"/>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:ID=\"C.6\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.6\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#byte\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.5\"/>\n"
                + "    <owl:cardinality rdf:datatype=\"/2001/XMLSchema#decimal\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.2\"/>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#short\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#b\">\n"
                + "  <rdfs:subClassOf rdf:resource=\"http://oiled.man.example.net/test#c.comp\"/><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.3\"/>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#unsignedByte\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#a\">\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class rdf:about=\"#C.1\"/></rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:ID=\"C.1\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.1\"/>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass>\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#b.comp\"/>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#c.comp\"/></owl:intersectionOf></owl:Class>\n"
                + " <oiled:Unsatisfiable/>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_601";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: fact1.1\n" + "If a, b and c are disjoint, then:\n"
                + "(a and b) or (b and c) or (c and a)\n" + "is unsatisfiable.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_602_old() {
        String premise = "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
                + "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n"
                + "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n"
                + "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n"
                + "Prefix(urn:=<urn:test#>)\n"
                + "Ontology(<urn:testonto:>\n"
                + "Declaration(Class(<urn:A.2>))\n"
                + "EquivalentClasses(<urn:A.2> ObjectAllValuesFrom(<urn:r> <urn:c>))\n"
                + "SubClassOf(<urn:A.2> <urn:d>)\n"
                + "Declaration(Class(<urn:Unsatisfiable>))\n"
                + "SubClassOf(<urn:Unsatisfiable> <urn:c>)\n"
                + "SubClassOf(<urn:Unsatisfiable> <urn:d.comp>)\n"
                + "Declaration(Class(<urn:c>))\n"
                + "SubClassOf(<urn:c> ObjectAllValuesFrom(<urn:r> <urn:c>))\n"
                + "Declaration(Class(<urn:d>))\n"
                + "EquivalentClasses(<urn:d> ObjectMaxCardinality(0 <urn:p>))\n"
                + "Declaration(Class(<urn:d.comp>))\n"
                + "EquivalentClasses(<urn:d.comp> ObjectMinCardinality(1 <urn:p>))\n"
                + "Declaration(ObjectProperty(<urn:r>))\n"
                + "Declaration(ObjectProperty(<urn:p>))\n"
                + "ClassAssertion(<urn:Unsatisfiable> urn:ind))";
        String conclusion = "";
        String id = "WebOnt_description_logic_602";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: fact2.1";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_602()
            throws OWLOntologyCreationException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLDataFactory f = m.getOWLDataFactory();
        OWLOntology o = m.createOntology();
        OWLClass A = f.getOWLClass(IRI.create("urn:A"));
        OWLClass C = f.getOWLClass(IRI.create("urn:C"));
        OWLClass D = f.getOWLClass(IRI.create("urn:D"));
        OWLClass B = f.getOWLClass(IRI.create("urn:B"));
        OWLClass U = f.getOWLClass(IRI.create("urn:U"));
        OWLObjectProperty p = f.getOWLObjectProperty(IRI.create("urn:p"));
        OWLObjectProperty r = f.getOWLObjectProperty(IRI.create("urn:r"));
        OWLObjectAllValuesFrom rAllC = f.getOWLObjectAllValuesFrom(r, C);
        m.addAxiom(o, f.getOWLEquivalentClassesAxiom(A, rAllC));
        m.addAxiom(o, f.getOWLSubClassOfAxiom(A, D));
        m.addAxiom(o, f.getOWLSubClassOfAxiom(U, C));
        m.addAxiom(o, f.getOWLSubClassOfAxiom(U, B));
        m.addAxiom(o, f.getOWLSubClassOfAxiom(C, rAllC));
        OWLObjectMaxCardinality zeroP = f.getOWLObjectMaxCardinality(0, p);
        m.addAxiom(o, f.getOWLEquivalentClassesAxiom(D, zeroP));
        OWLObjectMinCardinality oneP = f.getOWLObjectMinCardinality(1, p);
        m.addAxiom(o, f.getOWLEquivalentClassesAxiom(B, oneP));
        OWLReasoner reasoner = factory().createReasoner(o);
        reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        assertFalse("cannot find unsatisfiable class",
                reasoner.isSatisfiable(U));
        assertTrue("cannot infer disjoint",
                reasoner.isEntailed(f.getOWLDisjointClassesAxiom(D, B)));
        assertTrue("cannot infer U [= B",
                reasoner.isEntailed(f.getOWLSubClassOfAxiom(U, B)));
        assertTrue("cannot infer U [= C",
                reasoner.isEntailed(f.getOWLSubClassOfAxiom(U, C)));
        assertTrue("cannot infer C [= r some C",
                reasoner.isEntailed(f.getOWLSubClassOfAxiom(C, rAllC)));
        assertTrue("cannot infer r some C = A",
                reasoner.isEntailed(f.getOWLEquivalentClassesAxiom(rAllC, A)));
        assertTrue("cannot infer A [= D",
                reasoner.isEntailed(f.getOWLSubClassOfAxiom(A, D)));
        assertTrue("cannot infer U [= D",
                reasoner.isEntailed(f.getOWLSubClassOfAxiom(U, D)));
        assertFalse("cannot find unsatisfiable class",
                reasoner.isSatisfiable(U));
    }

    @Test
    public void testWebOnt_description_logic_603() {
        String premise = "<rdf:RDF xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent603\">\n"
                + " <owl:Ontology rdf:about=\"\"/>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.1\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f1\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/></owl:someValuesFrom></owl:Restriction>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f2\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom rdf:resource=\"http://oiled.man.example.net/test#p1.comp\"/></owl:Restriction>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f3\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"/></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.1\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f3\">\n"
                + "  <rdfs:subPropertyOf>\n"
                + "   <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f2\"/></rdfs:subPropertyOf>\n"
                + "  <rdfs:subPropertyOf>\n"
                + "   <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f1\"/></rdfs:subPropertyOf>\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#FunctionalProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f2\">\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#FunctionalProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f1\">\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#FunctionalProperty\"/></owl:ObjectProperty>\n"
                + " <oiled:Unsatisfiable/>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_603";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: fact3.1";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_604() {
        String premise = "<rdf:RDF xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent604\">\n"
                + " <owl:Ontology rdf:about=\"\"/>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#c2\"/>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#c1\"/>\n"
                + " <owl:Class rdf:ID=\"C.1.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.1\"/></owl:onProperty>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx3\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"#A.2\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"#C.1\"/>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx3\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom rdf:resource=\"http://oiled.man.example.net/test#c1\"/></owl:Restriction>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx4\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom rdf:resource=\"http://oiled.man.example.net/test#c2\"/></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:ID=\"C.1\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.1\"/>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"A.2\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#c1\"/>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#c2\"/></owl:intersectionOf></owl:Class>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx4\">\n"
                + "  <rdfs:subPropertyOf>\n"
                + "   <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx2\"/></rdfs:subPropertyOf>\n"
                + "  <rdfs:subPropertyOf>\n"
                + "   <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx\"/></rdfs:subPropertyOf>\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#FunctionalProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx3\">\n"
                + "  <rdfs:subPropertyOf>\n"
                + "   <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx1\"/></rdfs:subPropertyOf>\n"
                + "  <rdfs:subPropertyOf>\n"
                + "   <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx\"/></rdfs:subPropertyOf>\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#FunctionalProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rxa\"/>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx\">\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#FunctionalProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx1a\"/>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx2a\"/>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx3a\">\n"
                + "  <rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#rx1a\"/>\n"
                + "  <rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#rxa\"/>\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#FunctionalProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx4a\">\n"
                + "  <rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#rx2a\"/>\n"
                + "  <rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#rxa\"/>\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#FunctionalProperty\"/></owl:ObjectProperty>\n"
                + " <oiled:Unsatisfiable/>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_604";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: fact4.1";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_605() {
        String premise = "<rdf:RDF xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/description-logic/consistent605\">\n"
                + " <owl:Ontology rdf:about=\"\"/>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#c2\"/>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#Satisfiable\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"#C.1\"/>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx3a\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#c1\"/></owl:someValuesFrom></owl:Restriction>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx4a\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom rdf:resource=\"http://oiled.man.example.net/test#c2\"/></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:ID=\"C.1\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.1\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"A.2\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#c1\"/>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#c2\"/></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:ID=\"C.1.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.1\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx3a\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom rdf:resource=\"#A.2\"/></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx4\">\n"
                + "  <rdfs:subPropertyOf>\n"
                + "   <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx2\"/></rdfs:subPropertyOf>\n"
                + "  <rdfs:subPropertyOf>\n"
                + "   <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx\"/></rdfs:subPropertyOf>\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#FunctionalProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx3\">\n"
                + "  <rdfs:subPropertyOf>\n"
                + "   <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx1\"/></rdfs:subPropertyOf>\n"
                + "  <rdfs:subPropertyOf>\n"
                + "   <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx\"/></rdfs:subPropertyOf>\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#FunctionalProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rxa\"/>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx\">\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#FunctionalProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx1a\"/>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx2a\"/>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx3a\">\n"
                + "  <rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#rx1a\"/>\n"
                + "  <rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#rxa\"/>\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#FunctionalProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#rx4a\">\n"
                + "  <rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#rx2a\"/>\n"
                + "  <rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#rxa\"/>\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#FunctionalProperty\"/></owl:ObjectProperty>\n"
                + " <oiled:Satisfiable/>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_605";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "DL Test: fact4.2";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_606() {
        String premise = "<rdf:RDF xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/description-logic/consistent606\">\n"
                + " <owl:Ontology rdf:about=\"\"/>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p2.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.5\"/></owl:onProperty>\n"
                + "    <owl:cardinality rdf:datatype=\"/2001/XMLSchema#decimal\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p3.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:ID=\"P.6\"/></owl:onProperty>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#byte\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p4.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.7\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#integer\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p5.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:ID=\"P.4\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#nonNegativeInteger\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"C.1.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.1\"/></owl:onProperty>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"C.2.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:ID=\"P.2\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#short\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"C.3.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.3\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#unsignedByte\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#Satisfiable\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"#A.14\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"A.14\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/></owl:someValuesFrom></owl:Restriction>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:ID=\"C.3\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.3\"/>\n"
                + "    <owl:cardinality rdf:datatype=\"/2001/XMLSchema#unsignedByte\">0</owl:cardinality></owl:Restriction></owl:equivalentClass>\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#p4.comp\"/>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#p5.comp\"/></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:ID=\"C.2\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.2\"/>\n"
                + "    <owl:cardinality rdf:datatype=\"/2001/XMLSchema#short\">0</owl:cardinality></owl:Restriction></owl:equivalentClass>\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#p3.comp\"/>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#p4.comp\"/>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#p5.comp\"/></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:ID=\"C.1\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.1\"/>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass>\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#p2.comp\"/>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#p3.comp\"/>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#p4.comp\"/>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#p5.comp\"/></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.4\"/>\n"
                + "    <owl:cardinality rdf:datatype=\"/2001/XMLSchema#nonNegativeInteger\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\">\n"
                + "  <rdfs:subClassOf rdf:resource=\"http://oiled.man.example.net/test#p5.comp\"/><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.7\"/>\n"
                + "    <owl:cardinality rdf:datatype=\"/2001/XMLSchema#integer\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p3\">\n"
                + "  <rdfs:subClassOf rdf:resource=\"#C.3\"/><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.6\"/>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#byte\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\">\n"
                + "  <rdfs:subClassOf rdf:resource=\"#C.2\"/><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.5\"/>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#decimal\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\">\n"
                + "  <rdfs:subClassOf rdf:resource=\"#C.1\"/></owl:Class>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\">\n"
                + "  <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + " <oiled:Satisfiable/>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_606";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "DL Test: t1.1";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_608() {
        String premise = "<rdf:RDF xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent608\">\n"
                + " <owl:Ontology rdf:about=\"\"/>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p2.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.5\"/></owl:onProperty>\n"
                + "    <owl:cardinality rdf:datatype=\"/2001/XMLSchema#decimal\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p3.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:ID=\"P.6\"/></owl:onProperty>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#byte\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p4.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.7\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#integer\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p5.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:ID=\"P.4\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#nonNegativeInteger\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"C.1.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.1\"/></owl:onProperty>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"C.2.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:ID=\"P.2\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#short\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"/>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"#A.14\"/></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:ID=\"C.3.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.3\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#unsignedByte\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"A.14\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/></owl:someValuesFrom></owl:Restriction>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:ID=\"C.3\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.3\"/>\n"
                + "    <owl:cardinality rdf:datatype=\"/2001/XMLSchema#unsignedByte\">0</owl:cardinality></owl:Restriction></owl:equivalentClass>\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#p4.comp\"/>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#p5.comp\"/></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:ID=\"C.2\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.2\"/>\n"
                + "    <owl:cardinality rdf:datatype=\"/2001/XMLSchema#short\">0</owl:cardinality></owl:Restriction></owl:equivalentClass>\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#p3.comp\"/>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#p4.comp\"/>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#p5.comp\"/></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:ID=\"C.1\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.1\"/>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass>\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#p2.comp\"/>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#p3.comp\"/>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#p4.comp\"/>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#p5.comp\"/></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p5\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.4\"/>\n"
                + "    <owl:cardinality rdf:datatype=\"/2001/XMLSchema#nonNegativeInteger\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p4\">\n"
                + "  <rdfs:subClassOf rdf:resource=\"http://oiled.man.example.net/test#p5.comp\"/><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.7\"/>\n"
                + "    <owl:cardinality rdf:datatype=\"/2001/XMLSchema#integer\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p3\">\n"
                + "  <rdfs:subClassOf rdf:resource=\"#C.3\"/><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.6\"/>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#byte\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\">\n"
                + "  <rdfs:subClassOf rdf:resource=\"#C.2\"/><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.5\"/>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#decimal\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\">\n"
                + "  <rdfs:subClassOf rdf:resource=\"#C.1\"/></owl:Class>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\">\n"
                + "  <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + " <oiled:Unsatisfiable/>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_608";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: t1.3";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_609() {
        String premise = "<rdf:RDF xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/description-logic/consistent609\">\n"
                + " <owl:Ontology rdf:about=\"\"/>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#Satisfiable\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:someValuesFrom></owl:Restriction>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f1\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#p.comp\"/></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.1\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.1\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF1\">\n"
                + "  <owl:inverseOf>\n"
                + "   <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f1\"/></owl:inverseOf></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f1\">\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#FunctionalProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\">\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#FunctionalProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\">\n"
                + "  <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#f\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#s\">\n"
                + "  <rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#f\"/>\n"
                + "  <rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#f1\"/>\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#FunctionalProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invS\">\n"
                + "  <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#s\"/></owl:ObjectProperty>\n"
                + " <oiled:Satisfiable/>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_609";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "DL Test: t10.1";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_610() {
        String premise = "<rdf:RDF xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent610\">\n"
                + " <owl:Ontology rdf:about=\"\"/>\n"
                + " <owl:Class rdf:ID=\"A.2\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invS\"/></owl:onProperty>\n"
                + "    <owl:allValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:allValuesFrom></owl:Restriction>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\"/></owl:onProperty>\n"
                + "    <owl:allValuesFrom>\n"
                + "     <owl:Class rdf:about=\"#V.3\"/></owl:allValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.1\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#p.comp\"/>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom rdf:resource=\"#A.2\"/></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:ID=\"V.3\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#s\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.1\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF1\">\n"
                + "  <owl:inverseOf>\n"
                + "   <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f1\"/></owl:inverseOf></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f1\">\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#FunctionalProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\">\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#FunctionalProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\">\n"
                + "  <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#f\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#s\">\n"
                + "  <rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#f\"/>\n"
                + "  <rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#f1\"/>\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#FunctionalProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invS\">\n"
                + "  <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#s\"/></owl:ObjectProperty>\n"
                + " <oiled:Unsatisfiable/>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_610";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: t10.2";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_611() {
        String premise = "<rdf:RDF xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent611\">\n"
                + " <owl:Ontology rdf:about=\"\"/>\n"
                + " <owl:Class rdf:ID=\"A.2\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invS\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.1\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#s\"/></owl:onProperty>\n"
                + "    <owl:allValuesFrom rdf:resource=\"http://oiled.man.example.net/test#p.comp\"/></owl:Restriction>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#s\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom rdf:resource=\"#A.2\"/></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.1\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF1\">\n"
                + "  <owl:inverseOf>\n"
                + "   <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f1\"/></owl:inverseOf></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f1\">\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#FunctionalProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\">\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#FunctionalProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\">\n"
                + "  <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#f\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#s\">\n"
                + "  <rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#f\"/>\n"
                + "  <rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#f1\"/>\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#FunctionalProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invS\">\n"
                + "  <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#s\"/></owl:ObjectProperty>\n"
                + " <oiled:Unsatisfiable/>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_611";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: t10.3";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_612() {
        String premise = "<rdf:RDF xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent612\">\n"
                + " <owl:Ontology rdf:about=\"\"/>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.1\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:someValuesFrom></owl:Restriction>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#s\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"/2002/07/owl#Thing\"/></owl:someValuesFrom></owl:Restriction>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f1\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom rdf:resource=\"http://oiled.man.example.net/test#p.comp\"/></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.1\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF1\">\n"
                + "  <owl:inverseOf>\n"
                + "   <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f1\"/></owl:inverseOf></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f1\">\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#FunctionalProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\">\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#FunctionalProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\">\n"
                + "  <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#f\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#s\">\n"
                + "  <rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#f\"/>\n"
                + "  <rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#f1\"/>\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#FunctionalProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invS\">\n"
                + "  <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#s\"/></owl:ObjectProperty>\n"
                + " <oiled:Unsatisfiable/>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_612";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: t10.4";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_613() {
        String premise = "<rdf:RDF xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent613\">\n"
                + " <owl:Ontology rdf:about=\"\"/>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.1\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"V.3\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#s\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"/2002/07/owl#Thing\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:someValuesFrom></owl:Restriction>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f1\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"#A.2\"/></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.1\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"A.2\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#p.comp\"/>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF1\"/></owl:onProperty>\n"
                + "    <owl:allValuesFrom rdf:resource=\"#V.3\"/></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF1\">\n"
                + "  <owl:inverseOf>\n"
                + "   <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f1\"/></owl:inverseOf></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f1\">\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#FunctionalProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\">\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#FunctionalProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\">\n"
                + "  <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#f\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#s\">\n"
                + "  <rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#f\"/>\n"
                + "  <rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#f1\"/>\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#FunctionalProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invS\">\n"
                + "  <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#s\"/></owl:ObjectProperty>\n"
                + " <oiled:Unsatisfiable/>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_613";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: t10.5";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_614() {
        String premise = "<rdf:RDF xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent614\">\n"
                + " <owl:Ontology rdf:about=\"\"/>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.1\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"V.2\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invS\"/></owl:onProperty>\n"
                + "    <owl:allValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:allValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#p.comp\"/>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/></owl:onProperty>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <owl:someValuesFrom rdf:resource=\"#V.2\"/></owl:Restriction>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#s\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.1\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#s\">\n"
                + "  <rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invS\">\n"
                + "  <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#s\"/></owl:ObjectProperty>\n"
                + " <oiled:Unsatisfiable/>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_614";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: t11.1";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_615() {
        String premise = "<rdf:RDF xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent615\">\n"
                + " <owl:Ontology rdf:about=\"\"/>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.1\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#q.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:ID=\"P.2\"/></owl:onProperty>\n"
                + "    <owl:cardinality rdf:datatype=\"/2001/XMLSchema#short\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"V.5\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#s\"/></owl:onProperty>\n"
                + "    <owl:allValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:allValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#s\"/>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"#A.3\"/></owl:someValuesFrom></owl:Restriction>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"#A.4\"/></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:ID=\"A.4\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\"/></owl:onProperty>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom rdf:resource=\"#V.5\"/></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:ID=\"A.3\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#p.comp\"/>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#q.comp\"/></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#q\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.2\"/>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#short\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.1\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\">\n"
                + "  <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + " <oiled:Unsatisfiable/>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_615";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: t12.1";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_616() {
        String premise = "<rdf:RDF xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/description-logic/consistent616\">\n"
                + " <owl:Ontology rdf:about=\"\"/>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#Satisfiable\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f1\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/></owl:someValuesFrom></owl:Restriction>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f2\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"/></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p2.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.1\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.1\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\">\n"
                + "  <rdfs:subClassOf rdf:resource=\"http://oiled.man.example.net/test#p2.comp\"/></owl:Class>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\">\n"
                + "  <rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#f2\"/>\n"
                + "  <rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#f1\"/></owl:ObjectProperty>\n"
                + " <oiled:Satisfiable/>\n"
                + " <rdf:Description rdf:about=\"/2002/07/owl#Thing\">\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f1\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction></rdfs:subClassOf>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f2\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction></rdfs:subClassOf>\n"
                + " </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_616";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "DL Test: t2.1";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_617() {
        String premise = "<rdf:RDF xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent617\">\n"
                + " <owl:Ontology rdf:about=\"\"/>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p2.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.1\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f1\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/></owl:someValuesFrom></owl:Restriction>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f2\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"/></owl:someValuesFrom></owl:Restriction>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"/2002/07/owl#Thing\"/></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"/2002/07/owl#Thing\">\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f1\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction></rdfs:subClassOf>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f2\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p2\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.1\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\">\n"
                + "  <rdfs:subClassOf rdf:resource=\"http://oiled.man.example.net/test#p2.comp\"/></owl:Class>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\">\n"
                + "  <rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#f2\"/>\n"
                + "  <rdfs:subPropertyOf rdf:resource=\"http://oiled.man.example.net/test#f1\"/></owl:ObjectProperty>\n"
                + " <oiled:Unsatisfiable/>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_617";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: t2.2";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_623() {
        String premise = "<rdf:RDF xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent623\">\n"
                + " <owl:Ontology rdf:about=\"\"/>\n"
                + " <owl:Class rdf:ID=\"A.2\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"/2002/07/owl#Thing\"/></owl:someValuesFrom></owl:Restriction>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom rdf:resource=\"/2002/07/owl#Thing\"/></owl:Restriction>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <owl:allValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/></owl:allValuesFrom></owl:Restriction>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:onProperty>\n"
                + "    <owl:allValuesFrom>\n"
                + "     <owl:Class rdf:about=\"#V.3\"/></owl:allValuesFrom></owl:Restriction>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:onProperty>\n"
                + "    <owl:allValuesFrom>\n"
                + "     <owl:Class rdf:about=\"#V.4\"/></owl:allValuesFrom></owl:Restriction>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:onProperty>\n"
                + "    <owl:allValuesFrom>\n"
                + "     <owl:Class rdf:about=\"#V.5\"/></owl:allValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#a.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.1\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"V.7\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invP\"/></owl:onProperty>\n"
                + "    <owl:allValuesFrom>\n"
                + "     <owl:Class rdf:about=\"#V.6\"/></owl:allValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"V.6\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invS\"/></owl:onProperty>\n"
                + "    <owl:allValuesFrom rdf:resource=\"http://oiled.man.example.net/test#a.comp\"/></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"V.5\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <owl:allValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/></owl:allValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"V.4\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom rdf:resource=\"/2002/07/owl#Thing\"/></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"V.3\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <owl:someValuesFrom rdf:resource=\"/2002/07/owl#Thing\"/></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\">\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#a\"/></rdfs:subClassOf>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#s\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom rdf:resource=\"#A.2\"/></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\"/></owl:onProperty>\n"
                + "    <owl:allValuesFrom rdf:resource=\"#V.7\"/></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#a\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.1\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invP\">\n"
                + "  <owl:inverseOf>\n"
                + "   <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:inverseOf></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invS\">\n"
                + "  <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#s\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#p\">\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#TransitiveProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\">\n"
                + "  <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + " <oiled:Unsatisfiable/>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_623";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: t4.1\n" + "Dynamic blocking example";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_624() {
        String premise = "<rdf:RDF xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/description-logic/consistent624\">\n"
                + " <owl:Ontology rdf:about=\"\"/>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#Satisfiable\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#a.comp\"/>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#a\"/></owl:someValuesFrom></owl:Restriction>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"#V.2\"/></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#a.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.1\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"V.2\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#a\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#a\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.1\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\">\n"
                + "  <rdfs:subPropertyOf>\n"
                + "   <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/></rdfs:subPropertyOf></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\">\n"
                + "  <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#f\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\">\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#TransitiveProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\">\n"
                + "  <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + " <oiled:Satisfiable/>\n"
                + " <rdf:Description rdf:about=\"/2002/07/owl#Thing\">\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction></rdfs:subClassOf>\n"
                + " </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_624";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "DL Test: t5.1\n" + "Non-finite model example from paper\n"
                + "The concept should be coherent but has no finite model";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_625() {
        String premise = "<rdf:RDF xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/description-logic/consistent625\">\n"
                + " <owl:Ontology rdf:about=\"\"/>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#Satisfiable\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#a.comp\"/>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#a\"/></owl:someValuesFrom></owl:Restriction>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"#V.2\"/></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:ID=\"V.2\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#a\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#a.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.1\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#a\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.1\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\">\n"
                + "  <rdfs:subPropertyOf>\n"
                + "   <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/></rdfs:subPropertyOf>\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#FunctionalProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\">\n"
                + "  <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#f\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\">\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#TransitiveProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\">\n"
                + "  <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + " <oiled:Satisfiable/>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_625";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "DL Test: t5f.1\n" + "Non-finite model example from paper\n"
                + "The concept should be coherent but has no finite model";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_626() {
        String premise = "<rdf:RDF xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent626\">\n"
                + " <owl:Ontology rdf:about=\"\"/>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#c.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.1\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"V.3\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#c.comp\"/>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></owl:someValuesFrom></owl:Restriction>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\"/></owl:onProperty>\n"
                + "    <owl:allValuesFrom rdf:resource=\"#V.3\"/></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#d\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom rdf:resource=\"http://oiled.man.example.net/test#c.comp\"/></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.1\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\">\n"
                + "  <rdfs:subPropertyOf>\n"
                + "   <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/></rdfs:subPropertyOf></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\">\n"
                + "  <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#f\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\">\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#TransitiveProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\">\n"
                + "  <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + " <rdf:Description rdf:about=\"/2002/07/owl#Thing\">\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction></rdfs:subClassOf>\n"
                + " </rdf:Description>\n"
                + " <oiled:Unsatisfiable/>\n"
                + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_626";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: t6.1\n" + "Double blocking example.\n"
                + "The concept should be incoherent but needs double blocking";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_627() {
        String premise = "<rdf:RDF xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent627\">\n"
                + " <owl:Ontology rdf:about=\"\"/>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#c.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.1\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"V.3\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#d\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom rdf:resource=\"http://oiled.man.example.net/test#c.comp\"/></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#c.comp\"/>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom rdf:resource=\"http://oiled.man.example.net/test#d\"/></owl:Restriction>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\"/></owl:onProperty>\n"
                + "    <owl:allValuesFrom rdf:resource=\"#V.3\"/></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.1\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\">\n"
                + "  <rdfs:subPropertyOf>\n"
                + "   <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/></rdfs:subPropertyOf>\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#FunctionalProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\">\n"
                + "  <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#f\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\">\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#TransitiveProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\">\n"
                + "  <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + " <oiled:Unsatisfiable/>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_627";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: t6f.1\n" + "Double blocking example.\n"
                + "The concept should be incoherent but needs double blocking";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_628() {
        String premise = "<rdf:RDF xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/description-logic/consistent628\">\n"
                + " <owl:Ontology rdf:about=\"\"/>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#Satisfiable\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"#V.5\"/></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.1\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"C.3\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.3\"/></owl:onProperty>\n"
                + "    <owl:cardinality rdf:datatype=\"/2001/XMLSchema#unsignedByte\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"C.2\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:ID=\"P.2\"/></owl:onProperty>\n"
                + "    <owl:cardinality rdf:datatype=\"/2001/XMLSchema#short\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"A.4\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\"/></owl:onProperty>\n"
                + "    <owl:allValuesFrom rdf:resource=\"#C.2\"/></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:ID=\"C.2.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.2\"/>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#short\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass>\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/>\n"
                + "   <owl:Class rdf:about=\"#C.3\"/></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:ID=\"V.5\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom rdf:resource=\"#A.4\"/></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"C.3.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.3\"/>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#unsignedByte\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/></owl:onProperty>\n"
                + "    <owl:allValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/></owl:allValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.1\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\"/>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\">\n"
                + "  <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#f\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\">\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#TransitiveProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\">\n"
                + "  <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + " <oiled:Satisfiable/>\n"
                + " <rdf:Description rdf:about=\"/2002/07/owl#Thing\">\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction></rdfs:subClassOf>\n"
                + " </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_628";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "DL Test: t7.1";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_629() {
        String premise = "<rdf:RDF xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent629\">\n"
                + " <owl:Ontology rdf:about=\"\"/>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.1\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"V.3\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"#A.2\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"A.2\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\"/></owl:onProperty>\n"
                + "    <owl:allValuesFrom rdf:resource=\"http://oiled.man.example.net/test#p1.comp\"/></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom rdf:resource=\"#V.3\"/></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.1\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\"/>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\">\n"
                + "  <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#f\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\">\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#TransitiveProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\">\n"
                + "  <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + " <oiled:Unsatisfiable/>\n"
                + " <rdf:Description rdf:about=\"/2002/07/owl#Thing\">\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction></rdfs:subClassOf>\n"
                + " </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_629";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: t7.2";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_630() {
        String premise = "<rdf:RDF xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent630\">\n"
                + " <owl:Ontology rdf:about=\"\"/>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.1\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"A.2\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"#V.3\"/></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom rdf:resource=\"#A.2\"/></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"V.3\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f\"/>\n"
                + "    <owl:someValuesFrom rdf:resource=\"http://oiled.man.example.net/test#p1.comp\"/></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.1\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\">\n"
                + "  <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#f\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\">\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#TransitiveProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\">\n"
                + "  <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + " <oiled:Unsatisfiable/>\n"
                + " <rdf:Description rdf:about=\"/2002/07/owl#Thing\">\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#f\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction></rdfs:subClassOf>\n"
                + " </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_630";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: t7.3";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_631() {
        String premise = "<rdf:RDF xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/description-logic/consistent631\">\n"
                + " <owl:Ontology rdf:about=\"\"/>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#Satisfiable\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"#V.5\"/></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.1\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"C.2.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:ID=\"P.2\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#short\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass>\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/>\n"
                + "   <owl:Class rdf:about=\"#C.3\"/></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:ID=\"C.3.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.3\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#unsignedByte\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/></owl:onProperty>\n"
                + "    <owl:allValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/></owl:allValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"V.5\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"#A.4\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"C.3\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.3\"/>\n"
                + "    <owl:cardinality rdf:datatype=\"/2001/XMLSchema#unsignedByte\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"C.2\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.2\"/>\n"
                + "    <owl:cardinality rdf:datatype=\"/2001/XMLSchema#short\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"A.4\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\"/></owl:onProperty>\n"
                + "    <owl:allValuesFrom rdf:resource=\"#C.2\"/></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.1\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\">\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#FunctionalProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\">\n"
                + "  <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#f\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\">\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#TransitiveProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\">\n"
                + "  <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + " <oiled:Satisfiable/>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_631";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "DL Test: t7f.1";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_632() {
        String premise = "<rdf:RDF xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent632\">\n"
                + " <owl:Ontology rdf:about=\"\"/>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.1\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"A.2\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\"/></owl:onProperty>\n"
                + "    <owl:allValuesFrom rdf:resource=\"http://oiled.man.example.net/test#p1.comp\"/></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"#V.3\"/></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:ID=\"V.3\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom rdf:resource=\"#A.2\"/></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.1\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\">\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#FunctionalProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\">\n"
                + "  <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#f\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\">\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#TransitiveProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\">\n"
                + "  <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + " <oiled:Unsatisfiable/>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_632";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: t7f.2";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_633() {
        String premise = "<rdf:RDF xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent633\">\n"
                + " <owl:Ontology rdf:about=\"\"/>\n"
                + " <owl:Class rdf:ID=\"A.2\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"/>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\"/></owl:onProperty>\n"
                + "    <owl:allValuesFrom>\n"
                + "     <owl:Class rdf:about=\"#V.3\"/></owl:allValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.1\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom rdf:resource=\"#A.2\"/></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"V.3\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom rdf:resource=\"http://oiled.man.example.net/test#p1.comp\"/></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p1\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.1\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#f\">\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#FunctionalProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invF\">\n"
                + "  <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#f\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\">\n"
                + "  <rdf:type rdf:resource=\"/2002/07/owl#TransitiveProperty\"/></owl:ObjectProperty>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\">\n"
                + "  <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + " <oiled:Unsatisfiable/>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_633";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: t7f.3";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_634() {
        String premise = "<rdf:RDF xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/description-logic/consistent634\">\n"
                + " <owl:Ontology rdf:about=\"\"/>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#Satisfiable\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"#V.4\"/></owl:someValuesFrom></owl:Restriction>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"#V.5\"/></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.1\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"V.5\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\"/></owl:onProperty>\n"
                + "    <owl:allValuesFrom>\n"
                + "     <owl:Class rdf:about=\"#V.3\"/></owl:allValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"V.4\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\"/></owl:onProperty>\n"
                + "    <owl:allValuesFrom>\n"
                + "     <owl:Class rdf:about=\"#V.2\"/></owl:allValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"V.3\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r1\"/></owl:onProperty>\n"
                + "    <owl:allValuesFrom rdf:resource=\"http://oiled.man.example.net/test#p.comp\"/></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:ID=\"V.2\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r1\"/>\n"
                + "    <owl:allValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"/></owl:allValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#p\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.1\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#invR\">\n"
                + "  <owl:inverseOf rdf:resource=\"http://oiled.man.example.net/test#r\"/></owl:ObjectProperty>\n"
                + " <oiled:Satisfiable/>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_634";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "DL Test: t8.1";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_641() {
        String premise = "<rdf:RDF xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent641\">\n"
                + " <owl:Ontology rdf:about=\"\"/>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#c1\">\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#d1\"/></rdfs:subClassOf>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#d1.comp\"/></rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#e3\">\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/></rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#d1\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:ID=\"P.2\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#short\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#d.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.1\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#f\">\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.1\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\">\n"
                + "  <rdfs:subClassOf rdf:resource=\"http://oiled.man.example.net/test#d.comp\"/></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#d1.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.2\"/>\n"
                + "    <owl:cardinality rdf:datatype=\"/2001/XMLSchema#short\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + " <oiled:Unsatisfiable/>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_641";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: heinsohn1.1\n"
                + "Tbox tests from Heinsohn et al.\n"
                + "Tests incoherency caused by disjoint concept";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_642() {
        String premise = "<rdf:RDF xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent642\">\n"
                + " <owl:Ontology rdf:about=\"\"/>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#e3\">\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/></rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#c1\">\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#d1\"/></rdfs:subClassOf>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#d1.comp\"/></rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:ID=\"A.3\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#d1\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:ID=\"P.2\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#short\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#d.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.1\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#f\">\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/></owl:onProperty>\n"
                + "    <owl:allValuesFrom rdf:resource=\"#A.3\"/></owl:Restriction>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"/2002/07/owl#Thing\"/></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.1\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\">\n"
                + "  <rdfs:subClassOf rdf:resource=\"http://oiled.man.example.net/test#d.comp\"/></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#d1.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.2\"/>\n"
                + "    <owl:cardinality rdf:datatype=\"/2001/XMLSchema#short\">0</owl:cardinality></owl:Restriction></owl:equivalentClass>\n"
                + " </owl:Class>\n"
                + " <oiled:Unsatisfiable/>\n"
                + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_642";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: heinsohn1.2\n"
                + "Tbox tests from Heinsohn et al.\n"
                + "Tests incoherency caused by disjoint concept";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_643() {
        String premise = "<rdf:RDF xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent643\">\n"
                + " <owl:Ontology rdf:about=\"\"/>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#c1\">\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#d1\"/></rdfs:subClassOf>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#d1.comp\"/></rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#e3\">\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/></rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#d1\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:ID=\"P.2\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#short\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#d.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.1\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#f\">\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.1\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#e3\"/>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#f\"/></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\">\n"
                + "  <rdfs:subClassOf rdf:resource=\"http://oiled.man.example.net/test#d.comp\"/></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#d1.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.2\"/>\n"
                + "    <owl:cardinality rdf:datatype=\"/2001/XMLSchema#short\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + " <oiled:Unsatisfiable/>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_643";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: heinsohn1.3\n"
                + "Tbox tests from Heinsohn et al.\n"
                + "Tests incoherency caused by disjoint concept";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_644() {
        String premise = "<rdf:RDF xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent644\">\n"
                + " <owl:Ontology rdf:about=\"\"/>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#e3\">\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/></rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#d1\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:ID=\"P.2\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#short\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#d.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.1\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#f\">\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.1\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\">\n"
                + "  <rdfs:subClassOf rdf:resource=\"http://oiled.man.example.net/test#d1\"/>\n"
                + "  <rdfs:subClassOf>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#d1.comp\"/></rdfs:subClassOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\">\n"
                + "  <rdfs:subClassOf rdf:resource=\"http://oiled.man.example.net/test#d.comp\"/></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#d1.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.2\"/>\n"
                + "    <owl:cardinality rdf:datatype=\"/2001/XMLSchema#short\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/>\n"
                + " <oiled:Unsatisfiable/>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_644";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: heinsohn1.4\n"
                + "Tbox tests from Heinsohn et al.\n"
                + "Tests incoherency caused by disjoint concept";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_646() {
        String premise = "<rdf:RDF xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent646\">\n"
                + " <owl:Ontology rdf:about=\"\"/>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#d.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.1\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/></owl:onProperty>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\"/></owl:someValuesFrom></owl:Restriction>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.1\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\">\n"
                + "  <rdfs:subClassOf rdf:resource=\"http://oiled.man.example.net/test#d.comp\"/></owl:Class>\n"
                + " <oiled:Unsatisfiable/>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_646";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: heinsohn2.2\n"
                + "Tbox tests from Heinsohn et al.\n"
                + "Tests incoherency caused by number restrictions";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_650() {
        String premise = "<rdf:RDF xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent650\">\n"
                + " <owl:Ontology rdf:about=\"\"/>\n"
                + " <owl:Class rdf:ID=\"C.4\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:ID=\"P.4\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#nonNegativeInteger\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#d.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:ID=\"P.2\"/></owl:onProperty>\n"
                + "    <owl:cardinality rdf:datatype=\"/2001/XMLSchema#short\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#e.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.1\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#e\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.1\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#Unsatisfiable\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#r\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom rdf:resource=\"http://oiled.man.example.net/test#e.comp\"/></owl:Restriction>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <owl:allValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></owl:allValuesFrom></owl:Restriction>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#r\"/>\n"
                + "    <owl:allValuesFrom rdf:resource=\"#C.4\"/></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.2\"/>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#short\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#c\">\n"
                + "  <rdfs:subClassOf rdf:resource=\"http://oiled.man.example.net/test#d.comp\"/></owl:Class>\n"
                + " <owl:Class rdf:ID=\"C.4.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.4\"/>\n"
                + "    <owl:cardinality rdf:datatype=\"/2001/XMLSchema#nonNegativeInteger\">0</owl:cardinality></owl:Restriction></owl:equivalentClass>\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#e.comp\"/>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#d\"/></owl:intersectionOf></owl:Class>\n"
                + " <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#s\"/>\n"
                + " <oiled:Unsatisfiable/>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_650";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "DL Test: heinsohn4.1\n"
                + "Tbox tests from Heinsohn et al.\n"
                + "Tests role restrictions";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_665() {
        String premise = "<rdf:RDF xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/description-logic/premises665\">\n"
                + " <owl:Ontology rdf:about=\"\"/>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C2.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.1\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.1\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C4.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:ID=\"P.6\"/></owl:onProperty>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#byte\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C18\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#TOP\"/>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#C16\"/></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C10.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.3\"/></owl:onProperty>\n"
                + "    <owl:cardinality rdf:datatype=\"/2001/XMLSchema#unsignedByte\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C16\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#C8\"/>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#C14\"/></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C14\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#R1\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#C12\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C12\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#C2.comp\"/>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#C10.comp\"/></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C8\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#C6\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C10\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/>\n"
                + "    <owl:someValuesFrom rdf:resource=\"http://oiled.man.example.net/test#C2.comp\"/></owl:Restriction></owl:equivalentClass><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.3\"/>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#unsignedByte\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C6\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#C2.comp\"/>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#C4.comp\"/></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#TEST\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#C18\"/>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#TOP\"/></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/>\n"
                + "    <owl:someValuesFrom rdf:resource=\"http://oiled.man.example.net/test#C2.comp\"/></owl:Restriction></owl:equivalentClass><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.6\"/>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#byte\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Thing rdf:about=\"http://oiled.man.example.net/test#V16562\">\n"
                + "  <rdf:type>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/>\n"
                + "    <owl:allValuesFrom rdf:resource=\"http://oiled.man.example.net/test#C2\"/></owl:Restriction></rdf:type>\n"
                + "  <rdf:type rdf:resource=\"http://oiled.man.example.net/test#C10.comp\"/>\n"
                + "  <rdf:type rdf:resource=\"http://oiled.man.example.net/test#C2.comp\"/></owl:Thing>\n"
                + " <owl:Thing rdf:about=\"http://oiled.man.example.net/test#V16561\">\n"
                + "  <rdf:type>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/>\n"
                + "    <owl:allValuesFrom rdf:resource=\"http://oiled.man.example.net/test#C2\"/></owl:Restriction></rdf:type>\n"
                + "  <rdf:type rdf:resource=\"http://oiled.man.example.net/test#C4.comp\"/>\n"
                + "  <rdf:type rdf:resource=\"http://oiled.man.example.net/test#C2.comp\"/></owl:Thing>\n"
                + " <owl:Thing rdf:about=\"http://oiled.man.example.net/test#V16560\">\n"
                + "  <rdf:type rdf:resource=\"http://oiled.man.example.net/test#TEST\"/>\n"
                + "  <rdf:type rdf:resource=\"http://oiled.man.example.net/test#TOP\"/>\n"
                + "  <oiled:R1 rdf:resource=\"http://oiled.man.example.net/test#V16562\"/>\n"
                + "  <oiled:R1 rdf:resource=\"http://oiled.man.example.net/test#V16561\"/></owl:Thing>\n"
                + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "      xml:base=\"http://www.w3.org/2002/03owlt/description-logic/conclusions665\"\n"
                + ">\n"
                + "<owl:Ontology/>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V16560\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C18\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V16560\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C8\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V16560\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C16\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V16560\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C14\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V16561\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C6\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V16562\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C12\"/></rdf:type>\n"
                + "</owl:Thing>\n" + "</rdf:RDF>";
        String id = "WebOnt_description_logic_665";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "DL Test: k_lin\n"
                + "ABox test from DL98 systems comparison.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_667() {
        String premise = "<rdf:RDF xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/description-logic/premises667\">\n"
                + " <owl:Ontology rdf:about=\"\"/>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C2.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:DatatypeProperty rdf:ID=\"P.1\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.1\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C6.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:ID=\"P.4\"/></owl:onProperty>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#nonNegativeInteger\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C8.comp\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:ID=\"P.2\"/></owl:onProperty>\n"
                + "    <owl:cardinality rdf:datatype=\"/2001/XMLSchema#short\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C12\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty>\n"
                + "     <owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#R1\"/></owl:onProperty>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#C10\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C8\"><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/>\n"
                + "    <owl:someValuesFrom>\n"
                + "     <owl:Class rdf:about=\"http://oiled.man.example.net/test#C6\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.2\"/>\n"
                + "    <owl:minCardinality rdf:datatype=\"/2001/XMLSchema#short\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C10\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"/>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/></owl:intersectionOf></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C6\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#C2.comp\"/>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/></owl:intersectionOf><owl:equivalentClass><owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"#P.4\"/>\n"
                + "    <owl:cardinality rdf:datatype=\"/2001/XMLSchema#nonNegativeInteger\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#TEST\">\n"
                + "  <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#C8.comp\"/>\n"
                + "   <owl:Class rdf:about=\"http://oiled.man.example.net/test#C12\"/></owl:intersectionOf></owl:Class>\n"
                + " <owl:Thing rdf:about=\"http://oiled.man.example.net/test#V21081\">\n"
                + "  <rdf:type rdf:resource=\"http://oiled.man.example.net/test#C4\"/>\n"
                + "  <rdf:type rdf:resource=\"http://oiled.man.example.net/test#C2\"/>\n"
                + "  <rdf:type rdf:resource=\"http://oiled.man.example.net/test#C6.comp\"/></owl:Thing>\n"
                + " <owl:Thing rdf:about=\"http://oiled.man.example.net/test#V21080\">\n"
                + "  <rdf:type rdf:resource=\"http://oiled.man.example.net/test#TEST\"/>\n"
                + "  <rdf:type>\n"
                + "   <owl:Restriction>\n"
                + "    <owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/>\n"
                + "    <owl:allValuesFrom rdf:resource=\"http://oiled.man.example.net/test#C6.comp\"/></owl:Restriction></rdf:type>\n"
                + "  <oiled:R1 rdf:resource=\"http://oiled.man.example.net/test#V21081\"/>\n"
                + "  <rdf:type rdf:resource=\"http://oiled.man.example.net/test#C8.comp\"/></owl:Thing>\n"
                + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "      xml:base=\"http://www.w3.org/2002/03owlt/description-logic/conclusions667\"\n"
                + ">\n"
                + "<owl:Ontology/>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V21080\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C12\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V21081\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C10\"/></rdf:type>\n"
                + "</owl:Thing>\n" + "</rdf:RDF>";
        String id = "WebOnt_description_logic_667";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "DL Test: k_ph\n"
                + "ABox test from DL98 systems comparison.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_901() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/premises901\" >\n"
                + "  <owl:Ontology/>\n"
                + "  <owl:ObjectProperty rdf:ID=\"r\"/>\n"
                + "  <owl:ObjectProperty rdf:ID=\"p\">\n"
                + "    <rdfs:subPropertyOf rdf:resource=\"#r\"/>\n"
                + "    <rdfs:range>\n"
                + "      <owl:Class rdf:ID=\"A\"/></rdfs:range></owl:ObjectProperty>\n"
                + "  <owl:ObjectProperty rdf:ID=\"q\">\n"
                + "    <rdfs:subPropertyOf rdf:resource=\"#r\"/>\n"
                + "    <rdfs:range>\n"
                + "      <owl:Class rdf:ID=\"B\"/></rdfs:range></owl:ObjectProperty>\n"
                + "  <owl:Class rdf:about=\"#A\">\n"
                + "    <owl:disjointWith rdf:resource=\"#B\"/>\n"
                + "  </owl:Class></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/description-logic/premises901#\" xmlns:second=\"http://www.w3.org/2002/03owlt/description-logic/conclusions901#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/conclusions901\" >\n"
                + "  <owl:Ontology/>\n"
                + "  <owl:Class>\n"
                + "    <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"premises901#p\"/></owl:onProperty><owl:minCardinality rdf:datatype=\n"
                + "\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">2</owl:minCardinality></owl:Restriction><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"premises901#q\"/></owl:onProperty><owl:minCardinality rdf:datatype=\n"
                + "\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">3</owl:minCardinality></owl:Restriction></owl:intersectionOf>\n"
                + "    <rdfs:subClassOf><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"premises901#r\"/></owl:onProperty><owl:minCardinality rdf:datatype=\n"
                + "\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">5</owl:minCardinality></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + "</rdf:RDF>";
        String id = "WebOnt_description_logic_901";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "This entailment can be replicated for any three natural numbers i, j, k such that i+j >= k. In this example, they are chosen as 2, 3 and 5.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_902() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/premises902\" >\n"
                + "  <owl:Ontology/>\n"
                + "  <owl:ObjectProperty rdf:ID=\"r\"/>\n"
                + "  <owl:ObjectProperty rdf:ID=\"p\">\n"
                + "    <rdfs:subPropertyOf rdf:resource=\"#r\"/>\n"
                + "    <rdfs:range>\n"
                + "      <owl:Class rdf:ID=\"A\"/></rdfs:range></owl:ObjectProperty>\n"
                + "  <owl:ObjectProperty rdf:ID=\"q\">\n"
                + "    <rdfs:subPropertyOf rdf:resource=\"#r\"/>\n"
                + "    <rdfs:range>\n"
                + "      <owl:Class rdf:ID=\"B\"/></rdfs:range></owl:ObjectProperty>\n"
                + "  <owl:Class rdf:about=\"#A\">\n"
                + "    <owl:disjointWith rdf:resource=\"#B\"/>\n"
                + "  </owl:Class></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/nonconclusions902\" >\n"
                + "  <owl:Ontology/>\n"
                + "  <owl:Class>\n"
                + "    <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"premises902#p\"/></owl:onProperty><owl:minCardinality rdf:datatype=\n"
                + "\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">2</owl:minCardinality></owl:Restriction><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"premises902#q\"/></owl:onProperty><owl:minCardinality rdf:datatype=\n"
                + "\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">3</owl:minCardinality></owl:Restriction></owl:intersectionOf>\n"
                + "    <rdfs:subClassOf><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"premises902#r\"/></owl:onProperty><owl:minCardinality rdf:datatype=\n"
                + "\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">6</owl:minCardinality></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + "</rdf:RDF>";
        String id = "WebOnt_description_logic_902";
        TestClasses tc = TestClasses.valueOf("NEGATIVE_IMPL");
        String d = "This non-entailment can be replicated for any three natural numbers i, j, k such that i+j < k. In this example, they are chosen as 2, 3 and 6.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_905() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs= \"http://www.w3.org/2000/01/rdf-schema#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/consistent905\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:FunctionalProperty rdf:ID=\"p-N-to-1\" >\n"
                + "     <owl:inverseOf><owl:ObjectProperty rdf:ID=\"invP-1-to-N\" /></owl:inverseOf>\n"
                + "     <rdfs:domain rdf:resource=\"#cardinality-N\" />\n"
                + "     <rdfs:range rdf:resource=\"#only-d\" /></owl:FunctionalProperty>\n"
                + "   <owl:ObjectProperty rdf:about=\"#p-N-to-1\" />\n"
                + "   <owl:FunctionalProperty rdf:ID=\"q-M-to-1\" >\n"
                + "     <owl:inverseOf><owl:ObjectProperty  rdf:ID=\"invQ-1-to-M\" /></owl:inverseOf>\n"
                + "     <rdfs:domain rdf:resource=\"#cardinality-N-times-M\" />\n"
                + "     <rdfs:range rdf:resource=\"#cardinality-N\" /></owl:FunctionalProperty>\n"
                + "   <owl:ObjectProperty rdf:about=\"#q-M-to-1\" />\n"
                + "   <owl:FunctionalProperty rdf:ID=\"r-N-times-M-to-1\">\n"
                + "     <owl:inverseOf><owl:ObjectProperty  rdf:ID=\"invR-N-times-M-to-1\" /></owl:inverseOf>\n"
                + "     <rdfs:domain rdf:resource=\"#cardinality-N-times-M\" />\n"
                + "     <rdfs:range rdf:resource=\"#only-d\" /></owl:FunctionalProperty>\n"
                + "   <owl:ObjectProperty rdf:about=\"#r-N-times-M-to-1\"/>\n"
                + "    <owl:Class rdf:ID=\"only-d\"><owl:oneOf rdf:parseType=\"Collection\"><owl:Thing rdf:ID=\"d\"/></owl:oneOf><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#invP-1-to-N\"/><owl:cardinality rdf:datatype=\n"
                + "            \"http://www.w3.org/2001/XMLSchema#integer\">2</owl:cardinality></owl:Restriction></owl:equivalentClass><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#invR-N-times-M-to-1\"/><owl:cardinality rdf:datatype=\n"
                + "            \"http://www.w3.org/2001/XMLSchema#integer\">6</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:ID=\"cardinality-N\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#p-N-to-1\"/><owl:someValuesFrom rdf:resource=\"#only-d\"/></owl:Restriction></owl:equivalentClass><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#invQ-1-to-M\"/><owl:cardinality rdf:datatype=\n"
                + "               \"http://www.w3.org/2001/XMLSchema#integer\">3</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:ID=\"cardinality-N-times-M\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#q-M-to-1\"/><owl:someValuesFrom rdf:resource=\"#cardinality-N\"/></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"#cardinality-N-times-M\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#r-N-times-M-to-1\"/><owl:someValuesFrom rdf:resource=\"#only-d\"/></owl:Restriction></owl:equivalentClass></owl:Class></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_905";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "This test shows integer multiplication in OWL DL.\n"
                + "N is 2. M is 3. N times M is 6.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_908() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs= \"http://www.w3.org/2000/01/rdf-schema#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/consistent908\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:FunctionalProperty rdf:ID=\"p-N-to-1\" >\n"
                + "     <owl:inverseOf><owl:ObjectProperty rdf:ID=\"invP-1-to-N\" /></owl:inverseOf>\n"
                + "     <rdfs:domain rdf:resource=\"#cardinality-N\" />\n"
                + "     <rdfs:range rdf:resource=\"#infinite\" /></owl:FunctionalProperty>\n"
                + "   <owl:ObjectProperty rdf:about=\"#p-N-to-1\" />\n"
                + "   <owl:FunctionalProperty rdf:ID=\"q-M-to-1\" >\n"
                + "     <owl:inverseOf><owl:ObjectProperty  rdf:ID=\"invQ-1-to-M\" /></owl:inverseOf>\n"
                + "     <rdfs:domain rdf:resource=\"#cardinality-N-times-M\" />\n"
                + "     <rdfs:range rdf:resource=\"#cardinality-N\" /></owl:FunctionalProperty>\n"
                + "   <owl:ObjectProperty rdf:about=\"#q-M-to-1\" />\n"
                + "   <owl:FunctionalProperty rdf:ID=\"r-N-times-M-to-1\">\n"
                + "     <owl:inverseOf><owl:ObjectProperty  rdf:ID=\"invR-N-times-M-to-1\" /></owl:inverseOf>\n"
                + "     <rdfs:domain rdf:resource=\"#cardinality-N-times-M\" />\n"
                + "     <rdfs:range rdf:resource=\"#infinite\" /></owl:FunctionalProperty>\n"
                + "   <owl:ObjectProperty rdf:about=\"#r-N-times-M-to-1\"/>\n"
                + "    <owl:Class rdf:ID=\"infinite\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#invP-1-to-N\"/><owl:cardinality rdf:datatype=\n"
                + "            \"http://www.w3.org/2001/XMLSchema#integer\">2</owl:cardinality></owl:Restriction></owl:equivalentClass><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#invR-N-times-M-to-1\"/><owl:cardinality rdf:datatype=\n"
                + "            \"http://www.w3.org/2001/XMLSchema#integer\">5</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:ID=\"cardinality-N\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#p-N-to-1\"/><owl:someValuesFrom rdf:resource=\"#infinite\"/></owl:Restriction></owl:equivalentClass><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#invQ-1-to-M\"/><owl:cardinality rdf:datatype=\n"
                + "               \"http://www.w3.org/2001/XMLSchema#integer\">3</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:ID=\"cardinality-N-times-M\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#q-M-to-1\"/><owl:someValuesFrom rdf:resource=\"#cardinality-N\"/></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + "    <owl:Class rdf:about=\"#cardinality-N-times-M\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#r-N-times-M-to-1\"/><owl:someValuesFrom rdf:resource=\"#infinite\"/></owl:Restriction></owl:equivalentClass></owl:Class></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_908";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "This test shows integer multiplication in OWL DL, interacting with infinity.\n"
                + "N times infinity is 2  times infinity. \n"
                + "M times infinity is 3 times infinity. \n"
                + "N times M times infinity is 5 times infinity.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_differentFrom_001() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/differentFrom/premises001#\" xmlns:second=\"http://www.w3.org/2002/03owlt/differentFrom/conclusions001#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/differentFrom/premises001\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <rdf:Description rdf:about=\"premises001#a\"><owl:differentFrom rdf:resource=\"premises001#b\"/></rdf:Description></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/differentFrom/premises001#\" xmlns:second=\"http://www.w3.org/2002/03owlt/differentFrom/conclusions001#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/differentFrom/conclusions001\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <rdf:Description rdf:about=\"premises001#b\"><owl:differentFrom rdf:resource=\"premises001#a\"/></rdf:Description></rdf:RDF>";
        String id = "WebOnt_differentFrom_001";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "differentFrom is a SymmetricProperty.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_disjointWith_001() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:first=\"http://www.w3.org/2002/03owlt/disjointWith/premises001#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/disjointWith/premises001\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:Class rdf:ID=\"A\"><owl:disjointWith><owl:Class rdf:ID=\"B\"/></owl:disjointWith></owl:Class>\n"
                + "   <first:A rdf:ID=\"a\"/>\n"
                + "   <owl:Thing rdf:about=\"#a\"/>\n"
                + "   <first:B rdf:ID=\"b\"/>\n"
                + "   <owl:Thing rdf:about=\"#b\"/></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/disjointWith/premises001#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/disjointWith/conclusions001\" >\n"
                + "   <owl:Ontology/>\n"
                + "    <owl:Thing rdf:about=\"premises001#a\"><owl:differentFrom><owl:Thing rdf:about=\"premises001#b\"/></owl:differentFrom></owl:Thing></rdf:RDF>";
        String id = "WebOnt_disjointWith_001";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Disjoint classes have different members.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_disjointWith_003() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/disjointWith/consistent003\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:nodeID=\"A\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"K\"/><owl:Class rdf:ID=\"A\"/></owl:intersectionOf><owl:disjointWith rdf:nodeID=\"B\"/><owl:disjointWith rdf:nodeID=\"D\"/><owl:disjointWith rdf:nodeID=\"E\"/></owl:Class>\n"
                + "    <owl:Class rdf:nodeID=\"B\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"K\"/><owl:Class rdf:ID=\"B\"/></owl:intersectionOf><owl:disjointWith rdf:nodeID=\"A\"/><owl:disjointWith rdf:nodeID=\"C\"/><owl:disjointWith rdf:nodeID=\"E\"/></owl:Class>\n"
                + "    <owl:Class rdf:nodeID=\"C\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"K\"/><owl:Class rdf:ID=\"C\"/></owl:intersectionOf><owl:disjointWith rdf:nodeID=\"A\"/><owl:disjointWith rdf:nodeID=\"D\"/><owl:disjointWith rdf:nodeID=\"E\"/></owl:Class>\n"
                + "    <owl:Class rdf:nodeID=\"D\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"K\"/><owl:Class rdf:ID=\"D\"/></owl:intersectionOf><owl:disjointWith rdf:nodeID=\"C\"/><owl:disjointWith rdf:nodeID=\"B\"/><owl:disjointWith rdf:nodeID=\"E\"/></owl:Class>\n"
                + "    <owl:Class rdf:nodeID=\"E\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"K\"/><owl:Class rdf:ID=\"E\"/></owl:intersectionOf></owl:Class></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_disjointWith_003";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "If the owl:disjointWith edges in the graph form an undirected complete subgraph \n"
                + "then this may be within OWL DL.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_disjointWith_004() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/disjointWith/consistent004\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:nodeID=\"A\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"A\"/><owl:Class rdf:ID=\"K\"/></owl:intersectionOf><owl:disjointWith rdf:nodeID=\"B\"/><owl:disjointWith rdf:nodeID=\"D\"/><owl:disjointWith rdf:nodeID=\"E\"/></owl:Class>\n"
                + "    <owl:Class rdf:nodeID=\"B\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"B\"/><owl:Class rdf:ID=\"K\"/></owl:intersectionOf><owl:disjointWith rdf:nodeID=\"A\"/><owl:disjointWith rdf:nodeID=\"C\"/><owl:disjointWith rdf:nodeID=\"E\"/></owl:Class>\n"
                + "    <owl:Class rdf:nodeID=\"C\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"C\"/><owl:Class rdf:ID=\"K\"/></owl:intersectionOf><owl:disjointWith rdf:nodeID=\"A\"/><owl:disjointWith rdf:nodeID=\"E\"/></owl:Class>\n"
                + "    <owl:Class rdf:nodeID=\"D\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"D\"/><owl:Class rdf:ID=\"K\"/></owl:intersectionOf><owl:disjointWith rdf:nodeID=\"B\"/><owl:disjointWith rdf:nodeID=\"E\"/></owl:Class>\n"
                + "    <owl:Class rdf:nodeID=\"E\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"K\"/><owl:Class rdf:ID=\"E\"/></owl:intersectionOf></owl:Class></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_disjointWith_004";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "This example has owl:disjointWith edges in the graph which cannot be generated\n"
                + "by the mapping rules for DisjointClasses. Consider the lack of owl:disjointWith edge\n"
                + "between nodes C and D.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_disjointWith_005() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/disjointWith/consistent005\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:nodeID=\"A\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"A\"/><owl:Class rdf:ID=\"K\"/></owl:intersectionOf><owl:disjointWith rdf:nodeID=\"D\"/><owl:disjointWith rdf:nodeID=\"E\"/></owl:Class>\n"
                + "    <owl:Class rdf:nodeID=\"B\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"B\"/><owl:Class rdf:ID=\"K\"/></owl:intersectionOf><owl:disjointWith rdf:nodeID=\"C\"/></owl:Class>\n"
                + "    <owl:Class rdf:nodeID=\"C\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"C\"/><owl:Class rdf:ID=\"K\"/></owl:intersectionOf></owl:Class>\n"
                + "    <owl:Class rdf:nodeID=\"D\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"D\"/><owl:Class rdf:ID=\"K\"/></owl:intersectionOf><owl:disjointWith rdf:nodeID=\"E\"/></owl:Class>\n"
                + "    <owl:Class rdf:nodeID=\"E\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"E\"/><owl:Class rdf:ID=\"K\"/></owl:intersectionOf><owl:disjointWith rdf:nodeID=\"D\"/></owl:Class></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_disjointWith_005";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "If the owl:disjointWith edges in the graph form unconnected\n"
                + "undirected complete subgraphs\n"
                + "then this may be within OWL DL.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_disjointWith_006() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/disjointWith/consistent006\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:nodeID=\"A\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"A\"/><owl:Class rdf:ID=\"K\"/></owl:intersectionOf><owl:disjointWith rdf:nodeID=\"D\"/><owl:disjointWith rdf:nodeID=\"E\"/></owl:Class>\n"
                + "    <owl:Class rdf:nodeID=\"B\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"K\"/><owl:Class rdf:ID=\"B\"/></owl:intersectionOf><owl:disjointWith rdf:nodeID=\"C\"/><owl:disjointWith rdf:nodeID=\"A\"/></owl:Class>\n"
                + "    <owl:Class rdf:nodeID=\"C\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"K\"/><owl:Class rdf:ID=\"C\"/></owl:intersectionOf><owl:disjointWith rdf:nodeID=\"A\"/></owl:Class>\n"
                + "    <owl:Class rdf:nodeID=\"D\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"K\"/><owl:Class rdf:ID=\"D\"/></owl:intersectionOf><owl:disjointWith rdf:nodeID=\"E\"/></owl:Class>\n"
                + "    <owl:Class rdf:nodeID=\"E\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"K\"/><owl:Class rdf:ID=\"E\"/></owl:intersectionOf><owl:disjointWith rdf:nodeID=\"D\"/></owl:Class></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_disjointWith_006";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "If the owl:disjointWith edges in the graph form\n"
                + "undirected complete subgraphs which share blank nodes\n"
                + "then this was not within OWL DL, but is permissible in OWL 2 DL.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_disjointWith_007() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/disjointWith/consistent007\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:ID=\"A\"><owl:disjointWith rdf:nodeID=\"D\"/><owl:disjointWith rdf:nodeID=\"E\"/></owl:Class>\n"
                + "    <owl:Class rdf:nodeID=\"B\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"K\"/><owl:Class rdf:ID=\"B\"/></owl:intersectionOf><owl:disjointWith rdf:nodeID=\"C\"/><owl:disjointWith rdf:resource=\"#A\"/></owl:Class>\n"
                + "    <owl:Class rdf:nodeID=\"C\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"K\"/><owl:Class rdf:ID=\"C\"/></owl:intersectionOf><owl:disjointWith rdf:resource=\"#A\"/></owl:Class>\n"
                + "    <owl:Class rdf:nodeID=\"D\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"K\"/><owl:Class rdf:ID=\"D\"/></owl:intersectionOf><owl:disjointWith rdf:nodeID=\"E\"/></owl:Class>\n"
                + "    <owl:Class rdf:nodeID=\"E\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"K\"/><owl:Class rdf:ID=\"E\"/></owl:intersectionOf><owl:disjointWith rdf:nodeID=\"D\"/></owl:Class></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_disjointWith_007";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "If the owl:disjointWith edges in the graph form\n"
                + "undirected complete subgraphs which share URIref nodes\n"
                + "but do not share blank node\n"
                + "then this may be within OWL DL.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_disjointWith_008() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/disjointWith/consistent008\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:ID=\"A\"/>\n"
                + "    <owl:Class rdf:nodeID=\"B\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"K\"/><owl:Class rdf:ID=\"B\"/></owl:intersectionOf><owl:disjointWith rdf:resource=\"#A\"/></owl:Class>\n"
                + "    <owl:Class rdf:nodeID=\"C\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"K\"/><owl:Class rdf:ID=\"C\"/></owl:intersectionOf><owl:disjointWith rdf:resource=\"#A\"/></owl:Class>\n"
                + "    <owl:Class rdf:ID=\"D\"><owl:disjointWith rdf:nodeID=\"B\"/><owl:disjointWith rdf:nodeID=\"C\"/></owl:Class></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_disjointWith_008";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "A further example that cannot be generated from the mapping rule\n"
                + "for DisjointClasses.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_disjointWith_009() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/disjointWith/consistent009\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:ID=\"A\"/>\n"
                + "    <owl:Class rdf:nodeID=\"B\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"K\"/><owl:Class rdf:ID=\"B\"/></owl:intersectionOf><owl:disjointWith rdf:resource=\"#A\"/></owl:Class>\n"
                + "    <owl:Class rdf:nodeID=\"C\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"K\"/><owl:Class rdf:ID=\"C\"/></owl:intersectionOf><owl:disjointWith rdf:resource=\"#A\"/></owl:Class>\n"
                + "    <owl:Class rdf:ID=\"D\"><owl:disjointWith rdf:nodeID=\"B\"/><owl:disjointWith rdf:nodeID=\"C\"/><owl:disjointWith rdf:resource=\"#A\"/></owl:Class></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_disjointWith_009";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "If the owl:disjointWith edges in the graph form\n"
                + "undirected complete subgraphs which share URIref nodes\n"
                + "but do not share blank node\n"
                + "then this may be within OWL DL.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_equivalentClass_001() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/equivalentClass/premises001#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentClass/premises001\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:Class rdf:ID=\"Car\">\n"
                + "     <owl:equivalentClass><owl:Class rdf:ID=\"Automobile\"/></owl:equivalentClass></owl:Class>\n"
                + "  <first:Car rdf:ID=\"car\">\n"
                + "     <rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#Thing\" /></first:Car>\n"
                + "  <first:Automobile rdf:ID=\"auto\">\n"
                + "     <rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#Thing\" /></first:Automobile></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/equivalentClass/premises001#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentClass/conclusions001\" >\n"
                + "  <owl:Ontology/>\n"
                + "  <first:Car rdf:about=\"premises001#auto\">\n"
                + "     <rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#Thing\" /></first:Car>\n"
                + "  <first:Automobile rdf:about=\"premises001#car\">\n"
                + "     <rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#Thing\" /></first:Automobile>\n"
                + "   <owl:Class rdf:about=\"premises001#Car\"/>\n"
                + "   <owl:Class rdf:about=\"premises001#Automobile\"/>\n"
                + "</rdf:RDF>";
        String id = "WebOnt_equivalentClass_001";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Two classes may have the same class extension.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_equivalentClass_002() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentClass/premises002\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:Class rdf:ID=\"Car\">\n"
                + "     <owl:equivalentClass><owl:Class rdf:ID=\"Automobile\"/></owl:equivalentClass></owl:Class>\n"
                + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentClass/conclusions002\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:Class rdf:about=\"premises002#Car\">\n"
                + "     <rdfs:subClassOf><owl:Class rdf:about=\"premises002#Automobile\"><rdfs:subClassOf rdf:resource=\"premises002#Car\" /></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "</rdf:RDF>";
        String id = "WebOnt_equivalentClass_002";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Two classes may be different names for the same set of individuals";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_equivalentClass_003() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentClass/premises003\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:Class rdf:about=\"conclusions003#Car\">\n"
                + "     <rdfs:subClassOf><owl:Class rdf:about=\"conclusions003#Automobile\"><rdfs:subClassOf rdf:resource=\"conclusions003#Car\" /></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentClass/conclusions003\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:Class rdf:ID=\"Car\">\n"
                + "     <owl:equivalentClass><owl:Class rdf:ID=\"Automobile\"/></owl:equivalentClass></owl:Class>\n"
                + "</rdf:RDF>";
        String id = "WebOnt_equivalentClass_003";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Two classes may be different names for the same set of individuals";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_equivalentClass_004() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentClass/premises004\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:ID=\"c1\"><owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "    <owl:Class rdf:ID=\"c3\"/><owl:Restriction><owl:onProperty rdf:resource=\"#p\"/><owl:cardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:cardinality></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + "    <owl:Class rdf:ID=\"c2\"><owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "    <owl:Class rdf:ID=\"c3\"/><owl:Restriction><owl:onProperty rdf:resource=\"#p\"/><owl:cardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:cardinality></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + "     <owl:ObjectProperty rdf:ID=\"p\"/>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentClass/conclusions004\" >\n"
                + "  <owl:Ontology/>\n"
                + "  <owl:Class rdf:about=\"premises004#c1\">\n"
                + "     <owl:equivalentClass><owl:Class rdf:about=\"premises004#c2\"/></owl:equivalentClass></owl:Class>\n"
                + "</rdf:RDF>";
        String id = "WebOnt_equivalentClass_004";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Two classes with the same complete description are equivalent.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_equivalentClass_005() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentClass/premises005\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:ID=\"c1\"><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#p\"/><owl:cardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:cardinality></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:ID=\"c2\"><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#p\"/><owl:cardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:cardinality></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + "     <owl:ObjectProperty rdf:ID=\"p\"/>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentClass/nonconclusions005\" >\n"
                + "    <owl:Ontology/>\n"
                + "<owl:Class rdf:about=\"premises005#c1\">\n"
                + "     <owl:equivalentClass><owl:Class rdf:about=\"premises005#c2\"/></owl:equivalentClass></owl:Class>\n"
                + "</rdf:RDF>";
        String id = "WebOnt_equivalentClass_005";
        TestClasses tc = TestClasses.valueOf("NEGATIVE_IMPL");
        String d = "Two classes with the same partial description are not equivalent.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_equivalentClass_006() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentClass/premises006\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:Class rdf:ID=\"A\"/>\n"
                + "   <owl:Class rdf:ID=\"B\"/>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentClass/conclusions006\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf rdf:resource=\"premises006#A\"/></owl:Class><owl:Class><owl:complementOf rdf:resource=\"premises006#B\"/></owl:Class></owl:intersectionOf><owl:equivalentClass><owl:Class><owl:complementOf><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><rdf:Description rdf:about=\"premises006#A\"/><rdf:Description rdf:about=\"premises006#B\"/></owl:unionOf></owl:Class></owl:complementOf></owl:Class></owl:equivalentClass></owl:Class>   \n"
                + "   <owl:Class rdf:about=\"premises006#A\"/>\n"
                + "   <owl:Class rdf:about=\"premises006#B\"/>\n"
                + "</rdf:RDF>";
        String id = "WebOnt_equivalentClass_006";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "De Morgan's law.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_equivalentClass_008_Direct() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/equivalentClass/premises008#\" \n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentClass/premises008\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:Class rdf:ID=\"c1\"><owl:equivalentClass><owl:Class rdf:ID=\"c2\"/></owl:equivalentClass>\n"
                + "     <first:annotate>description of c1</first:annotate></owl:Class>\n"
                + "   <owl:AnnotationProperty rdf:ID=\"annotate\" />\n"
                + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/equivalentClass/premises008#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/equivalentClass/nonconclusions008\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:Class rdf:about=\"premises008#c2\">\n"
                + "     <first:annotate>description of c1</first:annotate></owl:Class>\n"
                + "   <owl:AnnotationProperty rdf:about=\"premises008#annotate\" /></rdf:RDF>";
        String id = "WebOnt_equivalentClass_008_Direct";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "A version of WebOnt-equivalentClass-008 modified for the Direct Semantics, under which annotations in the entailed ontology are ignored.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_equivalentClass_009() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/equivalentClass/consistent009\" >\n"
                + "   <owl:Ontology/>\n"
                + " <owl:Class rdf:nodeID=\"a\">\n"
                + "   <owl:oneOf rdf:parseType=\"Collection\"><owl:Thing rdf:ID=\"A\"/></owl:oneOf>\n"
                + "   <owl:equivalentClass>   \n"
                + "     <owl:Class rdf:nodeID=\"b\"><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"J\"/><owl:Class rdf:ID=\"B\"/></owl:unionOf></owl:Class></owl:equivalentClass>  \n"
                + "   <owl:equivalentClass>    \n"
                + "     <owl:Class rdf:nodeID=\"c\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"K\"/><owl:Class rdf:ID=\"C\"/></owl:intersectionOf></owl:Class></owl:equivalentClass>  \n"
                + "   <owl:equivalentClass>    \n"
                + "     <owl:Class rdf:nodeID=\"d\"><owl:complementOf><owl:Class rdf:ID=\"D\"/></owl:complementOf></owl:Class></owl:equivalentClass>  \n"
                + " </owl:Class>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_equivalentClass_009";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "A possible mapping of the EquivalentClasses axiom,\n"
                + "which is connected but without a Hamiltonian path.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_equivalentProperty_001() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/equivalentProperty/premises001#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentProperty/premises001\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:ObjectProperty rdf:ID=\"hasHead\"><owl:equivalentProperty><owl:ObjectProperty rdf:ID=\"hasLeader\"/></owl:equivalentProperty></owl:ObjectProperty>\n"
                + "   <owl:Thing rdf:ID=\"X\">\n"
                + "     <first:hasLeader><owl:Thing rdf:ID=\"Y\"/></first:hasLeader></owl:Thing>\n"
                + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/equivalentProperty/premises001#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentProperty/conclusions001\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:Thing rdf:about=\"premises001#X\">\n"
                + "     <first:hasHead><owl:Thing rdf:about=\"premises001#Y\"/></first:hasHead></owl:Thing>   \n"
                + "   <owl:ObjectProperty rdf:about=\"premises001#hasHead\"/></rdf:RDF>";
        String id = "WebOnt_equivalentProperty_001";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "hasLeader may be stated to be the owl:equivalentProperty of hasHead.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_equivalentProperty_002() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/equivalentProperty/premises002#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentProperty/premises002\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:ObjectProperty rdf:ID=\"hasHead\"><owl:equivalentProperty><owl:ObjectProperty rdf:ID=\"hasLeader\"/></owl:equivalentProperty></owl:ObjectProperty></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/equivalentProperty/premises002#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentProperty/conclusions002\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:ObjectProperty rdf:about=\"premises002#hasHead\"><rdfs:subPropertyOf rdf:resource=\"premises002#hasLeader\"/></owl:ObjectProperty>\n"
                + "   <owl:ObjectProperty rdf:about=\"premises002#hasLeader\"><rdfs:subPropertyOf rdf:resource=\"premises002#hasHead\"/></owl:ObjectProperty></rdf:RDF>";
        String id = "WebOnt_equivalentProperty_002";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "A reasoner can also deduce that hasLeader is a subProperty of hasHead and hasHead is a subProperty of hasLeader.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_equivalentProperty_003() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentProperty/premises003\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:ObjectProperty rdf:about=\"conclusions003#hasHead\"><rdfs:subPropertyOf rdf:resource=\"conclusions003#hasLeader\"/></owl:ObjectProperty>\n"
                + "   <owl:ObjectProperty rdf:about=\"conclusions003#hasLeader\"><rdfs:subPropertyOf rdf:resource=\"conclusions003#hasHead\"/></owl:ObjectProperty></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentProperty/conclusions003\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:ObjectProperty rdf:ID=\"hasHead\"><owl:equivalentProperty><owl:ObjectProperty rdf:ID=\"hasLeader\"/></owl:equivalentProperty></owl:ObjectProperty></rdf:RDF>";
        String id = "WebOnt_equivalentProperty_003";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "The inverse entailment of test 002 also holds.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_equivalentProperty_004() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentProperty/premises004\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:ObjectProperty rdf:ID=\"p\"><rdfs:domain rdf:resource=\"#d\"/></owl:ObjectProperty>\n"
                + "   <owl:ObjectProperty rdf:ID=\"q\"><rdfs:domain rdf:resource=\"#d\"/></owl:ObjectProperty>\n"
                + "   <owl:FunctionalProperty rdf:about=\"#q\"/>\n"
                + "   <owl:FunctionalProperty rdf:about=\"#p\"/>\n"
                + "   <owl:Thing rdf:ID=\"v\"/>\n"
                + "   <owl:Class rdf:ID=\"d\">\n"
                + "     <owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#p\"/><owl:hasValue rdf:resource=\"#v\"/></owl:Restriction></owl:equivalentClass>\n"
                + "     <owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#q\"/><owl:hasValue rdf:resource=\"#v\"/></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentProperty/conclusions004\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:ObjectProperty rdf:about=\"premises004#p\"><owl:equivalentProperty><owl:ObjectProperty rdf:about=\"premises004#q\"/></owl:equivalentProperty></owl:ObjectProperty></rdf:RDF>";
        String id = "WebOnt_equivalentProperty_004";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "If p and q have the same property extension then p equivalentProperty q.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_imports_011() {
        String premise = "<rdf:RDF "
                + "xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#' "
                + "xmlns:rdfs='http://www.w3.org/2000/01/rdf-schema#' "
                + "xmlns:owl='http://www.w3.org/2002/07/owl#' "
                + "    xml:base='http://www.w3.org/2002/03owlt/imports/premises011' >\n"
                + "    <owl:Ontology rdf:about=''></owl:Ontology>\n"
                + "<owl:Class rdf:about='urn:test#Man'><rdfs:subClassOf rdf:resource='urn:test#Mortal'/></owl:Class>\n"
                + "    <owl:Class rdf:about='urn:test#Mortal'/>\n"
                + "    <owl:Thing rdf:about='urn:test#Socrates'><rdf:type><owl:Class rdf:about='urn:test#Man'/></rdf:type></owl:Thing></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#' xmlns:rdfs='http://www.w3.org/2000/01/rdf-schema#' xmlns:owl='http://www.w3.org/2002/07/owl#'\n"
                + "    xml:base='http://www.w3.org/2002/03owlt/imports/conclusions011' >\n"
                + "    <owl:Ontology/>\n"
                + "    <rdf:Description rdf:about='urn:test#Socrates'><rdf:type><owl:Class rdf:about='urn:test#Mortal'/></rdf:type></rdf:Description></rdf:RDF>";
        String id = "WebOnt_imports_011";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "A Lite version of test <a xmlns=\"http://www.w3.org/1999/xhtml\" href=\"#imports-001\">imports-001</a>.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_maxCardinality_001() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/maxCardinality/inconsistent001#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/maxCardinality/inconsistent001\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <rdf:Description rdf:about=\"inconsistent001#sb1\"><rdf:type rdf:parseType=\"Resource\"><rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#Restriction\"/><owl:maxCardinality\n"
                + "              rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">2</owl:maxCardinality><owl:onProperty rdf:resource=\"inconsistent001#prop\"/></rdf:type><first:prop rdf:resource=\"inconsistent001#ob1\"/><first:prop rdf:resource=\"inconsistent001#ob2\"/><first:prop rdf:resource=\"inconsistent001#ob3\"/></rdf:Description>\n"
                + "    <rdf:Description rdf:about=\"inconsistent001#ob1\"><owl:differentFrom rdf:resource=\"inconsistent001#ob2\"/><owl:differentFrom rdf:resource=\"inconsistent001#ob3\"/></rdf:Description>\n"
                + "    <rdf:Description rdf:about=\"inconsistent001#ob2\"><owl:differentFrom rdf:resource=\"inconsistent001#ob3\"/></rdf:Description>\n"
                + "    <owl:ObjectProperty rdf:about=\"inconsistent001#prop\"/></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_maxCardinality_001";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "A property with maximum cardinality of two cannot take\n"
                + "three distinct values on some subject node.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_miscellaneous_102() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/miscellaneous/consistent102#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/miscellaneous/consistent102\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:Thing rdf:ID=\"i\">\n"
                + "     <rdf:type><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:ID=\"p\"/></owl:onProperty><owl:allValuesFrom><owl:Class rdf:ID=\"a\"/></owl:allValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#p\"/></owl:onProperty><owl:someValuesFrom><owl:Class rdf:ID=\"s\"/></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></rdf:type></owl:Thing>\n"
                + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_miscellaneous_102";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "Abstract syntax restrictions with multiple components\n"
                + "are in OWL DL.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_miscellaneous_103() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/miscellaneous/consistent103#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/miscellaneous/consistent103\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:Thing rdf:ID=\"i\">\n"
                + "     <rdf:type><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:ID=\"p\"/></owl:onProperty><owl:allValuesFrom><owl:Class rdf:ID=\"a\"/></owl:allValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#q\"/></owl:onProperty><owl:someValuesFrom><owl:Class rdf:ID=\"s\"/></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></rdf:type></owl:Thing>\n"
                + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_miscellaneous_103";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "This description cannot be expressed as a multicomponent restriction\n"
                + "in the OWL 1 abstract syntax.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_miscellaneous_302_Direct() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/miscellaneous/premises302#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/miscellaneous/premises302\" >\n"
                + "<owl:Ontology/>\n"
                + "<owl:AnnotationProperty rdf:ID=\"prop\" />\n"
                + "<owl:Thing rdf:about=\"#a\">\n"
                + "   <first:prop>foo</first:prop></owl:Thing>\n"
                + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/miscellaneous/premises302#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/miscellaneous/nonconclusions302\" >\n"
                + "<owl:Ontology/>\n"
                + "<owl:AnnotationProperty rdf:about=\"premises302#prop\" />\n"
                + "<owl:Thing rdf:about=\"premises302#a\">\n"
                + "   <first:prop>bar</first:prop>\n"
                + "</owl:Thing>\n"
                + "</rdf:RDF>";
        String id = "WebOnt_miscellaneous_302_Direct";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "A version of WebOnt-miscellaneous-302 applicable under the Direct Semantics, in which the annotation in the entailed ontology is not considered.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_miscellaneous_303() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/miscellaneous/consistent303\" >\n"
                + "  <owl:Ontology/>\n"
                + "  <owl:AnnotationProperty rdf:about='http://purl.org/dc/elements/1.0/creator'/></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_miscellaneous_303";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "dc:creator may be declared as an annotation property.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_oneOf_001() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/oneOf/consistent001#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/oneOf/consistent001\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:Class><owl:oneOf rdf:parseType=\"Collection\"><rdf:Description rdf:ID=\"amy\"/><rdf:Description rdf:ID=\"bob\"/><rdf:Description rdf:ID=\"caroline\"/></owl:oneOf><owl:equivalentClass><owl:Class><owl:oneOf rdf:parseType=\"Collection\"><rdf:Description rdf:ID=\"yolanda\"/><rdf:Description rdf:ID=\"zebedee\"/></owl:oneOf></owl:Class></owl:equivalentClass></owl:Class></rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_oneOf_001";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "oneOf <em>does not</em> indicate that the named\n"
                + "individuals are distinct. Thus a consistent interpretation\n"
                + "of this file is when all the individual names denote the\n"
                + "same individual.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_sameAs_001() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/sameAs/premises001#\"\n"
                + "  xml:base=\"http://www.w3.org/2002/03owlt/sameAs/premises001\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:Class rdf:ID=\"c1\">\n"
                + "     <owl:sameAs>\n"
                + "       <owl:Class rdf:ID=\"c2\"/></owl:sameAs>\n"
                + "     <first:annotate>description of c1</first:annotate></owl:Class>\n"
                + "   <owl:AnnotationProperty rdf:ID=\"annotate\" />\n"
                + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/sameAs/premises001#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/sameAs/conclusions001\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:Class rdf:about=\"premises001#c2\">\n"
                + "     <first:annotate>description of c1</first:annotate></owl:Class>\n"
                + "   <owl:AnnotationProperty rdf:about=\"premises001#annotate\" /></rdf:RDF>";
        String id = "WebOnt_sameAs_001";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Annotation properties refer to a class instance. sameAs, in OWL Full, also refers to the class instance.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_unionOf_003() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/unionOf/premises003\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:Class rdf:ID=\"A\">\n"
                + "     <owl:oneOf rdf:parseType=\"Collection\"><owl:Thing rdf:ID=\"a\"/></owl:oneOf></owl:Class>\n"
                + "   <owl:Class rdf:ID=\"B\">\n"
                + "     <owl:oneOf rdf:parseType=\"Collection\"><owl:Thing rdf:ID=\"b\"/></owl:oneOf></owl:Class>\n"
                + "   <owl:Class rdf:ID=\"A-and-B\">\n"
                + "     <owl:oneOf rdf:parseType=\"Collection\"><owl:Thing rdf:about=\"#a\"/><owl:Thing rdf:about=\"#b\"/></owl:oneOf></owl:Class>\n"
                + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/unionOf/conclusions003\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:Class rdf:about=\"premises003#A-and-B\">\n"
                + "     <owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"premises003#A\"/><owl:Class rdf:about=\"premises003#B\"/></owl:unionOf></owl:Class>\n"
                + "</rdf:RDF>";
        String id = "WebOnt_unionOf_003";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Sets with appropriate extensions are related by unionOf.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testWebOnt_unionOf_004() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/unionOf/premises004#\" xmlns:second=\"http://www.w3.org/2002/03owlt/unionOf/conclusions004#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/unionOf/premises004\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:Class rdf:about=\"#A-and-B\">\n"
                + "     <owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"#A\"/><owl:Class rdf:about=\"#B\"/></owl:unionOf></owl:Class>\n"
                + "   <owl:Class rdf:ID=\"A\">\n"
                + "     <owl:oneOf rdf:parseType=\"Collection\"><owl:Thing rdf:ID=\"a\"/></owl:oneOf></owl:Class>\n"
                + "   <owl:Class rdf:ID=\"B\">\n"
                + "     <owl:oneOf rdf:parseType=\"Collection\"><owl:Thing rdf:ID=\"b\"/></owl:oneOf></owl:Class>\n"
                + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/unionOf/premises004#\" xmlns:second=\"http://www.w3.org/2002/03owlt/unionOf/conclusions004#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/unionOf/conclusions004\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:Class rdf:about=\"premises004#A-and-B\">\n"
                + "     <owl:oneOf rdf:parseType=\"Collection\"><owl:Thing rdf:about=\"premises004#a\"/><owl:Thing rdf:about=\"premises004#b\"/></owl:oneOf></owl:Class>\n"
                + "</rdf:RDF>";
        String id = "WebOnt_unionOf_004";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "An inverse to test <a xmlns=\"http://www.w3.org/1999/xhtml\" href=\"#unionOf-003\">003</a>.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }
}
