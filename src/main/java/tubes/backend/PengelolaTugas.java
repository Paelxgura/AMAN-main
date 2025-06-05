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


    public User daftarAkun(String username, String email, String sandi) {


        String sqlCheck = "SELECT id FROM users WHERE username = ? OR email = ?";
        String sqlInsert = "INSERT INTO users (username, email, sandi) VALUES (?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement pstmtCheck = null;
        ResultSet rsCheck = null;
        PreparedStatement pstmtInsert = null;
        ResultSet generatedKeys = null;

        try {
            conn = DatabaseManager.getConnection();
            if (conn == null) {
                System.err.println("Daftar akun gagal: Tidak bisa mendapatkan koneksi database.");
                return null;
            }

            pstmtCheck = conn.prepareStatement(sqlCheck);
            pstmtCheck.setString(1, username);
            pstmtCheck.setString(2, email);
            rsCheck = pstmtCheck.executeQuery();
            if (rsCheck.next()) {
                System.out.println("Username atau email sudah terdaftar.");
                return null; // checking
            }
            
            DatabaseManager.closeResultSet(rsCheck); 
            rsCheck = null; 
            DatabaseManager.closeStatement(pstmtCheck);
            pstmtCheck = null; 


            pstmtInsert = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            pstmtInsert.setString(1, username);
            pstmtInsert.setString(2, email);
            pstmtInsert.setString(3, sandi);

            int affectedRows = pstmtInsert.executeUpdate();

            if (affectedRows > 0) {
                generatedKeys = pstmtInsert.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int newUserId = generatedKeys.getInt(1);
                    User newUser = new User(newUserId, username, email, sandi);
                    System.out.println("User " + username + " berhasil terdaftar dengan ID: " + newUserId);
                    return newUser;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error SQL saat mendaftarkan akun: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseManager.closeResultSet(rsCheck); 
            DatabaseManager.closeStatement(pstmtCheck); 
            DatabaseManager.closeResultSet(generatedKeys);
            DatabaseManager.closeStatement(pstmtInsert);
            DatabaseManager.closeConnection(conn);
        }
        return null;
    }

    public boolean masukSistem(String usernameOrEmail, String sandiInput) {
        String sql = "SELECT id, username, email, sandi FROM users WHERE username = ? OR email = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseManager.getConnection();
            if (conn == null) {
                System.err.println("Login gagal: Tidak bisa mendapatkan koneksi database.");
                return false;
            }

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, usernameOrEmail); 
            pstmt.setString(2, usernameOrEmail); 

            rs = pstmt.executeQuery();

            if (rs.next()) {
                String sandiDariDB = rs.getString("sandi");
                if (sandiDariDB.equals(sandiInput)) { 
                    this.currentUser = new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("email"),
                            sandiDariDB 
                    );
                    
                    this.currentUser.setDaftarTugas(getTugasByUserId(this.currentUser.getId(), conn)); 
                    System.out.println("User " + this.currentUser.getUsername() + " berhasil login.");
                    return true;
                } else {
                    System.out.println("Password salah untuk user: " + usernameOrEmail);
                }
            } else {
                System.out.println("Username/Email tidak ditemukan: " + usernameOrEmail);
            }
        } catch (SQLException e) {
            System.err.println("Error SQL saat login: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseManager.closeResultSet(rs);
            DatabaseManager.closeStatement(pstmt);
            DatabaseManager.closeConnection(conn); 
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

    public Tugas buatTugas(String judul, String deskripsi, LocalDateTime tanggalBatas, String kategori, String lokasi, String mataKuliah) {
        if (currentUser == null) {
            System.err.println("Tidak ada user yang login untuk membuat tugas.");
            return null;
        }

        String sql = "INSERT INTO tasks (user_id, judul, deskripsi, tanggal_batas, kategori, lokasi, mata_kuliah, selesai) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;

        try {
            conn = DatabaseManager.getConnection();
            if (conn == null) {
                 System.err.println("Buat tugas gagal: Tidak bisa mendapatkan koneksi database.");
                return null;
            }

            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, currentUser.getId());
            pstmt.setString(2, judul);
            pstmt.setString(3, deskripsi);

            if (tanggalBatas != null) {
                pstmt.setString(4, tanggalBatas.toString()); 
            } else {
                pstmt.setNull(4, java.sql.Types.VARCHAR); 
            }

            pstmt.setString(5, kategori);
            pstmt.setString(6, lokasi);
            pstmt.setString(7, mataKuliah);

    
            pstmt.setInt(8, 0);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int newTaskId = generatedKeys.getInt(1);
                    Tugas tugasBaru = new Tugas(newTaskId, currentUser.getId(), judul, deskripsi, tanggalBatas, kategori, lokasi, mataKuliah, false);
                    if (this.currentUser.getDaftarTugas() != null) { 
                        this.currentUser.tambahTugas(tugasBaru);
                    }
                    System.out.println("Tugas '" + judul + "' berhasil dibuat untuk user " + currentUser.getUsername() + " dengan ID: " + newTaskId);
                    return tugasBaru;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error SQL saat membuat tugas: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseManager.closeResultSet(generatedKeys);
            DatabaseManager.closeStatement(pstmt);
            DatabaseManager.closeConnection(conn);
        }
        return null;
    }

    public boolean ubahTugas(int idTugas, String judul, String deskripsi, LocalDateTime tanggalBatas, String kategori, String lokasi, String mataKuliah, boolean selesai) {
        if (currentUser == null) {
            System.err.println("Tidak ada user yang login untuk mengubah tugas.");
            return false;
        }

        String sql = "UPDATE tasks SET judul = ?, deskripsi = ?, tanggal_batas = ?, kategori = ?, " +
                     "lokasi = ?, mata_kuliah = ?, selesai = ? WHERE id = ? AND user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseManager.getConnection();
            if (conn == null) {
                System.err.println("Ubah tugas gagal: Tidak bisa mendapatkan koneksi database.");
                return false;
            }

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, judul);
            pstmt.setString(2, deskripsi);

            if (tanggalBatas != null) {
                pstmt.setString(3, tanggalBatas.toString());
            } else {
                pstmt.setNull(3, java.sql.Types.VARCHAR);
            }

            pstmt.setString(4, kategori);
            pstmt.setString(5, lokasi);
            pstmt.setString(6, mataKuliah);

            pstmt.setInt(7, selesai ? 1 : 0); 
            pstmt.setInt(8, idTugas);
            pstmt.setInt(9, currentUser.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                if (this.currentUser.getDaftarTugas() != null) {
                    this.currentUser.getDaftarTugas().stream()
                        .filter(t -> t.getId() == idTugas)
                        .findFirst()
                        .ifPresent(t -> {
                            t.setJudul(judul);
                            t.setDeskripsi(deskripsi);
                            t.setTanggalBatas(tanggalBatas);
                            t.setKategori(kategori);
                            t.setLokasi(lokasi);
                            t.setMataKuliah(mataKuliah);
                            t.setSelesai(selesai);
                        });
                }
                System.out.println("Tugas ID " + idTugas + " berhasil diubah.");
                return true;
            } else {
                System.err.println("Gagal mengubah tugas: Tugas ID " + idTugas + " tidak ditemukan atau bukan milik user.");
            }
        } catch (SQLException e) {
            System.err.println("Error SQL saat mengubah tugas: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseManager.closeStatement(pstmt);
            DatabaseManager.closeConnection(conn);
        }
        return false;
    }
    
    public boolean hapusTugas(int idTugas) {
        if (currentUser == null) {
            System.err.println("Tidak ada user yang login untuk menghapus tugas.");
            return false;
        }

        String sql = "DELETE FROM tasks WHERE id = ? AND user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseManager.getConnection();
            if (conn == null) {
                System.err.println("Hapus tugas gagal: Tidak bisa mendapatkan koneksi database.");
                return false;
            }

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idTugas);
            pstmt.setInt(2, currentUser.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                if (this.currentUser.getDaftarTugas() != null) {
                    this.currentUser.hapusTugas(idTugas);
                }
                System.out.println("Tugas ID " + idTugas + " berhasil dihapus.");
                return true;
            } else {
                 System.err.println("Gagal menghapus tugas: Tugas ID " + idTugas + " tidak ditemukan atau bukan milik user.");
            }
        } catch (SQLException e) {
            System.err.println("Error SQL saat menghapus tugas: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseManager.closeStatement(pstmt);
            DatabaseManager.closeConnection(conn);
        }
        return false;
    }

    public List<Tugas> getTugasCurrentUser() {
        if (currentUser == null) {
            return new ArrayList<>();
        }
        List<Tugas> tugasDariDB = getTugasByUserId(currentUser.getId(), null);
        currentUser.setDaftarTugas(tugasDariDB);
        return tugasDariDB;
    }

    private List<Tugas> getTugasByUserId(int userId, Connection existingConn) {
        List<Tugas> daftarTugasUser = new ArrayList<>();
        String sql = "SELECT id, user_id, judul, deskripsi, tanggal_batas, kategori, lokasi, mata_kuliah, selesai " +
                     "FROM tasks WHERE user_id = ? ORDER BY tanggal_batas ASC, id DESC";
        
        Connection conn = existingConn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean closeConnHere = false; 

        try {
            if (conn == null || conn.isClosed()) { 
                conn = DatabaseManager.getConnection();
                if (conn == null) {
                    System.err.println("Ambil tugas gagal: Tidak bisa mendapatkan koneksi database.");
                    return daftarTugasUser;
                }
                closeConnHere = true;
            }

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String tanggalBatasStr = rs.getString("tanggal_batas");
                LocalDateTime tglBatasObj = null;
                if (tanggalBatasStr != null && !tanggalBatasStr.isEmpty()) {
                    try {
                        tglBatasObj = LocalDateTime.parse(tanggalBatasStr); 
                    } catch (DateTimeParseException e) {
                        System.err.println("Format tanggal_batas tidak valid di DB: " + tanggalBatasStr + " untuk tugas id: " + rs.getInt("id"));
                    }
                }
                
                boolean statusSelesai = (rs.getInt("selesai") == 1); 

                Tugas tugas = new Tugas(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("judul"),
                        rs.getString("deskripsi"),
                        tglBatasObj, 
                        rs.getString("kategori"),
                        rs.getString("lokasi"),
                        rs.getString("mata_kuliah"),
                        statusSelesai 
                );
                daftarTugasUser.add(tugas);
            }
        } catch (SQLException e) {
            System.err.println("Error SQL saat mengambil tugas user ID " + userId + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseManager.closeResultSet(rs);
            DatabaseManager.closeStatement(pstmt);
            if (closeConnHere) { 
                DatabaseManager.closeConnection(conn);
            }
        }
        return daftarTugasUser;
    }

    public List<Tugas> getTugasCurrentUserByKategori(String kategoriFilter) {
        if (currentUser == null) {
            return new ArrayList<>();
        }
        if (kategoriFilter == null || kategoriFilter.equalsIgnoreCase("Semua") || kategoriFilter.trim().isEmpty()) {
            return getTugasCurrentUser(); 
        }

        List<Tugas> daftarTugasUser = new ArrayList<>();
        String sql = "SELECT id, user_id, judul, deskripsi, tanggal_batas, kategori, lokasi, mata_kuliah, selesai " +
                     "FROM tasks WHERE user_id = ? AND kategori = ? ORDER BY tanggal_batas ASC, id DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseManager.getConnection();
            if (conn == null) {
                System.err.println("Ambil tugas by kategori gagal: Tidak bisa mendapatkan koneksi database.");
                return daftarTugasUser;
            }

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, currentUser.getId());
            pstmt.setString(2, kategoriFilter);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String tanggalBatasStr = rs.getString("tanggal_batas");
                LocalDateTime tglBatasObj = null;
                if (tanggalBatasStr != null && !tanggalBatasStr.isEmpty()) {
                     try {
                        tglBatasObj = LocalDateTime.parse(tanggalBatasStr);
                    } catch (DateTimeParseException e) {
                        System.err.println("Format tanggal_batas tidak valid di DB: " + tanggalBatasStr + " untuk tugas id: " + rs.getInt("id"));
                    }
                }
                boolean statusSelesai = (rs.getInt("selesai") == 1);

                Tugas tugas = new Tugas(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("judul"),
                        rs.getString("deskripsi"),
                        tglBatasObj,
                        rs.getString("kategori"),
                        rs.getString("lokasi"),
                        rs.getString("mata_kuliah"),
                        statusSelesai
                );
                daftarTugasUser.add(tugas);
            }
        } catch (SQLException e) {
            System.err.println("Error SQL saat mengambil tugas berdasarkan kategori '" + kategoriFilter + "': " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseManager.closeResultSet(rs);
            DatabaseManager.closeStatement(pstmt);
            DatabaseManager.closeConnection(conn);
        }
        return daftarTugasUser;
    }

