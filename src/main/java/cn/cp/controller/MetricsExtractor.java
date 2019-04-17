package cn.cp.controller;

import cn.cp.model.MultiVersionMetrics;
import cn.cp.model.SingleVersionMetrics;
import com.github.mauricioaniche.ck.JavaMetricExtractor;
import java.io.File;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.util.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

/**
 * 为进行度量计算而实例化的类，其他模块在计算度量值时实际用到的类
 */


public class MetricsExtractor {


  private List<String> directoryPaths;
  private MultiVersionMetrics resultCached;

  public MetricsExtractor(String[] directoryPaths) {
    this.directoryPaths = Arrays.asList(directoryPaths);
  }

  /**
   * 计算度量值调用的方法，将给定的路径作为待测项目根目录，计算ck值
   *
   * @throws InvalidObjectException 输入路径不全是目录的时候抛出
   */
  public MetricsExtractor doExtract() throws InvalidObjectException {
    if (!checkPaths()) {
      throw new InvalidObjectException("输入路径不全是目录");
    }
    if (resultCached == null) {
      //提取所有版本度量值
      MultiVersionMetrics metrics = new MultiVersionMetrics(
          directoryPaths.stream()
              .map(
                  path -> new SingleVersionMetrics(new JavaMetricExtractor(path).process(), path))
              .sorted((a, b) -> a.compareVersion(b))
              .collect(Collectors.toList())
      );
      try {
        metrics.getChangeValue();
      } catch (Exception x) {
        x.printStackTrace();
      }
      resultCached = metrics;
    }
    return this;
  }


  //测试用，可随意修改
  public Map<String,Object> useSVM(InputStream train, InputStream test) throws Exception {
    Map<String,Object> result = new HashMap<String,Object>();
    Instances newTrainSets = new DataSource(train).getDataSet();
    Instances newTestSets = new DataSource(test).getDataSet();
    newTrainSets.setClassIndex(newTrainSets.numAttributes() - 1);
    newTestSets.setClassIndex(newTestSets.numAttributes() - 1);

    SMO smo = new SMO();
    smo.buildClassifier(newTrainSets);
    Evaluation eval = new Evaluation(newTrainSets);
    eval.evaluateModel(smo, newTestSets);
    System.out.println("recall : "+eval.recall(0));
    System.out.println(eval.toSummaryString("Summary:", true));
    System.out.println(eval.toClassDetailsString("Detail:"));
    result.put("recall",eval.recall(0));
    result.put("Prediction",smo);
    return result;
  }

  public Map<String,Object> useJ48(InputStream train, InputStream test) throws Exception {
    Map<String,Object> result = new HashMap<String,Object>();
    Instances newTrainSets = new DataSource(train).getDataSet();
    Instances newTestSets = new DataSource(test).getDataSet();

    newTrainSets.setClassIndex(newTrainSets.numAttributes() - 1);
    newTestSets.setClassIndex(newTestSets.numAttributes() - 1);

    J48 tree = new J48();
    tree.setOptions(new String[]{""});
    tree.buildClassifier(newTrainSets);
    Evaluation eval = new Evaluation(newTrainSets);
    eval.evaluateModel(tree, newTestSets);
    System.out.println("recall : "+eval.recall(0));
    System.out.println(eval.toSummaryString("Summary:", true));
    System.out.println(eval.toClassDetailsString("Detail:"));
    result.put("recall",eval.recall(0));
    result.put("Prediction",tree);
    return result;
  }

  public Map<String,Object> useBayes(InputStream train, InputStream test) throws Exception {
    Map<String,Object> result = new HashMap<String,Object>();
    Instances newTrainSets = new DataSource(train).getDataSet();
    Instances newTestSets = new DataSource(test).getDataSet();

    newTrainSets.setClassIndex(newTrainSets.numAttributes() - 1);
    newTestSets.setClassIndex(newTestSets.numAttributes() - 1);
    NaiveBayes bayes = new NaiveBayes();
    bayes.setOptions(new String[]{"-K"});
    bayes.buildClassifier(newTrainSets);
    Evaluation eval = new Evaluation(newTrainSets);
    eval.evaluateModel(bayes, newTestSets);
    System.out.println("recall : "+eval.recall(0));
    System.out.println(eval.toSummaryString("Summary:", true));
    System.out.println(eval.toClassDetailsString("Detail:"));
    result.put("recall",eval.recall(0));
    result.put("Prediction",bayes);
    return result;
  }

  //测试用，可随意修改
//  public Logistic useLogistic(InputStream file) throws Exception {
//    Instances instances = new DataSource(file).getDataSet();
//    instances.setClassIndex(instances.numAttributes() - 1);
//    Logistic logic = new Logistic();
//    logic.buildClassifier(instances);
//    return logic;
//  }
  public Map<String,Object> useLogistic(InputStream train, InputStream test) throws Exception {
    Map<String,Object> result = new HashMap<String,Object>();
    Instances newTrainSets = new DataSource(train).getDataSet();
    Instances newTestSets = new DataSource(test).getDataSet();

    newTrainSets.setClassIndex(newTrainSets.numAttributes() - 1);
    newTestSets.setClassIndex(newTestSets.numAttributes() - 1);

    Logistic logic = new Logistic();
    logic.buildClassifier(newTrainSets);
    Evaluation eval = new Evaluation(newTrainSets);
    eval.evaluateModel(logic, newTestSets);
    System.out.println("recall : "+eval.recall(0));
    System.out.println(eval.toSummaryString("Summary:", true));
    System.out.println(eval.toClassDetailsString("Detail:"));
    result.put("recall",eval.recall(0));
    result.put("Prediction",logic);
    return result;
  }

  /**
   * 用来检查输入的路径是否都是文件夹
   *
   * @return {@code true} 均为文件夹
   */
  public boolean checkPaths() {
    return directoryPaths.stream().allMatch(path -> new File(path).isDirectory());
  }

  public MultiVersionMetrics getMetrics() {
    return resultCached;
  }
}
