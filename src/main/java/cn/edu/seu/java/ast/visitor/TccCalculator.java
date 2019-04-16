package cn.edu.seu.java.ast.visitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.edu.seu.java.node.ASTClass;

/**
 * this class is borrowed from PMD6.0.0 (net.sourceforge.pmd.lang.java.metrics.impl.TccMetric)
 *
 * @author Clément Fournier
 */
public class TccCalculator {

  /**
   * Ref: [2004] a class cohesion metric focusing on cohesive-part size
   *
   * PM(c) = {m|m is a public method in class c} UF(m,c) = {f|f is a non-static field contained in
   * class c and used by method m} NMP(c) = |{(m_i, m_j)|interaction(UF(m_i, c), UF(m_j, c) != empty
   * && m_i and m_j belong to PM (c)}| NP(c) = |PM(c)|(|PM(c)-1|)/2 TCC(c) = NMP(c)/NP(c)
   */
  public double compute(ASTClass node) {
    Map<String, Set<String>> usagesByMethod = new TccAttributeAccessCollector(node).process();
    int numPairs = numMethodsRelatedByAttributeAccess(usagesByMethod);
    int maxPairs = maxMethodPairs(usagesByMethod.size());
    double tcc = 0;
    if (maxPairs != 0) {
      tcc = numPairs / (double) maxPairs;
    }
    return tcc;
  }

  /**
   * Gets the number of pairs of methods that use at least one attribute in common.
   *
   * @param usagesByMethod Map of method name to names of local attributes accessed
   * @return The number of pairs
   */
  private int numMethodsRelatedByAttributeAccess(Map<String, Set<String>> usagesByMethod) {
    List<String> methods = new ArrayList<>(usagesByMethod.keySet());
    int methodCount = methods.size();
    int pairs = 0;

    if (methodCount > 1) {
      for (int i = 0; i < methodCount - 1; i++) {
        for (int j = i + 1; j < methodCount; j++) {
          String firstMethodName = methods.get(i);
          String secondMethodName = methods.get(j);

          if (!Collections.disjoint(usagesByMethod.get(firstMethodName),
              usagesByMethod.get(secondMethodName))) {
            pairs++;
          }
        }
      }
    }
    return pairs;
  }

  /**
   * Calculates the number of possible method pairs of two methods.
   *
   * @param methods Number of methods in the class
   * @return Number of possible method pairs
   */
  private int maxMethodPairs(int methods) {
    return methods * (methods - 1) / 2;
  }
}
