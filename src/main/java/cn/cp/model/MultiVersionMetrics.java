package cn.cp.model;


import cn.cp.controller.TwoVersComparator;
import gumtree.spoon.diff.Diff;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import weka.classifiers.functions.LinearRegression;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

/**
 * 代表一个项目多个版本的所有度量值，包括计算后得到的各版本各类的度量值，各类的变化率和变化率的预测模型
 */

public class MultiVersionMetrics {

  public List<SingleVersionMetrics> getMetrics() {
    return metrics;
  }

  public void setMetrics(List<SingleVersionMetrics> metrics) {
    this.metrics = metrics;
  }

  private List<SingleVersionMetrics> metrics;
  private HashMap<String, ArrayList<Double>> changeRateCached;

  public MultiVersionMetrics(List<SingleVersionMetrics> source) {
    metrics = source;
    changeRateCached = null;
  }
  /**
   * @return 以类为key，变化率为value的Map
   * @param outputfile 是否输出为文件，输出目录为项目根目录
   *
   * 目前没用
   */
  public HashMap<String, ArrayList<Double>> getChangeRate(boolean outputfile) {
    if (changeRateCached == null) {
      HashMap<String, ArrayList<Double>> res = new HashMap<>();

      //className:metrics
      HashMap<String, SingleClassAllMetrics> valuesForEachClass = new HashMap<>();
      metrics.forEach(eachVersion -> {
        //每个版本
        for (SingleClassAllMetrics eachClass : eachVersion.getMetrics().values()) {
          //单个版本每个类
          if (valuesForEachClass.containsKey(eachClass.getClassName())) {
            SingleClassAllMetrics last = valuesForEachClass.get(eachClass.getClassName());
            Integer[] lastVal = last.getMetricsVal();
            Integer[] currentVal = eachClass.getMetricsVal();

//            double rateSum = 0.0;
//            for (int i = 1; i < lastVal.length; i++) {
//              if(lastVal[i]==0)
//                rateSum+=1;
//              else
//                rateSum += (double)Math.abs(currentVal[i] - lastVal[i])/ lastVal[i];
//            }
//            double avgRate = rateSum / (lastVal.length - 1);


            int changeValSum = 0;
            int lastSum=0;
            for (int i = 1; i < lastVal.length; i++) {
              lastSum+=lastVal[i];
              changeValSum += Math.abs(currentVal[i] - lastVal[i]);
            }
            double avgRate = (double)changeValSum /lastSum;


            if (res.containsKey(eachClass.getClassName())) {
              res.get(eachClass.getClassName()).add(avgRate);
            } else {
              ArrayList<Double> ay = new ArrayList<>();
              ay.add(avgRate);
              res.put(eachClass.getClassName(), ay);
            }
          }
          valuesForEachClass.put(eachClass.getClassName(), eachClass);
        }
      });
      changeRateCached=res;
    }
    if (outputfile) {
      try {
        PrintWriter ps = new PrintWriter("changeRate.csv");
        changeRateCached.forEach((k, v) ->{
          StringBuilder everyRow=new StringBuilder(k);
          v.forEach(each->everyRow.append(',').append(String.format("%.4f",each)));
          ps.println(everyRow);
        });
        ps.close();
      } catch (Exception x) {
        assert false;
      }
    }

    return changeRateCached;
  }

  public HashMap<String, ArrayList<Double>> getChangeRate() {
    return getChangeRate(false);
  }


  /**
   * 两两计算版本变化与否，最后一个版本不计算
   */
  public void getChangeValue() throws Exception {

    //逐版本遍历
    for (int i = 1; i < metrics.size(); i++) {
      SingleVersionMetrics currentVer = getMetrics().get(i - 1),
          behindVer = getMetrics().get(i);

      //提取两版本的变化值
      TwoVersComparator comparator = new TwoVersComparator();
      comparator.compare(new File(currentVer.originFilePath), new File(behindVer.originFilePath));
      HashMap<String, Diff> diffs = comparator.getDiffs();

      //计算中位数
      List<Diff> sortedVal = diffs.values()
          .stream()
          .sorted((a, b) -> a.getRootOperations().size() - b.getRootOperations().size())
          .collect(Collectors.toList());
      int midVal = sortedVal.get(sortedVal.size() / 2).getRootOperations().size();

      //将变更值放入相应的类
      for (Map.Entry<String, Diff> each : diffs.entrySet()) {
        String currentClassName = each.getKey();
        int changeVal = each.getValue().getRootOperations().size();
        SingleClassAllMetrics currentClass = currentVer.getMetrics().get(currentClassName);
        if (currentClass != null) {
          currentClass.setChange(changeVal, changeVal > midVal);
        } else {
          System.out.println("类未找到:" + currentClassName + currentVer.getVersion());
        }
      }//end for
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
