package tubes.backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PengelolaTugas {
    private User currentUser;

    public PengelolaTugas() {
        this.currentUser = null;
    }

    // Metode daftarAkun, masukSistem, logout, getCurrentUser tetap sama...
    public User daftarAkun(String username, String email, String sandi) {
        String sqlCheck = "SELECT id FROM users WHERE username = ? OR email = ?";
        String sqlInsert = "INSERT INTO users (username, email, sandi) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmtCheck = conn.prepareStatement(sqlCheck)) {

            pstmtCheck.setString(1, username);
            pstmtCheck.setString(2, email);
            try (ResultSet rsCheck = pstmtCheck.executeQuery()) {
                if (rsCheck.next()) {
                    System.out.println("Username atau email sudah terdaftar.");
                    return null;
                }
            }

            try (PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                pstmtInsert.setString(1, username);
                pstmtInsert.setString(2, email);
                pstmtInsert.setString(3, sandi);

                int affectedRows = pstmtInsert.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = pstmtInsert.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int newUserId = generatedKeys.getInt(1);
                            User newUser = new User(newUserId, username, email, sandi);
                            System.out.println("User " + username + " berhasil terdaftar dengan ID: " + newUserId);
                            return newUser;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error SQL saat mendaftarkan akun: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean masukSistem(String usernameOrEmail, String sandiInput) {
        String sql = "SELECT id, username, email, sandi FROM users WHERE username = ? OR email = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usernameOrEmail);
            pstmt.setString(2, usernameOrEmail);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String sandiDariDB = rs.getString("sandi");
                    if (sandiDariDB.equals(sandiInput)) {
                        this.currentUser = new User(
                                rs.getInt("id"),
                                rs.getString("username"),
                                rs.getString("email"),
                                sandiDariDB
                        );
                        this.currentUser.setDaftarTugas(getTugasByUserId(this.currentUser.getId()));
                        System.out.println("User " + this.currentUser.getUsername() + " berhasil login.");
                        return true;
                    } else {
                        System.out.println("Password salah untuk user: " + usernameOrEmail);
                    }
                } else {
                    System.out.println("Username/Email tidak ditemukan: " + usernameOrEmail);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error SQL saat login: " + e.getMessage());
            e.printStackTrace();
        }
        this.currentUser = null;
        return false;
    }

    public void logout() {
        if (this.currentUser != null) {
            System.out.println("User " + this.currentUser.getUsername() + " logout.");
        }
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }
    // Akhir dari metode yang tidak berubah

    // --- PERBAIKAN DIMULAI DI SINI ---

    public Tugas buatTugas(String judul, String deskripsi, LocalDateTime tanggalBatas, String kategori, String lokasi) {
        if (currentUser == null) {
            System.err.println("Tidak ada user yang login untuk membuat tugas.");
            return null;
        }

        // PERBAIKAN: Hapus 'mata_kuliah' dari query SQL
        String sql = "INSERT INTO tasks (user_id, judul, deskripsi, tanggal_batas, kategori, lokasi) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, currentUser.getId());
            pstmt.setString(2, judul);
            pstmt.setString(3, deskripsi);
            pstmt.setString(4, tanggalBatas != null ? tanggalBatas.toString() : null);
            pstmt.setString(5, kategori);
            pstmt.setString(6, lokasi);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int newTaskId = generatedKeys.getInt(1);
                        // PERBAIKAN: Panggil konstruktor yang benar (8 argumen) dengan status default
                        Tugas tugasBaru = new Tugas(newTaskId, currentUser.getId(), judul, deskripsi, tanggalBatas, kategori, lokasi, "Belum Dimulai");

                        if (this.currentUser.getDaftarTugas() != null) {
                            this.currentUser.tambahTugas(tugasBaru);
                        }
                        System.out.println("Tugas '" + judul + "' berhasil dibuat untuk user " + currentUser.getUsername());
                        return tugasBaru;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error SQL saat membuat tugas: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // PERBAIKAN: ubahTugas tanpa mataKuliah
    public boolean ubahTugas(int idTugas, String judul, String deskripsi, LocalDateTime tanggalBatas, String kategori, String lokasi) {
        if (currentUser == null) {
            return false;
        }
        // PERBAIKAN: Hapus mata_kuliah dari query SQL
        String sql = "UPDATE tasks SET judul = ?, deskripsi = ?, tanggal_batas = ?, kategori = ?, lokasi = ? WHERE id = ? AND user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, judul);
            pstmt.setString(2, deskripsi);
            pstmt.setString(3, tanggalBatas != null ? tanggalBatas.toString() : null);
            pstmt.setString(4, kategori);
            pstmt.setString(5, lokasi);
            pstmt.setInt(6, idTugas);
            pstmt.setInt(7, currentUser.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Metode hapusTugas tetap sama
    public boolean hapusTugas(int idTugas) {
        if (currentUser == null) {
            return false;
        }
        String sql = "DELETE FROM tasks WHERE id = ? AND user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idTugas);
            pstmt.setInt(2, currentUser.getId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                if (this.currentUser.getDaftarTugas() != null) {
                    this.currentUser.getDaftarTugas().removeIf(t -> t.getId() == idTugas);
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Tugas> getTugasCurrentUser() {
        if (currentUser == null) {
            return new ArrayList<>();
        }
        List<Tugas> tugasDariDB = getTugasByUserId(currentUser.getId());
        currentUser.setDaftarTugas(tugasDariDB);
        return tugasDariDB;
    }

    // PERBAIKAN: getTugasByUserId tanpa mataKuliah
    private List<Tugas> getTugasByUserId(int userId) {
        List<Tugas> daftarTugasUser = new ArrayList<>();
        // PERBAIKAN: Hapus mata_kuliah dari query SQL
        String sql = "SELECT id, user_id, judul, deskripsi, tanggal_batas, kategori, lokasi, status " +
                "FROM tasks WHERE user_id = ? ORDER BY tanggal_batas ASC, id DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String tanggalBatasStr = rs.getString("tanggal_batas");
                    LocalDateTime tglBatasObj = null;
                    if (tanggalBatasStr != null) {
                        try {
                            tglBatasObj = LocalDateTime.parse(tanggalBatasStr);
                        } catch (DateTimeParseException e) {
                            System.err.println("Format tanggal_batas tidak valid di DB: " + tanggalBatasStr);
                        }
                    }
                    // PERBAIKAN: Panggil konstruktor yang benar (8 argumen)
                    Tugas tugas = new Tugas(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("judul"),
                            rs.getString("deskripsi"),
                            tglBatasObj,
                            rs.getString("kategori"),
                            rs.getString("lokasi"),
                            rs.getString("status") // Ambil status dari DB
                    );
                    daftarTugasUser.add(tugas);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return daftarTugasUser;
    }
    public Tugas getTugasById(int idTugas) {
        if (currentUser == null) {
            System.err.println("Tidak ada user yang login untuk mengambil tugas by ID.");
            return null;
        }

        // Query SQL sudah diperbarui tanpa 'mata_kuliah'
        String sql = "SELECT id, user_id, judul, deskripsi, tanggal_batas, kategori, lokasi, status " +
                "FROM tasks WHERE id = ? AND user_id = ?";
        Tugas tugas = null;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idTugas);
            pstmt.setInt(2, currentUser.getId());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String tanggalBatasStr = rs.getString("tanggal_batas");
                    LocalDateTime tglBatasObj = null;
                    if (tanggalBatasStr != null && !tanggalBatasStr.trim().isEmpty()) {
                        try {
                            tglBatasObj = LocalDateTime.parse(tanggalBatasStr);
                        } catch (DateTimeParseException e) {
                            System.err.println("Format tanggal_batas tidak valid di DB (getTugasById): " + tanggalBatasStr);
                        }
                    }

                    // Panggil konstruktor Tugas yang benar (8 argumen, tanpa mata_kuliah)
                    tugas = new Tugas(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("judul"),
                            rs.getString("deskripsi"),
                            tglBatasObj,
                            rs.getString("kategori"),
                            rs.getString("lokasi"),
                            rs.getString("status")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error SQL saat mengambil tugas by ID " + idTugas + ": " + e.getMessage());
            e.printStackTrace();
        }
        return tugas;
    }
    public List<Tugas> getTugasCurrentUserByKategori(String kategoriFilter) {
        if (currentUser == null) {
            return new ArrayList<>();
        }
        // Jika filter "Semua" atau kosong, panggil metode yang mengambil semua tugas
        if (kategoriFilter == null || kategoriFilter.equalsIgnoreCase("Semua") || kategoriFilter.trim().isEmpty()) {
            return getTugasCurrentUser();
        }

        List<Tugas> daftarTugasUser = new ArrayList<>();
        // Query SQL sudah diperbarui tanpa 'mata_kuliah'
        String sql = "SELECT id, user_id, judul, deskripsi, tanggal_batas, kategori, lokasi, status " +
                "FROM tasks WHERE user_id = ? AND kategori = ? ORDER BY tanggal_batas ASC, id DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, currentUser.getId());
            pstmt.setString(2, kategoriFilter);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String tanggalBatasStr = rs.getString("tanggal_batas");
                    LocalDateTime tglBatasObj = null;
                    if (tanggalBatasStr != null && !tanggalBatasStr.trim().isEmpty()) {
                        try {
                            tglBatasObj = LocalDateTime.parse(tanggalBatasStr);
                        } catch (DateTimeParseException e) {
                            System.err.println("Format tanggal_batas tidak valid di DB: " + tanggalBatasStr);
                        }
                    }

                    // Panggil konstruktor Tugas yang benar (8 argumen)
                    Tugas tugas = new Tugas(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("judul"),
                            rs.getString("deskripsi"),
                            tglBatasObj,
                            rs.getString("kategori"),
                            rs.getString("lokasi"),
                            rs.getString("status")
                    );
                    daftarTugasUser.add(tugas);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error SQL saat mengambil tugas berdasarkan kategori '" + kategoriFilter + "': " + e.getMessage());
            e.printStackTrace();
        }
        return daftarTugasUser;
    }


    // Perbaikan untuk metode-metode lain yang serupa...
    // Anda perlu melanjutkan pola perbaikan ini untuk semua metode yang berinteraksi dengan tabel 'tasks'.
}
