package cn.cp.controller;

import cn.cp.model.ChangeType;
import cn.cp.model.MultiVersionMetrics;
import cn.cp.model.SingleClassAllMetrics;
import cn.cp.model.SingleVersionMetrics;
import com.github.mauricioaniche.ck.JavaMetricExtractor;
import java.io.File;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import weka.classifiers.AbstractClassifier;
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

@Getter
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
      metrics.getChangeValue();
      for (String eachClass : metrics.getComparetors().get(0).getAdd()) {
        SingleClassAllMetrics s = metrics.getMetrics().get(1).getMetrics().get(eachClass);
        if (s != null) {
          s.setChangeType(ChangeType.add);
        }
      }

      for (String eachClass : metrics.getComparetors().get(0).getDeleted()) {
        SingleClassAllMetrics s = metrics.getMetrics().get(0).getMetrics().get(eachClass);
        if (s != null) {
          s.setChangeType(ChangeType.deleted);
        } else {
          System.out.println(eachClass);
        }
      }
      resultCached = metrics;
    }
    return this;
  }

  /**
   * 运行预测，如果输入版本数量大于2，使用前两个版本的度量值进行学习器删选，以召回率作为标准选择较优的， 如果输入版本数量等于2，默认使用{@link
   * SMO}作为学习器，如果输入版本数量小于2，丢出异常
   *
   * @return 返回长度为2的数组，其中类型为{ {@link AbstractClassifier}学习器, {@link HashMap<String,Boolean>
   * }<类名，是否变化>}
   * @throws Exception 输入版本数量小于等于1时抛出异常
   */
  public Object[] doPredict() throws Exception {
    if (this.resultCached.getMetrics().size() <= 1) {
      throw new Exception("版本数量不足以预测");
    } else if (this.resultCached.getMetrics().size() == 2) {
      Object[] result = classify(SMO.class, resultCached.getMetrics().get(0).getWekaData(true),
          null);
      AbstractClassifier classifier = (AbstractClassifier) result[0];

      HashMap<String, Boolean> ret = new HashMap<>();
      SingleVersionMetrics metrics = resultCached.getMetrics().get(1);

      List<SingleClassAllMetrics> m = new ArrayList<>(metrics.getMetrics().values());
      Instances ins = metrics.getWekaData(false);
      for (int i = 0; i < ins.size(); i++) {
        double tmp = classifier.classifyInstance(ins.get(i));
        ret.put(m.get(i).getClassName(), tmp < 0.5);
      }
      result[1] = ret;
      return result;
    } else {
    }
    return null;
  }

  /**
   * 使用指定的学习器构建模型
   *
   * @param cls 指定的学习器类
   * @param train 训练集
   * @param test 测试集，如果为空，则输出中不包含模型的评估数据
   * @return 返回结果为长度为2的数组，其中第一个元素为学习器模型{{@link AbstractClassifier}}，对于第二个元素，如果参数test为空，返回
   * null，否则，返回测试集的评估结果{{@link Evaluation}}
   */
  private Object[] classify(Class<? extends AbstractClassifier> cls, Instances train,
      Instances test) {
    try {
      AbstractClassifier classifier = cls.newInstance();
      Object[] result = new Object[2];
      classifier.buildClassifier(train);
      if (test == null) {
        result[1] = null;
      } else {
        Evaluation eval = new Evaluation(train);
        eval.evaluateModel(classifier, test);
        result[1] = eval;
      }
      result[0] = classifier;
      return result;
    } catch (Exception x) {
      x.printStackTrace();
      return null;
    }
  }

  //测试用，可随意修改
  public Map<String, Object> useSVM(InputStream train, InputStream test) throws Exception {
    Map<String, Object> result = new HashMap<String, Object>();
    Instances newTrainSets = new DataSource(train).getDataSet();
    Instances newTestSets = new DataSource(test).getDataSet();
    newTrainSets.setClassIndex(newTrainSets.numAttributes() - 1);
    newTestSets.setClassIndex(newTestSets.numAttributes() - 1);

    SMO smo = new SMO();
    smo.buildClassifier(newTrainSets);
    Evaluation eval = new Evaluation(newTrainSets);
    eval.evaluateModel(smo, newTestSets);
    System.out.println("recall : " + eval.recall(0));
    System.out.println(eval.toSummaryString("Summary:", true));
    System.out.println(eval.toClassDetailsString("Detail:"));
    result.put("recall", eval.recall(0));
    result.put("Prediction", smo);
    return result;
  }

  public Map<String, Object> useJ48(InputStream train, InputStream test) throws Exception {
    Map<String, Object> result = new HashMap<String, Object>();
    Instances newTrainSets = new DataSource(train).getDataSet();
    Instances newTestSets = new DataSource(test).getDataSet();

    newTrainSets.setClassIndex(newTrainSets.numAttributes() - 1);
    newTestSets.setClassIndex(newTestSets.numAttributes() - 1);

    J48 tree = new J48();
    tree.setOptions(new String[]{""});
    tree.buildClassifier(newTrainSets);
    Evaluation eval = new Evaluation(newTrainSets);
    eval.evaluateModel(tree, newTestSets);
    System.out.println("recall : " + eval.recall(0));
    System.out.println(eval.toSummaryString("Summary:", true));
    System.out.println(eval.toClassDetailsString("Detail:"));
    result.put("recall", eval.recall(0));
    result.put("Prediction", tree);
    return result;
  }

  public Map<String, Object> useBayes(InputStream train, InputStream test) throws Exception {
    Map<String, Object> result = new HashMap<String, Object>();
    Instances newTrainSets = new DataSource(train).getDataSet();
    Instances newTestSets = new DataSource(test).getDataSet();

    newTrainSets.setClassIndex(newTrainSets.numAttributes() - 1);
    newTestSets.setClassIndex(newTestSets.numAttributes() - 1);
    NaiveBayes bayes = new NaiveBayes();
    bayes.setOptions(new String[]{"-K"});
    bayes.buildClassifier(newTrainSets);
    Evaluation eval = new Evaluation(newTrainSets);
    eval.evaluateModel(bayes, newTestSets);
    System.out.println("recall : " + eval.recall(0));
    System.out.println(eval.toSummaryString("Summary:", true));
    System.out.println(eval.toClassDetailsString("Detail:"));
    result.put("recall", eval.recall(0));
    result.put("Prediction", bayes);
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
  public Map<String, Object> useLogistic(InputStream train, InputStream test) throws Exception {
    Map<String, Object> result = new HashMap<String, Object>();
    Instances newTrainSets = new DataSource(train).getDataSet();
    Instances newTestSets = new DataSource(test).getDataSet();

    newTrainSets.setClassIndex(newTrainSets.numAttributes() - 1);
    newTestSets.setClassIndex(newTestSets.numAttributes() - 1);

    Logistic logic = new Logistic();
    logic.buildClassifier(newTrainSets);
    Evaluation eval = new Evaluation(newTrainSets);
    eval.evaluateModel(logic, newTestSets);
    System.out.println("recall : " + eval.recall(0));
    System.out.println(eval.toSummaryString("Summary:", true));
    System.out.println(eval.toClassDetailsString("Detail:"));
    result.put("recall", eval.recall(0));
    result.put("Prediction", logic);
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
}
