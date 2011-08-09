import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddressIdentification {

	public static List<String> findTextWithAddress(String text,
			Set<String> states) {

		int maxAddressLength = 50;

		Map<Integer, String> zipStarts = findZipCodePositions(text);
		Map<Integer, String> statePosition = findStates(text, states);

		Map<Integer, String> stateZip = findStateZip(text, zipStarts,
				statePosition);

		List<String> out = new ArrayList<String>();

		for (int start : stateZip.keySet()) {

			String sz = stateZip.get(start);
			String literal = Pattern.quote(sz);
			String addressRegex = "(\\s)*([\\d]+)([\\s]+)(.*)(" + literal + ")";

			Pattern pattern = Pattern.compile(addressRegex);
			

				String possibleAddress = text.substring(
						Math.max(0, start - maxAddressLength),
						start + sz.length());

				//System.out.println(possibleAddress);
				
				String parts [] = possibleAddress.split(" ");
				
				List<Integer> posNumber = new ArrayList<Integer>();
				
				int pos = 0;
				for (String part : parts){
					try{
						//System.out.println(part);
						Integer.valueOf(part.trim());
						posNumber.add(pos);
					}catch (NumberFormatException e){
					}
					pos++;
				}
				
				if (posNumber.size() < 2){
					continue;
				}
				 
				pos = posNumber.get(posNumber.size()-2);
				
				
				
				possibleAddress = possibleAddress.substring( possibleAddress.indexOf(parts[pos]));
				
				//System.out.println(possibleAddress);
				Matcher m = pattern.matcher(possibleAddress);
				boolean find = m.find();

				if (find) {
					String add = possibleAddress.substring(m.start(), m.end())
							.trim();
					System.out.println("start");
					System.out.println(add);
					System.out.println("end");
					out.add(add);
				}
			
		}

		return out;
	}

	public static Map<Integer, String> findStateZip(String text,
			Map<Integer, String> zipPositions, Map<Integer, String> statePos) {

		Map<Integer, String> out = new HashMap<Integer, String>();

		for (int stateP : statePos.keySet()) {
			String state = statePos.get(stateP);
			int zipStart = stateP + state.length() + 1;
			if (zipPositions.containsKey(zipStart)) {
				String match = text.substring(stateP, zipStart
						+ zipPositions.get(zipStart).length());
				System.out.println(match);
				out.put(stateP, match);

			}

		}

		return out;
	}

	public static Map<Integer, String> findStates(String text,
			Set<String> states) {
		text = text.toLowerCase();
		Map<Integer, String> out = new HashMap<Integer, String>();

		for (String state : states) {
			// System.out.println(state);
			String statePattern = Pattern.quote(state);
			String zipRegex = "(\\b)" + statePattern + "(\\b)";

			Pattern pattern = Pattern.compile(zipRegex);
			Matcher m = pattern.matcher(text);
			boolean find = m.find();

			while (find) {
				int start = m.start();
				out.put(start, state.toLowerCase());
				// System.out
				// .println(text.substring(start, start + state.length()));
				find = m.find();
			}
		}

		return out;
	}

	public static Map<Integer, String> findZipCodePositions(String text) {
		String zipRegex = "((\\b)[\\d]{5}(\\b|[!?]))|((\\b)[\\d]{5}-[\\d]{4}(\\b |[!?]))";

		Pattern pattern = Pattern.compile(zipRegex);
		Matcher m = pattern.matcher(text);
		boolean find = m.find();

		Map<Integer, String> out = new HashMap<Integer, String>();

		while (find) {
			int start = m.start();
			int edn = m.end();
			out.put(start, text.substring(start, edn));
			// System.out.println(start);
			// System.out.println(text.substring(start, start + 5));
			find = m.find();
		}
		return out;
	}

	public static Set<String> loadStates() throws IOException {

		File stateFile = new File(
				"/Users/alessandro/Documents/workspace/address-identification/states.txt");

		Set<String> states = new HashSet<String>();

		BufferedReader reader = new BufferedReader(new FileReader(stateFile));
		String line = reader.readLine();
		while (line != null) {
			String[] parts = line.split(",");
			for (String state : parts) {
				state = state.trim();
				if (state.length() > 0) {
					states.add(state);
				}
			}
			line = reader.readLine();
		}
		reader.close();
		return states;
	}

	public static final void main(String[] args) throws IOException {

		List<String> lines = new ArrayList<String>();

		File folder = new File(
		 "/Users/alessandro/Documents/workspace/address-identification/samples");
		//File folder = new File(
		//		"/Users/alessandro/Documents/workspace/address-identification/test");

		for (File filename : folder.listFiles()) {
			//System.out.println(filename);
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line = reader.readLine();
			while (line != null) {
				lines.add(line);
				line = reader.readLine();
			}
			reader.close();
		}

		StringBuffer buffer = new StringBuffer("");
		for (String line : lines) {
			buffer.append(line + " ");
		}
		String text = buffer.toString().replaceAll(" +", " ").toLowerCase();

		List<String> textWithAddress = findTextWithAddress(text, loadStates());
		for (String add : textWithAddress) {
			// System.out.println(add);
		}

	}

}
