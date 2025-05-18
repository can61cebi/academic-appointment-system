package tr.edu.istanbularel.cancebi.model;

/**
 * Öğrenci kullanıcı türü için model sınıfı
 */
public class Ogrenci extends Kullanici {
    private int ogrenciId;
    private String ogrenciNo;
    private String bolum;
    
    public Ogrenci() {
        super();
        setKullaniciTipi("ogrenci");
    }
    
    // Getter ve Setter metodları
    public int getOgrenciId() {
        return ogrenciId;
    }
    
    public void setOgrenciId(int ogrenciId) {
        this.ogrenciId = ogrenciId;
    }
    
    public String getOgrenciNo() {
        return ogrenciNo;
    }
    
    public void setOgrenciNo(String ogrenciNo) {
        this.ogrenciNo = ogrenciNo;
    }
    
    public String getBolum() {
        return bolum;
    }
    
    public void setBolum(String bolum) {
        this.bolum = bolum;
    }
}