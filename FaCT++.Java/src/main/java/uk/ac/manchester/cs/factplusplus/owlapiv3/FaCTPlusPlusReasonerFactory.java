package uk.ac.manchester.cs.factplusplus.owlapiv3;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.IllegalConfigurationException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;

/*
 * Copyright (C) 2009, University of Manchester
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
 * Date: 13-Jan-2010
 */
public class FaCTPlusPlusReasonerFactory implements OWLReasonerFactory {

    @Override
    public String getReasonerName() {
        return "FaCT++";
    }

    /**
     * real implementation of the createReasoner method
     * 
     * @param ontology
     *        ontology to reason on
     * @param config
     *        reasoner configuration
     * @param buffering
     *        buffering
     * @return configured reasoner
     * @throws IllegalConfigurationException
     *         if the configuration is illegal
     */
    protected OWLReasoner createReasoner(OWLOntology ontology,
            OWLReasonerConfiguration config, boolean buffering)
            throws IllegalConfigurationException {
        BufferingMode bufferingMode = buffering ? BufferingMode.BUFFERING
                : BufferingMode.NON_BUFFERING;
        final FaCTPlusPlusReasoner reasoner = new FaCTPlusPlusReasoner(
                ontology, config, bufferingMode);
        ontology.getOWLOntologyManager().addOntologyChangeListener(reasoner);
        return reasoner;
    }

    @Override
    public OWLReasoner createReasoner(OWLOntology ontology) {
        return createReasoner(ontology, new SimpleConfiguration(), true);
    }

    @Override
    public OWLReasoner createNonBufferingReasoner(OWLOntology ontology) {
        return createReasoner(ontology, new SimpleConfiguration(), false);
    }

    @Override
    public OWLReasoner createReasoner(OWLOntology ontology,
            OWLReasonerConfiguration config)
            throws IllegalConfigurationException {
        return createReasoner(ontology, config, true);
    }

    @Override
    public OWLReasoner createNonBufferingReasoner(OWLOntology ontology,
            OWLReasonerConfiguration config)
            throws IllegalConfigurationException {
        return createReasoner(ontology, config, false);
    }
}
