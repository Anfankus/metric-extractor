package com.github.mauricioaniche.ck;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;


public class FileUtil {

  public static String[] getAllDirs(String path) {
    ArrayList<String> dirs = new ArrayList<String>();
    getAllDirs(path, dirs);

    String[] ar = new String[dirs.size()];
    ar = dirs.toArray(ar);
    return ar;
  }

  private static void getAllDirs(String path, ArrayList<String> dirs) {

    File f = new File(path);
    if (f.getName().equals(".git")) {
      return;
    }

    for (File inside : f.listFiles()) {
      if (inside.isDirectory()) {
        String newDir = inside.getAbsolutePath();
        dirs.add(newDir);
        getAllDirs(newDir, dirs);
      }
    }
  }

  public static String[] getAllJarFiles(String path) {
    ArrayList<String> files = new ArrayList<String>();
    getAllFiles(path, files, ".jar");

    String[] ar = new String[files.size()];
    ar = files.toArray(ar);
    return ar;
  }

  public static String[] getAllJavaFiles(String path) {
    ArrayList<String> files = new ArrayList<String>();
    getAllFiles(path, files, ".java");

    String[] ar = new String[files.size()];
    ar = files.toArray(ar);
    return ar;
  }

  private static void getAllFiles(String path, ArrayList<String> files, String suffix) {
    File f = new File(path);
    if (f.getName().equals(".git")) {
      return;
    }
    if (suffix.equals(".java")
        &&
        Pattern.compile("(test)|(ui)", Pattern.CASE_INSENSITIVE).matcher(path).find()) {
      return;
    }

    for (File inside : f.listFiles()) {
      if (inside.isDirectory()) {
        String newDir = inside.getAbsolutePath();
        getAllFiles(newDir, files, suffix);
      } else if (inside.getAbsolutePath().toLowerCase().endsWith(suffix)) {
        files.add(inside.getAbsolutePath());
      }
    }
  }


}
