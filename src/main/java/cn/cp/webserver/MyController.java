package cn.cp.webserver;

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
    String [] paths;

    @PostMapping("/upload")
    public Object upload(@RequestParam("file") MultipartFile fileUpload){
        //获取文件名
        String fileName = fileUpload.getOriginalFilename();
        //指定本地文件夹存储图片
        String filePath = "/Users/lijiaxing/Downloads/junit/";
        try {
            //将图片保存到static文件夹里
            fileUpload.transferTo(new File(filePath+fileName));
           // unZipFiles(new File(filePath+fileName));

            return "success to upload";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail to upload";
        }
    }

    @RequestMapping("/picUpload")
    public String picUpload(){
        return "picUpload";
    }

    @RequestMapping("/launch")
    public MetricJson getM() throws Exception {
        MetricJson m=new MetricJson();
        paths=new String[]{
                "/Users/lijiaxing/Downloads/junit/junit4-r4.11",
                "/Users/lijiaxing/Downloads/junit/junit4-r4.12"
        };
        m.calculateMetrics(paths);
        return m;
    }

}
