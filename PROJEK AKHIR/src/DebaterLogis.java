public class DebaterLogis extends EntityPeserta implements AksiDebat {
    private int jumlahDataDikutip = 0;
    private boolean modeAnalitis = false;

    public DebaterLogis(String nama) {
        // Logis: Logika sangat tinggi (90), Retorika standar (60)
        super(nama, 60, 90);
    }

    @Override
    public double sampaikanArgumen() {
        double modifier = hitungModifier();
        double baseScore = (getBaseLogika() * 0.8) + (getBaseRetorika() * 0.2);

        // Kutip data untuk bonus
        if (Math.random() < 0.6) {
            kutipData();
            baseScore *= 1.1;
        }

        // Mode Analitis: bonus saat mental stabil
        if (getMentalHealth() > 0.6 && !modeAnalitis) {
            modeAnalitis = true;
            baseScore *= 1.12;
            System.out.println("🧠 " + getNama() + " masuk MODE ANALITIS MENDALAM!");
        } else if (getMentalHealth() < 0.4) {
            modeAnalitis = false;
        }

        double finalScore = baseScore * modifier;

        String[] gaya = {
                "menyusun argumen berdasarkan data dan fakta.",
                "menggunakan logika deduktif yang kuat.",
                "membangun argumentasi sistematis.",
                "menyajikan analisis mendalam dengan bukti konkret."
        };

        int idx = (int)(Math.random() * gaya.length);
        System.out.println("📊 " + getNama() + " " + gaya[idx]);

        // Konsumsi stamina lebih rendah karena tenang
        setStamina(getStamina() - 5);

        return finalScore;
    }

    @Override
    public void terimaSerangan(double impact) {
        // Fitur Kompleks: Debater Logis hanya menerima 50% damage mental karena tenang
        double reducedImpact = impact * 0.5;
        super.terimaSerangan(reducedImpact);

        if (getMentalHealth() > 0.5) {
            System.out.println("🛡️ " + getNama() + " tetap tenang dan rasional. Mental: " +
                    String.format("%.1f%%", getMentalHealth() * 100));
        } else {
            System.out.println("😰 " + getNama() + " mulai kesulitan mempertahankan logika.");
        }
    }

    @Override
    public void lakukanInterupsi() {
        String[] interupsi = {
                "mengoreksi data lawan dengan sopan: 'Maaf, data Anda tidak valid.'",
                "menunjukkan inkonsistensi: 'Pernyataan ini kontradiktif dengan sebelumnya.'",
                "meminta klarifikasi: 'Bisakah dijelaskan basis data tersebut?'",
                "mengidentifikasi logical fallacy: 'Ini adalah ad hominem.'"
        };

        int idx = (int)(Math.random() * interupsi.length);
        System.out.println("✋ " + getNama() + " " + interupsi[idx]);

        // Interupsi logis dihargai juri
        if (Math.random() < 0.4) {
            System.out.println("👏 Juri mengangguk: interupsi yang tepat!");
        }
    }

    @Override
    public void berikanRebuttal(String poin) {
        String[] rebuttal = {
                "membedah sesat pikir dari poin: " + poin,
                "menemukan kelemahan logis dalam: " + poin,
                "menyediakan counter-evidence untuk: " + poin,
                "menganalisis secara kritis argumen tentang: " + poin
        };

        int idx = (int)(Math.random() * rebuttal.length);
        System.out.println("🔍 " + getNama() + " " + rebuttal[idx]);
    }

    /**
     * Kutip data untuk memperkuat argumen
     */
    private void kutipData() {
        jumlahDataDikutip++;
        String[] sumber = {
                "jurnal peer-reviewed",
                "laporan riset internasional",
                "data statistik terkini",
                "studi empiris",
                "publikasi akademis"
        };

        int idx = (int)(Math.random() * sumber.length);
        System.out.println("📚 " + getNama() + " mengutip dari " + sumber[idx]);
    }

    /**
     * Special ability: Analisis mendalam untuk bonus skor
     */
    public double lakukanAnalisisMendalam() {
        if (getMentalHealth() > 0.5 && getStamina() > 30) {
            System.out.println("🎓 " + getNama() + " melakukan analisis mendalam komprehensif!");
            setStamina(getStamina() - 20); // Butuh fokus tinggi
            return getBaseLogika() * 0.3; // Bonus skor
        } else {
            System.out.println("😓 " + getNama() + " terlalu lelah untuk analisis mendalam.");
            return 0;
        }
    }

    /**
     * Deteksi logical fallacy dari argumen lawan
     */
    public void deteksiFallacy(String argumenLawan) {
        String[] fallacies = {
                "Ad Hominem - menyerang pribadi, bukan argumen",
                "Straw Man - mendistorsi argumen lawan",
                "False Dilemma - menyajikan pilihan hitam-putih palsu",
                "Slippery Slope - asumsi efek domino tanpa bukti",
                "Appeal to Authority - menggunakan otoritas tanpa relevansi"
        };

        if (Math.random() < 0.5) {
            int idx = (int)(Math.random() * fallacies.length);
            System.out.println("🚩 " + getNama() + " mendeteksi: " + fallacies[idx]);
            System.out.println("   dalam argumen: \"" + argumenLawan + "\"");
        }
    }

    // Getter untuk tracking
    public int getJumlahDataDikutip() { return jumlahDataDikutip; }
    public boolean isModeAnalitis() { return modeAnalitis; }
}