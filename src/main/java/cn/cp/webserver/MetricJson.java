package cn.cp.webserver;

import cn.cp.controller.MetricsExtractor;
import cn.cp.model.SingleClassAllMetrics;
import weka.classifiers.Evaluation;

import java.util.*;

public class MetricJson {
    public Data data;

    public MetricJson() {
        data = new Data();
    }

    public void calculateMetrics(String[] paths) throws Exception {
        MetricsExtractor m = new MetricsExtractor(paths);
        Object[] ret=m.doExtract().doPredict();
        Evaluation eval=(Evaluation)ret[0];
        HashMap<String,Boolean>  predicted=(HashMap<String,Boolean>)ret[1];
        ArrayList<Data.Predict.ModelInfo> modelinfos=new ArrayList<>();
        modelinfos.add(new Data.Predict.ModelInfo("正确分类实例",String.valueOf(eval.correct())));
        modelinfos.add(new Data.Predict.ModelInfo("错误分类实例",String.valueOf(eval.incorrect())));
        modelinfos.add(new Data.Predict.ModelInfo("Kappa统计量",String.valueOf((double) Math.round(eval.kappa() * 1000) / 1000)));
        modelinfos.add(new Data.Predict.ModelInfo("平均绝对误差",String.valueOf((double) Math.round(eval.meanAbsoluteError() * 1000) / 1000)));
        modelinfos.add(new Data.Predict.ModelInfo("均方根误差",String.valueOf((double) Math.round(eval.rootMeanSquaredError() * 1000) / 1000)));
        modelinfos.add(new Data.Predict.ModelInfo("相对误差",String.valueOf((double) Math.round(eval.relativeAbsoluteError() * 1000) / 1000)+"%"));
        modelinfos.add(new Data.Predict.ModelInfo("根相对平方误差",String.valueOf((double) Math.round(eval.rootRelativeSquaredError() * 1000) / 1000)+"%"));
        modelinfos.add(new Data.Predict.ModelInfo("实例总数",String.valueOf(eval.numInstances())));
        for (int i = 0; i < paths.length; i++) {
            ArrayList<Data.VersionInfo.Basic> basic = new ArrayList<>();
            basic.add(new Data.VersionInfo.Basic("项目名称",m.getResultCached().getMetrics().get(i).getProjectName()));
            basic.add(new Data.VersionInfo.Basic("版本",m.getResultCached().getMetrics().get(i).getVersion()));
            basic.add(new Data.VersionInfo.Basic("文件名称",m.getResultCached().getMetrics().get(i).getFileName()));
            basic.add(new Data.VersionInfo.Basic("类的数量",String.valueOf(m.getResultCached().getMetrics().get(i).getMetrics().size())));
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
                temp.add(s.getChangeValue()==null?"0":s.getChangeValue().toString());
                temp.add(s.getChangeType()==null?"null": s.getChangeType().toString());
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
        ArrayList<Data.Predict.Data_2> data_2s=new ArrayList<>();
        Iterator iterator1 = predicted.entrySet().iterator();
        while (iterator1.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator1.next();
            String name = (String) entry.getKey();
            Boolean value= (Boolean) entry.getValue();
            data_2s.add(new Data.Predict.Data_2(name,value));
        }
        this.data.predict=new Data.Predict(m.getResultCached().getMetrics().get(1).getVersion(),data_2s,modelinfos);
        int aaa=1;
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
            public String name;
            public String value;

            public Basic(String k, String v) {
                this.name = k;
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

    static class Predict {
        static class Data_2 {
            public String className;
            public boolean isChanged;
            public Data_2(String c,boolean i){
                this.className=c;
                this.isChanged=i;
            }
        }
        static class ModelInfo{
            public String name;
            public String value;
            public ModelInfo(String n,String v){
                this.name=n;
                this.value=v;
            }
        }
        public String version;
        public ArrayList<Data_2> data;
        public ArrayList<ModelInfo> modelInfo ;
        public Predict(String v,ArrayList<Data_2> d,ArrayList<ModelInfo> m){
            this.version=v;
            this.data=d;
            this.modelInfo=m;
        }
    }

    public ArrayList<VersionInfo> versionInfo = new ArrayList<>();
    public Predict predict;
}

