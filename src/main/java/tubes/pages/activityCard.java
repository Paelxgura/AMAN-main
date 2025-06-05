package tubes.pages;

import java.util.Optional;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import tubes.backend.Tugas;
import tubes.launch.mainApp;

public class activityCard {

    private Tugas tugas;
    private mainApp app;

    private Rectangle kotakAktivitas;
    private Label judulAktvLbl;
    private Label waktuAktvLbl;
    private Label lokasiStaticLbl;
    private Label namaLokasiAktvLbl;
    private Label deskripsiStaticLbl;
    private Label isiDeskripsiAktvLbl;
    private Button editBtn;
    private Button hapusBtn;

    private VBox judulWaktuVBox;
    private VBox lokasiVBox;
    private VBox deskripsiVBox;
    private VBox isiAKtivitasVBox;
    private VBox editHapusVBox;
    private BorderPane aktivitasCardLayout;
    private StackPane aktivitasPane;

    public activityCard(mainApp app, Tugas tugas) {
        this.app = app;
        this.tugas = tugas;

        this.kotakAktivitas = new Rectangle(1300, 400);
        this.kotakAktivitas.setStyle(
                "-fx-fill: #000612;" +
                        "-fx-opacity: 0.45;" +
                        "-fx-arc-Width: 100;" +
                        "-fx-arc-Height: 100;");

        this.judulAktvLbl = new Label(this.tugas.getJudul());
        this.judulAktvLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
        this.judulAktvLbl.setTextFill(Color.rgb(193, 214, 200, 1));

        this.waktuAktvLbl = new Label();
        if (this.tugas.getTanggalBatas() != null) {
            this.waktuAktvLbl.setText(this.tugas.getTanggalBatasFormatted());
        } else {
            this.waktuAktvLbl.setText("N/A");
        }
        this.waktuAktvLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
        this.waktuAktvLbl.setTextFill(Color.rgb(116, 0, 0, 1));

        this.judulWaktuVBox = new VBox(5);
        this.judulWaktuVBox.getChildren().addAll(this.judulAktvLbl, this.waktuAktvLbl);

        this.lokasiStaticLbl = new Label("LOKASI");
        this.lokasiStaticLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
        this.lokasiStaticLbl.setTextFill(Color.rgb(193, 214, 200, 1));
        this.lokasiStaticLbl.setUnderline(true);

        this.namaLokasiAktvLbl = new Label(this.tugas.getLokasi() != null && !this.tugas.getLokasi().trim().isEmpty() ? this.tugas.getLokasi() : "-");
        this.namaLokasiAktvLbl.setFont(Font.font("Segoe UI", 30));
        this.namaLokasiAktvLbl.setTextFill(Color.rgb(193, 214, 200, 1));
        this.namaLokasiAktvLbl.setWrapText(true);

        this.lokasiVBox = new VBox(5);
        this.lokasiVBox.getChildren().addAll(this.lokasiStaticLbl, this.namaLokasiAktvLbl);

        boolean hasValidLocation = this.tugas.getLokasi() != null && !this.tugas.getLokasi().trim().isEmpty() && !this.tugas.getLokasi().equalsIgnoreCase("Belum Diisi") && !this.tugas.getLokasi().equals("-");
        this.lokasiVBox.setVisible(hasValidLocation);
        this.lokasiVBox.setManaged(hasValidLocation);

        this.deskripsiStaticLbl = new Label("DESKRIPSI");
        this.deskripsiStaticLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
        this.deskripsiStaticLbl.setTextFill(Color.rgb(193, 214, 200, 1));
        this.deskripsiStaticLbl.setUnderline(true);

        this.isiDeskripsiAktvLbl = new Label(this.tugas.getDeskripsi() != null && !this.tugas.getDeskripsi().trim().isEmpty() ? this.tugas.getDeskripsi() : "-");
        this.isiDeskripsiAktvLbl.setFont(Font.font("Segoe UI", 30));
        this.isiDeskripsiAktvLbl.setTextFill(Color.rgb(193, 214, 200, 1));
        this.isiDeskripsiAktvLbl.setWrapText(true);
        this.isiDeskripsiAktvLbl.setMaxWidth(850);

        this.deskripsiVBox = new VBox(5);
        this.deskripsiVBox.getChildren().addAll(this.deskripsiStaticLbl, this.isiDeskripsiAktvLbl);

        this.isiAKtivitasVBox = new VBox(15);
        this.isiAKtivitasVBox.getChildren().addAll(this.judulWaktuVBox, this.lokasiVBox, this.deskripsiVBox);
        this.isiAKtivitasVBox.setAlignment(Pos.CENTER_LEFT);

        this.editBtn = new Button("EDIT");
        this.editBtn.setPrefSize(250, 60);
        this.editBtn.setStyle(
                "-fx-background-color: #012F10;" +
                        "-fx-text-fill: #C1D6C8;" +
                        "-fx-font-size: 25px;" +
                        "-fx-font-weight: BOLD;" +
                        "-fx-background-radius: 50;" +
                        "-fx-cursor: hand;");
        this.editBtn.setOnAction(e -> {
            this.app.switchSceneEditSchedulePage(this.tugas);
        });

        this.hapusBtn = new Button("HAPUS");
        this.hapusBtn.setPrefSize(250, 60);
        this.hapusBtn.setStyle(
                "-fx-background-color: #012F10;" +
                        "-fx-text-fill: #C1D6C8;" +
                        "-fx-font-size: 25px;" +
                        "-fx-font-weight: BOLD;" +
                        "-fx-background-radius: 50;" +
                        "-fx-cursor: hand;");
        this.hapusBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Konfirmasi Hapus");
            alert.setHeaderText("Anda yakin ingin menghapus tugas: '" + this.tugas.getJudul() + "'?");
            alert.setContentText("Tindakan ini tidak dapat dibatalkan.");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                boolean sukses = this.app.getPengelolaTugas().hapusTugas(this.tugas.getId());
                if (sukses) {
                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "Tugas berhasil dihapus.");
                    this.app.switchSceneSchedulePage();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menghapus tugas.");
                }
            }
        });

        this.editHapusVBox = new VBox(50);
        this.editHapusVBox.getChildren().addAll(this.editBtn, this.hapusBtn);
        this.editHapusVBox.setAlignment(Pos.CENTER);

        this.aktivitasCardLayout = new BorderPane();
        this.aktivitasCardLayout.setLeft(this.isiAKtivitasVBox);
        this.aktivitasCardLayout.setRight(this.editHapusVBox);
        this.aktivitasCardLayout.setPadding(new Insets(60, 80, 60, 80));

        this.aktivitasPane = new StackPane();
        this.aktivitasPane.setPrefSize(1400, 400);
        this.aktivitasPane.setMaxWidth(Double.MAX_VALUE);
        this.aktivitasPane.getChildren().addAll(this.kotakAktivitas, this.aktivitasCardLayout);
    }

    public StackPane getView() {
        return this.aktivitasPane;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}