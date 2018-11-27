package cn.edu.seu.java.ast.visitor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.SimpleType;

import cn.edu.seu.java.node.ASTCompilationUnit;
import cn.edu.seu.java.util.ASTNodeUtil;

// this visitor can be used to identify coupling and cohesion
// need optimization
public class PackageDependencyVisitor extends ASTVisitor {

  private Map<String, Set<String>> pacakgeDependencyMap;
  private Set<String> dependedPackageSetInCurrentUnit;
  private String packageNameInCurrentUnit;

  public PackageDependencyVisitor() {
    pacakgeDependencyMap = new HashMap<String, Set<String>>();
  }

  @Override
  public boolean visit(CompilationUnit node) {
    this.dependedPackageSetInCurrentUnit = new HashSet<String>();
    return true;
  }

  public boolean visit(SimpleType node) {
    ITypeBinding binding = (ITypeBinding) node.resolveBinding();
    if (binding != null && binding.getPackage() != null) {
      String importpackageName = binding.getPackage().getName();
      dependedPackageSetInCurrentUnit.add(importpackageName);
      //System.out.println("importpackageName: " + importpackageName);
      //System.out.println();
    }
    return true;
  }

  public void travel(List<ASTCompilationUnit> unitElements) {
    for (ASTCompilationUnit unitElement : unitElements) {
      CompilationUnit unit = unitElement.getCompilationUnit();
      this.packageNameInCurrentUnit = ASTNodeUtil.getPackageName(unit);
      if (packageNameInCurrentUnit.equals("DefaultPackageName")) {
        unit.accept(this);
        this.unionPackage();
      }
    }
  }

  private void unionPackage() {
    if (pacakgeDependencyMap.containsKey(packageNameInCurrentUnit)) {
      Set<String> oldDependedPackageSet = pacakgeDependencyMap.get(packageNameInCurrentUnit);
      for (String packageName : oldDependedPackageSet) {
        dependedPackageSetInCurrentUnit.add(packageName);
      }
    }
    pacakgeDependencyMap.put(packageNameInCurrentUnit, dependedPackageSetInCurrentUnit);
  }
}