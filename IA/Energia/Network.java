package IA.Energia;

public class Network {
    /* Class independent from AIMA classes
       - It has to implement the state of the problem and its operators
     *

    /* State data structure
        vector with the parity of the coins (we can assume 0 = heads, 1 = tails
     */

    private int [] board;
    private static int [] solution;

    /* Constructor */
    public ProbIA5Board(int []init, int[] goal) {

        board = new int[init.length];
        solution = new int[init.length];

        for (int i = 0; i< init.length; i++) {
            board[i] = init[i];
            solution[i] = goal[i];
        }

    }

    /* vvvvv TO COMPLETE vvvvv */
    public void mouClient(int i, int j){
        // mou client i a central j
                
    }
    
    public void swapClient(int i, int j){
        // swap client i amb client j
                
    }

    /* Heuristic function */
    public double heuristic(){
        // compute the number of coins out of place respect to solution
        return 0;
    }

     /* auxiliary functions */

     // Some functions will be needed for creating a copy of the state

    /* ^^^^^ TO COMPLETE ^^^^^ */

}
