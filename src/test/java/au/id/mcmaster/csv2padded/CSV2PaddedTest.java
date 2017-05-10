package au.id.mcmaster.csv2padded;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class CSV2PaddedTest {
	@Test
	public void test() {
		try
		{
			String schemaFile = "schema.csv";
			String inputFile = "input-data.csv";
			String outputFilePadded = "output-file.txt";
			String outputFileCSV = "output-file.csv";
			CSV2Padded csv2padded = new CSV2Padded(schemaFile);
			List<Map<String, String>> data = csv2padded.loadDataMaps(inputFile);
			List<String> paddedData = csv2padded.convert(data);
			csv2padded.saveData("src/test/resources/" + outputFilePadded, paddedData);
			paddedData = csv2padded.loadDataList(outputFilePadded);
			List<Map<String, String>> resultsMaps = csv2padded.parsePadded(paddedData);
			csv2padded.saveDataCSV("src/test/resources/" + outputFileCSV, resultsMaps);
			CSV2Padded.print(data, resultsMaps);
			Assert.assertTrue(CSV2Padded.compare(data, resultsMaps));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Assert.fail("Should not have thrown an exception: " + e.getMessage());
		}
	}
	
	@Test
	public void debugTesting() throws Exception {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-DD");
		System.out.println(df.parse("2000-01-01"));
		System.out.println(df.format(df.parse("2000-01-01")));
	}
}
