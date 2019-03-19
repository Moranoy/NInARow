var pullTimer = 1500;

var currentGameData;
var currentPlayerName;
var loggedInPlayerName;
var gameState = "Inactive";
var gameName;
var isMyTurn = false;
var turnNumber = 0;

$(function() {
    init();
    fetchLoggedInPlayerName();
    getGameData();
});

function init() {
    $('#leaveGameBtn').click(onLeaveGame);
}

function startPullingIntervals() {
    gameName = currentGameData.mGameName.replace(' ', '+');
    window.setInterval(pullGameState, pullTimer);
    window.setInterval(pullPlayersData, pullTimer);
    window.setInterval(pullTurnsDelta, pullTimer);
}

function initUI(gameData) {
    initGameDetailsUI(gameData);
    initBoard(gameData);
}

function onColumnClick(column) {
    var playTurnParams = new PlayTurnParameters(ADD_DISC, column, loggedInPlayerName);
    playTurnAsync(playTurnParams);
}

function onPopoutButtonClick(column) {
    console.log("Popout clicked! column: " + column);
    var playTurnParams = new PlayTurnParameters(POPOUT, column, loggedInPlayerName);
    playTurnAsync(playTurnParams);
}


function onFetchedPlayedTurnsData(historyData) {
    var historyDataObject = JSON.parse(historyData);
    currentPlayerName = historyDataObject.mCurrentPlayerName;
    checkIfMyTurn();

    if(historyDataObject.mTurnHistoryDelta !== undefined && historyDataObject.mTurnHistoryDelta.length > 0) {
        turnNumber += historyDataObject.mTurnHistoryDelta.length;
        $.each(historyDataObject.mTurnHistoryDelta, handlePlayedTurnData);
    }
}

function checkIfMyTurn() {
    isMyTurn = currentPlayerName === loggedInPlayerName;

    if(isMyTurn && isPopoutMode()) {
        //fetchAvailablePopoutColumnsIfNeeded(); //TODO in comm
    }
}

function handlePlayedTurnData(index, playedTurnData) {
    updateBoardWithNewPlay(playedTurnData.mUpdatedCellsCollection);
    var newGameState = playedTurnData.mGameStateAfterTurn;

    if(newGameState !== gameState) {
        gameState = newGameState;
        handleGameStateChanged(gameState)
    }
}

// For now, not handling winning sequences.
function handlePlayerAndWinningSequence(index, playerAndWinningSequence) {
    var player = playerAndWinningSequence.mPlayer;
    var playerName = player.mName;

    appendNotification(" -" + playerName + "- ");
}

// Helper functions

function isGameActive() {
    return gameState === "Active";
}

function isPopoutMode() {
    return currentGameData.mVariant === POPOUT;
}

function onLeaveGame() {
    console.log("Leave game clicked. Is game active: " + isGameActive());
    if(isGameActive()) {
        // Quit game in players turn.
        var playTurnParams = new PlayTurnParameters(PLAYER_QUIT, null, loggedInPlayerName);
        playTurnAsync(playTurnParams);

        if(!isMyTurn) {
            // Game is active and it is not the player's turn. Do not follow through with sending the get request from the leave game button default on click.
            return false;
        }
    } else {
        // Quit game before/after game.
        quitGame();
    }
}

function writeError(message) {
    setLabel(message, "red");
}

function writeNotification(message) {
    setLabel(message, "white");
}

function appendNotification(message) {
    $('#error-message-label').append(message);
}

function setLabel(text, color) {
    var label = $('#error-message-label');

    label.empty();
    label.append(text).css('color', color);
}

function clearLabel() {
    var label = $('#error-message-label');

    label.empty();
}