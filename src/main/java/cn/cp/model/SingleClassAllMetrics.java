package cn.cp.model;

import com.github.mauricioaniche.ck.MetricValue;
import java.io.PrintWriter;

/**
 * SingleClassAllMetrics 表示单一类的所有度量值，ck包中CKNumber类的封装，
 * 提供 返回度量指标名称数组，和度量值数组的方法
 */


public class SingleClassAllMetrics {
  private String className;
  private MetricValue metrics;
  private Boolean ischanged;
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

  /**
   * 将类中的所有度量输出一行
   * @param ps：用于输出的输出流
   * @param spliter :度量数值之间的分隔符
   * @return 返回传入的输出流，方便串联
   */
  PrintWriter println(PrintWriter ps,String spliter){

    if (changeValue != null) {
      Integer[] res = getMetricsVal();
      //StringBuilder sb = new StringBuilder(className);
      StringBuilder sb = new StringBuilder();

      sb.append(res[0]);
      for (int i = 1; i < res.length; i++) {
        sb.append(spliter).append(res[i]);
      }
      //sb.append(spliter).append(changeValue);
      sb.append(spliter).append(ischanged);
      ps.println(sb.toString());
    }
    return ps;
  }
  PrintWriter println(PrintWriter ps){
    return println(ps,",");
  }

  public MetricValue getMetrics() {
    return metrics;
  }
  public String getClassName(){
    return className;
  }

  public void setChange(int val, boolean change) {
    changeValue = val;
    ischanged = change;
  }

  public Object[] getChange() {
    return new Object[]{ischanged, changeValue};
  }
  public static String[] getMetricsName(){
    return "dit,noc,wmc,cbo,lcom,rfc,nom,nopm,nosm,no,nopf,nosf,nosi,loc,ischanged"
        .split(",");
  }
}