package au.id.mcmaster.csv2padded;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class CSV2PaddedTest {
	@Test
	public void test() {
		try
		{
			CSV2Padded csv2padded = new CSV2Padded("schema.csv");
			List<Map<String, String>> data = csv2padded.loadData("input-data.csv");
			List<String> paddedData = csv2padded.convert(data);
			csv2padded.saveData("src/test/resources/output-file.txt", paddedData);
			paddedData = csv2padded.getDataList("output-file.txt");
			List<Map<String, String>> resultsMaps = csv2padded.parsePadded(paddedData);
			csv2padded.saveDataCSV("src/test/resources/output-file.csv", resultsMaps);
			CSV2Padded.print(data, resultsMaps);
			Assert.assertTrue(CSV2Padded.compare(data, resultsMaps));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Assert.fail("Should not have thrown an exception: " + e.getMessage());
		}
	}
}
