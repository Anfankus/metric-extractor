package cn.cp.model;

import com.github.mauricioaniche.ck.MetricReport;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * SingleVersionMetrics 对于单个版本的所有度量，实质是SingleClassAllMetrics的数组封装
 */
public class SingleVersionMetrics {

  private String version;
  private String ProjectName;
  private String fileName;
  private HashMap<String, SingleClassAllMetrics> metrics;
  private MetricReport _source;
  public String originFilePath;

  public SingleVersionMetrics(MetricReport re, String path) {
    originFilePath = path;
    fileName = new File(path).getName();
    _source=re;
    metrics = (HashMap<String, SingleClassAllMetrics>) re
        .getCKMetrics()
        .stream()
        .map(each -> new SingleClassAllMetrics(each))
        .collect(Collectors.toMap(SingleClassAllMetrics::getClassName, x -> x));
    Matcher matcher = Pattern.compile("^(\\w+).*?(\\d+\\.\\d+(\\.\\d+)?)$")
        .matcher(fileName);
    matcher.find();
    setProjectName(matcher.group(1));
    setVersion(matcher.group(2));
  }

  /**
   * @return 计算返回该版本各个度量值的总和, key为度量名称，value为度量值总和
   */
  public HashMap<String, Integer> getMetricsSum() {
    HashMap<String, Integer> res = new HashMap<>();
    String[] names = SingleClassAllMetrics.getMetricsName();
    metrics.values()
        .stream()
        .map(x -> x.getMetricsVal())
        .forEach(each -> {
          for (int i = 1; i < each.length - 1; i++) {
            res.put(names[i], res.containsKey(names[i]) ? 0 : res.get(names[i]));
          }
        });
    return res;
  }


  /**
   * 两个版本的版本号进行对比
   *
   * @param b 另一个版本的度量值，
   * @return int 大于则返回正数，小于返回负数，同版本返回0
   */
  public int compareVersion(SingleVersionMetrics b) {
    Pattern reg = Pattern.compile("(\\d+).(\\d+)(.(\\d+))?");
    Matcher m1 = reg.matcher(version);
    Matcher m2 = reg.matcher(b.getVersion());

    m1.find();
    m2.find();
    int groupIndex[] = new int[]{1, 2, 4};
    for (int i : groupIndex) {
      int ver1 = Integer.parseInt(m1.group(i));
      int ver2 = Integer.parseInt(m2.group(i));
      if (ver1 != ver2) {
        return ver1 - ver2;
      }
    }
    return 0;
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

  public Map<String, SingleClassAllMetrics> getMetrics() {
    return metrics;
  }

  public void printFile(String filepath)throws Exception{
    File dst = new File(filepath);
    if (dst.exists()) {
      dst.delete();
      dst.createNewFile();
    }
    PrintWriter pw=new PrintWriter(new FileWriter(filepath,true));

    pw.println(String.join(",", SingleClassAllMetrics.getMetricsName()));
    for (SingleClassAllMetrics each : getMetrics().values()) {
      each.println(pw);
    }
    pw.close();
  }
}
