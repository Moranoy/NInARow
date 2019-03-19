package NinaRowHTTPClient.Lobby;

import NinaRowHTTPClient.GeneralCommunication.CommunicationHandler;
import Tasks.PullTimerTask;
import UI.UIMisc.GameDescriptionData;

import java.util.Timer;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class LobbyClientCommunicationHandler {
    private static final int PULL_INTERVAL = 1500;
    private static final String USERNAME_PARAM = "username";
    private static final String GAME_NAME_PARAM = "gamename";
    private PullTimerTask mPullOnlinePlayersTimerTask;
    private PullTimerTask mPullGamessTimerTask;

    public void startObservingOnlinePlayerNames(Consumer<String> onReceivedOnlinePlayerNames, BiConsumer<String, Integer> onErrorObservingOnlinePlayerNames) {
        CommunicationHandler communicationHandler = new CommunicationHandler();
        communicationHandler.setPath(LobbyServerAddresses.PULL_PLAYERS_URL);
        this.mPullOnlinePlayersTimerTask = new PullTimerTask(
                () -> communicationHandler.doGet(onReceivedOnlinePlayerNames, onErrorObservingOnlinePlayerNames)
        );

        Timer timer = new Timer();

        timer.schedule(this.mPullOnlinePlayersTimerTask, 0, PULL_INTERVAL);
    }

    public void startObservingGames(Consumer<String> onReceivedUpdatedGames, BiConsumer<String, Integer> onErrorObservingGames) {
        CommunicationHandler communicationHandler = new CommunicationHandler();
        communicationHandler.setPath(LobbyServerAddresses.PULL_GAMES_URL);
        this.mPullGamessTimerTask = new PullTimerTask(
                () -> communicationHandler.doGet(onReceivedUpdatedGames, onErrorObservingGames)
        );

        Timer timer = new Timer();

        timer.schedule(this.mPullGamessTimerTask, 0, PULL_INTERVAL);
    }

    public void logout(Runnable onLogoutFinish, String userName) {
        CommunicationHandler communicationHandler = new CommunicationHandler();

        communicationHandler.setPath(LobbyServerAddresses.LOGOUT_URL);
        communicationHandler.addParameter(USERNAME_PARAM, userName);
        communicationHandler.doGet(onLogoutFinish);
    }

    public void joinGameWithData(String userName, GameDescriptionData gameDescriptionData, Consumer<String> onJoinGameSuccess, BiConsumer<String, Integer> onJoinGameFailure) {
        CommunicationHandler communicationHandler = new CommunicationHandler();

        communicationHandler.setPath(LobbyServerAddresses.JOIN_GAME_URL);
        communicationHandler.addParameter(USERNAME_PARAM, userName);
        communicationHandler.addParameter(GAME_NAME_PARAM, gameDescriptionData.getmGameName());
        communicationHandler.doGet(onJoinGameSuccess, onJoinGameFailure);
    }

    public void stopTimerTasks() {
        this.mPullOnlinePlayersTimerTask.cancel();
        this.mPullGamessTimerTask.cancel();
    }

    private static class LobbyServerAddresses {
        private static final String JOIN_GAME_URL = "javafx/joingame";
        private static final String PULL_PLAYERS_URL = "userslist";
        private static final String PULL_GAMES_URL = "gamesdata";
        private static final String LOGOUT_URL = "javafx/logout";
    }
}
