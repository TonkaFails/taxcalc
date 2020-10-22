package basic;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;


//TODO make it possible to save current TableView content
//TODO think of other economy programs to realise and make a launcher
//TODO make program an exe



/**
 * TaxCalc
 * @author Tonka
 * @version 1.3
 */
public class TaxCalc extends Application {

    protected Scene scene;
    protected static Stage stage;
    private TextField bruttoField, einkaufsField, nameField, sheetnameField;
    private Button calculateButton, exitButton, saveButton, deleteButton, exportButton;
    private GridPane centerLayout;
    private Text taxText, nettoText, mageText;
    private Label bruttoLabel, nettoLabel, mageLabel, einkaufsLabel, taxLabel, nameLabel, taxRateLabel;
    private double brutto, purchasingPrice, tax, mage, netto;
    private final double TAX19 = 19;
    private final double TAX0 = 0;
    private final double TAX11 = 11;
    private final double TAX16 = 16;
    protected boolean threadShouldRun = true;
    CheckMenuItem englishLanguage, germanLanguage;


    TableView<Calculation> calculationTable;

    @Override
    public void start(Stage stage) throws Exception {

        this.stage = stage;
        stage.setTitle("Tax Calc Beta");

        // before anything else a Thread to display current system time
        // this thread is not essential for the program to run
        Label systemTimeLabel = new Label();
        Thread timerThread = new Thread(() -> {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            for(;threadShouldRun;) {
                try {
                    Thread.sleep(1000); //1 second
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final String time = simpleDateFormat.format(new Date());
                Platform.runLater(() -> {
                    systemTimeLabel.setText(time);
                });

            }
        });
        timerThread.start();
        // end of threadpiece

        //Label
        bruttoLabel = new Label("Brutto: ");
        GridPane.setConstraints(bruttoLabel, 0, 0);
        nettoLabel = new Label("Netto: ");
        GridPane.setConstraints(nettoLabel, 0, 2);
        mageLabel = new Label("Mage: ");
        GridPane.setConstraints(mageLabel, 4, 2);
        einkaufsLabel = new Label("Einkaufspreis: ");
        GridPane.setConstraints(einkaufsLabel, 2, 0);
        taxLabel = new Label("Steuer: ");
        GridPane.setConstraints(taxLabel, 2, 2);
        nameLabel = new Label("Name: ");
        GridPane.setConstraints(nameLabel, 2, 4);
        taxRateLabel = new Label("Steuersatz: ");
        GridPane.setConstraints(taxRateLabel, 5, 0);

        //Text
        taxText = new Text();
        GridPane.setConstraints(taxText, 3, 2);
        nettoText = new Text();
        GridPane.setConstraints(nettoText, 1, 2);
        mageText = new Text();
        GridPane.setConstraints(mageText, 5, 2);

        //Fields
        bruttoField = new TextField();
        bruttoField.setPromptText("Brutto");
        GridPane.setConstraints(bruttoField, 1, 0);

        einkaufsField = new TextField();
        einkaufsField.setPromptText("Einkauf");
        GridPane.setConstraints(einkaufsField, 3, 0);

        nameField = new TextField();
        nameField.setPromptText("Name");
        GridPane.setConstraints(nameField, 3, 4);

        sheetnameField = new TextField();
        sheetnameField.setPromptText("Tabellenname");

        // ChoiceBox
        ChoiceBox<Double> taxSelection = new ChoiceBox<Double>();
        taxSelection.getItems().addAll(TAX0, TAX11, TAX16, TAX19);
        taxSelection.setValue(TAX19);
        GridPane.setConstraints(taxSelection, 6, 0);


        // buttons
        calculateButton = new Button("Berechnen");
        GridPane.setConstraints(calculateButton, 4, 0);
        calculateButton.setOnAction(e -> calculatePrice(bruttoField.getText(), einkaufsField.getText(), taxSelection));


        saveButton = new Button("Speichern(s)");
        GridPane.setConstraints(saveButton, 4, 4);
        saveButton.setOnAction(e -> saveButtonFunction());

        deleteButton = new Button("Löschen(d)");
        deleteButton.setOnAction(e -> deleteButtonFunction());
        deleteButton.setAlignment(Pos.BOTTOM_RIGHT);

        exitButton = new Button("Beenden");
        GridPane.setConstraints(exitButton, 5, 4);
        exitButton.setOnAction(e -> {
            choiceAlert(stage, "Programm speichert nicht", null, "alle Daten gehen verloren");
        });

        exportButton = new Button("Export");
        exportButton.setOnAction(e -> {
            if(sheetnameField.getText().isEmpty()){
                if (germanLanguage.isSelected()) {
                    alertHint(stage, "Name fehlt", "Bitte Tabelle benennen");
                } else {
                    alertHint(stage, "Name missing", "Please name the sheet");
                }
            }else {

                try {
                    exportButtonFunction(sheetnameField.getText());
                } catch (IOException ioException) {

                    if (germanLanguage.isSelected()) {
                        alertHint(stage, "Exporterror", "Fehler beim exportieren");
                    } else {
                        alertHint(stage, "Exporterror", "Something went wrong while exporting");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println(ex.getMessage());
                }
            }
        });


        // Top Menubar
        HBox topMenu = new HBox();
        Menu startMenu = new Menu("Start");
        Menu viewMenu = new Menu("Fenster");
        Menu languageMenu = new Menu("Sprache");
        MenuBar topBar = new MenuBar();
        // start menu items
        MenuItem keymapItem = new MenuItem("Keymap(F1)");
        MenuItem creditItem = new MenuItem("Credits");
        MenuItem closeItem = new MenuItem("Beenden");
        // size menu items
        MenuItem eightSize = new MenuItem("800x500");
        MenuItem tenSize = new MenuItem("1000x600");
        MenuItem sixSize = new MenuItem("600x300");
        MenuItem defaultSize = new MenuItem("730x650(default)");
        CheckMenuItem fullScreenItem = new CheckMenuItem("Vollbild");
        // language menu items
        englishLanguage = new CheckMenuItem("english");
        germanLanguage = new CheckMenuItem("german(default)");

        startMenu.getItems().addAll(keymapItem, creditItem, closeItem);
        viewMenu.getItems().addAll(sixSize, eightSize, tenSize, defaultSize, fullScreenItem);
        languageMenu.getItems().addAll(germanLanguage, englishLanguage);

        topBar.getMenus().addAll(startMenu, viewMenu, languageMenu);

        topMenu.getChildren().addAll(topBar);

        //StartMenuItems functions
        keymapItem.setOnAction(e -> Keymap.display(stage));
        creditItem.setOnAction(e -> Credits.display(stage));
        closeItem.setOnAction(e -> choiceAlert(stage, "Programm speichert nicht", null, "alle Daten gehen verloren"));

        //TableView
        //column
        TableColumn<Calculation, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setMinWidth(100);


        TableColumn<Calculation, Double> bruttoColumn = new TableColumn<>("Brutto");
        bruttoColumn.setCellValueFactory(new PropertyValueFactory<>("brutto"));
        bruttoColumn.setMinWidth(100);


        TableColumn<Calculation, Double> einkaufColumn = new TableColumn<>("Einkauf");
        einkaufColumn.setCellValueFactory(new PropertyValueFactory<>("einkauf"));
        einkaufColumn.setMinWidth(100);

        TableColumn<Calculation, Double> nettoColumn = new TableColumn<>("Netto");
        nettoColumn.setCellValueFactory(new PropertyValueFactory<>("netto"));
        nettoColumn.setMinWidth(100);

        TableColumn<Calculation, Double> taxColumn = new TableColumn<>("Steuer");
        taxColumn.setCellValueFactory(new PropertyValueFactory<>("tax"));
        taxColumn.setMinWidth(100);

        TableColumn<Calculation, Double> mageColumn = new TableColumn<>("Mage");
        mageColumn.setCellValueFactory(new PropertyValueFactory<>("mage"));
        mageColumn.setMinWidth(100);

        calculationTable = new TableView<>();
        calculationTable.setItems(getCalculationList());
        calculationTable.getColumns().addAll(nameColumn, bruttoColumn, einkaufColumn, nettoColumn, taxColumn, mageColumn);
        calculationTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);



        // windows size action
        eightSize.setOnAction(e -> {
            if (!fullScreenItem.isSelected()) {
                stage.setHeight(500);
                stage.setWidth(800);
            }
            if (fullScreenItem.isSelected()) {
                alertHint(stage, "fullscreen is on", "disable fullscreen first");
            }
        });
        tenSize.setOnAction(e -> {
            if (!fullScreenItem.isSelected()) {
                stage.setHeight(600);
                stage.setWidth(1000);
            }
            if (fullScreenItem.isSelected()) {
                alertHint(stage, "fullscreen is on", "disable fullscreen first");
            }
        });
        sixSize.setOnAction(e -> {
            if (!fullScreenItem.isSelected()) {
                stage.setHeight(300);
                stage.setWidth(600);
            }
            if (fullScreenItem.isSelected()) {
                alertHint(stage, "fullscreen is on", "disable fullscreen first");
            }
        });
        defaultSize.setOnAction(e ->{
            if(!fullScreenItem.isSelected()){
                stage.setHeight(650);
                stage.setWidth(730);
            }
            if(fullScreenItem.isSelected()){
                alertHint(stage, "fullscreen is on", "disable fullscreen first");
            }
        });

        fullScreenItem.setOnAction(e -> {
            if (fullScreenItem.isSelected()) stage.setFullScreen(true);
            if (!fullScreenItem.isSelected()) stage.setFullScreen(false);
        });
        // language action
        englishLanguage.setOnAction(e -> {
            if (englishLanguage.isSelected()) {
                germanLanguage.setSelected(false);
                bruttoField.setPromptText("gross");
                einkaufsField.setPromptText("purchasing");
                bruttoLabel.setText("gross: ");
                nettoLabel.setText("net: ");
                einkaufsLabel.setText("purchasingprice: ");
                taxLabel.setText("tax: ");
                mageLabel.setText("profit: ");
                calculateButton.setText("calculate");
                fullScreenItem.setText("fullscreen");
                languageMenu.setText("language");
                viewMenu.setText("window");
                nameColumn.setText("name");
                bruttoColumn.setText("gross");
                einkaufColumn.setText("purchasing");
                nettoColumn.setText("net");
                taxColumn.setText("tax");
                mageColumn.setText("profit");
                taxRateLabel.setText("taxrate:");
                deleteButton.setText("delete(d)");
                saveButton.setText("save(s)");
                nameLabel.setText("name:");
                exitButton.setText("exit");
                nameField.setPromptText("name");
                closeItem.setText("exit");
                sheetnameField.setPromptText("sheetname");
            }
            if (!englishLanguage.isSelected()) {
                germanLanguage.setSelected(true);
                germanLanguage.fire();
            }
        });
        germanLanguage.setOnAction(e -> {

            if (germanLanguage.isSelected()) {
                englishLanguage.setSelected(false);
                bruttoField.setPromptText("Brutto");
                einkaufsField.setPromptText("Einkauf");
                bruttoLabel.setText("Brutto: ");
                nettoLabel.setText("Netto: ");
                einkaufsLabel.setText("Einkaufspreis: ");
                taxLabel.setText("Steuer: ");
                mageLabel.setText("Mage: ");
                calculateButton.setText("Berechnen");
                fullScreenItem.setText("Vollbild");
                languageMenu.setText("Sprache");
                viewMenu.setText("Fenster");
                nameColumn.setText("Name");
                bruttoColumn.setText("Brutto");
                einkaufColumn.setText("Einkaufspreis");
                nettoColumn.setText("Netto");
                taxColumn.setText("Steuer");
                mageColumn.setText("Mage");
                taxRateLabel.setText("Steuersatz:");
                deleteButton.setText("Löschen(d)");
                saveButton.setText("Speichern(s)");
                nameLabel.setText("Name:");
                exitButton.setText("Beenden");
                nameField.setPromptText("Name");
                closeItem.setText("Beenden");
                sheetnameField.setPromptText("Tabellenname");
            }
            if (!germanLanguage.isSelected()) {
                alertHint(stage, "Can't deselect default", "choose different language first");
                germanLanguage.setSelected(true);
            }
        });


        //export

        /*Workbook workbook = new HSSFWorkbook();
        Sheet spreadsheet = workbook.createSheet("Export");

        Row row = spreadsheet.createRow(0);

        for(int j = 0; j < calculationTable.getColumns().size(); j++){
            row.createCell(j).setCellValue(calculationTable.getColumns().get(j).getText());
        }
        for(int i = 0; i < calculationTable.getItems().size(); i++){
            row = spreadsheet.createRow(i +1);
            for(int j = 0; j < calculationTable.getColumns().size(); j++){
                if(calculationTable.getColumns().get(j).getCellData(i) != null){
                    row.createCell(j).setCellValue(calculationTable.getColumns().get(j).getCellData(i).toString());
                }else{
                    row.createCell(j).setCellValue("");
                }
            }
        }

        FileOutputStream fileOut = new FileOutputStream("workbook.xls");
        workbook.write(fileOut);
        fileOut.close();

        Platform.exit();*/



        // centerLayout
        centerLayout = new GridPane();
        centerLayout.setAlignment(Pos.TOP_CENTER);
        centerLayout.setVgap(10);
        centerLayout.setHgap(10);
        centerLayout.getChildren().addAll(exitButton, bruttoField, einkaufsField, calculateButton, taxText, nettoText, systemTimeLabel,
                mageText, nettoLabel, bruttoLabel, taxLabel, mageLabel, einkaufsLabel, saveButton, deleteButton, nameLabel, nameField, calculationTable, taxSelection, taxRateLabel);
        centerLayout.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, null, null)));
        centerLayout.setPadding(new Insets(5, 5 , 5, 5));

        // VBox for delete Button and SystemTime
        HBox underTable = new HBox(10);
        underTable.setAlignment(Pos.BASELINE_RIGHT);
        //underTable.setPadding(new Insets(10, 10 ,10, 10));
        underTable.getChildren().addAll(sheetnameField, exportButton, systemTimeLabel, deleteButton);

        //center center layout + scroll Pane
        VBox centerCenterLayout = new VBox(10);
        centerCenterLayout.setPadding(new Insets(10, 10, 10, 10));
        centerCenterLayout.getChildren().addAll(centerLayout, calculationTable, underTable);

        ScrollPane scroll = new ScrollPane();
        scroll.setContent(centerCenterLayout);

        // App layout
        BorderPane layout = new BorderPane();

        layout.setCenter(scroll);
        layout.setTop(topMenu);

        // adding a Application Icon
        stage.getIcons().add(new Image(TaxCalc.class.getResourceAsStream("/FAvicon_rose.png")));

        // setting up a warning before closing the program
        stage.setOnCloseRequest(e ->{
            e.consume();
            if(germanLanguage.isSelected()) {
                choiceAlert(stage, "Programm speichert nicht automatisch", null, "alle ungespeicherten Daten gehen verloren");
            }else{
                choiceAlert(stage, "Programm doensn't save automatically", null, "all unsaved Data gets lost");
            }

        });

        //KeyPressed function
        layout.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if(keyEvent.getCode().equals(KeyCode.ENTER)){
                    calculateButton.fire();
                }
                if(keyEvent.getCode().equals(KeyCode.D)){
                    deleteButton.fire();
                }
                if(keyEvent.getCode().equals(KeyCode.S)){
                    saveButton.fire();
                }
                if(keyEvent.getCode().equals(KeyCode.F1)){
                    keymapItem.fire();
                }
            }
        });

        scene = new Scene(layout);
        defaultSize.fire();
        germanLanguage.setSelected(true);

        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();

    }

    // function that calculates brutto netto
    protected void calculatePrice(String bruttoString, String einkaufspreisString, ChoiceBox<Double> choiceBox) {

        try {
            brutto = Double.parseDouble(bruttoString);
            purchasingPrice = Double.parseDouble(einkaufspreisString);

            netto = brutto / (100 + choiceBox.getValue()) * 100;
            tax = brutto - netto;
            mage = netto - purchasingPrice;

            DecimalFormat dF = new DecimalFormat("#.####");

            taxText.setText(String.valueOf(dF.format(tax)));
            nettoText.setText(String.valueOf(dF.format(netto)));
            mageText.setText(String.valueOf(dF.format(mage)));

        } catch (NumberFormatException nfe) {
            System.out.println("Number formatting went wrong");
        }

    }

    public static void main(String[] args) {
        launch(args);
    }

    static void alertHint(Stage owner, String headerMessage, String contentMessage) {
        Alert alert = new Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText(headerMessage);
        alert.setContentText(contentMessage);

        alert.initOwner(owner);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.showAndWait();
    }

    void choiceAlert(Stage owner, String title, String headerMessage, String contentMessage){

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        if(title != null)alert.setTitle(title);
        if(headerMessage != null)alert.setHeaderText(headerMessage);
        if(headerMessage == null)alert.setHeaderText(null);
        if(contentMessage != null)alert.setContentText(contentMessage);
        if(owner != null)alert.initOwner(owner);

        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == ButtonType.OK){
            threadShouldRun = false;
            owner.close();
            alert.close();
        }else{
            alert.close();
        }
    }

    public ObservableList<Calculation> getCalculationList(){

        ObservableList<Calculation> calculationsList = FXCollections.observableArrayList();


        return  calculationsList;

    }

    public void saveButtonFunction(){

        Calculation calculation = new Calculation();

        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator('.');

        DecimalFormat dF = new DecimalFormat("#.####", symbols);

        calculation.setName(nameField.getText());
        calculation.setBrutto(Double.parseDouble(bruttoField.getText()));
        calculation.setEinkauf(Double.parseDouble(einkaufsField.getText()));
        calculation.setNetto(Double.parseDouble(dF.format(netto)));
        calculation.setTax(Double.parseDouble(dF.format(tax)));
        calculation.setMage(Double.parseDouble(dF.format(mage)));

        calculationTable.getItems().add(calculation);

    }

    public void deleteButtonFunction(){

        ObservableList<Calculation> calculationSelected, allCalculations;

        allCalculations = calculationTable.getItems();
        calculationSelected = calculationTable.getSelectionModel().getSelectedItems();

        calculationSelected.forEach(allCalculations::remove);

    }

    public void exportButtonFunction(String sheetname) throws IOException {

        Workbook workbook = new HSSFWorkbook();
        Sheet spreadsheet = workbook.createSheet("Export");

        Row row = spreadsheet.createRow(0);

        for(int j = 0; j < calculationTable.getColumns().size(); j++){
            row.createCell(j).setCellValue(calculationTable.getColumns().get(j).getText());
        }
        for(int i = 0; i < calculationTable.getItems().size(); i++){
            row = spreadsheet.createRow(i +1);
            for(int j = 0; j < calculationTable.getColumns().size(); j++){
                if(calculationTable.getColumns().get(j).getCellData(i) != null){
                    row.createCell(j).setCellValue(calculationTable.getColumns().get(j).getCellData(i).toString());
                }else{
                    row.createCell(j).setCellValue("");
                }
            }
        }

        FileOutputStream fileOut = new FileOutputStream(sheetname + ".xls");
        workbook.write(fileOut);
        fileOut.close();

    }

}


