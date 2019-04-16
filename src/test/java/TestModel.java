import cn.cp.controller.MetricsExtractor;
import gumtree.spoon.AstComparator;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
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
        "E:\\IDEAProject\\demo\\JUnit\\junit4-r4.6",
        "E:\\IDEAProject\\demo\\JUnit\\junit4-r4.8",
        "E:\\IDEAProject\\demo\\JUnit\\junit4-r4.9",
        "E:\\IDEAProject\\demo\\JUnit\\junit4-r4.10"
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

    new MetricsExtractor(paths).useBayes(new FileInputStream("tempoutput/zxing 3.0.0.arff"),
        new FileInputStream("tempoutput/zxing 3.1.0.arff"));
  }

  @Test
  public void gumtree() throws Exception {
    String file1 = "E:\\IDEAProject\\demo\\JUnit\\junit4-r4.6\\src\\main\\java\\org\\junit\\runner\\Description.java";
    String file2 = "E:\\IDEAProject\\demo\\JUnit\\junit4-r4.8\\src\\main\\java\\org\\junit\\runner\\Description.java";

    new AstComparator().compare(new File(file1), new File(file2));

  }
}
