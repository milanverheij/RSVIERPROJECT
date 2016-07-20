package gui.gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ErrorBox extends Application{

	String errorMessage;

	public void setMessageAndStart(String errorMessage){
		this.errorMessage = errorMessage;
		try {
			start(new Stage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start(Stage errorStage) throws Exception {
		Image alertImage = new Image("/images/alert.png", 30, 30, true, true);
		ImageView alertImageView = new ImageView(alertImage);

		VBox vBox = new VBox();
		HBox imageEnLabelBox = new HBox();

		vBox.setAlignment(Pos.CENTER);
		imageEnLabelBox.setAlignment(Pos.CENTER);

		imageEnLabelBox.setSpacing(10);
		vBox.setSpacing(10);

		Button okeButton = new Button("Oke");
		okeButton.setOnAction(e -> errorStage.close());

		imageEnLabelBox.getChildren().add(alertImageView);
		imageEnLabelBox.getChildren().add(new Label(errorMessage));

		vBox.getChildren().add(imageEnLabelBox);
		vBox.getChildren().add(okeButton);

		errorStage.setTitle("Dat doettie anders nooit...");
		errorStage.getIcons().add(new Image("/images/icon.png"));
		errorStage.setScene(new Scene(vBox, 400, 150));
		errorStage.show();
	}
}