class Credits{

    static void display(Stage owner) {
        Stage creditStage = new Stage();
        creditStage.setTitle("Credits");
        creditStage.initModality(Modality.NONE);

        Text creditText = new Text("by Hofmeier Anton \r\n info@gopnikshop.de \r\n Hofmeier Anton & Tchutchikiv Rustam GbR");
        creditText.setTextAlignment(TextAlignment.CENTER);

        ImageView creditView = new ImageView("res/creditLogo.jpg");
        creditView.setFitHeight(150);
        creditView.setFitWidth(300);

        Button backButton = new Button("back");
        backButton.setOnAction(e -> creditStage.close());

        VBox creditLayout = new VBox(10);

        creditLayout.getChildren().addAll(creditView, creditText, backButton);

        creditLayout.setPadding(new Insets(10, 10, 10, 10));
        creditLayout.setAlignment(Pos.CENTER);

        Scene creditScene = new Scene(creditLayout, 300, 300);

        creditStage.getIcons().add(new Image(TaxCalc.class.getResourceAsStream("/icon.png" )));

        creditStage.setScene(creditScene);
        creditStage.initOwner(owner);

        creditStage.showAndWait();
    }

}
class Keymap{

    static void display(Stage owner) {
        Stage keymapStage = new Stage();
        keymapStage.setTitle("Keymap");
        keymapStage.initModality(Modality.NONE);

        Text enterText = new Text("calculate / berechnen");
        GridPane.setConstraints(enterText, 1, 0);

        ImageView enterImage = new ImageView("res/enterkey.png");
        enterImage.setFitHeight(50);
        enterImage.setFitWidth(50);
        GridPane.setConstraints(enterImage, 0, 0);

        Text dText = new Text("delete / löschen");
        GridPane.setConstraints(dText, 1, 1);

        ImageView dImage = new ImageView("res/letter_d.png");
        dImage.setFitWidth(50);
        dImage.setFitHeight(50);
        GridPane.setConstraints(dImage, 0, 1);

        Text sText = new Text("save / speichern");
        GridPane.setConstraints(sText, 1, 2);

        ImageView sImage = new ImageView("res/letter_s.png");
        sImage.setFitWidth(50);
        sImage.setFitHeight(50);
        GridPane.setConstraints(sImage, 0, 2);

        Text f1Text = new Text("Keymap");
        GridPane.setConstraints(f1Text, 1, 3);

        ImageView f1Image = new ImageView("res/f1_key.png");
        f1Image.setFitWidth(50);
        f1Image.setFitHeight(50);
        GridPane.setConstraints(f1Image, 0, 3);

        Button backButton = new Button("back");
        backButton.setOnAction(e -> keymapStage.close());
        AnchorPane.setBottomAnchor(backButton, 5.0);
        AnchorPane.setRightAnchor(backButton,  5.0);

        AnchorPane bottomLayout = new AnchorPane();
        bottomLayout.getChildren().addAll(backButton);

        GridPane centerLayout = new GridPane();
        centerLayout.setPadding(new Insets(5, 5 ,5, 5));
        centerLayout.setHgap(5.0);
        centerLayout.setVgap(5.0);
        centerLayout.getChildren().addAll(enterImage, enterText, dImage, dText, sImage, sText, f1Image, f1Text);

        BorderPane mainLayout = new BorderPane();

        mainLayout.setBottom(bottomLayout);
        mainLayout.setCenter(centerLayout);

        Scene keymapScene = new Scene(mainLayout, 300, 300);

        keymapStage.getIcons().add(new Image(TaxCalc.class.getResourceAsStream("/icon.png" )));

        keymapStage.setScene(keymapScene);
        keymapStage.initOwner(owner);

        keymapStage.showAndWait();
    }

}

