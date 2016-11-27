package orar.modeling.ontology;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;

import orar.dlfragmentvalidator.DLConstructor;
import orar.dlfragmentvalidator.DLFragment;
import orar.modeling.conceptassertion.ConceptAssertionBox;
import orar.modeling.roleassertion.RoleAssertionBox;
import orar.modeling.sameas2.SameAsBox2;

/**
 * Internal representation of an ontology.
 * 
 * @author kien
 * 
 * 
 */
public interface OrarOntology {

	/*
	 * Signature:getters
	 */
	public Set<Integer> getIndividualsInSignature();

	public Set<OWLClass> getConceptNamesInSignature();

	public Set<OWLObjectProperty> getRoleNamesInSignature();

	/*
	 * Signagure:setters/adders
	 */
	public void addIndividualToSignature(int individual);

	public void addIndividualsToSignature(Set<Integer> individuals);

	public void addConceptNameToSignature(OWLClass atomicClass);

	public void addConceptNamesToSignature(Set<OWLClass> conceptNames);

	public void addRoleNameToSignature(OWLObjectProperty atomicRole);

	public void addRoleNamesToSignature(Set<OWLObjectProperty> atomicRoles);

	/*
	 * Number of assertions when the ontology was first created: getters
	 */
	public int getNumberOfInputConceptAssertions();

	/**
	 * @return number of concept assertions currently in the ontology. It could
	 *         contain normalization symbols.
	 */
	public int getNumberOfConceptAssertions();

	/**
	 * @return number of concept assertions currently in the ontology without
	 *         the nomalization symbols and not taking sameas into account
	 */
	public int getNumberOfConceptAssertionsWithoutNormalizationSymbols();

	public int getNumberOfEqualityAssertions();

	public int getNumberOfInputRoleAssertions();

	/**
	 * @return number of role assertions currently in the ontology. We don't
	 *         have any normalization symbols for roles.
	 */
	public int getNumberOfRoleAssertions();

	public Set<OWLAxiom> getOWLAPIMaterializedAssertions();

	/*
	 * Number of assertions when the ontology was first created: setters
	 */
	public void setNumberOfInputRoleAssertions(int numberOfInputRoleAssertions);

	public void setNumberOfInputConceptAssertions(int numberOfInputConceptAssertions);

	/*
	 * Methods for testing correctness
	 */
	/**
	 * Get all OWLAPI concept assertions, <b> INCLUDING </b> those for
	 * individuals/concepts generated during NORMALIZATION and DL-PROFILE
	 * Extraction.<br>
	 * This method is not efficient and only used for checking
	 * correctness/comparing results with other tools via OWLAPI.
	 * 
	 * @return a set of all OWLAPI concept assertions of the ontology. Note that
	 *         this include those for concepts/individuals generated during the
	 *         normalization and profile validation phase.
	 */
	public Set<OWLClassAssertionAxiom> getOWLAPIConceptAssertionsWithNormalizationSymbols();

	/**
	 * Get all OWLAPI concept assertions, <b> EXCLUDING </b> those for
	 * individuals/concepts generated during NORMALIZATION and DL-PROFILE
	 * Extraction.<br>
	 * This method is not efficient and only used for checking
	 * correctness/comparing results with other tools via OWLAPI.
	 * 
	 * @return a set of OWLAPI concept assertions (taking sameas into account)
	 *         after removing those of concepts/individuals generated during
	 *         normalization and profile validation phase.
	 */
	public Set<OWLClassAssertionAxiom> getOWLAPIConceptAssertionsWHITOUTNormalizationSymbols();

	public Map<OWLClass, Set<OWLNamedIndividual>> getOWLAPIConcepAssertionMapWITHOUTNormalizationSymbols();

	/**
	 * Get all OWLAPI role assertions, <b> INCLUDING </b> those for
	 * individuals/concepts generated during NORMALIZATION and DL-PROFILE
	 * Extraction.<br>
	 * This method is not efficient and only used for checking
	 * correctness/comparing results with other tools via OWLAPI.
	 * 
	 * @return a set of all OWLAPI role assertions of the ontology.
	 */
	public Set<OWLObjectPropertyAssertionAxiom> getOWLAPIRoleAssertionsWithNormalizationSymbols();

