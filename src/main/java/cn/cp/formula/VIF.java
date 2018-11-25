package cn.cp.formula;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import weka.classifiers.evaluation.RegressionAnalysis;
import weka.classifiers.functions.LinearRegression;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class VIF {

  private Instances instances = null;
  private int attrIndex = -1;
  private boolean verbose = false;

  public VIF(String fileName) throws IOException {
    BufferedReader rdr = new BufferedReader(new FileReader(fileName));
    instances = new Instances(rdr);
  }

  public VIF(Instances instances) {
    this.instances = instances;
  }

  public VIF(String fileName, int attrIndex) throws IOException {
    this(fileName);
    this.attrIndex = attrIndex;
  }

  public VIF(Instances instances, int attrIndex) {
    this.instances = instances;
    this.attrIndex = attrIndex;
  }

  public static void main(String[] args) {
    String fileName = "C:\\Users\\38110\\Desktop\\datasets\\regression-datasets\\regression-datasets\\fried.arff";
    int attrIndex = -1;
    VIF vif = null;
    boolean verbose = false;

    for (int i = 0; i < args.length; i++) {
      switch (args[i]) {
        case "-f":
          fileName = args[++i];
          i++;
          break;
        case "-v":
          verbose = true;
          break;
        default:
          attrIndex = new Integer(args[i]);
          break;
      }
    }
    try {
      if (attrIndex != -1) {
        vif = new VIF(fileName, attrIndex);
      } else {
        vif = new VIF(fileName);
      }
    } catch (IOException ioex) {
      ioex.printStackTrace();
    }
    if (verbose) {
      vif.setVerbose(verbose);
    }
    try {
      double[] result = vif.getVIFs();
      System.out.println(Arrays.toString(result));
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public double[] getVIFs() throws Exception {
    return calculateVIF(instances, attrIndex);

  }

  public double[] calculateVIF(Instances instances, int attrIndex) throws Exception {

    Remove rm = new Remove();
    rm.setAttributeIndices("last");
    rm.setInputFormat(instances);
    instances = Filter.useFilter(instances, rm);

    int n = instances.numAttributes();
    System.out.println("n = " + n);
    double[] vifs = new double[n];
    if (verbose) {
      System.out.println("Relation: " + instances.relationName());
    }
    if (attrIndex == -1) {
      for (int i = 0; i < vifs.length; i++) {
        instances.setClassIndex(i);
        AccessibleLinearRegression regressor = new AccessibleLinearRegression();
        regressor.setAttributeSelectionMethod(new SelectedTag(1, LinearRegression.TAGS_SELECTION));
        regressor.setEliminateColinearAttributes(false);
        regressor.buildClassifier(instances);
        double r2 = regressor.getRSquared(instances);
        vifs[i] = 1d / (1d - r2);
        if (verbose) {
          System.out.println(i + "\t" + instances.attribute(i).name() + "\t" + vifs[i]);
        }
      }
    } else {
    }
    return vifs;
  }

  public void setVerbose(boolean verbose) {
    this.verbose = verbose;
  }
}

class AccessibleLinearRegression extends LinearRegression {

  public double getRSquared(Instances data) throws Exception {
    // calculate R^2
    double se = calculateSE(data, m_SelectedAttributes, m_Coefficients);
    return RegressionAnalysis.calculateRSquared(data, se);
  }

  public double calculateSE(Instances data, boolean[] selectedAttributes,
      double[] coefficients) throws Exception {
    double mse = 0;
    for (int i = 0; i < data.numInstances(); i++) {
      double prediction =
          regressionPrediction(data.instance(i), selectedAttributes,
              coefficients);
      double error = prediction - data.instance(i).classValue();
      mse += error * error;
    }
    return mse;
  }
}