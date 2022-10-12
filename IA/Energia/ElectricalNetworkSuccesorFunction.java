package IA.Energia;

import IA.Energia.*;

import aima.search.framework.SuccessorFunction;
import aima.search.framework.Successor;
import java.util.ArrayList;
import java.util.List;

public class ElectricalNetworkSuccesorFunction implements SuccessorFunction{

    public List getSuccessors(Object state){
		ArrayList <Successor> retval = new ArrayList<>();
    	ElectricalNetworkState networkState = new ElectricalNetworkState ((ElectricalNetworkState) state);

	
	int totalClients = 0;
	int totalCentrals = 0;
	
	for (int i = 0; i < totalClients; ++i) {
		for (int j = 0; j < totalCentrals; ++j) {
		    ElectricalNetworkState new_state = new ElectricalNetworkState(networkState.getConfiguraration(), networkState.getSolution()); 
		    new_state.mouClient(i, j);
		    retval.add(new Successor("actionName", new_state));
		}
	}
	
	for (int i = 0; i < totalClients; ++i) {
		for (int j = 0; j < totalClients; ++j) {
		    ElectricalNetworkState new_state = new ElectricalNetworkState();
		    new_state.swapClient(i, j);
		    retval.add(new Successor("actionName", new_state));
		}
	}
        return retval;
    }
}
