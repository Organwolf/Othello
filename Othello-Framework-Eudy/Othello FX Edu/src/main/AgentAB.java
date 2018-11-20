package main;

import com.eudycontreras.othello.capsules.AgentMove;
import com.eudycontreras.othello.controllers.Agent;
import com.eudycontreras.othello.enumerations.PlayerTurn;
import com.eudycontreras.othello.models.GameBoardState;

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

		return null;
	}

}
