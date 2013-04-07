package airplane.g2;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import airplane.sim.Plane;
import airplane.sim.Player;

public class SillyPlayer extends Player{
	private Logger logger = Logger.getLogger(this.getClass()); // for logging
	
	@Override
	public String getName() {
		return "Silly Player";
	}
	

	@Override
	public void startNewGame(ArrayList<Plane> planes) {

	}
	
	@Override
	public double[] updatePlanes(ArrayList<Plane> planes, int round, double[] bearings) {
		ArrayList<Plane> templist=new ArrayList<Plane>();
		for(int i=0;i<planes.size();i++){
			//iterate each plane;
			Plane p1=planes.get(i);
			if(round<p1.getDepartureTime()){
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
					np.setBearing(tbearing);
					if(templist.size()==index){
						templist.add(np);
					}else{
						templist.set(index, np);
					}
					if(!willCollision(templist,round)){
						bearings[i]=target+bias;
						break;
					}else if(bias!=0){
						tbearing=target-bias;
						tbearing=tbearing<0?tbearing+360:tbearing;
						np.setBearing(tbearing);
						if(templist.size()==index){
							templist.add(np);
						}else{
							templist.set(index, np);
						}
						if(!willCollision(templist,round)){
							bearings[i]=target-bias;
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
					if(!willCollision(templist,round)){
						//collisions[bias]=true;
						if(onestep(p1,tbearing)<mindiff){
							bestbear=tbearing;
							mindiff=onestep(p1,tbearing);
						}
					}
				}
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
		logger.error(message.toString());
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
	
	public boolean willCollision(ArrayList<Plane> planes, int round){
		ArrayList<Plane> temp=new ArrayList<Plane>();
		for(Plane p: planes){
			temp.add(new Plane(p));
		}
		StraightPlayer sp=new StraightPlayer();
		if(sp.startSimulation(temp, round)>0){
			return false;
		}else{
			return true;
		}
	}
}
