package uk.ac.manchester.cs.factplusplus;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An interface to the native FaCT++ reasoner. Use of this class requires the
 * FaCT++ JNI library for the appropriate platform.
 * 
 * @author Matthew Horridge, The University Of Manchester, Medical Informatics
 *         Group, Date: 10-Jul-2006 */
public class FaCTPlusPlus {

    /** The Constant initDone. */
    private static final AtomicBoolean initDone = new AtomicBoolean(false);
    static {
        if (!initDone.getAndSet(true)) {
            // Load the FaCT++ JNI library
            String jniPath = System.getProperty("factpp.jni.path", "nope");
            if (jniPath.equals("nope")) {
                System.loadLibrary("FaCTPlusPlusJNI");
            } else {
                System.load(jniPath);
            }
            // init all the IDs used
            initMethodsFieldsIDs();
        }
    }

    /**
     * Test.
     *
     * @return only ensure the native libraries are loaded
     */
    public static final boolean test() {
        // this is only useful to force the native library loading
        return initDone.get();
    }

    /**
     * Used to initialise methods and fields that will be used by the native
     * implementation.
     */
    private static native void initMethodsFieldsIDs();

    /** Set internally on the native side - DO NOT ALTER!. */
    private long KernelId;

    /**
     * Instantiates a new fa ct plus plus.
     *
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public FaCTPlusPlus() throws FaCTPlusPlusException {
        try {
            initKernel();
        } catch (Exception e) {
            throw new FaCTPlusPlusException(e);
        }
    }

    /**
     * Use this method to dispose of native resources. This method MUST be
     * called when the reasoner is no longer required. Failure to call dispose,
     * may result in memory leaks!
     * 
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public void dispose() throws FaCTPlusPlusException {
        deleteKernel();
    }

    /**
     * Inits the kernel.
     *
     * @throws Exception
     *         the exception
     */
    private native void initKernel() throws Exception;

    /**
     * Delete kernel.
     *
     * @throws FaCTPlusPlusException
     *         the fa ct plus plus exception
     */
    private native void deleteKernel() throws FaCTPlusPlusException;

    /**
     * Clears told and any cached information from the kernel.
     * 
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native void clearKernel() throws FaCTPlusPlusException;

    /**
     * Set the names of Top/Bottom data and object properties. The call of this
     * method automatically means that the OWL API v3 (and higher) is used.
     * Without this call, Top/Bottom properties would not appear in the query
     * results.
     * 
     * @param topObjectName
     *        top object
     * @param botObjectName
     *        bottom object
     * @param topDataName
     *        top data
     * @param botDataName
     *        bottom data
     */
    public native void setTopBottomPropertyNames(String topObjectName,
            String botObjectName, String topDataName, String botDataName);

    /**
     * Causes the whole taxonomy to be computed.
     * 
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native void classify() throws FaCTPlusPlusException;

    /**
     * Causes all individual types to be computed.
     *
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native void realise() throws FaCTPlusPlusException;

    /**
     * Checks if is realised.
     *
     * @return true iff the KB is realised
     */
    public native boolean isRealised();

