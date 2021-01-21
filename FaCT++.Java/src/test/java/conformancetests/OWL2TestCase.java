package conformancetests;

import org.junit.Ignore;
/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import org.junit.Test;

import testbase.TestBase;

@SuppressWarnings("javadoc")
public class OWL2TestCase extends TestBase {

    @Test
    public void testowl2_rl_anonymous_individual() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:j.0=\"http://owl2.test/rules#\">\n"
                + "  <owl:Ontology />\n"
                + "  <owl:ObjectProperty rdf:about=\"http://owl2.test/rules#op\"/>\n"
                + "  <owl:NamedIndividual rdf:about=\"http://owl2.test/rules#I\"/>\n"
                + "  <owl:NamedIndividual>\n"
                + "    <j.0:op rdf:resource=\"http://owl2.test/rules#I\"/></owl:NamedIndividual></rdf:RDF>";
        String conclusion = "";
        String id = "owl2_rl_anonymous_individual";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "OWL 2 RL allows anonymous individual.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testowl2_rl_invalid_leftside_allvaluesfrom() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Ontology />\n"
                + "  <owl:Class rdf:about=\"http://owl2.test/rules#C\"/>\n"
                + "  <owl:Class rdf:about=\"http://owl2.test/rules#C1\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://owl2.test/rules#op\"/>\n"
                + "  <owl:Restriction>\n"
                + "    <owl:allValuesFrom rdf:resource=\"http://owl2.test/rules#C1\"/>\n"
                + "    <owl:onProperty rdf:resource=\"http://owl2.test/rules#op\"/>\n"
                + "    <rdfs:subClassOf rdf:resource=\"http://owl2.test/rules#C\"/></owl:Restriction></rdf:RDF>";
        String conclusion = "";
        String id = "owl2_rl_invalid_leftside_allvaluesfrom";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "OWL 2 RL does not allow left side allValuesFrom in a subClassOf axiom.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testowl2_rl_invalid_leftside_maxcard() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Ontology />\n"
                + "  <owl:Class rdf:about=\"http://owl2.test/rules#C\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://owl2.test/rules#op\"/>\n"
                + "  <owl:Restriction>\n"
                + "    <rdfs:subClassOf rdf:resource=\"http://owl2.test/rules#C\"/>\n"
                + "    <owl:maxCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">2</owl:maxCardinality>\n"
                + "    <owl:onProperty rdf:resource=\"http://owl2.test/rules#op\"/></owl:Restriction></rdf:RDF>";
        String conclusion = "";
        String id = "owl2_rl_invalid_leftside_maxcard";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "Invalid OWL 2 RL due to maxCardinality usage.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testowl2_rl_invalid_oneof() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\">\n"
                + "  <owl:Ontology />\n"
                + "  <owl:Class rdf:about=\"http://owl2.test/rules#Cb\">\n"
                + "    <owl:oneOf rdf:parseType=\"Collection\">\n"
                + "      <owl:Thing rdf:about=\"http://owl2.test/rules#X\"/>\n"
                + "      <owl:Thing rdf:about=\"http://owl2.test/rules#Y\"/></owl:oneOf></owl:Class>\n"
                + "</rdf:RDF>";
        String conclusion = "";
        String id = "owl2_rl_invalid_oneof";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "OWL 2 RL does not permit owl:oneOf to define a named class (it can be used as a subclass expression).";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testowl2_rl_invalid_owlreal() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Ontology />\n"
                + "  <owl:Class rdf:about=\"http://owl2.test/rules#C_Sub\">\n"
                + "    <rdfs:subClassOf>\n"
                + "      <owl:Restriction>\n"
                + "        <owl:allValuesFrom rdf:resource=\"http://www.w3.org/2002/07/owl#real\"/>\n"
                + "        <owl:onProperty>\n"
                + "          <owl:DatatypeProperty rdf:about=\"http://owl2.test/rules#p\"/></owl:onProperty></owl:Restriction></rdfs:subClassOf>\n"
                + "  </owl:Class></rdf:RDF>";
        String conclusion = "";
        String id = "owl2_rl_invalid_owlreal";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "Invalid OWL 2 RL because owl:real is used.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testowl2_rl_invalid_rightside_somevaluesfrom() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\">\n"
                + "  <owl:Ontology />\n"
                + "  <owl:Class rdf:about=\"http://owl2.test/rules#C_Sub\">\n"
                + "    <rdfs:subClassOf>\n"
                + "      <owl:Restriction>\n"
                + "        <owl:someValuesFrom>\n"
                + "          <owl:Class rdf:about=\"http://owl2.test/rules#C1\"/></owl:someValuesFrom>\n"
                + "        <owl:onProperty>\n"
                + "          <owl:ObjectProperty rdf:about=\"http://owl2.test/rules#p\"/></owl:onProperty></owl:Restriction></rdfs:subClassOf>\n"
                + "  </owl:Class></rdf:RDF>";
        String conclusion = "";
        String id = "owl2_rl_invalid_rightside_somevaluesfrom";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "This is not a valid OWL 2 RL because someValuesFrom shows up on the right hand side of a SubClassOf axiom.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testowl2_rl_invalid_rightside_unionof() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Ontology />\n"
                + "  <owl:Class rdf:about=\"http://owl2.test/rules#C_Sub\">\n"
                + "    <rdfs:subClassOf>\n"
                + "      <owl:Class>\n"
                + "        <owl:unionOf rdf:parseType=\"Collection\">\n"
                + "          <owl:Class rdf:about=\"http://owl2.test/rules#C1\"/>\n"
                + "          <owl:Class rdf:about=\"http://owl2.test/rules#C2\"/></owl:unionOf></owl:Class></rdfs:subClassOf>\n"
                + "  </owl:Class></rdf:RDF>";
        String conclusion = "";
        String id = "owl2_rl_invalid_rightside_unionof";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "Incorrect OWL 2 RL syntax. unionOf shows up at the right hand side of a SubClassOf axiom.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testowl2_rl_invalid_unionof() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\">\n"
                + "  <owl:Ontology />\n"
                + "  <owl:Class rdf:about=\"http://owl2.test/rules#C\">\n"
                + "    <owl:unionOf rdf:parseType=\"Collection\">\n"
                + "      <owl:Class rdf:about=\"http://owl2.test/rules#C1\"/>\n"
                + "      <owl:Class rdf:about=\"http://owl2.test/rules#C2\"/></owl:unionOf></owl:Class>\n"
                + "</rdf:RDF>";
        String conclusion = "";
        String id = "owl2_rl_invalid_unionof";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "OWL 2 RL does not allow owl:unionOf to define a named class (it can be used as a subclass expression).";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testowl2_rl_rules_fp_differentFrom() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:j.0=\"http://owl2.test/rules/\">\n"
                + "  <owl:Ontology />\n"
                + "  <owl:FunctionalProperty rdf:about=\"http://owl2.test/rules/fp\">\n"
                + "    <rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#ObjectProperty\"/></owl:FunctionalProperty>\n"
                + "  <owl:NamedIndividual rdf:about=\"http://owl2.test/rules/Y2\">\n"
                + "    <j.0:fp>\n"
                + "      <owl:NamedIndividual rdf:about=\"http://owl2.test/rules/X2\"/></j.0:fp></owl:NamedIndividual>\n"
                + "  <owl:NamedIndividual rdf:about=\"http://owl2.test/rules/X1\">\n"
                + "    <owl:differentFrom rdf:resource=\"http://owl2.test/rules/X2\"/></owl:NamedIndividual>\n"
                + "  <owl:NamedIndividual rdf:about=\"http://owl2.test/rules/Y1\">\n"
                + "    <j.0:fp rdf:resource=\"http://owl2.test/rules/X1\"/></owl:NamedIndividual></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\">\n"
                + "  <owl:Ontology />\n"
                + "  <owl:NamedIndividual rdf:about=\"http://owl2.test/rules/Y2\"/>\n"
                + "  <owl:NamedIndividual rdf:about=\"http://owl2.test/rules/Y1\">\n"
                + "    <owl:differentFrom rdf:resource=\"http://owl2.test/rules/Y2\"/></owl:NamedIndividual></rdf:RDF>";
        String id = "owl2_rl_rules_fp_differentFrom";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "This test checks the interaction between an OWL functional property and differentFrom assertions.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testowl2_rl_rules_ifp_differentFrom() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:j.0=\"http://owl2.test/rules/\">\n"
                + "  <owl:Ontology />\n"
                + "  <owl:InverseFunctionalProperty rdf:about=\"http://owl2.test/rules/ifp\">\n"
                + "    <rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#ObjectProperty\"/></owl:InverseFunctionalProperty>\n"
                + "  <owl:NamedIndividual rdf:about=\"http://owl2.test/rules/Y2\">\n"
                + "    <j.0:ifp>\n"
                + "      <owl:NamedIndividual rdf:about=\"http://owl2.test/rules/X2\"/></j.0:ifp></owl:NamedIndividual>\n"
                + "  <owl:NamedIndividual rdf:about=\"http://owl2.test/rules/X1\"/>\n"
                + "  <owl:NamedIndividual rdf:about=\"http://owl2.test/rules/Y1\">\n"
                + "    <owl:differentFrom rdf:resource=\"http://owl2.test/rules/Y2\"/>\n"
                + "    <j.0:ifp rdf:resource=\"http://owl2.test/rules/X1\"/></owl:NamedIndividual></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\">\n"
                + "  <owl:Ontology />\n"
                + "  <owl:NamedIndividual rdf:about=\"http://owl2.test/rules/X1\">\n"
                + "    <owl:differentFrom>\n"
                + "      <owl:NamedIndividual rdf:about=\"http://owl2.test/rules/X2\"/></owl:differentFrom></owl:NamedIndividual>\n"
                + "</rdf:RDF>";
        String id = "owl2_rl_rules_ifp_differentFrom";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "This test checks the interaction between inverse functional property and differentFrom assertions.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testowl2_rl_valid_mincard() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:j.0=\"http://owl2.test/rules#\">\n"
                + "  <owl:Ontology rdf:about=\"http://org.semanticweb.ontologies/Ontology1232054810511161000\"/>\n"
                + "  <owl:Class rdf:about=\"http://owl2.test/rules#C\">\n"
                + "    <rdfs:subClassOf>\n"
                + "      <owl:Restriction>\n"
                + "        <owl:minCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:minCardinality>\n"
                + "        <owl:onProperty>\n"
                + "          <owl:ObjectProperty rdf:about=\"http://owl2.test/rules#OP\"/></owl:onProperty></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + "  <owl:Class rdf:about=\"http://www.w3.org/2002/07/owl#Thing\"/>\n"
                + "  <j.0:C rdf:about=\"http://owl2.test/rules#c\">\n"
                + "    <rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#Thing\"/></j.0:C></rdf:RDF>";
        String conclusion = "";
        String id = "owl2_rl_valid_mincard";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "OWL 2 RL does not allow min cardinality";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testowl2_rl_valid_oneof() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\">\n"
                + "  <owl:Ontology />\n"
                + "  <owl:Class rdf:about=\"http://owl2.test/rules#Cb\"/>\n"
                + "  <owl:NamedIndividual rdf:about=\"http://owl2.test/rules#X\"/>\n"
                + "  <owl:NamedIndividual rdf:about=\"http://owl2.test/rules#Y\"/>\n"
                + "  <rdf:Description>\n"
                + "    <rdfs:subClassOf rdf:resource=\"http://owl2.test/rules#Cb\"/>\n"
                + "    <owl:oneOf rdf:parseType=\"Collection\">\n"
                + "      <owl:NamedIndividual rdf:about=\"http://owl2.test/rules#X\"/>\n"
                + "      <owl:NamedIndividual rdf:about=\"http://owl2.test/rules#Y\"/></owl:oneOf></rdf:Description>\n"
                + "</rdf:RDF>";
        String conclusion = "";
        String id = "owl2_rl_valid_oneof";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "A valid usage of oneOf in OWL 2 RL";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testowl2_rl_valid_rightside_allvaluesfrom() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\">\n"
                + "  <owl:Ontology />\n"
                + "  <owl:Class rdf:about=\"http://owl2.test/rules#C\">\n"
                + "    <rdfs:subClassOf>\n"
                + "      <owl:Restriction>\n"
                + "        <owl:onProperty>\n"
                + "          <owl:ObjectProperty rdf:about=\"http://owl2.test/rules#op\"/></owl:onProperty>\n"
                + "        <owl:allValuesFrom>\n"
                + "          <owl:Class rdf:about=\"http://owl2.test/rules#C1\"/></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf>\n"
                + "  </owl:Class></rdf:RDF>";
        String conclusion = "";
        String id = "owl2_rl_valid_rightside_allvaluesfrom";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "Valid RL usage of allValuesFrom.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testNew_Feature_AnnotationAnnotations_001() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs= \"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + '\n'
                + "<owl:Ontology rdf:about=\"http://example.org/\"/>\n"
                + '\n'
                + "<rdf:Description rdf:about=\"http://example.org/\">\n"
                + "  <rdfs:label>An example ontology</rdfs:label></rdf:Description>\n"
                + '\n'
                + "<owl:Annotation>\n"
                + "  <owl:annotatedSource rdf:resource=\"http://example.org/\" />\n"
                + "  <owl:annotatedProperty rdf:resource=\"http://www.w3.org/2000/01/rdf-schema#label\" />\n"
                + "  <owl:annotatedTarget>An example ontology</owl:annotatedTarget>\n"
                + "  <author>Mike Smith</author>\n" + "</owl:Annotation>\n"
                + '\n' + "<owl:AnnotationProperty rdf:about=\"author\" />\n"
                + "<owl:NamedIndividual rdf:about=\"i\" />\n" + '\n'
                + "</rdf:RDF>";
        String conclusion = "";
        String id = "New_Feature_AnnotationAnnotations_001";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "Demonstrates annotation of an annotation.  Adapted from an example in the Mapping to RDF Graphs document.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testNew_Feature_AsymmetricProperty_001() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n' + "<owl:Ontology/>\n" + '\n'
                + "<owl:ObjectProperty rdf:about=\"parentOf\" />\n"
                + "<owl:AsymmetricProperty rdf:about=\"parentOf\" />\n" + '\n'
                + "<rdf:Description rdf:about=\"Peter\">\n"
                + "  <parentOf rdf:resource=\"Stewie\" />\n"
                + "</rdf:Description>\n" + '\n'
                + "<rdf:Description rdf:about=\"Stewie\">\n"
                + "  <parentOf rdf:resource=\"Peter\" />\n"
                + "</rdf:Description>\n" + '\n' + "</rdf:RDF>";
        String conclusion = "";
        String id = "New_Feature_AsymmetricProperty_001";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "Demonstrates use of an asymmetric object property axiom to cause a trivial inconsistency based on the example in the Structural Specification and Functional-Style Syntax document.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testNew_Feature_AxiomAnnotations_001() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<owl:Class rdf:about=\"Child\" />\n"
                + "<owl:Class rdf:about=\"Person\" />\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Child\">\n"
                + "  <rdfs:subClassOf rdf:resource=\"Person\" /></rdf:Description>\n"
                + '\n'
                + "<owl:Axiom>\n"
                + "  <owl:annotatedSource rdf:resource=\"Child\" />\n"
                + "  <owl:annotatedProperty rdf:resource=\"http://www.w3.org/2000/01/rdf-schema#subClassOf\" />\n"
                + "  <owl:annotatedTarget rdf:resource=\"Person\" />\n"
                + "  <rdfs:comment>Children are people.</rdfs:comment></owl:Axiom>\n"
                + '\n' + "</rdf:RDF>";
        String conclusion = "";
        String id = "New_Feature_AxiomAnnotations_001";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "Demonstrates axiom annotation based on an example in the Mapping to RDF Graphs document.  The axiom being annotated here generates a main triple when mapped to RDF.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testNew_Feature_BottomDataProperty_001() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<rdf:Description rdf:about=\"i\">\n"
                + "  <rdf:type>\n"
                + "    <owl:Restriction>\n"
                + "      <owl:onProperty rdf:resource=\"http://www.w3.org/2002/07/owl#bottomDataProperty\" />\n"
                + "      <owl:someValuesFrom rdf:resource=\"http://www.w3.org/2000/01/rdf-schema#Literal\" /></owl:Restriction></rdf:type>\n"
                + "</rdf:Description>\n" + '\n' + "</rdf:RDF>";
        String conclusion = "";
        String id = "New_Feature_BottomDataProperty_001";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "Demonstrates use of the bottom data property to create an inconsistency.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testNew_Feature_BottomObjectProperty_001() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<rdf:Description rdf:about=\"i\">\n"
                + "  <rdf:type>\n"
                + "    <owl:Restriction>\n"
                + "      <owl:onProperty rdf:resource=\"http://www.w3.org/2002/07/owl#bottomObjectProperty\" />\n"
                + "      <owl:someValuesFrom rdf:resource=\"http://www.w3.org/2002/07/owl#Thing\" /></owl:Restriction></rdf:type>\n"
                + "</rdf:Description>\n" + '\n' + "</rdf:RDF>";
        String conclusion = "";
        String id = "New_Feature_BottomObjectProperty_001";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "Demonstrates use of the bottom object property to create an inconsistency.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testNew_Feature_DisjointObjectProperties_001() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<owl:ObjectProperty rdf:about=\"hasFather\" />\n"
                + "<owl:ObjectProperty rdf:about=\"hasMother\" />\n"
                + '\n'
                + "<rdf:Description rdf:about=\"hasFather\">\n"
                + "  <owl:propertyDisjointWith rdf:resource=\"hasMother\" /></rdf:Description>\n"
                + '\n' + "<rdf:Description rdf:about=\"Stewie\">\n"
                + "  <hasFather rdf:resource=\"Peter\" />\n"
                + "</rdf:Description>\n" + '\n'
                + "<rdf:Description rdf:about=\"Stewie\">\n"
                + "  <hasMother rdf:resource=\"Lois\" />\n"
                + "</rdf:Description>\n" + '\n' + "</rdf:RDF>";
        String conclusion = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Peter\">\n"
                + "  <owl:differentFrom rdf:resource=\"Lois\" /></rdf:Description>\n"
                + '\n' + "</rdf:RDF>";
        String id = "New_Feature_DisjointObjectProperties_001";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Demonstrates use of a disjoint object properties axiom to infer that two individuals are different based on the example in the Structural Specification and Functional-Style Syntax document.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testNew_Feature_DisjointObjectProperties_002() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<owl:ObjectProperty rdf:about=\"hasFather\" />\n"
                + "<owl:ObjectProperty rdf:about=\"hasMother\" />\n"
                + "<owl:ObjectProperty rdf:about=\"hasChild\" />\n"
                + '\n'
                + "<owl:AllDisjointProperties>\n"
                + "  <owl:members rdf:parseType=\"Collection\">\n"
                + "    <rdf:Description rdf:about=\"hasFather\" />\n"
                + "    <rdf:Description rdf:about=\"hasMother\" />\n"
                + "    <rdf:Description rdf:about=\"hasChild\" /></owl:members></owl:AllDisjointProperties>\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Stewie\">\n"
                + "  <hasFather rdf:resource=\"Peter\" />\n"
                + "</rdf:Description>\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Stewie\">\n"
                + "  <hasMother rdf:resource=\"Lois\" />\n"
                + "</rdf:Description>\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Stewie\">\n"
                + "  <hasChild rdf:resource=\"StewieJr\" />\n"
                + "</rdf:Description>\n" + '\n' + "</rdf:RDF>";
        String conclusion = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<owl:AllDifferent>\n"
                + "  <owl:distinctMembers rdf:parseType=\"Collection\">\n"
                + "    <rdf:Description rdf:about=\"Peter\" />\n"
                + "    <rdf:Description rdf:about=\"Lois\" />\n"
                + "    <rdf:Description rdf:about=\"StewieJr\" /></owl:distinctMembers></owl:AllDifferent>\n"
                + '\n' + "</rdf:RDF>";
        String id = "New_Feature_DisjointObjectProperties_002";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "A modification of test New-Feature-DisjointObjectProperties-001 to demonstrate a ternary disjoint object properties axiom.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testNew_Feature_DisjointUnion_001() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<owl:Class rdf:about=\"Child\" />\n"
                + "<owl:Class rdf:about=\"Boy\" />\n"
                + "<owl:Class rdf:about=\"Girl\" />\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Child\">\n"
                + "  <owl:disjointUnionOf rdf:parseType=\"Collection\">\n"
                + "    <rdf:Description rdf:about=\"Boy\" />\n"
                + "    <rdf:Description rdf:about=\"Girl\" /></owl:disjointUnionOf></rdf:Description>\n"
                + '\n'
                + "<Child rdf:about=\"Stewie\" />\n"
                + "<rdf:Description rdf:about=\"Stewie\">\n"
                + "  <rdf:type>\n"
                + "    <owl:Class>\n"
                + "      <owl:complementOf rdf:resource=\"Girl\" /></owl:Class></rdf:type>\n"
                + "</rdf:Description>\n" + '\n' + "</rdf:RDF>";
        String conclusion = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n' + "<owl:Ontology/>\n" + '\n'
                + "<owl:Class rdf:about=\"Boy\" />\n" + '\n'
                + "<Boy rdf:about=\"Stewie\" />\n" + '\n' + "</rdf:RDF>";
        String id = "New_Feature_DisjointUnion_001";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Demonstrates a disjoint union axiom based on the example in the Structural Specification and Functional-Style Syntax document.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testNew_Feature_IrreflexiveProperty_001() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n' + "<owl:Ontology/>\n" + '\n'
                + "<owl:ObjectProperty rdf:about=\"marriedTo\" />\n"
                + "<owl:IrreflexiveProperty rdf:about=\"marriedTo\" />\n"
                + '\n' + "<rdf:Description rdf:about=\"Peter\">\n"
                + "  <marriedTo rdf:resource=\"Peter\" />\n"
                + "</rdf:Description>\n" + '\n' + "</rdf:RDF>";
        String conclusion = "";
        String id = "New_Feature_IrreflexiveProperty_001";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "Demonstrates use of an irreflexive object property axiom to cause a trivial inconsistency based on the example in the Structural Specification and Functional-Style Syntax document.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testNew_Feature_NegativeDataPropertyAssertion_001() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<owl:DatatypeProperty rdf:about=\"hasAge\" />\n"
                + '\n'
                + "<owl:NegativePropertyAssertion>\n"
                + "  <owl:sourceIndividual rdf:resource=\"Meg\" />\n"
                + "  <owl:assertionProperty rdf:resource=\"hasAge\" />\n"
                + "  <owl:targetValue rdf:datatype=\"http://www.w3.org/2001/XMLSchema#integer\">5</owl:targetValue></owl:NegativePropertyAssertion>\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Meg\">\n"
                + "  <hasAge rdf:datatype=\"http://www.w3.org/2001/XMLSchema#integer\">5</hasAge></rdf:Description>\n"
                + '\n' + "</rdf:RDF>";
        String conclusion = "";
        String id = "New_Feature_NegativeDataPropertyAssertion_001";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "Demonstrates use of a negative data property assertion to create a trivial inconsistency based on an example in the Structural Specification and Functional-Style Syntax document.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testNew_Feature_NegativeObjectPropertyAssertion_001() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<owl:ObjectProperty rdf:about=\"hasSon\" />\n"
                + '\n'
                + "<owl:NegativePropertyAssertion>\n"
                + "  <owl:sourceIndividual rdf:resource=\"Peter\" />\n"
                + "  <owl:assertionProperty rdf:resource=\"hasSon\" />\n"
                + "  <owl:targetIndividual rdf:resource=\"Meg\" /></owl:NegativePropertyAssertion>\n"
                + '\n' + "<rdf:Description rdf:about=\"Peter\">\n"
                + "  <hasSon rdf:resource=\"Meg\" />\n"
                + "</rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "New_Feature_NegativeObjectPropertyAssertion_001";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "Demonstrates use of a negative object property assertion to create a trivial inconsistency based on an example in the Structural Specification and Functional-Style Syntax document.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testNew_Feature_ObjectPropertyChain_001() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + "<owl:Ontology/><owl:ObjectProperty rdf:about=\"hasMother\" />\n"
                + "<owl:ObjectProperty rdf:about=\"hasSister\" />\n"
                + "<owl:ObjectProperty rdf:about=\"hasAunt\" />\n"
                + "<rdf:Description rdf:about=\"hasAunt\">\n"
                + "  <owl:propertyChainAxiom rdf:parseType=\"Collection\">\n"
                + "    <rdf:Description rdf:about=\"hasMother\" />\n"
                + "    <rdf:Description rdf:about=\"hasSister\" /></owl:propertyChainAxiom></rdf:Description>\n"
                + "<rdf:Description rdf:about=\"Stewie\">\n"
                + "  <hasMother rdf:resource=\"Lois\" />\n"
                + "</rdf:Description>\n"
                + "<rdf:Description rdf:about=\"Lois\">\n"
                + "  <hasSister rdf:resource=\"Carol\" />\n"
                + "</rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + "<owl:Ontology/>\n"
                + "<owl:ObjectProperty rdf:about=\"hasAunt\" />\n"
                + "<rdf:Description rdf:about=\"Stewie\"><hasAunt rdf:resource=\"Carol\" /></rdf:Description></rdf:RDF>";
        String id = "New_Feature_ObjectPropertyChain_001";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Demonstrates an object property chain in a subproperty axiom based on the example in the Structural Specification and Functional-Style Syntax document.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testNew_Feature_ObjectPropertyChain_BJP_004() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "    xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdfs= \"http://www.w3.org/2000/01/rdf-schema#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "    <rdf:Description rdf:about=\"p\">\n"
                + "        <owl:propertyChainAxiom rdf:parseType=\"Collection\">\n"
                + "            <owl:ObjectProperty rdf:about=\"p\"/>\n"
                + "            <owl:ObjectProperty rdf:about=\"q\"/></owl:propertyChainAxiom></rdf:Description>\n"
                + "    \n" + "    <rdf:Description rdf:about=\"a\">\n"
                + "        <q rdf:resource=\"b\"/>\n"
                + "    </rdf:Description>\n" + "    \n"
                + "    <rdf:Description rdf:about=\"b\">\n"
                + "        <q rdf:resource=\"c\"/>\n"
                + "    </rdf:Description>\n" + "   \n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF\n"
                + "    xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n' + "   <owl:Ontology/>\n"
                + "   <owl:ObjectProperty rdf:about=\"p\"/>\n"
                + "   <owl:TransitiveProperty rdf:about=\"p\"/>\n"
                + "</rdf:RDF>";
        String id = "New_Feature_ObjectPropertyChain_BJP_004";
        TestClasses tc = TestClasses.valueOf("NEGATIVE_IMPL");
        String d = "A test of an interaction between a role chain +hierarchy and transitivity axioms.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testNew_Feature_ObjectQCR_001() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<owl:ObjectProperty rdf:about=\"fatherOf\" />\n"
                + "<owl:Class rdf:about=\"Man\" />\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Peter\">\n"
                + "  <fatherOf rdf:resource=\"Stewie\" />\n"
                + "  <fatherOf rdf:resource=\"Chris\" />\n"
                + "</rdf:Description>\n"
                + '\n'
                + "<Man rdf:about=\"Stewie\" />\n"
                + "<Man rdf:about=\"Chris\" />\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Stewie\">\n"
                + "  <owl:differentFrom rdf:resource=\"Chris\" /></rdf:Description>\n"
                + '\n' + "</rdf:RDF>";
        String conclusion = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<owl:ObjectProperty rdf:about=\"fatherOf\" />\n"
                + "<owl:Class rdf:about=\"Man\" />\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Peter\">\n"
                + "  <rdf:type>\n"
                + "    <owl:Restriction>\n"
                + "      <owl:onProperty rdf:resource=\"fatherOf\" />\n"
                + "      <owl:minQualifiedCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">2</owl:minQualifiedCardinality>\n"
                + "      <owl:onClass rdf:resource=\"Man\" /></owl:Restriction></rdf:type>\n"
                + "</rdf:Description>\n" + '\n' + "</rdf:RDF>";
        String id = "New_Feature_ObjectQCR_001";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Demonstrates a qualified minimum cardinality object property restriction based on example in the Structural Specification and Functional-Style Syntax document.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testNew_Feature_ObjectQCR_002() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<owl:ObjectProperty rdf:about=\"fatherOf\" />\n"
                + "<owl:Class rdf:about=\"Woman\" />\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Peter\">\n"
                + "  <fatherOf rdf:resource=\"Stewie\" />\n"
                + "  <fatherOf rdf:resource=\"Meg\" /></rdf:Description>\n"
                + '\n'
                + "<Woman rdf:about=\"Meg\" />\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Peter\">\n"
                + "  <rdf:type>\n"
                + "    <owl:Restriction>\n"
                + "      <owl:onProperty rdf:resource=\"fatherOf\" />\n"
                + "      <owl:maxQualifiedCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:maxQualifiedCardinality>\n"
                + "      <owl:onClass rdf:resource=\"Woman\" /></owl:Restriction></rdf:type>\n"
                + "</rdf:Description>\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Stewie\">\n"
                + "  <owl:differentFrom rdf:resource=\"Meg\" /></rdf:Description>\n"
                + '\n' + "</rdf:RDF>";
        String conclusion = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<owl:Class rdf:about=\"Woman\" />\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Stewie\">\n"
                + "  <rdf:type>\n"
                + "    <owl:Class>\n"
                + "      <owl:complementOf rdf:resource=\"Woman\" /></owl:Class></rdf:type>\n"
                + "</rdf:Description>\n" + '\n' + "</rdf:RDF>";
        String id = "New_Feature_ObjectQCR_002";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Demonstrates a qualified maximum cardinality object property restriction adapted from example in the Structural Specification and Functional-Style Syntax document.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testFS2RDF_different_individuals_2_ar() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns=\"http://example.org/\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\">\n"
                + "  <owl:Ontology/>\n"
                + "  <rdf:Description rdf:about=\"http://example.org/a\">\n"
                + "    <owl:differentFrom rdf:resource=\"http://example.org/b\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "FS2RDF_different_individuals_2_ar";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "Functional syntax to RDFXML translation of ontology consisting of a 2 argument DifferentIndividuals";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testFS2RDF_different_individuals_3_ar() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns=\"http://example.org/\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\">\n"
                + "  <owl:Ontology/>\n"
                + "  <owl:AllDifferent>\n"
                + "    <owl:distinctMembers rdf:parseType=\"Collection\">\n"
                + "      <rdf:Description rdf:about=\"http://example.org/a\"/>\n"
                + "      <rdf:Description rdf:about=\"http://example.org/b\"/>\n"
                + "      <rdf:Description rdf:about=\"http://example.org/c\"/></owl:distinctMembers></owl:AllDifferent>\n"
                + "</rdf:RDF>";
        String conclusion = "";
        String id = "FS2RDF_different_individuals_3_ar";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "Functional syntax to RDFXML translation of ontology consisting of a 3 argument DifferentIndividuals";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testFS2RDF_no_builtin_prefixes_ar() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns=\"http://example.org/\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\">\n"
                + "  <owl:Ontology/>\n"
                + "  <rdf:Description rdf:about=\"http://example.org/d\">\n"
                + "    <owl:sameAs rdf:resource=\"http://example.org/e\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://example.org/a\">\n"
                + "    <owl:sameAs>\n"
                + "      <rdf:Description rdf:about=\"http://example.org/b\">\n"
                + "        <owl:sameAs>\n"
                + "          <rdf:Description rdf:about=\"http://example.org/c\">\n"
                + "            <owl:sameAs rdf:resource=\"http://example.org/d\"/></rdf:Description></owl:sameAs></rdf:Description></owl:sameAs></rdf:Description>\n"
                + "</rdf:RDF>";
        String conclusion = "";
        String id = "FS2RDF_no_builtin_prefixes_ar";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "Functional syntax to RDFXML checking that there aren't builtin prefixes for xsd, rdf, rdfs, owl";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testFS2RDF_same_individual_2_ar() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns=\"http://example.org/\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\">\n"
                + "  <owl:Ontology/>\n"
                + "  <rdf:Description rdf:about=\"http://example.org/a\">\n"
                + "    <owl:sameAs rdf:resource=\"http://example.org/b\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "FS2RDF_same_individual_2_ar";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "Functional syntax to RDFXML translation of ontology consisting of a 2 argument SameIndividual";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testbnode2somevaluesfrom() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:ex=\"http://example.org/\"\n"
                + "  xml:base=\"http://example.org/\">\n"
                + "  <owl:Ontology/>\n"
                + "  <owl:ObjectProperty rdf:about=\"p\"/>\n"
                + "   \n"
                + "  <owl:Thing rdf:about=\"a\">\n"
                + "    <ex:p><rdf:Description rdf:nodeID=\"x\"/></ex:p> </owl:Thing></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"   xmlns:ex=\"http://example.org/\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "          xml:base=\"http://example.org/\">\n"
                + "  <owl:Ontology/>\n"
                + "  <owl:ObjectProperty rdf:about=\"p\"/>\n"
                + "  <owl:Thing rdf:about=\"a\">\n"
                + "        <rdf:type>\n"
                + "            <owl:Restriction>\n"
                + "                <owl:onProperty rdf:resource=\"p\"/>\n"
                + "                <owl:someValuesFrom rdf:resource=\"http://www.w3.org/2002/07/owl#Thing\"/></owl:Restriction></rdf:type></owl:Thing></rdf:RDF>";
        String id = "bnode2somevaluesfrom";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Shows that a BNode is an existential variable.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testchain2trans1() {
        String premise = "<rdf:RDF \n"
                + "     xml:base=\"http://example.org/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\">\n"
                + '\n'
                + "    <owl:Ontology rdf:about=\"http://owl.semanticweb.org/page/Special:GetOntology/Chain2trans?m=p\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"#p\"/>\n"
                + '\n'
                + "    <rdf:Description rdf:about=\"#p\">\n"
                + "        <owl:propertyChainAxiom rdf:parseType=\"Collection\">\n"
                + "            <rdf:Description rdf:about=\"#p\"/>\n"
                + "            <rdf:Description rdf:about=\"#p\"/></owl:propertyChainAxiom></rdf:Description>\n"
                + "</rdf:RDF>";
        String conclusion = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF \n"
                + "     xml:base=\"http://example.org/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\">\n"
                + "    <owl:Ontology rdf:about=\"http://owl.semanticweb.org/page/Special:GetOntology/Chain2trans?m=c\"/>\n"
                + "    <owl:TransitiveProperty rdf:about=\"#p\"/>\n"
                + "</rdf:RDF>";
        String id = "chain2trans1";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "A role chain can be a synonym for transitivity.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testConsistent_dateTime() {
        String premise = "Prefix(:=<http://example.org/>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Ontology(\n"
                + "  Declaration(NamedIndividual(:a))\n"
                + "  Declaration(DataProperty(:dp))\n"
                + "  Declaration(Class(:A))\n"
                + "  SubClassOf(:A \n"
                + "    DataSomeValuesFrom(:dp DatatypeRestriction(\n"
                + "      xsd:dateTime \n"
                + "      xsd:minInclusive \"2008-10-08T20:44:11.656+01:00\"^^xsd:dateTime)\n"
                + "    )\n"
                + "  ) \n"
                + "  SubClassOf(:A \n"
                + "    DataAllValuesFrom(:dp DatatypeRestriction(\n"
                + "      xsd:dateTime \n"
                + "      xsd:maxInclusive \"2008-10-08T20:44:11.656+01:00\"^^xsd:dateTime)\n"
                + "    )\n" + "  )\n" + "  ClassAssertion(:A :a)\n" + ')';
        String conclusion = "";
        String id = "Consistent_dateTime";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "The datatype restrictions leave exactly one dateTime value as dp filler for the individual a, so the ontology is consistent.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testconsistent_integer_filler() {
        String premise = "Prefix(:=<http://example.org/>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Ontology(\n" + "  Declaration(NamedIndividual(:a))\n"
                + "  Declaration(DataProperty(:dp))\n"
                + "  Declaration(Class(:A))\n"
                + "  SubClassOf(:A DataHasValue(:dp \"18\"^^xsd:integer)) \n"
                + "  ClassAssertion(:A :a) \n"
                + "  ClassAssertion(DataAllValuesFrom(:dp xsd:integer) :a)\n"
                + ')';
        String conclusion = "";
        String id = "consistent_integer_filler";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "The individual a is in the extension of the class A, which implies that it has a hasAge filler of 18 as integer, which is consistent with the all values from integer assertion for a.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Ignore("FaCT++ datatype problems")
    public void testDatatype_DataComplementOf_001() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs= \"http://www.w3.org/2000/01/rdf-schema#\" >\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<owl:DatatypeProperty rdf:about=\"p\" />\n"
                + '\n'
                + "<rdf:Description rdf:about=\"p\">\n"
                + "  <rdfs:range>\n"
                + "    <rdfs:Datatype>\n"
                + "      <owl:datatypeComplementOf rdf:resource=\"http://www.w3.org/2001/XMLSchema#positiveInteger\" /></rdfs:Datatype></rdfs:range></rdf:Description>\n"
                + '\n'
                + "<rdf:Description rdf:about=\"i\">\n"
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
    public void testDifferent_types_in_Datatype_Restrictions_and_Complement() {
        String premise = "Prefix(:=<http://example.org/>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Ontology(\n" + "  Declaration(NamedIndividual(:a))\n"
                + "  Declaration(DataProperty(:dp))\n"
                + "  Declaration(Class(:A))\n"
                + "  SubClassOf(:A DataAllValuesFrom(:dp \n"
                + "    DataOneOf(\"3\"^^xsd:integer \"4\"^^xsd:int))\n"
                + "  ) \n" + "  SubClassOf(:A DataAllValuesFrom(:dp \n"
                + "    DataOneOf(\"2\"^^xsd:short \"3\"^^xsd:integer))\n"
                + "  )\n" + "  ClassAssertion(:A :a)\n"
                + "  ClassAssertion(DataSomeValuesFrom(:dp\n"
                + "    DataComplementOf(DataOneOf(\"3\"^^xsd:integer))) :a\n"
                + "  )\n" + ')';
        String conclusion = "";
        String id = "Different_types_in_Datatype_Restrictions_and_Complement";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "The individual a must have dp fillers that are in the sets {3, 4} and {2, 3} (different types are used, but shorts and ints are integers), but at the same time 3 is not allowed as a dp filler for a, which causes the inconsistency.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testinconsistent_integer_filler() {
        String premise = "Prefix(:=<http://example.org/>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Ontology(\n"
                + "  Declaration(NamedIndividual(:a))\n"
                + "  Declaration(DataProperty(:hasAge))\n"
                + "  Declaration(Class(:Eighteen))\n"
                + "  SubClassOf(DataHasValue(:hasAge \"18\"^^xsd:integer) :Eighteen) \n"
                + "  ClassAssertion(DataHasValue(:hasAge \"18\"^^xsd:integer) :a) \n"
                + "  ClassAssertion(ObjectComplementOf(:Eighteen) :a)\n" + ')';
        String conclusion = "";
        String id = "inconsistent_integer_filler";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "The individual has an asserted hasAge filler of 18 as integer. At the same time it is an instance of the negation of the class Eighteen, which implies that all hasAge fillers for a are not 18.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testDisjointClasses_001() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<owl:Class rdf:about=\"Boy\" />\n"
                + "<owl:Class rdf:about=\"Girl\" />\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Boy\">\n"
                + "  <owl:disjointWith rdf:resource=\"Girl\" /></rdf:Description>\n"
                + '\n' + "<Boy rdf:about=\"Stewie\" />\n" + "</rdf:RDF>";
        String conclusion = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<owl:Class rdf:about=\"Girl\" />\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Stewie\">\n"
                + "  <rdf:type>\n"
                + "    <owl:Class>\n"
                + "      <owl:complementOf rdf:resource=\"Girl\" /></owl:Class></rdf:type>\n"
                + "</rdf:Description>\n" + '\n' + "</rdf:RDF>";
        String id = "DisjointClasses_001";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Demonstrates a binary disjoint classes axiom based on example in the Structural Specification and Functional-Style Syntax document.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testDisjointClasses_002() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<owl:Class rdf:about=\"Boy\" />\n"
                + "<owl:Class rdf:about=\"Girl\" />\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Boy\">\n"
                + "  <owl:disjointWith rdf:resource=\"Girl\" /></rdf:Description>\n"
                + '\n' + "<Boy rdf:about=\"Stewie\" />\n"
                + "<Girl rdf:about=\"Stewie\" />\n" + '\n' + "</rdf:RDF>";
        String conclusion = "";
        String id = "DisjointClasses_002";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "Demonstrates a binary disjoint classes axiom and class assertions causing an inconsistency based on example in the Structural Specification and Functional-Style Syntax document.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testDisjointClasses_003() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n' + "<owl:Ontology/>\n" + '\n'
                + "<owl:Class rdf:about=\"Boy\" />\n"
                + "<owl:Class rdf:about=\"Girl\" />\n"
                + "<owl:Class rdf:about=\"Dog\" />\n" + '\n'
                + "<owl:AllDisjointClasses>\n"
                + "  <owl:members rdf:parseType=\"Collection\">\n"
                + "    <rdf:Description rdf:about=\"Boy\" />\n"
                + "    <rdf:Description rdf:about=\"Girl\" />\n"
                + "    <rdf:Description rdf:about=\"Dog\" />\n"
                + "  </owl:members></owl:AllDisjointClasses>\n" + '\n'
                + "<Boy rdf:about=\"Stewie\" />\n" + '\n' + "</rdf:RDF>";
        String conclusion = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<owl:Class rdf:about=\"Girl\" />\n"
                + "<owl:Class rdf:about=\"Dog\" />\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Stewie\">\n"
                + "  <rdf:type>\n"
                + "    <owl:Class>\n"
                + "      <owl:complementOf rdf:resource=\"Girl\" /></owl:Class></rdf:type>\n"
                + "</rdf:Description>\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Stewie\">\n"
                + "  <rdf:type>\n"
                + "    <owl:Class>\n"
                + "      <owl:complementOf rdf:resource=\"Dog\" /></owl:Class></rdf:type>\n"
                + "</rdf:Description>\n" + '\n' + "</rdf:RDF>";
        String id = "DisjointClasses_003";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "A modification of test DisjointClasses-001 to demonstrate a ternary disjoint classes axiom.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }
}
