package cn.edu.seu.java.parser;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import cn.edu.seu.java.node.ASTCompilationUnit;
import cn.edu.seu.java.util.StringUtil;
import cn.edu.seu.java.util.TimeMonitor;
import data.io.util.IOUtil;

/**
 * @author Huihui Liu
 * @Time 2015-01-03
 */
public class JavaASTParser {

  private VersionASTRequestor astRequestor;
  private List<String> ignoredFilePaths;


  public JavaASTParser(String versionPath) {
    this.astRequestor = new VersionASTRequestor(versionPath);
  }

  public JavaASTParser(VersionASTRequestor astRequestor) {
    this.astRequestor = astRequestor;
  }

  public JavaASTParser(VersionASTRequestor astRequestor, List<String> ignoredSourceFilePaths) {
    this.astRequestor = astRequestor;
    this.ignoredFilePaths = ignoredSourceFilePaths;
  }

  public List<ASTCompilationUnit> generateASTs() {
    String versionPath = astRequestor.getVersionPath();
    if (this.isDirectory(versionPath)) {
      ASTParser parser = ASTParser.newParser(AST.JLS10);

      // classpathEntries: jar files for resolving blinding,
      String[] classpathEntries = StringUtil.getFilePaths(versionPath, ".jar");
      String[] sourcePathEntries = {versionPath};
      parser.setEnvironment(classpathEntries, sourcePathEntries, null, true);
      parser.setResolveBindings(true);
      parser.setBindingsRecovery(true);
      parser.setStatementsRecovery(true);
      parser.setKind(ASTParser.K_COMPILATION_UNIT);

      Map<String, String> complierOptions = JavaCore.getOptions();
      JavaCore.setComplianceOptions(JavaCore.VERSION_1_8, complierOptions);
      parser.setCompilerOptions(complierOptions);

      /*
       * parser.createASTs(String[] sourceFilePaths, String[] bindingKeys, String[] encodings
       * FileASTRequestor requestor, IProgressMonitor monitor)
       * String[] sourceFilePaths: paths of all *.java files in a version
       * String[] bindingKeys
       * String[] encodings
       * FileASTRequestor requestor
       * IProgressMonitor monitor
       */
      try {
        String[] sourceFilePaths = StringUtil.getFilePaths(versionPath, ".java");

        // make sure each ignored file path does not contain backslashes ("\")
        if (ignoredFilePaths != null) {
          int len = ignoredFilePaths.size();
          if (len > 0) {
            sourceFilePaths = StringUtil
                .remove(sourceFilePaths, ignoredFilePaths.toArray(new String[len]));
          }
        }

        parser.createASTs(sourceFilePaths, null, new String[0], astRequestor, null);
      } catch (Exception e) {
        String time = TimeMonitor.dateAndTime();
        System.out.println(
            "exception | " + time + " | " + "fail to creat ASTs in batch" + " | " + versionPath);
        IOUtil.catchError(
            "exception | " + time + " | " + "fail to creat ASTs in batch" + " | " + versionPath);
      }
    }

    return astRequestor.getASTCompilationUnits();
  }

  public List<CompilationUnit> generateASTs2() {
    String versionPath = astRequestor.getVersionPath();
    if (this.isDirectory(versionPath)) {
      ASTParser parser = ASTParser.newParser(AST.JLS10);

      // classpathEntries: jar files for resolving blinding,
      String[] classpathEntries = StringUtil.getFilePaths(versionPath, ".jar");
      String[] sourcePathEntries = {versionPath};
      parser.setEnvironment(classpathEntries, sourcePathEntries, null, true);
      parser.setResolveBindings(true);
      parser.setBindingsRecovery(true);
      parser.setStatementsRecovery(true);
      parser.setKind(ASTParser.K_COMPILATION_UNIT);

      Map<String, String> complierOptions = JavaCore.getOptions();
      JavaCore.setComplianceOptions(JavaCore.VERSION_1_8, complierOptions);
      parser.setCompilerOptions(complierOptions);

      /*
       * parser.createASTs(String[] sourceFilePaths, String[] bindingKeys, String[] encodings
       * FileASTRequestor requestor, IProgressMonitor monitor)
       * String[] sourceFilePaths: paths of all *.java files in a version
       * String[] bindingKeys
       * String[] encodings
       * FileASTRequestor requestor
       * IProgressMonitor monitor
       */
      try {
        String[] sourceFilePaths = StringUtil.getFilePaths(versionPath, ".java");

        // make sure each ignored file path does not contain backslashes ("\")
        if (ignoredFilePaths != null) {
          int len = ignoredFilePaths.size();
          if (len > 0) {
            sourceFilePaths = StringUtil
                .remove(sourceFilePaths, ignoredFilePaths.toArray(new String[len]));
          }
        }

        parser.createASTs(sourceFilePaths, null, new String[0], astRequestor, null);
      } catch (Exception e) {
        String time = TimeMonitor.dateAndTime();
        System.out.println(
            "exception | " + time + " | " + "fail to creat ASTs in batch" + " | " + versionPath);
        IOUtil.catchError(
            "exception | " + time + " | " + "fail to creat ASTs in batch" + " | " + versionPath);
      }
    }

    return astRequestor.getCompilationUnits();
  }

  private boolean isDirectory(String versionPath) {
    File file1 = new File(versionPath);
    if (file1.isDirectory() != true) {
      System.err.println("Invalid Project Version Path: " + versionPath);
    }
    return file1.isDirectory();
  }
}