import cn.cp.controller.MetricsExtractor;
import gumtree.spoon.AstComparator;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class TestModel {

  String[] paths;
  @Before
  public void setPaths() {
    String[] zxings = new String[]{
        "E:\\IDEAProject\\demo\\ZXing\\zxing-zxing-3.0.0",
        "E:\\IDEAProject\\demo\\ZXing\\zxing-zxing-3.1.0",
        "E:\\IDEAProject\\demo\\ZXing\\zxing-zxing-3.2.0",
        "E:\\IDEAProject\\demo\\ZXing\\zxing-zxing-3.3.0",
        "E:\\IDEAProject\\demo\\ZXing\\zxing-zxing-3.3.1"
    };
    String[] junits = new String[]{
        "C:\\Users\\Thinkpad\\Desktop\\变更预测\\JCM\\junit4-r3.8.2",
        "C:\\Users\\Thinkpad\\Desktop\\变更预测\\JCM\\junit4-r4.9b3"
    };

    paths = junits;
  }

  /**
   * 从输入的目录中分别计算类的各个度量值，然后在计算两两版本之间的变化值，然后标记变化与否，将结果输出至 tempoutput 目录下 输入多于两个版本的路径，只是会分别两两对比，没有级联
   */
  @Test
  public void calculateMetric() throws Exception {
    MetricsExtractor m = new MetricsExtractor(paths);
    m.doExtract(x -> {
      //x为计算度量完成后的结果
      try {
        //获取所有度量值并保存至目录
        x.getMetrics().print2Direcory("tempoutput");
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    });
  }

  /**
   * 测试预测模型
   */
  @Test
  public void classify() throws Exception {
//    InputStream train = TestModel.class.getResourceAsStream("/zxing 3.0.0.arff");
//    InputStream test = TestModel.class.getResourceAsStream("/zxing 3.1.0.arff");
//    new MetricsExtractor(paths).useJ48(train,test);
    Map<String,Object> result;
    Map<String,Object> result1 =new MetricsExtractor(paths).useSVM(new FileInputStream("C:\\Users\\Thinkpad\\Desktop\\变更预测\\Metrics-master0.2\\Metrics-master\\src\\test\\resources\\zxing 3.0.0.arff"),
            new FileInputStream("C:\\Users\\Thinkpad\\Desktop\\变更预测\\Metrics-master0.2\\Metrics-master\\src\\test\\resources\\zxing 3.1.0.arff"));

    result=result1;
    Map<String,Object> result2 =new MetricsExtractor(paths).useBayes(new FileInputStream("C:\\Users\\Thinkpad\\Desktop\\变更预测\\Metrics-master0.2\\Metrics-master\\src\\test\\resources\\zxing 3.0.0.arff"),
            new FileInputStream("C:\\Users\\Thinkpad\\Desktop\\变更预测\\Metrics-master0.2\\Metrics-master\\src\\test\\resources\\zxing 3.1.0.arff"));

    if(Double.parseDouble(result.get("recall").toString())<Double.parseDouble(result1.get("recall").toString()))
    {
      result=result2;
    }

    Map<String,Object> result3 =new MetricsExtractor(paths).useJ48(new FileInputStream("C:\\Users\\Thinkpad\\Desktop\\变更预测\\Metrics-master0.2\\Metrics-master\\src\\test\\resources\\zxing 3.0.0.arff"),
            new FileInputStream("C:\\Users\\Thinkpad\\Desktop\\变更预测\\Metrics-master0.2\\Metrics-master\\src\\test\\resources\\zxing 3.1.0.arff"));

    if(Double.parseDouble(result.get("recall").toString())<Double.parseDouble(result3.get("recall").toString()))
    {
      result=result3;
    }
    Map<String,Object> result4 =new MetricsExtractor(paths).useLogistic(new FileInputStream("C:\\Users\\Thinkpad\\Desktop\\变更预测\\Metrics-master0.2\\Metrics-master\\src\\test\\resources\\zxing 3.0.0.arff"),
            new FileInputStream("C:\\Users\\Thinkpad\\Desktop\\变更预测\\Metrics-master0.2\\Metrics-master\\src\\test\\resources\\zxing 3.1.0.arff"));

    if(Double.parseDouble(result.get("recall").toString())<Double.parseDouble(result4.get("recall").toString()))
    {
      result=result4;
    }
    System.out.println("final recall : "+result.get("recall"));



  }

  @Test
  public void gumtree() throws Exception {
    String file1 = "E:\\IDEAProject\\demo\\JUnit\\junit4-r4.6\\src\\main\\java\\org\\junit\\runner\\Description.java";
    String file2 = "E:\\IDEAProject\\demo\\JUnit\\junit4-r4.8\\src\\main\\java\\org\\junit\\runner\\Description.java";

    new AstComparator().compare(new File(file1), new File(file2));

  }
}
