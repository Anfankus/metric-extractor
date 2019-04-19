package cn.cp.webserver;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import cn.cp.controller.MetricsExtractor;
import cn.edu.seu.aggregation.ChangeAggregation;
import cn.edu.seu.aggregation.ClassDiffEntity;
import cn.edu.seu.aggregation.MethodDiffEntity;
import java.io.File;
import java.io.FileInputStream;
import java.io.InvalidObjectException;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class MetricJson {
     public Data data=new Data();
     public MetricJson(){

//         Data.VersionInfo.Basic b=new Data.VersionInfo.Basic("loc",11);
//         ArrayList<Data.VersionInfo.Basic> b1=new ArrayList<>();
//         b1.add(b);
//         ArrayList<String > h=new ArrayList<>();
//         h.add("classname");
//         h.add("noc");
//         ArrayList<ArrayList<String >> h1=new ArrayList<>();
//         h1.add(h);
//         data.versionInfo.add(new Data.VersionInfo("junit","4.8",b1,new Data.VersionInfo.Metric(h,h1)));
     }

     public void calculateMetrics(String [] paths) throws Exception {
         MetricsExtractor m = new MetricsExtractor(paths);
         m.doExtract().getMetrics().print2Direcory("tempoutput");
         for(int i=0;)
     }
}

class Data{
    static class VersionInfo{
        static class Metric{
            public ArrayList<String> head;
            public ArrayList<ArrayList<String>> data;
            public Metric(ArrayList<String> h,ArrayList<ArrayList<String>> d){
                this.head=h;
                this.data=d;
            }
        }
        static class Basic{
            public String key;
            public int value;
            public Basic(String k,int v){
                this.key=k;
                this.value=v;
            }
        }
        public String projectName;
        public String version;
        public ArrayList<Basic> basic=new ArrayList<>();
        public Metric metric;
        public VersionInfo(String p,String v,ArrayList<Basic> b,Metric m){
             this.projectName=p;
             this.version=v;
             this.basic=b;
             this.metric=m;
        }
    }
    class Predict{
        class Data_2{
            String classname;
            boolean isChanged;
        }
        public String version;
        public ArrayList<Data_2> data=new ArrayList<>();
        public ArrayList<HashMap<String,String>> modelInfo=new ArrayList<>();
    }
        public ArrayList<VersionInfo> versionInfo=new ArrayList<>();
        public Predict predict=new Predict();
}

