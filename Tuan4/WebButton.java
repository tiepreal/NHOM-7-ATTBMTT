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

    // Hàm khởi tạo nhận vào tên nút và 2 mã màu (để tạo khối gradient)
    public WebButton(String text, String hexMauSang, String hexMauToi) {
        super(text);
        this.mauSang = Color.decode(hexMauSang);
        this.mauToi = Color.decode(hexMauToi);
        
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE); // Chữ màu trắng
        setFont(new Font("Segoe UI", Font.BOLD, 14)); // Font chữ hiện đại giống Web
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Cảm biến chuột để tạo hiệu ứng chuyển động
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

    // Ghi đè hàm vẽ đồ họa của Java để tạo hình khối và bo góc
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        // Bật khử răng cưa giúp viền nút mịn màng không bị rỗ
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color topColor = mauSang;
        Color bottomColor = mauToi;

        // Xử lý hiệu ứng nổi/chìm khi tương tác
        if (isPressed) {
            topColor = mauToi.darker();
            bottomColor = mauSang.darker();
        } else if (isHovered) {
            topColor = mauSang.brighter();
            bottomColor = mauToi.brighter();
        }

        // Tạo hiệu ứng đổ màu Gradient từ trên xuống dưới (Tạo độ phồng 3D)
        GradientPaint gradient = new GradientPaint(0, 0, topColor, 0, getHeight(), bottomColor);
        g2.setPaint(gradient);
        
        // Vẽ nút với bán kính bo góc là 15 pixel
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 15, 15));

        super.paintComponent(g);
        g2.dispose();
    }
}