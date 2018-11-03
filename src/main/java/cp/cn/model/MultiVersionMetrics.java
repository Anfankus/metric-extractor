package cp.cn.model;


import java.util.HashMap;
import java.util.List;
import weka.core.matrix.LinearRegression;

/**
 * 代表一个项目多个版本的所有度量值，包括计算后得到的各版本各类的度量值，各类的变化率和变化率的预测模型
 */
public class MultiVersionMetrics {

  private List<SingleVersionMetrics> metrics;
  private HashMap<String,Double[]> changeRateCached;
  private LinearRegression regressionCached;

  public MultiVersionMetrics(List<SingleVersionMetrics> source){
    metrics=source;
    changeRateCached=null;
    regressionCached=null;
  }

  /**
   * TODO 将 metrics 中的度量值，计算每个类在多个版本过程中的变化率（结合此类所有度量值）
   * @return 以类为key，变化率为value的Map
   */
  public HashMap<String,Double[]> getChangeRate(){
    if(changeRateCached ==null){
      HashMap<String,Double> res=new HashMap<>();



    }
    return changeRateCached;
  }

  /**
   * TODO 使用 changeRateCached 以计算得到的数据生成线性模型
   * @return 计算完成的线性模型
   */
  public LinearRegression getRegression(){
    if(regressionCached==null){
      HashMap<String,Double[]> changeRate=getChangeRate();

    }
    return regressionCached;
  }

}
