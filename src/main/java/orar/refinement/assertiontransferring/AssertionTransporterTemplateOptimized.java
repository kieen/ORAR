package orar.refinement.assertiontransferring;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import orar.abstraction.PairOfSubjectAndObject;
import orar.config.Configuration;
import orar.config.DebugLevel;
import orar.config.LogInfo;
import orar.data.DataForTransferingEntailments;
import orar.indexing.IndividualIndexerUsingMapAndList;
import orar.modeling.ontology.OrarOntology;
import orar.modeling.roleassertion.IndexedRoleAssertionList;
import orar.refinement.abstractroleassertion.AbstractRoleAssertionBox;
import orar.refinement.abstractroleassertion.RoleAssertionList;
import orar.util.PrintingHelper;

public abstract class AssertionTransporterTemplateOptimized implements AssertionTransporter {
	// original ontology
	protected final OrarOntology orarOntology;
	// entailments of the abstraction
	protected Map<OWLNamedIndividual, Set<OWLClass>> xAbstractConceptAssertionsAsMap;
	protected Map<OWLNamedIndividual, Set<OWLClass>> yAbstractConceptAssertionsAsMap;
	protected Map<OWLNamedIndividual, Set<OWLClass>> zAbstractConceptAssertionsAsMap;
	protected AbstractRoleAssertionBox abstractRoleAssertionBox;
	protected Map<OWLNamedIndividual, Set<OWLNamedIndividual>> abstractSameasMap;
	// flag for abox updating
	protected boolean isABoxExtended;
	protected boolean isABoxExtendedViaX;
	protected boolean isABoxExtendedViaY;
	protected boolean isABoxExtendedViaZ;
	protected boolean isABoxExtendedWithNewSpecialRoleAssertions;
	protected boolean isABoxExtendedWithNewSameasAssertions;
	// debugging
	protected final Configuration config;
	private static final Logger logger = Logger.getLogger(AssertionTransporterTemplateOptimized.class);
	// map/data for transferring assertions
	protected final DataForTransferingEntailments dataForTransferingEntailments;
	// output
	protected final IndexedRoleAssertionList newRoleAssertions;
	protected final Set<Set<Integer>> newSameasAssertions;

	public AssertionTransporterTemplateOptimized(OrarOntology orarOntoloy) {
		this.orarOntology = orarOntoloy;

		// this.abstractConceptAssertionsAsMap = new HashMap<>();
		// this.abstractRoleAssertionBox = new AbstractRoleAssertionBox();
		// this.abstractSameasMap = new HashMap<>();
		/*
		 * initialize flags of updating
		 */
		this.isABoxExtended = false;
		this.isABoxExtendedViaX = false;
		this.isABoxExtendedViaY = false;
		this.isABoxExtendedViaZ = false;
		this.isABoxExtendedWithNewSpecialRoleAssertions = false;
		this.isABoxExtendedWithNewSameasAssertions = false;
		/*
		 * others
		 */
		this.config = Configuration.getInstance();
		this.dataForTransferingEntailments = DataForTransferingEntailments.getInstance();
		this.newRoleAssertions = new IndexedRoleAssertionList();
		this.newSameasAssertions = new HashSet<Set<Integer>>();
	}

	@Override
	public void updateOriginalABox() {
		long startUpdateConceptAssertion = System.currentTimeMillis();
		transferConceptAssertions();// not change
		long endUpdateConceptAssertion = System.currentTimeMillis();
		if (this.config.getLogInfos().contains(LogInfo.TIME_STAMP_FOR_EACH_STEP)) {
			long updateingConceptAsesrtionTime = (endUpdateConceptAssertion - startUpdateConceptAssertion) / 1000;
			logger.info("Time for updating concep assertions in the original ABox in seconds: "
					+ updateingConceptAsesrtionTime);
		}
		transferRoleAssertions();// not change
		transferSameasAssertions();// varies
	}

	protected abstract void transferSameasAssertions();

	private void transferRoleAssertions() {
		transferRoleAssertionsForLoopConcepts();// not change
		tranferRoleAssertionsBetweenUX();// varies
		transferRoleAssertionsForXYHavingFunctionalRoles();// not change
		transferRoleAssertionsForZXHavingInverseFunctionalRoles();// not change
	}

