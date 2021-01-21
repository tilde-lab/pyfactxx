package bugs;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;

import javax.annotation.Nonnull;

import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

@SuppressWarnings("javadoc")
public class VerifyComplianceOWLSNewFeaturesTestCase extends VerifyComplianceBase {

    @Nonnull
    String in = "Prefix(:=<http://www.w3.org/2002/07/owl#>)\n"
            + "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
            + "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n"
            + "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n"
            + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
            + "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n"
            + "\n"
            + "\n"
            + "Ontology(\n"
            + "Declaration(Class(<urn:process#Any-Order>))\nDeclaration(Class(<urn:process#AtomicProcess>))\nDeclaration(Class(<urn:process#Binding>))\nDeclaration(Class(<urn:process#Choice>))\nDeclaration(Class(<urn:process#CompositeProcess>))\nDeclaration(Class(<urn:process#ConditionalEffect>))\nDeclaration(Class(<urn:process#ConditionalOutput>))\nDeclaration(Class(<urn:process#ControlConstruct>))\nDeclaration(Class(<urn:process#ControlConstructBag>))\nDeclaration(Class(<urn:process#ControlConstructList>))\nDeclaration(Class(<urn:process#If-Then-Else>))\nDeclaration(Class(<urn:process#Input>))\nDeclaration(Class(<urn:process#InputBinding>))\nDeclaration(Class(<urn:process#Iterate>))\nDeclaration(Class(<urn:process#Local>))\nDeclaration(Class(<urn:process#Output>))\nDeclaration(Class(<urn:process#OutputBinding>))\nDeclaration(Class(<urn:process#Parameter>))\nDeclaration(Class(<urn:process#Participant>))\nDeclaration(Class(<urn:process#Perform>))\nDeclaration(Class(<urn:process#Process>))\nDeclaration(Class(<urn:process#ProcessComponent>))\nDeclaration(Class(<urn:process#Produce>))\nDeclaration(Class(<urn:process#Repeat-Until>))\nDeclaration(Class(<urn:process#Repeat-While>))\nDeclaration(Class(<urn:process#Result>))\nDeclaration(Class(<urn:process#ResultVar>))\nDeclaration(Class(<urn:process#Sequence>))\nDeclaration(Class(<urn:process#SimpleProcess>))\nDeclaration(Class(<urn:process#Split>))\nDeclaration(Class(<urn:process#Split-Join>))\nDeclaration(Class(<urn:process#Unordered>))\nDeclaration(Class(<urn:process#ValueOf>))\nDeclaration(Class(<urn:profile#Profile>))\nDeclaration(Class(<urn:profile#ServiceCategory>))\nDeclaration(Class(<urn:profile#ServiceParameter>))\nDeclaration(Class(<urn:Service.owl#Service>))\nDeclaration(Class(<urn:Service.owl#ServiceGrounding>))\nDeclaration(Class(<urn:Service.owl#ServiceModel>))\nDeclaration(Class(<urn:Service.owl#ServiceProfile>))\nDeclaration(Class(<urn:expr#Condition>))\nDeclaration(Class(<urn:expr#DRS-Condition>))\nDeclaration(Class(<urn:expr#DRS-Expression>))\nDeclaration(Class(<urn:expr#Expression>))\nDeclaration(Class(<urn:expr#KIF-Condition>))\nDeclaration(Class(<urn:expr#KIF-Expression>))\nDeclaration(Class(<urn:expr#LogicLanguage>))\nDeclaration(Class(<urn:expr#SWRL-Condition>))\nDeclaration(Class(<urn:expr#SWRL-Expression>))\nDeclaration(Class(<urn:generic/ObjectList.owl#List>))\nDeclaration(Class(<urn:timeentry#IntervalThing>))\nDeclaration(Class(<http://www.w3.org/2003/11/swrl#AtomList>))\nDeclaration(Class(<http://www.w3.org/2003/11/swrl#Variable>))\nDeclaration(ObjectProperty(<urn:process#collapse>))\nDeclaration(ObjectProperty(<urn:process#collapsesTo>))\nDeclaration(ObjectProperty(<urn:process#components>))\nDeclaration(ObjectProperty(<urn:process#composedOf>))\nDeclaration(ObjectProperty(<urn:process#computedEffect>))\nDeclaration(ObjectProperty(<urn:process#computedInput>))\nDeclaration(ObjectProperty(<urn:process#computedOutput>))\nDeclaration(ObjectProperty(<urn:process#computedPrecondition>))\nDeclaration(ObjectProperty(<urn:process#else>))\nDeclaration(ObjectProperty(<urn:process#expand>))\nDeclaration(ObjectProperty(<urn:process#expandsTo>))\nDeclaration(ObjectProperty(<urn:process#fromProcess>))\nDeclaration(ObjectProperty(<urn:process#hasClient>))\nDeclaration(ObjectProperty(<urn:process#hasDataFrom>))\nDeclaration(ObjectProperty(<urn:process#hasEffect>))\nDeclaration(ObjectProperty(<urn:process#hasInput>))\nDeclaration(ObjectProperty(<urn:process#hasLocal>))\nDeclaration(ObjectProperty(<urn:process#hasOutput>))\nDeclaration(ObjectProperty(<urn:process#hasParameter>))\nDeclaration(ObjectProperty(<urn:process#hasParticipant>))\nDeclaration(ObjectProperty(<urn:process#hasPrecondition>))\nDeclaration(ObjectProperty(<urn:process#hasResult>))\nDeclaration(ObjectProperty(<urn:process#hasResultVar>))\nDeclaration(ObjectProperty(<urn:process#ifCondition>))\nDeclaration(ObjectProperty(<urn:process#inCondition>))\nDeclaration(ObjectProperty(<urn:process#performedBy>))\nDeclaration(ObjectProperty(<urn:process#process>))\nDeclaration(ObjectProperty(<urn:process#producedBinding>))\nDeclaration(ObjectProperty(<urn:process#realizedBy>))\nDeclaration(ObjectProperty(<urn:process#realizes>))\nDeclaration(ObjectProperty(<urn:process#theVar>))\nDeclaration(ObjectProperty(<urn:process#then>))\nDeclaration(ObjectProperty(<urn:process#timeout>))\nDeclaration(ObjectProperty(<urn:process#toParam>))\nDeclaration(ObjectProperty(<urn:process#untilCondition>))\nDeclaration(ObjectProperty(<urn:process#untilProcess>))\nDeclaration(ObjectProperty(<urn:process#valueSource>))\nDeclaration(ObjectProperty(<urn:process#whileCondition>))\nDeclaration(ObjectProperty(<urn:process#whileProcess>))\nDeclaration(ObjectProperty(<urn:process#withOutput>))\nDeclaration(ObjectProperty(<urn:profile#contactInformation>))\nDeclaration(ObjectProperty(<urn:profile#hasInput>))\nDeclaration(ObjectProperty(<urn:profile#hasOutput>))\nDeclaration(ObjectProperty(<urn:profile#hasParameter>))\nDeclaration(ObjectProperty(<urn:profile#hasPrecondition>))\nDeclaration(ObjectProperty(<urn:profile#hasResult>))\nDeclaration(ObjectProperty(<urn:profile#has_process>))\nDeclaration(ObjectProperty(<urn:profile#sParameter>))\nDeclaration(ObjectProperty(<urn:profile#serviceCategory>))\nDeclaration(ObjectProperty(<urn:profile#serviceParameter>))\nDeclaration(ObjectProperty(<urn:Service.owl#describedBy>))\nDeclaration(ObjectProperty(<urn:Service.owl#describes>))\nDeclaration(ObjectProperty(<urn:Service.owl#isDescribedBy>))\nDeclaration(ObjectProperty(<urn:Service.owl#isPresentedBy>))\nDeclaration(ObjectProperty(<urn:Service.owl#isSupportedBy>))\nDeclaration(ObjectProperty(<urn:Service.owl#presentedBy>))\nDeclaration(ObjectProperty(<urn:Service.owl#presents>))\nDeclaration(ObjectProperty(<urn:Service.owl#providedBy>))\nDeclaration(ObjectProperty(<urn:Service.owl#provides>))\nDeclaration(ObjectProperty(<urn:Service.owl#supportedBy>))\nDeclaration(ObjectProperty(<urn:Service.owl#supports>))\nDeclaration(ObjectProperty(<urn:expr#expressionLanguage>))\nDeclaration(ObjectProperty(<urn:generic/ObjectList.owl#first>))\nDeclaration(ObjectProperty(<urn:generic/ObjectList.owl#rest>))\nDeclaration(DataProperty(<urn:process#invocable>))\nDeclaration(DataProperty(<urn:process#name>))\nDeclaration(DataProperty(<urn:process#parameterType>))\nDeclaration(DataProperty(<urn:process#parameterValue>))\nDeclaration(DataProperty(<urn:process#valueData>))\nDeclaration(DataProperty(<urn:process#valueForm>))\nDeclaration(DataProperty(<urn:process#valueFunction>))\nDeclaration(DataProperty(<urn:process#valueSpecifier>))\nDeclaration(DataProperty(<urn:process#valueType>))\nDeclaration(DataProperty(<urn:profile#categoryName>))\nDeclaration(DataProperty(<urn:profile#code>))\nDeclaration(DataProperty(<urn:profile#serviceClassification>))\nDeclaration(DataProperty(<urn:profile#serviceName>))\nDeclaration(DataProperty(<urn:profile#serviceParameterName>))\nDeclaration(DataProperty(<urn:profile#serviceProduct>))\nDeclaration(DataProperty(<urn:profile#taxonomy>))\nDeclaration(DataProperty(<urn:profile#textDescription>))\nDeclaration(DataProperty(<urn:profile#value>))\nDeclaration(DataProperty(<urn:expr#expressionBody>))\nDeclaration(DataProperty(<urn:expr#refURI>))\nDeclaration(NamedIndividual(<urn:process#TheClient>))\nDeclaration(NamedIndividual(<urn:process#TheParentPerform>))\nDeclaration(NamedIndividual(<urn:process#TheServer>))\nDeclaration(NamedIndividual(<urn:process#ThisPerform>))\nDeclaration(NamedIndividual(<urn:expr#AlwaysTrue>))\nDeclaration(NamedIndividual(<urn:expr#DRS>))\nDeclaration(NamedIndividual(<urn:expr#KIF>))\nDeclaration(NamedIndividual(<urn:expr#SWRL>))\nDeclaration(NamedIndividual(<urn:generic/ObjectList.owl#nil>))\n"
            + "EquivalentClasses(<urn:process#Any-Order> <urn:process#Unordered>)\n"
            + "SubClassOf(<urn:process#Any-Order> <urn:process#ControlConstruct>)\n"
            + "SubClassOf(<urn:process#Any-Order> ObjectAllValuesFrom(<urn:process#components> <urn:process#ControlConstructBag>))\n"
            + "SubClassOf(<urn:process#Any-Order> ObjectExactCardinality(1 <urn:process#components>))\n"
            + "SubClassOf(<urn:process#AtomicProcess> <urn:process#Process>)\n"
            + "SubClassOf(<urn:process#AtomicProcess> ObjectHasValue(<urn:process#hasClient> <urn:process#TheClient>))\n"
            + "SubClassOf(<urn:process#AtomicProcess> ObjectHasValue(<urn:process#performedBy> <urn:process#TheServer>))\n"
            + "DisjointClasses(<urn:process#AtomicProcess> <urn:process#CompositeProcess>)\n"
            + "DisjointClasses(<urn:process#AtomicProcess> <urn:process#SimpleProcess>)\n"
            + "SubClassOf(<urn:process#Binding> ObjectExactCardinality(1 <urn:process#toParam>))\n"
            + "SubClassOf(<urn:process#Binding> ObjectMaxCardinality(1 <urn:process#valueSource>))\n"
            + "SubClassOf(<urn:process#Binding> DataMaxCardinality(1 <urn:process#valueData>))\n"
            + "SubClassOf(<urn:process#Binding> DataMaxCardinality(1 <urn:process#valueSpecifier>))\n"
            + "SubClassOf(<urn:process#Choice> <urn:process#ControlConstruct>)\n"
            + "SubClassOf(<urn:process#Choice> ObjectAllValuesFrom(<urn:process#components> <urn:process#ControlConstructBag>))\n"
            + "SubClassOf(<urn:process#Choice> ObjectExactCardinality(1 <urn:process#components>))\n"
            + "EquivalentClasses(<urn:process#CompositeProcess> ObjectIntersectionOf(ObjectExactCardinality(1 <urn:process#composedOf>) <urn:process#Process>))\n"
            + "SubClassOf(<urn:process#CompositeProcess> <urn:process#Process>)\n"
            + "SubClassOf(<urn:process#CompositeProcess> ObjectMaxCardinality(1 <urn:process#computedEffect>))\n"
            + "SubClassOf(<urn:process#CompositeProcess> ObjectMaxCardinality(1 <urn:process#computedInput>))\n"
            + "SubClassOf(<urn:process#CompositeProcess> ObjectMaxCardinality(1 <urn:process#computedOutput>))\n"
            + "SubClassOf(<urn:process#CompositeProcess> ObjectMaxCardinality(1 <urn:process#computedPrecondition>))\n"
            + "SubClassOf(<urn:process#CompositeProcess> DataMaxCardinality(1 <urn:process#invocable>))\n"
            + "DisjointClasses(<urn:process#CompositeProcess> <urn:process#SimpleProcess>)\n"
            + "EquivalentClasses(<urn:process#ControlConstruct> <urn:process#ProcessComponent>)\n"
            + "SubClassOf(<urn:process#ControlConstruct> ObjectMaxCardinality(1 <urn:process#timeout>))\n"
            + "SubClassOf(<urn:process#ControlConstructBag> <urn:generic/ObjectList.owl#List>)\n"
            + "SubClassOf(<urn:process#ControlConstructBag> ObjectAllValuesFrom(<urn:generic/ObjectList.owl#first> <urn:process#ControlConstruct>))\n"
            + "SubClassOf(<urn:process#ControlConstructBag> ObjectAllValuesFrom(<urn:generic/ObjectList.owl#rest> <urn:process#ControlConstructBag>))\n"
            + "SubClassOf(<urn:process#ControlConstructList> <urn:generic/ObjectList.owl#List>)\n"
            + "SubClassOf(<urn:process#ControlConstructList> ObjectAllValuesFrom(<urn:generic/ObjectList.owl#first> <urn:process#ControlConstruct>))\n"
            + "SubClassOf(<urn:process#ControlConstructList> ObjectAllValuesFrom(<urn:generic/ObjectList.owl#rest> <urn:process#ControlConstructList>))\n"
            + "SubClassOf(<urn:process#If-Then-Else> <urn:process#ControlConstruct>)\n"
            + "SubClassOf(<urn:process#If-Then-Else> ObjectExactCardinality(1 <urn:process#ifCondition>))\n"
            + "SubClassOf(<urn:process#If-Then-Else> ObjectExactCardinality(1 <urn:process#then>))\n"
            + "SubClassOf(<urn:process#If-Then-Else> ObjectMaxCardinality(1 <urn:process#else>))\n"
            + "SubClassOf(<urn:process#Input> <urn:process#Parameter>)\n"
            + "DisjointClasses(<urn:process#Input> <urn:process#Local>)\n"
            + "DisjointClasses(<urn:process#Input> <urn:process#Output>)\n"
            + "DisjointClasses(<urn:process#Input> <urn:process#ResultVar>)\n"
            + "EquivalentClasses(<urn:process#InputBinding> ObjectIntersectionOf(ObjectAllValuesFrom(<urn:process#toParam> <urn:process#Input>) <urn:process#Binding>))\n"
            + "SubClassOf(<urn:process#InputBinding> <urn:process#Binding>)\n"
            + "SubClassOf(<urn:process#Iterate> <urn:process#ControlConstruct>)\n"
            + "SubClassOf(<urn:process#Local> <urn:process#Parameter>)\n"
            + "DisjointClasses(<urn:process#Local> <urn:process#Output>)\n"
            + "DisjointClasses(<urn:process#Local> <urn:process#ResultVar>)\n"
            + "SubClassOf(<urn:process#Output> <urn:process#Parameter>)\n"
            + "DisjointClasses(<urn:process#Output> <urn:process#ResultVar>)\n"
            + "EquivalentClasses(<urn:process#OutputBinding> ObjectIntersectionOf(ObjectAllValuesFrom(<urn:process#toParam> <urn:process#Output>) <urn:process#Binding>))\n"
            + "SubClassOf(<urn:process#OutputBinding> <urn:process#Binding>)\n"
            + "SubClassOf(<urn:process#Parameter> <http://www.w3.org/2003/11/swrl#Variable>)\n"
            + "SubClassOf(<urn:process#Parameter> DataMinCardinality(1 <urn:process#parameterType>))\n"
            + "SubClassOf(<urn:process#Perform> <urn:process#ControlConstruct>)\n"
            + "SubClassOf(<urn:process#Perform> ObjectExactCardinality(1 <urn:process#process>))\n"
            + "EquivalentClasses(<urn:process#Process> ObjectUnionOf(<urn:process#AtomicProcess> <urn:process#CompositeProcess> <urn:process#SimpleProcess>))\n"
            + "SubClassOf(<urn:process#Process> <urn:Service.owl#ServiceModel>)\n"
            + "SubClassOf(<urn:process#Process> DataMaxCardinality(1 <urn:process#name>))\n"
            + "SubClassOf(<urn:process#Produce> <urn:process#ControlConstruct>)\n"
            + "SubClassOf(<urn:process#Repeat-Until> <urn:process#Iterate>)\n"
            + "SubClassOf(<urn:process#Repeat-Until> ObjectExactCardinality(1 <urn:process#untilCondition>))\n"
            + "SubClassOf(<urn:process#Repeat-Until> ObjectExactCardinality(1 <urn:process#untilProcess>))\n"
            + "SubClassOf(<urn:process#Repeat-While> <urn:process#Iterate>)\n"
            + "SubClassOf(<urn:process#Repeat-While> ObjectExactCardinality(1 <urn:process#whileCondition>))\n"
            + "SubClassOf(<urn:process#Repeat-While> ObjectExactCardinality(1 <urn:process#whileProcess>))\n"
            + "SubClassOf(<urn:process#ResultVar> <urn:process#Parameter>)\n"
            + "SubClassOf(<urn:process#Sequence> <urn:process#ControlConstruct>)\n"
            + "SubClassOf(<urn:process#Sequence> ObjectAllValuesFrom(<urn:process#components> <urn:process#ControlConstructList>))\n"
            + "SubClassOf(<urn:process#Sequence> ObjectExactCardinality(1 <urn:process#components>))\n"
            + "SubClassOf(<urn:process#SimpleProcess> <urn:process#Process>)\n"
            + "SubClassOf(<urn:process#Split> <urn:process#ControlConstruct>)\n"
            + "SubClassOf(<urn:process#Split> ObjectAllValuesFrom(<urn:process#components> <urn:process#ControlConstructBag>))\n"
            + "SubClassOf(<urn:process#Split> ObjectExactCardinality(1 <urn:process#components>))\n"
            + "SubClassOf(<urn:process#Split-Join> <urn:process#ControlConstruct>)\n"
            + "SubClassOf(<urn:process#Split-Join> ObjectAllValuesFrom(<urn:process#components> <urn:process#ControlConstructBag>))\n"
            + "SubClassOf(<urn:process#ValueOf> ObjectExactCardinality(1 <urn:process#theVar>))\n"
            + "SubClassOf(<urn:process#ValueOf> ObjectMaxCardinality(1 <urn:process#fromProcess>))\n"
            + "SubClassOf(<urn:profile#Profile> <urn:Service.owl#ServiceProfile>)\n"
            + "SubClassOf(<urn:profile#Profile> DataExactCardinality(1 <urn:profile#serviceName>))\n"
            + "SubClassOf(<urn:profile#Profile> DataExactCardinality(1 <urn:profile#textDescription>))\n"
            + "SubClassOf(<urn:profile#ServiceCategory> DataExactCardinality(1 <urn:profile#categoryName>))\n"
            + "SubClassOf(<urn:profile#ServiceCategory> DataExactCardinality(1 <urn:profile#code>))\n"
            + "SubClassOf(<urn:profile#ServiceCategory> DataExactCardinality(1 <urn:profile#taxonomy>))\n"
            + "SubClassOf(<urn:profile#ServiceCategory> DataExactCardinality(1 <urn:profile#value>))\n"
            + "SubClassOf(<urn:profile#ServiceParameter> ObjectExactCardinality(1 <urn:profile#sParameter>))\n"
            + "SubClassOf(<urn:profile#ServiceParameter> DataExactCardinality(1 <urn:profile#serviceParameterName>))\n"
            + "SubClassOf(<urn:Service.owl#Service> ObjectMaxCardinality(1 <urn:Service.owl#describedBy>))\n"
            + "SubClassOf(<urn:Service.owl#ServiceGrounding> ObjectExactCardinality(1 <urn:Service.owl#supportedBy>))\n"
            + "SubClassOf(<urn:expr#Condition> <urn:expr#Expression>)\n"
            + "SubClassOf(<urn:expr#DRS-Condition> <urn:expr#Condition>)\n"
            + "SubClassOf(<urn:expr#DRS-Condition> <urn:expr#DRS-Expression>)\n"
            + "SubClassOf(<urn:expr#DRS-Expression> <urn:expr#Expression>)\n"
            + "SubClassOf(<urn:expr#DRS-Expression> ObjectHasValue(<urn:expr#expressionLanguage> <urn:expr#DRS>))\n"
            + "SubClassOf(<urn:expr#DRS-Expression> DataAllValuesFrom(<urn:expr#expressionBody> rdf:XMLLiteral))\n"
            + "SubClassOf(<urn:expr#Expression> ObjectExactCardinality(1 <urn:expr#expressionLanguage>))\n"
            + "SubClassOf(<urn:expr#Expression> DataExactCardinality(1 <urn:expr#expressionBody>))\n"
            + "SubClassOf(<urn:expr#KIF-Condition> <urn:expr#Condition>)\n"
            + "SubClassOf(<urn:expr#KIF-Condition> <urn:expr#KIF-Expression>)\n"
            + "SubClassOf(<urn:expr#KIF-Expression> <urn:expr#Expression>)\n"
            + "SubClassOf(<urn:expr#KIF-Expression> ObjectHasValue(<urn:expr#expressionLanguage> <urn:expr#KIF>))\n"
            + "SubClassOf(<urn:expr#SWRL-Condition> <urn:expr#Condition>)\n"
            + "SubClassOf(<urn:expr#SWRL-Condition> <urn:expr#SWRL-Expression>)\n"
            + "SubClassOf(<urn:expr#SWRL-Expression> <urn:expr#Expression>)\n"
            + "SubClassOf(<urn:expr#SWRL-Expression> ObjectHasValue(<urn:expr#expressionLanguage> <urn:expr#SWRL>))\n"
            + "SubClassOf(<urn:expr#SWRL-Expression> DataAllValuesFrom(<urn:expr#expressionBody> rdf:XMLLiteral))\n"
            + "EquivalentObjectProperties(<urn:process#collapse> <urn:process#collapsesTo>)\n"
            + "InverseObjectProperties(<urn:process#collapsesTo> <urn:process#expandsTo>)\n"
            + "ObjectPropertyDomain(<urn:process#collapsesTo> <urn:process#CompositeProcess>)\n"
            + "ObjectPropertyRange(<urn:process#collapsesTo> <urn:process#SimpleProcess>)\n"
            + "ObjectPropertyDomain(<urn:process#components> ObjectUnionOf(<urn:process#Any-Order> <urn:process#Choice> <urn:process#Sequence> <urn:process#Split> <urn:process#Split-Join>))\n"
            + "ObjectPropertyDomain(<urn:process#composedOf> <urn:process#CompositeProcess>)\n"
            + "ObjectPropertyRange(<urn:process#composedOf> <urn:process#ControlConstruct>)\n"
            + "ObjectPropertyDomain(<urn:process#computedEffect> <urn:process#CompositeProcess>)\n"
            + "ObjectPropertyRange(<urn:process#computedEffect> owl:Thing)\n"
            + "ObjectPropertyDomain(<urn:process#computedInput> <urn:process#CompositeProcess>)\n"
            + "ObjectPropertyRange(<urn:process#computedInput> owl:Thing)\n"
            + "ObjectPropertyDomain(<urn:process#computedOutput> <urn:process#CompositeProcess>)\n"
            + "ObjectPropertyRange(<urn:process#computedOutput> owl:Thing)\n"
            + "ObjectPropertyDomain(<urn:process#computedPrecondition> <urn:process#CompositeProcess>)\n"
            + "ObjectPropertyRange(<urn:process#computedPrecondition> owl:Thing)\n"
            + "ObjectPropertyDomain(<urn:process#else> <urn:process#If-Then-Else>)\n"
            + "ObjectPropertyRange(<urn:process#else> <urn:process#ControlConstruct>)\n"
            + "EquivalentObjectProperties(<urn:process#expand> <urn:process#expandsTo>)\n"
            + "ObjectPropertyDomain(<urn:process#expandsTo> <urn:process#SimpleProcess>)\n"
            + "ObjectPropertyRange(<urn:process#expandsTo> <urn:process#CompositeProcess>)\n"
            + "ObjectPropertyDomain(<urn:process#fromProcess> <urn:process#ValueOf>)\n"
            + "ObjectPropertyRange(<urn:process#fromProcess> <urn:process#Perform>)\n"
            + "SubObjectPropertyOf(<urn:process#hasClient> <urn:process#hasParticipant>)\n"
            + "ObjectPropertyDomain(<urn:process#hasClient> <urn:process#Process>)\n"
            + "ObjectPropertyDomain(<urn:process#hasDataFrom> <urn:process#Perform>)\n"
            + "ObjectPropertyRange(<urn:process#hasDataFrom> <urn:process#Binding>)\n"
            + "ObjectPropertyDomain(<urn:process#hasEffect> <urn:process#Result>)\n"
            + "ObjectPropertyRange(<urn:process#hasEffect> <urn:expr#Expression>)\n"
            + "SubObjectPropertyOf(<urn:process#hasInput> <urn:process#hasParameter>)\n"
            + "ObjectPropertyDomain(<urn:process#hasInput> <urn:process#Process>)\n"
            + "ObjectPropertyRange(<urn:process#hasInput> <urn:process#Input>)\n"
            + "SubObjectPropertyOf(<urn:process#hasLocal> <urn:process#hasParameter>)\n"
            + "ObjectPropertyDomain(<urn:process#hasLocal> <urn:process#Process>)\n"
            + "ObjectPropertyRange(<urn:process#hasLocal> <urn:process#Local>)\n"
            + "SubObjectPropertyOf(<urn:process#hasOutput> <urn:process#hasParameter>)\n"
            + "ObjectPropertyDomain(<urn:process#hasOutput> <urn:process#Process>)\n"
            + "ObjectPropertyRange(<urn:process#hasOutput> <urn:process#Output>)\n"
            + "ObjectPropertyDomain(<urn:process#hasParameter> <urn:process#Process>)\n"
            + "ObjectPropertyRange(<urn:process#hasParameter> <urn:process#Parameter>)\n"
            + "ObjectPropertyDomain(<urn:process#hasParticipant> <urn:process#Process>)\n"
            + "ObjectPropertyRange(<urn:process#hasParticipant> <urn:process#Participant>)\n"
            + "ObjectPropertyDomain(<urn:process#hasPrecondition> <urn:process#Process>)\n"
            + "ObjectPropertyRange(<urn:process#hasPrecondition> <urn:expr#Condition>)\n"
            + "ObjectPropertyDomain(<urn:process#hasResult> <urn:process#Process>)\n"
            + "ObjectPropertyRange(<urn:process#hasResult> <urn:process#Result>)\n"
            + "ObjectPropertyDomain(<urn:process#hasResultVar> <urn:process#Result>)\n"
            + "ObjectPropertyRange(<urn:process#hasResultVar> <urn:process#ResultVar>)\n"
            + "ObjectPropertyDomain(<urn:process#ifCondition> <urn:process#If-Then-Else>)\n"
            + "ObjectPropertyRange(<urn:process#ifCondition> <urn:expr#Condition>)\n"
            + "ObjectPropertyDomain(<urn:process#inCondition> <urn:process#Result>)\n"
            + "ObjectPropertyRange(<urn:process#inCondition> <urn:expr#Condition>)\n"
            + "SubObjectPropertyOf(<urn:process#performedBy> <urn:process#hasParticipant>)\n"
            + "ObjectPropertyDomain(<urn:process#performedBy> <urn:process#Process>)\n"
            + "ObjectPropertyDomain(<urn:process#process> <urn:process#Perform>)\n"
            + "ObjectPropertyRange(<urn:process#process> <urn:process#Process>)\n"
            + "ObjectPropertyDomain(<urn:process#producedBinding> <urn:process#Produce>)\n"
            + "ObjectPropertyRange(<urn:process#producedBinding> <urn:process#OutputBinding>)\n"
            + "InverseObjectProperties(<urn:process#realizedBy> <urn:process#realizes>)\n"
            + "ObjectPropertyDomain(<urn:process#realizedBy> <urn:process#SimpleProcess>)\n"
            + "ObjectPropertyRange(<urn:process#realizedBy> <urn:process#AtomicProcess>)\n"
            + "ObjectPropertyDomain(<urn:process#realizes> <urn:process#AtomicProcess>)\n"
            + "ObjectPropertyRange(<urn:process#realizes> <urn:process#SimpleProcess>)\n"
            + "ObjectPropertyDomain(<urn:process#theVar> <urn:process#ValueOf>)\n"
            + "ObjectPropertyRange(<urn:process#theVar> <urn:process#Parameter>)\n"
            + "ObjectPropertyDomain(<urn:process#then> <urn:process#If-Then-Else>)\n"
            + "ObjectPropertyRange(<urn:process#then> <urn:process#ControlConstruct>)\n"
            + "ObjectPropertyDomain(<urn:process#timeout> <urn:process#ControlConstruct>)\n"
            + "ObjectPropertyRange(<urn:process#timeout> <urn:timeentry#IntervalThing>)\n"
            + "ObjectPropertyDomain(<urn:process#toParam> <urn:process#Binding>)\n"
            + "ObjectPropertyRange(<urn:process#toParam> <urn:process#Parameter>)\n"
            + "ObjectPropertyDomain(<urn:process#untilCondition> <urn:process#Repeat-Until>)\n"
            + "ObjectPropertyRange(<urn:process#untilCondition> <urn:expr#Condition>)\n"
            + "ObjectPropertyDomain(<urn:process#untilProcess> <urn:process#Repeat-Until>)\n"
            + "ObjectPropertyRange(<urn:process#untilProcess> <urn:process#ControlConstruct>)\n"
            + "ObjectPropertyDomain(<urn:process#valueSource> <urn:process#Binding>)\n"
            + "ObjectPropertyRange(<urn:process#valueSource> <urn:process#ValueOf>)\n"
            + "ObjectPropertyDomain(<urn:process#whileCondition> <urn:process#Repeat-While>)\n"
            + "ObjectPropertyRange(<urn:process#whileCondition> <urn:expr#Condition>)\n"
            + "ObjectPropertyDomain(<urn:process#whileProcess> <urn:process#Repeat-While>)\n"
            + "ObjectPropertyRange(<urn:process#whileProcess> <urn:process#ControlConstruct>)\n"
            + "ObjectPropertyDomain(<urn:process#withOutput> <urn:process#Result>)\n"
            + "ObjectPropertyRange(<urn:process#withOutput> <urn:process#OutputBinding>)\n"
            + "ObjectPropertyDomain(<urn:profile#contactInformation> <urn:profile#Profile>)\n"
            + "SubObjectPropertyOf(<urn:profile#hasInput> <urn:profile#hasParameter>)\n"
            + "ObjectPropertyRange(<urn:profile#hasInput> <urn:process#Input>)\n"
            + "SubObjectPropertyOf(<urn:profile#hasOutput> <urn:profile#hasParameter>)\n"
            + "ObjectPropertyRange(<urn:profile#hasOutput> <urn:process#Output>)\n"
            + "ObjectPropertyDomain(<urn:profile#hasParameter> <urn:profile#Profile>)\n"
            + "ObjectPropertyRange(<urn:profile#hasParameter> <urn:process#Parameter>)\n"
            + "ObjectPropertyDomain(<urn:profile#hasPrecondition> <urn:profile#Profile>)\n"
            + "ObjectPropertyRange(<urn:profile#hasPrecondition> <urn:expr#Condition>)\n"
            + "ObjectPropertyDomain(<urn:profile#hasResult> <urn:profile#Profile>)\n"
            + "ObjectPropertyRange(<urn:profile#hasResult> <urn:process#Result>)\n"
            + "FunctionalObjectProperty(<urn:profile#has_process>)\n"
            + "ObjectPropertyDomain(<urn:profile#has_process> <urn:profile#Profile>)\n"
            + "ObjectPropertyRange(<urn:profile#has_process> <urn:process#Process>)\n"
            + "ObjectPropertyDomain(<urn:profile#sParameter> <urn:profile#ServiceParameter>)\n"
            + "ObjectPropertyRange(<urn:profile#sParameter> owl:Thing)\n"
            + "ObjectPropertyDomain(<urn:profile#serviceCategory> <urn:profile#Profile>)\n"
            + "ObjectPropertyRange(<urn:profile#serviceCategory> <urn:profile#ServiceCategory>)\n"
            + "ObjectPropertyDomain(<urn:profile#serviceParameter> <urn:profile#Profile>)\n"
            + "ObjectPropertyRange(<urn:profile#serviceParameter> <urn:profile#ServiceParameter>)\n"
            + "EquivalentObjectProperties(<urn:Service.owl#describedBy> <urn:Service.owl#isDescribedBy>)\n"
            + "InverseObjectProperties(<urn:Service.owl#describedBy> <urn:Service.owl#describes>)\n"
            + "ObjectPropertyDomain(<urn:Service.owl#describedBy> <urn:Service.owl#Service>)\n"
            + "ObjectPropertyRange(<urn:Service.owl#describedBy> <urn:Service.owl#ServiceModel>)\n"
            + "ObjectPropertyDomain(<urn:Service.owl#describes> <urn:Service.owl#ServiceModel>)\n"
            + "ObjectPropertyRange(<urn:Service.owl#describes> <urn:Service.owl#Service>)\n"
            + "EquivalentObjectProperties(<urn:Service.owl#isPresentedBy> <urn:Service.owl#presentedBy>)\n"
            + "EquivalentObjectProperties(<urn:Service.owl#isSupportedBy> <urn:Service.owl#supportedBy>)\n"
            + "InverseObjectProperties(<urn:Service.owl#presents> <urn:Service.owl#presentedBy>)\n"
            + "ObjectPropertyDomain(<urn:Service.owl#presentedBy> <urn:Service.owl#ServiceProfile>)\n"
            + "ObjectPropertyRange(<urn:Service.owl#presentedBy> <urn:Service.owl#Service>)\n"
            + "ObjectPropertyDomain(<urn:Service.owl#presents> <urn:Service.owl#Service>)\n"
            + "ObjectPropertyRange(<urn:Service.owl#presents> <urn:Service.owl#ServiceProfile>)\n"
            + "InverseObjectProperties(<urn:Service.owl#providedBy> <urn:Service.owl#provides>)\n"
            + "ObjectPropertyDomain(<urn:Service.owl#providedBy> <urn:Service.owl#Service>)\n"
            + "ObjectPropertyRange(<urn:Service.owl#provides> <urn:Service.owl#Service>)\n"
            + "InverseObjectProperties(<urn:Service.owl#supports> <urn:Service.owl#supportedBy>)\n"
            + "ObjectPropertyDomain(<urn:Service.owl#supportedBy> <urn:Service.owl#ServiceGrounding>)\n"
            + "ObjectPropertyRange(<urn:Service.owl#supportedBy> <urn:Service.owl#Service>)\n"
            + "ObjectPropertyDomain(<urn:Service.owl#supports> <urn:Service.owl#Service>)\n"
            + "ObjectPropertyRange(<urn:Service.owl#supports> <urn:Service.owl#ServiceGrounding>)\n"
            + "ObjectPropertyDomain(<urn:expr#expressionLanguage> <urn:expr#Expression>)\n"
            + "ObjectPropertyRange(<urn:expr#expressionLanguage> <urn:expr#LogicLanguage>)\n"
            + "ObjectPropertyDomain(<urn:generic/ObjectList.owl#first> <urn:generic/ObjectList.owl#List>)\n"
            + "ObjectPropertyDomain(<urn:generic/ObjectList.owl#rest> <urn:generic/ObjectList.owl#List>)\n"
            + "DataPropertyDomain(<urn:process#invocable> <urn:process#CompositeProcess>)\n"
            + "DataPropertyRange(<urn:process#invocable> xsd:boolean)\n"
            + "DataPropertyDomain(<urn:process#name> <urn:process#Process>)\n"
            + "DataPropertyDomain(<urn:process#parameterType> <urn:process#Parameter>)\n"
            + "DataPropertyRange(<urn:process#parameterType> xsd:anyURI)\n"
            + "DataPropertyDomain(<urn:process#parameterValue> <urn:process#Parameter>)\n"
            + "DataPropertyRange(<urn:process#parameterValue> rdf:XMLLiteral)\n"
            + "DataPropertyDomain(<urn:process#valueData> <urn:process#Binding>)\n"
            + "SubDataPropertyOf(<urn:process#valueForm> <urn:process#valueSpecifier>)\n"
            + "DataPropertyDomain(<urn:process#valueForm> <urn:process#Binding>)\n"
            + "DataPropertyRange(<urn:process#valueForm> rdf:XMLLiteral)\n"
            + "SubDataPropertyOf(<urn:process#valueFunction> <urn:process#valueSpecifier>)\n"
            + "DataPropertyDomain(<urn:process#valueFunction> <urn:process#Binding>)\n"
            + "DataPropertyRange(<urn:process#valueFunction> rdf:XMLLiteral)\n"
            + "DataPropertyDomain(<urn:process#valueSpecifier> <urn:process#Binding>)\n"
            + "SubDataPropertyOf(<urn:process#valueType> <urn:process#valueSpecifier>)\n"
            + "DataPropertyDomain(<urn:process#valueType> <urn:process#Binding>)\n"
            + "DataPropertyRange(<urn:process#valueType> xsd:anyURI)\n"
            + "DataPropertyDomain(<urn:profile#categoryName> <urn:profile#ServiceCategory>)\n"
            + "DataPropertyDomain(<urn:profile#code> <urn:profile#ServiceCategory>)\n"
            + "DataPropertyDomain(<urn:profile#serviceClassification> <urn:profile#Profile>)\n"
            + "DataPropertyRange(<urn:profile#serviceClassification> xsd:anyURI)\n"
            + "DataPropertyDomain(<urn:profile#serviceName> <urn:profile#Profile>)\n"
            + "DataPropertyDomain(<urn:profile#serviceParameterName> <urn:profile#ServiceParameter>)\n"
            + "DataPropertyDomain(<urn:profile#serviceProduct> <urn:profile#Profile>)\n"
            + "DataPropertyRange(<urn:profile#serviceProduct> xsd:anyURI)\n"
            + "DataPropertyDomain(<urn:profile#taxonomy> <urn:profile#ServiceCategory>)\n"
            + "DataPropertyDomain(<urn:profile#textDescription> <urn:profile#Profile>)\n"
            + "DataPropertyDomain(<√.owl#value> <urn:profile#ServiceCategory>)\n"
            + "DataPropertyDomain(<urn:expr#expressionBody> <urn:expr#Expression>)\n"
            + "DataPropertyDomain(<urn:expr#refURI> <urn:expr#LogicLanguage>)\n"
            + "DataPropertyRange(<urn:expr#refURI> xsd:anyURI)\n"
            + "ClassAssertion(<urn:process#Participant> <urn:process#TheClient>)\n"
            + "ClassAssertion(<urn:process#Perform> <urn:process#TheParentPerform>)\n"
            + "ClassAssertion(<urn:process#Participant> <urn:process#TheServer>)\n"
            + "ClassAssertion(<urn:process#Perform> <urn:process#ThisPerform>)\n"
            + "ClassAssertion(<urn:expr#SWRL-Condition> <urn:expr#AlwaysTrue>)\n"
            + "ObjectPropertyAssertion(<urn:expr#expressionLanguage> <urn:expr#AlwaysTrue> <urn:expr#SWRL>)\n"
            + "ClassAssertion(<urn:expr#LogicLanguage> <urn:expr#DRS>)\n"
            + "DataPropertyAssertion(<urn:expr#refURI> <urn:expr#DRS> \"urn:generic/drs.owl\"^^xsd:anyURI)\n"
            + "ClassAssertion(<urn:expr#LogicLanguage> <urn:expr#KIF>)\n"
            + "DataPropertyAssertion(<urn:expr#refURI> <urn:expr#KIF> \"http://logic.stanford.edu/kif/kif.html\"^^xsd:anyURI)\n"
            + "ClassAssertion(<urn:expr#LogicLanguage> <urn:expr#SWRL>)\n"
            + "DataPropertyAssertion(<urn:expr#refURI> <urn:expr#SWRL> \"http://www.w3.org/2003/11/swrl\"^^xsd:anyURI)\n"
            + "ClassAssertion(<urn:generic/ObjectList.owl#List> <urn:generic/ObjectList.owl#nil>)\n"
            + ")";

