package gumtree.spoon;

import java.util.ArrayList;
import java.util.List;

public class Example {

  public static void main(String[] args) {
    List<Long> list = new ArrayList<Long>();
    list.add(1L);
    list.add(2L);
    list.add(3L);
    list.add(4L);
    list.add(5L);
    list.add(6L);
    list.add(7L);
    list.add(8L);
    list.add(9L);
    list.add(10L);
    List<List<Long>> splits = partition(list, 3);

    for (List<Long> oneList : splits) {
      System.out.println(oneList);
    }

  }

  public static <T> List<List<T>> partition(List<T> list, final int L) {
    List<List<T>> parts = new ArrayList<List<T>>();
    final int N = list.size();
    for (int i = 0; i < N; i += L) {
      parts.add(new ArrayList<T>(list.subList(i, Math.min(N, i + L))));
    }
    return parts;
  }
}
