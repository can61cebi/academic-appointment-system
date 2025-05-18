package tr.edu.istanbularel.cancebi.gui;

import com.toedter.calendar.JDateChooser;
import tr.edu.istanbularel.cancebi.dao.KullaniciDAO;
import tr.edu.istanbularel.cancebi.dao.MusaitlikDAO;
import tr.edu.istanbularel.cancebi.dao.RandevuDAO;
import tr.edu.istanbularel.cancebi.model.Musaitlik;
import tr.edu.istanbularel.cancebi.model.OgretimUyesi;
import tr.edu.istanbularel.cancebi.model.Ogrenci;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Öğrenci işlemlerinin yapıldığı panel
 */
public class OgrenciPaneli extends JFrame implements ActionListener {
    private Ogrenci ogrenci;
    private JTabbedPane tabbedPane;
    private JComboBox<OgretimUyesi> ogretimUyesiComboBox;
    private JDateChooser tarihSecici;
    private JComboBox<String> saatComboBox;
    private JTextField konuField;
    private JTextArea notlarArea;
    private JButton randevuOlusturButton;
    private JTable randevuTablo;
    private DefaultTableModel randevuModel;
    private JButton randevuIptalButton;
    private JLabel durumLabel;
    
    private KullaniciDAO kullaniciDAO;
    private RandevuDAO randevuDAO;
    private MusaitlikDAO musaitlikDAO;
    
    public OgrenciPaneli(Ogrenci ogrenci) {
        super("Öğrenci Paneli - " + ogrenci.getTamAd());
        this.ogrenci = ogrenci;
        
        kullaniciDAO = new KullaniciDAO();
        randevuDAO = new RandevuDAO();
        musaitlikDAO = new MusaitlikDAO();
        
        // Ana paneli oluştur
        tabbedPane = new JTabbedPane();
        
        // Randevu oluşturma sekmesi
        JPanel randevuOlusturPanel = randevuOlusturPaneliHazirla();
        tabbedPane.addTab("Randevu Oluştur", randevuOlusturPanel);
        
        // Randevularım sekmesi
        JPanel randevularimPanel = randevularPaneliHazirla();
        tabbedPane.addTab("Randevularım", randevularimPanel);
        
        // Profil sekmesi
        JPanel profilPanel = profilPaneliHazirla();
        tabbedPane.addTab("Profil", profilPanel);
        
        // Ana pencereye ekle
        add(tabbedPane);
        
        // Randevuları yükle
        randevulariYukle();
        
        // Pencere ayarları
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }
    
    /**
     * Randevu oluşturma panelini hazırlar
     * @return Randevu oluşturma paneli
     */
    private JPanel randevuOlusturPaneliHazirla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Form paneli
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Öğretim üyesi seçimi
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Öğretim Üyesi:"), gbc);
        
        ogretimUyesiComboBox = new JComboBox<>();
        ogretimUyesiComboBox.addActionListener(this);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(ogretimUyesiComboBox, gbc);
        
        // Tarih seçimi
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Tarih:"), gbc);
        
        tarihSecici = new JDateChooser();
        tarihSecici.setMinSelectableDate(new Date()); // Bugün ve sonrası
        tarihSecici.setDate(new Date()); // Bugünü seç
        tarihSecici.getDateEditor().addPropertyChangeListener(e -> {
            if ("date".equals(e.getPropertyName())) {
                musaitSaatleriYukle();
            }
        });
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(tarihSecici, gbc);
        
        // Saat seçimi
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Saat:"), gbc);
        
        saatComboBox = new JComboBox<>();
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(saatComboBox, gbc);
        
        // Konu
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Konu:"), gbc);
        
        konuField = new JTextField(20);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(konuField, gbc);
        
        // Notlar
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Notlar:"), gbc);
        
        notlarArea = new JTextArea(5, 20);
        notlarArea.setLineWrap(true);
        notlarArea.setWrapStyleWord(true);
        JScrollPane notlarScroll = new JScrollPane(notlarArea);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(notlarScroll, gbc);
        
        // Randevu oluştur butonu
        randevuOlusturButton = new JButton("Randevu Oluştur");
        randevuOlusturButton.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(randevuOlusturButton, gbc);
        
        // Durum mesajı
        durumLabel = new JLabel("");
        durumLabel.setForeground(Color.RED);
        gbc.gridy = 6;
        formPanel.add(durumLabel, gbc);
        
        panel.add(formPanel, BorderLayout.NORTH);
        
        // Açıklama paneli
        JPanel aciklamaPanel = new JPanel(new BorderLayout());
        aciklamaPanel.setBorder(BorderFactory.createTitledBorder("Bilgi"));
        
