package cn.cp.webserver;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 测试控制器
 *
 * @author: @Leejiaxing
 * @create: 2018-05-08-下午 16:46
 */
@RestController
public class MyController {
    String [] paths;
    @RequestMapping("/hello")
    public String hello() {
        return "Hello Spring Bootcddddddd!";
    }

    @RequestMapping("/picUpload")
    public String picUpload(){
        return "index";
    }

    @RequestMapping("/launch")
    public MetricJson getM() throws Exception {
        MetricJson m=new MetricJson();
        paths=new String[]{
            "E:\\IDEAProject\\demo\\ZXing\\zxing-zxing-3.0.0",
            "E:\\IDEAProject\\demo\\ZXing\\zxing-zxing-3.1.0",
        };
        m.calculateMetrics(paths);
//        Data.VersionInfo.Basic b=new Data.VersionInfo.Basic("loc",11);
//        ArrayList<Data.VersionInfo.Basic> b1=new ArrayList<>();
//        b1.add(b);
//        String [] h={"className","type","changeValue","changeType","dit","noc","wmc","cbo","cbo",
//                "lcom","rfc","nom","nopm","nosm","nof","nopf","nosf","nosi","loc"};
//        ArrayList<ArrayList<String >> h1=new ArrayList<>();
//        m.data.versionInfo.add(new Data.VersionInfo("junit","4.8",b1,new Data.VersionInfo.Metric(h,h1)));

        return m;
    }

}

