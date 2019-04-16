package cn.edu.seu.java.ast.visitor;

import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberRef;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.MethodRefParameter;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.UnionType;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.WildcardType;

import cn.edu.seu.java.node.ASTCompilationUnit;

public class DepthFirstSearchVisitor extends ASTVisitor {

  //01-AnnotationTypeDeclaration
  public boolean visit(AnnotationTypeDeclaration node) {
    print(node);
    return true;
  }

  //02-AnnotationTypeMemberDeclaration
  public boolean visit(AnnotationTypeMemberDeclaration node) {
    print(node);
    return true;
  }

  //03-AnonymousClassDeclaration
  public boolean visit(AnonymousClassDeclaration node) {
    print(node);
    return true;
  }

  //04-ArrayAccess
  public boolean visit(ArrayAccess node) {
    print(node);
    return true;
  }

  //05-ArrayCreation
  public boolean visit(ArrayCreation node) {
    print(node);
    return true;
  }

  //06-ArrayInitializer
  public boolean visit(ArrayInitializer node) {
    print(node);
    return true;
  }

  //07-ArrayType
  public boolean visit(ArrayType node) {
    print(node);
    return true;
  }

  //08-AssertStatement
  public boolean visit(AssertStatement node) {
    print(node);
    return true;
  }

  //09-Assignment
  public boolean visit(Assignment node) {
    print(node);
    return true;
  }

  //10-Block
  public boolean visit(Block node) {
    print(node);
    return true;
  }

  //11-BlockComment
  public boolean visit(BlockComment node) {
    print(node);
    return true;
  }

  //12-BooleanLiteral
  public boolean visit(BooleanLiteral node) {
    print(node);
    return true;
  }

  //13-BreakStatement
  public boolean visit(BreakStatement node) {
    print(node);
    return true;
  }

  //14-CastExpression
  public boolean visit(CastExpression node) {
    print(node);
    return true;
  }

  //15-CatchClause
  public boolean visit(CatchClause node) {
    print(node);
    return true;
  }

  //16-CharacterLiteral
  public boolean visit(CharacterLiteral node) {
    print(node);
    return true;
  }

  //17-ClassInstanceCreation
  public boolean visit(ClassInstanceCreation node) {
    print(node);
    return true;
  }

  //18-CompilationUnit
  public boolean visit(CompilationUnit node) {
    print(node);
    return true;
  }

  @Override
  public void endVisit(CompilationUnit node) {
    System.out.println("endVisit CompilationUnion: ");
  }

  //19-ConditionalExpression
  public boolean visit(ConditionalExpression node) {
    print(node);
    return true;
  }

  //20-ConstructorInvocation
  public boolean visit(ConstructorInvocation node) {
    print(node);
    return true;
  }

  //21-ContinueStatement
  public boolean visit(ContinueStatement node) {
    print(node);
    return true;
  }

  //22-DoStatement
  public boolean visit(DoStatement node) {
    print(node);
    return true;
  }

  //23-EmptyStatement
  public boolean visit(EmptyStatement node) {
    print(node);
    return true;
  }

  //24-EnhancedForStatement
  public boolean visit(EnhancedForStatement node) {
    print(node);
    return true;
  }

  //25-EnumConstantDeclaration
  public boolean visit(EnumConstantDeclaration node) {
    print(node);
    return true;
  }

  //26-EnumDeclaration
  public boolean visit(EnumDeclaration node) {
    print(node);
    return true;
  }

  //27-ExpressionStatement
  public boolean visit(ExpressionStatement node) {
    print(node);
    return true;
  }

  //28-FieldAccess
  public boolean visit(FieldAccess node) {
    print(node);
    return true;
  }

  //29-FieldDeclaration
  public boolean visit(FieldDeclaration node) {
    print(node);
    return true;
  }

  //30-ForStatement
  public boolean visit(ForStatement node) {
    print(node);
    return true;
  }

  //31-IfStatement
  public boolean visit(IfStatement node) {
    print(node);

    //bracifyIfStatement(node);
    return true;
  }

