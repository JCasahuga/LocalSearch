package IA.Energia;

import java.lang.reflect.Array;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.*;
import java.util.function.ObjDoubleConsumer;

import javax.swing.text.html.HTMLDocument.Iterator;

import java.lang.Math;

import IA.Energia.*;
import aima.datastructures.PriorityQueue;
import aima.search.framework.SearchAgent;

public class ElectricalNetworkState {

    // Constants
    public static final int CENTRALA = 0;
    public static final int CENTRALB = 1;
    public static final int CENTRALC = 2;

    public static final int CLIENTEG = 2;
    public static final int CLIENTEMG = 1;
    public static final int CLIENTEXG = 0;

    public static final int GARANTIZADO = 0;
    public static final int NOGARANTIZADO = 1;

    // Atributtes
    private Clientes clients;
    private Centrales centrals;

    private int[] assignedClients; 
    private double[] leftPowerCentral;
    private double benefDynamic = 0;

    // ------------------------ Constructors -------------------------------
    public ElectricalNetworkState() {}

    public ElectricalNetworkState(Clientes clients, Centrales centrals) {
        this.clients = clients;
        this.centrals = centrals;
    }

    public ElectricalNetworkState(ElectricalNetworkState networkState) {
        clients = networkState.getClients();
        centrals = networkState.getCentrals();

        assignedClients = Arrays.copyOf(networkState.getAssignedClients(), getClientsNumber());
        leftPowerCentral = Arrays.copyOf(networkState.getLeftPowerCentral(), getCentralsNumber());
        benefDynamic = networkState.getDynamicBenefit();
    }

    // public ElectricalNetworkState(Clientes clnts, Centrales ctrls, int[] assigClt, double[] lfPwCtrl) {
    //     clients = clnts;
    //     centrals = ctrls;

    //     assignedClients = assigClt;
    //     leftPowerCentral = lfPwCtrl;
    // }



    //  ---------------------- Initial states generation --------------
    public void generateInitialSolution(int method)
    {
        assignedClients = new int[clients.size()];
        leftPowerCentral = new double[centrals.size()];

        switch(method) {
            case 0:
                generateInitialSolutionClosest();
                break;
            case 1:
                generateInitialSolution1();
                break;
        }

        benefDynamic = getBenefit();
    }

    private void generateInitialSolutionClosest() {
        int tClients = getClientsNumber();
        int tCentrals = getCentralsNumber();

        // Energia Centrals = Producció
        for (int i = 0; i < tCentrals; ++i) {
            leftPowerCentral[i] = centrals.get(i).getProduccion();
        }

        Vector<Integer> clientsOrdenats = new Vector<Integer>();
        
        for (int i = 0; i < tClients; ++i) {
            if (isGuaranteed(i)) 
                clientsOrdenats.add(i);
        }

        for (int i = 0; i < tClients; ++i) {
            if (!isGuaranteed(i)) 
                clientsOrdenats.add(i);
        }


        // Assignació Clients
        for (Integer i : clientsOrdenats) {
            Cliente cl = clients.get(i);
            int closest = -1;
            double minDistance = 10000;
            double minConsumption = 10000;
            for (int j = 0; j < tCentrals; ++j) {
                Central ce = centrals.get(j);
                double d = getDistance(cl, ce);

                if (getRealConsumption(cl, ce) <= leftPowerCentral[j] && d < minDistance) {
                    minDistance = d;
                    closest = j;
                    minConsumption = getRealConsumption(cl, ce);
                }
            }
            assignedClients[i] = closest;
            if (closest != -1)  {
                leftPowerCentral[closest] -= minConsumption;
            }
            //System.out.println("Assigned client " + i + " to central " + closest);
        }
    }

    private double powerLossCompensation(double d) {
        if (d <= 10) return 1;
        if (d <= 25) return 1 / 0.9;
        if (d <= 50) return 1 / 0.8;
        if (d <= 75) return 1 / 0.6;
        return 1 / 0.4;
    }

    private double getDistance(int x1, int y1, int x2, int y2) {
        int distX = x1 - x2;
        int distY = y1 - y2;
        return Math.sqrt(distX*distX + distY*distY);
    }

    private double getDistance(int cl, int ce) {
        return getDistance(getClient(cl), getCentral(ce));
    }

    private double getDistance(Cliente cl, Central ce) {
        return getDistance(cl.getCoordX(), cl.getCoordY(), ce.getCoordX(), ce.getCoordY());
    }

