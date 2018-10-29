package cp.cn.model;

import com.github.mauricioaniche.ck.CKNumber;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SingleClassAllMetrics {
  private CKNumber metrics;
  public SingleClassAllMetrics(CKNumber source){
    metrics=source;
  }
  public String[] getMetricsName(){
    return "dit,noc,wmc,cbo,lcom,rfc,nom,nopm,nosm,no,nopf,nosf,nosi,loc".split(",");
  }

  public CKNumber getMetrics() {
    return metrics;
  }

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
}
