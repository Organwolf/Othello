package main;

import java.util.List;

import com.eudycontreras.othello.capsules.AgentMove;
import com.eudycontreras.othello.capsules.MoveWrapper;
import com.eudycontreras.othello.capsules.ObjectiveWrapper;
import com.eudycontreras.othello.controllers.Agent;
import com.eudycontreras.othello.controllers.AgentController;
import com.eudycontreras.othello.controllers.AgentController.StrategyType;
import com.eudycontreras.othello.enumerations.PlayerTurn;
import com.eudycontreras.othello.models.GameBoardState;
import com.eudycontreras.othello.threading.ThreadManager;
import com.eudycontreras.othello.threading.TimeSpan;
import com.eudycontreras.othello.utilities.GameTreeUtility;

public class AlphaBeta extends Agent {
	int nodesExplored = 0;
	int prunedBranches = 0;
	int depth = 0;
	private static boolean searchCutoff = false;
	
	public AlphaBeta() {
		super(PlayerTurn.PLAYER_ONE);
	}
	public AlphaBeta(PlayerTurn playerTurn) {
		super(playerTurn);
	}
	public AgentMove getMove(GameBoardState gameState) {
		
		int waitTime = UserSettings.MIN_SEARCH_TIME;
		ThreadManager.pause(TimeSpan.millis(waitTime));
		
		
		long startTime = System.currentTimeMillis();
		int maxScore = Integer.MIN_VALUE;
		MoveWrapper bestMoveFound = new MoveWrapper(null);
		List<ObjectiveWrapper> AvailableMoves = AgentController.getAvailableMoves(gameState, playerTurn);
		
		for (ObjectiveWrapper move : AvailableMoves) {
			GameBoardState childState = AgentController.getNewState(gameState, move);
			long searchTimeLimit = ((UserSettings.MAX_SEARCH_TIME) / (AvailableMoves.size()));
			int score = iterativeDeepeningSearch(childState, searchTimeLimit);
			if (score > maxScore) {
				maxScore = score;
				bestMoveFound = new MoveWrapper(move);
			}
		}
		System.out.println("time take: " + String.valueOf(System.currentTimeMillis()-startTime));
		System.out.println("Move based on " + String.valueOf(depth) + " moves ahead!");
		System.out.println("bestMove: "+String.valueOf(maxScore));
		// System.out.println("Depth reached: " + depth);
		System.out.println("Nodes explored: "+ nodesExplored);
		System.out.println("Branched pruned: " + prunedBranches);
		return bestMoveFound;
	}
	
	
	
	
	
	
	
	private int iterativeDeepeningSearch(GameBoardState state, long timeLimit) {
		long startTime = System.currentTimeMillis();
		long endTime = startTime + timeLimit;
		depth = 1;
		int score = 0;
		searchCutoff = false;
		
		while (true) {
			long currentTime = System.currentTimeMillis();
			
			if (currentTime >= endTime) {
				break;
			}
			
			int searchResult = alphaBeta(Integer.MIN_VALUE, Integer.MAX_VALUE, depth, state, false, playerTurn, currentTime, endTime - currentTime);
			
			//
			// If the search finds a winning move, stop searching
			//
			
			if (!searchCutoff) {
				score = searchResult;
			}
			
			depth++;
		}
		System.out.println("result on depth " + String.valueOf(depth)+ ": " + String.valueOf(score));
		return score;
	}
	
