package NinaRowHTTPClient.Game;

import NinaRowHTTPClient.GeneralCommunication.CommunicationHandler;
import NinaRowHTTPClient.Lobby.LobbyClientCommunicationHandler;
import Tasks.PullTimerTask;

import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class GameClientsCommunicationHandler {

    private static final int PULL_INTERVAL = 1500;
    private static final String GAME_NAME_PARAM = "gamename";
    private static final String TURN_NUMBER_PARAM = "turnnumber";
    private static final String USER_NAME_PARAM = "username";


    private PullTimerTask mPullPlayersTimerTask;
    private PullTimerTask mPullGameStateTimerTask;
    private PullTimerTask mPullTurnHistoryDelta;
    private AtomicInteger mAtomicCurrentTurnCounter = new AtomicInteger();

    public void startObservingPlayers(String gameName, Consumer<String> onFetchedPlayersSuccess, BiConsumer<String, Integer> onFetchedPlayersFailure) {
        CommunicationHandler communicationHandler = new CommunicationHandler();
        communicationHandler.setPath(GameClientsCommunicationHandler.GameServerAddresses.PULL_PLAYERS_URL);
        communicationHandler.addParameter(GAME_NAME_PARAM, gameName);
        this.mPullPlayersTimerTask = new PullTimerTask(
                () -> communicationHandler.doGet(onFetchedPlayersSuccess, onFetchedPlayersFailure)
        );

        Timer timer = new Timer();

        timer.schedule(this.mPullPlayersTimerTask, 0, PULL_INTERVAL);
    }

    public void startObservingGameState(String gameName, Consumer<String> onFetchedGameStateSuccess, BiConsumer<String, Integer> onFetchedGameStateFailure) {
        CommunicationHandler communicationHandler = new CommunicationHandler();
        communicationHandler.setPath(GameClientsCommunicationHandler.GameServerAddresses.PULL_GAME_STATE_URL);
        communicationHandler.addParameter(GAME_NAME_PARAM, gameName);
        this.mPullGameStateTimerTask = new PullTimerTask(
                () -> communicationHandler.doGet(onFetchedGameStateSuccess, onFetchedGameStateFailure)
        );

        Timer timer = new Timer();

        timer.schedule(this.mPullGameStateTimerTask, 0, PULL_INTERVAL);
    }

    public void playTurn(String gameName, String body, Consumer<String> onPlayTurnSuccess, BiConsumer<String, Integer> onPlayTurnFailure) {
        CommunicationHandler communicationHandler = new CommunicationHandler();
        communicationHandler.setPath(GameClientsCommunicationHandler.GameServerAddresses.PLAY_TURN_URL);
        communicationHandler.addParameter(GAME_NAME_PARAM, gameName);
        communicationHandler.addBody(body);

        // Send empty success block. Only need to be notified of failure.
        communicationHandler.doGet(onPlayTurnSuccess, onPlayTurnFailure);
    }

    public void startObservingTurnHistory(String gameName, Consumer<String> onTurnHistoryUpdateSuccess, BiConsumer<String, Integer> onTurnHistoryUpdateFailure) {
        CommunicationHandler communicationHandler = new CommunicationHandler();
        communicationHandler.setPath(GameClientsCommunicationHandler.GameServerAddresses.PULL_TURN_HISTORY_DELTA);
        communicationHandler.addParameter(GAME_NAME_PARAM, gameName);

        this.mPullTurnHistoryDelta = new PullTimerTask(
                () -> {
                    communicationHandler.addParameter(TURN_NUMBER_PARAM, Integer.toString(this.mAtomicCurrentTurnCounter.get()));
                    communicationHandler.doGet(onTurnHistoryUpdateSuccess, onTurnHistoryUpdateFailure);
                }
        );

        Timer timer = new Timer();

        timer.schedule(this.mPullTurnHistoryDelta, 0, PULL_INTERVAL);
    }

    public void setTurnCounter(int turnCounter) {
        this.mAtomicCurrentTurnCounter.set(turnCounter);
    }

    public void increaseTurnCounterBy(int turns) {
        this.mAtomicCurrentTurnCounter.set(this.mAtomicCurrentTurnCounter.get() + turns);
    }

    // Fetches

    public void fetchPlayerToWinningSequenceMap(String gameName, Consumer<String> onFetchedWinningSequenceMapSuccess, BiConsumer<String, Integer> onFetchedWinningSequenceMapFailure) {
        CommunicationHandler communicationHandler = new CommunicationHandler();
        communicationHandler.setPath(GameServerAddresses.WINNING_SEQUENCE_URL);
        communicationHandler.addParameter(GAME_NAME_PARAM, gameName);

        communicationHandler.doGet(onFetchedWinningSequenceMapSuccess, onFetchedWinningSequenceMapFailure);
    }

    public void fetchAvailablePopoutColumns(String gameName, String mLoggedInPlayerName, Consumer<String> onFetchAvailablePopoutColumnsSuccess, BiConsumer<String, Integer> onFetchAvailablePopoutColumnsFailure) {
        CommunicationHandler communicationHandler = new CommunicationHandler();
        communicationHandler.setPath(GameServerAddresses.POPOUT_COLUMNS_URL);
        communicationHandler.addParameter(GAME_NAME_PARAM, gameName);
        communicationHandler.addParameter(USER_NAME_PARAM, mLoggedInPlayerName);

        communicationHandler.doGet(onFetchAvailablePopoutColumnsSuccess, onFetchAvailablePopoutColumnsFailure);
    }

    public void stopObserving() {
        this.mPullTurnHistoryDelta.cancel();
        this.mPullGameStateTimerTask.cancel();
        this.mPullGameStateTimerTask.cancel();
    }

    public void stopObservingPlayedTurns() {
        this.mPullTurnHistoryDelta.cancel();
    }

    private static class GameServerAddresses {
        private static final String PLAY_TURN_URL = "playturn";
        private static final String PULL_PLAYERS_URL = "playerslist";
        private static final String PULL_GAME_STATE_URL = "gamestate";
        private static final String PULL_TURN_HISTORY_DELTA = "turnhistory";
        private static final String WINNING_SEQUENCE_URL = "winningsequence";
        private static final String POPOUT_COLUMNS_URL = "popoutcolumns";
    }
}
