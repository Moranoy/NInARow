package Logic.ComputerPlayer;

import Logic.Enums.eTurnType;
import Logic.Enums.eVariant;
import Logic.Interfaces.IComputerPlayerAlgo;
import Logic.Models.*;
import Logic.SequenceSearchers.SequenceSearcher;

import java.util.*;

public class ComputerPlayerAlgo implements IComputerPlayerAlgo {
    private Board mBoard; // The game board.
    private SequenceSearcher mSequenceSearcher;
    private int mTarget;
    private boolean isPopoutMode;

    public void Init(Board board, GameSettings gameSettings, boolean isPopoutMode) {
        this.mBoard = board;
        mSequenceSearcher = new SequenceSearcher();
        mSequenceSearcher.setBoard(mBoard);
        this.mTarget = gameSettings.getTarget();
        this.mSequenceSearcher.setGameSettings(gameSettings);
        this.isPopoutMode = isPopoutMode;
    }

    @Override
    public PlayTurnParameters getNextPlay(Player playingPlayer){
        int selectedColumn;
        eTurnType turnType;

        if(shouldPopout(playingPlayer)) {
            selectedColumn = getPopoutColumn(playingPlayer);
            turnType = eTurnType.Popout;
        } else {
            selectedColumn = getColumnIndexOfLargestSequence(playingPlayer);
            turnType = eTurnType.AddDisc;
        }

        PlayTurnParameters turnData = new PlayTurnParameters(selectedColumn, turnType);

        turnData.setmPlayerName(playingPlayer.getName());

        return turnData;
    }

    @Override
    public boolean hasNextPlay(Player playingPlayer) {
        boolean isBoardFull = mBoard.IsBoardFull();
        boolean canPopout = this.isPopoutMode && mBoard.CanPlayerPerformPopout(playingPlayer);

        return !isBoardFull || canPopout; // If the board is full and the computer player is unable to popout - Draw.
    }

    private int getPopoutColumn(Player playingPlayer) {
        int selectedColumn = 0;

        for(int i = 0; i < this.mBoard.getColumns(); i++) {
            if(mBoard.CanPlayerPerformPopoutForColumn(playingPlayer, i)) {
                selectedColumn = i;
                break;
            }
        }

        return selectedColumn;
    }

    private boolean shouldPopout(Player playingPlayer){
        boolean isBoardFull = mBoard.IsBoardFull();
        boolean canPopout = isPopoutMode && mBoard.CanPlayerPerformPopout(playingPlayer);
        boolean shouldPopout = false;

        if(isBoardFull && canPopout) {
            shouldPopout = true;
        }

        return shouldPopout;
    }

    // Return a column index so that if we insert a disc to that column, we will get the largest sequence possible on the board.
    private int getColumnIndexOfLargestSequence(Player playingPlayer) {
        Map<Cell, Integer> firstAvailableCellToMaxSequenceLengthMap = new HashMap<>(); // a list of all the cells that are available for disc insertion

        for(int i = 0; i < this.mBoard.getColumns(); i++) {
            Cell firstAvailableCellInColumn = this.mBoard.getFirstAvailableCellForColumn(i);

            if(firstAvailableCellInColumn == null) {
                continue; // No first cell in column,
            }

            int maxSequenceLengthForCell = this.getSequenceMaxLengthStartingFromCell(firstAvailableCellInColumn, playingPlayer);
            firstAvailableCellToMaxSequenceLengthMap.put(firstAvailableCellInColumn, maxSequenceLengthForCell);
        }

        // Get an entry set of the available cell that has the max sequence length starting from it.
        Optional<Map.Entry<Cell, Integer>> cellToMaxSequenceLengthOfCellEntrySet = firstAvailableCellToMaxSequenceLengthMap
                .entrySet()
                .stream()
                .max(Comparator.comparingInt(Map.Entry::getValue));

        int selectedColumn;

        if(cellToMaxSequenceLengthOfCellEntrySet.isPresent() && cellToMaxSequenceLengthOfCellEntrySet.get().getValue() > 0) {
            // We have found a sequence with length of more than 1 that we can continue. Get the column index of that cell.
            selectedColumn = cellToMaxSequenceLengthOfCellEntrySet.get().getKey().getColumnIndex();
        } else {
            // No sequence to continue, Get first available column.
            selectedColumn = this.getFirstAvailableColumn();
        }

        return selectedColumn;
    }

    // Use the sequence searcher to look for the largest sequence in the board.
    private int getSequenceMaxLengthStartingFromCell(Cell cell, Player playingPlayer) {
        // Search for the largest sequence
        return mSequenceSearcher.getLargestSequenceSize(cell, playingPlayer);
    }

    private int getFirstAvailableColumn() {
        int selectedColumn = 0;

        for(int i = 0; i < this.mBoard.getColumns(); i++) {
            if(!mBoard.IsColumnFull(i)) {
                selectedColumn = i;
                break;
            }
        }

        return selectedColumn;
    }
}
