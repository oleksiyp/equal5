package engine.calculation.vector;

import engine.expressions.Function;

import java.util.Arrays;

/**
 * User: Oleksiy Pylypenko
 * At: 3/12/13  6:43 PM
 */
public class VectorMachineEvaluator implements ConcurrentVectorEvaluator {
    private VectorMachine machine;
    private VectorMachine.State state;

    private int size;
    private TimeReporter timeReporter;
    private Function[] functions;
    private Integer concurrency;
    {
        clear();
    }

    public VectorMachineEvaluator() {
    }

    @Override
    public void setFunctions(Function []functions) {
        if (functions == null) {
            throw new IllegalArgumentException("function");
        }
        if (!Arrays.equals(functions, this.functions)) {
            this.functions = functions;
            this.machine = null;
            this.state = null;
        }
    }

    @Override
    public void setSize(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("size");
        }
        if (this.size != size) {
            state = null;
            this.size = size;
        }
    }

    @Override
    public void setTimeReporter(TimeReporter timeReporter) {
        this.timeReporter = timeReporter;
    }

    @Override
    public void prepare() {
        if (machine == null) {
            VectorMachineBuilder builder = new VectorMachineBuilder(functions);
            if (concurrency != null) {
                builder.setConcurrency(concurrency);
            }
            machine = builder.build();
        }

        if (state == null) {
            state = machine.newState(size);
            state.init(false);
        } else {
            state.init(true);
        }

    }

    @Override
    public double[][] calculate(VectorArguments arguments) {
        if (state == null || machine == null) {
            throw new IllegalStateException("not prepared");
        }

        for (String argument : arguments.getArguments()) {
            Integer slot = machine.getArgumentSlot(argument);
            if (slot == null) {
                continue;
            }
            VectorFiller filler = arguments.getVectorFiller(argument);
            if (filler == null) {
                throw new NullPointerException("filler");
            }

            filler.fill(state.get(slot));
        }

        if (timeReporter != null) {
            state.applyAndEstimateOperations(timeReporter);
        } else {
            state.applyOperations();
        }

        int[] slots = machine.getResultSlots();
        double [][]result = new double[slots.length][];
        int i = 0;
        for (int slot : slots) {
            result[i++] = state.get(slot);
        }

        return result;
    }

    @Override
    public void clear() {
        machine = null;
        state = null;
        size = 1;
        timeReporter = null;
        functions = new Function[0];
        concurrency = null;
    }

    @Override
    public void setConcurrency(Integer value) {
        this.concurrency = value;
        this.machine = null;
        this.state = null;
    }
}
