package com.kagg886.sylu_eoa.api.v2.util

/**
 * rsa加密类
 *
 * @author kagg886
 * @date 2023/9/3 17:59
 **/
import com.kagg886.sylu_eoa.api.v2.RSAParam
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.math.BigInteger
import java.security.InvalidKeyException
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.interfaces.RSAPublicKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.RSAPublicKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException

/**
 * @projectName: 掌上沈理青春版
 * @package: com.kagg886.jxw_collector.internal.protocol
 * @className: RSA
 * @author: kagg886
 * @description: RSA加密类
 * @date: 2023/4/13 13:04
 * @version: 1.0
 */


object RSA {
    var TAG: String = "RSA"
    fun encrypt(keyVal: RSAParam, pwd: String): String {
        return encrypt0(keyVal.modulus, keyVal.exponent, pwd)
    }

    fun encrypt(data: String, key: String): String {
        try {
            val cipher = Cipher.getInstance(TAG)

            val keyFactory = KeyFactory.getInstance("RSA")
            val x509KeySpec = X509EncodedKeySpec(Base64.getDecoder().decode(key.toByteArray()))
            val publicKey = keyFactory.generatePublic(x509KeySpec) as RSAPublicKey

            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
            return Base64.getEncoder().encodeToString(
                rsaSplitCodec(
                    cipher,
                    Cipher.ENCRYPT_MODE,
                    data.toByteArray(),
                    publicKey.modulus.bitLength()
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("加密字符串[$data]时遇到异常", e)
        }
    }

    private fun rsaSplitCodec(cipher: Cipher, opMode: Int, datas: ByteArray, keySize: Int): ByteArray {
        var maxBlock = 0
        maxBlock = if (opMode == Cipher.DECRYPT_MODE) {
            keySize / 8
        } else {
            keySize / 8 - 11
        }
        val out = ByteArrayOutputStream()
        var offSet = 0
        var buff: ByteArray
        var i = 0
        try {
            while (datas.size > offSet) {
                buff = if (datas.size - offSet > maxBlock) {
                    cipher.doFinal(datas, offSet, maxBlock)
                } else {
                    cipher.doFinal(datas, offSet, datas.size - offSet)
                }
                out.write(buff, 0, buff.size)
                i++
                offSet = i * maxBlock
            }
        } catch (e: Exception) {
            throw RuntimeException("加解密阀值为[$maxBlock]的数据时发生异常", e)
        }
        val resultDatas = out.toByteArray()
        try {
            out.close()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        return resultDatas
    }

    /**
     * @param e modulus
     * @param m exponent
     * @param pwd 明文字符串
     * @return null
     * @author kagg886
     * @description 隐藏的静态加密方法
     * @date 2023/04/13 13:07
     * @see [xiaoyun2003/ZFJAVA](https://github.com/xiaoyun2003/ZFJAVA)
     */
    @Throws(
        NoSuchAlgorithmException::class,
        InvalidKeySpecException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class,
        IOException::class
    )
    private fun encrypt0(m: String, e: String, pwd: String): String {
        val mb = BigInteger(Base64.getDecoder().decode(m))
        val eb = BigInteger(Base64.getDecoder().decode(e))
        //x509编码
        val spec = RSAPublicKeySpec(mb, eb)
        val KF = KeyFactory.getInstance("RSA")
        val pub = KF.generatePublic(spec)
        val ci = Cipher.getInstance(TAG)
        ci.init(Cipher.ENCRYPT_MODE, pub)
        val inputLen = pwd.toByteArray().size
        val out = ByteArrayOutputStream()
        var offset = 0
        var cache: ByteArray
        var i = 0
        // 对数据分段加密
        while (inputLen - offset > 0) {
            cache = if (inputLen - offset > 128) {
                ci.doFinal(pwd.toByteArray(), offset, 128)
            } else {
                ci.doFinal(pwd.toByteArray(), offset, inputLen - offset)
            }
            out.write(cache, 0, cache.size)
            i++
            offset = i * 128
        }
        val encryptedData = out.toByteArray()
        out.close()
        return String(Base64.getEncoder().encode(encryptedData))
    }

}
