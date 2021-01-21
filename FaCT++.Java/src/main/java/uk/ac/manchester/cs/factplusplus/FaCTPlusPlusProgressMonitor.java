package uk.ac.manchester.cs.factplusplus;

/**
 * @author Matthew Horridge, The University Of Manchester, Medical Informatics
 *         Group, 10-Jul-2006
 */
public interface FaCTPlusPlusProgressMonitor {

    /**
     * @param classCount
     *        number of classes
     */
    void setClassificationStarted(int classCount);

    /**
     * move to next class
     */
    void nextClass();

    /**
     * finish classification
     */
    void setFinished();

    /**
     * @return true if cancelled
     */
    boolean isCancelled();
}
