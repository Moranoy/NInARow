package Logic.SequenceSearchers.Strategy;

import Logic.Interfaces.ISequenceSearcherStrategy;
import Logic.Models.GameSettings;

public class BottomSequenceSearcherStrategy implements ISequenceSearcherStrategy {
    private int mNumberOfRows;

    public BottomSequenceSearcherStrategy(int mNumberOfRows) {
        this.mNumberOfRows = mNumberOfRows;
    }

    @Override
    public int GetNextRow(int currentRow) {
        return currentRow + 1;
    }

    @Override
    public int GetNextColumn(int currentColumn) {
        return currentColumn;
    }

    @Override
    public boolean shouldStopLooking(int row, int column) {
        return row >= this.mNumberOfRows;
    }
}
