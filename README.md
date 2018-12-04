# Othello
Assignment 1 in AI course DA272a

First and foremost, big thanks to Eudy Contreras for letting us use his Othello FX framework that can be found at: https://github.com/EudyContreras/Othello-Framework

How to run the code:

As we have chosen to work with the framework you will run the code through its main method which can be found in AgentManager.java. 
What we have added to the framework can be found in AlphaBeta.java. This class extends Agent and therefor has a getMove method. Within the getMove method the iterativeDeepeningSearch is called. This method performs alpha-beta pruning on the given node and searches though each child until either the time is up (5 seconds in our case) or the maximum depth (12 with a 4x4 board) is reached returning the best move thus far.

To the right of the board statistics of how many branches have been pruned and what depth was reached can be seen. We have set the MIN_SEARCH_TIME to 2 seconds. Otherwise the statistics get overwritten. If this is annoying just trust that the algorithm actually does what it’s supposed to and change the minimum search time to zero.

GitHub: https://github.com/Organwolf/Othello

//Aron & Filip