    @Override
    protected OWLOntology load(String input)
            throws OWLOntologyCreationException {
        OWLOntology onto = OWLManager.createOWLOntologyManager()
                .loadOntologyFromOntologyDocument(new StringDocumentSource(in));
        return onto;
    }

    @Override
    protected String input() {
        return "";
    }

    @Test
    public void shouldPassgetObjectPropertyRangesisPresentedByfalse() {
        OWLClass Thing = C("http://www.w3.org/2002/07/owl#Thing");
        OWLClass Service = C("urn:Service.owl#Service");
        OWLObjectProperty isPresentedBy = OP("urn:Service.owl#isPresentedBy");
        // expected Thing, Service
        // actual__ isPresentedBy, false
        equal(reasoner.getObjectPropertyRanges(isPresentedBy, false), Thing,
                Service);
    }

    @Test
    public void
            shouldPassgetObjectPropertyRangesisPresentedByfalseBasicOntology()
                    throws OWLOntologyCreationException {
        String inputString = "Prefix(:=<http://www.w3.org/2002/07/owl#>)\n"
                + "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
                + "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n"
                + "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n"
                + "Ontology(\n"
                + "Declaration(Class(<urn:Service.owl#Service>))\n"
                + "Declaration(ObjectProperty(<urn:Service.owl#isPresentedBy>))\n"
                + "Declaration(ObjectProperty(<urn:Service.owl#presentedBy>))\n\n"
                + "EquivalentObjectProperties(<urn:Service.owl#isPresentedBy> <urn:Service.owl#presentedBy>)\n"
                + "ObjectPropertyRange(<urn:Service.owl#presentedBy> <urn:Service.owl#Service>)\n"
                + ")";
        OWLOntology onto = OWLManager.createOWLOntologyManager()
                .loadOntologyFromOntologyDocument(
                        new StringDocumentSource(inputString));
        OWLReasoner r = factory().createReasoner(onto);
        OWLClass Thing = C("http://www.w3.org/2002/07/owl#Thing");
        OWLClass Service = C("urn:Service.owl#Service");
        OWLObjectProperty isPresentedBy = OP("urn:Service.owl#isPresentedBy");
        // expected Thing, Service
        // actual__ isPresentedBy, false
        equal(r.getObjectPropertyRanges(isPresentedBy, false), Thing, Service);
    }

