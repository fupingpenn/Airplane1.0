package airplane.g2;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import airplane.sim.Plane;
import airplane.sim.Player;

public class PowerSaverPlayer extends Player{
	private Logger logger = Logger.getLogger(this.getClass()); // for logging
	PSillyPlayer realplayer=new PSillyPlayer();
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Power Saver Player";
	}

	@Override
	public void startNewGame(ArrayList<Plane> planes) {
		ArrayList<Integer> starttime=new ArrayList<Integer>();
		ArrayList<Plane> templist=new ArrayList<Plane>();
		for(Plane p:planes){
			Plane p1=new Plane(p);
			int index=starttime.size();
			starttime.add(p1.getDepartureTime());
			templist.add(p1);
			int delay=0;
			boolean success=false;
			while(!success){
				starttime.set(index, starttime.get(index)+delay);
				PSillyPlayer sp=new PSillyPlayer();
				int round=sp.startSimulation(templist, starttime, 5);
				if(round<0){
					delay++;
				}else{
					success=true;
				}
			}
		}
		logger.error(starttime);
		this.realplayer.setStarttTime(starttime);
		
	}

	@Override
	public double[] updatePlanes(ArrayList<Plane> planes, int round,
			double[] bearings) {
		// TODO Auto-generated method stub
		return realplayer.updatePlanes(planes, round, bearings);
	}

}
