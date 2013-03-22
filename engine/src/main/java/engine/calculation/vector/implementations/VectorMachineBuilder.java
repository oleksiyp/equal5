package engine.calculation.vector.implementations;

import engine.calculation.vector.builder.VectorMachineFactory;
import engine.calculation.vector.VectorMachine;
import engine.calculation.vector.opeartions.*;
import engine.expressions.Function;

import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * User: Oleksiy Pylypenko
 * At: 3/12/13  6:44 PM
 */
public class VectorMachineBuilder implements VectorMachineFactory {
    private ExecutorService executor;
    private int concurrency = 0;

    public VectorMachineBuilder() {
    }

    public void setConcurrency(int concurrency, ExecutorService executor) {
        if (concurrency <= 0) {
            throw new IllegalArgumentException("concurrency");
        }
        if (executor == null) {
            throw new IllegalArgumentException("executor");
        }
        this.concurrency = concurrency;
        this.executor = executor;
    }


    public ExecutorService getExecutor() {
        return executor;
    }

    public int getConcurrency() {
        return concurrency;
    }

    @Override
    public VectorMachine create(Function[] functions) {
        VectorMachineConstructingVisitor visitor = new VectorMachineConstructingVisitor();
        int []resultSlots = new int[functions.length];
        for (int i = 0; i < functions.length; i++) {
            resultSlots[i] = visitor.build(functions[i]);
        }

        List<VectorOperation> op = visitor.getOperations();
        VectorOperation[] operationsArray = op.toArray(new VectorOperation[op.size()]);
        if (concurrency <= 1 || executor == null) {
            return new SequentialVectorMachine(visitor.getNSlots(),
                    resultSlots,
                    operationsArray,
                    visitor.getVariables(),
                    visitor.getConstants());
        } else {
            return new ParallelVectorMachine(visitor.getNSlots(),
                    resultSlots,
                    operationsArray,
                    visitor.getVariables(),
                    visitor.getConstants(),
                    executor,
                    concurrency);
        }
    }

}
