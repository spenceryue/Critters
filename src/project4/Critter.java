/* CRITTERS
 * EE422C Project 4 submission by
 * Spencer Yue
 * STY223
 * https://github.com/spenceryue/critters
 * Slip days used: 2
 * Summer 2016
 */
package project4;

import java.util.Map;

import javafx.scene.Node;
import javafx.scene.paint.Color;

import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/* see the PDF for descriptions of the methods and fields in this class
 * you may add fields, methods or inner classes to Critter ONLY if you make your additions private
 * no new public, protected or default-package code or data can be added to Critter
 */
public abstract class Critter {
	
	private static java.util.Random rand = new java.util.Random();
	public static int getRandomInt(int max) {
		return rand.nextInt(max);
	}
	
	public static void setSeed(long new_seed) {
		rand = new java.util.Random(new_seed);
	}
	
	
	/* a one-character long string that visually depicts your critter in the ASCII interface */
	public String toString() { return ""; }
	
	private int energy = 0;
	protected int getEnergy() { return energy; }
	
	private int x_coord;
	private int y_coord;
	private Point p;
	
	private boolean hasWalked;
	
	private int[] interpret(int direction){
		// takes directions 0-7
		int x = 0; int y = 0;
		if (direction%2 == 1)
			x = y = 1;
		else {
			x = (direction/2+1)%2; y = 1-x;
		}
		if (direction > 2 && direction < 6)
			x *= -1;
		if (direction - 4 > 0)
			y *= -1;
		
		int[] result = {x,y};
		return result;
	}
	
	protected final void walk(int direction) {
		// incur cost
		energy -= Params.walk_energy_cost;
		// conditions? 1. alive? 2. hasWalked?
		if (energy <= 0) return;
		if (hasWalked) return;
		// interpret direction
		int[] coord = interpret(direction);
		// construct new (x,y)
		int x = (x_coord+coord[0]+Params.world_width)%Params.world_width;
		int y = (y_coord+coord[1]+Params.world_height)%Params.world_height;
		// test for conflict when "resolving" flag raised
		if (resolving) {
			Point pp = new Point(x,y);
			if (grid.containsKey(pp) && grid.get(pp).size() > 0)
				return;
		}
		// set new (x,y)
		x_coord = x;
		y_coord = y;
		// refresh
		refresh();
		// set hasWalked
		hasWalked = true;
	}
	
	protected final void run(int direction) {
		// incur cost
		energy -= Params.run_energy_cost;
		// conditions? 1. alive? 2. hasWalked?
		if (energy <= 0) return;
		if (hasWalked) return;
		// "walk" twice (and nullify added costs)
		energy += Params.walk_energy_cost;
		walk(direction);
		// first "walk" call failed?
		if (!hasWalked)
			return;
		// proceed to second "walk"
		hasWalked = false;
		energy += Params.walk_energy_cost;
		walk(direction);
	}
	
	protected final String look(int direction){
		return look(direction,1);
	}
	
	protected final String look2(int direction){
		return look(direction,2);
	}
	
	private final String look(int direction, int depth) {
		// sufficient energy?
		if (energy < Params.look_energy_cost)
			return null;
		else
			energy -= Params.look_energy_cost;
		// get indicated direction
		int[] coord = interpret(direction);
		// construct new (x,y)
		int x = (x_coord+coord[0]*depth+Params.world_width)%Params.world_width;
		int y = (y_coord+coord[1]*depth+Params.world_height)%Params.world_height;
		// construct Point for search location
		Point pp = new Point(x,y);
		// use up-to-date grid when resolving conflicts
		List<Critter> result = grid.get(pp);
		if (resolving) {
			if (result == null || result.isEmpty())
				return null;
			else if (!grid.get(pp).isEmpty())
				return result.get(0).toString();
		}
		// regular operation: check startingPlace for searched Point
		if (!startingPlace.containsKey(this))
			return null;
		else
			return startingPlace.get(this).toString();
	}
	
