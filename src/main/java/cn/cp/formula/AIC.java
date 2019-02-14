package cn.cp.formula;

import weka.classifiers.functions.Logistic;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.unsupervised.attribute.NumericToNominal;

public class AIC {

  public static double aic = 0;
  public static boolean[] attributes;

  static Logistic trainModel() throws Exception {

    DataSource source = new DataSource(
        "D:\\文档\\Tencent Files\\381108807\\FileRecv\\junit4-r4.11.csv");

    Instances traindata = source.getDataSet();//    ѵ
    traindata.setClassIndex(traindata.numAttributes() - 1);

    NumericToNominal Filter = new NumericToNominal();//     һ   Numericת  ΪNominal
    String options[] = new String[2];
    options[0] = "-R";//û 鵽API  δȷ     ܣ
    options[1] = "18";// Ե 18 н  й
    Filter.setOptions(options);
    Filter.setInputFormat(traindata);
    Instances newInstances = Filter.useFilter(traindata, Filter); //ת        µ ʵ
    newInstances.setClassIndex(newInstances.numAttributes() - 1);//           һ       ( б  Ǵ 0  ʼ)
    System.out.println(newInstances);

    Logistic logic = new Logistic();
    logic.buildClassifier(newInstances);//     ߼  ع ѵ
    return logic;
  }

  ;

  public static void main(String[] args) throws Exception {
    // TODO Auto-generated method stub

    DataSource CheckSource = new DataSource(
        "D:\\文档\\Tencent Files\\381108807\\FileRecv\\junit4-r4.12.csv");//
    Instances CheckData = CheckSource.getDataSet();
    int k = CheckData.numAttributes();
    attributes = new boolean[k];//false就删掉该属性，true为保留属性，默认全为false进行AIC验证。
    attributes[0] = true;//默认保留第一个
    attributes[k - 1] = true;//最后一个当做验证需要保留
    for (int i = 1; i < k - 2; i++) {
      AIC(CheckSource, attributes, i);
      for (int j = 0; j < attributes.length; j++) {
        System.out.print(attributes[j]);
      }
      System.out.println(" ");
    }


  }

  public static void AIC(DataSource data, boolean[] atr, int i) throws Exception//i从1开始
  {
    Instances TrainData = data.getDataSet();
    atr[i] = true;//选取第i个属性进行AIC
    for (int m = atr.length - 1; m > 0; m--) {
      if (atr[m] == false) {
        TrainData.deleteAttributeAt(m);
      }

    }
    int k = TrainData.numAttributes();

    NumericToNominal Filter = new NumericToNominal();
    String options[] = new String[2];
    options[0] = "-R";
    options[1] = String.valueOf(k);
    Filter.setOptions(options);
    Filter.setInputFormat(TrainData);
    Instances newInstances = Filter.useFilter(TrainData, Filter);

    newInstances.setClassIndex(k - 1);//放在最后设置分类属性
    Logistic logic = new Logistic();
    logic.buildClassifier(newInstances);

    int sum = newInstances.numInstances();
    double averageSSR = 0;
    for (int n = 0; n < sum; n++) {
      Instance ins = newInstances.instance(n);
      double val = ins.classValue() - logic.classifyInstance(ins);
      averageSSR += val * val;

    }
    double AICvalue = 2 * k + sum * Math.log(averageSSR / sum);
    System.out.println(aic);
    System.out.println(AICvalue);

    if (aic == 0.0) {
      aic = AICvalue;
      attributes[i] = true;
    }
    if (AICvalue < aic) {
      aic = AICvalue;
      attributes[i] = true;

    } else {
      attributes[i] = false;
    }


  }

}