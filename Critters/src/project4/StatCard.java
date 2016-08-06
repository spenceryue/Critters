/* CRITTERS
 * EE422C Project 4 submission by
 * Spencer Yue
 * STY223
 * https://github.com/spenceryue/critters
 * Slip days used: 2
 * Summer 2016
 */
package project4;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class StatCard {

/*	static HashMap<Class<?>, ImageView> photos = new HashMap<Class<?>, ImageView>();
	static {
		try {
		photos.put(Class.forName("project4.Algae"), new ImageView(new Image("\\images\\algae.png")));
		photos.put(Class.forName("project4.Craig"), new ImageView(new Image("\\images\\craig.png")));
		photos.put(Class.forName("project4.Berserker"), new ImageView(new Image("\\images\\berserker.png")));
		photos.put(Class.forName("project4.ZigZagger"), new ImageView(new Image("\\images\\zigzagger.png")));
		} catch (ClassNotFoundException e) {}*/
		
	static ImageView findPicture(File root,String c) {
		String term = c.replace("project4.", "").toLowerCase();
		System.out.println(term);
		ImageView test;
		
		//for (String s : Main.classes)
			//try {
			//if (!photos.containsKey(Class.forName("project4."+s)))
				for (File f : root.listFiles()) {
					
					if (f.getName().endsWith(".png")
						|| f.getName().endsWith(".jpeg")
						|| f.getName().endsWith(".gif"))
						if (f.getName().contains(term)){
							System.out.println("\t"+f.getPath());
							return new ImageView(new Image("file:"+f.getPath())); }
					if (f.isDirectory()) {
						System.out.println("\t"+f.getName());
						if( (test = findPicture(f,c)) != null)
							return test;
					}
				}
			//} catch (ClassNotFoundException e) {}
		return null;
	}
	
	Critter c;
	VBox container;
		HBox row1_name;
			Icon myIcon;
			Text name;
		ImageView photo;
		Button photo_substitute;
		int countAlive = 0;
        int countDead = 0;
        Text alive;
        Text dead;
        Text lifetimeEnergy;
	
	public StatCard(Critter c) {
		this.c = c;
		
		container = new VBox();
			
			row1_name = new HBox();
			container.getChildren().add(row1_name);
			
				myIcon = new Icon(c);
				
				name = new Text(Main.match(c.getClass().getName()));
				
			row1_name.getChildren().addAll(myIcon.body,name);
			
			System.out.println(System.getProperty("user.dir"));
			photo = findPicture(new File ("."),c.getClass().getName());//photos.containsKey(c.getClass()) ? photos.get(c.getClass()) : null;
			if (photo == null) {
				System.out.println("bug: no picture");
		        photo_substitute = new Icon(c).body;
		        photo_substitute.resize(100,100);
		        container.getChildren().add(photo_substitute);
			} else {
				photo.setPreserveRatio(true);
				photo.setFitWidth(100);
				photo.setId("photo-"+c.getClass().getName().replace("project4.", ""));
				container.getChildren().add(photo);
			}
	        
//	        String plural = (name.getText().endsWith("e") || name.getText().endsWith("s") ?
//	   			   name.getText() : name.getText()+"s");
	        
	        alive = new Text(countAlive + "\tAlive");
	        container.getChildren().add(alive);
	        
	        dead = new Text(countDead + "\tDead");
	        container.getChildren().add(dead);
	        
	        lifetimeEnergy = new Text(getLifeTimeEnergy() + "\tLifetime Energy");
	        container.getChildren().add(lifetimeEnergy);
	        
	        refresh();
	}
	
	private int getLifeTimeEnergy() {
		return (countAlive+countDead)*Params.start_energy;
	}
	
	public void refresh() {
		List<Critter> critterList = null;
		try {
			critterList = Critter.getInstances(c.getClass().getName());
		} catch (InvalidCritterException e) {}
		countAlive = 0;
		countDead = 0;
		for (Critter c : critterList)
			if (c.getEnergy() > 0)
				countAlive++;
			else
				countDead++;
		alive.setText(countAlive + "\tAlive");
		dead.setText(countDead + "\tDead");
		lifetimeEnergy.setText(getLifeTimeEnergy() + "\tLifetime Energy");
	}
	
	public Text getDetails() {
		// Redirect System.out
		OutputStream out = new ByteArrayOutputStream();
		PrintStream capture = new PrintStream(out);
		PrintStream orig = System.out;
		System.setOut(capture);
		
		List<Critter> critterList = null;
		try {
			critterList = Critter.getInstances(c.getClass().getName());
			Method runStats = Class.forName(c.getClass().getName()).getDeclaredMethod("runStats", List.class);
			runStats.invoke(null, critterList);
		} catch (SecurityException 
				| InvocationTargetException
				| IllegalAccessException
				| ClassNotFoundException
				| InvalidCritterException e) {
			System.err.println(e.toString());
		} catch (NoSuchMethodException e) {
			Critter.runStats(critterList);
		}
		
		System.setOut(orig);
		return new Text(out.toString());
	}
}
