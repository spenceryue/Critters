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
import project4.Critter.TestCritter;

public class Algae extends TestCritter {

	public String toString() { return "@"; }
	
	public boolean fight(String not_used) { return false; }
	
	public void doTimeStep() {
		setEnergy(getEnergy() + Params.photosynthesis_energy_amount);
	}

	@Override
	protected Color viewOutlineColor() {
		return Color.rgb(12, 172, 56); // green
	}

	@Override
	protected Color viewFillColor() {
		return Color.rgb(164, 230, 23); // lime
	}

	@Override
	protected Color viewTextColor() {
		return Color.rgb(22, 125, 29); // forest
	}
}
