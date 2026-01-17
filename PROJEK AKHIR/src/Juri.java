public class Juri {
    private String namaJuri;
    private String tipeFokus;
    private int pengalamanTahun;

    public Juri(String nama, String fokus) {
        this.namaJuri = nama;
        this.tipeFokus = fokus;
        this.pengalamanTahun = (int)(Math.random() * 20) + 5; // 5-25 tahun
    }

    public double berikanPenilaianAkhir(EntityPeserta p, double skorDebat) {
        double skorFinal = skorDebat;

        // Bonus berdasarkan kesesuaian tipe dengan fokus juri
        if (this.tipeFokus.equalsIgnoreCase("Logika") && p instanceof DebaterLogis) {
            skorFinal += 15;
            System.out.println("✅ Juri " + namaJuri + " sangat terkesan dengan argumentasi logis " + p.getNama());
        } else if (this.tipeFokus.equalsIgnoreCase("Retorika") && p instanceof DebaterAgresif) {
            skorFinal += 15;
            System.out.println("✅ Juri " + namaJuri + " terpukau dengan kemampuan retorika " + p.getNama());
        } else if (this.tipeFokus.equalsIgnoreCase("Seimbang")) {
            // Juri seimbang memberi bonus kecil untuk semua tipe
            skorFinal += 8;
            System.out.println("⚖️ Juri " + namaJuri + " menghargai performa seimbang " + p.getNama());
        }

        // Bonus konsistensi: mental masih di atas 70%
        if (p.getMentalHealth() >= 0.7) {
            skorFinal += 10;
            System.out.println("💪 Bonus konsistensi: " + p.getNama() + " tetap tenang sepanjang debat");
        }

        // Penalti jika mental sangat buruk (di bawah 20%)
        if (p.getMentalHealth() < 0.2) {
            skorFinal -= 20;
            System.out.println("⚠️ Penalti berat: " + p.getNama() + " kehilangan kontrol emosi di panggung");
        } else if (p.getMentalHealth() < 0.4) {
            skorFinal -= 10;
            System.out.println("⚠️ Penalti: " + p.getNama() + " terlihat terguncang");
        }

        // Bonus pengalaman juri (semakin berpengalaman, semakin teliti)
        if (pengalamanTahun > 15) {
            // Juri senior lebih ketat
            skorFinal *= 0.95;
        } else if (pengalamanTahun < 10) {
            // Juri junior lebih longgar
            skorFinal *= 1.02;
        }

        return Math.max(0, skorFinal); // Pastikan tidak negatif
    }

    public String berikanKomentar(EntityPeserta p1, EntityPeserta p2, double skor1, double skor2) {
        StringBuilder komentar = new StringBuilder();
        komentar.append("\n👨‍⚖️ KOMENTAR JURI " + namaJuri.toUpperCase() + ":\n");
        komentar.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        if (skor1 > skor2) {
            komentar.append("Saya memberikan kemenangan kepada " + p1.getNama() + ".\n");
            komentar.append("Argumentasi yang konsisten dan meyakinkan.\n");
        } else if (skor2 > skor1) {
            komentar.append("Saya memberikan kemenangan kepada " + p2.getNama() + ".\n");
            komentar.append("Kemampuan rebuttal yang luar biasa.\n");
        } else {
            komentar.append("Kedua tim menunjukkan performa setara.\n");
            komentar.append("Debat yang sangat berimbang.\n");
        }

        // Analisis berdasarkan fokus juri
        if (tipeFokus.equalsIgnoreCase("Logika")) {
            komentar.append("\nSebagai juri yang fokus pada logika, saya menilai:\n");
            komentar.append("- Kekuatan argumentasi berbasis data dan fakta\n");
            komentar.append("- Konsistensi dalam penalaran\n");
        } else if (tipeFokus.equalsIgnoreCase("Retorika")) {
            komentar.append("\nSebagai juri yang fokus pada retorika, saya menilai:\n");
            komentar.append("- Kemampuan persuasi dan penyampaian\n");
            komentar.append("- Dampak emosional terhadap audiens\n");
        } else {
            komentar.append("\nDengan pendekatan seimbang, saya menilai:\n");
            komentar.append("- Kombinasi logika dan kemampuan komunikasi\n");
            komentar.append("- Keseimbangan antara fakta dan persuasi\n");
        }

        komentar.append("\nPengalaman saya " + pengalamanTahun + " tahun sebagai juri\n");
        komentar.append("memastikan penilaian yang objektif dan adil.\n");

        return komentar.toString();
    }

    // Getter
    public String getNamaJuri() { return namaJuri; }
    public String getTipeFokus() { return tipeFokus; }
    public int getPengalamanTahun() { return pengalamanTahun; }
}