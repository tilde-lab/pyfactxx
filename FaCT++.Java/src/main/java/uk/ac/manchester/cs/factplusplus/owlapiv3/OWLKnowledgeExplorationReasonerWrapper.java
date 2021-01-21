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
package uk.ac.manchester.cs.factplusplus.owlapiv3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
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
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;
import org.semanticweb.owlapi.reasoner.TimeOutException;
import org.semanticweb.owlapi.reasoner.UnsupportedEntailmentTypeException;
import org.semanticweb.owlapi.reasoner.knowledgeexploration.OWLKnowledgeExplorerReasoner;
import org.semanticweb.owlapi.util.Version;

import uk.ac.manchester.cs.factplusplus.NodePointer;

/**
 * Wrapper class for the new interface in the OWL API; it decouples the rest of
 * the reasoner from relying on an OWL API above 3.2.4
 */
public class OWLKnowledgeExplorationReasonerWrapper implements
        OWLKnowledgeExplorerReasoner {

    private class RootNodeImpl implements RootNode {

        private final NodePointer pointer;

        public RootNodeImpl(NodePointer p) {
            pointer = p;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T getNode() {
            return (T) pointer;
        }

        @Override
        public int hashCode() {
            return pointer.hashCode();
        }

        @Override
        public boolean equals(Object arg0) {
            if (arg0 == null) {
                return false;
            }
            if (this == arg0) {
                return true;
            }
            if (arg0 instanceof RootNode) {
                return pointer.equals(((RootNode) arg0).getNode());
            }
            return false;
        }
    }

    private final FaCTPlusPlusReasoner r;

    /**
     * @param r
     *        reasoner
     */
    public OWLKnowledgeExplorationReasonerWrapper(FaCTPlusPlusReasoner r) {
        this.r = r;
    }

    /**
     * @param changes
     *        changes
     */
    public void ontologiesChanged(List<? extends OWLOntologyChange> changes) {
        r.ontologiesChanged(changes);
    }

    /**
     * @return reasoner configuration
     */
    public OWLReasonerConfiguration getReasonerConfiguration() {
        return r.getReasonerConfiguration();
    }

    @Override
    public BufferingMode getBufferingMode() {
        return r.getBufferingMode();
    }

    @Override
    public long getTimeOut() {
        return r.getTimeOut();
    }

    @Override
    public OWLOntology getRootOntology() {
        return r.getRootOntology();
    }

    @Override
    public List<OWLOntologyChange> getPendingChanges() {
        return r.getPendingChanges();
    }

    @Override
    public Set<OWLAxiom> getPendingAxiomAdditions() {
        return r.getPendingAxiomAdditions();
    }

    @Override
    public Set<OWLAxiom> getPendingAxiomRemovals() {
        return r.getPendingAxiomRemovals();
    }

    @Override
    public FreshEntityPolicy getFreshEntityPolicy() {
        return r.getFreshEntityPolicy();
    }

    @Override
    public IndividualNodeSetPolicy getIndividualNodeSetPolicy() {
        return r.getIndividualNodeSetPolicy();
    }

    @Override
    public String getReasonerName() {
        return r.getReasonerName();
    }

    @Override
    public Version getReasonerVersion() {
        return r.getReasonerVersion();
    }

    @Override
    public void interrupt() {
        r.interrupt();
    }

    @Override
    public void precomputeInferences(InferenceType... inferenceTypes)
            throws ReasonerInterruptedException, TimeOutException,
            InconsistentOntologyException {
        r.precomputeInferences(inferenceTypes);
    }

    @Override
    public boolean isPrecomputed(InferenceType inferenceType) {
        return r.isPrecomputed(inferenceType);
    }

    @Override
    public Set<InferenceType> getPrecomputableInferenceTypes() {
        return r.getPrecomputableInferenceTypes();
    }

    @Override
    public boolean isConsistent() throws ReasonerInterruptedException,
            TimeOutException {
        return r.isConsistent();
    }

    @Override
    public boolean isSatisfiable(OWLClassExpression classExpression)
            throws ReasonerInterruptedException, TimeOutException,
            ClassExpressionNotInProfileException, FreshEntitiesException,
            InconsistentOntologyException {
        return r.isSatisfiable(classExpression);
    }

    @Override
    public Node<OWLClass> getUnsatisfiableClasses()
            throws ReasonerInterruptedException, TimeOutException,
            InconsistentOntologyException {
        return r.getUnsatisfiableClasses();
    }

    @Override
    public boolean isEntailed(OWLAxiom axiom)
            throws ReasonerInterruptedException,
            UnsupportedEntailmentTypeException, TimeOutException,
            AxiomNotInProfileException, FreshEntitiesException,
            InconsistentOntologyException {
        return r.isEntailed(axiom);
    }

    @Override
    public boolean isEntailed(Set<? extends OWLAxiom> axioms)
            throws ReasonerInterruptedException,
            UnsupportedEntailmentTypeException, TimeOutException,
            AxiomNotInProfileException, FreshEntitiesException,
            InconsistentOntologyException {
        return r.isEntailed(axioms);
    }

    @Override
    public boolean isEntailmentCheckingSupported(AxiomType<?> axiomType) {
        return r.isEntailmentCheckingSupported(axiomType);
    }

    /**
     * @param axiom
     *        axiom to trace
     * @return trace
     */
    public Set<OWLAxiom> getTrace(OWLAxiom axiom) {
        return r.getTrace(axiom);
    }

    @Override
    public Node<OWLClass> getTopClassNode() {
        return r.getTopClassNode();
    }

    @Override
    public Node<OWLClass> getBottomClassNode() {
        return r.getBottomClassNode();
    }

    @Override
    public NodeSet<OWLClass>
            getSubClasses(OWLClassExpression ce, boolean direct)
                    throws ReasonerInterruptedException, TimeOutException,
                    FreshEntitiesException, InconsistentOntologyException {
        return r.getSubClasses(ce, direct);
    }

    @Override
    public NodeSet<OWLClass> getSuperClasses(OWLClassExpression ce,
            boolean direct) throws InconsistentOntologyException,
            ClassExpressionNotInProfileException, ReasonerInterruptedException,
            TimeOutException {
        return r.getSuperClasses(ce, direct);
    }

    @Override
    public Node<OWLClass> getEquivalentClasses(OWLClassExpression ce)
            throws InconsistentOntologyException,
            ClassExpressionNotInProfileException, ReasonerInterruptedException,
            TimeOutException {
        return r.getEquivalentClasses(ce);
    }

    @Override
    public Node<OWLObjectPropertyExpression> getTopObjectPropertyNode() {
        return r.getTopObjectPropertyNode();
    }

    @Override
    public Node<OWLObjectPropertyExpression> getBottomObjectPropertyNode() {
        return r.getBottomObjectPropertyNode();
    }

    @Override
    public NodeSet<OWLObjectPropertyExpression> getSubObjectProperties(
            OWLObjectPropertyExpression pe, boolean direct)
            throws InconsistentOntologyException, ReasonerInterruptedException,
            TimeOutException {
        return r.getSubObjectProperties(pe, direct);
    }

    @Override
    public NodeSet<OWLObjectPropertyExpression> getSuperObjectProperties(
            OWLObjectPropertyExpression pe, boolean direct)
            throws InconsistentOntologyException, ReasonerInterruptedException,
            TimeOutException {
        return r.getSuperObjectProperties(pe, direct);
    }

    @Override
    public Node<OWLObjectPropertyExpression> getEquivalentObjectProperties(
            OWLObjectPropertyExpression pe)
            throws InconsistentOntologyException, ReasonerInterruptedException,
            TimeOutException {
        return r.getEquivalentObjectProperties(pe);
    }

    @Override
    public NodeSet<OWLObjectPropertyExpression> getDisjointObjectProperties(
            OWLObjectPropertyExpression pe)
            throws InconsistentOntologyException, ReasonerInterruptedException,
            TimeOutException {
        return r.getDisjointObjectProperties(pe);
    }

    @Override
    public Node<OWLObjectPropertyExpression> getInverseObjectProperties(
            OWLObjectPropertyExpression pe)
            throws InconsistentOntologyException, ReasonerInterruptedException,
            TimeOutException {
        return r.getInverseObjectProperties(pe);
    }

    @Override
    public NodeSet<OWLClass> getObjectPropertyDomains(
            OWLObjectPropertyExpression pe, boolean direct)
            throws InconsistentOntologyException, ReasonerInterruptedException,
            TimeOutException {
        return r.getObjectPropertyDomains(pe, direct);
    }

    @Override
    public NodeSet<OWLClass> getObjectPropertyRanges(
            OWLObjectPropertyExpression pe, boolean direct)
            throws InconsistentOntologyException, ReasonerInterruptedException,
            TimeOutException {
        return r.getObjectPropertyRanges(pe, direct);
    }

    @Override
    public Node<OWLDataProperty> getTopDataPropertyNode() {
        return r.getTopDataPropertyNode();
    }

    @Override
    public Node<OWLDataProperty> getBottomDataPropertyNode() {
        return r.getBottomDataPropertyNode();
    }

    @Override
    public NodeSet<OWLDataProperty> getSubDataProperties(OWLDataProperty pe,
            boolean direct) throws InconsistentOntologyException,
            ReasonerInterruptedException, TimeOutException {
        return r.getSubDataProperties(pe, direct);
    }

    @Override
    public NodeSet<OWLDataProperty> getSuperDataProperties(OWLDataProperty pe,
            boolean direct) throws InconsistentOntologyException,
            ReasonerInterruptedException, TimeOutException {
        return r.getSuperDataProperties(pe, direct);
    }

    @Override
    public Node<OWLDataProperty>
            getEquivalentDataProperties(OWLDataProperty pe)
                    throws InconsistentOntologyException,
                    ReasonerInterruptedException, TimeOutException {
        return r.getEquivalentDataProperties(pe);
    }

    @Override
    public NodeSet<OWLClass> getTypes(OWLNamedIndividual ind, boolean direct)
            throws InconsistentOntologyException, ReasonerInterruptedException,
            TimeOutException {
        return r.getTypes(ind, direct);
    }

    @Override
    public NodeSet<OWLNamedIndividual> getInstances(OWLClassExpression ce,
            boolean direct) throws InconsistentOntologyException,
            ClassExpressionNotInProfileException, ReasonerInterruptedException,
            TimeOutException {
        return r.getInstances(ce, direct);
    }

    @Override
    public NodeSet<OWLNamedIndividual> getObjectPropertyValues(
            OWLNamedIndividual ind, OWLObjectPropertyExpression pe)
            throws InconsistentOntologyException, ReasonerInterruptedException,
            TimeOutException {
        return r.getObjectPropertyValues(ind, pe);
    }

    @Override
    public Node<OWLNamedIndividual> getSameIndividuals(OWLNamedIndividual ind)
            throws InconsistentOntologyException, ReasonerInterruptedException,
            TimeOutException {
        return r.getSameIndividuals(ind);
    }

    @Override
    public void dispose() {
        r.dispose();
    }

    @Override
    public void flush() {
        r.flush();
    }

    @Override
    public NodeSet<OWLClass> getDisjointClasses(OWLClassExpression ce) {
        return r.getDisjointClasses(ce);
    }

    @Override
    public NodeSet<OWLDataProperty> getDisjointDataProperties(
            OWLDataPropertyExpression pe) throws InconsistentOntologyException,
            ReasonerInterruptedException, TimeOutException {
        return r.getDisjointDataProperties(pe);
    }

    @Override
    public NodeSet<OWLClass> getDataPropertyDomains(OWLDataProperty pe,
            boolean direct) throws InconsistentOntologyException,
            ReasonerInterruptedException, TimeOutException {
        return r.getDataPropertyDomains(pe, direct);
    }

    @Override
    public Set<OWLLiteral> getDataPropertyValues(OWLNamedIndividual ind,
            OWLDataProperty pe) throws InconsistentOntologyException,
            ReasonerInterruptedException, TimeOutException {
        return r.getDataPropertyValues(ind, pe);
    }

    @Override
    public NodeSet<OWLNamedIndividual> getDifferentIndividuals(
            OWLNamedIndividual ind) throws InconsistentOntologyException,
            ReasonerInterruptedException, TimeOutException {
        return r.getDifferentIndividuals(ind);
    }

    @Override
    public RootNode getRoot(OWLClassExpression expression) {
        return new RootNodeImpl(r.getRoot(expression));
    }

    @Override
    public Node<? extends OWLObjectPropertyExpression> getObjectNeighbours(
            RootNode object, boolean deterministicOnly) {
        return r.getObjectNeighbours((NodePointer) object.getNode(),
                deterministicOnly);
    }

    @Override
    public Node<OWLDataProperty> getDataNeighbours(RootNode object,
            boolean deterministicOnly) {
        return r.getDataNeighbours((NodePointer) object.getNode(),
                deterministicOnly);
    }

    @Override
    public Collection<RootNode> getObjectNeighbours(RootNode object,
            OWLObjectProperty property) {
        Collection<RootNode> toReturn = new ArrayList<>();
        for (NodePointer p : r.getObjectNeighbours(
                (NodePointer) object.getNode(), property)) {
            toReturn.add(new RootNodeImpl(p));
        }
        return toReturn;
    }

    @Override
    public Collection<RootNode> getDataNeighbours(RootNode object,
            OWLDataProperty property) {
        Collection<RootNode> toReturn = new ArrayList<>();
        for (NodePointer p : r.getDataNeighbours(
                (NodePointer) object.getNode(), property)) {
            toReturn.add(new RootNodeImpl(p));
        }
        return toReturn;
    }

    @Override
    public Node<? extends OWLClassExpression> getObjectLabel(RootNode object,
            boolean deterministicOnly) {
        return r.getObjectLabel((NodePointer) object.getNode(),
                deterministicOnly);
    }

    @Override
    public Node<? extends OWLDataRange> getDataLabel(RootNode object,
            boolean deterministicOnly) {
        return r.getDataLabel((NodePointer) object.getNode(), deterministicOnly);
    }

    @Override
    public RootNode getBlocker(RootNode object) {
        return new RootNodeImpl(r.getBlocker((NodePointer) object.getNode()));
    }
}
