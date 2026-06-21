package Tuan4;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class WebButton extends JButton {
    private Color mauSang;
    private Color mauToi;
    private boolean isHovered = false;
    private boolean isPressed = false;

    public WebButton(String text, String hexMauSang, String hexMauToi) {
        super(text);
        this.mauSang = Color.decode(hexMauSang);
        this.mauToi = Color.decode(hexMauToi);
        
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE); 
        setFont(new Font("Segoe UI", Font.BOLD, 14)); 
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
            @Override
            public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
            @Override
            public void mousePressed(MouseEvent e) { isPressed = true; repaint(); }
            @Override
            public void mouseReleased(MouseEvent e) { isPressed = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color topColor = mauSang;
        Color bottomColor = mauToi;

        if (isPressed) {
            topColor = mauToi.darker();
            bottomColor = mauSang.darker();
        } else if (isHovered) {
            topColor = mauSang.brighter();
            bottomColor = mauToi.brighter();
        }

        // Do mau Gradient 3D
        GradientPaint gradient = new GradientPaint(0, 0, topColor, 0, getHeight(), bottomColor);
        g2.setPaint(gradient);
        
        // Bo goc 15px
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 15, 15));

        super.paintComponent(g);
        g2.dispose();
    }
}