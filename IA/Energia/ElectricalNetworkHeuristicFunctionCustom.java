package IA.Energia;

import aima.search.framework.HeuristicFunction;

public class ElectricalNetworkHeuristicFunctionCustom implements HeuristicFunction {

    public double getHeuristicValue(Object n){
        ElectricalNetworkState state = (ElectricalNetworkState) n;
        double d = state.getDynamicDistance();
        d *= d;
        double p = state.getDynamicPowerLeft();
        p *= p;
        double h = d - p;
        return h;
    }
}
