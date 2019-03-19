package MultiGamesLogic;

import Logic.Enums.eGameState;
import Logic.Exceptions.InvalidFileInputException;
import Logic.Exceptions.InvalidInputException;
import Logic.Game;
import Logic.Managers.FileManager;
import Logic.Models.*;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class GamesManager {
    private Map<String, Game> mGameNameToGame = new ConcurrentHashMap<>(); // ConcurrentHashMap for thread safety
    private FileManager mFileManager = new FileManager();

    // User actions.

    public synchronized void addGame(InputStream gameContentStream, String uploaderName) throws InterruptedException, IOException, JAXBException, InvalidFileInputException {
        GameSettings gameSettingsFromFile;

        gameSettingsFromFile = this.mFileManager.LoadGameFile(gameContentStream);
        gameSettingsFromFile.setUploaderName(uploaderName);

        if(!gameSettingsFromFile.getmGameName().contains("+")) { // Game name does not contain seporator - valid name.
            if (!this.isGameNameAlreadyExist(gameSettingsFromFile.getmGameName())) {
                Game newGame = new Game(gameSettingsFromFile);
                this.mGameNameToGame.put(gameSettingsFromFile.getmGameName(), newGame);
            } else {
                throw new InvalidFileInputException("Invalid game file! Game name is already exists.");
            }
        } else {
            throw new InvalidFileInputException("Invalid game name! Game name must not contain '+'."); // Game contains seporator - invalid name.
        }
    }

    private boolean isGameNameAlreadyExist(String gameName) {
        return this.mGameNameToGame.keySet().stream().anyMatch(name -> name.equals(gameName));
    }

    public void addUserToGame(String gameName, Player joiningPlayer) throws Exception {
        Game game = this.mGameNameToGame.get(gameName);

        synchronized (game.getmGameLock()) {
            game.addPlayer(joiningPlayer);

            if(game.shouldStartGame()) {
                game.StartGame();
            }
        }
    }

    public void userLeftGame(String gameName, String playerName) {
        // get relevant game from map.

        // remove player from game.
    }

    public void playTurn(String gameName, PlayTurnParameters params, Player playingPlayer) throws InvalidInputException {
        Game game = this.mGameNameToGame.get(gameName);

        if(game.doesContainPlayerWithName(params.getmPlayerName())) {
            synchronized (game.getmGameLock()) {
                if(game.isGameActive()) {
                    // Player quits in the middle of an active game
                    game.playTurn(params);
                } else {
                    game.playerQuitWhileGameIsNotActive(playingPlayer);
                }
            }
        } else {
            throw new InvalidInputException("Player " + params.getmPlayerName() + " is not playing in game " + gameName);
        }
    }


    public GameDescriptionData getGameDescriptionForGameName(String gameName) {
        Game game = this.mGameNameToGame.get(gameName);
        GameDescriptionData gameData;

        synchronized (game.getmGameLock()) {
            gameData = game.getGameDescriptionData();
        }

        return gameData;
    }

    // Pull

    public List<GameDescriptionData> getAllGamesInfo() {
        List<GameDescriptionData> gameDescriptionDataList = new ArrayList<>();

        // Map is thread safe. No need to worry about concurrency issues.
        this.mGameNameToGame.values().forEach(
                (game) ->  {
                    synchronized (game.getmGameLock()) {
                        gameDescriptionDataList.add(game.getGameDescriptionData());
                    }
                }
        );

        return gameDescriptionDataList;
    }

    public List<PlayedTurnData> getTurnHistoryForGame(String gameName, int currentTurnForClient) {
        Game game = this.mGameNameToGame.get(gameName);
        List<PlayedTurnData> gameHistory;

        synchronized (game.getmGameLock()) {
            gameHistory = game.getTurnHistory().subList(currentTurnForClient, game.getTurnHistory().size());
        }

        return gameHistory;
    }

    public Collection<Player> getPlayersForGame(String gameName) {
        Game game = this.mGameNameToGame.get(gameName);
        Collection<Player> players;

        synchronized (game.getmGameLock()) {
            players = game.getPlayers();
        }

        return players;
    }

    public eGameState getGameState(String gameName) {
        Game game = this.mGameNameToGame.get(gameName);
        eGameState gameState;

        synchronized (game.getmGameLock()) {
            gameState = game.GetGameState();
        }

        return gameState;
    }

    public String getGameNameForPlayerName(String username) {
        String gameName = null;

        // Map is thread safe. No need to worry about concurrency issues.
       for(Game game: this.mGameNameToGame.values()) {
           synchronized (game.getmGameLock()) {
               if(game.doesContainPlayerWithName(username)) {
                   gameName = game.getGameDescriptionData().getmGameName();
                   break;
               }
           }
       }

        return gameName;
    }

    public String getCurrentPlayerName(String gameName) {
        Game game = this.mGameNameToGame.get(gameName);
        String currentPlayerName;

        synchronized (game.getmGameLock()) {
            currentPlayerName = game.getCurrentPlayerName();
        }

        return currentPlayerName;
    }

    public Map<Player, Collection<Cell>> getPlayerToWinningSequenceMap(String gameName) {
        Game game = this.mGameNameToGame.get(gameName);
        Map<Player, Collection<Cell>> playerToWinningSequenceMap;

        synchronized (game.getmGameLock()) {
            playerToWinningSequenceMap = game.getPlayerToWinningSequencesMap();
        }

        return playerToWinningSequenceMap;
    }

    public Collection<Integer> getAailablePopoutColumnsForPlayer(String gameName, Player player) {
        Game game = this.mGameNameToGame.get(gameName);
        Collection<Integer> availablePopoutColumns;

        synchronized (game.getmGameLock()) {
            availablePopoutColumns = game.getAvailablePopoutColumnsForPlayer(player);
        }

        return availablePopoutColumns;
    }
}
