package engine.expressions.parser.antlr;

import engine.calculation.functions.*;
import engine.expressions.Function;
import engine.expressions.parser.ClauseType;
import engine.expressions.parser.ClauseTypeVisitor;
import engine.expressions.parser.SyntaxError;
import engine.expressions.parser.SyntaxErrorMessages;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.tree.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/30/13
 * Time: 2:43 PM
 */
public class AntlrExpressionBuilder {
    private final List<SyntaxError> errors = new ArrayList<SyntaxError>();

    public Object build(ClauseType clause, final ParserRuleReturnScope rule)
            throws ExpressionBuilderFailure {
        try {
            return clause.accept(new TreeWalker((Tree) rule.getTree()));
        } catch (ExpressionBuilderFailure failure) {
            throw failure;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<SyntaxError> getErrors() {
        return errors;
    }

    private class TreeWalker implements ClauseTypeVisitor<Object> {
        private final Stack<Tree> stack;

        public TreeWalker(Tree tree) {
            stack = new Stack<Tree>();
            stack.add(tree);
        }

        @Override
        public Object equations() {
            return null;
        }

        @Override
        public Object equation() {
            return null;
        }

        public Function expression() throws ExpressionBuilderFailure {
            Tree tree = stack.peek();
            switch (tree.getType()) {
                case EqualParser.PLUS:
                case EqualParser.MINUS:
                    if (tree.getChildCount() == 1) {
                        return unary();
                    } else {
                        return additive();
                    }
                case EqualParser.STAR:
                case EqualParser.SLASH:
                    return multiplicative();
                case EqualParser.MATH_FUNC:
                case EqualParser.VARIABLE:
                case EqualParser.CONSTANT:
                case EqualParser.OPEN_BRACKET:
                    return primaryExpression();
            }
            throw runtimeException(tree, "expression(): unknown token type");
        }

        public Function additive() throws ExpressionBuilderFailure {
            Tree tree = stack.pop();
            if (tree.getChildCount() != 2) {
                throw runtimeException(tree, "additiveExpression(): child counts != 2");
            }
            pushChildrenReverse(tree);
            Function left = expression();
            Function right = expression();
            switch (tree.getType()) {
                case EqualParser.PLUS:
                    return new Addition(left, right);
                case EqualParser.MINUS:
                    return new Subtraction(left, right);
            }
            throw runtimeException(tree, "additiveExpression(): unknown token type");
        }

        public Function multiplicative() throws ExpressionBuilderFailure {
            Tree tree = stack.pop();
            if (tree.getChildCount() != 2) {
                throw runtimeException(tree, "multiplicativeExpression(): child counts != 2");
            }
            pushChildrenReverse(tree);
            Function left = expression();
            Function right = expression();
            switch (tree.getType()) {
                case EqualParser.STAR:
                    return new Multiplication(left, right);
                case EqualParser.SLASH:
                    return new Division(left, right);
            }
            throw runtimeException(tree, "multiplicativeExpression(): unknown token type");
        }

        public Function unary() throws ExpressionBuilderFailure {
            Tree tree = stack.peek();
            if (tree.getChildCount() != 1) {
                throw runtimeException(tree, "unaryExpression(): child counts != 1");
            }
            switch (tree.getType()) {
                case EqualParser.MINUS:
                    tree = stack.pop();
                    pushFirstChild(tree);
                    return additiveExpression();
                case EqualParser.PLUS:
                    tree = stack.pop();
                    pushFirstChild(tree);
                    return new Negation(additiveExpression());
            }
            throw runtimeException(tree, "unaryExpression(): unknown token type");
        }

        @Override
        public Function additiveExpression() throws ExpressionBuilderFailure {
            return expression();
        }

        @Override
        public Function multiplicativeExpression() throws ExpressionBuilderFailure {
            return  expression();
        }


        private Function unaryExpression() throws ExpressionBuilderFailure {
            return expression();
        }

        @Override
        public Function primaryExpression() throws ExpressionBuilderFailure {
            Tree tree = stack.peek();
            switch (tree.getType()) {
                case EqualParser.MATH_FUNC:
                    return mathFunction();
                case EqualParser.VARIABLE:
                    return variable();
                case EqualParser.CONSTANT:
                    return constant();
                case EqualParser.OPEN_BRACKET:
                    return parentheses();
            }
            throw runtimeException(tree, "primaryExpression(): unknown token type");
        }


        @Override
        public Function parentheses() throws ExpressionBuilderFailure {
            Tree tree = stack.pop();
            if (tree.getChildCount() != 1) {
                throw runtimeException(tree, "parentheses(): child count != 1");
            }
            pushFirstChild(tree);
            return expression();
        }

        @Override
        public Constant constant() {
            Tree tree = stack.pop();
            if (tree.getChildCount() != 1) {
                throw runtimeException(tree, "constant(): child count != 1");
            }
            Tree constVal = tree.getChild(0);
            try {
                String text = constVal.getText();
                double value = Double.parseDouble(text);
                return new Constant(value);
            } catch (NumberFormatException nfe) {
                throw runtimeException(tree, "constant(): number is in wrong format");
            }
        }

        @Override
        public Variable variable() {
            Tree tree = stack.pop();
            if (tree.getChildCount() != 1) {
                throw runtimeException(tree, "variable(): child count != 1");
            }
            Tree child = tree.getChild(0);
            String name = child.getText();
            return new Variable(name);
        }

        public Double decimalFloat() {
            Tree pop = stack.pop();
            String text = pop.getText();
            return Double.parseDouble(text);
        }

        @Override
        public Function []arguments() throws ExpressionBuilderFailure {
            Tree tree = stack.pop();
            pushChildrenReverse(tree);
            int nArgs = tree.getChildCount();
            Function[] arguments = new Function[nArgs];
            for (int i = 0; i < nArgs; i++) {
                arguments[i] = expression();
            }
            return arguments;
        }

        @Override
        public Function mathFunction() throws ExpressionBuilderFailure {
            Tree tree = stack.pop();
            if (tree.getChildCount() != 2) {
                throw runtimeException(tree, "mathFunction(): child count != 2");
            }

            Tree nameToken = tree.getChild(0);
            String name = nameToken.getText();

            stack.push(tree.getChild(1));
            Function[] args = arguments();

            MathFunctionType type = MathFunctionType.bySignature(name, args.length);
            if (type == null) {
                errors.add(errorByToken(nameToken, SyntaxErrorMessages.unknownFunction(name, args)));
                throw new ExpressionBuilderFailure();
            }

            return new MathFunction(type, args);
        }

        private SyntaxError errorByToken(Tree nameToken, String message) {
            return new SyntaxError(
                    nameToken.getLine(),
                    nameToken.getCharPositionInLine(),
                    nameToken.getTokenStartIndex(),
                    nameToken.getTokenStopIndex(),
                    message);
        }

        private void pushFirstChild(Tree tree) {
            stack.push(tree.getChild(0));
        }

        private void pushChildrenReverse(Tree tree) {
            int cnt = tree.getChildCount();
            for (int i = cnt - 1; i >= 0; i--) {
                stack.push(tree.getChild(i));
            }
        }

        private RuntimeException runtimeException(Tree tree, String message) {
            return new RuntimeException(message + ", tree:" + tree.toStringTree());
        }
    }

}
