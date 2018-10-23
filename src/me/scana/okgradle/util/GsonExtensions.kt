package me.scana.okgradle.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Reader

inline fun <reified T> Gson.fromJson(reader: Reader) = this.fromJson<T>(reader, object : TypeToken<T>() {}.type)!!