    @Test
    @Ignore("disjoint properties not supported")
    public void shouldPassgetDisjointObjectPropertieshasOutput1() {
        OWLObjectProperty hasLocal = OP("urn:process#hasLocal");
        OWLObjectProperty hasInput = OP("urn:process#hasInput");
        OWLObjectProperty hasInput1 = OP("urn:profile#hasInput");
        OWLObjectProperty hasResultVar = OP("urn:process#hasResultVar");
        OWLObjectProperty hasOutput = OP("urn:profile#hasOutput");
        // expected hasLocal, hasInput, hasInput, hasResultVar,
        // bottomObjectProperty
        // actual__ hasOutput
        equal(reasoner.getDisjointObjectProperties(hasOutput), hasLocal,
                hasInput, hasInput1, hasResultVar, bottomObjectProperty);
    }

    @Test
    @Ignore("disjoint properties not supported")
    public void shouldPassgetDisjointObjectPropertieshasInput2() {
        OWLObjectProperty hasOutput = OP("urn:profile#hasOutput");
        OWLObjectProperty hasLocal = OP("urn:process#hasLocal");
        OWLObjectProperty hasOutput1 = OP("urn:process#hasOutput");
        OWLObjectProperty hasResultVar = OP("urn:process#hasResultVar");
        OWLObjectProperty hasInput = OP("urn:process#hasInput");
        // expected hasOutput, hasLocal, hasOutput, hasResultVar,
        // bottomObjectProperty
        // actual__ hasInput
        equal(reasoner.getDisjointObjectProperties(hasInput), hasOutput,
                hasLocal, hasOutput1, hasResultVar, bottomObjectProperty);
    }

