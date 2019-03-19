package Logic.SequenceSearchers.Strategy;

import Logic.Interfaces.ISequenceSearcherStrategy;
import Logic.Models.GameSettings;

public class BotLeftSequenceSearcherStrategy implements ISequenceSearcherStrategy {
    int mNumberOfRows;

    public BotLeftSequenceSearcherStrategy(int rows) {
        this.mNumberOfRows = rows;
    }

    @Override
    public int GetNextRow(int currentRow) {
        return currentRow + 1;
    }

    @Override
    public int GetNextColumn(int currentColumn) {
        return currentColumn - 1;
    }

    @Override
    public boolean shouldStopLooking(int row, int column) {
        return row >= this.mNumberOfRows || column < 0;
    }
}
