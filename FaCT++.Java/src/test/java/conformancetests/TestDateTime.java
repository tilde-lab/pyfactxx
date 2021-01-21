package conformancetests;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static org.junit.Assert.*;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.Class;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.ClassAssertion;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.DataProperty;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.DataPropertyAssertion;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.DataSomeValuesFrom;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.Datatype;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.DatatypeRestriction;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.EquivalentClasses;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.FacetRestriction;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.FunctionalDataProperty;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.IRI;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.Literal;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.NamedIndividual;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.SameIndividual;

import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLFacet;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

import testbase.TestBase;

@SuppressWarnings("javadoc")
public class TestDateTime extends TestBase {

    @Test
    public void testEqual() throws OWLOntologyCreationException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = m.createOntology();
        OWLDataFactory f = m.getOWLDataFactory();
        OWLNamedIndividual x = f
                .getOWLNamedIndividual(IRI.create("urn:test:x"));
        OWLNamedIndividual y = f
                .getOWLNamedIndividual(IRI.create("urn:test:y"));
        OWLDataProperty p = f.getOWLDataProperty(IRI.create("urn:test:p"));
        OWLLiteral date = f.getOWLLiteral("2008-07-08T20:44:11.656+01:00",
                OWL2Datatype.XSD_DATE_TIME);
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
    @Ignore("FaCT++ datatype problems")
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
    @Ignore("FaCT++ datatype problems")
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
    @Ignore("FaCT++ unsipported datatype")
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
    @Ignore("FaCT++ unsipported datatype")
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
    @Ignore("FaCT++ unsipported datatype")
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
}
