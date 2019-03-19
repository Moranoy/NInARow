package NinaRowHTTPClient.Game;

import Logic.Enums.eGameState;
import Logic.Models.Cell;
import Logic.Models.PlayedTurnData;
import Logic.Models.Player;

import java.util.Collection;
import java.util.List;
import java.util.Map;

// TODO: seporate methods to different interfaces by functionality. Do this to avoid "big fat interface"
public interface IGameClientLogicDelegate {

    void myTurnStarted();

    void myTurnFinished();

    void gameStarted();

    void updatePlayers(List<Player> playerList);

    void onTurnPlayed(PlayedTurnData playedTurnData, String currentPlayerName);

    void discAddedToFullColumn(int column);

    void currentPlayerCannotPopoutAtColumn(int column);

    void gameEnded(eGameState gameEndedState);

    void onPlayerToWinningSequenceUpdate(Map<Player, Collection<Cell>> playerToWinningSequenceMap);

    void onAvailablePopoutColumnsUpdate(Collection<Integer> availablePopoutColumns);

    void gameReset();
}
