package Tuan4;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.crypto.spec.SecretKeySpec;

public class MainForm extends javax.swing.JFrame {

    private JTextArea txtBanRo, txtBanMa;
    private JTextField txtKhoa;
    private WebButton btnSinhKhoa, btnMoFile, btnMaHoa, btnGiaiMa, btnLuuFile, btnLamMoi;

    // Biến lưu vết trạng thái (State Tracking) để bắt lỗi người dùng sửa bậy
    private String lastKey = "";
    private String lastCipher = "";

    public MainForm() {
        setTitle("Chương trình mã hóa AES - Nhóm 7");
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        getContentPane().setBackground(Color.decode("#F5F6F7")); 
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ==========================================
        // 1. CỘT TRÁI: THANH MENU THAO TÁC
        // ==========================================
        JPanel pnlLeft = new JPanel();
        pnlLeft.setBackground(Color.decode("#F5F6F7"));
        pnlLeft.setLayout(new GridLayout(6, 1, 10, 15));
        pnlLeft.setBorder(BorderFactory.createTitledBorder("Thao Tác"));
        pnlLeft.setPreferredSize(new Dimension(160, 0)); 

        btnMoFile = new WebButton("Mở File", "#95a5a6", "#7f8c8d");     
        btnMaHoa = new WebButton("Mã Hóa", "#2ecc71", "#27ae60");    
        btnGiaiMa = new WebButton("Giải Mã", "#e67e22", "#d35400");  
        btnLuuFile = new WebButton("Lưu File", "#3498db", "#2980b9");   
        btnLamMoi = new WebButton("Làm Mới", "#e74c3c", "#c0392b");     

        pnlLeft.add(btnMoFile);
        pnlLeft.add(btnMaHoa);
        pnlLeft.add(btnGiaiMa);
        pnlLeft.add(btnLuuFile);
        pnlLeft.add(btnLamMoi);

        // ==========================================
        // 2. KHUNG PHẢI: NHẬP LIỆU VÀ HIỂN THỊ
        // ==========================================
        JPanel pnlRight = new JPanel(new BorderLayout(10, 10));
        pnlRight.setBackground(Color.decode("#F5F6F7"));

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
        
        btnSinhKhoa = new WebButton("Sinh Khóa", "#9b59b6", "#8e44ad");
        pnlKey.add(btnSinhKhoa, BorderLayout.EAST);
        pnlRight.add(pnlKey, BorderLayout.NORTH);

        JPanel pnlCenter = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlCenter.setBackground(Color.decode("#F5F6F7"));
        
        JPanel pnlInput = new JPanel(new BorderLayout());
        pnlInput.setBackground(Color.decode("#F5F6F7"));
        pnlInput.setBorder(BorderFactory.createTitledBorder("Văn bản gốc (Bản Rõ)"));
        txtBanRo = new JTextArea();
        txtBanRo.setLineWrap(true);
        txtBanRo.setWrapStyleWord(true);
        txtBanRo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnlInput.add(new JScrollPane(txtBanRo), BorderLayout.CENTER);

        JPanel pnlOutput = new JPanel(new BorderLayout());
        pnlOutput.setBackground(Color.decode("#F5F6F7"));
        pnlOutput.setBorder(BorderFactory.createTitledBorder("Mật mã (Bản Mã Base64)"));
        txtBanMa = new JTextArea();
        txtBanMa.setLineWrap(true);
        txtBanMa.setWrapStyleWord(true);
        txtBanMa.setFont(new Font("Consolas", Font.PLAIN, 14));
        txtBanMa.setForeground(Color.decode("#2980b9")); 
        pnlOutput.add(new JScrollPane(txtBanMa), BorderLayout.CENTER);

        pnlCenter.add(pnlInput);
        pnlCenter.add(pnlOutput);
        pnlRight.add(pnlCenter, BorderLayout.CENTER);

        add(pnlLeft, BorderLayout.WEST);
        add(pnlRight, BorderLayout.CENTER);

        // ==========================================
        // 3. CÀI ĐẶT SỰ KIỆN CHO CÁC NÚT
        // ==========================================
        btnSinhKhoa.addActionListener(e -> {
            try {
                SecretKeySpec key = AESService.createRandomKey();
                txtKhoa.setText(AESService.bytesToBase64(key.getEncoded()));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi sinh khóa hệ thống!");
            }
        });

        btnMoFile.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Chọn File (Hỗ trợ TXT, DOCX, PDF)");
            javax.swing.filechooser.FileNameExtensionFilter filter = new javax.swing.filechooser.FileNameExtensionFilter(
                "Các file văn bản (txt, docx, pdf)", "txt", "docx", "pdf");
            chooser.setFileFilter(filter);

            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    String path = chooser.getSelectedFile().getAbsolutePath();
                    String content = AESService.readFromFile(path);
                    
                    String[] options = {"Nạp vào Bản Rõ", "Nạp vào Bản Mã", "Nạp vào Khóa"};
                    int choice = JOptionPane.showOptionDialog(this, 
                            "Đọc file thành công! Bạn muốn tải dữ liệu vào đâu?", 
                            "Chọn vị trí", JOptionPane.DEFAULT_OPTION, 
                            JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                            
                    if (choice == 0) { 
                        txtBanRo.setText(content); 
                    } else if (choice == 1) { 
                        txtBanMa.setText(content);
                        lastCipher = content;
                    } else if (choice == 2) {
                        txtKhoa.setText(content);
                        lastKey = content;
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi Đọc File", JOptionPane.ERROR_MESSAGE);
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
                
                JOptionPane.showMessageDialog(this, "MÃ HÓA DỮ LIỆU THÀNH CÔNG!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi Khóa / Mã Hóa", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnGiaiMa.addActionListener(e -> xuLyGiaiMaVaBatLoi());

        btnLuuFile.addActionListener(e -> {
            if(txtKhoa.getText().isEmpty() && txtBanRo.getText().isEmpty() && txtBanMa.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không có dữ liệu để lưu!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String[] options = {"Lưu Khóa", "Lưu Bản Rõ", "Lưu Bản Mã"};
            int choice = JOptionPane.showOptionDialog(this, 
                    "Chọn dữ liệu cần xuất ra File:", 
                    "Lưu File", JOptionPane.DEFAULT_OPTION, 
                    JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
            
            if (choice == -1) return;

            String content = "";
            String titleStr = "";
            if (choice == 0) { content = txtKhoa.getText(); titleStr = "Lưu File Khóa"; }
            else if (choice == 1) { content = txtBanRo.getText(); titleStr = "Lưu File Bản Rõ"; }
            else if (choice == 2) { content = txtBanMa.getText(); titleStr = "Lưu File Bản Mã"; }

            if (content.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ô dữ liệu bạn chọn đang trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle(titleStr);

            // Thêm các bộ lọc hiển thị ở thanh Save As Type
            javax.swing.filechooser.FileNameExtensionFilter txtFilter = new javax.swing.filechooser.FileNameExtensionFilter("Text Document (*.txt)", "txt");
            javax.swing.filechooser.FileNameExtensionFilter docxFilter = new javax.swing.filechooser.FileNameExtensionFilter("Word Document (*.docx)", "docx");
            javax.swing.filechooser.FileNameExtensionFilter pdfFilter = new javax.swing.filechooser.FileNameExtensionFilter("PDF Document (*.pdf)", "pdf");

            chooser.addChoosableFileFilter(txtFilter);
            chooser.addChoosableFileFilter(docxFilter);
            chooser.addChoosableFileFilter(pdfFilter);
            chooser.setFileFilter(txtFilter); // Mặc định là txt
            chooser.setAcceptAllFileFilterUsed(false);

            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    String path = chooser.getSelectedFile().getAbsolutePath();
                    javax.swing.filechooser.FileFilter selectedFilter = chooser.getFileFilter();

                    // Tự động gắn đuôi file tương ứng
                    if (selectedFilter == txtFilter && !path.toLowerCase().endsWith(".txt")) path += ".txt";
                    else if (selectedFilter == docxFilter && !path.toLowerCase().endsWith(".docx")) path += ".docx";
                    else if (selectedFilter == pdfFilter && !path.toLowerCase().endsWith(".pdf")) path += ".pdf";
                    else if (!path.contains(".")) path += ".txt"; // Fallback an toàn

                    AESService.saveToFile(path, content);
                    
                    if (selectedFilter == pdfFilter) {
                        JOptionPane.showMessageDialog(this, "Đã lưu tại:\n" + path + "\n(Lưu ý: Đây là PDF dạng thô. Để hiển thị chuẩn bằng Foxit/Adobe cần cài thêm PDFBox)", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Đã lưu thành công tại:\n" + path, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Xảy ra lỗi khi lưu file!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnLamMoi.addActionListener(e -> {
            txtKhoa.setText(""); txtBanRo.setText(""); txtBanMa.setText("");
            lastKey = ""; lastCipher = ""; txtKhoa.requestFocus();
        });
    }

    // ==========================================
    // 4. LOGIC BẮT LỖI BẢO MẬT (ĐÃ FIX THEO LỜI THẦY)
    // ==========================================
    private void xuLyGiaiMaVaBatLoi() {                                         
        String strKey = txtKhoa.getText().trim();
        String strCipher = txtBanMa.getText().trim();
        
        if(strKey.isEmpty() || strCipher.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập đủ Khóa và Bản Mã!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // BƯỚC 1: KIỂM TRA LƯU VẾT TRƯỚC TIÊN (Bắt lỗi cố tình sửa bậy Khóa tự động/Bản mã)
        boolean khoaBiSua = false;
        boolean banMaBiSua = false;

        if (!lastKey.isEmpty() && !strKey.equals(lastKey)) { khoaBiSua = true; }
        if (!lastCipher.isEmpty() && !strCipher.equals(lastCipher)) { banMaBiSua = true; }

        if (khoaBiSua || banMaBiSua) {
            hienThiLoi(khoaBiSua, banMaBiSua);
            return; 
        }

        // BƯỚC 2: NẾU NHẬP MỚI THỦ CÔNG -> KIỂM TRA ĐỘ DÀI KHÓA XEM CÓ ĐỦ 16/24/32 BYTE KHÔNG
        SecretKeySpec secretKey = null;
        try { 
            secretKey = AESService.createKeyFromText(strKey); 
        } catch (Exception e) { 
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi Khóa", JOptionPane.ERROR_MESSAGE);
            return; 
        }

        // BƯỚC 3: KIỂM TRA CHUẨN BASE64 CỦA BẢN MÃ
        byte[] cipherBytes;
        try { 
            cipherBytes = AESService.base64ToBytes(strCipher); 
        } catch (IllegalArgumentException e) { 
            JOptionPane.showMessageDialog(this, "LỖI: Bản mã không đúng chuẩn định dạng Base64!", "Lỗi Bản Mã", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // BƯỚC 4: THỰC HIỆN GIẢI MÃ LÕI
        try {
            String plaintext = AESService.decrypt(cipherBytes, secretKey);
            txtBanRo.setText(plaintext);
            
            lastKey = strKey;
            lastCipher = strCipher;
            JOptionPane.showMessageDialog(this, "GIẢI MÃ DỮ LIỆU THÀNH CÔNG!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (javax.crypto.BadPaddingException | javax.crypto.IllegalBlockSizeException e) {
            JOptionPane.showMessageDialog(this, "LỖI TOÀN VẸN: Khóa sai hoặc Bản Mã đã bị hỏng trong quá trình truyền tải!", "Lỗi Giải Mã", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void hienThiLoi(boolean khoaBiSua, boolean banMaBiSua) {
        if (khoaBiSua && banMaBiSua) {
            JOptionPane.showMessageDialog(this, "NGUY HIỂM: Cả KHÓA và BẢN MÃ đều đã bị chỉnh sửa bậy bạ!", "Lỗi Kép", JOptionPane.ERROR_MESSAGE);
        } else if (khoaBiSua) {
            JOptionPane.showMessageDialog(this, "LỖI TOÀN VẸN: KHÓA giải mã đã bị thay đổi cấu trúc hoặc bị can thiệp!", "Cảnh báo Khóa", JOptionPane.WARNING_MESSAGE);
        } else if (banMaBiSua) {
            JOptionPane.showMessageDialog(this, "LỖI TOÀN VẸN: BẢN MÃ đã bị can thiệp sửa đổi nội dung, không thể khôi phục!", "Cảnh báo Bản Mã", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
        java.awt.EventQueue.invokeLater(() -> new MainForm().setVisible(true));
    }
}