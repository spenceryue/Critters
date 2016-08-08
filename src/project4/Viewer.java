/* CRITTERS
 * EE422C Project 4 submission by
 * Spencer Yue
 * STY223
 * https://github.com/spenceryue/critters
 * Slip days used: 2
 * Summer 2016
 */
package project4;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
//import javafx.scene.shape.Rectangle;
//import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Viewer extends Application {

	static double screenWidth;
	static double screenHeight;
	
	public static void main(String[] args) {
		Params.world_width = Integer.parseInt(args[0]);
		Params.world_height = Integer.parseInt(args[1]);
		System.out.println(Params.world_width + " " + Params.world_height);
		rows = Params.world_width;
		cols = Params.world_height;
		stageWidth = Icon.fontSize*2.2*rows+505;
		stageHeight = Icon.fontSize*2*cols > 380 ? Icon.fontSize*2*cols : 380;
		Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
		screenWidth = screenSize.getWidth();
		screenHeight = screenSize.getHeight();
		if (stageWidth > screenWidth)
			stageWidth = screenWidth;
		if (stageHeight > screenHeight)
			stageHeight = screenHeight;
		launch(args);}
	
	static int rows;
	static int cols;
	
	final static boolean GUI = true;
	
	//static double stageWidth = Icon.fontSize*1.5*Params.world_width+480;
	//static double stageHeight = Icon.fontSize*2*Params.world_height > 380 ? Icon.fontSize*3*Params.world_height : 380;
	static double stageWidth;
	static double stageHeight;
	
	static VBox layer1_left_pane; static GridPane layer2_right_row0_world; static GridPane world; static Label layer2_step_counter; static Button layer4_clear; static Button layer4_back; static HBox layer2_left_row1_controls_grid; static Button layer3_go; static CustomTextField layer4_add_a_critter; static CustomTextField layer4_how_many_steps; static CustomTextField layer4_how_many_critters; static CustomSlider layer4_speed; static CustomToggleSet layer3_skip_to_finish; static ComboBox<String> layer4_all_critters; static HBox layer4_critters_clip_box;
	static {
		layer2_right_row0_world = world = new GridPane();
		layer2_right_row0_world.setId("layer2-right-row0-world");
		layer2_step_counter = new Label("");
		layer2_step_counter.setId("layer2-step-counter");
		layer4_clear = new Button("Clear");
		layer4_back = new Button("x");
		icons = new Image[] {StatCard.findPicture(new File("."),"icon_big_green_bordered").getImage(),//{new Image("file:\\images\\icon_big_green_bordered.png"),
				StatCard.findPicture(new File("."),"icon_small_green_bordered").getImage()};//new Image("file:\\images\\icon_small_green_bordered.png") };
		
	}
	
	static HBox layer0_white_bg = new HBox();
	static int steps = 0;
	static double speed = 3.0;
	static String statSelect = "All Critters";
	static boolean skip = false;
