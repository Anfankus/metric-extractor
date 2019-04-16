package com.github.mauricioaniche.ck;

import org.eclipse.jdt.core.dom.ASTNode;

/**
 * metric extractor
 */
public interface MetricExtractor {

  public void execute(ASTNode node, MetricReport report);
}
