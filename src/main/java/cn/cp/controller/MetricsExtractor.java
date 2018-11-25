package cn.cp.controller;

import cn.cp.model.MultiVersionMetrics;
import com.github.mauricioaniche.ck.CK;
import cn.cp.model.SingleVersionMetrics;
import java.io.File;
import java.io.InvalidObjectException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
   * @param s
   */
  public void doExtract(Consumer<MultiVersionMetrics> s) throws InvalidObjectException {
    doExtract(s, false);
  }

  public void doExtract(Consumer<MultiVersionMetrics> s, boolean wait)
      throws InvalidObjectException {
    if (!checkPaths()) {
      throw new InvalidObjectException("输入路径不全是目录");
    }
    if (resultCached == null) {
      Thread t=new Thread(() -> {
        //提取每个类度量值
        MultiVersionMetrics metrics=new MultiVersionMetrics(
            directoryPaths.parallelStream()
            .map(path->new SingleVersionMetrics(new CK().calculate(path),path))
            .sorted((a,b)->a.compareVersion(b))
                .collect(Collectors.toList())
        );

        //TODO 补全代码
        //计算两两版本变化
        try {
          for (int i = 1; i < directoryPaths.size(); i++) {
            TwoVersComparator comparator = new TwoVersComparator();
            comparator
                .compare(new File(directoryPaths.get(i - 1)), new File(directoryPaths.get(i)));
            comparator.getDiffs();

          }
        } catch (Exception x) {
          System.out.println("版本对比异常");
        }

        metrics.getChangeRate(true);
        //metrics.getRegression();
        resultCached=metrics;
        s.accept(resultCached);
      });
      t.start();
      if(wait)
        try{
          t.join();
        }
        catch (Exception x){
          x.printStackTrace();
        }
    }
    else
      s.accept(resultCached);
  }


  public boolean checkPaths(){
    return directoryPaths.stream().allMatch(path->new File(path).isDirectory());
  }
}