	protected final void reproduce(Critter offspring, int direction) {
//		if (offspring.getClass().getName().equals("project4.Berserker"))
//		System.out.println("BERSERKER SPAWNED!!!");

		// conditions? 1. sufficient energy? (yes -> incur cost)	2. alive still?		3. child will live?
		if (energy < Params.min_reproduce_energy) return;
		if (energy == 0) return;
		if (energy == 1) return;
		// set child energy, parent energy
		offspring.energy = this.energy/2; this.energy -= offspring.energy;
		// interpret direction
		int[] coord = interpret(direction);
		// assign child location
		offspring.x_coord = (x_coord+coord[0]+Params.world_width)%Params.world_width;
		offspring.y_coord = (y_coord+coord[1]+Params.world_height)%Params.world_height;
		offspring.p = new Point(offspring.x_coord, offspring.y_coord);
		// queue for update
		babies.add(offspring);
		population.add(offspring);
		
		// DEBUG	*****	*****	*****	*****	REMOVE LATER	*****	*****	*****	*****
//		System.out.println(offspring.x_coord+" "+offspring.y_coord+" "+ offspring.energy);			
		// DEBUG	*****	*****	*****	*****	REMOVE LATER	*****	*****	*****	*****
	}
	
	// My Custom Formatting Options: 
	// I draw each Critter as a circle with its own ASCII symbol inside.
	// I specify the cirlce outline, cirlce fill, and text fill for each Critter.
	// Default is Black, White, Black respectively.
	protected final String viewShape() {
		return "Circle";
	}
	
	protected javafx.scene.paint.Color viewOutlineColor() {
		return Color.BLACK;
	}
	protected javafx.scene.paint.Color viewFillColor() {
		return Color.WHITE;
	}
	
	protected javafx.scene.paint.Color viewTextColor() {
		return Color.BLACK;
	}
	
	public final javafx.scene.paint.Color[] getIconParameters(){
		return new javafx.scene.paint.Color[] {viewOutlineColor(), viewFillColor(), viewTextColor()};
	}
	
	public abstract void doTimeStep();
	public abstract boolean fight(String oponent);
	
	/* create and initialize a Critter subclass
	 * critter_class_name must be the name of a concrete subclass of Critter, if not
	 * an InvalidCritterException must be thrown
	 */
	public static void makeCritter(String critter_class_name) throws InvalidCritterException {
		String qualifiedName = critter_class_name;
		if (!critter_class_name.startsWith("project4."))
			qualifiedName = "project4."+qualifiedName;
		
		Class<?> type = null;
		try {
			// create new Critter with reflection
			type = Class.forName(qualifiedName, true, URLClassLoader.newInstance(new URL[] {new File(".").toURI().toURL()} ) );
			Object created = type.newInstance();
			
			type = Critter.class;
			Field x_coord = type.getDeclaredField("x_coord");
			Field y_coord = type.getDeclaredField("y_coord");
			Field p = type.getDeclaredField("p");
			Field energy = type.getDeclaredField("energy");
			
			int x = getRandomInt(Params.world_width);
			int y = getRandomInt(Params.world_height);
			x_coord.set(created, x);
			y_coord.set(created, y);
			p.set(created, new Point(x,y));
			energy.set(created, 1+getRandomInt(Params.start_energy));
			
			// DEBUG	*****	*****	*****	*****	REMOVE LATER	*****	*****	*****	*****
//			System.out.println(x_coord.get(created)+" "+y_coord.get(created)+" "+Arrays.toString(((Point)p.get(created)).pair())+" "+ energy.get(created));
			
			// DEBUG	*****	*****	*****	*****	REMOVE LATER	*****	*****	*****	*****
			
			// Add to population, active, grid
			Critter c = (Critter)created;
			population.add(c);
			active.add(c);
			if (!grid.containsKey(c.p))
				grid.put(c.p, new java.util.LinkedList<Critter>());
			grid.get(c.p).add(c);
			
		} catch (MalformedURLException | ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			throw new InvalidCritterException(critter_class_name);
		}
	}
	
	public static List<Critter> getInstances(String critter_class_name) throws InvalidCritterException {
		String qualifiedName = critter_class_name;
		if (!critter_class_name.startsWith("project4."))
			qualifiedName = "project4."+qualifiedName;
		
		List<Critter> result = new java.util.ArrayList<Critter>();
		
		try {
			Class<?> type = Class.forName(qualifiedName, true, URLClassLoader.newInstance(new URL[] {new File(".").toURI().toURL()} ) );
			for (Critter c : population)
				if (type.isInstance(c))
					result.add(c);
		} catch (ClassNotFoundException | MalformedURLException e) {
			throw new InvalidCritterException(critter_class_name);
		}
		
		return result;
	}
	
