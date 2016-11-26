package orar.data;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

import orar.indexing.IndividualIndexerInterface;
import orar.indexing.IndividualIndexerUsingMapAndList;

public class DataEncodingTest {

	@Test
	public void test() {
		IndividualIndexerInterface indexer = IndividualIndexerUsingMapAndList.getInstance();
		Assert.assertTrue(indexer.getIndexOfIndividualStringID("string1") == 0);

		Assert.assertTrue(1 == indexer.getIndexOfIndividualStringID("string2"));
		Assert.assertTrue(0 == indexer.getIndexOfIndividualStringID("string1"));
		Assert.assertTrue(1 == indexer.getIndexOfIndividualStringID("string2"));
//
//		// PrintingHelper.printMap(indexer.viewMapIndividuslString2Long());
//		// PrintingHelper.printMap(indexer.viewMapLong2IndividuslString());
//
		assertEquals("string1", indexer.getIndividualString(0));
		assertEquals("string2", indexer.getIndividualString(1));
		Assert.assertTrue(2 == indexer.getSize());
	}

}
