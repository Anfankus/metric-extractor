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
        String fileName=zip.getName().substring(zip.getName().lastIndexOf(File.separator)+1,zip.getName().lastIndexOf("."));
        String dir=zipFile.getAbsolutePath().substring(0,zipFile.getAbsolutePath().lastIndexOf("."));
        new File(dir).mkdir();
        for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements(); ) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            String zipEntryName = entry.getName();
            String sapaEs=File.separator.replaceAll("\\\\","\\\\\\\\");
            if(zipEntryName.startsWith(fileName)){
                zipEntryName=zipEntryName.substring(fileName.length()+1);
            }
            String outPath = (dir+File.separator+zipEntryName).replaceAll("/",sapaEs);
            File outFile=new File(outPath);
            if(outPath.endsWith(File.separator)){
                if(!outFile.exists())outFile.mkdirs();
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
    public static  boolean deleteDir(File dir){
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
    //测试  
    public static void main(String[] args) {
        try {
            deleteDir(new File("C:\\Users\\abckek123\\Downloads\\metrics\\junit4-r4.9"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}


