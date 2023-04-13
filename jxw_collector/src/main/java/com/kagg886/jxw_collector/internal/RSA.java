package com.kagg886.jxw_collector.internal;

import com.alibaba.fastjson.JSONObject;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.kagg886.jxw_collector.internal.protocol
 * @className: RSA
 * @author: kagg886
 * @description: RSA加密类
 * @date: 2023/4/13 13:04
 * @version: 1.0
 */
public class RSA {
    private static final RSA rsa = new RSA();

    public static RSA getInstance() {
        return rsa;
    }

    public String encrypt(JSONObject keyVal,String pwd) {
        try {
            return encrypt0(keyVal.getString("modulus"),keyVal.getString("exponent"),pwd);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException | IOException e) {
            throw new RuntimeException("Encrypt Failed!",e);
        }
    }

    /**
     * @param e modulus
     * @param m exponent
     * @param pwd 明文字符串
     * @return null
     * @author kagg886
     * @description 隐藏的静态加密方法
     * @date 2023/04/13 13:07
     * @see <a href="https://github.com/xiaoyun2003/ZFJAVA">xiaoyun2003/ZFJAVA</a>
     */
    private static String encrypt0(String m,String e,String pwd) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        BigInteger mb = new BigInteger(Base64.getDecoder().decode(m));
        BigInteger eb = new BigInteger(Base64.getDecoder().decode(e));
        //x509编码
        RSAPublicKeySpec spec = new RSAPublicKeySpec(mb, eb);
        KeyFactory KF = KeyFactory.getInstance("RSA");
        PublicKey pub = KF.generatePublic(spec);
        Cipher ci = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        ci.init(Cipher.ENCRYPT_MODE, pub);
        int inputLen = pwd.getBytes().length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offset = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offset > 0) {
            if (inputLen - offset > 128) {
                cache = ci.doFinal(pwd.getBytes(), offset, 128);
            } else {
                cache = ci.doFinal(pwd.getBytes(), offset, inputLen - offset);
            }
            out.write(cache, 0, cache.length);
            i++;
            offset = i * 128;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return new String(Base64.getEncoder().encode(encryptedData));
    }

}
