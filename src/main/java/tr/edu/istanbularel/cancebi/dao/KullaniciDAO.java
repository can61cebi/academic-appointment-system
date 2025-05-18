package tr.edu.istanbularel.cancebi.dao;

import tr.edu.istanbularel.cancebi.model.Kullanici;
import tr.edu.istanbularel.cancebi.model.Ogrenci;
import tr.edu.istanbularel.cancebi.model.OgretimUyesi;
import tr.edu.istanbularel.cancebi.util.SifreYoneticisi;
import tr.edu.istanbularel.cancebi.veritabani.VeritabaniYoneticisi;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Kullanıcı veri erişim nesnesi - Veritabanı işlemleri
 */
public class KullaniciDAO {
    /**
     * Kullanıcı adı ve şifre ile giriş kontrolü yapar
     * @param kullaniciAdi Kullanıcı adı
     * @param sifre Şifre (hash'lenmemiş)
     * @return Kullanıcı nesnesi, geçersiz ise null
     */
    public Kullanici girisYap(String kullaniciAdi, String sifre) {
        String sql = "SELECT * FROM kullanicilar WHERE kullanici_adi = ?";
        
        try (Connection conn = VeritabaniYoneticisi.getOrnek().baglantiAl();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, kullaniciAdi);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String hashliSifre = rs.getString("sifre_hash");
                    
                    // Şifre kontrolü
                    if (SifreYoneticisi.sifreKontrol(sifre, hashliSifre)) {
                        Kullanici kullanici = new Kullanici();
                        kullanici.setId(rs.getInt("id"));
                        kullanici.setKullaniciAdi(rs.getString("kullanici_adi"));
                        kullanici.setEmail(rs.getString("email"));
                        kullanici.setSifreHash(hashliSifre);
                        kullanici.setAd(rs.getString("ad"));
                        kullanici.setSoyad(rs.getString("soyad"));
                        kullanici.setKullaniciTipi(rs.getString("kullanici_tipi"));
                        kullanici.setOlusturmaTarihi(rs.getTimestamp("olusturma_tarihi"));
                        
                        return kullanici;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Giriş yapma hatası: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null; // Giriş başarısız
    }
    
    /**
     * Öğrenci kaydı oluşturur
     * @param ogrenci Öğrenci bilgileri
     * @param sifre Hash'lenmemiş şifre
     * @return Kayıt başarılı ise true, değilse false
     */
    public boolean ogrenciKaydet(Ogrenci ogrenci, String sifre) {
        Connection conn = null;
        PreparedStatement stmtKullanici = null;
        PreparedStatement stmtOgrenci = null;
        
        try {
            conn = VeritabaniYoneticisi.getOrnek().baglantiAl();
            conn.setAutoCommit(false); // Transaction başlat
            
            // 1. Kullanıcı tablosuna kaydet
            String sqlKullanici = "INSERT INTO kullanicilar (kullanici_adi, email, sifre_hash, ad, soyad, kullanici_tipi) " +
                                  "VALUES (?, ?, ?, ?, ?, 'ogrenci') RETURNING id";
            
            stmtKullanici = conn.prepareStatement(sqlKullanici);
            stmtKullanici.setString(1, ogrenci.getKullaniciAdi());
            stmtKullanici.setString(2, ogrenci.getEmail());
            stmtKullanici.setString(3, SifreYoneticisi.sifreHashle(sifre));
            stmtKullanici.setString(4, ogrenci.getAd());
            stmtKullanici.setString(5, ogrenci.getSoyad());
            
            ResultSet rsKullanici = stmtKullanici.executeQuery();
            if (rsKullanici.next()) {
                int kullaniciId = rsKullanici.getInt(1);
                
                // 2. Öğrenci tablosuna kaydet
                String sqlOgrenci = "INSERT INTO ogrenciler (kullanici_id, ogrenci_no, bolum) VALUES (?, ?, ?)";
                
                stmtOgrenci = conn.prepareStatement(sqlOgrenci);
                stmtOgrenci.setInt(1, kullaniciId);
                stmtOgrenci.setString(2, ogrenci.getOgrenciNo());
                stmtOgrenci.setString(3, ogrenci.getBolum());
                
                stmtOgrenci.executeUpdate();
                conn.commit(); // Transaction tamamla
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Öğrenci kaydetme hatası: " + e.getMessage());
            e.printStackTrace();
            
            // Transaction geri al
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            // Kaynakları serbest bırak
            try {
                if (stmtOgrenci != null) stmtOgrenci.close();
                if (stmtKullanici != null) stmtKullanici.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return false;
    }
    
    /**
     * Öğretim üyesi kaydı oluşturur
     * @param ogretimUyesi Öğretim üyesi bilgileri
     * @param sifre Hash'lenmemiş şifre
     * @return Kayıt başarılı ise true, değilse false
     */
    public boolean ogretimUyesiKaydet(OgretimUyesi ogretimUyesi, String sifre) {
        Connection conn = null;
        PreparedStatement stmtKullanici = null;
        PreparedStatement stmtOgretimUyesi = null;
        
        try {
            conn = VeritabaniYoneticisi.getOrnek().baglantiAl();
            conn.setAutoCommit(false); // Transaction başlat
            
            // 1. Kullanıcı tablosuna kaydet
            String sqlKullanici = "INSERT INTO kullanicilar (kullanici_adi, email, sifre_hash, ad, soyad, kullanici_tipi) " +
                                  "VALUES (?, ?, ?, ?, ?, 'ogretim_uyesi') RETURNING id";
            
            stmtKullanici = conn.prepareStatement(sqlKullanici);
            stmtKullanici.setString(1, ogretimUyesi.getKullaniciAdi());
            stmtKullanici.setString(2, ogretimUyesi.getEmail());
            stmtKullanici.setString(3, SifreYoneticisi.sifreHashle(sifre));
            stmtKullanici.setString(4, ogretimUyesi.getAd());
            stmtKullanici.setString(5, ogretimUyesi.getSoyad());
            
            ResultSet rsKullanici = stmtKullanici.executeQuery();
            if (rsKullanici.next()) {
                int kullaniciId = rsKullanici.getInt(1);
                
                // 2. Öğretim üyesi tablosuna kaydet
                String sqlOgretimUyesi = "INSERT INTO ogretim_uyeleri (kullanici_id, unvan, bolum, ofis) VALUES (?, ?, ?, ?)";
                
                stmtOgretimUyesi = conn.prepareStatement(sqlOgretimUyesi);
                stmtOgretimUyesi.setInt(1, kullaniciId);
                stmtOgretimUyesi.setString(2, ogretimUyesi.getUnvan());
                stmtOgretimUyesi.setString(3, ogretimUyesi.getBolum());
                stmtOgretimUyesi.setString(4, ogretimUyesi.getOfis());
                
                stmtOgretimUyesi.executeUpdate();
                conn.commit(); // Transaction tamamla
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Öğretim üyesi kaydetme hatası: " + e.getMessage());
            e.printStackTrace();
            
            // Transaction geri al
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            // Kaynakları serbest bırak
            try {
                if (stmtOgretimUyesi != null) stmtOgretimUyesi.close();
                if (stmtKullanici != null) stmtKullanici.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return false;
    }
    
    /**
     * Kullanıcı adının veritabanında var olup olmadığını kontrol eder
     * @param kullaniciAdi Kontrol edilecek kullanıcı adı
     * @return Varsa true, yoksa false
     */
    public boolean kullaniciAdiVarMi(String kullaniciAdi) {
        String sql = "SELECT COUNT(*) FROM kullanicilar WHERE kullanici_adi = ?";
        
        try (Connection conn = VeritabaniYoneticisi.getOrnek().baglantiAl();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, kullaniciAdi);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Kullanıcı adı kontrolü hatası: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * E-posta adresinin veritabanında var olup olmadığını kontrol eder
     * @param email Kontrol edilecek e-posta
     * @return Varsa true, yoksa false
     */
    public boolean emailVarMi(String email) {
        String sql = "SELECT COUNT(*) FROM kullanicilar WHERE email = ?";
        
        try (Connection conn = VeritabaniYoneticisi.getOrnek().baglantiAl();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("E-posta kontrolü hatası: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Öğrenci numarasının veritabanında var olup olmadığını kontrol eder
     * @param ogrenciNo Kontrol edilecek öğrenci numarası
     * @return Varsa true, yoksa false
     */
    public boolean ogrenciNoVarMi(String ogrenciNo) {
        String sql = "SELECT COUNT(*) FROM ogrenciler WHERE ogrenci_no = ?";
        
        try (Connection conn = VeritabaniYoneticisi.getOrnek().baglantiAl();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, ogrenciNo);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Öğrenci numarası kontrolü hatası: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Kullanıcı ID'sine göre öğrenci bilgilerini getirir
     * @param kullaniciId Kullanıcı ID
     * @return Öğrenci nesnesi, bulunamazsa null
     */
    public Ogrenci ogrenciGetir(int kullaniciId) {
        String sql = "SELECT o.*, k.* FROM ogrenciler o " +
                     "JOIN kullanicilar k ON o.kullanici_id = k.id " +
                     "WHERE o.kullanici_id = ?";
        
        try (Connection conn = VeritabaniYoneticisi.getOrnek().baglantiAl();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, kullaniciId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Ogrenci ogrenci = new Ogrenci();
                    ogrenci.setId(rs.getInt("kullanici_id"));
                    ogrenci.setOgrenciId(rs.getInt("id"));
                    ogrenci.setKullaniciAdi(rs.getString("kullanici_adi"));
                    ogrenci.setEmail(rs.getString("email"));
                    ogrenci.setSifreHash(rs.getString("sifre_hash"));
                    ogrenci.setAd(rs.getString("ad"));
                    ogrenci.setSoyad(rs.getString("soyad"));
                    ogrenci.setKullaniciTipi(rs.getString("kullanici_tipi"));
                    ogrenci.setOlusturmaTarihi(rs.getTimestamp("olusturma_tarihi"));
                    ogrenci.setOgrenciNo(rs.getString("ogrenci_no"));
                    ogrenci.setBolum(rs.getString("bolum"));
                    
                    return ogrenci;
                }
            }
        } catch (SQLException e) {
            System.err.println("Öğrenci getirme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Kullanıcı ID'sine göre öğretim üyesi bilgilerini getirir
     * @param kullaniciId Kullanıcı ID
     * @return Öğretim üyesi nesnesi, bulunamazsa null
     */
    public OgretimUyesi ogretimUyesiGetir(int kullaniciId) {
        String sql = "SELECT o.*, k.* FROM ogretim_uyeleri o " +
                     "JOIN kullanicilar k ON o.kullanici_id = k.id " +
                     "WHERE o.kullanici_id = ?";
        
        try (Connection conn = VeritabaniYoneticisi.getOrnek().baglantiAl();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, kullaniciId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    OgretimUyesi ogretimUyesi = new OgretimUyesi();
                    ogretimUyesi.setId(rs.getInt("kullanici_id"));
                    ogretimUyesi.setOgretimUyesiId(rs.getInt("id"));
                    ogretimUyesi.setKullaniciAdi(rs.getString("kullanici_adi"));
                    ogretimUyesi.setEmail(rs.getString("email"));
                    ogretimUyesi.setSifreHash(rs.getString("sifre_hash"));
                    ogretimUyesi.setAd(rs.getString("ad"));
                    ogretimUyesi.setSoyad(rs.getString("soyad"));
                    ogretimUyesi.setKullaniciTipi(rs.getString("kullanici_tipi"));
                    ogretimUyesi.setOlusturmaTarihi(rs.getTimestamp("olusturma_tarihi"));
                    ogretimUyesi.setUnvan(rs.getString("unvan"));
                    ogretimUyesi.setBolum(rs.getString("bolum"));
                    ogretimUyesi.setOfis(rs.getString("ofis"));
                    
                    return ogretimUyesi;
                }
            }
        } catch (SQLException e) {
            System.err.println("Öğretim üyesi getirme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Tüm öğretim üyelerini listeler
     * @return Öğretim üyeleri listesi
     */
    public List<OgretimUyesi> tumOgretimUyeleriniGetir() {
        List<OgretimUyesi> ogretimUyeleri = new ArrayList<>();
        String sql = "SELECT o.*, k.* FROM ogretim_uyeleri o " +
                     "JOIN kullanicilar k ON o.kullanici_id = k.id " +
                     "ORDER BY k.ad, k.soyad";
        
        try (Connection conn = VeritabaniYoneticisi.getOrnek().baglantiAl();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                OgretimUyesi ogretimUyesi = new OgretimUyesi();
                ogretimUyesi.setId(rs.getInt("kullanici_id"));
                ogretimUyesi.setOgretimUyesiId(rs.getInt("id"));
                ogretimUyesi.setKullaniciAdi(rs.getString("kullanici_adi"));
                ogretimUyesi.setEmail(rs.getString("email"));
                ogretimUyesi.setAd(rs.getString("ad"));
                ogretimUyesi.setSoyad(rs.getString("soyad"));
                ogretimUyesi.setUnvan(rs.getString("unvan"));
                ogretimUyesi.setBolum(rs.getString("bolum"));
                ogretimUyesi.setOfis(rs.getString("ofis"));
                
                ogretimUyeleri.add(ogretimUyesi);
            }
        } catch (SQLException e) {
            System.err.println("Öğretim üyelerini getirme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        
        return ogretimUyeleri;
    }
}