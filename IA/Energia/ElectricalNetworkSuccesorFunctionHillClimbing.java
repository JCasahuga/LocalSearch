package IA.Energia;

import IA.Energia.*;

import aima.search.framework.SuccessorFunction;
import aima.search.framework.Successor;
import java.util.ArrayList;
import java.util.List;

public class ElectricalNetworkSuccesorFunctionHillClimbing implements SuccessorFunction{

    public List getSuccessors(Object state){
		ArrayList <Successor> retval = new ArrayList<>();
    	ElectricalNetworkState networkState = new ElectricalNetworkState ((ElectricalNetworkState) state);
		retval.add(new Successor("Mateix estat", networkState));

	
	int totalClients = networkState.getClientsNumber();
	int totalCentrals = networkState.getCentralsNumber();
	
	String action;
	for (int i = 0; i < totalClients; ++i) {
		for (int j = 0; j < totalCentrals; ++j) {
		    ElectricalNetworkState new_state = new ElectricalNetworkState(networkState); 
		    new_state.mouClient(i, j);
			action = "Moved client " + i + " to central " + j;
		    retval.add(new Successor(action, new_state));
		}
	}
	
	// for (int i = 0; i < totalClients; ++i) {
	// 	for (int j = 0; j < totalClients; ++j) {
	// 	    ElectricalNetworkState new_state = new ElectricalNetworkState(networkState);
	// 	    new_state.swapClient(i, j);
	// 	    retval.add(new Successor("actionName", new_state));
	// 	}
	// }
        return retval;
    }
}
