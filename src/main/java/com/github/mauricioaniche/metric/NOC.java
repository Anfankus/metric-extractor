package com.github.mauricioaniche.metric;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.github.mauricioaniche.ck.MetricValue;
import com.github.mauricioaniche.ck.MetricReport;
import com.github.mauricioaniche.ck.MetricExtractor;

/**
 * NOC - Number of Children
 */
public class NOC extends ASTVisitor implements MetricExtractor {

  private MetricReport report;

  @Override
  public boolean visit(TypeDeclaration node) {
    ITypeBinding binding = node.resolveBinding();
    ITypeBinding father = binding.getSuperclass();
    if (father != null) {
      MetricValue fatherCk = report.getByClassName(father.getBinaryName());
      if (fatherCk != null) {
        fatherCk.increaseNoc();
      }
    }
    return false;
  }

  @Override
  public void execute(ASTNode node, MetricReport report) {
    this.report = report;
    node.accept(this);
  }
}