//	static GridPane world;
	static boolean playing = false;
	static Timeline routine;
	static ArrayList<StatCard> statCards;
	static boolean stepping = false;
	
	private static Image[] icons;
	private static boolean entered = false;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene s = new Scene(layer0_white_bg);
		primaryStage.setScene(s);
		primaryStage.setWidth(stageWidth);
		primaryStage.setHeight(stageHeight);
		primaryStage.setResizable(true);
		primaryStage.setTitle("Critters");
		primaryStage.getIcons().addAll(icons);
		primaryStage.centerOnScreen();
		layer0_white_bg.setId("layer0-white-bg");
		
			layer1_left_pane = new VBox();
			layer1_left_pane.setId("layer1-left-pane");
	//		layer1_left_pane.setMinWidth(480);
	//		layer1_left_pane.minWidthProperty().bind(layer1_left_pane.prefWidthProperty());;
			layer0_white_bg.getChildren().add(layer1_left_pane);
			
				HBox layer2_left_row0_header = new HBox();
				layer2_left_row0_header.setId("layer2-left-row0-header");
				layer1_left_pane.getChildren().add(layer2_left_row0_header);
				
					Label layer3_welcome = new Label("Welcome ");
					layer3_welcome.setId("layer3-welcome");layer3_welcome.setTextOverrun(OverrunStyle.CLIP);
					//layer3_welcome.setMinWidth(180);
					layer2_left_row0_header.getChildren().add(layer3_welcome);
					
					Label layer3_player = new Label("Player");
					layer3_player.setId("layer3-player");layer3_player.setTextOverrun(OverrunStyle.CLIP);
					//layer3_critter_player.setMinWidth(220);
					layer2_left_row0_header.getChildren().add(layer3_player);
					
					Label layer3_exclamation= new Label("!");
					layer3_exclamation.setId("layer3-exclamation");
					layer2_left_row0_header.getChildren().add(layer3_exclamation);
									
				layer2_left_row1_controls_grid = new HBox();
				layer2_left_row1_controls_grid.setId("layer2-left-row1-controls-grid");
				layer1_left_pane.getChildren().add(layer2_left_row1_controls_grid);
				
			/*		VBox layer3_gap1 = new VBox();
					layer3_gap1.setId("layer3-gap1");
					HBox.setHgrow(layer3_gap1,Priority.ALWAYS);
					layer2_left_row1_controls_grid.getChildren().add(layer3_gap1);
			*/	
					VBox layer3_left_column1 = new VBox();
					layer3_left_column1.setId("layer3-left-column1");
					HBox.setHgrow(layer3_left_column1,Priority.ALWAYS);
					layer2_left_row1_controls_grid.getChildren().add(layer3_left_column1);
					
						layer4_add_a_critter = new CustomTextField("Add a Critter","layer4-add-a-critter");
						layer3_left_column1.getChildren().add(layer4_add_a_critter.container);
						
						layer4_how_many_critters = new CustomTextField("How Many Critters?","layer4-how-many-critters");
						layer3_left_column1.getChildren().add(layer4_how_many_critters.container);
					
			/*		VBox layer3_gap2 = new VBox();
					layer3_gap2.setId("layer3-gap2");
					HBox.setHgrow(layer3_gap2,Priority.ALWAYS);
					layer2_left_row1_controls_grid.getChildren().add(layer3_gap2);
			*/		
					VBox layer3_left_column2 = new VBox();
					layer3_left_column2.setId("layer3-left-column2");
					HBox.setHgrow(layer3_left_column2,Priority.ALWAYS);
					layer2_left_row1_controls_grid.getChildren().add(layer3_left_column2);
					
						layer4_how_many_steps = new CustomTextField("How Many Steps?","layer4-how-many-steps");
						layer3_left_column2.getChildren().add(layer4_how_many_steps.container);
						
						layer4_speed = new CustomSlider("Speed","layer4-speed");
						layer3_left_column2.getChildren().add(layer4_speed.container);
						
			/*		VBox layer3_gap3 = new VBox();
					layer3_gap3.setId("layer3-gap3");
					HBox.setHgrow(layer3_gap3,Priority.ALWAYS);
					layer2_left_row1_controls_grid.getChildren().add(layer3_gap3);
			*/			
				HBox layer2_left_row2_submit_box = new HBox();
				layer2_left_row2_submit_box.setId("layer2-left-row2-submit-box");
				layer1_left_pane.getChildren().add(layer2_left_row2_submit_box);
				
					layer3_skip_to_finish = new CustomToggleSet("layer3-skip-to-finish","Skip to Finish? ","Y","N");
					layer2_left_row2_submit_box.getChildren().add(layer3_skip_to_finish.container);
					
					layer3_go = new Button("Go!");
					layer3_go.setOnMouseClicked(new GoClickHandler());
					layer3_go.setOnKeyPressed((event) -> {
						if (event.getText().equals(" ") || event.getCode() == KeyCode.ENTER)
							layer3_go.getOnMouseClicked().handle(null);
					});
					layer2_left_row2_submit_box.getChildren().add(layer3_go);
				
				VBox layer2_left_row3_stats_box = new VBox();
				layer2_left_row3_stats_box.setId("layer2-left-row3-stats-box");
				VBox.setVgrow(layer2_left_row3_stats_box, Priority.ALWAYS);
				layer1_left_pane.getChildren().add(layer2_left_row3_stats_box);
				
					HBox layer3_stats_select = new HBox();
					layer3_stats_select.setId("layer3-stats-select");
					//layer3_stats_select.widthProperty().bind(layer1_left_pane.getWidth());
					HBox.setHgrow(layer3_stats_select, Priority.ALWAYS);
					layer2_left_row3_stats_box.getChildren().add(layer3_stats_select);
				
						Label layer4_stats_for = new Label("Stats for ");
						layer4_stats_for.setId("layer4-stats-for");
						layer3_stats_select.getChildren().add(layer4_stats_for);
						
						layer4_all_critters = new ComboBox<String>();
						layer4_all_critters.setId("layer4-all-critters");
						layer4_all_critters.setEditable(true);
						String special = "All Critters";
						layer4_all_critters.getItems().add(special);
						layer4_all_critters.getItems().addAll(Main.classes);
						layer4_all_critters.setOnAction((event) -> {
							if (entered)
								return;
							
							entered = true;
							String selected = layer4_all_critters.valueProperty().toString() != null ?
									layer4_all_critters.valueProperty()
									.toString()
									.replace("ObjectProperty [bean: ComboBox[id=layer4-all-critters, styleClass=combo-box-base combo-box], name: value, value: ", "")
									: null;
							selected = selected != null ? selected.replace("]", "") : null;
						    //String selected = layer4_all_critters.getSelectionModel().getSelectedItem();
							if (selected != null && special.toLowerCase().equals(selected.toLowerCase())) {
						    	selected = special;
						    	layer0_white_bg.lookup("#layer5-all-view"+(screenWidth>screenHeight? "-hor":"-ver")).setVisible(true);
								layer0_white_bg.lookup("#layer5-all-view"+(screenWidth>screenHeight? "-hor":"-ver")).setManaged(true);
								layer0_white_bg.lookup("#layer5-all-view"+(screenWidth>screenHeight? "-hor":"-ver")).toFront();
								layer0_white_bg.lookup("#layer5-single-view").setVisible(false);
								layer0_white_bg.lookup("#layer5-single-view").setManaged(false);
								layer0_white_bg.lookup("#layer5-single-view").toBack();
								
								((Group) layer0_white_bg.lookup("#layer6-left")).getChildren().clear();
								((StackPane) layer0_white_bg.lookup("#layer6-details")).getChildren().clear();
								
								for (StatCard sc : statCards) {
									sc.refresh();
									sc.container.setId("stat-card");
								}
								
							} else if (selected != null && (selected = Main.match(selected)) != null) {
								layer0_white_bg.lookup("#layer5-single-view").setVisible(true);
								layer0_white_bg.lookup("#layer5-single-view").setManaged(true);
								layer0_white_bg.lookup("#layer5-single-view").toFront();
								layer0_white_bg.lookup("#layer5-all-view"+(screenWidth>screenHeight? "-hor":"-ver")).setVisible(false);
								layer0_white_bg.lookup("#layer5-all-view"+(screenWidth>screenHeight? "-hor":"-ver")).setManaged(false);
								layer0_white_bg.lookup("#layer5-all-view"+(screenWidth>screenHeight? "-hor":"-ver")).toBack();
								
								((Group) layer0_white_bg.lookup("#layer6-left")).getChildren().clear();
								((StackPane) layer0_white_bg.lookup("#layer6-details")).getChildren().clear();
								for (StatCard sc : statCards)
									if (sc.className.equals(selected)) {
										((Group) layer0_white_bg.lookup("#layer6-left")).getChildren().add(new StatCard(sc.c,true).container);
										((StackPane) layer0_white_bg.lookup("#layer6-details")).getChildren().add(sc.getDetails());
										break;
									}
								
							} else {
					    		layer4_all_critters.setValue("");
					    		entered = false;
					    		return;
				    		}
							
							statSelect = selected;
						    layer0_white_bg.lookup("#layer3-stats-display").requestFocus();
						//    layer4_all_critters.setValue("");
						    layer4_all_critters.setPromptText(selected);

						    System.out.println(selected);
						    entered=false;
						});
						layer4_all_critters.setPromptText("All Critters");
						layer3_stats_select.getChildren().add(layer4_all_critters);
						
						HBox layer4_back_container = new HBox();
						//layer4_back_container.setMinWidth(100);
						HBox.setHgrow(layer4_back_container, Priority.ALWAYS);
						layer4_back_container.setAlignment(Pos.CENTER_RIGHT);
						layer3_stats_select.getChildren().add(layer4_back_container);
