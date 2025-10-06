package demo.infra.user.config.jwt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class JwtConfig {
  public final ResourceLoader resourceLoader;
  public final PasswordEncoder passwordEncoder;

  private static final String PRIVATE_KEY_FILE = "private_key.pem";
  private static final String PUBLIC_KEY_FILE = "public_key.pem";

  public JwtConfig(ResourceLoader resourceLoader, PasswordEncoder passwordEncoder) {
    this.resourceLoader = resourceLoader;
    this.passwordEncoder = passwordEncoder;
  }

  @Bean
  public KeyPair keyPair() throws Exception {
    var privateKeyFile = getFileFromResources(PRIVATE_KEY_FILE);
    var publicKeyFile = getFileFromResources(PUBLIC_KEY_FILE);

    if (!privateKeyFile.exists() || !publicKeyFile.exists()) {
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
      keyPairGenerator.initialize(2048);
      KeyPair keyPair = keyPairGenerator.generateKeyPair();

      saveKey(privateKeyFile, "PRIVATE KEY", keyPair.getPrivate().getEncoded());
      saveKey(publicKeyFile, "PUBLIC KEY", keyPair.getPublic().getEncoded());

      return keyPair;
    } else {
      var privateKey = loadPrivateKey();
      var publicKey = loadPublicKey();
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

  private PrivateKey loadPrivateKey() throws Exception {
    String privateKeyPEM = new String(Files.readAllBytes(new File(ClassLoader.getSystemResource(PRIVATE_KEY_FILE).getFile()).toPath()));
    privateKeyPEM = privateKeyPEM.replace("----BEGIN PRIVATE KEY-----", "")
      .replace("-----END PRIVATE KEY-----", "")
      .replaceAll("\\s+", "");
    byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyPEM);
    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    return keyFactory.generatePrivate(keySpec);
  }

  private PublicKey loadPublicKey() throws Exception {
    String publicKeyPEM = new String(Files.readAllBytes(new File(ClassLoader.getSystemResource(PUBLIC_KEY_FILE).getFile()).toPath()));
    publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----", "")
      .replace("-----END PUBLIC KEY-----", "")
      .replaceAll("\\s+", "");
    byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyPEM);
    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    return keyFactory.generatePublic(keySpec);
  }

  private File getFileFromResources(String fileName) throws IOException {
    Resource resource = resourceLoader.getResource(fileName);
    return resource.getFile();
  }
}
