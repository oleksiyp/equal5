package engine.calculation.vector;

import engine.calculation.FunctionVisitor;
import engine.calculation.functions.*;
import engine.calculation.vector.opeartions.*;
import engine.expressions.Function;

import java.util.*;

/**
 * User: Oleksiy Pylypenko
 * At: 3/12/13  6:44 PM
 */
public class VectorMachineBuilder {
    private final Function []functions;
    private int concurrency = Runtime.getRuntime().availableProcessors();

    public VectorMachineBuilder(Function []functions) {
        this.functions = functions;
    }

    public int getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }

    public VectorMachine build() {
        ConstructingVisitor visitor = new ConstructingVisitor();
        int []resultSlots = new int[functions.length];
        for (int i = 0; i < functions.length; i++) {
            resultSlots[i] = visitor.build(functions[i]);
        }

        List<VectorOperation> op = visitor.operations;
        VectorOperation[] operationsArray = op.toArray(new VectorOperation[op.size()]);
        if (concurrency <= 1) {
            return new SequentialVectorMachine(visitor.nSlots,
                    resultSlots,
                    operationsArray,
                    visitor.variables,
                    visitor.constants);
        } else {
            return new ParallelVectorMachine(visitor.nSlots,
                    resultSlots,
                    operationsArray,
                    visitor.variables,
                    visitor.constants,
                    concurrency);
        }
    }

    private class ConstructingVisitor implements FunctionVisitor {
        private final Stack<Integer> slotStack = new Stack<Integer>();
        private final List<VectorOperation> operations = new ArrayList<VectorOperation>();

        private final Map<Function, Integer> slotFunctions = new HashMap<Function, Integer>();

        private final Map<Integer, Double> constants = new HashMap<Integer, Double>();
        private final Map<String, Integer> variables = new HashMap<String, Integer>();

        private int nSlots = 0;

        public int build(Function function) {
            Integer slot = slotFunctions.get(function);
            if (slot != null) {
                return slot;
            }
            slotStack.push(nSlots++);
            function.accept(this);
            Integer resultSlot = slotStack.pop();
            slotFunctions.put(function, resultSlot);

            return resultSlot;
        }

        @Override
        public void visit(Constant constant) {
            constants.put(slotStack.peek(), constant.getValue());
        }

        @Override
        public void visit(Variable variable) {
            variables.put(variable.getString(), slotStack.peek());
        }

        @Override
        public void visit(Addition addition) {
            int lSlot = build(addition.getLeftSide());
            int rSlot = build(addition.getRightSide());

            int resultSlot = slotStack.peek();

            operations.add(
                    new AdditionVectorOperation(lSlot, rSlot, resultSlot)
            );
        }

        @Override
        public void visit(Subtraction subtraction) {
            int lSlot = build(subtraction.getLeftSide());
            int rSlot = build(subtraction.getRightSide());

            int resultSlot = slotStack.peek();

            operations.add(
                    new SubtractionVectorOperation(lSlot, rSlot, resultSlot)
            );
        }

        @Override
        public void visit(Multiplication multiplication) {
            int lSlot = build(multiplication.getLeftSide());
            int rSlot = build(multiplication.getRightSide());

            int resultSlot = slotStack.peek();

            operations.add(
                    new MultiplicationVectorOperation(lSlot, rSlot, resultSlot)
            );
        }

        @Override
        public void visit(Division division) {
            int lSlot = build(division.getLeftSide());
            int rSlot = build(division.getRightSide());

            int resultSlot = slotStack.peek();

            operations.add(
                    new DivisionVectorOperation(lSlot, rSlot, resultSlot)
            );
        }

        @Override
        public void visit(Power power) {
            int lSlot = build(power.getLeftSide());
            int rSlot = build(power.getRightSide());

            int resultSlot = slotStack.peek();

            operations.add(
                    new PowerVectorOperation(lSlot, rSlot, resultSlot)
            );
        }

        @Override
        public void visit(MathFunction mathFunction) {
            Function[] arguments = mathFunction.getArguments();
            int nArgs = arguments.length;
            int []slots = new int[nArgs];
            for (int i = 0; i < nArgs; i++) {
                slots[i] = build(arguments[i]);
            }

            int resultSlot = slotStack.peek();

            operations.add(new MathVectorOperation(mathFunction.getType(), slots, resultSlot));
        }
    }
}
