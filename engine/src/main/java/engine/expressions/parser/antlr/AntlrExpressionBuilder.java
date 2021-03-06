package engine.expressions.parser.antlr;

import engine.calculation.functions.*;
import engine.expressions.Calculable;
import engine.expressions.Equation;
import engine.expressions.parser.ClauseType;
import engine.expressions.parser.ClauseTypeVisitor;
import engine.expressions.parser.SyntaxError;
import engine.expressions.parser.SyntaxErrorMessages;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/30/13
 * Time: 2:43 PM
 */
public class AntlrExpressionBuilder {
    private final List<SyntaxError> errors = new ArrayList<SyntaxError>();
    private final Map<String, Double> knownConstants;
    private final List<String> varList;

    public AntlrExpressionBuilder(Map<String, Double> knownConstants, List<String> varList) {
        this.knownConstants = knownConstants;
        this.varList = varList;
    }

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
        public Equation[] equations() throws ExpressionBuilderFailure {
            Tree tree = stack.pop();
            int nChilds = tree.getChildCount();
            if (nChilds < 1) {
                throw runtimeException(tree, "equations(): child counts < 1");
            }
            pushChildrenReverse(tree);
            Equation[] equations = new Equation[nChilds];
            for (int i = 0; i < nChilds; i++) {
                equations[i] = equation();
            }
            return equations;
        }

        @Override
        public Equation equation() throws ExpressionBuilderFailure {
            Tree tree = stack.pop();
            if (tree.getChildCount() != 2) {
                throw runtimeException(tree, "equation(): child counts != 2");
            }
            pushChildrenReverse(tree);
            Calculable left = expression();
            Calculable right = expression();

            String typeStr = tree.getText();
            Equation.Type type = Equation.Type.byOperator(typeStr);
            if (type == null) {
                throw runtimeException(tree, "bad equation type: " + typeStr);
            }
            return new Equation(left, type, right);
        }

        public Calculable expression() throws ExpressionBuilderFailure {
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

        public Calculable additive() throws ExpressionBuilderFailure {
            Tree tree = stack.pop();
            if (tree.getChildCount() != 2) {
                throw runtimeException(tree, "additiveExpression(): child counts != 2");
            }
            pushChildrenReverse(tree);
            Calculable left = expression();
            Calculable right = expression();
            switch (tree.getType()) {
                case EqualParser.PLUS:
                    return new Addition(left, right);
                case EqualParser.MINUS:
                    return new Subtraction(left, right);
            }
            throw runtimeException(tree, "additiveExpression(): unknown token type");
        }

        public Calculable multiplicative() throws ExpressionBuilderFailure {
            Tree tree = stack.pop();
            if (tree.getChildCount() != 2) {
                throw runtimeException(tree, "multiplicativeExpression(): child counts != 2");
            }
            pushChildrenReverse(tree);
            Calculable left = expression();
            Calculable right = expression();
            switch (tree.getType()) {
                case EqualParser.STAR:
                    return new Multiplication(left, right);
                case EqualParser.SLASH:
                    return new Division(left, right);
            }
            throw runtimeException(tree, "multiplicativeExpression(): unknown token type");
        }

        public Calculable unary() throws ExpressionBuilderFailure {
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
        public Calculable additiveExpression() throws ExpressionBuilderFailure {
            return expression();
        }

        @Override
        public Calculable multiplicativeExpression() throws ExpressionBuilderFailure {
            return  expression();
        }


        private Calculable unaryExpression() throws ExpressionBuilderFailure {
            return expression();
        }

        @Override
        public Calculable primaryExpression() throws ExpressionBuilderFailure {
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
        public Calculable parentheses() throws ExpressionBuilderFailure {
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
        public Calculable variable() throws ExpressionBuilderFailure {
            Tree tree = stack.pop();
            if (tree.getChildCount() != 1) {
                throw runtimeException(tree, "variable(): child count != 1");
            }
            Tree nameToken = tree.getChild(0);
            String name = nameToken.getText();
            if (knownConstants.containsKey(name)) {
                return new Constant(knownConstants.get(name));
            }
            if (varList != null) {
                if (!varList.contains(name)) {
                    errors.add(errorByToken(nameToken, SyntaxErrorMessages.unknownVariable(name)));
                    throw new ExpressionBuilderFailure();
                }
            }
            return new Variable(name);
        }

        @Override
        public Calculable[]arguments() throws ExpressionBuilderFailure {
            Tree tree = stack.pop();
            pushChildrenReverse(tree);
            int nArgs = tree.getChildCount();
            Calculable[] arguments = new Calculable[nArgs];
            for (int i = 0; i < nArgs; i++) {
                arguments[i] = expression();
            }
            return arguments;
        }

        @Override
        public Calculable mathFunction() throws ExpressionBuilderFailure {
            Tree tree = stack.pop();
            if (tree.getChildCount() != 2) {
                throw runtimeException(tree, "mathFunction(): child count != 2");
            }

            Tree nameToken = tree.getChild(0);
            String name = nameToken.getText();

            stack.push(tree.getChild(1));
            Calculable[] args = arguments();

            MathFunctionType type = MathFunctionType.bySignature(name, args.length);
            if (type == null) {
                errors.add(errorByToken(nameToken, SyntaxErrorMessages.unknownFunction(name, args)));
                throw new ExpressionBuilderFailure();
            }

            return new MathCalculable(type, args);
        }

        private SyntaxError errorByToken(Tree tree, String message) {


            int line = tree.getLine();
            int column = tree.getCharPositionInLine();
            int start = tree.getTokenStartIndex();
            int end = start;
            if (tree instanceof CommonTree) {
                Token token = ((CommonTree) tree).getToken();
                if (token instanceof CommonToken) {
                    CommonToken ct = (CommonToken) token;
                    start = ct.getStartIndex();
                    end = ct.getStopIndex() + 1;
                }
            }
            return new SyntaxError(
                    line,
                    column,
                    start,
                    end,
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
