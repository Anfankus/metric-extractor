package com.github.mauricioaniche.ck;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import java.util.regex.Pattern;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class ASTUtil {

  public static List<CompilationUnit> getASTs(String versionPath) {
    ASTParser parser = ASTParser.newParser(AST.JLS10);

    // classpathEntries: jar files for resolving blinding,
    String[] classpathEntries = FileUtil.getAllJarFiles(versionPath);
    String[] sourcePathEntries = {versionPath};
    parser.setEnvironment(classpathEntries, sourcePathEntries, null, true);

    Map<String, String> complierOptions = JavaCore.getOptions();
    JavaCore.setComplianceOptions(JavaCore.VERSION_1_8, complierOptions);
    parser.setCompilerOptions(complierOptions);

    parser.setResolveBindings(true);
    parser.setBindingsRecovery(true);
    parser.setStatementsRecovery(true);
    parser.setKind(ASTParser.K_COMPILATION_UNIT);

    List<CompilationUnit> units = new LinkedList<CompilationUnit>();
    FileASTRequestor requestor = new FileASTRequestor() {
      @Override
      public void acceptAST(String sourceFilePath, CompilationUnit cu) {
        cu.setProperty("sourceFilePath", sourceFilePath);
        units.add(cu);
      }
    };

    String[] sourceFilePaths = FileUtil.getAllJavaFiles(versionPath);
    parser.createASTs(sourceFilePaths, null, new String[0], requestor, null);

    return units;
  }

  public static int getLOC(final ASTNode node) {
    return getEndLine(node) - getStartLine(node) + 1;
  }

  public static int getLinesOfCode(final CompilationUnit node) {
    return getEndLine(node) - getStartLine(node) + 1;
  }

  public static int getLinesOfCode(final TypeDeclaration node) {
    return getEndLine(node) - getStartLine(node) + 1;
  }

  /*
   * sometimes, program will cast exception, because there does not exist ROOT
   * node. e.g., LineComment, more details can refer to JDT API
   */
  public static int getEndLine(final ASTNode node) {
    int startPoint = 0;
    int endLineNumber = 0;
    // LineComment, BlockComment, Javadoc can not directly cast to
    // CompilationUnit by using node.getRoot() method, instead, ONLY using getAlternateRoot();
    if (node.getNodeType() == ASTNode.JAVADOC) {
      Javadoc javaDoc = (Javadoc) node;
      ASTNode astNode = javaDoc.getAlternateRoot();
      if (astNode != null) {
        CompilationUnit unit = (CompilationUnit) astNode;
        startPoint = node.getStartPosition();
        int length = node.getLength();
        int endPoint = startPoint + length - 1;
        endLineNumber = unit.getLineNumber(endPoint);
      }
    } else if (node.getNodeType() == ASTNode.LINE_COMMENT) {
      LineComment lineComment = (LineComment) node;
      ASTNode astNode = lineComment.getAlternateRoot();
      if (astNode != null) {
        CompilationUnit unit = (CompilationUnit) astNode;
        startPoint = node.getStartPosition();
        int length = node.getLength();
        int endPoint = startPoint + length - 1;
        endLineNumber = unit.getLineNumber(endPoint);
      }

    } else if (node.getNodeType() == ASTNode.BLOCK_COMMENT) {
      BlockComment blockComment = (BlockComment) node;
      ASTNode astNode = blockComment.getAlternateRoot();
      if (astNode != null) {
        CompilationUnit unit = (CompilationUnit) astNode;
        startPoint = node.getStartPosition();
        int length = node.getLength();
        int endPoint = startPoint + length - 1;
        endLineNumber = unit.getLineNumber(endPoint);
      }
    } else {
      CompilationUnit unit = (CompilationUnit) node.getRoot();
      startPoint = node.getStartPosition();
      int length = node.getLength();
      int endPoint = startPoint + length - 1;
      endLineNumber = unit.getLineNumber(endPoint);
    }
    return endLineNumber;
  }

  public static int getStartLine(final ASTNode node) {
    int startPoint = 0;
    int startLineNumber = 0;
    // LineComment, BlockComment, Javadoc can not directly cast to
    // CompilationUnit by using node.getRoot() method, instead, using getAlternateRoot();
    if (node.getNodeType() == ASTNode.JAVADOC) {
      Javadoc javaDoc = (Javadoc) node;
      ASTNode astNode = javaDoc.getAlternateRoot();
      if (astNode != null) {
        CompilationUnit unit = (CompilationUnit) astNode;
        startPoint = node.getStartPosition();
        startLineNumber = unit.getLineNumber(startPoint);
      }
    } else if (node.getNodeType() == ASTNode.LINE_COMMENT) {
      LineComment lineComment = (LineComment) node;
      ASTNode astNode = lineComment.getAlternateRoot();
      if (astNode != null) {
        CompilationUnit unit = (CompilationUnit) astNode;
        startPoint = node.getStartPosition();
        startLineNumber = unit.getLineNumber(startPoint);
      }

    } else if (node.getNodeType() == ASTNode.BLOCK_COMMENT) {
      BlockComment blockComment = (BlockComment) node;
      ASTNode astNode = blockComment.getAlternateRoot();
      if (astNode != null) {
        CompilationUnit unit = (CompilationUnit) astNode;
        startPoint = node.getStartPosition();
        startLineNumber = unit.getLineNumber(startPoint);
      }
    } else {
      CompilationUnit unit = (CompilationUnit) node.getRoot();
      startPoint = node.getStartPosition();
      startLineNumber = unit.getLineNumber(startPoint);
    }
    return startLineNumber;
  }


}
