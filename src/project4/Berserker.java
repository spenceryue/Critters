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

public class Berserker extends Critter {

	private static int spawned = 0;
	private int dir;
	private int[] dirSpace;
	private int next = 0;
	
	public Berserker() {
		dir = getRandomInt(8);
		dirSpace = new int[] {(dir-2+8)%8,(dir-1+8)%8,(dir),(dir+1)%8,(dir+2)%8};
		spawned++;
	}
	
	public Berserker(int dir){
		this.dir=dir;
		dirSpace = new int[] {(dir-2+8)%8,(dir-1+8)%8,(dir),(dir+1)%8,(dir+2)%8};
		spawned++;
	}
	
	@Override
	public String toString(){
		return "B";
	}
	
	@Override
	public void doTimeStep() {
		while (getEnergy() > Params.min_reproduce_energy) {
			int d = dirSpace[next++%5];
			Berserker child = new Berserker(d);
			reproduce(child,d);
			child.doTimeStep();
		}
		
		if (getEnergy() > Params.run_energy_cost)
			run(dirSpace[next++%5]);
		else if (getEnergy() > Params.run_energy_cost)
			walk(dirSpace[next++%5]);
	}

	@Override
	public boolean fight(String opponent) {
		if (isLast() && !opponent.equals("@")) {
			return ace();
		}
		doTimeStep();
		if (opponent.equals("B"))
			return false;
		else
			return true;
	}
	
	private boolean isLast() {
		try {
			java.util.List<Critter> friends= getInstances("Berserker");
			int count = 0;
			for (Critter c : friends)
				if (c.getEnergy() > 0)
					count++;
			return count == 1; }
		catch (Exception e){return false;}
	}
	
	private boolean ace(){
		try {
		java.util.List<Critter> others = getInstances("Critter");
		//int count = 0;
		/*for (Critter c : others)
			if (!(c instanceof Berserker) && !(c instanceof Algae) && c.getEnergy() > 0){
				count+= Math.ceil((double)c.getEnergy()/Params.walk_energy_cost);
			}*/
		for (Critter c : others)
			if (!(c instanceof Berserker) && !(c instanceof Algae))
				while (c.getEnergy() > 0) {
					c.walk(0);
					c.run(0);
					c.look(0);
					c.look2(0);
					//System.out.format("BERSERKER ATTACKS!!! %d%n",count--);
				}
		} catch (InvalidCritterException /*| InterruptedException*/ e) {}
		return healAndDouble();
	}
	
	private boolean healAndDouble() {
		java.util.List<Critter> algae = null;
		try {algae = getInstances("Algae");} catch(Exception e){}
		Algae a = (Algae) algae.get(0);
		a.setEnergy(4*Params.start_energy);
		a.setXCoord(getRandomInt(Params.world_width));
		a.setYCoord(getRandomInt(Params.world_height));
		a.reproduce(this,0);
		dir = 4;
		dirSpace = new int[] {(dir-2+8)%8,(dir-1+8)%8,(dir),(dir+1)%8,(dir+2)%8};
		next = 2;
		return true;
	}

	public static void runStats(java.util.List<Critter> Berserkers) {
		if (Berserkers == null)
			Berserkers = new java.util.LinkedList<Critter>();
		
		int tot_energy = 0;
		
		for (Object obj : Berserkers) {
			Berserker z = (Berserker) obj;
			tot_energy += z.getEnergy();
		}
		System.out.print("" + Berserkers.size() + " total Berserkers    ");
		
		System.out.println("total energy: "+tot_energy+"   total spawned: "+spawned);
	}

	@Override
	protected Color viewOutlineColor() {
		return Color.rgb(130, 82, 209); // purple
	}

	@Override
	protected Color viewFillColor() {
		return Color.rgb(250, 243, 32); // yellow
	}
	
	@Override
	protected Color viewTextColor() {
		return Color.BLACK;
	}
}
