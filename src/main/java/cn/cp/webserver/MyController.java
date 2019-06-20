package cn.cp.webserver;

import java.util.Arrays;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.Serializable;

import static cn.cp.Util.UnZipFile.deleteDir;
import static cn.cp.Util.UnZipFile.unZipFile;

/**
 * 测试控制器
 *
 * @author: @Leejiaxing
 * @create: 2018-05-08-下午 16:46
 */
@RestController
public class MyController implements Serializable {
    final String savePath=System.getProperty("user.home")+File.separator+"Downloads"+File.separator+"metrics";

    String [] paths=new String[2];
    int count=0;

    @PostMapping("/upload")
    public Boolean upload(@RequestParam("file") MultipartFile fileUpload){
        //获取文件名
        String uploadedFileName=fileUpload.getOriginalFilename();
        String saveFileName=savePath+File.separator+uploadedFileName;
        File saveFile=new File(saveFileName);
        File saveFileDir=new File(savePath);
        if(!saveFileDir.exists()||!saveFileDir.isDirectory()){
            saveFileDir.mkdir();
        }

        try {
            fileUpload.transferTo(saveFile);
            unZipFile(saveFile);
            saveFile.delete();
            paths[count]=saveFileName.substring(0,saveFileName.lastIndexOf('.'));
            count++;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @RequestMapping("/launch")
    public MetricJson getM() throws Exception {
      System.out.println(Arrays.toString(paths));
        MetricJson m=new MetricJson();
        m.calculateMetrics(paths);
        paths=new String[2];
        count=0;
        return m;
    }

}
