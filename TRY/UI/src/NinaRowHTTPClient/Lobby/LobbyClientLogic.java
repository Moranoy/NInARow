package NinaRowHTTPClient.Lobby;

import UI.UIMisc.GameDescriptionData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class LobbyClientLogic {
    private Gson mGson = new Gson();
    private ILobbyClientLogicDelegate mDelegate;
    private LobbyClientCommunicationHandler mLobbyCommunicationHandler;

    public LobbyClientLogic(ILobbyClientLogicDelegate delegate) {
        this.mDelegate = delegate;
        this.mLobbyCommunicationHandler = new LobbyClientCommunicationHandler();
    }

    public void observeOnlinePlayerNames() {
        this.mLobbyCommunicationHandler.startObservingOnlinePlayerNames(this::onReceivedOnlinePlayerNames, this::onErrorObservingOnlinePlayerNames);
    }

    public void observeGames() {
        this.mLobbyCommunicationHandler.startObservingGames(this::onReceivedUpdatedGames, this::onErrorObservingGames);
    }

    public void logout(String username) {
        this.mLobbyCommunicationHandler.logout(this::onLogoutFinish, username);
    }

    public void joinGameWithData(String userName, GameDescriptionData gameDescriptionData) {
        this.mLobbyCommunicationHandler.joinGameWithData(userName, gameDescriptionData, this::onJoinGameSuccess, this::onJoinGameFailure);
    }

    private void onReceivedOnlinePlayerNames(String responseString) {
        TypeToken<ArrayList<String>> token = new TypeToken<ArrayList<String>>() {};
        ArrayList<String> playerNames = mGson.fromJson(responseString, token.getType());

        this.mDelegate.onPlayerNamesUpdate(playerNames);
    }

    private void onErrorObservingOnlinePlayerNames(String errorMessage, Integer statusCode) {
        this.mDelegate.onErrorUpdatingPlayerNames(errorMessage);
    }

    private void onReceivedUpdatedGames(String responseString) {
        TypeToken<List<GameDescriptionData>> token = new TypeToken<List<GameDescriptionData>>() {};
        List<GameDescriptionData> updatedGamesList = mGson.fromJson(responseString, token.getType());

        this.mDelegate.onGamesUpdate(updatedGamesList);
    }

    private void onErrorObservingGames(String errorMessage, Integer statusCode) {
        this.mDelegate.onErrorUpdatingGames(errorMessage);
    }

    private void onLogoutFinish() {
        this.mLobbyCommunicationHandler.stopTimerTasks();
        this.mDelegate.onLogoutFinish();
    }

    private void onJoinGameSuccess(String responseString) {
        this.mLobbyCommunicationHandler.stopTimerTasks();

        GameDescriptionData gameDescriptionData = mGson.fromJson(responseString, GameDescriptionData.class);

        this.mDelegate.onJoinGameSuccess(gameDescriptionData);
    }

    private void onJoinGameFailure(String errorMessage, Integer statusCode) {
        this.mDelegate.onJoinGameFailure(errorMessage);
    }
}
