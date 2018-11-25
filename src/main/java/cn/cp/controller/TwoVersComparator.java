package cn.cp.controller;

import gumtree.spoon.AstComparator;
import gumtree.spoon.diff.Diff;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spoon.reflect.declaration.CtType;

public class TwoVersComparator extends AstComparator {

  private HashMap<String, Diff> diffs;
  private ArrayList<String> add;
  private ArrayList<String> deleted;

  public TwoVersComparator() {
    super();
    add = new ArrayList<>();
    deleted = new ArrayList<>();
    diffs = new HashMap<String, Diff>();
  }

  public HashMap<String, Diff> getDiffs() {
    return diffs;
  }

  public ArrayList<String> getAdd() {
    return add;
  }

  public ArrayList<String> getDeleted() {
    return deleted;
  }

  @Override
  public void compare(File f1, File f2) throws Exception {
    List<CtType<?>> list1 = getCtType(f1);
    List<CtType<?>> list2 = getCtType(f2);

    for (CtType ctTypeLeft : list1) {
      CtType toMatch = null;
      for (CtType ctTypeRight : list2) {
        if (ctTypeLeft.getQualifiedName().equals(ctTypeRight.getQualifiedName())) {
          toMatch = ctTypeRight;
          diffs.put(ctTypeRight.getQualifiedName(), this.compare(ctTypeLeft, ctTypeRight));
          break;
        }
      }

      if (toMatch == null) { // indicates deleted class in old version
        deleted.add(ctTypeLeft.getQualifiedName());
      }
    }

    for (CtType ctTypeRight : list2) {
      CtType toMatch = null;
      for (CtType ctTypeLeft : list1) {
        if (ctTypeRight.getQualifiedName().equals(ctTypeLeft.getQualifiedName())) {
          toMatch = ctTypeLeft;
          break;
        }
      }
      if (toMatch == null) { // indicates added class in new version
        add.add(ctTypeRight.getQualifiedName());

      }
    }
  }

  public static void main(String[] args) throws Exception {
    String path1 = "E:\\IDEAProject\\demo\\junit4-r4.6";
    String path2 = "E:\\IDEAProject\\demo\\junit4-r4.8";
    File file1 = new File(path1);
    File file2 = new File(path2);

    TwoVersComparator comparator = new TwoVersComparator();
    comparator.compare(file1, file2);
    HashMap<String, Diff> map = comparator.getDiffs();

    for (Map.Entry<String, Diff> each : map.entrySet()) {
      System.out.println(each.getKey() + ":" + each.getValue().getRootOperations().size());
    }
    System.out.println("----------------------------------");
    System.out.println("add:" + comparator.getAdd());
    System.out.println("delete:" + comparator.getDeleted());
  }

}
