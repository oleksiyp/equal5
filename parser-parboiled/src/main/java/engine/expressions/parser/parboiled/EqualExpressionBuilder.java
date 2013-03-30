package engine.expressions.parser.parboiled;

import engine.calculation.functions.*;
import engine.expressions.Equation;
import engine.expressions.Function;
import engine.expressions.parser.ClauseType;
import engine.expressions.parser.ClauseTypeVisitor;
import engine.expressions.parser.SyntaxErrorMessages;
import org.parboiled.Node;
import org.parboiled.buffers.InputBuffer;
import org.parboiled.common.Predicate;
import org.parboiled.errors.ParseError;
import org.parboiled.support.ParsingResult;

import java.util.*;

import static org.parboiled.support.ParseTreeUtils.*;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/24/13
 * Time: 2:10 AM
 */
public class EqualExpressionBuilder {
    // rule names
    public static final String WHOLE_SENTENCE = "WholeSentence";
    public static final String EQUATIONS = "Equations";
    public static final String EQUATION = "Equation";
    public static final String EXPRESSION = "Expression";
    public static final String EQUALITY_SIGN = "EqualitySign";
    public static final String TERM = "Term";
    public static final String OPERATOR = "Operator";
    public static final String FACTOR = "Factor";
    public static final String PARENTHESES = "Parentheses";
    public static final String CONSTANT = "Constant";
    public static final String MATH_FUNC = "MathFunc";
    public static final String VARIABLE = "Variable";
    public static final String NAME = "Name";
    public static final String ARGUMENTS = "Arguments";
    public static final String WHITE_SPACE = "WhiteSpace";
    public static final String EOI = "EOI";
    // pathes
    public static final String ZERO_OR_MORE_SEQUENCE_EQUATION = "ZeroOrMore/Sequence/Equation";
    public static final String TAIL_OPERATOR_TERM = "Tail/OperatorTerm";
    public static final String TAIL_OPERATOR_FACTOR = "Tail/OperatorFactor";
    public static final String SEQUENCE_TAIL_COMMA_EXPRESSION_EXPRESSION = "Sequence/Tail/CommaExpression/Expression";

    private final List<ParseError> errors = new ArrayList<ParseError>();
    private final InputBuffer inputBuffer;
    private final ParsingResult<Object> result;

    private List<String> varList = null;
    private Map<String, Double> knownConstants = new HashMap<String, Double>();
    private final Node<Object> rootNode;

    public EqualExpressionBuilder(ParsingResult<Object> result) {
        this.result = result;
        this.inputBuffer = result.inputBuffer;
        this.rootNode = result.parseTreeRoot;
    }

    public List<String> getVarList() {
        return varList;
    }

    public void setVarList(List<String> varList) {
        this.varList = varList;
    }

    public Map<String, Double> getKnownConstants() {
        return knownConstants;
    }

    public void setKnownConstants(Map<String, Double> knownConstants) {
        if (knownConstants == null) {
            throw new IllegalArgumentException("knownConstants");
        }
        this.knownConstants = knownConstants;
    }

    public List<ParseError> getErrors() {
        return errors;
    }

    protected RuntimeException runtimeError(Node<Object> node, String message) {
        return new RuntimeException(message);
    }

    protected void errorAndContinue(Node<Object> node, String message) {
        errors.add(new TreeNodeError(inputBuffer, node, message));
    }

    public Object build(ClauseType clauseType)
            throws ParsingFailureException {

        if (WHOLE_SENTENCE.equals(rootNode.getLabel())) {
            final Node<Object> childNode = findNode(rootNode.getChildren(),
                    new SkipWhitespaceOrEOIPredicate());
            if (childNode == null) {
                throw runtimeError(rootNode, "[WholeSentence] do not contain '"  + clauseType + "'");
            }
            return new ExprBuilder(childNode).build(clauseType);
        }
        return new ExprBuilder(rootNode).build(clauseType);
    }

    private class ExprBuilder implements ClauseTypeVisitor<Object> {
        private Stack<Node<Object>> stack = new Stack<Node<Object>>();
        public ExprBuilder(Node<Object> node) {
            stack.push(node);
        }

