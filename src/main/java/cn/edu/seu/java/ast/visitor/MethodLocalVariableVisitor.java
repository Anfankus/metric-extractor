package cn.edu.seu.java.ast.visitor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import cn.edu.seu.java.node.ASTClass;
import cn.edu.seu.java.node.ASTCompilationUnit;

public class MethodLocalVariableVisitor extends ASTVisitor {

  private String sourceFilePath;
  private String methodName;
  public int numLocalDeclaredVariable;
  private int numUsedInstanceVariable;

  public MethodLocalVariableVisitor(String sourceFilePath, MethodDeclaration node) {
    this.sourceFilePath = sourceFilePath;
    this.methodName = node.getName().getIdentifier();
    node.accept(this);
  }

  public boolean visit(SimpleName node) {
    /*
     * LocalDeclaredVariable: include local variables of method formal parameters
     */
    if (node.isDeclaration() && node.getFullyQualifiedName().equals(methodName) != true) {
      this.numLocalDeclaredVariable++;
    }

    if (node.isDeclaration() == false) {
      IBinding iSimpleNameBinding = node.resolveBinding();
      //varibale types: PACKAGE, TYPE, VARIABLE, METHOD, ANNOTATION, or MEMBER_VALUE_PAIR
      if (iSimpleNameBinding != null && iSimpleNameBinding.getKind() == 3) {
        IVariableBinding iVarBinding = (IVariableBinding) iSimpleNameBinding;
        if (iVarBinding.isField()) {
          this.numUsedInstanceVariable++;
					/*System.out.println("----------------------------------------------");
					System.out.println(sourceFilePath);
					System.out.println("beginLine: " + getStartLine(node));
					System.out.println("endLine: " + getEndLine(node));
					System.out.println("field ref: " + node.toString());*/
        }
        //int flags = iVarBinding.getModifiers();
        //String modifier = Modifier.toString(iVarBinding.getModifiers());
				/*if (java.lang.reflect.Modifier.isPublic(flags)) {
					//this.numLocalVariables++;
					System.out.println("----------------------------------------------");
					System.out.println(this.sourceFile.getSourceFilePath());
					System.out.println("beginLine: " + getStartLine(node));
					System.out.println("endLine: " + getEndLine(node));
					System.out.println("public field ref: " + node.toString());
				}*/
      }
    }
    return true;
  }

  private long getStartLine(ASTNode node) {
    CompilationUnit unit = (CompilationUnit) node.getRoot();
    int startPoint = node.getStartPosition();
    int startLineNumber = unit.getLineNumber(startPoint);
    return startLineNumber;
  }

  private long getEndLine(ASTNode node) {
    CompilationUnit unit = (CompilationUnit) node.getRoot();
    int length = node.getLength();
    int startPoint = node.getStartPosition();
    int endPoint = startPoint + length - 1;
    int endLineNumber = unit.getLineNumber(endPoint);
    return endLineNumber;
  }

  public int numOfLocalDeclaredVariable() {
    return numLocalDeclaredVariable;
  }

  public int numOfUsedInstanceVariable() {
    return numUsedInstanceVariable;
  }
}

