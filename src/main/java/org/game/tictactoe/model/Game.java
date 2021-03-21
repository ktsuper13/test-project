package org.game.tictactoe.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.game.tictactoe.model.enums.GameStatus;

@JsonIgnoreProperties (ignoreUnknown = true)
@ApiModel (
        description =
                "A game object."
)
@JsonPropertyOrder ({
        "id",
        "board",
        "status"
})
public class Game {

    @ApiModelProperty (
            example = "3b4445ec-c894-4069-a317-36181192ebfa",
            value = "The game ID."
    )
    private String id;

    @ApiModelProperty (
            required = true,
            example = "XO--X--OX",
            value = "The board state.",
            position = -1
    )
    private String board;

    @ApiModelProperty (
            example = "RUNNING",
            allowableValues = "RUNNING, X_WON, O_WON, DRAW",
            value = "The game status, read-only, the client can not change this.",
            position = -2
    )
    private GameStatus status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }
}