    private void generateInitialSolution1() {
        int tClients = getClientsNumber();
        int tCentrals = getCentralsNumber();

        // Energia Centrals = Producció
        for (int i = 0; i < tCentrals; ++i) {
            leftPowerCentral[i] = centrals.get(i).getProduccion();
        }

        Vector<Integer> clientsOrdenats = new Vector<Integer>();
        
        for (int i = 0; i < tClients; ++i) {
            if (isGuaranteed(i)) 
                clientsOrdenats.add(i);
        }

        for (int i = 0; i < tClients; ++i) {
            if (!isGuaranteed(i)) 
                clientsOrdenats.add(i);
        }


        // Assignació Clients
        for (Integer i : clientsOrdenats) {
            Cliente cl = clients.get(i);
            int closest = -1;
            double minDistance = 10000;
            double minConsumption = 10000;
            for (int j = 0; j < tCentrals; ++j) {
                Central ce = centrals.get(j);
                double d = getDistance(cl, ce);

                if (getRealConsumption(cl, ce) <= leftPowerCentral[j] && isGuaranteed(i)) {
                    if (d < minDistance) {
                        minDistance = d;
                        closest = j;
                        minConsumption = getRealConsumption(cl, ce);
                    }
                }
            }
            
            assignedClients[i] = closest;
            if (closest != -1)  {
                leftPowerCentral[closest] -= minConsumption;
            }

            //System.out.println("Assigned client " + i + " to central " + closest);
        }
    }


    // ------------------------ Funcions auxiliars ---------------------
    public Clientes getClients(){
        return clients;
    }

    public Centrales getCentrals(){
        return centrals;
    }

    public int[] getAssignedClients(){
        return assignedClients;
    }

    public double[] getLeftPowerCentral(){
        return leftPowerCentral;
    }

    public void printState(boolean finalState, double time, boolean printSteps, SearchAgent agent, int algorithm)
    {
        if (!finalState)System.out.println ("------- Starting generated solution: "); 
        else            System.out.println ("------- Final generated solution: "); 
        
        if (algorithm == 0 && finalState && printSteps) { // We only print for HC
            printActions(agent.getActions());
            printInstrumentation(agent.getInstrumentation());
        }

        if (finalState) System.out.println ("Time to generate solution    " + time + " ms");
        System.out.println ("Solution benefit:            " + getBenefit());
        System.out.println ("Average distance to central: " + getAverageDistanceToCentrals());
        System.out.println ("Central ocupation distr.:    " + getOccupationDistribution() + " out of " + getCentralsNumber());
        System.out.println ("Nombre de clients assignats  " + numberOfAssignedClients() + " / " + getClientsNumber());
        System.out.println ("Valid state:                 " + isValidState());
        System.out.println();
    }

    private static void printActions(List actions) {
        for (int i = 0; i < actions.size(); i++) {
            String action = (String) actions.get(i);
            System.out.println(action);
        }
    }

