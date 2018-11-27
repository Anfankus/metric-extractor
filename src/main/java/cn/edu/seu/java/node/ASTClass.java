package cn.edu.seu.java.node;

import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;

import cn.edu.seu.java.ast.visitor.McCabeClassVisitor;
import cn.edu.seu.java.ast.visitor.McCabeMethodVisitor;
import cn.edu.seu.java.util.ASTNodeUtil;
import cn.edu.seu.java.util.StringUtil;

public class ASTClass extends JavaNode {

  private TypeDeclaration typeDeclaration;
  private String fullyQualifiedName;
  private String fullSuperClassName;
  private Vector<String> fullSuperInterfaceNames;
  private int classCyclomaticComplexity;
  private String sourceDirectory;
  private String relativeSourcePath;
  private String fullyQualifiedTopClassName;

  public ASTClass(String sourceFilePath, TypeDeclaration node) {
    this.sourceFilePath = sourceFilePath;
    this.typeDeclaration = node;

    this.fullyQualifiedName = ASTNodeUtil.getFullyQualifedName(node);
    this.fullyQualifiedTopClassName = ASTNodeUtil.getFullyQualifiedTopClassName(node);

    this.fullSuperInterfaceNames = new Vector<String>();
    this.fullSuperClassName = "";
    Type type = this.typeDeclaration.getSuperclassType();
    if (type != null) {
      ITypeBinding iTypeBinding = type.resolveBinding();
      if (iTypeBinding != null && iTypeBinding.getPackage() != null) {
        String binaryName = iTypeBinding.getQualifiedName();
        this.fullSuperClassName = StringUtil.convert(binaryName);
      }
    }

    @SuppressWarnings("unchecked")
    List<Type> interfaces = node.superInterfaceTypes();
    for (Type t : interfaces) {
      ITypeBinding iTypeBinding = t.resolveBinding();
      if (iTypeBinding != null && iTypeBinding.getPackage() != null) {
        String binaryName = iTypeBinding.getQualifiedName();
        this.fullSuperInterfaceNames.add(StringUtil.convert(binaryName));
      }
    }
  }

  public boolean isAbstract() {
    int modifer = this.typeDeclaration.getModifiers();
    return Modifier.isAbstract(modifer);
  }

  public boolean isInterface() {
    return this.typeDeclaration.isInterface();
  }

  public boolean isTopLevelClass() {
    ITypeBinding iTypeBinding = this.typeDeclaration.resolveBinding();
    if (iTypeBinding != null) {
      return iTypeBinding.isTopLevel();
    }

    // try again if fail to binding
    boolean result = true;
    ASTNode parentNode = this.typeDeclaration.getParent();
    while (parentNode.getNodeType() != ASTNode.COMPILATION_UNIT) {
      if (parentNode.getNodeType() == ASTNode.TYPE_DECLARATION) {
        // bug fix: add a condition to teminate while loop
        result = false;
        break;
      }
      parentNode = parentNode.getParent();
    }
    return result;
  }

  public boolean hasSuperclass() {
    // only consider super class (e.g., like "XX extends YY")
    Type type = typeDeclaration.getSuperclassType();
    return type != null;
  }

  public boolean isDeprecated() {
    boolean fromMarkerAnnotation = false;
    @SuppressWarnings("unchecked")
    List<IExtendedModifier> modifiers = this.typeDeclaration.modifiers();
    for (IExtendedModifier iEModifier : modifiers) {
      if (iEModifier.isAnnotation()) {
        Annotation annotation = (Annotation) iEModifier;
        String name = annotation.getTypeName().getFullyQualifiedName();
        if (name.equals("Deprecated")) {
          fromMarkerAnnotation = true;
          break;
        }
      }
    }

    boolean fromJavadoc = false;
    Javadoc javadoc = typeDeclaration.getJavadoc();
    if (javadoc != null) {
      @SuppressWarnings("unchecked")
      List<TagElement> tags = javadoc.tags();
      for (TagElement tag : tags) {
        String tagname = tag.getTagName();
        if (tagname != null && tagname.equals("@deprecated")) {
          fromJavadoc = true;
          break;
        }
      }
    }

    return fromMarkerAnnotation || fromJavadoc;
  }

  /**
   * @return full superclass name if exist, otherwise return empty string.
   */
  public String getFullSuperclassName() {
    return this.fullSuperClassName;
  }

  public Vector<String> getFullSuperInterfaceNames() {
    return fullSuperInterfaceNames;
  }

  public TypeDeclaration getTypeDeclaration() {
    return typeDeclaration;
  }

  /**
   * WMC: weighted method in class
   *
   * @return WMC metric for a given class
   */
  public int cyclomaticComplexity() {
    // to avoid recomputation
    if (this.classCyclomaticComplexity == 0) {
      return this.getClassComplexity();
    } else {
      return this.classCyclomaticComplexity;
    }
  }

  private int getClassComplexity() {
    List<MethodDeclaration> allMethods = new LinkedList<MethodDeclaration>();
    typeDeclaration.accept(new ASTVisitor() {
      @Override
      public boolean visit(AnonymousClassDeclaration node) {
        // do not consider the method call within anonymous class
        return false;
      }

      @Override
      public boolean visit(TypeDeclarationStatement node) {
        // do not consider the method call within local class (localizing in method's body)
        return false;
      }

      @Override
      public boolean visit(MethodDeclaration node) {
        allMethods.add(node);
        return true;
      }
    });

    for (MethodDeclaration method : allMethods) {
      if (method.getBody() != null) {
        McCabeMethodVisitor visitor = new McCabeMethodVisitor(method);
        classCyclomaticComplexity =
            classCyclomaticComplexity + visitor.getMethodCyclomaticComplexity();
      }
    }
    return this.classCyclomaticComplexity;
  }

  @Override
  public int hashCode() {
    HashCodeBuilder b = new HashCodeBuilder(17, 37);
    return b.append(this.fullyQualifiedName).toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    EqualsBuilder eb = new EqualsBuilder();
    ASTClass other = (ASTClass) obj;
    return eb.append(this.fullyQualifiedName, other.getFullyQualifiedName()).isEquals();
  }

  public String toString() {
    return this.sourceFilePath + "\n" + this.fullyQualifiedName;
  }

  public void setSourceDirectory(String sourceDirectory) {
    this.sourceDirectory = sourceDirectory;
  }

  public String getSourceDirectory() {
    return sourceDirectory;
  }

  public void setRelativeSourcePath(String relativeSourcePath) {
    this.relativeSourcePath = relativeSourcePath;
  }

  public String getRelativeSourceFilePath() {
    return relativeSourcePath;
  }

  /**
   * @return packageName + chainedClassName
   */
  public String getFullyQualifiedName() {
    return this.fullyQualifiedName;
  }

  public String getFullyQualifiedTopClassName() {
    return fullyQualifiedTopClassName;
  }
}
