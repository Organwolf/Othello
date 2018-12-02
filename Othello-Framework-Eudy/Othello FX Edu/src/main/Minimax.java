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
import com.eudycontreras.othello.utilities.GameTreeUtility;

public class Minimax extends Agent {
	
	private boolean checkNextDepth;

	public Minimax() {
		super(PlayerTurn.PLAYER_ONE);
	}
	public Minimax(PlayerTurn playerTurn) {
		super(playerTurn);
	}
	public AgentMove getMove(GameBoardState gameState) {
		int waitTime = UserSettings.MIN_SEARCH_TIME;
		
		ThreadManager.pause(TimeSpan.millis(waitTime)); // Pauses execution for the wait time to cause delay
		
		// never use depth = 1 -> will jump to evaluation immediately
		int depth = 2;
		long startTime = System.currentTimeMillis();
		AgentMove val = minimaxRoot(depth, gameState, true, playerTurn); 
		long endTime = System.currentTimeMillis();
		System.out.println("Time taken: " + String.valueOf(endTime-startTime) + "mS");
		return val;
	}
	
	private MoveWrapper minimaxRoot(int depth, GameBoardState state, boolean isMaximizingPlayer, PlayerTurn playerTurn) {
			
			List<ObjectiveWrapper> AvailableMoves = AgentController.getAvailableMoves(state, playerTurn);
			int bestMove = Integer.MIN_VALUE;
			MoveWrapper bestMoveFound = new MoveWrapper(null);
			
			for (ObjectiveWrapper move : AvailableMoves) {
				GameBoardState childState = AgentController.getNewState(state, move);	
				int value = minimax(depth-1, childState, !isMaximizingPlayer, playerTurn);
				if (value>bestMove) {
					bestMove = value;
					bestMoveFound = new MoveWrapper(move);
				}
			}	
			System.out.println("bestMove: "+String.valueOf(bestMove));
			return bestMoveFound;
		}
	
	private int minimax(int depth, GameBoardState state, boolean isMaximizingPlayer, PlayerTurn playerTurn) {
		List<ObjectiveWrapper> availableMoves = AgentController.getAvailableMoves(state, playerTurn);
		// Do we have to terminate if the player can't make any moves?
		int nbrOfWhiteMoves =  AgentController.getAvailableMoves(state, PlayerTurn.PLAYER_ONE).size();
		int nbrOfBlackMoves =  AgentController.getAvailableMoves(state, PlayerTurn.PLAYER_TWO).size();
		if (depth == 0 || state.isTerminal() || ((nbrOfBlackMoves+nbrOfWhiteMoves)==0)) {
			return (int) AgentController.getGameEvaluation(state, playerTurn);
			//return (int) AgentController.getDifferentiationHeuristic(state);
		}

		if (availableMoves.isEmpty()) {
			//System.out.println("no children, pass to next!");
			return minimax(depth-1, state, !isMaximizingPlayer, GameTreeUtility.getCounterPlayer(playerTurn));
		}
		
		if (isMaximizingPlayer) {
			int maxVal = Integer.MIN_VALUE;
			for (ObjectiveWrapper move : availableMoves) {
				GameBoardState childState = AgentController.getNewState(state, move);	
				maxVal = Math.max(maxVal, minimax(depth-1, childState, !isMaximizingPlayer, GameTreeUtility.getCounterPlayer(playerTurn)));
			}
			return maxVal;			
		}		
		else {
			int minVal = Integer.MAX_VALUE;
			for (ObjectiveWrapper move : availableMoves) {
				GameBoardState childState = AgentController.getNewState(state, move);	
				minVal = Math.min(minVal, minimax(depth-1, childState, !isMaximizingPlayer, GameTreeUtility.getCounterPlayer(playerTurn)));
			}
			return minVal;
		}		
	}
	
	private class TurnTimer extends Thread{
		public void run() {
			long startTime = System.currentTimeMillis();
			while((startTime+UserSettings.MAX_SEARCH_TIME)>System.currentTimeMillis());
			checkNextDepth=false;
		}
	}
}
