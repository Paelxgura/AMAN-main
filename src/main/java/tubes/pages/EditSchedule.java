package tubes.pages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import tubes.backend.Tugas;
import tubes.backend.User;
import tubes.launch.mainApp;
import javafx.geometry.HPos;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Optional;

public class EditSchedule extends StackPane {

    private mainApp app;
    private Tugas tugasToEdit;

    private Label topLbl;
    private TextField namaKegiatanField;
    private ComboBox<String> kategoriCB;
    private DatePicker tanggalDP;
    private TextField waktuField;
    private TextField lokasiField;
    private TextField deskripsiField;
    private CheckBox ingatkanSayaCheckBox;
    private ComboBox<Integer> jamSebelumComboBox;

    public EditSchedule(mainApp app, Tugas tugas) {
        this.app = app;
        this.tugasToEdit = tugas;

        String backgroundString = getClass().getResource("/Background.png").toString();
        ImageView backgroundImage = new ImageView(new Image(backgroundString));
        backgroundImage.fitWidthProperty().bind(this.widthProperty());
        backgroundImage.fitHeightProperty().bind(this.heightProperty());

        this.topLbl = new Label();
        this.topLbl.setFont(Font.font("Franklin Gothic Heavy", FontWeight.BOLD, FontPosture.ITALIC, 70));
        this.topLbl.setTextFill(Color.rgb(193, 214, 200, 1));
        this.topLbl.setTextAlignment(TextAlignment.CENTER);
        HBox topLblWrapper = new HBox(this.topLbl);
        topLblWrapper.setAlignment(Pos.CENTER);
        topLblWrapper.setPadding(new Insets(10, 0, 10, 0));

        Rectangle persegi = new Rectangle();
        persegi.setStyle(
                "-fx-fill: #FFFFFF;" +
                        "-fx-opacity: 0.35;" +
                        "-fx-arc-width: 25;" +
                        "-fx-arc-height: 25;"
        );

        Label namaKegiatanLbl = new Label("NAMA KEGIATAN");
        namaKegiatanLbl.setFont(Font.font("Segoe UI", 20));
        namaKegiatanLbl.setTextFill(Color.rgb(1, 47, 16, 1));
        Label kategoriLbl = new Label("KATEGORI");
        kategoriLbl.setFont(Font.font("Segoe UI", 20));
        kategoriLbl.setTextFill(Color.rgb(1, 47, 16, 1));
        Label tanggalLbl = new Label("TANGGAL");
        tanggalLbl.setFont(Font.font("Segoe UI", 20));
        tanggalLbl.setTextFill(Color.rgb(1, 47, 16, 1));
        Label waktuLbl = new Label("WAKTU");
        waktuLbl.setFont(Font.font("Segoe UI", 20));
        waktuLbl.setTextFill(Color.rgb(1, 47, 16, 1));
        Label lokasiLbl = new Label("LOKASI");
        lokasiLbl.setFont(Font.font("Segoe UI", 20));
        lokasiLbl.setTextFill(Color.rgb(1, 47, 16, 1));
        Label deskripsiLbl = new Label("DESKRIPSI");
        deskripsiLbl.setFont(Font.font("Segoe UI", 20));
        deskripsiLbl.setTextFill(Color.rgb(1, 47, 16, 1));
        Label pengingatLbl = new Label("PENGINGAT EMAIL");
        pengingatLbl.setFont(Font.font("Segoe UI", 20));
        pengingatLbl.setTextFill(Color.rgb(1, 47, 16, 1));

        String fieldStyle = "-fx-background-color: rgb(0, 6, 18, 0.35);" +
                "-fx-border-color: transparent;" +
                "-fx-border-radius: 5;" +
                "-fx-text-fill: rgb(193, 214, 200, 1);" +
                "-fx-font-size: 15px;";
        double fieldPrefWidth = 600;

        this.namaKegiatanField = new TextField();
        this.namaKegiatanField.setPrefWidth(fieldPrefWidth);
        this.namaKegiatanField.setMinWidth(Region.USE_PREF_SIZE);
        this.namaKegiatanField.setStyle(fieldStyle);

        ArrayList<String> kategoriItems = new ArrayList<>();
        kategoriItems.add("AKADEMIK");
        kategoriItems.add("NON-AKADEMIK");
        this.kategoriCB = new ComboBox<>();
        this.kategoriCB.getItems().addAll(kategoriItems);
        this.kategoriCB.setPrefWidth(fieldPrefWidth);
        this.kategoriCB.setMinWidth(Region.USE_PREF_SIZE);
        this.kategoriCB.setStyle(fieldStyle + "-fx-background-insets: 0;");

        this.tanggalDP = new DatePicker();
        this.tanggalDP.setPrefWidth(fieldPrefWidth);
        this.tanggalDP.setMinWidth(Region.USE_PREF_SIZE);
        this.tanggalDP.setStyle(fieldStyle);
        this.tanggalDP.getEditor().setStyle("-fx-text-fill: rgb(193, 214, 200, 1);");

        this.waktuField = new TextField();
        this.waktuField.setPromptText("HH:mm (contoh: 14:30)");
        this.waktuField.setPrefWidth(fieldPrefWidth);
        this.waktuField.setMinWidth(Region.USE_PREF_SIZE);
        this.waktuField.setStyle(fieldStyle);

        this.lokasiField = new TextField();
        this.lokasiField.setPrefWidth(fieldPrefWidth);
        this.lokasiField.setMinWidth(Region.USE_PREF_SIZE);
        this.lokasiField.setStyle(fieldStyle);

        this.deskripsiField = new TextField();
        this.deskripsiField.setPrefWidth(fieldPrefWidth);
        this.deskripsiField.setMinWidth(Region.USE_PREF_SIZE);
        this.deskripsiField.setStyle(fieldStyle);

        this.ingatkanSayaCheckBox = new CheckBox("Ingatkan saya");
        this.ingatkanSayaCheckBox.setTextFill(Color.rgb(1, 47, 16, 1));
        this.ingatkanSayaCheckBox.setFont(Font.font("Segoe UI", 16));

        this.jamSebelumComboBox = new ComboBox<>();
        this.jamSebelumComboBox.getItems().addAll(1, 2, 3, 6, 12, 24);
        this.jamSebelumComboBox.setPromptText("Jam sebelum");
        this.jamSebelumComboBox.setPrefWidth(150);
        this.jamSebelumComboBox.setStyle(fieldStyle + "-fx-background-insets: 0;");
        this.jamSebelumComboBox.setDisable(true);

        this.ingatkanSayaCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            jamSebelumComboBox.setDisable(!newVal);
            if (!newVal) {
                jamSebelumComboBox.getSelectionModel().clearSelection();
            } else {
                jamSebelumComboBox.getSelectionModel().selectFirst();
            }
        });

        HBox pengingatOptionsBox = new HBox(10, this.ingatkanSayaCheckBox, this.jamSebelumComboBox);
        pengingatOptionsBox.setAlignment(Pos.CENTER_LEFT);

        if (this.tugasToEdit != null) {
            this.topLbl.setText("EDIT JADWAL");
            this.namaKegiatanField.setText(this.tugasToEdit.getJudul());
            this.kategoriCB.setValue(this.tugasToEdit.getKategori());
            if (this.tugasToEdit.getTanggalBatas() != null) {
                this.tanggalDP.setValue(this.tugasToEdit.getTanggalBatas().toLocalDate());
                this.waktuField.setText(this.tugasToEdit.getTanggalBatas().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            }
            this.lokasiField.setText(this.tugasToEdit.getLokasi());
            this.deskripsiField.setText(this.tugasToEdit.getDeskripsi());
        } else {
            this.topLbl.setText("MASUKKAN JADWAL BARU");
        }

        GridPane inputGridPane = new GridPane();
        inputGridPane.setHgap(20);
        inputGridPane.setVgap(20);
        inputGridPane.setAlignment(Pos.CENTER);
        inputGridPane.setPadding(new Insets(25, 50, 25, 50));

        ColumnConstraints col0 = new ColumnConstraints();
        col0.setHalignment(HPos.LEFT);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        inputGridPane.getColumnConstraints().addAll(col0, col1);

        inputGridPane.add(namaKegiatanLbl, 0, 0);
        inputGridPane.add(this.namaKegiatanField, 1, 0);
        inputGridPane.add(kategoriLbl, 0, 1);
        inputGridPane.add(this.kategoriCB, 1, 1);
        inputGridPane.add(tanggalLbl, 0, 2);
        inputGridPane.add(this.tanggalDP, 1, 2);
        inputGridPane.add(waktuLbl, 0, 3);
        inputGridPane.add(this.waktuField, 1, 3);
        inputGridPane.add(lokasiLbl, 0, 4);
        inputGridPane.add(this.lokasiField, 1, 4);
        inputGridPane.add(deskripsiLbl, 0, 5);
        inputGridPane.add(this.deskripsiField, 1, 5);
        inputGridPane.add(pengingatLbl, 0, 6);
        inputGridPane.add(pengingatOptionsBox, 1, 6);

        String buttonStyle = "-fx-background-color: rgb(1, 47, 16, 1);" +
                "-fx-border-color: transparent;" +
                "-fx-text-fill: rgb(193, 214, 200, 1);" +
                "-fx-font-size: 18px;" +
                "-fx-cursor: hand;" +
                "-fx-font-weight: BOLD;" +
                "-fx-background-radius: 30;";
        Button batalBtn = new Button("BATAL");
        batalBtn.setStyle(buttonStyle);
        batalBtn.setPrefSize(180, 35);
        Button simpanBtn = new Button("SIMPAN");
        simpanBtn.setStyle(buttonStyle);
        simpanBtn.setPrefSize(180, 35);

        StackPane inputCard = new StackPane();
        persegi.widthProperty().bind(inputGridPane.widthProperty());
        persegi.heightProperty().bind(inputGridPane.heightProperty());
        inputCard.getChildren().addAll(persegi, inputGridPane);
        inputCard.setPadding(new Insets(20));

        HBox tombolBox = new HBox(30, batalBtn, simpanBtn);
        tombolBox.setAlignment(Pos.CENTER);
        tombolBox.setPadding(new Insets(20, 0, 20, 0));

        VBox formContentVBox = new VBox(15, inputCard, tombolBox);
        formContentVBox.setAlignment(Pos.TOP_CENTER);
        formContentVBox.setMaxWidth(Double.MAX_VALUE);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(formContentVBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        BorderPane pageLayout = new BorderPane();
        pageLayout.setTop(topLblWrapper);
        pageLayout.setCenter(scrollPane);
        pageLayout.setPadding(new Insets(10, 30, 10, 30));

        this.getChildren().addAll(backgroundImage, pageLayout);

        batalBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Konfirmasi Batal");
            alert.setHeaderText("Anda yakin ingin membatalkan?");
            alert.setContentText(this.tugasToEdit != null ? "Perubahan tidak akan disimpan." : "Jadwal baru tidak akan dibuat.");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                this.app.switchSceneSchedulePage();
            }
        });

        simpanBtn.setOnAction(e -> {
            handleSimpan();
        });
    }

    private void handleSimpan() {
        String namaKegiatan = this.namaKegiatanField.getText();
        String kategori = this.kategoriCB.getValue();
        LocalDate tanggal = this.tanggalDP.getValue();
        String waktuText = this.waktuField.getText();
        String lokasi = this.lokasiField.getText();
        String deskripsi = this.deskripsiField.getText();
        boolean ingatkan = this.ingatkanSayaCheckBox.isSelected();
        Integer jamSebelumDeadline = this.jamSebelumComboBox.getValue();

        if (namaKegiatan == null || namaKegiatan.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Tidak Lengkap", "Nama Kegiatan tidak boleh kosong.");
            return;
        }
        if (kategori == null) {
            showAlert(Alert.AlertType.WARNING, "Input Tidak Lengkap", "Kategori harus dipilih.");
            return;
        }
        if (tanggal == null) {
            showAlert(Alert.AlertType.WARNING, "Input Tidak Lengkap", "Tanggal harus dipilih.");
            return;
        }

        LocalDateTime tanggalBatasGabungan = null;
        if (waktuText != null && !waktuText.trim().isEmpty()) {
            try {
                LocalTime waktu = LocalTime.parse(waktuText, DateTimeFormatter.ofPattern("HH:mm"));
                tanggalBatasGabungan = LocalDateTime.of(tanggal, waktu);
            } catch (DateTimeParseException ex) {
                showAlert(Alert.AlertType.ERROR, "Format Waktu Salah", "Gunakan format HH:mm untuk waktu (contoh: 09:30 atau 14:00).");
                return;
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Input Tidak Lengkap", "Waktu harus diisi.");
            return;
        }

        if (ingatkan && (jamSebelumDeadline == null || jamSebelumDeadline <= 0)) {
            showAlert(Alert.AlertType.WARNING, "Input Tidak Lengkap", "Pilih berapa jam sebelum deadline untuk pengingat.");
            return;
        }

        String lokasiInput = (lokasi == null || lokasi.trim().isEmpty()) ? "Belum Diisi" : lokasi.trim();
        boolean sukses;
        String mataKuliahInput = "";
        Tugas tugasHasil = null;

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi Simpan");
        confirmAlert.setHeaderText("Anda yakin ingin menyimpan jadwal ini?");
        confirmAlert.setContentText("Pastikan semua data sudah benar.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (tugasToEdit == null) {
                tugasHasil = this.app.getPengelolaTugas().buatTugas(
                        namaKegiatan,
                        deskripsi,
                        tanggalBatasGabungan,
                        kategori,
                        lokasiInput,
                        mataKuliahInput
                );
                sukses = (tugasHasil != null);
            } else {
                sukses = this.app.getPengelolaTugas().ubahTugas(
                        tugasToEdit.getId(),
                        namaKegiatan,
                        deskripsi,
                        tanggalBatasGabungan,
                        kategori,
                        lokasiInput,
                        mataKuliahInput,
                        tugasToEdit.isSelesai()
                );
                if(sukses) tugasHasil = app.getPengelolaTugas().getTugasById(tugasToEdit.getId());
            }

            if (sukses) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data jadwal berhasil disimpan.");
                if (ingatkan && jamSebelumDeadline != null && tugasHasil != null && app.getPengelolaTugas().getCurrentUser() != null) {
                    LocalDateTime waktuPengingat = tanggalBatasGabungan.minusHours(jamSebelumDeadline);
                    User currentUser = app.getPengelolaTugas().getCurrentUser();
                    app.scheduleEmailReminder(tugasHasil, currentUser, waktuPengingat);
                }
                this.app.switchSceneSchedulePage();
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menyimpan data jadwal. Periksa konsol untuk detail error.");
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}