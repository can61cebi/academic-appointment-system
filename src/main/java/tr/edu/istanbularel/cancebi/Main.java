package tr.edu.istanbularel.cancebi;

import com.formdev.flatlaf.FlatLightLaf;
import tr.edu.istanbularel.cancebi.gui.GirisEkrani;
import tr.edu.istanbularel.cancebi.veritabani.VeritabaniYoneticisi;

import javax.swing.*;
import java.awt.*;

/**
 * Randevu Sistemi uygulamasının ana sınıfı
 */
public class Main {
    public static void main(String[] args) {
        // Modern FlatLaf temasını ayarla
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("FlatLaf teması yüklenemedi: " + e.getMessage());
            // Varsayılan tema ile devam et
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        // Veritabanı bağlantısını kontrol et
        boolean baglanti = VeritabaniYoneticisi.getOrnek().baglantiKontrol();
        
        if (!baglanti) {
            JOptionPane.showMessageDialog(null, 
                "Veritabanına bağlanılamadı. Lütfen ağ bağlantınızı kontrol edin.",
                "Bağlantı Hatası", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        // Giriş ekranını başlat
        SwingUtilities.invokeLater(() -> {
            GirisEkrani girisEkrani = new GirisEkrani();
            girisEkrani.setVisible(true);
        });
    }
}