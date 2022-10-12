package IA.Energia;

import java.lang.reflect.Array;
import java.security.GeneralSecurityException;
import java.lang.Math;

import IA.Energia.*;

public class ElectricalNetworkState {
    // Atributtes
    private Clientes clients;
    private Centrales centrals;

    private int[] assignedClients; 
    private double[] leftPowerCentral;

    private int benefici;

    // ------------------------ Constructors -------------------------------
    public ElectricalNetworkState() {}

    public ElectricalNetworkState(Clientes clients, Centrales centrals) {
        this.clients = clients;
        this.centrals = centrals;
        this.benefici = 0;
    }

    public ElectricalNetworkState(ElectricalNetworkState networkState) {
        this.clients = networkState.clients;
        this.centrals = networkState.centrals;

        this.assignedClients = networkState.assignedClients;
        this.leftPowerCentral = networkState.leftPowerCentral;
        this.benefici = networkState.benefici;
    }


    //  ---------------------- Initial states generation --------------
    public void generateInitialSolution(int method)
    {
        assignedClients = new int[clients.size()];
        leftPowerCentral = new double[centrals.size()];

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
        int tClients = clients.size();
        int tCentrals = centrals.size();

        // Energia Centrals = Producció
        for (int i = 0; i < tCentrals; ++i) {
            leftPowerCentral[i] = centrals.get(i).getProduccion();
        }

        // Assignació Clients
        for (int i = 0; i < tClients; ++i) {
            Cliente cl = clients.get(i);
            int closest = 0;
            double minDistance = 10000;
            double minConsumption = 10000;
            
            for (int j = 0; j < tCentrals; ++j) {
                Central ce = centrals.get(j);
                double d = distance(cl.getCoordX(), cl.getCoordY(), ce.getCoordX(), ce.getCoordY());
                double consumption = cl.getConsumo() * powerLossCompensation(d);
                if (consumption <= leftPowerCentral[j] && d < minDistance) {
                    minDistance = d;
                    closest = j;
                    minConsumption = consumption;
                }
            }

            System.out.println("Assigned client " + i + " to central " + close);
            assignedClients[i] = closest;
            leftPowerCentral[closest] -= minConsumption;
        }
    }

    private double powerLossCompensation(double d) {
        if (d <= 10) return 1;
        if (d <= 25) return 1 / 0.9;
        if (d <= 50) return 1 / 0.8;
        if (d <= 75) return 1 / 0.6;
        return 1 / 0.4;
    }

    private double distance(int x1, int y1, int x2, int y2) {
        int distX = x1 - x2;
        int distY = y1 - y2;
        return Math.sqrt(distX*distX + distY*distY);
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
        return benefici;
    }

    private int getAverageDistanceToCentrals() {
        return 0;
    }

    private int getOccupationDistribution() {
        return 0;
    }

    // -------------------- Funcions redefinides auxiliars ---------------------

    public int getClientsNumber() {
        return clients.size();
    }

    public int getCentralsNumber() {
        return centrals.size();
    }

    public int heuristic(){
        return benefici;
    }

    private Cliente getClient(int client) {
        return clients.get(client);
    }

    private Central getCentral(int central) {
        return clients.get(central);
    }

    private double getConsumption(int client) {
        return getClient(client).getConsumo();
    }

    private int getContract(int client) {
        return getClient(client).getContrato();
    }

    private boolean isGuaranteed(int client) {
        return getContract(client) == GARANTIZADO;
    }

    private boolean centralInUse(int central) {
        return getCentral(central).getProduccion() == leftPowerCentral[central];
    }

    // Returns the real consumption of a client given a central
    private double getConsumption(Cliente client, Central central) {
        return getRealConsumtion(getDistance(client, central), client.getConsumo());
    }

    // Distance in km and consum
    private double getRealConsumtion(double distance, double consumption) {
        double loses = 0;
        if      (distance > 75) loses = 0.6;
        else if (distance > 50) loses = 0.4;
        else if (distance > 25) loses = 0.2;
        else if (distance > 10) loses = 0.1;
        return consumption * (1+loses);
    }

    private boolean canMove(int client, int central)
    {
        if (leftPowerCentral[central] < getConsumption(client)) return false;
        return true;
    }

    ///////////////////////////////////////////////////////
    public void mouClient(int i, int j){
        
    }
    
    public void swapClient(int i, int j){
        // swap client i amb client j
        
    }
}
