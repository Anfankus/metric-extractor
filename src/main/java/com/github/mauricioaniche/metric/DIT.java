package com.github.mauricioaniche.metric;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.github.mauricioaniche.ck.MetricValue;
import com.github.mauricioaniche.ck.MetricReport;
import com.github.mauricioaniche.ck.MetricExtractor;

public class DIT extends ASTVisitor implements MetricExtractor {

  int dit = 1; // Object is the father of everyone!

  @Override
  public boolean visit(TypeDeclaration node) {
    ITypeBinding binding = node.resolveBinding();
    if (binding != null) {
      calculate(binding);
    }

    return super.visit(node);
  }

  private void calculate(ITypeBinding binding) {
    ITypeBinding father = binding.getSuperclass();
    int count = 0;
    while (father != null) {
      String fatherName = father.getQualifiedName();
      if (fatherName.endsWith("Object")) {
        break;
      } else {
        count = count + 1;
        if (count > 15) {
          break;
        }
        dit = dit + 1;
        father = father.getSuperclass();
      }
    }
  }

  @Override
  public void execute(ASTNode node, MetricReport report) {
    String fullyQualifiedName = (String) node.getProperty("fullyQualifiedName");
    if (fullyQualifiedName != null && fullyQualifiedName.isEmpty() == false) {
      node.accept(this);
      MetricValue ckn = report.getByClassName(fullyQualifiedName);
      ckn.setDit(dit);
    }
  }
}
