package au.id.mcmaster.csv2padded;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CSV2Padded {
	private List<SpecItem> specItems;
	
	public void saveDataCSV(String fileName, List<Map<String, String>> resultsMaps) throws IOException {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(fileName)))) {
			bw.write(specItems.stream().map(s -> s.getName()).collect(joining(",")) + "\n");
			bw.write(resultsMaps.stream().map(m -> specItems.stream().map(s -> m.getOrDefault(s.getName(),"")).collect(joining(","))).collect(joining("\n")) + "\n");
		}
	}

	public CSV2Padded(String scemaFileName) throws Exception {
		List<Map<String, String>> schema = getDataMap(scemaFileName);
		this.specItems = schema.stream().map(m -> new SpecItem(m)).filter(s -> s.isValid()).collect(toList());
	}
	
	public List<Map<String, String>> parsePadded(List<String> paddedData) {
	    return paddedData.stream().map(l -> specItems.stream().collect(toMap(s->s.getName(),s->s.parse(l)))).collect(toList());
	}

	public void saveData(String fileName, List<String> lines) throws IOException {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(fileName)))) {
			bw.write(lines.stream().collect(joining("\n")));
		}
	}

	public List<String> convert(List<Map<String, String>> data) {
	    return data.stream().map(m -> specItems.stream().map(s -> s.format(m.get(s.getName()))).collect(joining(""))).collect(toList());
	}

	public List<Map<String, String>> loadData(String csvDataFile) throws Exception {
		return getDataMap(csvDataFile);
	}
	
	private static List<Map<String, String>> getDataMap(String fileName) throws IOException {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(CSV2Padded.class.getClassLoader().getResourceAsStream(fileName)))) {
		    String[] headers = br.readLine().split(",");
		    return br.lines().map(s -> s.split(",")).map(t -> range(0, t.length).boxed().collect(toMap(i -> headers[i], i -> t[i]))).collect(toList());
		}		
	}
	
	public List<String> getDataList(String fileName) throws IOException {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(CSV2Padded.class.getClassLoader().getResourceAsStream(fileName)))) {
		    return br.lines().collect(toList());
		}		
	}
	
	public static boolean compare(List<Map<String,String>> data1, List<Map<String,String>> data2) {
		return range(0, data1.size()).boxed().map(i -> compare(data1.get(i), data2.get(i))).reduce(true, (a,b) -> a && b);
	}
	
	public static boolean compare(Map<String,String> map1, Map<String,String> map2) {
		return map1.keySet().stream().map(k -> map1.get(k).equals(map2.get(k))).reduce(true, (a,b) -> a && b);
	}

	public static void print(List<Map<String,String>> data1, List<Map<String,String>> data2) {
		range(0, data1.size()).boxed().forEach(i -> print(data1.get(i), data2.get(i)));
	}
	
	public static void print(Map<String,String> map1, Map<String,String> map2) {
		map1.keySet().stream().map(k -> String.format("| %-20.20s | %-20.20s | %-20.20s | %-5.5s |", k, map1.get(k), map2.get(k), map1.getOrDefault(k, "").equals(map2.getOrDefault(k, "")))).forEach(l -> System.out.println(l));
	}

	private static class SpecItem {
		private String name;
		private String type;
		private String format;
		private int offset;
		private int length;
	
		public SpecItem(Map<String,String> map) {
			this.name = map.get("Name");
			this.type = map.get("Type");
			this.format = map.get("Format");
			this.offset = Integer.parseInt(map.get("Offset"));
			this.length = Integer.parseInt(map.get("Length"));
		}
		
		public boolean isValid() {
			return offset > 0;
		}
		
		public String getName() {
			return this.name;
		}
		
		private String padString(String value) {
			return String.format(String.format("%%-%d.%ds", length, length), (value == null ? "" : (value.length() < length ? value : value.substring(0,length))));
		}
		
		public String parse(String value) {
			int start = offset-1;
			int end = start + length;
			try
			{
				String parsedValue =  ((end <= value.length()) ? value.substring(start, end).trim() : "");
				return parsedValue;
			}
			catch (StringIndexOutOfBoundsException e) {
				throw e;
			}
		}
		
		public String format(String value) {
			return padString(Formatters.MAP.getOrDefault(format, Formatters::formatIdentity).apply(value));
		}
	
		@Override
		public String toString() {
			return "SpecItem [name=" + name + ", type=" + type + ", format=" + format + ", offset=" + offset + ", length=" + length + "]";
		}
	}
	
	private static class Formatters
	{
		@SuppressWarnings("serial")
		public static final Map<String,Function<String, String>> MAP = new HashMap<String,Function<String, String>>() {{
			put("YYYY-MM-DD",  Formatters::formatDate);
		}};
		
		public static String formatDate(String value) {
		    try {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd");
				System.out.println(value);
				System.out.println(df.parse(value));
				System.out.println(df.format(df.parse(value)));
				return df.format(df.parse(value));
			} catch (ParseException e) {
				return "";
			}
		}
		
		public static String formatIdentity(String value) {
			return value;
		}
	}
}