/* CRITTERS
 * EE422C Project 4 submission by
 * Spencer Yue
 * STY223
 * https://github.com/spenceryue/critters
 * Slip days used: 2
 * Summer 2016
 */
package project4;

import javafx.scene.paint.Color;

public class ZigZagger extends Critter {
	private static double suppressor = (double) Params.rest_energy_cost;
	private static final double MAXLIFE = suppressor==0 ? 0 : Params.start_energy/Params.rest_energy_cost;;
	private static final int REPRODUCE_THRESHOLD = (int) (Params.start_energy * (getRandomInt(50)+50)*0.01);
	
	private final int WIDTH;
	private final int DIRECTION;
	private final int LEFT, RIGHT;
	
	private int leftOrRight;
	private double lifeSpan;
	private boolean justWalked;
	
	public ZigZagger() {
		DIRECTION = super.getRandomInt(8);
		WIDTH = super.getRandomInt(5)+1;
		LEFT = (DIRECTION-1+8)%8;
		RIGHT = (DIRECTION+1+8)%8;
		lifeSpan = suppressor==0 ? (double) getEnergy()/(1e-3+Params.walk_energy_cost) : (double) getEnergy()/Params.rest_energy_cost;
		leftOrRight = 1;
		justWalked = false;
	}
	
	public ZigZagger(int direction) {
		DIRECTION = direction;
		WIDTH = super.getRandomInt(5)+1;
		LEFT = (DIRECTION-1+8)%8;
		RIGHT = (DIRECTION+1+8)%8;
		lifeSpan = suppressor==0 ? (double) getEnergy()/(1e-3+Params.walk_energy_cost) : (double) getEnergy()/Params.rest_energy_cost;
		leftOrRight = 1;
		justWalked = false;
	}
	
	private int nextDirection() {
		if (leftOrRight > 0 && leftOrRight <= WIDTH) {
			if (leftOrRight == WIDTH)
				leftOrRight *= -1;
			else
				leftOrRight++;
			return LEFT;
		} else if (leftOrRight >= -WIDTH && leftOrRight < 0) {
			if (leftOrRight == -1)
				leftOrRight *= -1;
			else
				leftOrRight++;
			return RIGHT;
		} else
			throw new IllegalStateException("Somehow the next direction is unknown.");
	}
	
	private int lookNextDirection(){
		int temp = leftOrRight;
		int result = nextDirection();
		leftOrRight = temp;
		return result;
	}
	
	@Override
	public String toString() {
		return "Z";
	}

	@Override
	public void doTimeStep() {
		justWalked = false;
		
		// reproduce if can and energy below threshhold
		if (getEnergy() < REPRODUCE_THRESHOLD && getEnergy() > Params.min_reproduce_energy+1 || getRandomInt(3) != 0) {
			int newDirection = (DIRECTION+1+getRandomInt(8))%8;
			reproduce(new ZigZagger(newDirection), newDirection);
		}
		
		lifeSpan = suppressor==0 ? (double) getEnergy()/(1e-3+Params.walk_energy_cost) : (double) getEnergy()/Params.rest_energy_cost;
		
		// near death (-> conserve)
		if (lifeSpan < 0.2*MAXLIFE)
			return;
		
		// record justWalked
		justWalked = true;
		
		// below half (-> panic)
		if (lifeSpan < 0.5*MAXLIFE && getEnergy() > Params.run_energy_cost)
			if (getEnergy() > Params.look_energy_cost && getRandomInt(2) == 0) {
				String result = look2(lookNextDirection());
				if (result != null && result.equals("B")); // avoid Berserker 50% time
				else
					run(nextDirection());
			}
		
		// normal feeding
		else if (getEnergy() > Params.walk_energy_cost)
			if (getEnergy() > Params.look_energy_cost && getRandomInt(2) == 0) {
				String result = look(lookNextDirection());
				if (result != null && result.equals("B")); // avoid Berserker 50% time
				else
					walk(nextDirection());
			}
		else
			justWalked=false;
	}

	@Override
	public boolean fight(String opponent) {
		if (opponent.contains("@"))
			return true;
		if (opponent.contains("Z"))
			return false;
		if (justWalked)
			return true;
		else {
			for (int i=0; i<8; i++)
				if (look(i) == null) {
					walk(i);
					break;
				}
			return true;
		}
	}
	
	public static void runStats(java.util.List<Critter> zigZaggers) {
		if (zigZaggers == null)
			zigZaggers = new java.util.LinkedList<Critter>();
		//String[] dir = {"Right","Up-Right","Up","Left-Up","Left","Down-Left","Down","Right-Down"};
		int[] total_dir = new int[8];
		int tot_energy = 0;
		
		for (Object obj : zigZaggers) {
			ZigZagger z = (ZigZagger) obj;
			total_dir[z.DIRECTION]++;
			tot_energy += z.getEnergy();
		}
		System.out.print("" + zigZaggers.size() + " total ZigZaggers    ");
		for (int i=0; i<8; i++)
			System.out.format("%.2f%%   ", zigZaggers.size() == 0 ? 0 : total_dir[i] / (0.01 * zigZaggers.size()));
		System.out.println("total energy: "+tot_energy);
	}
	
	@Override
	protected Color viewOutlineColor() {
		return Color.rgb(144, 76, 21); // brown
	}

	@Override
	protected Color viewFillColor() {
		return Color.rgb(255, 206, 31); // tangy
	}
	
	@Override
	protected Color viewTextColor() {
		return Color.rgb(237, 51, 31); // red
	}
}
