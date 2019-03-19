var GAME_DATA_URL = buildUrlWithContextPath("gamedata");
var GAME_STATE_URL = buildUrlWithContextPath("gamestate");
var FETCH_USER_NAME_URL = buildUrlWithContextPath("getname");
var PLAY_TURN_URL = buildUrlWithContextPath("playturn");
var PLAYER_LIST_URL = buildUrlWithContextPath("playerslist");
var TURN_HISTORY_URL = buildUrlWithContextPath("turnhistory");
var WINNING_SEQUENCE_URL = buildUrlWithContextPath("winningsequence");


function fetchLoggedInPlayerName() {
    $.ajax({
        url: FETCH_USER_NAME_URL,
        timeout: 2000,
        error: function(e) {
            console.error("Failed to send ajax");
        },
        success: function(username) {
            loggedInPlayerName = username;
        }
    });
}

function getGameData() {
    $.ajax({
        url: GAME_DATA_URL,
        timeout: 2000,
        error: function(e) {
            console.error("Failed to send ajax");
        },
        success: onFetchedGameData
    });
}

// gameDataJson = { mLoggedInPlayerName = "", mGameDescriptionData = { mGameName = "", mGameState = "", mCurrentNumberOfPlayers = 0, mMaxPlayers = 4, mRows = 7, mColumns = 8, mTarget = 4, mUploaderName = "" }}
function onFetchedGameData(gameDataJson) {
    loggedInPlayerName = gameDataJson.mLoggedInPlayerName;
    currentGameData = gameDataJson.mGameDescriptionData;
    startPullingIntervals();

    initUI(currentGameData);
}

function pullGameState() {
    $.ajax({
        url: GAME_STATE_URL,
        data: { "gamename": gameName },
        timeout: 2000,
        error: function(e) {
            console.error("Failed to send ajax");
        },
        success: function(newGameState) {
            if(newGameState !== gameState) {
                gameState = newGameState;
                console.log("Game state changed to " + newGameState);
                handleGameStateChanged(gameState);
            }
        }
    });
}

function pullTurnsDelta() {

    $.ajax({
        url: TURN_HISTORY_URL,
        data: { "gamename": gameName, "turnnumber": turnNumber.toString() },
        timeout: 2000,
        error: function(e) {
            console.error("Failed to send ajax");
        },
        success: function(playedTurnsData) {
            console.log("Fetched played turn data: " + playedTurnsData);
            onFetchedPlayedTurnsData(playedTurnsData);
        }
    });
}

function pullPlayersData() {
    $.ajax({
        url: PLAYER_LIST_URL,
        data: { "gamename": gameName },
        timeout: 2000,
        error: function(e) {
            console.error("Failed to send ajax");
        },
        success: function(playersArray) {
            console.log("Fetched players: " + playersArray);

            if(!isPlayerColorsSet && isGameActive()) {
                initPlayerColors(playersArray);
            }

            $('#player-details-tbody').children().remove();
            $.each(playersArray || [], addPlayerData);
        }
    });
}

function fetchPlayerToWinningSequenceMap() {
    $.ajax({
        url: WINNING_SEQUENCE_URL,
        data: { "gamename": gameName },
        timeout: 2000,
        error: function(e) {
            console.error("Failed to send ajax");
        },
        success: function(playerAndWinningSequenceResponse) {
            console.log("Fetched players and winning sequences");
            writeNotification("Game was won by:");
            var playerAndWinningSequenceArray = JSON.parse(playerAndWinningSequenceResponse);
            $.each(playerAndWinningSequenceArray || [], handlePlayerAndWinningSequence);
        }
    });
}


// User events from board

//Turn types
var ADD_DISC = "AddDisc";
var POPOUT = "Popout";
var PLAYER_QUIT = "PlayerQuit";

function PlayTurnParameters(turnType, column, playerName) {
    this.mTurnType = turnType;
    this.mColumn = column;
    this.mPlayerName = playerName;
}

function sendTurnToServer(playTurnParams) {
    console.log("Sending turn to server " + playTurnParams);

    $.ajax({
        url: PLAY_TURN_URL,
        data: {
            "gamename": gameName,
            "turnType": playTurnParams.mTurnType,
            "column": playTurnParams.mColumn,
            "playerName": playTurnParams.mPlayerName
        },
        timeout: 2000,
        error: function (e) {
            console.error("Failed to send ajax");
            if(e.status === 498) { // Input error code.
                writeError(e.responseText);
            }
        },
        success: function () {
            console.log("Successfully played turn " + playTurnParams);
        }
    });
}

function playTurnAsync(playTurnParams) {
    // Clear label that might contain error messages.
    clearLabel();

    if(isGameActive() ) {
        if(isMyTurn) {
            sendTurnToServer(playTurnParams);
        } else {
            writeError("Cannot make a play during another player's turn.");
            console.log("Attempted to play while not in player's turn");
        }
    } else {
        writeError("Cannot make a play while game is not active.");
        console.log("Attempted to play while game is not active.");
    }
}

function quitGame() {
    var playTurnParams = new PlayTurnParameters(PLAYER_QUIT, null, loggedInPlayerName);
    sendTurnToServer(playTurnParams);
}