package data.io.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.csvreader.CsvReader;

import cn.edu.seu.java.util.StringUtil;


public class DataReader {

  private String[] header;
  private List<String[]> listOfValueWithoutHeader;
  private CsvReader csvReader;

  public DataReader(String csvFilePath) {
    this.listOfValueWithoutHeader = new ArrayList<String[]>();
    try {
      this.csvReader = new CsvReader(csvFilePath, ',', Charset.forName("GBK"));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public String[] getHeader() {
    try {
      if (csvReader.readHeaders()) {
        header = csvReader.getHeaders();
      } else {
        header = new String[0];
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return header;
  }

  public HashMap<String, Integer[]> readRecordWithoutHeaderToMap() {
    HashMap<String, Integer[]> methodName2MethodCCArray = splitListToMap(listOfValueWithoutHeader);
    return methodName2MethodCCArray;
  }

  private HashMap<String, Integer[]> splitListToMap(List<String[]> methodCCList) {
    HashMap<String, Integer[]> methodName2MethodCCArray = new HashMap<String, Integer[]>();
    for (String[] item : methodCCList) {
      String fullMethodName = item[0];
      Integer[] methodCCArray = new Integer[item.length - 1];
      for (int i = 1; i < item.length; i++) {
        methodCCArray[i - 1] = Integer.valueOf(item[i]);
      }
      methodName2MethodCCArray.put(fullMethodName, methodCCArray);
    }
    return methodName2MethodCCArray;
  }

  public List<String[]> readRecordWithoutHeaderToList() {
    this.getHeader();
    try {
      while (csvReader.readRecord()) {
        listOfValueWithoutHeader.add(csvReader.getValues());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return listOfValueWithoutHeader;
  }

  public void close() {
    csvReader.close();
  }

  public static void main(String[] arg1) {
		/*DataReader dr = new DataReader("C:/Desktop/base-defect/camel/camel-1.0.csv");
		String[] head = dr.getHeader();
		List<String[]> results = dr.readRecordWithoutHeaderToList();
		System.out.println(head[0] + "," + head[head.length - 1]);
		for (String[] line : results) {
			System.out.println(line[0] + "," + line[1] + "," + line[2] + "," + line[line.length - 1]);
		}
		dr.close();*/
  }
}
