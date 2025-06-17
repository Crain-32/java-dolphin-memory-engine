//package org.crain.dme4j.query;
//
//import org.apache.commons.lang3.StringUtils;
//import org.crain.memory.query.token.Token;
//import org.crain.memory.query.token.TokenType;
//
//import java.util.ArrayDeque;
//import java.util.Queue;
//
//class Lexer {
//
//    static Queue<Token> lexString(String rawInput) throws IllegalArgumentException {
//        if (StringUtils.isBlank(rawInput)) throw new IllegalArgumentException("Provided input string was blank!");
//        var filteredInput = rawInput.trim().toLowerCase();
//        Queue<Token> result = new ArrayDeque<>(rawInput.length() / 2);
//        var lastToken = Token.start();
//        for (char c : filteredInput.toCharArray()) {
//            switch (c) {
//                case '.' -> {
//
//                    result.add(lastToken);
//                    lastToken = Token.dot();
//                }
//                case '$' -> {
//                    result.add(lastToken);
//                    lastToken = Token.dollar();
//                }
//                case '#' -> {
//                    result.add(lastToken);
//                    lastToken = Token.pound();
//                }
//                case '*' -> {
//                    result.add(lastToken);
//                    lastToken = Token.asterisk();
//                }
//                case '[' -> {
//                    result.add(lastToken);
//                    lastToken = Token.leftBracket();
//                }
//                case ']' -> {
//                    result.add(lastToken);
//                    lastToken = Token.rightBracket();
//                }
//                case '{' -> {
//                    result.add(lastToken);
//                    lastToken = Token.leftSqirly();
//                }
//                case '}' -> {
//                    result.add(lastToken);
//                    lastToken = Token.rightSqirly();
//                }
//                case '!' -> {
//                    result.add(lastToken);
//                    lastToken = Token.bang();
//                }
//                case '\'' -> {
//                    result.add(lastToken);
//                    lastToken = Token.singleQuote();
//                }
//                case '=' -> {
//                    result.add(lastToken);
//                    lastToken = Token.equalsToken();
//                }
//                case '>' -> {
//                    result.add(lastToken);
//                    lastToken = Token.greaterToken();
//                }
//                case '<' -> {
//                    result.add(lastToken);
//                    lastToken = Token.lessToken();
//                }
//                case ' ' -> {
//                    if (lastToken.type() == TokenType.SPACE) continue;
//                    result.add(lastToken);
//                    lastToken = Token.space();
//                }
//                case '?' -> {
//                    result.add(lastToken);
//                    lastToken = Token.questionMark();
//                }
//                default -> lastToken = defaultHandler(c, lastToken, result);
//            }
//        }
//        result.add(lastToken);
//        result.add(Token.end());
//        return result;
//    }
//
//    private static Token defaultHandler(char input, Token lastToken, Queue<Token> tokenQueue) {
//        if (Character.isDigit(input) && lastToken.type() == TokenType.INT) {
//            return lastToken.append(input);
//        } else if (Character.isDigit(input)) {
//            tokenQueue.add(lastToken);
//            return new Token(Character.toString(input), TokenType.INT);
//        }
//        if (Character.isLetter(input) && lastToken.type() == TokenType.WORD) {
//            return lastToken.append(input);
//        } else if (Character.isLetter(input)) {
//            tokenQueue.add(lastToken);
//            return new Token(Character.toString(input), TokenType.WORD);
//        }
//        tokenQueue.add(lastToken);
//        return Token.illegal(Character.toString(input));
//    }
//}
