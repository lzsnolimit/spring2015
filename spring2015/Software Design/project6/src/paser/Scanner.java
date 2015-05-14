/*
 * File: Scanner.java
 */

package paser;

import ast5.Token;

public class Scanner
{
	private static final char NULL_CHAR = '\u0000';

	private String inputString; //the string to be converted into tokens
	private String tokenString; //the current token's spelling
    private int tokenKind;  //the current token's kind
	private int index; //index of the next character in the inputString

	public Scanner(String s)
	{
		if (s == null)
            s = "";
        inputString = s;
		tokenString = "";
        tokenKind = Token.ERROR;
		index = 0;
	}

	private char currentCharacter()
	{
		char c = NULL_CHAR;
		try {
			c = inputString.charAt(index);
		} catch (IndexOutOfBoundsException e) {
			// do nothing, fall through to return the null character
		}
		return c;
	}

	public Token getNextToken()
	{
		// trim any initial whitespace & comments
        trimWhiteSpaceAndComments();

        tokenKind = Token.ERROR;
        tokenString = "";
        int startIndex = index;
		computeTokenStringAndKind(); //compute the kind and the spelling

        return new Token(tokenKind, tokenString, startIndex);
	}

    private void trimWhiteSpaceAndComments()
    {
//      THIS CODE IS USED IF YOU ALLOW /COMMENTS/
//      while( Character.isSpaceChar(currentCharacter()) ||
//                currentCharacter() == '/' ) {
//            if( currentCharacter() == '/' ) {
//                index++;
//                while( currentCharacter() != '/' )
//                    index++;
//                index++;
//            }
//            else
//                index++;
//        }
        while (Character.isSpaceChar(currentCharacter()) ) {
            index++;
        }
    }

    private void addCharToToken()
    {
        // move the first character from the input buffer to the end of
        // the token buffer
        tokenString += (inputString.charAt(index));
        index++;

    }

	//get all the token characters and determine the kind of token
    private void computeTokenStringAndKind()
	{
        //handle symbols and keywords, all of which start with a letter
        if( Character.isLetter(currentCharacter()) ) {
			addCharToToken();
			while( Character.isLetter(currentCharacter()) ||
					Character.isDigit(currentCharacter()) ) {
				addCharToToken();
			}
            if (tokenString.equals("class"))
                tokenKind = Token.CLASS;
            else if (tokenString.equals("while"))
                tokenKind = Token.WHILE;
            else if (tokenString.equals("print"))
            	tokenKind = Token.PRINT;
            else if (tokenString.equals("new"))
            	tokenKind = Token.NEW;
			else
				tokenKind = Token.SYMBOL;
		}
		// if it starts with a digit, it's an integer literal
		else if( Character.isDigit(currentCharacter()) ) {
			addCharToToken();
			while( Character.isDigit(currentCharacter()) ) {
				addCharToToken();
			}
			tokenKind = Token.INTLITERAL;
		}
		// handle all other special case characters
		else {
			switch( currentCharacter() ) {
				case '(':
					addCharToToken();
					tokenKind = Token.LEFTPAREN;
					break;
				case ')':
					addCharToToken();
					tokenKind = Token.RIGHTPAREN;
					break;
				case '{':
					addCharToToken();
					tokenKind = Token.LEFTBRACE;
					break;
				case '}':
					addCharToToken();
					tokenKind = Token.RIGHTBRACE;
					break;
				case ';':
					addCharToToken();
					tokenKind = Token.SEMICOLON;
					break;
				case ',':
					addCharToToken();
					tokenKind = Token.COMMA;
					break;
				case '=':
					addCharToToken();
					tokenKind = Token.EQUALS;
					break;
				case '.':
					addCharToToken();
					tokenKind = Token.DOT;
					break;
				case '+':
					addCharToToken();
					tokenKind = Token.PLUS;
					break;
				case '<':
					addCharToToken();
					tokenKind = Token.LESSTHAN;
					break;					
				case NULL_CHAR:
					if( index >= inputString.length())
                        tokenKind = Token.EOF;
                    else
                        tokenKind = Token.ERROR;
					break;
                default: //any other characters are illegal
                    tokenKind = Token.ERROR;
            }
		}
	}
}
