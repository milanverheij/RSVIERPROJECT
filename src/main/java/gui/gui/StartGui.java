package gui.gui;

import gui.model.GuiPojo;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import logger.DeLogger;

import java.awt.*;

public class StartGui extends Application{
	Stage stage;
	VBox vbox;
	StackPane stackPane;

	TextField inlogNaamField;
	PasswordField wachtwoordField;
	Button inlogButton;
	Label issues;

	MenuBar menuBar;

	Image backgroundImage = new Image("/images/achtergrond.jpg");

	String databaseSelected = "MySQL";
	String connectionSelected = "HikariCP";

	final BooleanProperty firstTime = new SimpleBooleanProperty(true); // Variable to store the focus on stage load

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		stage = primaryStage;

		makeMenus();
		maakDisplayItems();
		populateVbox();
		setStageProperties();
		populateStackPane();

		VBox box = new VBox();
		box.getChildren().addAll(menuBar, stackPane);

		Scene scene = new Scene(box, stage.getWidth(), stage.getHeight());

		stage.setResizable(false);
		stage.setScene(scene);
		stage.show();
		haalFocusVanTextField();
	}

	private void maakDisplayItems() {
		inlogNaamField = new TextField();
		wachtwoordField = new PasswordField();

		inlogNaamField.setPromptText("Inlognaam");
		wachtwoordField.setPromptText("Wachtwoord");

		inlogButton = new Button("Log in");
		issues = new Label();

		inlogButton.setOnAction(e -> controleerGegevens());
	}

	private void makeMenus() {
		menuBar = new MenuBar();
		ToggleGroup databaseGroup = new ToggleGroup();
		ToggleGroup connectionGroup = new ToggleGroup();

		RadioMenuItem fireBirdMenuItem = new RadioMenuItem("FireBird");
		fireBirdMenuItem.setOnAction(e -> databaseSelected = "FireBird");
		fireBirdMenuItem.setToggleGroup(databaseGroup);

		RadioMenuItem mysqlMenuItem = new RadioMenuItem("MySQL");
		mysqlMenuItem.setSelected(true);
		mysqlMenuItem.setOnAction(e -> databaseSelected = "MySQL");
		mysqlMenuItem.setToggleGroup(databaseGroup);

		Menu databaseSelectorMenu = new Menu("Database");
		databaseSelectorMenu.getItems().addAll(mysqlMenuItem, fireBirdMenuItem);

		RadioMenuItem hikariMenuItem = new RadioMenuItem("HikariCP");
		hikariMenuItem.setOnAction(e -> connectionSelected = "HikariCP");
		hikariMenuItem.setToggleGroup(connectionGroup);
		hikariMenuItem.setSelected(true);

		RadioMenuItem c3poMenuItem = new RadioMenuItem("C3PO");
		c3poMenuItem.setOnAction(e -> connectionSelected = "c3po");
		c3poMenuItem.setToggleGroup(connectionGroup);

		Menu connectionSelectorMenu = new Menu("Connectie");
		connectionSelectorMenu.getItems().addAll(hikariMenuItem, c3poMenuItem);

		hikariMenuItem.setSelected(true);
		mysqlMenuItem.setSelected(true);

		menuBar.getMenus().addAll(databaseSelectorMenu, connectionSelectorMenu);
	}

	private void haalFocusVanTextField(){
		inlogNaamField.focusedProperty().addListener((observable,  oldValue,  newValue) -> {
			if(newValue && firstTime.get()){
				vbox.requestFocus();
				firstTime.setValue(false);
			}
		});
	}

	private void populateStackPane(){
		stackPane = new StackPane();
		Rectangle rect = new Rectangle(backgroundImage.getWidth(), backgroundImage.getHeight());
		rect.setFill(Color.WHITE);

		stackPane.getChildren().add(rect);
		stackPane.getChildren().add(getAchtergrond());
		stackPane.getChildren().add(vbox);
		StackPane.setAlignment(vbox, Pos.CENTER);
	}

	private ImageView getAchtergrond(){
		Image image = backgroundImage;
		return new ImageView(image);
	}

	private void setStageProperties(){
		stage.getIcons().add(new Image("/images/icon.png"));
		stage.setTitle("Harrie's Tweedehands Beessies");

		stage.setWidth(backgroundImage.getWidth());
		stage.setHeight(backgroundImage.getHeight() + 60);
	}

	private void populateVbox(){
		vbox = new VBox();
		vbox.setPadding(new Insets(5));
		vbox.setSpacing(10);

		vbox.getChildren().addAll(inlogNaamField, wachtwoordField, inlogButton, issues);
		vbox.setMaxHeight(108);
		vbox.setMaxWidth(159);
	}

	private void controleerGegevens(){
		if(inlogNaamField.getText().equals("Harrie") && wachtwoordField.getText().equals("1234")){
			HoofdGui hoofd = new HoofdGui();
			try {
				hoofd.setConnection(databaseSelected, connectionSelected);
				hoofd.start(new Stage());
				DeLogger.getLogger().info("Succesvol ingelogd");
			} catch (Exception e) {
				DeLogger.getLogger().error(e.getMessage());
				e.printStackTrace();
			}
			stage.close();
		}else{
			GuiPojo.errorBox.setMessageAndStart("Incorrecte inloggegevens");
		}
	}
}