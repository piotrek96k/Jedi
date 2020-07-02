package com.piotrek.main;

import com.piotrek.util.FxmlUtil;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;

public class ApplicationMain extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader(FxmlUtil.VIEW_URL);
		SplitPane pane = loader.load();
		stage.setScene(new Scene(pane));
		stage.show();
	}

}
