package cn.cp.model;

import cn.cp.formula.VIF;
import com.github.mauricioaniche.ck.MetricReport;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.Data;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

/**
 * SingleVersionMetrics 对于单个版本的所有度量，实质是SingleClassAllMetrics的数组封装
 */
@Data
public class SingleVersionMetrics {

  public String originFilePath;
  private String version;
  private String ProjectName;
  private String fileName;
  private HashMap<String, SingleClassAllMetrics> metrics;
  private MetricReport _source;

  public SingleVersionMetrics(MetricReport re, String path) {
    originFilePath = path;
    fileName = new File(path).getName();
    _source = re;
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


  /**
   * @return 返回这个版本的度量值经过vif过滤之后得到的weka数据集格式，即{@link weka.core.Instances}
   */
  public Instances getWekaData(boolean withChange) {
    ArrayList<Attribute> attrs = new ArrayList<>();
    for (int i = 0; i < SingleClassAllMetrics.getMetricsName().length - 1; i++) {
      attrs.add(new Attribute(SingleClassAllMetrics.getMetricsName()[i]));
    }
    attrs.add(new Attribute("isChanged", Arrays.asList("true", "false")));
    Instances ins = new Instances(getProjectName(), attrs, 0);
    for (SingleClassAllMetrics eachClass : metrics.values()) {
      Integer[] data = eachClass.getMetricsVal();

      Instance currentIns = new DenseInstance(attrs.size());
      for (int i = 0; i < data.length; i++) {
        currentIns.setValue(attrs.get(i), data[i]);
      }
      Attribute at = attrs.get(attrs.size() - 1);
      if (withChange) {
        Object[] obs = eachClass.getChange();
        if (obs[0] == null) {
          continue;
        }
        currentIns.setValue(at, obs[0].toString());
      }
      currentIns.setDataset(ins);
      ins.add(currentIns);
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
  public void printFile(String filepath) throws Exception {
    Instances ins = getWekaData(true);
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