        JTextArea aciklamaArea = new JTextArea(
            "Randevu oluşturmak için:\n\n" +
            "1. Önce bir öğretim üyesi seçin.\n" +
            "2. Randevu tarihini belirleyin (hafta içi günler seçilebilir).\n" +
            "3. Müsait saatlerden birini seçin.\n" +
            "4. Görüşme konusunu kısaca belirtin.\n" +
            "5. İsterseniz ek notlar ekleyin.\n" +
            "6. 'Randevu Oluştur' butonuna tıklayın.\n\n" +
            "Not: Randevular öğretim üyesi tarafından onaylanmalıdır."
        );
        aciklamaArea.setEditable(false);
        aciklamaArea.setBackground(new Color(240, 240, 240));
        aciklamaArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        aciklamaPanel.add(new JScrollPane(aciklamaArea), BorderLayout.CENTER);
        panel.add(aciklamaPanel, BorderLayout.CENTER);
        
        // Önemli: Tüm UI bileşenleri oluşturulduktan sonra öğretim üyelerini yükle
        ogretimUyeleriniYukle();
        
        return panel;
    }
    
    /**
     * Randevular panelini hazırlar
     * @return Randevular paneli
     */
    private JPanel randevularPaneliHazirla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Tablo modeli
        String[] columnNames = {"ID", "Öğretim Üyesi", "Tarih", "Saat", "Konu", "Durum", "Notlar"};
        randevuModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Düzenleme kapalı
            }
        };
        
        randevuTablo = new JTable(randevuModel);
        randevuTablo.getColumnModel().getColumn(0).setMaxWidth(50); // ID sütunu küçük
        
        JScrollPane scrollPane = new JScrollPane(randevuTablo);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buton paneli
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        randevuIptalButton = new JButton("Randevu İptal Et");
        randevuIptalButton.addActionListener(this);
        buttonPanel.add(randevuIptalButton);
        
        JButton yenileButton = new JButton("Yenile");
        yenileButton.addActionListener(e -> randevulariYukle());
        buttonPanel.add(yenileButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
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
        JLabel baslikLabel = new JLabel("Öğrenci Bilgileri", SwingConstants.CENTER);
        baslikLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        bilgiPanel.add(baslikLabel, gbc);
        
        // Ad Soyad
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        bilgiPanel.add(new JLabel("Ad Soyad:"), gbc);
        
        gbc.gridx = 1;
        bilgiPanel.add(new JLabel(ogrenci.getTamAd()), gbc);
        
        // Öğrenci No
        gbc.gridx = 0;
        gbc.gridy = 2;
        bilgiPanel.add(new JLabel("Öğrenci No:"), gbc);
        
        gbc.gridx = 1;
        bilgiPanel.add(new JLabel(ogrenci.getOgrenciNo()), gbc);
        
        // E-posta
        gbc.gridx = 0;
        gbc.gridy = 3;
        bilgiPanel.add(new JLabel("E-posta:"), gbc);
        
        gbc.gridx = 1;
        bilgiPanel.add(new JLabel(ogrenci.getEmail()), gbc);
        
        // Bölüm
        gbc.gridx = 0;
        gbc.gridy = 4;
        bilgiPanel.add(new JLabel("Bölüm:"), gbc);
        
        gbc.gridx = 1;
        bilgiPanel.add(new JLabel(ogrenci.getBolum()), gbc);
        
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
     * Öğretim üyelerini ComboBox'a yükler
     */
    private void ogretimUyeleriniYukle() {
        ogretimUyesiComboBox.removeAllItems();
        
        List<OgretimUyesi> ogretimUyeleri = kullaniciDAO.tumOgretimUyeleriniGetir();
        for (OgretimUyesi ogretimUyesi : ogretimUyeleri) {
            ogretimUyesiComboBox.addItem(ogretimUyesi);
        }
        
        // ComboBox'ın toString metodunu özelleştir
        ogretimUyesiComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, 
                                                         int index, boolean isSelected, 
                                                         boolean cellHasFocus) {
                if (value instanceof OgretimUyesi) {
                    OgretimUyesi ogretimUyesi = (OgretimUyesi) value;
                    value = ogretimUyesi.getUnvan() + " " + ogretimUyesi.getAd() + " " + 
                           ogretimUyesi.getSoyad() + " (" + ogretimUyesi.getBolum() + ")";
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        
        if (ogretimUyesiComboBox.getItemCount() > 0) {
            ogretimUyesiComboBox.setSelectedIndex(0);
            musaitSaatleriYukle();
        }
    }
    
    /**
     * Seçilen öğretim üyesi ve tarih için müsait saatleri yükler
     */
    private void musaitSaatleriYukle() {
        // saatComboBox null kontrolü eklendi
        if (saatComboBox == null || ogretimUyesiComboBox == null || tarihSecici == null) {
            return;
        }
        
        saatComboBox.removeAllItems();
        
        if (ogretimUyesiComboBox.getSelectedItem() == null || tarihSecici.getDate() == null) {
            return;
        }
        
        OgretimUyesi ogretimUyesi = (OgretimUyesi) ogretimUyesiComboBox.getSelectedItem();
        LocalDate seciliTarih = tarihSecici.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        
        // Hafta sonu kontrolü
        DayOfWeek gun = seciliTarih.getDayOfWeek();
        if (gun == DayOfWeek.SATURDAY || gun == DayOfWeek.SUNDAY) {
            JOptionPane.showMessageDialog(this,
                "Hafta sonu (Cumartesi ve Pazar) randevu oluşturulamaz!",
                "Hatalı Tarih",
                JOptionPane.WARNING_MESSAGE);
            tarihSecici.setDate(null);
            return;
        }
        
        // Öğretim üyesinin müsait saatlerini getir
        String gunAdi = TarihSaatUtil.gunAdiGetir(seciliTarih);
        List<Musaitlik> musaitlikler = musaitlikDAO.ogretimUyesiMusaitlikGetir(ogretimUyesi.getOgretimUyesiId());
        
        // Seçilen güne ait müsaitlikleri filtrele
        List<Musaitlik> gunlukMusaitlikler = new ArrayList<>();
        for (Musaitlik musaitlik : musaitlikler) {
            if (musaitlik.getGunAdi().equals(gunAdi)) {
                gunlukMusaitlikler.add(musaitlik);
            }
        }
        
        // Müsait saatler yoksa
        if (gunlukMusaitlikler.isEmpty()) {
            saatComboBox.addItem("Bu gün için müsait saat bulunmuyor");
            saatComboBox.setEnabled(false);
            randevuOlusturButton.setEnabled(false);
            return;
        }
        
        // Mevcut randevuları kontrol et
        List<Randevu> gunlukRandevular = randevuDAO.gunlukRandevulariGetir(
                ogretimUyesi.getOgretimUyesiId(), seciliTarih);
        
        // Her müsaitlik için 20 dakikalık slotları oluştur
        List<LocalTime> tumSlotlar = new ArrayList<>();
        for (Musaitlik musaitlik : gunlukMusaitlikler) {
            List<LocalTime> slotlar = TarihSaatUtil.slotlariOlustur(
                    musaitlik.getBaslangicSaat(), musaitlik.getBitisSaat());
            tumSlotlar.addAll(slotlar);
        }
        
        // Müsait slotları ComboBox'a ekle
        boolean musaitSlotVar = false;
        for (LocalTime baslangicSaat : tumSlotlar) {
            // 20 dakikalık randevu
            LocalTime bitisSaat = baslangicSaat.plusMinutes(20);
            
            // Çakışma kontrolü
            boolean cakismaVar = false;
            for (Randevu randevu : gunlukRandevular) {
                if (!randevu.getDurum().equals("reddedildi") && 
                    (baslangicSaat.compareTo(randevu.getBaslangicSaat()) >= 0 && 
                     baslangicSaat.compareTo(randevu.getBitisSaat()) < 0) ||
                    (bitisSaat.compareTo(randevu.getBaslangicSaat()) > 0 && 
                     bitisSaat.compareTo(randevu.getBitisSaat()) <= 0) ||
                    (baslangicSaat.compareTo(randevu.getBaslangicSaat()) <= 0 && 
                     bitisSaat.compareTo(randevu.getBitisSaat()) >= 0)) {
                    cakismaVar = true;
                    break;
                }
            }
            
            if (!cakismaVar) {
                saatComboBox.addItem(TarihSaatUtil.saatFormatla(baslangicSaat) + " - " + 
                                    TarihSaatUtil.saatFormatla(bitisSaat));
                musaitSlotVar = true;
            }
        }
        
        if (!musaitSlotVar) {
            saatComboBox.addItem("Bu gün için müsait saat bulunmuyor");
            saatComboBox.setEnabled(false);
            randevuOlusturButton.setEnabled(false);
        } else {
            saatComboBox.setEnabled(true);
            randevuOlusturButton.setEnabled(true);
        }
    }
    
    /**
     * Öğrencinin randevularını yükler
     */
    private void randevulariYukle() {
        // Tabloyu temizle
        randevuModel.setRowCount(0);
        
        // Randevuları getir
        List<Randevu> randevular = randevuDAO.ogrenciRandevulariniGetir(ogrenci.getOgrenciId());
        
        // Tabloya ekle
        for (Randevu randevu : randevular) {
            Object[] row = {
                randevu.getId(),
                randevu.getOgretimUyesiTamAd(),
                randevu.getTarih(),
                randevu.getSaatAraligi(),
                randevu.getKonu(),
                randevu.getDurum(),
                randevu.getNotlar()
            };
            
            randevuModel.addRow(row);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ogretimUyesiComboBox) {
            musaitSaatleriYukle();
        } else if (e.getSource() == randevuOlusturButton) {
            randevuOlustur();
        } else if (e.getSource() == randevuIptalButton) {
            randevuIptalEt();
        }
    }
    
    /**
     * Randevu oluşturur
     */
    private void randevuOlustur() {
        if (ogretimUyesiComboBox.getSelectedItem() == null) {
            durumLabel.setText("Lütfen bir öğretim üyesi seçin!");
            return;
        }
        
        if (tarihSecici.getDate() == null) {
            durumLabel.setText("Lütfen bir tarih seçin!");
            return;
        }
        
        if (saatComboBox.getSelectedItem() == null || 
            saatComboBox.getSelectedItem().toString().contains("müsait saat bulunmuyor")) {
            durumLabel.setText("Müsait saat bulunmuyor!");
            return;
        }
        
        String konu = konuField.getText().trim();
        if (konu.isEmpty()) {
            durumLabel.setText("Lütfen randevu konusunu belirtin!");
            return;
        }
        
        // Saat bilgisini ayır
        String saatAraligi = saatComboBox.getSelectedItem().toString();
        String[] saatler = saatAraligi.split(" - ");
        LocalTime baslangicSaat = TarihSaatUtil.saatParse(saatler[0]);
        LocalTime bitisSaat = TarihSaatUtil.saatParse(saatler[1]);
        
        // Tarihi LocalDate'e çevir
        LocalDate tarih = tarihSecici.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        
        // Öğretim üyesi
        OgretimUyesi ogretimUyesi = (OgretimUyesi) ogretimUyesiComboBox.getSelectedItem();
        
        // Randevu oluştur
        Randevu randevu = new Randevu();
        randevu.setOgrenciId(ogrenci.getOgrenciId());
        randevu.setOgretimUyesiId(ogretimUyesi.getOgretimUyesiId());
        randevu.setTarih(tarih);
        randevu.setBaslangicSaat(baslangicSaat);
        randevu.setBitisSaat(bitisSaat);
        randevu.setKonu(konu);
        randevu.setNotlar(notlarArea.getText());
        
        // Veritabanına kaydet
        boolean kayitBasarili = randevuDAO.randevuEkle(randevu);
        
        if (kayitBasarili) {
            JOptionPane.showMessageDialog(this,
                "Randevu talebiniz başarıyla oluşturuldu.\nÖğretim üyesinin onayı bekleniyor.",
                "Randevu Oluşturuldu",
                JOptionPane.INFORMATION_MESSAGE);
                
            // Formu temizle
            konuField.setText("");
            notlarArea.setText("");
            
            // Randevu listesini güncelle
            randevulariYukle();
            
            // Sekmeyi değiştir
            tabbedPane.setSelectedIndex(1); // Randevularım sekmesi
        } else {
            durumLabel.setText("Randevu oluşturulurken bir hata oluştu!");
        }
    }
    
    /**
     * Randevu iptal eder
     */
    private void randevuIptalEt() {
        int selectedRow = randevuTablo.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Lütfen iptal etmek istediğiniz randevuyu seçin.",
                "Seçim Yapılmadı",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int randevuId = (int) randevuModel.getValueAt(selectedRow, 0);
        String durum = (String) randevuModel.getValueAt(selectedRow, 5);
        
        if ("onaylandı".equals(durum)) {
            int cevap = JOptionPane.showConfirmDialog(this,
                "Onaylanmış bir randevuyu iptal etmek istediğinize emin misiniz?",
                "Randevu İptal Onayı",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
                
            if (cevap != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        boolean silmeBasarili = randevuDAO.randevuSil(randevuId);
        
        if (silmeBasarili) {
            JOptionPane.showMessageDialog(this,
                "Randevu başarıyla iptal edildi.",
                "Randevu İptal Edildi",
                JOptionPane.INFORMATION_MESSAGE);
                
            // Randevu listesini güncelle
            randevulariYukle();
        } else {
            JOptionPane.showMessageDialog(this,
                "Randevu iptal edilirken bir hata oluştu!",
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