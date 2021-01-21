package conformancetests;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.io.SystemOutDocumentTarget;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.profiles.OWL2DLProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

@SuppressWarnings({ "javadoc", "null" })
public class JUnitRunner {

    private static final int _10000 = 1000000;

    public static void print(String premise) throws OWLException {
        OWLOntology o = OWLManager.createOWLOntologyManager()
                .loadOntologyFromOntologyDocument(
                        new StringDocumentSource(premise));
        o.getOWLOntologyManager().saveOntology(o,
                new FunctionalSyntaxDocumentFormat(),
                new SystemOutDocumentTarget());
    }

    private final TestClasses t;
    private OWLReasonerFactory f;
    private final String testId;
    private final String premise;
    private final String consequence;
    private final String description;

    public JUnitRunner(String premise, String consequence, String testId,
            TestClasses t, String description) {
        this.testId = testId;
        this.premise = premise;
        this.consequence = consequence;
        this.description = description;
        this.t = t;
    }

    public void setReasonerFactory(OWLReasonerFactory f) {
        this.f = f;
    }

    private static boolean isConsistent(OWLReasoner reasoner) {
        return reasoner.isConsistent();
    }

    private static boolean
            isEntailed(OWLReasoner reasoner, OWLAxiom conclusion) {
        return reasoner.isEntailed(conclusion);
    }

    public OWLOntology getPremise() throws OWLOntologyCreationException {
        if (premise != null) {
            StringDocumentSource documentSource = new StringDocumentSource(
                    premise);
            return OWLManager.createOWLOntologyManager()
                    .loadOntologyFromOntologyDocument(documentSource);
        }
        return null;
    }

    public OWLOntology getConsequence() throws OWLOntologyCreationException {
        if (consequence != null) {
            StringDocumentSource documentSource = new StringDocumentSource(
                    consequence);
            return OWLManager.createOWLOntologyManager()
                    .loadOntologyFromOntologyDocument(documentSource);
        }
        return null;
    }

    public void run() {
        OWLOntology premiseOntology = null;
        // OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        // m.setSilentMissingImportsHandling(true);
        try {
            if (premise != null) {
                premiseOntology = getPremise();
                OWL2DLProfile profile = new OWL2DLProfile();
                OWLProfileReport report = profile
                        .checkOntology(premiseOntology);
                if (report.getViolations().size() > 0) {
                    System.out.println("JUnitRunner.run() " + testId);
                    System.out
                            .println("JUnitRunner.run() premise violations:\n"
                                    + report.toString());
                    throw new RuntimeException("errors!\n" + report.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            // System.out.println("JUnitRunner.run() premise:\n" + premise);
            throw new RuntimeException(e);
        }
        OWLOntology conclusionOntology = null;
        try {
            if (consequence != null) {
                conclusionOntology = getConsequence();
                OWL2DLProfile profile = new OWL2DLProfile();
                OWLProfileReport report = profile
                        .checkOntology(conclusionOntology);
                if (report.getViolations().size() > 0) {
                    System.out.println("JUnitRunner.run() " + testId
                            + report.getViolations().size());
                    System.out
                            .println("JUnitRunner.run() conclusion violations:\n"
                                    + report.toString());
                    throw new RuntimeException("errors!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            throw new RuntimeException(e);
        }
        this.run(premiseOntology, conclusionOntology);
    }

    protected void run(OWLOntology premiseOntology,
            OWLOntology conclusionOntology) {
        StringBuilder b = new StringBuilder();
        b.append("JUnitRunner.logTroubles() premise");
        b.append('\n');
        for (OWLAxiom ax1 : premiseOntology.getAxioms()) {
            b.append(ax1);
            b.append('\n');
        }
        OWLReasoner reasoner = f.createReasoner(premiseOntology);
        actual(conclusionOntology, b, reasoner);
        // reasoner = roundtrip(reasoner);
        // actual(conclusionOntology, b, reasoner);
        premiseOntology.getOWLOntologyManager().removeOntologyChangeListener(
                (OWLOntologyChangeListener) reasoner);
    }

    private static OWLReasoner roundtrip(OWLReasoner r) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream stream = new ObjectOutputStream(out);
            stream.writeObject(r);
            stream.flush();
            ByteArrayInputStream in = new ByteArrayInputStream(
                    out.toByteArray());
            ObjectInputStream inStream = new ObjectInputStream(in);
            return (OWLReasoner) inStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void actual(OWLOntology conclusionOntology, StringBuilder b,
            OWLReasoner reasoner) {
        switch (t) {
            case CONSISTENCY: {
                boolean consistent = isConsistent(reasoner);
                if (!consistent) {
                    String message = b.toString()
                            + logTroubles(true, consistent, t, null);
                    assertTrue(message, consistent);
                }
            }
                break;
            case INCONSISTENCY: {
                boolean consistent = isConsistent(reasoner);
                if (consistent) {
                    String message = b.toString()
                            + logTroubles(false, consistent, t, null);
                    assertFalse(message, consistent);
                }
            }
                break;
            case NEGATIVE_IMPL: {
                boolean consistent = isConsistent(reasoner);
                if (!consistent) {
                    String message = b.toString()
                            + logTroubles(true, consistent, t, null);
                    assertTrue(message, consistent);
                }
                boolean entailed = false;
                for (OWLAxiom ax : conclusionOntology.getLogicalAxioms()) {
                    boolean temp = isEntailed(reasoner, ax);
                    entailed |= temp;
                    if (temp) {
                        b.append(logTroubles(false, entailed, t, ax));
                    }
                }
                assertFalse(b.toString(), entailed);
            }
                break;
            case POSITIVE_IMPL: {
                boolean consistent = isConsistent(reasoner);
                if (!consistent) {
                    String message = b.toString()
                            + logTroubles(true, consistent, t, null);
                    assertTrue(message, consistent);
                }
                boolean entailed = true;
                for (OWLAxiom ax : conclusionOntology.getLogicalAxioms()) {
                    boolean temp = isEntailed(reasoner, ax);
                    entailed &= temp;
                    if (!temp) {
                        b.append(logTroubles(true, entailed, t, ax));
                    }
                }
                assertTrue(b.toString(), entailed);
            }
                break;
            default:
                break;
        }
    }

    public String logTroubles(boolean expected, boolean actual,
            TestClasses testclass, OWLAxiom axiom) {
        StringBuilder b = new StringBuilder();
        b.append("JUnitRunner.logTroubles() \t");
        b.append(testclass);
        b.append('\t');
        b.append(testId);
        b.append("\n ======================================\n");
        b.append(description);
        b.append("\nexpected: ");
        b.append(expected);
        b.append("\t actual: ");
        b.append(actual);
        if (axiom != null) {
            b.append("\n for axiom: ");
            b.append(axiom.toString());
        }
        String string = b.toString();
        System.out.println(string);
        return string;
    }

    public void printPremise() throws OWLOntologyStorageException,
            OWLOntologyCreationException {
        OWLOntology premise2 = getPremise();
        premise2.getOWLOntologyManager().saveOntology(premise2,
                new FunctionalSyntaxDocumentFormat(),
                new SystemOutDocumentTarget());
    }

    public void printConsequence() throws OWLOntologyStorageException,
            OWLOntologyCreationException {
        OWLOntology consequence2 = getConsequence();
        consequence2.getOWLOntologyManager().saveOntology(consequence2,
                new FunctionalSyntaxDocumentFormat(),
                new SystemOutDocumentTarget());
    }
}
