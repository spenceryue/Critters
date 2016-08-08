/* CRITTERS
 * EE422C Project 4 submission by
 * Spencer Yue
 * STY223
 * https://github.com/spenceryue/critters
 * Slip days used: 2
 * Summer 2016
 */
package project4;

import javafx.scene.CacheHint;
//import javafx.scene.control.Button;
import javafx.scene.control.Label;
//import javafx.scene.text.Text;

public class Icon {
	Critter c;
//	Button body;
	Label body;
//	Text body;
	String text_fill;
	String bg_color;
	String border_color;

	// y = -0.0778x + 12.697	where x = sqrt(width*height)
	//static double fontSize = -0.0778*(Math.max(Params.world_width,Params.world_height)) + 12.697;
	static double fontSize = -0.0778*(Math.max(Viewer.rows,Viewer.cols)) + 12.697;
	static {
		if (fontSize > 12.5)
			fontSize = 12.5;
		else if (fontSize < 5)
			fontSize = 5;
	}
	
	public Icon(Critter c, boolean quality) {
		this(c);
		if (quality)
			body.setStyle(body.getStyle()+"-fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 2, 0.0 , 0 , 1 );");
	}
	public Icon(Critter c) {
		this.c=c;
		
		String[] temp;
		text_fill = c.viewTextColor().toString().replace("0x", "");
			temp = text_fill.split("(?<=\\G..)");
			text_fill = "rgba(" + Integer.parseInt(temp[0],16) + ", " + Integer.parseInt(temp[1],16) + ", " + Integer.parseInt(temp[2],16) + ", " + Integer.parseInt(temp[3],16) + ")";
		bg_color = c.viewFillColor().toString().replace("0x", "");
			temp = bg_color.split("(?<=\\G..)");
			bg_color = "rgba(" + Integer.parseInt(temp[0],16) + ", " + Integer.parseInt(temp[1],16) + ", " + Integer.parseInt(temp[2],16) + ", " + Integer.parseInt(temp[3],16) + ")";
		border_color = c.viewOutlineColor().toString().replace("0x", "");
			temp = border_color.split("(?<=\\G..)");
			border_color = "rgba(" + Integer.parseInt(temp[0],16) + ", " + Integer.parseInt(temp[1],16) + ", " + Integer.parseInt(temp[2],16) + ", " + Integer.parseInt(temp[3],16) + ")";
		
//		body = new Text(c.toString());
		body = new Label(c.toString());
//		body = new Button(c.toString());
		body.setStyle(
				  "-fx-alignment: center;\n"
				+ "-fx-font-family: Helvetica;\n"
				+ "-fx-font-size: " + fontSize + ";\n"
				+ "-fx-text-fill: " + text_fill + ";\n"
				+ "-fx-background-radius: 5em;\n"
				+ "-fx-background-color: " + bg_color + ";\n"
				+ "-fx-background-insets: 0;\n"
				+ "-fx-border-style: solid;\n"
				+ "-fx-border-width: 1;\n"
				+ "-fx-border-color: " + border_color + ";\n"
				+ "-fx-border-radius: 5em;\n"
				+ "-fx-border-insets: 0;\n"
//				+ "-fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 2, 0.0 , 0 , 1 );\n"
				+ "-fx-min-width: 2em;\n"
				+ "-fx-min-height: 2em;\n"
				+ "-fx-max-width: 2em;\n"
				+ "-fx-max-height: 2em;\n"
				+ "-fx-padding:0px;\n");
		
		body.setCache(true);
		body.setCacheHint(CacheHint.SPEED);
	}
	
	public void setBackgroundColorFromDefault(String colorName) {
		String change = body.getStyle().replace("-fx-background-color: " + bg_color + ";", "-fx-background-color: " +colorName+";");
		body.setStyle(change);
	}
	
	public void resizeFromDefault(double width, double height) {
		String change = body.getStyle().replace("-fx-min-width: 2em;", "-fx-min-width: "+width+";");
		change = change.replace("-fx-max-width: 2em;", "-fx-max-width: "+width+";");
		change = change.replace("-fx-min-height: 2em;", "-fx-min-height: "+height+";");
		change = change.replace("-fx-max-height: 2em;", "-fx-max-height: "+height+";");
		change = change.replace("-fx-font-size: " + fontSize + ";", "-fx-font-size: " + 2*Math.max(width, height)/fontSize + ";");
		change = change.replace("-fx-border-width: 1;", "-fx-border-width: "+Math.max(width, height)/fontSize/2+";");
		body.setStyle(change);
	}
}