public Tugas getTugasById(int idTugas) {
    if (currentUser == null) {
        System.err.println("Tidak ada user yang login untuk mengambil tugas by ID.");
        return null;
    }

    String sql = "SELECT id, user_id, judul, deskripsi, tanggal_batas, kategori, lokasi, mata_kuliah, selesai " +
                 "FROM tasks WHERE id = ? AND user_id = ?";
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    Tugas tugas = null;

    try {
        conn = DatabaseManager.getConnection();
        if (conn == null) {
            System.err.println("Ambil tugas by ID gagal: Tidak bisa mendapatkan koneksi database.");
            return null;
        }

        pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, idTugas);
        pstmt.setInt(2, currentUser.getId());
        rs = pstmt.executeQuery();

        if (rs.next()) {
            String tanggalBatasStr = rs.getString("tanggal_batas");
            LocalDateTime tglBatasObj = null;
            if (tanggalBatasStr != null && !tanggalBatasStr.isEmpty()) {
                try {
                    tglBatasObj = LocalDateTime.parse(tanggalBatasStr);
                } catch (DateTimeParseException e) {
                    System.err.println("Format tanggal_batas tidak valid di DB (getTugasById): " + tanggalBatasStr + " untuk tugas id: " + rs.getInt("id"));
                }
            }
            boolean statusSelesai = (rs.getInt("selesai") == 1);

            tugas = new Tugas(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("judul"),
                    rs.getString("deskripsi"),
                    tglBatasObj,
                    rs.getString("kategori"),
                    rs.getString("lokasi"),
                    rs.getString("mata_kuliah"),
                    statusSelesai
            );
        } else {
            System.out.println("Tugas dengan ID " + idTugas + " tidak ditemukan untuk user " + currentUser.getUsername());
        }
    } catch (SQLException e) {
        System.err.println("Error SQL saat mengambil tugas by ID " + idTugas + ": " + e.getMessage());
        e.printStackTrace();
    } finally {
        DatabaseManager.closeResultSet(rs);
        DatabaseManager.closeStatement(pstmt);
        DatabaseManager.closeConnection(conn);
    }
    return tugas;
}

    public Map<String, Long> rekapKategoriCurrentUser() {
        if (currentUser == null) {
            return new HashMap<>();
        }
        List<Tugas> tugasUserSaatIni = getTugasByUserId(currentUser.getId(), null);
        if (tugasUserSaatIni.isEmpty()) {
            return new HashMap<>();
        }
        return tugasUserSaatIni.stream()
                .filter(t -> t.getKategori() != null) 
                .collect(Collectors.groupingBy(Tugas::getKategori, Collectors.counting()));
    }
}