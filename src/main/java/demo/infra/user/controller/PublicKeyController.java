package demo.infra.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Map;

import static demo.infra.user.constant.AuthConstants.PUBLIC_KEY_POSTFIX;
import static demo.infra.user.constant.AuthConstants.PUBLIC_KEY_PREFIX;

@RestController
@RequestMapping("/${api.path}/public-key")
public class PublicKeyController {
  private final KeyPair keyPair;

  public PublicKeyController(KeyPair keyPair) {
    this.keyPair = keyPair;
  }

  @GetMapping("")
  public Map<String, String> getPublicKey() {
    PublicKey publicKey = keyPair.getPublic();
    String publicKeyPEM = PUBLIC_KEY_PREFIX +
      Base64.getEncoder().encodeToString(publicKey.getEncoded()) +
      PUBLIC_KEY_POSTFIX;
    return Map.of("publicKey", publicKeyPEM);
  }
}
