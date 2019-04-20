package cn.cp.controller;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.ast.ASTHelper;
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
import java.util.stream.Collectors;
import lombok.Data;

@Data
public class TwoVersComparator {

  private HashMap<String, Integer> diffs = new HashMap<>();
  private List<String> unchanged = new ArrayList<>(); //类名
  private List<String> add = new ArrayList<>();       //文件名
  private List<String> deleted = new ArrayList<>();   //文件名


  public void compare(File f1, File f2) {
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

    List<String> tempDel = new ArrayList<>(all);
    all.addAll(list2);

    List<String> tempAdd = new ArrayList<>(list2);
    tempAdd.removeAll(list1);

    for (String each : tempAdd) {
      FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
      ASTHelper helper = distiller.getFactory()
          .create(new File(f2.getAbsolutePath() + each), "1.8");
      add.addAll(helper.getClassName());
    }

    for (String each : tempDel) {
      FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
      ASTHelper helper = distiller.getFactory()
          .create(new File(f1.getAbsolutePath() + each), "1.8");
      deleted.addAll(helper.getClassName());
    }

    for (String fileName : both) {
      File leftFile = new File(f1.getAbsolutePath() + fileName);
      File rightFile = new File(f2.getAbsolutePath() + fileName);
      FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
      distiller.extractClassifiedSourceCodeChanges(leftFile, "1.8", rightFile, "1.8");

      List<ClassDiffEntity> classDiffEntities = ChangeAggregation.doClassAggregation(
          distiller.getSourceCodeChanges2(),
          leftFile.getAbsolutePath(),
          rightFile.getAbsolutePath());

      if (classDiffEntities.isEmpty()) {
        this.unchanged.addAll(distiller.getNewVerClassName());
      } else {
        for (ClassDiffEntity e : classDiffEntities) {
          int sum = 0;
          for (SourceCodeChange eachChange : e.allSourceCodeChanges) {
            sum += eachChange.getChangeType().getSignificance().value();
          }
          this.diffs.put(e.newFullClassName, sum);
        }
      }
    }
  }

}
