import org.mindrot.jbcrypt.BCrypt;

public class HashPassword {
    public static void main(String[] args) {
        String password = "adminpassword"; // Or get from args[0]
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        System.out.println("Hashed password: " + hashed);
    }
}