package tr.edu.istanbularel.cancebi.gui;

import tr.edu.istanbularel.cancebi.dao.KullaniciDAO;
import tr.edu.istanbularel.cancebi.model.Ogrenci;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Öğrenci kayıt ekranı
 */
public class KayitEkrani extends JFrame implements ActionListener {
    private JTextField adField;
    private JTextField soyadField;
    private JTextField kullaniciAdiField;
    private JTextField emailField;
    private JTextField ogrenciNoField;
    private JTextField bolumField;
    private JPasswordField sifreField;
    private JPasswordField sifreTekrarField;
    private JButton kaydetButton;
    private JButton iptalButton;
    private JLabel durumLabel;
    
    private KullaniciDAO kullaniciDAO;
    private GirisEkrani girisEkrani;
    
    public KayitEkrani(GirisEkrani girisEkrani) {
        super("Öğrenci Kayıt Ekranı");
        this.girisEkrani = girisEkrani;
        kullaniciDAO = new KullaniciDAO();
        
        // UI bileşenlerini oluştur
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Başlık
        JLabel baslikLabel = new JLabel("Öğrenci Kayıt Formu", SwingConstants.CENTER);
        baslikLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(baslikLabel, gbc);
        
        // Boşluk
        gbc.gridy = 1;
        panel.add(Box.createVerticalStrut(10), gbc);
        
        // Form alanları
        gbc.gridwidth = 1;
        
        // Ad
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Ad:"), gbc);
        
        adField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(adField, gbc);
        
        // Soyad
        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Soyad:"), gbc);
        
        soyadField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(soyadField, gbc);
        
        // Kullanıcı adı
        gbc.gridy = 4;
        gbc.gridx = 0;
        panel.add(new JLabel("Kullanıcı Adı:"), gbc);
        
        kullaniciAdiField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(kullaniciAdiField, gbc);
        
        // E-posta
        gbc.gridy = 5;
        gbc.gridx = 0;
        panel.add(new JLabel("E-posta:"), gbc);
        
        emailField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(emailField, gbc);
        
        // Öğrenci no
        gbc.gridy = 6;
        gbc.gridx = 0;
        panel.add(new JLabel("Öğrenci No:"), gbc);
        
        ogrenciNoField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(ogrenciNoField, gbc);
        
        // Bölüm
        gbc.gridy = 7;
        gbc.gridx = 0;
        panel.add(new JLabel("Bölüm:"), gbc);
        
        bolumField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(bolumField, gbc);
        
	    // Şifre
        gbc.gridy = 8;
        gbc.gridx = 0;
        panel.add(new JLabel("Şifre:"), gbc);
        
        sifreField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(sifreField, gbc);
        
        // Şifre tekrar
        gbc.gridy = 9;
        gbc.gridx = 0;
        panel.add(new JLabel("Şifre Tekrar:"), gbc);
        
        sifreTekrarField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(sifreTekrarField, gbc);
        
        // Durum mesajı
        durumLabel = new JLabel("", SwingConstants.CENTER);
        durumLabel.setForeground(Color.RED);
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        panel.add(durumLabel, gbc);
        
        // Butonlar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        kaydetButton = new JButton("Kaydet");
        kaydetButton.addActionListener(this);
        iptalButton = new JButton("İptal");
        iptalButton.addActionListener(this);
        
        buttonPanel.add(kaydetButton);
        buttonPanel.add(iptalButton);
        
        gbc.gridy = 11;
        panel.add(buttonPanel, gbc);
        
        add(panel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == kaydetButton) {
            ogrenciKaydet();
        } else if (e.getSource() == iptalButton) {
            girisEkraninaGeriDon();
        }
    }
    
    /**
     * Öğrenci kaydı yapar
     */
    private void ogrenciKaydet() {
        // Form kontrolü
        String ad = adField.getText().trim();
        String soyad = soyadField.getText().trim();
        String kullaniciAdi = kullaniciAdiField.getText().trim();
        String email = emailField.getText().trim();
        String ogrenciNo = ogrenciNoField.getText().trim();
        String bolum = bolumField.getText().trim();
        String sifre = new String(sifreField.getPassword());
        String sifreTekrar = new String(sifreTekrarField.getPassword());
        
        // Boş alan kontrolü
        if (ad.isEmpty() || soyad.isEmpty() || kullaniciAdi.isEmpty() || email.isEmpty() ||
            ogrenciNo.isEmpty() || bolum.isEmpty() || sifre.isEmpty() || sifreTekrar.isEmpty()) {
            durumLabel.setText("Tüm alanlar doldurulmalıdır!");
            return;
        }
        
        // E-posta formatı kontrolü
        if (!email.matches(".+@.+\\..+")) {
            durumLabel.setText("Geçerli bir e-posta adresi giriniz!");
            return;
        }
        
        // Şifre uzunluğu kontrolü
        if (sifre.length() < 6) {
            durumLabel.setText("Şifre en az 6 karakter olmalıdır!");
            return;
        }
        
        // Şifre eşleşme kontrolü
        if (!sifre.equals(sifreTekrar)) {
            durumLabel.setText("Şifreler eşleşmiyor!");
            return;
        }
        
        // Kullanıcı adı benzersizlik kontrolü
        if (kullaniciDAO.kullaniciAdiVarMi(kullaniciAdi)) {
            durumLabel.setText("Bu kullanıcı adı zaten kullanılıyor!");
            return;
        }
        
        // E-posta benzersizlik kontrolü
        if (kullaniciDAO.emailVarMi(email)) {
            durumLabel.setText("Bu e-posta adresi zaten kullanılıyor!");
            return;
        }
        
        // Öğrenci numarası benzersizlik kontrolü
        if (kullaniciDAO.ogrenciNoVarMi(ogrenciNo)) {
            durumLabel.setText("Bu öğrenci numarası zaten kayıtlı!");
            return;
        }
        
        // Öğrenci nesnesini oluştur
        Ogrenci ogrenci = new Ogrenci();
        ogrenci.setAd(ad);
        ogrenci.setSoyad(soyad);
        ogrenci.setKullaniciAdi(kullaniciAdi);
        ogrenci.setEmail(email);
        ogrenci.setOgrenciNo(ogrenciNo);
        ogrenci.setBolum(bolum);
        
        // Veritabanına kaydet
        boolean kayitBasarili = kullaniciDAO.ogrenciKaydet(ogrenci, sifre);
        
        if (kayitBasarili) {
            JOptionPane.showMessageDialog(this,
                "Kayıt başarıyla tamamlandı!\nKullanıcı adı ve şifrenizle giriş yapabilirsiniz.",
                "Kayıt Başarılı",
                JOptionPane.INFORMATION_MESSAGE);
            girisEkraninaGeriDon();
        } else {
            durumLabel.setText("Kayıt sırasında bir hata oluştu!");
        }
    }
    
    /**
     * Giriş ekranına geri döner
     */
    private void girisEkraninaGeriDon() {
        this.dispose();
        girisEkrani.setVisible(true);
    }
}