package org.yona.intellij.plugin.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IncorrectOperationException;
import org.antlr.intellij.adaptor.lexer.RuleIElementType;
import org.antlr.intellij.adaptor.psi.ANTLRPsiLeafNode;
import org.antlr.intellij.adaptor.psi.Trees;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.yona.intellij.plugin.YonaLanguage;
import org.yona.intellij.plugin.YonaParserDefinition;
import yona.parser.YonaParser;

/**
 * From doc: "Every element which can be renamed or referenced
 * needs to implement com.intellij.psi.PsiNamedElement interface."
 * <p>
 * So, all leaf nodes that represent variables, functions, classes, or
 * whatever in your plugin language must be instances of this not just
 * LeafPsiElement.  Your ASTFactory should create this kind of object for
 * ID tokens. This node is for references *and* definitions because you can
 * highlight a function and say "jump to definition". Also we want defs
 * to be included in "find usages." Besides, there is no context information
 * in the AST factory with which you could decide whether this node
 * is a definition or a reference.
 * <p>
 * PsiNameIdentifierOwner (vs PsiNamedElement) implementations are the
 * corresponding subtree roots that define symbols.
 * <p>
 * You can click on an ID in the editor and ask for a rename for any node
 * of this type.
 */
public class Identifier extends ANTLRPsiLeafNode implements PsiNamedElement {
  public Identifier(IElementType type, CharSequence text) {
    super(type, text);
  }

  @Override
  public String getName() {
    return getText();
  }

  /**
   * Alter this node to have text specified by the argument. Do this by
   * creating a new node through parsing of an ID and then doing a
   * replace.
   */
  @Override
  public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
    if (getParent() == null) return this; // weird but it happened once
    PsiElement newID = Trees.createLeafFromText(getProject(),
        YonaLanguage.INSTANCE,
        getContext(),
        name,
        YonaParserDefinition.ID);
    if (newID != null) {
      return this.replace(newID); // use replace on leaves but replaceChild on ID nodes that are part of defs/decls.
    }
    return this;
  }

  /**
   * Create and return a PsiReference object associated with this ID
   * node. The reference object will be asked to resolve this ref
   * by using the text of this node to identify the appropriate definition
   * site. The definition site is typically a subtree for a function
   * or variable definition whereas this reference is just to this ID
   * leaf node.
   * <p>
   * As the AST factory has no context and cannot create different kinds
   * of PsiNamedElement nodes according to context, every ID node
   * in the tree will be of this type. So, we distinguish references
   * from definitions or other uses by looking at context in this method
   * as we have parent (context) information.
   */
  @Override
  public PsiReference getReference() {
    PsiElement parent = getParent();
    IElementType elType = parent.getNode().getElementType();
    // do not return a reference for the ID nodes in a definition
    if (elType instanceof RuleIElementType) {
      switch (((RuleIElementType) elType).getRuleIndex()) {
        case YonaParser.RULE_expression:
        case YonaParser.RULE_input:
          return new AliasRef(this);
        case YonaParser.RULE_apply:
          return new FunctionRef(this);
      }
    }
    return null;
  }
}
