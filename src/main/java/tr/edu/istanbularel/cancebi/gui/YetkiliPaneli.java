package tr.edu.istanbularel.cancebi.gui;

import tr.edu.istanbularel.cancebi.dao.KullaniciDAO;
import tr.edu.istanbularel.cancebi.model.OgretimUyesi;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Yetkili işlemlerinin yapıldığı panel
 */
public class YetkiliPaneli extends JFrame implements ActionListener {
    private JTabbedPane tabbedPane;
    
    // Öğretim üyesi ekleme
    private JTextField adField;
    private JTextField soyadField;
    private JTextField kullaniciAdiField;
    private JTextField emailField;
    private JComboBox<String> unvanComboBox;
    private JTextField bolumField;
    private JTextField ofisField;
    private JPasswordField sifreField;
    private JPasswordField sifreTekrarField;
    private JButton kaydetButton;
    private JLabel durumLabel;
    
    // Öğretim üyeleri listesi
    private JTable ogretimUyesiTablo;
    private DefaultTableModel ogretimUyesiModel;
    
    private KullaniciDAO kullaniciDAO;
    
    public YetkiliPaneli() {
        super("Akademik Randevu Sistemi - Yetkili Paneli");
        kullaniciDAO = new KullaniciDAO();
        
        // Ana paneli oluştur
        tabbedPane = new JTabbedPane();
        
        // Öğretim üyesi ekleme sekmesi
        JPanel eklePanel = eklePaneliHazirla();
        tabbedPane.addTab("Öğretim Üyesi Ekle", eklePanel);
        
        // Öğretim üyeleri listesi sekmesi
        JPanel listePanel = listePaneliHazirla();
        tabbedPane.addTab("Öğretim Üyeleri", listePanel);
        
        // Ana pencereye ekle
        add(tabbedPane);
        
        // Öğretim üyelerini yükle
        ogretimUyeleriniYukle();
        
        // Pencere ayarları
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }
    
    /**
     * Öğretim üyesi ekleme panelini hazırlar
     * @return Ekleme paneli
     */
    private JPanel eklePaneliHazirla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Form paneli
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Başlık
        JLabel baslikLabel = new JLabel("Yeni Öğretim Üyesi Kaydı", SwingConstants.CENTER);
        baslikLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(baslikLabel, gbc);
        
        // Form alanları
        gbc.gridwidth = 1;
        int row = 1;
        
        // Unvan
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Unvan:"), gbc);
        
        String[] unvanlar = {"Prof. Dr.", "Doç. Dr.", "Dr. Öğr. Üyesi", "Öğr. Gör.", "Arş. Gör."};
        unvanComboBox = new JComboBox<>(unvanlar);
        gbc.gridx = 1;
        formPanel.add(unvanComboBox, gbc);
        row++;
        
        // Ad
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Ad:"), gbc);
        
        adField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(adField, gbc);
        row++;
        
        // Soyad
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Soyad:"), gbc);
        
        soyadField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(soyadField, gbc);
        row++;
        
        // Kullanıcı adı
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Kullanıcı Adı:"), gbc);
        
        kullaniciAdiField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(kullaniciAdiField, gbc);
        row++;
        
