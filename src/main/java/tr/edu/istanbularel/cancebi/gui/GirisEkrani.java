package tr.edu.istanbularel.cancebi.gui;

import tr.edu.istanbularel.cancebi.dao.KullaniciDAO;
import tr.edu.istanbularel.cancebi.model.Kullanici;
import tr.edu.istanbularel.cancebi.model.Ogrenci;
import tr.edu.istanbularel.cancebi.model.OgretimUyesi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kullanıcı giriş ekranı
 */
public class GirisEkrani extends JFrame implements ActionListener {
    private JTextField kullaniciAdiField;
    private JPasswordField sifreField;
    private JButton girisButton;
    private JButton kayitButton;
    private JLabel durumLabel;
    private KullaniciDAO kullaniciDAO;
    
    public GirisEkrani() {
        super("Akademik Randevu Sistemi - Giriş");
        kullaniciDAO = new KullaniciDAO();
        
        // UI bileşenlerini oluştur
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Logo veya başlık
        JLabel baslikLabel = new JLabel("Akademik Randevu Sistemi", SwingConstants.CENTER);
        baslikLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(baslikLabel, gbc);
        
        // Alt başlık
        JLabel altBaslikLabel = new JLabel("İstanbul Arel Üniversitesi", SwingConstants.CENTER);
        altBaslikLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(altBaslikLabel, gbc);
        
        // Boşluk
        gbc.gridy = 2;
        panel.add(Box.createVerticalStrut(20), gbc);
        
        // Kullanıcı adı
        gbc.gridwidth = 1;
        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Kullanıcı Adı:"), gbc);
        
        kullaniciAdiField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(kullaniciAdiField, gbc);
        
        // Şifre
        gbc.gridy = 4;
        gbc.gridx = 0;
        panel.add(new JLabel("Şifre:"), gbc);
        
        sifreField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(sifreField, gbc);
        
        // Giriş butonu
        girisButton = new JButton("Giriş Yap");
        girisButton.addActionListener(this);
        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(girisButton, gbc);
        
        // Kayıt butonu
        kayitButton = new JButton("Öğrenci Kaydı Oluştur");
        kayitButton.addActionListener(this);
        gbc.gridy = 6;
        panel.add(kayitButton, gbc);
        
        // Durum mesajı
        durumLabel = new JLabel("", SwingConstants.CENTER);
        durumLabel.setForeground(Color.RED);
        gbc.gridy = 7;
        panel.add(durumLabel, gbc);
        
        add(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == girisButton) {
            girisYap();
        } else if (e.getSource() == kayitButton) {
            kayitEkraniGoster();
        }
    }
    
    /**
     * Kullanıcı girişi işlemi
     */
    private void girisYap() {
        String kullaniciAdi = kullaniciAdiField.getText();
        String sifre = new String(sifreField.getPassword());
        
        if (kullaniciAdi.isEmpty() || sifre.isEmpty()) {
            durumLabel.setText("Kullanıcı adı ve şifre boş olamaz!");
            return;
        }
        
        // Normal kullanıcı girişi
        Kullanici kullanici = kullaniciDAO.girisYap(kullaniciAdi, sifre);
        
        if (kullanici != null) {
            durumLabel.setText("Giriş başarılı! Yönlendiriliyor...");
            
            // Kullanıcı tipine göre panel açma
            this.dispose();
            switch (kullanici.getKullaniciTipi()) {
                case "ogrenci":
                    Ogrenci ogrenci = kullaniciDAO.ogrenciGetir(kullanici.getId());
                    SwingUtilities.invokeLater(() -> new OgrenciPaneli(ogrenci).setVisible(true));
                    break;
                case "ogretim_uyesi":
                    OgretimUyesi ogretimUyesi = kullaniciDAO.ogretimUyesiGetir(kullanici.getId());
                    SwingUtilities.invokeLater(() -> new OgretimUyesiPaneli(ogretimUyesi).setVisible(true));
                    break;
                case "yetkili":
                    SwingUtilities.invokeLater(() -> new YetkiliPaneli().setVisible(true));
                    break;
            }
        } else {
            durumLabel.setText("Kullanıcı adı veya şifre hatalı!");
        }
    }
    
    /**
     * Kayıt ekranını gösterir
     */
    private void kayitEkraniGoster() {
        this.setVisible(false);
        new KayitEkrani(this).setVisible(true);
    }
}