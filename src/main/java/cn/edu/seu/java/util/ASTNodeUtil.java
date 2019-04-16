package cn.edu.seu.java.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import cn.edu.seu.java.ast.visitor.ASTClassVisitor;
import cn.edu.seu.java.ast.visitor.ASTMethodVisitor;
import cn.edu.seu.java.ast.visitor.McCabeMethodVisitor;

import cn.edu.seu.java.node.ASTClass;
import cn.edu.seu.java.node.ASTCompilationUnit;
import cn.edu.seu.java.node.ASTMethod;
import cn.edu.seu.java.parser.JavaASTParser;
import cn.edu.seu.java.parser.VersionASTRequestor;

public class ASTNodeUtil {

  /**
   * @return text with removing all comments
   */
  public String getFormalizedCode(final ASTNode node) {
    ASTNode copiedAST = ASTNode.copySubtree(node.getAST(), node);
    copiedAST.accept(new ASTVisitor() {
      public boolean visit(Javadoc node) {
        node.delete();
        return true;
      }

      public boolean visit(LineComment node) {
        node.delete();
        return true;
      }

      public boolean visit(BlockComment node) {
        node.delete();
        return true;
      }
    });
    return copiedAST.toString();
  }

  public char[] getMainTypeName(CompilationUnit cu, String path) {
    char[] mainTypeName = null;
    @SuppressWarnings("unchecked")
    List<AbstractTypeDeclaration> types = cu.types();

    if (types.size() == 1) {
      AbstractTypeDeclaration classType = types.get(0);
      mainTypeName = classType.getName().getFullyQualifiedName().toCharArray();
    } else {
      for (AbstractTypeDeclaration type : types) {
        if (type.getModifiers() == Modifier.PUBLIC) {
          AbstractTypeDeclaration classType = (AbstractTypeDeclaration) type;
          mainTypeName = classType.getName().getFullyQualifiedName().toCharArray();
          if (mainTypeName != null) {
            break;
          }
        }
      }
    }

    if ((cu.types().size() > 0) && (mainTypeName == null)) {
      mainTypeName = path.toString()
          .substring(path.toString().lastIndexOf(System.getProperty("file.separator")) + 1,
              path.toString().length() - 5).toCharArray();
    }
    return mainTypeName;
  }

  public static int getLinesOfCode(final CompilationUnit node) {
    return getEndLine(node) - getStartLine(node) + 1;
  }

  public static int getLinesOfCode(final TypeDeclaration node) {
    return getEndLine(node) - getStartLine(node) + 1;
  }

  public static int getLinesOfCode(final MethodDeclaration node) {
    return getEndLine(node) - getStartLine(node) + 1;
  }

  public static int getLocWithoutCommentsAndBlankLinesFromASTNode(final ASTNode node) {
    ASTNode copiedAST = ASTNode.copySubtree(node.getAST(), node);
    copiedAST.accept(new ASTVisitor() {
      public boolean visit(Javadoc node) {
        node.delete();
        return true;
      }

      public boolean visit(LineComment node) {
        node.delete();
        return true;
      }

      public boolean visit(BlockComment node) {
        node.delete();
        return true;
      }
    });
    return StringUtil.getLOCWithRemovingBlankLines(copiedAST.toString());
  }

  public static long getlinesOfVersion(List<ASTCompilationUnit> units) {
    long loc = 0;
    for (ASTCompilationUnit unit : units) {
      loc = loc + ASTNodeUtil.getLinesOfCode(unit.getCompilationUnit());
    }
    return loc;
  }

  /**
   * @param node : ASTCompilationUnit
   * @return total number of LOC which excluding blank lines, comments(line, block and javadoc
   * comment)
   */
  public static int getLocWithoutCommentsAndBlankLines(final ASTCompilationUnit node) {
    CompilationUnit unit = node.getCompilationUnit();
    ASTNode copiedAST = ASTNode.copySubtree(unit.getAST(), unit);
    copiedAST.accept(new ASTVisitor() {
      public boolean visit(Javadoc node) {
        node.delete();
        return true;
      }

      public boolean visit(LineComment node) {
        node.delete();
        return true;
      }

      public boolean visit(BlockComment node) {
        node.delete();
        return true;
      }
    });
    return StringUtil.getLOCWithRemovingBlankLines(copiedAST.toString());
  }

