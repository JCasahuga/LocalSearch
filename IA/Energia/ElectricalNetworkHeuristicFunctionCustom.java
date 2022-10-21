package IA.Energia;

import aima.search.framework.HeuristicFunction;

public class ElectricalNetworkHeuristicFunctionCustom implements HeuristicFunction {

    public double getHeuristicValue(Object n){
        ElectricalNetworkState state = (ElectricalNetworkState) n;
        
        //double h = 0;

        //double b = state.getDynamicBenefit();

        double d = state.getDynamicDistance();
        d *= d;
        // //double h = d * d;
        double p = state.getDynamicPowerLeft();
        p *= p;
        // double a = state.numberOfAssignedClients();
        //double a = state.getDynamicAssignedC();
        //if (a !=  state.numberOfAssignedClients()) System.out.println(a + " " + state.numberOfAssignedClients());
        //a *= a;
        // double h = d - b - a;
        //double h = d - b ;
        double h = d - p;
        // System.err.println("Potencia " + p);
        // System.err.println("Distance " + d);
        // System.err.println("Persones " + a);
        // System.err.println("Benefici " + b);
        // System.err.println();

        //h -= state.numberOfGClientsUsed() * 4;
        //h -= state.numberOfMGlientsUsed() * 2;
        //h -= state.numberOfXGClientsUsed();

        //h += state.numberOfACentralsUsed() * 100.0;
        //h += state.numberOfBCentralsUsed() * 200.0;
        //h += state.numberOfCCentralsUsed() * 300.0;

        //h -= a;

        //double h = ((ElectricalNetworkState) n).getAverageDistanceToCentrals();
        //System.err.println("Heuristic result is : " + h);
        return h;
    }
}
