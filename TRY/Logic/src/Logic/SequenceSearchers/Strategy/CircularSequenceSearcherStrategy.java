package Logic.SequenceSearchers.Strategy;

import Logic.Interfaces.ISequenceSearcherStrategy;

public class CircularSequenceSearcherStrategy implements ISequenceSearcherStrategy {
    private ISequenceSearcherStrategy mInnerSequenceSearcherStrategy;
    private int mRowLimit;
    private int mColumnLimit;

    public CircularSequenceSearcherStrategy(ISequenceSearcherStrategy mInnerSequenceSearcherStrategy, int mRowLimit, int mColumnLimit) {
        this.mInnerSequenceSearcherStrategy = mInnerSequenceSearcherStrategy;
        this.mRowLimit = mRowLimit;
        this.mColumnLimit = mColumnLimit;
    }

    @Override
    public int GetNextRow(int currentRow) {
        int innerStrategyNextRow = mInnerSequenceSearcherStrategy.GetNextRow(currentRow);

        if(innerStrategyNextRow < 0) {
            innerStrategyNextRow += mRowLimit;
        }

        return  innerStrategyNextRow % mRowLimit;
    }

    @Override
    public int GetNextColumn(int currentColumn) {
        int innerStrategyNextColumn = mInnerSequenceSearcherStrategy.GetNextColumn(currentColumn);

        if(innerStrategyNextColumn < 0) {
            innerStrategyNextColumn += mColumnLimit;
        }

        return  innerStrategyNextColumn % mColumnLimit;    }

    @Override
    public boolean shouldStopLooking(int row, int column) {
        return false; // In a circular algorithm should never stop searching.
    }
}
