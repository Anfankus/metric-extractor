package cn.edu.seu.java.node;

import java.util.List;
import java.util.Vector;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import cn.edu.seu.java.util.ASTNodeUtil;

public class ASTEnum extends JavaNode {

  private EnumDeclaration node;
  private Vector<String> superInterfaceNames;
  private String fullyQualifiedName;

  public ASTEnum(String sourceFilePath, EnumDeclaration node) {
    this.sourceFilePath = sourceFilePath;
    this.node = node;
    this.fullyQualifiedName = ASTNodeUtil.getFullyQualifedName(node);
    this.superInterfaceNames = new Vector<String>();
    @SuppressWarnings("unchecked")
    List<Type> interfaces = node.superInterfaceTypes();
    for (Type t : interfaces) {
      ITypeBinding iType = t.resolveBinding();
      if (iType != null) {
        superInterfaceNames.add(iType.getName());
      }
    }
  }

  public String getFullyQualifiedName() {
    return this.fullyQualifiedName;
  }

  public EnumDeclaration getCodeEntity() {
    return node;
  }

  public Vector<String> getSuperInterfaces() {
    return superInterfaceNames;
  }

  @Override
  public int hashCode() {
    HashCodeBuilder b = new HashCodeBuilder(17, 37);
    String fullClassName = ASTNodeUtil.getFullyQualifedName(node);
    return b.append(fullClassName).toHashCode();
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
    ASTEnum other = (ASTEnum) obj;
    String otherPackageName = other.getPackageName();
    String otherTypeName = other.getCodeEntity().getName().getIdentifier();
    return this.packageName.equals(otherPackageName)
        && this.getCodeEntity().getName().getIdentifier().equals(otherTypeName);
  }

  public String toString() {
    return this.sourceFilePath + "\n" + node.getName().getIdentifier();
  }
}