	private int alphaBeta(int alpha, int beta, int depth, GameBoardState state, boolean isMaximizingPlayer, PlayerTurn playerTurn, long startTime, long timeLimit) {
	nodesExplored++;
	long currentTime = System.currentTimeMillis();
	long elapsedTime = (currentTime - startTime);
	
	if (elapsedTime >= timeLimit) {
		searchCutoff = true;
	}
	
	List<ObjectiveWrapper> availableMoves = AgentController.getAvailableMoves(state, playerTurn);
	int nbrOfWhiteMoves =  AgentController.getAvailableMoves(state, PlayerTurn.PLAYER_ONE).size();
	int nbrOfBlackMoves =  AgentController.getAvailableMoves(state, PlayerTurn.PLAYER_TWO).size();
	
	// Iterative deepening - time limit exceeded should terminate the search
	if (searchCutoff || depth == 0 || state.isTerminal() || ((nbrOfBlackMoves+nbrOfWhiteMoves)==0)) {
		//return (int) AgentController.getGameEvaluation(state, playerTurn);
		return (int) AgentController.getMobilityHeuristic(state);
		//return (int) AgentController.getDifferentiationHeuristic(state);
	}
	
	if (availableMoves.isEmpty()) return alphaBeta(alpha, beta, depth-1, state, !isMaximizingPlayer, GameTreeUtility.getCounterPlayer(playerTurn), startTime, timeLimit);
	
	if (isMaximizingPlayer) {
		int maxEval = Integer.MIN_VALUE;
		for (ObjectiveWrapper move : availableMoves) {
			GameBoardState childState = AgentController.getNewState(state, move);	
			int eval = alphaBeta(alpha, beta, depth-1, childState, !isMaximizingPlayer, GameTreeUtility.getCounterPlayer(playerTurn), startTime, timeLimit);
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
			int eval = alphaBeta(alpha, beta, depth-1, childState, !isMaximizingPlayer, GameTreeUtility.getCounterPlayer(playerTurn), startTime, timeLimit);
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

	
	
//	private MoveWrapper alphaBetaRoot(int depth, GameBoardState state, boolean isMaximizingPlayer, PlayerTurn playerTurn) {
//		nodesExplored=0;
//		prunedBranches=0;
//		long startTime = System.currentTimeMillis();
//		long endTime = startTime + 2000;
//		
//		List<ObjectiveWrapper> AvailableMoves = AgentController.getAvailableMoves(state, playerTurn);
//		int bestMove = 0;
//		MoveWrapper bestMoveFound = new MoveWrapper(null);
//		bestMove = Integer.MIN_VALUE;
//		depth++;
//		for (ObjectiveWrapper move : AvailableMoves) {
//			GameBoardState childState = AgentController.getNewState(state, move);	
//			int value = alphaBeta(Integer.MIN_VALUE, Integer.MAX_VALUE, depth-1, childState, !isMaximizingPlayer, playerTurn);
//			if (value>bestMove) {
//				bestMove = value;
//				bestMoveFound = new MoveWrapper(move);
//			}
//		}	
//		System.out.println("Move based on " + String.valueOf(depth) + " moves ahead!");
//		System.out.println("bestMove: "+String.valueOf(bestMove));
//		// System.out.println("Depth reached: " + depth);
//		System.out.println("Nodes explored: "+ nodesExplored);
//		System.out.println("Branched pruned: " + prunedBranches);
//		return bestMoveFound;
//		
//	}
//	
//	
//	
//	
//	
//	
//	
//	
//	private int alphaBeta(int alpha, int beta, int depth, GameBoardState state, boolean isMaximizingPlayer, PlayerTurn playerTurn) {
//		nodesExplored++;
//		
//		List<ObjectiveWrapper> availableMoves = AgentController.getAvailableMoves(state, playerTurn);
//		int nbrOfWhiteMoves =  AgentController.getAvailableMoves(state, PlayerTurn.PLAYER_ONE).size();
//		int nbrOfBlackMoves =  AgentController.getAvailableMoves(state, PlayerTurn.PLAYER_TWO).size();
//		
//		// Iterative deepening - time limit exceeded should terminate the search
//		if (depth <= 0 || state.isTerminal() || ((nbrOfBlackMoves+nbrOfWhiteMoves)==0)) {
//			//return (int) AgentController.getGameEvaluation(state, playerTurn);
//			return (int) AgentController.getDifferentiationHeuristic(state);
//		}
//		if (availableMoves.isEmpty()) {
//			return alphaBeta(alpha, beta, depth-1, state, !isMaximizingPlayer, GameTreeUtility.getCounterPlayer(playerTurn));
//		}
//		
//		if (isMaximizingPlayer) {
//			int maxEval = Integer.MIN_VALUE;
//			for (ObjectiveWrapper move : availableMoves) {
//				GameBoardState childState = AgentController.getNewState(state, move);	
//				int eval = alphaBeta(alpha, beta, depth-1, childState, !isMaximizingPlayer, GameTreeUtility.getCounterPlayer(playerTurn));
//				maxEval = Math.max(maxEval, eval);
//				alpha = Math.max(alpha, eval);
//				if (beta <= alpha) {
//					prunedBranches++;
//					break;
//				}
//			}
//			return maxEval;			
//		} else {
//			int minEval = Integer.MAX_VALUE;
//			for (ObjectiveWrapper move : availableMoves) {
//				GameBoardState childState = AgentController.getNewState(state, move);	
//				int eval = alphaBeta(alpha, beta, depth-1, childState, !isMaximizingPlayer, GameTreeUtility.getCounterPlayer(playerTurn));
//				minEval = Math.min(minEval, eval);
//				beta = Math.min(beta, eval);
//				if (beta <= alpha) {
//					prunedBranches++;
//					break;
//				}
//			}
//			return minEval;
//		}
//	}
	
//	private class TurnTimer extends Thread{
//		public void run() {
//			long startTime = System.currentTimeMillis();
//			while((startTime+UserSettings.MAX_SEARCH_TIME)>System.currentTimeMillis());
//			checkNextDepth=false;
//		}
//	}
}
