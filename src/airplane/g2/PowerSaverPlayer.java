package airplane.g2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import airplane.sim.GameEngine;
import airplane.sim.Plane;
import airplane.sim.Player;

public class PowerSaverPlayer extends Player{
	private Logger logger = Logger.getLogger(this.getClass()); // for logging
	//PSillyPlayer realplayer=new PSillyPlayer();
	private LinkedList<double[]> actions;
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Power Saver Player";
	}

	@Override
	public void startNewGame(ArrayList<Plane> planes) {
		String dirname="solutions/"+this.getName();
		File dir=new File(dirname);
		if(!dir.exists()){
			dir.mkdirs();
		}
		String boardname=GameEngine.config.boardFile.getName();
		String solutionfile=dirname+"/"+boardname+"_solution";
		File sf=new File(solutionfile);
		if(sf.exists()){
			LoadActions(sf);
			return;
		}
		ArrayList<Integer> starttime=new ArrayList<Integer>();
		ArrayList<Plane> templist=new ArrayList<Plane>();
		double count=0;
		for(Plane p:planes){
			count++;
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
			logger.error(count/planes.size()*100+"%");
		}
		
		//logger.error(starttime);
		//this.realplayer.setStarttTime(starttime);
		WriteActions(sf);
		
	}
	
	public void WriteActions(File sf){
		try {
			sf.createNewFile();
			PrintWriter out=new PrintWriter(sf);
			for(double[] temp:actions){
				StringBuffer sb=new StringBuffer();
				for(double t:temp){
					sb.append(t+";");
				}
				sb.deleteCharAt(sb.length()-1);
				out.println(sb.toString());
			}
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void LoadActions(File sf){
		try{
			BufferedReader br=new BufferedReader(new FileReader(sf));
			String line=br.readLine();
			actions=new LinkedList<double[]>();
			while(line!=null&&line.length()>0&&line.contains(";")){
				String numbers[]=line.split(";");
				double[] temp=new double[numbers.length];
				for(int i=0;i<temp.length;i++){
					temp[i]=Double.parseDouble(numbers[i]);
				}
				actions.add(temp);
				line=br.readLine();
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public boolean feasible(ArrayList<Plane> planes,ArrayList<Integer> starttime){
		PSillyPlayer sp=new PSillyPlayer();
		Result res=sp.startSimulation(planes, starttime, 10,actions);
		if(res.getRound()>0){
			this.actions=res.getActions();
			return true;
		}else{
			return false;
		}
	}

	@Override
	public double[] updatePlanes(ArrayList<Plane> planes, int round,
			double[] bearings) {
		// TODO Auto-generated method stub
		return actions.pop();
	}

}
