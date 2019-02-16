

import cn.cp.controller.MetricsExtractor;

import gumtree.spoon.AstComparator;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Before;
import org.junit.Test;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.SMO;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class TestModel {

  String[] paths;

  @Before
  public void setPaths(){
    paths = new String[]{"E:\\IDEAProject\\demo\\zxing-zxing-3.0.0",
        "E:\\IDEAProject\\demo\\zxing-zxing-3.1.0"};
  }

  /**
   * 从输入的目录中分别计算类的各个度量值，然后在计算两两版本之间的变化值，然后标记变化与否，将结果输出至 tempoutput 目录下 输入多于两个版本的路径，只是会分别两两对比，没有级联
   */
  @Test
  public void testMultiVersions() throws Exception {
    MetricsExtractor m=new MetricsExtractor(paths);
    m.doExtract(x->{
      //x为计算度量完成后的结果
      try {
        x.getMetrics().print2Direcory("tempoutput");
      } catch (Exception ex){
        ex.printStackTrace();
      }
    },true);
  }

  @Test
  public void vif() throws Exception {
    String file1 = "E:\\IDEAProject\\demo\\junit4-r4.6\\src\\main\\java\\org\\junit\\runner\\Description.java";
    String file2 = "E:\\IDEAProject\\demo\\junit4-r4.8\\src\\main\\java\\org\\junit\\runner\\Description.java";

    new AstComparator().compare(new File(file1), new File(file2));

  }

  /**
   * 测试预测模型
   * @throws Exception
   */
  @Test
  public void testClassfier() throws Exception {
    new MetricsExtractor(paths).useSVM("tempoutput/zxing 3.0.0.csv");
  }
}
