package cn.cp.controller;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.ast.ASTHelperFactory;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaASTHelper;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import cn.edu.seu.aggregation.ChangeAggregation;
import cn.edu.seu.aggregation.ClassDiffEntity;
import com.github.mauricioaniche.ck.FileUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TwoVersComparator {

  private HashMap<ClassDiffEntity, Integer> diffs;
  private List<String> unchanged; //类名
  private List<String> add;       //文件名
  private List<String> deleted;   //文件名

  public TwoVersComparator() {
    super();
    add = new ArrayList<>();
    deleted = new ArrayList<>();
    unchanged = new ArrayList<>();
    diffs = new HashMap<>();
  }

  public static void main(String[] args) throws Exception {
    String path1 = "E:\\IDEAProject\\demo\\ZXing\\zxing-zxing-3.0.0";
    String path2 = "E:\\IDEAProject\\demo\\ZXing\\zxing-zxing-3.1.0";
    File file1 = new File(path1);
    File file2 = new File(path2);

    TwoVersComparator comparator = new TwoVersComparator();
    comparator.compare(file1, file2);
    HashMap<ClassDiffEntity, Integer> map = comparator.getDiffs();

    int i = 1;
    for (Map.Entry<ClassDiffEntity, Integer> each : map.entrySet()) {
      System.out.println((i++) + each.getKey().newFullClassName + ":" + each.getValue());
    }
    System.out.println("----------------------------------");
    System.out.println("add:" + comparator.getAdd());
    System.out.println("delete:" + comparator.getDeleted());
  }

  public HashMap<ClassDiffEntity, Integer> getDiffs() {
    return diffs;
  }

  public List<String> getAdd() {
    return add;
  }

  public List<String> getDeleted() {
    return deleted;
  }

  public List<String> getUnchanged() {
    return unchanged;
  }

  public void compare(File f1, File f2) throws Exception {
    List<String> list1 = Arrays.asList(FileUtil.getAllJavaFiles(f1.getAbsolutePath()));
    list1 = list1.stream()
        .map(each -> each.substring(f1.getAbsolutePath().length())).collect(Collectors.toList());
    List<String> list2 = Arrays.asList(FileUtil.getAllJavaFiles(f2.getAbsolutePath()));
    list2 = list2.stream()
        .map(each -> each.substring(f2.getAbsolutePath().length())).collect(Collectors.toList());

    List<String> both = new ArrayList<>(list1);
    both.retainAll(list2);
    List<String> all = new ArrayList<>(list1);
    all.removeAll(list2);

    deleted.addAll(all);
    all.addAll(list2);

    add.addAll(list2);
    add.removeAll(list1);

    for (String fileName : both) {
      File leftFile = new File(f1.getAbsolutePath() + fileName);
      File rightFile = new File(f2.getAbsolutePath() + fileName);
      FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
      distiller.extractClassifiedSourceCodeChanges(leftFile, "1.8", rightFile, "1.8");

      List<ClassDiffEntity> classDiffEntities = ChangeAggregation.doClassAggregation(
          distiller.getSourceCodeChanges2(),
          leftFile.getAbsolutePath(),
          rightFile.getAbsolutePath());

      int flag = 0;
      for (ClassDiffEntity e : classDiffEntities) {
        int sum = 0;
        flag++;
        for (SourceCodeChange eachChange : e.allSourceCodeChanges) {
          sum += eachChange.getChangeType().getSignificance().value();
        }
        this.diffs.put(e, sum);
      }
      if (flag == 0) {
        this.unchanged.addAll(distiller.getNewVerClassName());
      }
    }
  }

}
