package orar.indexing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import orar.util.PrintingHelper;

/**
 * To index individuals (using its StringID) to an integer number. This indexing
 * is 1-to-1
 * 
 * @author kien
 *
 */
public class IndividualIndexerUsingMapAndList implements IndividualIndexerInterface {
	private static IndividualIndexerUsingMapAndList instance;
	private Map<String, Integer> mapIndividualStringID2Number;
	private List<String> listOfIndividualString;
	private Integer index;
	private OWLDataFactory owlDataFactory;
	// private Logger logger =
	// Logger.getLogger(IndividualIndexerUsingMapAndList.class);

	private IndividualIndexerUsingMapAndList() {
		this.mapIndividualStringID2Number = new HashMap<String, Integer>();
		this.listOfIndividualString = new ArrayList<>();
		index = -1;
		this.owlDataFactory = OWLManager.getOWLDataFactory();
	}

	public static IndividualIndexerUsingMapAndList getInstance() {
		if (instance == null) {
			instance = new IndividualIndexerUsingMapAndList();
		}
		return instance;
	}

	/**
	 * @param individualStringID
	 * @return an unique integer number, which is an index for this
	 *         individuslStringID; reused the existing number if this
	 *         individualStringID existed in the indexing map.
	 */
	public Integer getIndexOfIndividualStringID(String individualStringID) {
		Integer existingIndex = this.mapIndividualStringID2Number.get(individualStringID);
		if (existingIndex == null) {
			index++;
			this.mapIndividualStringID2Number.put(individualStringID, index);
			this.listOfIndividualString.add(individualStringID);
			return index;
		}
		return existingIndex;
	}

	/**
	 * @param individualStrings
	 * @return their indexes
	 */
	public Set<Integer> getIndexesOfIndividualString(Set<String> individualStrings) {
		Set<Integer> indexes = new HashSet<Integer>();
		for (String eachString : individualStrings) {
			indexes.add(getIndexOfIndividualStringID(eachString));
		}
		return indexes;

	}

	/**
	 * @param individualStrings
	 * @return their indexes
	 */
	public Set<Integer> getIndexesOfOWLIndividuals(Set<OWLNamedIndividual> individuals) {
		Set<Integer> indexes = new HashSet<Integer>();
		for (OWLNamedIndividual eachIndividual : individuals) {
			indexes.add(getIndexOfIndividualStringID(eachIndividual.getIRI().toString()));
		}
		return indexes;

	}

	/**
	 * @param individualStrings
	 * @return their indexes
	 */
	public Integer getIndexOfOWLIndividual(OWLNamedIndividual individual) {

		return getIndexOfIndividualStringID(individual.getIRI().toString());

	}

	public String getIndividualString(Integer indexOfIndividualStringID) {
		return this.listOfIndividualString.get(indexOfIndividualStringID);
	}

	/**
	 * only for testing
	 * 
	 * @param indexOfIndividualStringID
	 * @return
	 */
	public OWLNamedIndividual getOWLIndividual(Integer indexOfIndividualStringID) {
		// logger.info("Map:");
//		PrintingHelper.printMap(this.mapIndividualStringID2Number);
		// logger.info("List");
		// logger.info(this.listOfIndividualString);
		String indString = this.listOfIndividualString.get(indexOfIndividualStringID);
		return this.owlDataFactory.getOWLNamedIndividual(IRI.create(indString));
	}

	/**
	 * only for testing
	 * 
	 * @param indexOfIndividuals
	 * @return a set of OWLNamedIndividual corresponding to the given indexes
	 */
	public Set<OWLNamedIndividual> getOWLIndividuals(Set<Integer> indexOfIndividuals) {
		Set<OWLNamedIndividual> owlIndividuals = new HashSet<OWLNamedIndividual>();
		for (Integer eachInd : indexOfIndividuals) {
			String eachIndString = this.listOfIndividualString.get(eachInd);
			OWLNamedIndividual owlInd = this.owlDataFactory.getOWLNamedIndividual(IRI.create(eachIndString));
			owlIndividuals.add(owlInd);
		}
		return owlIndividuals;
	}

	public Integer getSize() {
		return this.mapIndividualStringID2Number.size();
	}

	/**
	 * @return a copy of a mapping from Individual(StringID) to an integer. This
	 *         method is used mostly for testing.
	 */
	public Map<String, Integer> viewMapIndividuslString2Integer() {
		Map<String, Integer> currentView = new HashMap<String, Integer>();
		currentView.putAll(this.mapIndividualStringID2Number);
		return currentView;
	}

	// /**
	// * @return a copy of a mapping from an integer to an Individual(StringID).
	// * This method is used mostly for testing.
	// */
	// public Map<Integer, String> viewMapInteger2IndividuslString() {
	// Map<Integer, String> currentView = new HashMap<Integer, String>();
	// currentView.putAll(this.mapNumber2IndividualString);
	// return currentView;
	// }

	public Set<Integer> getAllEncodedIndividuals() {
		Set<Integer> allEncodedIndividuals = new HashSet<>();
		for (int i = 0; i < this.listOfIndividualString.size(); i++) {
			allEncodedIndividuals.add(i);
		}
		return allEncodedIndividuals;
	}

	/**
	 * clear indexing data
	 */
	public void clear() {
		this.mapIndividualStringID2Number.clear();
		this.listOfIndividualString.clear();
		this.index = -1;
	}

}
