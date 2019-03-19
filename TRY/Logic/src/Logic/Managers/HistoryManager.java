package Logic.Managers;

import Logic.Models.PlayedTurnData;

import java.util.ArrayList;
import java.util.List;

public class HistoryManager {
    // hold a collection of PlayerTurns
    private List<PlayedTurnData> mPlayedTurnData = new ArrayList<>();

    public List<PlayedTurnData> GetGameHistory(){
        return mPlayedTurnData;
    }

    public void SetCurrentTurn(PlayedTurnData playedTurnData) {
        mPlayedTurnData.add(playedTurnData); //Add new turn to history collection
    }

    public void Clear() {
        mPlayedTurnData.clear();
    }
}
