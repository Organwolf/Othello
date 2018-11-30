package main;

import java.util.LinkedList;
import java.util.List;

import com.eudycontreras.othello.capsules.AgentMove;
import com.eudycontreras.othello.capsules.MoveWrapper;
import com.eudycontreras.othello.capsules.ObjectiveWrapper;
import com.eudycontreras.othello.controllers.Agent;
import com.eudycontreras.othello.controllers.AgentController;
import com.eudycontreras.othello.controllers.AgentController.ResultWrapper;
import com.eudycontreras.othello.enumerations.PlayerTurn;
import com.eudycontreras.othello.models.GameBoardState;
import com.eudycontreras.othello.threading.ThreadManager;
import com.eudycontreras.othello.threading.TimeSpan;

public class AlphaBetaPruning extends Agent {
	
	public AlphaBetaPruning() {
		super(PlayerTurn.PLAYER_ONE);
	}
	public AlphaBetaPruning(PlayerTurn playerTurn) {
		super(playerTurn);
	}
	public AgentMove getMove(GameBoardState gameState) {
		int waitTime = UserSettings.MIN_SEARCH_TIME;
		
		ThreadManager.pause(TimeSpan.millis(waitTime)); // Pauses execution for the wait time to cause delay
		
		int depth = 4;
		return AgentController.alphaBetaRoot(Integer.MIN_VALUE, Integer.MAX_VALUE, depth, gameState, true, playerTurn); 
	}
}
