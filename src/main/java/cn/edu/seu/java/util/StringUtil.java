package cn.edu.seu.java.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A number of String-specific utility methods for use by PMD.
 *
 * @author BrianRemedios & Huihui Liu
 */
public class StringUtil {

  private static final String[] EMPTY_STRINGS = new String[0];

  public static boolean isNumeric(String str) {
    for (int i = 0; i < str.length(); i++) {
      if (!Character.isDigit(str.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  public static String toString(String[] text1) {
    StringBuffer sb = new StringBuffer();
    for (String s : text1) {
      sb.append(s + " ");
    }
    return sb.toString().trim();
  }

  public static String toString(String[] text1, String sep) {
    StringBuffer sb = new StringBuffer();
    for (String s : text1) {
      sb.append(s + sep);
    }

    if (text1.length > 0) {
      sb.delete(sb.length() - sep.length(), sb.length());
    }
    return sb.toString().trim();
  }

  public static String toString(List<String> text1, String sep) {
    StringBuffer sb = new StringBuffer();
    for (String s : text1) {
      sb.append(s + sep);
    }

    if (text1.size() > 0) {
      sb.deleteCharAt(sb.length() - 1);
    }
    return sb.toString().trim();
  }

  public static String toString(Set<String> text1, String sep) {
    StringBuffer sb = new StringBuffer();
    for (String s : text1) {
      sb.append(s + sep);
    }

    if (text1.size() > 0) {
      sb.deleteCharAt(sb.length() - 1);
    }
    return sb.toString().trim();
  }

  public static String[] toArray(List<String> list) {
    String[] array = new String[list.size()];
    for (int i = 0; i < list.size(); i++) {
      array[i] = list.get(i);
    }
    return array;
  }

  /**
   * @param integers an array to convert
   * @return convert the given array to a single string in which elements are seperated by a space
   */
  public static String toString(Integer[] integers) {
    StringBuffer sb = new StringBuffer();
    for (Integer i : integers) {
      sb.append(i + " ");
    }
    return sb.toString().trim();
  }

  public static String toString(Double[] doubles) {
    StringBuffer sb = new StringBuffer();
    for (Double d : doubles) {
      sb.append(d + " ");
    }
    return sb.toString().trim();
  }

  public static String toString(Set<String> set) {
    List<String> smellTypeList = new LinkedList<String>(set);
    Collections.sort(smellTypeList); // default: ascending  aa-->ab-->cc
    StringBuffer sb = new StringBuffer();
    for (String s : smellTypeList) {
      sb.append(s + " ");
    }
    return sb.toString().trim();
  }

  public static int getSmellsNumberInFile(String smellVector) {
    int sumSmellsInFile = 0;
    String[] array = smellVector.split(" ");
    for (int i = 0; i < array.length; i++) {
      sumSmellsInFile = sumSmellsInFile + Integer.valueOf(array[i]).intValue();
    }
    return sumSmellsInFile;
  }

  /**
   * @return string
   */
  public static String toString(List<String> strings) {
    //Collections.sort(strings); // default: ascending  aa-->ab-->cc
    StringBuffer sb = new StringBuffer();
    for (String s : strings) {
      sb.append(s + " ");
    }
    return sb.toString().trim();
  }

  public static String getSourceFileMd5(String textContent) {
    /*
     * \r    carriage return [Mac OS]
     * \n    new line [Unix/Linux OS]
     * \r\n  [Windows OS]
     * \t    table
     */
    textContent = textContent.replaceAll("\r|\n|\t", "");
    byte[] bytesRepresentation = null;
    try {
      bytesRepresentation = textContent.getBytes("UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    MessageDigest md = null;
    try {
      md = MessageDigest.getInstance("MD5");// from jdk (in rt.jar)
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }

    byte[] digest = md.digest(bytesRepresentation);
    BigInteger bigInt = new BigInteger(1, digest);
    String hashtext = bigInt.toString(16);

    while (hashtext.length() < 32) {
      hashtext = "0" + hashtext;
    }

    if (hashtext.length() > 32) {
      hashtext = hashtext.substring(0, 32);
    }

    return hashtext;
  }

  /**
   * read lines in a given source file path, excluding blank lines.
   */
  public static List<String> readFile(String filePath) {
    List<String> list = new LinkedList<String>();
    FileInputStream fis;
    try {
      fis = new FileInputStream(filePath);
      InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
      BufferedReader br = new BufferedReader(isr);
      String line = "";
      while ((line = br.readLine()) != null) {
        line = line.trim();
        if (line.length() != 0) {
          list.add(line);
        }
      }
      br.close();
      isr.close();
      fis.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return list;
  }

  /**
   * read lines in a given source file path, including blank lines.
   */
  public static List<String> readText(String sourceFilePath) {
    List<String> result = new LinkedList<String>();
    try {
      FileReader fr = new FileReader(sourceFilePath);
      BufferedReader br = new BufferedReader(fr);
      String line = "";
      while ((line = br.readLine()) != null) {
        result.add(line);
      }
      fr.close();
      br.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }

  public static String removeBlankLine(String sourcecode) {
    StringBuffer strBuffer = new StringBuffer();
    Scanner scanner = new Scanner(sourcecode);
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      line = line.trim(); // remove leading and trailing whitespace
      if (line.isEmpty() != true) // remove blank lines
      {
        strBuffer.append(line + "\n");
      }
    }
    scanner.close();
    return strBuffer.toString();
  }

  /**
   * TODO: BUGFIX  found in 2017-06-08
   */
  public static List<String> getDirectlyNestedDirectories(String projectPath) {
    File file = new File(projectPath);
    List<String> directories = new LinkedList<String>();
    if (file.isDirectory() == true) {
      File[] files = file.listFiles();
      for (File f : files) {
        if (f.isDirectory()) {
          directories.add(f.getAbsolutePath().replace("\\", "/"));
        }
      }
    }
    return directories;
  }

  /**
   * @return LOC including blank lines
   */
  public static int getLOC(String source) {
    int loc = 0;
    String regex = "[\n]";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(source);
    while (matcher.find()) {
      loc++;
    }
    return loc + 1;
  }

  /**
   * @return LOC without including blank lines
   */
  public static int getLOCWithRemovingBlankLines(String source) {
    int count = 0;
    String[] lines = source.split("\n");
    for (String rawLine : lines) {
      if (rawLine.trim().isEmpty() != true) {
        count = count + 1;
      }
    }
    return count;
  }

  /**
   * TODO: BugFix in 2017-06-08
   */
  public static int getLOCWithRemovingCommentsAndBlankLines(String source) {
    String[] lines = source.split("\n");
    List<String> filteredLines = new ArrayList<String>(lines.length);
    for (String rawLine : lines) {
      String line = rawLine.trim();
      if (rawLine.isEmpty()) {
        continue;
      }
      if (line.startsWith("//")) {
        continue;
      }
      if (line.charAt(0) == '*') {
        continue;
      }
      if (line.startsWith("/**")) {
        continue;
      }
      if (line.startsWith("/*")) {
        continue;
      }
      // if encounting the statement: i = i + 1  /* variable i*/
      // this line should be taken into account
      filteredLines.add(line);
    }
    return filteredLines.size();
  }

  /**
   * @param pathOfSourceFile "F:/jEditor0.2/editor/Animal.java" or "F:\\jEditor0.2\\editor\\Animal.java"
   * @return Animal
   */
  public static String getJavaFileName(String pathOfSourceFile) {
    String javaFileName = "";
    File file = new File(pathOfSourceFile);
    if (file.isFile()) {
      javaFileName = file.getName().replace(".java", "");
    }

    // process special case
    if (javaFileName.endsWith(".JAVA")) {
      javaFileName = javaFileName.replace(".JAVA", "");
    }
    return javaFileName;
  }

  public static Comparator<String> getStringComparator() {
    return new Comparator<String>() {
      @Override
      public int compare(String u1, String u2) {
        return u1.compareTo(u2);
      }
    };
  }

  /**
   * @param projectVersionPath = "F:\\corpus\\corpus_ArgoUML\\ArgoUML2.1" or projectVersionPath =
   * "F:\\corpus\\corpus_ArgoUML\\ArgoUML"
   * @return projectName = "ArgoUML"
   */
  public static String getProjectName(String projectVersionPath) {
    String projectNameAndVersionNum = "";
    char[] chArray = projectVersionPath.toCharArray();
    int len = chArray.length;
    int lastindex = 0;
    for (int i = 0; i < len; i++) {
      char c = chArray[len - i - 1];
      if (c == '\\') {
        lastindex = len - i - 1;
        break;
      } else if (c == '/') {
        lastindex = len - i - 1;
        break;
      }
    }

    projectNameAndVersionNum = projectVersionPath.substring(lastindex + 1, len);
    String versionNo = StringUtil.getVersionNumber(projectVersionPath);
    int index = projectNameAndVersionNum.indexOf(versionNo);
    // BUG: why is index equal to 0 ? when versionNo is empty string.
    // I think this will lead to ambiguous semantic. In fact, empty string
    // is a special case, the indexOf() should return -1, rather than 0 when taking
    // empty string as an input
    if (versionNo.length() == 0 || index == -1) {
      return projectNameAndVersionNum;
    } else {
      return projectNameAndVersionNum.substring(0, index);
    }
  }

  /**
   * @param projectVersionPath = "F:\\corpus\log4j2.1.3" or "F:/corpus/log4j2.1.3"
   * @return versionNo = "2.1.3"
   */
  public static String getVersionNumber(String projectVersionPath) {
    String projectNameAndVersionNum = "";
    String versionNo = "";
    char[] chArray = projectVersionPath.toCharArray();
    int len = chArray.length;
    int lastindex = 0;
    for (int i = 0; i < len; i++) {
      char c = chArray[len - i - 1];
      if (c == '\\') {
        lastindex = len - i - 1;
        break;
      } else if (c == '/') {
        lastindex = len - i - 1;
        break;
      }
    }
    projectNameAndVersionNum = projectVersionPath.substring(lastindex + 1, len);
    char[] array = projectNameAndVersionNum.toCharArray();

    final String digitStr = ".0123456789";
    int count = 0;
    int length = array.length;
    for (int i = 0; i < length; i++) {
      String ch = String.valueOf(array[length - i - 1]);
      if (digitStr.contains(ch) != true) {
        count = length - i;
        break;
      }
    }
    if (0 < count && count < projectNameAndVersionNum.length()) {
      versionNo = projectNameAndVersionNum.substring(count, projectNameAndVersionNum.length());
    }
    return versionNo;
  }

  public static String getProjectNameAndVersionNumber(String path) {
    String projectNameAndVersionNum = "";
    char[] chArray = path.toCharArray();
    int len = chArray.length;
    int lastindex = 0;
    for (int i = 0; i < len; i++) {
      char c = chArray[len - i - 1];
      if (c == '\\') {
        lastindex = len - i - 1;
        break;
      } else if (c == '/') {
        lastindex = len - i - 1;
        break;
      }
    }
    projectNameAndVersionNum = path.substring(lastindex + 1, len);
    return projectNameAndVersionNum;
  }

  /**
   * <li> rawName = "a.b.c.A$B$C" or "a.b.c.A$B$C$1" return ""a.b.c.A.B.C"</li>
   * <li> rawName = "org.core.AbstractContext<java.lang.Class<?>>" return
   * "org.core.spi.AbstractContext"</li>
   * <li> rawName = "org.core.AbstractContext[]" return org.core.AbstractContext"</li>
   * <li> rawName = null or "", return empty string</li>
   *
   * <p> NB: anonymous class name is reomved </p>
   *
   * @param rawName a string to input
   * @return the actual fully qualified name of this binding, or the empty string if it has none
   */
  public static String convert(String rawName) {
    if (rawName == null || rawName.length() == 0) {
      return "";
    }

    //case 1: "a.b.c.A$B$C" or "a.b.c.A$B$C$1"
    int index = rawName.lastIndexOf("$");
    if (index != -1) {
      int len = rawName.length();
      String lastStr = rawName.substring(index + 1, len);
      Pattern pattern = Pattern.compile("[0-9]*");
      Matcher isNum = pattern.matcher(lastStr);
      if (isNum.matches()) {// if anonymous class name exists, then remove it.
        rawName = rawName.substring(0, index);
      }
      rawName = rawName.replace('$', '.');
    }

    //case 2: "a.b.c.AbstractContext<java.lang.Class<?>>"
    index = rawName.indexOf("<");
    if (index != -1) {
      rawName = rawName.substring(0, index); // even index equals 0
    }

    //case 3: "org.core.spi.context.AbstractContext[]"
    index = rawName.indexOf("[");
    if (index != -1) {
      rawName = rawName.substring(0, index); // even index equals 0
    }

    return rawName;
  }

  /**
   * all backslashes ("\") within each source file path are replaced by slash ("/")
   *
   * @param versionPath top-level project directory to analyze
   * @param suffix the string that extracted source files ends with
   */
  public static String[] getFilePaths(String versionPath, String suffix) {
    List<String> filePaths = new ArrayList<String>();
    File file = new File(versionPath);
    if (file.isDirectory() == true) {
      readFiles(file, suffix, filePaths);
    }
    String[] array = filePaths.toArray(new String[filePaths.size()]);
    return array;
  }

  private static void readFiles(File file, String suffix, List<String> filePaths) {
    if (file != null) {
      if (file.isDirectory()) {
        File f[] = file.listFiles();
        if (f != null) {
          for (int i = 0; i < f.length; i++) {
            readFiles(f[i], suffix, filePaths);
          }
        }
      } else if (file.getName().endsWith(suffix)) {
        filePaths.add(file.getAbsolutePath().replace("\\", "/"));
      }
    }
  }

  /**
   * Return whether the non-null text arg starts with any of the prefix values.
   *
   * @return boolean
   */
  public static boolean startsWithAny(String text, String... prefixes) {
    for (String prefix : prefixes) {
      if (text.startsWith(prefix)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Returns whether the non-null text arg matches any of the test values.
   *
   * @return boolean
   */
  public static boolean isAnyOf(String text, String... tests) {

    for (String test : tests) {
      if (text.equals(test)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Checks for the existence of any of the listed prefixes on the non-null text and removes them.
   *
   * @return String
   */
  public static String withoutPrefixes(String text, String... prefixes) {
    for (String prefix : prefixes) {
      if (text.startsWith(prefix)) {
        return text.substring(prefix.length());
      }
    }

    return text;
  }

  /**
   * Returns true if the value arg is either null, empty, or full of whitespace characters. More
   * efficient that calling (string).trim().length() == 0
   *
   * @return <code>true</code> if the value is empty, <code>false</code>
   * otherwise.
   */
  public static boolean isEmpty(String value) {

    if (value == null || "".equals(value)) {
      return true;
    }

    for (int i = 0; i < value.length(); i++) {
      if (!Character.isWhitespace(value.charAt(i))) {
        return false;
      }
    }

    return true;
  }

  /**
   * Returns true if both strings are effectively null or whitespace, returns false otherwise if
   * they have actual text that differs.
   *
   * @return boolean
   */
  public static boolean areSemanticEquals(String a, String b) {
    if (a == null) {
      return isEmpty(b);
    }
    if (b == null) {
      return isEmpty(a);
    }

    return a.equals(b);
  }

  /**
   * @param original String
   * @param oldChar char
   * @param newString String
   * @return String
   */
  public static String replaceString(final String original, char oldChar, final String newString) {
    int index = original.indexOf(oldChar);
    if (index == -1) {
      return original;
    } else {
      final String replace = (newString == null ? "" : newString);
      final StringBuilder buf = new StringBuilder(
          Math.max(16, original.length() + replace.length()));
      int last = 0;
      while (index != -1) {
        buf.append(original.substring(last, index));
        buf.append(replace);
        last = index + 1;
        index = original.indexOf(oldChar, last);
      }
      buf.append(original.substring(last));
      return buf.toString();
    }
  }

  /**
   * @param original String
   * @param oldString String
   * @param newString String
   * @return String
   */
  public static String replaceString(final String original, final String oldString,
      final String newString) {
    int index = original.indexOf(oldString);
    if (index < 0) {
      return original;
    } else {
      final String replace = newString == null ? "" : newString;
      final StringBuilder buf = new StringBuilder(
          Math.max(16, original.length() + replace.length()));
      int last = 0;
      while (index != -1) {
        buf.append(original.substring(last, index));
        buf.append(replace);
        last = index + oldString.length();
        index = original.indexOf(oldString, last);
      }
      buf.append(original.substring(last));
      return buf.toString();
    }
  }

  /**
   * Replace some whitespace characters so they are visually apparent.
   *
   * @return String
   */
  public static String escapeWhitespace(Object o) {
    if (o == null) {
      return null;
    }
    String s = String.valueOf(o);
    s = s.replace("\n", "\\n");
    s = s.replace("\r", "\\r");
    s = s.replace("\t", "\\t");
    return s;
  }

  /**
   * Parses the input source using the delimiter specified. This method is much faster than using
   * the StringTokenizer or String.split(char) approach and serves as a replacement for
   * String.split() for JDK1.3 that doesn't have it.
   *
   * FIXME - we're on JDK 1.4 now, can we replace this with String.split?
   *
   * @param source String
   * @param delimiter char
   * @return String[]
   */
  public static String[] substringsOf(String source, char delimiter) {

    if (source == null || source.length() == 0) {
      return EMPTY_STRINGS;
    }

    int delimiterCount = 0;
    int length = source.length();
    char[] chars = source.toCharArray();

    for (int i = 0; i < length; i++) {
      if (chars[i] == delimiter) {
        delimiterCount++;
      }
    }

    if (delimiterCount == 0) {
      return new String[]{source};
    }

    String[] results = new String[delimiterCount + 1];

    int i = 0;
    int offset = 0;

    while (offset <= length) {
      int pos = source.indexOf(delimiter, offset);
      if (pos < 0) {
        pos = length;
      }
      results[i++] = pos == offset ? "" : source.substring(offset, pos);
      offset = pos + 1;
    }

    return results;
  }

  /**
   * Much more efficient than StringTokenizer.
   *
   * @param str String
   * @param separator char
   * @return String[]
   */
  public static String[] substringsOf(String str, String separator) {

    if (str == null || str.length() == 0) {
      return EMPTY_STRINGS;
    }

    int index = str.indexOf(separator);
    if (index == -1) {
      return new String[]{str};
    }

    List<String> list = new ArrayList<>();
    int currPos = 0;
    int len = separator.length();
    while (index != -1) {
      list.add(str.substring(currPos, index));
      currPos = index + len;
      index = str.indexOf(separator, currPos);
    }
    list.add(str.substring(currPos));
    return list.toArray(new String[list.size()]);
  }

  /**
   * Copies the elements returned by the iterator onto the string buffer each delimited by the
   * separator.
   *
   * @param sb StringBuffer
   * @param iter Iterator
   * @param separator String
   */
  public static void asStringOn(StringBuffer sb, Iterator<?> iter, String separator) {

    if (!iter.hasNext()) {
      return;
    }

    sb.append(iter.next());

    while (iter.hasNext()) {
      sb.append(separator);
      sb.append(iter.next());
    }
  }

  /**
   * Copies the array items onto the string builder each delimited by the separator. Does nothing if
   * the array is null or empty.
   *
   * @param sb StringBuilder
   * @param items Object[]
   * @param separator String
   */
  public static void asStringOn(StringBuilder sb, Object[] items, String separator) {

    if (items == null || items.length == 0) {
      return;
    }

    sb.append(items[0]);

    for (int i = 1; i < items.length; i++) {
      sb.append(separator);
      sb.append(items[i]);
    }
  }

  /**
   * Return the length of the shortest string in the array. If the collection is empty or any one of
   * them is null then it returns 0.
   *
   * @param strings String[]
   * @return int
   */
  public static int lengthOfShortestIn(String[] strings) {
		/*if (CollectionUtil.isEmpty(strings)) {
		    return 0;
		}*/

    if (strings.length == 0) {
      return 0;
    }
    int minLength = Integer.MAX_VALUE;

    for (int i = 0; i < strings.length; i++) {
      if (strings[i] == null) {
        return 0;
      }
      minLength = Math.min(minLength, strings[i].length());
    }

    return minLength;
  }

  /**
   * Determine the maximum number of common leading whitespace characters the strings share in the
   * same sequence. Useful for determining how many leading characters can be removed to shift all
   * the text in the strings to the left without misaligning them.
   *
   * @param strings String[]
   * @return int
   */
  public static int maxCommonLeadingWhitespaceForAll(String[] strings) {

    int shortest = lengthOfShortestIn(strings);
    if (shortest == 0) {
      return 0;
    }

    char[] matches = new char[shortest];

    String str;
    for (int m = 0; m < matches.length; m++) {
      matches[m] = strings[0].charAt(m);
      if (!Character.isWhitespace(matches[m])) {
        return m;
      }
      for (int i = 0; i < strings.length; i++) {
        str = strings[i];
        if (str.charAt(m) != matches[m]) {
          return m;
        }
      }
    }

    return shortest;
  }

  /**
   * Trims off the leading characters off the strings up to the trimDepth specified. Returns the
   * same strings if trimDepth = 0
   *
   * @return String[]
   */
  public static String[] trimStartOn(String[] strings, int trimDepth) {

    if (trimDepth == 0) {
      return strings;
    }

    String[] results = new String[strings.length];
    for (int i = 0; i < strings.length; i++) {
      results[i] = strings[i].substring(trimDepth);
    }
    return results;
  }

  /**
   * Left pads a string.
   *
   * @param s The String to pad
   * @param length The desired minimum length of the resulting padded String
   * @return The resulting left padded String
   */
  public static String lpad(String s, int length) {
    String res = s;
    if (length - s.length() > 0) {
      char[] arr = new char[length - s.length()];
      Arrays.fill(arr, ' ');
      res = new StringBuilder(length).append(arr).append(s).toString();
    }
    return res;
  }

  /**
   * Are the two String values the same. The Strings can be optionally trimmed before checking. The
   * Strings can be optionally compared ignoring case. The Strings can be have embedded whitespace
   * standardized before comparing. Two null values are treated as equal.
   *
   * @param s1 The first String.
   * @param s2 The second String.
   * @param trim Indicates if the Strings should be trimmed before comparison.
   * @param ignoreCase Indicates if the case of the Strings should ignored during comparison.
   * @param standardizeWhitespace Indicates if the embedded whitespace should be standardized before
   * comparison.
   * @return <code>true</code> if the Strings are the same, <code>false</code>
   * otherwise.
   */
  public static boolean isSame(String s1, String s2, boolean trim, boolean ignoreCase,
      boolean standardizeWhitespace) {
    if (s1 == null && s2 == null) {
      return true;
    } else if (s1 == null || s2 == null) {
      return false;
    } else {
      if (trim) {
        s1 = s1.trim();
        s2 = s2.trim();
      }
      if (standardizeWhitespace) {
        // Replace all whitespace with a standard single space character.
        s1 = s1.replaceAll("\\s+", " ");
        s2 = s2.replaceAll("\\s+", " ");
      }
      return ignoreCase ? s1.equalsIgnoreCase(s2) : s1.equals(s2);
    }
  }

  /**
   * Formats all items onto a string with separators if more than one exists, return an empty string
   * if the items are null or empty.
   *
   * @param items Object[]
   * @param separator String
   * @return String
   */
  public static String asString(Object[] items, String separator) {

    if (items == null || items.length == 0) {
      return "";
    }
    if (items.length == 1) {
      return items[0].toString();
    }

    StringBuilder sb = new StringBuilder(items[0].toString());
    for (int i = 1; i < items.length; i++) {
      sb.append(separator).append(items[i]);
    }

    return sb.toString();
  }

  /**
   * Returns an empty array of string
   *
   * @return String
   */
  public static String[] getEmptyStrings() {
    return EMPTY_STRINGS;
  }

  public static String[] appendPrefixAndSuffix(String[] array, String prefix, String suffix) {
    String[] result = new String[array.length];
    for (int i = 0; i < array.length; i++) {
      result[i] = prefix + array[i] + suffix;
    }
    return result;
  }

  /**
   * @param str the added element
   * @return new array adding given element in the end
   */
  public static String[] add(String[] array, String str) {
    String[] result = new String[array.length + 1];
    for (int i = 0; i < array.length; i++) {
      result[i] = array[i];
    }
    result[result.length - 1] = str;
    return result;
  }

  public static String[] add(String str, String[] array) {
    String[] result = new String[array.length + 1];
    result[0] = str;
    for (int i = 0; i < array.length; i++) {
      result[i + 1] = array[i];
    }
    return result;
  }

  /**
   * @param source the given string
   * @param targetStrings the target strings
   * @return whether there exit a string in targetStrings as the starting portion of the given line
   */
  public static boolean startWithTargetLines(String source, String[] targetStrings) {
    if (source == null) {
      return false;
    } else {
      boolean occurrence = false;
      for (int i = 0; i < targetStrings.length; i++) {
        if (source.startsWith(targetStrings[i])) {
          occurrence = true;
          break;
        }
      }
      return occurrence;
    }
  }

  public static int firstNumIndexOf(String s) {
    char[] chars = s.toCharArray();
    for (int i = 0; i < chars.length; i++) {
      char ch = chars[i];
      if (ch == '0' || ch == '1' || ch == '2' || ch == '3' || ch == '4' || ch == '5' || ch == '6'
          || ch == '7'
          || ch == '8' || ch == '9') {
        if (i + 1 < chars.length) {
          char next = chars[i + 1];
          if (Character.isLetter(next)) {
            continue;
          }
        }
        return i;
      }
    }
    return -1;
  }

  public static String[] remove(String[] originalArray, String[] arrayToRemove) {
    if (arrayToRemove == null || originalArray == null
        || arrayToRemove.length > originalArray.length) {
      return originalArray;
    } else {
      LinkedList<String> source = new LinkedList<String>(Arrays.asList(originalArray));
      List<String> target = new LinkedList<String>(Arrays.asList(arrayToRemove));
      Iterator<String> iterator = source.iterator();
      while (iterator.hasNext()) {
        String one = iterator.next();
        if (target.contains(one)) {
          iterator.remove();
        }
      }
      return source.toArray(new String[source.size()]);
    }
  }

  public static List<File> readDirectories(File file) {
    List<File> fileDirectories = new ArrayList<File>();
    readDirectories(file, fileDirectories);
    return fileDirectories;
  }

  private static void readDirectories(File file, List<File> fileDirectories) {
    if (file != null && file.isDirectory()) {
      fileDirectories.add(file);
      File[] f = file.listFiles();
      if (f != null) {
        for (int i = 0; i < f.length; i++) {
          readDirectories(f[i], fileDirectories);
        }
      }
    }
  }

  public static String asString(int[] input) {
    String s = "";
    for (int i : input) {
      s = s + " " + i;
    }
    return s.trim();
  }

  public static List<String> splitToWords(String text) {
    List<String> words = new ArrayList<String>();
    BreakIterator breakIterator = BreakIterator.getWordInstance();
    breakIterator.setText(text);
    int lastIndex = breakIterator.first();
    while (BreakIterator.DONE != lastIndex) {
      int firstIndex = lastIndex;
      lastIndex = breakIterator.next();
      if (lastIndex != BreakIterator.DONE && Character.isLetterOrDigit(text.charAt(firstIndex))) {
        words.add(text.substring(firstIndex, lastIndex));
      }
    }

    // another implementation
    // String text = "This is a sample sentence. \n I am a boy.";
		/*String[] wordArrays = text.split("\\s+");
		for (int i = 0; i < wordArrays.length; i++) {
			// You may want to check for a non-word character before blindly performing a replacement. 
			// It may also be necessary to adjust the character class
			wordArrays[i] = wordArrays[i].replaceAll("[^\\w]", "");
			words.add(wordArrays[i]);
		}*/

    return words;
  }

  /**
   * <p> Note that punctuation (e.g., comma and colon) are not filtered out which is not different
   * from method <code>splitToWords()</code> </p>
   *
   * @param source a string to input
   * @return the splitted tokens as array form,
   */
  public static List<String> convertToTokens(String source) {
    //StringTokenizer st = new StringTokenizer("Hello, how\n    **are you   ?", "   ");
    StringTokenizer st = new StringTokenizer(source, "  ");
    List<String> list = new ArrayList<String>();
    while (st.hasMoreTokens()) {
      String name = st.nextToken().trim();
      if (name.isEmpty() == false) {
        list.add(name);
      }
    }
    //return list.toArray(new String[list.size()]);
    return list;
  }

  public static void main(String[] arg1) {
    System.out.println(splitToWords("This is a sample    sentence. \n I am a boy."));
    System.out.println(convertToTokens("This is a sample    sentence. \n I am a boy."));

  }
}
