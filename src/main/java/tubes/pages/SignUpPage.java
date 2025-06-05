package tubes.pages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import tubes.launch.mainApp;

public class SignUpPage extends StackPane {

    public SignUpPage (mainApp app) {

        // Background
        String backgroundString = getClass().getResource("/Background.png").toString();
        ImageView backgroundImage = new ImageView(new Image(backgroundString));

        // Left
        String elemenString = getClass().getResource("/noteVector.png").toString();
        ImageView elemenImage = new ImageView(new Image(elemenString));
        elemenImage.setFitWidth(400);
        elemenImage.setFitHeight(400);

        Label welcomeLbl = new Label("WELCOME !");
        welcomeLbl.setFont(Font.font("Franklin Gothic Heavy", FontWeight.BOLD, FontPosture.ITALIC, 90));
        welcomeLbl.setTextFill(Color.WHITE);

        Region jarak1 = new Region();
        jarak1.setMinHeight(70);

        Region jarak2 = new Region();
        jarak2.setMinHeight(60);

        VBox VLeftLayout = new VBox();
        VLeftLayout.getChildren().addAll(welcomeLbl, jarak1, elemenImage, jarak2);
        VLeftLayout.setAlignment(Pos.CENTER);

        // Right
        Label judul = new Label("SIGN UP");
        judul.setFont(Font.font("Candara Light", 40));
        judul.setTextFill(Color.WHITE);

        Label unameLbl = new Label("Username:");
        unameLbl.setFont(Font.font("Candara Light", 20));
        unameLbl.setTextFill(Color.WHITE);

        Label pwLbl = new Label("Password:");
        pwLbl.setFont(Font.font("Candara Light", 20));
        pwLbl.setTextFill(Color.WHITE);

        Label emailLbl = new Label("e-Mail:");
        emailLbl.setFont(Font.font("Candara Light", 20));
        emailLbl.setTextFill(Color.WHITE);

        TextField unameField = new TextField();
        unameField.setPrefSize(500, 40);
        unameField.setStyle(
                "-fx-background-color: rgb(0, 6, 18, 0.35);" +
                        "-fx-background-opacity: 0.35;" +
                        "-fx-border-color: transparent;" +
                        "-fx-border-radius: 5;" +
                        "-fx-text-fill: rgb(193, 214, 200, 1);" +
                        "-fx-font-size: 15px"
        );

        TextField emailField = new TextField();
        emailField.setPrefSize(500, 40);
        emailField.setStyle(
                "-fx-background-color: rgb(0, 6, 18, 0.35);" +
                        "-fx-background-opacity: 0.35;" +
                        "-fx-border-color: transparent;" +
                        "-fx-border-radius: 5;" +
                        "-fx-text-fill: rgb(193, 214, 200, 1);" +
                        "-fx-font-size: 15px"
        );

        TextField pwField = new TextField();
        pwField.setPrefSize(500, 40);
        pwField.setStyle(
                "-fx-background-color: rgb(0, 6, 18, 0.35);" +
                        "-fx-background-opacity: 0.35;" +
                        "-fx-border-color: transparent;" +
                        "-fx-border-radius: 5;" +
                        "-fx-text-fill: rgb(193, 214, 200, 1);" +
                        "-fx-font-size: 15px"
        );

        Button signUpBtn = new Button("SIGN UP");
        signUpBtn.setStyle(
                "-fx-background-color: #C1D6C8;" +
                "-fx-text-fill: #002A22;" +
                "-fx-font-size: 20px;" +
                "-fx-background-radius: 50;" +
                "-fx-cursor: hand;"
        );
        signUpBtn.setPrefSize(200, 40);

        Button logInBtn = new Button("LOG IN");
        logInBtn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-border-color: transparent;" +
                "-fx-text-fill: #1E90FF;" +
                "-fx-font-size: 15px;" +
                "-fx-underline: true;" +
                "-fx-cursor: hand;"
        );

        VBox unameLayout = new VBox(5);
        unameLayout.getChildren().addAll(unameLbl, unameField);
        unameLayout.setAlignment(Pos.CENTER_LEFT);

        VBox emailLayout = new VBox(5);
        emailLayout.getChildren().addAll(emailLbl, emailField);
        emailLayout.setAlignment(Pos.CENTER_LEFT);

        VBox pwLayout = new VBox(5);
        pwLayout.getChildren().addAll(pwLbl, pwField);
        pwLayout.setAlignment(Pos.CENTER_LEFT);

        VBox VFieldLayout = new VBox(30);
        VFieldLayout.getChildren().addAll(
                unameLayout,
                emailLayout,
                pwLayout
        );
        VFieldLayout.setAlignment(Pos.CENTER);

        Region jarak3 = new Region();
        jarak3.setPrefHeight(30);

        Region jarak4 = new Region();
        jarak4.setPrefHeight(50);

        Region jarak5 = new Region();
        jarak5.setPrefHeight(70);

        VBox VRightLayout = new VBox();
        VRightLayout.setPadding(new Insets(30));
        VRightLayout.getChildren().addAll(judul, jarak3, VFieldLayout, jarak4, signUpBtn, jarak5, logInBtn);
        VRightLayout.setAlignment(Pos.CENTER);

        Rectangle persegi = new Rectangle(600, 600);
        persegi.setStyle(
                "-fx-fill: #FFFFFF;" +
                        "-fx-opacity: 0.35;" +
                        "-fx-arc-Width: 100;" +
                        "-fx-arc-Height: 100;"
        );

        StackPane stackRightLayout = new StackPane(persegi, VRightLayout);

//      Main Layout
        HBox mainHBox = new HBox(150);
        mainHBox.getChildren().addAll(VLeftLayout, stackRightLayout);
        mainHBox.setAlignment(Pos.CENTER);

        // Background + Layout
        this.getChildren().addAll(backgroundImage, mainHBox);

        // ADJUST BACKGROUND
        backgroundImage.fitWidthProperty().bind(this.widthProperty());
        backgroundImage.fitHeightProperty().bind(this.heightProperty());

        logInBtn.setOnAction(e -> {
            app.switchSceneLogInPage();
        });

        signUpBtn.setOnAction(e -> {
            app.switchSceneSchedulePage();
        });

    }

}