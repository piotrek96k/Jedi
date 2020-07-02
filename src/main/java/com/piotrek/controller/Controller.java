package com.piotrek.controller;

import java.io.File;
import java.sql.SQLException;
import java.util.Comparator;

import com.piotrek.exception.NameException;
import com.piotrek.exception.ValueException;
import com.piotrek.model.Jedi;
import com.piotrek.model.LightsaberColor;
import com.piotrek.model.Order;
import com.piotrek.model.Side;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class Controller {

	@FXML
	private TableView<Order> ordersTableView;

	@FXML
	private TableView<Jedi> orderJedisTableView;

	@FXML
	private TextField orderNameField;

	@FXML
	private ChoiceBox<Order> orderChoiceBox;

	@FXML
	private Button addJedisButton;

	@FXML
	private TableView<Jedi> freeJedisTableView;

	@FXML
	private TableView<Jedi> jedisTableView;

	@FXML
	private TextField jediNameField;

	@FXML
	private Button orderRegisterButton;

	@FXML
	private Button orderClearButton;

	@FXML
	private ChoiceBox<LightsaberColor> lightsaberChoiceBox;

	@FXML
	private Slider powerSlider;

	@FXML
	private Label minPowerLabel;

	@FXML
	private Label currentPowerLabel;

	@FXML
	private ToggleGroup sidesGroup;

	@FXML
	private RadioButton lightRadioButton;

	@FXML
	private RadioButton darkRadioButton;

	@FXML
	private Button jediRegisterButton;

	@FXML
	private Button jediClearButton;

	@FXML
	private Button jediImportButton;

	@FXML
	private Button jediExportButton;

	@FXML
	private Button orderImportButton;

	@FXML
	private Button orderExportButton;

	@FXML
	private TextField jediPathField;

	@FXML
	private TextField orderPathField;

	private ExtensionFilter jediExtensionFilter;

	private ExtensionFilter orderExtensionFilter;

	private Order lastOrder;

	{
		jediExtensionFilter = new ExtensionFilter("Jedi Files", "*" + Jedi.JEDI_FILE_EXTENSION);
		orderExtensionFilter = new ExtensionFilter("Order Files", "*" + Order.ORDER_FILE_EXTENSION);
	}

	@FXML
	private void initialize() {
		load();
		initJedisTable();
		lightsaberChoiceBox.getItems().addAll(LightsaberColor.values());
		initPowerSlider();
		initSideRadioButtons();
		initJediRegisterButton();
		initJediClearButton();
		initOrdersTable();
		initFreeJedisTableView();
		initOrderRegistryButton();
		initOrderClearButton();
		loadOrderChoiceBox();
		initAddJedisButton();
		initJediImportButton();
		initJediEXportButton();
		initOrderImportButton();
		initOrderExportButton();
	}

	private void openAlertDialog(String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Błąd");
		alert.setHeaderText("Nie udało połączyć się z bazą danych :(");
		alert.setContentText(message);

		alert.showAndWait();
	}

	private void initOrderExportButton() {
		orderExportButton.setOnAction(event -> {
			FileChooser fileChooser = getSaveFileChooser(orderExtensionFilter);
			File file = fileChooser.showSaveDialog(orderExportButton.getScene().getWindow());
			Order.saveInFile(file);
			if (file != null)
				orderPathField.setText(file.toString());
		});
	}

	private void initOrderImportButton() {
		orderImportButton.setOnAction(event -> {
			FileChooser fileChooser = getOpenFileChooser(orderExtensionFilter);
			File file = fileChooser.showOpenDialog(orderImportButton.getScene().getWindow());
			try {
				Order.readFromFile(file);
			} catch (SQLException e) {
				openAlertDialog(e.getMessage());
			}
			if (file != null)
				orderPathField.setText(file.toString());
			loadJedisTableView();
			loadFreeJedisTableView();
			loadOrderJedisTableView(lastOrder);
			loadOrderTableView();
			loadOrderChoiceBox();
		});
	}

	private void initJediEXportButton() {
		jediExportButton.setOnAction(event -> {
			FileChooser fileChooser = getSaveFileChooser(jediExtensionFilter);
			File file = fileChooser.showSaveDialog(jediExportButton.getScene().getWindow());
			Jedi.saveInFile(file);
			if (file != null)
				jediPathField.setText(file.toString());
		});
	}

	private void initJediImportButton() {
		jediImportButton.setOnAction(event -> {
			FileChooser fileChooser = getOpenFileChooser(jediExtensionFilter);
			File file = fileChooser.showOpenDialog(jediImportButton.getScene().getWindow());
			try {
				Jedi.readFromFile(file);
			} catch (SQLException e) {
				openAlertDialog(e.getMessage());
			}
			if (file != null)
				jediPathField.setText(file.toString());
			loadJedisTableView();
			loadFreeJedisTableView();
			loadOrderJedisTableView(lastOrder);
			loadOrderTableView();
			loadOrderChoiceBox();
		});
	}

	private FileChooser getSaveFileChooser(ExtensionFilter extensionFilter) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Zapisz Plik");
		fileChooser.getExtensionFilters().add(extensionFilter);
		return fileChooser;
	}

	private FileChooser getOpenFileChooser(ExtensionFilter extensionFilter) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Otwórz plik");
		fileChooser.getExtensionFilters().add(extensionFilter);
		return fileChooser;
	}

	private void initAddJedisButton() {
		addJedisButton.disableProperty().bind(orderChoiceBox.getSelectionModel().selectedIndexProperty().lessThan(0)
				.or(freeJedisTableView.getSelectionModel().selectedIndexProperty().lessThan(0)));
		addJedisButton.setOnAction(event -> {
			Order order = orderChoiceBox.getSelectionModel().getSelectedItem();
			for (Jedi jedi : freeJedisTableView.getSelectionModel().getSelectedItems()) {
				try {
					jedi.addOrder(order);
					order.addJedi(jedi);
				} catch (SQLException e) {
					openAlertDialog(e.getMessage());
				}
			}
			orderChoiceBox.getSelectionModel().clearSelection();
			loadJedisTableView();
			loadFreeJedisTableView();
			loadOrderJedisTableView(lastOrder);
		});
	}

	private void loadOrderChoiceBox() {
		orderChoiceBox.getItems().clear();
		for (Order order : Order.getOrdersList())
			orderChoiceBox.getItems().add(order);
	}

	private void initOrderRegistryButton() {
		orderRegisterButton.disableProperty().bind(orderNameField.textProperty().isEmpty());
		orderRegisterButton.setOnAction(event -> {
			try {
				Order order = Order.addOrder(orderNameField.getText());
				ordersTableView.getItems().add(order);
				orderChoiceBox.getItems().add(order);
			} catch (SQLException e) {
				openAlertDialog(e.getMessage());
			} catch (NameException e) {
				e.printStackTrace();
			}
			orderNameField.clear();
		});
	}

	private void initOrderClearButton() {
		orderClearButton.setOnAction(event -> orderNameField.clear());
	}

	private void initFreeJedisTableView() {
		freeJedisTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		addColumn(freeJedisTableView, "Id", "id");
		addColumn(freeJedisTableView, "Nazwa", "name");
		addColumn(freeJedisTableView, "Moc", "power");
		addColumn(freeJedisTableView, "Strona Mocy", "side");
		loadFreeJedisTableView();
	}

	private void loadFreeJedisTableView() {
		freeJedisTableView.getItems().clear();
		for (Jedi jedi : Jedi.getJedisList())
			if (jedi.getOrderId() == 0)
				freeJedisTableView.getItems().add(jedi);
	}

	private void initOrdersTable() {
		addColumn(ordersTableView, "Id", "id");
		addColumn(ordersTableView, "Name", "name");
		addColumn(orderJedisTableView, "Id", "id");
		addColumn(orderJedisTableView, "Name", "name");
		loadOrderTableView();
		ordersTableView.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> loadOrderJedisTableView(newValue));
	}

	private void loadOrderTableView() {
		ordersTableView.getItems().clear();
		for (Order order : Order.getOrdersList())
			ordersTableView.getItems().add(order);
	}

	private void loadOrderJedisTableView(Order order) {
		if (order == null)
			return;
		lastOrder = order;
		orderJedisTableView.getItems().clear();
		for (Jedi jedi : order.getJedisList())
			orderJedisTableView.getItems().add(jedi);
		orderJedisTableView.getItems().sort(Comparator.comparing(Jedi::getId));
	}

	private void initJediClearButton() {
		jediClearButton.setOnAction(event -> clearJediDataInput());
	}

	private void clearJediDataInput() {
		jediNameField.clear();
		lightsaberChoiceBox.getSelectionModel().clearSelection();
		powerSlider.setValue(0.0);
		lightRadioButton.setSelected(true);
	}

	private void initJediRegisterButton() {
		jediRegisterButton.disableProperty().bind(jediNameField.textProperty().isEmpty()
				.or(lightsaberChoiceBox.getSelectionModel().selectedIndexProperty().lessThan(0)));
		jediRegisterButton.setOnAction(event -> {
			String name = jediNameField.getText();
			LightsaberColor lightsaberColor = lightsaberChoiceBox.getSelectionModel().getSelectedItem();
			int power = (int) powerSlider.getValue();
			Side side = (Side) sidesGroup.getSelectedToggle().getUserData();
			try {
				jedisTableView.getItems().add(Jedi.addJedi(name, lightsaberColor, power, side));
			} catch (SQLException e) {
				openAlertDialog(e.getMessage());
			} catch (NameException | ValueException e) {
				e.printStackTrace();
			}
			clearJediDataInput();
			loadFreeJedisTableView();
		});
	}

	private void initSideRadioButtons() {
		lightRadioButton.setUserData(Side.LIGHT);
		lightRadioButton.setText(Side.LIGHT.toString());
		darkRadioButton.setUserData(Side.DARK);
		darkRadioButton.setText(Side.DARK.toString());
	}

	private void initPowerSlider() {
		minPowerLabel.setText(Jedi.MIN_POWER + "");
		currentPowerLabel.setText((int) powerSlider.getValue() + "");
		powerSlider.setMin(Jedi.MIN_POWER);
		powerSlider.setMax(Jedi.MAX_POWER);
		powerSlider.valueProperty()
				.addListener((observable, oldValue, newValue) -> currentPowerLabel.setText(newValue.intValue() + ""));
	}

	private void initJedisTable() {
		addColumn(jedisTableView, "Id", "id");
		addColumn(jedisTableView, "Nazwa", "name");
		addColumn(jedisTableView, "Kolor Miecza", "lightsaberColor");
		addColumn(jedisTableView, "Moc", "power");
		addColumn(jedisTableView, "Strona Mocy", "side");
		addColumn(jedisTableView, "Zakon", "order");
		loadJedisTableView();
	}

	private void loadJedisTableView() {
		jedisTableView.getItems().clear();
		for (Jedi jedi : Jedi.getJedisList())
			jedisTableView.getItems().add(jedi);
	}

	private <T> void addColumn(TableView<T> tablewView, String name, String pointer) {
		TableColumn<T, String> column = new TableColumn<T, String>(name);
		column.setCellValueFactory(new PropertyValueFactory<T, String>(pointer));
		tablewView.getColumns().add(column);
	}

	private void load() {
		try {
			Jedi.loadJedisFromDatabase();
		} catch (SQLException e) {
			openAlertDialog(e.getMessage());
		} catch (NameException | ValueException e) {
			e.printStackTrace();
		}
	}

}
