package orar.indexing;

import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

public interface IndividualIndexerInterface {

	/**
	 * @param individualStringID
	 * @return an unique integer number, which is an index for this
	 *         individuslStringID; reused the existing number if this
	 *         individualStringID existed in the indexing map.
	 */
	Integer getIndexOfIndividualStringID(String individualStringID);

	/**
	 * @return a set of all indexes for StringID of all individuals.
	 */
	Set<Integer> getAllEncodedIndividuals();

	/*
	 * Methods for testing, converting results to OWLAPI
	 */
	/**
	 * @param individualStrings
	 * @return their indexes
	 */
	Set<Integer> getIndexesOfIndividualString(Set<String> individualStrings);

	/**
	 * @param individualStrings
	 * @return their indexes
	 */
	Set<Integer> getIndexesOfOWLIndividuals(Set<OWLNamedIndividual> individuals);

	/**
	 * @param individualStrings
	 * @return their indexes
	 */
	Integer getIndexOfOWLIndividual(OWLNamedIndividual individual);

	String getIndividualString(Integer indexOfIndividualStringID);

	/**
	 * only for testing
	 * 
	 * @param indexOfIndividualStringID
	 * @return
	 */
	OWLNamedIndividual getOWLIndividual(Integer indexOfIndividualStringID);

	/**
	 * only for testing
	 * 
	 * @param indexOfIndividuals
	 * @return a set of OWLNamedIndividual corresponding to the given indexes
	 */
	Set<OWLNamedIndividual> getOWLIndividuals(Set<Integer> indexOfIndividuals);

	Integer getSize();

	/**
	 * @return a copy of a mapping from Individual(StringID) to an integer. This
	 *         method is used mostly for testing.
	 */
	Map<String, Integer> viewMapIndividuslString2Integer();

	

	/**
	 * clear indexing data
	 */
	void clear();

}