package conformancetests;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import testbase.TestBase;

@SuppressWarnings("javadoc")
public class StrangeTestCase extends TestBase {

    @Test
    public void shouldFindThreeSubclasses() throws OWLOntologyCreationException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = m.createOntology();
        OWLDataFactory d = m.getOWLDataFactory();
        m.addAxiom(
                o,
                d.getOWLSubClassOfAxiom(d.getOWLClass(IRI.create("urn:b")),
                        d.getOWLClass(IRI.create("urn:c"))));
        m.addAxiom(
                o,
                d.getOWLSubClassOfAxiom(d.getOWLClass(IRI.create("urn:a")),
                        d.getOWLClass(IRI.create("urn:b"))));
        OWLReasoner r = factory().createReasoner(o);
        r.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        NodeSet<OWLClass> subClasses = r.getSubClasses(
                d.getOWLClass(IRI.create("urn:c")), false);
        Set<OWLClass> flat = subClasses.getFlattened();
        assertEquals(flat.toString(), 3, flat.size());
    }
}
