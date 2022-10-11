package IA.Energia;

public class Network {

    private int [] board;
    private static int [] solution;

    /* Constructor */
    public Network(int []init, int[] goal) {

        board = new int[init.length];
        solution = new int[init.length];

        for (int i = 0; i < init.length; i++) {
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
        return 0;
    }

     /* auxiliary functions */

     // Some functions will be needed for creating a copy of the state

    /* ^^^^^ TO COMPLETE ^^^^^ */

}