	/**
	 * Get all OWLAPI role assertions, <b> EXCLUDING </b> those for
	 * individuals/concepts generated during NORMALIZATION and DL-PROFILE
	 * Extraction.<br>
	 * This method is not efficient and only used for checking
	 * correctness/comparing results with other tools via OWLAPI.
	 * 
	 * @return a set of OWLAPI concept assertions after removing those of
	 *         concepts/individuals generated during normalization and profile
	 *         validation phase.
	 */
	public Set<OWLObjectPropertyAssertionAxiom> getOWLAPIRoleAssertionsWITHOUTNormalizationSymbols();

	/*
	 * Methods for DL fragments
	 */
	/**
	 * @return set target DL Fragment of this ontology. A Target DL Fragment is
	 *         the DL for which algorithms guarantee soundness and completeness.
	 */
	public void setTargetDLFragment(DLFragment targetDLFragment);

	/**
	 * @return get target DL Fragment of this ontology. A Target DL Fragment is
	 *         the DL for which algorithms guarantee soundness and completeness.
	 */
	public DLFragment getTargetDLFragment();

	/**
	 * @return get constructors really occurring in this ontology. This will be
	 *         used to design suitable optimization, e.g. optimizations for
	 *         ontology without nominals will be different from the ones with
	 *         nominals.
	 */
	public Set<DLConstructor> getActualDLConstructors();

	public void setActualDLConstructors(Set<DLConstructor> constructors);

	/*
	 * TBox
	 */
	/**
	 * @return a set of OWLAPI TBox axioms, including role-axioms
	 */
	public Set<OWLAxiom> getTBoxAxioms();

	public void addTBoxAxioms(Set<OWLAxiom> tboxAxioms);

	public void addTBoxAxiom(OWLAxiom tboxAxiom);

	/*
	 * Adding concept assertions
	 */
	public boolean addConceptAssertion(int individual, OWLClass concept);

	public boolean addManyConceptAssertions(int originalInd, Set<OWLClass> concepts);

	/**
	 * Adding role assertions.
	 * 
	 * @param subject
	 * @param role
	 * @param object
	 * @return true if new assertion has been added, false otherwise.
	 */
	public boolean addRoleAssertion(int subject, OWLObjectProperty role, int object);

	// public boolean addManyRoleAssertions(int subject, OWLObjectProperty role,
	// Set<Integer> objects);

	/*
	 * Methods for sameas assertions
	 */
	public boolean addSameAsAssertion(int individual, int equalIndividual);

	public boolean addManySameAsAssertions(int individual, Set<Integer> equalIndividuals);

	public boolean addNewManySameAsAssertions(Set<Integer> equalIndividuals);

	/**
	 * @param individual
	 * @return a (possible empty) set of individuals that are equal to
	 *         {@code individual}
	 */
	public Set<Integer> getSameIndividuals(int individual);

	/**
	 * @param individual
	 * @return a (possibly empty) set of asserted concepts for the given
	 *         individual
	 */
	public Set<OWLClass> getAssertedConcepts(int individual);

	/**
	 * @param individual
	 * @return get successor assertions (stored in a map: role --> set of
	 *         objects) of the given individual
	 */
	public Map<OWLObjectProperty, Set<Integer>> getSuccessorRoleAssertionsAsMap(int subjectIndividual);

	/**
	 * @param individual
	 * @return get predecessor assertions (stored in a map: role --> set of
	 *         objects) of the given individual
	 */
	public Map<OWLObjectProperty, List<Integer>> getPredecessorRoleAssertionsAsMap(int objectIndividual);

	public Set<Integer> getPredecessors(int object, OWLObjectProperty role);

	/**
	 * @param object
	 * @param role
	 * @return get a copy of all Predecessors
	 */
	public Set<Integer> getPredecessorsTakingEqualityIntoAccount(int object, OWLObjectProperty role);

	public Set<Integer> getSuccessors(int subject, OWLObjectProperty role);

