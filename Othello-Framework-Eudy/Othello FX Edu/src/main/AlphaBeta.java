package main;

import java.util.List;

import com.eudycontreras.othello.capsules.AgentMove;
import com.eudycontreras.othello.capsules.MoveWrapper;
import com.eudycontreras.othello.capsules.ObjectiveWrapper;
import com.eudycontreras.othello.controllers.Agent;
import com.eudycontreras.othello.controllers.AgentController;
import com.eudycontreras.othello.enumerations.PlayerTurn;
import com.eudycontreras.othello.models.GameBoardState;
import com.eudycontreras.othello.threading.ThreadManager;
import com.eudycontreras.othello.threading.TimeSpan;
import com.eudycontreras.othello.utilities.GameTreeUtility;

public class AlphaBeta extends Agent {
	int nodesExplored = 0;
	int prunedBranches = 0;
	
	public AlphaBeta() {
		super(PlayerTurn.PLAYER_ONE);
	}
	public AlphaBeta(PlayerTurn playerTurn) {
		super(playerTurn);
	}
	public AgentMove getMove(GameBoardState gameState) {
		
		int waitTime = UserSettings.MIN_SEARCH_TIME;
		ThreadManager.pause(TimeSpan.millis(waitTime));
		int depth = 6;
		return alphaBetaRoot(depth, gameState, true, this.playerTurn);
	}
	
	private MoveWrapper alphaBetaRoot(int depth, GameBoardState state, boolean isMaximizingPlayer, PlayerTurn playerTurn) {
		nodesExplored=0;
		prunedBranches=0;
		
		List<ObjectiveWrapper> AvailableMoves = AgentController.getAvailableMoves(state, playerTurn);
		int bestMove = Integer.MIN_VALUE;
		MoveWrapper bestMoveFound = new MoveWrapper(null);
		
		for (ObjectiveWrapper move : AvailableMoves) {
			GameBoardState childState = AgentController.getNewState(state, move);	
			int value = alphaBeta(Integer.MIN_VALUE, Integer.MAX_VALUE, depth-1, childState, !isMaximizingPlayer, playerTurn);
			if (value>bestMove) {
				bestMove = value;
				bestMoveFound = new MoveWrapper(move);
			}
		}	
		System.out.println("bestMove: "+String.valueOf(bestMove));
		// System.out.println("Depth reached: " + depth);
		System.out.println("Nodes explored: "+ nodesExplored);
		System.out.println("Branched pruned: " + prunedBranches);
		return bestMoveFound;
		
	}
	
	private int alphaBeta(int alpha, int beta, int depth, GameBoardState state, boolean isMaximizingPlayer, PlayerTurn playerTurn) {
		nodesExplored++;
		
		List<ObjectiveWrapper> availableMoves = AgentController.getAvailableMoves(state, playerTurn);
		int nbrOfWhiteMoves =  AgentController.getAvailableMoves(state, PlayerTurn.PLAYER_ONE).size();
		int nbrOfBlackMoves =  AgentController.getAvailableMoves(state, PlayerTurn.PLAYER_TWO).size();
		
		// Iterative deepening - time limit exceeded should terminate the search
		if (depth <= 0 || state.isTerminal() || ((nbrOfBlackMoves+nbrOfWhiteMoves)==0)) {
			return (int) AgentController.getGameEvaluation(state, playerTurn);
		}
		if (availableMoves.isEmpty()) {
			return alphaBeta(alpha, beta, depth-1, state, !isMaximizingPlayer, GameTreeUtility.getCounterPlayer(playerTurn));
		}
		
		if (isMaximizingPlayer) {
			int maxEval = Integer.MIN_VALUE;
			for (ObjectiveWrapper move : availableMoves) {
				GameBoardState childState = AgentController.getNewState(state, move);	
				int eval = alphaBeta(alpha, beta, depth-1, childState, !isMaximizingPlayer, GameTreeUtility.getCounterPlayer(playerTurn));
				maxEval = Math.max(maxEval, eval);
				alpha = Math.max(alpha, eval);
				if (beta <= alpha) {
					prunedBranches++;
					break;
				}
			}
			return maxEval;			
		} else {
			int minEval = Integer.MAX_VALUE;
			for (ObjectiveWrapper move : availableMoves) {
				GameBoardState childState = AgentController.getNewState(state, move);	
				int eval = alphaBeta(alpha, beta, depth-1, childState, !isMaximizingPlayer, GameTreeUtility.getCounterPlayer(playerTurn));
				minEval = Math.min(minEval, eval);
				beta = Math.min(beta, eval);
				if (beta <= alpha) {
					prunedBranches++;
					break;
				}
			}
			return minEval;
		}
	}
}
