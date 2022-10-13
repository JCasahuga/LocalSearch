package IA.Energia;

import aima.search.framework.HeuristicFunction;

public class ElectricalNetworkHeuristicFunction implements HeuristicFunction {

    public double getHeuristicValue(Object n){
        double d = ((ElectricalNetworkState) n).getBenefit();
        //System.err.println("Heuristic result is : " + d);
        return d;
    }
}
