package engine.expressions.parser.parboiled;

import engine.calculation.functions.*;
import engine.expressions.Equation;
import engine.expressions.Function;
import engine.expressions.parser.ClauseType;
import org.parboiled.Node;
import org.parboiled.buffers.InputBuffer;
import org.parboiled.common.Predicate;
import org.parboiled.errors.ParseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private List<String> varList = null;
    private Map<String, Double> knownConstants = new HashMap<String, Double>();

    public EqualExpressionBuilder(InputBuffer inputBuffer) {
        this.inputBuffer = inputBuffer;
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

    public Object build(ClauseType clauseType, Node<Object> node)
            throws ParsingFailureException {
        if (WHOLE_SENTENCE.equals(node.getLabel())) {
            final Node<Object> childNode = findNode(node.getChildren(),
                    new SkipWhitespaceOrEOIPredicate());
            if (childNode == null) {
                throw runtimeError(node, "[WholeSentence] do not contain '"  + clauseType + "'");
            }
            return buildVariety(clauseType, childNode);
        }
        return buildVariety(clauseType, node);
    }

    private Object buildVariety(ClauseType clauseType, Node<Object> node) throws ParsingFailureException {
        switch (clauseType) {
            case EQUATIONS: return equations(node);
            case EQUATION: return equation(node);
            case EXPRESSION: return expression(node);
            case TERM: return term(node);
            case FACTOR: return factor(node);
            case PARENTHESES: return parentheses(node);
            case CONSTANT: return constant(node);
            case VARIABLE: return variable(node);
            case MATH_FUNCTION: return mathFunc(node);
            case ARGUMENTS: return arguments(node);
            case DECIMAL_FLOAT: return decimalFloat(node);
            case EXPONENT: return exponent(node);
            case DIGIT: return digit(node);
        }

        throw runtimeError(node, "clause type '" + clauseType + "' not handled");
    }

    private Object decimalFloat(Node<Object> node) {
        return Double.parseDouble(getNodeText(node, inputBuffer));
    }

    private Object exponent(Node<Object> node) {
        return getNodeText(node, inputBuffer);
    }

    private Object digit(Node<Object> node) {
        return getNodeText(node, inputBuffer);
    }

    private ParsingFailureException error(Node<Object> node, String message) {
        TreeNodeError error = new TreeNodeError(inputBuffer, node, message);
        errors.add(error);
        return new ParsingFailureException(error);
    }

    private RuntimeException runtimeError(Node<Object> node, String message) {
        return new RuntimeException(message);
    }

    private void errorAndContinue(Node<Object> node, String message) {
        errors.add(new TreeNodeError(inputBuffer, node, message));
    }

    public Equation[] equations(Node<Object> node)
            throws ParsingFailureException {
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
            result[i++] = equation(eqNode);
        }

        return result;
    }

    public Equation equation(Node<Object> node) throws ParsingFailureException {
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

        Function left = expression(leftNode);
        Function right = expression(rightNode);
        return new Equation(left, type, right);
    }

    private Function expression(Node<Object> node) throws ParsingFailureException {
        if (!EXPRESSION.equals(node.getLabel())) {
            throw runtimeError(node, "[Expression] mismatch");
        }
        Node<Object> firstTermNode = findNodeByPath(node, TERM);
        if (firstTermNode == null) {
            throw runtimeError(node, "first [Term] not found in [Expression]");
        }

        Function result = term(firstTermNode);

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

            Function termFunc = term(termNode);

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

    private Function term(Node<Object> node) throws ParsingFailureException {
        if (!TERM.equals(node.getLabel())) {
            throw runtimeError(node, "[Term] mismatch");
        }
        Node<Object> firstFactorNode = findNodeByPath(node, FACTOR);
        if (firstFactorNode == null) {
            throw runtimeError(node, "first [Factor] not found in [Term]");
        }

        Function result = factor(firstFactorNode);

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

            Function factorFunc = factor(factorNode);

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

    private Function factor(Node<Object> node) throws ParsingFailureException {
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
            return constant(firstSubNode);
        } else if (MATH_FUNC.equals(label)) {
            return mathFunc(firstSubNode);
        } else if (VARIABLE.equals(label)) {
            return variable(firstSubNode);
        } else if (PARENTHESES.equals(label)) {
            return parentheses(firstSubNode);
        } else {
            throw runtimeError(node, "[Factor] should contain [Constant], [MathFunc], " +
                    "[Variables], [Parentheses]");
        }
    }

    private Function parentheses(Node<Object> node) throws ParsingFailureException {
        if (!PARENTHESES.equals(node.getLabel())) {
            throw runtimeError(node, "[Parentheses] mismatch");
        }
        Node<Object> exprNode = findNodeByPath(node, EXPRESSION);
        if (exprNode == null) {
            throw runtimeError(node, "[Expression] should be in [Parentheses]");
        }
        return expression(exprNode);
    }

    private Function variable(Node<Object> node) throws ParsingFailureException {
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
            throw error(nameNode, "Unknown variable or known constant '" + name + "'");
        }

        return new Variable(name);
    }

    private Function mathFunc(Node<Object> node) throws ParsingFailureException {
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

        Function []args = arguments(argumentsNode);

        String name = getNodeText(nameNode, inputBuffer);

        MathFunctionType type = MathFunctionType.bySignature(name, args.length);
        if (type == null) {
            errorAndContinue(nameNode, "Unknown function '" + name
                    + "' with " + args.length +
                    " argument(s)");
            return new MathFunction(MathFunctionType.IDENTITY, new Constant(0));
        }

        return new MathFunction(type, args);
    }

    private Function[] arguments(Node<Object> node) throws ParsingFailureException {
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
            result.add(expression(exprNode));
        }

        return result.toArray(new Function[result.size()]);
    }

    private Function constant(Node<Object> node) {
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


    private static class SkipWhitespaceOrEOIPredicate implements Predicate<Node<Object>> {
        @Override
        public boolean apply(Node<Object> input) {
            return !(WHITE_SPACE.equals(input.getLabel())
                    || EOI.equals(input.getLabel()));
        }
    }
}
