package orar.factory;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLClass;

import junit.framework.Assert;
import orar.util.DefaultTestDataFactory;

public class ConceptTypeUsingWeakHashMapTest {

	DefaultTestDataFactory testData= DefaultTestDataFactory.getInsatnce();
	@Test
	public void test() {
		ConceptTypeFactory ctFactory= ConceptTypeFactoryUsingWeakHashMap.getInstance();
		Set<OWLClass> AB = testData.getSetOfConcepts("A","B");
		Set<OWLClass> BA = testData.getSetOfConcepts("B","A");
		
		Set<OWLClass> uniqueAB = ctFactory.getConceptType(AB);
		Set<OWLClass> uniqueBA = ctFactory.getConceptType(BA);
		
		Assert.assertEquals(1, ctFactory.getSize());
		Assert.assertTrue(uniqueAB==uniqueBA);
	}

}
