package cp.cn.core;

import com.github.mauricioaniche.ck.CK;
import cp.cn.model.SingleVersionMetrics;
import java.io.File;
import java.io.InvalidObjectException;
import java.util.function.Consumer;

public class MetricsExtractor {
  private String directoryPath;
  private CK ck;

  public MetricsExtractor(String directoryPath) {
    this.directoryPath = directoryPath;
    ck=null;
  }

  /**
   * 计算度量值时调用，将给定的路径作为待测项目根目录，计算ck值
   * @param s：将计算结果作为参数的异步回调函数
   * @throws InvalidObjectException 输入路径不是一个目录时抛出异常
   */
  public void doExtract(Consumer<SingleVersionMetrics> s) throws InvalidObjectException{
    if(ck==null)
      ck=new CK();
    if(!new File(directoryPath).isDirectory())
      throw new InvalidObjectException("输入路径不是一个目录");


    new Thread(()->{
      SingleVersionMetrics res=new SingleVersionMetrics(ck.calculate(directoryPath));
      //todo 将计算所得数据进行预测模型
      s.accept(res);
    });
  }
}