	public static void runStats(List<Critter> critters) {
		System.out.print("" + critters.size() + " critters as follows -- ");
		java.util.Map<String, Integer> critter_count = new java.util.HashMap<String, Integer>();
		for (Critter crit : critters) {
			String crit_string = crit.toString();
			Integer old_count = critter_count.get(crit_string);
			if (old_count == null) {
				critter_count.put(crit_string, 1);
			} else {
				critter_count.put(crit_string, old_count.intValue() + 1);
			}
		}
		String prefix = "";
		for (String s : critter_count.keySet()) {
			System.out.print(prefix + s + ":" + critter_count.get(s));
			prefix = ", ";
		}
		System.out.println();		
	}
	
	/* the TestCritter class allows some critters to "cheat". If you want to 
	 * create tests of your Critter model, you can create subclasses of this class
	 * and then use the setter functions contained here. 
	 * 
	 * NOTE: you must make sure that the setter functions work with your implementation
	 * of Critter. That means, if you're recording the positions of your critters
	 * using some sort of external grid or some other data structure in addition
	 * to the x_coord and y_coord functions, then you MUST update these setter functions
	 * so that they correctly update your grid/data structure.
	 */
	static abstract class TestCritter extends Critter {
		protected void setEnergy(int new_energy_value) {
			super.energy = new_energy_value;
		}
		
		protected void setXCoord(int new_x_coord) {
			super.x_coord = new_x_coord;
			refresh();	
		}
		
		protected void setYCoord(int new_y_coord) {
			super.y_coord = new_y_coord;
			refresh();
		}
		
		private void refresh() {
			Point old = super.p;
			int[] pair = old.pair();
			Point newPoint = new Point(super.x_coord, super.y_coord);
			// remove Critter from old location in grid
			if (grid.containsKey(old))
				grid.get(old).remove(this);
			// add Critter to new location in grid
			if (!grid.containsKey(newPoint))
				grid.put(newPoint,new java.util.LinkedList<Critter>());
			grid.get(newPoint).add(this);
			super.p = newPoint;
			// record Critter location in startingPlace (for this turn)
			if (!startingPlace.containsKey(this))
				startingPlace.put(this, old);
			// erase previous spot on world String[][]
			world[pair[1]][pair[0]+1] = " ";
		}
	}
	
	private void refresh() {
		Point old = p;
		int[] pair = old.pair();
		Point newPoint = new Point(x_coord, y_coord);
		// remove Critter from old location in grid
		if (grid.containsKey(old))
			grid.get(old).remove(this);
		// add Critter to new location in grid
		if (!grid.containsKey(newPoint))
			grid.put(newPoint,new java.util.LinkedList<Critter>());
		grid.get(newPoint).add(this);
		// record Critter location in startingPlace (for this turn)
		if (!startingPlace.containsKey(this))
			startingPlace.put(this, old);
		// erase previous spot on world String[][]
		world[pair[1]][pair[0]+1] = " ";
	}
	
	private	static List<Critter> population = new java.util.ArrayList<Critter>();
	private static List<Critter> active = new java.util.ArrayList<Critter>();
	private static List<Critter> babies = new java.util.ArrayList<Critter>();
	private static Map<Point,List<Critter>> grid = new java.util.HashMap<Point,List<Critter>>();
	private static Map<Critter,Point> startingPlace = new java.util.HashMap<Critter,Point>();
	private static Map<Critter,Icon> iconBank = new java.util.HashMap<Critter,Icon>();
	
	private static boolean resolve(List<Critter> conflicts) {
		if (conflicts.isEmpty() || conflicts.size() == 1)
			return true;
		
		Critter A = conflicts.get(0);
		Critter B = conflicts.get(1);
		
		if (A.energy <= 0)
			conflicts.remove(A);
		if (B.energy <= 0)
			conflicts.remove(B);
		
		if (A.energy > 0 && B.energy > 0) {
			boolean intentA = A.fight(B.toString());
			boolean intentB = B.fight(A.toString());
			
			if (!conflicts.contains(A) || !conflicts.contains(B))
				return resolve(conflicts);
			else if (A.energy <= 0){
				conflicts.remove(A);
				return resolve(conflicts);
			} else if (B.energy <= 0) {
				conflicts.remove(B);
				return resolve(conflicts);
			} else if (intentA && intentB) {
				int result = getRandomInt(A.energy) - getRandomInt(B.energy);
				if (result > 0) {
					A.energy += B.energy/2;
					B.energy = 0;
				} else {
					B.energy += A.energy/2;
					A.energy = 0;
				}
			}
			else if (intentA && !intentB) {
				A.energy += B.energy/2;
				B.energy = 0;
			}
			else if (!intentA && intentB) {
				B.energy += A.energy/2;
				A.energy = 0;
			}
			else if (!intentA && !intentB) {
				if (A.energy > B.energy) {
					A.energy += B.energy/2;
					B.energy = 0;
				} else {
					B.energy += A.energy/2;
					A.energy = 0;
				}
			}	
		}
			
		return resolve(conflicts);
	}
	
