package airplane.g2;

import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.log4j.Logger;

import airplane.sim.GameConfig;
import airplane.sim.Plane;
import airplane.sim.Player;

public class SillyPlayer extends Player{
	private Logger logger = Logger.getLogger(this.getClass()); // for logging
	//private int starttime[]=null;
	private ArrayList<Integer> starttime;
	
	@Override
	public String getName() {
		return "Silly Player";
	}
	

	@Override
	public void startNewGame(ArrayList<Plane> planes) {
		//starttime=new int[planes.size()];
		/*Hashtable<Double,Integer> record=new Hashtable<Double,Integer>();
		for(int i=0;i<planes.size();i++){
			Plane p=planes.get(i);
			double dest=p.getDestination().getX()*101+p.getDestination().getY();
			if(!record.containsKey(dest)){
				record.put(dest, p.getDepartureTime());
				starttime[i]=p.getDepartureTime();
			}else{
				int next=max(record.get(dest),p.getDepartureTime());
				next+=5;
				record.put(dest, next);
				starttime[i]=next;
			}
		}*/
	}
	
	public int max(int a , int b){
		return a>b?a:b;
	}
	
	@Override
	protected double[] simulateUpdate(ArrayList<Plane> planes, int round, double[] bearings) {
		// not implemented
		return updatePlanes(planes,round,bearings);
	}
	
	public int  startSimulation(ArrayList<Plane> planes, ArrayList<Integer> starttime, int maxround ){
		this.starttime=starttime;
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
    	int round=0;
    	// now loop through the simulation
    	while(landed != simBearings.length && continueSimulation) {
    		// update the round number
    		round++;
    		if(round>=maxround){
    			return -1;
    		}
    		// the player simulates the update of the planes
    		simBearings = simulateUpdate(simPlanes, round, simBearings);
    		// if it's null, then don't bother
    		if (simBearings == null) return -1 * round;
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

    	}
    
    	return round;
	}
	
	@Override
	public double[] updatePlanes(ArrayList<Plane> planes, int round, double[] bearings) {
		ArrayList<Plane> templist=new ArrayList<Plane>();
		for(int i=0;i<planes.size();i++){
			//iterate each plane;
			Plane p1=planes.get(i);
			if(round<starttime.get(i)){
				//it's not ready to take off;
				continue;
			}else if(p1.getBearing()==-2){
				//it have finished the trip
				continue;
			}else if(p1.getBearing()==-1){
				//it's time to take off;
				int index=templist.size();
				double target=calculateBearing(p1.getLocation(), p1.getDestination());
				for(int bias=0;bias<90;bias++){
					Plane np=new Plane(p1);
					double tbearing=target+bias;
					tbearing=tbearing>=360?tbearing-360:tbearing;
					tbearing=tbearing<0?tbearing+360:tbearing;
					np.setBearing(tbearing);
					if(templist.size()==index){
						templist.add(np);
					}else{
						templist.set(index, np);
					}
					if(!willCollision(templist,round,100)){
						bearings[i]=tbearing;
						break;
					}else if(bias!=0){
						tbearing=target-bias;
						tbearing=tbearing<0?tbearing+360:tbearing;
						tbearing=tbearing>=360?tbearing-360:tbearing;
						np.setBearing(tbearing);
						if(templist.size()==index){
							templist.add(np);
						}else{
							templist.set(index, np);
						}
						if(!willCollision(templist,round,100)){
							bearings[i]=tbearing;
							break;
						}
					}
				}
				
			}else{
				//I'm already flying
				int index=templist.size();
				double standard=p1.getBearing();
				//boolean collisions[]=new boolean[21];
				double bestbear=0;
				double mindiff=1000;
				boolean find =false;
				double target=calculateBearing(p1.getLocation(), p1.getDestination());
				for(int bias=0;bias<21;bias++){
					Plane np=new Plane(p1);
					double tbearing=standard+bias-10;
					tbearing=tbearing<0?tbearing+360:tbearing;
					tbearing=tbearing>=360?tbearing-360:tbearing;
					
					np.setBearing(tbearing);
					if(templist.size()==index){
						templist.add(np);
					}else{
						templist.set(index, np);
					}
					if(!willCollision(templist,round,100)){
							//collisions[bias]=true;
						find=true;
						if(onestep(p1,tbearing)<mindiff){
							bestbear=tbearing;
							mindiff=onestep(p1,tbearing);
						}
					}
				}
				
				/*if(!find){
					mindiff=1000;
					for(int bias=0;bias<21;bias++){
						Plane np=new Plane(p1);
						double tbearing=standard+bias-10;
						tbearing=tbearing<0?tbearing+360:tbearing;
						tbearing=tbearing>=360?tbearing-360:tbearing;
					
						np.setBearing(tbearing);
						if(templist.size()==index){
							templist.add(np);
						}else{
							templist.set(index, np);
						}
						int neground=getcount(templist,round,25);
						if(neground<mindiff){
							bestbear=tbearing;
							mindiff=neground;
						}
					}
					
				}*/
				if(!find){
					bestbear=p1.getBearing()+10;
					logger.error("make turn");
				}
				bestbear=bestbear>360?bestbear-360:bestbear;
				bestbear=bestbear<0?bestbear+360:bestbear;
				Plane np=new Plane(p1);
				np.setBearing(bestbear);
				templist.set(index, np);
				bearings[i]=bestbear;
			}
		}
		StringBuffer message=new StringBuffer();
		for(int j=0;j<bearings.length;j++){
			message.append(bearings[j]+" ");
		}
		//logger.error(message.toString());
		return bearings;
	}
	
	
	
	public double onestep(Plane p, double tbearing){
		Plane temp=new Plane(p);
		temp.move(tbearing);
		double newtarget=calculateBearing(temp.getLocation(),temp.getDestination());
		return caldifference(newtarget,tbearing);
		
	}
	
	public double caldifference(double b1,double b2){
		double res=Math.abs(b1-b2);
		return res>180?360-res:res;
	}
	
	public boolean willCollision(ArrayList<Plane> planes, int round, int max){
		ArrayList<Plane> temp=new ArrayList<Plane>();
		for(Plane p: planes){
			temp.add(new Plane(p));
		}
		StraightPlayer sp=new StraightPlayer();
		if(sp.startSimulation(temp, round,round+max)>0){
			return false;
		}else{
			return true;
		}
	}
	
	public int getcount(ArrayList<Plane> planes, int round, int max){
		ArrayList<Plane> temp=new ArrayList<Plane>();
		for(Plane p: planes){
			temp.add(new Plane(p));
		}
		StraightPlayer sp=new StraightPlayer();
		return sp.startSimulation(temp, round, round+max);
	}
	
	public void setStarttTime(ArrayList<Integer> starttime){
		this.starttime=starttime;
	}
}
