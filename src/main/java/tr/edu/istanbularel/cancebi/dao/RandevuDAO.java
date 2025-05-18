package tr.edu.istanbularel.cancebi.dao;

import tr.edu.istanbularel.cancebi.model.Randevu;
import tr.edu.istanbularel.cancebi.veritabani.VeritabaniYoneticisi;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Randevu veri erişim nesnesi - Veritabanı işlemleri
 */
public class RandevuDAO {
    /**
     * Yeni randevu oluşturur
     * @param randevu Randevu bilgileri
     * @return Başarılı ise true, değilse false
     */
    public boolean randevuEkle(Randevu randevu) {
        String sql = "INSERT INTO randevular (ogrenci_id, ogretim_uyesi_id, tarih, baslangic_saat, bitis_saat, durum, konu, notlar) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = VeritabaniYoneticisi.getOrnek().baglantiAl();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, randevu.getOgrenciId());
            stmt.setInt(2, randevu.getOgretimUyesiId());
            stmt.setDate(3, Date.valueOf(randevu.getTarih()));
            stmt.setTime(4, Time.valueOf(randevu.getBaslangicSaat()));
            stmt.setTime(5, Time.valueOf(randevu.getBitisSaat()));
            stmt.setString(6, randevu.getDurum());
            stmt.setString(7, randevu.getKonu());
            stmt.setString(8, randevu.getNotlar());
            
            int etkilenenSatir = stmt.executeUpdate();
            return etkilenenSatir > 0;
            
        } catch (SQLException e) {
            System.err.println("Randevu ekleme hatası: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Randevu durumunu günceller
     * @param randevuId Randevu ID
     * @param yeniDurum Yeni durum (beklemede, onaylandı, reddedildi)
     * @param notlar Varsa ek notlar
     * @return Başarılı ise true, değilse false
     */
    public boolean randevuDurumGuncelle(int randevuId, String yeniDurum, String notlar) {
        String sql = "UPDATE randevular SET durum = ?, notlar = ? WHERE id = ?";
        
        try (Connection conn = VeritabaniYoneticisi.getOrnek().baglantiAl();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, yeniDurum);
            stmt.setString(2, notlar);
            stmt.setInt(3, randevuId);
            
            int etkilenenSatir = stmt.executeUpdate();
            return etkilenenSatir > 0;
            
        } catch (SQLException e) {
            System.err.println("Randevu durumu güncelleme hatası: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Randevu siler
     * @param randevuId Silinecek randevu ID
     * @return Başarılı ise true, değilse false
     */
    public boolean randevuSil(int randevuId) {
        String sql = "DELETE FROM randevular WHERE id = ?";
        
        try (Connection conn = VeritabaniYoneticisi.getOrnek().baglantiAl();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, randevuId);
            
            int etkilenenSatir = stmt.executeUpdate();
            return etkilenenSatir > 0;
            
        } catch (SQLException e) {
            System.err.println("Randevu silme hatası: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Öğrencinin randevularını getirir
     * @param ogrenciId Öğrenci ID
     * @return Randevu listesi
     */
    public List<Randevu> ogrenciRandevulariniGetir(int ogrenciId) {
        List<Randevu> randevular = new ArrayList<>();
        String sql = "SELECT r.*, " +
                     "o.ogrenci_no, " +
                     "ks.ad AS ogrenci_adi, ks.soyad AS ogrenci_soyadi, " +
                     "ko.ad AS ogretim_uyesi_adi, ko.soyad AS ogretim_uyesi_soyadi, " +
                     "ou.unvan AS ogretim_uyesi_unvan " +
                     "FROM randevular r " +
                     "JOIN ogrenciler o ON r.ogrenci_id = o.id " +
                     "JOIN kullanicilar ks ON o.kullanici_id = ks.id " +
                     "JOIN ogretim_uyeleri ou ON r.ogretim_uyesi_id = ou.id " +
                     "JOIN kullanicilar ko ON ou.kullanici_id = ko.id " +
                     "WHERE r.ogrenci_id = ? " +
                     "ORDER BY r.tarih DESC, r.baslangic_saat DESC";
        
        try (Connection conn = VeritabaniYoneticisi.getOrnek().baglantiAl();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, ogrenciId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Randevu randevu = new Randevu();
                    randevuDoldur(randevu, rs);
                    randevular.add(randevu);
                }
            }
        } catch (SQLException e) {
            System.err.println("Öğrenci randevularını getirme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        
        return randevular;
    }
    
    /**
     * Öğretim üyesinin randevularını getirir
     * @param ogretimUyesiId Öğretim üyesi ID
     * @return Randevu listesi
     */
    public List<Randevu> ogretimUyesiRandevulariniGetir(int ogretimUyesiId) {
        List<Randevu> randevular = new ArrayList<>();
        String sql = "SELECT r.*, " +
                     "o.ogrenci_no, " +
                     "ks.ad AS ogrenci_adi, ks.soyad AS ogrenci_soyadi, " +
                     "ko.ad AS ogretim_uyesi_adi, ko.soyad AS ogretim_uyesi_soyadi, " +
                     "ou.unvan AS ogretim_uyesi_unvan " +
                     "FROM randevular r " +
                     "JOIN ogrenciler o ON r.ogrenci_id = o.id " +
                     "JOIN kullanicilar ks ON o.kullanici_id = ks.id " +
                     "JOIN ogretim_uyeleri ou ON r.ogretim_uyesi_id = ou.id " +
                     "JOIN kullanicilar ko ON ou.kullanici_id = ko.id " +
                     "WHERE r.ogretim_uyesi_id = ? " +
                     "ORDER BY r.tarih DESC, r.baslangic_saat DESC";
        
        try (Connection conn = VeritabaniYoneticisi.getOrnek().baglantiAl();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, ogretimUyesiId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Randevu randevu = new Randevu();
                    randevuDoldur(randevu, rs);
                    randevular.add(randevu);
                }
            }
        } catch (SQLException e) {
            System.err.println("Öğretim üyesi randevularını getirme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        
        return randevular;
    }
    
    /**
     * Belirli bir tarihte öğretim üyesinin randevularını getirir
     * @param ogretimUyesiId Öğretim üyesi ID
     * @param tarih Tarih
     * @return Randevu listesi
     */
    public List<Randevu> gunlukRandevulariGetir(int ogretimUyesiId, LocalDate tarih) {
        List<Randevu> randevular = new ArrayList<>();
        String sql = "SELECT r.*, " +
                     "o.ogrenci_no, " +
                     "ks.ad AS ogrenci_adi, ks.soyad AS ogrenci_soyadi, " +
                     "ko.ad AS ogretim_uyesi_adi, ko.soyad AS ogretim_uyesi_soyadi, " +
                     "ou.unvan AS ogretim_uyesi_unvan " +
                     "FROM randevular r " +
                     "JOIN ogrenciler o ON r.ogrenci_id = o.id " +
                     "JOIN kullanicilar ks ON o.kullanici_id = ks.id " +
                     "JOIN ogretim_uyeleri ou ON r.ogretim_uyesi_id = ou.id " +
                     "JOIN kullanicilar ko ON ou.kullanici_id = ko.id " +
                     "WHERE r.ogretim_uyesi_id = ? AND r.tarih = ? " +
                     "ORDER BY r.baslangic_saat";
        
        try (Connection conn = VeritabaniYoneticisi.getOrnek().baglantiAl();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, ogretimUyesiId);
            stmt.setDate(2, Date.valueOf(tarih));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Randevu randevu = new Randevu();
                    randevuDoldur(randevu, rs);
                    randevular.add(randevu);
                }
            }
        } catch (SQLException e) {
            System.err.println("Günlük randevuları getirme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        
        return randevular;
    }
    
    /**
     * Randevu nesnesi oluşturur
     * @param randevu Doldurulacak randevu nesnesi
     * @param rs ResultSet
     * @throws SQLException Veritabanı hatası
     */
    private void randevuDoldur(Randevu randevu, ResultSet rs) throws SQLException {
        randevu.setId(rs.getInt("id"));
        randevu.setOgrenciId(rs.getInt("ogrenci_id"));
        randevu.setOgretimUyesiId(rs.getInt("ogretim_uyesi_id"));
        randevu.setTarih(rs.getDate("tarih").toLocalDate());
        randevu.setBaslangicSaat(rs.getTime("baslangic_saat").toLocalTime());
        randevu.setBitisSaat(rs.getTime("bitis_saat").toLocalTime());
        randevu.setDurum(rs.getString("durum"));
        randevu.setKonu(rs.getString("konu"));
        randevu.setNotlar(rs.getString("notlar"));
        randevu.setOlusturmaTarihi(rs.getTimestamp("olusturma_tarihi"));
        
        // İlişkili bilgiler
        randevu.setOgrenciNo(rs.getString("ogrenci_no"));
        randevu.setOgrenciAdi(rs.getString("ogrenci_adi"));
        randevu.setOgrenciSoyadi(rs.getString("ogrenci_soyadi"));
        randevu.setOgretimUyesiUnvan(rs.getString("ogretim_uyesi_unvan"));
        randevu.setOgretimUyesiAdi(rs.getString("ogretim_uyesi_adi"));
        randevu.setOgretimUyesiSoyadi(rs.getString("ogretim_uyesi_soyadi"));
    }
    
    /**
     * Saatler arasında çakışma kontrolü yapar
     * @param ogretimUyesiId Öğretim üyesi ID
     * @param tarih Randevu tarihi
     * @param baslangicSaat Başlangıç saati
     * @param bitisSaat Bitiş saati
     * @return Çakışma varsa true, yoksa false
     */
    public boolean randevuCakismaVarMi(int ogretimUyesiId, LocalDate tarih, 
                                      LocalTime baslangicSaat, LocalTime bitisSaat) {
        String sql = "SELECT COUNT(*) FROM randevular WHERE ogretim_uyesi_id = ? " +
                     "AND tarih = ? AND durum != 'reddedildi' AND " +
                     "((baslangic_saat <= ? AND bitis_saat > ?) OR " +
                     "(baslangic_saat < ? AND bitis_saat >= ?) OR " +
                     "(baslangic_saat >= ? AND bitis_saat <= ?))";
        
        try (Connection conn = VeritabaniYoneticisi.getOrnek().baglantiAl();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, ogretimUyesiId);
            stmt.setDate(2, Date.valueOf(tarih));
            stmt.setTime(3, Time.valueOf(baslangicSaat));
            stmt.setTime(4, Time.valueOf(baslangicSaat));
            stmt.setTime(5, Time.valueOf(bitisSaat));
            stmt.setTime(6, Time.valueOf(bitisSaat));
            stmt.setTime(7, Time.valueOf(baslangicSaat));
            stmt.setTime(8, Time.valueOf(bitisSaat));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Randevu çakışma kontrolü hatası: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
}