	private static boolean resolving = false;
	
	public static void worldTimeStep() {
		// load babies & new algae from last step to active and grid
		Iterator<Critter> I = babies.iterator();
		while (I.hasNext()) {
			Critter c = I.next();
			active.add(c);
			if (!grid.containsKey(c.p))
				grid.put(c.p, new java.util.LinkedList<Critter>());
			grid.get(c.p).add(c);
			I.remove();
		}

		// for each active Critter...
		for (Critter c : active) {
			// 1. set hasWalked = false;
			c.hasWalked = false;
			// 2. incur rest energy
			c.energy -= Params.rest_energy_cost;
			// 3. call doTimeStep()
			if (c.energy > 0)
				c.doTimeStep();
		}

		// for each Critter resolve encounters:
		for (Critter c : active) {
		// 1. raise resolving flag
			resolving = true;
		// 2. call resolve() for each conflicting grid node
			if (grid.get(c.p).size() > 1 && c.energy > 0)
				resolve(grid.get(c.p));
		// 3. clear startingPlace map for next turn
			startingPlace.clear();
		// 4. Lower resolving flag
			resolving = false;
		}
		
		// add children to population
		// (already done in reproduce() method)
		
		// add new algae to babies and population
		for (int i=Params.refresh_algae_count; i>0; i--) {
			Critter a = new Algae();
			a.x_coord = getRandomInt(Params.world_width);
			a.y_coord = getRandomInt(Params.world_height);
			a.p = new Point(a.x_coord,a.y_coord);
			a.energy = Params.start_energy;
			babies.add(a);
			population.add(a);
		}

		// clean up dead
		I = active.iterator();
		while (I.hasNext()) {
			Critter c = I.next();
			if (c.energy <= 0) {
				c.energy = 0;
				grid.get(c.p).remove(c);
				I.remove();
				world[c.y_coord][c.x_coord+1] = " ";
				iconBank.remove(c);
				Node self = Viewer.spaceMap.remove(c.p);
				Viewer.world.getChildren().remove(self);
			}
		}
		Viewer.initWorldGrid();
	}
	static int round = 0;
	static long max = 0;
	static int whichRound = 0;
	static int[] whichStep = new int[5];
	static long allTotal = 0;
	
	
	private static final String[] EDGE;
	private static String[][] world;
	static {
		EDGE = new String[Params.world_height+2];
		Arrays.fill(EDGE, "-");
		EDGE[0] = EDGE[EDGE.length-1] = "+";
		
		world = new String[Params.world_height][Params.world_width+2];
		for (String[] row : world) {
			Arrays.fill(row, " ");
			row[0] = row[row.length-1] = "|";
		}
	}
	
	public static void displayWorld() {
		// Graphical displayWorld()
		if (Viewer.GUI) {
			if (Viewer.skip);
			else
				for (Critter c : active)
					if (c.energy > 0)
						paintToGrid(c,c.x_coord,c.y_coord);
			return;
		}
		
		// load changes to world String[][]
		for (Critter c : active)
			world[c.y_coord][c.x_coord+1] = c.toString();
		
		// print top edge
		for (String s : EDGE)
			System.out.print(s);
		System.out.println();
		
		// display world String[][]
		for (String[] row : world){
			for (String s : row)
				System.out.print(s);
			System.out.println();
		}
		
		// print bottom edge
		for (String s : EDGE)
			System.out.print(s);
		System.out.println();
	}
	
	private static void paintToGrid(Critter c, int x, int y){
		if (!iconBank.containsKey(c))
			iconBank.put(c, new Icon(c));
		
		if (Viewer.spaceMap.containsKey(c.p)) {
			if (Viewer.spaceMap.get(c.p).equals(iconBank.get(c).body))
				return;
			
			Node occupant = Viewer.spaceMap.remove(c.p);
			Viewer.world.getChildren().remove(occupant);
		}
		Viewer.spaceMap.put(c.p, iconBank.get(c).body);
		Viewer.world.getChildren().remove(iconBank.get(c).body);
		Viewer.world.add(iconBank.get(c).body, x, y);
	}
}
