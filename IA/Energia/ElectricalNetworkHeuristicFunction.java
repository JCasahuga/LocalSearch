package IA.Energia;

import aima.search.framework.HeuristicFunction;

public class ElectricalNetworkHeuristicFunction implements HeuristicFunction {

    public double getHeuristicValue(Object n){
        double h = -((ElectricalNetworkState) n).getDynamicBenefit();
        //double h = ((ElectricalNetworkState) n).getAverageDistanceToCentrals();
        //System.err.println("Heuristic result is : " + h);
        return h;
    }
}
