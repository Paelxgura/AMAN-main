package tubes.backend;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Tugas {
    private int id;
    private int userId;
    private String judul;
    private String deskripsi;
    private LocalDateTime tanggalBatas;
    private String kategori;
    private String lokasi;
    private String mataKuliah;
    private boolean selesai;

    public Tugas(int id, int userId, String judul, String deskripsi, LocalDateTime tanggalBatas, String kategori, String lokasi, String mataKuliah, boolean selesai) {
        this.id = id;
        this.userId = userId;
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.tanggalBatas = tanggalBatas;
        this.kategori = kategori;
        this.lokasi = lokasi;
        this.mataKuliah = mataKuliah;
        this.selesai = selesai;
    }


    public Tugas(int userId, String judul, String deskripsi, LocalDateTime tanggalBatas, String kategori, String lokasi, String mataKuliah) {
        this.userId = userId;
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.tanggalBatas = tanggalBatas;
        this.kategori = kategori;
        this.lokasi = lokasi;
        this.mataKuliah = mataKuliah;
        this.selesai = false;
    }


    // GETTERS
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getJudul() { return judul; }
    public String getDeskripsi() { return deskripsi; }
    public LocalDateTime getTanggalBatas() { return tanggalBatas; }
    public String getKategori() { return kategori; }
    public String getLokasi() { return lokasi; }
    public String getMataKuliah() { return mataKuliah; }
    public boolean isSelesai() { return selesai; }

    // SETTERS
    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setJudul(String judul) { this.judul = judul; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    public void setTanggalBatas(LocalDateTime tanggalBatas) { this.tanggalBatas = tanggalBatas; }
    public void setKategori(String kategori) { this.kategori = kategori; }
    public void setLokasi(String lokasi) { this.lokasi = lokasi; }
    public void setMataKuliah(String mataKuliah) { this.mataKuliah = mataKuliah; }
    public void setSelesai(boolean selesai) { this.selesai = selesai; }

    public String getTanggalBatasFormatted() {
        if (tanggalBatas == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm E, dd MMM yy"); // Format yy untuk tahun 2 digit
        return tanggalBatas.format(formatter);
    }

    public String buatLaporan() {
        return String.format("Tugas: %s\nDeskripsi: %s\nBatas Waktu: %s\nKategori: %s\nStatus: %s",
                judul,
                deskripsi == null ? "-" : deskripsi,
                getTanggalBatasFormatted(),
                kategori == null ? "-" : kategori,
                selesai ? "Selesai" : "Belum Selesai");
    }

    @Override
    public String toString() {
        return "Tugas{" +
                "id=" + id +
                ", userId=" + userId +
                ", judul='" + judul + '\'' +
                ", selesai=" + selesai +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tugas tugas = (Tugas) o;
        return id == tugas.id && userId == tugas.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId);
    }
}