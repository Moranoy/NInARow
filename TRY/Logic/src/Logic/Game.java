package Logic;

import Logic.ComputerPlayer.ComputerPlayerAlgo;
import Logic.Enums.eGameState;
import Logic.Enums.ePlayerType;
import Logic.Enums.eTurnType;
import Logic.Enums.eVariant;
import Logic.Exceptions.InvalidInputException;
import Logic.Exceptions.InvalidUserInputException;
import Logic.Interfaces.IComputerPlayerAlgo;
import Logic.Interfaces.IGameStatus;
import Logic.Managers.HistoryManager;
import Logic.Models.*;
import Logic.SequenceSearchers.SequenceSearcher;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class Game {
    private HistoryManager mHistoryManager;
    private GameStatus mGameStatus;
    private Board mGameBoard;
    private SequenceSearcher mSequenceSearcher;
    private IComputerPlayerAlgo mComputerPlayerAlgo;
    private List<Player> mPlayerList;
    private GameSettings mGameSettings;

    private final Object mGameLock = new Object();

    public Game(GameSettings gameSettings) {
        this.mHistoryManager = new HistoryManager();
        this.mGameStatus = new GameStatus(this);
        this.mSequenceSearcher = new SequenceSearcher();
        this.mComputerPlayerAlgo = new ComputerPlayerAlgo();
        this.mPlayerList = new ArrayList<>();
        this.mGameSettings = gameSettings;
        this.mGameBoard = new Board(mGameSettings.getRows(), mGameSettings.getColumns());
    }

    // Concurrency.
    public Object getmGameLock() {
        return mGameLock;
    }


    // ILogic interface implementation.

    public void StartGame() {
        // Set game board
        this.mGameBoard.Init(mGameSettings.getRows(), mGameSettings.getColumns());
        this.mSequenceSearcher.Clear();
        this.mSequenceSearcher.setBoard(this.mGameBoard);
        this.mSequenceSearcher.setGameSettings(this.mGameSettings);
        boolean isPopoutMode = this.mGameSettings.getVariant().equals(eVariant.Popout);
        this.mComputerPlayerAlgo.Init(this.mGameBoard, this.mGameSettings, isPopoutMode);
        this.mHistoryManager.Clear();
        this.mGameStatus.StartNewGame();

        if(this.mGameStatus.mCurrentPlayer.getType().equals(ePlayerType.Computer)) {
            try {
                this.playComputerAlgoTurn();
            } catch (InvalidInputException e) { // This exception should never happen. If it does, print stack trace.
                e.printStackTrace();
            }
        }
    }

    public Map<Player, Collection<Cell>> getPlayerToWinningSequencesMap() {
        Map<Player, Collection<Cell>> playerToWinningSequenceMap;

        if(this.mPlayerList.size() == 1) {
            // Player won by default, not by winning sequence.
            playerToWinningSequenceMap = new HashMap<>();
            playerToWinningSequenceMap.put(this.GetCurrentPlayer(), null);
        } else {
            playerToWinningSequenceMap = this.mSequenceSearcher.getPlayerToWinningSequencesMap();
        }

        return playerToWinningSequenceMap;
    }

    public GameStatus GetGameStatus() {
        return mGameStatus;
    }

    public GameDescriptionData getGameDescriptionData() {
        GameDescriptionData gameDescriptionData = new GameDescriptionData();

        gameDescriptionData.setmGameName(this.mGameSettings.getmGameName());
        gameDescriptionData.setmGameState(this.mGameStatus.getGameState());
        gameDescriptionData.setmMaxPlayers(this.mGameSettings.getGameNumberOfPlayers());
        gameDescriptionData.setmCurrentNumberOfPlayers(this.mPlayerList.size());
        gameDescriptionData.setmVariant(this.mGameSettings.getVariant());
        gameDescriptionData.setmRows(this.mGameSettings.getRows());
        gameDescriptionData.setmColumns(this.mGameSettings.getColumns());
        gameDescriptionData.setmTarget(this.mGameSettings.getTarget());
        gameDescriptionData.setmUploaderName(this.mGameSettings.getUploaderName());

        return gameDescriptionData;
    }

    private void executeTurn(PlayTurnParameters playTurnParameters) throws InvalidInputException {
        PlayedTurnData playedTurnData = playTurnParameters.getmTurnType().equals(eTurnType.AddDisc) ?
                this.addDisc(playTurnParameters.getmSelectedColumn()) : this.popout(playTurnParameters.getmSelectedColumn());

        playedTurnData.setTurnType(playTurnParameters.getmTurnType());

        // Update game state when turn ends.
        this.finishedPlayingTurn(playedTurnData);
    }

    private void playComputerAlgoTurn() throws InvalidInputException {
        if(this.mComputerPlayerAlgo.hasNextPlay(this.mGameStatus.mCurrentPlayer)) {
            // Use algo to determine next computer turn.
            PlayTurnParameters computerPlayTurnParams = this.mComputerPlayerAlgo.getNextPlay(this.mGameStatus.mCurrentPlayer);
            this.playTurn(computerPlayTurnParams);
        } else {
            // Computer player cannot make a play - Draw!
            // In Ex02 we shouldn't get to this situation because a draw would've been determined after the previous turn.
            // Check again in Ex03
        }
    }

    // This function should be called when a player has ended his turn (but not on playerQuit)
    private void finishedPlayingTurn(PlayedTurnData playedTurnData) {
        this.finishedPlayingTurn(playedTurnData, true);
    }

    // Second parameter should be true only when player quit. When a player has quit, game status is updated
    // Through another function.
    private void finishedPlayingTurn(PlayedTurnData playedTurnData, boolean shouldUpdateGameStatus) {
        this.mGameStatus.setGameState(playedTurnData.getGameState());
        this.mHistoryManager.SetCurrentTurn(playedTurnData);
        if(shouldUpdateGameStatus) {
            this.mGameStatus.FinishedTurn();
        }
    }

    private PlayedTurnData addDisc(int column) throws InvalidInputException {
        Cell chosenCell = this.mGameBoard.UpdateBoard(column, this.mGameStatus.getPlayer()); // send parameter to logic board
        System.out.println("#$# Entering disc to cell " + chosenCell.getRowIndex() + "," + chosenCell.getColumnIndex());
        return this.updateGameStatusAfterDiscAdded(chosenCell);
    }

    private PlayedTurnData popout(int column) throws InvalidInputException {
        if(mGameBoard.CanPlayerPerformPopoutForColumn(this.mGameStatus.getPlayer(), column)) {
            PlayedTurnData playedTurnData = new PlayedTurnData();
            Collection<Cell> updatedCells = this.mGameBoard.PopoutAndGetUpdatedCells(column);

            playedTurnData.setUpdatedCellsCollection(updatedCells);
            // Cannot draw after popout - only check if a player won.
            playedTurnData.setGameState(this.mSequenceSearcher.CheckColumnForWinningSequences(column) ? eGameState.Won : eGameState.Active);
            playedTurnData.setPlayerTurn(this.mGameStatus.mCurrentPlayer);
            playedTurnData.setTurnType(eTurnType.Popout);

            // Check if there is a winning sequence starting from a cell in the selected column as a result of the Popout.
            return playedTurnData;
        } else {
            throw new InvalidInputException("Player named " + this.mGameStatus.getPlayer().getName() + " cannot perform popout on column " + column);
        }
    }

    private void currentPlayerQuit(PlayTurnParameters turnParameters) {
        PlayedTurnData playedTurnData = new PlayedTurnData();
        eGameState gameState = eGameState.Active;

        // If there are more than 2 players, the game will go on after player quits.
        Collection<Cell> updatedCells = this.mGameBoard.RemoveAllPlayerDiscsFromBoardAndGetUpdatedCells(this.mGameStatus.getPlayer());
        playedTurnData.setUpdatedCellsCollection(updatedCells);

        if (this.mPlayerList.size() > 2) {
            if (this.mSequenceSearcher.CheckEntireBoardForWinningSequences()) {  //run all over board and check if someone won
                gameState = eGameState.Won;
            } else if (this.isDrawForNextPlayer()) {
                gameState = eGameState.Draw;
            }
        } else {
            gameState = eGameState.Won;
        }

        this.mGameStatus.CurrentPlayerQuitGame();

        playedTurnData.setGameState(gameState);
        playedTurnData.setTurnType(turnParameters.getmTurnType());

        // Don't update game status, it was done in GameStatus.CurrentPlayerQuit.
        this.finishedPlayingTurn(playedTurnData, false);

    }

    public boolean isGameActive(){
        return (mGameStatus != null && mGameStatus.getGameState() == eGameState.Active);
    }

    private PlayedTurnData updateGameStatusAfterDiscAdded(Cell updatedCell) {
        eGameState currentGameState = this.getCurrentGameState(updatedCell);
        PlayedTurnData playedTurnData = new PlayedTurnData();
        List<Cell> updatedCells = new ArrayList<>();

        updatedCells.add(updatedCell);
        playedTurnData.setUpdatedCellsCollection(updatedCells);
        playedTurnData.setPlayerTurn(updatedCell.getPlayer());
        playedTurnData.setGameState(currentGameState);

        return playedTurnData;
    }

    private eGameState getCurrentGameState(Cell updatedCell) {
        eGameState gameState;

        if(mSequenceSearcher.CheckCellForWinningSequence(updatedCell)) {
            gameState = eGameState.Won;
        } else if (this.isDrawForNextPlayer()) {
            gameState = eGameState.Draw;
        } else {
            gameState = eGameState.Active;
        }

        return gameState;
    }

    public List<PlayedTurnData> GetTurnHistory() {
        // send parameters to history manager
        return this.mHistoryManager.GetGameHistory();
    }

    public Player GetCurrentPlayer() {
        return this.mGameStatus.getPlayer();
    }

    public Board getBoard() {
        return mGameBoard;
    }

    public eGameState GetGameState() {
        return mGameStatus.getGameState();
    }

    public void reset() {
        this.mGameBoard.Clear();
        this.mHistoryManager.Clear();
        this.mSequenceSearcher.Clear();
        this.mGameStatus.clear();
    }

    public Collection<Integer> getAvailablePopoutColumnsForPlayer(Player player) {
        return mGameBoard.getAvailablePopoutColumnsForCurrentPlayer(player);
    }

    private boolean isDrawForNextPlayer() {
        boolean isPopoutGameMode = mGameSettings.getVariant().equals(eVariant.Popout);
        // Check if next player can perform popout. If he can't, and the board is full - game is in a draw.
        boolean canNextPlayerPopout = this.mGameBoard.CanPlayerPerformPopout(this.mGameStatus.getNextPlayer());
        boolean isBoardFull = this.mGameBoard.IsBoardFull();
        boolean isDraw;

        if(!isPopoutGameMode && isBoardFull) {
            // Not in popout mode and board is full
            isDraw = true;
        } else if (isPopoutGameMode && isBoardFull && !canNextPlayerPopout) {
            // In popout mode, board is full and next player can't popout
            isDraw = true;
        } else {
            isDraw = false;
        }

        return isDraw;
    }

    public void playTurn(PlayTurnParameters playedTurnParams) throws InvalidInputException {
        // Check if game is active and if play was made by current player.
        if (this.isGameActive() && playedTurnParams.getmPlayerName().equals(this.mGameStatus.mCurrentPlayer.getName())) {
            // Check turn type
            if (playedTurnParams.getmTurnType().equals(eTurnType.PlayerQuit)) {
                this.currentPlayerQuit(playedTurnParams);
            } else {
                this.executeTurn(playedTurnParams);
            }

            // Play computer algo if needed.
            if (this.mGameStatus.mCurrentPlayer.getType().equals(ePlayerType.Computer)) {
                this.playComputerAlgoTurn();
            }
        }
    }

    public List<PlayedTurnData> getTurnHistory() {
        return mHistoryManager.GetGameHistory();
    }

    public void addPlayer(Player player) {

        if(this.isGameActive()) {
            //TODO: exception
        } else if(this.mPlayerList.size() >= this.mGameSettings.getGameNumberOfPlayers()) {
            //TODO: exception
        } else if (this.mPlayerList.contains(player)) {
            //TODO: exception
        }

        this.mPlayerList.add(player);
    }

    public boolean shouldStartGame() {
        return this.mPlayerList.size() == this.mGameSettings.getGameNumberOfPlayers();
    }

    public List<Player> getPlayers() {
        return this.mPlayerList;
    }

    public boolean doesContainPlayerWithName(String username) {
        boolean doesContainPlayerWithName = false;

        for(Player player: this.mPlayerList) {
            if(player.getName().equals(username)) {
                doesContainPlayerWithName = true;
                break;
            }
        }

        return doesContainPlayerWithName;
    }

    public String getCurrentPlayerName() {
        return this.isGameActive() ? this.mGameStatus.mCurrentPlayer.getName() : null;
    }

    public void playerQuitWhileGameIsNotActive(Player quittingPlayer) {
        // Game is not active yet. Remove player without any consequences.
        this.mPlayerList.remove(quittingPlayer);
    }

    public class GameStatus implements IGameStatus {
        private eGameState mGameState = eGameState.Inactive;
        private Player mCurrentPlayer;
        private ListIterator<Player> mPlayerIterator;
        private Instant mGameStart;
        private Game mGame;

        private GameStatus(Game game) {
            this.mGame = game;
        }

        // Getters/Setters

        public Player getPlayer() {
            return mCurrentPlayer;
        }

        @Override
        public eGameState getGameState() { return mGameState; }

        @Override
        public void setGameState(eGameState newGameState) {
            if(!this.mGameState.equals(newGameState)) {
                // Game state changed.
                switch(newGameState) {
                    case Inactive:
                        // restart game.
                        this.mGame.reset();
                        break;
                    case Draw:
                        // Start timer until reset
                        this.startRestartGameTimer();
                        break;
                    case Won:
                        // Start timer until reset
                        this.startRestartGameTimer();
                        break;
                }
            }

            this.mGameState = newGameState;
        }

        @Override
        public String getNameOfPlayerCurrentlyPlaying() {
            return mCurrentPlayer.getName();
        }

        @Override
        public Duration getGameDuration() {
            return Duration.between(mGameStart, Instant.now());
        }

        @Override
        public void clear() {
            this.mCurrentPlayer = null;
            mPlayerList.forEach(
                    (player) -> player.setTurnCounter(0) // Reset turn counter
            );
        }

        // API

        private void StartNewGame() {
            this.mPlayerIterator = mPlayerList.listIterator();
            this.mCurrentPlayer = this.mPlayerIterator.next();
            this.mGameState = eGameState.Active;
            mPlayerList.forEach(
                    (player) -> player.setTurnCounter(0) // Reset turn counter
            );
            this.mGameStart = Instant.now();
        }

        // Adjust game status when a player has finished his turn.
        private void FinishedTurn() {
            this.mCurrentPlayer.FinishedTurn();
            this.nextPlayer();
        }

        private void CurrentPlayerQuitGame() {
            // Was unable to perform this logic by using Iterator's remove function. Instead, using the following brute force technique.
            Player quittingPlayer = this.mCurrentPlayer;
            int quittingPlayerIndex = mPlayerList.indexOf(quittingPlayer);

            mPlayerList.remove(quittingPlayer); // Remove current player.
            this.mPlayerIterator = mPlayerList.listIterator(quittingPlayerIndex); // Reset iterator after list was changed.
            this.nextPlayer(); // Assign the current player to the iterator's next element.
        }

        // Helper functions

        private void nextPlayer() {
            // If done iterating over players, reset iterator.
            if(!this.mPlayerIterator.hasNext()) {
                this.mPlayerIterator = mPlayerList.listIterator();
            }

            this.mCurrentPlayer = this.mPlayerIterator.next();
        }

        private Player getNextPlayer() {
            Player nextPlayer;

            if(this.mPlayerIterator.hasNext()) {
                nextPlayer = this.mPlayerIterator.next(); // Go next and then set back to previous element
                this.mPlayerIterator.previous();
            } else {
                nextPlayer = mPlayerList.iterator().next(); // Get first element.
            }

            return nextPlayer;
        }

        private void startRestartGameTimer() {
            Timer restartGameTimer = new Timer();
            final int TIME_TIL_RESET = 30 * 1000; // wait 30 seconds.

            restartGameTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    setGameState(eGameState.Inactive);
                    startNewGameIfNeeded();
                }
            }, TIME_TIL_RESET);
        }

        private void startNewGameIfNeeded() {
            Timer restartGameIfNeededTimer = new Timer();
            final int TIME_TO_WAIT = 5 * 1000; // wait 5 seconds.

            restartGameIfNeededTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(mGame.shouldStartGame()) {
                        mGame.StartGame();
                    }
                }
            }, TIME_TO_WAIT);
        }
    }
}
