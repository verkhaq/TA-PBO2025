import java.util.Scanner;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("========================================");
        System.out.println("   WELCOME TO AUTODEBATE SIMULATOR MK   ");
        System.out.println("========================================\n");

        // --- Bagian Input (Sama seperti sebelumnya) ---
        System.out.print("Masukkan Nama Debater Tim PRO: ");
        String namaPro = input.nextLine();
        System.out.println("Pilih Tipe (1: Agresif, 2: Logis): ");
        int tipePro = input.nextInt();
        input.nextLine();

        EntityPeserta pPro = (tipePro == 1) ? new DebaterAgresif(namaPro) : new DebaterLogis(namaPro);

        System.out.print("\nMasukkan Nama Debater Tim KONTRA: ");
        String namaKontra = input.nextLine();
        System.out.println("Pilih Tipe (1: Agresif, 2: Logis): ");
        int tipeKontra = input.nextInt();
        input.nextLine();

        EntityPeserta pKontra = (tipeKontra == 1) ? new DebaterAgresif(namaKontra) : new DebaterLogis(namaKontra);

        System.out.print("\nMasukkan Nama Juri: ");
        String namaJuri = input.nextLine();
        System.out.print("Fokus Penilaian Juri (Logika/Retorika): ");
        String fokusJuri = input.nextLine();

        Juri juriMatch = new Juri(namaJuri, fokusJuri);

        // --- Simulasi Jalannya Debat ---
        System.out.println("\n--- PERTANDINGAN DIMULAI ---");
        double skorP1 = pPro.sampaikanArgumen();
        pKontra.terimaSerangan(0.2);

        double skorP2 = pKontra.sampaikanArgumen();
        pPro.terimaSerangan(0.15);

        double finalPro = juriMatch.berikanPenilaianAkhir(pPro, skorP1);
        double finalKontra = juriMatch.berikanPenilaianAkhir(pKontra, skorP2);

        // --- Logika Penentuan Pemenang ---
        String hasilStatus;
        if (finalPro > finalKontra) hasilStatus = "PEMENANG: TIM PRO (" + pPro.getNama() + ")";
        else if (finalKontra > finalPro) hasilStatus = "PEMENANG: TIM KONTRA (" + pKontra.getNama() + ")";
        else hasilStatus = "HASIL: SERI";

        System.out.println("\n" + hasilStatus);

        // --- FITUR BARU: SAVE TO FILE ---
        simpanLaporan(pPro, pKontra, juriMatch, finalPro, finalKontra, hasilStatus);

        input.close();
    }

    // Method Statis untuk Menangani Penyimpanan File
    public static void simpanLaporan(EntityPeserta p1, EntityPeserta p2, Juri j, double s1, double s2, String status) {
        String fileName = "Log_Pertandingan.txt";

        // Mendapatkan waktu saat ini
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write("========== LAPORAN DEBAT PBO ==========");
            writer.newLine();
            writer.write("Waktu Pertandingan: " + dtf.format(now));
            writer.newLine();
            writer.write("Tim PRO    : " + p1.getNama() + " | Skor Akhir: " + String.format("%.2f", s1));
            writer.newLine();
            writer.write("Tim KONTRA : " + p2.getNama() + " | Skor Akhir: " + String.format("%.2f", s2));
            writer.newLine();
            writer.write("Keputusan Juri: " + status);
            writer.newLine();
            writer.write("=======================================");
            writer.newLine();
            writer.newLine(); // Spasi untuk pertandingan berikutnya

            System.out.println("\n[Sistem] Laporan pertandingan telah disimpan di " + fileName);
        } catch (IOException e) {
            System.out.println("[Error] Gagal menyimpan laporan: " + e.getMessage());
        }
    }
}