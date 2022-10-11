package IA.Energia;

import java.lang.reflect.Array;
import java.security.GeneralSecurityException;

import IA.Energia.*;

public class ElectricalNetworkState {
    // Atributtes
    private Clientes clients;
    private Centrales centrals;

    private int[] assignacioClients; 

    // ------------------------ Constructors -------------------------------
    public ElectricalNetworkState() {}

    public ElectricalNetworkState(Clientes clients, Centrales centrals) {
        this.clients = clients;
        this.centrals = centrals;

    }

    //  ---------------------- Initial states generation --------------
    public void generateInitialSolution(int method)
    {
        switch(method) {
            case 0:
                generateInitialSolution0();
                break;
            case 1:
                generateInitialSolution1();
                break;
        }
    }

    private void generateInitialSolution0() {
        for
    }

    private void generateInitialSolution1() {}


    // ------------------------ Funcions auxiliars ---------------------
    public void printState(boolean finalState, double time)
    {
        if (finalState) System.out.println ("Time to generate solution    " + time + " ms");
        System.out.println ("Solution benefit:            " + getBenefit());
        System.out.println ("Average distance to central: " + getAverageDistanceToCentrals());
        System.out.println ("Central ocupation distr.:    " + getOccupationDistribution());
        System.out.println();
    }
    
    private int getBenefit() {
        return 0;
    }

    private int getAverageDistanceToCentrals() {
        return 0;
    }

    private int getOccupationDistribution() {
        return 0;
    }
}
