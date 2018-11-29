

import cn.cp.controller.MetricsExtractor;

import gumtree.spoon.AstComparator;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Before;
import org.junit.Test;
import weka.classifiers.functions.Logistic;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class TestModel {
  String[] paths;

  @Before
  public void setPaths(){
    paths=new String[]{"E:\\IDEAProject\\demo\\junit4-r4.12",
        "E:\\IDEAProject\\demo\\junit4-r4.11",
        "E:\\IDEAProject\\demo\\junit4-r4.10",
        "E:\\IDEAProject\\demo\\junit4-r4.9",
        "E:\\IDEAProject\\demo\\junit4-r4.8",
        "E:\\IDEAProject\\demo\\junit4-r4.6"};
  }

  @Test
  public void testMultiVersions()throws Exception{
    MetricsExtractor m=new MetricsExtractor(paths);
    m.doExtract(x->{
      try{
        x.getMetrics().print2Direcory("tempoutput");

        Logistic logistic = x.getRegression("tempoutput/junit4 4.6.csv");


      }
      catch (Exception ex){
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

  @Test
  public void modelTest() throws Exception {
    MetricsExtractor x = new MetricsExtractor(new String[]{});
    Logistic logic = x.getRegression("tempoutput/junit4 4.8.csv");
    Instances test = new DataSource("tempoutput/junit4 4.10.csv").getDataSet();
    test.setClassIndex(test.numAttributes() - 1);
    int a = 0;
    int b = 0;
    for (Instance i : test) {
      if (logic.classifyInstance(i) == i.classValue()) {
        a++;
      }
      b++;
    }

    System.out.println(a * 1.0 / b);

  }
}
