package cn.edu.seu.java.node;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import cn.edu.seu.java.ast.visitor.McCabeMethodVisitor;
import cn.edu.seu.java.util.ASTNodeUtil;
import data.io.util.IOUtil;

public class ASTMethod {

  private MethodDeclaration method;
  private String sourceFilePath;
  private String packageName;
  private String unitName;
  private String methodName;
  private String projectName;
  private String versionNo;
  private String versionPath;
  private String signature;
  private String sourceDirectory;
  private String relativeSourcePath;
  private String fullyQualifiedTopClassName;
  private String fullyQualifiedName;
  private String chainedClassName;

  public ASTMethod(String pkgName, String unitName, String sourceFilePath, MethodDeclaration node) {
    this.packageName = pkgName;
    this.unitName = unitName;
    this.sourceFilePath = sourceFilePath;
    this.method = node;
    this.fullyQualifiedName = ASTNodeUtil.getFullyQualifedName(node);
    this.signature = ASTNodeUtil.getSignature(node);
    this.chainedClassName = this.fullyQualifiedName.replace(packageName + ".", "")
        .replace("." + signature, "");

		/*IMethodBinding iMethodBinding = node.resolveBinding();
		if (iMethodBinding != null) {
			String iFullMethdName = iMethodBinding.getDeclaringClass().getQualifiedName();
			fullyQualifiedName = iFullMethdName + "." + this.signature;
		} else {
			//fullyQualifiedName = fullyQualifiedName.replace("." + this.signature, "");
			IOUtil.outputToFile("--------------------------------");
			IOUtil.outputToFile(sourceFilePath);
			IOUtil.outputToFile(ASTNodeUtil.getStartLine(node));
			IOUtil.outputToFile(fullyQualifiedName);
			IOUtil.outputToFile(ASTNode.nodeClassForType(node.getNodeType()).getName());
			IOUtil.outputToFile(ASTNode.nodeClassForType(node.getParent().getNodeType()).getName());
			IOUtil.outputToFile("");
		}*/
    this.methodName = node.getName().getIdentifier();
  }

  public boolean isSetter() {
    boolean isSetter = false;
    if (this.method.isConstructor() || this.isEmptyMethod() || this.isAbstractMethod()) {
      return false;
    }

    // check whether method's return is void
    boolean isVoidOfReturnType = false;
    Type type = method.getReturnType2();
    if (type != null && type.isPrimitiveType() == true) {
      PrimitiveType ptype = (PrimitiveType) type;
      if (ptype.getPrimitiveTypeCode() == PrimitiveType.VOID) {
        isVoidOfReturnType = true;
      }
    }

    boolean onlyOneParameter = method.parameters().size() == 1;
    // check last statement is an assignment, and left side is a field
    boolean isAssignment = false;
    Block block = method.getBody();
    if (block != null) {
      @SuppressWarnings("unchecked")
      List<Statement> statementList = block.statements();
      if (statementList.size() == 1) {
        isAssignment = isFieldAssignmentStatement(statementList.get(statementList.size() - 1));
      }
    }
    if (isVoidOfReturnType && onlyOneParameter && isAssignment) {
      isSetter = true;
    }
    return isSetter;
  }

  private boolean isFieldAssignmentStatement(Statement s) {
    boolean isFieldAssignment = false;
    if (s.getNodeType() == ASTNode.EXPRESSION_STATEMENT) {
      ExpressionStatement expressionStatement = (ExpressionStatement) s;
      Expression expression = expressionStatement.getExpression();
      if (expression.getNodeType() == ASTNode.ASSIGNMENT) {
        Assignment assignment = (Assignment) expression;
        Expression leftSide = assignment.getLeftHandSide();
        if (leftSide.getNodeType() == ASTNode.SIMPLE_NAME) {
          SimpleName simpleName = (SimpleName) leftSide;
          isFieldAssignment = this.isFieldName(simpleName);
        } else if (leftSide.getNodeType() == ASTNode.FIELD_ACCESS) {
          isFieldAssignment = true;
        }
      }
    }
    return isFieldAssignment;
  }

