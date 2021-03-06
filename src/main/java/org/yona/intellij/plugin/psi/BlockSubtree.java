package org.yona.intellij.plugin.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import org.antlr.intellij.adaptor.SymtabUtils;
import org.antlr.intellij.adaptor.psi.ANTLRPsiNode;
import org.antlr.intellij.adaptor.psi.ScopeNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yona.intellij.plugin.YonaLanguage;

public class BlockSubtree extends ANTLRPsiNode implements ScopeNode {
  public BlockSubtree(@NotNull ASTNode node) {
    super(node);
  }

  @Nullable
  @Override
  public PsiElement resolve(PsiNamedElement element) {
    return SymtabUtils.resolve(this, YonaLanguage.INSTANCE,
        element, "/block/vardef/ID");
  }
}
