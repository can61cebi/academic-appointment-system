package tr.edu.istanbularel.cancebi.model;

import java.time.LocalTime;

/**
 * Öğretim üyesinin müsait olduğu saatleri tutan model sınıfı
 */
public class Musaitlik {
    private int id;
    private int ogretimUyesiId;
    private String gunAdi; // Pazartesi, Salı, Çarşamba, Perşembe, Cuma
    private LocalTime baslangicSaat;
    private LocalTime bitisSaat;
    
    public Musaitlik() {
    }
    
    public Musaitlik(int id, int ogretimUyesiId, String gunAdi, 
                    LocalTime baslangicSaat, LocalTime bitisSaat) {
        this.id = id;
        this.ogretimUyesiId = ogretimUyesiId;
        this.gunAdi = gunAdi;
        this.baslangicSaat = baslangicSaat;
        this.bitisSaat = bitisSaat;
    }
    
    // Getter ve Setter metodları
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getOgretimUyesiId() {
        return ogretimUyesiId;
    }
    
    public void setOgretimUyesiId(int ogretimUyesiId) {
        this.ogretimUyesiId = ogretimUyesiId;
    }
    
    public String getGunAdi() {
        return gunAdi;
    }
    
    public void setGunAdi(String gunAdi) {
        this.gunAdi = gunAdi;
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
    
    @Override
    public String toString() {
        return gunAdi + " " + baslangicSaat + " - " + bitisSaat;
    }
}