	/**
	 * @param subject
	 * @param role
	 * @return get a copy of all Successors
	 */
	public Set<Integer> getSuccessorsTakingEqualityIntoAccount(int subject, OWLObjectProperty role);

	/**
	 * @return entailed sameas assertion as a map. Note that this include (a
	 *         equivalent a) for every individuals a. And note that in the
	 *         datastructure we store only sameas assertions in which a has a
	 *         really different equivalent individual, e.g. b.
	 */
	public Map<Integer, Set<Integer>> getEntailedSameasAssertions();

	public Set<OWLAxiom> getOWLAPISameasAssertions();

	public Integer getNumberOfEntailedSameasAssertions();

	/**
	 * @param role
	 * @return a copy of subjects occurring in role assertions of the role
	 *         {@code role}
	 */
	public Set<Integer> getSubjectsInRoleAssertions(OWLObjectProperty role);

	/**
	 * @param role
	 * @return a copy of objects occurring in role assertions of the role
	 *         {@code role}
	 */
	public Set<Integer> getObjectsInRoleAssertions(OWLObjectProperty role);

	public boolean addSameasAssertion(Set<Integer> setOfSameasIndividuals);

	public void increaseNumberOfInputConceptAssertions(int addedNumber);

	public void increaseNumberOfInputRoleAssertions(int addedNumber);

	public ConceptAssertionBox getConceptAssertionBox();

	public RoleAssertionBox getRoleAssertionBox();

	public SameAsBox2 getSameasBox();

	// public Integer getNumberOfEntailedAssertionsWithoutNormalizedSymbols();
	/**
	 * @return all number of concept assertions without normalization symbols
	 *         and TAKING SAMEAS into account. Note that universal concept
	 *         (owl:thing) is ignored.
	 */
	public int getNumberOfConceptAssertionsWithoutNormalizationSymbolsTakingSAMEASIntoAccount();

	/**
	 * @param individual
	 * @return number of concept assertions for the given individual, without
	 *         normalization symbols and TAKING SAMEAS into account. Note that
	 *         universal concept (owl:thing) is ignored.
	 */
	public int getNumberOfConceptAssertionsWithoutNormalizationSymbolsTakingSAMEASIntoAccount(int individual);

	/**
	 * @param givenIndividual
	 * @return number of role assertions for the given individual, TAKING SAMEAS
	 *         into account. Note that universal role is ignored.
	 */
	public int getNumberOfRoleAssertionsTakingSAMEASIntoAccount(int givenIndividual);

	/**
	 * @return number of ALL role assertions for ALL individuals, TAKING SAMEAS
	 *         into account. Note that universal role is ignored.
	 */
	public int getNumberOfRoleAssertionsTakingSAMEASIntoAccount();

	/**
	 * @return all OWLAPI concept assertions without normalization symbols and
	 *         TAKING SAMEAS into account. Note that universal concept
	 *         (owl:thing) is ignored.
	 */
	public Set<OWLClassAssertionAxiom> getOWLAPIConceptAssertionsWithoutNormalizationSymbolsTakingSAMEASIntoAccount();

	/**
	 * @param individual
	 * @return OWLAPI concept assertions for the given individual, without
	 *         normalization symbols and TAKING SAMEAS into account. Note that
	 *         universal concept (owl:thing) is ignored.
	 */
	public Set<OWLClassAssertionAxiom> getOWLAPIConceptAssertionsWithoutNormalizationSymbolsTakingSAMEASIntoAccount(
			int individual);

	/**
	 * @param givenIndividual
	 * @return OWLAPI role assertions for the given individual, TAKING SAMEAS
	 *         into account. Note that universal role is ignored.
	 */
	public Set<OWLObjectPropertyAssertionAxiom> getOWLAPIRoleAssertionsTakingSAMEASIntoAccount(int givenIndividual);

	/**
	 * @return ALL OWLAPI role assertions for ALL individuals, TAKING SAMEAS
	 *         into account. Note that universal role is ignored.
	 */
	public Set<OWLObjectPropertyAssertionAxiom> getOWLAPIRoleAssertionsTakingSAMEASIntoAccount();
}
