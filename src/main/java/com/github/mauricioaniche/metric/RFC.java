package com.github.mauricioaniche.metric;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.github.mauricioaniche.ck.MetricValue;
import com.github.mauricioaniche.ck.MetricReport;
import com.github.mauricioaniche.ck.MetricExtractor;

import cn.edu.seu.java.util.ASTNodeUtil;
import data.io.util.IOUtil;

/**
 * Ref: [2015Jureczko] Which process metrics can significantly improve defect prediction models
 * [2015Jureczko] Software product metrics used to build defect prediction models.pdf
 */
public class RFC extends ASTVisitor implements MetricExtractor {

  private Set<String> methodInvocations = new HashSet<String>();
  private int methodCount = 0;

  @Override
  public boolean visit(MethodDeclaration node) {
    if (isInLocalClass(node) != true) {
      this.methodCount = this.methodCount + 1;
    }

    return super.visit(node);
  }

  public static boolean isInLocalClass(MethodDeclaration node) {
    boolean flag = false;
    ASTNode pointer = node;
    while (pointer != null && pointer.getNodeType() != node.getRoot().getNodeType()) {
      int typeCode = pointer.getNodeType();
      if (typeCode == ASTNode.TYPE_DECLARATION || typeCode == ASTNode.ANONYMOUS_CLASS_DECLARATION) {
        break;
      }
      pointer = pointer.getParent();
    }

    if (pointer.getNodeType() == ASTNode.TYPE_DECLARATION) {
      TypeDeclaration typeDec = (TypeDeclaration) pointer;
      if (typeDec.isLocalTypeDeclaration() == true) {
        flag = true;
      }
    } else if (pointer.getNodeType() == ASTNode.ANONYMOUS_CLASS_DECLARATION) {
      flag = true;
    }

    return flag;
  }

  @Override
  public boolean visit(SuperMethodInvocation node) {
    IMethodBinding binding = node.resolveMethodBinding();
    @SuppressWarnings("unchecked")
    List<Expression> args = node.arguments();

    String simplegSignature = node.getName() + "(" + arguments(args) + ")";
    count(simplegSignature, binding);

    return super.visit(node);
  }

  @Override
  public boolean visit(MethodInvocation node) {
    IMethodBinding binding = node.resolveMethodBinding();
    @SuppressWarnings("unchecked")
    List<Expression> args = node.arguments();

    String simplegSignature = node.getName() + "(" + arguments(args) + ")";
    count(simplegSignature, binding);

    return super.visit(node);
  }

  private int arguments(List<Expression> arguments) {
    if (arguments == null || arguments.isEmpty()) {
      return 0;
    }
    return arguments.size();
  }

  private void count(String methodName, IMethodBinding iMBinding) {
    if (iMBinding != null) {
      String fullyQualifiedMethodName = this.getFullyQualifiedMethodName(iMBinding);
      if (fullyQualifiedMethodName.isEmpty() != true) {
        methodInvocations.add(fullyQualifiedMethodName);
      }
    } else {
      methodInvocations.add(methodName);
    }
  }

  private String getFullyQualifiedMethodName(final IMethodBinding iMBinding) {
    StringBuffer methodParList = new StringBuffer();
    if (iMBinding != null) {
      ITypeBinding[] iParTypeBinding = iMBinding.getParameterTypes();
      for (ITypeBinding iParTB : iParTypeBinding) {
        methodParList.append(iParTB.getName() + ",");
      }
    }

    if (methodParList.length() > 0) {
      methodParList.deleteCharAt(methodParList.length() - 1);
    }

    methodParList.insert(0, "(");
    methodParList.append(")");

    ITypeBinding iDeclaringTypeBinding = iMBinding.getDeclaringClass();
    if (iDeclaringTypeBinding.isFromSource()) {
      methodParList.insert(0, iDeclaringTypeBinding.getQualifiedName() + "." + iMBinding.getName());
      return methodParList.toString();
    } else {
      return "";
    }
  }

  @Override
  public void execute(ASTNode node, MetricReport report) {
    String fullyQualifiedName = (String) node.getProperty("fullyQualifiedName");
    if (fullyQualifiedName != null && fullyQualifiedName.isEmpty() == false) {
      node.accept(this);
      MetricValue ckn = report.getByClassName(fullyQualifiedName);
      ckn.setRfc(methodInvocations.size() + this.methodCount);
    }
  }
}