    @Test
    @Ignore("disjoint properties not supported")
    public void shouldPassgetDisjointObjectPropertieshasResultVar() {
        OWLObjectProperty hasOutput = OP("urn:profile#hasOutput");
        OWLObjectProperty hasLocal = OP("urn:process#hasLocal");
        OWLObjectProperty hasInput = OP("urn:process#hasInput");
        OWLObjectProperty hasOutput1 = OP("urn:process#hasOutput");
        OWLObjectProperty hasInput1 = OP("urn:profile#hasInput");
        OWLObjectProperty hasResultVar = OP("urn:process#hasResultVar");
        // expected hasOutput, hasLocal, hasInput, hasOutput, hasInput,
        // bottomObjectProperty
        // actual__ hasResultVar
        equal(reasoner.getDisjointObjectProperties(hasResultVar), hasOutput,
                hasLocal, hasInput, hasOutput1, hasInput1, bottomObjectProperty);
    }

    @Test
    @Ignore("disjoint properties not supported")
    public void shouldPassgetDisjointObjectPropertieshasLocal() {
        OWLObjectProperty hasOutput = OP("urn:profile#hasOutput");
        OWLObjectProperty hasInput = OP("urn:process#hasInput");
        OWLObjectProperty hasOutput1 = OP("urn:process#hasOutput");
        OWLObjectProperty hasInput1 = OP("urn:profile#hasInput");
        OWLObjectProperty hasResultVar = OP("urn:process#hasResultVar");
        OWLObjectProperty hasLocal = OP("urn:process#hasLocal");
        // expected hasOutput, hasInput, hasOutput, hasInput, hasResultVar,
        // bottomObjectProperty
        // actual__ hasLocal
        equal(reasoner.getDisjointObjectProperties(hasLocal), hasOutput,
                hasInput, hasOutput1, hasInput1, hasResultVar,
                bottomObjectProperty);
    }

