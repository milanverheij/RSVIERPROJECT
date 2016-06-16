package gui;

import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class StartGui extends Application{
	Stage stage;
	VBox vbox;
	StackPane stackPane;
	
	TextField inlogNaamField;
	PasswordField wachtwoordField;
	Button inlogButton;
	Label issues;
	
	final BooleanProperty firstTime = new SimpleBooleanProperty(true); // Variable to store the focus on stage load
	
	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		stage = primaryStage;
		
		inlogNaamField = new TextField();
		wachtwoordField = new PasswordField();
		
		inlogNaamField.setPromptText("Inlognaam");
		wachtwoordField.setPromptText("Wachtwoord");
		
		inlogButton = new Button("Log in");
		issues = new Label();
		
		inlogButton.setOnAction(e -> controleerGegevens());

		populateVbox();
		setStageProperties();
		populateStackPane();
		Scene scene = new Scene(stackPane, 400, 300);
		stage.setScene(scene);
		stage.show();
		haalFocusVanTextField();

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
		Rectangle rect = new Rectangle(400, 300);
		rect.setFill(Color.WHITE);
		
		stackPane.getChildren().add(rect);
		stackPane.getChildren().add(getAchtergrond());
		stackPane.getChildren().add(vbox);
		StackPane.setAlignment(vbox, Pos.CENTER);
	}
	
	private ImageView getAchtergrond(){
		Image image = new Image("\\images\\achtergrond.jpg", 300, 300, false, false);
		return new ImageView(image);
	}
	
	private void setStageProperties(){
		stage.getIcons().add(new Image("\\images\\icon.jpg"));
		stage.setTitle("Harrie's Tweedehands Beessies");
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
		if(inlogNaamField.getText().equals("rs4") && wachtwoordField.getText().equals("1234")){
			HoofdGui hoofd = new HoofdGui();
			try {
				hoofd.start(new Stage());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			stage.close();
		}else{
			issues.setText("Incorrecte inloggegevens");
		}
	}

}
