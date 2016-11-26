package orar.modeling.roleassertion;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import orar.util.DefaultTestDataFactory;
import orar.util.PrintingHelper;

public class MapbasedRoleAssertionBoxTest {
	DefaultTestDataFactory testData = DefaultTestDataFactory.getInsatnce();
	OWLObjectProperty R = testData.getRole("R");
	int a = 0;
	int b = 1;
	int c = 2;

	RoleAssertionBox roleAssertionBox = new MapbasedRoleAssertionBox();

	@Before
	public void setup() {
		roleAssertionBox.addRoleAssertion(a, R, b);
		roleAssertionBox.addRoleAssertion(a, R, c);
		roleAssertionBox.addRoleAssertion(a, R, b);
		roleAssertionBox.addRoleAssertion(a, R, c);
	}

	@Test
	public void getSubjectsInRoleAssertionsTest() {
		PrintingHelper.printSet(roleAssertionBox.getSubjectsInRoleAssertions(R));
		Set<Integer> set_a = new HashSet<>();
		set_a.add(0);
		Assert.assertEquals(set_a, roleAssertionBox.getSubjectsInRoleAssertions(R));

	}

	@Test
	public void getObjectsInRoleAssertionsTest() {
		Set<Integer> set_bc = new HashSet<>();
		set_bc.add(1);
		set_bc.add(2);
		PrintingHelper.printSet(roleAssertionBox.getObjectsInRoleAssertions(R));
		Assert.assertEquals(set_bc, roleAssertionBox.getObjectsInRoleAssertions(R));

	}

	@Test
	public void getNumberOfRoleAsesrtionsTest() {

		PrintingHelper.printString(this.toString(), roleAssertionBox.getNumberOfRoleAssertions() + "");
		Assert.assertEquals(2, roleAssertionBox.getNumberOfRoleAssertions());
	}

	@Test
	public void addRoleAssertion_ShouldNotAddDuplicatedAssertion() {
		Assert.assertFalse(roleAssertionBox.addRoleAssertion(a, R, b));
	}

}
