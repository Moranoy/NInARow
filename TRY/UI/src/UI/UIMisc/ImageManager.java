package UI.UIMisc;

import javafx.scene.image.Image;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ImageManager {
    private static Map<String, Image> sPlayerIDToImagePathMap = new HashMap<>();
    private final static String[] sfImagePaths = {
            "/UI/Images/1.JPG",
            "/UI/Images/2.JPG",
            "/UI/Images/3.JPG",
            "/UI/Images/4.JPG",
            "/UI/Images/5.JPG",
            "/UI/Images/6.JPG"
    };
    private static final Image sfEmptyDiscSlotImage = new Image("/UI/Images/EmptyCell.JPG");


    public static void SetImagesForPlayerIDs(Collection<String> playerIDCollection) {
        int pathIndex = 0;

        for(String playerID: playerIDCollection) {
            sPlayerIDToImagePathMap.put(playerID, new Image(sfImagePaths[pathIndex++])); // Map between each player's ID to an image.
        }
    }

    public static Image getImageForPlayerID(String playerID) {
        return sPlayerIDToImagePathMap.get(playerID);
    }

    public static void Clear() {
        sPlayerIDToImagePathMap.clear();
    }

    public static Image getEmptyDiscSlotImage() {
        return sfEmptyDiscSlotImage;
    }
}
