package Logic.SequenceSearchers;

import Logic.Enums.eSequenceSearcherType;
import Logic.Interfaces.ISequenceSearcherStrategy;
import Logic.SequenceSearchers.Strategy.*;

import java.util.HashMap;
import java.util.Map;

import Logic.Enums.eSequenceSearcherType;
import Logic.Enums.eVariant;
import Logic.Interfaces.ISequenceSearcherStrategy;
import Logic.Models.GameSettings;
import Logic.SequenceSearchers.Strategy.*;

import java.util.HashMap;
import java.util.Map;

public class SequenceSearcherStrategyFactory {

    public static ISequenceSearcherStrategy getSequenceSearcherStrategyForType(eSequenceSearcherType type, GameSettings gameSettings) {
        return createSequenceSearcherForType(type, gameSettings);
    }

    private static ISequenceSearcherStrategy createSequenceSearcherForType(eSequenceSearcherType type, GameSettings gameSettings) {
        boolean isCircularGameMode = gameSettings.getVariant() == eVariant.Circular;
        ISequenceSearcherStrategy sequenceSearcherStrategy;

        switch(type) {
            case Top:
                sequenceSearcherStrategy = new TopSequenceSearcherStrategy();
                break;
            case Bottom:
                sequenceSearcherStrategy = new BottomSequenceSearcherStrategy(gameSettings.getRows());
                break;
            case Right:
                sequenceSearcherStrategy = new RightSequenceSearcherStrategy(gameSettings.getColumns());
                break;
            case Left:
                sequenceSearcherStrategy = new LeftSequenceSearcherStrategy();
                break;
            case TopRight:
                sequenceSearcherStrategy = new TopRightSequenceSearcherStrategy(gameSettings.getColumns());
                break;
            case TopLeft:
                sequenceSearcherStrategy = new TopLeftSequenceSearcherStrategy();
                break;
            case BottomRight:
                sequenceSearcherStrategy = new BotRightSequenceSearcherStrategy(gameSettings.getRows(), gameSettings.getColumns());
                break;
            case BottomLeft:
                sequenceSearcherStrategy = new BotLeftSequenceSearcherStrategy(gameSettings.getRows());
                break;
            default:
                sequenceSearcherStrategy = null;
        }

        // If needed, wrap strategy with a circular strategy.
        if(isCircularGameMode && doesStrategySupportCircularGameMode(type)) {
            ISequenceSearcherStrategy innerStrategy = sequenceSearcherStrategy;
            sequenceSearcherStrategy = new CircularSequenceSearcherStrategy(innerStrategy, gameSettings.getRows(),
                    gameSettings.getColumns());
        }

        return sequenceSearcherStrategy;
    }

    private static boolean doesStrategySupportCircularGameMode(eSequenceSearcherType type) {
        return type == eSequenceSearcherType.Bottom || type == eSequenceSearcherType.Top
                || type == eSequenceSearcherType.Right || type == eSequenceSearcherType.Left;
    }
}