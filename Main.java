
import IA.Energia.*;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;
import aima.search.informed.IterativeDeepeningAStarSearch;

import java.util.Arrays;
import java.util.Scanner;


public class Main {

    static private int[] numberOfCentrals = new int[]{5, 10, 25};           // Centrals type A, B, C (?)
    static private int numberOfClients = 1000;                              // Number of clients
    static private double[] typeOfClients = new double[]{0.25, 0.3, 0.45};  // Client type XG, MG, G (?)
    static private double propGuaranteed = 0.75;                            // % of clients with guaranteed supply
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
                    Centrales centrals = new Centrales(numberOfCentrals, seed);
                    Clientes clients = new Clientes(numberOfClients, typeOfClients, propGuaranteed, seed);
                    // TODO: Executar segons params
					ElectricalNetworkState networkState  = new ElectricalNetworkState(clients, centrals);
					networkState.generateInitialSolution(generationMethod);
					run(networkState, algorithm, heuristic);
					System.out.println("----------- Finished ----------");
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
		
		System.out.println("Number of centrals: " + Arrays.toString(numberOfCentrals));
		System.out.println("Number of clients: " + numberOfClients);
		
		if      (algorithm == 0) System.out.println("Search algorithm: hill climbing");
		else if (algorithm == 1) System.out.println("Search algorithm: simulated annealing");
        else                     System.out.println("ERROR: No search algorithm");
		
		if      (heuristic == 0) System.out.println("heuristica: Benefit");
		else if (heuristic == 1) System.out.println("heuristica: Custom");
        else                     System.out.println("ERROR: No heuristic");
		
		if      (generationMethod == 0) System.out.println("Generation method: [0] Everyone to closest");
		else if (generationMethod == 1) System.out.println("Generation method: [1] Random amb garantits (1)");
		else if (generationMethod == 2) System.out.println("Generation method: [2] Random amb garantits (centrals/2)");
		else if (generationMethod == 3) System.out.println("Generation method: [3] Random (1)");
		else if (generationMethod == 4) System.out.println("Generation method: [4] Random (centrals/2)");
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
		System.out.println ("genmethod <I>            -- change generation method ([0]- Closest, [1]- )");
		System.out.println ("heur <I>                 -- change heuristic ([0]- Benefici, [1]- Custom)");
		System.out.println ("seed <I>                 -- change the seed");
		System.out.println ("print                    -- see current values");
		System.out.println ("cmds                     -- see commands");
		System.out.println ("================");
    }

    // -------------------- Run model methods ---------------------------

    private static boolean run(ElectricalNetworkState networkstate, int algorithm, int heuristic) {
        boolean error = false;
        
        if      (algorithm == 0)	error = ElectricalNetwork_HillClimbing(networkstate);
    	else if (algorithm == 1)	error = ElectricalNetwork_SimulatedAnnealing(networkstate);

        return error;
    }

	private static boolean ElectricalNetwork_HillClimbing(ElectricalNetworkState networkState) {
        if 		(heuristic == 0) System.out.println ("Solution using Hill Climbing + Benefici: ");
		else if (heuristic == 1) System.out.println ("Solution using Hill Climbing + Custom: ");
		try {
			networkState.printState(false, 0, false, null, algorithm);
			long time = System.currentTimeMillis();
			
			Problem problem;
			if 		(heuristic == 0) problem = new Problem (networkState, new ElectricalNetworkSuccesorFunctionHillClimbing(), new ElectricalNetworkGoalTest(), new ElectricalNetworkHeuristicFunctionBenefit());
			else    				 problem = new Problem (networkState, new ElectricalNetworkSuccesorFunctionHillClimbing(), new ElectricalNetworkGoalTest(), new ElectricalNetworkHeuristicFunctionCustom());
			Search search = new HillClimbingSearch();
			SearchAgent agent = new SearchAgent (problem, search);
			
			networkState = (ElectricalNetworkState) search.getGoalState();
			time = System.currentTimeMillis() - time;
			
			networkState.printState(true, time, true, agent, algorithm);
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    }

    private static boolean ElectricalNetwork_SimulatedAnnealing(ElectricalNetworkState networkState) {
		if 		(heuristic == 0) System.out.println ("Solution using Simulated Annealing + Benefici: ");
		else if (heuristic == 1) System.out.println ("Solution using Simulated Annealing + Custom: ");
		try {
			networkState.printState(false, 0, false, null, algorithm);
			long time = System.currentTimeMillis();
			
			Problem problem;
			if 		(heuristic == 0) problem = new Problem (networkState, new ElectricalNetworkSuccesorSimulatedAnnealing(), new ElectricalNetworkGoalTest(), new ElectricalNetworkHeuristicFunctionBenefit());
			else    				 problem = new Problem (networkState, new ElectricalNetworkSuccesorSimulatedAnnealing(), new ElectricalNetworkGoalTest(), new ElectricalNetworkHeuristicFunctionCustom());
			Search search = new SimulatedAnnealingSearch();
			SearchAgent agent = new SearchAgent (problem, search);
			
			networkState = (ElectricalNetworkState) search.getGoalState();
			time = System.currentTimeMillis() - time;
			
			networkState.printState(true, time, true, agent, algorithm);
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
