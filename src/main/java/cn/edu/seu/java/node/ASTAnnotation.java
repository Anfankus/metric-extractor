package cn.edu.seu.java.node;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import cn.edu.seu.java.util.ASTNodeUtil;

public class ASTAnnotation extends JavaNode {

  private AnnotationTypeDeclaration node;

  public ASTAnnotation(String sourceFilePath, AnnotationTypeDeclaration node) {
    this.sourceFilePath = sourceFilePath;
    this.node = node;
  }

  public AnnotationTypeDeclaration getCodeEntity() {
    return node;
  }

  public String getSourceFilePath() {
    return sourceFilePath;
  }
	
	/*@Override
	public int hashCode() {
		HashCodeBuilder b = new HashCodeBuilder(17, 37);
		String fullClassName = Helper.getFullyQualifedName(node);
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
		ASTAnnotation other = (ASTAnnotation) obj;
		return true; // need to update!
	}

	public String toString() {
		return this.sourceFilePath + "\n" + Helper.getFullyQualifedName(node);
	}
*/
}
