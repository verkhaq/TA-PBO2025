import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * Panel 3D Arena Debat dengan karakter animasi
 * Menampilkan 2 debater di podium dengan audience dan scoring
 */
public class DebateArena3D extends JPanel {
    private float animationFrame = 0;
    private Timer animationTimer;

    // Data pertandingan
    private String namaPro = "Tim PRO";
    private String namaKontra = "Tim KONTRA";
    private int skorPro = 0;
    private int skorKontra = 0;
    private String topikDebat = "Menunggu Topik...";
    private int waktuSisa = 0;
    private boolean isDebating = false;

    // Animasi karakter
    private float characterAnimPro = 0;
    private float characterAnimKontra = 0;
    private boolean proIsSpeaking = false;
    private boolean kontraIsSpeaking = false;

    // Warna tema yang lebih soft dan enak dipandang
    private Color colorPro = new Color(120, 81, 169); // Soft Purple
    private Color colorProLight = new Color(168, 139, 211);
    private Color colorKontra = new Color(255, 152, 66); // Soft Orange
    private Color colorKontraLight = new Color(255, 188, 127);
    private Color colorStage = new Color(45, 48, 71);
    private Color colorAudience = new Color(28, 31, 48);
    private Color colorBackground = new Color(18, 20, 35);

    public DebateArena3D() {
        setPreferredSize(new Dimension(800, 500));
        setBackground(colorBackground);
        startAnimation();
    }

    private void startAnimation() {
        animationTimer = new Timer(50, e -> {
            animationFrame += 0.1f;
            if (animationFrame > 360) animationFrame = 0;

            if (proIsSpeaking) {
                characterAnimPro += 0.3f;
            }
            if (kontraIsSpeaking) {
                characterAnimKontra += 0.3f;
            }

            repaint();
        });
        animationTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Background gradient
        drawBackground(g2d, width, height);

        // Audience (penonton yang ramai)
        drawCrowdAudience(g2d, width, height);

        // Stage lights effects
        drawStageLights(g2d, width, height);

        // Stage platform
        drawStage(g2d, width, height);

        // Debate podiums
        int centerX = width / 2;
        int centerY = height / 2 + 50;

        drawPodium(g2d, centerX - 220, centerY, colorPro, "PRO");
        drawPodium(g2d, centerX + 220, centerY, colorKontra, "KONTRA");

        // Characters
        drawCharacter(g2d, centerX - 220, centerY - 80, colorPro, proIsSpeaking, characterAnimPro, true);
        drawCharacter(g2d, centerX + 220, centerY - 80, colorKontra, kontraIsSpeaking, characterAnimKontra, false);

        // Center timer/topic display
        drawCenterDisplay(g2d, centerX, centerY - 20);

        // Score panels dengan desain lebih modern
        drawModernScorePanel(g2d, 50, height - 120, namaPro, skorPro, colorPro, colorProLight);
        drawModernScorePanel(g2d, width - 250, height - 120, namaKontra, skorKontra, colorKontra, colorKontraLight);

        // Topic banner
        drawTopicBanner(g2d, width);

        // Spotlight effects yang lebih soft
        drawSoftSpotlights(g2d, centerX - 220, centerY, colorProLight);
        drawSoftSpotlights(g2d, centerX + 220, centerY, colorKontraLight);
    }

    private void drawBackground(Graphics2D g2d, int width, int height) {
        // Gradient background yang lebih smooth
        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(18, 20, 35),
                width, height, new Color(35, 38, 58)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);

