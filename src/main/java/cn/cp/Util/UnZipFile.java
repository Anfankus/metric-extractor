package cn.cp.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

//文件解压
public class UnZipFile {

    /**
     *  
     *      * 解压文件到指定目录 
     *      * 解压后的文件名，和之前一致 
     *      * @param zipFile   待解压的zip文件 
     *      
     */
    @SuppressWarnings("rawtypes")
    public static void unZipFile(File zipFile) throws IOException {
        ZipFile zip = new ZipFile(zipFile, Charset.forName("GBK") );//解决中文文件夹乱码  
        String dir=zipFile.getAbsolutePath().substring(0,zipFile.getAbsolutePath().lastIndexOf(File.separator));
        for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements(); ) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            String zipEntryName = entry.getName();
            String sapaEs=File.separator.replaceAll("\\\\","\\\\\\\\");
            String outPath = (dir+File.separator+zipEntryName).replaceAll("/",sapaEs);
            File outFile=new File(outPath);
            if(outPath.endsWith(File.separator)&&!outFile.exists()){
                outFile.mkdirs();
            }else {
                InputStream in = zip.getInputStream(entry);
                FileOutputStream out = new FileOutputStream(outPath);
                byte[] buf1 = new byte[1024];
                int len;
                while ((len = in.read(buf1)) > 0) {
                    out.write(buf1, 0, len);
                }
                in.close();
                out.close();
            }
            // 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压  
//            if (new File(outPath).isDirectory()) {
//                continue;
//            }
            // 输出文件路径信息  
            // System.out.println(outPath);

        }
        zip.close();
        //System.out.println("******************解压完毕********************");
    }

    //测试  
    public static void main(String[] args) {
        try {
            unZipFile(new File("/Users/lijiaxing/Downloads/junitzip/junit4-r4.12.zip"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}


