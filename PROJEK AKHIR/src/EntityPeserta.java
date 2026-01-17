public abstract class EntityPeserta {
    private String nama;
    private int baseRetorika;
    private int baseLogika;
    protected double mentalHealth;
    protected int stamina;
    protected int pengalaman;

    public EntityPeserta(String nama, int retorika, int logika) {
        this.nama = nama;
        this.baseRetorika = retorika;
        this.baseLogika = logika;
        this.mentalHealth = 1.0; // 100% mental
        this.stamina = 100;
        this.pengalaman = (int)(Math.random() * 10) + 1; // 1-10 tingkat pengalaman
    }

    // Polimorfisme: tiap tipe debater punya cara argumen berbeda
    public abstract double sampaikanArgumen();

    /**
     * Menerima serangan mental dari lawan
     * @param impact Besaran damage (0.0 - 1.0)
     */
    public void terimaSerangan(double impact) {
        // Faktor stamina mempengaruhi daya tahan
        double actualImpact = impact * (1.0 - (stamina / 200.0));

        this.mentalHealth -= actualImpact;
        this.stamina -= (int)(impact * 10);

        if (this.mentalHealth < 0) this.mentalHealth = 0;
        if (this.stamina < 0) this.stamina = 0;

        // Notifikasi status
        if (this.mentalHealth < 0.3) {
            System.out.println("⚠️ " + nama + " mulai terguncang! Mental: " +
                    String.format("%.1f%%", mentalHealth * 100));
        }
    }

    /**
     * Pulihkan stamina sedikit setelah ronde
     */
    public void pulihkanStamina() {
        this.stamina = Math.min(100, stamina + 15);
        System.out.println("💚 " + nama + " memulihkan stamina. Stamina: " + stamina);
    }

    /**
     * Hitung modifier berdasarkan kondisi debater
     */
    protected double hitungModifier() {
        double modifier = 1.0;

        // Bonus pengalaman
        modifier += (pengalaman * 0.02);

        // Penalti jika mental rendah
        if (mentalHealth < 0.3) {
            modifier -= 0.3;
        } else if (mentalHealth < 0.5) {
            modifier -= 0.15;
        }

        // Penalti jika stamina rendah
        if (stamina < 30) {
            modifier -= 0.2;
        } else if (stamina < 50) {
            modifier -= 0.1;
        }

        return Math.max(0.5, modifier); // Minimum 50% efektivitas
    }

    /**
     * Cek apakah masih bisa melanjutkan debat
     */
    public boolean bisaMelanjutkan() {
        return mentalHealth > 0.1 && stamina > 10;
    }

    /**
     * Dapatkan status lengkap debater
     */
    public String getStatusLengkap() {
        return String.format(
                "%s | Mental: %.1f%% | Stamina: %d | Pengalaman: Lv.%d",
                nama, mentalHealth * 100, stamina, pengalaman
        );
    }

    // Getter & Setter (Encapsulation)
    public String getNama() { return nama; }
    public int getBaseRetorika() { return baseRetorika; }
    public int getBaseLogika() { return baseLogika; }
    public double getMentalHealth() { return mentalHealth; }
    public int getStamina() { return stamina; }
    public int getPengalaman() { return pengalaman; }

    public void setMentalHealth(double mental) {
        this.mentalHealth = Math.max(0, Math.min(1, mental));
    }
    public void setStamina(int stam) {
        this.stamina = Math.max(0, Math.min(100, stam));
    }
}