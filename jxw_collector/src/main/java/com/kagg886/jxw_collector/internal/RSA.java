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
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
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

    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize) {
        int maxBlock = 0;
        if (opmode == Cipher.DECRYPT_MODE) {
            maxBlock = keySize / 8;
        } else {
            maxBlock = keySize / 8 - 11;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] buff;
        int i = 0;
        try {
            while (datas.length > offSet) {
                if (datas.length - offSet > maxBlock) {
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                } else {
                    buff = cipher.doFinal(datas, offSet, datas.length - offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
        } catch (Exception e) {
            throw new RuntimeException("加解密阀值为[" + maxBlock + "]的数据时发生异常", e);
        }
        byte[] resultDatas = out.toByteArray();
        try {
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return resultDatas;
    }

    public String encrypt(JSONObject keyVal, String pwd) {
        try {
            return encrypt0(keyVal.getString("modulus"), keyVal.getString("exponent"), pwd);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException | IOException e) {
            throw new RuntimeException("Encrypt Failed!", e);
        }
    }

    /*
     * @param data: 数据
     * @param key: 公钥
     * @return String
     * @author kagg886
     * @description 只根据公钥的RSA加密
     * @date 2023/05/04 14:00
     */
    public String encrypt(String data, String key) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(key.getBytes()));
            RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);

            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.getEncoder().encodeToString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(), publicKey.getModulus().bitLength()));
        } catch (Exception e) {
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
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
