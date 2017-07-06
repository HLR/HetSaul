
package edu.illinois.cs.cogcomp.saulexamples.TestClassifier;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Umar Manzoor
 * 
 */
public class TestClassifierReader {

	private List<TestClassifierData> dataList;
	private String filepath;

	TestClassifierReader(String file){
		filepath = file;
	}

	public List<TestClassifierData> getDatafromFile() throws IOException {

		BufferedReader reader = new BufferedReader(new FileReader(filepath));

		dataList = new ArrayList<TestClassifierData>();

		String line;
		while ((line = reader.readLine()) != null) {
			String[] s=line.split(" ");
			dataList.add(new TestClassifierData(s[0], s[1].replaceAll("\\t", " "),s[2], s[3]));
		}
		reader.close();
		return dataList;
	}
}