    /**
     * Gets the thing.
     *
     * @return Gets the class corresponding to TOP
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ClassPointer getThing() throws FaCTPlusPlusException;

    /**
     * Gets the nothing.
     *
     * @return Gets the class corresponding to BOTTOM
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ClassPointer getNothing() throws FaCTPlusPlusException;

    /**
     * Gets a pointer to a named class.
     * 
     * @param name
     *        The name of the class.
     * @return A <code>ClassPointer</code>
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ClassPointer getNamedClass(String name)
            throws FaCTPlusPlusException;

    /**
     * Gets the top object property.
     *
     * @return object property pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ObjectPropertyPointer getTopObjectProperty()
            throws FaCTPlusPlusException;

    /**
     * Gets the bottom object property.
     *
     * @return object property pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ObjectPropertyPointer getBottomObjectProperty()
            throws FaCTPlusPlusException;

    /**
     * Gets a pointer to an object property.
     * 
     * @param name
     *        The name of the property.
     * @return A pointer to the object property that has the specified name.
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ObjectPropertyPointer getObjectProperty(String name)
            throws FaCTPlusPlusException;

    /**
     * Gets the top data property.
     *
     * @return data property pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native DataPropertyPointer getTopDataProperty()
            throws FaCTPlusPlusException;

    /**
     * Gets the bottom data property.
     *
     * @return data property pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native DataPropertyPointer getBottomDataProperty()
            throws FaCTPlusPlusException;

    /**
     * Gets the data property.
     *
     * @param name
     *        the name
     * @return data property pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native DataPropertyPointer getDataProperty(String name)
            throws FaCTPlusPlusException;

    /**
     * Gets the individual.
     *
     * @param name
     *        the name
     * @return the individual
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native IndividualPointer getIndividual(String name)
            throws FaCTPlusPlusException;

    // ------------------------------------------------------------------------
    // Datatype stuff
    // ------------------------------------------------------------------------
    /**
     * Gets the data top.
     *
     * @return Gets the top data type.
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native DataTypePointer getDataTop() throws FaCTPlusPlusException;

    /**
     * Gets the built in data type.
     *
     * @param name
     *        The name of the datatype. e.g. string, int, float etc.
     * @return Gets a pointer to a built in data type
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native DataTypePointer getBuiltInDataType(String name)
            throws FaCTPlusPlusException;

    /**
     * Gets the data sub type.
     *
     * @param name
     *        The name to assign to the datatype
     * @param datatypeExpression
     *        the datatype expression
     * @return Assigns a name to a datatype expression
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native DataTypeExpressionPointer getDataSubType(String name,
            DataTypeExpressionPointer datatypeExpression)
            throws FaCTPlusPlusException;

    /**
     * Gets the data enumeration.
     *
     * @return Gets a data enumeration using previously added arguments
     *         (initArgList, addArg, closeArgList)
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native DataTypeExpressionPointer getDataEnumeration()
            throws FaCTPlusPlusException;

    /**
     * Gets the restricted data type.
     *
     * @param d
     *        the d
     * @param facet
     *        the facet
     * @return the restricted data type
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native DataTypeExpressionPointer getRestrictedDataType(
            DataTypeExpressionPointer d, DataTypeFacet facet)
            throws FaCTPlusPlusException;

    // ------------------------------------------------------------------------
    // Datatype facets
    // ------------------------------------------------------------------------
    /**
     * Gets the length.
     *
     * @param dv
     *        pointer
     * @return facet
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native DataTypeFacet getLength(DataValuePointer dv)
            throws FaCTPlusPlusException;

    /**
     * Gets the min length.
     *
     * @param dv
     *        pointer
     * @return facet
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native DataTypeFacet getMinLength(DataValuePointer dv)
            throws FaCTPlusPlusException;

    /**
     * Gets the max length.
     *
     * @param dv
     *        pointer
     * @return facet
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native DataTypeFacet getMaxLength(DataValuePointer dv)
            throws FaCTPlusPlusException;

    /**
     * Gets the pattern.
     *
     * @param dv
     *        pointer
     * @return facet
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native DataTypeFacet getPattern(DataValuePointer dv)
            throws FaCTPlusPlusException;

    /**
     * Gets the min exclusive facet.
     *
     * @param dv
     *        pointer
     * @return facet
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native DataTypeFacet getMinExclusiveFacet(DataValuePointer dv)
            throws FaCTPlusPlusException;

    /**
     * Gets the max exclusive facet.
     *
     * @param dv
     *        pointer
     * @return facet
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native DataTypeFacet getMaxExclusiveFacet(DataValuePointer dv)
            throws FaCTPlusPlusException;

    /**
     * Gets the min inclusive facet.
     *
     * @param dv
     *        pointer
     * @return facet
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native DataTypeFacet getMinInclusiveFacet(DataValuePointer dv)
            throws FaCTPlusPlusException;

    /**
     * Gets the max inclusive facet.
     *
     * @param dv
     *        pointer
     * @return facet
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native DataTypeFacet getMaxInclusiveFacet(DataValuePointer dv)
            throws FaCTPlusPlusException;

    /**
     * Gets the total digits facet.
     *
     * @param dv
     *        pointer
     * @return facet
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native DataTypeFacet getTotalDigitsFacet(DataValuePointer dv)
            throws FaCTPlusPlusException;

    /**
     * Gets the fraction digits facet.
     *
     * @param dv
     *        pointer
     * @return facet
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native DataTypeFacet getFractionDigitsFacet(DataValuePointer dv)
            throws FaCTPlusPlusException;

    /**
     * Gets the data not.
     *
     * @param d
     *        expression pointer
     * @return facet
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native DataTypeExpressionPointer getDataNot(
            DataTypeExpressionPointer d) throws FaCTPlusPlusException;

    /**
     * Gets the data intersection of.
     *
     * @return Gets a data intersection using previously added arguments
     *         (initArgList, addArg, closeArgList)
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native DataTypeExpressionPointer getDataIntersectionOf()
            throws FaCTPlusPlusException;

    /**
     * Gets the data union of.
     *
     * @return Gets a data union using previously added arguments (initArgList,
     *         addArg, closeArgList)
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native DataTypeExpressionPointer getDataUnionOf()
            throws FaCTPlusPlusException;

    /**
     * Gets the data value.
     *
     * @param literal
     *        the literal
     * @param type
     *        the type
     * @return the data value
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native DataValuePointer getDataValue(String literal,
            DataTypePointer type) throws FaCTPlusPlusException;

    // ------------------------------------------------------------------------
    // concept expressions
    // ------------------------------------------------------------------------
    /**
     * Gets the concept and.
     *
     * @return Gets an intersection whose operands are in the last closed arg
     *         list.
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ClassPointer getConceptAnd() throws FaCTPlusPlusException;

    /**
     * Gets the concept or.
     *
     * @return Gets a union whose operands are in the last closed arg list.
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ClassPointer getConceptOr() throws FaCTPlusPlusException;

    /**
     * Gets the concept not.
     *
     * @param c
     *        pointer
     * @return translated class pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ClassPointer getConceptNot(ClassPointer c)
            throws FaCTPlusPlusException;

    /**
     * Gets the object some.
     *
     * @param r
     *        pointer
     * @param c
     *        pointer
     * @return translated class pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ClassPointer getObjectSome(ObjectPropertyPointer r,
            ClassPointer c) throws FaCTPlusPlusException;

    /**
     * Gets the object all.
     *
     * @param r
     *        pointer
     * @param c
     *        pointer
     * @return translated class pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ClassPointer getObjectAll(ObjectPropertyPointer r,
            ClassPointer c) throws FaCTPlusPlusException;

    /**
     * Gets the object value.
     *
     * @param r
     *        pointer
     * @param i
     *        individualpointer
     * @return translated class pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ClassPointer getObjectValue(ObjectPropertyPointer r,
            IndividualPointer i) throws FaCTPlusPlusException;

    /**
     * Gets the object at least.
     *
     * @param num
     *        the num
     * @param r
     *        pointer
     * @param c
     *        pointer
     * @return translated class pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ClassPointer getObjectAtLeast(int num,
            ObjectPropertyPointer r, ClassPointer c)
            throws FaCTPlusPlusException;

    /**
     * Gets the object exact.
     *
     * @param num
     *        the num
     * @param r
     *        pointer
     * @param c
     *        pointer
     * @return translated class pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ClassPointer getObjectExact(int num, ObjectPropertyPointer r,
            ClassPointer c) throws FaCTPlusPlusException;

    /**
     * Gets the object at most.
     *
     * @param num
     *        the num
     * @param r
     *        pointer
     * @param c
     *        pointer
     * @return translated class pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ClassPointer getObjectAtMost(int num,
            ObjectPropertyPointer r, ClassPointer c)
            throws FaCTPlusPlusException;

    /**
     * Gets the data some.
     *
     * @param r
     *        pointer
     * @param d
     *        pointer
     * @return translated class pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ClassPointer getDataSome(DataPropertyPointer r,
            DataTypeExpressionPointer d) throws FaCTPlusPlusException;

    /**
     * Gets the data all.
     *
     * @param r
     *        pointer
     * @param d
     *        pointer
     * @return translated class pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ClassPointer getDataAll(DataPropertyPointer r,
            DataTypeExpressionPointer d) throws FaCTPlusPlusException;

    /**
     * Gets the data value.
     *
     * @param r
     *        pointer
     * @param d
     *        pointer
     * @return translated class pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ClassPointer getDataValue(DataPropertyPointer r,
            DataValuePointer d) throws FaCTPlusPlusException;

    /**
     * Gets the data at least.
     *
     * @param num
     *        the num
     * @param r
     *        pointer
     * @param d
     *        pointer
     * @return translated class pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ClassPointer getDataAtLeast(int num, DataPropertyPointer r,
            DataTypeExpressionPointer d) throws FaCTPlusPlusException;

    /**
     * Gets the data exact.
     *
     * @param num
     *        the num
     * @param r
     *        pointer
     * @param d
     *        pointer
     * @return translated class pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ClassPointer getDataExact(int num, DataPropertyPointer r,
            DataTypeExpressionPointer d) throws FaCTPlusPlusException;

    /**
     * Gets the data at most.
     *
     * @param num
     *        the num
     * @param r
     *        pointer
     * @param d
     *        pointer
     * @return translated class pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ClassPointer getDataAtMost(int num, DataPropertyPointer r,
            DataTypeExpressionPointer d) throws FaCTPlusPlusException;

    /**
     * Gets the inverse property.
     *
     * @param r
     *        pointer
     * @return translated object property pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ObjectPropertyPointer getInverseProperty(
            ObjectPropertyPointer r) throws FaCTPlusPlusException;

    /**
     * Gets the property composition.
     *
     * @return Gets a property chain whose properties are in the last closed arg
     *         list.
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ObjectPropertyPointer getPropertyComposition()
            throws FaCTPlusPlusException;

    /**
     * Gets the data property key.
     *
     * @return Gets a data key whose properties are in the last closed arg list.
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native DataPropertyPointer getDataPropertyKey()
            throws FaCTPlusPlusException;

    /**
     * Gets the object property key.
     *
     * @return Gets an object key whose properties are in the last closed arg
     *         list.
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ObjectPropertyPointer getObjectPropertyKey()
            throws FaCTPlusPlusException;

    /**
     * Gets the one of.
     *
     * @return Gets an enumeration whose individuals are in the last closed arg
     *         list.
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ClassPointer getOneOf() throws FaCTPlusPlusException;

    /**
     * Gets the self.
     *
     * @param r
     *        pointer
     * @return translated object property pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ClassPointer getSelf(ObjectPropertyPointer r)
            throws FaCTPlusPlusException;

    // ------------------------------------------------------------------------
    // Axioms
    // ------------------------------------------------------------------------
    /**
     * Tell class declaration.
     *
     * @param c
     *        pointer
     * @return translated axiom pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native AxiomPointer tellClassDeclaration(ClassPointer c)
            throws FaCTPlusPlusException;

    /**
     * Tell object property declaration.
     *
     * @param op
     *        pointer
     * @return translated axiom pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native AxiomPointer tellObjectPropertyDeclaration(
            ObjectPropertyPointer op) throws FaCTPlusPlusException;

    /**
     * Tell data property declaration.
     *
     * @param dp
     *        pointer
     * @return translated axiom pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native AxiomPointer tellDataPropertyDeclaration(
            DataPropertyPointer dp) throws FaCTPlusPlusException;

    /**
     * Tell individual declaration.
     *
     * @param ip
     *        pointer
     * @return translated axiom pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native AxiomPointer tellIndividualDeclaration(IndividualPointer ip)
            throws FaCTPlusPlusException;

    /**
     * Tell datatype declaration.
     *
     * @param dp
     *        pointer
     * @return translated axiom pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native AxiomPointer tellDatatypeDeclaration(DataTypePointer dp)
            throws FaCTPlusPlusException;

    /**
     * Tell sub class of.
     *
     * @param c
     *        pointer
     * @param d
     *        pointer
     * @return translated axiom pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native AxiomPointer tellSubClassOf(ClassPointer c, ClassPointer d)
            throws FaCTPlusPlusException;

    /**
     * Tell equivalent class.
     *
     * @return Tells an equivalent classes axiom, whose classes are in the last
     *         closed arg list.
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native AxiomPointer tellEquivalentClass()
            throws FaCTPlusPlusException;

    /**
     * Tell disjoint classes.
     *
     * @return Tells a disjoint classes axiom, whose classes are in the last
     *         closed arg list.
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native AxiomPointer tellDisjointClasses()
            throws FaCTPlusPlusException;

    /**
     * Tell disjoint union.
     *
     * @param cls
     *        pointer
     * @return Tells a disjoint union axiom, where defined class in CLS and
     *         whose disjoint classes are in the last closed arg list.
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native AxiomPointer tellDisjointUnion(ClassPointer cls)
            throws FaCTPlusPlusException;

    /**
     * Tell has key.
     *
     * @param cls
     *        pointer
     * @param dataKey
     *        the data key
     * @param objectKey
     *        the object key
     * @return translated axiom pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native AxiomPointer tellHasKey(ClassPointer cls,
            DataPropertyPointer dataKey, ObjectPropertyPointer objectKey)
            throws FaCTPlusPlusException;

    /**
     * Tell sub object properties.
     *
     * @param s
     *        pointer
     * @param r
     *        pointer
     * @return translated axiom pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native AxiomPointer tellSubObjectProperties(ObjectPropertyPointer s,
            ObjectPropertyPointer r) throws FaCTPlusPlusException;

    /**
     * Tell equivalent object properties.
     *
     * @return Tells an equivalent object properties axiom, whose properties are
     *         in the last closed arg list.
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native AxiomPointer tellEquivalentObjectProperties()
            throws FaCTPlusPlusException;

    /**
     * Tell inverse properties.
     *
     * @param s
     *        pointer
     * @param r
     *        pointer
     * @return translated axiom pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native AxiomPointer tellInverseProperties(ObjectPropertyPointer s,
            ObjectPropertyPointer r) throws FaCTPlusPlusException;

    /**
     * Tell object property range.
     *
     * @param s
     *        pointer
     * @param c
     *        pointer
     * @return translated axiom pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native AxiomPointer tellObjectPropertyRange(ObjectPropertyPointer s,
            ClassPointer c) throws FaCTPlusPlusException;

    /**
     * Tell data property range.
     *
     * @param s
     *        pointer
     * @param d
     *        pointer
     * @return translated axiom pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native AxiomPointer tellDataPropertyRange(DataPropertyPointer s,
            DataTypeExpressionPointer d) throws FaCTPlusPlusException;

    /**
     * Tell object property domain.
     *
     * @param s
     *        pointer
     * @param c
     *        pointer
     * @return translated axiom pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native AxiomPointer tellObjectPropertyDomain(
            ObjectPropertyPointer s, ClassPointer c)
            throws FaCTPlusPlusException;

    /**
     * Tell data property domain.
     *
     * @param s
     *        pointer
     * @param c
     *        pointer
     * @return translated axiom pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native AxiomPointer tellDataPropertyDomain(DataPropertyPointer s,
            ClassPointer c) throws FaCTPlusPlusException;

    /**
     * Tell disjoint object properties.
     *
     * @return Tells a disjoint object properties axiom, whose properties are in
     *         the last closed arg list.
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native AxiomPointer tellDisjointObjectProperties()
            throws FaCTPlusPlusException;

    /**
     * Tell functional object property.
     *
     * @param s
     *        pointer
     * @return translated axiom pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native AxiomPointer tellFunctionalObjectProperty(
            ObjectPropertyPointer s) throws FaCTPlusPlusException;

    /**
     * Tell inverse functional object property.
     *
     * @param s
     *        pointer
     * @return translated axiom pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native AxiomPointer tellInverseFunctionalObjectProperty(
            ObjectPropertyPointer s) throws FaCTPlusPlusException;

    /**
     * Tell symmetric object property.
     *
     * @param s
     *        pointer
     * @return translated axiom pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native AxiomPointer tellSymmetricObjectProperty(
            ObjectPropertyPointer s) throws FaCTPlusPlusException;

    /**
     * Tell asymmetric object property.
     *
     * @param s
     *        pointer
     * @return translated axiom pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native AxiomPointer tellAsymmetricObjectProperty(
            ObjectPropertyPointer s) throws FaCTPlusPlusException;

    /**
     * Tell reflexive object property.
     *
     * @param s
     *        pointer
     * @return translated axiom pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native AxiomPointer tellReflexiveObjectProperty(
            ObjectPropertyPointer s) throws FaCTPlusPlusException;

    /**
     * Tell irreflexive object property.
     *
     * @param s
     *        pointer
     * @return translated axiom pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native AxiomPointer tellIrreflexiveObjectProperty(
            ObjectPropertyPointer s) throws FaCTPlusPlusException;

    /**
     * Tell transitive object property.
     *
     * @param s
     *        pointer
     * @return translated axiom pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native AxiomPointer tellTransitiveObjectProperty(
            ObjectPropertyPointer s) throws FaCTPlusPlusException;

    /**
     * Tell sub data properties.
     *
     * @param s
     *        pointer
     * @param r
     *        pointer
     * @return translated axiom pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native AxiomPointer tellSubDataProperties(DataPropertyPointer s,
            DataPropertyPointer r) throws FaCTPlusPlusException;

    /**
     * Tell equivalent data properties.
     *
     * @return Tells an equivalent data properties axiom, whose properties are
     *         in the last closed arg list.
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native AxiomPointer tellEquivalentDataProperties()
            throws FaCTPlusPlusException;

    /**
     * Tell disjoint data properties.
     *
     * @return Tells a disjoint data properties axiom, whose properties are in
     *         the last closed arg list.
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native AxiomPointer tellDisjointDataProperties()
            throws FaCTPlusPlusException;

    /**
     * Tell functional data property.
     *
     * @param s
     *        pointer
     * @return translated axiom pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native AxiomPointer
            tellFunctionalDataProperty(DataPropertyPointer s)
                    throws FaCTPlusPlusException;

    /**
     * Tell individual type.
     *
     * @param i
     *        pointer
     * @param c
     *        pointer
     * @return translated axiom pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native AxiomPointer tellIndividualType(IndividualPointer i,
            ClassPointer c) throws FaCTPlusPlusException;

    /**
     * Tell related individuals.
     *
     * @param i
     *        pointer
     * @param r
     *        pointer
     * @param j
     *        pointer @param r pointer
     * @return translated axiom pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native AxiomPointer tellRelatedIndividuals(IndividualPointer i,
            ObjectPropertyPointer r, IndividualPointer j)
            throws FaCTPlusPlusException;

    /**
     * Tell not related individuals.
     *
     * @param i
     *        pointer
     * @param r
     *        pointer
     * @param j
     *        pointer
     * @return translated axiom pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native AxiomPointer tellNotRelatedIndividuals(IndividualPointer i,
            ObjectPropertyPointer r, IndividualPointer j)
            throws FaCTPlusPlusException;

    /**
     * Tell related individual value.
     *
     * @param i
     *        pointer
     * @param r
     *        pointer
     * @param dv
     *        pointer
     * @return translated axiom pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native AxiomPointer tellRelatedIndividualValue(IndividualPointer i,
            DataPropertyPointer r, DataValuePointer dv)
            throws FaCTPlusPlusException;

    /**
     * Tell not related individual value.
     *
     * @param i
     *        pointer
     * @param r
     *        pointer
     * @param dv
     *        pointer
     * @return translated axiom pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native AxiomPointer tellNotRelatedIndividualValue(
            IndividualPointer i, DataPropertyPointer r, DataValuePointer dv)
            throws FaCTPlusPlusException;

    /**
     * Tell same individuals.
     *
     * @return Tells a same individuals axiom, whose individuals are in the last
     *         closed arg list.
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native AxiomPointer tellSameIndividuals()
            throws FaCTPlusPlusException;

    /**
     * Tell different individuals.
     *
     * @return Tells a different individuals axiom, whose individuals are in the
     *         last closed arg list.
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native AxiomPointer tellDifferentIndividuals()
            throws FaCTPlusPlusException;

    // ------------------------------------------------------------------------
    // Retraction
    // ------------------------------------------------------------------------
    /**
     * Retract.
     *
     * @param a
     *        axiom pointer
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native void retract(AxiomPointer a) throws FaCTPlusPlusException;

    // ------------------------------------------------------------------------
    // ASK queries
    // ------------------------------------------------------------------------
    /**
     * Checks if is KB consistent.
     *
     * @return true if consistent
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native boolean isKBConsistent() throws FaCTPlusPlusException;

    /**
     * Checks if is class satisfiable.
     *
     * @param c
     *        pointer
     * @return true if satisfiable
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native boolean isClassSatisfiable(ClassPointer c)
            throws FaCTPlusPlusException;

    /**
     * Checks if is class subsumed by.
     *
     * @param c
     *        pointer
     * @param d
     *        pointer
     * @return true if subsumed
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native boolean isClassSubsumedBy(ClassPointer c, ClassPointer d)
            throws FaCTPlusPlusException;

    /**
     * Checks if is class equivalent to.
     *
     * @param c
     *        pointer
     * @param d
     *        pointer
     * @return true if equivalent
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native boolean isClassEquivalentTo(ClassPointer c, ClassPointer d)
            throws FaCTPlusPlusException;

    /**
     * Checks if is class disjoint with.
     *
     * @param c
     *        pointer
     * @param d
     *        pointer
     * @return true if disjoint
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native boolean isClassDisjointWith(ClassPointer c, ClassPointer d)
            throws FaCTPlusPlusException;

    /**
     * Ask sub classes.
     *
     * @param c
     *        pointer
     * @param direct
     *        true if direct only
     * @return sub classes
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ClassPointer[][]
            askSubClasses(ClassPointer c, boolean direct)
                    throws FaCTPlusPlusException;

    /**
     * Ask super classes.
     *
     * @param c
     *        pointer
     * @param direct
     *        true if direct only
     * @return super classes
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ClassPointer[][] askSuperClasses(ClassPointer c,
            boolean direct) throws FaCTPlusPlusException;

    /**
     * Ask equivalent classes.
     *
     * @param c
     *        pointer
     * @return equivalent classes
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ClassPointer[] askEquivalentClasses(ClassPointer c)
            throws FaCTPlusPlusException;

    /**
     * Ask disjoint classes.
     *
     * @param c
     *        pointer
     * @return disjoint classes
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ClassPointer[][] askDisjointClasses(ClassPointer c)
            throws FaCTPlusPlusException;

    /**
     * Ask super object properties.
     *
     * @param r
     *        pointer
     * @param direct
     *        true if direct only
     * @return super properties
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ObjectPropertyPointer[][] askSuperObjectProperties(
            ObjectPropertyPointer r, boolean direct)
            throws FaCTPlusPlusException;

    /**
     * Ask sub object properties.
     *
     * @param r
     *        pointer
     * @param direct
     *        true if direct only
     * @return sub properties
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ObjectPropertyPointer[][] askSubObjectProperties(
            ObjectPropertyPointer r, boolean direct)
            throws FaCTPlusPlusException;

    /**
     * Ask equivalent object properties.
     *
     * @param r
     *        pointer
     * @return equivalent properties
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ObjectPropertyPointer[] askEquivalentObjectProperties(
            ObjectPropertyPointer r) throws FaCTPlusPlusException;

    /**
     * Ask object property domain.
     *
     * @param r
     *        pointer
     * @param direct
     *        true if direct only
     * @return the class pointer[][]
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ClassPointer[][] askObjectPropertyDomain(
            ObjectPropertyPointer r, boolean direct)
            throws FaCTPlusPlusException;

    /**
     * Ask object property range.
     *
     * @param r
     *        pointer
     * @param direct
     *        true if direct only
     * @return the class pointer[][]
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ClassPointer[][] askObjectPropertyRange(
            ObjectPropertyPointer r, boolean direct)
            throws FaCTPlusPlusException;

    /**
     * Checks if is object property functional.
     *
     * @param r
     *        pointer
     * @return true, if is object property functional
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native boolean isObjectPropertyFunctional(ObjectPropertyPointer r)
            throws FaCTPlusPlusException;

    /**
     * Checks if is object property inverse functional.
     *
     * @param r
     *        pointer
     * @return true, if is object property inverse functional
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native boolean isObjectPropertyInverseFunctional(
            ObjectPropertyPointer r) throws FaCTPlusPlusException;

    /**
     * Checks if is object property symmetric.
     *
     * @param r
     *        pointer
     * @return true, if is object property symmetric
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native boolean isObjectPropertySymmetric(ObjectPropertyPointer r)
            throws FaCTPlusPlusException;

    /**
     * Checks if is object property asymmetric.
     *
     * @param r
     *        pointer
     * @return true, if is object property asymmetric
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native boolean isObjectPropertyAsymmetric(ObjectPropertyPointer r)
            throws FaCTPlusPlusException;

    /**
     * Checks if is object property transitive.
     *
     * @param r
     *        pointer
     * @return true, if is object property transitive
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native boolean isObjectPropertyTransitive(ObjectPropertyPointer r)
            throws FaCTPlusPlusException;

    /**
     * Checks if is object property reflexive.
     *
     * @param r
     *        pointer
     * @return true, if is object property reflexive
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native boolean isObjectPropertyReflexive(ObjectPropertyPointer r)
            throws FaCTPlusPlusException;

    /**
     * Checks if is object property irreflexive.
     *
     * @param r
     *        pointer
     * @return true, if is object property irreflexive
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native boolean isObjectPropertyIrreflexive(ObjectPropertyPointer r)
            throws FaCTPlusPlusException;

    /**
     * Checks if is object sub property of.
     *
     * @param r
     *        pointer
     * @param s
     *        pointer
     * @return true iff R is a sub-property of S
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native boolean isObjectSubPropertyOf(ObjectPropertyPointer r,
            ObjectPropertyPointer s) throws FaCTPlusPlusException;

    /**
     * Checks if is object property disjoint with.
     *
     * @param r
     *        pointer
     * @param s
     *        pointer
     * @return true iff R is disjoint with S
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native boolean isObjectPropertyDisjointWith(ObjectPropertyPointer r,
            ObjectPropertyPointer s) throws FaCTPlusPlusException;

    /**
     * Checks if is sub property chain of.
     *
     * @param r
     *        pointer
     * @return true iff R is a super-property of a chain given in the argument
     *         list
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native boolean isSubPropertyChainOf(ObjectPropertyPointer r)
            throws FaCTPlusPlusException;

    /**
     * Are properties disjoint.
     *
     * @return true iff all the properties in the arg-list are disjoint
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native boolean arePropertiesDisjoint() throws FaCTPlusPlusException;

    /**
     * Ask super data properties.
     *
     * @param r
     *        pointer
     * @param direct
     *        true if direct only
     * @return the data property pointer[][]
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native DataPropertyPointer[][] askSuperDataProperties(
            DataPropertyPointer r, boolean direct) throws FaCTPlusPlusException;

    /**
     * Ask sub data properties.
     *
     * @param r
     *        pointer
     * @param direct
     *        true if direct only
     * @return the data property pointer[][]
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native DataPropertyPointer[][] askSubDataProperties(
            DataPropertyPointer r, boolean direct) throws FaCTPlusPlusException;

    /**
     * Ask equivalent data properties.
     *
     * @param r
     *        pointer
     * @return the data property pointer[]
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native DataPropertyPointer[] askEquivalentDataProperties(
            DataPropertyPointer r) throws FaCTPlusPlusException;

    /**
     * Ask data property domain.
     *
     * @param r
     *        pointer
     * @param direct
     *        true if direct only
     * @return the class pointer[][]
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ClassPointer[][] askDataPropertyDomain(DataPropertyPointer r,
            boolean direct) throws FaCTPlusPlusException;

    /**
     * Checks if is data property functional.
     *
     * @param r
     *        pointer
     * @return true, if is data property functional
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native boolean isDataPropertyFunctional(DataPropertyPointer r)
            throws FaCTPlusPlusException;

    /**
     * Checks if is data sub property of.
     *
     * @param r
     *        pointer
     * @param s
     *        pointer
     * @return true iff R is a sub-property of S
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native boolean isDataSubPropertyOf(DataPropertyPointer r,
            DataPropertyPointer s) throws FaCTPlusPlusException;

    /**
     * Checks if is data property disjoint with.
     *
     * @param r
     *        pointer
     * @param s
     *        pointer
     * @return true iff R is disjoint with S @throws FaCTPlusPlusException fact
     *         exception
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native boolean isDataPropertyDisjointWith(DataPropertyPointer r,
            DataPropertyPointer s) throws FaCTPlusPlusException;

    /**
     * Ask individual types.
     *
     * @param i
     *        pointer
     * @param direct
     *        true if direct only
     * @return the class pointer[][]
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ClassPointer[][] askIndividualTypes(IndividualPointer i,
            boolean direct) throws FaCTPlusPlusException;

    /**
     * Ask object properties.
     *
     * @param i
     *        pointer
     * @return helper for the askObjectPropertyRelationships()
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native ObjectPropertyPointer[] askObjectProperties(
            IndividualPointer i) throws FaCTPlusPlusException;

    /**
     * Ask related individuals.
     *
     * @param individualPointer
     *        the individual pointer
     * @param r
     *        pointer
     * @return the individual pointer[]
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native IndividualPointer[] askRelatedIndividuals(
            IndividualPointer individualPointer, ObjectPropertyPointer r)
            throws FaCTPlusPlusException;

    /**
     * Ask data properties.
     *
     * @param i
     *        pointer
     * @return helper for the askDataPropertyRelationships()
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native DataPropertyPointer[] askDataProperties(IndividualPointer i)
            throws FaCTPlusPlusException;

    /**
     * Ask related values.
     *
     * @param individualPointer
     *        the individual pointer
     * @param r
     *        pointer
     * @return the data value pointer[]
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native DataValuePointer[] askRelatedValues(
            IndividualPointer individualPointer, DataPropertyPointer r)
            throws FaCTPlusPlusException;

    /**
     * Checks for data property relationship.
     *
     * @param i
     *        pointer
     * @param r
     *        pointer
     * @param v
     *        the v
     * @return true, if successful
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native boolean hasDataPropertyRelationship(IndividualPointer i,
            DataPropertyPointer r, DataValuePointer v)
            throws FaCTPlusPlusException;

    /**
     * Checks for object property relationship.
     *
     * @param i
     *        pointer
     * @param r
     *        pointer
     * @param j
     *        pointer
     * @return true, if successful
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native boolean hasObjectPropertyRelationship(IndividualPointer i,
            ObjectPropertyPointer r, IndividualPointer j)
            throws FaCTPlusPlusException;

    /**
     * Checks if is instance of.
     *
     * @param i
     *        pointer
     * @param c
     *        pointer
     * @return true, if is instance of
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native boolean isInstanceOf(IndividualPointer i, ClassPointer c)
            throws FaCTPlusPlusException;

    /**
     * Ask instances.
     *
     * @param c
     *        pointer
     * @param direct
     *        true if direct only
     * @return the individual pointer[]
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native IndividualPointer[] askInstances(ClassPointer c,
            boolean direct) throws FaCTPlusPlusException;

    /**
     * Ask instances grouped.
     *
     * @param c
     *        pointer
     * @param direct
     *        true if direct only
     * @return return instances grouped by the SameAs relation
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native IndividualPointer[][] askInstancesGrouped(ClassPointer c,
            boolean direct) throws FaCTPlusPlusException;

    /**
     * Ask same as.
     *
     * @param i
     *        pointer
     * @return the individual pointer[]
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native IndividualPointer[] askSameAs(IndividualPointer i)
            throws FaCTPlusPlusException;

    /**
     * Checks if is same as.
     *
     * @param i
     *        pointer
     * @param j
     *        pointer
     * @return true, if is same as
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native boolean isSameAs(IndividualPointer i, IndividualPointer j)
            throws FaCTPlusPlusException;

    /**
     * Gets the data related individuals.
     *
     * @param r
     *        pointer
     * @param s
     *        pointer
     * @param op
     *        the op
     * @return the data related individuals
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native IndividualPointer[] getDataRelatedIndividuals(
            DataPropertyPointer r, DataPropertyPointer s, int op)
            throws FaCTPlusPlusException;

    // ------------------------------------------------------------------------
    // Options
    // ------------------------------------------------------------------------
    /**
     * sets single operation timeout in milliseconds.
     *
     * @param millis
     *        the new operation timeout
     */
    public native void setOperationTimeout(long millis);

