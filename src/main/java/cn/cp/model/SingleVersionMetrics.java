package cn.cp.model;

import cn.cp.formula.VIF;
import com.github.mauricioaniche.ck.MetricReport;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

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


  /**
   * @return 返回这个版本的度量值经过vif过滤之后得到的weka数据集格式，即{@link weka.core.Instances}
   */
  public Instances getWekaData() {
    ArrayList<Attribute> attrs = new ArrayList<>();
    for (int i = 0; i < SingleClassAllMetrics.getMetricsName().length - 1; i++) {
      attrs.add(new Attribute(SingleClassAllMetrics.getMetricsName()[i]));
    }
    attrs.add(new Attribute("isChanged", Arrays.asList("true", "false")));
    Instances ins = new Instances(getProjectName(), attrs, 0);
    for (SingleClassAllMetrics eachClass : getMetrics().values()) {
      Integer[] data = eachClass.getMetricsVal();

      Instance currentIns = new DenseInstance(attrs.size());
      for (int i = 0; i < attrs.size() - 1; i++) {
        currentIns.setValue(attrs.get(i), data[i]);
      }
      Attribute at = attrs.get(attrs.size() - 1);
      Object[] obs = eachClass.getChange();
      if (obs[0] != null) {
        currentIns.setValue(at, obs[0].toString());
        currentIns.setDataset(ins);
        ins.add(currentIns);
      }
    }
    ins.setClassIndex(ins.numAttributes() - 1);
    return ins;
  }

  /**
   * 把当前度量值输出为一个.arff文件
   *
   * @param filepath 以{@code .arff}结尾的文件名，若文件已存在则会覆盖此文件
   * @throws Exception 抛出IO异常{@link java.io.IOException}
   */
  public void printFile(String filepath)throws Exception{
    Instances ins = getWekaData();
    if (ins.size() <= 0) {
      return;
    }
    VIF vif = new VIF(ins);
    double[] result = vif.getVIFs();
    for (int index = 0, i = 1; index < result.length; index++) {
      if (result[index] > 10) {
        Remove remove = new Remove();
        remove.setOptions(new String[]{"-R", i + ""});
        remove.setInputFormat(ins);
        ins = Filter.useFilter(ins, remove);
      } else {
        i++;
      }
    }
    File dst = new File(filepath);
    if (dst.exists()) {
      dst.delete();
    }
    ArffSaver saver = new ArffSaver();
    saver.setInstances(ins);
    saver.setFile(dst);
    saver.writeBatch();
  }
}
