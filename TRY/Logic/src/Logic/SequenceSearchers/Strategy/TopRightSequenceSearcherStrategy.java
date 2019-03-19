package Logic.SequenceSearchers.Strategy;

import Logic.Interfaces.ISequenceSearcherStrategy;
import Logic.Models.GameSettings;

public class TopRightSequenceSearcherStrategy implements ISequenceSearcherStrategy {

    private int mNumberOfColumns;

    public TopRightSequenceSearcherStrategy(int mNumberOfColumns) {
        this.mNumberOfColumns = mNumberOfColumns;
    }

    @Override
    public int GetNextRow(int currentRow) {
        return currentRow - 1;
    }

    @Override
    public int GetNextColumn(int currentColumn) {
        return currentColumn + 1;
    }

    @Override
    public boolean shouldStopLooking(int row, int column) {
        return row < 0 || column >= this.mNumberOfColumns;
    }
}
