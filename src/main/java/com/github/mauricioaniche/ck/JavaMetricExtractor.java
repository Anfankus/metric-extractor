package com.github.mauricioaniche.ck;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.github.mauricioaniche.metric.CBO;
import com.github.mauricioaniche.metric.DIT;
import com.github.mauricioaniche.metric.LCOM;
import com.github.mauricioaniche.metric.NOC;
import com.github.mauricioaniche.metric.NOF;
import com.github.mauricioaniche.metric.NOM;
import com.github.mauricioaniche.metric.NOPF;
import com.github.mauricioaniche.metric.NOPM;
import com.github.mauricioaniche.metric.NOSF;
import com.github.mauricioaniche.metric.NOSI;
import com.github.mauricioaniche.metric.NOSM;
import com.github.mauricioaniche.metric.RFC;
import com.github.mauricioaniche.metric.WMC;

import data.io.util.IOUtil;

public class JavaMetricExtractor {

  private MetricReport report;
  private Callable<List<MetricExtractor>> metricExtractors;
  private List<CompilationUnit> units;

  private static Logger log = Logger.getLogger(JavaMetricExtractor.class);

  public JavaMetricExtractor(String versionPath) {
    this.units = ASTUtil.getASTs(versionPath);
    this.metricExtractors = () -> usedMetricExtractors();
    this.report = new MetricReport();
  }

  /**
   * 13 kinds of code metrics in total
   */
  private List<MetricExtractor> usedMetricExtractors() {
    //MetricExtractor[] mExtractors = { new CBO() };
    //return new ArrayList<MetricExtractor>(Arrays.asList(mExtractors));

    MetricExtractor[] mExtractors = {new CBO(), new DIT(), new LCOM(), new NOC(), new NOF(),
        new NOM(), new NOPF(),
        new NOPM(), new NOSF(), new NOSI(), new NOSM(), new RFC(), new WMC()};
    return new ArrayList<MetricExtractor>(Arrays.asList(mExtractors));
  }

  public MetricReport process() {
    initialize();

    //System.out.println("-process");

    for (CompilationUnit cu : units) {
      if (cu.getPackage() != null) {
        // top-level types: TypeDeclaration EnumDeclaration AnnotationTypeDeclaration
        @SuppressWarnings("unchecked")
        List<AbstractTypeDeclaration> types = cu.types();
        try {
          for (AbstractTypeDeclaration atd : types) {
            //ignore: AnnotationTypeDeclaration
            if (!(atd instanceof AnnotationTypeDeclaration)) {
              for (MetricExtractor visitor : metricExtractors.call()) { // multi-threads processing
                visitor.execute(atd, report);
              }
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }

    return report;
  }

  private String visiterName(MetricExtractor visitor) {
    // new CBO(), new DIT(), new LCOM(), new NOC(), new NOF(), new NOM(), new NOPF(),
    // new NOPM(), new NOSF(), new NOSI(), new NOSM(), new RFC(), new WMC()
    if (visitor instanceof CBO) {
      return "CBO";
    } else if (visitor instanceof DIT) {
      return "DIT";
    } else if (visitor instanceof LCOM) {
      return "LCOM";
    } else if (visitor instanceof NOC) {
      return "NOC";
    } else if (visitor instanceof NOF) {
      return "NOF";
    } else if (visitor instanceof NOM) {
      return "NOM";
    } else if (visitor instanceof NOPF) {
      return "NOPF";
    } else if (visitor instanceof NOPM) {
      return "NOPM";
    } else if (visitor instanceof NOSF) {
      return "NOSF";
    } else if (visitor instanceof NOSI) {
      return "NOSI";
    } else if (visitor instanceof NOSM) {
      return "NOSM";
    } else if (visitor instanceof RFC) {
      return "RFC";
    } else if (visitor instanceof WMC) {
      return "WMC";
    }

    return "";
  }

  private void initialize() {
    for (CompilationUnit cu : units) {
      String sourceFilePath = (String) cu.getProperty("sourceFilePath");
      if (cu.getPackage() == null) {
        continue;
      }

      String packageName = cu.getPackage().getName().getFullyQualifiedName();
      String fullyQualifiedName = "";
      String type = "";

      // top-level types: TypeDeclaration EnumDeclaration AnnotationTypeDeclaration
      @SuppressWarnings("unchecked")
      List<AbstractTypeDeclaration> types = cu.types();
      for (AbstractTypeDeclaration atd : types) {
        //ignore: AnnotationTypeDeclaration
        if (atd instanceof TypeDeclaration) {
          TypeDeclaration node = (TypeDeclaration) atd;
          fullyQualifiedName = packageName + "." + node.getName().getIdentifier();
          if (node.isInterface()) {
            type = "interface";
          } else {
            type = "class";
          }
        } else if (atd instanceof EnumDeclaration) {
          EnumDeclaration node = (EnumDeclaration) atd;
          fullyQualifiedName = packageName + "." + node.getName().getIdentifier();
          type = "enum";
        }

        if (fullyQualifiedName.isEmpty() == false && type.isEmpty() == false) {
          atd.setProperty("sourceFilePath", sourceFilePath); // important !!
          atd.setProperty("fullyQualifiedName", fullyQualifiedName); // important !!
          atd.setProperty("type", type); // important !!

          MetricValue result = new MetricValue(sourceFilePath, fullyQualifiedName, type);
          result.setLoc(ASTUtil.getLOC(atd));
          report.add(result);
        }
      }
    }
  }
}