    /**
     * sets single operation timeout in milliseconds.
     *
     * @param allowFresh
     *        the new fresh entity policy
     */
    public native void setFreshEntityPolicy(boolean allowFresh);

    /**
     * Sets the progress monitor.
     *
     * @param progressMonitor
     *        the new progress monitor
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native void setProgressMonitor(
            FaCTPlusPlusProgressMonitor progressMonitor)
            throws FaCTPlusPlusException;

    /**
     * start changes.
     */
    public native void startChanges();

    /**
     * end changes.
     */
    public native void endChanges();

    // ------------------------------------------------------------------------
    // Argument list processing
    // ------------------------------------------------------------------------
    /**
     * Starts an arg list. Note that only ONE arg list may be created at any
     * given time. For example, it is illegal to call initArgList for a second
     * time without closing calling closeArgList first.
     * 
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native void initArgList() throws FaCTPlusPlusException;

    /**
     * Adds an argument to the currently open arg list.
     * 
     * @param p
     *        A pointer to the argument to be added.
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native void addArg(Pointer p) throws FaCTPlusPlusException;

    /**
     * Closes the currently open arg list. It is illegal to close an empty arg
     * list. It is also illegal to call this method without calling initArgList
     * first.
     * 
     * @throws FaCTPlusPlusException
     *         fact exception
     */
    public native void closeArgList() throws FaCTPlusPlusException;