//						Button layer4_back = new Button("x");
						layer4_back.setId("layer4-back");
						layer4_back.setOnMouseClicked((event) -> {
							statSelect = special;
							layer4_all_critters.setPromptText(special);
							layer4_all_critters.setValue(special);
						});
						layer4_back_container.getChildren().add(layer4_back);
						
					ScrollPane layer3_stats_display = new ScrollPane();
					layer3_stats_display.setId("layer3-stats-display");
					VBox.setVgrow(layer3_stats_display, Priority.ALWAYS);
					layer2_left_row3_stats_box.getChildren().add(layer3_stats_display);

						// update layer4_critters_clip_box contents' contents with "StatCards"
						// for each created Critter
						layer4_critters_clip_box = new HBox();
						layer4_critters_clip_box.setId("layer4-critters-clip-box");
						layer3_stats_display.setContent(layer4_critters_clip_box);
						
							TilePane layer5_all_view = new TilePane();
							if (screenWidth > screenHeight) {
								layer5_all_view.setId("layer5-all-view-hor");
								layer5_all_view.setPrefColumns(Main.classes.size());
							} else {
								layer5_all_view.setId("layer5-all-view-ver");
								layer5_all_view.setPrefRows(Main.classes.size());
							}
							layer4_critters_clip_box.getChildren().add(layer5_all_view);
							
								statCards = new ArrayList<StatCard>(Main.classes.size());
								for (String p : Main.classes) {
									statCards.add(new StatCard((Critter)Class.forName("project4."+p).newInstance() ) );
									layer5_all_view.getChildren().add(statCards.get(statCards.size()-1).container);
								}
							
							HBox layer5_single_view = new HBox();
							layer5_single_view.setId("layer5-single-view");
							layer5_single_view.setManaged(false);
							layer5_single_view.setVisible(false);
							layer4_critters_clip_box.getChildren().add(layer5_single_view);
							
								Group layer6_left = new Group();
								layer6_left.setId("layer6-left");
								layer5_single_view.getChildren().add(layer6_left);
								
								StackPane layer6_details = new StackPane();
								layer6_details.setId("layer6-details");
								layer5_single_view.getChildren().add(layer6_details);
						
			VBox layer1_right_pane = new VBox();
			layer1_right_pane.setId("layer1-right-pane");
			//layer1_right_pane.maxWidthProperty().bind(world.widthProperty());
			layer0_white_bg.getChildren().add(layer1_right_pane);
			
