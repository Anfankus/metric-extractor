package data.io.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import cn.edu.seu.java.util.TimeMonitor;

public class IOUtil {

  private static String outPutpath;
  private static String errorOutputPath;

  static {
    File file = new File("");
    try {
      IOUtil.outPutpath = file.getAbsolutePath() + File.separator + "result.txt";
      IOUtil.errorOutputPath = file.getAbsolutePath() + File.separator + "error.txt";

      if (isVaidPath(outPutpath, errorOutputPath) == false) {
        System.exit(0);
      }
    } catch (Exception e) {
      IOUtil.catchError(e.toString());
    }
  }

  private static boolean isVaidPath(String outPutpathName, String errorOutputPathName) {
    boolean isvalid = true;
    File file1 = new File(outPutpathName);

    if (file1.exists() != true) {
      isvalid = false;
      IOUtil.catchError(TimeMonitor.dateAndTime());
      IOUtil.catchError(outPutpathName + " is NOT a valid output path.");
      System.out.println(outPutpathName + " is NOT a valid output path.");
    }

    File file2 = new File(errorOutputPathName);
    if (file2.exists() != true) {
      isvalid = false;
      IOUtil.catchError(TimeMonitor.dateAndTime());
      IOUtil.catchError(errorOutputPathName + " is NOT a valid errorOutPut path.");
      System.out.println(errorOutputPathName + " is NOT a valid errorOutPut path.");
    }

    return isvalid;
  }

  public static void outputToFile(int i) {
    IOUtil.outputToFile(Integer.toString(i));
  }

  public static void output(long l) {
    IOUtil.outputToFile(Long.toString(l));
  }

  public static void output(double d) {
    IOUtil.outputToFile(Double.toString(d));
  }

  public static void outputToFile(List<String> list) {
    File file = new File(outPutpath);
    boolean append = true;
    try {
      FileWriter fw = new FileWriter(file, append);
      if (list.size() > 0) {
        for (String str : list) {
          fw.write(str + System.getProperty("line.separator"));
        }
      }
      fw.flush();
      fw.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public static void output(String[] record) {
    if (record.length > 0) {
      File file = new File(outPutpath);
      boolean append = true;
      try {
        FileWriter fw = new FileWriter(file, append);
        for (String str : record) {
          fw.write(str + System.getProperty("line.separator"));
        }
        fw.flush();
        fw.close();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }

  public static void outputToFile(String content) {
    File file = new File(outPutpath);

    boolean append = true;
    try {
      if (file.exists() == false) {
        file.createNewFile();
      }
      FileWriter fw = new FileWriter(file, append);
      fw.write(content + System.getProperty("line.separator"));

      fw.flush();
      fw.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }


  public static void println(String[] record) {
    if (record.length > 0) {
      for (String s : record) {
        System.out.println(s + System.getProperty("line.separator"));
      }
    }
  }

  public static void println(String s) {
    System.out.println(s);
  }

  public static void println(double d) {
    System.out.println(d);
  }

  public static void println(int i) {
    System.out.println(i);
  }

  public static void println(List<String> list) {
    if (list.size() > 0) {
      for (String str : list) {
        System.out.println(str + System.getProperty("line.separator"));
      }
    }
  }

  public static void print(String s) {
    System.out.print(s);
  }

  public static void catchError(String content) {
    File file = new File(errorOutputPath);
    boolean append = true;
    try {
      if (!file.exists()) {
        file.createNewFile();
      }
      FileWriter fw = new FileWriter(file, append);
      content = content + System.getProperty("line.separator");
      fw.write(content);

      fw.flush();
      fw.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public static void catchError(List<String> list) {
    if (list.size() > 0) {
      for (String str : list) {
        IOUtil.catchError(str);
      }
    }
  }

  public static void clear() {
    File file = new File(outPutpath);
    try {
      if (!file.exists()) {
        file.createNewFile();
      }
      FileWriter fw = new FileWriter(file, false);
      fw.write("");
      fw.flush();
      fw.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public static String[] mergeToStringArray(String firstValue, Integer[] sourceArray) {
    String[] targetAarray = new String[sourceArray.length + 1];
    targetAarray[0] = firstValue;
    for (int i = 0; i < sourceArray.length; i++) {
      targetAarray[i + 1] = String.valueOf(sourceArray[i]);
    }
    return targetAarray;
  }

  public static String[] mergeToStringArray(String firstValue, int[] sourceArray) {
    String[] targetAarray = new String[sourceArray.length + 1];
    targetAarray[0] = firstValue;
    for (int i = 0; i < sourceArray.length; i++) {
      targetAarray[i + 1] = String.valueOf(sourceArray[i]);
    }
    return targetAarray;
  }

  public static String[] mergeToStringArray(String firstValue, String[] sourceArray) {
    String[] targetAarray = new String[sourceArray.length + 1];
    targetAarray[0] = firstValue;
    for (int i = 0; i < sourceArray.length; i++) {
      targetAarray[i + 1] = sourceArray[i];
    }
    return targetAarray;
  }

  public static void setOutputPath(String path) {
    File file = new File(path);
    try {
      IOUtil.outPutpath = path;
      if (file.isFile() == false) {
        System.exit(0);
      }
    } catch (Exception e) {
      IOUtil.catchError(e.toString());
    }

  }

  public static void main(String[] arg1) throws IOException {
    File file = new File(
        "C:/Desktop/textdiff/Eliminated_Test_Source_File/renamed_file_copy/aaa.java");
    org.apache.commons.io.FileUtils.writeStringToFile(file, "ssss", "UTF-8", false);
  }

}
