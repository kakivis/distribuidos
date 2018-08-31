import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.RSAPublicKeySpec;
import javax.crypto.Cipher;
  
public class Criptography {
    public static final String ALGORITHM = "RSA";
    public static KeyPair key;
   
    /**
     * Gera a chave que contém um par de chave Privada e Pública usando 2048 bytes.
     * Armazena a dupla de chaves na variável key
     * Não é possível criar uma chave maior pois torna as Strings criptografadas muito grandes
     */
    public Criptography() {
      try {
        final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyGen.initialize(2048);
        key = keyGen.generateKeyPair();
      } catch (NoSuchAlgorithmException e) { 
          System.out.println(e);
      }
    }
   
    /**
     * Criptografa o texto puro usando chave pública. 
     * @param texto
     * @param chave
     * @return 
     */
    public static String criptografa(String texto, Key chave) {
        byte[] textoCriptografado = null;

        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            // Criptografa o texto puro usando a chave Púlica
            cipher.init(Cipher.ENCRYPT_MODE, chave);
            textoCriptografado = cipher.doFinal(texto.getBytes());
        } catch (Exception e) { e.printStackTrace(); }

        // Importante essa forma de conversão, pois caso fosse utilizada a conversão
        // getBytes() da javax.crypto.BadPaddingException: Decryption error
        // por problema de conversão
        return Base64.encode(textoCriptografado);
    }
   
    /**
     * Decriptografa o texto puro usando chave privada.
     * @param texto
     * @param chave
     * @return 
     */
    public static String descriptografa(String texto, Key chave) {
      byte[] decryptedText;
      String decrypted = "";
      
      try {
        final Cipher cipher = Cipher.getInstance(ALGORITHM);
        // Decriptografa o texto puro usando a chave Privada
        cipher.init(Cipher.DECRYPT_MODE, chave);
        // Importante essa forma de conversão, pois caso fosse utilizada a conversão
        // new String(texto) da javax.crypto.BadPaddingException: Decryption error
        // por problema de conversão
        decryptedText = cipher.doFinal(Base64.decode(texto));
        decrypted = new String(decryptedText, "UTF-8");
   
      } catch (Exception e) {
        System.out.println(e);
      }
   
      return decrypted;
    }

    /** 
     * Método para retornar o módulo da chave: melhor forma de enviar a chave
     * sem problemas de conversão
     * @return  
     */
    public BigInteger getModulusPublic() {
        try {
            KeyFactory fact = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec pub = fact.getKeySpec(key.getPublic(),
                RSAPublicKeySpec.class);
            return pub.getModulus();
        } catch(Exception e) {
            System.out.println(e);
        }
        
        return new BigInteger("0");
    }
    
    /** 
     * Método para retornar o expoente da chave: melhor forma de enviar a chave
     * sem problemas de conversão
     * @return  
     */
    public BigInteger getExpoentPublic() {
        try {
            KeyFactory fact = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec pub = fact.getKeySpec(key.getPublic(),
                RSAPublicKeySpec.class);
            return pub.getPublicExponent();
        } catch(Exception e) {
            System.out.println(e);
        }
        
        return new BigInteger("0");
    }
}
