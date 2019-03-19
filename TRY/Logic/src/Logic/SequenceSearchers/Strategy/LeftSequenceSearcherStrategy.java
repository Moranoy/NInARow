package Logic.SequenceSearchers.Strategy;

import Logic.Interfaces.ISequenceSearcherStrategy;

public class LeftSequenceSearcherStrategy implements ISequenceSearcherStrategy {
    @Override
    public int GetNextRow(int currentRow) {
        return currentRow;
    }

    @Override
    public int GetNextColumn(int currentColumn) {
        return currentColumn - 1;
    }

    @Override
    public boolean shouldStopLooking(int row, int column) {
        return column < 0;
    }
}
