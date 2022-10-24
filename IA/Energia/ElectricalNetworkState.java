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
    private double distanceDynamic = 0;
    private double powerLeftDynamic = 0;
    private double guaranteedNotAssigned = 0;
    private double totalGuaranteed = 0;

    Random rand = new Random();

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
        powerLeftDynamic = networkState.getDynamicPowerLeft();
        distanceDynamic = networkState.getDynamicDistance();
        guaranteedNotAssigned = networkState.getGuaranteedNotAssigned();
        totalGuaranteed = networkState.getTotalGuaranteed();
    }

    //  ---------------------- Initial states generation --------------
    // Permet seleccionar quina generació de soluciona inicial s'utilitza
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
        powerLeftDynamic = getTotalLeftPowerCentral();
    }

    // Generació de solució buida
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

    // Generació de solució per proximitat
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
            if (closest != -1)
                leftPowerCentral[closest] -= minConsumption;
            else
                if (isGuaranteed(i)) ++guaranteedNotAssigned;
        }
    }

    // Generació de solució totalment aleatoria només garantits
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
        }
    }

    // Generació de solució totalment aleatoria
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
        }
    }

    // Retorna el percentatge de compensació
    private double powerLossCompensation(double d) {
        if (d <= 10) return 1;
        if (d <= 25) return 1 / 0.9;
        if (d <= 50) return 1 / 0.8;
        if (d <= 75) return 1 / 0.6;
        return 1 / 0.4;
    }

    // Retorna la distancia per punt1 i punt2
    private double getDistance(int x1, int y1, int x2, int y2) {
        int distX = x1 - x2;
        int distY = y1 - y2;
        return Math.sqrt(distX*distX + distY*distY);
    }

    // Retorna la distancia per cl i ce
    private double getDistance(int cl, int ce) {
        if (ce == -1) return 150;
        return getDistance(getClient(cl), getCentral(ce));
    }

    // Retorna la distancia per cl i ce
    private double getDistance(Cliente cl, Central ce) {
        return getDistance(cl.getCoordX(), cl.getCoordY(), ce.getCoordX(), ce.getCoordY());
    }

    // ------------------------ Funcions auxiliars ---------------------
    // Retornen els clients
    public Clientes getClients(){
        return clients;
    }

    // Retorna les centrals
    public Centrales getCentrals(){
        return centrals;
    }

    // Retorna l'assignació de cada client
    public int[] getAssignedClients(){
        return assignedClients;
    }

    // Retorna l'array dels valors d'energia restant de cada central
    public double[] getLeftPowerCentral(){
        return leftPowerCentral;
    }

    // Retorna l'energia restant total
    public double getTotalLeftPowerCentral(){
        double c = 0;
        for (int i = 0; i < getCentralsNumber(); ++i) {
            if (centralInUse(i))
                c += leftPowerCentral[i];
        }
        return c;
    }

    // Fa print de l'estat inicial i final
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
        System.out.println ("Nombre de clients assignats  " + numberOfAssignedClients() + " / " + getClientsNumber());
        getOccupationDistribution();
        System.out.println ("Average distance to central: " + getAverageDistanceToCentrals());
        System.out.println ("Valid state:                 " + isValidState());
        System.out.println();
    }

    // Fa print de les accions per arribar aquella solució, activat només per HC
    private static void printActions(List actions) {
        for (int i = 0; i < actions.size(); i++) {
            String action = (String) actions.get(i);
            System.out.println(action);
        }
    }

    // Fa print dels parametres d'execucio
    private static void printInstrumentation(Properties properties) {
        var keys = properties.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String property = properties.getProperty(key);
            System.out.println(key + " : " + property);
        }
    }

    // Retorna la distancia mitjana entre clients i centrals
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

    // Retorna distancia entre clients i centrals
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

    /** 
        Imprimeix la distribució de cada un dels tipus de centrals per indicar el numero de
        centrals que es troben dins del rang de consum.
        Per exempl: El tercer valor indica que aquell numero centrals s'estan utilitzan entre el 30% i 40% de la seva capacitat
    */
    private String getOccupationDistribution() {
        int[] count = new int[10];
        int t = 0;
        double total = 0;
        for (int i = 0; i < getCentralsNumber(); ++i) {
            if (getCentral(i).getTipo() == CENTRALB) {
                double production = getCentral(i).getProduccion();
                int index = Math.max((int)((leftPowerCentral[i]/production)*10)-1, 0);
                total += (production-leftPowerCentral[i])/production;
                count[index]++;
                ++t;
            }
        }
        total *= 100;
        total /= t;
        int[] countReversed = new int[10];
        for (int i = 0; i <= 9; ++i) countReversed[i] = count[9-i];
        System.out.println("Centrals A " + Arrays.toString(countReversed) + " " + Math.round(total));
        count = new int[10];
        total = 0;
        t = 0;
        for (int i = 0; i < getCentralsNumber(); ++i) {
            if (getCentral(i).getTipo() == CENTRALB) {
                double production = getCentral(i).getProduccion();
                int index = Math.max((int)((leftPowerCentral[i]/production)*10)-1, 0);
                count[index]++;
                total += (production-leftPowerCentral[i])/production;
                ++t;
            }
        }
        total *= 100;
        total /= t;
        countReversed = new int[10];
        for (int i = 0; i <= 9; ++i) countReversed[i] = count[9-i];
        System.out.println("Centrals B " + Arrays.toString(countReversed) + " " + Math.round(total));
        count = new int[10];
        total = 0;
        t = 0;
        for (int i = 0; i < getCentralsNumber(); ++i) {
            if (getCentral(i).getTipo() == CENTRALC) {
                double production = getCentral(i).getProduccion();
                int index = Math.max((int)((leftPowerCentral[i]/production)*10)-1, 0);
                count[index]++;
                total += (production-leftPowerCentral[i])/production;
                ++t;
            }
        }
        total *= 100;
        total /= t;


        countReversed = new int[10];
        for (int i = 0; i <= 9; ++i) countReversed[i] = count[9-i];
        System.out.println("Centrals C " + Arrays.toString(countReversed) + " " + Math.round(total));
        countReversed = new int[10];
        for (int i = 0; i <= 9; ++i) countReversed[i] = count[9-i];
        return Arrays.toString(countReversed);
    }

    // Retorna el numero de clients assignats
    public int numberOfAssignedClients() {
        int count = 0;
        for (int i : assignedClients) if (i != -1) ++count;
        return count;
    }

    // Retorna el numero de clients
    public int getClientsNumber() {
        return clients.size();
    }

    // Retorna el numero de centrals
    public int getCentralsNumber() {
        return centrals.size();
    }

    // Retorna el benefici d'un estat, utilitzar benefici dynamic preferentment
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

    // Retorna el numero de clients garantits
    public double getTotalGuaranteed() {
        return totalGuaranteed;
    }

    // Retorna l'energiaDynamica
    public double getDynamicPowerLeft() {
        return powerLeftDynamic;
    }
    
    // Retorna el beneficiDynamic
    public double getDynamicBenefit() {
        return benefDynamic;
    }
    
    // Retorna el numero de clients garantitzats no assignats
    public double getGuaranteedNotAssigned() {
        return guaranteedNotAssigned;
    }

    // Retorna la dynamicDistance
    public double getDynamicDistance() {
        return distanceDynamic;
    }

    // Retorna el cost de la central i
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

    // Retorna el benefici del client i
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

    // Retorna l'objecte del client i
    private Cliente getClient(int client) {
        return clients.get(client);
    }

    // Retorna l'objecte de la central i
    private Central getCentral(int central) {
        return centrals.get(central);
    }

    // Retorna el tipus de contracte del client
    private int getContract(int client) {
        return getClient(client).getContrato();
    }

    // Retorna cert si el client és garantit
    private boolean isGuaranteed(int client) {
        return getContract(client) == GARANTIZADO;
    }

    // Retorna cert si la central té un client assignat
    public boolean centralInUse(int central) {
        return getCentral(central).getProduccion() != leftPowerCentral[central];
    }

    // Retorna l'energia que consumeix segons la compensació de distancia
    private double getRealConsumption(Cliente client, Central central) {
        return getRealConsumption(getDistance(client, central), client.getConsumo());
    }

    // Retorna l'energia que consumeix segons la compensació de distancia
    private double getRealConsumption(int client, int central) {
        return getRealConsumption(getClient(client), getCentral(central));
    }

    // Retorna l'energia que consumeix segons la compensació de distancia
    private double getRealConsumption(double distance, double consumption) {
        return consumption * powerLossCompensation(distance);
    }

    // Retorna cert si totes les centrals tenen energia restant >= 0 i els clients garantitzats estan assignats
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

    // Retorna el numero de centrals tipo A utilitzades
    public int numberOfACentralsUsed() {
        int count = 0;
        for (int i = 0; i < getCentralsNumber(); ++i) {
            if (centralInUse(i) && getCentral(i).getTipo() == CENTRALA) ++count;
        }
        return count;
    }

    // Retorna el numero de centrals tipo B utilitzades
    public int numberOfBCentralsUsed() {
        int count = 0;
        for (int i = 0; i < getCentralsNumber(); ++i) {
            if (centralInUse(i) && getCentral(i).getTipo() == CENTRALB) ++count;
        }
        return count;
    }

    // Retorna el numero de centrals tipo C utilitzades
    public int numberOfCCentralsUsed() {
        int count = 0;
        for (int i = 0; i < getCentralsNumber(); ++i) {
            if (centralInUse(i) && getCentral(i).getTipo() == CENTRALC) ++count;
        }
        return count;
    }
    
    // Verifica si l'operació de mou es possible
    public boolean canMove(int client, int central)
    {
        if (assignedClients[client] == central) return false;
        if (central == -1 && isGuaranteed(client)) return false;
        if (central == -1 || leftPowerCentral[central] < getRealConsumption(client, central)) return false;
        return true;
    }

    // Verifica si l'operació de swap es possible
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

    // Actualitza l'energia restant a la central amb els nous valors de consum
    private void updateLeftPower(int central, double oldclientcons, double nouclientcons){
        boolean wasInUse = centralInUse(central); 
        powerLeftDynamic -= leftPowerCentral[central];
        leftPowerCentral[central] += oldclientcons - nouclientcons;
        powerLeftDynamic += leftPowerCentral[central];
        if (!centralInUse(central) && wasInUse) powerLeftDynamic -= getCentral(central).getProduccion();
    }

    // Mou el client a la central
    public boolean mouClient(int client, int central) {
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
                if (orCentral == -1 && central != -1)
                    --guaranteedNotAssigned;
                if (orCentral != -1 && central == -1)
                    ++guaranteedNotAssigned;
            }

            return true;
    }
    
    // Retorna la central en que el client c esta assignat
    public int getCentralAssignedToClient(int c) {
        return assignedClients[c];
    }

    // Caniva l'assignació del client1 al client 2
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

    // Treu tots els clients que estiguin assignats a la central
    public boolean resetCentral(int central){
        for (int i = 0; i < getClientsNumber(); ++i) {
            if (assignedClients[i] == central) {
                benefDynamic -= beneficiClient(i);
                assignedClients[i] = -1;
                benefDynamic += beneficiClient(i);
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