  private boolean isFieldName(SimpleName node) {
    boolean isField = false;
    IBinding iBinding = node.resolveBinding();
    if (iBinding != null && iBinding.getKind() == 3) { // 3-variable, 4-method
      IVariableBinding iVarBinding = (IVariableBinding) iBinding;
      if (iVarBinding.isField() == true) {
        isField = true;
      }
    }
    return isField;
  }

  public boolean isGetter() {
    MethodDeclaration method = this.method;
    boolean isGetter = false;
    if (method.isConstructor() || method.getBody() == null) {
      return false;
    }

    // check the number of statement in method body
    int stmtSize = method.getBody().statements().size();

    //check Its signature has no parameters
    boolean hasZeroParameter = method.parameters().isEmpty();

    //check whether method return field
    boolean whetherReturnField = false;

    List<ReturnStatement> returnStmts = this.getReturnStatements(method);
    if (returnStmts.size() == 1) {
      //return [Expression];
      ReturnStatement returnStatement = returnStmts.get(0);
      Expression expression = returnStatement.getExpression();
      if (expression != null) {
        if (expression.getNodeType() == ASTNode.SIMPLE_NAME) {
          SimpleName simpleName = (SimpleName) expression;
          IBinding iBinding = simpleName.resolveBinding();
          if (iBinding != null && iBinding.getKind() == 3) { // 3-variable, 4-method
            whetherReturnField = ((IVariableBinding) iBinding).isField();
          }
        } else if (expression.getNodeType() == ASTNode.FIELD_ACCESS) {
          FieldAccess fa = (FieldAccess) expression;
          Expression ep = fa.getExpression();
          if (ep.getNodeType() == ASTNode.THIS_EXPRESSION) {
            whetherReturnField = true;
          }
        }
      }
    }
    if (stmtSize == 1 && hasZeroParameter == true && whetherReturnField == true) {
      isGetter = true;
    }
    return isGetter;
  }

  private List<ReturnStatement> getReturnStatements(MethodDeclaration d) {
    Block block = d.getBody();
    List<ReturnStatement> returnStmts = new ArrayList<ReturnStatement>();
    if (block != null) {
      @SuppressWarnings("unchecked")
      List<Statement> statementList = block.statements();
      for (int i = 0; i < statementList.size(); ++i) {
        if (statementList.get(i).getNodeType() == ReturnStatement.RETURN_STATEMENT) {
          returnStmts.add((ReturnStatement) statementList.get(i));
        }
      }
    }
    return returnStmts;
  }

  public boolean isInitMethod() {
    String methodName = this.method.getName() + "";
    return methodName.startsWith("init") || methodName.startsWith("initialize");
  }

  /**
   * @return whether functionality of this method is finished by other method
   */
  public boolean isDelegateMethod() {
    String methodName = this.method.getName() + "";
    boolean isDelegater = methodName.startsWith("size") || methodName.startsWith("length");
    if (isDelegater) {
      return isDelegater;
    }
    Block block = this.method.getBody();
    if (block != null) {
      @SuppressWarnings("unchecked")
      List<Statement> statementList = block.statements();
      if (statementList.size() == 1 || statementList.size() == 2) {
        Statement stmt = statementList.get(statementList.size() - 1);
        if (stmt.getNodeType() == ASTNode.RETURN_STATEMENT) {
          ReturnStatement returnStmt = (ReturnStatement) stmt;
          Expression exp = returnStmt.getExpression();
          if (exp != null && exp.getNodeType() == ASTNode.METHOD_INVOCATION) {
            return true;
          }
        }
        if (stmt.getNodeType() == ASTNode.EXPRESSION_STATEMENT) {
          ExpressionStatement es = (ExpressionStatement) stmt;
          Expression exp = es.getExpression();
          if (exp != null && exp.getNodeType() == ASTNode.METHOD_INVOCATION) {
            return true;
          }
        }
      }
    }
    return isDelegater;
  }

  public boolean isAbstractMethod() {
    //return this.method.getBody() == null;
    int mod = this.method.getModifiers();
    return Modifier.isAbstract(mod);
  }

