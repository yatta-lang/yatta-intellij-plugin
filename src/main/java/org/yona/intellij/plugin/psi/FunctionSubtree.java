package org.yona.intellij.plugin.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.tree.IElementType;
import org.antlr.intellij.adaptor.SymtabUtils;
import org.antlr.intellij.adaptor.psi.IdentifierDefSubtree;
import org.antlr.intellij.adaptor.psi.ScopeNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yona.intellij.plugin.YonaLanguage;

/**
 * A subtree associated with a function definition.
 * Its scope is the set of arguments.
 */
public class FunctionSubtree extends IdentifierDefSubtree implements ScopeNode {
  public FunctionSubtree(@NotNull ASTNode node, @NotNull IElementType idElementType) {
    super(node, idElementType);
  }

  @Nullable
  @Override
  public PsiElement resolve(PsiNamedElement element) {
    return SymtabUtils.resolve(this, YonaLanguage.INSTANCE, element, "/script/function/ID");
  }
}