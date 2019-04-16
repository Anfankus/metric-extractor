package data.io.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.csvreader.CsvWriter;
//import com.google.common.io.Files;

import cn.edu.seu.java.util.StringUtil;

public class DataWriter {

  private CsvWriter writer;

  public DataWriter(String csvFilePath) {
    File file = new File(csvFilePath);
    if (file.exists() != true) {
      try {
        file.getParentFile().mkdirs();
        file.createNewFile();
        System.out.println("create a new file " + file.getName());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    this.writer = new CsvWriter(csvFilePath, ',', Charset.forName("SJIS"));
  }

  public void writeArrayToCSVFile(String[] oneRecord) {
    try {
      writer.writeRecord(oneRecord);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void writeListToCSVFile(List<String[]> sourceList) {
    for (String[] oneRecord : sourceList) {
      try {
        writer.writeRecord(oneRecord);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void writeMapToCSVFile(HashMap<String, Integer[]> records) {
    Set<String> keys = records.keySet();
    for (String key : keys) {
      Integer[] value = records.get(key);
      String[] record = IOUtil.mergeToStringArray(key, value);
      this.writeArrayToCSVFile(record);
    }
  }

  public void close() {
    writer.close();
  }

  public static void main(String[] arg1) {
		/*String releasePath = "C:/Desktop/cc-defect/ant/ant1.1/";
		for (ASTClass clazz : allASTClasses) {
		String fullClassName = clazz.getFullyQualifiedName();
		if (clazz.isAbstract() == false && clazz.isInterface() == false && clazz.isTopLevelClass()) {
			//DataWriter dw = new DataWriter(releasePath + clazz.getFullyQualifiedName() + ".csv");
			//dw.writeListToCSVFile(lines);
			//dw.close();
			//System.out.println("----------------------------------------------------------------");
		}
		}*/
  }
}