  public static int getLocOfJavadocs(final ASTCompilationUnit node) {
    CompilationUnit unit = node.getCompilationUnit();
    int commentLines = 0;

    // this design is not good enough, it should return empty list when the
    // parsing failed
    @SuppressWarnings("unchecked")
    List<Comment> comments = unit.getCommentList();
    if (comments != null) {
      for (Comment comment : comments) {// attention: comments may contain null element
        if (comment != null && comment.getNodeType() == ASTNode.JAVADOC) {
          commentLines = commentLines + StringUtil.getLOCWithRemovingBlankLines(comment.toString());
        }
      }
    }
    return commentLines;
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

  // herein, only consider TypeDeclaration, other cases are ignored.
  // maybe, a source file can contain only one enum class or none-public class
  public static String getCompilationUnitName(final CompilationUnit node) {
    String nameOfPublicClass = "";

    if (node == null) {
      return nameOfPublicClass;
    }

    @SuppressWarnings("unchecked")
    List<AbstractTypeDeclaration> typeList = node.types();
    for (AbstractTypeDeclaration abstractTypeDec : typeList) {
      if (abstractTypeDec.getNodeType() == ASTNode.TYPE_DECLARATION) {
        TypeDeclaration typeDec = (TypeDeclaration) abstractTypeDec;
        List<Modifier> modifiers = ASTNodeUtil.getListOfModifier(typeDec);
        for (Modifier standardModifer : modifiers) {
          String keyWord = standardModifer.getKeyword().toString();
          if ("public".equals(keyWord)) {
            nameOfPublicClass = typeDec.getName().toString();
            return nameOfPublicClass;
          }
        }
      }
    }
    return nameOfPublicClass;
  }

  public static String getCompilationUnitName(TypeDeclaration node) {
    CompilationUnit unit = (CompilationUnit) node.getRoot();
    return ASTNodeUtil.getCompilationUnitName(unit);
  }

  /**
   * extract classes including top-level class, member class, but except enum class and local class
   * located in a statement (e.g., method, if-statement)
   */
  public static List<ASTClass> getASTClasses(final ASTCompilationUnit unit) {
    ASTClassVisitor typeVisitor = new ASTClassVisitor(unit);
    return typeVisitor.getListOfASTClass();
  }

  public static List<ASTClass> getListOfTopASTClass(final ASTCompilationUnit unitNode) {
    @SuppressWarnings("unchecked")
    List<AbstractTypeDeclaration> list = unitNode.getCompilationUnit().types();
    List<ASTClass> listOfASTClass = new ArrayList<ASTClass>();
    for (AbstractTypeDeclaration type : list) {
      if (type.getNodeType() == ASTNode.TYPE_DECLARATION) {
        TypeDeclaration typeDeclaration = (TypeDeclaration) type;
        ASTClass classNode = new ASTClass(unitNode.getSourceFilePath(), typeDeclaration);
        classNode.setProjectName(unitNode.getProjectName());
        classNode.setVersionNo(unitNode.getVersionNum());
        classNode.setVersionPath(unitNode.getVersionPath());
        classNode.setPackageName(unitNode.getPackageName());
        classNode.setUnitName(unitNode.getCompilationUnitName());
        classNode.setSourceDirectory(unitNode.getSourceDirectory());
        classNode.setRelativeSourcePath(unitNode.getRelativeSourceFilePath());
        listOfASTClass.add(classNode);
      }
    }
    return listOfASTClass;
  }

  public static List<ASTClass> getTopASTClasses(final List<ASTCompilationUnit> listOfUnits) {
    List<ASTClass> listOfASTClass = new ArrayList<ASTClass>();
    for (ASTCompilationUnit unit : listOfUnits) {
      listOfASTClass.addAll(getListOfTopASTClass(unit));
    }
    return listOfASTClass;
  }

  /**
   * extract classes including top-level class, member class, but except enum class and local class
   * enclosed within a method or statement
   */
  public static List<ASTClass> getASTClasses(final List<ASTCompilationUnit> listOfUnits) {
    List<ASTClass> listOfASTClass = new ArrayList<ASTClass>();
    for (ASTCompilationUnit unit : listOfUnits) {
      ASTClassVisitor typeVisitor = new ASTClassVisitor(unit);
      List<ASTClass> temp = typeVisitor.getListOfASTClass();
      listOfASTClass.addAll(temp);
    }
    return listOfASTClass;
  }

  public static List<ASTClass> getASTClasses(String versionPath) {
    List<ASTCompilationUnit> unitList = ASTNodeUtil.getASTCompilationUnits(versionPath);
    List<ASTClass> classList = new ArrayList<ASTClass>();
    for (ASTCompilationUnit unit : unitList) {
      classList.addAll(ASTNodeUtil.getASTClasses(unit));
    }
    return classList;
  }

  public static String getFullyQualifedName(final ASTClass classNode) {
    return getFullyQualifedName(classNode.getTypeDeclaration());
  }

  public static String getFullyQualifiedTopClassName(final TypeDeclaration clazz) {
    final CompilationUnit unit = (CompilationUnit) clazz.getRoot();
    TypeDeclaration pointer = clazz;
    for (ASTNode node = clazz.getParent(); node != unit; node = node.getParent()) {
      if (node.getNodeType() == ASTNode.TYPE_DECLARATION) {
        pointer = (TypeDeclaration) node;
      }
    }
    // assume that package name already exists
    return unit.getPackage().getName().getFullyQualifiedName() + "." + pointer.getName()
        .getIdentifier();
  }

  public static String getFullyQualifiedTopClassName(final MethodDeclaration method) {
    final CompilationUnit unit = (CompilationUnit) method.getRoot();
    TypeDeclaration pointer = null;
    for (ASTNode node = method.getParent(); node != unit; node = node.getParent()) {
      if (node.getNodeType() == ASTNode.TYPE_DECLARATION) {
        pointer = (TypeDeclaration) node;
      }
    }
    if (pointer != null) {
      // assume that package name already exists
      if (unit.getPackage() != null) {
        return unit.getPackage().getName().getFullyQualifiedName() + "." + pointer.getName()
            .getIdentifier();
      } else {
        return pointer.getName().getIdentifier();
      }
    } else {
      return "";
    }
  }

  /**
   * @return packageName + chainedClassName
   */
  public static String getFullyQualifedName(final TypeDeclaration node) {
    // pacakageName.{OuterTypeDeclarationName.}TypeDeclarationName
    final CompilationUnit unit = (CompilationUnit) node.getRoot();
    String pacakageName = ASTNodeUtil.getPackageName(unit);
    String outClassName = ASTNodeUtil.getChainedParentClassName(node);
    if (outClassName.length() == 0) {
      return pacakageName + '.' + node.getName().getIdentifier();
    } else {
      return pacakageName + '.' + outClassName + '.' + node.getName().getIdentifier();
    }
  }

  public static String getFullyQualifedName(final EnumDeclaration node) {
    // pacakageName.{outerClassName$}enumDecName
    StringBuffer fullEnumDecName = new StringBuffer();
    final CompilationUnit unit = (CompilationUnit) node.getRoot();
    fullEnumDecName.append(ASTNodeUtil.getPackageName(unit) + ".");
    String chainedParentClassName = ASTNodeUtil.getChainedParentClassName(node);
    if (chainedParentClassName.isEmpty() != true) {
      fullEnumDecName.append(chainedParentClassName + ".");
    }
    fullEnumDecName.append(node.getName().getIdentifier());
    return fullEnumDecName.toString();
  }

  public static String getFullyQuallifedName(FieldDeclaration field) {
    ASTNode node = ASTNodeUtil.getClosestOuterTypeNode(field);
    if (node.getNodeType() == ASTNode.TYPE_DECLARATION) {
      return ASTNodeUtil.getFullyQualifedName((TypeDeclaration) node);
    } else if (node.getNodeType() == ASTNode.ENUM_DECLARATION) {
      return ASTNodeUtil.getFullyQualifedName((EnumDeclaration) node);
    } else if (node.getNodeType() == ASTNode.ANNOTATION_TYPE_DECLARATION) {
      return ASTNodeUtil.getFullyQualifedName((AnnotationTypeDeclaration) node);
    } else {
      return "";
    }
  }

  public static String getFullyQualifedName(final AnnotationTypeDeclaration node) {
    StringBuffer fullAnnotationTypeDecName = new StringBuffer();
    final CompilationUnit unit = (CompilationUnit) node.getRoot();
    fullAnnotationTypeDecName.append(ASTNodeUtil.getPackageName(unit) + ".");

    String chainedParentClassName = ASTNodeUtil.getChainedParentClassName(node);
    if (chainedParentClassName.isEmpty() != true) {
      fullAnnotationTypeDecName.append(chainedParentClassName + ".");
    }
    fullAnnotationTypeDecName.append(node.getName().getIdentifier());
    return fullAnnotationTypeDecName.toString();
  }

  /**
   * @return all concatenated name of nested types  (including class, enum, annotation) and itself
   * (if this ASTNode is type), otherwise, return empty string
   */
  public static String getChainedParentClassName(final ASTNode originalNode) {
    final CompilationUnit unit = (CompilationUnit) originalNode.getRoot();
    TypeDeclaration outerTypeDec = null;
    EnumDeclaration outerEnumDec = null;
    AnnotationTypeDeclaration outerAnnotationDec = null;
    StringBuffer sb = new StringBuffer();
    for (ASTNode node = originalNode.getParent(); node != unit; node = node.getParent()) {
      if (node.getNodeType() == ASTNode.TYPE_DECLARATION) {
        outerTypeDec = (TypeDeclaration) node;
        if (outerTypeDec.isLocalTypeDeclaration() == false) {
          sb.insert(0, outerTypeDec.getName().getIdentifier() + '.');
        }
      } else if (node.getNodeType() == ASTNode.ENUM_DECLARATION) {
        outerEnumDec = (EnumDeclaration) node;
        sb.insert(0, outerEnumDec.getName().getIdentifier() + '.');
      } else if (node.getNodeType() == ASTNode.ANNOTATION_TYPE_DECLARATION) {
        outerAnnotationDec = (AnnotationTypeDeclaration) node;
        sb.insert(0, outerAnnotationDec.getName().getIdentifier() + '.');
      }
    }

    int len = sb.length();
    if (len > 0) {
      sb.deleteCharAt(len - 1);
    }
    return sb.toString();
  }

  /**
   * return [packageName].[className|enumName].methodName([ParameterType{, ParameterType}])
   */
  public static String getFullyQualifedName(final MethodDeclaration node) {
    StringBuffer fullName = new StringBuffer();
    final CompilationUnit unit = (CompilationUnit) node.getRoot();
    String pacakageName = ASTNodeUtil.getPackageName(unit);
    String outerClassName = ASTNodeUtil.getChainedParentClassName(node);
    String signature = ASTNodeUtil.getSignature(node);
    if (outerClassName.length() == 0) {
      fullName.append(pacakageName + '.' + signature);
    } else {
      fullName.append(pacakageName + '.' + outerClassName + '.' + signature);
    }
    return fullName.toString();
  }

  public static String getFullyQualifedName(final ASTMethod method) {
    return ASTNodeUtil.getFullyQualifedName(method.getMethodDeclaration());
  }

  /**
   * @retrun methodName([parType {, parType } ]) e.g., getName(Color)
   */

  public static String getSignature(final MethodDeclaration node) {
    StringBuffer signature = new StringBuffer();
    signature.append(node.getName().getIdentifier());
    signature.append(ASTNodeUtil.getParameter(node));
    return signature.toString();
  }

  /**
   * if original method's signature is like "getName(a.b.c.Animal.Color c)", then, return"(Color)"
   * rather than "(a.b.c.Animal.Color)" i.e., only return simple name, ignoring fully qualified
   * modifiers.
   */
  public static String getParameter(final MethodDeclaration node) {
    StringBuffer parameter = new StringBuffer();
    parameter.append("(");
    @SuppressWarnings("unchecked")
    List<SingleVariableDeclaration> paraList = node.parameters();
    for (SingleVariableDeclaration par : paraList) {
      SimpleName sn = par.getName();
      ITypeBinding iBinding = sn.resolveTypeBinding();
      if (iBinding != null) {
        parameter.append(iBinding.getName() + ",");
      } else {
        parameter.append(par.getType().toString() + ",");
      }
    }
    if (paraList.size() > 0) {
      parameter.deleteCharAt(parameter.length() - 1);
    }
    parameter.append(")");
    return parameter.toString();
  }

  // methodName([parType {,parType}])
  public static String getSignature(final MethodInvocation node) {
    StringBuffer signature = new StringBuffer();
    signature.append(node.getName() + "(");
    signature.append(ASTNodeUtil.getMethodParameters(node));
    signature.append(")");
    return signature.toString();
  }

  public static String getMethodParameters(final MethodInvocation node) {
    StringBuffer methodParList = new StringBuffer();
    IMethodBinding iMBinding = node.resolveMethodBinding();
    if (iMBinding != null) {
      ITypeBinding[] iParTypeBinding = iMBinding.getParameterTypes();
      for (ITypeBinding iParTB : iParTypeBinding) {
        methodParList.append(iParTB.getName() + ",");
      }
    }
    if (methodParList.length() > 0) {
      methodParList.deleteCharAt(methodParList.length() - 1);
    }
    return methodParList.toString();
  }

  /**
   * @return direct outer node of code entity, it may include TypeDeclaration, EnumDeclaration or
   * AnnotationTypeDeclartion
   */
  public static ASTNode getClosestOuterTypeNode(ASTNode node) {
    ASTNode pointer = node;
    while (pointer != null && pointer != node.getRoot()) {
      if (pointer.getNodeType() == ASTNode.TYPE_DECLARATION
          || pointer.getNodeType() == ASTNode.ENUM_DECLARATION
          || pointer.getNodeType() == ASTNode.ANNOTATION_TYPE_DECLARATION) {
        break;
      }
      pointer = pointer.getParent();
    }
    return pointer;
  }

  public static ASTNode getOutermostTypeNode(ASTNode node) {
    ASTNode pointer = node;
    ASTNode furthestASTNode = node;
    while (pointer != null && pointer.getNodeType() != ASTNode.COMPILATION_UNIT) {
      if (pointer.getNodeType() == ASTNode.TYPE_DECLARATION
          || pointer.getNodeType() == ASTNode.ENUM_DECLARATION) {
        furthestASTNode = pointer;
      }
      pointer = pointer.getParent();
    }
    return furthestASTNode;
  }

  public static List<TypeDeclaration> getTopTypeDeclarations(final CompilationUnit node) {
    List<TypeDeclaration> listOfTypeDeclaration = new ArrayList<TypeDeclaration>();
    @SuppressWarnings("unchecked")
    List<AbstractTypeDeclaration> list = node.types();
    for (AbstractTypeDeclaration type : list) {
      if (type.getNodeType() == ASTNode.TYPE_DECLARATION) {
        listOfTypeDeclaration.add((TypeDeclaration) type);
      }
    }
    return listOfTypeDeclaration;
  }

  public static List<EnumDeclaration> getTopEnumDeclarations(final CompilationUnit node) {
    @SuppressWarnings("unchecked")
    List<AbstractTypeDeclaration> list = node.types();
    List<EnumDeclaration> listOfEnumDeclaration = new ArrayList<EnumDeclaration>();

    for (AbstractTypeDeclaration type : list) {
      if (type.getNodeType() == ASTNode.ENUM_DECLARATION) {
        listOfEnumDeclaration.add((EnumDeclaration) type);
      }
    }
    return listOfEnumDeclaration;
  }

  public static List<Modifier> getListOfModifier(TypeDeclaration typeDec) {
    // It may include more than one modifier
    List<Modifier> listOfStandardModifier = new ArrayList<Modifier>();
    @SuppressWarnings("unchecked")
    List<IExtendedModifier> list = typeDec.modifiers();
    if (list.size() > 0) {
      for (IExtendedModifier obj : list) {
        if (obj.isModifier()) {
          Modifier standardModifer = (Modifier) obj;
          listOfStandardModifier.add(standardModifer);
        }
      }
    }
    return listOfStandardModifier;
  }

  public static String getSuperClassType(TypeDeclaration typeDec) {
    String fullTypeName = "";
    if (typeDec.isInterface() != true) {// only consider class
      if (typeDec.getSuperclassType() != null) {// a class in java only
        // has ONE super class
        Type type = typeDec.getSuperclassType();
        if (type.isSimpleType()) {
          fullTypeName = ASTNodeUtil.getNameOfSimpleType((SimpleType) type);
        }
      }
    }
    return fullTypeName;
  }

  /*
   * a class may implement more than one interface
   */
  public static List<String> getSuperInterfaceTypes(TypeDeclaration typeDec) {
    List<String> superInterfaceName = new ArrayList<String>();
    @SuppressWarnings("unchecked")
    List<Type> list = typeDec.superInterfaceTypes();
    for (Type type : list) {
      // SimpleType refers to class type which may be defined by user or
      // third party library.
      if (type.isSimpleType()) { // TODO: other type will be considered
        // later
        String simpleName = ASTNodeUtil.getNameOfSimpleType((SimpleType) type);
        if (simpleName.equals("") != true) {
          superInterfaceName.add(simpleName);
        }
      }
    }
    return superInterfaceName;
  }

  private static String getNameOfSimpleType(SimpleType simpleType) {
    String name = "";
    ITypeBinding typeBinding = simpleType.resolveBinding();
    if (typeBinding != null) {
      name = typeBinding.getQualifiedName();
    } else {
      name = simpleType.getName().getFullyQualifiedName();
    }
    return name;
  }

  public static List<Initializer> getInitializers(TypeDeclaration node) {
    List<Initializer> initializerList = new ArrayList<Initializer>();
    @SuppressWarnings("unchecked")
    List<BodyDeclaration> bodyDecList = node.bodyDeclarations();
    for (BodyDeclaration body : bodyDecList) {
      if (body instanceof Initializer) {
        Initializer initializerNode = (Initializer) body;
        if (initializerNode != null) {
          initializerList.add(initializerNode);
        }
      }
    }
    return initializerList;
  }

  public static Set<String> getAllPackageName(List<ASTCompilationUnit> unitElements) {
    Set<String> setOfPackgeNamesInVersion = new HashSet<String>();
    for (ASTCompilationUnit element : unitElements) {
      String packageName = ASTNodeUtil.getPackageName(element.getCompilationUnit());
      if (packageName.equals("default") != true) {
        setOfPackgeNamesInVersion.add(packageName);
      }
    }
    return setOfPackgeNamesInVersion;
  }

  /**
   *
   * @param unit
   * @return
   */
  public static String getPackageName(final CompilationUnit unit) {
    if (unit.getPackage() != null) {
      return unit.getPackage().getName().getFullyQualifiedName();
    } else {
      return "";
    }
  }

  public static String getName(TypeDeclaration type) {
    String typeDecName = type.getName().getFullyQualifiedName();
    for (ASTNode node = type.getParent(); node != node.getRoot(); node = node.getParent()) {
      if (node.getNodeType() == ASTNode.TYPE_DECLARATION) {
        TypeDeclaration outerType = (TypeDeclaration) node;
        typeDecName = outerType.getName().getFullyQualifiedName() + '.' + typeDecName;
      }
    }
    return typeDecName;
  }

  public static String getName(EnumDeclaration enumDec) {
    String enumDecName = enumDec.getName().getFullyQualifiedName();
    for (ASTNode node = enumDec.getParent(); node != node.getRoot(); node = node.getParent()) {
      if (node.getNodeType() == ASTNode.TYPE_DECLARATION) {
        TypeDeclaration outerType = (TypeDeclaration) node;
        enumDecName = outerType.getName().getFullyQualifiedName() + '.' + enumDecName;
      }
    }
    return enumDecName;
  }

  public static List<String> getName(FieldDeclaration field) {
    LinkedList<String> list = new LinkedList<String>();
    /*
     * @SuppressWarnings("unchecked") // IExtendedModifier: Modifier +
     * Annotation List<IExtendedModifier> modifiers = field.modifiers(); for
     * (IExtendedModifier iModifier : modifiers) { if
     * (iModifier.isModifier() == true) { Modifier m = (Modifier) iModifier;
     * result.append(m.getKeyword().toString() + " "); } }
     */
    @SuppressWarnings("unchecked")
    List<VariableDeclarationFragment> variableDecFragments = field.fragments();
    for (VariableDeclarationFragment var : variableDecFragments) {
      list.add(var.getName().getIdentifier());
    }
    return list;
  }

  public static boolean containsLocalClass(ASTMethod m) {
    MethodDeclaration node = m.getMethodDeclaration();
    Block block = node.getBody();
    boolean flag = false;
    if (block != null) {
      @SuppressWarnings("unchecked")
      List<Statement> stmts = block.statements();
      for (Statement stmt : stmts) {
        if (stmt.getNodeType() == ASTNode.TYPE_DECLARATION_STATEMENT) {
          flag = true;
          break;
        }
      }
    }
    return flag;
  }

  public static boolean isInLocalClass(ASTMethod methodEntity) {
    MethodDeclaration node = methodEntity.getMethodDeclaration();
    return isInLocalClass(node);
  }

  public static void isNotExitedThenCreatPath(String filePath) {
    File f = new File(filePath);
    if (f.exists() != true) {
      f.mkdirs();
    }
  }

  public static void eliminateCommonElement(List<String> list1, List<String> list2) {
    List<String> aList1 = new LinkedList<String>(list1);
    List<String> aList2 = new LinkedList<String>(list2);
    for (String str1 : aList1) {
      for (String str2 : aList2) {
        if (str1.equals(str2)) {
          list1.remove(str1);
          list2.remove(str2);
        }
      }
    }
  }

  public static int getStartIntervalIndex(int loc) {
    int up = 10000;
    int[] a = new int[up];
    for (int i = 0; i < up; i++) {
      a[i] = i;
    }

    int Start = 0;
    for (int i = 1; i < a.length - 4; i = i + 5) {
      if (a[i] <= loc && loc <= a[i + 4]) {
        Start = i;
        break;
      }
    }
    return Start;
  }

  /**
   * @return all methods that is not enclosed in anonymous class, local class or enum class
   */
  public static List<ASTMethod> getASTMethods(List<ASTCompilationUnit> units) {
    ASTMethodVisitor visitor = new ASTMethodVisitor(units);
    return visitor.getASTMethodsNotEnclosedInLocalClassAndEnumClassAndAnonymousClass();
  }

  public static List<ASTMethod> getASTMethods(ASTCompilationUnit unit) {
    ASTMethodVisitor visitor = new ASTMethodVisitor(unit);
    return visitor.getASTMethodsNotEnclosedInLocalClassAndEnumClassAndAnonymousClass();
  }

  /**
   * @return all ASTMethods which are not nested in local class, enum class and anonymous class
   */
  public static List<ASTMethod> getASTMethods(ASTClass classNode) {
    ASTMethodVisitor visitor = new ASTMethodVisitor(classNode);
    return visitor.getASTMethodsNotEnclosedInLocalClassAndEnumClassAndAnonymousClass();
  }

  public static List<ASTMethod> getASTMethods(String projectVersionPath) {
    List<ASTCompilationUnit> unitElements = ASTNodeUtil.getASTCompilationUnits(projectVersionPath);
    return ASTNodeUtil.getASTMethods(unitElements);
  }

  public static List<ASTClass> getListOfTopASTClass(String projectVersionPath) {
    List<ASTCompilationUnit> unitElements = getASTCompilationUnits(projectVersionPath);
    List<ASTClass> listOfTopASTClass = new LinkedList<ASTClass>();
    for (ASTCompilationUnit unitElement : unitElements) {
      listOfTopASTClass.addAll(ASTNodeUtil.getListOfTopASTClass(unitElement));
    }
    return listOfTopASTClass;
  }

  /**
   * @param versionPath the version path of a specified project, e.g.,
   * F:/commit-corpus/guava/guava2009-10-06-00-31-24-a194b64
   * @return corresponding compilation units of java source files in specified version
   */
  public static List<ASTCompilationUnit> getASTCompilationUnits(String versionPath) {
    VersionASTRequestor astRequestor = new VersionASTRequestor(versionPath);
    JavaASTParser parser = new JavaASTParser(astRequestor);
    parser.generateASTs();
    return astRequestor.getASTCompilationUnits();
  }

  public static List<ASTCompilationUnit> getASTCompilationUnits(String projectName,
      String versionNum,
      String versionPath) {
    VersionASTRequestor astRequestor = new VersionASTRequestor(projectName, versionNum,
        versionPath);
    JavaASTParser parser = new JavaASTParser(astRequestor);
    parser.generateASTs();
    return astRequestor.getASTCompilationUnits();
  }

  public static String[] mergeToStringArray(String firstValue, Integer[] sourceArray) {
    String[] targetAarray = new String[sourceArray.length + 1];
    targetAarray[0] = firstValue;
    for (int i = 0; i < sourceArray.length; i++) {
      targetAarray[i + 1] = String.valueOf(sourceArray[i]);
    }
    return targetAarray;
  }

  public static String[] mergeToStringArray(String firstValue, int[] sourceArray) {
    String[] targetAarray = new String[sourceArray.length + 1];
    targetAarray[0] = firstValue;
    for (int i = 0; i < sourceArray.length; i++) {
      targetAarray[i + 1] = String.valueOf(sourceArray[i]);
    }
    return targetAarray;
  }

  public static String[] mergeToStringArray(String firstValue, String[] sourceArray) {
    String[] targetAarray = new String[sourceArray.length + 1];
    targetAarray[0] = firstValue;
    for (int i = 0; i < sourceArray.length; i++) {
      targetAarray[i + 1] = sourceArray[i];
    }
    return targetAarray;
  }

  /**
   * @return true if method is enclosed within local TypeDeclaration or AnonymousTypeDeclaration.
   */
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

  /**
   * @return true if method is enclosed in enum class.
   */
  public static boolean isInEnumClass(MethodDeclaration node) {
    boolean flag = false;
    ASTNode pointer = node;
    while (pointer != null && pointer != node.getRoot()) {
      int typeCode = pointer.getNodeType();
      if (typeCode == ASTNode.ENUM_DECLARATION) {
        break;
      }
      pointer = pointer.getParent();
    }
    if (pointer.getNodeType() == ASTNode.ENUM_DECLARATION) {
      flag = true;
    }
    return flag;
  }

  public static String readFileContent(String absoluteFilePath) {
    StringBuffer sb = new StringBuffer();
    File file = new File(absoluteFilePath);
    if (file.isFile()) {
      InputStreamReader isr = null;
      char[] buf = new char[1024];
      int len;
      try {
        isr = new InputStreamReader(new FileInputStream(absoluteFilePath));
        try {
          while ((len = isr.read(buf)) > 0) {
            sb.append(buf, 0, len);
          }
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      } catch (FileNotFoundException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      } finally {
        if (isr != null) {
          try {
            isr.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    } else {
      System.out.println("Invalid File Path: " + absoluteFilePath);
    }
    return sb.toString();
  }

  /**
   * e.g., Animal a = new Animal(); a.getColor(); suppose user-defined Animal class is declared in
   * package "a.b.c"; thus, getFullUserDefinedDeclaringClassName("a.getColor()") will return
   * "a.b.c.Animal"
   */

  public static String getFullUserDefinedDeclaringClassName(MethodInvocation node) {
    String fullyDeclaringClassOfMI = "";
    IMethodBinding imb = node.resolveMethodBinding();
    if (imb != null) {
      ITypeBinding iTypeBinding = imb.getDeclaringClass();
      if (iTypeBinding != null && iTypeBinding.isFromSource()) { // user-defined method
        fullyDeclaringClassOfMI = iTypeBinding.getQualifiedName();
        fullyDeclaringClassOfMI = StringUtil.convert(fullyDeclaringClassOfMI);
        // TODO: other strategies can remedy the parsing failure?
      }
    }
    return fullyDeclaringClassOfMI;
  }
}