  public boolean isProtected() {
    int mod = this.method.getModifiers();
    return Modifier.isProtected(mod);
  }

  /**
   * @return true if #stmts in method's body (block) == 0 (given that the block exists); else return
   * false;
   */
  public boolean isEmptyMethod() {
    boolean isEmpty = false;
    Block block = this.method.getBody();
    if (block != null) {
      if (block.statements().size() == 0) {
        isEmpty = true;
      }
    }
    return isEmpty;
  }

  public boolean simpleNameMatch(ASTMethod targeASTMethod) {
    return ASTNodeUtil.getFullyQualifedName(this.method)
        .equals(ASTNodeUtil.getFullyQualifedName(targeASTMethod.method));
  }

  public String getFullyQualifiedName() {
    return this.fullyQualifiedName;
  }

  public MethodDeclaration getMethodDeclaration() {
    return method;
  }

  public String getPackageName() {
    return packageName;
  }

  public String getUnitName() { // compilation unit name
    return this.unitName;
  }

  public String getSourceFilePath() {
    return this.sourceFilePath;
  }

  public String getName() {
    return this.methodName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  public void setVersionNum(String versionNo) {
    this.versionNo = versionNo;
  }

  public void setVersionPath(String versionPath) {
    this.versionPath = versionPath;
  }

  public String getProjectName() {
    return projectName;
  }

  public String getVersionNum() {
    return versionNo;
  }

  public String getVersionPath() {
    return this.versionPath;
  }

  public List<Statement> getStatementsNestedInTryStatements(MethodDeclaration node) {
    List<Statement> stmts = new LinkedList<Statement>();
    Block block = node.getBody();
    if (block != null) {
      @SuppressWarnings("unchecked")
      List<Statement> methodStmts = block.statements();
      for (Statement stmt : methodStmts) {
        if (stmt.getNodeType() != ASTNode.TRY_STATEMENT) {
          stmts.add(stmt);
        } else {
          TryStatement tryStmt = (TryStatement) stmt;
          Block tryBlock = tryStmt.getBody();
          @SuppressWarnings("unchecked")
          List<Statement> tryStmts = tryBlock.statements();
          for (Statement stmtInTryBlock : tryStmts) {
            stmts.add(stmtInTryBlock);
          }
        }
      }
    }
    return stmts;
  }

  public int getMethodComplexity() {
    return (new McCabeMethodVisitor(this)).getMethodCyclomaticComplexity();
  }

  /**
   * @return packageName + chainedClassName
   */
  public String getFullyQualifiedDeclaringClassName() {
    return this.packageName + "." + this.chainedClassName;
  }

  public String getTopLevelClassName() {
    int index = this.chainedClassName.indexOf(".");
    if (index != -1) {
      return this.chainedClassName.substring(0, index);
    }
    return this.chainedClassName;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(ASTNodeUtil.getFullyQualifedName(method))
        .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return obj == this ? true
        : obj == null || getClass() != obj.getClass() ? false
            : ASTNodeUtil.getFullyQualifedName(method)
                .equals(ASTNodeUtil.getFullyQualifedName(((ASTMethod) obj).getMethodDeclaration()));
  }

  public String toString() {
    return this.sourceFilePath + " " + this.projectName + " " + this.versionNo + " " + method
        .getName() + " ";
  }

  public void setSourceDirectory(String sourceDirectory) {
    this.sourceDirectory = sourceDirectory;
  }

  public void setRelativeSourcePath(String relativeSourcePath) {
    this.relativeSourcePath = relativeSourcePath;
  }

  public String getSourceDirectory() {
    return sourceDirectory;
  }

  public String getRelativeSourceFilePath() {
    return relativeSourcePath;
  }

  public void setFullyQualifiedTopClassName(String fullyQualifiedTopClassName) {
    this.fullyQualifiedTopClassName = fullyQualifiedTopClassName;
  }

  public String getFullyQualifiedTopDeclaringClassName() {
    return fullyQualifiedTopClassName;
  }
}