        // E-posta
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("E-posta:"), gbc);
        
        emailField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);
        row++;
        
        // Bölüm
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Bölüm:"), gbc);
        
        bolumField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(bolumField, gbc);
        row++;
        
        // Ofis
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Ofis (opsiyonel):"), gbc);
        
        ofisField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(ofisField, gbc);
        row++;
        
        // Şifre
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Şifre:"), gbc);
        
        sifreField = new JPasswordField(20);
        gbc.gridx = 1;
        formPanel.add(sifreField, gbc);
        row++;
        
        // Şifre tekrar
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Şifre Tekrar:"), gbc);
        
        sifreTekrarField = new JPasswordField(20);
        gbc.gridx = 1;
        formPanel.add(sifreTekrarField, gbc);
        row++;
        
        // Durum mesajı
        durumLabel = new JLabel("", SwingConstants.CENTER);
        durumLabel.setForeground(Color.RED);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        formPanel.add(durumLabel, gbc);
        row++;
        
        // Kaydet butonu
        kaydetButton = new JButton("Öğretim Üyesi Kaydet");
        kaydetButton.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(kaydetButton, gbc);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        // Alt panel - çıkış butonu
        JPanel altPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cikisButton = new JButton("Çıkış Yap");
        cikisButton.addActionListener(e -> cikisYap());
        altPanel.add(cikisButton);
        
        panel.add(altPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Öğretim üyeleri listesi panelini hazırlar
     * @return Liste paneli
     */
    private JPanel listePaneliHazirla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Tablo modeli
        String[] columnNames = {"ID", "Unvan", "Ad", "Soyad", "Kullanıcı Adı", "E-posta", "Bölüm", "Ofis"};
        ogretimUyesiModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Düzenleme kapalı
            }
        };
        
        ogretimUyesiTablo = new JTable(ogretimUyesiModel);
        
        JScrollPane scrollPane = new JScrollPane(ogretimUyesiTablo);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buton paneli
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton yenileButton = new JButton("Listeyi Yenile");
        yenileButton.addActionListener(e -> ogretimUyeleriniYukle());
        buttonPanel.add(yenileButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Öğretim üyelerini tabloya yükler
     */
    private void ogretimUyeleriniYukle() {
        // Tabloyu temizle
        ogretimUyesiModel.setRowCount(0);
        
        // Öğretim üyelerini getir
        List<OgretimUyesi> ogretimUyeleri = kullaniciDAO.tumOgretimUyeleriniGetir();
        
        // Tabloya ekle
        for (OgretimUyesi ogretimUyesi : ogretimUyeleri) {
            Object[] row = {
                ogretimUyesi.getId(),
                ogretimUyesi.getUnvan(),
                ogretimUyesi.getAd(),
                ogretimUyesi.getSoyad(),
                ogretimUyesi.getKullaniciAdi(),
                ogretimUyesi.getEmail(),
                ogretimUyesi.getBolum(),
                ogretimUyesi.getOfis() != null ? ogretimUyesi.getOfis() : "-"
            };
            
            ogretimUyesiModel.addRow(row);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == kaydetButton) {
            ogretimUyesiKaydet();
        }
    }
    
    /**
     * Öğretim üyesi kaydeder
     */
    private void ogretimUyesiKaydet() {
        // Form kontrolü
        String unvan = (String) unvanComboBox.getSelectedItem();
        String ad = adField.getText().trim();
        String soyad = soyadField.getText().trim();
        String kullaniciAdi = kullaniciAdiField.getText().trim();
        String email = emailField.getText().trim();
        String bolum = bolumField.getText().trim();
        String ofis = ofisField.getText().trim();
        String sifre = new String(sifreField.getPassword());
        String sifreTekrar = new String(sifreTekrarField.getPassword());
        
        // Boş alan kontrolü
        if (ad.isEmpty() || soyad.isEmpty() || kullaniciAdi.isEmpty() || 
            email.isEmpty() || bolum.isEmpty() || sifre.isEmpty() || sifreTekrar.isEmpty()) {
            durumLabel.setText("Zorunlu alanlar boş bırakılamaz!");
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
        
        // Öğretim üyesi nesnesini oluştur
        OgretimUyesi ogretimUyesi = new OgretimUyesi();
        ogretimUyesi.setUnvan(unvan);
        ogretimUyesi.setAd(ad);
        ogretimUyesi.setSoyad(soyad);
        ogretimUyesi.setKullaniciAdi(kullaniciAdi);
        ogretimUyesi.setEmail(email);
        ogretimUyesi.setBolum(bolum);
        ogretimUyesi.setOfis(ofis.isEmpty() ? null : ofis);
        
        // Veritabanına kaydet
        boolean kayitBasarili = kullaniciDAO.ogretimUyesiKaydet(ogretimUyesi, sifre);
        
        if (kayitBasarili) {
            JOptionPane.showMessageDialog(this,
                "Öğretim üyesi başarıyla kaydedildi!",
                "Kayıt Başarılı",
                JOptionPane.INFORMATION_MESSAGE);
                
            // Formu temizle
            adField.setText("");
            soyadField.setText("");
            kullaniciAdiField.setText("");
            emailField.setText("");
            bolumField.setText("");
            ofisField.setText("");
            sifreField.setText("");
            sifreTekrarField.setText("");
            durumLabel.setText("");
            
            // Öğretim üyeleri listesini güncelle
            ogretimUyeleriniYukle();
            
            // Listeye geç
            tabbedPane.setSelectedIndex(1);
        } else {
            durumLabel.setText("Kayıt sırasında bir hata oluştu!");
        }
    }
    
    /**
     * Çıkış yapar
     */
    private void cikisYap() {
        int cevap = JOptionPane.showConfirmDialog(this,
            "Çıkış yapmak istediğinize emin misiniz?",
            "Çıkış Onayı",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
            
        if (cevap == JOptionPane.YES_OPTION) {
            this.dispose();
            new GirisEkrani().setVisible(true);
        }
    }
}