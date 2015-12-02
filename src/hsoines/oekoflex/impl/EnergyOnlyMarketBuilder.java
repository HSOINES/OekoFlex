package hsoines.oekoflex;

import repast.simphony.context.Context;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.space.graph.Network;

public class EnergyOnlyMarketBuilder implements ContextBuilder<Object>{

	@Override
	public Context build(Context<Object> context) {
		context.setId("EnergyOnlyMarket");
		
        NetworkBuilder <Object > netBuilder = new NetworkBuilder<Object >("EOMnetwork", context , true);
        netBuilder.buildNetwork();
        Network<Object> net = (Network<Object>) context.getProjection("EOMnetwork");
       
        MarketOperator mo = new MarketOperator();
		
		for (int i = 0; i < 5; i++){
			SimpleEnergyProducer prod = new SimpleEnergyProducer();
			context.add(prod);
			net.addEdge(prod, mo);
		}
		
		return context;
	}

}
