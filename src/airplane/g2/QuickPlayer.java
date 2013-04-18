package airplane.g2;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import airplane.sim.Plane;
import airplane.sim.Player;

public class QuickPlayer extends Player{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Quick Player";
	}

	private SillyPlayer realplayer=new SillyPlayer();
	private Logger logger = Logger.getLogger(this.getClass());
	@Override
	public void startNewGame(ArrayList<Plane> planes) {
		ArrayList<Integer> starttime=new ArrayList<Integer>();
		ArrayList<Plane> templist=new ArrayList<Plane>();
		for(Plane p:planes){
			Plane p1=new Plane(p);
			int index=starttime.size();
			starttime.add(p1.getDepartureTime());
			templist.add(p1);
			if(!feasible(templist,starttime)){
				int lts=starttime.get(index);
				int ts=lts;
				if(ts==0){
					ts=1;
				}else{
					ts*=2;
				}
				starttime.set(index, ts);
				while(!feasible(templist,starttime)){
					lts=ts;
					ts*=2;
					starttime.set(index, ts);
				}
				while(ts>lts){
					int mid=(ts+lts)/2;
					starttime.set(index, mid);
					if(feasible(templist,starttime)){
						ts=mid;
					}else{
						lts=mid+1;
					}
				}
				starttime.set(index, ts);
			}
		}
		logger.error(starttime);
		this.realplayer.setStarttTime(starttime);
		
		
	}
	
	public boolean feasible(ArrayList<Plane> planes,ArrayList<Integer> starttime){
		/*SillyPlayer sp=new SillyPlayer();
		int round=sp.startSimulation(planes, starttime, 300);
		return round>0;*/
		return true;
	}

	@Override
	public double[] updatePlanes(ArrayList<Plane> planes, int round,
			double[] bearings) {
		// TODO Auto-generated method stub
		return realplayer.updatePlanes(planes, round, bearings);
	}

}
