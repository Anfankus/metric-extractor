package com.github.mauricioaniche.metric;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.github.mauricioaniche.ck.MetricValue;
import com.github.mauricioaniche.ck.MetricReport;
import com.github.mauricioaniche.ck.MetricExtractor;

public class NOSI extends ASTVisitor implements MetricExtractor {

  private int count = 0;

  public boolean visit(MethodInvocation node) {
    IMethodBinding binding = node.resolveMethodBinding();
    if (binding != null && Modifier.isStatic(binding.getModifiers())) {
      count++;
    }
    return super.visit(node);
  }

  @Override
  public void execute(ASTNode node, MetricReport report) {
    String fullyQualifiedName = (String) node.getProperty("fullyQualifiedName");
    if (fullyQualifiedName != null && fullyQualifiedName.isEmpty() == false) {
      node.accept(this);
      MetricValue ckn = report.getByClassName(fullyQualifiedName);
      ckn.setNosi(count);
    }
  }
}
