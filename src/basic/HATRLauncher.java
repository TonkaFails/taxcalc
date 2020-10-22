package basic;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class HATRLauncher extends Application {

    TaxCalc taxCalc = new TaxCalc();
    Scene scene;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        //header pic
        ImageView launcherView = new ImageView("res/HATRLauncher.png");
        launcherView.setFitHeight(250);
        launcherView.setFitWidth(500);


        //Buttons
        Button startTaxCalcButton = new Button("Start TaxCalc");
        GridPane.setConstraints(startTaxCalcButton, 0, 0);
        startTaxCalcButton.setAlignment(Pos.CENTER);
        startTaxCalcButton.setOnAction(e -> {
            try {
                Stage newStage = new Stage();
                taxCalc.start(newStage);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
        Button openGSButton = new Button("open TsarAntonov.com");
        GridPane.setConstraints(openGSButton, 0, 1);
        openGSButton.setStyle("-fx-border-color: #ff0000; -fx-border-width: 5px;");
        Hyperlink gsDeLink = new Hyperlink();
        gsDeLink.setText("https://www.tsarantonov.com");
        gsDeLink.setOnAction(e -> getHostServices().showDocument(gsDeLink.getText()));
        openGSButton.setOnAction(e -> gsDeLink.fire());

        Button impressumButton = new Button("Impressum");
        GridPane.setConstraints(impressumButton, 1, 1);
        impressumButton.setStyle("-fx-border-color: #ff0000; -fx-border-width: 5px;");
        Hyperlink impressumLink = new Hyperlink();
        impressumLink.setText("https://www.tsarantonov.com/policies/legal-notice");
        impressumLink.setOnAction(e -> getHostServices().showDocument(impressumLink.getText()));
        impressumButton.setOnAction(e -> impressumLink.fire());

        Button contactButton = new Button("Kontakt");
        GridPane.setConstraints(contactButton, 2, 1);
        contactButton.setStyle("-fx-border-color: #ff0000; -fx-border-width: 5px;");
        Hyperlink contactLink = new Hyperlink();
        contactLink.setText("https://tsarantonov.com/pages/kontakt");
        contactLink.setOnAction(e -> getHostServices().showDocument(contactLink.getText()));
        contactButton.setOnAction(e -> contactLink.fire());

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> stage.close());

        //Text
        Text independentRun = new Text("Programs run independently");
        independentRun.setFill(Color.RED);

        //AnchorPane bottomLayout

        AnchorPane bottomLayout = new AnchorPane();
        bottomLayout.getChildren().addAll(independentRun, exitButton);
        AnchorPane.setBottomAnchor(independentRun, 5.0);
        AnchorPane.setLeftAnchor(independentRun, 5.0);

        AnchorPane.setBottomAnchor(exitButton, 5.0);
        AnchorPane.setRightAnchor(exitButton, 5.0);


        //GridPane buttonLayout

        GridPane buttonCenterLayout = new GridPane();
        buttonCenterLayout.setAlignment(Pos.TOP_CENTER);
        buttonCenterLayout.setVgap(10);
        buttonCenterLayout.setHgap(10);
        buttonCenterLayout.getChildren().addAll(startTaxCalcButton, openGSButton, impressumButton, contactButton);

        VBox centerLayout = new VBox(10);
        centerLayout.getChildren().addAll(launcherView, buttonCenterLayout);
        centerLayout.setPadding(new Insets(10, 10 ,10, 10));
        centerLayout.setAlignment(Pos.TOP_CENTER);

        //BorderPane mainLayout

        BorderPane mainLayout = new BorderPane();

        mainLayout.setCenter(centerLayout);
        mainLayout.setBottom(bottomLayout);


            //width parameters
            buttonCenterLayout.setPrefWidth(200);
            startTaxCalcButton.autosize();


        startTaxCalcButton.setOnKeyPressed(event -> {
                    if (event.getCode().equals(KeyCode.ENTER)) {
                        startTaxCalcButton.fire();
                    }
                }
        );

        stage.getIcons().add(new Image(HATRLauncher.class.getResourceAsStream("/icon.png" )));

        scene = new Scene(mainLayout, 700, 500, true, SceneAntialiasing.BALANCED);


        stage.setOpacity(0.9);


        stage.setScene(scene);

        stage.show();

    }


}
