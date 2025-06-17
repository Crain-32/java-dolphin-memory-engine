package org.crain.dme4j.query.syntax;

public interface SyntaxNode {

    void setChild(SyntaxNode child) throws SyntaxException;

    String getRepresentation();

    void canBeChild(SyntaxNode child) throws SyntaxException;

    boolean isLeaf();

    SyntaxNode getChild();
}
