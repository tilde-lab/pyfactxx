package conformancetests;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static org.junit.Assert.*;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import testbase.TestBase;

@SuppressWarnings("javadoc")
public class WebOnt_description_logic_602_TestCase extends TestBase {

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
        m.addAxiom(o, f.getOWLEquivalentClassesAxiom(C, rAllC));
        m.addAxiom(
                o,
                f.getOWLEquivalentClassesAxiom(D,
                        f.getOWLObjectMaxCardinality(0, p)));
        m.addAxiom(
                o,
                f.getOWLEquivalentClassesAxiom(B,
                        f.getOWLObjectMinCardinality(1, p)));
        OWLReasoner reasoner = factory().createReasoner(o);
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
}
