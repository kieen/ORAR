package orar.dlfragmentvalidator;

import org.junit.Test;

import orar.io.ontologyreader.HornSHOIF_OntologyReader;
import orar.io.ontologyreader.OntologyReader;
import orar.modeling.ontology.OrarOntology;
import orar.util.PrintingHelper;

public class TestOntologyProfile {

	@Test
	public void shouldReturnDLLiteExtension() {
		
		String tboxFileName="src/test/resources/dlfragmentvalidator/dlLiteExtension/dllite1.owl";
		String aboxListFileName="src/test/resources/dlfragmentvalidator/dlLiteExtension/aboxListFileName";
		OntologyReader reader= new HornSHOIF_OntologyReader();
		OrarOntology orarOntology= reader.getNormalizedOrarOntology(tboxFileName, aboxListFileName);
		PrintingHelper.printSet(orarOntology.getActualDLConstructors());
	}
	
}