    @Test
    @Ignore("disjoint properties not supported")
    public void shouldPassgetDisjointObjectPropertieshasInput() {
        OWLObjectProperty hasOutput = OP("urn:profile#hasOutput");
        OWLObjectProperty hasLocal = OP("urn:process#hasLocal");
        OWLObjectProperty hasOutput1 = OP("urn:process#hasOutput");
        OWLObjectProperty hasResultVar = OP("urn:process#hasResultVar");
        OWLObjectProperty hasInput = OP("urn:profile#hasInput");
        // expected hasOutput, hasLocal, hasOutput, hasResultVar,
        // bottomObjectProperty
        // actual__ hasInput
        equal(reasoner.getDisjointObjectProperties(hasInput), hasOutput,
                hasLocal, hasOutput1, hasResultVar, bottomObjectProperty);
    }

    @Test
    @Ignore("disjoint properties not supported")
    public void shouldPassgetDisjointObjectPropertieshasOutput() {
        OWLObjectProperty hasLocal = OP("urn:process#hasLocal");
        OWLObjectProperty hasInput = OP("urn:process#hasInput");
        OWLObjectProperty hasInput1 = OP("urn:profile#hasInput");
        OWLObjectProperty hasResultVar = OP("urn:process#hasResultVar");
        OWLObjectProperty hasOutput = OP("urn:process#hasOutput");
        // expected hasLocal, hasInput, hasInput, hasResultVar,
        // bottomObjectProperty
        // actual__ hasOutput
        equal(reasoner.getDisjointObjectProperties(hasOutput), hasLocal,
                hasInput, hasInput1, hasResultVar, bottomObjectProperty);
    }

