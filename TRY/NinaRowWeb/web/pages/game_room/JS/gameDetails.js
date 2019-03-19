
function initGameDetailsUI(gameData) {
    $('#tdGameType').append(gameData.mVariant);
    $('#tdTarget').append(gameData.mTarget);
    $('#tdBoardSize').append(gameData.mRows + "X" + gameData.mColumns);
    $('#tdGameUploader').append(gameData.mUploaderName);
    $('#tdGameState').append(gameData.mGameState);
}

function handleGameStateChanged(gameState) {
    $('#tdGameState').empty().append(gameState);

    switch(gameState) {
        case "Active":
            writeNotification("Game is active!");
            break;
        case "Inactive":
            writeNotification("Waiting for more players.");
            resetGame();
            break;
        case "Won":
            fetchPlayerToWinningSequenceMap();
            break;
        case "Draw":
            writeNotification("Game has ended in a draw!");
            break;
    }
}

function resetGame() {
    isMyTurn = false;
    turnNumber = 0;

    // Clear board here because this JS file does not know 'board.js'
    $(".board-cell").css("background-color", ""); // Remove cell's bg color, will revert back to row's bg color.
}
