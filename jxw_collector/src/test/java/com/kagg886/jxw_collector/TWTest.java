package com.kagg886.jxw_collector;

import com.kagg886.jxw_collector.exceptions.OfflineException;
import com.kagg886.jxw_collector.internal.HttpClient;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.kagg886.jxw_collector
 * @className: TWTest
 * @author: kagg886
 * @description: 研究团委系统
 * @date: 2023/4/27 13:25
 * @version: 1.0
 */
public class TWTest {

    public static RSAPublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过X509编码的Key指令获得公钥对象
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(com.alibaba.fastjson.util.Base64.decodeFast(publicKey));
        return (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
    }

    public static String publicEncrypt(String data, RSAPublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.getEncoder().encodeToString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(), publicKey.getModulus().bitLength()));
        } catch (Exception e) {
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
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

    @Test
    public void test() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        HttpClient client = new HttpClient();
        Connection.Response resp = client.url("http://xg.sylu.edu.cn/SyluTW/Sys/UserLogin.aspx")
                .get();
        Document dom = resp.parse();
        client.header("Cookie", resp.header("Set-Cookie"));

        String user = "2203050528";
        String pass = "Iveour@163.com";
        String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC3hzrH91c0OKgtaSB7GWGfDuUJsMrtiYThDXtJdrCr7exKt2fmIZngoFk71Dv/BPVQCHSuohNNvEV9VVDFSBhsP9xKEDAM4/2Lv+wlzN9CuZtLpV3Elo8VacjwMHcjTRmTchRBmijQzZRFrA2LM+qsH3U5tRM1uJFbfRMkBq24AwIDAQAB";


        //---------------根据公钥加密-----------------
        resp = client.data("UserName", user)
                .data("__VIEWSTATE", dom.getElementById("__VIEWSTATE").attr("value"))
                .data("__VIEWSTATEGENERATOR", dom.getElementById("__VIEWSTATEGENERATOR").attr("value"))
                .data("__EVENTVALIDATION", dom.getElementById("__EVENTVALIDATION").attr("value"))
                .data("Password", pass)
                .data("pwd", publicEncrypt(pass, getPublicKey(pubKey)))
                .data("pubKey", pubKey)
                .data("codeInput", "1145")
                .data("queryBtn", "%B5%C7++++++++++%C2%BC")
                .post();

        dom = resp.parse();

        String reallyCookie = resp.header("Set-Cookie");
        dom.getElementsByTag("script").forEach((v) -> {
            if (v.html().startsWith("layer.alert('")) {
                int l = v.html().indexOf("'") + 1;
                int r = v.html().indexOf("'", l);
                throw new OfflineException.LoginFailed(v.html().substring(l, r));
            }

            if (v.html().equals("window.location.href='SystemForm/main.htm';")) {
                System.out.println("登录成功!");
            }
        });

        resp = client
                .clearData()
                .url("http://xg.sylu.edu.cn/SyluTW/Sys/SystemForm/FinishExam/StuFinishStudentScore.aspx")
                .header("Cookie", reallyCookie)
                .get();
        System.out.println(resp.body());
    }
}
