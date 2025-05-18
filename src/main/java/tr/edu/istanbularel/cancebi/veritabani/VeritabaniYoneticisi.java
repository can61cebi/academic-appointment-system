package tr.edu.istanbularel.cancebi.veritabani;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Veritabanı bağlantısını yöneten sınıf
 */
public class VeritabaniYoneticisi {
	private static final String URL = "jdbc:postgresql://localhost:5432/randevu_db";
	private static final String KULLANICI = "kullanici_adi";
	private static final String SIFRE = "sifre";
    
    private static VeritabaniYoneticisi ornek;
    
    private VeritabaniYoneticisi() {
        // Singleton yapısı için private constructor
        try {
            // PostgreSQL sürücüsünü yükle
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL sürücüsü yüklenemedi: " + e.getMessage(), e);
        }
    }
    
    /**
     * VeritabaniYoneticisi singleton örneğini döndürür
     * @return VeritabaniYoneticisi örneği
     */
    public static synchronized VeritabaniYoneticisi getOrnek() {
        if (ornek == null) {
            ornek = new VeritabaniYoneticisi();
        }
        return ornek;
    }
    
    /**
     * Veritabanı bağlantısı oluşturur
     * @return Connection nesnesi
     * @throws SQLException Bağlantı hatası durumunda
     */
    public Connection baglantiAl() throws SQLException {
        return DriverManager.getConnection(URL, KULLANICI, SIFRE);
    }
    
    /**
     * Test amaçlı veritabanı bağlantısı kontrolü
     * @return Bağlantı başarılı ise true, değilse false
     */
    public boolean baglantiKontrol() {
        try (Connection conn = baglantiAl()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Veritabanı bağlantı hatası: " + e.getMessage());
            return false;
        }
    }
}