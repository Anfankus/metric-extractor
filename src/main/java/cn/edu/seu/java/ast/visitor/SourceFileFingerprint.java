package cn.edu.seu.java.ast.visitor;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Vector;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;

import cn.edu.seu.java.ast.visitor.MethodLocalVariableVisitor;

import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class SourceFileFingerprint extends ASTVisitor {

  private int numImports = 0;
  private int numInstanceVariables = 0;
  private int numContainedClasses = 0;
  private int numContainedInterfaces = 0;
  private int numInnerClasses = 0;
  private int numExceptionClasses = 0;
  private int numExtendedClasses = 0;
  private int numImplementedInterfaces = 0;

  private int numMethods = 0;
  private int numLocalDeclaredVariables = 0;
  private int numThrownExceptions = 0;
  private int numInvokedMethods = 0;
  private int numMethodParameters = 0;
  private String sourceFilePath;


  public SourceFileFingerprint(String sourceFilPath, CompilationUnit unit) {
    this.sourceFilePath = sourceFilPath;
    unit.accept(this);
  }

  public boolean visit(EnumDeclaration node) {
    if (node.getNodeType() == ASTNode.ENUM_DECLARATION) {
      EnumDeclaration enumDec = (EnumDeclaration) node;
      this.numImplementedInterfaces += this.getSuperInterfaces(enumDec).size();
    }
    return true;
  }

  private Vector<String> getSuperInterfaces(EnumDeclaration node) {
    Vector<String> superInterfaceNames = new Vector<String>();
    @SuppressWarnings("unchecked")
    List<Type> interfaces = node.superInterfaceTypes();
    for (Type t : interfaces) {
      ITypeBinding iType = t.resolveBinding();
      if (iType != null) {
        superInterfaceNames.add(iType.getName());
      }
    }
    return superInterfaceNames;
  }

  public boolean visit(AnonymousClassDeclaration node) {
    this.numContainedClasses++;
    return true;
  }

  public boolean visit(FieldDeclaration node) {
		/*if(org.eclipse.jdt.core.dom.Modifier.isStatic(node.getModifiers()) == false){
			this.numInstanceVariables++;
		}*/
    if (Modifier.isStatic(node.getModifiers()) == false) {
      this.numInstanceVariables++;
    }
    return true;
  }

  public boolean visit(ImportDeclaration node) {
    this.numImports++;
    return false;
  }

  public boolean visit(MethodDeclaration node) {
    this.numMethods++;
    this.numMethodParameters += node.parameters().size();
    this.numThrownExceptions += node.thrownExceptionTypes().size();
    MethodLocalVariableVisitor visitor = new MethodLocalVariableVisitor(this.sourceFilePath, node);
    this.numLocalDeclaredVariables += visitor.numOfLocalDeclaredVariable();
    return true;
  }

  public boolean visit(MethodInvocation node) {
    this.numInvokedMethods++;
    return true;
  }

  public boolean visit(TypeDeclaration node) {
    // count num of super class and super interface
    if (node.isInterface()) {
      this.numContainedInterfaces++;
      this.numExtendedClasses += node.superInterfaceTypes().size();
    } else {
      if (node.getSuperclassType() != null) {
        this.numExtendedClasses += 1;
      }
      this.numImplementedInterfaces += node.superInterfaceTypes().size();
    }

    // count num of inner class
    this.numContainedClasses++;
    if (node.isMemberTypeDeclaration()) {
      this.numInnerClasses++;
    }

    // count num of "Exception Class" which extends "Exception"
    String superClassName = "";
    Type superType = node.getSuperclassType();
    if (superType != null) {
      ITypeBinding iType = superType.resolveBinding();
      if (iType != null) {
        superClassName = iType.getName();
      }
    }
    if (superClassName.equals("Exception")) {
      this.numExceptionClasses++;
    }
    return true;
  }

  public String getSourceFileFingerPrint() {
    StringBuffer sb = new StringBuffer();
    sb.append(numImports + " ");
    sb.append(numInstanceVariables + " ");
    sb.append(numContainedClasses + " ");
    sb.append(numContainedInterfaces + " ");
    sb.append(numInnerClasses + " ");
    sb.append(numExceptionClasses + " ");
    sb.append(numExtendedClasses + " ");
    sb.append(numImplementedInterfaces + " ");
    sb.append(numMethods + " ");
    sb.append(numLocalDeclaredVariables + " ");
    sb.append(numThrownExceptions + " ");
    sb.append(numInvokedMethods + " ");
    sb.append(numMethodParameters);
    return sb.toString();
  }
}
