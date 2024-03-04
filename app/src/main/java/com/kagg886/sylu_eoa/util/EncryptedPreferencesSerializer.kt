package com.kagg886.sylu_eoa.util

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.PreferencesMapCompat
import androidx.datastore.preferences.PreferencesProto.*
import androidx.datastore.preferences.core.*
import com.kagg886.sylu_eoa.getApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec

fun newEncryptedPreferenceDataStore(name: String,ctx:Context): DataStore<Preferences> {
    return DataStoreFactory.create(
        serializer = EncryptedPreferencesSerializer(getDeviceId(ctx)),
        corruptionHandler = null,
        migrations = listOf(),
        scope = CoroutineScope(Dispatchers.IO)
    ) {
        File(getApp().filesDir, name).apply {
            if (!exists()) {
                createNewFile()
            }
        }
    }
}


class EncryptedPreferencesSerializer(password: String) : Serializer<Preferences> {

    override val defaultValue: Preferences
        get() {
            return emptyPreferences()
        }

    private val desCrypt = DESCrypt(password)

    @Throws(IOException::class, CorruptionException::class)
    override suspend fun readFrom(input: InputStream): Preferences {
        val preferencesProto = PreferencesMapCompat.readFrom(input)

        val mutablePreferences = mutablePreferencesOf()

        preferencesProto.preferencesMap.forEach { (name, value) ->
            addProtoEntryToPreferences(name, value, mutablePreferences)
        }

        return mutablePreferences.toPreferences()
    }

    @Throws(IOException::class, CorruptionException::class)
    override suspend fun writeTo(t: Preferences, output: OutputStream) {
        val preferences = t.asMap()
        val protoBuilder = PreferenceMap.newBuilder()

        for ((key, value) in preferences) {
            protoBuilder.putPreferences(key.name, getValueProto(value))
        }

        protoBuilder.build().writeTo(output)
    }

    private fun getValueProto(value: Any): Value {
        return when (value) {
            is Boolean -> Value.newBuilder().setBoolean(value).build()
            is Float -> Value.newBuilder().setFloat(value).build()
            is Double -> Value.newBuilder().setDouble(value).build()
            is Int -> Value.newBuilder().setInteger(value).build()
            is Long -> Value.newBuilder().setLong(value).build()
            is String -> Value.newBuilder().setString(desCrypt.encrypt(value)).build()
            is Set<*> -> @Suppress("UNCHECKED_CAST") Value.newBuilder().setStringSet(
                StringSet.newBuilder().addAllStrings(value as Set<String>)
            ).build()

            else -> throw IllegalStateException(
                "PreferencesSerializer does not support type: ${value.javaClass.name}"
            )
        }
    }

    private fun addProtoEntryToPreferences(
        name: String,
        value: Value,
        mutablePreferences: MutablePreferences,
    ) {
        return when (value.valueCase) {
            Value.ValueCase.BOOLEAN -> mutablePreferences[booleanPreferencesKey(name)] = value.boolean

            Value.ValueCase.FLOAT -> mutablePreferences[floatPreferencesKey(name)] = value.float
            Value.ValueCase.DOUBLE -> mutablePreferences[doublePreferencesKey(name)] = value.double
            Value.ValueCase.INTEGER -> mutablePreferences[intPreferencesKey(name)] = value.integer
            Value.ValueCase.LONG -> mutablePreferences[longPreferencesKey(name)] = value.long
            Value.ValueCase.STRING -> mutablePreferences[stringPreferencesKey(name)] = desCrypt.decrypt(value.string)
            Value.ValueCase.STRING_SET -> mutablePreferences[stringSetPreferencesKey(name)] =
                value.stringSet.stringsList.toSet()

            Value.ValueCase.VALUE_NOT_SET -> throw CorruptionException("Value not set.")

            null -> throw CorruptionException("Value case is null.")
        }
    }
}

//单例
class DESCrypt(password: String) {
    private val en: Cipher
    private val de: Cipher

    init {
        val c = Cipher.getInstance("DES")
        val kf = SecretKeyFactory.getInstance("DES")
        val keySpec = DESKeySpec(password.toByteArray())
        val key = kf.generateSecret(keySpec)
        c.init(Cipher.ENCRYPT_MODE, key)

        en = c
    }

    init {
        val c = Cipher.getInstance("DES")
        val kf = SecretKeyFactory.getInstance("DES")
        val keySpec = DESKeySpec(password.toByteArray())
        val key = kf.generateSecret(keySpec)
        c.init(Cipher.DECRYPT_MODE, key)
        de = c
    }

    fun encrypt(input: String): String {
        val encrypt = en.doFinal(input.toByteArray())
        return Base64.getEncoder().encodeToString(encrypt)
    }

    fun decrypt(input: String): String {
        val encrypt = de.doFinal(Base64.getDecoder().decode(input))
        return String(encrypt)
    }
}