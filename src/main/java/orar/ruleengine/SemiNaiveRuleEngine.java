package orar.ruleengine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.apache.log4j.Logger;

import orar.config.Configuration;
import orar.config.LogInfo;
import orar.modeling.ontology.OrarOntology;
import orar.modeling.roleassertion.IndexedRoleAssertion;
import orar.util.PrintingHelper;

public class SemiNaiveRuleEngine implements RuleEngine {
	private Queue<Set<Integer>> todoSameasAssertions;
	private Queue<IndexedRoleAssertion> todoRoleAssertions;
	private final OrarOntology orarOntology;
	private final Logger logger = Logger.getLogger(SemiNaiveRuleEngine.class);
	private RuleExecutor sameasRule;
	private RuleExecutor subroRule;
	private RuleExecutor tranRule;
	private RuleExecutor funcRule;
	private RuleExecutor inverseRule;
	private List<RuleExecutor> ruleExecutors;
	private long reasoningTime;

	public SemiNaiveRuleEngine(OrarOntology orarOntology) {
		this.orarOntology = orarOntology;
		this.todoSameasAssertions = new LinkedList<Set<Integer>>();
		this.todoRoleAssertions = new LinkedList<IndexedRoleAssertion>();

		this.sameasRule = new SameasRuleExecutor(orarOntology);
		this.funcRule = new FunctionalityRuleExecutor(orarOntology);
		this.tranRule = new TransitivityRuleExecutor(orarOntology);
		this.subroRule = new SubRoleRuleExecutor(orarOntology);
		// this.inverseRule = new InverseRoleRuleExecutor(orarOntology);

		this.ruleExecutors = new ArrayList<RuleExecutor>();
		/*
		 * order matters here. sameasRule and then subroleRule always need to be run before the others.
		 */
		this.ruleExecutors.add(sameasRule);
		this.ruleExecutors.add(subroRule);
		this.ruleExecutors.add(tranRule);
		this.ruleExecutors.add(funcRule);
		// this.ruleExecutors.add(inverseRule);
	}

	@Override
	public void materialize() {
		long starTime = System.currentTimeMillis();
		for (RuleExecutor ruleEx : this.ruleExecutors) {
			ruleEx.materialize();

			if (Configuration.getInstance().getLogInfos().contains(LogInfo.TUNING_SAMEAS)) {
				logger.info("after running " + ruleEx.getClass().getName() + " Sameas map in the ontology:");
				PrintingHelper.printMap(this.orarOntology.getSameasBox().getSameasMap());
			}

			if (Configuration.getInstance().getLogInfos().contains(LogInfo.TIME_STAMP_FOR_EACH_STEP)) {
				/*
				 * log for new roleassertions
				 */
				int countNewRoleAssertions = ruleEx.getNewRoleAssertions().size();
				if (countNewRoleAssertions > 0) {
					logger.info("Size of new role assertion from" + ruleEx.getClass().getName() + ": "
							+ countNewRoleAssertions);
				}
				/*
				 * log for new sameas
				 */
				int countNewSameas = ruleEx.getNewSameasAssertions().size();
				if (countNewSameas > 0) {
					logger.info("Size of new sameas assertion from  " + ruleEx.getClass().getName() + ": "
							+ countNewSameas);
				}
			}
			this.todoRoleAssertions.addAll(ruleEx.getNewRoleAssertions());
			this.todoSameasAssertions.addAll(ruleEx.getNewSameasAssertions());
			if (Configuration.getInstance().getLogInfos().contains(LogInfo.TUNING_SAMEAS)) {
				logger.info("after running " + ruleEx.getClass().getName() + " Sameas map in the todoQueue:");
				PrintingHelper.printQueue(this.todoSameasAssertions);
			}
		}

		long startTimeOfIncrementalReasoning = System.currentTimeMillis();
		incrementalMaterialize();
		if (Configuration.getInstance().getLogInfos().contains(LogInfo.TIME_STAMP_FOR_EACH_STEP)) {
			long endTime = System.currentTimeMillis();
			this.reasoningTime = (endTime - starTime) / 1000;
			long incrementalReasoningTime = (endTime - startTimeOfIncrementalReasoning) / 1000;
			logger.info("Reasoning time for incremental rule reasoning step: " + incrementalReasoningTime);
			logger.info("Reasoning time for deductive closure computing in this step: " + this.reasoningTime);
		}
	}

