package orar.modeling.roleassertion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;

import orar.indexing.IndividualIndexerInterface;
import orar.indexing.IndividualIndexerUsingMapAndList;
import orar.modeling.ontology.AssertionDecoder;

/**
 * Store role assertion using maps. For example R(a,b) and R(a,c) will be stored
 * in two map like: 1. a ---> < R --->{b,c} > 2. first entry: b ---->< R---->{a}
 * >, second entry: c ---> <R-->{c}>
 * 
 * @author kien
 *
 */
public class MapListbasedRoleAssertionBox implements RoleAssertionBox {

	/**
	 * Store role assertions grouping by subjects and roles. For example R(a,b)
	 * and R(a,c) will be stored in the map like: a ---> < R --->{b,c} >
	 */
	private final List<Map<OWLObjectProperty, Set<Integer>>> roleAssertionMapWithSubjectAsKey;

	/**
	 * Store role assertions grouping by objects and roles. For example R(a,b)
	 * and R(a,c) will be stored in the map like:<br>
	 * first entry: b ---> <R --->{a}> <br>
	 * second entry: c ---><R --->{a}>
	 */
	private final List<Map<OWLObjectProperty, List<Integer>>> roleAssertionMapWithObjectAsKey;

	/**
	 * Partial store of role assertions grouping by roles. E.g. R(a,b), R(a,c)
	 * will be stored in the map as: R ---> <a>
	 */
	private final Map<OWLObjectProperty, Set<Integer>> roleAssertionMapWithRoleAsKeyAndSubjectAsValue;

	/**
	 * Partial store of role assertions grouping by roles. E.g. R(a,b), R(c,d)
	 * will be stored in the map as: R ---> <b,d>
	 *
	 */
	private final Map<OWLObjectProperty, Set<Integer>> roleAssertionMapWithRoleAsKeyAndObjectAsValue;
	// TODO: maps from role to individuals should be restricted for using in
	// role-hierarchy rule, trans rule, and functionality rule.

	private IndividualIndexerInterface indexer;
	private OWLDataFactory owlDataFactory;

	public MapListbasedRoleAssertionBox() {
		this.roleAssertionMapWithSubjectAsKey = new ArrayList<Map<OWLObjectProperty, Set<Integer>>>();
		this.roleAssertionMapWithObjectAsKey = new ArrayList<Map<OWLObjectProperty, List<Integer>>>();
		this.roleAssertionMapWithRoleAsKeyAndObjectAsValue = new HashMap<OWLObjectProperty, Set<Integer>>();
		this.roleAssertionMapWithRoleAsKeyAndSubjectAsValue = new HashMap<OWLObjectProperty, Set<Integer>>();
		this.indexer = IndividualIndexerUsingMapAndList.getInstance();
		this.owlDataFactory = OWLManager.getOWLDataFactory();
	}

	@Override
	public boolean addRoleAssertion(Integer subject, OWLObjectProperty role, Integer object) {
		boolean hasNewElement = addToTheListStoringRoleAssertions(subject, role, object);
		if (hasNewElement) {
			addToTheListStoringInverseRoleAssertions(object, role, subject);
			addRoleAssertionToMapWithRoleAsKey(role, subject, this.roleAssertionMapWithRoleAsKeyAndSubjectAsValue);
			addRoleAssertionToMapWithRoleAsKey(role, object, this.roleAssertionMapWithRoleAsKeyAndObjectAsValue);
		}
		return hasNewElement;

	}

	private void addToTheListStoringInverseRoleAssertions(Integer object, OWLObjectProperty role, Integer subject) {
		if (this.roleAssertionMapWithObjectAsKey.size() > object) {
			Map<OWLObjectProperty, List<Integer>> mapElementOfTheList = this.roleAssertionMapWithObjectAsKey
					.get(object);
			if (mapElementOfTheList == null) {
				mapElementOfTheList = new HashMap<OWLObjectProperty, List<Integer>>();
				this.roleAssertionMapWithObjectAsKey.set(object, mapElementOfTheList);
			}
			List<Integer> neighbours = mapElementOfTheList.get(role);
			if (neighbours == null) {
				neighbours = new ArrayList<Integer>();
				mapElementOfTheList.put(role, neighbours);
			}
			neighbours.add(subject);
			// return addingSuccess;
		} else {
			addNullForIndividualInInverseRoleAssertionList(this.roleAssertionMapWithObjectAsKey, object);
			HashMap<OWLObjectProperty, List<Integer>> mapElementOfTheList = new HashMap<OWLObjectProperty, List<Integer>>();
			List<Integer> neighbours = new ArrayList<>();
			neighbours.add(subject);
			mapElementOfTheList.put(role, neighbours);
			this.roleAssertionMapWithObjectAsKey.add(mapElementOfTheList);

		}

	}

	private void addNullForIndividualInInverseRoleAssertionList(List<Map<OWLObjectProperty, List<Integer>>> list,
			Integer object) {
		for (int i = list.size(); i < object; i++) {
			list.add(null);
		}
	}

