import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import cn.cp.controller.MetricsExtractor;
import cn.cp.model.SingleClassAllMetrics;
import cn.edu.seu.aggregation.ChangeAggregation;
import cn.edu.seu.aggregation.ClassDiffEntity;
import cn.edu.seu.aggregation.MethodDiffEntity;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
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
    junits = new String[]{
        "/Users/lijiaxing/Downloads/junit/junit4-r4.8",
        "/Users/lijiaxing/Downloads/junit/junit4-r4.9",
        "/Users/lijiaxing/Downloads/junit/junit4-r4.10",
        "/Users/lijiaxing/Downloads/junit/junit4-r4.11",
        "/Users/lijiaxing/Downloads/junit/junit4-r4.12"
    };

    paths = junits;
  }

  /**
   * 从输入的目录中分别计算类的各个度量值，然后在计算两两版本之间的变化值，然后标记变化与否，将结果输出至 tempoutput 目录下 输入多于两个版本的路径，只是会分别两两对比，没有级联
   */
  @Test
  public void calculateMetric() throws Exception {
    MetricsExtractor m = new MetricsExtractor(paths);
    m.doExtract().getResultCached().print2Direcory("tempoutput");
    for(int i=0;i<paths.length;i++){
      String[] head={"className","type","changeValue","changeType","dit","noc","wmc","cbo","cbo",
              "lcom","rfc","nom","nopm","nosm","nof","nopf","nosf","nosi","loc"};
      ArrayList<ArrayList<String>> data_2=new ArrayList<>();
      Iterator iterator=m.getResultCached().getMetrics().get(i).getMetrics().entrySet().iterator();
      while(iterator.hasNext()){
        Map.Entry entry=(Map.Entry) iterator.next();
        SingleClassAllMetrics s= (SingleClassAllMetrics) entry.getValue();
        ArrayList<String> temp=new ArrayList<>();
        temp.add((String) entry.getKey());
        temp.add(s.getMetrics().getType());

      }
    }
    int a=0;
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

    if(Double.parseDouble(result.get("recall").toString())<Double.parseDouble(result1.get("recall").toString())) {
      result=result2;
    }

    Map<String,Object> result3 =new MetricsExtractor(paths).useJ48(new FileInputStream("C:\\Users\\Thinkpad\\Desktop\\变更预测\\Metrics-master0.2\\Metrics-master\\src\\test\\resources\\zxing 3.0.0.arff"),
        new FileInputStream("C:\\Users\\Thinkpad\\Desktop\\变更预测\\Metrics-master0.2\\Metrics-master\\src\\test\\resources\\zxing 3.1.0.arff"));

    if(Double.parseDouble(result.get("recall").toString())<Double.parseDouble(result3.get("recall").toString())) {
      result=result3;
    }
    Map<String,Object> result4 =new MetricsExtractor(paths).useLogistic(new FileInputStream("C:\\Users\\Thinkpad\\Desktop\\变更预测\\Metrics-master0.2\\Metrics-master\\src\\test\\resources\\zxing 3.0.0.arff"),
        new FileInputStream("C:\\Users\\Thinkpad\\Desktop\\变更预测\\Metrics-master0.2\\Metrics-master\\src\\test\\resources\\zxing 3.1.0.arff"));

    if(Double.parseDouble(result.get("recall").toString())<Double.parseDouble(result4.get("recall").toString())) {
      result=result4;
    }
    System.out.println("final recall : "+result.get("recall"));


  }

  @Test
  public void changeDistiller() throws Exception {
    String leftPath = "E:\\IDEAProject\\demo\\ZXing\\zxing-zxing-3.0.0\\android\\src\\com\\google\\zxing\\client\\android\\camera\\FrontLightMode.java";
    String rightPath = "E:\\IDEAProject\\demo\\ZXing\\zxing-zxing-3.1.0\\android\\src\\com\\google\\zxing\\client\\android\\camera\\FrontLightMode.java";

    //leftPath = "F:/corpus/camel/camel#2010-05-23-03-22-49#camel-2.3.0#8a471f4/camel-core/src/main/java/org/apache/camel/processor/MulticastProcessor.java";
    //rightPath = "F:/corpus/camel/camel#2011-01-25-05-49-08#camel-2.6.0#20f2898/camel-core/src/main/java/org/apache/camel/processor/MulticastProcessor.java";

    File leftFile = new File(leftPath);
    File rightFile = new File(rightPath);

    System.out.println("left file:" + leftFile.getAbsolutePath());
    System.out.println("right file:" + rightFile.getAbsolutePath() + "\n");

    FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);

    try {
      distiller.extractClassifiedSourceCodeChanges(leftFile, "1.9", rightFile, "1.9");
    } catch (Exception e) {
      System.err.println("Warning: error while change distilling. " + e.getMessage());
    }

    //List<SourceCodeChange> sccList = distiller.getSourceCodeChanges();

    List<ClassDiffEntity> classDiffEntities = ChangeAggregation
        .doClassAggregation(distiller.getSourceCodeChanges2(), leftPath, rightPath);

    for (ClassDiffEntity classDE : classDiffEntities) {
      System.out.println("----class change----");
      classDE.print();

      for (MethodDiffEntity methodDE : classDE.listMethodDiffEntity) {
        System.out.println("----method change----");
        methodDE.print();
      }
    }
  }
}
