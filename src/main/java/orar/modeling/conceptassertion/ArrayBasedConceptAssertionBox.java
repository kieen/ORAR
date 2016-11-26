package orar.modeling.conceptassertion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

import orar.data.NormalizationDataFactory;
import orar.dlfragmentvalidator.ValidatorDataFactory;
import orar.modeling.ontology.AssertionDecoder;

public class ArrayBasedConceptAssertionBox implements ConceptAssertionBox {

	private final List<Set<OWLClass>> conceptAssertionMap;

	private final NormalizationDataFactory normalizationDataFactory;

	public ArrayBasedConceptAssertionBox() {
		this.conceptAssertionMap = new ArrayList<Set<OWLClass>>();
		// this.indexer = IndividualIndexerUsingMapAndList.getInstance();
		// this.owlDataFactory = OWLManager.getOWLDataFactory();
		this.normalizationDataFactory = NormalizationDataFactory.getInstance();
	}

	@Override
	public Set<OWLClass> getAssertedConcepts(Integer individual) {
		if (this.conceptAssertionMap.size() > individual) {
			Set<OWLClass> assertedConcepts = this.conceptAssertionMap.get(individual);
			if (assertedConcepts != null) {
				return assertedConcepts;
			}
		}
		return new HashSet<OWLClass>();

	}

	@Override
	public Set<OWLClassAssertionAxiom> getOWLAPIConceptAssertions() {
		Set<OWLClassAssertionAxiom> classAssertionAxioms = new HashSet<OWLClassAssertionAxiom>();
		for (int i = 0; i < this.conceptAssertionMap.size(); i++) {
			Set<OWLClass> assertedClasses = this.conceptAssertionMap.get(i);
			for (OWLClass owlClass : assertedClasses) {
				OWLClassAssertionAxiom classAssertion = AssertionDecoder.getOWLAPIConceptAssertoin(owlClass, i);
				classAssertionAxioms.add(classAssertion);
			}
		}
		return classAssertionAxioms;
	}

	@Override
	public boolean addManyConceptAssertions(Integer individual, Set<OWLClass> concepts) {
		/*
		 * if the individual is already in the array
		 */
		if (this.conceptAssertionMap.size() > individual) {
			Set<OWLClass> existingClasses = this.conceptAssertionMap.get(individual);
			if (existingClasses == null) {
				existingClasses = new HashSet<OWLClass>();
				this.conceptAssertionMap.set(individual, existingClasses);

			}
			boolean hasNewElement = existingClasses.addAll(concepts);
			return hasNewElement;
		} else {
			addNullForIndividualBetweenSizeAnd(individual);
			HashSet<OWLClass> assertedConcepts = new HashSet<OWLClass>();
			assertedConcepts.addAll(assertedConcepts);
			this.conceptAssertionMap.add(assertedConcepts);
			return true;
		}
	}

	private void addNullForIndividualBetweenSizeAnd(Integer individual) {

		for (int i = this.conceptAssertionMap.size(); i < individual; i++) {
			this.conceptAssertionMap.add(null);
		}

	}

	@Override
	public boolean addConceptAssertion(Integer individual, OWLClass concept) {
		/*
		 * if the individual is already in the array
		 */
		if (this.conceptAssertionMap.size() > individual) {
			Set<OWLClass> existingClasses = this.conceptAssertionMap.get(individual);
			if (existingClasses == null) {
				existingClasses = new HashSet<OWLClass>();
				this.conceptAssertionMap.set(individual, existingClasses);
			}
			boolean hasNewElement = existingClasses.add(concept);
			return hasNewElement;
		} else {
			addNullForIndividualBetweenSizeAnd(individual);
			HashSet<OWLClass> assertedConcepts = new HashSet<OWLClass>();
			assertedConcepts.add(concept);
			this.conceptAssertionMap.add(assertedConcepts);
			return true;
		}
	}

	@Override
	public int getNumberOfConceptAssertions() {
		int numberOfCocneptAssertions = 0;
		for (Set<OWLClass> assertedConcepts : this.conceptAssertionMap) {
			if (assertedConcepts != null) {
				numberOfCocneptAssertions += assertedConcepts.size();
			}
		}

		return numberOfCocneptAssertions;
	}

	@Override
	public int getNumberOfConceptAssertionsWithoutNormalizationSymbols() {
		int numberOfCocneptAssertions = 0;
		// Set<Integer> processedIndividual= new HashSet<>();
		for (Set<OWLClass> assertedConcept : this.conceptAssertionMap) {

			SetView<OWLClass> intersection = Sets.intersection(assertedConcept,
					this.normalizationDataFactory.getConceptsByNormalization());
			numberOfCocneptAssertions = numberOfCocneptAssertions + assertedConcept.size() - intersection.size();
		}
		return numberOfCocneptAssertions;
	}

	@Override
	public boolean addConceptAssertion(OWLClass concept, Integer individual) {
		return addConceptAssertion(individual, concept);
	}

	@Override
	public Set<OWLClassAssertionAxiom> getOWLAPIConceptAssertionsWithoutNormalizationSymbols() {

		Set<OWLClassAssertionAxiom> classAssertionAxioms = new HashSet<OWLClassAssertionAxiom>();

		OWLClass thingConcept = OWLManager.getOWLDataFactory().getOWLThing();

		for (Integer ind = 0; ind < this.conceptAssertionMap.size(); ind++) {
			Set<OWLClass> assertedClasses = this.conceptAssertionMap.get(ind);
			if (assertedClasses != null) {
				for (OWLClass owlClass : assertedClasses) {
					boolean isNotIndividualByNormalization = !ValidatorDataFactory.getInstance()
							.getNamedIndividualGeneratedDuringValidation().contains(ind);
					boolean isNotConceptByNormalization = !NormalizationDataFactory.getInstance()
							.getConceptsByNormalization().contains(owlClass);
					boolean isNotThingConcept = !owlClass.equals(thingConcept);
					if (isNotConceptByNormalization && isNotIndividualByNormalization && isNotThingConcept) {

						OWLClassAssertionAxiom classAssertion = AssertionDecoder.getOWLAPIConceptAssertoin(owlClass,
								ind);
						classAssertionAxioms.add(classAssertion);
					}
				}
			}
		}

		return classAssertionAxioms;
	}

	@Override
	public Set<Integer> getAllIndividuals() {
		Set<Integer> allIndividuals = new HashSet<Integer>();
		for (int i = 0; i < this.conceptAssertionMap.size(); i++) {
			if (this.conceptAssertionMap.get(i) != null) {
				allIndividuals.add(i);
			}
		}
		return allIndividuals;
	}

}
