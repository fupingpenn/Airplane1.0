package airplane.g2;

import java.util.LinkedList;

public class Result {
	private int round;
	private LinkedList<double[]> actions;
	public Result(int round, LinkedList<double[]> actions){
		this.round=round;
		this.actions=actions;
	}
	public int getRound() {
		return round;
	}
	public void setRound(int round) {
		this.round = round;
	}
	public LinkedList<double[]> getActions() {
		return actions;
	}
	public void setActions(LinkedList<double[]> actions) {
		this.actions = actions;
	}
}
