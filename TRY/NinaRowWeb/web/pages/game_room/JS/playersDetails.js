var playersData;

// dataJson = { mName = "", mType = "", mTurnCounter = 0 }
function addPlayerData(index, dataJson) {
    var tableRow = getPlayerRow(index, dataJson);

    $('#player-details-tbody')
        .append(tableRow);
}

// dataJson = { mName = "", mType = "", mTurnCounter = 0 }
this.getPlayerRow = function(index, playerJson) {
    var playerNameLabel = $("<h1>").addClass("player-name").append(playerJson.mName);
    var playerTypeLabel = $("<h2>").addClass("player-type").append(playerJson.mType);
    var playerTurnLabel = $("<h2>").addClass("player-turn").append(playerJson.mTurnCounter);

    var colorForPlayer;
    var currentPlayerClass = undefined;

    if(isGameActive()) {
        colorForPlayer = getColorForPlayer(playerJson.mName);
        if(currentPlayerName === playerJson.mName) {
            currentPlayerClass = "currentPlayer";
        }
    } else {
        colorForPlayer = "transparent";
    }

    var playerDiv = $("<div>").css('background-color', colorForPlayer).addClass("playerDiv").addClass("player-" + index + "-div");
    playerDiv.append(playerNameLabel).append(playerTypeLabel).append(playerTurnLabel);

    if(currentPlayerClass !== undefined) {
        playerDiv.addClass(currentPlayerClass); //TODO: not working
    }

    var tableData = $("<td>").addClass("playerData").append(playerDiv);
    var tableRow = $("<tr>").append(tableData);

    return tableRow;
};

var playerNameToColor = new Map();
var colorsArray = ["Purple", "Orange", "Pink", "Yellow", "Blue", "Green"];
var isPlayerColorsSet = false;

function getColorForPlayer(playerName){
    return playerNameToColor.get(playerName);
}

function initPlayerColors(playersData) {
    for(var i = 0; i < playersData.length; i++) {
        playerNameToColor.set(playersData[i].mName, colorsArray[i]);
    }

    isPlayerColorsSet = true;
}