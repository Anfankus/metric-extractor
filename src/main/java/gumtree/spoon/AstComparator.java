package gumtree.spoon;

import gumtree.spoon.builder.SpoonGumTreeBuilder;
import gumtree.spoon.diff.Diff;
import gumtree.spoon.diff.DiffImpl;
import gumtree.spoon.diff.operations.Operation;
import spoon.SpoonModelBuilder;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.compiler.VirtualFile;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.text.html.parser.Entity;


import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.actions.model.Addition;
import com.github.gumtreediff.actions.model.Delete;
import com.github.gumtreediff.actions.model.Insert;
import com.github.gumtreediff.actions.model.Move;
import com.github.gumtreediff.actions.model.Update;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.TreeContext;

//import org.hamcrest.controller.Is;

/**
 * Computes the differences between two CtElements (i.e., compile-time element).
 *
 * @author Matias Martinez, matias.martinez@inria.fr
 */
public class AstComparator {

  static {
    // default 0.3
    // it seems that default value is really bad
    // 0.1 one failing much more changes
    // 0.2 one failing much more changes
    // 0.3 one failing test_t_224542
    // 0.4 fails for issue31
    // 0.5 fails for issue31
    // 0.6 OK
    // 0.7 1 failing
    // 0.8 2 failing
    // 0.9 two failing tests with more changes
    // see GreedyBottomUpMatcher.java in Gumtree
    System.setProperty("gumtree.match.bu.sim", "0.6");

    // default 2
    // 0 is really bad for 211903 t_224542 225391 226622
    // 1 is required for t_225262 and t_213712 to pass
    System.setProperty("gumtree.match.gt.minh", "1");

    // default 1000
    // 0 fails
    // 1 fails
    // 10 fails
    // 100 OK
    // 1000 OK
    // see AbstractBottomUpMatcher#SIZE_THRESHOD in Gumtree
    // System.setProperty("gumtree.match.bu.size","10");
    // System.setProperty("gt.bum.szt", "1000");
  }

  public AstComparator() {

  }

  /**
   * compares two java files
   */
  @SuppressWarnings("rawtypes")
  public void compare(File f1, File f2) throws Exception {
    List<CtType<?>> list1 = getCtType(f1);
    List<CtType<?>> list2 = getCtType(f2);

    for (CtType ctTypeLeft : list1) {
      CtType toMatch = null;
      for (CtType ctTypeRight : list2) {
        if (ctTypeLeft.getQualifiedName().equals(ctTypeRight.getQualifiedName())) {
          toMatch = ctTypeRight;
          Diff diff = this.compare(ctTypeLeft, ctTypeRight);
          this.runDiff(diff);
          break;
        }
      }

      if (toMatch == null) { // indicates deleted class in old version
        System.out.println("----------------------------------------------------");
        System.out.println("Delete: " + ctTypeLeft.getQualifiedName());
      }
    }

    for (CtType ctTypeRight : list2) {
      CtType toMatch = null;
      for (CtType ctTypeLeft : list1) {
        if (ctTypeRight.getQualifiedName().equals(ctTypeLeft.getQualifiedName())) {
          toMatch = ctTypeLeft;
          break;
        }
      }
      if (toMatch == null) { // indicates added class in new version
        System.out.println("----------------------------------------------------");
        System.out.println("Insert: " + ctTypeRight.getQualifiedName());
      }
    }
  }

