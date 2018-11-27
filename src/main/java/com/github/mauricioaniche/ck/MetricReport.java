package com.github.mauricioaniche.ck;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MetricReport {

  private Map<String, MetricValue> fullClassNameToMetrics;

  public MetricReport() {
    this.fullClassNameToMetrics = new HashMap<String, MetricValue>();
  }

  public void add(MetricValue ck) {
    fullClassNameToMetrics.put(ck.getFullyQualifiedClassName(), ck);
  }

  public MetricValue get(String fullClassName) {
    return fullClassNameToMetrics.get(fullClassName);
  }

  public Collection<MetricValue> getCKMetrics() {
    return fullClassNameToMetrics.values();
  }

  public MetricValue getByClassName(String fullClassName) {
    if (this.fullClassNameToMetrics.containsKey(fullClassName)) {
      return this.fullClassNameToMetrics.get(fullClassName);
    } else {
      return null;
    }
  }
}
