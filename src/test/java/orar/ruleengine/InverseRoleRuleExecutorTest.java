package orar.ruleengine;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import orar.data.DataForTransferingEntailments;
import orar.data.MetaDataOfOntology;
import orar.indexing.IndividualIndexerInterface;
import orar.indexing.IndividualIndexerUsingMapAndList;
import orar.modeling.ontology.MapbasedOrarOntology;
import orar.modeling.ontology.OrarOntology;
import orar.modeling.roleassertion.IndexedRoleAssertion;
import orar.util.DefaultTestDataFactory;
import orar.util.PrintingHelper;

public class InverseRoleRuleExecutorTest {
	DefaultTestDataFactory testData = DefaultTestDataFactory.getInsatnce();
	IndividualIndexerInterface indexer = IndividualIndexerUsingMapAndList.getInstance();

	/*
	 * Signature
	 */
	Integer a = indexer.getIndexOfOWLIndividual(testData.getIndividual("a"));
	Integer a1 = indexer.getIndexOfOWLIndividual(testData.getIndividual("a1"));
	Integer a2 = indexer.getIndexOfOWLIndividual(testData.getIndividual("a2"));

	Integer b = indexer.getIndexOfOWLIndividual(testData.getIndividual("b"));
	Integer b1 = indexer.getIndexOfOWLIndividual(testData.getIndividual("b1"));
	Integer b2 = indexer.getIndexOfOWLIndividual(testData.getIndividual("b2"));

	Integer c = indexer.getIndexOfOWLIndividual(testData.getIndividual("c"));
	Set<Integer> a1a2 = new HashSet<Integer>();
	Set<Integer> b1b2 = new HashSet<Integer>();
	Set<Integer> a1a2b1b2 = new HashSet<Integer>();
	Set<Integer> b1Set = new HashSet<Integer>();
	Set<Integer> a1Set = new HashSet<Integer>();

	Integer c1 = indexer.getIndexOfOWLIndividual(testData.getIndividual("c1"));
	Integer c2 = indexer.getIndexOfOWLIndividual(testData.getIndividual("c2"));
	Integer d = indexer.getIndexOfOWLIndividual(testData.getIndividual("d"));
	Integer o = indexer.getIndexOfOWLIndividual(testData.getIndividual("o"));

	OWLClass A = testData.getConcept("A");
	OWLClass A1 = testData.getConcept("A1");
	OWLClass A2 = testData.getConcept("A2");

	OWLClass B = testData.getConcept("B");
	OWLClass B1 = testData.getConcept("B1");
	OWLClass B2 = testData.getConcept("B2");

	OWLClass C = testData.getConcept("C");

	OWLClass No = testData.getConcept("No");
	/*
	 * Nominal-Concept, e.g. concept generated for each nominal.
	 */
	OWLClass NoC = testData.getConcept("NoC");

	OWLObjectProperty R = testData.getRole("R");
	OWLObjectProperty R1 = testData.getRole("R1");
	OWLObjectProperty R2 = testData.getRole("R2");

	OWLObjectProperty S = testData.getRole("S");

	OWLObjectProperty T = testData.getRole("T");
	OWLObjectProperty invT = testData.getRole("InvT");
	OWLObjectProperty F = testData.getRole("F");
	OWLObjectProperty InvF = testData.getRole("InvF");

	/*
	 * Others
	 */

	OWLObjectProperty funcRole = testData.getRole("funcRole");
	OWLObjectProperty invFuncRole = testData.getRole("invFuncRole");
	DataForTransferingEntailments sharedMap = DataForTransferingEntailments.getInstance();
	MetaDataOfOntology metaDataOfOntology = MetaDataOfOntology.getInstance();
	OWLDataFactory owlDataFactory = OWLManager.getOWLDataFactory();

	@Before
	public void init() {
		indexer.clear();
		a1a2.add(a1);
		a1a2.add(a2);

		b1b2.add(b1);
		b1b2.add(b2);

		a1a2b1b2.add(a1);
		a1a2b1b2.add(a2);
		a1a2b1b2.add(b1);
		a1a2b1b2.add(b2);

		b1Set.add(b1);
		a1Set.add(a1);
	}

	@Test
	public void test() {
		sharedMap.clear();

		/*
		 * create ontology
		 */
		OrarOntology orarOntology = new MapbasedOrarOntology();

		orarOntology.addRoleAssertion(a, R, b);
		// orarOntology.addRoleAssertion(c, S, d);

		OWLInverseObjectPropertiesAxiom inverse_R_S = owlDataFactory.getOWLInverseObjectPropertiesAxiom(R, S);
		orarOntology.getTBoxAxioms().add(inverse_R_S);

		RuleExecutor ruleExecutor = new InverseRoleRuleExecutor(orarOntology);
		IndexedRoleAssertion roleAssertion = new IndexedRoleAssertion(a, R, a);

		ruleExecutor.incrementalMaterialize(roleAssertion);
		// after computing deductive closure

		PrintingHelper.printSet(orarOntology.getOWLAPIRoleAssertionsWithNormalizationSymbols());
	}

}
