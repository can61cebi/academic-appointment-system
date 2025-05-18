package tr.edu.istanbularel.cancebi.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Şifre hashleme ve doğrulama işlemleri
 */
public class SifreYoneticisi {
    /**
     * Şifreyi BCrypt ile hashler
     * @param sifre Hash'lenmemiş şifre
     * @return Hash'lenmiş şifre
     */
    public static String sifreHashle(String sifre) {
        return BCrypt.hashpw(sifre, BCrypt.gensalt());
    }
    
    /**
     * Şifre doğrulama
     * @param sifre Düz metin şifre
     * @param hashliSifre Hash'lenmiş şifre
     * @return Eşleşiyorsa true, aksi halde false
     */
    public static boolean sifreKontrol(String sifre, String hashliSifre) {
        return BCrypt.checkpw(sifre, hashliSifre);
    }
}