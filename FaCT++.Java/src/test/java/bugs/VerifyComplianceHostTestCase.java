package bugs;

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLClass;

@SuppressWarnings("javadoc")
public class VerifyComplianceHostTestCase extends VerifyComplianceBase {

    @Override
    protected String input() {
        return "/host.owl";
    }

    @Test
    public void shouldPassgetSubClassesDbXreftrue() {
        OWLClass Nothing = C("http://www.w3.org/2002/07/owl#Nothing");
        OWLClass DbXref = C("http://www.geneontology.org/formats/oboInOwl#DbXref");
        // expected Nothing
        // actual__ DbXref, true
        equal(reasoner.getSubClasses(DbXref, true), Nothing);
    }

    @Test
    public void shouldPassgetSubClassesSynonymtrue() {
        OWLClass Nothing = C("http://www.w3.org/2002/07/owl#Nothing");
        OWLClass Synonym = C("http://www.geneontology.org/formats/oboInOwl#Synonym");
        // expected Nothing
        // actual__ Synonym, true
        equal(reasoner.getSubClasses(Synonym, true), Nothing);
    }

    @Test
    public void shouldPassgetSubClassesDefinitiontrue() {
        OWLClass Nothing = C("http://www.w3.org/2002/07/owl#Nothing");
        OWLClass Definition = C("http://www.geneontology.org/formats/oboInOwl#Definition");
        // expected Nothing
        // actual__ Definition, true
        equal(reasoner.getSubClasses(Definition, true), Nothing);
    }
}
