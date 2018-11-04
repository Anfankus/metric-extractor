

import cp.cn.core.MetricsExtractor;
import cp.cn.model.MultiVersionMetrics;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Before;
import org.junit.Test;

public class TestModel {
  String[] paths;

  @Before
  public void setPaths(){
    paths=new String[]{"D:\\360安全浏览器下载\\junit4-r4.6",
        "D:\\360安全浏览器下载\\junit4-r4.8",
        "D:\\360安全浏览器下载\\junit4-r4.9",
        "D:\\360安全浏览器下载\\junit4-r4.10",
        "D:\\360安全浏览器下载\\junit4-r4.11",
        "D:\\360安全浏览器下载\\junit4-r4.12"};
  }

  @Test
  public void testMultiVersions()throws Exception{
    MetricsExtractor m=new MetricsExtractor(paths);
    m.doExtract(x->{

    },true);
  }

  @Test
  public void t1(){
    String a1="^(\\w+).*?(\\d+\\.\\d+(\\.\\d+)?)$";
    Matcher x=Pattern.compile(a1).matcher("pro1-3.4.5");
    x.find();
    System.out.println(x);
  }
}
