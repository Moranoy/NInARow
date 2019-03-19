package Logic.Managers;

import Logic.Models.GameSettings;
import Logic.Models.PlayedTurnData;

import java.io.*;
import java.util.List;


public class HistoryFileManager {

    //Convert Object to XML
    public static void SaveGameHistoryInXMLFile(String path, List<PlayedTurnData> currentGameHistory) throws IOException, ClassNotFoundException, Exception {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
            //out.writeObject(GameSettings.getInstance()); //save gameSettings
            out.writeObject(currentGameHistory); //save history turns
            out.flush();
        }
    }

    //Convert XML to Object
    public static List<PlayedTurnData> ReadGameHistoryFromXMLFile(String path) throws IOException, ClassNotFoundException {
        List<PlayedTurnData> turnHistory = null;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(path))) {
            //GameSettings.LoadNewInstance( (GameSettings) in.readObject()); //load gameSettings
            turnHistory = (List<PlayedTurnData>) in.readObject(); //load history turns
        }

        return turnHistory;
    }
}