//				Text layer2_step_counter = new Text("Step 0");
//				layer2_step_counter.setId("layer2-step-counter");
				layer1_right_pane.getChildren().add(layer2_step_counter);
				
				ScrollPane layer2_right_world_scroll_box = new ScrollPane();
				layer2_right_world_scroll_box.setId("layer2-right-world-scroll-box");
				layer2_right_world_scroll_box.setPannable(true);
				layer2_right_world_scroll_box.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
				layer2_right_world_scroll_box.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
				layer1_right_pane.getChildren().add(layer2_right_world_scroll_box);
			
//				GridPane layer2_right_row0_world = world = new GridPane();
//				layer2_right_row0_world.setId("layer2-right-row0-world");
				//layer1_right_pane.getChildren().add(layer2_right_row0_world);
				layer2_right_world_scroll_box.setContent(layer2_right_row0_world);
				
				HBox layer2_right_row1_settings_box = new HBox();
				layer2_right_row1_settings_box.setId("layer2-right-row1-settings-box");
				layer1_right_pane.getChildren().add(layer2_right_row1_settings_box);
				
					HBox layer3_settings_clipped_box = new HBox();
					layer3_settings_clipped_box.setId("layer3-settings-clipped-box");
					layer2_right_row1_settings_box.getChildren().add(layer3_settings_clipped_box);
			
						CustomTextField layer4_set_seed = new CustomTextField("Set Seed","layer4-set-seed");
						layer3_settings_clipped_box.getChildren().add(layer4_set_seed.container);
						
						ImageView layer4_cog_icon = StatCard.findPicture(new File("."), "cog_icon");
//						layer4_cog_icon.fitHeightProperty().bind(layer3_settings_clipped_box.heightProperty());
//				        layer4_cog_icon.setPreserveRatio(true);
//				        layer4_cog_icon.setSmooth(true);
//				        layer4_cog_icon.setCache(true);
				        layer4_cog_icon.setFitWidth(35);
				        layer4_cog_icon.setFitHeight(35);
				        layer4_cog_icon.setId("layer4-cog-icon");
				        layer3_settings_clipped_box.getChildren().add(layer4_cog_icon);
