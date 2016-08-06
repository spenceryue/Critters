/* CRITTERS
 * EE422C Project 4 submission by
 * Spencer Yue
 * STY223
 * https://github.com/spenceryue/critters
 * Slip days used: 2
 * Summer 2016
 */
package project4;

import javafx.scene.control.Button;

public class Icon {
	Critter c;
	Button body;

	// y = -0.0778x + 12.697	where x = sqrt(width*height)
	static double fontSize = -0.0778*(Math.sqrt(Params.world_width*Params.world_height)) + 12.697;
	static {
		if (fontSize > 12.5)
			fontSize = 12.5;
		else if (fontSize < 5)
			fontSize = 5;
	}
	
	public Icon(Critter c) {
		this.c=c;
		
		String[] temp;
		String text_fill = c.viewTextColor().toString().replace("0x", "");
			temp = text_fill.split("(?<=\\G..)");
			text_fill = "rgba(" + Integer.parseInt(temp[0],16) + ", " + Integer.parseInt(temp[1],16) + ", " + Integer.parseInt(temp[2],16) + ", " + Integer.parseInt(temp[3],16) + ")";
		String bg_color = c.viewFillColor().toString().replace("0x", "");
			temp = bg_color.split("(?<=\\G..)");
			bg_color = "rgba(" + Integer.parseInt(temp[0],16) + ", " + Integer.parseInt(temp[1],16) + ", " + Integer.parseInt(temp[2],16) + ", " + Integer.parseInt(temp[3],16) + ")";
		String border_color = c.viewOutlineColor().toString().replace("0x", "");
			temp = border_color.split("(?<=\\G..)");
			border_color = "rgba(" + Integer.parseInt(temp[0],16) + ", " + Integer.parseInt(temp[1],16) + ", " + Integer.parseInt(temp[2],16) + ", " + Integer.parseInt(temp[3],16) + ")";
		
		body = new Button(c.toString());
		body.setStyle(
				  "-fx-font-family: Helvetica;"
				+ "-fx-font-size: " + fontSize + ";"
				+ "-fx-text-fill: " + text_fill + ";"
				+ "-fx-background-radius: 5em;"
				+ "-fx-background-color: " + bg_color + ";"
				+ "-fx-background-insets: 0;"
				+ "-fx-border-style: solid;"
				+ "-fx-border-width: 1;"
				+ "-fx-border-color: " + border_color + ";"
				+ "-fx-border-radius: 5em;"
				+ "-fx-border-insets: 0;"
				+ "-fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 2, 0.0 , 0 , 1 );"
				+ "-fx-min-width: 2em;"
				+ "-fx-min-height: 2em;"
				+ "-fx-max-width: 2em;"
				+ "-fx-max-height: 2em;"
				+ "-fx-padding:0px;");
	}
}
