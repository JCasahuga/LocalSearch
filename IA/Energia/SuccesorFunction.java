package IA.Energia;

import aima.search.framework.SuccessorFunction;
import aima.search.framework.Successor;
import java.util.ArrayList;
import java.util.List;

public class ProbIA5SuccesorFunction implements SuccessorFunction{

    public List getSuccessors(Object state){
        ArrayList retval = new ArrayList();
        ProbIA5Board board = (ProbIA5Board) state;
	
	int totalClients = 0;
	int totalCentrals = 0;
	
	for (int i = 0; i < totalClients; ++i) {
		for (int j = 0; j < totalCentrals; ++j) {
		    ProbIA5Board new_state = new ProbIA5Board(board.getConfiguraration(), board.getSolution());
		    new_state.mouClient(i, j);
		    retval.add(new Successor(s, new_state));
		}
	}
	
	for (int i = 0; i < totalClients; ++i) {
		for (int j = 0; j < totalClients; ++j) {
		    ProbIA5Board new_state = new ProbIA5Board(board.getConfiguraration(), board.getSolution());
		    new_state.swapClient(i, j);
		    retval.add(new Successor(s, new_state));
		}
	}
	
        return retval;

    }
}
