package uk.ac.manchester.cs.factplusplus.owlapiv3;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitorEx;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataRangeVisitorEx;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEntityVisitorEx;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLLogicalEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.reasoner.AxiomNotInProfileException;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.ClassExpressionNotInProfileException;
import org.semanticweb.owlapi.reasoner.FreshEntitiesException;
import org.semanticweb.owlapi.reasoner.FreshEntityPolicy;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.semanticweb.owlapi.reasoner.IndividualNodeSetPolicy;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.ReasonerInternalException;
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;
import org.semanticweb.owlapi.reasoner.TimeOutException;
import org.semanticweb.owlapi.reasoner.UnsupportedEntailmentTypeException;
import org.semanticweb.owlapi.reasoner.impl.DefaultNode;
import org.semanticweb.owlapi.reasoner.impl.DefaultNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNode;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLDataPropertyNode;
import org.semanticweb.owlapi.reasoner.impl.OWLDataPropertyNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLDatatypeNode;
import org.semanticweb.owlapi.reasoner.impl.OWLDatatypeNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLNamedIndividualNode;
import org.semanticweb.owlapi.reasoner.impl.OWLNamedIndividualNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLObjectPropertyNode;
import org.semanticweb.owlapi.reasoner.impl.OWLObjectPropertyNodeSet;
import org.semanticweb.owlapi.util.Version;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLFacet;

import uk.ac.manchester.cs.factplusplus.AxiomPointer;
import uk.ac.manchester.cs.factplusplus.ClassPointer;
import uk.ac.manchester.cs.factplusplus.DataPropertyPointer;
import uk.ac.manchester.cs.factplusplus.DataTypeExpressionPointer;
import uk.ac.manchester.cs.factplusplus.DataTypeFacet;
import uk.ac.manchester.cs.factplusplus.DataTypePointer;
import uk.ac.manchester.cs.factplusplus.DataValuePointer;
import uk.ac.manchester.cs.factplusplus.FaCTPlusPlus;
import uk.ac.manchester.cs.factplusplus.FaCTPlusPlusProgressMonitor;
import uk.ac.manchester.cs.factplusplus.IndividualPointer;
import uk.ac.manchester.cs.factplusplus.NodePointer;
import uk.ac.manchester.cs.factplusplus.ObjectPropertyPointer;
import uk.ac.manchester.cs.factplusplus.Pointer;
import uk.ac.manchester.cs.owlapi.modularity.ModuleType;

/*
 * Copyright (C) 2009-2010, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 29-Dec-2009 Synchronization policy: all methods for OWLReasoner are
 * synchronized, except the methods which do not touch the kernel or only affect
 * threadsafe data structures. inner private classes are not synchronized since
 * methods from those classes cannot be invoked from outsize synchronized
 * methods.
 */
