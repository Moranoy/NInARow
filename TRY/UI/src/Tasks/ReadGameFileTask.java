package Tasks;

import Logic.Exceptions.InvalidFileInputException;
import javafx.concurrent.Task;
import Logic.Game;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.function.Consumer;

public class ReadGameFileTask extends Task<Void> {

    private String mAbsoluteGameFilePath;
    private Runnable mOnFinish;
    private Consumer<String> mOnErrorCallback;
    private Game mGame;

    public ReadGameFileTask(String absoluteGameFilePath, Game game, Runnable onFinish, Consumer<String> onErrorCallback) {
        this.mAbsoluteGameFilePath = absoluteGameFilePath;
        this.mGame = game;
        this.mOnFinish = onFinish;
        this.mOnErrorCallback = onErrorCallback;
    }

    @Override
    protected Void call() throws Exception {
        // Open file.
        this.updateMessage("Preparing to load file...");
        this.updateProgress(0, 100);
        Thread.sleep(300);

        // Set callbacks
        Runnable onLoadFileFinish = () -> {
            // Check if file is valid.
            this.updateProgress(33, 100);
            this.updateMessage("Checking file content validation...");
        };

        Runnable onFinishedCheckingFileValidity = () -> {
            // Apply file content to game settings.
            this.updateMessage("Reading from file...");
            this.updateProgress(66, 100);
        };

        boolean didErrorOccur = true;
        String errorDescription = null;

//        try {
//            this.mGame.ReadGameFile(mAbsoluteGameFilePath, onLoadFileFinish, onFinishedCheckingFileValidity);
//            didErrorOccur = false; // Only reach this line if error did not occur during file reading.
//        } catch(FileNotFoundException e) {
//            errorDescription = e.getMessage();
//        } catch(InvalidFileInputException e) {
//            errorDescription = e.getMessage();
//        } catch(JAXBException e) {
//            errorDescription = "File is in the wrong xml format!";
//        } catch(IOException e) {
//            errorDescription = "Error reading from file.";
//        } catch (Exception e) {
//            errorDescription = "A general error has occurred";
//        }

        if(didErrorOccur) {
            this.updateMessage("");
            this.updateProgress(0, 100);
            this.mOnErrorCallback.accept(errorDescription);
        } else {
            // JAXBException,
            this.updateMessage("Finished reading from file :)");
            this.updateProgress(100, 100);
            Thread.sleep(300);

            this.mOnFinish.run();
        }


        return null;
    }
}