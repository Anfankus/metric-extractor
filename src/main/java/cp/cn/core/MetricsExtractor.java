package cp.cn.core;

import com.github.mauricioaniche.ck.CK;
import cp.cn.model.MultiVersionMetrics;
import cp.cn.model.SingleVersionMetrics;
import java.io.File;
import java.io.InvalidObjectException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
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
   */
  public void doExtract(Consumer<MultiVersionMetrics> s) throws InvalidObjectException {
    if (!checkPaths()) {
      throw new InvalidObjectException("输入路径不全是目录");
    }
    if (resultCached == null) {
      new Thread(() -> {
        MultiVersionMetrics metrics=new MultiVersionMetrics(
            directoryPaths.parallelStream()
            .map(path->new SingleVersionMetrics(new CK().calculate(path),path))
            .sorted((a,b)->a.compareVersion(b))
            .collect(Collectors.toList()) );

        //todo 将计算所得数据进行预测模型
        metrics.getChangeRate();
        metrics.getRegression();
        resultCached=metrics;
        s.accept(resultCached);
      }).start();
    }
    else
      s.accept(resultCached);
  }

  public boolean checkPaths(){
    return directoryPaths.stream().allMatch(path->new File(path).isDirectory());
  }
}
