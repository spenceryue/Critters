/* CRITTERS
 * EE422C Project 4 submission by
 * Spencer Yue
 * STY223
 * https://github.com/spenceryue/critters
 * Slip days used: 2
 * Summer 2016
 */
package project4;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

public class CustomToggleSet {

	final String prompt;
	final String selector;
	Label info;
	ToggleButton yes;
	ToggleButton no;
	HBox container;
	
	public CustomToggleSet(String selector, String msg, String... options) {
		prompt = msg;
		this.selector=selector;
		
		info = new Label(prompt);
		info.setId("custom-toggle-info");
		
		yes = new ToggleButton("Y");
		yes.setId("custom-toggle-button");
		yes.setOnMouseReleased(null);
		
		no = new ToggleButton ("N");
		no.setId("custom-toggle-button");
		no.setOnMouseReleased(null);
		
		ToggleGroup t = new ToggleGroup();
		t.getToggles().addAll(yes,no);
		t.selectToggle(no);
		no.setId(no.getId()+"-on");
		
		container = new HBox();
		container.setId(selector);
		container.getChildren().addAll(info,yes,no);
		
		yes.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (oldValue == newValue)
					return;
				
				Viewer.skip = !Viewer.skip;
				if (!Viewer.skip) {
					yes.setId("custom-toggle-button");
					no.setId(no.getId()+"-on");
				} else {
					yes.setId(yes.getId()+"-on");
					no.setId("custom-toggle-button");
				}
				System.out.println(Viewer.skip);
			}
		} );
	}
}
