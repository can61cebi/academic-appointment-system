package tr.edu.istanbularel.cancebi.dao;

import tr.edu.istanbularel.cancebi.model.Musaitlik;
import tr.edu.istanbularel.cancebi.veritabani.VeritabaniYoneticisi;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Öğretim üyesi müsaitlik bilgilerinin veritabanı işlemleri
 */
public class MusaitlikDAO {
    /**
     * Öğretim üyesinin müsaitlik bilgisini kaydeder
     * @param musaitlik Müsaitlik bilgisi
     * @return Başarılı ise true, değilse false
     */
    public boolean musaitlikEkle(Musaitlik musaitlik) {
        String sql = "INSERT INTO musaitlik (ogretim_uyesi_id, gun_adi, baslangic_saat, bitis_saat) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = VeritabaniYoneticisi.getOrnek().baglantiAl();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, musaitlik.getOgretimUyesiId());
            stmt.setString(2, musaitlik.getGunAdi());
            stmt.setTime(3, Time.valueOf(musaitlik.getBaslangicSaat()));
            stmt.setTime(4, Time.valueOf(musaitlik.getBitisSaat()));
            
            int etkilenenSatir = stmt.executeUpdate();
            return etkilenenSatir > 0;
            
        } catch (SQLException e) {
            System.err.println("Müsaitlik ekleme hatası: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Öğretim üyesinin müsaitlik bilgisini günceller
     * @param musaitlik Müsaitlik bilgisi
     * @return Başarılı ise true, değilse false
     */
    public boolean musaitlikGuncelle(Musaitlik musaitlik) {
        String sql = "UPDATE musaitlik SET gun_adi = ?, baslangic_saat = ?, bitis_saat = ? WHERE id = ?";
        
        try (Connection conn = VeritabaniYoneticisi.getOrnek().baglantiAl();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, musaitlik.getGunAdi());
            stmt.setTime(2, Time.valueOf(musaitlik.getBaslangicSaat()));
            stmt.setTime(3, Time.valueOf(musaitlik.getBitisSaat()));
            stmt.setInt(4, musaitlik.getId());
            
            int etkilenenSatir = stmt.executeUpdate();
            return etkilenenSatir > 0;
            
        } catch (SQLException e) {
            System.err.println("Müsaitlik güncelleme hatası: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Müsaitlik bilgisini siler
     * @param musaitlikId Silinecek müsaitlik ID'si
     * @return Başarılı ise true, değilse false
     */
    public boolean musaitlikSil(int musaitlikId) {
        String sql = "DELETE FROM musaitlik WHERE id = ?";
        
        try (Connection conn = VeritabaniYoneticisi.getOrnek().baglantiAl();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, musaitlikId);
            
            int etkilenenSatir = stmt.executeUpdate();
            return etkilenenSatir > 0;
            
        } catch (SQLException e) {
            System.err.println("Müsaitlik silme hatası: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Öğretim üyesinin tüm müsaitlik bilgilerini getirir
     * @param ogretimUyesiId Öğretim üyesi ID
     * @return Müsaitlik listesi
     */
    public List<Musaitlik> ogretimUyesiMusaitlikGetir(int ogretimUyesiId) {
        List<Musaitlik> musaitlikler = new ArrayList<>();
        String sql = "SELECT * FROM musaitlik WHERE ogretim_uyesi_id = ? ORDER BY " +
                     "CASE " +
                     "  WHEN gun_adi = 'Pazartesi' THEN 1 " +
                     "  WHEN gun_adi = 'Salı' THEN 2 " +
                     "  WHEN gun_adi = 'Çarşamba' THEN 3 " +
                     "  WHEN gun_adi = 'Perşembe' THEN 4 " +
                     "  WHEN gun_adi = 'Cuma' THEN 5 " +
                     "END, baslangic_saat";
        
        try (Connection conn = VeritabaniYoneticisi.getOrnek().baglantiAl();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, ogretimUyesiId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Musaitlik musaitlik = new Musaitlik();
                    musaitlik.setId(rs.getInt("id"));
                    musaitlik.setOgretimUyesiId(rs.getInt("ogretim_uyesi_id"));
                    musaitlik.setGunAdi(rs.getString("gun_adi"));
                    musaitlik.setBaslangicSaat(rs.getTime("baslangic_saat").toLocalTime());
                    musaitlik.setBitisSaat(rs.getTime("bitis_saat").toLocalTime());
                    
                    musaitlikler.add(musaitlik);
                }
            }
        } catch (SQLException e) {
            System.err.println("Müsaitlik listesi getirme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        
        return musaitlikler;
    }
}