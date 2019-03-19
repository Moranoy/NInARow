package NinaRowHTTPClient.Lobby;

import UI.UIMisc.GameDescriptionData;

import java.util.List;

public interface ILobbyClientLogicDelegate {

    void onPlayerNamesUpdate(List<String> playerNames);

    void onErrorUpdatingPlayerNames(String errorMessage);

    void onGamesUpdate(List<GameDescriptionData> updatedGamesList);

    void onErrorUpdatingGames(String errorMessage);

    void onLogoutFinish();

    void onJoinGameSuccess(GameDescriptionData gameDescriptionData);

    void onJoinGameFailure(String errorMessage);
}
