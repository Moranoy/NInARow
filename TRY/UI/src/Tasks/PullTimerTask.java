package Tasks;

import java.util.TimerTask;

public class PullTimerTask extends TimerTask {

    private Runnable mTask;

    public PullTimerTask(Runnable task) {
        this.mTask = task;
    }

    @Override
    public void run() {
        this.mTask.run();
    }
}
