package bugs;

import static org.junit.Assert.assertEquals;

import javax.annotation.Nonnull;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.io.SystemOutDocumentTarget;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import testbase.TestBase;

@SuppressWarnings("javadoc")
public class DebugVerifyComplianceUniversityTestCase extends TestBase {

    private OWLReasoner reasoner;
    private OWLDataFactory df = OWLManager.getOWLDataFactory();

    @Nonnull
    private static OWLOntology load() throws OWLOntologyCreationException {
        String input = "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
                + "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n"
                + "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n"
                + "Ontology(<urn:uni>\n"
                + "Declaration(Class(<urn:uni#Professor>))\n"
                + "Declaration(Class(<urn:uni#TeachingFaculty>))\n"
                + "Declaration(DataProperty(<urn:uni#hasTenure>))\n"
                + "EquivalentClasses(<urn:uni#Lecturer> DataHasValue(<urn:uni#hasTenure> \"false\"^^xsd:boolean))\n"
                + "DisjointClasses(<urn:uni#Lecturer> <urn:uni#Professor>)\n"
                + "EquivalentClasses(<urn:uni#Professor> DataHasValue(<urn:uni#hasTenure> \"true\"^^xsd:boolean))\n"
                + "DataPropertyDomain(<urn:uni#hasTenure> <urn:uni#TeachingFaculty>)\n"
                + "DataPropertyRange(<urn:uni#hasTenure> xsd:boolean)\n" + ')';
        return OWLManager.createOWLOntologyManager()
                .loadOntologyFromOntologyDocument(
                        new StringDocumentSource(input));
    }

    private static void equal(Object o, boolean object) {
        assertEquals(object, o);
    }

    @Nonnull
    private OWLClass C(@Nonnull String i) {
        return df.getOWLClass(IRI.create(i));
    }

    @Nonnull
    private OWLClass Professor = C("urn:uni#Professor");

    @Before
    public void setUp() throws OWLOntologyCreationException {
        reasoner = factory().createReasoner(load());
    }

    protected void print(OWLOntology o) throws OWLOntologyStorageException {
        o.getOWLOntologyManager().saveOntology(o,
                new FunctionalSyntaxDocumentFormat(),
                new SystemOutDocumentTarget());
    }

    @Test
    public void shouldPassisSatisfiableProfessor() {
        equal(reasoner.isSatisfiable(Professor), true);
    }
}
