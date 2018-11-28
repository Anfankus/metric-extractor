package cn.cp.controller;

import cn.cp.model.MultiVersionMetrics;
import cn.cp.model.SingleVersionMetrics;
import com.github.mauricioaniche.ck.CK;
import java.io.File;
import java.io.InvalidObjectException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import weka.classifiers.functions.Logistic;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

/**
 * 为进行度量计算而实例化的类，其他模块在计算度量值时实际用到的类
 */
public class MetricsExtractor {


  private List<String> directoryPaths;
  private MultiVersionMetrics resultCached;

  private Executor pool;//暂时无用

  public MetricsExtractor(String[] directoryPaths) {
    this.directoryPaths = Arrays.asList(directoryPaths);
    //pool= Executors.newCachedThreadPool();
  }

  /**
   * 计算度量值调用的方法，将给定的路径作为待测项目根目录，计算ck值
   *
   * @throws InvalidObjectException 输入路径不是一个目录时抛出异常
   */
  public void doExtract(Consumer<MetricsExtractor> s) throws InvalidObjectException {
    doExtract(s, false);
  }

  public void doExtract(Consumer<MetricsExtractor> s, boolean wait)
      throws InvalidObjectException {
    if (!checkPaths()) {
      throw new InvalidObjectException("输入路径不全是目录");
    }
    if (resultCached == null) {
      Thread t = new Thread(() -> {
        //提取所有版本度量值
        MultiVersionMetrics metrics = new MultiVersionMetrics(
            directoryPaths.stream()
                .map(path -> new SingleVersionMetrics(new CK().calculate(path), path))
                .sorted((a, b) -> a.compareVersion(b))
                .collect(Collectors.toList())
        );
        try {
          metrics.getChangeValue();
        } catch (Exception x) {
          System.out.println("版本对比异常" + x);
        }
        resultCached = metrics;
        s.accept(this);
      });
      t.start();
      if (wait) {
        try {
          t.join();
        } catch (Exception x) {
          x.printStackTrace();
        }
      }


    } else {
      s.accept(this);
    }
  }

  public Logistic getRegression(String file) throws Exception {

    Instances instances = new DataSource(file).getDataSet();
    instances.setClassIndex(instances.numAttributes() - 1);
    Logistic logic = new Logistic();
    logic.buildClassifier(instances);

    /*
     * 测试用
     */
    Instances test = new DataSource("tempoutput/junit4 4.11.csv").getDataSet();
    test.setClassIndex(test.numAttributes() - 1);
    /*

     */
    int a = 0;
    int b = 0;
    for (Instance i : test) {
      if (logic.classifyInstance(i) == i.classValue()) {
        a++;
      }
      b++;
    }

    System.out.println(a * 1.0 / b);
    return logic;
  }


  public boolean checkPaths() {
    return directoryPaths.stream().allMatch(path -> new File(path).isDirectory());
  }

  public MultiVersionMetrics getMetrics() {
    return resultCached;
  }
}
