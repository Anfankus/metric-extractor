package cn.cp.webserver;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import cn.cp.controller.MetricsExtractor;
import cn.cp.model.SingleClassAllMetrics;
import weka.classifiers.AbstractClassifier;

import java.util.*;

public class MetricJson {
    public Data data = new Data();

    public MetricJson() {

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

    public void calculateMetrics(String[] paths) throws Exception {
        MetricsExtractor m = new MetricsExtractor(paths);
        Object[] ret=m.doExtract().doPredict();
//        AbstractClassifier classifier=(AbstractClassifier)ret[0];
//        HashMap<String,Boolean>  predicted=(HashMap<String,Boolean>)ret[1];
        for (int i = 0; i < paths.length; i++) {
            ArrayList<Data.VersionInfo.Basic> basic = new ArrayList<>();
            basic.add(new Data.VersionInfo.Basic("项目名称",m.getResultCached().getMetrics().get(i).getProjectName()));

            String[] head = {"className", "type", "changeValue", "changeType", "dit", "noc", "wmc", "cbo",
                    "lcom", "rfc", "nom", "nopm", "nosm", "nof", "nopf", "nosf", "nosi", "loc"};
            ArrayList<ArrayList<String>> data_2 = new ArrayList<>();ArrayList<Data.VersionInfo.Basic> basics=new ArrayList<>();
            Iterator iterator = m.getResultCached().getMetrics().get(i).getMetrics().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                SingleClassAllMetrics s = (SingleClassAllMetrics) entry.getValue();
                ArrayList<String> temp = new ArrayList<>();
                temp.add((String) entry.getKey());
                temp.add(s.getMetrics().getType());
                    if (s.getChangeType() == null || s.getChangeValue() == null) {
                        temp.add("0");
                        temp.add("null");
                    } else {
                        temp.add(s.getChangeValue().toString());
                        temp.add(s.getChangeType().toString());
                    }

                temp.add(String.valueOf(s.getMetrics().getDit()));
                temp.add(String.valueOf(s.getMetrics().getNoc()));
                temp.add(String.valueOf(s.getMetrics().getWmc()));
                temp.add(String.valueOf(s.getMetrics().getCbo()));
                temp.add(String.valueOf(s.getMetrics().getLcom()));
                temp.add(String.valueOf(s.getMetrics().getRfc()));
                temp.add(String.valueOf(s.getMetrics().getNom()));
                temp.add(String.valueOf(s.getMetrics().getNopm()));
                temp.add(String.valueOf(s.getMetrics().getNosm()));
                temp.add(String.valueOf(s.getMetrics().getNof()));
                temp.add(String.valueOf(s.getMetrics().getNopf()));
                temp.add(String.valueOf(s.getMetrics().getNosf()));
                temp.add(String.valueOf(s.getMetrics().getNosi()));
                temp.add(String.valueOf(s.getMetrics().getLoc()));
                data_2.add(temp);
            }


            this.data.versionInfo.add(new Data.VersionInfo(m.getResultCached().getMetrics().get(i).getProjectName(),
                    m.getResultCached().getMetrics().get(i).getVersion(), basic,
                    new Data.VersionInfo.Metric(head, data_2)));
        }
    }
}

class Data {
    static class VersionInfo {
        static class Metric {
            public String[] head;
            public ArrayList<ArrayList<String>> data;

            public Metric(String[] h, ArrayList<ArrayList<String>> d) {
                this.head = h;
                this.data = d;
            }
        }

        static class Basic {
            public String key;
            public String value;

            public Basic(String k, String v) {
                this.key = k;
                this.value = v;
            }
        }

        public String projectName;
        public String version;
        public ArrayList<Basic> basic = new ArrayList<>();
        public Metric metric;

        public VersionInfo(String p, String v, ArrayList<Basic> b, Metric m) {
            this.projectName = p;
            this.version = v;
            this.basic = b;
            this.metric = m;
        }
    }

    class Predict {
        class Data_2 {
            String classname;
            boolean isChanged;
        }
        public String version;
        public ArrayList<Data_2> data = new ArrayList<>();
        public ArrayList<HashMap<String, String>> modelInfo = new ArrayList<>();
        public Predict(String v,ArrayList<Data_2> d,ArrayList<HashMap<String,String>> m){
            this.version=v;
            this.data=d;
            this.modelInfo=m;
        }
    }

    public ArrayList<VersionInfo> versionInfo = new ArrayList<>();
    public Predict predict;
}

