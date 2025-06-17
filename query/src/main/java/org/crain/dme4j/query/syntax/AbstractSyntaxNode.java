package org.crain.dme4j.query.syntax;

public abstract class AbstractSyntaxNode implements SyntaxNode {

    protected SyntaxNode child;

    protected AbstractSyntaxNode(SyntaxNode child) {
        this.child = child;
    }

    @Override
    public SyntaxNode getChild() {
        return child;
    }

    @Override
    public void setChild(final SyntaxNode child) throws SyntaxException {
        canBeChild(child);
        this.child = child;
    }

    @Override
    public boolean isLeaf() {
        return child == null;
    }
}
