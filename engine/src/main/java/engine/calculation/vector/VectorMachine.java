package engine.calculation.vector;


/**
 * User: Oleksiy Pylypenko
 * Date: 3/12/13
 * Time: 11:52 PM
 */
public interface VectorMachine {
    int getSlotCount();

    int[] getResultSlots();

    Integer getArgumentSlot(String name);

    State newState(int size);

    interface State {
        void init(boolean clear);

        double []get(int slot);

        void applyOperations();

        void applyAndEstimateOperations(TimeReporter timeReporter);
    }
}
