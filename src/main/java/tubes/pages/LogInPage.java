package tubes.pages;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;

import tubes.launch.mainApp;

public class LogInPage extends StackPane {

    private TextField unameField;
    private TextField emailField;
    private PasswordField pwField;

    public LogInPage (mainApp app) {

        String backgroundString = getClass().getResource("/Background.png").toString();
        ImageView backgroundImage = new ImageView(new Image(backgroundString));

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

        Label judul = new Label("LOG IN");
        judul.setFont(Font.font("Candara Light", 40));
        judul.setTextFill(Color.WHITE);

        Label unameLbl = new Label("Username or e-Mail:");
        unameLbl.setFont(Font.font("Candara Light", 20));
        unameLbl.setTextFill(Color.WHITE);

        Label pwLbl = new Label("Password:");
        pwLbl.setFont(Font.font("Candara Light", 20));
        pwLbl.setTextFill(Color.WHITE);

        unameField = new TextField();
        unameField.setPromptText("Enter your username or email");
        unameField.setPrefSize(500, 40);
        unameField.setStyle(
                "-fx-background-color: rgb(0, 6, 18, 0.35);" +
                        "-fx-background-opacity: 0.35;" +
                        "-fx-border-color: transparent;" +
                        "-fx-border-radius: 5;" +
                        "-fx-text-fill: rgb(193, 214, 200, 1);" +
                        "-fx-font-size: 15px"
        );

        pwField = new PasswordField();
        pwField.setPromptText("Enter your password");
        pwField.setPrefSize(500, 40);
        pwField.setStyle(
                "-fx-background-color: rgb(0, 6, 18, 0.35);" +
                        "-fx-background-opacity: 0.35;" +
                        "-fx-border-color: transparent;" +
                        "-fx-border-radius: 5;" +
                        "-fx-text-fill: rgb(193, 214, 200, 1);" +
                        "-fx-font-size: 15px"
        );

        Button logInBtn = new Button("LOG IN");
        logInBtn.setStyle(
                "-fx-background-color: #C1D6C8;" +
                "-fx-text-fill: #002A22;" +
                "-fx-font-size: 20px;" +
                "-fx-background-radius: 50;" +
                "-fx-cursor: hand;"
        );
        logInBtn.setPrefSize(200,40);

        Button signUpBtn = new Button("SIGN UP");
        signUpBtn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-border-color: transparent;" +
                "-fx-text-fill: #1E90FF;" +
                "-fx-font-size: 15px;" +
                "-fx-underline: true;" +
                "-fx-cursor: hand;"
        );

        VBox unameLayout = new VBox(5);
        unameLayout.getChildren().addAll(unameLbl, unameField);
        unameLayout.setAlignment(Pos.BOTTOM_LEFT);

        VBox pwLayout = new VBox(5);
        pwLayout.getChildren().addAll(pwLbl, pwField);
        pwLayout.setAlignment(Pos.BOTTOM_LEFT);

        VBox VFieldLayout = new VBox(30);
        VFieldLayout.getChildren().addAll(
                unameLayout,
                pwLayout
        );
        VFieldLayout.setAlignment(Pos.CENTER);

        Region jarak3 = new Region();
        jarak3.setMinHeight(30);

        Region jarak4 = new Region();
        jarak4.setMinHeight(50);

        Region jarak5 = new Region();
        jarak5.setMinHeight(70);

        VBox VRightLayout = new VBox();
        VRightLayout.setPadding(new Insets(30));
        VRightLayout.getChildren().addAll(judul, jarak3, VFieldLayout, jarak4, logInBtn, jarak5, signUpBtn);
        VRightLayout.setAlignment(Pos.CENTER);

        Rectangle persegi = new Rectangle(600, 600);
        persegi.setStyle(
                "-fx-fill: #FFFFFF;" +
                        "-fx-opacity: 0.35;" +
                        "-fx-radius: 25;" +
                        "-fx-arc-Width: 100;" +
                        "-fx-arc-Height: 100;"
        );

        StackPane stackRightLayout = new StackPane(persegi, VRightLayout);

        HBox mainHBox = new HBox(150);
        mainHBox.getChildren().addAll(VLeftLayout, stackRightLayout);
        mainHBox.setAlignment(Pos.CENTER);

        this.getChildren().addAll(backgroundImage, mainHBox);

        backgroundImage.fitWidthProperty().bind(this.widthProperty());
        backgroundImage.fitHeightProperty().bind(this.heightProperty());

        signUpBtn.setOnAction(e -> {
            app.switchSceneSignUpPage();
        });

        logInBtn.setOnAction(e -> {
            String usernameOrEmailInput = unameField.getText();
            String passwordInput = pwField.getText();

            if (usernameOrEmailInput.isEmpty() || passwordInput.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Input Kosong", "Username/Email dan Password tidak boleh kosong.");
                return;
            }

            boolean loginSukses = app.getPengelolaTugas().masukSistem(usernameOrEmailInput, passwordInput);

            if (loginSukses) {
                app.switchSceneSchedulePage();
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Gagal", "Username/Email atau Password salah. Silakan coba lagi.");
                pwField.clear();
            }
        });
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}