	/**
	 * add role assertions by the rule R^2_<: M(a), F(a,b) --> H(a,b). Case: F
	 * is atomic.
	 */
	private void transferRoleAssertionsForZXHavingInverseFunctionalRoles() {
		RoleAssertionList roleAssertionList = this.abstractRoleAssertionBox.getZxRoleAssertionsForType();
		int size = roleAssertionList.getSize();
		for (int index = 0; index < size; index++) {
			/*
			 * get role(z,x)
			 */
			OWLNamedIndividual zAbstractIndiv = roleAssertionList.getSubject(index);
			OWLNamedIndividual xAbstractIndiv = roleAssertionList.getObject(index);
			OWLObjectProperty roleInEntailedAssertion = roleAssertionList.getRole(index);
			/*
			 * each pair of (z,x) is connected by the ONLY one role in the
			 * abstraction. We use this role and original individuals
			 * corresponding to x to find the correct pair of individuals for
			 * adding role assertions.
			 * 
			 */

			PairOfSubjectAndObject pairOfZX = new PairOfSubjectAndObject(zAbstractIndiv, xAbstractIndiv);
			OWLObjectProperty roleConnectsZandX = this.dataForTransferingEntailments.getMap_ZX_2_Role().get(pairOfZX);
			if (roleConnectsZandX == null)
				continue;
			/*
			 * add role assertions to the original ABox.
			 */
			Set<Integer> originalIndsCorrespondingToX = this.dataForTransferingEntailments
					.getOriginalIndividuals(xAbstractIndiv);
			for (Integer eachOriginalIndiv : originalIndsCorrespondingToX) {
				Set<Integer> allSubjects = this.orarOntology.getPredecessors(eachOriginalIndiv, roleConnectsZandX);
				for (Integer eachSubject : allSubjects) {
					if (this.orarOntology.addRoleAssertion(eachSubject, roleInEntailedAssertion, eachOriginalIndiv)) {
						this.isABoxExtended = true;
						this.isABoxExtendedWithNewSpecialRoleAssertions = true;
						this.newRoleAssertions.addRoleAssertion(eachSubject, roleInEntailedAssertion,
								eachOriginalIndiv);
					}
				}
			}
		}

	}

	/**
	 * add role assertions by the rule R^2_<: M(a), F(a,b) --> H(a,b). Case: F
	 * is the inverse of an atomic role.
	 */
	private void transferRoleAssertionsForXYHavingFunctionalRoles() {
		RoleAssertionList roleAssertionList = this.abstractRoleAssertionBox.getXyRoleAssertionsForType();
		int size = roleAssertionList.getSize();
		for (int index = 0; index < size; index++) {
			/*
			 * get role(x,y)
			 */
			OWLNamedIndividual xAbstractIndiv = roleAssertionList.getSubject(index);
			OWLObjectProperty roleInEntailedAssertion = roleAssertionList.getRole(index);
			OWLNamedIndividual yAbstractIndiv = roleAssertionList.getObject(index);
			/*
			 * each pair of (x,y) is connected by the ONLY one role in the
			 * abstraction. We use this role and original individuals
			 * corresponding to x to find the correct pair of individuals for
			 * adding role assertions.
			 * 
			 */

			PairOfSubjectAndObject pairOfXY = new PairOfSubjectAndObject(xAbstractIndiv, yAbstractIndiv);
			OWLObjectProperty roleConnectsXandY = this.dataForTransferingEntailments.getMap_XY_2_Role().get(pairOfXY);
			if (roleConnectsXandY == null)
				continue;
			/*
			 * add role assertions
			 */
			Set<Integer> originalIndsCorrespondingToX = this.dataForTransferingEntailments
					.getOriginalIndividuals(xAbstractIndiv);
			for (Integer eachOriginalIndiv : originalIndsCorrespondingToX) {
				Set<Integer> allObjects = this.orarOntology.getSuccessors(eachOriginalIndiv, roleConnectsXandY);
				for (Integer eachObject : allObjects) {
					if (this.orarOntology.addRoleAssertion(eachOriginalIndiv, roleInEntailedAssertion, eachObject)) {
						this.isABoxExtended = true;
						this.isABoxExtendedWithNewSpecialRoleAssertions = true;
						this.newRoleAssertions.addRoleAssertion(eachOriginalIndiv, roleInEntailedAssertion, eachObject);
					}
				}
			}
		}

	}

	/**
	 * transferring from abstract role assertions of the form R(u,x) or R(x,u),
	 * e.g. from rule R_\exists
	 */
	protected abstract void tranferRoleAssertionsBetweenUX();

	private void transferRoleAssertionsForLoopConcepts() {
		RoleAssertionList roleAssertionList = this.abstractRoleAssertionBox.getLoopRoleAssertions();
		int size = roleAssertionList.getSize();
		for (int index = 0; index < size; index++) {
			OWLNamedIndividual xInd = roleAssertionList.getSubject(index);
			OWLObjectProperty role = roleAssertionList.getRole(index);
			// get original individuals
			Set<Integer> allOriginalInds = this.dataForTransferingEntailments.getOriginalIndividuals(xInd);
			// add assertions to the orignal ABox
			for (Integer eachOriginalInd : allOriginalInds) {
				if (this.orarOntology.addRoleAssertion(eachOriginalInd, role, eachOriginalInd)) {
					this.isABoxExtended = true;
					this.isABoxExtendedWithNewSpecialRoleAssertions = true;
					this.newRoleAssertions.addRoleAssertion(eachOriginalInd, role, eachOriginalInd);
				}
			}
		}
	}