	private void addNullForIndividualBetweenSizeAnd(List<Map<OWLObjectProperty, Set<Integer>>> list,
			Integer individual) {

		for (int i = list.size(); i < individual; i++) {
			list.add(null);
		}

	}

	private void addRoleAssertionToMapWithRoleAsKey(OWLObjectProperty role, Integer individual,
			Map<OWLObjectProperty, Set<Integer>> roleAssertionMapWithRoleAsKey) {

		Set<Integer> existingSet = roleAssertionMapWithRoleAsKey.get(role);
		if (existingSet == null) {
			existingSet = new HashSet<Integer>();
			roleAssertionMapWithRoleAsKey.put(role, existingSet);
		}
		existingSet.add(individual);

	}

	/**
	 * @param subject
	 * @param property
	 * @param object
	 *            add the element (property--->{object})} to the list storing
	 *            role assertion with subject as an index
	 * 
	 */
	boolean addToTheListStoringRoleAssertions(Integer subject, OWLObjectProperty property, Integer object) {
		if (this.roleAssertionMapWithSubjectAsKey.size() > subject) {
			Map<OWLObjectProperty, Set<Integer>> mapElementOfTheList = this.roleAssertionMapWithSubjectAsKey
					.get(subject);
			if (mapElementOfTheList == null) {
				mapElementOfTheList = new HashMap<OWLObjectProperty, Set<Integer>>();
				this.roleAssertionMapWithSubjectAsKey.set(subject, mapElementOfTheList);
			}
			Set<Integer> neighbours = mapElementOfTheList.get(property);
			if (neighbours == null) {
				neighbours = new HashSet<Integer>();
				mapElementOfTheList.put(property, neighbours);
			}
			boolean addingSuccess = neighbours.add(object);
			return addingSuccess;
		} else {
			addNullForIndividualBetweenSizeAnd(this.roleAssertionMapWithSubjectAsKey, subject);
			HashMap<OWLObjectProperty, Set<Integer>> mapElementOfTheList = new HashMap<OWLObjectProperty, Set<Integer>>();
			Set<Integer> neighbours = new HashSet<>();
			neighbours.add(object);
			mapElementOfTheList.put(property, neighbours);
			this.roleAssertionMapWithSubjectAsKey.add(mapElementOfTheList);
			return true;
		}
	}

	boolean addManyRoleAssertionsToMapWithIndividualAsKey(Integer ind1, OWLObjectProperty property,
			Set<Integer> manyInds2, Map<Integer, Map<OWLObjectProperty, Set<Integer>>> map) {

		Map<OWLObjectProperty, Set<Integer>> subMap = map.get(ind1);
		if (subMap == null) {
			subMap = new HashMap<OWLObjectProperty, Set<Integer>>();
			map.put(ind1, subMap);
		}
		Set<Integer> neighbours = subMap.get(property);
		if (neighbours == null) {
			neighbours = new HashSet<Integer>();
			subMap.put(property, neighbours);
		}
		boolean addingSuccess = neighbours.addAll(manyInds2);

		return addingSuccess;
	}
	// /**
	// * @param ind1
	// * @param property
	// * @param ind2
	// * @param map
	// * add the entry {@code ind1 --> (property--->{ind2})} to the
	// * {@code map}
	// */
	// boolean addRoleAssertionToMapWithIndividualAsKeyList(Integer ind1,
	// OWLObjectProperty property, Integer ind2,
	// Map<Integer, Map<OWLObjectProperty, List<Integer>>> map) {
	//
	// Map<OWLObjectProperty, List<Integer>> subMap = map.get(ind1);
	// if (subMap == null) {
	// subMap = new HashMap<OWLObjectProperty, List<Integer>>();
	// }
	// List<Integer> neighbours = subMap.get(property);
	// if (neighbours == null) {
	// neighbours = new ArrayList<Integer>();
	// }
	// boolean addingSuccess = neighbours.add(ind2);
	// subMap.put(property, neighbours);
	// map.put(ind1, subMap);
	// return addingSuccess;
	// }

	@Override
	public int getNumberOfRoleAssertions() {
		int numberOfRoleAssertions = 0;

		for (Map<OWLObjectProperty, Set<Integer>> successorMap : this.roleAssertionMapWithSubjectAsKey) {

			if (successorMap != null) {
				Iterator<Entry<OWLObjectProperty, Set<Integer>>> iteratorForSuccessorMap = successorMap.entrySet()
						.iterator();
				while (iteratorForSuccessorMap.hasNext()) {
					Entry<OWLObjectProperty, Set<Integer>> successorMapEntry = iteratorForSuccessorMap.next();
					// e.g R -->{b1,b2}
					numberOfRoleAssertions += successorMapEntry.getValue().size();
				}
			}
		}
		return numberOfRoleAssertions;
	}

