package main;

import com.eudycontreras.othello.capsules.AgentMove;
import com.eudycontreras.othello.controllers.Agent;
import com.eudycontreras.othello.controllers.AgentController;
import com.eudycontreras.othello.enumerations.PlayerTurn;
import com.eudycontreras.othello.models.GameBoardState;
import com.eudycontreras.othello.threading.ThreadManager;
import com.eudycontreras.othello.threading.TimeSpan;

public class AgentAB extends Agent{
	// why are the constructors private?
	
	public AgentAB() {
		super(PlayerTurn.PLAYER_ONE);
	}
	public AgentAB(PlayerTurn playerTurn) {
		super(playerTurn);
	}
	
	@Override
	public AgentMove getMove(GameBoardState gameState) {
		
		int waitTime = UserSettings.MIN_SEARCH_TIME;
		
		ThreadManager.pause(TimeSpan.millis(waitTime)); // Pauses execution for the wait time to cause delay
		
		return AgentController.ABMove(gameState, playerTurn); // returns an AB Pruning move
	}

}
