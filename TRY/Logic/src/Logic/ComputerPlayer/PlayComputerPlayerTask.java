package Logic.ComputerPlayer;

import javafx.concurrent.Task;

public class PlayComputerPlayerTask extends Task<Void> {

    private Runnable mPlayAllComputerPlayersTurns;

    public PlayComputerPlayerTask(Runnable onFinishedPlayingAllComputerPlayers) {
        this.mPlayAllComputerPlayersTurns = onFinishedPlayingAllComputerPlayers;
    }

    @Override
    protected Void call() {

        this.mPlayAllComputerPlayersTurns.run();

        return null;
    }
}
