package tr.edu.istanbularel.cancebi.gui;

import com.toedter.calendar.JDateChooser;
import tr.edu.istanbularel.cancebi.dao.MusaitlikDAO;
import tr.edu.istanbularel.cancebi.dao.RandevuDAO;
import tr.edu.istanbularel.cancebi.model.Musaitlik;
import tr.edu.istanbularel.cancebi.model.OgretimUyesi;
import tr.edu.istanbularel.cancebi.model.Randevu;
import tr.edu.istanbularel.cancebi.util.TarihSaatUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Öğretim üyesi işlemlerinin yapıldığı panel
 */
public class OgretimUyesiPaneli extends JFrame implements ActionListener {
    private OgretimUyesi ogretimUyesi;
    private JTabbedPane tabbedPane;
    
    // Randevu sekmesi
    private JTable randevuTablo;
    private DefaultTableModel randevuModel;
    private JButton onaylaButton;
    private JButton reddetButton;
    
    // Müsaitlik sekmesi
    private JComboBox<String> gunComboBox;
    private JSpinner baslangicSpinner;
    private JSpinner bitisSpinner;
    private JButton ekleButton;
    private JTable musaitlikTablo;
    private DefaultTableModel musaitlikModel;
    private JButton silButton;
    
    // Takvim sekmesi
    private JDateChooser takvimSecici;
    private JTextArea gunlukRandevularArea;
    
    private RandevuDAO randevuDAO;
    private MusaitlikDAO musaitlikDAO;
    
    public OgretimUyesiPaneli(OgretimUyesi ogretimUyesi) {
        super("Öğretim Üyesi Paneli - " + ogretimUyesi.getTamAd());
        this.ogretimUyesi = ogretimUyesi;
        
        randevuDAO = new RandevuDAO();
        musaitlikDAO = new MusaitlikDAO();
        
        // Ana paneli oluştur
        tabbedPane = new JTabbedPane();
        
        // Randevu sekmesi
        JPanel randevuPanel = randevuPaneliHazirla();
        tabbedPane.addTab("Randevu Talepleri", randevuPanel);
        
        // Müsaitlik sekmesi
        JPanel musaitlikPanel = musaitlikPaneliHazirla();
        tabbedPane.addTab("Müsaitlik Ayarları", musaitlikPanel);
        
        // Takvim sekmesi
        JPanel takvimPanel = takvimPaneliHazirla();
        tabbedPane.addTab("Takvim", takvimPanel);
        
        // Profil sekmesi
        JPanel profilPanel = profilPaneliHazirla();
        tabbedPane.addTab("Profil", profilPanel);
        
        // Ana pencereye ekle
        add(tabbedPane);
        
        // Verileri yükle
        randevulariYukle();
        musaitlikleriYukle();
        
        // Pencere ayarları
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
    }
    