  @SuppressWarnings("rawtypes")
  private void runDiff(Diff diff) {
    // note that Diff can not identify the differences about javadoc between two files from
    // a pair of consecutive versions
    for (Operation<?> op : diff.getRootOperations()) {
      System.out.println("----------------------------------------------------");
      Action action = op.getAction();
      CtElement element = (CtElement) action.getNode()
          .getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT);

      CtElement parent = element;
      while (parent.getParent() != null && !(parent
          .getParent() instanceof spoon.reflect.declaration.CtPackage)) {
        parent = parent.getParent();
      }

      // System.out.println("pretty print: " + partialElementPrint(parent));  // something seems to be odd

      if (parent instanceof spoon.reflect.declaration.CtClass) {
        CtClass ctClass = (CtClass) parent;
        System.out.println("class name: " + ctClass.getQualifiedName());
      } else if (parent instanceof spoon.reflect.declaration.CtInterface) {
        CtInterface ctInterface = (CtInterface) parent;
        System.out.println("interface name: " + ctInterface.getQualifiedName());
      } else if (parent instanceof spoon.reflect.declaration.CtEnum) {
        CtEnum ctEnum = (CtEnum) parent;
        System.out.println("enum name:" + ctEnum.getQualifiedName());
      }

      if (action instanceof Delete) {
        System.out.println("ChangeType: " + action.getClass().getSimpleName());
        System.out.println("Line: " + element.getPosition().getLine());
        System.out.println("Label: " + action.getNode().getLabel());
      } else if (action instanceof Insert) {
        System.out.println("ChangeType: " + action.getClass().getSimpleName());
        System.out.println("Line: " + element.getPosition().getLine());
        System.out.println("Label: " + partialElementPrint(element));
      } else if (action instanceof Move) {
        CtElement elementDest = (CtElement) action.getNode()
            .getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT_DEST);
        System.out.println("ChangeType: " + action.getClass().getSimpleName());
        System.out.println("OldLine: " + element.getPosition().getLine());
        System.out.println("NewLine: " + elementDest.getPosition().getLine());
        System.out.println("Label: " + action.getNode().getLabel());
      } else if (action instanceof Update) {
        System.out.println("ChangeType: " + action.getClass().getSimpleName());
        System.out.println("Line: " + element.getPosition().getLine());
        System.out.println("Label: " + action.getNode().getLabel());
      }
    }
  }

  @SuppressWarnings("rawtypes")
  protected List<CtType<?>> getCtType(File file) throws Exception {
    Factory factory = new FactoryImpl(new DefaultCoreFactory(), new StandardEnvironment());
    factory.getEnvironment().setNoClasspath(true);
    SpoonModelBuilder compiler = new JDTBasedSpoonCompiler(factory);
    compiler.getFactory().getEnvironment().setLevel("OFF");
    compiler.addInputSource(SpoonResourceHelper.createResource(file));
    compiler.build();
    return factory.Type().getAll();
  }

  /**
   * compares two snippet
   */
  public Diff compare(String left, String right) {
    return this.compare(getCtType(left), getCtType(right));
  }

  private CtType<?> getCtType(String content) {
    Factory factory = new FactoryImpl(new DefaultCoreFactory(), new StandardEnvironment());
    factory.getEnvironment().setNoClasspath(true);

    SpoonModelBuilder compiler = new JDTBasedSpoonCompiler(factory);
    compiler.addInputSource(new VirtualFile(content, "/test"));
    compiler.build();
    return factory.Type().getAll().get(0);
  }

  /**
   * compares two AST nodes
   */
  public Diff compare(CtElement left, CtElement right) {
    final SpoonGumTreeBuilder scanner = new SpoonGumTreeBuilder();
    return new DiffImpl(scanner.getTreeContext(), scanner.getTree(left), scanner.getTree(right));
  }

  private static String partialElementPrint(CtElement element) {
    DefaultJavaPrettyPrinter print = new DefaultJavaPrettyPrinter(
        element.getFactory().getEnvironment()) {
      @Override
      public DefaultJavaPrettyPrinter scan(CtElement e) {
        if (e != null && e.getMetadata("isMoved") == null) {
          return super.scan(e);
        }
        return this;
      }
    };

    print.scan(element);
    return print.getResult();
  }

  public static void main(String[] args) throws Exception {
    String path1 = "E:\\IDEAProject\\demo\\junit4-r4.6\\src\\main\\java\\org\\junit\\runner\\Description.java";
    String path2 = "E:\\IDEAProject\\demo\\junit4-r4.8\\src\\main\\java\\org\\junit\\runner\\Description.java";
    File file1 = new File(path1);
    File file2 = new File(path2);

    AstComparator comparator = new AstComparator();
    comparator.compare(file1, file2);
  }
}
