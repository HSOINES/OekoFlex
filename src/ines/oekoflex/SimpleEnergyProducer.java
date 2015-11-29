package ines.oekoflex;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.space.projection.Projection;
import repast.simphony.util.ContextUtils;

public class SimpleEnergyProducer {
	
    @ScheduledMethod(start = 1, interval = 1)
    public void sendBid(){
        Context<Object> context = ContextUtils.getContext(this);
        
        Network net = (Network) context.getProjection("EOMnetwork");
        
        Iterable<RepastEdge<Object>> iter = net.getOutEdges(this);
        
        MarketOperator mo = (MarketOperator) iter.iterator().next().getTarget();
        
        mo.sendBid(1234);
    }
}