    /**
     * Randevu panelini hazırlar
     * @return Randevu paneli
     */
    private JPanel randevuPaneliHazirla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Tablo modeli
        String[] columnNames = {"ID", "Öğrenci No", "Öğrenci", "Tarih", "Saat", "Konu", "Durum", "Notlar"};
        randevuModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Düzenleme kapalı
            }
        };
        
        randevuTablo = new JTable(randevuModel);
        randevuTablo.getColumnModel().getColumn(0).setMaxWidth(40); // ID sütunu küçük
        randevuTablo.getColumnModel().getColumn(1).setMaxWidth(100); // Öğrenci No sütunu
        randevuTablo.getColumnModel().getColumn(6).setMaxWidth(100); // Durum sütunu
        
        JScrollPane scrollPane = new JScrollPane(randevuTablo);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buton paneli
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        onaylaButton = new JButton("Randevuyu Onayla");
        onaylaButton.addActionListener(this);
        buttonPanel.add(onaylaButton);
        
        reddetButton = new JButton("Randevuyu Reddet");
        reddetButton.addActionListener(this);
        buttonPanel.add(reddetButton);
        
        JButton yenileButton = new JButton("Yenile");
        yenileButton.addActionListener(e -> randevulariYukle());
        buttonPanel.add(yenileButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Müsaitlik panelini hazırlar
     * @return Müsaitlik paneli
     */
    private JPanel musaitlikPaneliHazirla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Üst panel (ekleme formu)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Yeni Müsaitlik Ekle"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Gün seçimi
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Gün:"), gbc);
        
        String[] gunler = {"Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma"};
        gunComboBox = new JComboBox<>(gunler);
        gbc.gridx = 1;
        formPanel.add(gunComboBox, gbc);
        
        // Başlangıç saati
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Başlangıç Saati:"), gbc);
        
        SpinnerDateModel baslangicModel = new SpinnerDateModel();
        baslangicSpinner = new JSpinner(baslangicModel);
        JSpinner.DateEditor baslangicEditor = new JSpinner.DateEditor(baslangicSpinner, "HH:mm");
        baslangicSpinner.setEditor(baslangicEditor);
        gbc.gridx = 1;
        formPanel.add(baslangicSpinner, gbc);
        
        // Bitiş saati
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Bitiş Saati:"), gbc);
        
        SpinnerDateModel bitisModel = new SpinnerDateModel();
        bitisSpinner = new JSpinner(bitisModel);
        JSpinner.DateEditor bitisEditor = new JSpinner.DateEditor(bitisSpinner, "HH:mm");
        bitisSpinner.setEditor(bitisEditor);
        gbc.gridx = 1;
        formPanel.add(bitisSpinner, gbc);
        
        // Ekle butonu
        ekleButton = new JButton("Müsaitlik Ekle");
        ekleButton.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(ekleButton, gbc);
        
        panel.add(formPanel, BorderLayout.NORTH);
        
        // Orta panel (müsaitlik tablosu)
        String[] columnNames = {"ID", "Gün", "Başlangıç", "Bitiş"};
        musaitlikModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Düzenleme kapalı
            }
        };
        
        musaitlikTablo = new JTable(musaitlikModel);
        musaitlikTablo.getColumnModel().getColumn(0).setMaxWidth(40); // ID sütunu küçük
        
        JScrollPane scrollPane = new JScrollPane(musaitlikTablo);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Alt panel (silme butonu)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        silButton = new JButton("Seçili Müsaitliği Sil");
        silButton.addActionListener(this);
        buttonPanel.add(silButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Takvim panelini hazırlar
     * @return Takvim paneli
     */
    private JPanel takvimPaneliHazirla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Üst panel (tarih seçici)
        JPanel ustPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        ustPanel.add(new JLabel("Tarih Seçin:"));
        
        takvimSecici = new JDateChooser();
        takvimSecici.setDate(new Date());
        takvimSecici.setPreferredSize(new Dimension(150, 25));
        takvimSecici.getDateEditor().addPropertyChangeListener(e -> {
            if ("date".equals(e.getPropertyName()) && takvimSecici.getDate() != null) {
                gunlukRandevulariGoster();
            }
        });
        ustPanel.add(takvimSecici);
        
        panel.add(ustPanel, BorderLayout.NORTH);
        
        // Orta panel (randevu listesi)
        gunlukRandevularArea = new JTextArea();
        gunlukRandevularArea.setEditable(false);
        gunlukRandevularArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        gunlukRandevularArea.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(gunlukRandevularArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Günlük randevuları göster
        SwingUtilities.invokeLater(() -> gunlukRandevulariGoster());
        
        return panel;
    }
    
    /**
     * Profil panelini hazırlar
     * @return Profil paneli
     */
    private JPanel profilPaneliHazirla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Profil bilgileri
        JPanel bilgiPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Başlık
        JLabel baslikLabel = new JLabel("Öğretim Üyesi Bilgileri", SwingConstants.CENTER);
        baslikLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        bilgiPanel.add(baslikLabel, gbc);
        
        // Unvan Ad Soyad
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        bilgiPanel.add(new JLabel("Unvan Ad Soyad:"), gbc);
        
        gbc.gridx = 1;
        bilgiPanel.add(new JLabel(ogretimUyesi.getTamAd()), gbc);
        
        // E-posta
        gbc.gridx = 0;
        gbc.gridy = 2;
        bilgiPanel.add(new JLabel("E-posta:"), gbc);
        
        gbc.gridx = 1;
        bilgiPanel.add(new JLabel(ogretimUyesi.getEmail()), gbc);
        
        // Bölüm
        gbc.gridx = 0;
        gbc.gridy = 3;
        bilgiPanel.add(new JLabel("Bölüm:"), gbc);
        
        gbc.gridx = 1;
        bilgiPanel.add(new JLabel(ogretimUyesi.getBolum()), gbc);
        
        // Ofis
        gbc.gridx = 0;
        gbc.gridy = 4;
        bilgiPanel.add(new JLabel("Ofis:"), gbc);
        
        gbc.gridx = 1;
        bilgiPanel.add(new JLabel(ogretimUyesi.getOfis() != null ? ogretimUyesi.getOfis() : "-"), gbc);
        
        // Çıkış butonu
        JButton cikisButton = new JButton("Çıkış Yap");
        cikisButton.addActionListener(e -> cikisYap());
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 5, 5, 5);
        bilgiPanel.add(cikisButton, gbc);
        
        panel.add(bilgiPanel, BorderLayout.NORTH);
        
        return panel;
    }
    
    /**
     * Öğretim üyesinin randevularını yükler
     */
    private void randevulariYukle() {
        // Tabloyu temizle
        randevuModel.setRowCount(0);
        
        // Randevuları getir
        List<Randevu> randevular = randevuDAO.ogretimUyesiRandevulariniGetir(ogretimUyesi.getOgretimUyesiId());
        
        // Tabloya ekle
        for (Randevu randevu : randevular) {
            Object[] row = {
                randevu.getId(),
                randevu.getOgrenciNo(),
                randevu.getOgrenciTamAd(),
                randevu.getTarih(),
                randevu.getSaatAraligi(),
                randevu.getKonu(),
                randevu.getDurum(),
                randevu.getNotlar()
            };
            
            randevuModel.addRow(row);
        }
    }
    
    /**
     * Öğretim üyesinin müsaitliklerini yükler
     */
    private void musaitlikleriYukle() {
        // Tabloyu temizle
        musaitlikModel.setRowCount(0);
        
        // Müsaitlikleri getir
        List<Musaitlik> musaitlikler = musaitlikDAO.ogretimUyesiMusaitlikGetir(ogretimUyesi.getOgretimUyesiId());
        
        // Tabloya ekle
        for (Musaitlik musaitlik : musaitlikler) {
            Object[] row = {
                musaitlik.getId(),
                musaitlik.getGunAdi(),
                TarihSaatUtil.saatFormatla(musaitlik.getBaslangicSaat()),
                TarihSaatUtil.saatFormatla(musaitlik.getBitisSaat())
            };
            
            musaitlikModel.addRow(row);
        }
    }
    
    /**
     * Seçilen tarihteki randevuları gösterir
     */
    private void gunlukRandevulariGoster() {
        if (takvimSecici.getDate() == null) {
            return;
        }
        
        LocalDate seciliTarih = takvimSecici.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        
        // Randevuları getir
        List<Randevu> gunlukRandevular = randevuDAO.gunlukRandevulariGetir(
                ogretimUyesi.getOgretimUyesiId(), seciliTarih);
        
        // Metin alanını temizle
        gunlukRandevularArea.setText("");
        
        if (gunlukRandevular.isEmpty()) {
            gunlukRandevularArea.setText("Seçilen tarih için randevu bulunmuyor.");
            return;
        }
        
        // Başlık
        gunlukRandevularArea.append(seciliTarih + " (" + TarihSaatUtil.gunAdiGetir(seciliTarih) + ") Randevuları\n\n");
        
        // Randevuları listele
        for (Randevu randevu : gunlukRandevular) {
            gunlukRandevularArea.append("Saat: " + randevu.getSaatAraligi() + "\n");
            gunlukRandevularArea.append("Öğrenci: " + randevu.getOgrenciTamAd() + " (" + randevu.getOgrenciNo() + ")\n");
            gunlukRandevularArea.append("Konu: " + randevu.getKonu() + "\n");
            gunlukRandevularArea.append("Durum: " + randevu.getDurum() + "\n");
            
            if (randevu.getNotlar() != null && !randevu.getNotlar().isEmpty()) {
                gunlukRandevularArea.append("Notlar: " + randevu.getNotlar() + "\n");
            }
            
            gunlukRandevularArea.append("---------------------------------------------\n");
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == onaylaButton) {
            randevuOnayla();
        } else if (e.getSource() == reddetButton) {
            randevuReddet();
        } else if (e.getSource() == ekleButton) {
            musaitlikEkle();
        } else if (e.getSource() == silButton) {
            musaitlikSil();
        }
    }
    
    /**
     * Randevu onaylar
     */
    private void randevuOnayla() {
        int selectedRow = randevuTablo.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Lütfen onaylamak istediğiniz randevuyu seçin.",
                "Seçim Yapılmadı",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int randevuId = (int) randevuModel.getValueAt(selectedRow, 0);
        String durum = (String) randevuModel.getValueAt(selectedRow, 6);
        
        if ("onaylandı".equals(durum)) {
            JOptionPane.showMessageDialog(this,
                "Bu randevu zaten onaylanmış.",
                "Bilgi",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        if ("reddedildi".equals(durum)) {
            int cevap = JOptionPane.showConfirmDialog(this,
                "Reddedilmiş bir randevuyu onaylamak istediğinize emin misiniz?",
                "Onay",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
                
            if (cevap != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        String notlar = JOptionPane.showInputDialog(this, 
            "Randevu için notlar (opsiyonel):", 
            randevuModel.getValueAt(selectedRow, 7));
        
        boolean guncellemeBasarili = randevuDAO.randevuDurumGuncelle(randevuId, "onaylandı", notlar);
        
        if (guncellemeBasarili) {
            JOptionPane.showMessageDialog(this,
                "Randevu başarıyla onaylandı.",
                "Randevu Onaylandı",
                JOptionPane.INFORMATION_MESSAGE);
                
            // Randevu listesini güncelle
            randevulariYukle();
            gunlukRandevulariGoster();
        } else {
            JOptionPane.showMessageDialog(this,
                "Randevu onaylanırken bir hata oluştu!",
                "Hata",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Randevu reddeder
     */
    private void randevuReddet() {
        int selectedRow = randevuTablo.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Lütfen reddetmek istediğiniz randevuyu seçin.",
                "Seçim Yapılmadı",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int randevuId = (int) randevuModel.getValueAt(selectedRow, 0);
        String durum = (String) randevuModel.getValueAt(selectedRow, 6);
        
        if ("reddedildi".equals(durum)) {
            JOptionPane.showMessageDialog(this,
                "Bu randevu zaten reddedilmiş.",
                "Bilgi",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        if ("onaylandı".equals(durum)) {
            int cevap = JOptionPane.showConfirmDialog(this,
                "Onaylanmış bir randevuyu reddetmek istediğinize emin misiniz?",
                "Onay",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
                
            if (cevap != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        String notlar = JOptionPane.showInputDialog(this, 
            "Reddetme nedeni (öğrenciye gönderilecek):", 
            randevuModel.getValueAt(selectedRow, 7));
            
        if (notlar == null) {
            return; // İptal edildi
        }
        
        boolean guncellemeBasarili = randevuDAO.randevuDurumGuncelle(randevuId, "reddedildi", notlar);
        
        if (guncellemeBasarili) {
            JOptionPane.showMessageDialog(this,
                "Randevu başarıyla reddedildi.",
                "Randevu Reddedildi",
                JOptionPane.INFORMATION_MESSAGE);
                
            // Randevu listesini güncelle
            randevulariYukle();
            gunlukRandevulariGoster();
        } else {
            JOptionPane.showMessageDialog(this,
                "Randevu reddedilirken bir hata oluştu!",
                "Hata",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Müsaitlik ekler
     */
    private void musaitlikEkle() {
        String gunAdi = (String) gunComboBox.getSelectedItem();
        
        Date baslangicDate = (Date) baslangicSpinner.getValue();
        Date bitisDate = (Date) bitisSpinner.getValue();
        
        LocalTime baslangicSaat = baslangicDate.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        LocalTime bitisSaat = bitisDate.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        
        // Başlangıç ve bitiş kontrolü
        if (baslangicSaat.compareTo(bitisSaat) >= 0) {
            JOptionPane.showMessageDialog(this,
                "Başlangıç saati bitiş saatinden önce olmalıdır!",
                "Hatalı Saat",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Müsaitlik nesnesi oluştur
        Musaitlik musaitlik = new Musaitlik();
        musaitlik.setOgretimUyesiId(ogretimUyesi.getOgretimUyesiId());
        musaitlik.setGunAdi(gunAdi);
        musaitlik.setBaslangicSaat(baslangicSaat);
        musaitlik.setBitisSaat(bitisSaat);
        
        // Veritabanına kaydet
        boolean kayitBasarili = musaitlikDAO.musaitlikEkle(musaitlik);
        
        if (kayitBasarili) {
            JOptionPane.showMessageDialog(this,
                "Müsaitlik başarıyla eklendi.",
                "Müsaitlik Eklendi",
                JOptionPane.INFORMATION_MESSAGE);
                
            // Müsaitlik listesini güncelle
            musaitlikleriYukle();
        } else {
            JOptionPane.showMessageDialog(this,
                "Müsaitlik eklenirken bir hata oluştu!",
                "Hata",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Müsaitlik siler
     */
    private void musaitlikSil() {
        int selectedRow = musaitlikTablo.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Lütfen silmek istediğiniz müsaitliği seçin.",
                "Seçim Yapılmadı",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int musaitlikId = (int) musaitlikModel.getValueAt(selectedRow, 0);
        
        int cevap = JOptionPane.showConfirmDialog(this,
            "Seçili müsaitliği silmek istediğinize emin misiniz?",
            "Silme Onayı",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (cevap != JOptionPane.YES_OPTION) {
            return;
        }
        
        boolean silmeBasarili = musaitlikDAO.musaitlikSil(musaitlikId);
        
        if (silmeBasarili) {
            JOptionPane.showMessageDialog(this,
                "Müsaitlik başarıyla silindi.",
                "Müsaitlik Silindi",
                JOptionPane.INFORMATION_MESSAGE);
                
            // Müsaitlik listesini güncelle
            musaitlikleriYukle();
        } else {
            JOptionPane.showMessageDialog(this,
                "Müsaitlik silinirken bir hata oluştu!",
                "Hata",
                JOptionPane.ERROR_MESSAGE);
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