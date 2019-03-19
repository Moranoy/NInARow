package Logic.Interfaces;

import Logic.Enums.eGameState;
import Logic.Exceptions.InvalidFileInputException;
import Logic.Exceptions.InvalidUserInputException;
import Logic.Models.Board;
import Logic.Models.PlayTurnParameters;
import Logic.Models.Player;
import Logic.Models.PlayedTurnData;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface ILogic {

    public void ReadGameFile(String filePath) throws FileNotFoundException, InvalidFileInputException, IOException, JAXBException;
    public void StartGame();
    public List<PlayedTurnData> PlayTurn(PlayTurnParameters playTurnParameters) throws InvalidUserInputException, Exception;
    public List<PlayedTurnData> GetTurnHistory();
    public Player GetCurrentPlayer();
    public Board getBoard();
    public void SaveGame() throws  IOException, ClassNotFoundException, Exception;
    //public void LoadExistsGame() throws IOException, ClassNotFoundException, Exception;
    public eGameState GetGameState();

}
