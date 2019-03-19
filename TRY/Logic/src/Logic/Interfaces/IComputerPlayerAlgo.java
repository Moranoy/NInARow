package Logic.Interfaces;

import Logic.Models.GameSettings;
import Logic.Models.PlayTurnParameters;
import Logic.Models.Board;
import Logic.Models.Player;

public interface IComputerPlayerAlgo {
    PlayTurnParameters getNextPlay(Player playingPlayer);

    void Init(Board board, GameSettings gameSettings, boolean isPopoutMode);

    boolean hasNextPlay(Player playingPlayer);
}
