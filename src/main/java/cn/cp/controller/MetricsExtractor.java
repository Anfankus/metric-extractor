package cn.cp.controller;

import cn.cp.model.MultiVersionMetrics;
import cn.cp.model.SingleVersionMetrics;
import com.github.mauricioaniche.ck.JavaMetricExtractor;
import java.io.File;
import java.io.InvalidObjectException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.SMO;
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

  public void doExtract(Consumer<MetricsExtractor> s) throws InvalidObjectException {
    doExtract(s, false);
  }

  /**
   * 计算度量值调用的方法，将给定的路径作为待测项目根目录，计算ck值
   *
   * @param s 度量计算结束之后调用的回调函数
   * @param wait 为true表示阻塞线程直至度量计算完成，为false表示不等待
   * @throws InvalidObjectException 输入路径不全是目录的时候抛出
   */
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
                .map(
                    path -> new SingleVersionMetrics(new JavaMetricExtractor(path).process(), path))
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


  //测试用，可随意修改
  public SMO useSVM(String file) throws Exception {
    Instances instances = new DataSource(file).getDataSet();

    instances.setClassIndex(instances.numAttributes() - 1);
    SMO smo = new SMO();
    Evaluation eval = new Evaluation(instances);
    eval.crossValidateModel(smo, instances, 10, new Random(2));
    System.out.println(eval.toSummaryString());
    return smo;
  }
  //测试用，可随意修改

  public Logistic useLogistic(String file) throws Exception {
    Instances instances = new DataSource(file).getDataSet();
    instances.setClassIndex(instances.numAttributes() - 1);
    Logistic logic = new Logistic();
    logic.buildClassifier(instances);
    return logic;
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
