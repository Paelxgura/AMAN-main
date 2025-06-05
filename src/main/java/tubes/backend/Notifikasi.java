package tubes.backend;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Notifikasi {
    private EmailService pengirim;

    public Notifikasi(EmailService emailService) {
        this.pengirim = emailService;
    }

    @Deprecated
    public void jadwalPengingat(Tugas tugas, User user, LocalDateTime waktuPengingat) {
        System.out.println(String.format("DEPRECATED: Panggilan ke Notifikasi.jadwalPengingat. Penjadwalan kini di mainApp. Tugas '%s' milik %s pada %s",
                tugas.getJudul(), user.getUsername(), waktuPengingat.toString()));
    }

    public boolean kirimPengingat(Tugas tugas, User user) {
        if (user == null || user.getEmail() == null || user.getEmail().trim().isEmpty() || tugas == null) {
            System.err.println("User, email user, atau Tugas tidak boleh null untuk mengirim pengingat.");
            return false;
        }
        if (this.pengirim == null) {
            System.err.println("EmailService (pengirim) belum diinisialisasi di Notifikasi.");
            return false;
        }

        String emailTo = user.getEmail();
        String subjek = "Pengingat Tugas: " + tugas.getJudul();
        String tanggalBatasFormatted = (tugas.getTanggalBatas() != null) ?
                                       tugas.getTanggalBatas().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy 'pukul' HH:mm")) :
                                       "Tidak ditentukan";
        String namaUser = user.getUsername() != null ? user.getUsername() : "Pengguna";
        String judulTugas = tugas.getJudul() != null ? tugas.getJudul() : "(Tanpa Judul)";
        String deskripsiTugas = tugas.getDeskripsi() != null && !tugas.getDeskripsi().trim().isEmpty() ? tugas.getDeskripsi() : "-";
        String kategoriTugas = tugas.getKategori() != null ? tugas.getKategori() : "-";
        String lokasiTugas = (tugas.getLokasi() != null && !tugas.getLokasi().isEmpty() && !tugas.getLokasi().equalsIgnoreCase("Belum Diisi")) ? tugas.getLokasi() : null;

        String urlLogo = "https://placehold.co/100x40/012F10/C1D6C8?text=AMAN";


        String isiHtml = String.format("""
        <!DOCTYPE html>
        <html lang="id">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>%s</title>
            <style>
                body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; color: #333; }
                .container { max-width: 600px; margin: 20px auto; background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }
                .header { text-align: center; margin-bottom: 20px; }
                .header img { max-width: 150px; }
                .content h1 { color: #012F10; font-size: 24px; text-align: center; }
                .content p { line-height: 1.6; margin-bottom: 15px; }
                .task-details { background-color: #f9f9f9; padding: 15px; border-radius: 5px; margin-bottom: 20px; }
                .task-details strong { color: #012F10; }
                .button-container { text-align: center; margin-top: 25px; }
                .button { background-color: #012F10; color: #C1D6C8; padding: 12px 25px; text-decoration: none; border-radius: 25px; font-weight: bold; display: inline-block; }
                .footer { text-align: center; margin-top: 30px; font-size: 12px; color: #777; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                </div>
                <div class="content">
                    <h1>Pengingat Tugas Anda!</h1>
                    <p>Halo <strong>%s</strong>,</p>
                    <p>Ini adalah pengingat untuk tugas Anda yang akan segera jatuh tempo atau sudah lewat:</p>
                    <div class="task-details">
                        <p><strong>Judul:</strong> %s</p>
                        <p><strong>Waktu:</strong> %s</p>
                        <p><strong>Kategori:</strong> %s</p>
                        %s
                        <p><strong>Deskripsi:</strong></p>
                        <p>%s</p>
                    </div>
                    <p>Mohon untuk segera menyelesaikan tugas Anda.</p>
                    <!-- Anda bisa menambahkan tombol jika ingin mengarahkan ke aplikasi,
                         tapi untuk email notifikasi sederhana, ini mungkin tidak perlu.
                    <div class="button-container">
                        <a href="URL_APLIKASI_ANDA_JIKA_ADA" class="button">Lihat Tugas di Aplikasi</a>
                    </div>
                    -->
                </div>
                <div class="footer">
                    <p></p>
                </div>
            </div>
        </body>
        </html>
        """,
        subjek, 
        namaUser,
        judulTugas,
        tanggalBatasFormatted,
        kategoriTugas,
        (lokasiTugas != null ? String.format("<p><strong>Lokasi:</strong> %s</p>", lokasiTugas) : ""), 
        deskripsiTugas,
        LocalDateTime.now().getYear() 
        );

        System.out.println("Notifikasi: Mempersiapkan pengingat HTML untuk tugas: " + judulTugas + " kepada user: " + namaUser + " (" + emailTo + ")");
        return pengirim.kirimEmail(emailTo, subjek, isiHtml);
    }
}
