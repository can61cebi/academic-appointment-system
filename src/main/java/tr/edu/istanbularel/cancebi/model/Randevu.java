package tr.edu.istanbularel.cancebi.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

/**
 * Randevu bilgilerini tutan model sınıfı
 */
public class Randevu {
    private int id;
    private int ogrenciId;
    private int ogretimUyesiId;
    private LocalDate tarih;
    private LocalTime baslangicSaat;
    private LocalTime bitisSaat;
    private String durum; // beklemede, onaylandı, reddedildi
    private String konu;
    private String notlar;
    private Date olusturmaTarihi;
    
    // İlişkili öğrenci ve öğretim üyesi bilgileri (join için)
    private String ogrenciAdi;
    private String ogrenciSoyadi;
    private String ogrenciNo;
    private String ogretimUyesiUnvan;
    private String ogretimUyesiAdi;
    private String ogretimUyesiSoyadi;
    
    public Randevu() {
        this.durum = "beklemede";
    }
    
    // Getter ve Setter metodları
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getOgrenciId() {
        return ogrenciId;
    }
    
    public void setOgrenciId(int ogrenciId) {
        this.ogrenciId = ogrenciId;
    }
    
    public int getOgretimUyesiId() {
        return ogretimUyesiId;
    }
    
    public void setOgretimUyesiId(int ogretimUyesiId) {
        this.ogretimUyesiId = ogretimUyesiId;
    }
    
    public LocalDate getTarih() {
        return tarih;
    }
    
    public void setTarih(LocalDate tarih) {
        this.tarih = tarih;
    }
    
    public LocalTime getBaslangicSaat() {
        return baslangicSaat;
    }
    
    public void setBaslangicSaat(LocalTime baslangicSaat) {
        this.baslangicSaat = baslangicSaat;
    }
    
    public LocalTime getBitisSaat() {
        return bitisSaat;
    }
    
    public void setBitisSaat(LocalTime bitisSaat) {
        this.bitisSaat = bitisSaat;
    }
    
    public String getDurum() {
        return durum;
    }
    
    public void setDurum(String durum) {
        this.durum = durum;
    }
    
    public String getKonu() {
        return konu;
    }
    
    public void setKonu(String konu) {
        this.konu = konu;
    }
    
    public String getNotlar() {
        return notlar;
    }
    
    public void setNotlar(String notlar) {
        this.notlar = notlar;
    }
    
    public Date getOlusturmaTarihi() {
        return olusturmaTarihi;
    }
    
    public void setOlusturmaTarihi(Date olusturmaTarihi) {
        this.olusturmaTarihi = olusturmaTarihi;
    }
    
    public String getOgrenciAdi() {
        return ogrenciAdi;
    }
    
    public void setOgrenciAdi(String ogrenciAdi) {
        this.ogrenciAdi = ogrenciAdi;
    }
    
    public String getOgrenciSoyadi() {
        return ogrenciSoyadi;
    }
    
    public void setOgrenciSoyadi(String ogrenciSoyadi) {
        this.ogrenciSoyadi = ogrenciSoyadi;
    }
    
    public String getOgrenciNo() {
        return ogrenciNo;
    }
    
    public void setOgrenciNo(String ogrenciNo) {
        this.ogrenciNo = ogrenciNo;
    }
    
    public String getOgretimUyesiUnvan() {
        return ogretimUyesiUnvan;
    }
    
    public void setOgretimUyesiUnvan(String ogretimUyesiUnvan) {
        this.ogretimUyesiUnvan = ogretimUyesiUnvan;
    }
    
    public String getOgretimUyesiAdi() {
        return ogretimUyesiAdi;
    }
    
    public void setOgretimUyesiAdi(String ogretimUyesiAdi) {
        this.ogretimUyesiAdi = ogretimUyesiAdi;
    }
    
    public String getOgretimUyesiSoyadi() {
        return ogretimUyesiSoyadi;
    }
    
    public void setOgretimUyesiSoyadi(String ogretimUyesiSoyadi) {
        this.ogretimUyesiSoyadi = ogretimUyesiSoyadi;
    }
    
    /**
     * Randevu için öğrencinin tam adını döndürür
     * @return Öğrenci adı ve soyadı
     */
    public String getOgrenciTamAd() {
        return ogrenciAdi + " " + ogrenciSoyadi;
    }
    
    /**
     * Randevu için öğretim üyesinin tam adını döndürür
     * @return Unvan, ad ve soyad birleşimi
     */
    public String getOgretimUyesiTamAd() {
        return ogretimUyesiUnvan + " " + ogretimUyesiAdi + " " + ogretimUyesiSoyadi;
    }
    
    /**
     * Randevu saatini formatlanmış olarak döndürür
     * @return Başlangıç - Bitiş saat formatı
     */
    public String getSaatAraligi() {
        return baslangicSaat + " - " + bitisSaat;
    }
}