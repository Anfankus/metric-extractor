package cn.cp.webserver;

import java.util.Arrays;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import static cn.cp.Util.UnZipFile.unZipFiles;

/**
 * 测试控制器
 *
 * @author: @Leejiaxing
 * @create: 2018-05-08-下午 16:46
 */
@RestController
public class MyController implements Serializable {
    String [] paths=new String[2];
    int count=0;
    @PostMapping("/upload")
    public Boolean upload(@RequestParam("file") MultipartFile fileUpload){
        //获取文件名
        String fileName = fileUpload.getOriginalFilename();
        //指定本地文件夹存储图片
        String filePath = System.getProperty("user.home")+"/Downloads/";
        File f=new File(filePath+fileName);
        try {
            //将图片保存到static文件夹里
            fileUpload.transferTo(f);
            unZipFiles(f);
            paths[count]=(filePath+fileName).substring(0,(filePath+fileName).lastIndexOf('.'));
            count++;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @RequestMapping("/picUpload")
    public String picUpload(){
        return "picUpload";
    }

    @RequestMapping("/launch")
    public MetricJson getM() throws Exception {
      System.out.println(Arrays.toString(paths));
        MetricJson m=new MetricJson();
//        paths=new String[]{
//                "/Users/lijiaxing/Downloads/junit/junit4-r4.11",
//                "/Users/lijiaxing/Downloads/junit/junit4-r4.12"
//        };
        m.calculateMetrics(paths);
        count=0;
        return m;
    }

}
