package tr.edu.istanbularel.cancebi.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Tarih ve saat yardımcı işlevleri
 */
public class TarihSaatUtil {
    private static final DateTimeFormatter SAAT_FORMATI = DateTimeFormatter.ofPattern("HH:mm");
    private static final Locale TR_LOCALE = new Locale("tr", "TR");
    
    /**
     * Saati formatlar (HH:mm)
     * @param saat LocalTime
     * @return Formatlanmış saat
     */
    public static String saatFormatla(LocalTime saat) {
        return saat.format(SAAT_FORMATI);
    }
    
    /**
     * Stringden LocalTime oluşturur
     * @param saatStr "HH:mm" formatında saat
     * @return LocalTime nesnesi
     */
    public static LocalTime saatParse(String saatStr) {
        return LocalTime.parse(saatStr, SAAT_FORMATI);
    }
    
    /**
     * Türkçe gün adını döndürür
     * @param date Tarih
     * @return Gün adı (Pazartesi, Salı, vb.)
     */
    public static String gunAdiGetir(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek.getDisplayName(TextStyle.FULL, TR_LOCALE);
    }
    
    /**
     * DayOfWeek değerinden Türkçe gün adını döndürür
     * @param gun DayOfWeek değeri
     * @return Gün adı (Pazartesi, Salı, vb.)
     */
    public static String gunAdiGetir(DayOfWeek gun) {
        return gun.getDisplayName(TextStyle.FULL, TR_LOCALE);
    }
    
    /**
     * Türkçe gün adından DayOfWeek değeri döndürür
     * @param gunAdi Türkçe gün adı
     * @return DayOfWeek değeri
     */
    public static DayOfWeek dayOfWeekGetir(String gunAdi) {
        switch (gunAdi) {
            case "Pazartesi": return DayOfWeek.MONDAY;
            case "Salı": return DayOfWeek.TUESDAY;
            case "Çarşamba": return DayOfWeek.WEDNESDAY;
            case "Perşembe": return DayOfWeek.THURSDAY;
            case "Cuma": return DayOfWeek.FRIDAY;
            case "Cumartesi": return DayOfWeek.SATURDAY;
            case "Pazar": return DayOfWeek.SUNDAY;
            default: throw new IllegalArgumentException("Geçersiz gün adı: " + gunAdi);
        }
    }
    
    /**
     * Başlangıç ve bitiş saatleri arasındaki 20 dakikalık slotları oluşturur
     * @param baslangicSaat Başlangıç saati
     * @param bitisSaat Bitiş saati
     * @return 20 dakikalık slot listesi (başlangıç saatleri)
     */
    public static List<LocalTime> slotlariOlustur(LocalTime baslangicSaat, LocalTime bitisSaat) {
        List<LocalTime> slotlar = new ArrayList<>();
        
        LocalTime slot = baslangicSaat;
        while (slot.plusMinutes(20).compareTo(bitisSaat) <= 0) {
            slotlar.add(slot);
            slot = slot.plusMinutes(20);
        }
        
        return slotlar;
    }
}