//				        layer4_cog_icon.fitHeightProperty().bind(layer4_set_seed.container.heightProperty());
//				        layer4_cog_icon.maxHeight(layer4_cog_icon.getFitHeight());
				       
						HBox layer4_clear_container = new HBox();
						layer4_clear_container.setMinWidth(screenWidth/2);
						HBox.setHgrow(layer4_clear_container, Priority.ALWAYS);
//						layer4_clear_container.setAlignment(Pos.CENTER_RIGHT);
						layer4_clear_container.setId("layer4-clear-container");
						layer3_settings_clipped_box.getChildren().add(layer4_clear_container);
						
//				        Button layer4_clear = new Button("Clear");
				        layer4_clear.setId("layer4-clear");
				        layer4_clear.setOnMouseClicked((event) -> {
				        	world.getChildren().clear();
				        	spaceMap.clear();
				        	java.util.List<Critter> L =null;
				        	try { L = Critter.getInstances("Critter"); }
				        	catch (Exception e) {}
				        	for (Critter c : L)
				        		while (c.getEnergy() > 0) {
				        			c.walk(0); c.run(0); c.look(0);
				        		}
				        	initWorldGrid();
				        	
				        	layer4_all_critters.setValue("");
							layer4_all_critters.setValue(statSelect);
				        	
				        	CustomTextField.clearControls();
				        	
				        	steps = 0;
				        	
				        	layer2_step_counter.setText("");
				        	
				        	layer4_speed.slider.adjustValue(3.0);
				        	
				        	layer3_skip_to_finish.no.setSelected(true);
				        	
				        	layer4_back.getOnMouseClicked().handle(null);
				        	// clear statCards too...
				        });
				        layer4_clear_container.getChildren().add(layer4_clear);
				        
				 /*       Rectangle clip_mask = new Rectangle();
						clip_mask.layoutXProperty().bind(layer2_right_row1_settings_box.layoutXProperty());
						clip_mask.layoutYProperty().bind(layer2_right_row1_settings_box.layoutYProperty());
						clip_mask.widthProperty().bind(layer4_set_seed.container.widthProperty());
						clip_mask.heightProperty().bind(layer4_set_seed.container.heightProperty());
						layer3_settings_clipped_box.setClip(clip_mask);
				*/
			
        s.getStylesheets().add("project4/critters.css"); 
        primaryStage.setScene(s);
        