  /**
   * Helper method to recursivly bracify a if-statement.
   *
   * @param ifStatement the if statement
   */
  private void bracifyIfStatement(IfStatement ifStatement) {
    // change the then statement to a block if necessary
    if (!(ifStatement.getThenStatement() instanceof Block)) {
      if (ifStatement.getThenStatement() instanceof IfStatement) {
        bracifyIfStatement((IfStatement) ifStatement.getThenStatement());
      }
      Block block = createBracifiedCopy(ifStatement.getAST(), ifStatement.getThenStatement());
      ifStatement.setThenStatement(block);
    }

    // check the else statement if it is a block
    Statement elseStatement = ifStatement.getElseStatement();
    if (elseStatement != null && !(elseStatement instanceof Block)) {

      // in case the else statement is an further if statement
      // (else if)
      // do the recursion
      if (elseStatement instanceof IfStatement) {
        bracifyIfStatement((IfStatement) elseStatement);
      } else {
        // change the else statement to a block
        // Block block = ifStatement.getAST().newBlock();
        // block.statements().add(ASTNode.copySubtree(block.getAST(),
        // elseStatement));
        Block block = createBracifiedCopy(ifStatement.getAST(), ifStatement.getElseStatement());
        ifStatement.setElseStatement(block);
      }
    }
  }


  @SuppressWarnings("unchecked")
  private Block createBracifiedCopy(AST ast, Statement body) {
    Block block = ast.newBlock();
    block.statements().add(ASTNode.copySubtree(block.getAST(), body));
    return block;
  }

  //32-ImportDeclaration
  public boolean visit(ImportDeclaration node) {
    print(node);
    return true;
  }

  //33-InfixExpression
  public boolean visit(InfixExpression node) {
    print(node);
    return true;
  }

  //34-InstanceofExpression
  public boolean visit(InstanceofExpression node) {
    print(node);
    return true;
  }

  //35-Initializer
  public boolean visit(Initializer node) {
    print(node);
    return true;
  }

  //36-Javadoc
  public boolean visit(Javadoc node) {
    print(node);
    return true;
    //return this.visitDocTags;
  }

  //37-LabeledStatement
  public boolean visit(LabeledStatement node) {
    print(node);
    return true;
  }

  //38-LineComment
  public boolean visit(LineComment node) {
    print(node);
    return true;
  }

  //39-MarkerAnnotation
  public boolean visit(MarkerAnnotation node) {
    print(node);
    return true;
  }

  //40-MemberRef
  public boolean visit(MemberRef node) {
    print(node);
    return true;
  }

  //41-MemberValuePair
  public boolean visit(MemberValuePair node) {
    print(node);
    return true;
  }

  //42-MethodRef
  public boolean visit(MethodRef node) {
    print(node);
    return true;
  }

  //43-MethodRefParameter
  //Note: it belongs to the category of Expression (not Statement) in JDT
  public boolean visit(MethodRefParameter node) {
    print(node);
    return true;
  }

  //44-MethodDeclaration
  public boolean visit(MethodDeclaration node) {
    print(node);
    return true;
  }

  //45-MethodInvocation
  public boolean visit(MethodInvocation node) {
    print(node);
    return true;
  }

  //46-Modifier
  public boolean visit(Modifier node) {
    print(node);
    return true;
  }

  //47-NormalAnnotation
  public boolean visit(NormalAnnotation node) {
    print(node);
    return true;
  }

  //48-NullLiteral
  public boolean visit(NullLiteral node) {
    print(node);
    return true;
  }

  //49-NumberLiteral
  public boolean visit(NumberLiteral node) {
    print(node);
    return true;
  }

  //50-PackageDeclaration
  public boolean visit(PackageDeclaration node) {
    print(node);
    return true;
  }

  //51-ParameterizedType
  public boolean visit(ParameterizedType node) {
    print(node);
    return true;
  }

  //52-ParenthesizedExpression
  public boolean visit(ParenthesizedExpression node) {
    print(node);
    return true;
  }

  //53-PostfixExpression
  public boolean visit(PostfixExpression node) {
    print(node);
    return true;
  }

  //54-PrefixExpression
  public boolean visit(PrefixExpression node) {
    print(node);
    return true;
  }

  //55-PrimitiveType
  public boolean visit(PrimitiveType node) {
    print(node);
    return true;
  }

  //56-QualifiedName
  public boolean visit(QualifiedName node) {
    print(node);
    return true;
  }

  //57-QualifiedType
  public boolean visit(QualifiedType node) {
    print(node);
    return true;
  }

