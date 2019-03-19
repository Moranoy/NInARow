package ChatLogicEngine.users;

import Logic.Enums.ePlayerType;
import Logic.Models.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class PlayerManager {
    private final Set<Player> mPlayerSet;

    public PlayerManager() {
        mPlayerSet = new HashSet<>();
    }

    public synchronized void addPlayer(String username, boolean isComputer) {
        Player newPlayer = new Player();

        newPlayer.setName(username);
        newPlayer.setType(isComputer ? ePlayerType.Computer : ePlayerType.Human);

        mPlayerSet.add(newPlayer);
    }

    public synchronized void removePlayerWithName(String username) {
        Player removedPlayer = null;
       for(Player player: this.mPlayerSet) {
           if(player.getName().equals(username)) {
               removedPlayer = player;
               break;
           }
       }

        mPlayerSet.remove(removedPlayer);
    }

    public synchronized Set<String> getAllPlayerNames() {
        Set<String> playerNames = this.mPlayerSet.stream().map(Player::getName).collect(Collectors.toSet());
        return Collections.unmodifiableSet(playerNames);
    }

    public synchronized Set<Player> getAllPlayers() {
        return Collections.unmodifiableSet(this.mPlayerSet);
    }

    public boolean isUserExists(String username) {
        boolean isUserExist = false;

        for(Player player: this.mPlayerSet) {
            if(player.getName().equals(username)) {
                isUserExist = true;
                break;
            }
        }

        return isUserExist;
    }

    public synchronized Player getPlayerForName(String userName) {
        Player chosenPlayer = null;

        for(Player player: this.mPlayerSet) {
            if(player.getName().equals(userName)) {
                chosenPlayer  = player;
                break;
            }
        }
        return chosenPlayer;
    }
}