    // ------------------------------------------------------------------------
    // Tracing (currently unimplemented)
    // ------------------------------------------------------------------------
    /**
     * Switch on the tracing of the next reasoning operation.
     */
    public native void needTracing();

    /**
     * Gets the trace.
     *
     * @return get the trace-set (set of axioms) for the last reasoning
     *         operation
     */
    public native AxiomPointer[] getTrace();

    // ------------------------------------------------------------------------
    // Knowledge Exploration interface
    // ------------------------------------------------------------------------
    /**
     * Builds the completion tree.
     *
     * @param classPointer
     *        the class pointer
     * @return the node pointer
     */
    public native NodePointer buildCompletionTree(ClassPointer classPointer);

    /**
     * Gets the object neighbours.
     *
     * @param object
     *        the object
     * @param deterministicOnly
     *        the deterministic only
     * @return the object neighbours
     */
    public native ObjectPropertyPointer[] getObjectNeighbours(
            NodePointer object, boolean deterministicOnly);

    /**
     * Gets the data neighbours.
     *
     * @param object
     *        the object
     * @param deterministicOnly
     *        the deterministic only
     * @return the data neighbours
     */
    public native DataPropertyPointer[] getDataNeighbours(NodePointer object,
            boolean deterministicOnly);

    /**
     * Gets the object neighbours.
     *
     * @param object
     *        the object
     * @param property
     *        the property
     * @return the object neighbours
     */
    public native NodePointer[] getObjectNeighbours(NodePointer object,
            ObjectPropertyPointer property);

