package engine.calculation.vector.opeartions;

import engine.calculation.functions.MathFunctionType;
import engine.calculation.functions.MathFunctionTypeVisitor;

/**
 * User: Oleksiy Pylypenko
 * At: 3/13/13  4:37 PM
 */
public class MathVectorOperation extends VectorOperation {
    private MathFunctionType type;
    private int[] slots;
    private int resultSlot;

    public MathVectorOperation(MathFunctionType type, int[] slots, int resultSlot) {
        this.type = type;
        this.slots = slots;
        this.resultSlot = resultSlot;
    }

    @Override
    public void apply(int size, double[][] data) {
        type.accept(new MathVectorCalcVisitor(size, data));
    }

    @Override
    public boolean applicable(boolean[] calculated) {
        for (int slot : slots) {
            if (!calculated[slot]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void markCalculated(boolean[] calculatedSlots) {
        calculatedSlots[resultSlot] = true;
    }

    private class MathVectorCalcVisitor implements MathFunctionTypeVisitor {
        private final int size;
        private final double[] resultVector;
        private final double[][] inputVectors;

        public MathVectorCalcVisitor(int size, double[][] data) {
            this.size = size;

            resultVector = data[resultSlot];

            int nSlots = slots.length;

            inputVectors = new double[nSlots][];
            for (int i = 0; i < nSlots; i++) {
                inputVectors[i] = data[slots[i]];
            }
        }


        @Override
        public void identity() {
            System.arraycopy(inputVectors[0], 0, resultVector, 0, size);
        }

        @Override
        public void sin() {
            double[] x = inputVectors[0];
            for (int i = 0; i < size; i++) {
                resultVector[i] = Math.sin(x[i]);
            }
        }

        @Override
        public void cos() {
            double[] x = inputVectors[0];
            for (int i = 0; i < size; i++) {
                resultVector[i] = Math.sin(x[i]);
            }
        }

        @Override
        public void tan() {
            double[] x = inputVectors[0];
            for (int i = 0; i < size; i++) {
                resultVector[i] = Math.tan(x[i]);
            }
        }

        @Override
        public void asin() {
            double[] x = inputVectors[0];
            for (int i = 0; i < size; i++) {
                resultVector[i] = Math.asin(x[i]);
            }
        }

        @Override
        public void acos() {
            double[] x = inputVectors[0];
            for (int i = 0; i < size; i++) {
                resultVector[i] = Math.acos(x[i]);
            }
        }

        @Override
        public void atan() {
            double[] x = inputVectors[0];
            for (int i = 0; i < size; i++) {
                resultVector[i] = Math.atan(x[i]);
            }
        }

        @Override
        public void exp() {
            double[] x = inputVectors[0];
            for (int i = 0; i < size; i++) {
                resultVector[i] = Math.exp(x[i]);
            }
        }

        @Override
        public void log() {
            double[] x = inputVectors[0];
            for (int i = 0; i < size; i++) {
                resultVector[i] = Math.log(x[i]);
            }
        }

        @Override
        public void sqrt() {
            double[] x = inputVectors[0];
            for (int i = 0; i < size; i++) {
                resultVector[i] = Math.log(x[i]);
            }
        }

        @Override
        public void remainder() {
            double[] x = inputVectors[0];
            double[] y = inputVectors[1];
            for (int i = 0; i < size; i++) {
                resultVector[i] = Math.IEEEremainder(x[i], y[i]);
            }
        }

        @Override
        public void ceil() {
            double[] x = inputVectors[0];
            for (int i = 0; i < size; i++) {
                resultVector[i] = Math.ceil(x[i]);
            }
        }

        @Override
        public void floor() {
            double[] x = inputVectors[0];
            for (int i = 0; i < size; i++) {
                resultVector[i] = Math.floor(x[i]);
            }
        }

        @Override
        public void atan2() {
            double[] x = inputVectors[0];
            double[] y = inputVectors[1];
            for (int i = 0; i < size; i++) {
                resultVector[i] = Math.atan2(x[i], y[i]);
            }
        }

        @Override
        public void pow() {
            double[] x = inputVectors[0];
            double[] y = inputVectors[1];
            for (int i = 0; i < size; i++) {
                resultVector[i] = Math.pow(x[i], y[i]);
            }
        }

        @Override
        public void round() {
            double[] x = inputVectors[0];
            for (int i = 0; i < size; i++) {
                resultVector[i] = Math.round(x[i]);
            }
        }

        @Override
        public void random() {
            for (int i = 0; i < size; i++) {
                resultVector[i] = Math.random();
            }
        }

        @Override
        public void abs() {
            double[] x = inputVectors[0];
            for (int i = 0; i < size; i++) {
                resultVector[i] = Math.abs(x[i]);
            }
        }

        @Override
        public void max() {
            double[] x = inputVectors[0];
            double[] y = inputVectors[1];
            for (int i = 0; i < size; i++) {
                resultVector[i] = Math.max(x[i], y[i]);
            }
        }

        @Override
        public void min() {
            double[] x = inputVectors[0];
            double[] y = inputVectors[1];
            for (int i = 0; i < size; i++) {
                resultVector[i] = Math.min(x[i], y[i]);
            }
        }

        @Override
        public void signum() {
            double[] x = inputVectors[0];
            for (int i = 0; i < size; i++) {
                resultVector[i] = Math.signum(x[i]);
            }
        }

        @Override
        public void sinh() {
            double[] x = inputVectors[0];
            for (int i = 0; i < size; i++) {
                resultVector[i] = Math.sinh(x[i]);
            }
        }

        @Override
        public void cosh() {
            double[] x = inputVectors[0];
            for (int i = 0; i < size; i++) {
                resultVector[i] = Math.cosh(x[i]);
            }
        }

        @Override
        public void tanh() {
            double[] x = inputVectors[0];
            for (int i = 0; i < size; i++) {
                resultVector[i] = Math.tanh(x[i]);
            }
        }

        @Override
        public void hypot() {
            double[] x = inputVectors[0];
            double[] y = inputVectors[1];
            for (int i = 0; i < size; i++) {
                resultVector[i] = Math.hypot(x[i], y[i]);
            }
        }

        @Override
        public void expm1() {
            double[] x = inputVectors[0];
            for (int i = 0; i < size; i++) {
                resultVector[i] = Math.expm1(x[i]);
            }
        }

        @Override
        public void log1p() {
            double[] x = inputVectors[0];
            for (int i = 0; i < size; i++) {
                resultVector[i] = Math.log1p(x[i]);
            }
        }
    }
}
