package com.jhop.restservice;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.jhop.restservice.models.Game;
import com.jhop.restservice.models.GameMove;
import com.jhop.restservice.models.MoveResult;
import com.jhop.restservice.services.GameServer;

@RestController
public class GameController {

	private GameServer gameServer;

	// Because this is a component, Spring creates a gameServer, and passes it in.
	GameController(GameServer gameServer) {
		this.gameServer = gameServer;
	}

	/**
	 * startGame (send name)
	 * 
	 * This will return the game id. This is either a new game, or a game with an
	 * opponent. If the name is already in an active game, this will return null.
	 * 
	 */
	@GetMapping("/startGame")
	public Game startGame(@RequestParam(value = "name", defaultValue = "Player1") String name) {

		// TODO look at a better way to handle identity. use a session or
		// BasicAuthentication
		Game game = gameServer.startGame(name);
		if (game == null) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Name is in use");
		}

		return game;
	}

	/**
	 * This just returns the state of the game. This will return the state of the
	 * game board It will also return a status. This can be
	 * 
	 * awaiting_oponent YourMove TheirMove GameOver
	 * 
	 */
	@GetMapping("/Game/{gameID}")
	public Game getGameState(@PathVariable String gameID) {
		return gameServer.getGame(gameID);
	}

	/**
	 * makeMove
	 * 
	 * This will send a move. This will return Not_Your_Turn OK Invalid:<reason>
	 * 
	 */
	@PostMapping("/Game/{gameID}")
	public MoveResult makeMove(@PathVariable String gameID, @RequestBody GameMove newMove, HttpServletRequest request) {

		// check the move is OK
		Game game = gameServer.getGame(gameID);
		if (game == null) {
			return new MoveResult(false, "Invalid GameID");
		}

		if (game.isFinished()) {
			return new MoveResult(false, "Games Over");
		}

		if (!game.getTurn().equals(newMove.getPlayer())) {
			return new MoveResult(false, "Not Your Turn");
		}

		boolean moveDone = game.makeMove(newMove.getPlayer(), newMove.getColumn());

		MoveResult result = new MoveResult(moveDone);
		if (!moveDone) {
			result.setErrorReason("Invalid Move");
		}

		return result;
	}

	/**
	 * quit
	 * 
	 * This leaves the game. If the game was not won, then the other player wins by
	 * default This relies on a user passing in their name This doesn't actually
	 * remove the game, so maybe DELETE isn't strictly correct, but it will work
	 */
	@DeleteMapping("/Game/{gameID}")
	public void quitGame(@PathVariable String gameID, @RequestParam(value = "name") String name) {

		Game game = gameServer.getGame(gameID);
		if (game == null) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Game ID");
		}
		if (!game.hasPlayer(name)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your game");
		}

		game.quit(name);
	}
}