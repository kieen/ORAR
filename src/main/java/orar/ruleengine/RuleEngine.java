package orar.ruleengine;

import java.util.Set;

import orar.modeling.ontology.OrarOntology;
import orar.modeling.roleassertion.IndexedRoleAssertion;

public interface RuleEngine {

	public void materialize();

	public void incrementalMaterialize();

	public OrarOntology getOntology();

	public void addTodoSameasAssertions(Set<Set<Integer>> todoSameasAssertions);

	public void addTodoRoleAsesrtions(Set<IndexedRoleAssertion> odoRoleAssertions);

	public long getReasoningTime();
}
