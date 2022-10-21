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

		System.out.println(networkState.getDynamicBenefit());
		
		String action;
		boolean actionDone = false;
		for (int i = 0; i < totalClients; ++i) {
			for (int j = -1; j < totalCentrals; ++j) {
				if (networkState.canMove(i, j)) {
					ElectricalNetworkState new_state = new ElectricalNetworkState(networkState);
					actionDone = new_state.mouClient(i, j);
					action = "Moved client " + i + " to central " + j;
					retval.add(new Successor(action, new_state));
				}
			}
		}
		
		for (int i = 0; i < totalCentrals; ++i) {
			if (networkState.centralInUse(i)) {
				ElectricalNetworkState new_state = new ElectricalNetworkState(networkState);
				actionDone = new_state.resetCentral(i);
				action = "Eliminat clients central" + i;
				retval.add(new Successor(action, new_state));
			}
		}

		for (int i = 0; i < totalClients; ++i) {
			for (int j = i+1; j < totalClients; ++j) {
				if (networkState.canSwap(i, networkState.getCentralAssignedToClient(i),
										j, networkState.getCentralAssignedToClient(j))) 
				{
					ElectricalNetworkState new_state = new ElectricalNetworkState(networkState);
					actionDone = new_state.swapClient(i, j);
					action = "Swaped client " + i + " for " + j;
					retval.add(new Successor(action, new_state));
				}
			}
		}
		return retval;
	}
}
