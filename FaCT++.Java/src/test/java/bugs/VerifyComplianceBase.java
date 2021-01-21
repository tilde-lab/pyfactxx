package bugs;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nonnull;

import org.junit.Before;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.io.SystemOutDocumentTarget;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.profiles.OWLProfileViolation;
import org.semanticweb.owlapi.profiles.Profiles;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import testbase.TestBase;

@SuppressWarnings("javadoc")
public abstract class VerifyComplianceBase extends TestBase {

    protected abstract String input();

    protected OWLReasoner reasoner;
    protected OWLDataFactory df = OWLManager.getOWLDataFactory();

    @Nonnull
    protected OWLOntology load(String in) throws OWLOntologyCreationException {
        OWLOntology onto = OWLManager.createOWLOntologyManager()
                .loadOntologyFromOntologyDocument(
                        VerifyComplianceBase.class.getResourceAsStream(in));
        OWLProfileReport checkOntology = Profiles.OWL2_DL.checkOntology(onto);
        if (!checkOntology.isInProfile()) {
            for (OWLProfileViolation v : checkOntology.getViolations()) {
                System.out.println("VerifyComplianceBase.load() " + v);
            }
        }
        return onto;
    }

    @Nonnull
    protected OWLOntology loadFromString(@Nonnull String in)
            throws OWLOntologyCreationException {
        return OWLManager.createOWLOntologyManager()
                .loadOntologyFromOntologyDocument(new StringDocumentSource(in));
    }

    protected static String set(Iterable<OWLEntity> i) {
        Set<String> s = new TreeSet<>();
        for (OWLEntity e : i) {
            s.add(e.getIRI().getShortForm());
        }
        return s.toString().replace("[", "").replace("]", "")
                .replace(", ", "\n");
    }

    @SuppressWarnings({ "unchecked" })
    protected static void equal(NodeSet<?> node, OWLEntity... objects) {
        assertEquals(set(Arrays.asList(objects)),
                set((Set<OWLEntity>) node.getFlattened()));
    }

    @SuppressWarnings("unchecked")
    protected static void equal(Node<?> node, OWLEntity... objects) {
        assertEquals(set(Arrays.asList(objects)),
                set((Set<OWLEntity>) node.getEntities()));
    }

    protected static void equal(Object o, boolean object) {
        assertEquals(object, o);
    }

    @Nonnull
    protected OWLClass C(@Nonnull String i) {
        return df.getOWLClass(IRI.create(i));
    }

    @Nonnull
    protected OWLNamedIndividual I(@Nonnull String i) {
        return df.getOWLNamedIndividual(IRI.create(i));
    }

    @Nonnull
    protected OWLObjectProperty OP(@Nonnull String i) {
        return df.getOWLObjectProperty(IRI.create(i));
    }

    @Nonnull
    protected OWLDataProperty DP(@Nonnull String i) {
        return df.getOWLDataProperty(IRI.create(i));
    }

    @Nonnull
    protected OWLDataProperty bottomDataProperty = df
            .getOWLBottomDataProperty();
    @Nonnull
    protected OWLDataProperty topDataProperty = df.getOWLTopDataProperty();
    @Nonnull
    protected OWLObjectProperty topObjectProperty = df
            .getOWLTopObjectProperty();
    @Nonnull
    protected OWLObjectProperty bottomObjectProperty = df
            .getOWLBottomObjectProperty();
    @Nonnull
    protected OWLClass owlThing = df.getOWLThing();
    @Nonnull
    protected OWLClass owlNothing = df.getOWLNothing();

    @Before
    public void setUp() throws OWLOntologyCreationException {
        reasoner = factory().createReasoner(load(input()));
        reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
    }

    protected void switchLoggingOn() {
        // reasoner.getConfiguration().setLoggingActive(true);
    }

    protected void print() {
        OWLOntology o = reasoner.getRootOntology();
        try {
            o.getOWLOntologyManager().saveOntology(o,
                    new FunctionalSyntaxDocumentFormat(),
                    new SystemOutDocumentTarget());
        } catch (OWLOntologyStorageException e) {
            e.printStackTrace();
        }
    }
}
