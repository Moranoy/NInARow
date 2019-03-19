package NinaRowHTTPClient.Game;

import Logic.Enums.eGameState;
import Logic.Enums.eTurnType;
import Logic.Enums.eVariant;
import Logic.Models.Cell;
import Logic.Models.PlayTurnParameters;
import Logic.Models.PlayedTurnData;
import Logic.Models.Player;
import NinaRowHTTPClient.Game.ResponseModels.PlayerAndWinningSequence;
import NinaRowHTTPClient.Game.ResponseModels.TurnHistoryResponse;
import UI.UIMisc.GameDescriptionData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.*;

// The main class used to communicate with the server. Acts as an http client.
public class GameClientLogic {
    private final static Integer INVALID_TURN_PARAMS_CODE = 498;

    // Game state
    private eGameState mGameState;
    private GameDescriptionData mGameData;
    private List<PlayedTurnData> mPlayedTurnDataList;
    private boolean mIsPopoutMode;

    //Players
    private String mLoggedInPlayerName;
    private List<Player> mCachedPlayersList;
    private boolean mIsMyTurn;

    private Gson mGson;

    private GameClientsCommunicationHandler mGameClientCommunicationHandler;

    // Delegate
    private IGameClientLogicDelegate mDelegate;

    public GameClientLogic(GameDescriptionData mGameData, String loggedInPlayerName, IGameClientLogicDelegate delegate) {
        this.mLoggedInPlayerName = loggedInPlayerName;
        this.mGameState = eGameState.Inactive;
        this.mGameData = mGameData;
        this.mDelegate = delegate;
        this.mGameClientCommunicationHandler = new GameClientsCommunicationHandler();
        this.mGson = new GsonBuilder().create();
        this.mIsPopoutMode = this.mGameData.getmVariant().equals(eVariant.Popout);

        this.mPlayedTurnDataList = new ArrayList<>();
        this.mIsMyTurn = false;

        this.startPullingDataFromServer();
    }

    private void startPullingDataFromServer() {
        this.observePlayers();
        this.observeGameState();
    }

    public void exitGame() {
        // Send request for player quit (no need for response)
    }

    public GameDescriptionData getmGameData() {
        return this.mGameData;
    }

    public List<PlayedTurnData> GetTurnHistory() {
        return this.mPlayedTurnDataList;
    }

    public boolean isGameActive() {
        return this.mGameState.equals(eGameState.Active);
    }

    public void quitBeforeGameStarts() {
        PlayTurnParameters playTurnParameters = new PlayTurnParameters(eTurnType.PlayerQuit);
        playTurnParameters.setmPlayerName(this.mLoggedInPlayerName);

        this.playTurnAsync(playTurnParameters);
    }

    // Fetch from server

    public void playTurnAsync(PlayTurnParameters playTurnParameters) {
        String paramsString = this.mGson.toJson(playTurnParameters);

        this.mGameClientCommunicationHandler.playTurn(this.mGameData.getmGameName(), paramsString, this::onPlayTurnSuccess,
                (errorMessage, statusCode) -> this.onPlayTurnFailure(errorMessage, statusCode, playTurnParameters)
        );

        if(playTurnParameters.getmTurnType().equals(eTurnType.PlayerQuit)) {
            // Leaving game room, stop observing server data.
            this.mGameClientCommunicationHandler.stopObserving();
        }
    }

    private void onPlayTurnSuccess(String stringResponse) {
        this.mIsMyTurn = false;
        this.mDelegate.myTurnFinished();
    }

    private void onPlayTurnFailure(String errorMessage, Integer statusCode, PlayTurnParameters playTurnParameters) {
        if(statusCode == INVALID_TURN_PARAMS_CODE) {
            switch (playTurnParameters.getmTurnType()) {
                case AddDisc:
                    this.mDelegate.discAddedToFullColumn(playTurnParameters.getmSelectedColumn());
                    break;
                case Popout:
                    this.mDelegate.currentPlayerCannotPopoutAtColumn(playTurnParameters.getmSelectedColumn());
                    break;
            }
        }
    }

    public boolean isPopoutAllowed() {
        return this.mGameData.getmVariant().equals(eVariant.Popout); // check if popout mode
    }

    // Observing changes in server

    private void observeGameState() {
        this.mGameClientCommunicationHandler.startObservingGameState(this.mGameData.getmGameName(), this::onFetchGameStateSuccess, this::onFetchGameStateFailure);
    }

    private void onFetchGameStateFailure(String errorMessage, Integer statusCode) {
        //TODO
    }

    private void onFetchGameStateSuccess(String responseString) {
        eGameState gameState = this.mGson.fromJson(responseString, eGameState.class);

        if(!gameState.equals(this.mGameState)) {
            // Game state changed.
            this.mGameState = gameState;
            this.handleGameStateChanged();
        }
    }

    private void observePlayers() {
        this.mGameClientCommunicationHandler.startObservingPlayers(this.mGameData.getmGameName(), this::onPlayersUpdate, this::onPlayersUpdateFailure);
    }

    private void onPlayersUpdateFailure(String errorMessage, Integer statusCode) {
        //TODO
    }

    private void onPlayersUpdate(String responseString) {
        TypeToken<ArrayList<Player>> token = new TypeToken<ArrayList<Player>>() {};
        ArrayList<Player> players = mGson.fromJson(responseString, token.getType());
        System.out.println("#$# received players: " + players.toString());

        this.mCachedPlayersList = players;

        this.mDelegate.updatePlayers(players);
    }

    private void observeTurnHistory() {
        this.mGameClientCommunicationHandler.startObservingTurnHistory(this.mGameData.getmGameName(), this::onTurnHistoryUpdateSuccess, this::onTurnHistoryUpdateFailure);
    }

