import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Base64;

public class SaltGenerator {
    public String generateSalt() throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        String salt = new String(Base64.getEncoder().encode(saltBytes));
        return salt;}
    public String hashPassword(String password, String salt) throws NoSuchAlgorithmException {
        String passwordAndSalt = password + salt;
        String hashedPassword = "";
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(passwordAndSalt.getBytes());
        hashedPassword = String.format("%064x", new BigInteger(1, md.digest()));
        return hashedPassword;}}
