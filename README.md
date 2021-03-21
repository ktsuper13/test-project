# Tictactoe game

## Installation

Clone the project. Run it. It will be accessible on 2180 port.

```bash
http://localhost:2180
```
In tools/postman directory you can find Postman environment and collection for API ussage.

## Usage

* First you'll see start page with input of a game, where you can pass already started game, for example *"XOO-X-X-O"* (by default it is *"---------"* fully new game).
* Validations for input are:
   * Length of the game state is equal 9.
   * Possible characters are "X", "O" or "-".
* After pressing *Start* button - game will start. You're playing as X-player, server - O-player.
* When someone wins or this is a draw game(appropriate message will be shown), *Restart* button will appear and after pressing it, you'll be redirected to start page.
* Server player(O-player) tries to block your win combination.

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.
