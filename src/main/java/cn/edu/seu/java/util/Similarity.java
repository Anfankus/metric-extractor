package cn.edu.seu.java.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;

import data.io.util.IOUtil;


public class Similarity {

  private static int computeEditDistance(String s1, String s2) {
    s1 = s1.toLowerCase();
    s2 = s2.toLowerCase();

    int[] costs = new int[s2.length() + 1];
    for (int i = 0; i <= s1.length(); i++) {
      int lastValue = i;
      for (int j = 0; j <= s2.length(); j++) {
        if (i == 0) {
          costs[j] = j;
        } else {
          if (j > 0) {
            int newValue = costs[j - 1];
            if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
              newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
            }
            costs[j - 1] = lastValue;
            lastValue = newValue;
          }
        }
      }
      if (i > 0) {
        costs[s2.length()] = lastValue;
      }
    }
    return costs[s2.length()];
  }

  public static double similarityByLevenshtein(String s1, String s2) {
    double similarityOfStrings = 0.0;
    int editDistance = 0;
    if (s1.length() < s2.length()) { // s1 should always be bigger
      String swap = s1;
      s1 = s2;
      s2 = swap;
    }
    int bigLen = s1.length();
    editDistance = computeEditDistance(s1, s2);
    if (bigLen == 0) {
      similarityOfStrings = 1.0; /* both strings are zero length */
    } else {
      similarityOfStrings = (bigLen - editDistance) / (double) bigLen;
    }
    return similarityOfStrings;
  }

  private static long interSectionSize(String s1, String s2) {
    Set<Character> intersection = new HashSet<Character>();
    for (int i = 0; i < s1.length(); i++) {
      for (int j = 0; j < s2.length(); j++) {
        if (s1.charAt(i) == s2.charAt(j)) {
          //System.out.print(s1.charAt(i));
          intersection.add(s1.charAt(i));
        }
      }
    }
    return intersection.size();
  }


  public static double LCSSize(String source, String destination) {
    char[] str1 = source.toCharArray();
    char[] str2 = destination.toCharArray();
    int substringLength1 = str1.length;
    int substringLength2 = str2.length;
    int[][] opt = new int[substringLength1 + 1][substringLength2 + 1];
    for (int i = substringLength1 - 1; i >= 0; i--) {
      for (int j = substringLength2 - 1; j >= 0; j--) {
        if (str1[i] == str2[j]) {
          opt[i][j] = opt[i + 1][j + 1] + 1;
        } else {
          opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1]);
        }

      }
    }
    //System.out.print("LCS:");
    StringBuffer result = new StringBuffer();
    int i = 0, j = 0;
    while (i < substringLength1 && j < substringLength2) {
      if (str1[i] == str2[j]) {
        result.append(str1[i]);
        i++;
        j++;
      } else if (opt[i + 1][j] >= opt[i][j + 1]) {
        i++;
      } else {
        j++;
      }
    }
    return result.toString().length();
  }
	
	/*public static boolean XOR(boolean a, boolean b)
	{
	   return ( (a&&b) || (!a&&!b) );
	}*/

  public static double signatureSimilarity(MethodDeclaration removeMethod,
      MethodDeclaration addMethod) {
    double similarity = 0;
    String param1 = ASTNodeUtil.getParameter(removeMethod);
    String param2 = ASTNodeUtil.getParameter(addMethod);
    if (param1.equals(param2) != true) {
      return 0;
    }
    String methodName1 = removeMethod.getName().toString();
    String methodName2 = addMethod.getName().toString();
    double commonCharacterSize = interSectionSize(methodName1, methodName2);
    if (methodName1.length() > 0 && methodName2.length() > 0) {
      similarity = commonCharacterSize / methodName1.length() * 0.5
          + commonCharacterSize / methodName2.length() * 0.5;
    }
    return similarity;
  }

  public static double outgoingCallSetSimilarity(MethodDeclaration removeMethod,
      MethodDeclaration addMethod) {
    //MethodInvocationVisitor invoVisitor = new MethodInvocationVisitor();
    //removeMethod.accept(invoVisitor);
		
		/*String outgoingSetStr1 = invoVisitor.getInvocationMethod();
		addMethod.accept(invoVisitor);
		String outgoingSetStr2 = invoVisitor.getInvocationMethod();
		
		double commonCharacterSize = interSectionSize(outgoingSetStr1, outgoingSetStr2);
		double similarity = 0;
		if(outgoingSetStr1.length() > 0 && outgoingSetStr2.length() > 0){
			similarity= commonCharacterSize/outgoingSetStr1.length()*0.5 + commonCharacterSize/outgoingSetStr2.length()*0.5;
		}*/
    return 0;
  }

  public static double methodBodySimilarity(MethodDeclaration removeMethod,
      MethodDeclaration addMethod) {
    if (removeMethod == null || addMethod == null) {
      // out.println("removeMethod: " + removeMethod.toString());
      // out.println("addMethod: " + addMethod.toString());
      IOUtil.catchError("ErrorType: NullParameter");
      return 0;
    }

    Block block1 = removeMethod.getBody();
    Block block2 = addMethod.getBody();

    if (block1 == null && block2 == null) {
      return 0;
    } else if ((block1 != null && block2 == null) || (block1 == null && block2 != null)) {
      return 0;
    } else {
      String bodyText1 = removeMethod.getBody().toString();
      String bodyText2 = addMethod.getBody().toString();
      @SuppressWarnings("unchecked")
      List<Statement> statements1 = block1.statements();
      @SuppressWarnings("unchecked")
      List<Statement> statements2 = block2.statements();
      if (statements1.size() == 0 && statements2.size() == 0) {
        return 0;
      }
      double commonSize = LCSSize(bodyText1, bodyText2);
      double similarity = 0;
      if (bodyText1.length() > 0 && bodyText2.length() > 0) {
        similarity = (commonSize / bodyText1.length() + commonSize / bodyText2.length()) * 0.5;
      }
      return similarity;
    }
  }

}
