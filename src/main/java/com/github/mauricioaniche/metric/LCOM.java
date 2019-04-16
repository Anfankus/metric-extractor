package com.github.mauricioaniche.metric;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import com.github.mauricioaniche.ck.MetricValue;

import cn.edu.seu.java.util.ASTNodeUtil;
import data.io.util.IOUtil;

import com.github.mauricioaniche.ck.MetricReport;
import com.github.mauricioaniche.ck.MetricExtractor;

/**
 * LCOM: Lack of cohesion in methods
 */
public class LCOM implements MetricExtractor {

  private ArrayList<TreeSet<String>> methods;
  private Set<String> declaredFields;
  private List<MethodDeclaration> declaredMethods;

  public LCOM() {
    this.methods = new ArrayList<TreeSet<String>>();
    this.declaredFields = new HashSet<String>();
    this.declaredMethods = new LinkedList<MethodDeclaration>();
  }

  private class FieldAndMethodCollector extends ASTVisitor {

    @Override
    public boolean visit(AnonymousClassDeclaration node) {
      return false;
    }

    @Override
    public boolean visit(FieldDeclaration node) {
      @SuppressWarnings("unchecked")
      List<VariableDeclarationFragment> vdfs = node.fragments();
      for (VariableDeclarationFragment vdf : vdfs) {
        declaredFields.add(vdf.getName().getIdentifier());
      }
      return false;
    }

    @Override
    public boolean visit(MethodDeclaration node) {
      if (ASTNodeUtil.isInLocalClass(node) != true) {
        declaredMethods.add(node);
      }
      return false;
    }
  }

  private class FieldAccessChecker extends ASTVisitor {

    public FieldAccessChecker(MethodDeclaration md) {
      methods.add(new TreeSet<String>());
      Block block = md.getBody();
      if (block != null) {
        md.accept(this);
      }
    }

    @Override
    public boolean visit(SimpleName node) {
      //we only use weak condition to check whether simple name is field access
      String name = node.getIdentifier();
      if (declaredFields.contains(name)) {
        if (!methods.isEmpty()) {
          methods.get(methods.size() - 1).add(name);
        }
      }
      return false;
    }
  }

  @Override
  public void execute(ASTNode node, MetricReport report) {
    String fullyQualifiedName = (String) node.getProperty("fullyQualifiedName");
    if (fullyQualifiedName != null && fullyQualifiedName.isEmpty() == false) {
      node.accept(new FieldAndMethodCollector());

      String type = (String) node.getProperty("type");
      if (type.equals("interface") || this.declaredFields.size() == 0
          || this.declaredMethods.size() == 0) {
        int n = this.declaredMethods.size();
        int lcom = n * (n - 1) / 2;
        lcom = lcom > 0 ? lcom : 0;

        MetricValue ckn = report.getByClassName(fullyQualifiedName);
        ckn.setLcom(lcom);
      } else {
        for (MethodDeclaration md : this.declaredMethods) {
          new FieldAccessChecker(md);
        }

        MetricValue ckn = report.getByClassName(fullyQualifiedName);
        ckn.setLcom(this.getLCOM());
      }
    }
  }

  @SuppressWarnings("unchecked")
  private int getLCOM() {
    /*
     * LCOM = |P| - |Q| if |P| - |Q| > 0
     * where
     * P = set of all empty set intersections
     * Q = set of all nonempty set intersections
     */

    // extracted from https://github.com/dspinellis/ckjm
    int lcom = 0;
    for (int i = 0; i < methods.size(); i++) {
      TreeSet<String> intersection = methods.get(i);
      for (int j = i + 1; j < methods.size(); j++) {
        intersection = (TreeSet<String>) methods.get(i).clone();
        if (intersection.size() > 0) {
          intersection.retainAll(methods.get(j));
        }

        if (intersection.size() == 0) {
          lcom++;
        } else {
          lcom--;
        }
      }
    }
    return lcom > 0 ? lcom : 0;
  }

	/*private String toString(TreeSet<String> intersection) {
		StringBuffer sb = new StringBuffer();
		for (Iterator<String> iterator = intersection.iterator(); iterator.hasNext();) {
			String e = iterator.next();
			sb.append(e + " ");
		}
		return sb.toString().trim();
	}*/

}
