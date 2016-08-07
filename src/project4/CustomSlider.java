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
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;

public class CustomSlider {

	final String prompt;
	final String selector;
	Label info;
	Slider slider;
	HBox container;
	
	public CustomSlider(String msg, String selector) {
		prompt = msg;
		this.selector = selector;
		
		info = new Label(prompt);
		info.setId("custom-info");
		
		slider = new Slider(1,5,3);
		slider.setId("custom-slider");
//		slider.setShowTickLabels(true);
		slider.setShowTickMarks(true);
		slider.setMajorTickUnit(1);
		slider.setMinorTickCount(0);
		slider.setBlockIncrement(1);
		slider.setOrientation(Orientation.HORIZONTAL);
		slider.setSnapToTicks(true);
		slider.setBlockIncrement(1);
		
		slider.maxWidthProperty().bind(slider.prefWidthProperty());
		slider.maxHeightProperty().bind(slider.prefHeightProperty());
		
		slider.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (slider.isFocused())
					container.setId(container.getId()+"-focused");
				else
					container.setId(container.getId().replace("-focused",""));
			}
		});
		
		container = new HBox();
		container.setId(selector);
		container.getChildren().addAll(info,slider);
		container.setOnMouseEntered((event) -> {
			if (!container.getId().contains("-focused"))
					container.setId(container.getId()+"-focused");
		});
		container.setOnMouseExited((event) -> {
			container.setId(container.getId().replace("-focused",""));
		});
		
		slider.valueProperty().addListener(new ChangeListener<Number> () {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				if (arg0.getValue().doubleValue() % 1.0 != 0)
					return;
				Viewer.speed = arg0.getValue().doubleValue();
				System.out.println(Viewer.speed);
			}
		});
		
		slider.adjustValue(3.0);
	}
}
