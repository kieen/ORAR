package orar.modeling.conceptassertion;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import junit.framework.Assert;
import orar.util.DefaultTestDataFactory;

public class MapbasedConceptAssertionBoxTest {

	private OWLClass A, B, C;
	private int a, b, c;
	private OWLClassAssertionAxiom Aa, Ba, Ca, Ab, Bb, Cc;
	private Set<OWLClassAssertionAxiom> expectedAssertions;
	private ConceptAssertionBox conceptAssertionBox = new ArrayBasedConceptAssertionBox();

	@Before
	public void init() {
		DefaultTestDataFactory testDataFactory = DefaultTestDataFactory.getInsatnce();
		A = testDataFactory.getConcept("A");
		B = testDataFactory.getConcept("B");
		C = testDataFactory.getConcept("C");
		a = 1;
		b = 2;
		c = 3;

	
		expectedAssertions = new HashSet<OWLClassAssertionAxiom>();
		expectedAssertions.add(Aa);
		expectedAssertions.add(Ba);
		expectedAssertions.add(Ca);

		expectedAssertions.add(Ab);
		expectedAssertions.add(Bb);

		expectedAssertions.add(Cc);

		conceptAssertionBox.addConceptAssertion(a, A);
		conceptAssertionBox.addConceptAssertion(a, B);
		conceptAssertionBox.addConceptAssertion(a, C);

		conceptAssertionBox.addConceptAssertion(b, A);
		conceptAssertionBox.addConceptAssertion(b, B);

		conceptAssertionBox.addConceptAssertion(c, C);

		/*
		 * attempt to add duplicated axioms.
		 * 
		 */
		conceptAssertionBox.addConceptAssertion(a, A);
		conceptAssertionBox.addConceptAssertion(a, B);
		conceptAssertionBox.addConceptAssertion(a, C);

		conceptAssertionBox.addConceptAssertion(b, A);
		conceptAssertionBox.addConceptAssertion(b, B);

		conceptAssertionBox.addConceptAssertion(c, C);
	}

	@Test
	public void getAssertedConceptsTest() {

		Set<OWLClass> ABC = new HashSet<OWLClass>();
		ABC.add(A);
		ABC.add(B);
		ABC.add(C);

		Assert.assertEquals(ABC, conceptAssertionBox.getAssertedConcepts(a));

		Set<OWLClass> AB = new HashSet<OWLClass>();
		AB.add(A);
		AB.add(B);
		Assert.assertEquals(AB, conceptAssertionBox.getAssertedConcepts(b));

		HashSet<Object> CSet = new HashSet<Object>();
		CSet.add(C);
		Assert.assertEquals(CSet, conceptAssertionBox.getAssertedConcepts(c));

	}

//	@Test
//	public void getOWLAPIConceptAsertionsTest() {
//		Assert.assertEquals(expectedAssertions, conceptAssertionBox.getOWLAPIConceptAssertions());
//	}

	@Test
	public void getNumberOfConceptAssertionsTest() {
		Assert.assertEquals(6, conceptAssertionBox.getNumberOfConceptAssertions());
	}

	@Test
	public void addConceptAssertion_ShouldNotAddDuplicatedAxiom() {
		Assert.assertFalse(conceptAssertionBox.addConceptAssertion(a, A));
	}

}
