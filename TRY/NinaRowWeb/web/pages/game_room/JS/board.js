
function initBoard(gameData) {
    var rows = gameData.mRows;
    var columns = gameData.mColumns;
    var boardTable = $("#game-board-table");

    for(var i = 0; i < rows; i++) {
        var row = createRow(i, columns);
        boardTable.append(row);
    }

    if(isPopoutMode()) {
        initPopout(columns);
    }
}

function createRow(rowIndex, columns) {
    var row = $("<tr>").addClass("row-" + rowIndex).addClass("board-row");

    if(rowIndex % 2 == 0) {
        row.addClass("even-row");
    } else {
        row.addClass("odd-row");
    }

    for(var i = 0; i < columns; i++) {
        var cell = createCell(rowIndex, i);
        row.append(cell);
    }

    return row;
}

function createCell(rowIndex, columnIndex) {
    var cell = $("<td>").click(function() {
        onCellClick(rowIndex, columnIndex); // Call onclick function with row and column.
    }).addClass("cell-at-row-" + rowIndex).addClass("cell-at-column-" + columnIndex).addClass("board-cell").append();

    return cell;
}

function initPopout(numberOfComlumns) {
    var popoutRow = $("<tr>").addClass("popout-row");

    for(var i = 0; i < numberOfComlumns; i++) {
        var popoutButton = createPopoutButton(i);
        popoutRow.append(popoutButton);
    }

    var boardTable = $("#game-board-table");
    boardTable.append(popoutRow);
}

function createPopoutButton(index) {
    var popoutButton = $("<td> Popout </td>").click(function() {
        onPopoutButtonClick(index); // Call onclick function with row and column.
    }).addClass("popout-button").append();

    return popoutButton;
}

function onCellClick(row, column) {
    console.log("Cell clicked! row: " + row + " column: " + column);
    onColumnClick(column);
}

function updateBoardWithNewPlay(updatedCells) {
    for(var i = 0; i < updatedCells.length; i++) {
        var cell = updatedCells[i];
        var newCellBGColor = "";
        var rowClass = getRowClassFromRow(cell.mRowIndex);
        var columnClass = getColumnClassFromColumn(cell.mColumnIndex);

        if(cell.mPlayer !== undefined && cell.mPlayer !== null && cell.mPlayer.mName !== undefined && cell.mPlayer.mName !== null) {
            newCellBGColor = getColorForPlayer(cell.mPlayer.mName);
        }

        $('.' + rowClass + '.' + columnClass).css("background-color", newCellBGColor);
    }

}

function clearBoard() {
}

function getRowClassFromRow(rowIndex) {
    return "cell-at-row-" + rowIndex;
}

function getColumnClassFromColumn(columnIndex) {
    return "cell-at-column-" + columnIndex;
}