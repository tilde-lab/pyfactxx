package conformancetests;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import org.junit.Ignore;
import org.junit.Test;

import testbase.TestBase;

@SuppressWarnings("javadoc")
@Ignore
public class NewFeaturesTestCase extends TestBase {

    @Test
    public void testInconsistent_Disjoint_Dataproperties() {
        String premise = "Prefix(:=<http://example.org/>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Ontology(\n"
                + "  Declaration(NamedIndividual(:a))\nDeclaration(DataProperty(:dp1))\nDeclaration(DataProperty(:dp2))\nDeclaration(Class(:A))\n"
                + "  DisjointDataProperties(:dp1 :dp2) \n"
                + "  DataPropertyAssertion(:dp1 :a \"10\"^^xsd:integer)\n"
                + "  SubClassOf(:A DataSomeValuesFrom(:dp2 DatatypeRestriction(xsd:integer xsd:minInclusive \"10\"^^xsd:integer xsd:maxInclusive \"10\"^^xsd:integer)\n  )\n  )\n"
                + "  ClassAssertion(:A :a)\n" + ')';
        String conclusion = "";
        String id = "Inconsistent_Disjoint_Dataproperties";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "The data properties dp1 and dp2 are disjoint, but the individual a must have 10 as dp1 filler and 10 as dp2 filler (since 10 is the only integer satisfying >= 10 and <= 10), which causes the inconsistency.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testMinus_Infinity_is_not_in_owl_real() {
        String premise = "Prefix(:=<http://example.org/>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
                + "Ontology(\n"
                + "  Declaration(NamedIndividual(:a))\n"
                + "  Declaration(DataProperty(:dp))\n"
                + "  Declaration(Class(:A))\n"
                + "  SubClassOf(:A DataAllValuesFrom(:dp owl:real)) \n"
                + "  SubClassOf(:A \n"
                + "    DataSomeValuesFrom(:dp DataOneOf(\"-INF\"^^xsd:float \"-0\"^^xsd:integer))\n"
                + "  )\n"
                + "  ClassAssertion(:A :a) \n"
                + "  NegativeDataPropertyAssertion(:dp :a \"0\"^^xsd:unsignedInt)\n"
                + ')';
        String conclusion = "";
        String id = "Minus_Infinity_is_not_in_owl_real";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "The individual a must have either negative Infinity or 0 (-0 as integer is 0) as dp fillers and all dp successors must be from owl:real, which excludes negative infinity. Since 0 is excluded by the negative property assertion, the ontology is inconsistent.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testNew_Feature_DataQCR_001() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n' + "<owl:Ontology/>\n" + '\n'
                + "<owl:DatatypeProperty rdf:about=\"hasName\" />\n" + '\n'
                + "<rdf:Description rdf:about=\"Meg\">\n"
                + "  <hasName>Meg Griffin</hasName>\n"
                + "  <hasName>Megan Griffin</hasName>\n"
                + "</rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<owl:DatatypeProperty rdf:about=\"hasName\" />\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Meg\">\n"
                + "  <rdf:type>\n"
                + "    <owl:Restriction>\n"
                + "      <owl:onProperty rdf:resource=\"hasName\" />\n"
                + "      <owl:minQualifiedCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">2</owl:minQualifiedCardinality>\n"
                + "      <owl:onDataRange rdf:resource=\"http://www.w3.org/2001/XMLSchema#string\" /></owl:Restriction></rdf:type>\n"
                + "</rdf:Description>\n" + '\n' + "</rdf:RDF>";
        String id = "New_Feature_DataQCR_001";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Demonstrates a qualified minimum cardinality data property restriction based on example in the Structural Specification and Functional-Style Syntax document.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testNew_Feature_DisjointDataProperties_001() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<owl:DatatypeProperty rdf:about=\"hasName\" />\n"
                + "<owl:DatatypeProperty rdf:about=\"hasAddress\" />\n"
                + '\n'
                + "<rdf:Description rdf:about=\"hasName\">\n"
                + "  <owl:propertyDisjointWith rdf:resource=\"hasAddress\" /></rdf:Description>\n"
                + '\n' + "<rdf:Description rdf:about=\"Peter\">\n"
                + "  <hasName>Peter Griffin</hasName>\n"
                + "  <hasAddress>Peter Griffin</hasAddress>\n"
                + "</rdf:Description>\n" + '\n' + "</rdf:RDF>";
        String conclusion = "";
        String id = "New_Feature_DisjointDataProperties_001";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "Demonstrates use of a disjoint data properties axiom to create a trivial inconsistency based on the example in the Structural Specification and Functional-Style Syntax document.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testNew_Feature_DisjointDataProperties_002() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<owl:DatatypeProperty rdf:about=\"hasName\" />\n"
                + "<owl:DatatypeProperty rdf:about=\"hasAddress\" />\n"
                + "<owl:DatatypeProperty rdf:about=\"hasZip\" />\n"
                + '\n'
                + "<owl:AllDisjointProperties>\n"
                + "  <owl:members rdf:parseType=\"Collection\">\n"
                + "    <rdf:Description rdf:about=\"hasName\" />\n"
                + "    <rdf:Description rdf:about=\"hasAddress\" />\n"
                + "    <rdf:Description rdf:about=\"hasZip\" />\n"
                + "  </owl:members></owl:AllDisjointProperties>\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Peter\">\n"
                + "  <hasName>Peter Griffin</hasName>\n"
                + "</rdf:Description>\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Peter_Griffin\">\n"
                + "  <hasAddress>Peter Griffin</hasAddress>\n"
                + "</rdf:Description>\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Petre\">\n"
                + "  <hasZip>Peter Griffin</hasZip>\n"
                + "</rdf:Description>\n"
                + "</rdf:RDF>";
        String conclusion = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<owl:AllDifferent>\n"
                + "  <owl:distinctMembers rdf:parseType=\"Collection\">\n"
                + "    <rdf:Description rdf:about=\"Peter\" />\n"
                + "    <rdf:Description rdf:about=\"Peter_Griffin\" />\n"
                + "    <rdf:Description rdf:about=\"Petre\" /></owl:distinctMembers></owl:AllDifferent>\n"
                + '\n' + "</rdf:RDF>";
        String id = "New_Feature_DisjointDataProperties_002";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Demonstrates use of a ternary disjoint data properties axiom to infer different individuals.  Adapted from test New-Feature-DisjointDataProperties-001.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testNew_Feature_Keys_001() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<owl:DatatypeProperty rdf:about=\"hasSSN\" />\n"
                + '\n'
                + "<rdf:Description rdf:about=\"http://www.w3.org/2002/07/owl#Thing\">\n"
                + "  <owl:hasKey rdf:parseType=\"Collection\">\n"
                + "    <rdf:Description rdf:about=\"hasSSN\" />\n"
                + "  </owl:hasKey></rdf:Description>\n" + '\n'
                + "<rdf:Description rdf:about=\"Peter\">\n"
                + "  <hasSSN>123-45-6789</hasSSN>\n" + "</rdf:Description>\n"
                + '\n' + "<rdf:Description rdf:about=\"Peter_Griffin\">\n"
                + "  <hasSSN>123-45-6789</hasSSN>\n" + "</rdf:Description>\n"
                + "</rdf:RDF>";
        String conclusion = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Peter\">\n"
                + "  <owl:sameAs rdf:resource=\"Peter_Griffin\" /></rdf:Description>\n"
                + '\n' + "</rdf:RDF>";
        String id = "New_Feature_Keys_001";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Demonstrates use of a key axiom to merge individuals based on an example in the Structural Specification and Functional-Style Syntax document.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testNew_Feature_Keys_002() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<owl:DatatypeProperty rdf:about=\"hasSSN\" />\n"
                + '\n'
                + "<rdf:Description rdf:about=\"http://www.w3.org/2002/07/owl#Thing\">\n"
                + "  <owl:hasKey rdf:parseType=\"Collection\">\n"
                + "    <rdf:Description rdf:about=\"hasSSN\" />\n"
                + "  </owl:hasKey></rdf:Description>\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Peter\">\n"
                + "  <hasSSN>123-45-6789</hasSSN>\n"
                + "</rdf:Description>\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Peter_Griffin\">\n"
                + "  <hasSSN>123-45-6789</hasSSN>\n"
                + "</rdf:Description>\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Peter\">\n"
                + "  <owl:differentFrom rdf:resource=\"Peter_Griffin\" /></rdf:Description>\n"
                + '\n' + "</rdf:RDF>";
        String conclusion = "";
        String id = "New_Feature_Keys_002";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "Demonstrates use of a key axiom to cause a trivial inconsistency based on an example in the Structural Specification and Functional-Style Syntax document.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testNew_Feature_Keys_003() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<owl:Class rdf:about=\"GriffinFamilyMember\" />\n"
                + '\n'
                + "<owl:DatatypeProperty rdf:about=\"hasName\" />\n"
                + '\n'
                + "<rdf:Description rdf:about=\"GriffinFamilyMember\">\n"
                + "  <owl:hasKey rdf:parseType=\"Collection\">\n"
                + "    <rdf:Description rdf:about=\"hasName\" />\n"
                + "  </owl:hasKey></rdf:Description>\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Peter\">\n"
                + "  <hasName>Peter</hasName>\n"
                + "  <rdf:type rdf:resource=\"GriffinFamilyMember\" /></rdf:Description>\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Peter_Griffin\">\n"
                + "  <hasName>Peter</hasName>\n"
                + "  <rdf:type rdf:resource=\"GriffinFamilyMember\" /></rdf:Description>\n"
                + '\n'
                + "<rdf:Description rdf:about=\"StPeter\">\n"
                + "  <hasName>Peter</hasName>\n"
                + "</rdf:Description>\n"
                + "</rdf:RDF>";
        String conclusion = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Peter\">\n"
                + "  <owl:sameAs rdf:resource=\"Peter_Griffin\" /></rdf:Description>\n"
                + '\n' + "</rdf:RDF>";
        String id = "New_Feature_Keys_003";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Demonstrates use of a \"localized\" key axiom to merge individuals based on an example in the Structural Specification and Functional-Style Syntax document.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testNew_Feature_Keys_004() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<owl:Class rdf:about=\"GriffinFamilyMember\" />\n"
                + '\n'
                + "<owl:DatatypeProperty rdf:about=\"hasName\" />\n"
                + '\n'
                + "<rdf:Description rdf:about=\"GriffinFamilyMember\">\n"
                + "  <owl:hasKey rdf:parseType=\"Collection\">\n"
                + "    <rdf:Description rdf:about=\"hasName\" />\n"
                + "  </owl:hasKey></rdf:Description>\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Peter\">\n"
                + "  <hasName>Peter</hasName>\n"
                + "  <rdf:type rdf:resource=\"GriffinFamilyMember\" /></rdf:Description>\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Peter_Griffin\">\n"
                + "  <hasName>Peter</hasName>\n"
                + "  <rdf:type rdf:resource=\"GriffinFamilyMember\" /></rdf:Description>\n"
                + '\n'
                + "<rdf:Description rdf:about=\"StPeter\">\n"
                + "  <hasName>Peter</hasName>\n"
                + "</rdf:Description>\n"
                + "</rdf:RDF>";
        String conclusion = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n' + "<owl:Ontology/>\n" + '\n'
                + "<rdf:Description rdf:about=\"Peter\">\n"
                + "  <owl:sameAs rdf:resource=\"StPeter\" />\n"
                + "</rdf:Description>\n" + '\n' + "</rdf:RDF>";
        String id = "New_Feature_Keys_004";
        TestClasses tc = TestClasses.valueOf("NEGATIVE_IMPL");
        String d = "Demonstrates that use of a \"localized\" key axiom only merges individuals that are instances of the given class expression.  Based on an example in the Structural Specification and Functional-Style Syntax document.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testNew_Feature_Keys_005() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<owl:Class rdf:about=\"GriffinFamilyMember\" />\n"
                + '\n'
                + "<owl:DatatypeProperty rdf:about=\"hasName\" />\n"
                + '\n'
                + "<rdf:Description rdf:about=\"GriffinFamilyMember\">\n"
                + "  <owl:hasKey rdf:parseType=\"Collection\">\n"
                + "    <rdf:Description rdf:about=\"hasName\" />\n"
                + "  </owl:hasKey></rdf:Description>\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Peter\">\n"
                + "  <hasName>Peter</hasName>\n"
                + "  <hasName>Kichwa-Tembo</hasName>\n"
                + "  <rdf:type rdf:resource=\"GriffinFamilyMember\" /></rdf:Description>\n"
                + '\n' + "</rdf:RDF>";
        String conclusion = "";
        String id = "New_Feature_Keys_005";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "Demonstrates that a key axiom does not make all properties used in it functional. Based on an example in the Structural Specification and Functional-Style Syntax document.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testNew_Feature_Keys_006() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<owl:Class rdf:about=\"GriffinFamilyMember\" />\n"
                + '\n'
                + "<owl:DatatypeProperty rdf:about=\"hasName\" />\n"
                + '\n'
                + "<rdf:Description rdf:about=\"GriffinFamilyMember\">\n"
                + "  <owl:hasKey rdf:parseType=\"Collection\">\n"
                + "    <rdf:Description rdf:about=\"hasName\" /></owl:hasKey></rdf:Description>\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Peter\">\n"
                + "  <hasName>Peter</hasName>\n"
                + "  <hasName>Kichwa-Tembo</hasName>\n"
                + "  <rdf:type rdf:resource=\"GriffinFamilyMember\" /></rdf:Description>\n"
                + '\n'
                + "<rdf:Description rdf:about=\"hasName\">\n"
                + "  <rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#FunctionalProperty\" /></rdf:Description>\n"
                + '\n' + "</rdf:RDF>";
        String conclusion = "";
        String id = "New_Feature_Keys_006";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "Demonstrates that a key axiom does not make all properties used in it functional, but these properties may be made functional with other axioms. Based on an example in the Structural Specification and Functional-Style Syntax document.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testNew_Feature_Keys_007() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<owl:Class rdf:about=\"Person\" />\n"
                + '\n'
                + "<owl:Class rdf:about=\"Man\" />\n"
                + '\n'
                + "<owl:DatatypeProperty rdf:about=\"hasSSN\" />\n"
                + '\n'
                + "<owl:ObjectProperty rdf:about=\"marriedTo\" />\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Person\">\n"
                + "  <owl:hasKey rdf:parseType=\"Collection\">\n"
                + "    <rdf:Description rdf:about=\"hasSSN\" /></owl:hasKey></rdf:Description>\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Peter\">\n"
                + "  <hasSSN>123-45-6789</hasSSN>\n"
                + "  <rdf:type rdf:resource=\"Person\" /></rdf:Description>\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Lois\">\n"
                + "  <rdf:type>\n"
                + "    <owl:Restriction>\n"
                + "      <owl:onProperty rdf:resource=\"marriedTo\" />\n"
                + "      <owl:someValuesFrom>\n"
                + "        <owl:Class>\n"
                + "          <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "            <rdf:Description rdf:about=\"Man\" />\n"
                + "            <owl:Restriction>\n"
                + "              <owl:onProperty rdf:resource=\"hasSSN\" />\n"
                + "              <owl:hasValue>123-45-6789</owl:hasValue></owl:Restriction></owl:intersectionOf></owl:Class></owl:someValuesFrom></owl:Restriction></rdf:type>\n"
                + "</rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n' + "<owl:Ontology/>\n" + '\n'
                + "<owl:Class rdf:about=\"Man\" />\n" + '\n'
                + "<Man rdf:about=\"Peter\" />\n" + '\n' + "</rdf:RDF>";
        String id = "New_Feature_Keys_007";
        TestClasses tc = TestClasses.valueOf("NEGATIVE_IMPL");
        String d = "Demonstrates that a key axiom only applies to named individuals.  Based on an example in the Structural Specification and Functional-Style Syntax document.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testNew_Feature_ObjectPropertyChain_BJP_003() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "    xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdfs= \"http://www.w3.org/2000/01/rdf-schema#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org/p\"/>\n"
                // XXX this is a bug, needs to be fixed
                + "<owl:Thing rdf:about=\"http://www.example.org/a\"/>\n"
                + "<owl:Thing rdf:about=\"http://www.example.org/c\"/>\n"
                + "    <rdf:Description rdf:about=\"p\">\n"
                + "        <owl:propertyChainAxiom rdf:parseType=\"Collection\">\n"
                + "            <owl:ObjectProperty rdf:about=\"p\"/>\n"
                + "            <owl:ObjectProperty rdf:about=\"q\"/></owl:propertyChainAxiom></rdf:Description>\n"
                + "    \n" + "    <rdf:Description rdf:about=\"a\">\n"
                + "        <p rdf:resource=\"b\"/>\n"
                + "    </rdf:Description>\n" + "    \n"
                + "    <rdf:Description rdf:about=\"b\">\n"
                + "        <q rdf:resource=\"c\"/>\n"
                + "    </rdf:Description>\n" + "   \n" + "</rdf:RDF>";
        // XXX this should parse equal to the second example but does not
        String conclusion = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "    xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + "    <owl:Ontology/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org/p\"/>\n"
                + "    <owl:NamedIndividual rdf:about=\"a\">\n"
                + "        <p rdf:resource=\"c\"/>\n"
                + "    </owl:NamedIndividual>\n" + "</rdf:RDF>";
        conclusion = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF xmlns=\"http://www.w3.org/2002/07/owl#\"\n"
                + "xml:base=\"http://www.w3.org/2002/07/owl\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:test=\"http://www.example.org/\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + "<ObjectProperty rdf:about=\"http://www.example.org/p\"/>\n"
                + "<NamedIndividual rdf:about=\"http://www.example.org/a\">\n"
                + "<test:p rdf:resource=\"http://www.example.org/c\"/></NamedIndividual>\n"
                + "</rdf:RDF>";
        String id = "New_Feature_ObjectPropertyChain_BJP_003";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "A simple test of role chains and role hierarchy.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testNew_Feature_Rational_001() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<owl:DatatypeProperty rdf:about=\"dp\" />\n"
                + '\n'
                + "<rdf:Description rdf:about=\"a\">\n"
                + "  <rdf:type>\n"
                + "    <owl:Restriction>\n"
                + "      <owl:onProperty rdf:resource=\"dp\" />\n"
                + "      <owl:allValuesFrom rdf:resource=\"http://www.w3.org/2002/07/owl#rational\" /></owl:Restriction></rdf:type></rdf:Description>\n"
                + '\n'
                + "<rdf:Description rdf:about=\"a\">\n"
                + "  <rdf:type>\n"
                + "    <owl:Restriction>\n"
                + "      <owl:onProperty rdf:resource=\"dp\" />\n"
                + "      <owl:minCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">2</owl:minCardinality></owl:Restriction></rdf:type>\n"
                + "</rdf:Description>\n" + '\n' + "</rdf:RDF>";
        String conclusion = "";
        String id = "New_Feature_Rational_001";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "A consistent ontology using owl:rational";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testNew_Feature_Rational_002() {
        String premise = "Prefix( : = <http://example.org/> )\n"
                + "Prefix( owl: = <http://www.w3.org/2002/07/owl#> )\n"
                + "Prefix( xsd: = <http://www.w3.org/2001/XMLSchema#> )\n"
                + '\n'
                + "Ontology(\n"
                + " Declaration( DataProperty( :dp ) )\n"
                + " ClassAssertion( DataAllValuesFrom( :dp DataOneOf( \"0.5\"^^xsd:decimal \"1/2\"^^owl:rational ) ) :a )\n"
                + " ClassAssertion( DataMinCardinality( 2 :dp ) :a )\n" + ')';
        String conclusion = "";
        String id = "New_Feature_Rational_002";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "An inconsistent ontology using owl:rational";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testNew_Feature_Rational_003() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs= \"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<owl:DatatypeProperty rdf:about=\"dp\" />\n"
                + '\n'
                + "<rdf:Description rdf:about=\"a\">\n"
                + "  <rdf:type>\n"
                + "    <owl:Restriction>\n"
                + "      <owl:onProperty rdf:resource=\"dp\" />\n"
                + "      <owl:allValuesFrom>\n"
                + "        <rdfs:Datatype>\n"
                + "          <owl:oneOf>\n"
                + "            <rdf:Description>\n"
                + "              <rdf:first rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">0.3333333333333333</rdf:first>\n"
                + "              <rdf:rest>\n"
                + "                <rdf:Description>\n"
                + "                  <rdf:first rdf:datatype=\"http://www.w3.org/2002/07/owl#rational\">1/3</rdf:first>\n"
                + "                  <rdf:rest rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"/></rdf:Description></rdf:rest></rdf:Description></owl:oneOf></rdfs:Datatype></owl:allValuesFrom></owl:Restriction></rdf:type></rdf:Description>\n"
                + '\n'
                + "<rdf:Description rdf:about=\"a\">\n"
                + "  <rdf:type>\n"
                + "    <owl:Restriction>\n"
                + "      <owl:onProperty rdf:resource=\"dp\" />\n"
                + "      <owl:minCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">2</owl:minCardinality></owl:Restriction></rdf:type>\n"
                + "</rdf:Description>\n" + '\n' + "</rdf:RDF>";
        String conclusion = "";
        String id = "New_Feature_Rational_003";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "A consistent ontology demonstrating owl:rational is different from xsd:decimal.  The decimal literal requires 16 digits, the minimum required for conformance.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testNew_Feature_ReflexiveProperty_001() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n' + "<owl:Ontology/>\n" + '\n'
                + "<owl:ObjectProperty rdf:about=\"knows\" />\n"
                + "<owl:NamedIndividual rdf:about=\"Peter\" />\n" + '\n'
                + "<owl:ReflexiveProperty rdf:about=\"knows\" />\n" + '\n'
                + "</rdf:RDF>";
        String conclusion = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n' + "<owl:Ontology/>\n" + '\n'
                + "<owl:ObjectProperty rdf:about=\"knows\" />\n" + '\n'
                + "<rdf:Description rdf:about=\"Peter\">\n"
                + "  <knows rdf:resource=\"Peter\" />\n"
                + "</rdf:Description>\n" + "</rdf:RDF>";
        String id = "New_Feature_ReflexiveProperty_001";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Uses a reflexive object property axiom to infer a property value based on the example in the Structural Specification and Functional-Style Syntax document.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testNew_Feature_SelfRestriction_001() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<owl:ObjectProperty rdf:about=\"likes\" />\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Peter\">\n"
                + "  <rdf:type>\n"
                + "    <owl:Restriction>\n"
                + "      <owl:onProperty rdf:resource=\"likes\" />\n"
                + "      <owl:hasSelf rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">true</owl:hasSelf></owl:Restriction></rdf:type>\n"
                + "</rdf:Description>\n" + '\n' + "</rdf:RDF>";
        String conclusion = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n' + "<owl:Ontology/>\n" + '\n'
                + "<owl:ObjectProperty rdf:about=\"likes\" />\n" + '\n'
                + "<rdf:Description rdf:about=\"Peter\">\n"
                + "  <likes rdf:resource=\"Peter\" />\n"
                + "</rdf:Description>\n" + "</rdf:RDF>";
        String id = "New_Feature_SelfRestriction_001";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Demonstrates use of a self-restriction to infer a property value based on example in the Structural Specification and Functional-Style Syntax document.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testNew_Feature_SelfRestriction_002() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n' + "<owl:Ontology/>\n" + '\n'
                + "<owl:ObjectProperty rdf:about=\"likes\" />\n" + '\n'
                + "<rdf:Description rdf:about=\"Peter\">\n"
                + "  <likes rdf:resource=\"Peter\" />\n"
                + "</rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<owl:ObjectProperty rdf:about=\"likes\" />\n"
                + '\n'
                + "<rdf:Description rdf:about=\"Peter\">\n"
                + "  <rdf:type>\n"
                + "    <owl:Restriction>\n"
                + "      <owl:onProperty rdf:resource=\"likes\" />\n"
                + "      <owl:hasSelf rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">true</owl:hasSelf></owl:Restriction></rdf:type>\n"
                + "</rdf:Description>\n" + '\n' + "</rdf:RDF>";
        String id = "New_Feature_SelfRestriction_002";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Demonstrates use of an object property assertion to infer membership in a self restriction based on example in the Structural Specification and Functional-Style Syntax document.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testNew_Feature_TopObjectProperty_001() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\" xmlns     = \"http://example.org/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + '\n'
                + "<owl:Ontology/>\n"
                + '\n'
                + "<rdf:Description rdf:about=\"i\">\n"
                + "  <rdf:type>\n"
                + "    <owl:Class>\n"
                + "      <owl:complementOf>\n"
                + "        <owl:Restriction>\n"
                + "          <owl:onProperty rdf:resource=\"http://www.w3.org/2002/07/owl#topObjectProperty\" />\n"
                + "          <owl:someValuesFrom rdf:resource=\"http://www.w3.org/2002/07/owl#Thing\" /></owl:Restriction></owl:complementOf></owl:Class>\n"
                + "  </rdf:type></rdf:Description>\n" + '\n' + "</rdf:RDF>";
        String conclusion = "";
        String id = "New_Feature_TopObjectProperty_001";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "Demonstrates use of the top object property to create an inconsistency.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testconsistent_dataproperty_disjointness() {
        String premise = "Prefix(:=<http://example.org/>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Ontology(\n" + "  Declaration(NamedIndividual(:a))\n"
                + "  Declaration(DataProperty(:dp1))\n"
                + "  Declaration(DataProperty(:dp2))\n"
                + "  Declaration(Class(:A))\n"
                + "  DisjointDataProperties(:dp1 :dp2) \n"
                + "  DataPropertyAssertion(:dp1 :a \"10\"^^xsd:integer)\n"
                + "  SubClassOf(:A DataSomeValuesFrom(:dp2 \n"
                + "    DatatypeRestriction(xsd:integer \n"
                + "      xsd:minInclusive \"18\"^^xsd:integer \n"
                + "      xsd:maxInclusive \"18\"^^xsd:integer)\n" + "    )\n"
                + "  )\n" + "  ClassAssertion(:A :a)\n" + ')';
        String conclusion = "";
        String id = "consistent_dataproperty_disjointness";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "The datatype properties dp1 and dp2 are disjoint, but the individual a can have 10 as a filler for dp1 and 18 as filler for dp2, which satisfies the disjointness.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Ignore()
    @Changed(reason = "not OWL 2 DL - data properties cannt be inverse functional properties")
    public
            void testrdfbased_sem_char_inversefunc_data() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:InverseFunctionalProperty rdf:about=\"http://www.example.org#p\">\n"
                + "    <rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#DatatypeProperty\"/></owl:InverseFunctionalProperty>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x1\">\n"
                + "    <ex:p>data</ex:p>\n"
                + "  </rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x2\">\n"
                + "    <ex:p>data</ex:p>\n" + "  </rdf:Description></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x1\">\n"
                + "    <owl:sameAs rdf:resource=\"http://www.example.org#x2\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_char_inversefunc_data";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "For two triples with the same inverse functional data property as their predicates and with the same data object, the subjects are the same. This test shows that the OWL 2 RDF-Based Semantics allows for IFDPs.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_key_def() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "  <owl:DatatypeProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                + "  <ex:c rdf:about=\"http://www.example.org#x\">\n"
                + "    <ex:p1 rdf:resource=\"http://www.example.org#z\"/>\n"
                + "    <ex:p2>data</ex:p2></ex:c>\n"
                + "  <ex:c rdf:about=\"http://www.example.org#y\">\n"
                + "    <ex:p1 rdf:resource=\"http://www.example.org#z\"/>\n"
                + "    <ex:p2>data</ex:p2></ex:c>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#c\">\n"
                + "    <owl:hasKey rdf:parseType=\"Collection\">\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#p1\"/>\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#p2\"/></owl:hasKey></rdf:Description>\n"
                + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x\">\n"
                + "    <owl:sameAs rdf:resource=\"http://www.example.org#y\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_key_def";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "For two triples matching the conditions of a key axiom the subjects are identified.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_npa_ind_fw() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#z\">\n"
                + "    <owl:sourceIndividual>\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#s\">\n"
                + "        <ex:p rdf:resource=\"http://www.example.org#o\"/></rdf:Description></owl:sourceIndividual>\n"
                + "    <owl:assertionProperty rdf:resource=\"http://www.example.org#p\"/>\n"
                + "    <owl:targetIndividual rdf:resource=\"http://www.example.org#o\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "rdfbased_sem_npa_ind_fw";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "A negative property assertion NPA(s p o) must not occur together with the corresponding positive property assertion s p o.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }
}
