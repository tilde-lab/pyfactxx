package conformancetests;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import org.junit.Test;

import testbase.TestBase;

@SuppressWarnings("javadoc")
public class RDFBasedTestCase extends TestBase {

    @Test
    @Changed
    public void testrdfbased_sem_bool_complement_inst() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c2\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c1\"/>\n"
                + "  <ex:c1 rdf:about=\"http://www.example.org#x\">\n"
                + "    <rdf:type rdf:resource=\"http://www.example.org#c2\"/></ex:c1>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#c1\">\n"
                + "    <owl:complementOf rdf:resource=\"http://www.example.org#c2\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "rdfbased_sem_bool_complement_inst";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "An individual cannot be an instance of both a class and its complement.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_bool_intersection_inst_expr() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#x\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#y\"/>\n"
                + "  <ex:c rdf:about=\"http://www.example.org#z\"/>\n"
                + "  <rdf:Description rdf:nodeID=\"A0\">\n"
                + "    <rdf:first rdf:resource=\"http://www.example.org#x\"/>\n"
                + "    <rdf:rest rdf:parseType=\"Collection\">\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#y\"/></rdf:rest></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#c\">\n"
                + "    <owl:intersectionOf rdf:nodeID=\"A0\"/></rdf:Description></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#x\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#y\"/>\n"
                + "  <ex:x rdf:about=\"http://www.example.org#z\">\n"
                + "    <rdf:type rdf:resource=\"http://www.example.org#y\"/></ex:x></rdf:RDF>";
        String id = "rdfbased_sem_bool_intersection_inst_expr";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "An individual, which is an instance of an intersection class expression of two classes, is an instance of every component class.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_chain_def() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x\">\n"
                + "    <ex:p1>\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#y\">\n"
                + "        <ex:p2 rdf:resource=\"http://www.example.org#z\"/></rdf:Description></ex:p1></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p\">\n"
                + "    <owl:propertyChainAxiom rdf:parseType=\"Collection\">\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#p1\"/>\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#p2\"/></owl:propertyChainAxiom></rdf:Description>\n"
                + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x\">\n"
                + "    <ex:p rdf:resource=\"http://www.example.org#z\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_chain_def";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "For a sub property chain axiom with super property p and chain properties p1 and p2, from x p1 y and y p2 z follows x p z.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_char_asymmetric_inst() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "  <owl:AsymmetricProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x\">\n"
                + "    <ex:p>\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#y\">\n"
                + "        <ex:p rdf:resource=\"http://www.example.org#x\"/></rdf:Description></ex:p></rdf:Description>\n"
                + "</rdf:RDF>";
        String conclusion = "";
        String id = "rdfbased_sem_char_asymmetric_inst";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "For a triple having an asymmetrical property as its predicate, the reverse triple must not exist.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_char_asymmetric_term() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:SymmetricProperty rdf:about=\"http://www.example.org#p\">\n"
                + "    <rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#AsymmetricProperty\"/></owl:SymmetricProperty>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x\">\n"
                + "    <ex:p rdf:resource=\"http://www.example.org#y\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "rdfbased_sem_char_asymmetric_term";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "A non-empty property cannot be both symmetrical and asymmetrical.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_char_inversefunc_inst() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:InverseFunctionalProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x1\">\n"
                + "    <ex:p rdf:resource=\"http://www.example.org#y\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x2\">\n"
                + "    <ex:p rdf:resource=\"http://www.example.org#y\"/></rdf:Description></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x1\">\n"
                + "    <owl:sameAs rdf:resource=\"http://www.example.org#x2\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_char_inversefunc_inst";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "For two triples with the same inverse functional property as their predicates and with the same object, the subjects are the same.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_char_irreflexive_inst() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "  <owl:IrreflexiveProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x\">\n"
                + "    <ex:p rdf:resource=\"http://www.example.org#x\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "rdfbased_sem_char_irreflexive_inst";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "For an irreflexive property, there must not exist any reflexive triple.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_char_symmetric_inst() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "  <owl:SymmetricProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x\">\n"
                + "    <ex:p rdf:resource=\"http://www.example.org#y\"/></rdf:Description></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#y\">\n"
                + "    <ex:p rdf:resource=\"http://www.example.org#x\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_char_symmetric_inst";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "For a triple having a symmetrical property as its predicate, the reverse triple also exists.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_char_transitive_inst() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "  <owl:TransitiveProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x\">\n"
                + "    <ex:p>\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#y\">\n"
                + "        <ex:p rdf:resource=\"http://www.example.org#z\"/></rdf:Description></ex:p></rdf:Description>\n"
                + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x\">\n"
                + "    <ex:p rdf:resource=\"http://www.example.org#z\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_char_transitive_inst";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "For two chained triples having the same transitive property as their predicate, the transitive result triple also exists.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_class_nothing_ext() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Nothing rdf:about=\"http://www.example.org#x\"/></rdf:RDF>";
        String conclusion = "";
        String id = "rdfbased_sem_class_nothing_ext";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "The extension of the vocabulary class owl:Nothing is empty.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_class_nothing_term() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.w3.org/2002/07/owl#Nothing\">\n"
                + "    <rdfs:subClassOf rdf:resource=\"http://www.example.org#c\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_class_nothing_term";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Every OWL class is a super class of the vocabulary class owl:Nothing.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_class_nothing_type() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.w3.org/2002/07/owl#Nothing\"/></rdf:RDF>";
        String id = "rdfbased_sem_class_nothing_type";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "The type of the vocabulary class owl:Nothing is the class of OWL classes.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_class_thing_term() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#c\">\n"
                + "    <rdfs:subClassOf rdf:resource=\"http://www.w3.org/2002/07/owl#Thing\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_class_thing_term";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Every OWL class is a sub class of the vocabulary class owl:Thing.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_class_thing_type() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.w3.org/2002/07/owl#Thing\"/></rdf:RDF>";
        String id = "rdfbased_sem_class_thing_type";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "The type of the vocabulary class owl:Thing is the class of OWL classes.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_eqdis_different_sameas() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x\">\n"
                + "    <owl:sameAs rdf:resource=\"http://www.example.org#y\"/>\n"
                + "    <owl:differentFrom rdf:resource=\"http://www.example.org#y\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "rdfbased_sem_eqdis_different_sameas";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "Two individuals cannot both be the same and different.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_eqdis_disclass_eqclass() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c1\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c2\"/>\n"
                + "  <ex:c1 rdf:about=\"http://www.example.org#x\"/>\n"
                + "  <ex:c2 rdf:about=\"http://www.example.org#y\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#c1\">\n"
                + "    <owl:equivalentClass rdf:resource=\"http://www.example.org#c2\"/>\n"
                + "    <owl:disjointWith rdf:resource=\"http://www.example.org#c2\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "rdfbased_sem_eqdis_disclass_eqclass";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "Two non-empty classes cannot both be equivalent and disjoint.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_eqdis_disclass_inst() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c2\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c1\"/>\n"
                + "  <ex:c1 rdf:about=\"http://www.example.org#w\">\n"
                + "    <rdf:type rdf:resource=\"http://www.example.org#c2\"/></ex:c1>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#c1\">\n"
                + "    <owl:disjointWith rdf:resource=\"http://www.example.org#c2\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "rdfbased_sem_eqdis_disclass_inst";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "Individuals being instances of disjoint classes are different from each other.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_eqdis_eqclass_inst() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c2\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c1\"/>\n"
                + "  <ex:c1 rdf:about=\"http://www.example.org#x\"/>\n"
                + "  <ex:c2 rdf:about=\"http://www.example.org#y\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#c1\">\n"
                + "    <owl:equivalentClass rdf:resource=\"http://www.example.org#c2\"/></rdf:Description></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c2\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c1\"/>\n"
                + "  <ex:c2 rdf:about=\"http://www.example.org#x\"/>\n"
                + "  <ex:c1 rdf:about=\"http://www.example.org#y\"/>\n"
                + "</rdf:RDF>";
        String id = "rdfbased_sem_eqdis_eqclass_inst";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "For two equivalent classes, any instance of one class is also an instance of the other class, and vice versa.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_eqdis_eqclass_rflxv() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#c\">\n"
                + "    <owl:equivalentClass rdf:resource=\"http://www.example.org#c\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_eqdis_eqclass_rflxv";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Equivalence of two classes is reflexive.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_eqdis_eqclass_subclass_2() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c2\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c1\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#c2\">\n"
                + "    <rdfs:subClassOf>\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#c1\">\n"
                + "        <rdfs:subClassOf rdf:resource=\"http://www.example.org#c2\"/></rdf:Description></rdfs:subClassOf></rdf:Description>\n"
                + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c2\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c1\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#c1\">\n"
                + "    <owl:equivalentClass rdf:resource=\"http://www.example.org#c2\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_eqdis_eqclass_subclass_2";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Two classes that are sub classes of each other are equivalent classes.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_eqdis_eqclass_subst() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c2\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c1\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#d1\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#d2\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#c1\">\n"
                + "    <rdfs:subClassOf>\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#c2\">\n"
                + "        <owl:equivalentClass rdf:resource=\"http://www.example.org#d2\"/></rdf:Description></rdfs:subClassOf>\n"
                + "    <owl:equivalentClass rdf:resource=\"http://www.example.org#d1\"/></rdf:Description></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c2\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c1\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#d1\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#d2\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#c1\">\n"
                + "    <rdfs:subClassOf rdf:resource=\"http://www.example.org#d2\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#d1\">\n"
                + "    <rdfs:subClassOf rdf:resource=\"http://www.example.org#c2\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_eqdis_eqclass_subst";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Equivalence of two classes allows for substituting one class for the other in a sub class axiom.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_eqdis_eqclass_sym() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#c1\">\n"
                + "    <owl:equivalentClass rdf:resource=\"http://www.example.org#c2\"/></rdf:Description></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#c2\">\n"
                + "    <owl:equivalentClass rdf:resource=\"http://www.example.org#c1\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_eqdis_eqclass_sym";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Equivalence of two classes is symmetrical.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_eqdis_eqclass_trans() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#c1\">\n"
                + "    <owl:equivalentClass>\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#c2\">\n"
                + "        <owl:equivalentClass rdf:resource=\"http://www.example.org#c3\"/></rdf:Description></owl:equivalentClass></rdf:Description>\n"
                + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#c1\">\n"
                + "    <owl:equivalentClass rdf:resource=\"http://www.example.org#c3\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_eqdis_eqclass_trans";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Equivalence of two classes is transitive.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_eqdis_eqprop_inst() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#s2\">\n"
                + "    <ex:p2 rdf:resource=\"http://www.example.org#o2\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#s1\">\n"
                + "    <ex:p1 rdf:resource=\"http://www.example.org#o1\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p1\">\n"
                + "    <owl:equivalentProperty rdf:resource=\"http://www.example.org#p2\"/></rdf:Description></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#s2\">\n"
                + "    <ex:p1 rdf:resource=\"http://www.example.org#o2\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#s1\">\n"
                + "    <ex:p2 rdf:resource=\"http://www.example.org#o1\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_eqdis_eqprop_inst";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "For two equivalent properties and any triple having one property as its predicate, the corresponding triple having the other property as its predicate also exists, and vice versa.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_eqdis_eqprop_rflxv() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#op\"/>\n"
                + "  <owl:DatatypeProperty rdf:about=\"http://www.example.org#dp\"/></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#dp\">\n"
                + "    <owl:equivalentProperty rdf:resource=\"http://www.example.org#dp\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#op\">\n"
                + "    <owl:equivalentProperty rdf:resource=\"http://www.example.org#op\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_eqdis_eqprop_rflxv";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Equivalence of two properties is reflexive.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_eqdis_eqprop_subprop_1() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p1\">\n"
                + "    <owl:equivalentProperty rdf:resource=\"http://www.example.org#p2\"/></rdf:Description></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p2\">\n"
                + "    <rdfs:subPropertyOf>\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#p1\">\n"
                + "        <rdfs:subPropertyOf rdf:resource=\"http://www.example.org#p2\"/></rdf:Description></rdfs:subPropertyOf></rdf:Description>\n"
                + "</rdf:RDF>";
        String id = "rdfbased_sem_eqdis_eqprop_subprop_1";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Two equivalent properties are sub properties of each other.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_eqdis_eqprop_subprop_2() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p2\">\n"
                + "    <rdfs:subPropertyOf>\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#p1\">\n"
                + "        <rdfs:subPropertyOf rdf:resource=\"http://www.example.org#p2\"/></rdf:Description></rdfs:subPropertyOf></rdf:Description>\n"
                + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p1\">\n"
                + "    <owl:equivalentProperty rdf:resource=\"http://www.example.org#p2\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_eqdis_eqprop_subprop_2";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Two properties that are sub properties of each other are equivalent properties.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_eqdis_eqprop_subst() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#q1\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#q2\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p1\">\n"
                + "    <rdfs:subPropertyOf>\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#p2\">\n"
                + "        <owl:equivalentProperty rdf:resource=\"http://www.example.org#q2\"/></rdf:Description></rdfs:subPropertyOf>\n"
                + "    <owl:equivalentProperty rdf:resource=\"http://www.example.org#q1\"/></rdf:Description></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#q1\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#q2\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#q1\">\n"
                + "    <rdfs:subPropertyOf rdf:resource=\"http://www.example.org#p2\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p1\">\n"
                + "    <rdfs:subPropertyOf rdf:resource=\"http://www.example.org#q2\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_eqdis_eqprop_subst";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Equivalence of two properties allows for substituting one property for the other in a sub property axiom.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_eqdis_eqprop_sym() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p1\">\n"
                + "    <owl:equivalentProperty rdf:resource=\"http://www.example.org#p2\"/></rdf:Description></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p2\">\n"
                + "    <owl:equivalentProperty rdf:resource=\"http://www.example.org#p1\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_eqdis_eqprop_sym";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Equivalence of two properties is symmetrical.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_eqdis_eqprop_trans() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p1\">\n"
                + "    <owl:equivalentProperty>\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#p2\">\n"
                + "        <owl:equivalentProperty rdf:resource=\"http://www.example.org#p3\"/></rdf:Description></owl:equivalentProperty></rdf:Description>\n"
                + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p1\">\n"
                + "    <owl:equivalentProperty rdf:resource=\"http://www.example.org#p3\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_eqdis_eqprop_trans";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Equivalence of two properties is transitive.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_eqdis_sameas_rflxv() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#s\">\n"
                + "    <ex:p rdf:resource=\"http://www.example.org#o\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#s1\">\n"
                + "    <owl:sameAs rdf:resource=\"http://www.example.org#s\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#o1\">\n"
                + "    <owl:sameAs rdf:resource=\"http://www.example.org#o\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p1\">\n"
                + "    <owl:sameAs rdf:resource=\"http://www.example.org#p\"/></rdf:Description></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#s\">\n"
                + "    <owl:sameAs rdf:resource=\"http://www.example.org#s1\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#o\">\n"
                + "    <owl:sameAs rdf:resource=\"http://www.example.org#o1\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p\">\n"
                + "    <owl:sameAs rdf:resource=\"http://www.example.org#p1\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_eqdis_sameas_rflxv";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Equality of two individuals is reflexive.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_inv_inst() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#q\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#s2\">\n"
                + "    <ex:q rdf:resource=\"http://www.example.org#o2\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#s1\">\n"
                + "    <ex:p rdf:resource=\"http://www.example.org#o1\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#q\">\n"
                + "    <owl:inverseOf rdf:resource=\"http://www.example.org#p\"/></rdf:Description></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#q\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#o1\">\n"
                + "    <ex:q rdf:resource=\"http://www.example.org#s1\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#o2\">\n"
                + "    <ex:p rdf:resource=\"http://www.example.org#s2\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_inv_inst";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "The subject and object of a triple are reversed by an inverse property.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_ndis_alldifferent_fw() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:AllDifferent rdf:about=\"http://www.example.org#z\">\n"
                + "    <owl:members rdf:parseType=\"Collection\">\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#w1\">\n"
                + "        <owl:sameAs rdf:resource=\"http://www.example.org#w2\"/></rdf:Description>\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#w2\"/>\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#w3\"/></owl:members></owl:AllDifferent>\n"
                + "</rdf:RDF>";
        String conclusion = "";
        String id = "rdfbased_sem_ndis_alldifferent_fw";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "All the members of an owl:AllDifferent construct are mutually different individuals.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_ndis_alldifferent_fw_distinctmembers() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:AllDifferent rdf:about=\"http://www.example.org#z\">\n"
                + "    <owl:distinctMembers rdf:parseType=\"Collection\">\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#w1\">\n"
                + "        <owl:sameAs rdf:resource=\"http://www.example.org#w2\"/></rdf:Description>\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#w2\"/>\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#w3\"/></owl:distinctMembers></owl:AllDifferent>\n"
                + "</rdf:RDF>";
        String conclusion = "";
        String id = "rdfbased_sem_ndis_alldifferent_fw_distinctmembers";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "All the members of an owl:AllDifferent construct are mutually different from each other. This test applies the legacy property owl:distinctMembers.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_ndis_alldisjointclasses_fw() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c2\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c1\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c3\"/>\n"
                + "  <owl:AllDisjointClasses rdf:about=\"http://www.example.org#z\">\n"
                + "    <owl:members rdf:parseType=\"Collection\">\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#c1\"/>\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#c2\"/>\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#c3\"/></owl:members></owl:AllDisjointClasses>\n"
                + "  <ex:c1 rdf:about=\"http://www.example.org#w\">\n"
                + "    <rdf:type rdf:resource=\"http://www.example.org#c2\"/></ex:c1></rdf:RDF>";
        String conclusion = "";
        String id = "rdfbased_sem_ndis_alldisjointclasses_fw";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "All the members of an owl:AllDisjointClasses construct are mutually disjoint classes.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_prop_backwardcompatiblewith_type_annot() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:AnnotationProperty rdf:about=\"http://www.w3.org/2002/07/owl#backwardCompatibleWith\"/></rdf:RDF>";
        String id = "rdfbased_sem_prop_backwardcompatiblewith_type_annot";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "The type of the vocabulary property owl:backwardCompatibleWith is the class of annotation properties.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_prop_comment_type() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:AnnotationProperty rdf:about=\"http://www.w3.org/2000/01/rdf-schema#comment\"/></rdf:RDF>";
        String id = "rdfbased_sem_prop_comment_type";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "The type of the vocabulary property rdfs:comment is the class of annotation properties.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_prop_deprecated_type() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:AnnotationProperty rdf:about=\"http://www.w3.org/2002/07/owl#deprecated\"/></rdf:RDF>";
        String id = "rdfbased_sem_prop_deprecated_type";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "The type of the vocabulary property owl:deprecated is the class of annotation properties.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_prop_incompatiblewith_type_annot() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:AnnotationProperty rdf:about=\"http://www.w3.org/2002/07/owl#incompatibleWith\"/></rdf:RDF>";
        String id = "rdfbased_sem_prop_incompatiblewith_type_annot";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "The type of the vocabulary property owl:incompatibleWith is the class of annotation properties.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_prop_isdefinedby_type() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:AnnotationProperty rdf:about=\"http://www.w3.org/2000/01/rdf-schema#isDefinedBy\"/></rdf:RDF>";
        String id = "rdfbased_sem_prop_isdefinedby_type";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "The type of the vocabulary property rdfs:isDefinedBy is the class of annotation properties.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_prop_label_type() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:AnnotationProperty rdf:about=\"http://www.w3.org/2000/01/rdf-schema#label\"/></rdf:RDF>";
        String id = "rdfbased_sem_prop_label_type";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "The type of the vocabulary property rdfs:label is the class of annotation properties.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_prop_priorversion_type_annot() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:AnnotationProperty rdf:about=\"http://www.w3.org/2002/07/owl#priorVersion\"/></rdf:RDF>";
        String id = "rdfbased_sem_prop_priorversion_type_annot";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "The type of the vocabulary property owl:priorVersion is the class of annotation properties.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_prop_seealso_type() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:AnnotationProperty rdf:about=\"http://www.w3.org/2000/01/rdf-schema#seeAlso\"/></rdf:RDF>";
        String id = "rdfbased_sem_prop_seealso_type";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "The type of the vocabulary property rdfs:seeAlso is the class of annotation properties.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_prop_versioninfo_type() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:AnnotationProperty rdf:about=\"http://www.w3.org/2002/07/owl#versionInfo\"/></rdf:RDF>";
        String id = "rdfbased_sem_prop_versioninfo_type";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "The type of the vocabulary property owl:versionInfo is the class of annotation properties.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_rdfs_subclass_cond() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c2\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c1\"/>\n"
                + "  <ex:c1 rdf:about=\"http://www.example.org#w\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#c1\">\n"
                + "    <rdfs:subClassOf rdf:resource=\"http://www.example.org#c2\"/></rdf:Description></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c2\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c1\"/>\n"
                + "  <ex:c2 rdf:about=\"http://www.example.org#w\"/>\n"
                + "</rdf:RDF>";
        String id = "rdfbased_sem_rdfs_subclass_cond";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "The extensions of two classes related by rdfs:subClassOf are in a subsumption relationship.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_rdfs_subclass_trans() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c2\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c1\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c3\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#c1\">\n"
                + "    <rdfs:subClassOf>\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#c2\">\n"
                + "        <rdfs:subClassOf rdf:resource=\"http://www.example.org#c3\"/></rdf:Description></rdfs:subClassOf></rdf:Description>\n"
                + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c2\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c1\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c3\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#c1\">\n"
                + "    <rdfs:subClassOf rdf:resource=\"http://www.example.org#c3\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_rdfs_subclass_trans";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "The property rdfs:subClassOf is transitive.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_rdfs_subprop_cond() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#s\">\n"
                + "    <ex:p1 rdf:resource=\"http://www.example.org#o\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p1\">\n"
                + "    <rdfs:subPropertyOf rdf:resource=\"http://www.example.org#p2\"/></rdf:Description></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#s\">\n"
                + "    <ex:p2 rdf:resource=\"http://www.example.org#o\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_rdfs_subprop_cond";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "The extensions of two properties related by rdfs:subPropertyOf are in a subsumption relationship.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_rdfs_subprop_trans() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p3\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p1\">\n"
                + "    <rdfs:subPropertyOf>\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#p2\">\n"
                + "        <rdfs:subPropertyOf rdf:resource=\"http://www.example.org#p3\"/></rdf:Description></rdfs:subPropertyOf></rdf:Description>\n"
                + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p3\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p1\">\n"
                + "    <rdfs:subPropertyOf rdf:resource=\"http://www.example.org#p3\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_rdfs_subprop_trans";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "The property rdfs:subPropertyOf is transitive.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_rdfsext_domain_subprop() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p1\">\n"
                + "    <rdfs:subPropertyOf>\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#p2\">\n"
                + "        <rdfs:domain rdf:resource=\"http://www.example.org#c\"/></rdf:Description></rdfs:subPropertyOf></rdf:Description>\n"
                + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p1\">\n"
                + "    <rdfs:domain rdf:resource=\"http://www.example.org#c\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_rdfsext_domain_subprop";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Every sub property of a given property with a given domain also has this domain.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_rdfsext_domain_superclass() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c2\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c1\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p\">\n"
                + "    <rdfs:domain>\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#c1\">\n"
                + "        <rdfs:subClassOf rdf:resource=\"http://www.example.org#c2\"/></rdf:Description></rdfs:domain></rdf:Description>\n"
                + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c2\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c1\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p\">\n"
                + "    <rdfs:domain rdf:resource=\"http://www.example.org#c2\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_rdfsext_domain_superclass";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Every super class of a domain for a given property is itself a domain for that property.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_rdfsext_range_subprop() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p1\">\n"
                + "    <rdfs:subPropertyOf>\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#p2\">\n"
                + "        <rdfs:range rdf:resource=\"http://www.example.org#c\"/></rdf:Description></rdfs:subPropertyOf></rdf:Description>\n"
                + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p1\">\n"
                + "    <rdfs:range rdf:resource=\"http://www.example.org#c\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_rdfsext_range_subprop";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Every sub property of a given property with a given range also has this range.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    @Test
    @Changed
    public void testrdfbased_sem_rdfsext_range_superclass() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c2\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c1\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p\">\n"
                + "    <rdfs:range>\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#c1\">\n"
                + "        <rdfs:subClassOf rdf:resource=\"http://www.example.org#c2\"/></rdf:Description></rdfs:range></rdf:Description>\n"
                + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c2\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c1\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p\">\n"
                + "    <rdfs:range rdf:resource=\"http://www.example.org#c2\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_rdfsext_range_superclass";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Every super class of a range for a given property is itself a range for that property.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }
}
