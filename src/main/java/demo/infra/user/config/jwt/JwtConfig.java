package demo.infra.user.config.jwt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class JwtConfig {

  private static final String KEYS_DIR = "keys";
  private static final String PRIVATE_KEY_FILE = KEYS_DIR + "/private_key.pem";
  private static final String PUBLIC_KEY_FILE = KEYS_DIR + "/public_key.pem";

  public JwtConfig(ResourceLoader resourceLoader, PasswordEncoder passwordEncoder) {
  }

  @Bean
  public KeyPair keyPair() throws Exception {
    File keysDir = new File(KEYS_DIR);
    if (!keysDir.exists()) {
      keysDir.mkdirs(); // ✅ create directory if missing
    }

    File privateKeyFile = new File(PRIVATE_KEY_FILE);
    File publicKeyFile = new File(PUBLIC_KEY_FILE);

    if (!privateKeyFile.exists() || !publicKeyFile.exists()) {
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
      keyPairGenerator.initialize(2048);
      KeyPair keyPair = keyPairGenerator.generateKeyPair();

      saveKey(privateKeyFile, "PRIVATE KEY", keyPair.getPrivate().getEncoded());
      saveKey(publicKeyFile, "PUBLIC KEY", keyPair.getPublic().getEncoded());

      return keyPair;
    } else {
      PrivateKey privateKey = loadPrivateKey(privateKeyFile);
      PublicKey publicKey = loadPublicKey(publicKeyFile);
      return new KeyPair(publicKey, privateKey);
    }
  }

  private void saveKey(File file, String keyType, byte[] keyBytes) throws Exception {
    String keyString = "-----BEGIN " + keyType + "-----\n" +
      Base64.getEncoder().encodeToString(keyBytes) +
      "\n-----END " + keyType + "-----";
    try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
      fileOutputStream.write(keyString.getBytes());
    }
  }

  private PrivateKey loadPrivateKey(File file) throws Exception {
    String privateKeyPEM = new String(Files.readAllBytes(file.toPath()));
    privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----", "")
      .replace("-----END PRIVATE KEY-----", "")
      .replaceAll("\\s+", "");
    byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyPEM);
    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
    return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
  }

  private PublicKey loadPublicKey(File file) throws Exception {
    String publicKeyPEM = new String(Files.readAllBytes(file.toPath()));
    publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----", "")
      .replace("-----END PUBLIC KEY-----", "")
      .replaceAll("\\s+", "");
    byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyPEM);
    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
    return KeyFactory.getInstance("RSA").generatePublic(keySpec);
  }
}