    @Test
    @Ignore("disjoint properties not supported")
    public void shouldPassgetDisjointDataPropertiesserviceProduct() {
        OWLDataProperty valueForm = DP("urn:process#valueForm");
        OWLDataProperty parameterValue = DP("urn:process#parameterValue");
        OWLDataProperty valueFunction = DP("urn:process#valueFunction");
        OWLDataProperty invocable = DP("urn:process#invocable");
        OWLDataProperty serviceProduct = DP("urn:profile#serviceProduct");
        // expected valueForm, parameterValue, bottomDataProperty,
        // valueFunction, invocable
        // actual__ serviceProduct
        equal(reasoner.getDisjointDataProperties(serviceProduct), valueForm,
                parameterValue, bottomDataProperty, valueFunction, invocable);
    }

    @Test
    @Ignore("disjoint properties not supported")
    public void shouldPassgetDisjointDataPropertiesserviceClassification() {
        OWLDataProperty valueForm = DP("urn:process#valueForm");
        OWLDataProperty parameterValue = DP("urn:process#parameterValue");
        OWLDataProperty valueFunction = DP("urn:process#valueFunction");
        OWLDataProperty invocable = DP("urn:process#invocable");
        OWLDataProperty serviceClassification = DP("urn:profile#serviceClassification");
        // expected valueForm, parameterValue, bottomDataProperty,
        // valueFunction, invocable
        // actual__ serviceClassification
        equal(reasoner.getDisjointDataProperties(serviceClassification),
                valueForm, parameterValue, bottomDataProperty, valueFunction,
                invocable);
    }

