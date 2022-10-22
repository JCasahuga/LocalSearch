package IA.Energia;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.*;

import java.lang.Math;

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

    public int genMethod = 0;
    public int heuristic = 0;

    // Atributtes
    private Clientes clients;
    private Centrales centrals;

    private int[] assignedClients; 
    private double[] leftPowerCentral;
    private double benefDynamic = 0;
    private double assignedCDynamic = 0;
    private double distanceDynamic = 0;
    private double powerLeftDynamic = 0;
    private double guaranteedNotAssigned = 0;
    private double totalGuaranteed = 0;

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
        assignedCDynamic = networkState.getDynamicAssignedC();
        powerLeftDynamic = networkState.getDynamicPowerLeft();
        distanceDynamic = networkState.getDynamicDistance();
        guaranteedNotAssigned = networkState.getGuaranteedNotAssigned();
        totalGuaranteed = networkState.getTotalGuaranteed();
    }

    // public ElectricalNetworkState(Clientes clnts, Centrales ctrls, int[] assigClt, double[] lfPwCtrl) {
    //     clients = clnts;
    //     centrals = ctrls;

    //     assignedClients = assigClt;
    //     leftPowerCentral = lfPwCtrl;
    // }



    public double getDynamicPowerLeft() {
        return powerLeftDynamic;
    }

    //  ---------------------- Initial states generation --------------
    public void generateInitialSolution(int method)
    {
        assignedClients = new int[clients.size()];
        leftPowerCentral = new double[centrals.size()];
        genMethod = method;

        switch(method) {
            case 0:
                generateInitialSolutionClosestFull();
                break;
            case 1:
                generateInitialSolutionRandomGuaranteed(1);
                break;
            case 2:
                generateInitialSolutionRandomGuaranteed(getCentralsNumber()/2);
                break;
            case 3:
                generateInitialSolutionRandom(1);
                break;
            case 4:
                generateInitialSolutionRandom(getCentralsNumber()/2);
                break;
            case 5:
                generateEmpty();
                break;
        }
        benefDynamic = getBenefit();
        distanceDynamic = getDistanceToCentrals();
        assignedCDynamic = numberOfAssignedClients();
        powerLeftDynamic = getTotalLeftPowerCentral();
    }

    private void generateEmpty() {
        int tClients = getClientsNumber();
        int tCentrals = getCentralsNumber();

        for (int i = 0; i < tCentrals; ++i) {
            leftPowerCentral[i] = centrals.get(i).getProduccion();
        }
        for (int i = 0; i < tClients; ++i) {
            if (isGuaranteed(i)) ++guaranteedNotAssigned;
            assignedClients[i] = -1;
        }
        totalGuaranteed = guaranteedNotAssigned;
        System.out.println(totalGuaranteed);
    }

    private void generateInitialSolutionClosestFull() {
        int tClients = getClientsNumber();
        int tCentrals = getCentralsNumber();

        // Energia Centrals = Producció
        for (int i = 0; i < tCentrals; ++i) {
            leftPowerCentral[i] = centrals.get(i).getProduccion();
        }

        Vector<Integer> clientsOrdenats = new Vector<Integer>();
        
        for (int i = 0; i < tClients; ++i) {
            assignedClients[i] = -1;
            if (isGuaranteed(i)) {
                clientsOrdenats.add(i);
                ++totalGuaranteed;
            }
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
            } else {
                if (isGuaranteed(i)) ++guaranteedNotAssigned;
            }
            //System.out.println("Assigned client " + i + " to central " + closest);
        }
    }


    private void generateInitialSolutionRandomGuaranteed(int maxTotal) {
        int tClients = getClientsNumber();
        int tCentrals = getCentralsNumber();

        // Energia Centrals = Producció
        for (int i = 0; i < tCentrals; ++i) {
            leftPowerCentral[i] = centrals.get(i).getProduccion();
        }

        Vector<Integer> clientsOrdenats = new Vector<Integer>();
        
        for (int i = 0; i < tClients; ++i) {
            assignedClients[i] = -1;
            if (isGuaranteed(i)) {
                clientsOrdenats.add(i);
                ++totalGuaranteed;
            }
        }

        Integer[] array = new Integer[getCentralsNumber()];

        for (int i = 0; i < getCentralsNumber(); ++i) 
            array[i] = i;

        List<Integer> intList = Arrays.asList(array);

        Collections.shuffle(intList);
        
        // Assignació Clients
        for (Integer i : clientsOrdenats) {
            int pos = 0;
            int j = intList.get(pos);
            double consumption = getRealConsumption(i, j);
            Integer[] a = new Integer[maxTotal];
            int total = 0;
            ++pos;
            while (total < maxTotal && pos < getCentralsNumber()) {
                if (consumption <= leftPowerCentral[j]) {
                    a[total] = j;
                    ++total;
                }
                j = intList.get(pos);
                consumption = getRealConsumption(i, j);
                ++pos;
            }

            int closest = -1;
            double minDistance = 10000;
            for (int k = 0; k < total; ++k) {
                double d = getDistance(i, a[k]);
                if (d < minDistance) {
                    minDistance = d;
                    closest = a[k];
                }
            }
            if (closest != -1)  {
                assignedClients[i] = closest;
                leftPowerCentral[closest] -= getRealConsumption(i, closest);;
            } else {
                generateInitialSolutionRandomGuaranteed(++maxTotal);
            }

            Collections.shuffle(intList);
            //System.out.println("Assigned client " + i + " to central " + closest);
        }
    }


    private void generateInitialSolutionRandom(int maxTotal) {
        int tClients = getClientsNumber();
        int tCentrals = getCentralsNumber();

        // Energia Centrals = Producció
        for (int i = 0; i < tCentrals; ++i) {
            leftPowerCentral[i] = centrals.get(i).getProduccion();
        }

        Vector<Integer> clientsOrdenats = new Vector<Integer>();
        
        int totalGuaranteed = 0;

        for (int i = 0; i < tClients; ++i) {
            assignedClients[i] = -1;
            if (isGuaranteed(i)) {
                clientsOrdenats.add(i);
                ++totalGuaranteed;
            }
        }

        for (int i = 0; i < tClients; ++i) {
            if (!isGuaranteed(i))
                clientsOrdenats.add(i);
        }

        Integer[] array = new Integer[getCentralsNumber()];
        
        for (int i = 0; i < getCentralsNumber(); ++i) 
            array[i] = i;

        List<Integer> intList = Arrays.asList(array);

        Collections.shuffle(intList);
        
        // Assignació Clients
        for (int i = 0; i < clientsOrdenats.size(); ++i) {
            int index = clientsOrdenats.get(i);
            int pos = 0;
            int j = intList.get(pos);
            double consumption = getRealConsumption(index, j);
            Integer[] a = new Integer[maxTotal];
            int total = 0;
            ++pos;
            while (total < maxTotal && pos < getCentralsNumber()) {
                if (consumption <= leftPowerCentral[j]) {
                    a[total] = j;
                    ++total;
                }
                j = intList.get(pos);
                consumption = getRealConsumption(index, j);
                ++pos;
            }

            int closest = -1;
            double minDistance = 10000;
            for (int k = 0; k < total; ++k) {
                double d = getDistance(index, a[k]);
                if (d < minDistance) {
                    minDistance = d;
                    closest = a[k];
                }
            }
            if (closest != -1)  {
                assignedClients[index] = closest;
                leftPowerCentral[closest] -= getRealConsumption(index, closest);;
            } else {
                if (i < totalGuaranteed)
                    generateInitialSolutionRandom(++maxTotal);
            }

            Collections.shuffle(intList);
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

    private double getDistancePow(int x1, int y1, int x2, int y2) {
        int distX = x1 - x2;
        int distY = y1 - y2;
        return distX*distX + distY*distY;
    }
    
    private double getDistancePow(int cl, int ce) {
        return getDistancePow(getClient(cl), getCentral(ce));
    }

    private double getDistance(int cl, int ce) {
        if (ce == -1) return 150;
        return getDistance(getClient(cl), getCentral(ce));
    }

    private double getDistance(Cliente cl, Central ce) {
        return getDistance(cl.getCoordX(), cl.getCoordY(), ce.getCoordX(), ce.getCoordY());
    }

    private double getDistancePow(Cliente cl, Central ce) {
        return getDistancePow(cl.getCoordX(), cl.getCoordY(), ce.getCoordX(), ce.getCoordY());
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

    public double getTotalLeftPowerCentral(){
        double c = 0;
        for (int i = 0; i < getCentralsNumber(); ++i) {
            if (centralInUse(i))
                c += leftPowerCentral[i];
        }
        return c;
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
        if (count == 0) return 1000000;
        return sum/count;
    }

    public double getDistanceToCentrals() {
        double sum = 0, count = 0;
        for (int i = 0; i < getClientsNumber(); ++i) {
            if (assignedClients[i] != -1) {
                sum += getDistance(i, assignedClients[i]);
                ++count;
            } else {
                sum += 150;
            }
        }
        if (count == 0) return 1000000;
        return sum;
    }

    public double getPowAverageDistanceToCentrals() {
        double sum = 0, count = 0;
        for (int i = 0; i < getClientsNumber(); ++i) {
            if (assignedClients[i] != -1) {
                sum += getDistancePow(i, assignedClients[i]);
                ++count;
            }
        }
        return sum/count;
    }


    public int numberOfAssignedClients() {
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

    Random rand = new Random();
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

    public double getTotalGuaranteed() {
        return totalGuaranteed;
    }

    /** Returns the current state's benefit (it is autoupdated on each "move") */
    public double getDynamicBenefit() {
        return benefDynamic;
    }

    public double getDynamicAssignedC() {
        return assignedCDynamic;
    }

    public double getGuaranteedNotAssigned() {
        return guaranteedNotAssigned;
    }

    public double getDynamicDistance() {
        return distanceDynamic;
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
                    return -10000;
                }
                if (assignedClients[client] != -1) return consumClient*300;
                return -consumClient*50;

            case CLIENTEMG:
                if(c.getContrato() == GARANTIZADO) {
                    if (assignedClients[client] != -1) return consumClient*500;
                    return -10000;
                }
                if (assignedClients[client] != -1) return consumClient*400;
                return -consumClient*50;

            case CLIENTEG:
                if(c.getContrato() == GARANTIZADO) {
                    if (assignedClients[client] != -1) return consumClient*600;
                    return -10000;
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

    private int getContract(int client) {
        return getClient(client).getContrato();
    }

    private boolean isGuaranteed(int client) {
        return getContract(client) == GARANTIZADO;
    }

    public boolean centralInUse(int central) {
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
            if (isGuaranteed(i) && assignedClients[i] == -1) {
                System.out.println("Client Sense Assignar " + guaranteedNotAssigned);
                return false;
            }
        }
        for (int i = 0; i < getCentralsNumber(); ++i) {
            if (leftPowerCentral[i] < 0) {
                System.out.println("Central sense energia!");
                return false;
            }
        }
        return true;
    }

    public int numberOfXGClients() {
        int count = 0;
        for (Cliente c : clients) {
            if (c.getTipo() == CLIENTEXG) ++count;
        }
        return count;
    }

    public int numberOfXGClientsUsed() {
        int count = 0;
        for (int i = 0; i < getClientsNumber(); ++i) {
            if (clients.get(i).getTipo() == CLIENTEXG && assignedClients[i] != -1) ++count;
        }
        return count;
    }

    public int numberOfMGClients() {
        int count = 0;
        for (Cliente c : clients) {
            if (c.getTipo() == CLIENTEMG) ++count;
        }
        return count;
    }

    public int numberOfMGlientsUsed() {
        int count = 0;
        for (int i = 0; i < getClientsNumber(); ++i) {
            if (clients.get(i).getTipo() == CLIENTEMG && assignedClients[i] != -1) ++count;
        }
        return count;
    }

    public int numberOfGClients() {
        int count = 0;
        for (Cliente c : clients) {
            if (c.getTipo() == CLIENTEG) ++count;
        }
        return count;
    }

    public int numberOfGClientsUsed() {
        int count = 0;
        for (int i = 0; i < getClientsNumber(); ++i) {
            if (clients.get(i).getTipo() == CLIENTEG && assignedClients[i] != -1) ++count;
        }
        return count;
    }

    public int numberOfACentralsUsed() {
        int count = 0;
        for (int i = 0; i < getCentralsNumber(); ++i) {
            if (centralInUse(i) && getCentral(i).getTipo() == CENTRALA) ++count;
        }
        return count;
    }

    public int numberOfBCentralsUsed() {
        int count = 0;
        for (int i = 0; i < getCentralsNumber(); ++i) {
            if (centralInUse(i) && getCentral(i).getTipo() == CENTRALB) ++count;
        }
        return count;
    }

    public int numberOfCCentralsUsed() {
        int count = 0;
        for (int i = 0; i < getCentralsNumber(); ++i) {
            if (centralInUse(i) && getCentral(i).getTipo() == CENTRALC) ++count;
        }
        return count;
    }

    public boolean canMove(int client, int central)
    {
        if (assignedClients[client] == central) return false;
        if (central == -1 && isGuaranteed(client)) return false;
        if (central == -1 || leftPowerCentral[central] < getRealConsumption(client, central)) return false;
        return true;
    }

    public boolean canSwap(int client1, int central1, int client2, int central2) {
        if (central1 == central2) return false;
        if (central1 == -1 && isGuaranteed(client2)) return false;
        if (central2 == -1 && isGuaranteed(client1)) return false;

        if (central1 == -1)
            return getRealConsumption(client2, central2) >= getRealConsumption(client1, central2);

        if (central2 == -1)
            return getRealConsumption(client1, central1) >= getRealConsumption(client2, central1);

        return getRealConsumption(client1, central2) <= leftPowerCentral[central2] + getRealConsumption(client2, central2)
            && getRealConsumption(client2, central1) <= leftPowerCentral[central1] + getRealConsumption(client1, central1);
    }

    private void updateLeftPower(int central, double oldclientcons, double nouclientcons){
        boolean wasInUse = centralInUse(central); 
        powerLeftDynamic -= leftPowerCentral[central];
        leftPowerCentral[central] += oldclientcons - nouclientcons;
        powerLeftDynamic += leftPowerCentral[central];
        if (!centralInUse(central) && wasInUse) powerLeftDynamic -= getCentral(central).getProduccion();
    }

    ///////////////////////////////////////////////////////
    public boolean mouClient(int client, int central) {
        //System.err.println("Moving " + client + " to central " + central);
            int orCentral = assignedClients[client];
            double orClient = beneficiClient(client);

            distanceDynamic -= getDistance(client, assignedClients[client]);
            distanceDynamic += getDistance(client, central);

            if (assignedClients[client] != -1)
                updateLeftPower(assignedClients[client], 0, -getRealConsumption(client, assignedClients[client]));
            
            assignedClients[client] = central;
            updateLeftPower(central, 0, getRealConsumption(client, assignedClients[client]));
            
            benefDynamic -= orClient;
            benefDynamic += beneficiClient(client);

            if (isGuaranteed(client)) {
                if (orCentral == -1 && central != -1) {
                    --guaranteedNotAssigned;
                }
                if (orCentral != -1 && central == -1) {
                    ++guaranteedNotAssigned;
                } 
            }

            if (central != -1 && orCentral == -1) ++assignedCDynamic;
            if (central == -1 && orCentral != -1) --assignedCDynamic;

            return true;
    }
    
    public int getCentralAssignedToClient(int c) {
        return assignedClients[c];
    }

    public boolean swapClient(int client1, int client2){
        int central1 = assignedClients[client1], central2 = assignedClients[client2];
            double oldC1 = beneficiClient(client1);
            double oldC2 = beneficiClient(client2);

            assignedClients[client1] = central2;

            distanceDynamic -= getDistance(client1, central1);
            distanceDynamic += getDistance(client1, central2);
            distanceDynamic -= getDistance(client2, central2);
            distanceDynamic += getDistance(client2, central1);

            if(central2 != -1)
                updateLeftPower(central2, getRealConsumption(client2, central2), getRealConsumption(client1, central2));
                
            assignedClients[client2] = central1;

            if(central1 != -1)
                updateLeftPower(central1, getRealConsumption(client1, central1), getRealConsumption(client2, central1));
        
            benefDynamic -= oldC1;
            benefDynamic += beneficiClient(client1);
            benefDynamic -= oldC2;
            benefDynamic += beneficiClient(client2);

            if (isGuaranteed(client1)) {
                if (central1 == -1 && central2 != -1) {
                    --guaranteedNotAssigned;
                }
                if (central1 != -1 && central2 == -1) {
                    ++guaranteedNotAssigned;
                }
            }

            if (isGuaranteed(client2)) {
                if (central2 == -1 && central1 != -1) {
                    --guaranteedNotAssigned;
                }
                if (central2 != -1 && central1 == -1) {
                    ++guaranteedNotAssigned;
                }
            }

            return true;
    }

    public boolean resetCentral(int central){
        for (int i = 0; i < getClientsNumber(); ++i) {
            if (assignedClients[i] == central) {
                benefDynamic -= beneficiClient(i);
                assignedClients[i] = -1;
                benefDynamic += beneficiClient(i);
                --assignedCDynamic;
                powerLeftDynamic -= leftPowerCentral[central];
                distanceDynamic -= getDistance(i, central);
                distanceDynamic += getDistance(i, -1);
                leftPowerCentral[central] = 0;

                if (isGuaranteed(i)) ++guaranteedNotAssigned;
            }
        }
        return true;
    }
}
