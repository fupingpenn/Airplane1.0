package airplane.g2;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import airplane.sim.GameConfig;
import airplane.sim.Plane;
import airplane.sim.Player;

public class SelfPlayer extends Player{

	private int maxround=500;
	private Logger logger = Logger.getLogger(this.getClass()); // for logging
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Self-Player";
	}

	@Override
	public void startNewGame(ArrayList<Plane> planes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double[] updatePlanes(ArrayList<Plane> planes, int round, double[] bearings) {
		
		ArrayList<Plane> templist=new ArrayList<Plane>();
		for(int i=0;i<planes.size();i++){
			Plane p1=planes.get(i);
			if(round<p1.getDepartureTime()){
				continue;
			}else if(p1.getBearing()==-2){
				continue;
			}else if(p1.getBearing()==-1){
				int index=templist.size();
				double target=calculateBearing(p1.getLocation(), p1.getDestination());
				int minround=maxround;
				double bestbias=-1;
				for(int bias=0;bias<90;bias++){
					for(int factor=-1;factor<=1;factor+=2){
						double tbearing=target+bias*factor;
						tbearing=tbearing>=360?tbearing-360:tbearing;
						tbearing=tbearing<0?tbearing+360:tbearing;
						Plane np=new Plane(p1);
						np.setBearing(tbearing);
						if(templist.size()==index){
							templist.add(np);
						}else{
							templist.set(index, np);
						}
						int roundcount=simulatestrategy(templist,round);
						if(roundcount>0&&roundcount<minround){
							minround=roundcount;
							bestbias=tbearing;
						}
					}
					
				}
				Plane np=new Plane(p1);
				np.setBearing(bestbias);
				templist.set(index, np);
				bearings[i]=np.getBearing();
			}else{
				int index=templist.size();
				double standard=p1.getBearing();
				int minround=maxround;
				double bestbias=-1;
				for(int bias=0;bias<=10;bias++){
					for(int factor=-1;factor<=1;factor+=2){
						double tbearing=standard+bias*factor;
						tbearing=tbearing>=360?tbearing-360:tbearing;
						tbearing=tbearing<0?tbearing+360:tbearing;
						Plane np=new Plane(p1);
						np.setBearing(tbearing);
						if(templist.size()==index){
							templist.add(np);
						}else{
							templist.set(index, np);
						}
						int roundcount=simulatestrategy(templist,round);
						if(roundcount>0&&roundcount<minround){
							minround=roundcount;
							bestbias=tbearing;
						}
					}
					
				}
				Plane np=new Plane(p1);
				np.setBearing(bestbias);
				templist.set(index, np);
				bearings[i]=np.getBearing();
			}
		}
		
		StringBuffer message=new StringBuffer();
		for(int i=0;i<bearings.length;i++){
			message.append(bearings[i]+" ");
		}
		logger.error(message);
		
		return bearings;
	}
	
	
	public int startSimulation(ArrayList<Plane> planes, int round){
		//logger.error("haha, This is a testing player :)");
				continueSimulation = true;
		    	// make a copy of all the Planes (so the originals don't get affected)
		    	ArrayList<Plane> simPlanes = new ArrayList<Plane>();
		    	for (Plane p : planes) {
		    		simPlanes.add(new Plane(p));
		    	}
		    	// make an array of all the bearings
		    	double simBearings[] = new double[simPlanes.size()];
		    	for (int i = 0; i < simBearings.length; i++) {
		    		simBearings[i] = simPlanes.get(i).getBearing();
		    	}
		    	// count how many have landed
		    	int landed = 0;
		    	for (double b : simBearings) {
		    		if (b == -2) landed++;
		    	}
		    	
		    	// now loop through the simulation
		    	while(landed != simBearings.length && continueSimulation) {
		    		// update the round number
		    		round++;
		    		if(round>=maxround){
		    			return round;
		    		}
		    		// the player simulates the update of the planes
		    		
		    		// make sure no planes took off too early
					for (int i = 0; i < simPlanes.size(); i++) {
						if (simPlanes.get(i).getDepartureTime() > round && simBearings[i] > -1) {
							return -1 * round;
						}
					}
		    		// update the locations
		    		for (int i = 0; i < simPlanes.size(); i++) {
		    			Plane p = simPlanes.get(i);
		    			if (simBearings[i] >= 0) {
		    				if (p.move(simBearings[i])) {
			    				// see if it landed, i.e. it's within 0.5 of its destination
			    				if (p.getLocation().distance(p.getDestination()) < 0.5) {
			    					// the plane has landed
			    					p.setBearing(-2);
			    					simBearings[i] = -2;
			    					landed++;
			    				}
		    				}
		    				// if an error occurs
		    				else return -1 * round;
		    			}
		    		}
		    		// make sure the planes aren't too close to each other
					// make sure planes aren't too close to each other
					for(Plane l1 : simPlanes)
					{
						for(Plane l2: simPlanes)
						{
							if (!l1.equals(l2) && l1.getBearing() != -2 && l1.getBearing() != -1 && l2.getBearing() != -2 && l2.getBearing() != -1) 
							{
								if (l1.getLocation().distance(l2.getLocation()) < GameConfig.SAFETY_RADIUS)
									return -1 * round;
							}
						}
					}
					
					simBearings = simulateUpdate(simPlanes, round, simBearings);
		    		// if it's null, then don't bother
		    		if (simBearings == null) return -1 * round;

		    	}
		    
		    	return round;
	}
	
	@Override
	protected double[] simulateUpdate(ArrayList<Plane> planes, int round, double[] bearings) {
		// not implemented
		return updatePlanes(planes,round,bearings);
	}
	
	public int simulatestrategy(ArrayList<Plane> planes, int round){
		SelfPlayer sp=new SelfPlayer();
		return sp.startSimulation(planes, round);
	}

}