	protected void transferConceptAssertions() {
		this.isABoxExtendedViaX = transferConceptAssertions(this.xAbstractConceptAssertionsAsMap,
				this.dataForTransferingEntailments.getMap_XAbstractIndiv_2_OriginalIndivs());
		this.isABoxExtendedViaY = transferConceptAssertions(this.yAbstractConceptAssertionsAsMap,
				this.dataForTransferingEntailments.getMap_YAbstractIndiv_2_OriginalIndivs());
		this.isABoxExtendedViaZ = transferConceptAssertions(this.zAbstractConceptAssertionsAsMap,
				this.dataForTransferingEntailments.getMap_ZAbstractIndiv_2_OriginalIndivs());
	}

	/**
	 * add concept assertions based on concept assertions of representatives
	 * (X,Y,Z) for combined-types.
	 */
	protected boolean transferConceptAssertions(Map<OWLNamedIndividual, Set<OWLClass>> assertionMap,
			Map<OWLNamedIndividual, Set<Integer>> mapFromAbstractIndividual2OriginalIndividual) {
		Iterator<Entry<OWLNamedIndividual, Set<OWLClass>>> iterator = assertionMap.entrySet().iterator();
		boolean hasNewAssertionsFromThisMap = false;
		while (iterator.hasNext()) {
			Entry<OWLNamedIndividual, Set<OWLClass>> entry = iterator.next();
			OWLNamedIndividual abstractInd = entry.getKey();
			Set<OWLClass> concepts = entry.getValue();
			if (concepts != null) {
				Set<Integer> originalIndividuals = mapFromAbstractIndividual2OriginalIndividual.get(abstractInd);
				for (Integer originalInd : originalIndividuals) {

					// /*
					// * Debug
					// */
					// if (originalInd.equals(bug_individual)){
					// logger.info("***something wrong might come from here
					// ***");
					// logger.info("individual:"+bug_individual);
					// logger.info("Concepts:"+concepts);
					// logger.info("Reason:");
					// logger.info("By assertion from bstract
					// individual:"+abstractInd);
					// //if (sharedData.get)
					//
					// }
					if (config.getDebuglevels().contains(DebugLevel.UPDATING_CONCEPT_ASSERTION)) {
						logger.info("***DEBUG Update concept assertions in the original ABox ***");
						logger.info("Individual:"
								+ IndividualIndexerUsingMapAndList.getInstance().getIndividualString(originalInd));
						logger.info("has new concepts:" + concepts);
						logger.info("Reason: get from concept assertion of the abstract individual:" + abstractInd);
						logger.info("*=====================================================*");
					}
					/*
					 * end of debug
					 */
					Set<OWLClass> existingAssertedConcept = new HashSet<OWLClass>();
					if (this.config.getDebuglevels().contains(DebugLevel.TRANSFER_CONCEPTASSERTION)) {
						existingAssertedConcept.addAll(this.orarOntology.getAssertedConcepts(originalInd));
					}
					if (this.orarOntology.addManyConceptAssertions(originalInd, concepts)) {
						hasNewAssertionsFromThisMap = true;
						this.isABoxExtended = true;
						if (this.config.getDebuglevels().contains(DebugLevel.TRANSFER_CONCEPTASSERTION)) {
							logger.info("***DEBUG***TRANSFER_CONCEPTASSERTION:");
							logger.info("For individual:"
									+ IndividualIndexerUsingMapAndList.getInstance().getIndividualString(originalInd));
							logger.info("Existing asserted concepts:");
							PrintingHelper.printSet(existingAssertedConcept);
							logger.info("Newly added asserted concepts:" + concepts);
							logger.info("updated=true");
						}
					}
				}
			}
		}
		return hasNewAssertionsFromThisMap;
	}

	@Override
	public boolean isABoxExtended() {

		return this.isABoxExtended;
	}

	public boolean isABoxExtendedViaX() {
		return this.isABoxExtendedViaX;
	}

	public boolean isABoxExtendedViaY() {
		return this.isABoxExtendedViaY;
	}

	public boolean isABoxExtendedViaZ() {
		return this.isABoxExtendedViaZ;
	}

	public boolean isABoxExtendedWithNewSameasAssertions() {
		return this.isABoxExtendedWithNewSameasAssertions;
	}

	public boolean isABoxExtendedWithNewSpecialRoleAssertions() {
		return this.isABoxExtendedWithNewSpecialRoleAssertions;
	}

	@Override
	public IndexedRoleAssertionList getNewlyAddedRoleAssertions() {
		return this.newRoleAssertions;
	}

	@Override
	public Set<Set<Integer>> getNewlyAddedSameasAssertions() {
		return this.newSameasAssertions;
	}
}
