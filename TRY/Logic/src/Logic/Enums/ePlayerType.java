package Logic.Enums;

import java.io.Serializable;

public enum ePlayerType implements Serializable {
    Human,
    Computer;

    public static ePlayerType ConvertStringToePlayerType(String playerTypeStr) {
        if (Human.name().equals(playerTypeStr)){
            return Human;
        }
        else if(Computer.name().equals(playerTypeStr)){
            return Computer;
        }
        else{
            return null;
        }
    }
}