    private static void printInstrumentation(Properties properties) {
        var keys = properties.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String property = properties.getProperty(key);
            System.out.println(key + " : " + property);
        }
    }

    public double getAverageDistanceToCentrals() {
        double sum = 0, count = 0;
        for (int i = 0; i < getClientsNumber(); ++i) {
            if (assignedClients[i] != -1) {
                sum += getDistance(i, assignedClients[i]);
                ++count;
            }
        }
        return sum/count;
    }

    private int numberOfAssignedClients() {
        int count = 0;
        for (int i : assignedClients) if (i != -1) ++count;
        return count;
    }

    /** 
        Returns a string in array looking format with 10 values each of them indicate the
        number of centrals within a range consumption usage. 
        F.e. 3rd value will indicate the number of centrals that are being used between 
        30% and 40% of its capacity
    */
    private String getOccupationDistribution() {
        //String[] occupation = new String[10];
        int[] count = new int[10];
        //System.err.println("Getting occupation dist:");
        for (int i = 0; i < getCentralsNumber(); ++i) {
            double production = getCentral(i).getProduccion();
            //System.err.print(leftPowerCentral[i] + " - " + production + " -> index: ");
            int index = Math.max((int)((leftPowerCentral[i]/production)*10)-1, 0);
            //System.err.println(index);
            count[index]++;
        }
        // for (int i = 0; i < 10; ++i) {
        //     occupation[i] = String.valueOf((double)count[i]/(double)getCentralsNumber());
        //     //System.err.println(occupation[i] + " - " + count[i]);
        // }
        int[] countReversed = new int[10];
        for (int i = 0; i <= 9; ++i) countReversed[i] = count[9-i];
        return Arrays.toString(countReversed);
    }

    // -------------------- Funcions redefinides auxiliars ---------------------

    public int getClientsNumber() {
        return clients.size();
    }

    public int getCentralsNumber() {
        return centrals.size();
    }

    /** Returns the benefit of the current state. Use getDynamicBenefit if possible. */
    public double getBenefit(){
        double benef = 0, costc = 0;
        for(int i = 0; i < leftPowerCentral.length; ++i){
            costc += costCentral(i);
        }
        for(int i = 0; i < assignedClients.length; ++i){
            benef += beneficiClient(i);
        }
        return benef-costc;
    }

    /** Returns the current state's benefit (it is autoupdated on each "move") */
    public double getDynamicBenefit() {
        return benefDynamic;
    }

    private double costCentral(int central){
        Central c = getCentral(central);
        double consumcentral = c.getProduccion();
        switch(c.getTipo()){
            case CENTRALA:
                if(centralInUse(central)) return consumcentral*50 + 20000;
                return 15000;
            case CENTRALB:
                if(centralInUse(central)) return consumcentral*80 + 10000;
                return 5000;
            case CENTRALC:
                if(centralInUse(central)) return consumcentral*150 + 5000;
                return 1500;
        }
        System.err.println("Error while getting cost central " + central);
        return 0;
    }

    private double beneficiClient(int client){
        Cliente c = getClient(client);
        double consumClient = c.getConsumo();
        switch(c.getTipo()){
            case CLIENTEXG:
                if(c.getContrato() == GARANTIZADO) {
                    if (assignedClients[client] != -1) return consumClient*400;
                    return -1000000;
                }
                if (assignedClients[client] != -1) return consumClient*300;
                return -consumClient*50;

            case CLIENTEMG:
                if(c.getContrato() == GARANTIZADO) {
                    if (assignedClients[client] != -1) return consumClient*500;
                    return -1000000;
                }
                if (assignedClients[client] != -1) return consumClient*400;
                return -consumClient*50;

            case CLIENTEG:
                if(c.getContrato() == GARANTIZADO) {
                    if (assignedClients[client] != -1) return consumClient*600;
                    return -1000000;
                }
                if (assignedClients[client] != -1) return consumClient*500;
                return -consumClient*50;
        }
        System.err.println("Error while getting income client " + client);
        return 0;
    }

    private Cliente getClient(int client) {
        return clients.get(client);
    }

    private Central getCentral(int central) {
        return centrals.get(central);
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
        //System.err.println("Produccio " + getCentral(central).getProduccion() + " power left " + leftPowerCentral[central] + " -- " + (getCentral(central).getProduccion() == leftPowerCentral[central]));
        return getCentral(central).getProduccion() != leftPowerCentral[central];
    }

    // Returns the real consumption of a client given a central
    private double getRealConsumption(Cliente client, Central central) {
        return getRealConsumtion(getDistance(client, central), client.getConsumo());
    }

    private double getRealConsumption(int client, int central) {
        return getRealConsumption(getClient(client), getCentral(central));
    }

    // Distance in km and consum
    private double getRealConsumtion(double distance, double consumption) {
        return consumption * powerLossCompensation(distance);
    }

    private boolean isValidState() {
        for (int i = 0; i < getClientsNumber(); ++i) {
            if (isGuaranteed(i) && assignedClients[i] == -1) return false;
        }
        for (int i = 0; i < getCentralsNumber(); ++i) {
            if (leftPowerCentral[i] < 0) return false;
        }
        return true;
    }

    private boolean canMove(int client, int central)
    {
        if (assignedClients[client] == central) return false;
        if (central == -1 && isGuaranteed(client)) return false;
        if (central == -1 || leftPowerCentral[central] < getRealConsumption(client, central)) return false;
        return true;
    }

    private boolean canSwap(int client1, int central1, int client2, int central2) {
        if (central1 == central2) return false; // Si que es pot pero es inútil
        if (central1 == -1 && isGuaranteed(client2)) return false;
        if (central2 == -1 && isGuaranteed(client1)) return false;

        if (central1 == -1) {
            //System.err.println("Central 1 = -1");
            return getRealConsumption(client2, central2) >= getRealConsumption(client1, central2);
        }
        if (central2 == -1) {
            // System.err.println("Central 2 = -1");
            return getRealConsumption(client1, central1) >= getRealConsumption(client2, central1);
        }

        return getRealConsumption(client1, central2) <= leftPowerCentral[central2] + getRealConsumption(client2, central2)
            && getRealConsumption(client2, central1) <= leftPowerCentral[central1] + getRealConsumption(client1, central1);
    }

    private void updateLeftPower(int central, double oldclientcons, double nouclientcons){
        leftPowerCentral[central] += oldclientcons - nouclientcons;
    }

    ///////////////////////////////////////////////////////
    public void mouClient(int client, int central){
        //System.err.println("Moving " + client + " to central " + central);
        if(canMove(client, central)){
            //System.err.println("Moved succesfuly!");
            //System.err.println("For client " + client + " was " + assignedClients[client] + " now is " + central);
            // System.err.println("Consum real client " + getRealConsumption(client, assignedClients[client]));
            // System.err.println("Consum abans antiga" + leftPowerCentral[assignedClients[client]]);

            int orCentral = assignedClients[client];
            double orClient = beneficiClient(client);

            Boolean centralWasInUse = centralInUse(central);
            double cost = costCentral(central);

            Boolean orCentralWasInUse = false;
            double orCost = 0;
            
            if (orCentral != -1)  {
                orCentralWasInUse = centralInUse(assignedClients[client]);
                orCost = costCentral(assignedClients[client]);
            }

            if (assignedClients[client] != -1) updateLeftPower(assignedClients[client], 0, -getRealConsumption(client, assignedClients[client]));
            //System.err.println("Consum despres antiga" + leftPowerCentral[assignedClients[client]]);
            assignedClients[client] = central;
            //System.err.println("Consum abans nova" + leftPowerCentral[assignedClients[client]]);
            updateLeftPower(central, 0, getRealConsumption(client, assignedClients[client]));
            
            //System.err.println("Benefici despres nova " + benefDynamic);
            
            if ((centralInUse(central) && !centralWasInUse) || (!centralInUse(central) && centralWasInUse)) {
                System.err.println("Change 1");
                benefDynamic += cost;
                benefDynamic -= costCentral(central);
            }
            if (orCentral != -1) {
                if ((centralInUse(orCentral) && !orCentralWasInUse) || (!centralInUse(orCentral) && orCentralWasInUse)) {
                    System.err.println("Change 2");
                    benefDynamic += orCost;
                    benefDynamic -= costCentral(orCentral);
                }
            } else if (orCentral == -1) {
                //System.err.println("Change 3");
                benefDynamic -= orClient;
                benefDynamic += beneficiClient(client);
            }

            /*
            double dynBen = benefDynamic;
            double nouBen = getBenefit();
            if (nouBen != dynBen) {
                System.err.println(client + " " + orCentral + " " + central);
                System.err.println("Benefici despres nova " + dynBen);
                System.err.println("Benefici despres nova " + nouBen);
                System.err.println();
            }
            */
            //System.err.println("Consum despres nova " + leftPowerCentral[assignedClients[client]]);
        }
    }
    
    public void swapClient(int client1, int client2){
        int central1 = assignedClients[client1], central2 = assignedClients[client2];
        if(canSwap(client1, central1, client2, central2)){
            //System.err.println("Can swap!");
            
            double oldC1 = beneficiClient(client1);
            double oldC2 = beneficiClient(client2);

            assignedClients[client1] = central2;
            if(central2 != -1) updateLeftPower(central2, getRealConsumption(client2, central2), getRealConsumption(client1, central2));
            assignedClients[client2] = central1;
            if(central1 != -1) updateLeftPower(central1, getRealConsumption(client1, central1), getRealConsumption(client2, central1));
        
            if (assignedClients[client1] != assignedClients[client2] && (assignedClients[client1] == -1 || assignedClients[client2] == -1) ) {
                benefDynamic -= oldC1;
                benefDynamic += beneficiClient(client1);
                benefDynamic -= oldC2;
                benefDynamic += beneficiClient(client2);
            }
        
        }   
    }

    public void resetCentral(int central){
        for (int i = 0; i < getClientsNumber(); ++i) {
            benefDynamic -= beneficiClient(i);
            assignedClients[i] = -1;
            benefDynamic += beneficiClient(i);
        }
    }
}
