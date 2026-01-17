import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AutoDebateGUI extends JFrame {
    /* ===================== GLOBAL STYLE - IMPROVED CONTRAST ===================== */
    private final Color bgDark   = new Color(15, 15, 30);
    private final Color panelBg = new Color(35, 35, 60);
    private final Color textBright = new Color(245, 245, 255);
    private final Color textMedium = new Color(220, 220, 235);

    // Komponen UI
    private JTextField txtNamaPro, txtNamaKontra, txtNamaJuri, txtTopik;
    private JComboBox<String> cbTipePro, cbTipeKontra, cbFokusJuri;
    private JTextArea areaHasil, areaStatistik;
    private JButton btnMulai, btnPause, btnReset, btnHistory;
    private JProgressBar barMentalPro, barMentalKontra;
    private JLabel lblStatusPro, lblStatusKontra, lblRonde;

    // Arena 3D
    private DebateArena3D arena3D;

    // Data Pertandingan
    private EntityPeserta pPro, pKontra;
    private Juri juriMatch;
    private Timer timerDebat;
    private int rondeSekarang = 0;
    private int maxRonde = 5;
    private int waktuPerRonde = 60;
    private double totalSkorPro = 0, totalSkorKontra = 0;
    private List<String> logHistory = new ArrayList<>();
    private boolean isPaused = false;
    private Random random = new Random();

    public AutoDebateGUI() {
        initUI();
        setVisible(true);
    }

    private void initUI() {
        setTitle("⚔️ AutoDebate Simulator 3D - Interactive Arena Edition");
        setSize(1400, 850);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(bgDark);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top: 3D Arena
        arena3D = new DebateArena3D();
        arena3D.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 220, 255), 2),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        mainPanel.add(arena3D, BorderLayout.CENTER);

        // Bottom: Control Panel
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setOpaque(false);

        JPanel configPanel = createConfigPanel();
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setOpaque(false);
        splitPane.setLeftComponent(createLogPanel());
        splitPane.setRightComponent(createStatsPanel());

        JPanel statusPanel = createStatusPanel();

        bottomPanel.add(configPanel, BorderLayout.WEST);
        bottomPanel.add(splitPane, BorderLayout.CENTER);
        bottomPanel.add(statusPanel, BorderLayout.EAST);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        JPanel controlPanel = createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.NORTH);

        add(mainPanel);
    }

    private JPanel createConfigPanel() {
        JPanel panel = new JPanel(new GridLayout(9, 2, 5, 5));
        panel.setPreferredSize(new Dimension(300, 0));
        panel.setBackground(new Color(25, 25, 45));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(160, 100, 255), 2),
                "🎮 KONFIGURASI",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 13),
                new Color(220, 180, 255)
        ));

        Font labelFont = new Font("Arial", Font.BOLD, 11);
        Color labelColor = new Color(230, 230, 250);

        addConfigRow(panel, "👤 Tim PRO:", txtNamaPro = new JTextField("Alpha Team"), labelFont, labelColor);
        addConfigRow(panel, "⚔️ Tipe PRO:", cbTipePro = new JComboBox<>(new String[]{"Agresif", "Logis"}), labelFont, labelColor);
        addConfigRow(panel, "👤 Tim KONTRA:", txtNamaKontra = new JTextField("Beta Team"), labelFont, labelColor);
        addConfigRow(panel, "🛡️ Tipe KONTRA:", cbTipeKontra = new JComboBox<>(new String[]{"Agresif", "Logis"}), labelFont, labelColor);
        cbTipeKontra.setSelectedIndex(1);
        addConfigRow(panel, "👨‍⚖️ Juri:", txtNamaJuri = new JTextField("Prof. Hakim"), labelFont, labelColor);
        addConfigRow(panel, "⚖️ Fokus:", cbFokusJuri = new JComboBox<>(new String[]{"Logika", "Retorika", "Seimbang"}), labelFont, labelColor);

        JLabel lblRonde = createStyledLabel("🔄 Ronde:", labelFont, labelColor);
        JSpinner spinnerRonde = new JSpinner(new SpinnerNumberModel(5, 3, 10, 1));
        spinnerRonde.addChangeListener(e -> maxRonde = (int)((JSpinner)e.getSource()).getValue());
        styleSpinner(spinnerRonde);
        panel.add(lblRonde);
        panel.add(spinnerRonde);

        addConfigRow(panel, "📝 Topik:", txtTopik = new JTextField("Teknologi vs Humanitas"), labelFont, labelColor);

        JLabel lblWaktu = createStyledLabel("⏱️ Waktu/Ronde:", labelFont, labelColor);
        JSpinner spinnerWaktu = new JSpinner(new SpinnerNumberModel(60, 30, 180, 10));
        spinnerWaktu.addChangeListener(e -> waktuPerRonde = (int)((JSpinner)e.getSource()).getValue());
        styleSpinner(spinnerWaktu);
        panel.add(lblWaktu);
        panel.add(spinnerWaktu);

        return panel;
    }

    private void addConfigRow(JPanel panel, String labelText, JComponent component, Font font, Color color) {
        JLabel label = createStyledLabel(labelText, font, color);
        panel.add(label);
        if (component instanceof JTextField) {
            styleTextField((JTextField) component);
        } else if (component instanceof JComboBox) {
            styleComboBox((JComboBox<String>) component);
        }
        panel.add(component);
    }

    private JLabel createStyledLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(25, 25, 45));
        panel.setPreferredSize(new Dimension(400, 200));

        areaHasil = new JTextArea();
        areaHasil.setEditable(false);
        areaHasil.setFont(new Font("Consolas", Font.PLAIN, 11));
        areaHasil.setBackground(new Color(20, 20, 35));
        areaHasil.setForeground(new Color(220, 255, 220));
        areaHasil.setLineWrap(false);

        JScrollPane scroll = new JScrollPane(areaHasil);
        scroll.setBackground(new Color(25, 25, 45));
        scroll.getViewport().setBackground(new Color(20, 20, 35));
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(80, 220, 255), 2),
                "📜 LOG PERTANDINGAN",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                new Color(150, 240, 255)
        ));

        panel.add(scroll);
        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(25, 25, 45));

        areaStatistik = new JTextArea();
        areaStatistik.setEditable(false);
        areaStatistik.setFont(new Font("Consolas", Font.BOLD, 11));
        areaStatistik.setBackground(new Color(20, 20, 35));
        areaStatistik.setForeground(new Color(255, 230, 255));
        areaStatistik.setLineWrap(false);

        JScrollPane scroll = new JScrollPane(areaStatistik);
        scroll.setBackground(new Color(25, 25, 45));
        scroll.getViewport().setBackground(new Color(20, 20, 35));
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(255, 100, 180), 2),
                "📊 STATISTIK",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                new Color(255, 180, 220)
        ));

        panel.add(scroll);
        updateStatistik();
        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(25, 25, 45));
        panel.setPreferredSize(new Dimension(250, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(0, 10, 0, 0),
                BorderFactory.createLineBorder(new Color(100, 100, 150), 1)
        ));

        // Ronde label
        lblRonde = new JLabel("Ronde: 0 / " + maxRonde, SwingConstants.CENTER);
        lblRonde.setFont(new Font("Arial", Font.BOLD, 18));
        lblRonde.setForeground(new Color(255, 255, 255));
        lblRonde.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblRonde.setBorder(new EmptyBorder(10, 5, 10, 5));
        panel.add(lblRonde);
        panel.add(Box.createVerticalStrut(10));

        // Pro status
        lblStatusPro = new JLabel("💜 PRO - Mental: 100%");
        lblStatusPro.setForeground(new Color(220, 180, 255));
        lblStatusPro.setFont(new Font("Arial", Font.BOLD, 12));
        lblStatusPro.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblStatusPro);

        barMentalPro = new JProgressBar(0, 100);
        barMentalPro.setValue(100);
        barMentalPro.setStringPainted(true);
        barMentalPro.setFont(new Font("Arial", Font.BOLD, 11));
        barMentalPro.setForeground(new Color(160, 80, 240));
        barMentalPro.setBackground(new Color(40, 40, 65));
        barMentalPro.setMaximumSize(new Dimension(240, 28));
        barMentalPro.setBorder(BorderFactory.createLineBorder(new Color(120, 60, 200), 1));
        panel.add(barMentalPro);
        panel.add(Box.createVerticalStrut(20));

        // Kontra status
        lblStatusKontra = new JLabel("🧡 KONTRA - Mental: 100%");
        lblStatusKontra.setForeground(new Color(255, 180, 120));
        lblStatusKontra.setFont(new Font("Arial", Font.BOLD, 12));
        lblStatusKontra.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblStatusKontra);

        barMentalKontra = new JProgressBar(0, 100);
        barMentalKontra.setValue(100);
        barMentalKontra.setStringPainted(true);
        barMentalKontra.setFont(new Font("Arial", Font.BOLD, 11));
        barMentalKontra.setForeground(new Color(255, 160, 50));
        barMentalKontra.setBackground(new Color(40, 40, 65));
        barMentalKontra.setMaximumSize(new Dimension(240, 28));
        barMentalKontra.setBorder(BorderFactory.createLineBorder(new Color(220, 120, 30), 1));
        panel.add(barMentalKontra);

        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        panel.setOpaque(false);

        btnMulai = createStyledButton("🎮 MULAI DEBAT", new Color(160, 80, 240));
        btnPause = createStyledButton("⏸ PAUSE", new Color(255, 160, 50));
        btnPause.setEnabled(false);
        btnReset = createStyledButton("🔄 RESET", new Color(80, 150, 220));
        btnHistory = createStyledButton("📜 HISTORY", new Color(255, 80, 180));

        btnMulai.addActionListener(e -> mulaiDebat());
        btnPause.addActionListener(e -> pauseDebat());
        btnReset.addActionListener(e -> resetDebat());
        btnHistory.addActionListener(e -> tampilkanHistory());

        panel.add(btnMulai);
        panel.add(btnPause);
        panel.add(btnReset);
        panel.add(btnHistory);

        return panel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBackground(color);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(color.brighter(), 2),
                new EmptyBorder(10, 18, 10, 18)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(color.brighter());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(color);
            }
        });

        return btn;
    }

    private void styleTextField(JTextField field) {
        field.setBackground(new Color(45, 45, 75));
        field.setForeground(new Color(245, 245, 255));
        field.setCaretColor(new Color(120, 220, 255));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(100, 110, 140), 1),
                new EmptyBorder(4, 10, 4, 10)
        ));
        field.setFont(new Font("Arial", Font.PLAIN, 8));
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setBackground(new Color(45, 45, 75));
        combo.setForeground(new Color(45, 45, 75));
        combo.setFont(new Font("Arial", Font.PLAIN, 8));
    }

    private void styleSpinner(JSpinner spinner) {
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField textField = ((JSpinner.DefaultEditor) editor).getTextField();
            textField.setBackground(new Color(45, 45, 75));
            textField.setForeground(new Color(245, 245, 255));
            textField.setFont(new Font("Arial", Font.PLAIN, 8));
        }
        spinner.setBackground(new Color(45, 45, 75));
    }

    private void mulaiDebat() {
        if (!validateInput()) return;

        initDebat();
        btnMulai.setEnabled(false);
        btnPause.setEnabled(true);
        btnHistory.setEnabled(false);

        arena3D.setNamaPro(pPro.getNama());
        arena3D.setNamaKontra(pKontra.getNama());
        arena3D.setTopikDebat(txtTopik.getText());
        arena3D.setDebating(true);

        areaHasil.setText("");
        tambahLog("╔════════════════════════════════════════╗");
        tambahLog("║   ⚡ PERTANDINGAN DIMULAI! ⚡          ║");
        tambahLog("╚════════════════════════════════════════╝\n");
        tambahLog("📝 Topik: " + txtTopik.getText());
        tambahLog("⚔️ " + pPro.getNama() + " VS " + pKontra.getNama());
        tambahLog("👨‍⚖️ Juri: " + txtNamaJuri.getText() + "\n");

        jalankanDebat();
    }

    private boolean validateInput() {
        if (txtNamaPro.getText().trim().isEmpty() ||
                txtNamaKontra.getText().trim().isEmpty() ||
                txtNamaJuri.getText().trim().isEmpty() ||
                txtTopik.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "⚠️ Semua field harus diisi!",
                    "Validasi Input",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void initDebat() {
        pPro = cbTipePro.getSelectedIndex() == 0 ?
                new DebaterAgresif(txtNamaPro.getText().trim()) :
                new DebaterLogis(txtNamaPro.getText().trim());
        pKontra = cbTipeKontra.getSelectedIndex() == 0 ?
                new DebaterAgresif(txtNamaKontra.getText().trim()) :
                new DebaterLogis(txtNamaKontra.getText().trim());

        juriMatch = new Juri(txtNamaJuri.getText().trim(), (String) cbFokusJuri.getSelectedItem());

        rondeSekarang = 0;
        totalSkorPro = 0;
        totalSkorKontra = 0;
        isPaused = false;
    }

    private void jalankanDebat() {
        timerDebat = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isPaused) {
                    prosesRonde();
                }
            }
        });
        timerDebat.start();
    }

    private void prosesRonde() {
        rondeSekarang++;
        lblRonde.setText("Ronde: " + rondeSekarang + " / " + maxRonde);
        arena3D.setWaktuSisa(waktuPerRonde);

        if (rondeSekarang <= maxRonde) {
            tambahLog("\n━━━━━━━━━━ 🔥 RONDE " + rondeSekarang + " 🔥 ━━━━━━━━━━");

            arena3D.setProSpeaking(true);
            arena3D.setKontraSpeaking(false);

            Timer proTimer = new Timer(1500, ev -> {
                ((Timer)ev.getSource()).stop();

                double skorPro = pPro.sampaikanArgumen();
                double damage = random.nextDouble() * 0.25 + 0.1;
                pKontra.terimaSerangan(damage);

                String aksiPro = generateAksiBonus(pPro);
                tambahLog("⚔️ " + pPro.getNama() + " menyampaikan argumen! " + aksiPro);
                tambahLog("   📈 Skor: " + String.format("%.2f", skorPro));

                totalSkorPro += skorPro;
                updateMentalBar();
                arena3D.setSkorPro((int)totalSkorPro);

                arena3D.setProSpeaking(false);

                Timer kontraTimer = new Timer(1500, ev2 -> {
                    ((Timer)ev2.getSource()).stop();

                    arena3D.setKontraSpeaking(true);

                    Timer kontraSpeakTimer = new Timer(1500, ev3 -> {
                        ((Timer)ev3.getSource()).stop();

                        double skorKontra = pKontra.sampaikanArgumen();
                        double damageKontra = random.nextDouble() * 0.25 + 0.1;
                        pPro.terimaSerangan(damageKontra);

                        String aksiKontra = generateAksiBonus(pKontra);
                        tambahLog("🛡️ " + pKontra.getNama() + " memberikan bantahan! " + aksiKontra);
                        tambahLog("   📈 Skor: " + String.format("%.2f", skorKontra));

                        totalSkorKontra += skorKontra;
                        updateMentalBar();
                        arena3D.setSkorKontra((int)totalSkorKontra);
                        arena3D.setKontraSpeaking(false);

                        updateStatistik();

                        if (pPro.getMentalHealth() < 0.1 || pKontra.getMentalHealth() < 0.1) {
                            selesaiDebat();
                        } else if (rondeSekarang >= maxRonde) {
                            Timer delayFinal = new Timer(1500, finalEv -> {
                                ((Timer)finalEv.getSource()).stop();
                                selesaiDebat();
                            });
                            delayFinal.setRepeats(false);
                            delayFinal.start();
                        }
                    });
                    kontraSpeakTimer.setRepeats(false);
                    kontraSpeakTimer.start();
                });
                kontraTimer.setRepeats(false);
                kontraTimer.start();
            });
            proTimer.setRepeats(false);
            proTimer.start();
        }
    }

    private String generateAksiBonus(EntityPeserta p) {
        int chance = random.nextInt(3);
        if (p instanceof AksiDebat) {
            AksiDebat aksi = (AksiDebat) p;
            switch(chance) {
                case 0:
                    aksi.lakukanInterupsi();
                    return "[💥 INTERUPSI!]";
                case 1:
                    aksi.berikanRebuttal("argumen lawan");
                    return "[🎯 REBUTTAL!]";
                default:
                    return "";
            }
        }
        return "";
    }

    private void selesaiDebat() {
        if (timerDebat != null) timerDebat.stop();

        arena3D.setDebating(false);
        arena3D.setProSpeaking(false);
        arena3D.setKontraSpeaking(false);

        tambahLog("\n╔════════════════════════════════════════╗");
        tambahLog("║   🏆 PERTANDINGAN SELESAI! 🏆          ║");
        tambahLog("╚════════════════════════════════════════╝\n");

        double finalPro = juriMatch.berikanPenilaianAkhir(pPro, totalSkorPro);
        double finalKontra = juriMatch.berikanPenilaianAkhir(pKontra, totalSkorKontra);

        tambahLog("📊 SKOR AKHIR:");
        tambahLog("   💜 " + pPro.getNama() + ": " + String.format("%.2f", finalPro));
        tambahLog("   🧡 " + pKontra.getNama() + ": " + String.format("%.2f", finalKontra));

        String pemenang;
        if (finalPro > finalKontra) {
            pemenang = pPro.getNama();
            tambahLog("\n🏆 PEMENANG: " + pemenang.toUpperCase() + " (PRO)! 🎉");
        } else if (finalKontra > finalPro) {
            pemenang = pKontra.getNama();
            tambahLog("\n🏆 PEMENANG: " + pemenang.toUpperCase() + " (KONTRA)! 🎉");
        } else {
            pemenang = "SERI";
            tambahLog("\n🤝 HASIL: SERI! ⚖️");
        }

        String historyEntry = String.format("[%s] %s vs %s - %s (%.2f:%.2f)",
                java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")),
                pPro.getNama(), pKontra.getNama(), pemenang, finalPro, finalKontra);
        logHistory.add(historyEntry);

        Main.simpanLaporan(pPro, pKontra, juriMatch, finalPro, finalKontra, "PEMENANG: " + pemenang);

        btnMulai.setEnabled(true);
        btnPause.setEnabled(false);
        btnHistory.setEnabled(true);

        JOptionPane.showMessageDialog(this,
                "🎊 Pertandingan selesai!\n🏆 Pemenang: " + pemenang,
                "Hasil Akhir",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void pauseDebat() {
        isPaused = !isPaused;
        btnPause.setText(isPaused ? "▶️ RESUME" : "⏸ PAUSE");
        tambahLog(isPaused ? "\n⏸️ DI-PAUSE" : "\n▶️ DILANJUTKAN");
        arena3D.setDebating(!isPaused);
    }

    private void resetDebat() {
        if (timerDebat != null) timerDebat.stop();

        rondeSekarang = 0;
        totalSkorPro = 0;
        totalSkorKontra = 0;

        barMentalPro.setValue(100);
        barMentalKontra.setValue(100);
        lblRonde.setText("Ronde: 0 / " + maxRonde);
        lblStatusPro.setText("💜 PRO - Mental: 100%");
        lblStatusKontra.setText("🧡 KONTRA - Mental: 100%");

        arena3D.setSkorPro(0);
        arena3D.setSkorKontra(0);
        arena3D.setWaktuSisa(0);
        arena3D.setDebating(false);
        arena3D.setProSpeaking(false);
        arena3D.setKontraSpeaking(false);

        areaHasil.setText("");
        updateStatistik();

        btnMulai.setEnabled(true);
        btnPause.setEnabled(false);
        btnHistory.setEnabled(true);
        isPaused = false;

        tambahLog("🔄 Sistem direset! ✨");
    }

    private void tampilkanHistory() {
        if (logHistory.isEmpty()) {
            JOptionPane.showMessageDialog(this, "📭 Belum ada riwayat.", "History", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JTextArea historyArea = new JTextArea(20, 50);
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        historyArea.setBackground(new Color(20, 20, 35));
        historyArea.setForeground(new Color(220, 255, 220));

        StringBuilder sb = new StringBuilder("📜 RIWAYAT:\n━━━━━━━━━━━━━━━━━━━━━\n\n");
        for (int i = 0; i < logHistory.size(); i++) {
            sb.append((i+1) + ". " + logHistory.get(i) + "\n");
        }
        historyArea.setText(sb.toString());

        JScrollPane scroll = new JScrollPane(historyArea);
        JOptionPane.showMessageDialog(this, scroll, "History", JOptionPane.PLAIN_MESSAGE);
    }

    private void updateMentalBar() {
        int mentalPro = (int)(pPro.getMentalHealth() * 100);
        int mentalKontra = (int)(pKontra.getMentalHealth() * 100);

        barMentalPro.setValue(mentalPro);
        barMentalKontra.setValue(mentalKontra);

        lblStatusPro.setText("💜 PRO - Mental: " + mentalPro + "%");
        lblStatusKontra.setText("🧡 KONTRA - Mental: " + mentalKontra + "%");

        if (mentalPro < 30) barMentalPro.setForeground(new Color(255, 80, 80));
        else if (mentalPro < 60) barMentalPro.setForeground(new Color(255, 180, 80));
        else barMentalPro.setForeground(new Color(160, 80, 240));

        if (mentalKontra < 30) barMentalKontra.setForeground(new Color(255, 80, 80));
        else if (mentalKontra < 60) barMentalKontra.setForeground(new Color(255, 180, 80));
        else barMentalKontra.setForeground(new Color(255, 160, 50));
    }

    private void updateStatistik() {
        StringBuilder stats = new StringBuilder();
        stats.append("╔═════════════════════════╗\n");
        stats.append("║   📊 STATISTIK 📊      ║\n");
        stats.append("╚═════════════════════════╝\n\n");

        if (pPro != null && pKontra != null) {
            stats.append("💜 PRO: ").append(pPro.getNama()).append("\n");
            stats.append("   Mental: ").append(String.format("%.1f%%", pPro.getMentalHealth() * 100)).append("\n");
            stats.append("   Skor: ").append(String.format("%.2f", totalSkorPro)).append("\n");
            stats.append("   Avg: ").append(rondeSekarang > 0 ? String.format("%.2f", totalSkorPro / rondeSekarang) : "0.00").append("\n\n");

            stats.append("🧡 KONTRA: ").append(pKontra.getNama()).append("\n");
            stats.append("   Mental: ").append(String.format("%.1f%%", pKontra.getMentalHealth() * 100)).append("\n");
            stats.append("   Skor: ").append(String.format("%.2f", totalSkorKontra)).append("\n");
            stats.append("   Avg: ").append(rondeSekarang > 0 ? String.format("%.2f", totalSkorKontra / rondeSekarang) : "0.00").append("\n\n");

            stats.append("━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            stats.append("Ronde: ").append(rondeSekarang).append(" / ").append(maxRonde).append("\n");
            stats.append("Selisih Skor: ").append(String.format("%.2f", Math.abs(totalSkorPro - totalSkorKontra))).append("\n");
        } else {
            stats.append("Belum ada data.\n");
        }

        areaStatistik.setText(stats.toString());
    }

    private void tambahLog(String pesan) {
        areaHasil.append(pesan + "\n");
        areaHasil.setCaretPosition(areaHasil.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AutoDebateGUI());
    }
}