    /**
     * Gets the data neighbours.
     *
     * @param object
     *        the object
     * @param property
     *        the property
     * @return the data neighbours
     */
    public native NodePointer[] getDataNeighbours(NodePointer object,
            DataPropertyPointer property);

    /**
     * Gets the object label.
     *
     * @param object
     *        the object
     * @param deterministicOnly
     *        the deterministic only
     * @return the object label
     */
    public native ClassPointer[] getObjectLabel(NodePointer object,
            boolean deterministicOnly);

    /**
     * Gets the data label.
     *
     * @param object
     *        the object
     * @param deterministicOnly
     *        the deterministic only
     * @return the data label
     */
    public native DataTypePointer[] getDataLabel(NodePointer object,
            boolean deterministicOnly);

    /**
     * Gets the blocker.
     *
     * @param object
     *        the object
     * @return the blocker
     */
    public native NodePointer getBlocker(NodePointer object);

    // ------------------------------------------------------------------------
    // Atomic Decomposition interface
    // ------------------------------------------------------------------------
    /**
     * Gets the atomic decomposition size.
     *
     * @param moduleMethod
     *        the module method
     * @param moduleType
     *        the module type
     * @return the atomic decomposition size
     */
    public native int getAtomicDecompositionSize(int moduleMethod,
            int moduleType);

