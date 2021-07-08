package com.bjtu.bookstore.utils.encodeUtils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @program: questionPlatform_back_end
 * @description: encode util
 * @author: CodingLiOOT
 * @create: 2021-04-06 14:05
 * @version: 1.0
 **/
@Data
@ConfigurationProperties(prefix = "encode")
@Component
public class EncodeUtil {
    private String secret;
    private BCryptPasswordEncoder encoder;


    public EncodeUtil(@Value(value = "${encode.secret}") String secret) {
        this.secret = secret;
        this.encoder = new BCryptPasswordEncoder();
    }

    public String genCode(String rawPass, String salt) {
        return encoder.encode(rawPass + secret + salt);
    }

    public Boolean verifyEncode(String rawPass, String salt, String encode) {
        return encoder.matches(rawPass + secret + salt, encode);
    }

}
