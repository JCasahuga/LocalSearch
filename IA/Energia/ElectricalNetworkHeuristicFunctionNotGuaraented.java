package IA.Energia;

import aima.search.framework.HeuristicFunction;

public class ElectricalNetworkHeuristicFunctionNotGuaraented implements HeuristicFunction {

    public double getHeuristicValue(Object n){
        ElectricalNetworkState state = (ElectricalNetworkState) n;
        double notG = state.getGuaranteedNotAssigned();
        notG *= 100000000;
        double d = state.getDynamicDistance();
        d *= d;
        double p = state.getDynamicPowerLeft();
        p *= p;
        double h = d - p + notG;
        return h;
    }
}
