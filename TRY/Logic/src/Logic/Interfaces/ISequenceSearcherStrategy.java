package Logic.Interfaces;

public interface ISequenceSearcherStrategy {
    int GetNextRow(int currentRow);

    int GetNextColumn(int currentColumn);

    boolean shouldStopLooking(int row, int column);
}