    private void onTurnHistoryUpdateFailure(String errorMessage, Integer statusCode) {

    }

    private void onTurnHistoryUpdateSuccess(String responseString) {
        TurnHistoryResponse turnHistoryResponse = mGson.fromJson(responseString, TurnHistoryResponse.class);

        this.mGameClientCommunicationHandler.increaseTurnCounterBy(turnHistoryResponse.getmTurnHistoryDelta().size());

        if(turnHistoryResponse.getmTurnHistoryDelta().size() > 0) {
            // Log
            System.out.println("Received turns data: " + turnHistoryResponse);
            this.mPlayedTurnDataList.addAll(turnHistoryResponse.getmTurnHistoryDelta());
        }

        if(turnHistoryResponse.getmCurrentPlayerName() != null &&
                turnHistoryResponse.getmCurrentPlayerName().equals(this.mLoggedInPlayerName)) {
            this.mIsMyTurn = true;
            this.fetchAvailablePopoutColumnsIfNeeded();
            this.mDelegate.myTurnStarted();
        }

        turnHistoryResponse.getmTurnHistoryDelta().forEach(
                (turnData) -> this.handleTurnData(turnData, turnHistoryResponse.getmCurrentPlayerName())
        );
    }

    // The response is an array of PlayerAndWinningSequence instead of a map, because I was unable to parse the map object that is stored in the server.
    // This is a workaround to the described problem.
    private void onFetchedWinningSequenceMapSuccess(String responseString) {
        TypeToken<ArrayList<PlayerAndWinningSequence>> token = new TypeToken<ArrayList<PlayerAndWinningSequence>>() {};
        ArrayList<PlayerAndWinningSequence> playerAndWinningSequenceList = mGson.fromJson(responseString, token.getType());
        System.out.println("#$# received game state: " + playerAndWinningSequenceList.toString());
        Map<Player, Collection<Cell>> playerToWinningSequenceMap = this.getPlayerToWinningSequenceMap(playerAndWinningSequenceList);

        this.mDelegate.onPlayerToWinningSequenceUpdate(playerToWinningSequenceMap);
    }

    private void onFetchedWinningSequenceMapFailure(String responseString, Integer statusCode) {
        this.mDelegate.onPlayerToWinningSequenceUpdate(null);
    }

    private void fetchAvailablePopoutColumnsIfNeeded() {
        if(this.mIsPopoutMode) {
            this.mGameClientCommunicationHandler.fetchAvailablePopoutColumns(this.mGameData.getmGameName(), this.mLoggedInPlayerName, this::onFetchAvailablePopoutColumnsSuccess, this::onFetchAvailablePopoutColumnsFailure);
        }
    }

    private void onFetchAvailablePopoutColumnsSuccess(String responseString) {
        TypeToken<Collection<Integer>> token = new TypeToken<Collection<Integer>>() {};
        Collection<Integer> availablePopoutColumns = mGson.fromJson(responseString, token.getType());

        // Check if it's still the players turn.
        if(this.mIsMyTurn) {
            this.mDelegate.onAvailablePopoutColumnsUpdate(availablePopoutColumns);
        }
    }

    private void onFetchAvailablePopoutColumnsFailure(String responseString, Integer statusCode) {

    }

    // Helper functions

    private void handleTurnData(PlayedTurnData turnData, String currentPlayerName) {
        eGameState gameStateAfterTurn = turnData.getGameState();

        this.mDelegate.onTurnPlayed(turnData, currentPlayerName);

        switch (gameStateAfterTurn) {
            case Draw:
                this.onGameEnded();
                this.mDelegate.gameEnded(gameStateAfterTurn);
                break;
            case Won:
                this.onGameEnded();
                this.mGameClientCommunicationHandler.fetchPlayerToWinningSequenceMap(this.mGameData.getmGameName(), this::onFetchedWinningSequenceMapSuccess, this::onFetchedWinningSequenceMapFailure);
                this.mDelegate.gameEnded(gameStateAfterTurn);
                break;
        }
    }

    private void handleGameStateChanged() {
        System.out.println("#$# Game state changed to: " + this.mGameState.name());

        switch(this.mGameState) {
            case Inactive:
                // Game state changed from being active, to won/draw, to Inactive.
                this.initGame();
                break;
            case Active:
                // Game state changed from being Inactive to Active.
                this.onGameStarted();
                break;
        }
    }

    // This function is called either when the player first joins the game room, or when  the game is about to become active again (after being won).
    private void initGame() {
        this.mPlayedTurnDataList.clear();
        this.mDelegate.gameReset();
    }

    // The game has ended, some one won.
    private void onGameEnded() {
        this.mGameClientCommunicationHandler.stopObservingPlayedTurns();
        this.mIsMyTurn = false;
    }

    // The game has started.
    private void onGameStarted() {
        this.mGameClientCommunicationHandler.setTurnCounter(0);
        this.observeTurnHistory();
        this.mDelegate.gameStarted();
    }

    public List<Player> getPlayers() {
        return this.mCachedPlayersList;
    }

    public void resetCurrentTurnCounter() {
        this.mGameClientCommunicationHandler.setTurnCounter(0);
    }

    private Map<Player, Collection<Cell>> getPlayerToWinningSequenceMap(ArrayList<PlayerAndWinningSequence> playerAndWinningSequenceList) {
        Map<Player, Collection<Cell>> playerToWinningSequenceMap = new HashMap<>();

        for(PlayerAndWinningSequence playerAndWinningSequence: playerAndWinningSequenceList) {
            playerToWinningSequenceMap.put(playerAndWinningSequence.getmPlayer(), playerAndWinningSequence.getmWinningSequence());
        }

        return playerToWinningSequenceMap;
    }
}