    @Test
    @Ignore("disjoint properties not supported")
    public void shouldPassgetDisjointDataPropertiesrefURI() {
        OWLDataProperty valueForm = DP("urn:process#valueForm");
        OWLDataProperty parameterValue = DP("urn:process#parameterValue");
        OWLDataProperty valueFunction = DP("urn:process#valueFunction");
        OWLDataProperty invocable = DP("urn:process#invocable");
        OWLDataProperty refURI = DP("urn:expr#refURI");
        // expected valueForm, parameterValue, bottomDataProperty,
        // valueFunction, invocable
        // actual__ refURI
        equal(reasoner.getDisjointDataProperties(refURI), valueForm,
                parameterValue, bottomDataProperty, valueFunction, invocable);
    }

    @Test
    @Ignore("disjoint properties not supported")
    public void shouldPassgetDisjointDataPropertiesvalueFunction() {
        OWLDataProperty serviceProduct = DP("urn:profile#serviceProduct");
        OWLDataProperty serviceClassification = DP("urn:profile#serviceClassification");
        OWLDataProperty refURI = DP("urn:expr#refURI");
        OWLDataProperty valueType = DP("urn:process#valueType");
        OWLDataProperty parameterType = DP("urn:process#parameterType");
        OWLDataProperty invocable = DP("urn:process#invocable");
        OWLDataProperty valueFunction = DP("urn:process#valueFunction");
        // expected serviceProduct, serviceClassification, refURI,
        // bottomDataProperty, valueType, parameterType, invocable
        // actual__ valueFunction
        equal(reasoner.getDisjointDataProperties(valueFunction),
                serviceProduct, serviceClassification, refURI,
                bottomDataProperty, valueType, parameterType, invocable);
    }

