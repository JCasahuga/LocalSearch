package IA.Energia;

import aima.search.framework.HeuristicFunction;

public class ElectricalNetworkHeuristicFunction implements HeuristicFunction {

    public double getHeuristicValue(Object n){
        return 0.1;
    }
}
