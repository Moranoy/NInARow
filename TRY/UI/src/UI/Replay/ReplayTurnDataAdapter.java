package UI.Replay;

import Logic.Models.Cell;
import Logic.Models.PlayedTurnData;
import javafx.beans.property.IntegerProperty;

import java.util.*;
import java.util.stream.Collectors;

// An adapter class for the replay manager, manipulate the return data for "getPrevious" so that the UI could handle it better.
public class ReplayTurnDataAdapter {
    private ReplayManager mReplayManager;

    public ReplayTurnDataAdapter() {
        this.mReplayManager = new ReplayManager();
    }

    public void start(List<PlayedTurnData> playedTurnData) {
        this.mReplayManager.start(playedTurnData);
    }

    public PlayedTurnData getNextTurnData() {
        // Return the original next turn data
        return this.mReplayManager.getNextTurnData();
    }

    public PlayedTurnData getPreviousTurnData() {
        PlayedTurnData previousTurnData = this.mReplayManager.getPreviousTurnData();
        PlayedTurnData manipulatedTurnData = null;

        switch(previousTurnData.getTurnType()) {
            case AddDisc:
                manipulatedTurnData = this.manipulatePreviousAddDiscTurn(previousTurnData);
                break;
            case Popout:
                manipulatedTurnData = this.manipulatePreviousPopoutTurn(previousTurnData);
                break;
        }

        manipulatedTurnData.setTurnType(previousTurnData.getTurnType());
        manipulatedTurnData.setPlayerTurn(previousTurnData.getPlayerTurn());
        manipulatedTurnData.setGameState(previousTurnData.getGameState());

        return manipulatedTurnData;
    }

    private PlayedTurnData manipulatePreviousAddDiscTurn(PlayedTurnData previousTurnData) {
        PlayedTurnData manipulatedTurnData = new PlayedTurnData();
        Collection<Cell> updatedCellCollection = new ArrayList<>();

        // In add disc there's only 1 updated cell. Make the player that occupies that cell null.
        Cell updatedCell = ((List<Cell>)previousTurnData.getUpdatedCellsCollection()).get(0).getShallowCopy();
        updatedCell.setPlayer(null);
        updatedCellCollection.add(updatedCell);

        manipulatedTurnData.setUpdatedCellsCollection(updatedCellCollection);

        return manipulatedTurnData;
    }

    private PlayedTurnData manipulatePreviousPopoutTurn(PlayedTurnData previousTurnData) {
        PlayedTurnData manipulatedTurnData = new PlayedTurnData();
        Cell updatedCellArray[] = new Cell[previousTurnData.getUpdatedCellsCollection().size()];
        Iterator<Cell> cellIterator = previousTurnData.getUpdatedCellsCollection().iterator();
        int index = 0;

        // Convert the updated cells collection to an array.
        while (cellIterator.hasNext()) {
            // Shallow copy cell so that manipulating it won't affect the original cell.
            updatedCellArray[index++] = cellIterator.next().getShallowCopy();
        }

        // Sort cells by rows in an ascending order (row 0...num of rows - 1)
        Arrays.sort(updatedCellArray, Comparator.comparingInt(Cell::getRowIndex));

        // Set the player of each cell with the player of the cell bellow it.
        for(int i = 0; i < updatedCellArray.length - 1; i++) {
            Cell currentCell = updatedCellArray[i];
            Cell lowerCell = updatedCellArray[i + 1];

            currentCell.setPlayer(lowerCell.getPlayer());
        }

        // Set the player of lowest cell in column to be the one who played the turn.
        updatedCellArray[updatedCellArray.length - 1].setPlayer(previousTurnData.getPlayer());

        // Convert array to collection.
        Collection<Cell> updatedCellsCollection = Arrays.stream(updatedCellArray).collect(Collectors.toList());

        manipulatedTurnData.setUpdatedCellsCollection(updatedCellsCollection);

        return manipulatedTurnData;
    }

    public boolean hasNext() {
        return this.mReplayManager.hasNext();
    }

    public boolean hasPrevious() {
        return this.mReplayManager.hasPrevious();
    }

    public IntegerProperty getCurrentTurnNumberInReplayProperty() {
        return this.mReplayManager.getCurrentTurnNumberInReplayProperty();
    }

    public List<PlayedTurnData> getAllNextTurnsCollection() {
        return this.mReplayManager.getAllNextTurnsCollection();
    }

    public int getNumberOfTurnsPlayed() {
        return this.mReplayManager.getNumberOfTurnsPlayed();
    }
}
