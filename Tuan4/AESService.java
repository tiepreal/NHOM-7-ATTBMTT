package Tuan4;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.io.File;
import java.io.FileOutputStream;

public class AESService {   

    // 1. TOAN HOC CO SO VA BANG S-BOX
    public static byte galoisMultiply(byte a, byte b) {
        byte p = 0;
        for (int i = 0; i < 8; i++) {
            if ((b & 1) != 0) { p ^= a; }
            boolean hiBitSet = (a & 0x80) != 0;
            a <<= 1;
            if (hiBitSet) { a ^= 0x1B; }
            b >>= 1;
        }
        return p;
    }

    private static final int[] S_BOX = {
        0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76,
        0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0,
        0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15,
        0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a, 0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75,
        0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0, 0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84,
        0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b, 0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf,
        0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8,
        0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2,
        0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73,
        0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb,
        0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79,
        0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08,
        0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a,
        0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e,
        0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf,
        0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16
    };

    public static byte subByte(byte b) { return (byte) S_BOX[b & 0xFF]; }

    // 2. CHUAN HOA VA QUAN LY KHOA (CAP NHAT BAT LOI 16 BYTE)
    public static String bytesToBase64(byte[] bytes) { 
        return Base64.getEncoder().encodeToString(bytes); 
    }
    public static byte[] base64ToBytes(String base64Str) { 
        return Base64.getDecoder().decode(base64Str); 
    }

    public static SecretKeySpec createKeyFromText(String textKey) throws Exception {
        byte[] keyBytes;
        try {
            // Thu doc theo chuan Base64 truoc (Dung khi load file hoac sinh tu dong)
            keyBytes = Base64.getDecoder().decode(textKey);
        } catch (IllegalArgumentException e) {
            // Neu la chuoi go tay binh thuong
            keyBytes = textKey.getBytes("UTF-8");
        }
        
        if (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32) {
            throw new Exception("Kích thước Khóa không hợp lệ!\n Khóa AES phải dài 16, 24 hoặc 32 byte / ký tự.\n(Hiện tại bản đăng nhập: " + keyBytes.length + " byte)");
        }
        return new SecretKeySpec(keyBytes, "AES");
    }

    public static SecretKeySpec createRandomKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128); // Sinh khoa AES-128
        return new SecretKeySpec(keyGen.generateKey().getEncoded(), "AES");
    }

  
    // 3. MA HOA VA GIAI MA LOI
  
    public static byte[] encrypt(String plaintext, SecretKeySpec key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(plaintext.getBytes("UTF-8"));
    }

    public static String decrypt(byte[] ciphertextBytes, SecretKeySpec key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(ciphertextBytes), "UTF-8");
    }
  
    public static void saveToFile(String filePath, String content) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(new File(filePath))) {
            fos.write(content.getBytes("UTF-8"));
            fos.flush();
        }
    }

    public static String readFromFile(String filePath) throws Exception {
        File file = new File(filePath);
        String lowerPath = filePath.toLowerCase();

        if (lowerPath.endsWith(".txt")) {
            byte[] bytes = java.nio.file.Files.readAllBytes(file.toPath());
            return new String(bytes, "UTF-8");
        } 
        else if (lowerPath.endsWith(".docx")) {
            StringBuilder sb = new StringBuilder();
            try (java.util.zip.ZipInputStream zis = new java.util.zip.ZipInputStream(new java.io.FileInputStream(file))) {
                java.util.zip.ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    if (entry.getName().equals("word/document.xml")) {
                        java.util.Scanner scanner = new java.util.Scanner(zis, "UTF-8");
                        while (scanner.hasNextLine()) {
                            String line = scanner.nextLine();
                            line = line.replaceAll("<w:p[^>]*>", "\n"); // Xuong dong
                            line = line.replaceAll("<[^>]+>", ""); // Xoa the XML
                            sb.append(line);
                        }
                        scanner.close();
                        break;
                    }
                }
            }
            return sb.toString().trim();
        } 
        else if (lowerPath.endsWith(".pdf")) {
            throw new Exception("De doc file PDF can cai dat thu vien Apache PDFBox!");
        }
        else {
            throw new Exception("Dinh dang file khong duoc ho tro!");
        }
    }
}