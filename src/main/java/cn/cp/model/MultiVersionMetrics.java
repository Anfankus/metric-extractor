package cn.cp.model;


import cn.cp.controller.TwoVersComparator;
import cn.edu.seu.aggregation.ClassDiffEntity;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.Data;

/**
 * 代表一个项目多个版本的所有度量值，包括计算后得到的各版本各类的度量值，各类的变化率和变化率的预测模型
 */
@Data
public class MultiVersionMetrics {

  private List<SingleVersionMetrics> metrics;
  private List<TwoVersComparator> comparetors;

  public MultiVersionMetrics(List<SingleVersionMetrics> source) {
    metrics = source;
    comparetors = new ArrayList<>();
  }
  /**
   * 两两计算版本变化与否，最后一个版本不计算
   */
  public void getChangeValue() {

    //逐版本遍历
    for (int i = 0; i < metrics.size() - 1; i++) {
      SingleVersionMetrics currentVer = getMetrics().get(i),
          behindVer = getMetrics().get(i + 1);

      //提取两版本的变化值
      TwoVersComparator comparator = new TwoVersComparator();
      this.comparetors.add(comparator);
      comparator.compare(new File(currentVer.originFilePath), new File(behindVer.originFilePath));
      HashMap<ClassDiffEntity, Integer> diffs = comparator.getDiffs();

      //计算中位数
      List<Entry<ClassDiffEntity, Integer>> sortedVal = diffs.entrySet()
          .stream()
          .sorted(Comparator.comparingInt(Entry::getValue))
          .collect(Collectors.toList());
      int midVal = sortedVal.get(sortedVal.size() / 2).getValue();

      //将变更值放入相应的类
      for (Map.Entry<ClassDiffEntity, Integer> each : diffs.entrySet()) {
        String currentClassName = each.getKey().newFullClassName;
        int changeVal = each.getValue();
        SingleClassAllMetrics currentClass = currentVer.getMetrics().get(currentClassName);
        if (currentClass != null) {
          currentClass.setChange(changeVal, changeVal > midVal);
        } else {
          System.out.println("类未找到:" + currentClassName + currentVer.getVersion());
        }
      }//end for

      //因为 diffs 里面没有未变化的类，这一部分把未变化的类也放进相应的SingleClassAllMetrics对象里
      for (String className : comparator.getUnchanged()) {
        SingleClassAllMetrics currentClass = currentVer.getMetrics().get(className);
        if (currentClass != null) {
          currentClass.setChange(0, false);
        } else {
          System.out.println("类未找到:" + className + currentVer.getVersion());
        }
      }

    }//end for
  }
  public void print2Direcory(String rootPath)throws Exception{
    File f=new File(rootPath);
    if(!(f.exists()&&f.isDirectory()))
      f.mkdir();
    for(SingleVersionMetrics eachVer:metrics){
      String path =
          rootPath + "/" + eachVer.getProjectName() + " " + eachVer.getVersion() + ".arff";
      eachVer.printFile(path);
    }
  }

}
