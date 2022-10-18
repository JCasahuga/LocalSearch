package IA.Energia;

import aima.search.framework.HeuristicFunction;

public class ElectricalNetworkHeuristicFunctionCustom implements HeuristicFunction {

    public double getHeuristicValue(Object n){
        ElectricalNetworkState state = (ElectricalNetworkState) n;
        double h = state.getAverageDistanceToCentrals();
        //double h = ((ElectricalNetworkState) n).getAverageDistanceToCentrals();
        //System.err.println("Heuristic result is : " + h);
        return h;
    }
}
