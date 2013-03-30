grammar Equal;
options {
	output=AST;
	backtrack=true;
	memoize=true;
}
tokens {
    EQUAL = '=';
    LESS = '<';
    GREATER = '>';
    LESS_OR_EQUAL = '<=';
    GREATER_OR_EQUAL = '>=';
    PLUS = '+';
    MINUS = '-';
    STAR = '*';
    SLASH = '/';
    OPEN_BRACKET = '(';
    CLOSE_BRACKET = ')';
    MATH_FUNC;
    VARIABLE;
    CONSTANT;
    ARGUMENTS;

}

@header {
    package engine.expressions.parser.antlr;
}
@lexer::header {
    package engine.expressions.parser.antlr;
}

theEnd
    : EOF
    ;

equations
    : equation (WhiteSpace! equation)*
    ;

equation
    : expression (EQUAL | LESS | GREATER
    | LESS_OR_EQUAL | GREATER_OR_EQUAL)^ expression
    ;

expression
    : additiveExpression
    ;

additiveExpression
	: multiplicativeExpression ((PLUS | MINUS)^ multiplicativeExpression)*
	;

multiplicativeExpression
	: unaryExpression ((STAR | SLASH)^ unaryExpression)*
	;

unaryExpression
	: primaryExpression
	| (PLUS | MINUS)^ unaryExpression
	;

primaryExpression
    : mathFunction
    | variable
    | parentheses
    | constant
    ;

mathFunction
    : IDENTIFIER '(' arguments ')' -> ^(MATH_FUNC IDENTIFIER arguments)
    ;

variable
    : IDENTIFIER -> ^(VARIABLE IDENTIFIER)
    ;

arguments
    : (expression (',' expression)*)? -> ^(ARGUMENTS expression+)
    ;

parentheses
    : OPEN_BRACKET^ expression CLOSE_BRACKET!
    ;

constant
    : DECIMAL_FLOAT -> ^(CONSTANT DECIMAL_FLOAT)
    ;

IDENTIFIER
    : LETTER (LETTER|DIGIT)*
    ;

DECIMAL_FLOAT
    : DIGIT+ '.' DIGIT* EXPONENT?
    | '.'? DIGIT+ EXPONENT?
    ;

EXPONENT
    : ('e'|'E') ('+'|'-')? DIGIT+
    ;

fragment DIGIT
    : ('0'..'9')
    ;

fragment LETTER
    : ('a'..'z')
    ;

WhiteSpace
	: ('\t' | '\v' | '\f' | ' ' | '\u00A0')	{$channel=HIDDEN;}
	;