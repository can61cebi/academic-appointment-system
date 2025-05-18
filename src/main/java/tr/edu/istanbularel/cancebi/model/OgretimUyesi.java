package tr.edu.istanbularel.cancebi.model;

/**
 * Öğretim üyesi kullanıcı türü için model sınıfı
 */
public class OgretimUyesi extends Kullanici {
    private int ogretimUyesiId;
    private String unvan;
    private String bolum;
    private String ofis;
    
    public OgretimUyesi() {
        super();
        setKullaniciTipi("ogretim_uyesi");
    }
    
    // Getter ve Setter metodları
    public int getOgretimUyesiId() {
        return ogretimUyesiId;
    }
    
    public void setOgretimUyesiId(int ogretimUyesiId) {
        this.ogretimUyesiId = ogretimUyesiId;
    }
    
    public String getUnvan() {
        return unvan;
    }
    
    public void setUnvan(String unvan) {
        this.unvan = unvan;
    }
    
    public String getBolum() {
        return bolum;
    }
    
    public void setBolum(String bolum) {
        this.bolum = bolum;
    }
    
    public String getOfis() {
        return ofis;
    }
    
    public void setOfis(String ofis) {
        this.ofis = ofis;
    }
    
    /**
     * Unvan ve ismi birleştirerek döndürür
     * @return Unvan ve isim
     */
    @Override
    public String getTamAd() {
        return unvan + " " + getAd() + " " + getSoyad();
    }
}