public class FaCTPlusPlusReasoner implements OWLReasoner,
        OWLOntologyChangeListener {

    /**
     * reasoner name
     */
    public static final String REASONER_NAME = "FaCT++";
    /**
     * reasoner version
     */
    public static final Version VERSION = new Version(1, 6, 4, 1);
    protected final AtomicBoolean interrupted = new AtomicBoolean(false);
    protected final FaCTPlusPlus kernel = new FaCTPlusPlus();
    private volatile AxiomTranslator axiomTranslator = new AxiomTranslator();
    private volatile ClassExpressionTranslator classExpressionTranslator;
    private volatile DataRangeTranslator dataRangeTranslator;
    private volatile ObjectPropertyTranslator objectPropertyTranslator;
    private volatile DataPropertyTranslator dataPropertyTranslator;
    private volatile IndividualTranslator individualTranslator;
    private volatile EntailmentChecker entailmentChecker;
    private final Map<OWLAxiom, AxiomPointer> axiom2PtrMap = new HashMap<>();
    private final Map<AxiomPointer, OWLAxiom> ptr2AxiomMap = new HashMap<>();
    private static final Set<InferenceType> SupportedInferenceTypes = new HashSet<>(
            Arrays.asList(InferenceType.CLASS_ASSERTIONS,
                    InferenceType.CLASS_HIERARCHY,
                    InferenceType.DATA_PROPERTY_HIERARCHY,
                    InferenceType.OBJECT_PROPERTY_HIERARCHY,
                    InferenceType.SAME_INDIVIDUAL));
    private final OWLOntologyManager manager;
    private final OWLOntology rootOntology;
    private final BufferingMode bufferingMode;
    private final List<OWLOntologyChange> rawChanges = new ArrayList<>();
    // private final ReentrantReadWriteLock rawChangesLock = new
    // ReentrantReadWriteLock();
    private final Set<OWLAxiom> reasonerAxioms = new HashSet<>();
    private final long timeOut;
    private final OWLReasonerConfiguration configuration;

    @Override
    public void ontologiesChanged(List<? extends OWLOntologyChange> changes) {
        handleRawOntologyChanges(changes);
    }

    /**
     * @return reasoner configuration
     */
    public OWLReasonerConfiguration getReasonerConfiguration() {
        return configuration;
    }

    @Override
    public BufferingMode getBufferingMode() {
        return bufferingMode;
    }

    @Override
    public long getTimeOut() {
        return timeOut;
    }

    @Override
    public OWLOntology getRootOntology() {
        return rootOntology;
    }

    private final boolean log = false;

    /**
     * Handles raw ontology changes. If the reasoner is a buffering reasoner
     * then the changes will be stored in a buffer. If the reasoner is a
     * non-buffering reasoner then the changes will be automatically flushed
     * through to the change filter and passed on to the reasoner.
     * 
     * @param changes
     *        The list of raw changes.
     */
    private synchronized void handleRawOntologyChanges(
            List<? extends OWLOntologyChange> changes) {
        if (log) {
            System.out.println(Thread.currentThread().getName()
                    + " OWLReasonerBase.handleRawOntologyChanges() " + changes);
        }
        rawChanges.addAll(changes);
        // We auto-flush the changes if the reasoner is non-buffering
        if (bufferingMode.equals(BufferingMode.NON_BUFFERING)) {
            flush();
        }
    }

    @Override
    public synchronized List<OWLOntologyChange> getPendingChanges() {
        return new ArrayList<>(rawChanges);
    }

    @Override
    public synchronized Set<OWLAxiom> getPendingAxiomAdditions() {
        if (rawChanges.size() > 0) {
            Set<OWLAxiom> added = new HashSet<>();
            computeDiff(added, new HashSet<OWLAxiom>());
            return added;
        }
        return Collections.emptySet();
    }

    @Override
    public synchronized Set<OWLAxiom> getPendingAxiomRemovals() {
        if (rawChanges.size() > 0) {
            Set<OWLAxiom> removed = new HashSet<>();
            computeDiff(new HashSet<OWLAxiom>(), removed);
            return removed;
        }
        return Collections.emptySet();
    }

    /**
     * Flushes the pending changes from the pending change list. The changes
     * will be analysed to determine which axioms have actually been added and
     * removed from the imports closure of the root ontology and then the
     * reasoner will be asked to handle these changes via the
     * {@link #handleChanges(java.util.Set, java.util.Set)} method.
     */
    @Override
    public synchronized void flush() {
        // Process the changes
        if (rawChanges.size() > 0) {
            final Set<OWLAxiom> added = new HashSet<>();
            final Set<OWLAxiom> removed = new HashSet<>();
            computeDiff(added, removed);
            reasonerAxioms.removeAll(removed);
            reasonerAxioms.addAll(added);
            rawChanges.clear();
            if (!added.isEmpty() || !removed.isEmpty()) {
                handleChanges(added, removed);
            }
        }
    }

    /**
     * Computes a diff of what axioms have been added and what axioms have been
     * removed from the list of pending changes. Note that even if the list of
     * pending changes is non-empty then there may be no changes for the
     * reasoner to deal with.
     * 
     * @param added
     *        The logical axioms that have been added to the imports closure of
     *        the reasoner root ontology
     * @param removed
     *        The logical axioms that have been removed from the imports closure
     *        of the reasoner root ontology
     */
    private synchronized void computeDiff(Set<OWLAxiom> added,
            Set<OWLAxiom> removed) {
        for (OWLOntologyChange change : rawChanges) {
            if (change instanceof AddAxiom) {
                OWLAxiom ax = change.getAxiom().getAxiomWithoutAnnotations();
                // check whether an axiom is new
                if (!reasonerAxioms.contains(ax)) {
                    added.add(ax);
                }
            } else if (change instanceof RemoveAxiom) {
                removed.add(change.getAxiom().getAxiomWithoutAnnotations());
            }
        }
        // in case an axiom was added and then removed without a flush()
        added.removeAll(removed);
    }

    /**
     * Gets the axioms that should be currently being reasoned over.
     * 
     * @return A collections of axioms (not containing duplicates) that the
     *         reasoner should be taking into consideration when reasoning. This
     *         set of axioms many not correspond to the current state of the
     *         imports closure of the reasoner root ontology if the reasoner is
     *         buffered.
     */
    public synchronized Collection<OWLAxiom> getReasonerAxioms() {
        return new ArrayList<>(reasonerAxioms);
    }

    @Override
    public FreshEntityPolicy getFreshEntityPolicy() {
        return configuration.getFreshEntityPolicy();
    }

    @Override
    public IndividualNodeSetPolicy getIndividualNodeSetPolicy() {
        return configuration.getIndividualNodeSetPolicy();
    }

    /**
     * @return data factory
     */
    public OWLDataFactory getOWLDataFactory() {
        return rootOntology.getOWLOntologyManager().getOWLDataFactory();
    }

    /**
     * @param rootOntology
     *        ontology
     * @param configuration
     *        configuration
     * @param bufferingMode
     *        buffering
     */
    public FaCTPlusPlusReasoner(OWLOntology rootOntology,
            OWLReasonerConfiguration configuration, BufferingMode bufferingMode) {
        this.rootOntology = rootOntology;
        this.bufferingMode = bufferingMode;
        this.configuration = configuration;
        timeOut = configuration.getTimeOut();
        manager = rootOntology.getOWLOntologyManager();
        for (OWLOntology ont : rootOntology.getImportsClosure()) {
            for (OWLAxiom ax : ont.getLogicalAxioms()) {
                reasonerAxioms.add(ax.getAxiomWithoutAnnotations());
            }
            for (OWLAxiom ax : ont.getAxioms(AxiomType.DECLARATION)) {
                reasonerAxioms.add(ax.getAxiomWithoutAnnotations());
            }
        }
        axiomTranslator = new AxiomTranslator();
        classExpressionTranslator = new ClassExpressionTranslator();
        dataRangeTranslator = new DataRangeTranslator();
        objectPropertyTranslator = new ObjectPropertyTranslator();
        dataPropertyTranslator = new DataPropertyTranslator();
        individualTranslator = new IndividualTranslator();
        entailmentChecker = new EntailmentChecker();
        kernel.setTopBottomPropertyNames(
                "http://www.w3.org/2002/07/owl#topObjectProperty",
                "http://www.w3.org/2002/07/owl#bottomObjectProperty",
                "http://www.w3.org/2002/07/owl#topDataProperty",
                "http://www.w3.org/2002/07/owl#bottomDataProperty");
        kernel.setProgressMonitor(new ProgressMonitorAdapter(configuration
                .getProgressMonitor(), interrupted));
        long millis = configuration.getTimeOut();
        if (millis == Long.MAX_VALUE) {
            millis = 0;
        }
        kernel.setOperationTimeout(millis);
        kernel.setFreshEntityPolicy(configuration.getFreshEntityPolicy() == FreshEntityPolicy.ALLOW);
        loadReasonerAxioms();
    }

    // /////////////////////////////////////////////////////////////////////////
    //
    // load/retract axioms
    //
    // /////////////////////////////////////////////////////////////////////////
    private void loadAxiom(OWLAxiom axiom) {
        if (axiom2PtrMap.containsKey(axiom)) {
            return;
        }
        final AxiomPointer axiomPointer = axiom.accept(axiomTranslator);
        if (axiomPointer != null) {
            axiom2PtrMap.put(axiom, axiomPointer);
            ptr2AxiomMap.put(axiomPointer, axiom);
        }
    }

    private void retractAxiom(OWLAxiom axiom) {
        final AxiomPointer ptr = axiom2PtrMap.get(axiom);
        if (ptr != null) {
            kernel.retract(ptr);
            axiom2PtrMap.remove(axiom);
            ptr2AxiomMap.remove(ptr);
        }
    }

    /**
     * Asks the reasoner implementation to handle axiom additions and removals
     * from the imports closure of the root ontology. The changes will not
     * include annotation axiom additions and removals.
     * 
     * @param addAxioms
     *        The axioms to be added to the reasoner.
     * @param removeAxioms
     *        The axioms to be removed from the reasoner
     */
    protected void handleChanges(Set<OWLAxiom> addAxioms,
            Set<OWLAxiom> removeAxioms) {
        kernel.startChanges();
        for (OWLAxiom ax_a : addAxioms) {
            loadAxiom(ax_a);
        }
        for (OWLAxiom ax_r : removeAxioms) {
            retractAxiom(ax_r);
        }
        kernel.endChanges();
    }

    private void loadReasonerAxioms() {
        getReasonerConfiguration().getProgressMonitor().reasonerTaskStarted(
                ReasonerProgressMonitor.LOADING);
        getReasonerConfiguration().getProgressMonitor().reasonerTaskBusy();
        kernel.clearKernel();
        axiomTranslator = new AxiomTranslator();
        classExpressionTranslator = new ClassExpressionTranslator();
        dataRangeTranslator = new DataRangeTranslator();
        objectPropertyTranslator = new ObjectPropertyTranslator();
        dataPropertyTranslator = new DataPropertyTranslator();
        individualTranslator = new IndividualTranslator();
        axiom2PtrMap.clear();
        ptr2AxiomMap.clear();
        for (OWLAxiom ax : reasonerAxioms) {
            loadAxiom(ax);
        }
        getReasonerConfiguration().getProgressMonitor().reasonerTaskStopped();
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////
    // ////
    // //// Implementation of reasoner interfaces
    // ////
    // ////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public String getReasonerName() {
        return REASONER_NAME;
    }

    @Override
    public Version getReasonerVersion() {
        return VERSION;
    }

    @Override
    public void interrupt() {
        interrupted.set(true);
    }

    // precompute inferences
    @Override
    public synchronized void precomputeInferences(
            InferenceType... inferenceTypes)
            throws ReasonerInterruptedException, TimeOutException,
            InconsistentOntologyException {
        for (InferenceType it : inferenceTypes) {
            if (SupportedInferenceTypes.contains(it)) {
                kernel.realise();
                return;
            }
        }
    }

    @Override
    public boolean isPrecomputed(InferenceType inferenceType) {
        if (SupportedInferenceTypes.contains(inferenceType)) {
            return kernel.isRealised();
        }
        return true;
    }

    @Override
    public Set<InferenceType> getPrecomputableInferenceTypes() {
        return SupportedInferenceTypes;
    }

    // consistency
    @Override
    public synchronized boolean isConsistent()
            throws ReasonerInterruptedException, TimeOutException {
        return kernel.isKBConsistent();
    }

    private void checkConsistency() {
        if (interrupted.get()) {
            throw new ReasonerInterruptedException();
        }
        if (!isConsistent()) {
            throw new InconsistentOntologyException();
        }
    }

    @Override
    public synchronized boolean
            isSatisfiable(OWLClassExpression classExpression)
                    throws ReasonerInterruptedException, TimeOutException,
                    ClassExpressionNotInProfileException,
                    FreshEntitiesException, InconsistentOntologyException {
        checkConsistency();
        return kernel.isClassSatisfiable(toClassPointer(classExpression));
    }

    @Override
    public Node<OWLClass> getUnsatisfiableClasses()
            throws ReasonerInterruptedException, TimeOutException,
            InconsistentOntologyException {
        return getBottomClassNode();
    }

    // entailments
    @Override
    public synchronized boolean isEntailed(OWLAxiom axiom)
            throws ReasonerInterruptedException,
            UnsupportedEntailmentTypeException, TimeOutException,
            AxiomNotInProfileException, FreshEntitiesException,
            InconsistentOntologyException {
        checkConsistency();
        // if(rootOntology.containsAxiom(axiom, true)) {
        // return true;
        // }
        Boolean entailed = axiom.accept(entailmentChecker);
        if (entailed == null) {
            throw new UnsupportedEntailmentTypeException(axiom);
        }
        return entailed;
    }

    @Override
    public synchronized boolean isEntailed(Set<? extends OWLAxiom> axioms)
            throws ReasonerInterruptedException,
            UnsupportedEntailmentTypeException, TimeOutException,
            AxiomNotInProfileException, FreshEntitiesException,
            InconsistentOntologyException {
        for (OWLAxiom ax : axioms) {
            if (!isEntailed(ax)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isEntailmentCheckingSupported(AxiomType<?> axiomType) {
        if (axiomType.equals(AxiomType.SWRL_RULE)) {
            return false;
        }
        // FIXME!! check later
        return true;
    }

    // tracing
    /**
     * @param axiom
     *        axiom to trace
     * @return tracing set (set of axioms that were participate in achieving
     *         result) for a given entailment. Return empty set if the axiom is
     *         not entailed.
     */
    public Set<OWLAxiom> getTrace(OWLAxiom axiom) {
        kernel.needTracing();
        Set<OWLAxiom> ret = new HashSet<>();
        if (isEntailed(axiom)) {
            for (AxiomPointer ap : kernel.getTrace()) {
                ret.add(ptr2AxiomMap.get(ap));
            }
        }
        return ret;
    }

    // classes
    @Override
    public Node<OWLClass> getTopClassNode() {
        return getEquivalentClasses(getOWLDataFactory().getOWLThing());
    }

    @Override
    public Node<OWLClass> getBottomClassNode() {
        return getEquivalentClasses(getOWLDataFactory().getOWLNothing());
    }

    @Override
    public synchronized NodeSet<OWLClass> getSubClasses(OWLClassExpression ce,
            boolean direct) throws ReasonerInterruptedException,
            TimeOutException, FreshEntitiesException,
            InconsistentOntologyException {
        checkConsistency();
        return classExpressionTranslator.getNodeSetFromPointers(kernel
                .askSubClasses(toClassPointer(ce), direct));
    }

    @Override
    public synchronized NodeSet<OWLClass> getSuperClasses(
            OWLClassExpression ce, boolean direct)
            throws InconsistentOntologyException,
            ClassExpressionNotInProfileException, ReasonerInterruptedException,
            TimeOutException {
        checkConsistency();
        return classExpressionTranslator.getNodeSetFromPointers(kernel
                .askSuperClasses(toClassPointer(ce), direct));
    }

    @Override
    public synchronized Node<OWLClass> getEquivalentClasses(
            OWLClassExpression ce) throws InconsistentOntologyException,
            ClassExpressionNotInProfileException, ReasonerInterruptedException,
            TimeOutException {
        checkConsistency();
        ClassPointer[] pointers = kernel
                .askEquivalentClasses(toClassPointer(ce));
        return classExpressionTranslator.getNodeFromPointers(pointers);
    }

    @Override
    public synchronized NodeSet<OWLClass> getDisjointClasses(
            OWLClassExpression ce) {
        checkConsistency();
        return classExpressionTranslator.getNodeSetFromPointers(kernel
                .askDisjointClasses(toClassPointer(ce)));
    }

    // object properties
    @Override
    public Node<OWLObjectPropertyExpression> getTopObjectPropertyNode() {
        return getEquivalentObjectProperties(getOWLDataFactory()
                .getOWLTopObjectProperty());
    }

    @Override
    public Node<OWLObjectPropertyExpression> getBottomObjectPropertyNode() {
        return getEquivalentObjectProperties(getOWLDataFactory()
                .getOWLBottomObjectProperty());
    }

    @Override
    public synchronized NodeSet<OWLObjectPropertyExpression>
            getSubObjectProperties(OWLObjectPropertyExpression pe,
                    boolean direct) throws InconsistentOntologyException,
                    ReasonerInterruptedException, TimeOutException {
        checkConsistency();
        return objectPropertyTranslator.getNodeSetFromPointers(kernel
                .askSubObjectProperties(toObjectPropertyPointer(pe), direct));
    }

    @Override
    public synchronized NodeSet<OWLObjectPropertyExpression>
            getSuperObjectProperties(OWLObjectPropertyExpression pe,
                    boolean direct) throws InconsistentOntologyException,
                    ReasonerInterruptedException, TimeOutException {
        checkConsistency();
        return objectPropertyTranslator.getNodeSetFromPointers(kernel
                .askSuperObjectProperties(toObjectPropertyPointer(pe), direct));
    }

    @Override
    public synchronized Node<OWLObjectPropertyExpression>
            getEquivalentObjectProperties(OWLObjectPropertyExpression pe)
                    throws InconsistentOntologyException,
                    ReasonerInterruptedException, TimeOutException {
        checkConsistency();
        return objectPropertyTranslator.getNodeFromPointers(kernel
                .askEquivalentObjectProperties(toObjectPropertyPointer(pe)));
    }

    @Override
    public synchronized NodeSet<OWLObjectPropertyExpression>
            getDisjointObjectProperties(OWLObjectPropertyExpression pe)
                    throws InconsistentOntologyException,
                    ReasonerInterruptedException, TimeOutException {
        checkConsistency();
        // TODO: incomplete
        OWLObjectPropertyNodeSet toReturn = new OWLObjectPropertyNodeSet();
        toReturn.addNode(getBottomObjectPropertyNode());
        return toReturn;
    }

    @Override
    public synchronized Node<OWLObjectPropertyExpression>
            getInverseObjectProperties(OWLObjectPropertyExpression pe)
                    throws InconsistentOntologyException,
                    ReasonerInterruptedException, TimeOutException {
        checkConsistency();
        return objectPropertyTranslator.getNodeFromPointers(kernel
                .askEquivalentObjectProperties(toObjectPropertyPointer(pe
                        .getInverseProperty())));
    }

    @Override
    public synchronized NodeSet<OWLClass> getObjectPropertyDomains(
            OWLObjectPropertyExpression pe, boolean direct)
            throws InconsistentOntologyException, ReasonerInterruptedException,
            TimeOutException {
        checkConsistency();
        return classExpressionTranslator.getNodeSetFromPointers(kernel
                .askObjectPropertyDomain(
                        objectPropertyTranslator.getPointerFromEntity(pe),
                        direct));
    }

    @Override
    public NodeSet<OWLClass> getObjectPropertyRanges(
            OWLObjectPropertyExpression pe, boolean direct)
            throws InconsistentOntologyException, ReasonerInterruptedException,
            TimeOutException {
        checkConsistency();
        return classExpressionTranslator.getNodeSetFromPointers(kernel
                .askObjectPropertyRange(
                        objectPropertyTranslator.getPointerFromEntity(pe),
                        direct));
    }

    // data properties
    @Override
    public Node<OWLDataProperty> getTopDataPropertyNode() {
        OWLDataPropertyNode toReturn = new OWLDataPropertyNode();
        toReturn.add(getOWLDataFactory().getOWLTopDataProperty());
        return toReturn;
    }

    @Override
    public Node<OWLDataProperty> getBottomDataPropertyNode() {
        return getEquivalentDataProperties(getOWLDataFactory()
                .getOWLBottomDataProperty());
    }

    @Override
    public synchronized NodeSet<OWLDataProperty> getSubDataProperties(
            OWLDataProperty pe, boolean direct)
            throws InconsistentOntologyException, ReasonerInterruptedException,
            TimeOutException {
        checkConsistency();
        return dataPropertyTranslator.getNodeSetFromPointers(kernel
                .askSubDataProperties(toDataPropertyPointer(pe), direct));
    }

    @Override
    public synchronized NodeSet<OWLDataProperty> getSuperDataProperties(
            OWLDataProperty pe, boolean direct)
            throws InconsistentOntologyException, ReasonerInterruptedException,
            TimeOutException {
        checkConsistency();
        return dataPropertyTranslator.getNodeSetFromPointers(kernel
                .askSuperDataProperties(toDataPropertyPointer(pe), direct));
    }

    @Override
    public synchronized Node<OWLDataProperty> getEquivalentDataProperties(
            OWLDataProperty pe) throws InconsistentOntologyException,
            ReasonerInterruptedException, TimeOutException {
        checkConsistency();
        return dataPropertyTranslator.getNodeFromPointers(kernel
                .askEquivalentDataProperties(toDataPropertyPointer(pe)));
    }

    @Override
    public synchronized NodeSet<OWLDataProperty> getDisjointDataProperties(
            OWLDataPropertyExpression pe) throws InconsistentOntologyException,
            ReasonerInterruptedException, TimeOutException {
        checkConsistency();
        // TODO:
        return new OWLDataPropertyNodeSet();
    }

    @Override
    public NodeSet<OWLClass> getDataPropertyDomains(OWLDataProperty pe,
            boolean direct) throws InconsistentOntologyException,
            ReasonerInterruptedException, TimeOutException {
        checkConsistency();
        return classExpressionTranslator.getNodeSetFromPointers(kernel
                .askDataPropertyDomain(
                        dataPropertyTranslator.getPointerFromEntity(pe),
                        direct));
    }

    // individuals
    @Override
    public synchronized NodeSet<OWLClass> getTypes(OWLNamedIndividual ind,
            boolean direct) throws InconsistentOntologyException,
            ReasonerInterruptedException, TimeOutException {
        checkConsistency();
        return classExpressionTranslator.getNodeSetFromPointers(kernel
                .askIndividualTypes(toIndividualPointer(ind), direct));
    }

    @Override
    public synchronized NodeSet<OWLNamedIndividual> getInstances(
            OWLClassExpression ce, boolean direct)
            throws InconsistentOntologyException,
            ClassExpressionNotInProfileException, ReasonerInterruptedException,
            TimeOutException {
        checkConsistency();
        return translateIndividualPointersToNodeSet(kernel.askInstances(
                toClassPointer(ce), direct));
    }

    @Override
    public synchronized NodeSet<OWLNamedIndividual> getObjectPropertyValues(
            OWLNamedIndividual ind, OWLObjectPropertyExpression pe)
            throws InconsistentOntologyException, ReasonerInterruptedException,
            TimeOutException {
        checkConsistency();
        return translateIndividualPointersToNodeSet(kernel
                .askRelatedIndividuals(toIndividualPointer(ind),
                        toObjectPropertyPointer(pe)));
    }

    @Override
    public synchronized Set<OWLLiteral> getDataPropertyValues(
            OWLNamedIndividual ind, OWLDataProperty pe)
            throws InconsistentOntologyException, ReasonerInterruptedException,
            TimeOutException {
        // TODO:
        checkConsistency();
        return Collections.emptySet();
    }

    @Override
    public synchronized Node<OWLNamedIndividual> getSameIndividuals(
            OWLNamedIndividual ind) throws InconsistentOntologyException,
            ReasonerInterruptedException, TimeOutException {
        checkConsistency();
        return individualTranslator.getNodeFromPointers(kernel
                .askSameAs(toIndividualPointer(ind)));
    }

    @Override
    public NodeSet<OWLNamedIndividual> getDifferentIndividuals(
            OWLNamedIndividual ind) throws InconsistentOntologyException,
            ReasonerInterruptedException, TimeOutException {
        OWLClassExpression ce = getOWLDataFactory().getOWLObjectOneOf(ind)
                .getObjectComplementOf();
        return getInstances(ce, false);
    }

    /**
     * get all individuals from the set individuals that has r-successor and
     * s-successor and those are related via OP: r op s
     *
     * @param individuals
     *        set of individuals to choose from
     * @param r
     *        first operand of the comparison
     * @param s
     *        second operand of the comparison
     * @param op
     *        comparison operation: 0 means "==", 1 means "!=", 2 means "&lt;",
     *        3 means "&lt;=", 4 means "&gt;", 5 means "&gt;="
     * @return data related individuals
     */
    public Node<OWLNamedIndividual> getDataRelatedIndividuals(
            Set<OWLIndividual> individuals, OWLDataProperty r,
            OWLDataProperty s, int op) {
        checkConsistency();
        // load all the individuals as parameters
        translateIndividualSet(individuals);
        return individualTranslator.getNodeFromPointers(kernel
                .getDataRelatedIndividuals(toDataPropertyPointer(r),
                        toDataPropertyPointer(s), op));
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // //
    // // Translation to FaCT++ structures and back
    // //
    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private abstract class OWLEntityTranslator<E extends OWLObject, P extends Pointer> {

        private final Map<E, P> entity2PointerMap = new ConcurrentHashMap<>();
        protected final Map<P, E> pointer2EntityMap = new ConcurrentHashMap<>();

        protected void fillEntityPointerMaps(E entity, P pointer) {
            entity2PointerMap.put(entity, pointer);
            pointer2EntityMap.put(pointer, entity);
        }

        protected OWLEntityTranslator() {
            E topEntity = getTopEntity();
            if (topEntity != null) {
                fillEntityPointerMaps(topEntity, getTopEntityPointer());
            }
            E bottomEntity = getBottomEntity();
            if (bottomEntity != null) {
                fillEntityPointerMaps(bottomEntity, getBottomEntityPointer());
            }
        }

        protected P registerNewEntity(E entity) {
            P pointer = createPointerForEntity(entity);
            fillEntityPointerMaps(entity, pointer);
            return pointer;
        }

        public E getEntityFromPointer(P pointer) {
            return pointer2EntityMap.get(pointer);
        }

        public P getPointerFromEntity(E entity) {
            if (entity.isTopEntity()) {
                return getTopEntityPointer();
            } else if (entity.isBottomEntity()) {
                return getBottomEntityPointer();
            } else {
                P pointer = entity2PointerMap.get(entity);
                if (pointer == null) {
                    pointer = registerNewEntity(entity);
                }
                return pointer;
            }
        }

        public Node<E> getNodeFromPointers(P[] pointers) {
            DefaultNode<E> node = createDefaultNode();
            for (P pointer : pointers) {
                final E entityFromPointer = getEntityFromPointer(pointer);
                if (entityFromPointer != null) {
                    node.add(entityFromPointer);
                }
            }
            return node;
        }

        public NodeSet<E> getNodeSetFromPointers(P[][] pointers) {
            DefaultNodeSet<E> nodeSet = createDefaultNodeSet();
            for (P[] pointerArray : pointers) {
                nodeSet.addNode(getNodeFromPointers(pointerArray));
            }
            return nodeSet;
        }

        protected abstract DefaultNode<E> createDefaultNode();

        protected abstract DefaultNodeSet<E> createDefaultNodeSet();

        protected abstract P getTopEntityPointer();

        protected abstract P getBottomEntityPointer();

        protected abstract P createPointerForEntity(E entity);

        protected abstract E getTopEntity();

        protected abstract E getBottomEntity();
    }

    protected ClassPointer toClassPointer(OWLClassExpression classExpression) {
        return classExpression.accept(classExpressionTranslator);
    }

    protected DataTypeExpressionPointer toDataTypeExpressionPointer(
            OWLDataRange dataRange) {
        return dataRange.accept(dataRangeTranslator);
    }

    protected ObjectPropertyPointer toObjectPropertyPointer(
            OWLObjectPropertyExpression propertyExpression) {
        return objectPropertyTranslator.getPointerFromEntity(propertyExpression);
    }

    protected DataPropertyPointer toDataPropertyPointer(
            OWLDataPropertyExpression propertyExpression) {
        return dataPropertyTranslator.getPointerFromEntity(propertyExpression
                .asOWLDataProperty());
    }

    protected synchronized IndividualPointer toIndividualPointer(
            OWLIndividual individual) {
        if (!individual.isAnonymous()) {
            return individualTranslator.getPointerFromEntity(individual
                    .asOWLNamedIndividual());
        } else {
            return kernel.getIndividual(individual.toStringID());
        }
    }

    protected synchronized DataTypePointer toDataTypePointer(
            OWLDatatype datatype) {
        if (datatype == null) {
            throw new IllegalArgumentException("datatype cannot be null");
        }
        String name = checkDateTime(datatype);
        return kernel.getBuiltInDataType(name);
    }

    protected static final String checkDateTime(OWLDatatype datatype) {
        String name = datatype.toStringID();
        if (datatype.isBuiltIn()) {
            OWL2Datatype builtInDatatype = datatype.getBuiltInDatatype();
            OWL2Datatype xsdDateTime = OWL2Datatype.XSD_DATE_TIME;
            if (builtInDatatype == xsdDateTime) {
                name = name + "AsLong";
            }
        }
        return name;
    }

    protected synchronized DataValuePointer toDataValuePointer(
            OWLLiteral literal) {
        String value = literal.getLiteral();
        if (literal.isRDFPlainLiteral()) {
            value = value + "@" + literal.getLang();
        }
        if (literal.getDatatype().isBuiltIn()
                && literal.getDatatype().getBuiltInDatatype() == OWL2Datatype.XSD_DATE_TIME) {
            return kernel.getDataValue(convertToLongDateTime(value),
                    toDataTypePointer(literal.getDatatype()));
        }
        return kernel.getDataValue(value,
                toDataTypePointer(literal.getDatatype()));
    }

    private static final String convertToLongDateTime(String input) {
        XMLGregorianCalendar calendar;
        try {
            calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(
                    input);
            if (calendar.getTimezone() == DatatypeConstants.FIELD_UNDEFINED) {
                // set it to 0 (UTC) in this case; not perfect but avoids
                // indeterminate situations where two datetime literals cannot
                // be compared
                calendar.setTimezone(0);
            }
            long l = calendar.toGregorianCalendar().getTimeInMillis();
//            System.out.println("FaCTPlusPlusReasoner.convertToLongDateTime()\n"
//                    + input + "\n" + Long.toString(l));
            return Long.toString(l);
        } catch (DatatypeConfigurationException e) {
            throw new OWLRuntimeException(
                    "Error: the datatype support in the Java VM is broken! Cannot parse: "
                            + input, e);
        }
    }

    private NodeSet<OWLNamedIndividual> translateIndividualPointersToNodeSet(
            IndividualPointer[] pointers) {
        OWLNamedIndividualNodeSet ns = new OWLNamedIndividualNodeSet();
        for (IndividualPointer pointer : pointers) {
            if (pointer != null) {
                OWLNamedIndividual ind = individualTranslator
                        .getEntityFromPointer(pointer);
                // XXX skipping anonymous individuals - counterintuitive but
                // that's the specs for you
                if (ind != null) {
                    ns.addEntity(ind);
                }
            }
        }
        return ns;
    }

    protected synchronized void translateIndividualSet(Set<OWLIndividual> inds) {
        kernel.initArgList();
        for (OWLIndividual ind : inds) {
            IndividualPointer ip = toIndividualPointer(ind);
            kernel.addArg(ip);
        }
        kernel.closeArgList();
    }

    private class ClassExpressionTranslator extends
            OWLEntityTranslator<OWLClass, ClassPointer> implements
            OWLClassExpressionVisitorEx<ClassPointer> {

        public ClassExpressionTranslator() {}

        @Override
        protected ClassPointer getTopEntityPointer() {
            return kernel.getThing();
        }

        @Override
        protected ClassPointer getBottomEntityPointer() {
            return kernel.getNothing();
        }

        @Override
        protected OWLClass getTopEntity() {
            return getOWLDataFactory().getOWLThing();
        }

        @Override
        protected OWLClass getBottomEntity() {
            return getOWLDataFactory().getOWLNothing();
        }

        @Override
        protected ClassPointer createPointerForEntity(OWLClass entity) {
            return kernel.getNamedClass(entity.toStringID());
        }

        @Override
        protected DefaultNode<OWLClass> createDefaultNode() {
            return new OWLClassNode();
        }

        @Override
        protected DefaultNodeSet<OWLClass> createDefaultNodeSet() {
            return new OWLClassNodeSet();
        }

        @Override
        public ClassPointer visit(OWLClass desc) {
            return getPointerFromEntity(desc);
        }

        @Override
        public ClassPointer visit(OWLObjectIntersectionOf desc) {
            translateClassExpressionSet(desc.getOperands());
            return kernel.getConceptAnd();
        }

        private void translateClassExpressionSet(
                Set<OWLClassExpression> classExpressions) {
            kernel.initArgList();
            for (OWLClassExpression ce : classExpressions) {
                ClassPointer cp = ce.accept(this);
                kernel.addArg(cp);
            }
            kernel.closeArgList();
        }

        @Override
        public ClassPointer visit(OWLObjectUnionOf desc) {
            translateClassExpressionSet(desc.getOperands());
            return kernel.getConceptOr();
        }

        @Override
        public ClassPointer visit(OWLObjectComplementOf desc) {
            return kernel.getConceptNot(desc.getOperand().accept(this));
        }

        @Override
        public ClassPointer visit(OWLObjectSomeValuesFrom desc) {
            return kernel.getObjectSome(toObjectPropertyPointer(desc
                    .getProperty()), desc.getFiller().accept(this));
        }

        @Override
        public ClassPointer visit(OWLObjectAllValuesFrom desc) {
            return kernel.getObjectAll(toObjectPropertyPointer(desc
                    .getProperty()), desc.getFiller().accept(this));
        }

        @Override
        public ClassPointer visit(OWLObjectHasValue desc) {
            return kernel.getObjectValue(
                    toObjectPropertyPointer(desc.getProperty()),
                    toIndividualPointer(desc.getFiller()));
        }

        @Override
        public ClassPointer visit(OWLObjectMinCardinality desc) {
            return kernel.getObjectAtLeast(desc.getCardinality(),
                    toObjectPropertyPointer(desc.getProperty()), desc
                            .getFiller().accept(this));
        }

        @Override
        public ClassPointer visit(OWLObjectExactCardinality desc) {
            return kernel.getObjectExact(desc.getCardinality(),
                    toObjectPropertyPointer(desc.getProperty()), desc
                            .getFiller().accept(this));
        }

        @Override
        public ClassPointer visit(OWLObjectMaxCardinality desc) {
            return kernel.getObjectAtMost(desc.getCardinality(),
                    toObjectPropertyPointer(desc.getProperty()), desc
                            .getFiller().accept(this));
        }

        @Override
        public ClassPointer visit(OWLObjectHasSelf desc) {
            return kernel.getSelf(toObjectPropertyPointer(desc.getProperty()));
        }

        @Override
        public ClassPointer visit(OWLObjectOneOf desc) {
            translateIndividualSet(desc.getIndividuals());
            return kernel.getOneOf();
        }

        @Override
        public ClassPointer visit(OWLDataSomeValuesFrom desc) {
            return kernel.getDataSome(
                    toDataPropertyPointer(desc.getProperty()),
                    toDataTypeExpressionPointer(desc.getFiller()));
        }

        @Override
        public ClassPointer visit(OWLDataAllValuesFrom desc) {
            return kernel.getDataAll(toDataPropertyPointer(desc.getProperty()),
                    toDataTypeExpressionPointer(desc.getFiller()));
        }

        @Override
        public ClassPointer visit(OWLDataHasValue desc) {
            return kernel.getDataValue(
                    toDataPropertyPointer(desc.getProperty()),
                    toDataValuePointer(desc.getFiller()));
        }

        @Override
        public ClassPointer visit(OWLDataMinCardinality desc) {
            return kernel.getDataAtLeast(desc.getCardinality(),
                    toDataPropertyPointer(desc.getProperty()),
                    toDataTypeExpressionPointer(desc.getFiller()));
        }

        @Override
        public ClassPointer visit(OWLDataExactCardinality desc) {
            return kernel.getDataExact(desc.getCardinality(),
                    toDataPropertyPointer(desc.getProperty()),
                    toDataTypeExpressionPointer(desc.getFiller()));
        }

        @Override
        public ClassPointer visit(OWLDataMaxCardinality desc) {
            return kernel.getDataAtMost(desc.getCardinality(),
                    toDataPropertyPointer(desc.getProperty()),
                    toDataTypeExpressionPointer(desc.getFiller()));
        }
    }

    private class DataRangeTranslator extends
            OWLEntityTranslator<OWLDatatype, DataTypePointer> implements
            OWLDataRangeVisitorEx<DataTypeExpressionPointer> {

        public DataRangeTranslator() {}

        @Override
        protected DataTypePointer getTopEntityPointer() {
            return kernel.getDataTop();
        }

        @Override
        protected DataTypePointer getBottomEntityPointer() {
            return null;
        }

        @Override
        protected DefaultNode<OWLDatatype> createDefaultNode() {
            return new OWLDatatypeNode();
        }

        @Override
        protected OWLDatatype getTopEntity() {
            return getOWLDataFactory().getTopDatatype();
        }

        @Override
        protected OWLDatatype getBottomEntity() {
            return null;
        }

        @Override
        protected DefaultNodeSet<OWLDatatype> createDefaultNodeSet() {
            return new OWLDatatypeNodeSet();
        }

        @Override
        protected DataTypePointer createPointerForEntity(OWLDatatype entity) {
            return kernel.getBuiltInDataType(checkDateTime(entity));
        }

        @Override
        public DataTypeExpressionPointer visit(OWLDatatype node) {
            return kernel.getBuiltInDataType(checkDateTime(node));
        }

        @Override
        public DataTypeExpressionPointer visit(OWLDataOneOf node) {
            kernel.initArgList();
            for (OWLLiteral literal : node.getValues()) {
                DataValuePointer dvp = toDataValuePointer(literal);
                kernel.addArg(dvp);
            }
            kernel.closeArgList();
            return kernel.getDataEnumeration();
        }

        @Override
        public DataTypeExpressionPointer visit(OWLDataComplementOf node) {
            return kernel.getDataNot(node.getDataRange().accept(this));
        }

        @Override
        public DataTypeExpressionPointer visit(OWLDataIntersectionOf node) {
            translateDataRangeSet(node.getOperands());
            return kernel.getDataIntersectionOf();
        }

        private void translateDataRangeSet(Set<OWLDataRange> dataRanges) {
            kernel.initArgList();
            for (OWLDataRange op : dataRanges) {
                DataTypeExpressionPointer dtp = op.accept(this);
                kernel.addArg(dtp);
            }
            kernel.closeArgList();
        }

        @Override
        public DataTypeExpressionPointer visit(OWLDataUnionOf node) {
            translateDataRangeSet(node.getOperands());
            return kernel.getDataUnionOf();
        }

        @Override
        public DataTypeExpressionPointer visit(OWLDatatypeRestriction node) {
            DataTypeExpressionPointer dte = node.getDatatype().accept(this);
            for (OWLFacetRestriction restriction : node.getFacetRestrictions()) {
                DataValuePointer dv = toDataValuePointer(restriction
                        .getFacetValue());
                DataTypeFacet facet;
                if (restriction.getFacet().equals(OWLFacet.MIN_INCLUSIVE)) {
                    facet = kernel.getMinInclusiveFacet(dv);
                } else if (restriction.getFacet()
                        .equals(OWLFacet.MAX_INCLUSIVE)) {
                    facet = kernel.getMaxInclusiveFacet(dv);
                } else if (restriction.getFacet()
                        .equals(OWLFacet.MIN_EXCLUSIVE)) {
                    facet = kernel.getMinExclusiveFacet(dv);
                } else if (restriction.getFacet()
                        .equals(OWLFacet.MAX_EXCLUSIVE)) {
                    facet = kernel.getMaxExclusiveFacet(dv);
                } else if (restriction.getFacet().equals(OWLFacet.LENGTH)) {
                    facet = kernel.getLength(dv);
                } else if (restriction.getFacet().equals(OWLFacet.MIN_LENGTH)) {
                    facet = kernel.getMinLength(dv);
                } else if (restriction.getFacet().equals(OWLFacet.MAX_LENGTH)) {
                    facet = kernel.getMaxLength(dv);
                } else if (restriction.getFacet().equals(
                        OWLFacet.FRACTION_DIGITS)) {
                    facet = kernel.getFractionDigitsFacet(dv);
                } else if (restriction.getFacet().equals(OWLFacet.PATTERN)) {
                    facet = kernel.getPattern(dv);
                } else if (restriction.getFacet().equals(OWLFacet.TOTAL_DIGITS)) {
                    facet = kernel.getTotalDigitsFacet(dv);
                } else {
                    throw new OWLRuntimeException("Unsupported facet: "
                            + restriction.getFacet());
                }
                dte = kernel.getRestrictedDataType(dte, facet);
            }
            return dte;
        }
    }

    private class IndividualTranslator extends
            OWLEntityTranslator<OWLNamedIndividual, IndividualPointer> {

        public IndividualTranslator() {}

        @Override
        protected IndividualPointer getTopEntityPointer() {
            return null;
        }

        @Override
        protected IndividualPointer getBottomEntityPointer() {
            return null;
        }

        @Override
        protected IndividualPointer createPointerForEntity(
                OWLNamedIndividual entity) {
            return kernel.getIndividual(entity.toStringID());
        }

        @Override
        protected OWLNamedIndividual getTopEntity() {
            return null;
        }

        @Override
        protected OWLNamedIndividual getBottomEntity() {
            return null;
        }

        @Override
        protected DefaultNode<OWLNamedIndividual> createDefaultNode() {
            return new OWLNamedIndividualNode();
        }

        @Override
        protected DefaultNodeSet<OWLNamedIndividual> createDefaultNodeSet() {
            return new OWLNamedIndividualNodeSet();
        }
    }

    private class ObjectPropertyTranslator
            extends
            OWLEntityTranslator<OWLObjectPropertyExpression, ObjectPropertyPointer> {

        public ObjectPropertyTranslator() {}

        @Override
        protected ObjectPropertyPointer getTopEntityPointer() {
            return kernel.getTopObjectProperty();
        }

        @Override
        protected ObjectPropertyPointer getBottomEntityPointer() {
            return kernel.getBottomObjectProperty();
        }

        @Override
        protected ObjectPropertyPointer registerNewEntity(
                OWLObjectPropertyExpression entity) {
            ObjectPropertyPointer pointer = createPointerForEntity(entity);
            fillEntityPointerMaps(entity, pointer);
            entity = entity.getInverseProperty();
            fillEntityPointerMaps(entity, createPointerForEntity(entity));
            return pointer;
        }

        @Override
        protected ObjectPropertyPointer createPointerForEntity(
                OWLObjectPropertyExpression entity) {
            ObjectPropertyPointer p = kernel.getObjectProperty(entity
                    .getNamedProperty().toStringID());
            if (entity.isAnonymous()) {
                p = kernel.getInverseProperty(p);
            }
            return p;
        }

        @Override
        protected OWLObjectProperty getTopEntity() {
            return getOWLDataFactory().getOWLTopObjectProperty();
        }

        @Override
        protected OWLObjectProperty getBottomEntity() {
            return getOWLDataFactory().getOWLBottomObjectProperty();
        }

        @Override
        protected DefaultNode<OWLObjectPropertyExpression> createDefaultNode() {
            return new OWLObjectPropertyNode();
        }

        @Override
        protected DefaultNodeSet<OWLObjectPropertyExpression>
                createDefaultNodeSet() {
            return new OWLObjectPropertyNodeSet();
        }
    }

    private class DataPropertyTranslator extends
            OWLEntityTranslator<OWLDataProperty, DataPropertyPointer> {

        public DataPropertyTranslator() {}

        @Override
        protected DataPropertyPointer getTopEntityPointer() {
            return kernel.getTopDataProperty();
        }

        @Override
        protected DataPropertyPointer getBottomEntityPointer() {
            return kernel.getBottomDataProperty();
        }

        @Override
        protected DataPropertyPointer createPointerForEntity(
                OWLDataProperty entity) {
            return kernel.getDataProperty(entity.toStringID());
        }

        @Override
        protected OWLDataProperty getTopEntity() {
            return getOWLDataFactory().getOWLTopDataProperty();
        }

        @Override
        protected OWLDataProperty getBottomEntity() {
            return getOWLDataFactory().getOWLBottomDataProperty();
        }

        @Override
        protected DefaultNode<OWLDataProperty> createDefaultNode() {
            return new OWLDataPropertyNode();
        }

        @Override
        protected DefaultNodeSet<OWLDataProperty> createDefaultNodeSet() {
            return new OWLDataPropertyNodeSet();
        }
    }

    private class AxiomTranslator implements OWLAxiomVisitorEx<AxiomPointer> {

        private final class DeclarationVisitorEx implements
                OWLEntityVisitorEx<AxiomPointer> {

            public DeclarationVisitorEx() {}

            @Override
            public AxiomPointer visit(OWLClass cls) {
                return kernel.tellClassDeclaration(toClassPointer(cls));
            }

            @Override
            public AxiomPointer visit(OWLObjectProperty property) {
                return kernel
                        .tellObjectPropertyDeclaration(toObjectPropertyPointer(property));
            }

            @Override
            public AxiomPointer visit(OWLDataProperty property) {
                return kernel
                        .tellDataPropertyDeclaration(toDataPropertyPointer(property));
            }

            @Override
            public AxiomPointer visit(OWLNamedIndividual individual) {
                return kernel
                        .tellIndividualDeclaration(toIndividualPointer(individual));
            }

            @Override
            public AxiomPointer visit(OWLDatatype datatype) {
                return kernel
                        .tellDatatypeDeclaration(toDataTypePointer(datatype));
            }

            @Override
            public AxiomPointer visit(OWLAnnotationProperty property) {
                return null;
            }
        }

        private final DeclarationVisitorEx v;

        public AxiomTranslator() {
            v = new DeclarationVisitorEx();
        }

        @Override
        public AxiomPointer visit(OWLSubClassOfAxiom axiom) {
            return kernel.tellSubClassOf(toClassPointer(axiom.getSubClass()),
                    toClassPointer(axiom.getSuperClass()));
        }

        @Override
        public AxiomPointer
                visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
            return kernel.tellNotRelatedIndividuals(
                    toIndividualPointer(axiom.getSubject()),
                    toObjectPropertyPointer(axiom.getProperty()),
                    toIndividualPointer(axiom.getObject()));
        }

        @Override
        public AxiomPointer visit(OWLAsymmetricObjectPropertyAxiom axiom) {
            return kernel
                    .tellAsymmetricObjectProperty(toObjectPropertyPointer(axiom
                            .getProperty()));
        }

        @Override
        public AxiomPointer visit(OWLReflexiveObjectPropertyAxiom axiom) {
            return kernel
                    .tellReflexiveObjectProperty(toObjectPropertyPointer(axiom
                            .getProperty()));
        }

        @Override
        public AxiomPointer visit(OWLDisjointClassesAxiom axiom) {
            translateClassExpressionSet(axiom.getClassExpressions());
            return kernel.tellDisjointClasses();
        }

        private void translateClassExpressionSet(
                Collection<OWLClassExpression> classExpressions) {
            kernel.initArgList();
            for (OWLClassExpression ce : classExpressions) {
                ClassPointer cp = toClassPointer(ce);
                kernel.addArg(cp);
            }
            kernel.closeArgList();
        }

        @Override
        public AxiomPointer visit(OWLDataPropertyDomainAxiom axiom) {
            return kernel.tellDataPropertyDomain(
                    toDataPropertyPointer(axiom.getProperty()),
                    toClassPointer(axiom.getDomain()));
        }

        @Override
        public AxiomPointer visit(OWLObjectPropertyDomainAxiom axiom) {
            return kernel.tellObjectPropertyDomain(
                    toObjectPropertyPointer(axiom.getProperty()),
                    toClassPointer(axiom.getDomain()));
        }

        @Override
        public AxiomPointer visit(OWLEquivalentObjectPropertiesAxiom axiom) {
            translateObjectPropertySet(axiom.getProperties());
            return kernel.tellEquivalentObjectProperties();
        }

        private void translateObjectPropertySet(
                Collection<OWLObjectPropertyExpression> properties) {
            kernel.initArgList();
            for (OWLObjectPropertyExpression property : properties) {
                ObjectPropertyPointer opp = toObjectPropertyPointer(property);
                kernel.addArg(opp);
            }
            kernel.closeArgList();
        }

        @Override
        public AxiomPointer visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
            return kernel.tellNotRelatedIndividualValue(
                    toIndividualPointer(axiom.getSubject()),
                    toDataPropertyPointer(axiom.getProperty()),
                    toDataValuePointer(axiom.getObject()));
        }

        @Override
        public AxiomPointer visit(OWLDifferentIndividualsAxiom axiom) {
            translateIndividualSet(axiom.getIndividuals());
            return kernel.tellDifferentIndividuals();
        }

        @Override
        public AxiomPointer visit(OWLDisjointDataPropertiesAxiom axiom) {
            translateDataPropertySet(axiom.getProperties());
            return kernel.tellDisjointDataProperties();
        }

        private void translateDataPropertySet(
                Set<OWLDataPropertyExpression> properties) {
            kernel.initArgList();
            for (OWLDataPropertyExpression property : properties) {
                DataPropertyPointer dpp = toDataPropertyPointer(property);
                kernel.addArg(dpp);
            }
            kernel.closeArgList();
        }

        @Override
        public AxiomPointer visit(OWLDisjointObjectPropertiesAxiom axiom) {
            translateObjectPropertySet(axiom.getProperties());
            return kernel.tellDisjointObjectProperties();
        }

        @Override
        public AxiomPointer visit(OWLObjectPropertyRangeAxiom axiom) {
            return kernel.tellObjectPropertyRange(
                    toObjectPropertyPointer(axiom.getProperty()),
                    toClassPointer(axiom.getRange()));
        }

        @Override
        public AxiomPointer visit(OWLObjectPropertyAssertionAxiom axiom) {
            return kernel.tellRelatedIndividuals(
                    toIndividualPointer(axiom.getSubject()),
                    toObjectPropertyPointer(axiom.getProperty()),
                    toIndividualPointer(axiom.getObject()));
        }

        @Override
        public AxiomPointer visit(OWLFunctionalObjectPropertyAxiom axiom) {
            return kernel
                    .tellFunctionalObjectProperty(toObjectPropertyPointer(axiom
                            .getProperty()));
        }

        @Override
        public AxiomPointer visit(OWLSubObjectPropertyOfAxiom axiom) {
            return kernel.tellSubObjectProperties(
                    toObjectPropertyPointer(axiom.getSubProperty()),
                    toObjectPropertyPointer(axiom.getSuperProperty()));
        }

        @Override
        public AxiomPointer visit(OWLDisjointUnionAxiom axiom) {
            translateClassExpressionSet(axiom.getClassExpressions());
            return kernel
                    .tellDisjointUnion(toClassPointer(axiom.getOWLClass()));
        }

        @Override
        public AxiomPointer visit(OWLDeclarationAxiom axiom) {
            OWLEntity entity = axiom.getEntity();
            return entity.accept(v);
        }

        @Override
        public AxiomPointer visit(OWLAnnotationAssertionAxiom axiom) {
            // Ignore
            return null;
        }

        @Override
        public AxiomPointer visit(OWLSymmetricObjectPropertyAxiom axiom) {
            return kernel
                    .tellSymmetricObjectProperty(toObjectPropertyPointer(axiom
                            .getProperty()));
        }

        @Override
        public AxiomPointer visit(OWLDataPropertyRangeAxiom axiom) {
            return kernel.tellDataPropertyRange(
                    toDataPropertyPointer(axiom.getProperty()),
                    toDataTypeExpressionPointer(axiom.getRange()));
        }

        @Override
        public AxiomPointer visit(OWLFunctionalDataPropertyAxiom axiom) {
            return kernel
                    .tellFunctionalDataProperty(toDataPropertyPointer(axiom
                            .getProperty()));
        }

        @Override
        public AxiomPointer visit(OWLEquivalentDataPropertiesAxiom axiom) {
            translateDataPropertySet(axiom.getProperties());
            return kernel.tellEquivalentDataProperties();
        }

        @Override
        public AxiomPointer visit(OWLClassAssertionAxiom axiom) {
            return kernel.tellIndividualType(
                    toIndividualPointer(axiom.getIndividual()),
                    toClassPointer(axiom.getClassExpression()));
        }

        @Override
        public AxiomPointer visit(OWLEquivalentClassesAxiom axiom) {
            translateClassExpressionSet(axiom.getClassExpressions());
            return kernel.tellEquivalentClass();
        }

        @Override
        public AxiomPointer visit(OWLDataPropertyAssertionAxiom axiom) {
            return kernel.tellRelatedIndividualValue(
                    toIndividualPointer(axiom.getSubject()),
                    toDataPropertyPointer(axiom.getProperty()),
                    toDataValuePointer(axiom.getObject()));
        }

        @Override
        public AxiomPointer visit(OWLTransitiveObjectPropertyAxiom axiom) {
            return kernel
                    .tellTransitiveObjectProperty(toObjectPropertyPointer(axiom
                            .getProperty()));
        }

        @Override
        public AxiomPointer visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
            return kernel
                    .tellIrreflexiveObjectProperty(toObjectPropertyPointer(axiom
                            .getProperty()));
        }

        @Override
        public AxiomPointer visit(OWLSubDataPropertyOfAxiom axiom) {
            return kernel.tellSubDataProperties(
                    toDataPropertyPointer(axiom.getSubProperty()),
                    toDataPropertyPointer(axiom.getSuperProperty()));
        }

        @Override
        public AxiomPointer
                visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
            return kernel
                    .tellInverseFunctionalObjectProperty(toObjectPropertyPointer(axiom
                            .getProperty()));
        }

        @Override
        public AxiomPointer visit(OWLSameIndividualAxiom axiom) {
            translateIndividualSet(axiom.getIndividuals());
            return kernel.tellSameIndividuals();
        }

        @Override
        public AxiomPointer visit(OWLSubPropertyChainOfAxiom axiom) {
            translateObjectPropertySet(axiom.getPropertyChain());
            return kernel.tellSubObjectProperties(
                    kernel.getPropertyComposition(),
                    toObjectPropertyPointer(axiom.getSuperProperty()));
        }

        @Override
        public AxiomPointer visit(OWLInverseObjectPropertiesAxiom axiom) {
            return kernel.tellInverseProperties(
                    toObjectPropertyPointer(axiom.getFirstProperty()),
                    toObjectPropertyPointer(axiom.getSecondProperty()));
        }

        @Override
        public AxiomPointer visit(OWLHasKeyAxiom axiom) {
            translateObjectPropertySet(axiom.getObjectPropertyExpressions());
            ObjectPropertyPointer objectPropertyPointer = kernel
                    .getObjectPropertyKey();
            translateDataPropertySet(axiom.getDataPropertyExpressions());
            DataPropertyPointer dataPropertyPointer = kernel
                    .getDataPropertyKey();
            return kernel.tellHasKey(
                    toClassPointer(axiom.getClassExpression()),
                    dataPropertyPointer, objectPropertyPointer);
        }

        @Override
        public AxiomPointer visit(OWLDatatypeDefinitionAxiom axiom) {
            kernel.getDataSubType(axiom.getDatatype().getIRI().toString(),
                    toDataTypeExpressionPointer(axiom.getDataRange()));
            return null;
        }

        @Override
        public AxiomPointer visit(SWRLRule rule) {
            // Ignore
            return null;
        }

        @Override
        public AxiomPointer visit(OWLSubAnnotationPropertyOfAxiom axiom) {
            // Ignore
            return null;
        }

        @Override
        public AxiomPointer visit(OWLAnnotationPropertyDomainAxiom axiom) {
            // Ignore
            return null;
        }

        @Override
        public AxiomPointer visit(OWLAnnotationPropertyRangeAxiom axiom) {
            // Ignore
            return null;
        }
    }

    private class EntailmentChecker implements OWLAxiomVisitorEx<Boolean> {

        public EntailmentChecker() {}

        @Override
        public Boolean visit(OWLSubClassOfAxiom axiom) {
            if (axiom.getSuperClass().equals(getOWLDataFactory().getOWLThing())) {
                return true;
            }
            if (axiom.getSubClass().equals(getOWLDataFactory().getOWLNothing())) {
                return true;
            }
            return kernel.isClassSubsumedBy(
                    toClassPointer(axiom.getSubClass()),
                    toClassPointer(axiom.getSuperClass()));
        }

        @Override
        public Boolean visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
            return axiom.asOWLSubClassOfAxiom().accept(this);
        }

        @Override
        public Boolean visit(OWLAsymmetricObjectPropertyAxiom axiom) {
            return kernel
                    .isObjectPropertyAsymmetric(toObjectPropertyPointer(axiom
                            .getProperty()));
        }

        @Override
        public Boolean visit(OWLReflexiveObjectPropertyAxiom axiom) {
            return kernel
                    .isObjectPropertyReflexive(toObjectPropertyPointer(axiom
                            .getProperty()));
        }

        @Override
        public Boolean visit(OWLDisjointClassesAxiom axiom) {
            Set<OWLClassExpression> classExpressions = axiom
                    .getClassExpressions();
            if (classExpressions.size() == 2) {
                Iterator<OWLClassExpression> it = classExpressions.iterator();
                return kernel.isClassDisjointWith(toClassPointer(it.next()),
                        toClassPointer(it.next()));
            } else {
                for (OWLAxiom ax : axiom.asOWLSubClassOfAxioms()) {
                    if (!ax.accept(this)) {
                        return false;
                    }
                }
                return true;
            }
        }

        @Override
        public Boolean visit(OWLDataPropertyDomainAxiom axiom) {
            return axiom.asOWLSubClassOfAxiom().accept(this);
        }

        @Override
        public Boolean visit(OWLObjectPropertyDomainAxiom axiom) {
            return axiom.asOWLSubClassOfAxiom().accept(this);
        }

        @Override
        public Boolean visit(OWLEquivalentObjectPropertiesAxiom axiom) {
            for (OWLAxiom ax : axiom.asSubObjectPropertyOfAxioms()) {
                if (!ax.accept(this)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public Boolean visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
            return axiom.asOWLSubClassOfAxiom().accept(this);
        }

        @Override
        public Boolean visit(OWLDifferentIndividualsAxiom axiom) {
            for (OWLSubClassOfAxiom ax : axiom.asOWLSubClassOfAxioms()) {
                if (!ax.accept(this)) {
                    return false;
                }
            }
            return true;
        }

        // TODO: this check is incomplete
        @Override
        public Boolean visit(OWLDisjointDataPropertiesAxiom axiom) {
            kernel.initArgList();
            for (OWLDataPropertyExpression p : axiom.getProperties()) {
                kernel.addArg(toDataPropertyPointer(p));
            }
            kernel.closeArgList();
            return kernel.arePropertiesDisjoint();
        }

        @Override
        public Boolean visit(OWLDisjointObjectPropertiesAxiom axiom) {
            kernel.initArgList();
            for (OWLObjectPropertyExpression p : axiom.getProperties()) {
                kernel.addArg(toObjectPropertyPointer(p));
            }
            kernel.closeArgList();
            return kernel.arePropertiesDisjoint();
        }

        @Override
        public Boolean visit(OWLObjectPropertyRangeAxiom axiom) {
            return axiom.asOWLSubClassOfAxiom().accept(this);
        }

        @Override
        public Boolean visit(OWLObjectPropertyAssertionAxiom axiom) {
            return axiom.asOWLSubClassOfAxiom().accept(this);
        }

        @Override
        public Boolean visit(OWLFunctionalObjectPropertyAxiom axiom) {
            return kernel
                    .isObjectPropertyFunctional(toObjectPropertyPointer(axiom
                            .getProperty()));
        }

        @Override
        public Boolean visit(OWLSubObjectPropertyOfAxiom axiom) {
            return kernel.isObjectSubPropertyOf(
                    toObjectPropertyPointer(axiom.getSubProperty()),
                    toObjectPropertyPointer(axiom.getSuperProperty()));
        }

        @Override
        public Boolean visit(OWLDisjointUnionAxiom axiom) {
            return axiom.getOWLEquivalentClassesAxiom().accept(this)
                    && axiom.getOWLDisjointClassesAxiom().accept(this);
        }

        @Override
        public Boolean visit(OWLDeclarationAxiom axiom) {
            // TODO uhm might be needed?
            return false;
        }

        @Override
        public Boolean visit(OWLAnnotationAssertionAxiom axiom) {
            return false;
        }

        @Override
        public Boolean visit(OWLSymmetricObjectPropertyAxiom axiom) {
            return kernel
                    .isObjectPropertySymmetric(toObjectPropertyPointer(axiom
                            .getProperty()));
        }

        @Override
        public Boolean visit(OWLDataPropertyRangeAxiom axiom) {
            return axiom.asOWLSubClassOfAxiom().accept(this);
        }

        @Override
        public Boolean visit(OWLFunctionalDataPropertyAxiom axiom) {
            return kernel.isDataPropertyFunctional(toDataPropertyPointer(axiom
                    .getProperty()));
        }

        @Override
        public Boolean visit(OWLEquivalentDataPropertiesAxiom axiom) {
            // TODO check
            // this is not implemented in OWL API
            // for (OWLAxiom ax : axiom.asSubDataPropertyOfAxioms()) {
            // if (!ax.accept(this)) {
            // return false;
            // }
            // }
            // return true;
            return null;
        }

        @Override
        public Boolean visit(OWLClassAssertionAxiom axiom) {
            return kernel.isInstanceOf(
                    toIndividualPointer(axiom.getIndividual()),
                    toClassPointer(axiom.getClassExpression()));
        }

        @Override
        public Boolean visit(OWLEquivalentClassesAxiom axiom) {
            Set<OWLClassExpression> classExpressionSet = axiom
                    .getClassExpressions();
            if (classExpressionSet.size() == 2) {
                Iterator<OWLClassExpression> it = classExpressionSet.iterator();
                return kernel.isClassEquivalentTo(toClassPointer(it.next()),
                        toClassPointer(it.next()));
            } else {
                for (OWLAxiom ax : axiom.asOWLSubClassOfAxioms()) {
                    if (!ax.accept(this)) {
                        return false;
                    }
                }
                return true;
            }
        }

        @Override
        public Boolean visit(OWLDataPropertyAssertionAxiom axiom) {
            return axiom.asOWLSubClassOfAxiom().accept(this);
        }

        @Override
        public Boolean visit(OWLTransitiveObjectPropertyAxiom axiom) {
            return kernel
                    .isObjectPropertyTransitive(toObjectPropertyPointer(axiom
                            .getProperty()));
        }

        @Override
        public Boolean visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
            return kernel
                    .isObjectPropertyIrreflexive(toObjectPropertyPointer(axiom
                            .getProperty()));
        }

        // TODO: this is incomplete
        @Override
        public Boolean visit(OWLSubDataPropertyOfAxiom axiom) {
            return kernel.isDataSubPropertyOf(
                    toDataPropertyPointer(axiom.getSubProperty()),
                    toDataPropertyPointer(axiom.getSuperProperty()));
        }

        @Override
        public Boolean visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
            return kernel
                    .isObjectPropertyInverseFunctional(toObjectPropertyPointer(axiom
                            .getProperty()));
        }

        @Override
        public Boolean visit(OWLSameIndividualAxiom axiom) {
            for (OWLSameIndividualAxiom ax : axiom.asPairwiseAxioms()) {
                Iterator<OWLIndividual> it = ax.getIndividuals().iterator();
                OWLIndividual indA = it.next();
                OWLIndividual indB = it.next();
                if (!kernel.isSameAs(toIndividualPointer(indA),
                        toIndividualPointer(indB))) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public Boolean visit(OWLSubPropertyChainOfAxiom axiom) {
            kernel.initArgList();
            for (OWLObjectPropertyExpression p : axiom.getPropertyChain()) {
                kernel.addArg(toObjectPropertyPointer(p));
            }
            kernel.closeArgList();
            return kernel.isSubPropertyChainOf(toObjectPropertyPointer(axiom
                    .getSuperProperty()));
        }

        @Override
        public Boolean visit(OWLInverseObjectPropertiesAxiom axiom) {
            for (OWLAxiom ax : axiom.asSubObjectPropertyOfAxioms()) {
                if (!ax.accept(this)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public Boolean visit(OWLHasKeyAxiom axiom) {
            // FIXME!! unsupported by FaCT++ ATM
            return null;
        }

        @Override
        public Boolean visit(OWLDatatypeDefinitionAxiom axiom) {
            // FIXME!! unsupported by FaCT++ ATM
            return null;
        }

        @Override
        public Boolean visit(SWRLRule rule) {
            // FIXME!! unsupported by FaCT++ ATM
            return null;
        }

        @Override
        public Boolean visit(OWLSubAnnotationPropertyOfAxiom axiom) {
            return false;
        }

        @Override
        public Boolean visit(OWLAnnotationPropertyDomainAxiom axiom) {
            return false;
        }

        @Override
        public Boolean visit(OWLAnnotationPropertyRangeAxiom axiom) {
            return false;
        }
    }

    @Override
    public synchronized void dispose() {
        manager.removeOntologyChangeListener(this);
        axiomTranslator = null;
        classExpressionTranslator = null;
        dataRangeTranslator = null;
        objectPropertyTranslator = null;
        dataPropertyTranslator = null;
        individualTranslator = null;
        entailmentChecker = null;
        axiom2PtrMap.clear();
        ptr2AxiomMap.clear();
        rawChanges.clear();
        reasonerAxioms.clear();
        kernel.dispose();
    }

    private static class ProgressMonitorAdapter implements
            FaCTPlusPlusProgressMonitor {

        private int count = 0;
        private int total = 0;
        private final ReasonerProgressMonitor progressMonitor;
        private final AtomicBoolean interrupted;

        public ProgressMonitorAdapter(ReasonerProgressMonitor p,
                AtomicBoolean interr) {
            progressMonitor = p;
            interrupted = interr;
        }

        @Override
        public void setClassificationStarted(int classCount) {
            count = 0;
            total = classCount;
            progressMonitor
                    .reasonerTaskStarted(ReasonerProgressMonitor.CLASSIFYING);
            progressMonitor.reasonerTaskProgressChanged(count, classCount);
        }

        @Override
        public void nextClass() {
            count++;
            progressMonitor.reasonerTaskProgressChanged(count, total);
        }

        @Override
        public void setFinished() {
            progressMonitor.reasonerTaskStopped();
        }

        @Override
        public boolean isCancelled() {
            if (interrupted.get()) {
                throw new ReasonerInterruptedException();
            }
            return false;
        }
    }

    /**
     * @param pw
     *        printstream
     * @param includeBottomNode
     *        true if bottom node included
     */
    public void dumpClassHierarchy(PrintStream pw, boolean includeBottomNode) {
        dumpSubClasses(getTopClassNode(), pw, 0, includeBottomNode);
    }

    private void dumpSubClasses(Node<OWLClass> node, PrintStream pw, int depth,
            boolean includeBottomNode) {
        if (includeBottomNode || !node.isBottomNode()) {
            for (int i = 0; i < depth; i++) {
                pw.print("    ");
            }
            pw.println(node);
            for (Node<OWLClass> sub : getSubClasses(
                    node.getRepresentativeElement(), true)) {
                dumpSubClasses(sub, pw, depth + 1, includeBottomNode);
            }
        }
    }

    /**
     * @param expression
     *        expression to find
     * @return root node for expression
     */
    public NodePointer getRoot(OWLClassExpression expression) {
        return kernel.buildCompletionTree(toClassPointer(expression));
    }

    /**
     * @param object
     *        object to check
     * @param deterministicOnly
     *        true if deterministic only
     * @return neighbors
     */
    public Node<? extends OWLObjectPropertyExpression> getObjectNeighbours(
            NodePointer object, boolean deterministicOnly) {
        return objectPropertyTranslator.getNodeFromPointers(kernel
                .getObjectNeighbours(object, deterministicOnly));
    }

    /**
     * @param object
     *        object to check
     * @param deterministicOnly
     *        true if deterministic only
     * @return neighbors
     */
    public Node<OWLDataProperty> getDataNeighbours(NodePointer object,
            boolean deterministicOnly) {
        return dataPropertyTranslator.getNodeFromPointers(kernel
                .getDataNeighbours(object, deterministicOnly));
    }

    /**
     * @param n
     *        object to check
     * @param property
     *        property to follow
     * @return neighbors
     */
    public Collection<NodePointer> getObjectNeighbours(NodePointer n,
            OWLObjectProperty property) {
        return Arrays.asList(kernel.getObjectNeighbours(n,
                toObjectPropertyPointer(property)));
    }

    /**
     * @param n
     *        object to check
     * @param property
     *        property to follow
     * @return neighbors
     */
    public Collection<NodePointer> getDataNeighbours(NodePointer n,
            OWLDataProperty property) {
        return Arrays.asList(kernel.getDataNeighbours(n,
                toDataPropertyPointer(property)));
    }

    /**
     * @param object
     *        object to check
     * @param deterministicOnly
     *        true if deterministic only
     * @return neighbors
     */
    public Node<? extends OWLClassExpression> getObjectLabel(
            NodePointer object, boolean deterministicOnly) {
        return classExpressionTranslator.getNodeFromPointers(kernel
                .getObjectLabel(object, deterministicOnly));
    }

    /**
     * @param object
     *        object to check
     * @param deterministicOnly
     *        true if deterministic only
     * @return neighbors
     */
    public Node<? extends OWLDataRange> getDataLabel(NodePointer object,
            boolean deterministicOnly) {
        return dataRangeTranslator.getNodeFromPointers(kernel.getDataLabel(
                object, deterministicOnly));
    }

    /**
     * @param object
     *        node pointer
     * @return blocker for object
     */
    public NodePointer getBlocker(NodePointer object) {
        return kernel.getBlocker(object);
    }

    /**
     * Translate OWLAPI enum for the type of module into the interface-friendly
     * int
     * 
     * @param moduleType
     *        module type
     * @return index
     */
    private static int getIndexByModuleType(ModuleType moduleType) {
        switch (moduleType) {
            case BOT:
                return 0;
            case TOP:
                return 1;
            case STAR:
                return 2;
            default:
                throw new ReasonerInternalException("Unsupported module type");
        }
    }

    /**
     * Translate OWLAPI enum for the method of modularisation into the
     * interface-friendly int
     * 
     * @param moduleMethod
     *        module type
     * @return index
     */
    private static int getIndexByModuleMethod(ModuleMethod moduleMethod) {
        switch (moduleMethod) {
            case SYNTACTIC_STANDARD:
                return 0;
            case SYNTACTIC_COUNTING:
                return 1;
            case SEMANTIC:
                return 2;
            default:
                throw new ReasonerInternalException("Unsupported module method");
        }
    }

    /**
     * Build an atomic decomposition using syntactic/semantic locality checking
     * 
     * @param moduleMethod
     *        if true, use semantic locality checking; if false, use syntactic
     *        one
     * @param moduleType
     *        if 0, use \bot modules; if 1, use \top modules; if 2, use STAR
     *        modules
     * @return the size of the constructed atomic decomposition
     */
    public int getAtomicDecompositionSize(ModuleMethod moduleMethod,
            ModuleType moduleType) {
        final int iModuleMethod = getIndexByModuleMethod(moduleMethod);
        final int iModuleType = getIndexByModuleType(moduleType);
        return kernel.getAtomicDecompositionSize(iModuleMethod, iModuleType);
    }

    /**
     * @param index
     *        index of atom
     * @return atom
     */
    public Set<OWLAxiom> getAtomAxioms(int index) {
        AxiomPointer[] axioms = kernel.getAtomAxioms(index);
        return axiomsToSet(axioms);
    }

    /**
     * @param index
     *        index of module
     * @return module
     */
    public Set<OWLAxiom> getAtomModule(int index) {
        AxiomPointer[] axioms = kernel.getAtomModule(index);
        return axiomsToSet(axioms);
    }

    private Set<OWLAxiom> axiomsToSet(AxiomPointer[] axioms) {
        Set<OWLAxiom> toReturn = new HashSet<>();
        for (AxiomPointer p : axioms) {
            final OWLAxiom owlAxiom = ptr2AxiomMap.get(p);
            if (owlAxiom != null) {
                toReturn.add(owlAxiom);
            }
        }
        return toReturn;
    }

    /**
     * @param index
     *        atom index
     * @return atom dependents indexes
     */
    public int[] getAtomDependents(int index) {
        return kernel.getAtomDependents(index);
    }

    /**
     * @return loc check number
     */
    public int getLocCheckNumber() {
        return kernel.getLocCheckNumber();
    }

    private final class EntityVisitorEx implements OWLEntityVisitorEx<Pointer> {

        public EntityVisitorEx() {}

        @Override
        public Pointer visit(OWLClass cls) {
            return toClassPointer(cls);
        }

        @Override
        public Pointer visit(OWLObjectProperty property) {
            return toObjectPropertyPointer(property);
        }

        @Override
        public Pointer visit(OWLDataProperty property) {
            return toDataPropertyPointer(property);
        }

        @Override
        public Pointer visit(OWLNamedIndividual individual) {
            return toIndividualPointer(individual);
        }

        @Override
        public Pointer visit(OWLDatatype datatype) {
            return null;
        }

        @Override
        public Pointer visit(OWLAnnotationProperty property) {
            return null;
        }
    }

    final EntityVisitorEx entityTranslator = new EntityVisitorEx();

    /**
     * @param signature
     *        if true, use semantic locality checking; if false, use syntactic
     *        one
     * @param moduleMethod
     *        module method
     * @param moduleType
     *        if 0, use \bot modules; if 1, use \top modules; if 2, use STAR
     *        modules
     * @return module
     */
    public Set<OWLAxiom> getModule(Set<OWLEntity> signature,
            ModuleMethod moduleMethod, ModuleType moduleType) {
        kernel.initArgList();
        for (OWLEntity entity : signature) {
            if (entity instanceof OWLLogicalEntity) {
                kernel.addArg(entity.accept(entityTranslator));
            }
        }
        final int iModuleMethod = getIndexByModuleMethod(moduleMethod);
        final int iModuleType = getIndexByModuleType(moduleType);
        AxiomPointer[] axioms = kernel.getModule(iModuleMethod, iModuleType);
        return axiomsToSet(axioms);
    }

    /**
     * @param signature
     *        signature
     * @param moduleMethod
     *        module method
     * @param moduleType
     *        module type
     * @return non local axioms
     */
    public Set<OWLAxiom> getNonLocal(Set<OWLEntity> signature,
            ModuleMethod moduleMethod, ModuleType moduleType) {
        kernel.initArgList();
        for (OWLEntity entity : signature) {
            if (entity instanceof OWLLogicalEntity) {
                kernel.addArg(entity.accept(entityTranslator));
            }
        }
        final int iModuleMethod = getIndexByModuleMethod(moduleMethod);
        final int iModuleType = getIndexByModuleType(moduleType);
        AxiomPointer[] axioms = kernel.getNonLocal(iModuleMethod, iModuleType);
        return axiomsToSet(axioms);
    }

    /*
     * Save/load support
     */
    /**
     * @param name
     *        save/load context name
     * @return true iff the file exists
     */
    public boolean checkSaveLoadContext(String name) {
        return kernel.checkSaveLoadContext(name);
    }

    /**
     * Set a save/load file to a given name. If the named file doesn't exist,
     * then it would be created and the classified taxonomy would be saved
     * there. If the file exists, the once classified taxonomy will be loaded
     * from there. If the parameter is null, no loading of taxonomy is
     * performed.
     * 
     * @param name
     *        save/load file name. Do not load the pre-classified taxonomy if
     *        null
     * @return true if successful
     */
    public boolean setSaveLoadContext(String name) {
        return kernel.setSaveLoadContext(name);
    }

    /**
     * Clear the saved reasoner state in the file name
     * 
     * @param name
     *        save/load file name
     * @return true if successful
     */
    public boolean clearSaveLoadContext(String name) {
        return kernel.clearSaveLoadContext(name);
    }
}
