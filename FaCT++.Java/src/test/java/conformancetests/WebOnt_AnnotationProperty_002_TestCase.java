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
public class WebOnt_AnnotationProperty_002_TestCase extends TestBase {

    @Test
    @Ignore("FaCT++ vs OWL API mismatch")
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
}
