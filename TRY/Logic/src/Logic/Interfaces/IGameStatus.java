package Logic.Interfaces;

import Logic.Enums.eGameState;

import java.time.Duration;

public interface IGameStatus {
    String getNameOfPlayerCurrentlyPlaying();

    Duration getGameDuration();

    eGameState getGameState();

    void clear();

    void setGameState(eGameState gameState);
}
