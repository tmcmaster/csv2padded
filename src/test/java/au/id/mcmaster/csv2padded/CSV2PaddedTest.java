package au.id.mcmaster.csv2padded;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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
			//data.get(2).keySet().stream().forEach(k -> System.out.println(String.format("%20.20s = %s", k, data.get(2).get(k))));
			List<String> paddedData = csv2padded.convert(data);
			csv2padded.saveData("src/test/resources/output-file.txt", paddedData);
			paddedData = csv2padded.getDataList("output-file.txt");
			List<Map<String, String>> resultsMaps = csv2padded.parsePadded(paddedData);
			//resultsMaps.get(2).keySet().stream().forEach(k -> System.out.println(String.format("%20.20s = %s", k, resultsMaps.get(2).get(k))));
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
	
	@Test
	public void debugTesting() throws ParseException {
	    DateFormat df = new SimpleDateFormat("YYYY-MM-DD");
	    Date date = df.parse("1965-04-20");
	    System.out.println(df.format(date));;
	    
		//DateTimeFormatter.ofPattern("YYYY-MM-DD").format(date);
	}
}
