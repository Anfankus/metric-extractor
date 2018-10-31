package cp.cn.model;

import com.github.mauricioaniche.ck.CKReport;
import java.util.Arrays;
import java.util.HashMap;

/**
 * SingleVersionMetrics 对于单个版本的所有度量，实质是SingleClassAllMetrics的数组封装
 */
public class SingleVersionMetrics {
  private String version;
  private String ProjectName;
  private SingleClassAllMetrics[] metrics;

  public SingleVersionMetrics(SingleClassAllMetrics[] m){
    metrics=m;
  }
  public SingleVersionMetrics(CKReport re){
    metrics=(SingleClassAllMetrics[])re.all().toArray();
  }

  /**
   * @return 计算返回该版本各个度量值的总和,key为度量名称，value为度量值总和
   */
  public HashMap<String,Integer> getMetricsSum(){
    HashMap<String,Integer> res=new HashMap<>();
    String[] names=SingleClassAllMetrics.getMetricsName();
    Arrays.stream(metrics)
        .map(x->x.getMetricsVal())
        .forEach(each->{
          for(int i=0;i<each.length;i++){
            res.put(names[i],res.containsKey(names[i])?0:res.get(names[i]));
          }
        });
    return res;
  }


  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getProjectName() {
    return ProjectName;
  }

  public void setProjectName(String projectName) {
    ProjectName = projectName;
  }

  public SingleClassAllMetrics[] getMetrics() {
    return metrics;
  }


}
