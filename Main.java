
import IA.Energia.*;
import aima.search.framework.GraphSearch;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.AStarSearch;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.IterativeDeepeningAStarSearch;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.Vector;


public class Main {

    static private int[] numberOfCentrals = new int[]{20, 20, 10};          // Centrals type A, B, C (?)
    static private int numberOfClients = 500;                               // Number of clients
    static private double[] typeOfClients = new double[]{20, 20, 10};       // Client type XG, MG, G (?)
    static private double propGuaranteed = 0.8;                             // % of clients with guaranteed supply
    static private int algorithm = 0;                                       // (0) - Hill Climbling, (1) - Simulated Annealing
    static private int generationMethod = 0;                                // (0) - , (1) - , (2) - ...
    static private int heuristic = 0;                                       // (0) - ,...
    static private int seed = 1234;                                         // default: 1234

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception{

        System.out.println("Type 'cmds' to see the commands available");

        Scanner sc = new Scanner(System.in);
        boolean executed = false;
    	
    	while (!executed) {
    		while (!sc.hasNext());
    		String cmd = sc.next(); 
    		cmd.trim(); cmd.toLowerCase();
    		
    		switch (cmd) {
    			case "run":
                    System.out.println("Not implemented");
                    Centrales centrals = new Centrales(numberOfCentrals, seed);
                    Clientes clients = new Clientes(numberOfClients, typeOfClients, propGuaranteed, seed);
                    // TODO: Executar segons params
					ElectricalNetworkState networkState  = new ElectricalNetworkState(clients, centrals);
					networkState.generateInitialSolution(generationMethod);
					run(networkState, algorithm, heuristic);

    				break;
    				
    			case "ncentrals":
    				numberOfCentrals = new int[]{sc.nextInt(), sc.nextInt(), sc.nextInt()};
    				break;
    				
    			case "nclients":
    				numberOfClients = sc.nextInt();
    				break;
    				
    			case "algo":
                    algorithm = sc.nextInt();
    				break;
    				
    			case "genmethod":
    				generationMethod = sc.nextInt();
    				break;
    
                case "typeclient":
                    double[] ctypes = new double[]{sc.nextDouble(), sc.nextDouble(), sc.nextDouble()};
                    if (ctypes.length == 3 && Arrays.stream(ctypes).sum() == 1) {
                        typeOfClients = ctypes;
                    }
                    else System.out.println("Error while setting the type of client, make sure that you put 3 numbers that add 1");
                    break;

                case "gclient":
                    double prop = sc.nextDouble();
                    if (0 <= prop && prop <= 1) propGuaranteed = prop;
                    else System.out.println("Error while setting the guaranteed clients, make sure that your input is between 0 and 1");
                    break;

    			case "heur":
                    heuristic = sc.nextInt();
    				break;
    				
    			case "seed":
    				seed = sc.nextInt();
    				break;
    				
    			case "print":
                    printOptions();
    				break;
    				
    			case "cmds":
                    printCommands();
    				break;
    				
    			default:
    				System.out.println ("please enter valid option");
    				printCommands();
    				break;
            }
        }
    }

    private static void printOptions() {
        System.out.println("Current Values: ");
		System.out.println("================");
		
		System.out.println("Number of centrals: " + numberOfCentrals);
		System.out.println("Number of clients: " + numberOfClients);
		
		if      (algorithm == 0) System.out.println("Search algorithm: hill climbing");
		else if (algorithm == 1) System.out.println("Search algorithm: simulated annealing");
        else                     System.out.println("ERROR: No search algorithm");
		
		if      (heuristic == 0) System.out.println("heuristica: 1");
		else if (heuristic == 1) System.out.println("heuristica: 2");
        else                     System.out.println("ERROR: No heuristic");
		
		if      (generationMethod == 0) System.out.println("Generation method: 1");
		else if (generationMethod == 1) System.out.println("Generation method: 1");
        else                     System.out.println("ERROR: No generation method");
		
		System.out.println("Seed: " + seed);
        System.out.println("================");
	}
    
    private static void printCommands() {
        System.out.println ("Commands: ");
        System.out.println ("================");
		System.out.println ("run                      --  execute search");
		System.out.println ("ncentrals <A> <B> <C>    -- change number of centrals");
		System.out.println ("nclients <N>             -- change number of clients");
        System.out.println ("typeclient <XG> <MG> <G> -- change client types");
        System.out.println ("gclient <N>              -- change % of clients with guaranteed supply ");
		System.out.println ("algo <I>                 -- change search algorithm ([0]- Hill climbing, [1]- Simulated annealing)");
		System.out.println ("genmethod <I>            -- change generation method ([0]- , [1]- )");
		System.out.println ("heur <I>                 -- change heuristic ([0]- , [1]- )");
		System.out.println ("seed <I>                 -- change the seed");
		System.out.println ("print                    -- see current values");
		System.out.println ("cmds                     -- see commands");
		System.out.println ("================");
    }

    // -------------------- Run model methods ---------------------------

    private static boolean run(ElectricalNetworkState networkstate, int algorithm, int heuristic) {
        boolean error = false;
        
        if      (algorithm == 0 &&  heuristic == 0)	error = ElectricalNetwork_HillClimbing_Benefici(networkstate);
    	else if (algorithm == 1 &&  heuristic == 0)	error = ElectricalNetwork_SimulatedAnnealing_Benefici(networkstate);

        return error;
    }

	private static boolean ElectricalNetwork_HillClimbing_Benefici(ElectricalNetworkState networkState) {
        System.out.println ("Solution using Hill Climbing + Benefici: ");
		try {
			networkState.printState(false, 0);
			long time = System.currentTimeMillis();
			
			Problem problem = new Problem (networkState, new ElectricalNetworkSuccesorFunction(), new ElectricalNetworkGoalTest(), new ElectricalNetworkHeuristicFunction());
			Search search = new HillClimbingSearch();
			SearchAgent agent = new SearchAgent (problem, search);
			
			networkState = (ElectricalNetworkState) search.getGoalState();
			time = System.currentTimeMillis() - time;
			
			networkState.printState(true, time);
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    }

    private static boolean ElectricalNetwork_SimulatedAnnealing_Benefici(ElectricalNetworkState networkState) {return false;}

}
