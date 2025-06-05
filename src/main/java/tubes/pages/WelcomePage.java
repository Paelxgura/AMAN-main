package tubes.pages;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Region;

import tubes.launch.mainApp;

public class WelcomePage extends StackPane {

    public WelcomePage(mainApp app) {

        // Inisiasi Background
        String backgroundString = getClass().getResource("/Background.png").toString();
        ImageView backgroundImage = new ImageView(new Image(backgroundString));

        // Inisiasi Label Text
        Label amanLbl = new Label ("AMAN");
        amanLbl.setFont(Font.font("Eras Bold ITC", 90));
        amanLbl.setTextFill(Color.WHITE);
        amanLbl.setAlignment(Pos.TOP_CENTER);

        Label sloganLbl = new Label("Solusi untuk mengatur waktumu");
        sloganLbl.setFont(Font.font("Candara Light", 23));
        sloganLbl.setTextFill(Color.WHITE);
        sloganLbl.setAlignment(Pos.TOP_CENTER);

        Label selamatLbl = new Label("SELAMAT");
        selamatLbl.setFont(Font.font("Franklin Gothic Heavy", FontWeight.BOLD, FontPosture.ITALIC, 130));
        selamatLbl.setTextFill(Color.WHITE);

        Label datangLbl = new Label("DATANG");
        datangLbl.setFont(Font.font("Franklin Gothic Heavy", FontWeight.BOLD,  FontPosture.ITALIC, 150));
        datangLbl.setTextFill(Color.WHITE);

        // Inisiasi Button
        Button logInBtn = new Button("LOG IN");
        logInBtn.setStyle(
                "-fx-background-color: #C1D6C8;" +
                "-fx-text-fill: #002A22;" +
                "-fx-font-size: 20px;" +
                "-fx-background-radius: 50;" +
                "-fx-cursor: hand;"
        );
        logInBtn.setMinWidth(200);
        logInBtn.setMinHeight(40);

        Button signUpBtn = new Button("SIGN UP");
        signUpBtn.setStyle(
                "-fx-background-color: #C1D6C8;" +
                "-fx-text-fill: #002A22;" +
                "-fx-font-size: 20px;" +
                "-fx-background-radius: 50;" +
                "-fx-cursor: hand;"
        );
        signUpBtn.setMinWidth(200);
        signUpBtn.setMinHeight(40);

        // Layout
        VBox VTopLayout = new VBox();
        VTopLayout.getChildren().addAll(amanLbl, sloganLbl, logInBtn);
        VTopLayout.setAlignment(Pos.CENTER);

        VBox VMiddleLayout = new VBox();
        VMiddleLayout.getChildren().addAll(selamatLbl, datangLbl);
        VMiddleLayout.setAlignment(Pos.CENTER);

        HBox HButtonLayout = new HBox(22);
        HButtonLayout.getChildren().addAll(logInBtn, signUpBtn);
        HButtonLayout.setAlignment(Pos.CENTER);

        // Region
        Region jarak1 = new Region();
        jarak1.setMinHeight(80);

        Region jarak2 = new Region();
        jarak2.setMinHeight(60);

        Region jarak3 = new Region();
        jarak3.setMinHeight(80);

        // Layout Utama
        VBox VMainLayout = new VBox();
        VMainLayout.getChildren().addAll(VTopLayout, jarak1, VMiddleLayout, jarak2, HButtonLayout, jarak3);
        VMainLayout.setAlignment(Pos.CENTER);

        // Background + Layout
        this.getChildren().addAll(backgroundImage, VMainLayout);

        // ADJUST BACKGROUND
        backgroundImage.fitWidthProperty().bind(this.widthProperty());
        backgroundImage.fitHeightProperty().bind(this.heightProperty());

        logInBtn.setOnAction(e -> {
            app.switchSceneLogInPage();
        });

        signUpBtn.setOnAction(e -> {
            app.switchSceneSignUpPage();
        });

    }

}