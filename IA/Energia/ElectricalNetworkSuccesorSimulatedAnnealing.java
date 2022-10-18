package IA.Energia;

import IA.Energia.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

public class ElectricalNetworkSuccesorSimulatedAnnealing implements SuccessorFunction {
		    

    private static int moveCases;
    private static int swapCases;
    private static int resetCases;

    private static int saDepth = 10;
    private static int numberOfSuccessors = 300;

    public List getSuccessors (Object state) {

    	ArrayList <Successor> retval = new ArrayList<>();
    	ElectricalNetworkState networkState = new ElectricalNetworkState ((ElectricalNetworkState) state);
        retval.add(new Successor("Mateix estat", networkState));
    	
        int clientsNumber = networkState.getClientsNumber(), centralsNumber = networkState.getCentralsNumber();
        moveCases = clientsNumber * centralsNumber;
        swapCases = (clientsNumber * clientsNumber)/2;
        resetCases = centralsNumber * centralsNumber;         // Customized value for enhancing the chances
        int totalCases = moveCases + swapCases + resetCases;

    	String action = "ERROR";
        Random rand = new Random();
    	
        for (int i = 0; i < numberOfSuccessors; ++i) {
            
            ElectricalNetworkState nextState = new ElectricalNetworkState(networkState);

            boolean actionDone = false;
            while (!actionDone) {
                for (int j = 0; j < saDepth; ++j) {
                    int k = getRandomStep(totalCases);
                    //System.err.println("k is " + k);
                    switch (k) {
                    // Move Client
                    case 0:
                        int client = rand.nextInt(clientsNumber), central = rand.nextInt(centralsNumber);
                        actionDone = actionDone || nextState.mouClient(client, central);
                        action = "Moved client " + client + " to central " + central;
                        //System.err.println(action);
                        break;    			
                    // Switch Client
                    case 1:
                        int client1 = rand.nextInt(clientsNumber), client2 = rand.nextInt(clientsNumber);
                        actionDone = actionDone || nextState.swapClient(client1, client2);
                        action = "Swaped client " + client1 + " for " + client2;
                        //System.err.println(action);
                        break;
                    // Reset Central
                    case 2:
                        int centralRemoved = rand.nextInt(centralsNumber);
                        actionDone = actionDone || nextState.resetCentral(centralRemoved);
                        action = "Eliminat clients central" + centralRemoved;
                        //System.err.println(action);
                        retval.add(new Successor(action, nextState));
                        break;
                    // Default
                    default:
                        System.err.println("uooo you shouldn't be here... (error in sa successor function)");
                        break;
                    }
                }
            }
            retval.add(new Successor(action, nextState));
        }
        return retval;
    }

    private int getRandomStep(int totalCases) {
        Random rand = new Random();
        double randomNum = rand.nextInt(totalCases);
        int limit = moveCases;
        if (randomNum < limit) return 0;
        limit += swapCases;
        if (randomNum < limit) return 1;
        limit += resetCases;
        if (randomNum < limit) return 2;
        System.err.println("Random number is " + randomNum + " / " + totalCases + " limit (" + limit+")");
        return -1;
    }
}