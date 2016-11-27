package orar.modeling.roleassertion3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;

import orar.indexing.IndividualIndexerInterface;
import orar.indexing.IndividualIndexerUsingMapAndList;
import orar.modeling.roleassertion.RoleAssertionBox;

/**
 * Store role assertion using maps. For example R(a,b) and R(a,c) will be stored
 * in two map like: 1. a ---> < R --->{b,c} > 2. first entry: b ---->< R---->{a}
 * >, second entry: c ---> <R-->{c}>
 * 
 * @author kien
 *
 */
public class ListbasedRoleAssertionBox3 implements RoleAssertionBox {

	/*
	 * arrays for role assertions; E.g. R(a,b) will be stored in as
	 * subjectList[i]=a; roleList[i]=R; objectList[i]=b; at some index i.
	 */
	private final List<Integer> subjectList;
	private final List<OWLObjectProperty> roleList;
	private final List<Integer> objectList;

	/**
	 * indexing by subjects
	 */
	private final Map<Integer, List<Integer>> subjectIndexMap;

	/**
	 * 
	 * indexing by objects
	 */
	private final Map<Integer, List<Integer>> objectIndexMap;

	/**
	 * Partial store of role assertions grouping by roles. E.g. R(a,b), R(a,c)
	 * will be stored in the map as: R ---> <a>
	 */
	private final Map<OWLObjectProperty, List<Integer>> roleIndexMap;

	private IndividualIndexerInterface indexer;
	private OWLDataFactory owlDataFactory;

	public ListbasedRoleAssertionBox3() {
		this.subjectList = new ArrayList<>();
		this.roleList = new ArrayList<>();
		this.objectList = new ArrayList<>();

		this.subjectIndexMap = new HashMap<>();
		this.objectIndexMap = new HashMap<>();
		this.roleIndexMap = new HashMap<>();

		this.indexer = IndividualIndexerUsingMapAndList.getInstance();
		this.owlDataFactory = OWLManager.getOWLDataFactory();
	}

	@Override
	public boolean addRoleAssertion(Integer subject, OWLObjectProperty role, Integer object) {
		boolean isOldAsserton = isOldAssertion(subject, role, object);
		if (!isOldAsserton) {
			addNewAssertionToList(subject, role, object);
			indexAssertion(subject, role, object);
		}
		return !isOldAsserton;

	}

	private void addNewAssertionToList(Integer subject, OWLObjectProperty role, Integer object) {
		this.subjectList.add(subject);
		this.roleList.add(role);
		this.objectList.add(object);
	}

	private void indexAssertion(Integer subject, OWLObjectProperty role, Integer object) {
		int addedIndex = this.subjectList.size() - 1;
		addIndexToMap(this.subjectIndexMap, subject, addedIndex);
		addIndexToMap(this.roleIndexMap, role, addedIndex);
		addIndexToMap(this.objectIndexMap, object, addedIndex);

	}

	private <T> void addIndexToMap(Map<T, List<Integer>> indexMap, T key, Integer addedIndex) {
		List<Integer> existingIndexes = indexMap.get(key);
		if (existingIndexes == null) {
			existingIndexes = new ArrayList<>();
			indexMap.put(key, existingIndexes);
		}
		existingIndexes.add(addedIndex);
	}

	/**
	 * @param subject
	 * @param role
	 * @param object
	 * @return true if this assertion is already stored; false otherwise
	 */
	private boolean isOldAssertion(Integer subject, OWLObjectProperty role, Integer object) {
		List<Integer> indexesOfSubjects = this.subjectIndexMap.get(subject);
		List<Integer> indexesOfObjects = this.objectIndexMap.get(object);

		/*
		 * take smaller one to iterate
		 */
		if (indexesOfObjects == null || indexesOfSubjects == null) {
			return false;
		}

		if (indexesOfSubjects.isEmpty())
			return false;

		if (indexesOfSubjects.size() <= indexesOfObjects.size()) {
			for (int eachIndex : indexesOfSubjects) {
				if (this.objectList.get(eachIndex) == object && roleList.get(eachIndex).equals(role)) {
					return true;
				}
			}
			return false;
		} else {
			for (int eachIndex : indexesOfObjects) {
				if (this.subjectList.get(eachIndex) == subject && roleList.get(eachIndex).equals(role)) {
					return true;
				}

			}
			return false;
		}
	}

	@Override
	public int getNumberOfRoleAssertions() {
		return this.subjectIndexMap.size();
	}

	@Override
	public Set<OWLObjectPropertyAssertionAxiom> getOWLAPIRoleAssertions() {
		// OWLDataFactory owlDataFactory = OWLManager.getOWLDataFactory();
		return null;
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
		return null;
	}

	@Override
	public Set<Integer> getObjectsInRoleAssertions(OWLObjectProperty role) {
		return null;
	}

	@Override
	public Set<Integer> getAllIndividuals() {
		return null;
	}

	@Override
	public Map<OWLObjectProperty, Set<Integer>> getSuccesorRoleAssertionsAsMap(Integer subjectIndividual) {
		return null;
	}

	@Override
	public Map<OWLObjectProperty, List<Integer>> getPredecessorRoleAssertionsAsMap(Integer objectIndividual) {
		return null;

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
