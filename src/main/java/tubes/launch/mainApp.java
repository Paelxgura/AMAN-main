package tubes.launch;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tubes.backend.DatabaseManager;
import tubes.backend.EmailService;
import tubes.backend.Notifikasi;
import tubes.backend.PengelolaTugas;
import tubes.backend.Tugas;
import tubes.backend.User;
import tubes.pages.EditSchedule;
import tubes.pages.LogInPage;
import tubes.pages.SchedulePage;
import tubes.pages.SignUpPage;
import tubes.pages.WelcomePage;

public class mainApp extends Application {

    private Stage stage;
    private Scene scene;

    public static PengelolaTugas pengelolaTugas;
    public static Notifikasi notifikasiService;
    public static EmailService emailService;

    private ScheduledExecutorService scheduler;

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SMTP_USERNAME = "amannotifikasi@gmail.com";
    private static final String SMTP_PASSWORD = "klac pxfp mkne govf";
    private static final boolean SMTP_AUTH = true;
    private static final boolean SMTP_STARTTLS = true;
    private static final boolean SMTP_SSL_ENABLE = false;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();

        pengelolaTugas = new PengelolaTugas();
        inisialisasiDatabase();

        emailService = new EmailService(
                SMTP_HOST,
                SMTP_PORT,
                SMTP_USERNAME,
                SMTP_PASSWORD,
                SMTP_AUTH,
                SMTP_STARTTLS,
                SMTP_SSL_ENABLE
        );
        notifikasiService = new Notifikasi(emailService);

        WelcomePage welcomePage = new WelcomePage(this);
        this.scene = new Scene(welcomePage, 1440, 800);

        stage.setTitle("AMAN");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            System.out.println("Aplikasi ditutup. Mematikan scheduler...");
            shutdownScheduler();
        });
        stage.show();
    }

    private void inisialisasiDatabase() {
        System.out.println("Memulai proses inisialisasi database SQLite...");
        String createUserTableSql = "CREATE TABLE IF NOT EXISTS users ("
                                  + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                                  + "username TEXT NOT NULL UNIQUE,"
                                  + "email TEXT NOT NULL UNIQUE,"
                                  + "sandi TEXT NOT NULL,"
                                  + "created_at TEXT DEFAULT (STRFTIME('%Y-%m-%d %H:%M:%S', 'now', 'localtime'))"
                                  + ");";

        String createTasksTableSql = "CREATE TABLE IF NOT EXISTS tasks ("
                                   + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                                   + "user_id INTEGER NOT NULL,"
                                   + "judul TEXT NOT NULL,"
                                   + "deskripsi TEXT,"
                                   + "tanggal_batas TEXT,"
                                   + "kategori TEXT,"
                                   + "lokasi TEXT,"
                                   + "mata_kuliah TEXT,"
                                   + "selesai INTEGER DEFAULT 0,"
                                   + "pengingat_dikirim INTEGER DEFAULT 0,"
                                   + "created_at TEXT DEFAULT (STRFTIME('%Y-%m-%d %H:%M:%S', 'now', 'localtime')),"
                                   + "updated_at TEXT,"
                                   + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"
                                   + ");";

        String createTasksUpdateTriggerSql = "CREATE TRIGGER IF NOT EXISTS update_tasks_updated_at "
                                           + "AFTER UPDATE ON tasks "
                                           + "FOR EACH ROW "
                                           + "BEGIN "
                                           + "    UPDATE tasks SET updated_at = STRFTIME('%Y-%m-%d %H:%M:%S', 'now', 'localtime') WHERE id = OLD.id; "
                                           + "END;";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            if (conn == null) {
                System.err.println("INISIALISASI DB GAGAL: Koneksi database null.");
                return;
            }
            stmt.execute(createUserTableSql);
            stmt.execute(createTasksTableSql);
            stmt.execute(createTasksUpdateTriggerSql);
            System.out.println("Skema database SQLite (users, tasks, trigger) berhasil disiapkan/diverifikasi.");
        } catch (SQLException e) {
            System.err.println("Error SQL saat inisialisasi skema database SQLite:" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void scheduleEmailReminder(Tugas tugas, User user, LocalDateTime waktuPengingat) {
        if (scheduler == null || scheduler.isShutdown()) {
            System.err.println("Scheduler tidak aktif. Tidak dapat menjadwalkan pengingat.");
            return;
        }
        if (tugas == null || user == null || user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            System.err.println("Data tugas atau user tidak valid untuk penjadwalan email.");
            return;
        }
        
        long delay;
        if (waktuPengingat == null) {
            System.err.println("Waktu pengingat tidak valid (null) untuk tugas: " + tugas.getJudul());
            return; 
        }

        if (waktuPengingat.isBefore(LocalDateTime.now())) {
            System.out.println("Waktu pengingat untuk tugas '" + tugas.getJudul() + "' (" + waktuPengingat + ") sudah lewat. Mengirim segera.");
            delay = 0; 
        } else {
            delay = Duration.between(LocalDateTime.now(), waktuPengingat).toMillis();
        }

        if (delay < 0) {
            delay = 0;
        }

        scheduler.schedule(() -> {
            System.out.println("Scheduler: Memulai proses pengiriman pengingat untuk tugas: " + tugas.getJudul());
            boolean terkirim = notifikasiService.kirimPengingat(tugas, user);
            if (terkirim) {
                System.out.println("Scheduler: Pengingat untuk '" + tugas.getJudul() + "' berhasil dikirim ke " + user.getEmail());
            } else {
                System.err.println("Scheduler: Gagal mengirim pengingat untuk '" + tugas.getJudul() + "' ke " + user.getEmail());
            }
        }, delay, TimeUnit.MILLISECONDS);

        if (delay == 0) {
             System.out.println("Pengingat untuk tugas '" + tugas.getJudul() + "' dijadwalkan untuk dikirim segera.");
        } else {
             System.out.println("Pengingat untuk tugas '" + tugas.getJudul() + "' dijadwalkan pada " + waktuPengingat + " (delay: " + delay + "ms)");
        }
    }

    public void shutdownScheduler() {
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                    if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                        System.err.println("Scheduler tidak dapat dimatikan dengan benar.");
                    }
                }
            } catch (InterruptedException ie) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }


    public void switchSceneWelcomePage(){
        this.scene.setRoot(new WelcomePage(this));
    }

    public void switchSceneLogInPage(){
        this.scene.setRoot(new LogInPage(this));
    }

    public void switchSceneSignUpPage(){
        this.scene.setRoot(new SignUpPage(this));
    }

    public void switchSceneSchedulePage(){
        if (pengelolaTugas == null || pengelolaTugas.getCurrentUser() == null) {
            System.out.println("Tidak ada user yang login atau PengelolaTugas belum siap, mengarahkan ke halaman Login.");
            switchSceneLogInPage();
            return;
        }
        this.scene.setRoot(new SchedulePage(this));
    }

    public void switchSceneEditSchedulePage(Tugas tugasToEdit){
        if (pengelolaTugas == null || pengelolaTugas.getCurrentUser() == null) {
            switchSceneLogInPage();
            return;
        }
        this.scene.setRoot(new EditSchedule(this, tugasToEdit));
    }

    public void switchSceneEditSchedulePage(){
        if (pengelolaTugas == null || pengelolaTugas.getCurrentUser() == null) {
            switchSceneLogInPage();
            return;
        }
        this.scene.setRoot(new EditSchedule(this, null));
    }

    public PengelolaTugas getPengelolaTugas() {
        return pengelolaTugas;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}