    @Test
    @Ignore("disjoint properties not supported")
    public void shouldPassgetDisjointDataPropertiesvalueType() {
        OWLDataProperty valueForm = DP("urn:process#valueForm");
        OWLDataProperty parameterValue = DP("urn:process#parameterValue");
        OWLDataProperty valueFunction = DP("urn:process#valueFunction");
        OWLDataProperty invocable = DP("urn:process#invocable");
        OWLDataProperty valueType = DP("urn:process#valueType");
        // expected valueForm, parameterValue, bottomDataProperty,
        // valueFunction, invocable
        // actual__ valueType
        equal(reasoner.getDisjointDataProperties(valueType), valueForm,
                parameterValue, bottomDataProperty, valueFunction, invocable);
    }

    @Test
    @Ignore("disjoint properties not supported")
    public void shouldPassgetDisjointDataPropertiesinvocable() {
        OWLDataProperty serviceProduct = DP("urn:profile#serviceProduct");
        OWLDataProperty valueForm = DP("urn:process#valueForm");
        OWLDataProperty serviceClassification = DP("urn:profile#serviceClassification");
        OWLDataProperty parameterValue = DP("urn:process#parameterValue");
        OWLDataProperty refURI = DP("urn:expr#refURI");
        OWLDataProperty valueFunction = DP("urn:process#valueFunction");
        OWLDataProperty valueType = DP("urn:process#valueType");
        OWLDataProperty parameterType = DP("urn:process#parameterType");
        OWLDataProperty invocable = DP("urn:process#invocable");
        // expected serviceProduct, valueForm, serviceClassification,
        // parameterValue, refURI, bottomDataProperty, valueFunction, valueType,
        // parameterType
        // actual__ invocable
        equal(reasoner.getDisjointDataProperties(invocable), serviceProduct,
                valueForm, serviceClassification, parameterValue, refURI,
                bottomDataProperty, valueFunction, valueType, parameterType);
    }

    @Test
    @Ignore("disjoint properties not supported")
    public void shouldPassgetDisjointDataPropertiesvalueForm() {
        OWLDataProperty serviceProduct = DP("urn:profile#serviceProduct");
        OWLDataProperty serviceClassification = DP("urn:profile#serviceClassification");
        OWLDataProperty refURI = DP("urn:expr#refURI");
        OWLDataProperty valueType = DP("urn:process#valueType");
        OWLDataProperty parameterType = DP("urn:process#parameterType");
        OWLDataProperty invocable = DP("urn:process#invocable");
        OWLDataProperty valueForm = DP("urn:process#valueForm");
        // expected serviceProduct, serviceClassification, refURI,
        // bottomDataProperty, valueType, parameterType, invocable
        // actual__ valueForm
        equal(reasoner.getDisjointDataProperties(valueForm), serviceProduct,
                serviceClassification, refURI, bottomDataProperty, valueType,
                parameterType, invocable);
    }

    @Test
    @Ignore("disjoint properties not supported")
    public void shouldPassgetDisjointDataPropertiesparameterValue() {
        OWLDataProperty serviceProduct = DP("urn:profile#serviceProduct");
        OWLDataProperty serviceClassification = DP("urn:profile#serviceClassification");
        OWLDataProperty refURI = DP("urn:expr#refURI");
        OWLDataProperty valueType = DP("urn:process#valueType");
        OWLDataProperty parameterType = DP("urn:process#parameterType");
        OWLDataProperty invocable = DP("urn:process#invocable");
        OWLDataProperty parameterValue = DP("urn:process#parameterValue");
        // expected serviceProduct, serviceClassification, refURI,
        // bottomDataProperty, valueType, parameterType, invocable
        // actual__ parameterValue
        equal(reasoner.getDisjointDataProperties(parameterValue),
                serviceProduct, serviceClassification, refURI,
                bottomDataProperty, valueType, parameterType, invocable);
    }

    @Test
    @Ignore("disjoint properties not supported")
    public void shouldPassgetDisjointDataPropertiesparameterType() {
        OWLDataProperty valueForm = DP("urn:process#valueForm");
        OWLDataProperty parameterValue = DP("urn:process#parameterValue");
        OWLDataProperty valueFunction = DP("urn:process#valueFunction");
        OWLDataProperty invocable = DP("urn:process#invocable");
        OWLDataProperty parameterType = DP("urn:process#parameterType");
        // expected valueForm, parameterValue, bottomDataProperty,
        // valueFunction, invocable
        // actual__ parameterType
        equal(reasoner.getDisjointDataProperties(parameterType), valueForm,
                parameterValue, bottomDataProperty, valueFunction, invocable);
    }

    @Test
    @Ignore("disjoint properties not supported")
    public void shouldPassgetDataPropertyValuesKIFrefURI() {
        OWLNamedIndividual KIF = df.getOWLNamedIndividual(IRI
                .create("urn:expr#KIF"));
        OWLDataProperty refURI = DP("urn:expr#refURI");
        // expected ["http://logic.stanford.edu/kif/kif.html"^^xsd:anyURI]
        // actual__ KIF, refURI
        assertEquals(
                reasoner.getDataPropertyValues(KIF, refURI),
                new HashSet<>(
                        Arrays.asList(df
                                .getOWLLiteral("http://logic.stanford.edu/kif/kif.html"))));
    }

    @Test
    @Ignore("disjoint properties not supported")
    public void shouldPassgetDataPropertyValuesDRSrefURI() {
        OWLNamedIndividual DRS = df.getOWLNamedIndividual(IRI
                .create("urn:expr#DRS"));
        OWLDataProperty refURI = DP("urn:expr#refURI");
        // expected
        // ["http://www.daml.org/services/owl-s/1.1/generic/drs.owl"^^xsd:anyURI]
        // actual__ DRS, refURI
        assertEquals(
                reasoner.getDataPropertyValues(DRS, refURI),
                new HashSet<>(
                        Arrays.asList(df
                                .getOWLLiteral("http://www.daml.org/services/owl-s/1.1/generic/drs.owl"))));
    }

    @Test
    @Ignore("disjoint properties not supported")
    public void shouldPassgetDataPropertyValuesSWRLrefURI() {
        OWLNamedIndividual SWRL = df.getOWLNamedIndividual(IRI
                .create("urn:expr#SWRL"));
        OWLDataProperty refURI = DP("urn:expr#refURI");
        // expected ["http://www.w3.org/2003/11/swrl"^^xsd:anyURI]
        // actual__ SWRL, refURI
        assertEquals(
                reasoner.getDataPropertyValues(SWRL, refURI),
                new HashSet<>(Arrays.asList(df
                        .getOWLLiteral("http://www.w3.org/2003/11/swrl"))));
    }

    @Test
    public void shouldPassgetObjectPropertyRangesisPresentedBytrue() {
        OWLClass Service = C("http://www.daml.org/services/owl-s/1.1/Service.owl#Service");
        OWLObjectProperty isPresentedBy = OP("urn:Service.owl#isPresentedBy");
        // expected Service
        // actual__ isPresentedBy, true
        equal(reasoner.getObjectPropertyRanges(isPresentedBy, true), Service);
    }
}