  //58-ReturnStatement
  public boolean visit(ReturnStatement node) {
    print(node);
    return true;
  }

  //59-SimpleName
  public boolean visit(SimpleName node) {
    print(node);
    return true;
  }

  //60-SimpleType
  public boolean visit(SimpleType node) {
    print(node);
    return true;
  }

  //61-SingleMemberAnnotation
  public boolean visit(SingleMemberAnnotation node) {
    print(node);
    return true;
  }

  //62-SingleVariableDeclaration
  public boolean visit(SingleVariableDeclaration node) {
    print(node);
    return true;
  }

  //63-StringLiteral
  public boolean visit(StringLiteral node) {
    print(node);
    return true;
  }

  //64-SuperConstructorInvocation
  public boolean visit(SuperConstructorInvocation node) {
    print(node);
    return true;
  }

  //65-SuperFieldAccess
  public boolean visit(SuperFieldAccess node) {
    print(node);
    return true;
  }

  //66-SuperMethodInvocation
  public boolean visit(SuperMethodInvocation node) {
    print(node);
    return true;
  }

  //67-SwitchCase
  public boolean visit(SwitchCase node) {
    print(node);
    return true;
  }

  //68-SwitchStatement
  public boolean visit(SwitchStatement node) {
    print(node);
    return true;
  }

  //69-SynchronizedStatement
  public boolean visit(SynchronizedStatement node) {
    print(node);
    return true;
  }

  //70-TagElement
  public boolean visit(TagElement node) {
    print(node);
    return true;
  }

  //71-TextElement
  public boolean visit(TextElement node) {
    print(node);
    return true;
  }

  //72-ThisExpression
  public boolean visit(ThisExpression node) {
    print(node);
    return true;
  }

  //73-ThrowStatement
  public boolean visit(ThrowStatement node) {
    print(node);
    return true;
  }

  //74-TryStatement
  public boolean visit(TryStatement node) {
    print(node);
    return true;
  }

  //75-TypeDeclaration
  public boolean visit(TypeDeclaration node) {
    print(node);
    return true;
  }

  //76-TypeDeclarationStatement
  public boolean visit(TypeDeclarationStatement node) {
    print(node);
    return true;
  }

  //77-TypeLiteral
  public boolean visit(TypeLiteral node) {
    print(node);
    return true;
  }

  //78-TypeParameter
  public boolean visit(TypeParameter node) {
    print(node);
    return true;
  }

  //79-UnionType
  public boolean visit(UnionType node) {
    print(node);
    return true;
  }

  //80-VariableDeclarationExpression
  public boolean visit(VariableDeclarationExpression node) {
    print(node);
    return true;
  }

  //81-VariableDeclarationStatement
  public boolean visit(VariableDeclarationStatement node) {
    print(node);
    return true;
  }

  //82-VariableDeclarationFragment
  public boolean visit(VariableDeclarationFragment node) {
    print(node);
    return true;
  }

  //83-WhileStatement
  public boolean visit(WhileStatement node) {
    print(node);
    return true;
  }

  //84-WildcardType
  public boolean visit(WildcardType node) {
    print(node);
    return true;
  }

  public void print(ASTNode node) {
    String nodeType = ASTNode.nodeClassForType(node.getNodeType()).getSimpleName();
    System.out.println("Line" + this.getStartLine(node) + ": " + nodeType);
    System.out.println(node.toString());
    System.out.println();
  }

  public long getStartLine(ASTNode node) {
    CompilationUnit unit = (CompilationUnit) node.getRoot();
    int startPoint = node.getStartPosition();
    int startLineNumber = unit.getLineNumber(startPoint);
    return startLineNumber;
  }

  public long getEndLine(ASTNode node) {
    CompilationUnit unit = (CompilationUnit) node.getRoot();
    int length = node.getLength();
    int startPoint = node.getStartPosition();
    int endPoint = startPoint + length - 1;
    int endLineNumber = unit.getLineNumber(endPoint);
    return endLineNumber;
  }

  public void DFS(List<ASTCompilationUnit> unitElements) {
    for (ASTCompilationUnit unitElement : unitElements) {
      CompilationUnit unit = unitElement.getCompilationUnit();
      System.out.println(unitElement.getSourceFilePath());
      unit.accept(this);
    }
  }

}
