package org.crain.dme4j.query.token;

public record Token(String literal, TokenType type) {

    public Token append(char a) {
        return new Token(literal + a, type);
    }

    public static Token illegal(String illegal) {
        return new Token(illegal, TokenType.ILLEGAL);
    }

    public static Token start() {
        return new Token(null, TokenType.SOS);
    }

    public static Token dot() {
        return new Token(".", TokenType.DOT);
    }

    public static Token dollar() {
        return new Token("$", TokenType.DOLLAR);
    }

    public static Token pound() {
        return new Token("#", TokenType.POUND);
    }

    public static Token asterisk() {
        return new Token("*", TokenType.ASTERISK);
    }

    public static Token leftBracket() {
        return new Token("[", TokenType.LEFT_BRACKET);
    }

    public static Token rightBracket() {
        return new Token("]", TokenType.RIGHT_BRACKET);
    }

    public static Token leftSqirly() {
        return new Token("{", TokenType.LEFT_SQIRLY);
    }

    public static Token rightSqirly() {
        return new Token("}", TokenType.RIGHT_SQIRLY);
    }

    public static Token bang() {
        return new Token("!", TokenType.BANG);
    }
    public static Token singleQuote() {
        return new Token("'", TokenType.SINGLE_QUOTE);
    }

    public static Token equalsToken() {
        return new Token("=", TokenType.EQUALS);
    }

    public static Token greaterToken() {
        return new Token(">", TokenType.GREATER_THAN);
    }

    public static Token lessToken() {
        return new Token("<", TokenType.LESS_THAN);
    }
    public static Token space() {
        return new Token(" ", TokenType.SPACE);
    }

    public static Token questionMark() {
        return new Token("?", TokenType.QUESTION_MARK);
    }
    public static Token end() {
        return new Token(null, TokenType.EOS);
    }
}
