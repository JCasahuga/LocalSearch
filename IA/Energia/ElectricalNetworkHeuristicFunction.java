package IA.Energia;

import aima.search.framework.HeuristicFunction;

public class ElectricalNetworkHeuristicFunction implements HeuristicFunction {

    public double getHeuristicValue(Object n){
        double h = -((ElectricalNetworkState) n).getBenefit();
        //double h = ((ElectricalNetworkState) n).getAverageDistanceToCentrals();
        //System.err.println("Heuristic result is : " + d);
        return h;
    }
}