//		CustomTextField.initFieldFormats();
		initWorldGrid();
		primaryStage.show();
	//	resizeStatBox();
	}
	
	static void resizeStatBox() {
		int width = 0;
		for (int i=0; i<4 && i<statCards.size(); i++)
			width += statCards.get(statCards.size()-1).container.getMinWidth();
		layer1_left_pane.setMaxWidth(width = Math.max(480, width));
		layer4_critters_clip_box.setMaxWidth(width);
	}
	
	static void initWorldGrid() {
		for (int i=0; i<Params.world_width; i++) {
			Icon filler = new Icon(new Algae());
		//	filler.setBackgroundColorFromDefault("red");
			filler.body.setVisible(false);

			int y=0;
			while (spaceMap.containsKey(new Point(i,y)) && y<Params.world_height)
				y++;
			if (!spaceMap.containsKey(new Point(i,y))) {
				spaceMap.put(new Point(i,0), filler.body);
				world.add(filler.body, i, 0);
			}
			}
		for (int j=1; j<Params.world_height; j++) {
			Icon filler = new Icon(new Algae());
		//	filler.setBackgroundColorFromDefault("red");
			filler.body.setVisible(false);
			
			int x = 0;
			while (spaceMap.containsKey(new Point(x,j)) && x<Params.world_width)
				x++;
			if (!spaceMap.containsKey(new Point(x,j))) {
				spaceMap.put(new Point(0,j), filler.body);
				world.add(filler.body, 0, j);
			}
		}	
	}
	
	static HashMap<Point,Node> spaceMap = new HashMap<Point,Node>();
	
	static class GoClickHandler implements EventHandler<MouseEvent> {
		static void defineRoutine() {
			routine = new Timeline(
					new KeyFrame(
							Duration.millis(1000/(speed*0.6)),
							g,
							new KeyValue(world.opacityProperty(), 1-speed/8),
							new KeyValue(layer2_step_counter.textProperty(),"Step Left: "+(steps-1)))
					);
		}
		
		static void defineRoutineStep() {
			routine = new Timeline(
					new KeyFrame(
							Duration.millis(1000/(speed*0.6)),
							new EventHandler<ActionEvent>() {
								public void handle(ActionEvent e) {
									world.setOpacity(1);
								}
							},
							new KeyValue(world.opacityProperty(), 1-speed/8))
					);
		}
		
		private void begin() {
			System.out.println("Beginning");
			defineRoutine();
			layer2_step_counter.setText("Steps Left: "+steps);
			//layer2_left_row1_controls_grid.setDisable(true);
			layer4_add_a_critter.container.setDisable(true);
			layer4_how_many_steps.container.setDisable(true);
			layer4_how_many_critters.container.setDisable(true);
			layer3_go.setText("Pause");
			
		//	routine.setCycleCount(steps);//routine.setCycleCount(--steps);
			playing = true;
//			Critter.worldTimeStep();
//			Critter.displayWorld();
//			steps--;
			
			routine.play();
		}
		
		static boolean ignore = false;
		
	/*	private String lowerCopy() {
			int lower = Integer.parseInt(copy.getText()) -1;
			String l = Integer.toString(lower);
			
			return l;
		}*/
		
		@Override
		public void handle(MouseEvent arg0) {
			if (ignore || steps <= 0)
				return;
			
			if (stepping) {
				defineRoutineStep();
				routine.play();
				Critter.worldTimeStep();
				Critter.displayWorld();
				
				if (!statSelect.equals("All Critters")) {
					layer4_all_critters.setValue("");
					layer4_all_critters.setValue(statSelect);
				} else
					for (StatCard s : statCards)
						s.refresh();
			}
				
			else if (!playing) {
				if (skip) {
					ignore = true;
					while (steps > 0) {
						Critter.worldTimeStep();
						steps--;
					}
					conclude();
					return;
				}
				if (first++==0)
					begin();
				else {
					transitionToPlay();
					startAnother();
				}
			} else {
				transitionToPause();
				routine.pause();

				if (!statSelect.equals("All Critters"))
					layer4_all_critters.setValue(statSelect);
				else
					for (StatCard s : statCards)
						s.refresh();
				
				Viewer.layer2_step_counter.setText("Steps Left: "+steps);
				Viewer.world.setOpacity(1);
			}
		}
		
		private void transitionToPlay(){
			layer4_add_a_critter.container.setDisable(true);
			layer4_how_many_steps.container.setDisable(true);
			layer4_how_many_critters.container.setDisable(true);
			layer3_go.setText("Pause");
			playing = true;
			}
		private void transitionToPause(){
			layer4_add_a_critter.container.setDisable(false);
			layer4_how_many_steps.container.setDisable(false);
			layer4_how_many_critters.container.setDisable(false);
			layer3_go.setText("Go!");
			playing = false;
			}
		public static void conclude() {
			ignore = false;
			
			layer4_add_a_critter.container.setDisable(false);
			layer4_how_many_steps.container.setDisable(false);
			layer4_how_many_critters.container.setDisable(false);
			
			layer3_go.setText("Go!");
			
			playing = false;
			
			first =0;
			
			if (routine != null)
				routine.stop();

        	CustomTextField.clearControls();
        	
        	steps = 0;
        	
        	Viewer.layer2_step_counter.setText("");
        	
        	Viewer.layer4_speed.slider.adjustValue(3.0);
        	
        	Viewer.layer3_skip_to_finish.no.setSelected(true);
        	
        	layer4_back.getOnMouseClicked().handle(null);
        	
        	initWorldGrid();
        	Critter.displayWorld();
		}
		
		static void startAnother() {
			GoClickHandler.defineRoutine();
			Viewer.routine.play();
		}
	}
	static int first=0;
	static GoHandler g = new GoHandler();
	
	static class GoHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e){
			if(Viewer.steps > 0) {
				Critter.worldTimeStep();
				Critter.displayWorld();
				Viewer.world.setOpacity(1);
				
				if (!statSelect.equals("All Critters")) {
					layer4_all_critters.setValue("");
					layer4_all_critters.setValue(statSelect);
				} else
					for (StatCard s : statCards)
						s.refresh();
				
				Viewer.steps--;
				if (steps > 0)
					GoClickHandler.startAnother();
			}
			if (steps == 0) {
				System.out.println("Completed");
				GoClickHandler.conclude();
			}
		}
	}
}
