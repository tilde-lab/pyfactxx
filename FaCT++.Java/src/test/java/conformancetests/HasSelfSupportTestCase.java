package conformancetests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import testbase.TestBase;

@SuppressWarnings("javadoc")
public class HasSelfSupportTestCase extends TestBase {

    @Test
    public void shouldBeConsistent() throws OWLOntologyCreationException {
        String input = "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
                + "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n"
                + "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n"
                + "\n"
                + "\n"
                + "Ontology(\n"
                + "SubClassOf(<http://purl.obolibrary.org/obo/BFO_0000006> ObjectHasSelf(<http://purl.obolibrary.org/obo/BFO_0000083>))\n"
                + ")";
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = m
                .loadOntologyFromOntologyDocument(new StringDocumentSource(
                        input));
        OWLReasoner r = factory().createReasoner(o);
        r.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        assertTrue(r.isConsistent());
    }
}
