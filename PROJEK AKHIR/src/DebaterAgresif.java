public class DebaterAgresif extends EntityPeserta implements AksiDebat {
    private int jumlahInterupsi = 0;
    private boolean modeBerapi = false;

    public DebaterAgresif(String nama) {
        super(nama, 85, 60); // Agresif: Retorika tinggi, Logika sedang
    }

    @Override
    public double sampaikanArgumen() {
        double modifier = hitungModifier();
        double baseScore = (getBaseLogika() * 0.4) + (getBaseRetorika() * 0.6);

        // Mode Berapi: bonus damage saat mental masih tinggi
        if (getMentalHealth() > 0.7 && !modeBerapi) {
            modeBerapi = true;
            baseScore *= 1.15;
            System.out.println("🔥 " + getNama() + " masuk MODE BERAPI-API!");
        } else if (getMentalHealth() < 0.5) {
            modeBerapi = false;
        }

        double finalScore = baseScore * modifier;

        String[] gaya = {
                "menyerang dengan retorika berapi-api!",
                "menggunakan bahasa yang sangat persuasif!",
                "menekan lawan dengan argumen agresif!",
                "membombardir dengan serangan verbal!"
        };

        int idx = (int)(Math.random() * gaya.length);
        System.out.println("⚔️ " + getNama() + " " + gaya[idx]);

        // Konsumsi stamina lebih besar karena agresif
        setStamina(getStamina() - 8);

        return finalScore;
    }

    @Override
    public void terimaSerangan(double impact) {
        // Debater agresif lebih rentan terhadap serangan (125% damage)
        super.terimaSerangan(impact * 1.25);

        if (getMentalHealth() < 0.4) {
            System.out.println("😤 " + getNama() + " mulai emosional!");
        }
    }

    @Override
    public void lakukanInterupsi() {
        jumlahInterupsi++;
        String[] interupsi = {
                "memotong pembicaraan lawan dengan keras!",
                "langsung menyela: 'TUNGGU DULU!'",
                "tidak memberi kesempatan lawan bicara!",
                "mengambil alih momentum dengan interupsi!"
        };

        int idx = (int)(Math.random() * interupsi.length);
        System.out.println("💥 " + getNama() + " " + interupsi[idx]);

        // Interupsi terlalu banyak bisa jadi boomerang
        if (jumlahInterupsi > 3) {
            System.out.println("⚠️ Juri mencatat: " + getNama() + " terlalu sering interupsi!");
        }
    }

    @Override
    public void berikanRebuttal(String poin) {
        String[] rebuttal = {
                "membantah keras poin: " + poin,
                "langsung menyerang balik: '" + poin + " adalah keliru!'",
                "dengan agresif menolak argumen: " + poin,
                "menghancurkan klaim tentang: " + poin
        };

        int idx = (int)(Math.random() * rebuttal.length);
        System.out.println("🎯 " + getNama() + " " + rebuttal[idx]);
    }

    /**
     * Special ability: Provokasi untuk menurunkan mental lawan
     */
    public void lakukanProvokasi(EntityPeserta lawan) {
        System.out.println("😈 " + getNama() + " melakukan provokasi terhadap " + lawan.getNama() + "!");
        lawan.terimaSerangan(0.15);

        // Tapi ada risiko: juri tidak suka provokasi
        if (Math.random() < 0.3) {
            System.out.println("⚖️ Juri menilai provokasi tidak sportif!");
        }
    }

    // Getter untuk tracking
    public int getJumlahInterupsi() { return jumlahInterupsi; }
    public boolean isModeBerapi() { return modeBerapi; }
}