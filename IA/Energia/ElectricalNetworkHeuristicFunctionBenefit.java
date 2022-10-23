package IA.Energia;

import aima.search.framework.HeuristicFunction;

public class ElectricalNetworkHeuristicFunctionBenefit implements HeuristicFunction {

    public double getHeuristicValue(Object n){
        double h = -((ElectricalNetworkState) n).getDynamicBenefit();
        return h;
    }
}
