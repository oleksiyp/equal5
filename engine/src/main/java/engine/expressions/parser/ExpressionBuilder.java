package engine.expressions.parser;

import engine.calculation.functions.*;
import engine.expressions.ClauseType;
import engine.expressions.Equation;
import engine.expressions.Function;
import org.parboiled.Node;
import org.parboiled.buffers.InputBuffer;
import org.parboiled.common.Predicate;
import org.parboiled.errors.ParseError;

import java.util.ArrayList;
import java.util.List;

import static org.parboiled.support.ParseTreeUtils.*;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/24/13
 * Time: 2:10 AM
 */
public class ExpressionBuilder {
    private final List<ParseError> errors = new ArrayList<ParseError>();
    private final InputBuffer inputBuffer;

    public ExpressionBuilder(InputBuffer inputBuffer) {
        this.inputBuffer = inputBuffer;
    }

    public List<ParseError> getErrors() {
        return errors;
    }

    public Object build(ClauseType clauseType, Node<Object> node)
            throws ParsingFailureException {
        if ("WholeSentence".equals(node.getLabel())) {
            final Node<Object> childNode = findNode(node.getChildren(),
                    new SkipWhitespaceOrEOIPredicate());
            if (childNode == null) {
                throw error(node, "[WholeSentence] do not contain '"  + clauseType + "'");
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
            case PARENTS: return parents(node);
            case CONSTANT: return constant(node);
            case VARIABLE: return variable(node);
            case MATH_FUNCTION: return mathFunc(node);
//            ARGUMENTS, // T
//            DECIMAL_FLOAT, // T
//            EXPONENT, // T
//            DIGIT // T
        }

        throw error(node, "clause type '" + clauseType + "' not handled");
    }

    private ParsingFailureException error(Node<Object> node, String message) {
        TreeNodeError error = new TreeNodeError(inputBuffer, node, message);
        errors.add(error);
        return new ParsingFailureException(error);
    }

    private void errorAndContinue(Node<Object> node, String message) {
        TreeNodeError error = new TreeNodeError(inputBuffer, node, message);
        errors.add(error);
    }

    public Equation[] equations(Node<Object> node)
            throws ParsingFailureException {
        if (!"Equations".equals(node.getLabel())) {
            errorAndContinue(node, "[Equations] mismatch");
        }

        Node<Object> firstEq = findNodeByPath(node, "Equation");
        if (firstEq == null) {
            throw error(node, "[Equation] not found");
        }

        List<Node<Object>> equationNodes = new ArrayList<Node<Object>>();
        equationNodes.add(firstEq);

        collectNodesByPath(node, "ZeroOrMore/Sequence/Equation", equationNodes);

        Equation[] result = new Equation[equationNodes.size()];
        int i = 0;
        for (Node<Object> eqNode : equationNodes) {
            result[i++] = equation(eqNode);
        }

        return result;
    }

    public Equation equation(Node<Object> node) throws ParsingFailureException {
        if (!"Equation".equals(node.getLabel())) {
            errorAndContinue(node, "[Equation] mismatch");
        }

        List<Node<Object>> expressionParts = new ArrayList<Node<Object>>();
        collectNodesByPath(node, "Expression", expressionParts);

        if (expressionParts.size() < 2) {
            throw error(node, "at least two [Expression] should be in [Equation]");
        }
        if (expressionParts.size() != 2) {
            errorAndContinue(node, "only two [Expression] should be in [Equation]");
        }

        Node<Object> signNode = findNodeByPath(node, "EqualitySign");
        if (signNode == null) {
            throw error(node, "[EqualitySign] not found in [Equation]");
        }

        Node<Object> leftNode = expressionParts.get(0);
        Node<Object> rightNode = expressionParts.get(1);

        String sign = getNodeText(signNode, inputBuffer);
        Equation.Type type = Equation.Type.byOperator(sign);
        if (type == null) {
            errorAndContinue(signNode, "bad equation operator '" + sign + "'");
            type = Equation.Type.EQUAL;
        }

        Function left = expression(leftNode);
        Function right = expression(rightNode);
        return new Equation(left, type, right);
    }

    private Function expression(Node<Object> node) throws ParsingFailureException {
        if (!"Expression".equals(node.getLabel())) {
            errorAndContinue(node, "[Expression] mismatch");
        }
        Node<Object> firstTermNode = findNodeByPath(node, "Term");
        if (firstTermNode == null) {
            throw error(node, "first [Term] not found in [Expression]");
        }

        Function result = term(firstTermNode);

        List<Node<Object>> opTermNodes = new ArrayList<Node<Object>>();
        collectNodesByPath(node, "Tail/OperatorTerm", opTermNodes);
        for (Node<Object> opTermNode : opTermNodes) {
            Node<Object> opNode = findNodeByPath(opTermNode, "Operator");
            if (opNode == null) {
                throw error(opTermNode, "[OperatorTerm] should contain [Operator]");
            }
            Node<Object> termNode = findNodeByPath(opTermNode, "Term");
            if (termNode == null) {
                throw error(opTermNode, "[OperatorTerm] should contain [Term]");
            }

            Function termFunc = term(termNode);

            String op = getNodeText(opNode, inputBuffer);
            if ("+".equals(op)) {
                result = new Addition(result, termFunc);
            } else if ("-".equals(op)) {
                result = new Subtraction(result, termFunc);
            } else {
                throw error(opNode, "[Operator] should be '+' or '-'");
            }
        }

        return result;
    }

    private Function term(Node<Object> node) throws ParsingFailureException {
        if (!"Term".equals(node.getLabel())) {
            errorAndContinue(node, "[Term] mismatch");
        }
        Node<Object> firstFactorNode = findNodeByPath(node, "Factor");
        if (firstFactorNode == null) {
            throw error(node, "first [Factor] not found in [Term]");
        }

        Function result = factor(firstFactorNode);

        List<Node<Object>> opFactorNodes = new ArrayList<Node<Object>>();
        collectNodesByPath(node, "Tail/OperatorFactor", opFactorNodes);
        for (Node<Object> opFactorNode : opFactorNodes) {
            Node<Object> opNode = findNodeByPath(opFactorNode, "Operator");
            if (opNode == null) {
                throw error(opFactorNode, "[OperatorFactor] should contain [Operator]");
            }
            Node<Object> factorNode = findNodeByPath(opFactorNode, "Factor");
            if (factorNode == null) {
                throw error(opFactorNode, "[OperatorFactor] should contain [Factor]");
            }

            Function factorFunc = factor(factorNode);

            String op = getNodeText(opNode, inputBuffer);
            if ("*".equals(op)) {
                result = new Multiplication(result, factorFunc);
            } else if ("/".equals(op)) {
                result = new Division(result, factorFunc);
            } else {
                throw error(opNode, "[Operator] should be '*' or '/'");
            }
        }

        return result;
    }

    private Function factor(Node<Object> node) throws ParsingFailureException {
        if (!"Factor".equals(node.getLabel())) {
            errorAndContinue(node, "[Factor] mismatch");
        }
        if (node.getChildren().size() < 1) {
            throw error(node, "[Factor] should contain at least one child");
        } else if (node.getChildren().size() != 1) {
            errorAndContinue(node, "[Factor] should contain one child");
        }

        Node<Object> firstSubNode = node.getChildren().get(0);
        String label = firstSubNode.getLabel();
        if ("Constant".equals(label)) {
            return constant(firstSubNode);
        } else if ("MathFunc".equals(label)) {
            return mathFunc(firstSubNode);
        } else if ("Variable".equals(label)) {
            return variable(firstSubNode);
        } else if ("Parents".equals(label)) {
            return parents(firstSubNode);
        } else {
            throw error(node, "[Factor] should contain [Constant], [MathFunc], " +
                    "[Variables], [Parents]");
        }
    }

    private Function parents(Node<Object> node) throws ParsingFailureException {
        if (!"Parents".equals(node.getLabel())) {
            errorAndContinue(node, "[Parents] mismatch");
        }
        Node<Object> exprNode = findNodeByPath(node, "Expression");
        if (exprNode == null) {
            throw error(node, "[Expression] should be in [Parents]");
        }
        return expression(exprNode);
    }

    private Function variable(Node<Object> node) {
        if (!"Variable".equals(node.getLabel())) {
            errorAndContinue(node, "[Variable] mismatch");
        }
        String name = getNodeText(node, inputBuffer);
        return new Variable(name);
    }

    private Function mathFunc(Node<Object> node) throws ParsingFailureException {
        if (!"MathFunc".equals(node.getLabel())) {
            errorAndContinue(node, "[MathFunc] mismatch");
        }
        Node<Object> nameNode = findNodeByPath(node, "Name");
        if (nameNode == null) {
            throw error(node, "[Name] should be in [MathFunc]");
        }

        Node<Object> argumentsNode = findNodeByPath(node, "Arguments");
        if (argumentsNode == null) {
            throw error(node, "[Arguments] should be in [MathFunc]");
        }

        Function []args = arguments(argumentsNode);

        String name = getNodeText(nameNode, inputBuffer);

        MathFunctionType type = MathFunctionType.bySignature(name, args.length);
        if (type == null) {
            throw error(nameNode, "Function matching [Name] = '" + name
                    + "' and [Arguments] count = " + args.length +
                    " no found");
        }

        return new MathFunction(type, args);
    }

    private Function[] arguments(Node<Object> node) throws ParsingFailureException {
        if (!"Arguments".equals(node.getLabel())) {
            errorAndContinue(node, "[Arguments] mismatch");
        }

        Node<Object> firstExprNode = findNodeByPath(node, "Expression");
        if (firstExprNode == null) {
            throw error(node, "first [Expression] not found in [Arguments]");
        }

        List<Node<Object>> argExprNodes = new ArrayList<Node<Object>>();
        argExprNodes.add(firstExprNode);

        collectNodesByPath(node, "Tail/CommaExpression/Expression", argExprNodes);
        List<Function> result = new ArrayList<Function>();

        for (Node<Object> exprNode : argExprNodes) {
            result.add(expression(exprNode));
        }

        return result.toArray(new Function[result.size()]);
    }

    private Function constant(Node<Object> node) {
        if (!"Constant".equals(node.getLabel())) {
            errorAndContinue(node, "[Constant] mismatch");
        }
        String strValue = getNodeText(node, inputBuffer);
        double value = 0;
        try{
            value = Double.parseDouble(strValue);
        } catch (NumberFormatException ex) {
            errorAndContinue(node, "Bad double literal: '" + strValue + "'");
        }
        return new Constant(value);
    }


    private static class SkipWhitespaceOrEOIPredicate implements Predicate<Node<Object>> {
        @Override
        public boolean apply(Node<Object> input) {
            return !("WhiteSpace".equals(input.getLabel())
                    || "EOI".equals(input.getLabel()));
        }
    }
}
