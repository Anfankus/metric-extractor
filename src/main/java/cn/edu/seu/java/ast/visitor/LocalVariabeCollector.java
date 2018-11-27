package cn.edu.seu.java.ast.visitor;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.jgrapht.alg.shortestpath.TreeSingleSourcePathsImpl;

import cn.edu.seu.java.node.ASTClass;
import cn.edu.seu.java.util.ASTNodeUtil;
import data.io.util.IOUtil;

public class LocalVariabeCollector extends ASTVisitor {

  private String sourceFilePath;
  private String currentMethodName;
  private TypeDeclaration exploredClass;
  private List<String> allPublicMethods;
  private List<String> nonStaticFields;
  private List<String> getterMethods;
  private Map<String, Set<String>> methodAttributeAccess;

  public LocalVariabeCollector(ASTClass exploredClass) {
    this.exploredClass = exploredClass.getTypeDeclaration();
    this.sourceFilePath = exploredClass.getSourceFilePath();
    this.methodAttributeAccess = new HashMap<String, Set<String>>();
    this.allPublicMethods = new ArrayList<String>();
    this.getterMethods = new ArrayList<String>();
    this.nonStaticFields = new ArrayList<String>();
    this.initialize();
  }

  private void initialize() {
    // extract all non-static fields in the measured class
    for (FieldDeclaration field : this.exploredClass.getFields()) {
      if (Modifier.isStatic(field.getModifiers()) == false) {
        @SuppressWarnings("unchecked")
        List<VariableDeclarationFragment> vdfs = field.fragments();
        for (VariableDeclarationFragment vdf : vdfs) {
          this.nonStaticFields.add(vdf.getName().getIdentifier());
        }
      }
    }

    // extract signatures of all [direct] methods in the measured class
    MethodDeclaration[] allMethods = this.exploredClass.getMethods();
    for (MethodDeclaration md : allMethods) {
      String signature = ASTNodeUtil.getSignature(md);
      if (md.getModifiers() == Modifier.PUBLIC) {
        // use signature to avoid "operation overload"
        this.allPublicMethods.add(signature);
      }

      if (isGetter(md)) {
        this.getterMethods.add(signature);
      }
    }
  }

  private boolean isGetter(MethodDeclaration method) {
    if (method.isConstructor() == true || method.getBody() == null
        || method.getModifiers() == Modifier.PRIVATE) {
      return false;
    }

    // calculate the number of statement in method body
    int stmtSize = method.getBody().statements().size();
    if (stmtSize != 1) {
      return false;
    }

    // check whether method has parameters
    boolean hasParameter = (method.parameters().size() > 0) ? true : false;
    if (hasParameter == true) {
      return false;
    }

    // check whether method returns a non-static field
    boolean returnNonStaticField = whetherReturnNonStaticField(method);
    if (returnNonStaticField == false) {
      return false;
    }

    /**
     * String methodName = method.getName().getIdentifier();
     *
     * Strong Condition:
     * stmtSize == 1 && hasParameter == false && returnNonStaticField == true &&
     * methodName.startsWith("get") == true
     *
     * Week Condition:
     * stmtSize == 1 && hasParameter == false && returnNonStaticField == true
     */

    return true;
  }

  private boolean whetherReturnNonStaticField(MethodDeclaration method) {
    boolean isNonStaticField = false;
    List<ReturnStatement> returnStatementList = this.getReturnStatements(method);
    if (returnStatementList != null && returnStatementList.size() == 1) {
      ReturnStatement returnStatement = returnStatementList.get(0); // only one return stmt
      Expression expression = returnStatement.getExpression();
      if (expression != null) {
        if (expression.getNodeType() == ASTNode.SIMPLE_NAME) {
          SimpleName simpleName = (SimpleName) expression;
          String returnName = simpleName.getIdentifier();
          isNonStaticField = this.nonStaticFields.contains(returnName);
        } else if (expression.getNodeType() == ASTNode.FIELD_ACCESS) {
          FieldAccess fdAccess = (FieldAccess) expression;
          String fieldName = fdAccess.getName().getIdentifier();
          isNonStaticField = this.nonStaticFields.contains(fieldName);
        }
      }
    }
    return isNonStaticField;
  }

  private List<ReturnStatement> getReturnStatements(MethodDeclaration md) {
    Block block = md.getBody();
    List<ReturnStatement> returnStatementList = new ArrayList<ReturnStatement>();
    if (block != null) {
      @SuppressWarnings("unchecked")
      List<Statement> statementList = block.statements();
      for (int i = 0; i < statementList.size(); i++) {
        if (statementList.get(i).getNodeType() == ReturnStatement.RETURN_STATEMENT) {
          ReturnStatement statement = (ReturnStatement) statementList.get(i);
          returnStatementList.add(statement);
        }
      }
    }
    return returnStatementList;
  }

  public Map<String, Set<String>> process() {
    for (MethodDeclaration method : this.exploredClass.getMethods()) {
      this.currentMethodName = ASTNodeUtil.getSignature(method);
      if (this.allPublicMethods.contains(currentMethodName)) {
        methodAttributeAccess.put(currentMethodName, new HashSet<String>());
        method.accept(this);
      }
    }
    return this.methodAttributeAccess;
  }

  @Override
  public boolean visit(SimpleName node) {
    IBinding iBinding = node.resolveBinding();
    ITypeBinding iTypeBinding = null;
    if (iBinding != null && iBinding.getKind() == 3) { // 3-variable, 4-method
      IVariableBinding iVarBinding = (IVariableBinding) iBinding;
      if (iVarBinding.isField()) { // SimpleName is Field
        iTypeBinding = iVarBinding.getDeclaringClass();
      }
    }

    if (iTypeBinding != null && iTypeBinding.isFromSource() == true
        && this.nonStaticFields.contains(node.getIdentifier())) {
      Set<String> accessedNonStaticField = this.methodAttributeAccess.get(this.currentMethodName);
      accessedNonStaticField.add(node.getIdentifier());

      IOUtil.outputToFile("==================SimpleName=====================");
      IOUtil.outputToFile(sourceFilePath);
      IOUtil.outputToFile(this.currentMethodName);
      IOUtil.outputToFile("Line:" + ASTNodeUtil.getStartLine(node) + " " + node.getIdentifier());
      IOUtil.outputToFile("=================================================");
    }

    return false;
  }

  @Override
  public boolean visit(MethodInvocation node) {
    String signature = ASTNodeUtil.getSignature(node);
    if (this.getterMethods.contains(signature)) {
      Set<String> accessedNonStaticField = this.methodAttributeAccess.get(this.currentMethodName);
      // use the signature of method invocation as the proxy of field name accessed by method
      accessedNonStaticField.add(signature);

      IOUtil.outputToFile("================MethodInvocation=================");
      IOUtil.outputToFile(sourceFilePath);
      IOUtil.outputToFile(this.currentMethodName);
      IOUtil.outputToFile("Line:" + ASTNodeUtil.getStartLine(node) + " " + signature);
      IOUtil.outputToFile("=================================================");
    }

    return true;
  }
}
