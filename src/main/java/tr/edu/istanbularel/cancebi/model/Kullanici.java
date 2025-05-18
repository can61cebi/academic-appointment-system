package tr.edu.istanbularel.cancebi.model;

import java.util.Date;

/**
 * Sistemdeki tüm kullanıcılar için temel sınıf
 */
public class Kullanici {
    private int id;
    private String kullaniciAdi;
    private String email;
    private String sifreHash;
    private String ad;
    private String soyad;
    private String kullaniciTipi; // "ogrenci", "ogretim_uyesi", "yetkili"
    private Date olusturmaTarihi;
    
    public Kullanici() {
    }
    
    public Kullanici(int id, String kullaniciAdi, String email, String sifreHash, 
                     String ad, String soyad, String kullaniciTipi, Date olusturmaTarihi) {
        this.id = id;
        this.kullaniciAdi = kullaniciAdi;
        this.email = email;
        this.sifreHash = sifreHash;
        this.ad = ad;
        this.soyad = soyad;
        this.kullaniciTipi = kullaniciTipi;
        this.olusturmaTarihi = olusturmaTarihi;
    }
    
    // Getter ve Setter metodları
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getKullaniciAdi() {
        return kullaniciAdi;
    }
    
    public void setKullaniciAdi(String kullaniciAdi) {
        this.kullaniciAdi = kullaniciAdi;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getSifreHash() {
        return sifreHash;
    }
    
    public void setSifreHash(String sifreHash) {
        this.sifreHash = sifreHash;
    }
    
    public String getAd() {
        return ad;
    }
    
    public void setAd(String ad) {
        this.ad = ad;
    }
    
    public String getSoyad() {
        return soyad;
    }
    
    public void setSoyad(String soyad) {
        this.soyad = soyad;
    }
    
    public String getKullaniciTipi() {
        return kullaniciTipi;
    }
    
    public void setKullaniciTipi(String kullaniciTipi) {
        this.kullaniciTipi = kullaniciTipi;
    }
    
    public Date getOlusturmaTarihi() {
        return olusturmaTarihi;
    }
    
    public void setOlusturmaTarihi(Date olusturmaTarihi) {
        this.olusturmaTarihi = olusturmaTarihi;
    }
    
    /**
     * Kullanıcının tam adını döndürür
     * @return Ad ve soyad birleşimi
     */
    public String getTamAd() {
        return ad + " " + soyad;
    }
}