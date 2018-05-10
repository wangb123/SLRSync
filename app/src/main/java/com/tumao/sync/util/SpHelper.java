package com.tumao.sync.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.tumao.sync.App;

import java.util.Set;

/**
 * 作者：wang on 2016/9/20 11:40
 */
public class SpHelper {
    public static void save(Class<?> cls, StringEntry<?>... args) {

        SharedPreferences sp = getSharedPreferences(cls);
        SharedPreferences.Editor editor = sp.edit();

        for (StringEntry<?> arg : args) {
            Object value = arg.getValue();
            if (value instanceof Integer) {
                editor.putInt(arg.getKey(), (int) value);
            } else if (value instanceof Float) {
                editor.putFloat(arg.getKey(), (float) value);
            } else if (value instanceof Long) {
                editor.putLong(arg.getKey(), (long) value);
            } else if (value instanceof String) {
                editor.putString(arg.getKey(), (String) value);
            } else if (value instanceof Boolean) {
                editor.putBoolean(arg.getKey(), (boolean) value);
            } else if (value instanceof Set) {
                editor.putStringSet(arg.getKey(), (Set<String>) value);
            }
        }

        editor.commit();
    }

    public static int getInt(Class<?> cls, String key) {
        SharedPreferences sp = getSharedPreferences(cls);
        return sp.getInt(key, 0);
    }

    public static int getInt(Class<?> cls, String key, int defValue) {
        SharedPreferences sp = getSharedPreferences(cls);
        return sp.getInt(key, defValue);
    }

    public static void putInt(Class<?> cls, String key, int value) {
        getSharedPreferences(cls)
                .edit()
                .putInt(key, value)
                .apply();
    }

    public static long getLong(Class<?> cls, String key) {
        SharedPreferences sp = getSharedPreferences(cls);
        return sp.getLong(key, 0);
    }

    public static long getLong(Class<?> cls, String key, long defValue) {
        SharedPreferences sp = getSharedPreferences(cls);
        return sp.getLong(key, defValue);
    }

    public static void putLong(Class<?> cls, String key, long value) {
        getSharedPreferences(cls)
                .edit()
                .putLong(key, value)
                .apply();
    }

    public static float getFloat(Class<?> cls, String key) {
        SharedPreferences sp = getSharedPreferences(cls);
        return sp.getFloat(key, 0);
    }

    public static void putFloat(Class<?> cls, String key, float value) {
        getSharedPreferences(cls)
                .edit()
                .putFloat(key, value)
                .apply();
    }

    public static String getString(Class<?> cls, String key) {
        SharedPreferences sp = getSharedPreferences(cls);
        return sp.getString(key, null);
    }

    public static String getString(Class<?> cls, String key, @Nullable String defValue) {
        SharedPreferences sp = getSharedPreferences(cls);
        return sp.getString(key, defValue);
    }

    public static void putString(Class<?> cls, String key, String value) {
        getSharedPreferences(cls)
                .edit()
                .putString(key, value)
                .apply();
    }

    public static Set<String> getStringSet(Class<?> cls, String key) {
        SharedPreferences sp = getSharedPreferences(cls);
        return sp.getStringSet(key, null);
    }

    public static void putStringSet(Class<?> cls, String key, Set<String> values) {
        getSharedPreferences(cls)
                .edit()
                .putStringSet(key, values)
                .apply();
    }

    public static boolean getBoolean(Class<?> cls, String key) {
        SharedPreferences sp = getSharedPreferences(cls);
        return sp.getBoolean(key, false);
    }

    public static void putBoolean(Class<?> cls, String key, boolean value) {
        getSharedPreferences(cls)
                .edit()
                .putBoolean(key, value)
                .apply();
    }

    public static void clear(Class<?> cls) {
        getSharedPreferences(cls)
                .edit()
                .clear()
                .apply();
    }

    public static void remove(Class<?> cls, String key) {
        getSharedPreferences(cls)
                .edit()
                .remove(key)
                .apply();
    }


    public static SharedPreferences getSharedPreferences(Class<?> cls) {
        return App.getApp().getSharedPreferences(cls.getSimpleName(), Context.MODE_PRIVATE);
    }

    public static class StringEntry<V> {
        String key;
        V value;

        public StringEntry(String key, V value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

    }


}
