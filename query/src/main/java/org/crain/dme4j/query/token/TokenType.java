package org.crain.dme4j.query.token;

public enum TokenType {
    ILLEGAL, // %@)(&^,<>/"\|-_+
    SOS, // Start of Statement
    DOT, // .
    DOLLAR, // $
    POUND, // #
    ASTERISK, // *
    LEFT_BRACKET, // [
    RIGHT_BRACKET, // ]
    LEFT_SQIRLY, // {
    RIGHT_SQIRLY, // }
    INT, // 0123456789
    BANG, // !
    WORD, // String of Characters
    SINGLE_QUOTE, // '
    EQUALS, // =
    GREATER_THAN, // >
    LESS_THAN, // <
    SPACE, // ' '
    QUESTION_MARK, // ?
    EOS, // End of Statement
}
