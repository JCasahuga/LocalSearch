package IA.Energia;

import aima.search.framework.HeuristicFunction;

public class ElectricalNetworkHeuristicFunction implements HeuristicFunction {

    public double getHeuristicValue(Object n){
        double h = -((ElectricalNetworkState) n).getDynamicBenefit();
        /*if (((ElectricalNetworkState) n).getBenefit() != -h) {
            System.err.println("Not equal");
            System.err.println();
        }*/
        //double h = ((ElectricalNetworkState) n).getAverageDistanceToCentrals();
        //System.err.println("Heuristic result is : " + h);
        return h;
    }
}
