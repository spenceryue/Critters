/* CRITTERS Main.java
 * EE422C Project 4 submission by
 * Replace <...> with your actual data.
 * Spencer Yue
 * STY223
 * https://github.com/spenceryue/critters
 * Slip days used: 2
 * Summer 2016
 */
package project4;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
	// true -> default "make" command of 100 Algae, 25 Craig
	private static boolean test_Stage12 = false;
	private static boolean allowQuick = true;
	private static List<String> classes = new java.util.ArrayList<String>();
	
	public static void main(String[] args) throws InvalidCritterException {
		Scanner sc = new Scanner(System.in);
		buildClassList();
		System.out.print("Available Critters: "+classes.get(0));
		for (String s : classes.subList(1, classes.size()-1))
			System.out.print(", "+s);
		System.out.println("\n");
		
		while (true) {
			System.out.print("critters> ");
			String line = null;
			if(sc.hasNextLine())
				line = sc.nextLine();
			String[] input = line.split("[ \\t]++");
			
			int i = 0;
			while (input[i].length()==0)
				i++;
			String s = input[i].toLowerCase();
			
			if (s.equals("quit"))
				break;
			else if (s.equals("show"))
				Critter.displayWorld();
			else if (s.equals("step"))
				if (i+1<input.length && input[i+1].matches("\\d+"))
					for (int j=0; j<Integer.parseInt(input[i+1]); j++)
						Critter.worldTimeStep();
				else
					Critter.worldTimeStep();
			else if (s.equals("seed"))
				if (i+1<input.length && input[i+1].matches("\\d+"))
					Critter.setSeed(Long.parseLong(input[i+1]));
				else
					// remove print statement... "*"
				{System.out.println("*");printUsage();}
			else if (s.equals("make"))
				if (i+1<input.length && classes.contains(input[i+1]))
					if (i+2<input.length && input[i+2].matches("\\d+"))
						for (int j=0; j<Integer.parseInt(input[i+2]); j++)
							try {
								Critter.makeCritter(input[i+1]);
							} catch (InvalidCritterException e) {
								System.err.println(e.toString());
							} finally {
								i--;
							}
					else
						if (!test_Stage12)
							try {
								Critter.makeCritter(input[i+1]);
							} catch (InvalidCritterException e) {
								System.err.println(e.toString());
							}
						else
							// default make command: 100 Algae, 25 Craig
							for (int j=0; j<100; j++)
								if (j<25) {
									Critter.makeCritter("Algae");
									Critter.makeCritter("Craig");
								} else
									Critter.makeCritter("Algae");
				else
					// remove print statement... "**"
					{System.out.println("**");printUsage();}
			else if (s.equals("stats"))
				if (i+1<input.length && classes.contains(input[i+1]))
					try {
						String qualifiedName = input[i+1];
						if (!input[i+1].startsWith("project4."))
							qualifiedName = "project4."+qualifiedName;
						List<Critter> critterList = Critter.getInstances(qualifiedName);
						Method runStats = Class.forName(qualifiedName).getDeclaredMethod("runStats", List.class);
						runStats.invoke(null, critterList);
					} catch (NoSuchMethodException 
							| SecurityException 
							| InvocationTargetException
							| IllegalAccessException
							| ClassNotFoundException
							| InvalidCritterException e) {
						System.err.println(e.toString());
					}
				else
					// remove print statement... "***"
				{System.out.println("***");printUsage();}
			else if (s.equals("'") && allowQuick)
					for (int j=0; j<10; j++){
						Critter.worldTimeStep();
						Critter.displayWorld();
						try { Thread.sleep(1000); } catch (InterruptedException e) {}
					}
				else
				{System.out.println("****");printUsage();}
		}
		sc.close();
		System.exit(0);
		
//		System.out.println(new Algae().getClass().getName());
//		System.out.println(
//				Class.forName("project4.Algae", true, URLClassLoader.newInstance(new URL[] {new File(".").toURI().toURL()} ) ).getTypeName());
		try {
			System.out.println(
					Class.forName("project4.Algae", true, URLClassLoader.newInstance(new URL[] {new File(".").toURI().toURL()} ) ).getTypeName());
		} catch (Exception e) {
		}
//		String s = "project4.Algae";
//		System.out.println((s.matches("[a-zA-Z[0-9][.]]++")));
	}
	
	private static void printUsage() {
		System.err.println("Accepted usage for critter:\n"
				+ "quit\tterminates the program\n"
				+ "show\tinvokes Critter.displayWorld()\n"
				+ "step [<count>]\tinvokes Critter.worldTimeStep() the specified <count> number of times\n"
				+ "seed <number>\tinvokes Critter.setSeed(<number>)\n"
				+ "make <class_name> [<count>]\tinvokes Critter.makeCritter(<class_name>) the specified <count> number of times\n"
				+ "\t\tunless the test_Stage12 flag is set, in which case 100 Algae and 25 Craigs are made.\n"
				+ "stats <class_name>\tinvokes Critter.getInstances(<class_name>) and passes the returning List<Critter>\n"
				+ "\t\tobject to the <class_name> specific static runStats() method\n");
	}
	
	private static void buildClassList() {
		File root = new File(".");
		explore(root);
		classes.remove("InvalidCritterException");
		classes.remove("Critter$TestCritter");
		classes.remove("Critter");
		classes.remove("Main");
		classes.remove("Params");
		classes.remove("Point");
	}
	
	private static void explore(File start){
		for (File f : start.listFiles())
			if (f.isDirectory())
				explore(f);
			else if (f.isFile())
				if (f.getName().endsWith(".class"))
					classes.add(f.getName().substring(0,f.getName().length()-6));
	}
}
