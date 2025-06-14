package tubes.pages;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import tubes.backend.PengelolaTugas;
import tubes.backend.Tugas;
import tubes.launch.mainApp;

public class SchedulePage extends StackPane {

    private mainApp app;
    private VBox daftarAktivitasVBox;
    private ComboBox<String> filterComboBox;
    private ScrollPane scrollPane;

    public SchedulePage(mainApp app) {
        this.app = app;

        String backgroundString = getClass().getResource("/Background.png").toString();
        ImageView backgroundImage = new ImageView(new Image(backgroundString));
        backgroundImage.fitWidthProperty().bind(this.widthProperty());
        backgroundImage.fitHeightProperty().bind(this.heightProperty());

        Label jadwalLbl = new Label("JADWAL");
        jadwalLbl.setFont(Font.font("Franklin Gothic Heavy", FontWeight.BOLD, FontPosture.ITALIC, 70));
        jadwalLbl.setTextFill(Color.WHITE);

        Button keluarBtn = new Button("KELUAR");
        keluarBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: transparent;" +
                        "-fx-text-fill: #FFFFFF;" +
                        "-fx-font-size: 18px;" +
                        "-fx-underline: true;" +
                        "-fx-cursor: hand;"
        );
        keluarBtn.setOnAction(e -> {
            Alert confirmLogout = new Alert(Alert.AlertType.CONFIRMATION);
            confirmLogout.setTitle("Konfirmasi Keluar");
            confirmLogout.setHeaderText("Anda yakin ingin keluar dari akun Anda?");
            confirmLogout.setContentText("Anda akan dikembalikan ke halaman utama.");
            Optional<ButtonType> result = confirmLogout.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (app.getPengelolaTugas() != null) {
                    app.getPengelolaTugas().logout();
                }
                app.switchSceneWelcomePage();
            }
        });

        Label filterLbl = new Label("FILTER:");
        filterLbl.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #FFFFFF;" +
                        "-fx-font-size: 18px;"
        );

        ArrayList<String> filterItems = new ArrayList<>();
        filterItems.add("Semua");
        filterItems.add("AKADEMIK");
        filterItems.add("NON-AKADEMIK");

        filterComboBox = new ComboBox<>();
        filterComboBox.getItems().addAll(filterItems);
        filterComboBox.getSelectionModel().selectFirst();
        filterComboBox.setStyle("-fx-font-size: 14px;");
        filterComboBox.setOnAction(e -> loadDaftarAktivitas());

        HBox filterHBox = new HBox(10, filterLbl, filterComboBox);
        filterHBox.setAlignment(Pos.CENTER_LEFT);

        BorderPane topBarPane = new BorderPane();
        topBarPane.setLeft(filterHBox);
        topBarPane.setCenter(jadwalLbl);
        topBarPane.setRight(keluarBtn);
        topBarPane.setPadding(new Insets(20, 40, 10, 40));
        BorderPane.setAlignment(jadwalLbl, Pos.CENTER);

        daftarAktivitasVBox = new VBox(20);
        daftarAktivitasVBox.setPadding(new Insets(20, 30, 20, 30));
        daftarAktivitasVBox.setAlignment(Pos.TOP_CENTER);

        scrollPane = new ScrollPane(daftarAktivitasVBox);
        scrollPane.setStyle(
                "-fx-background: transparent;" +
                "-fx-background-color: transparent;" +
                "-fx-border-color: transparent;"
        );
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToWidth(true);

        StackPane midSectionStack = new StackPane();
        Region persegiBackground = new Region();
        persegiBackground.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-opacity: 0.30;" +
                "-fx-background-radius: 50 50 0 0;"
        );
        midSectionStack.getChildren().addAll(persegiBackground, scrollPane);

        Button addScheduleBtn = new Button("+");
        addScheduleBtn.setStyle(
                "-fx-background-color: #68AE5A;" +
                "-fx-text-fill: #FFFFFF;" +
                "-fx-font-size: 35px;" +
                "-fx-background-radius: 100px;" +
                "-fx-cursor: hand;" +
                "-fx-font-weight: bold;"
        );
        addScheduleBtn.setPrefSize(70, 70);
        addScheduleBtn.setOnAction(e -> {
            app.switchSceneEditSchedulePage();
        });

        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(topBarPane);
        mainLayout.setCenter(midSectionStack);

        StackPane rootStack = new StackPane();
        rootStack.getChildren().addAll(mainLayout, addScheduleBtn);
        StackPane.setAlignment(addScheduleBtn, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(addScheduleBtn, new Insets(0, 40, 40, 0));

        this.getChildren().addAll(backgroundImage, rootStack);

        loadDaftarAktivitas();
    }

    private void loadDaftarAktivitas() {
        daftarAktivitasVBox.getChildren().clear();

        PengelolaTugas pengelola = app.getPengelolaTugas();
        if (pengelola == null || pengelola.getCurrentUser() == null) {
            Label loginLabel = new Label("Silakan login terlebih dahulu untuk melihat jadwal.");
            loginLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 20));
            loginLabel.setTextFill(Color.WHITE);
            loginLabel.setPadding(new Insets(20));
            daftarAktivitasVBox.getChildren().add(loginLabel);
            return;
        }

        String kategoriFilter = filterComboBox.getValue();
        List<Tugas> daftarTugasUser;

        if (kategoriFilter == null || kategoriFilter.equalsIgnoreCase("Semua") || kategoriFilter.trim().isEmpty()) {
            daftarTugasUser = pengelola.getTugasCurrentUser();
        } else {
            daftarTugasUser = pengelola.getTugasCurrentUserByKategori(kategoriFilter);
        }

        if (daftarTugasUser.isEmpty()) {
            Label kosongLabel = new Label("Belum ada jadwal kegiatan" +
                    (kategoriFilter != null && !kategoriFilter.equalsIgnoreCase("Semua") && !kategoriFilter.trim().isEmpty() ? " untuk kategori '" + kategoriFilter + "'" : "") +
                    ".");
            kosongLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 22));
            kosongLabel.setTextFill(Color.rgb(10, 20, 30, 0.9));
            kosongLabel.setPadding(new Insets(30));
            HBox centerLabelBox = new HBox(kosongLabel);
            centerLabelBox.setAlignment(Pos.CENTER);
            daftarAktivitasVBox.getChildren().add(centerLabelBox);

        } else {
            for (Tugas tugas : daftarTugasUser) {
                activityCard card = new activityCard(app, tugas);
                daftarAktivitasVBox.getChildren().add(card.getView());
            }
        }
        if (scrollPane != null) {
            scrollPane.setVvalue(0.0);
        }
    }
}
