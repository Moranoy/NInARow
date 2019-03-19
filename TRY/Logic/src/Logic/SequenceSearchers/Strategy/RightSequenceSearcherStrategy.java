package Logic.SequenceSearchers.Strategy;

import Logic.Interfaces.ISequenceSearcherStrategy;
import Logic.Models.GameSettings;

public class RightSequenceSearcherStrategy implements ISequenceSearcherStrategy {

    private int mNumberOfColumns;

    public RightSequenceSearcherStrategy(int mNumberOfColumns) {
        this.mNumberOfColumns = mNumberOfColumns;
    }

    @Override
    public int GetNextRow(int currentRow) {
        return currentRow;
    }

    @Override
    public int GetNextColumn(int currentColumn) {
        return currentColumn + 1;
    }

    @Override
    public boolean shouldStopLooking(int row, int column) {
        return column >= this.mNumberOfColumns;
    }
}
