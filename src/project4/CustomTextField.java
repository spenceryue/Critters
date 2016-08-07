/* CRITTERS
 * EE422C Project 4 submission by
 * Spencer Yue
 * STY223
 * https://github.com/spenceryue/critters
 * Slip days used: 2
 * Summer 2016
 */
package project4;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
//import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
//import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

class CustomTextField {
	static String critter =null;
	static String critterCount =null;
	static String steps =null;
	static HBox[] made = new HBox[4];
	static String[] madeSelector = new String[4];
	static int index = 0;
	
	final String prompt;
	final String selector;
	TextField field;
	Button confirm;
	HBox container;
	
	final EventHandler<ActionEvent> fieldEnterHandle;
	final EventHandler<? super MouseEvent> confirmClickHandle;
	
	
	public CustomTextField(String msg, String selector){
		prompt = msg;
		this.selector = selector;
		
		field = new TextField();
		field.setId("custom-field");
		field.maxWidthProperty().bind(field.prefWidthProperty());
		field.maxHeightProperty().bind(field.prefHeightProperty());
		field.promptTextProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				if (field.getPromptText() != null)
					field.setMinWidth(field.getPromptText().length() * 6.3);
			}
		} );
		field.setPromptText(prompt);
		
		confirm = new Button("→");
		confirm.setId("custom-confirm");
		confirm.setPrefWidth(26.01);
		confirm.minWidthProperty().bind(confirm.prefWidthProperty());
		confirm.maxWidthProperty().bind(confirm.prefWidthProperty());
		confirm.maxHeightProperty().bind(field.maxHeightProperty());
		confirm.setFocusTraversable(false);
		
		container = new HBox();
		container.setId(selector);
		container.getChildren().addAll(field,confirm);
		container.setOnMouseEntered((event) -> {
			if (!container.getId().contains("-focused"))
					container.setId(container.getId().replace("-focused","")+"-focused");
		});
		container.setOnMouseExited((event) -> {
			container.setId(container.getId().replace("-focused",""));
		});
		
		fieldEnterHandle = new ConfirmHandler();
		confirmClickHandle = new ConfirmHandlerMouse();
		
		field.setOnAction(fieldEnterHandle);
		confirm.setOnMouseClicked(confirmClickHandle);
		
		field.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (field.isFocused()) {
					field.clear();
					container.setId(container.getId().replace("-focused","")+"-focused");
				} else
					container.setId(container.getId().replace("-focused",""));
			}
		});
		
		if (prompt.equals("How Many Critters?")) {
			container.setDisable(true);
			container.setVisible(false);
		}
		
		made[index] = container;
		madeSelector[index++] = selector;
		
		Viewer.layer0_white_bg.requestFocus();
	}
	
	private class SeedHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent arg0) {
			String input = field.textProperty().getValueSafe();
			
			if (input.length() == 0)
				return;
			
			String keyText = null;
			
			try {
				Critter.setSeed(Long.parseLong(input));
				
				field.clear();
				
				keyText = "Seed Set: "+input;
				Viewer.layer0_white_bg.requestFocus();
				
				container.setId("custom-container-blue");
				
			} catch (NumberFormatException e) {
				field.clear();

				keyText = "Enter an Integer";
				Viewer.layer0_white_bg.requestFocus();
				
				container.setId("custom-container-red");
			}
			
			Timeline delay = new Timeline(
					new KeyFrame(
							Duration.ZERO,
							"k0",
							new KeyValue(field.promptTextProperty(),keyText)),
					new KeyFrame(
							Duration.seconds(1),
							"k1",
							new DelayHandler(),
							new KeyValue(field.promptTextProperty(), ""))
				);
			
			delay.play();
		}
	}
	
	private class DelayHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			field.setPromptText(prompt);
			field.requestFocus();
			container.setId(selector);
		}
	}
	
	private class ConfirmHandlerMouse implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			if (prompt.toLowerCase().equals("set seed"))
				new SeedHandler().handle(null);
			else
				fieldEnterHandle.handle(null);
		}
	}

	private class ConfirmHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			if (prompt.toLowerCase().equals("set seed"))
				new SeedHandler().handle(null);
			
			else if (checkError(field.textProperty().getValueSafe()))
				reportError();
			else
				updateStuff();
		}
		
		// true means Error detected
		private boolean checkError(String input) {
			if (input.length()==0)
				return true;
			if (prompt.toLowerCase().equals("add a critter"))
				return (critter=Main.match(input))==null;
			if (prompt.toLowerCase().contains("how many"))
				try {
					int in = Integer.parseInt(input);
					return in <= 0;
				} catch (NumberFormatException e) {
					return true;
				}
			else
				return true;
		}
		
		private void reportError() {
			String input = field.textProperty().getValueSafe();
			
			if (input.length()==0)
				return;
			
			field.clear();
			String keyText = null;
			
			if (prompt.toLowerCase().equals("add a critter")) {
				keyText ="Unrecognized Critter: "+input;
				
				HBox next = findMade("layer4-how-many-critters");
				TextField nextField = (TextField) next.lookup("#custom-field");
				next.setVisible(false);
				next.setDisable(true);
				nextField.setPromptText("How Many Critters?");
			}
			
			else if (prompt.toLowerCase().contains("how many "))
				keyText ="Enter a Positive Integer";
			
			Viewer.layer0_white_bg.requestFocus();
			
			container.setId("custom-container-red");
			
			Timeline delay = new Timeline(
					new KeyFrame(
							Duration.ZERO,
							"k0",
							new KeyValue(field.promptTextProperty(),keyText)),
					new KeyFrame(
							Duration.seconds(1),
							"k1",
							new DelayHandler(),
							new KeyValue(field.promptTextProperty(), "")
					)
				);
			delay.play();
		}
		
		private void updateStuff() {
			String input = field.textProperty().getValueSafe();
			
			HBox next = findMade("layer4-how-many-critters");
			TextField nextField = (TextField) next.lookup("#custom-field");
			Button nextConfirm = (Button) next.lookup("#custom-confirm");
			
			container.setId("custom-container-blue");
			field.clear();
			
			class FadeHandler implements EventHandler<ActionEvent> {
				@Override
				public void handle(ActionEvent e) {
					if (container.equals(findMade("layer4-add-a-critter"))) {
						container.setId(selector);
						nextField.requestFocus();
						
					} else if (container.equals(findMade("layer4-how-many-critters"))) {
						container.setDisable(true);
						container.setVisible(false);
						container.setOpacity(1.0);
						field.setPromptText(prompt);
						critterCount = null;
						container.setId(selector);
						
						HBox c = findMade("layer4-add-a-critter");
						TextField f = (TextField)c.lookup("#custom-field");
						f.setPromptText("Add a Critter");
						critter = null;
						c.setId("layer4-add-a-critter");
						
						f.requestFocus();
					}
				}
			}
			
			if (prompt.toLowerCase().equals("add a critter")) {
				Viewer.layer0_white_bg.requestFocus();
				
				next.setDisable(false);
				next.setVisible(true);
				
				String myKeyText = "Choice Confirmed: "+(critter);
				
				String nextKeyText = "How Many " + (critter.endsWith("e") || critter.endsWith("s") ?
						   critter : critter+"s") + "?";
				
				field.setPromptText(myKeyText);
				nextField.setPromptText(nextKeyText);
				
				Timeline fade = new Timeline(
						new KeyFrame(
								Duration.ZERO,
								"k0",
								new KeyValue(field.promptTextProperty(),myKeyText),
								new KeyValue(nextField.promptTextProperty(),nextKeyText),
								new KeyValue(next.opacityProperty(),0),
								new KeyValue(nextField.opacityProperty(),0),
								new KeyValue(nextConfirm.opacityProperty(),0)),
						new KeyFrame(
								Duration.seconds(1),
								"k1",
								new FadeHandler(),
								new KeyValue(next.opacityProperty(),1),//,Interpolator.EASE_IN),
								new KeyValue(nextField.opacityProperty(),1),//,Interpolator.EASE_IN),
								new KeyValue(nextConfirm.opacityProperty(),1))//,Interpolator.EASE_IN))
						);
				
				fade.play();
			}
			
			else if (prompt.toLowerCase().equals("how many critters?")) {
				for (int i = 0; i < Integer.parseInt(critterCount=input); i++)
					try { Critter.makeCritter(critter); }
					catch (InvalidCritterException e) {}
				Critter.displayWorld();
				for (StatCard sc : Viewer.statCards)
					sc.refresh();
				
				ScrollPane grid = (ScrollPane) Viewer.layer0_white_bg.lookup("#layer2-right-world-scroll-box");
				grid.requestFocus();
				
				String myKeyText =
						"Adding "+(critterCount)+" "
						+ (critter.endsWith("e") || critter.endsWith("s") ?
						   critter : critter+"s");
				
				field.setPromptText(myKeyText);
				
				Timeline fade = new Timeline(
						new KeyFrame(
								Duration.ZERO,
								"k0",
								new KeyValue(field.promptTextProperty(),myKeyText),
								new KeyValue(container.opacityProperty(),1),
								new KeyValue(field.opacityProperty(),1),
								new KeyValue(confirm.opacityProperty(),1)),
						new KeyFrame(
								Duration.seconds(1),
								"k1",
								new FadeHandler(),
								new KeyValue(container.opacityProperty(),0,Interpolator.EASE_OUT),
								new KeyValue(field.opacityProperty(),0,Interpolator.EASE_OUT),
								new KeyValue(confirm.opacityProperty(),0,Interpolator.EASE_OUT))
						);
				
				fade.play();
			}
			
			else if (prompt.toLowerCase().equals("how many steps?")) {
				Viewer.steps = Integer.parseInt(steps=input);
				
				field.setPromptText("Time Steps: " + (steps));
				
				if (Viewer.steps > 1) {
				
//					HBox nextt = (HBox) Viewer.layer0_white_bg.lookup("#layer4-speed");
//					Slider nextSlider = (Slider) nextt.lookup("#custom-slider");
//					
//					nextSlider.requestFocus();
					
					Viewer.layer3_go.requestFocus();
					
					Viewer.steps = Integer.parseInt(steps);
					
					Viewer.layer2_step_counter.setText("Steps Left: "+steps);
					
					Viewer.stepping = false;
					
					Viewer.layer3_go.setText("Go!");
					
				} else {
					
					Viewer.layer3_go.requestFocus();
					
					Viewer.layer3_go.setText("Step");
					
					Viewer.layer2_step_counter.setText("");
					
					Viewer.stepping = true;
				}
				// Delay border fade... (if have time to implement css)
			/*	Timeline delay = new Timeline(
						new KeyFrame(
								Duration.seconds(1),
								"k1",
								new KeyValue(container.idProperty(),selector))
						);
				
				delay.play(); */
			}
		}
	}
	
	public static void clearControls() {
		HBox A = findMade("layer4-add-a-critter");
		TextField a = (TextField) A.lookup("#custom-field");
		a.setPromptText("Add a Critter");
		A.setId("layer4-add-a-critter");
		critter = null;
		
		HBox B = findMade("layer4-how-many-critters");
		TextField b = (TextField) B.lookup("#custom-field");
		B.setDisable(true);
		B.setVisible(false);
		b.setPromptText("How Many Critters?");
		B.setId("layer4-how-many-critters");
		critterCount = null;
		
		HBox C = findMade("layer4-how-many-steps");
		TextField c = (TextField) C.lookup("#custom-field");
		c.setPromptText("How Many Steps?");
		C.setId("layer4-how-many-steps");
		steps = null;
	}
	
/*	public static void initFieldFormats() {
		for (HBox h : made)
			for (Node c : h.getChildren())
				if (c instanceof TextField)
					((TextField) c).getOnAction().handle(null);
	}
*/	
	private static HBox findMade(String selector) {
		for (int i=0; i<made.length; i++)
			if (madeSelector[i].equals(selector))
				return made[i];
		return null;
	}
}