        public Object build(ClauseType clauseType) throws ParsingFailureException {
            Node<Object> node = stack.peek();

            try {
                return clauseType.accept(this);
            } catch (ParsingFailureException ex){
                throw ex;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private ParsingFailureException error(Node<Object> node, String message) {
            TreeNodeError error = new TreeNodeError(inputBuffer, node, message);
            errors.add(error);
            return new ParsingFailureException(error);
        }

        public Equation[] equations()
                throws ParsingFailureException {
            Node<Object> node = stack.pop();
            if (!EQUATIONS.equals(node.getLabel())) {
                throw runtimeError(node, "[Equations] mismatch");
            }

            Node<Object> firstEq = findNodeByPath(node, EQUATION);
            if (firstEq == null) {
                throw runtimeError(node, "[Equation] not found");
            }

            List<Node<Object>> equationNodes = new ArrayList<Node<Object>>();
            equationNodes.add(firstEq);

            collectNodesByPath(node, ZERO_OR_MORE_SEQUENCE_EQUATION, equationNodes);

            Equation[] result = new Equation[equationNodes.size()];
            int i = 0;
            for (Node<Object> eqNode : equationNodes) {
                stack.push(eqNode);
                result[i++] = equation();
            }

            return result;
        }

        public Equation equation() throws ParsingFailureException {
            Node<Object> node = stack.pop();
            if (!EQUATION.equals(node.getLabel())) {
                throw runtimeError(node, "[Equation] mismatch");
            }

            List<Node<Object>> expressionParts = new ArrayList<Node<Object>>();
            collectNodesByPath(node, EXPRESSION, expressionParts);

            if (expressionParts.size() < 2) {
                throw runtimeError(node, "at least two [Expression] should be in [Equation]");
            }
            if (expressionParts.size() != 2) {
                throw runtimeError(node, "only two [Expression] should be in [Equation]");
            }

            Node<Object> signNode = findNodeByPath(node, EQUALITY_SIGN);
            if (signNode == null) {
                throw runtimeError(node, "[EqualitySign] not found in [Equation]");
            }

            Node<Object> leftNode = expressionParts.get(0);
            Node<Object> rightNode = expressionParts.get(1);

            String sign = getNodeText(signNode, inputBuffer);
            Equation.Type type = Equation.Type.byOperator(sign);
            if (type == null) {
                throw runtimeError(signNode, "bad equation operator '" + sign + "'");
            }
            stack.push(leftNode);
            Function left = additiveExpression();
            stack.push(rightNode);
            Function right = additiveExpression();
            return new Equation(left, type, right);
        }

        public Function additiveExpression() throws ParsingFailureException {
            Node<Object> node = stack.pop();
            if (!EXPRESSION.equals(node.getLabel())) {
                throw runtimeError(node, "[Expression] mismatch");
            }
            Node<Object> firstTermNode = findNodeByPath(node, TERM);
            if (firstTermNode == null) {
                throw runtimeError(node, "first [Term] not found in [Expression]");
            }

            stack.push(firstTermNode);
            Function result = multiplicativeExpression();

            List<Node<Object>> opTermNodes = new ArrayList<Node<Object>>();
            collectNodesByPath(node, TAIL_OPERATOR_TERM, opTermNodes);
            for (Node<Object> opTermNode : opTermNodes) {
                Node<Object> opNode = findNodeByPath(opTermNode, OPERATOR);
                if (opNode == null) {
                    throw runtimeError(opTermNode, "[OperatorTerm] should contain [Operator]");
                }
                Node<Object> termNode = findNodeByPath(opTermNode, TERM);
                if (termNode == null) {
                    throw runtimeError(opTermNode, "[OperatorTerm] should contain [Term]");
                }

                stack.push(termNode);
                Function termFunc = multiplicativeExpression();

                String op = getNodeText(opNode, inputBuffer);
                if ("+".equals(op)) {
                    result = new Addition(result, termFunc);
                } else if ("-".equals(op)) {
                    result = new Subtraction(result, termFunc);
                } else {
                    throw runtimeError(opNode, "[Operator] should be '+' or '-'");
                }
            }

            return result;
        }

        public Function multiplicativeExpression() throws ParsingFailureException {
            Node<Object> node = stack.pop();
            if (!TERM.equals(node.getLabel())) {
                throw runtimeError(node, "[Term] mismatch");
            }
            Node<Object> firstFactorNode = findNodeByPath(node, FACTOR);
            if (firstFactorNode == null) {
                throw runtimeError(node, "first [Factor] not found in [Term]");
            }

            stack.push(firstFactorNode);
            Function result = primaryExpression();

            List<Node<Object>> opFactorNodes = new ArrayList<Node<Object>>();
            collectNodesByPath(node, TAIL_OPERATOR_FACTOR, opFactorNodes);
            for (Node<Object> opFactorNode : opFactorNodes) {
                Node<Object> opNode = findNodeByPath(opFactorNode, OPERATOR);
                if (opNode == null) {
                    throw runtimeError(opFactorNode, "[OperatorFactor] should contain [Operator]");
                }
                Node<Object> factorNode = findNodeByPath(opFactorNode, FACTOR);
                if (factorNode == null) {
                    throw runtimeError(opFactorNode, "[OperatorFactor] should contain [Factor]");
                }

                stack.push(factorNode);
                Function factorFunc = primaryExpression();

                String op = getNodeText(opNode, inputBuffer);
                if ("*".equals(op)) {
                    result = new Multiplication(result, factorFunc);
                } else if ("/".equals(op)) {
                    result = new Division(result, factorFunc);
                } else {
                    throw runtimeError(opNode, "[Operator] should be '*' or '/'");
                }
            }

            return result;
        }

        public Function primaryExpression() throws ParsingFailureException {
            Node<Object> node = stack.pop();
            if (!FACTOR.equals(node.getLabel())) {
                throw runtimeError(node, "[Factor] mismatch");
            }
            if (node.getChildren().size() < 1) {
                throw runtimeError(node, "[Factor] should contain at least one child");
            } else if (node.getChildren().size() != 1) {
                throw runtimeError(node, "[Factor] should contain one child");
            }

            Node<Object> firstSubNode = node.getChildren().get(0);
            String label = firstSubNode.getLabel();
            if (CONSTANT.equals(label)) {
                stack.push(firstSubNode);
                return constant();
            } else if (MATH_FUNC.equals(label)) {
                stack.push(firstSubNode);
                return mathFunction();
            } else if (VARIABLE.equals(label)) {
                stack.push(firstSubNode);
                return variable();
            } else if (PARENTHESES.equals(label)) {
                stack.push(firstSubNode);
                return parentheses();
            } else {
                throw runtimeError(node, "[Factor] should contain [Constant], [MathFunc], " +
                        "[Variables], [Parentheses]");
            }
        }

        public Function parentheses() throws ParsingFailureException {
            Node<Object> node = stack.pop();
            if (!PARENTHESES.equals(node.getLabel())) {
                throw runtimeError(node, "[Parentheses] mismatch");
            }
            Node<Object> exprNode = findNodeByPath(node, EXPRESSION);
            if (exprNode == null) {
                throw runtimeError(node, "[Expression] should be in [Parentheses]");
            }
            stack.push(exprNode);
            return additiveExpression();
        }

        public Function variable() throws ParsingFailureException {
            Node<Object> node = stack.pop();
            if (!VARIABLE.equals(node.getLabel())) {
                throw runtimeError(node, "[Variable] mismatch");
            }
            Node<Object> nameNode = findNodeByPath(node, NAME);
            if (nameNode == null) {
                throw runtimeError(node, "[Name] should be in [Variable]");
            }
            String name = getNodeText(nameNode, inputBuffer);
            if (knownConstants.containsKey(name)) {
                return new Constant(knownConstants.get(name));
            }

            if (varList != null && !varList.contains(name)) {
                throw error(nameNode, SyntaxErrorMessages.unknownVariable(name));
            }

            return new Variable(name);
        }

        public Function mathFunction() throws ParsingFailureException {
            Node<Object> node = stack.pop();
            if (!MATH_FUNC.equals(node.getLabel())) {
                throw runtimeError(node, "[MathFunc] mismatch");
            }
            Node<Object> nameNode = findNodeByPath(node, NAME);
            if (nameNode == null) {
                throw runtimeError(node, "[Name] should be in [MathFunc]");
            }

            Node<Object> argumentsNode = findNodeByPath(node, ARGUMENTS);
            if (argumentsNode == null) {
                throw runtimeError(node, "[Arguments] should be in [MathFunc]");
            }

            stack.push(argumentsNode);
            Function []args = arguments();

            String name = getNodeText(nameNode, inputBuffer);

            MathFunctionType type = MathFunctionType.bySignature(name, args.length);
            if (type == null) {
                errorAndContinue(nameNode, SyntaxErrorMessages.unknownFunction(name, args));
                return new MathFunction(MathFunctionType.IDENTITY, new Constant(0));
            }

            return new MathFunction(type, args);
        }

        public Function[] arguments() throws ParsingFailureException {
            Node<Object> node = stack.pop();
            if (!ARGUMENTS.equals(node.getLabel())) {
                throw runtimeError(node, "[Arguments] mismatch");
            }

            Node<Object> firstExprNode = findNodeByPath(node, "Sequence/Expression");
            if (firstExprNode == null) {
                return new Function[0];
            }

            List<Node<Object>> argExprNodes = new ArrayList<Node<Object>>();
            argExprNodes.add(firstExprNode);

            collectNodesByPath(node, SEQUENCE_TAIL_COMMA_EXPRESSION_EXPRESSION, argExprNodes);
            List<Function> result = new ArrayList<Function>();

            for (Node<Object> exprNode : argExprNodes) {
                stack.push(exprNode);
                result.add(additiveExpression());
            }

            return result.toArray(new Function[result.size()]);
        }

        public Function constant() {
            Node<Object> node = stack.pop();
            if (!CONSTANT.equals(node.getLabel())) {
                throw runtimeError(node, "[Constant] mismatch");
            }
            String strValue = getNodeText(node, inputBuffer);
            double value = 0;
            try{
                value = Double.parseDouble(strValue);
            } catch (NumberFormatException ex) {
                throw runtimeError(node, "Bad double literal: '" + strValue + "'");
            }
            return new Constant(value);
        }
    }

    private static class SkipWhitespaceOrEOIPredicate implements Predicate<Node<Object>> {
        @Override
        public boolean apply(Node<Object> input) {
            return !(WHITE_SPACE.equals(input.getLabel())
                    || EOI.equals(input.getLabel()));
        }
    }
}