    /**
     * Gets the atom axioms.
     *
     * @param index
     *        the index
     * @return the atom axioms
     */
    public native AxiomPointer[] getAtomAxioms(int index);

    /**
     * Gets the atom module.
     *
     * @param index
     *        the index
     * @return the atom module
     */
    public native AxiomPointer[] getAtomModule(int index);

    /**
     * Gets the atom dependents.
     *
     * @param index
     *        the index
     * @return the atom dependents
     */
    public native int[] getAtomDependents(int index);

    /**
     * Gets the loc check number.
     *
     * @return the loc check number
     */
    public native int getLocCheckNumber();

    // ------------------------------------------------------------------------
    // Modularity interface
    // ------------------------------------------------------------------------
    /**
     * Gets the module.
     *
     * @param moduleMethod
     *        the module method
     * @param moduleType
     *        the module type
     * @return the module
     */
    public native AxiomPointer[] getModule(int moduleMethod, int moduleType);

    /**
     * Gets the non local.
     *
     * @param moduleMethod
     *        the module method
     * @param moduleType
     *        the module type
     * @return the non local
     */
    public native AxiomPointer[] getNonLocal(int moduleMethod, int moduleType);

    // ------------------------------------------------------------------------
    // Save/Load interface
    // ------------------------------------------------------------------------
    /**
     * Check save load context.
     *
     * @param name
     *        the name
     * @return true, if successful
     */
    public native boolean checkSaveLoadContext(String name);

    /**
     * Sets the save load context.
     *
     * @param name
     *        the name
     * @return true, if successful
     */
    public native boolean setSaveLoadContext(String name);

    /**
     * Clear save load context.
     *
     * @param name
     *        the name
     * @return true, if successful
     */
    public native boolean clearSaveLoadContext(String name);
}
