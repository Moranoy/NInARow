var USER_LIST_URL = buildUrlWithContextPath("userslist");
var UPLOAD_FILE_URL = buildUrlWithContextPath("upload");
var PULL_GAMES_DATA_URL = buildUrlWithContextPath("gamesdata");
var JOIN_GAME_URL = buildUrlWithContextPath("joingame");

$(function() {
    var pullTimer = 1500;
    console.log(window.loggedInUserName);
    window.setInterval(pullUserNames, pullTimer);
    window.setInterval(pullGames, pullTimer);
    $("#uploadForm").submit(onFormSubmit);
});

function pullUserNames() {
    $.ajax({
        url: USER_LIST_URL,
        timeout: 2000,
        error: function(e) {
            console.error("Failed to send ajax");
        },
        success: function(objectsDataArray) {
            $('#users-names').children().remove();
            $.each(objectsDataArray || [], addUserName);
        }
    });
}

// dataJson = "username"
function addUserName(index, dataJson) {
    var tableData = $("<td>").append(dataJson);
    var tableRow = $("<tr>").append(tableData);

    $('#users-names')
        .append(tableRow);
}

function onFormSubmit() {
    var file = this[0].files[0];
    var formData = new FormData();
    formData.append("fake-key-1", file);

    $.ajax({
        method:'POST',
        data: formData,
        url: UPLOAD_FILE_URL,
        processData: false, // Don't process the files
        contentType: false, // Set content type to false as jQuery will tell the server its a query string request
        timeout: 4000,
        error: function(e) {
            if (e.status === 499) {
                alert("Failed to submit. Reason: " + e.responseText);
                console.error("Failed to submit. " + e.message());
            }},
        success: function(r) {
            $("#result").text("Success uploading file: " + r);
            console.log(r);
        }
    });

    // return value of the submit operation
    // by default - we'll always return false so it doesn't redirect the user.
    return false;
}

function pullGames() {
    $.ajax({
        url: PULL_GAMES_DATA_URL,
        timeout: 2000,
        error: function(e) {
            console.error("Failed to send ajax");
        },
        success: function(objectsDataArray) {
            $('#game-details').children().remove();
            $.each(objectsDataArray || [], addGameDetails);
        }
    });
}


// dataJson = { mGameName = "", mGameState = "", mCurrentNumberOfPlayers = 0, mMaxPlayers = 4, mRows = 7, mColumns = 8, mTarget = 4, mUploaderName = "" }
function addGameDetails(index, dataJson) {
    var tableRow = $("<tr>");

    tableRow.append("<td>" + dataJson.mGameName + "</td>");
    tableRow.append("<td>" + dataJson.mGameState + "</td>");
    tableRow.append("<td>" + dataJson.mCurrentNumberOfPlayers + "/" + dataJson.mMaxPlayers + "</td>");
    tableRow.append("<td>" + dataJson.mVariant + "</td>");
    tableRow.append("<td>" + dataJson.mRows + "X" + dataJson.mColumns+ "</td>");
    tableRow.append("<td>" + dataJson.mTarget+ "</td>");
    tableRow.append("<td>" + dataJson.mUploaderName + "</td>");

    if (dataJson.mGameState === "Inactive"){
        tableRow.append("<td><button onclick='joinGame(\"" + dataJson.mGameName + "\")'>Join Game</button></td>");
    } else {
        tableRow.append("<td><button disabled='true'(\"" + dataJson.mGameName + "\")'>Join Game</button></td>");
    }

    $('#game-details').append(tableRow);
}

function joinGame(gameName) {
    console.log("Join " + gameName);
    var url = JOIN_GAME_URL + '?gamename=' + gameName;
    window.location = url.replace(' ', '+');

    return true;
}