

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StartGui extends Application{
	Stage stage;
	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		stage = primaryStage;
		TextField inlogNaamField = new TextField();
		PasswordField wachtwoordField = new PasswordField();
		Button inlogButton = new Button("Log in");
		Label issues = new Label();
		inlogButton.setOnAction(e -> controleerGegevens(inlogNaamField.getText(), wachtwoordField.getText(), issues));

		GridPane grid = new GridPane();
		grid.setHgap(5);
		grid.setVgap(2);

		grid.add(new Label("Inloggen"), 0, 0);
		grid.add(new Label("Inlognaam"), 0, 1);
		grid.add(new Label("Wachtwoord"), 0, 2);
		grid.add(inlogNaamField, 1, 1);
		grid.add(wachtwoordField, 1, 2);
		grid.add(inlogButton, 0, 3);

		VBox vbox = new VBox();
		vbox.setPadding(new Insets(5));

		vbox.getChildren().addAll(grid, issues);

		Scene scene = new Scene(vbox);
		stage.setScene(scene);
		stage.show();
	}

	private void controleerGegevens(String inlogNaam, String wachtwoord, Label issues){
		if(inlogNaam.equals("rs4") && wachtwoord.equals("1234")){
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
