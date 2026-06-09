package Tuan4;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.crypto.spec.SecretKeySpec;

public class MainForm extends javax.swing.JFrame {

    private JTextArea txtBanRo, txtBanMa;
    private JTextField txtKhoa;
    private WebButton btnSinhKhoa, btnMoFile, btnMaHoa, btnGiaiMa, btnLuuFile, btnLamMoi;

    private String lastKey = "";
    private String lastCipher = "";

    public MainForm() {
        setTitle("Chương trình mã hóa AES - Nhóm 7");
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        
        // Màu nền xám nhạt sang trọng kiểu Web
        getContentPane().setBackground(Color.decode("#F5F6F7"));
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ==========================================
        // 1. CỘT BÊN TRÁI: THANH MENU CHỨC NĂNG
        // ==========================================
        JPanel pnlLeft = new JPanel();
        pnlLeft.setBackground(Color.decode("#F5F6F7"));
        pnlLeft.setLayout(new GridLayout(6, 1, 10, 15));
        pnlLeft.setBorder(BorderFactory.createTitledBorder("Thao Tác"));
        pnlLeft.setPreferredSize(new Dimension(160, 0)); 

        // Khởi tạo các nút có hình khối (Bo góc + Gradient 3D)
        btnMoFile = new WebButton("Mở File", "#95a5a6", "#7f8c8d");     // Xám
        btnMaHoa = new WebButton("Mã Hóa ->", "#2ecc71", "#27ae60");    // Xanh lá
        btnGiaiMa = new WebButton("<- Giải Mã", "#e67e22", "#d35400");  // Cam
        btnLuuFile = new WebButton("Lưu File", "#3498db", "#2980b9");   // Xanh biển
        btnLamMoi = new WebButton("Làm Mới", "#e74c3c", "#c0392b");     // Đỏ

        pnlLeft.add(btnMoFile);
        pnlLeft.add(btnMaHoa);
        pnlLeft.add(btnGiaiMa);
        pnlLeft.add(btnLuuFile);
        pnlLeft.add(btnLamMoi);

        // ==========================================
        // 2. KHUNG BÊN PHẢI: NHẬP LIỆU VÀ HIỂN THỊ
        // ==========================================
        JPanel pnlRight = new JPanel(new BorderLayout(10, 10));
        pnlRight.setBackground(Color.decode("#F5F6F7"));

        // Khung Nhập Khóa
        JPanel pnlKey = new JPanel(new BorderLayout(10, 0));
        pnlKey.setBackground(Color.WHITE);
        pnlKey.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel lblKhoa = new JLabel(" Khóa bảo mật: ");
        lblKhoa.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pnlKey.add(lblKhoa, BorderLayout.WEST);
        
        txtKhoa = new JTextField();
        txtKhoa.setFont(new Font("Consolas", Font.BOLD, 16));
        txtKhoa.setForeground(Color.decode("#8e44ad")); 
        txtKhoa.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#bdc3c7"), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        pnlKey.add(txtKhoa, BorderLayout.CENTER);
        
        btnSinhKhoa = new WebButton("Sinh Khóa", "#9b59b6", "#8e44ad"); // Tím
        pnlKey.add(btnSinhKhoa, BorderLayout.EAST);
        
        pnlRight.add(pnlKey, BorderLayout.NORTH);

        // Khung Bản Rõ & Bản Mã
        JPanel pnlCenter = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlCenter.setBackground(Color.decode("#F5F6F7"));
        
        JPanel pnlInput = new JPanel(new BorderLayout());
        pnlInput.setBackground(Color.decode("#F5F6F7"));
        pnlInput.setBorder(BorderFactory.createTitledBorder("Văn bản gốc (Bản Rõ)"));
        txtBanRo = new JTextArea();
        txtBanRo.setLineWrap(true);
        txtBanRo.setWrapStyleWord(true);
        txtBanRo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBanRo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pnlInput.add(new JScrollPane(txtBanRo), BorderLayout.CENTER);

        JPanel pnlOutput = new JPanel(new BorderLayout());
        pnlOutput.setBackground(Color.decode("#F5F6F7"));
        pnlOutput.setBorder(BorderFactory.createTitledBorder("Mật mã (Bản Mã Base64)"));
        txtBanMa = new JTextArea();
        txtBanMa.setLineWrap(true);
        txtBanMa.setWrapStyleWord(true);
        txtBanMa.setFont(new Font("Consolas", Font.PLAIN, 14));
        txtBanMa.setForeground(Color.decode("#2980b9")); 
        txtBanMa.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pnlOutput.add(new JScrollPane(txtBanMa), BorderLayout.CENTER);

        pnlCenter.add(pnlInput);
        pnlCenter.add(pnlOutput);
        
        pnlRight.add(pnlCenter, BorderLayout.CENTER);

        add(pnlLeft, BorderLayout.WEST);
        add(pnlRight, BorderLayout.CENTER);

        // ==========================================
        // 3. CÀI ĐẶT SỰ KIỆN CHO CÁC NÚT (Giữ nguyên logic)
        // ==========================================
        btnSinhKhoa.addActionListener(e -> {
            try {
                SecretKeySpec key = AESService.createRandomKey();
                txtKhoa.setText(AESService.bytesToBase64(key.getEncoded()));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi sinh khóa!");
            }
        });

        btnMoFile.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    String content = AESService.readFromFile(chooser.getSelectedFile().getAbsolutePath());
                    String[] options = {"Nạp vào ô Bản Rõ", "Nạp vào ô Bản Mã"};
                    int choice = JOptionPane.showOptionDialog(this, 
                            "Bạn muốn tải dữ liệu này vào đâu để xử lý?", 
                            "Chọn vị trí", JOptionPane.DEFAULT_OPTION, 
                            JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                            
                    if(choice == 0) {
                        txtBanRo.setText(content);
                    } else if (choice == 1) {
                        txtBanMa.setText(content);
                        lastCipher = content;
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Không thể đọc file này!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnMaHoa.addActionListener(e -> {
            try {
                String keyStr = txtKhoa.getText().trim();
                String plain = txtBanRo.getText();
                if(keyStr.isEmpty() || plain.isEmpty()){
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ Khóa và Bản Rõ!", "Thiếu dữ liệu", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                SecretKeySpec key = AESService.createKeyFromText(keyStr);
                byte[] cipherBytes = AESService.encrypt(plain, key);
                txtBanMa.setText(AESService.bytesToBase64(cipherBytes));
                
                lastKey = keyStr;
                lastCipher = txtBanMa.getText();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi mã hóa! Vui lòng kiểm tra lại cấu trúc Khóa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnGiaiMa.addActionListener(this::xuLyGiaiMaVaBatLoi);

        btnLuuFile.addActionListener(e -> {
            if(txtBanMa.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không có dữ liệu để lưu!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            JFileChooser chooser = new JFileChooser();
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    String path = chooser.getSelectedFile().getAbsolutePath();
                    if (!path.toLowerCase().endsWith(".txt") && !path.toLowerCase().endsWith(".doc")) {
                        path += ".txt"; 
                    }
                    AESService.saveToFile(path, txtBanMa.getText());
                    JOptionPane.showMessageDialog(this, "Đã lưu thành công tại:\n" + path, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Xảy ra lỗi khi lưu file!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnLamMoi.addActionListener(e -> {
            txtKhoa.setText("");
            txtBanRo.setText("");
            txtBanMa.setText("");
            lastKey = "";
            lastCipher = "";
            txtKhoa.requestFocus();
        });
    }

    private void xuLyGiaiMaVaBatLoi(java.awt.event.ActionEvent evt) {                                         
        String strKey = txtKhoa.getText().trim();
        String strCipher = txtBanMa.getText().trim();
        
        if(strKey.isEmpty() || strCipher.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ Khóa và Bản Mã để Giải Mã!", "Thiếu dữ liệu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean khoaHopLe = true;
        boolean banMaHopLe = true;

        if (!lastKey.isEmpty() && !strKey.equals(lastKey)) { khoaHopLe = false; }
        if (!lastCipher.isEmpty() && !strCipher.equals(lastCipher)) { banMaHopLe = false; }

        if (!khoaHopLe || !banMaHopLe) {
            hienThiLoi(khoaHopLe, banMaHopLe);
            return;
        }

        try {
            SecretKeySpec secretKey = AESService.createKeyFromText(strKey);
            byte[] cipherBytes = AESService.base64ToBytes(strCipher);
            
            String plaintext = AESService.decrypt(cipherBytes, secretKey);
            txtBanRo.setText(plaintext);
            
            lastKey = strKey;
            lastCipher = strCipher;
        } catch (IllegalArgumentException e) {
            hienThiLoi(true, false);
        } catch (javax.crypto.BadPaddingException | javax.crypto.IllegalBlockSizeException e) {
            JOptionPane.showMessageDialog(this, "LỖI TÒAN VẸN: Khóa không đúng hoặc Bản Mã đã bị hỏng!", "Lỗi Giải Mã", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void hienThiLoi(boolean khoa, boolean banMa) {
        if (!khoa && !banMa) {
            JOptionPane.showMessageDialog(this, "CẢNH BÁO: Cả KHÓA và BẢN MÃ đều đã bị chỉnh sửa!", "Lỗi Kép", JOptionPane.ERROR_MESSAGE);
        } else if (!khoa) {
            JOptionPane.showMessageDialog(this, "LỖI TÒAN VẸN: KHÓA giải mã đã bị thay đổi cấu trúc hoặc bị chỉnh sửa!", "Sai Khóa", JOptionPane.WARNING_MESSAGE);
        } else if (!banMa) {
            JOptionPane.showMessageDialog(this, "LỖI TÒAN VẸN: BẢN MÃ đã bị can thiệp sửa đổi nội dung, không thể khôi phục!", "Sai Bản Mã", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String args[]) {
        try {
            // Lấy giao diện native của hệ điều hành (Windows) để các hộp thoại, thanh cuộn nhìn mượt hơn
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        java.awt.EventQueue.invokeLater(() -> {
            new MainForm().setVisible(true);
        });
    }
}
