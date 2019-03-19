package NinaRowHTTPClient.Game.ResponseModels;

import Logic.Models.PlayedTurnData;

import java.util.List;

public class TurnHistoryResponse {
    private List<PlayedTurnData> mTurnHistoryDelta;
    private String mCurrentPlayerName;

    public List<PlayedTurnData> getmTurnHistoryDelta() {
        return mTurnHistoryDelta;
    }

    public String getmCurrentPlayerName() {
        return mCurrentPlayerName;
    }

    @Override
    public String toString() {
        return "TurnHistoryResponse{" +
                "mTurnHistoryDelta=" + mTurnHistoryDelta +
                ", mCurrentPlayerName='" + mCurrentPlayerName + '\'' +
                '}';
    }
}