	@Override
	public void incrementalMaterialize() {
		boolean hasNewElement = false;
		while (!this.todoRoleAssertions.isEmpty() || !this.todoSameasAssertions.isEmpty()) {
			while (!this.todoSameasAssertions.isEmpty()) {
				Set<Integer> setOfSameasIndividuals = this.todoSameasAssertions.poll();

				for (RuleExecutor ruleEx : this.ruleExecutors) {
					ruleEx.clearOldBuffer();

					if (Configuration.getInstance().getLogInfos().contains(LogInfo.TUNING_SAMEAS)) {
						logger.info("Considering :" + setOfSameasIndividuals);
					}

					ruleEx.incrementalMaterialize(setOfSameasIndividuals);
					if (Configuration.getInstance().getLogInfos().contains(LogInfo.TIME_STAMP_FOR_EACH_STEP)) {
						int countNewSameas = ruleEx.getNewSameasAssertions().size();
						if (countNewSameas > 0) {
							logger.info("Size of new sameas assertion from (incremental) " + ruleEx.getClass().getName()
									+ ": " + countNewSameas);
						}
					}
					this.todoRoleAssertions.addAll(ruleEx.getNewRoleAssertions());
					this.todoSameasAssertions.addAll(ruleEx.getNewSameasAssertions());

					if (Configuration.getInstance().getLogInfos().contains(LogInfo.TUNING_SAMEAS)) {
						// && ruleEx instanceof SameasRuleExecutor) {
						logger.info("after running incremental step " + ruleEx.getClass().getName() + " Sameas map:");
						PrintingHelper.printMap(this.orarOntology.getSameasBox().getSameasMap());

						logger.info("after running incremental step" + ruleEx.getClass().getName()
								+ " Sameas map in the todoQueue:");
						PrintingHelper.printQueue(this.todoSameasAssertions);
					}
				}
			}

			while (!this.todoRoleAssertions.isEmpty()) {
				IndexedRoleAssertion aRoleAssertion = this.todoRoleAssertions.poll();
				for (RuleExecutor ruleEx : this.ruleExecutors) {
					ruleEx.clearOldBuffer();
					ruleEx.incrementalMaterialize(aRoleAssertion);
					if (Configuration.getInstance().getLogInfos().contains(LogInfo.TIME_STAMP_FOR_EACH_STEP)) {
						int countNewSameas = ruleEx.getNewSameasAssertions().size();
						if (countNewSameas > 0) {
							logger.info("Size of new sameas assertion from (incremental) " + ruleEx.getClass().getName()
									+ ": " + countNewSameas);
						}
					}
					this.todoRoleAssertions.addAll(ruleEx.getNewRoleAssertions());
					this.todoSameasAssertions.addAll(ruleEx.getNewSameasAssertions());

					if (Configuration.getInstance().getLogInfos().contains(LogInfo.TUNING_SAMEAS)) {
						logger.info("after running incremental mode " + ruleEx.getClass().getName() + " Sameas map:");
						PrintingHelper.printMap(this.orarOntology.getSameasBox().getSameasMap());
					}
				}
			}
		}
	}

	@Override
	public OrarOntology getOntology() {

		return this.orarOntology;
	}

	@Override
	public void addTodoSameasAssertions(Set<Set<Integer>> todoSameasAssertions) {
		this.todoSameasAssertions.addAll(todoSameasAssertions);

	}

	@Override
	public void addTodoRoleAsesrtions(Set<IndexedRoleAssertion> odoRoleAssertions) {
		this.todoRoleAssertions.addAll(odoRoleAssertions);

	}

	@Override
	public long getReasoningTime() {

		return this.reasoningTime;
	}

}