        // Ambient particles yang lebih subtle
        g2d.setColor(new Color(255, 255, 255, 30));
        for (int i = 0; i < 40; i++) {
            float x = (i * 137.5f) % width;
            float y = (i * 73.2f + animationFrame * 0.3f) % height;
            float size = 1 + (i % 3) * 0.5f;
            float alpha = 0.2f + (float)Math.sin(animationFrame * 0.05f + i) * 0.15f;

            g2d.setColor(new Color(255, 255, 255, Math.max(0, Math.min(255, (int)(alpha * 255)))));
            g2d.fill(new Ellipse2D.Float(x, y, size, size));
        }
    }

    private void drawCrowdAudience(Graphics2D g2d, int width, int height) {
        // Penonton yang lebih detail dan ramai
        int baseY = height - 180;

        // Draw multiple rows of audience dengan variasi
        for (int row = 0; row < 4; row++) {
            int y = baseY + row * 25;
            int numPeople = 18 - row * 2;
            int spacing = width / (numPeople + 1);

            for (int i = 0; i < numPeople; i++) {
                int x = spacing * (i + 1);
                int xOffset = (int)(Math.sin(animationFrame * 0.05f + i) * 3);

                // Variasi ukuran berdasarkan kedalaman
                float scale = 1.0f - (row * 0.15f);
                int headSize = (int)(16 * scale);
                int bodyHeight = (int)(22 * scale);

                // Warna penonton yang bervariasi
                Color audienceColor = getAudienceColor(i, row);

                // Shadow
                g2d.setColor(new Color(0, 0, 0, 40));
                g2d.fillOval(x + xOffset - headSize/2, y + headSize + bodyHeight + 2, headSize + 4, 4);

                // Head dengan highlight
                g2d.setColor(audienceColor.darker());
                g2d.fillOval(x + xOffset - headSize/2, y, headSize, headSize);

                // Highlight on head
                g2d.setColor(audienceColor);
                g2d.fillOval(x + xOffset - headSize/2 + 2, y + 2, headSize/3, headSize/3);

                // Body
                int bodyWidth = (int)(headSize * 1.2);
                g2d.setColor(audienceColor);
                g2d.fillRoundRect(x + xOffset - bodyWidth/2, y + headSize, bodyWidth, bodyHeight, 5, 5);

                // Arms - some raised (cheering)
                if (isDebating && i % 3 == 0) {
                    // Raised arms
                    g2d.setStroke(new BasicStroke(3 * scale));
                    int armY = y + headSize + 5;
                    g2d.drawLine(x + xOffset - bodyWidth/2, armY, x + xOffset - bodyWidth/2 - 8, armY - 10);
                    g2d.drawLine(x + xOffset + bodyWidth/2, armY, x + xOffset + bodyWidth/2 + 8, armY - 10);
                }
            }
        }

        // Crowd glow effect
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.08f));
        for (int i = 0; i < 3; i++) {
            RadialGradientPaint glow = new RadialGradientPaint(
                    width/2 + (i - 1) * 200, baseY + 50, 150,
                    new float[]{0f, 1f},
                    new Color[]{
                            i == 0 ? colorProLight : (i == 1 ? new Color(100, 200, 255) : colorKontraLight),
                            new Color(0, 0, 0, 0)
                    }
            );
            g2d.setPaint(glow);
            g2d.fillRect(0, baseY - 50, width, 200);
        }
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    private Color getAudienceColor(int index, int row) {
        // Variasi warna untuk penonton
        Color[] colors = {
                new Color(80, 100, 140),
                new Color(100, 80, 120),
                new Color(70, 90, 130),
                new Color(90, 70, 110),
                new Color(85, 95, 125)
        };
        return colors[(index + row) % colors.length];
    }

    private void drawStageLights(Graphics2D g2d, int width, int height) {
        // Stage lights dari atas
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.12f));

        for (int i = 0; i < 5; i++) {
            int x = (width / 6) * (i + 1);
            Color lightColor = i % 2 == 0 ? colorProLight : colorKontraLight;

            RadialGradientPaint light = new RadialGradientPaint(
                    x, 50, 120,
                    new float[]{0f, 0.5f, 1f},
                    new Color[]{
                            lightColor,
                            new Color(lightColor.getRed(), lightColor.getGreen(), lightColor.getBlue(), 100),
                            new Color(0, 0, 0, 0)
                    }
            );
            g2d.setPaint(light);

            // Light beam
            int[] xPoints = {x - 40, x + 40, x + 80, x - 80};
            int[] yPoints = {50, 50, height / 2, height / 2};
            g2d.fillPolygon(xPoints, yPoints, 4);
        }

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    private void drawStage(Graphics2D g2d, int width, int height) {
        int stageY = height / 2 + 120;

        // Stage shadow dengan blur effect
        g2d.setColor(new Color(0, 0, 0, 80));
        for (int i = 0; i < 5; i++) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
            int[] xPoints = {60 - i*2, width - 60 + i*2, width - 110 + i*2, 110 - i*2};
            int[] yPoints = {stageY + 50 + i, stageY + 50 + i, stageY + 85 + i, stageY + 85 + i};
            g2d.fillPolygon(xPoints, yPoints, 4);
        }
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        // Stage with better gradient
        GradientPaint stageGradient = new GradientPaint(
                0, stageY, new Color(55, 58, 81),
                0, stageY + 50, colorStage
        );
        g2d.setPaint(stageGradient);
        g2d.fillRect(80, stageY, width - 160, 50);

        // Stage edge with glow
        g2d.setColor(new Color(120, 130, 160, 180));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(80, stageY, width - 80, stageY);

        // Stage pattern
        g2d.setColor(new Color(255, 255, 255, 20));
        for (int i = 0; i < width; i += 40) {
            g2d.setStroke(new BasicStroke(1));
            g2d.drawLine(i, stageY, i, stageY + 50);
        }
    }

    private void drawPodium(Graphics2D g2d, int x, int y, Color color, String side) {
        // Podium dengan depth lebih baik
        Color podiumDark = new Color(
                Math.max(0, color.getRed() - 60),
                Math.max(0, color.getGreen() - 60),
                Math.max(0, color.getBlue() - 60)
        );

        // Front face dengan gradient
        GradientPaint baseGradient = new GradientPaint(
                x - 35, y + 20, color,
                x + 35, y + 80, podiumDark
        );
        g2d.setPaint(baseGradient);

        int[] xPoints = {x - 35, x + 35, x + 30, x - 30};
        int[] yPoints = {y + 20, y + 20, y + 80, y + 80};
        g2d.fillPolygon(xPoints, yPoints, 4);

        // Top face
        GradientPaint topGradient = new GradientPaint(
                x, y + 15, color.brighter(),
                x, y + 20, color
        );
        g2d.setPaint(topGradient);
        int[] topX = {x - 35, x + 35, x + 40, x - 40};
        int[] topY = {y + 20, y + 20, y + 15, y + 15};
        g2d.fillPolygon(topX, topY, 4);

        // Side highlight
        g2d.setColor(color.brighter().brighter());
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(x - 35, y + 20, x - 35, y + 78);

        // Microphone
        drawMicrophone(g2d, x, y - 10, color);

        // Side label dengan better text rendering
        g2d.setFont(new Font("Arial", Font.BOLD, 13));
        g2d.setColor(Color.WHITE);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(side);
        g2d.drawString(side, x - textWidth/2, y + 55);
    }

    private void drawMicrophone(Graphics2D g2d, int x, int y, Color color) {
        // Stand
        g2d.setColor(new Color(110, 115, 135));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(x, y + 20, x, y - 30);

        // Mic head dengan detail lebih baik
        GradientPaint micGradient = new GradientPaint(
                x - 8, y - 35, new Color(160, 165, 185),
                x + 8, y - 25, new Color(90, 95, 115)
        );
        g2d.setPaint(micGradient);
        g2d.fillRoundRect(x - 8, y - 40, 16, 20, 8, 8);

        // Mic details (grill)
        g2d.setColor(new Color(70, 75, 95));
        for (int i = 0; i < 3; i++) {
            g2d.drawLine(x - 6, y - 36 + i * 4, x + 6, y - 36 + i * 4);
        }

        // Highlight
        g2d.setColor(new Color(220, 225, 240, 180));
        g2d.fillOval(x - 4, y - 38, 5, 10);

        // Glow when speaking
        if ((x < getWidth()/2 && proIsSpeaking) || (x > getWidth()/2 && kontraIsSpeaking)) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
            RadialGradientPaint glow = new RadialGradientPaint(
                    x, y - 30, 25,
                    new float[]{0f, 0.6f, 1f},
                    new Color[]{
                            color.brighter(),
                            color,
                            new Color(color.getRed(), color.getGreen(), color.getBlue(), 0)
                    }
            );
            g2d.setPaint(glow);
            g2d.fillOval(x - 25, y - 55, 50, 50);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
    }

    private void drawCharacter(Graphics2D g2d, int x, int y, Color color, boolean isSpeaking, float anim, boolean facingRight) {
        int direction = facingRight ? 1 : -1;

        // Shadow dengan blur
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        for (int i = 0; i < 3; i++) {
            g2d.setColor(new Color(0, 0, 0, 20));
            g2d.fillOval(x - 28 + i, y + 95 + i, 56 - i*2, 12 - i);
        }
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        float bounce = isSpeaking ? (float)Math.sin(anim) * 3 : 0;
        int bodyY = (int)(y + bounce);

        // Legs dengan lebih detail
        Color pantsColor = new Color(50, 55, 80);
        g2d.setColor(pantsColor);
        g2d.setStroke(new BasicStroke(9, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine(x - 8, bodyY + 50, x - 10, bodyY + 85);
        g2d.drawLine(x + 8, bodyY + 50, x + 10, bodyY + 85);

        // Shoes dengan highlight
        g2d.setColor(new Color(35, 38, 58));
        g2d.fillRoundRect(x - 18, bodyY + 82, 14, 9, 5, 5);
        g2d.fillRoundRect(x + 5, bodyY + 82, 14, 9, 5, 5);
        g2d.setColor(new Color(60, 65, 90));
        g2d.fillRoundRect(x - 17, bodyY + 82, 12, 4, 3, 3);
        g2d.fillRoundRect(x + 6, bodyY + 82, 12, 4, 3, 3);

        // Body/Jacket dengan gradient lebih smooth
        GradientPaint bodyGradient = new GradientPaint(
                x, bodyY + 10, color.brighter(),
                x, bodyY + 55, color.darker()
        );
        g2d.setPaint(bodyGradient);
        g2d.fillRoundRect(x - 22, bodyY + 10, 44, 45, 15, 15);

        // Shirt/inner dengan bayangan
        g2d.setColor(new Color(245, 245, 250));
        g2d.fillRoundRect(x - 15, bodyY + 20, 30, 28, 10, 10);

        // Collar detail
        g2d.setColor(new Color(220, 220, 230));
        int[] collarX = {x - 5, x + 5, x + 8, x - 8};
        int[] collarY = {bodyY + 20, bodyY + 20, bodyY + 25, bodyY + 25};
        g2d.fillPolygon(collarX, collarY, 4);

        // Arms dengan animasi lebih natural
        float armAngle = isSpeaking ? (float)Math.sin(anim * 2) * 0.4f : 0.15f;

        drawArm(g2d, x - 22, bodyY + 20, armAngle, color, direction, true);
        drawArm(g2d, x + 22, bodyY + 20, armAngle + 0.5f, color, -direction, false);

        // Neck
        g2d.setColor(new Color(255, 220, 185));
        g2d.fillRect(x - 8, bodyY, 16, 12);

        // Head dengan gradient untuk depth
        GradientPaint headGradient = new GradientPaint(
                x - 15, bodyY - 20, new Color(255, 230, 200),
                x + 15, bodyY + 10, new Color(255, 210, 180)
        );
        g2d.setPaint(headGradient);
        g2d.fillOval(x - 18, bodyY - 25, 36, 38);

        // Ear
        g2d.setColor(new Color(255, 220, 190));
        int earX = facingRight ? x + 15 : x - 18;
        g2d.fillOval(earX, bodyY - 8, 8, 12);

        // Hair dengan style lebih menarik
        g2d.setColor(new Color(45, 35, 25));
        GeneralPath hair = new GeneralPath();
        hair.moveTo(x - 18, bodyY - 8);
        hair.curveTo(x - 22, bodyY - 22, x - 12, bodyY - 30, x, bodyY - 28);
        hair.curveTo(x + 12, bodyY - 30, x + 22, bodyY - 22, x + 18, bodyY - 8);
        hair.lineTo(x + 15, bodyY - 5);
        hair.lineTo(x - 15, bodyY - 5);
        hair.closePath();
        g2d.fill(hair);

        // Hair highlight
        g2d.setColor(new Color(70, 55, 40));
        g2d.fillOval(x - 8, bodyY - 24, 16, 8);

        // Face features
        int faceDir = facingRight ? 5 : -5;

        // Eyes dengan lebih expressive
        g2d.setColor(Color.WHITE);
        g2d.fillOval(x - 10 + faceDir, bodyY - 10, 9, 9);
        g2d.fillOval(x + 2 + faceDir, bodyY - 10, 9, 9);

        // Eye outline
        g2d.setColor(new Color(100, 80, 60));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawOval(x - 10 + faceDir, bodyY - 10, 9, 9);
        g2d.drawOval(x + 2 + faceDir, bodyY - 10, 9, 9);

        // Pupils
        g2d.setColor(new Color(50, 35, 25));
        int pupilX = isSpeaking ? (int)(Math.sin(anim * 3) * 2) : 0;
        g2d.fillOval(x - 8 + faceDir + pupilX, bodyY - 8, 5, 5);
        g2d.fillOval(x + 4 + faceDir + pupilX, bodyY - 8, 5, 5);

        // Pupil highlights
        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.fillOval(x - 7 + faceDir + pupilX, bodyY - 7, 2, 2);
        g2d.fillOval(x + 5 + faceDir + pupilX, bodyY - 7, 2, 2);

        // Eyebrows
        g2d.setColor(new Color(40, 30, 20));
        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine(x - 12 + faceDir, bodyY - 14, x - 4 + faceDir, bodyY - 13);
        g2d.drawLine(x + 1 + faceDir, bodyY - 13, x + 9 + faceDir, bodyY - 14);

        // Nose
        g2d.setColor(new Color(255, 200, 170));
        g2d.drawLine(x + 3 + faceDir, bodyY - 2, x + 4 + faceDir, bodyY + 1);

        // Mouth
        g2d.setColor(new Color(200, 100, 100));
        if (isSpeaking) {
            int mouthOpen = (int)(Math.abs(Math.sin(anim * 2)) * 10) + 3;
            g2d.fillRoundRect(x - 5 + faceDir, bodyY + 5, 11, mouthOpen, 5, 5);
            // Teeth
            g2d.setColor(Color.WHITE);
            g2d.fillRect(x - 4 + faceDir, bodyY + 6, 9, 2);
        } else {
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawArc(x - 5 + faceDir, bodyY + 3, 11, 9, 0, -180);
        }

        // Speaking indicator
        if (isSpeaking) {
            drawSpeechIndicator(g2d, x + (30 * direction), bodyY - 30, color, anim);
        }
    }

    private void drawArm(Graphics2D g2d, int startX, int startY, float angle, Color color, int direction, boolean isLeft) {
        int armLength = 28;
        int endX = (int)(startX + Math.cos(angle) * armLength * direction);
        int endY = (int)(startY + Math.sin(angle) * armLength);

        // Arm
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(11, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine(startX, startY, endX, endY);

        // Hand
        g2d.setColor(new Color(255, 220, 185));
        g2d.fillOval(endX - 7, endY - 7, 14, 14);

        // Hand highlight
        g2d.setColor(new Color(255, 235, 205));
        g2d.fillOval(endX - 5, endY - 5, 6, 6);
    }

    private void drawSpeechIndicator(Graphics2D g2d, int x, int y, Color color, float anim) {
        for (int i = 0; i < 3; i++) {
            float offset = (anim * 0.5f + i) % 3;
            int size = 6 + i * 3;
            float alpha = 0.8f - (offset / 3.5f);

            Color indicatorColor = new Color(
                    color.getRed(),
                    color.getGreen(),
                    color.getBlue(),
                    Math.max(0, Math.min(255, (int)(alpha * 200)))
            );
            g2d.setColor(indicatorColor);
            g2d.fillOval(x + (int)(offset * 18), y - (int)(offset * 12), size, size);

            // Inner glow
            g2d.setColor(new Color(255, 255, 255, (int)(alpha * 100)));
            g2d.fillOval(x + (int)(offset * 18) + 1, y - (int)(offset * 12) + 1, size - 2, size - 2);
        }
    }

    private void drawCenterDisplay(Graphics2D g2d, int x, int y) {
        int width = 160;
        int height = 100;

        // Base dengan 3D effect
        Color baseColor = new Color(40, 43, 65);
        GradientPaint baseGrad = new GradientPaint(
                x, y - 20, baseColor.brighter(),
                x, y + 40, baseColor.darker()
        );
        g2d.setPaint(baseGrad);

        int[] xPoints = {x - width/2, x + width/2, x + width/2 - 12, x - width/2 + 12};
        int[] yPoints = {y - 20, y - 20, y + 40, y + 40};
        g2d.fillPolygon(xPoints, yPoints, 4);

        // Screen dengan border
        g2d.setColor(new Color(15, 18, 30));
        g2d.fillRoundRect(x - 60, y - 10, 120, 45, 12, 12);

        // Screen glow
        g2d.setColor(new Color(100, 200, 255, 80));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x - 60, y - 10, 120, 45, 12, 12);

        // Inner glow
        g2d.setColor(new Color(100, 200, 255, 40));
        g2d.drawRoundRect(x - 58, y - 8, 116, 41, 10, 10);

        // Timer text dengan better rendering
        int minutes = waktuSisa / 60;
        int seconds = waktuSisa % 60;
        String timeText = String.format("%02d:%02d", minutes, seconds);

        g2d.setFont(new Font("Arial", Font.BOLD, 28));
        FontMetrics fm = g2d.getFontMetrics();

        // Glow effect untuk text
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
        g2d.setColor(new Color(100, 220, 255));
        for (int i = 1; i <= 2; i++) {
            g2d.drawString(timeText, x - fm.stringWidth(timeText)/2 - i, y + 20 - i);
            g2d.drawString(timeText, x - fm.stringWidth(timeText)/2 + i, y + 20 + i);
        }

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        g2d.setColor(Color.WHITE);
        g2d.drawString(timeText, x - fm.stringWidth(timeText)/2, y + 20);

        // Decorative lights dengan pulse effect
        for (int i = 0; i < 3; i++) {
            float pulse = (float)Math.sin(animationFrame * 0.1f + i * Math.PI/3) * 0.3f + 0.7f;
            Color lightColor = new Color(100, 220, 255, (int)(pulse * 220));
            g2d.setColor(lightColor);
            g2d.fillOval(x - 70 + i * 60, y + 48, 8, 8);

            // Light glow
            g2d.setColor(new Color(100, 220, 255, (int)(pulse * 100)));
            g2d.fillOval(x - 74 + i * 60, y + 44, 16, 16);
        }
    }

    private void drawModernScorePanel(Graphics2D g2d, int x, int y, String name, int score, Color color, Color lightColor) {
        // Panel dengan glass effect
        g2d.setColor(new Color(30, 33, 50, 230));
        g2d.fillRoundRect(x, y, 200, 100, 20, 20);

        // Glass reflection
        GradientPaint glassGrad = new GradientPaint(
                x, y, new Color(255, 255, 255, 30),
                x, y + 50, new Color(255, 255, 255, 0)
        );
        g2d.setPaint(glassGrad);
        g2d.fillRoundRect(x + 5, y + 5, 190, 45, 15, 15);

        // Border dengan gradient
        GradientPaint borderGrad = new GradientPaint(
                x, y, color.brighter(),
                x + 200, y + 100, lightColor
        );
        g2d.setPaint(borderGrad);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(x, y, 200, 100, 20, 20);

        // Inner border glow
        g2d.setStroke(new BasicStroke(1));
        g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 80));
        g2d.drawRoundRect(x + 3, y + 3, 194, 94, 17, 17);

        // Name label dengan shadow
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics fm = g2d.getFontMetrics();

        // Shadow
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.drawString(name, x + 100 - fm.stringWidth(name)/2 + 1, y + 26);

        // Main text
        g2d.setColor(Color.WHITE);
        g2d.drawString(name, x + 100 - fm.stringWidth(name)/2, y + 25);

        // Score label
        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        g2d.setColor(new Color(200, 200, 220));
        fm = g2d.getFontMetrics();
        g2d.drawString("SCORE", x + 100 - fm.stringWidth("SCORE")/2, y + 45);

        // Score value dengan glow
        String scoreText = String.valueOf(score);
        g2d.setFont(new Font("Arial", Font.BOLD, 32));
        fm = g2d.getFontMetrics();

        // Glow
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g2d.setColor(lightColor);
        for (int i = 1; i <= 3; i++) {
            g2d.drawString(scoreText, x + 100 - fm.stringWidth(scoreText)/2 - i, y + 80 - i);
            g2d.drawString(scoreText, x + 100 - fm.stringWidth(scoreText)/2 + i, y + 80 + i);
        }

        // Main score
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        g2d.setColor(Color.WHITE);
        g2d.drawString(scoreText, x + 100 - fm.stringWidth(scoreText)/2, y + 80);
    }

    private void drawTopicBanner(Graphics2D g2d, int width) {
        // Banner dengan glass morphism
        int bannerY = 30;
        int bannerHeight = 65;

        // Background blur effect
        g2d.setColor(new Color(35, 38, 58, 240));
        g2d.fillRoundRect(width/2 - 360, bannerY, 720, bannerHeight, 25, 25);

        // Glass reflection
        GradientPaint glassGrad = new GradientPaint(
                width/2, bannerY, new Color(255, 255, 255, 40),
                width/2, bannerY + bannerHeight/2, new Color(255, 255, 255, 0)
        );
        g2d.setPaint(glassGrad);
        g2d.fillRoundRect(width/2 - 355, bannerY + 5, 710, bannerHeight/2, 20, 20);

        // Border dengan rainbow gradient
        GradientPaint borderGrad = new GradientPaint(
                width/2 - 360, bannerY, colorProLight,
                width/2 + 360, bannerY + bannerHeight, colorKontraLight
        );
        g2d.setPaint(borderGrad);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(width/2 - 360, bannerY, 720, bannerHeight, 25, 25);

        // Topic text dengan shadow
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics fm = g2d.getFontMetrics();

        // Shadow
        g2d.setColor(new Color(0, 0, 0, 120));
        g2d.drawString(topikDebat, width/2 - fm.stringWidth(topikDebat)/2 + 2, bannerY + 42);

        // Main text
        g2d.setColor(Color.WHITE);
        g2d.drawString(topikDebat, width/2 - fm.stringWidth(topikDebat)/2, bannerY + 40);

        // Animated corner sparkles
        for (int i = 0; i < 4; i++) {
            float angle = i * (float)Math.PI / 2 + animationFrame * 0.08f;
            int cornerX = i < 2 ? width/2 - 345 : width/2 + 345;
            int cornerY = i % 2 == 0 ? bannerY + 15 : bannerY + bannerHeight - 15;

            int sparkX = cornerX + (int)(Math.cos(angle) * 18);
            int sparkY = cornerY + (int)(Math.sin(angle) * 18);

            // Sparkle glow
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
            Color sparkColor = i % 2 == 0 ? colorProLight : colorKontraLight;
            RadialGradientPaint sparkGlow = new RadialGradientPaint(
                    sparkX, sparkY, 10,
                    new float[]{0f, 1f},
                    new Color[]{sparkColor, new Color(sparkColor.getRed(), sparkColor.getGreen(), sparkColor.getBlue(), 0)}
            );
            g2d.setPaint(sparkGlow);
            g2d.fillOval(sparkX - 10, sparkY - 10, 20, 20);

            // Sparkle center
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            g2d.setColor(Color.WHITE);
            g2d.fillOval(sparkX - 3, sparkY - 3, 6, 6);
        }

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    private void drawSoftSpotlights(Graphics2D g2d, int x, int y, Color color) {
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));

        RadialGradientPaint spotlight = new RadialGradientPaint(
                x, y - 150, 180,
                new float[]{0f, 0.5f, 1f},
                new Color[]{
                        new Color(color.getRed(), color.getGreen(), color.getBlue(), 200),
                        new Color(color.getRed(), color.getGreen(), color.getBlue(), 80),
                        new Color(0, 0, 0, 0)
                }
        );
        g2d.setPaint(spotlight);
        g2d.fillOval(x - 180, y - 330, 360, 360);

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    // Public methods untuk kontrol dari luar
    public void setNamaPro(String nama) {
        this.namaPro = nama != null && !nama.trim().isEmpty() ? nama : "Tim PRO";
        repaint();
    }

    public void setNamaKontra(String nama) {
        this.namaKontra = nama != null && !nama.trim().isEmpty() ? nama : "Tim KONTRA";
        repaint();
    }

    public void setSkorPro(int skor) {
        this.skorPro = Math.max(0, skor);
        repaint();
    }

    public void setSkorKontra(int skor) {
        this.skorKontra = Math.max(0, skor);
        repaint();
    }

    public void setTopikDebat(String topik) {
        this.topikDebat = topik != null && !topik.trim().isEmpty() ? topik : "Menunggu Topik...";
        repaint();
    }

    public void setWaktuSisa(int waktu) {
        this.waktuSisa = Math.max(0, waktu);
        repaint();
    }

    public void setDebating(boolean debating) {
        this.isDebating = debating;
    }

    public void setProSpeaking(boolean speaking) {
        this.proIsSpeaking = speaking;
        if (!speaking) characterAnimPro = 0;
        repaint();
    }

    public void setKontraSpeaking(boolean speaking) {
        this.kontraIsSpeaking = speaking;
        if (!speaking) characterAnimKontra = 0;
        repaint();
    }

    public void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        stopAnimation();
    }
}