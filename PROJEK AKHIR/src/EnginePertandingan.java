import java.util.ArrayList;
import java.util.List;

public class EnginePertandingan {
    private List<EntityPeserta> timPro = new ArrayList<>();
    private List<EntityPeserta> timKontra = new ArrayList<>();

    public void tambahPesertaPro(EntityPeserta p) { timPro.add(p); }
    public void tambahPesertaKontra(EntityPeserta p) { timKontra.add(p); }

    public void mulaiDebat() {
        System.out.println("=== PERTANDINGAN DIMULAI ===");
        for (int i = 0; i < timPro.size(); i++) {
            double skorPro = timPro.get(i).sampaikanArgumen();
            double skorKontra = timKontra.get(i).sampaikanArgumen();

            System.out.println("Skor Ronde " + (i+1) + ": " + skorPro + " vs " + skorKontra);
        }
    }
}