	@Override
	public Set<OWLObjectPropertyAssertionAxiom> getOWLAPIRoleAssertions() {
		// OWLDataFactory owlDataFactory = OWLManager.getOWLDataFactory();
		Set<OWLObjectPropertyAssertionAxiom> assertions = new HashSet<OWLObjectPropertyAssertionAxiom>();
		for (int subject = 0; subject < this.roleAssertionMapWithSubjectAsKey.size(); subject++) {
			Map<OWLObjectProperty, Set<Integer>> successorMap = this.roleAssertionMapWithSubjectAsKey.get(subject);
			if (successorMap != null) {
				Iterator<Entry<OWLObjectProperty, Set<Integer>>> subIterator = successorMap.entrySet().iterator();
				while (subIterator.hasNext()) {
					Entry<OWLObjectProperty, Set<Integer>> subEntry = subIterator.next();
					OWLObjectProperty role = subEntry.getKey();
					for (Integer object : subEntry.getValue()) {
						OWLObjectPropertyAssertionAxiom assertion = AssertionDecoder.getOWLAPIRoleAssertion(role,
								subject, object);
						assertions.add(assertion);
					}
				}
			}
		}
		return assertions;
	}

	// private OWLObjectPropertyAssertionAxiom
	// getOWLAPIRoleAssertion(OWLObjectProperty owlapiRole, Integer
	// subjectInteger,
	// Integer objectInteger) {
	// String owlapiSubjectString =
	// this.indexer.getIndividualString(subjectInteger);
	// OWLNamedIndividual owlapiSubject =
	// this.owlDataFactory.getOWLNamedIndividual(IRI.create(owlapiSubjectString));
	// String owlapiObjectString =
	// this.indexer.getIndividualString(objectInteger);
	// OWLNamedIndividual owlapiObject =
	// this.owlDataFactory.getOWLNamedIndividual(IRI.create(owlapiObjectString));
	// OWLObjectPropertyAssertionAxiom assertion =
	// owlDataFactory.getOWLObjectPropertyAssertionAxiom(owlapiRole,
	// owlapiSubject, owlapiObject);
	// return assertion;
	// }

	@Override
	public Set<Integer> getSubjectsInRoleAssertions(OWLObjectProperty role) {
		Set<Integer> subjects = this.roleAssertionMapWithRoleAsKeyAndSubjectAsValue.get(role);
		if (subjects != null) {
			return new HashSet<Integer>(subjects);
		} else {
			return new HashSet<Integer>();
		}
	}

	@Override
	public Set<Integer> getObjectsInRoleAssertions(OWLObjectProperty role) {
		Set<Integer> subjects = this.roleAssertionMapWithRoleAsKeyAndObjectAsValue.get(role);
		if (subjects != null) {
			return new HashSet<Integer>(subjects);
		} else {
			return new HashSet<Integer>();
		}
	}

	@Override
	public Set<Integer> getAllIndividuals() {
		Set<Integer> allIndividuals = new HashSet<Integer>();
		int size = this.roleAssertionMapWithSubjectAsKey.size();
		for (int subject = 0; subject < size; subject++) {
			if (this.roleAssertionMapWithSubjectAsKey.get(subject) != null) {
				allIndividuals.add(subject);
			}
		}

		for (int object = 0; object < size; object++) {
			if (this.roleAssertionMapWithObjectAsKey.get(object) != null) {
				allIndividuals.add(object);
			}
		}

		return allIndividuals;
	}

	@Override
	public Map<OWLObjectProperty, Set<Integer>> getSuccesorRoleAssertionsAsMap(Integer subjectIndividual) {
		if (this.roleAssertionMapWithSubjectAsKey.size() > subjectIndividual) {
			Map<OWLObjectProperty, Set<Integer>> successorAssertionsAsMap = this.roleAssertionMapWithSubjectAsKey
					.get(subjectIndividual);
			if (successorAssertionsAsMap == null) {
				successorAssertionsAsMap = new HashMap<OWLObjectProperty, Set<Integer>>();
			}
			return successorAssertionsAsMap;
		}
		return new HashMap<OWLObjectProperty, Set<Integer>>();
	}

	@Override
	public Map<OWLObjectProperty, List<Integer>> getPredecessorRoleAssertionsAsMap(Integer objectIndividual) {
		if (this.roleAssertionMapWithObjectAsKey.size() > objectIndividual) {
			Map<OWLObjectProperty, List<Integer>> predecessorAssertionsAsMap = this.roleAssertionMapWithObjectAsKey
					.get(objectIndividual);
			if (predecessorAssertionsAsMap == null) {
				predecessorAssertionsAsMap = new HashMap<OWLObjectProperty, List<Integer>>();
			}
			return predecessorAssertionsAsMap;
		}
		return new HashMap<OWLObjectProperty, List<Integer>>();

	}

	// @Override
	// public Set<OWLObjectPropertyAssertionAxiom>
	// getOWLAPIRoleAssertions(Integer subject) {
	// Map<OWLObjectProperty, Set<Integer>> sucessorMap =
	// this.roleAssertionMapWithSubjectAsKey.get(subject);
	// Set<OWLObjectPropertyAssertionAxiom> roleAssertions;
	// if(sucessorMap ==null)
	// return null;
	// }

}
