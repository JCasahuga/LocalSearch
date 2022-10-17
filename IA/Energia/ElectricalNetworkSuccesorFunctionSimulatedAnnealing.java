package IA.Energia;

import IA.Energia.*;

import aima.search.framework.SuccessorFunction;
import aima.search.framework.Successor;
import java.util.ArrayList;
import java.util.List;

public class ElectricalNetworkSuccesorFunctionSimulatedAnnealing implements SuccessorFunction{

    public List getSuccessors(Object state){
		ArrayList <Successor> retval = new ArrayList<>();
    	ElectricalNetworkState networkState = new ElectricalNetworkState ((ElectricalNetworkState) state);

	
        int totalClients = networkState.getClientsNumber();
        int totalCentrals = networkState.getCentralsNumber();

   
		String action;
		for (int i = 0; i < totalClients; ++i) {
			for (int j = -1; j < totalCentrals; ++j) {
				ElectricalNetworkState new_state = new ElectricalNetworkState(networkState);
				new_state.mouClient(i, j);

				//System.err.println("No ha mogut?: " + state.equals(new_state));

				//System.out.println(new_state.getAssignedClients());
				//System.err.println("Moved client " + i + " to central " + j);
				action = "Moved client " + i + " to central " + j;
				//System.out.println(action);
				retval.add(new Successor(action, new_state));
			}
		}

		for (int i = 0; i < totalCentrals; ++i) {
			ElectricalNetworkState new_state = new ElectricalNetworkState(networkState);
			new_state.resetCentral(i);
			action = "Eliminat clients central" + i;
			retval.add(new Successor(action, new_state));
		}

		for (int i = 0; i < totalClients; ++i) {
			for (int j = i+1; j < totalClients; ++j) {
				ElectricalNetworkState new_state = new ElectricalNetworkState(networkState);
				new_state.swapClient(i, j);
				action = "Swaped client " + i + " for " + j;
				//System.out.println(action);
				retval.add(new Successor(action, new_state));
			}
		}
        return retval;
    }
}
