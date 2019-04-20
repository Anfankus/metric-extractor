package cn.cp.model;

import com.github.mauricioaniche.ck.MetricValue;
import lombok.Data;

/**
 * SingleClassAllMetrics 表示单一类的所有度量值，ck包中CKNumber类的封装，
 * 提供 返回度量指标名称数组，和度量值数组的方法
 */
@Data
public class SingleClassAllMetrics {
  private String className;
  private MetricValue metrics;
  private Boolean ischanged;
  private ChangeType changeType;
  private Integer changeValue;

  public SingleClassAllMetrics(MetricValue source) {
    metrics=source;
    className = metrics.getFullyQualifiedClassName();
    if (className.equalsIgnoreCase("org.junit.AfterClass")) {
      System.out.println();
    }
  }

  /**
   * 获取度量值数值
   * @return 14个度量值的数组
   */
  public Integer[] getMetricsVal(){
    return new Integer[]{
        metrics.getDit(),
        metrics.getNoc(),
        metrics.getWmc(),
        metrics.getCbo(),
        metrics.getLcom(),
        metrics.getRfc(),
        metrics.getNom(),

        metrics.getNopm(),
        metrics.getNosm(),
        metrics.getNof(),
        metrics.getNopf(),
        metrics.getNosf(),
        metrics.getNosi(),
        metrics.getLoc()
    };
  }
  public void setChange(int val, boolean change) {
    changeValue = val;
    ischanged = change;
    changeType = ChangeType.updated;
  }
  public Object[] getChange() {
    return new Object[]{ischanged, changeValue};
  }
  public static String[] getMetricsName(){
    return "dit,noc,wmc,cbo,lcom,rfc,nom,nopm,nosm,no,nopf,nosf,nosi,loc,ischanged"
        .split(",");
  }
}
