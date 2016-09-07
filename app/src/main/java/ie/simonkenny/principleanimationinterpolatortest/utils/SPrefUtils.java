package ie.simonkenny.principleanimationinterpolatortest.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.StringTokenizer;

/**
 * Created by simonkenny on 07/09/2016.
 */
public class SPrefUtils {
    private static StringBuffer sStringBuffer = new StringBuffer();

    /* * * * * * * * * * *
    * Shared Preferences
    * * * * * * * * *  */
    //region Application level persistence
    public static SharedPreferences getPrefManager(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
    }
    public static SharedPreferences getPrefs(Context ctx, String prefsKey) {
        return ctx.getApplicationContext().getSharedPreferences(prefsKey, Context.MODE_PRIVATE);
    }
    public static SharedPreferences.Editor getDefaultPrefsEditor(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext()).edit();
    }
    public static SharedPreferences.Editor getPrefsEditor(Context ctx, String prefsKey) {
        return ctx.getApplicationContext().getSharedPreferences(prefsKey, Context.MODE_PRIVATE).edit();
    }

    public static void saveString(Context ctx, String key, String value) {
        getDefaultPrefsEditor(ctx).putString(key, value).apply();
    }
    public static String getString(Context ctx, String key) {
        return getPrefManager(ctx).getString(key, "");
    }

    public static void saveString(Context ctx, String prefsKey, String key, String value) {
        getPrefsEditor(ctx, prefsKey).putString(key, value).apply();
    }
    public static String getString(Context ctx, String prefsKey, String key) {
        return getPrefs(ctx, prefsKey).getString(key, "");
    }

    public static void saveInt(Context ctx, String key, int value) {
        getDefaultPrefsEditor(ctx).putInt(key, value).apply();

    }
    public static int getInt(Context ctx, String key) {
        return getPrefManager(ctx).getInt(key, 0);
    }

    public static void saveInt(Context ctx, String prefsKey, String key, int value) {
        getPrefsEditor(ctx, prefsKey).putInt(key, value).apply();
    }
    public static int getInt(Context ctx, String prefsKey, String key) {
        return getPrefs(ctx, prefsKey).getInt(key, 0);
    }


    public static void saveIntArray(Context ctx, String key, int[] value) {
        saveIntArray(ctx, null, key, value);
    }

    public static void saveIntArray(Context ctx, String prefsKey, String key, int[] value) {
        sStringBuffer.setLength(0);
        for (int i = 0; i < value.length; i++) {
            sStringBuffer.append(value[i]).append(",");
        }
        (TextUtils.isEmpty(prefsKey) ?
                getDefaultPrefsEditor(ctx) : getPrefsEditor(ctx, prefsKey)).putString(key,
                sStringBuffer.toString()).apply();
    }

    public static int[] getIntArray(Context ctx, String key) {
        return getIntArray(ctx, null, key);
    }

    public static int[] getIntArray(Context ctx, String prefsKey, String key) {
        StringTokenizer st = new StringTokenizer((TextUtils.isEmpty(prefsKey) ?
                getPrefManager(ctx) : getPrefs(ctx, prefsKey)).getString(key, ""), ",");
        int[] savedList = new int[st.countTokens()];
        for (int i = 0; i < savedList.length; i++) {
            savedList[i] = Integer.parseInt(st.nextToken());
        }
        return savedList;
    }

    public static void saveLong(Context ctx, String key, long value) {
        getDefaultPrefsEditor(ctx).putLong(key, value).apply();
    }

    public static long getLong(Context ctx, String key) {
        return getPrefManager(ctx).getLong(key, 0);
    }

    public static void saveBool(Context ctx, String key, boolean value) {
        getDefaultPrefsEditor(ctx).putBoolean(key, value).apply();
    }

    public static boolean getBool(Context ctx, String key) {
        return getPrefManager(ctx).getBoolean(key, false);
    }

    public static void saveBool(Context ctx, String prefsKey, String key, boolean value) {
        getPrefsEditor(ctx, prefsKey).putBoolean(key, value).apply();
    }

    public static boolean getBool(Context ctx, String prefsKey, String key) {
        return getPrefs(ctx, prefsKey).getBoolean(key, false);
    }
}
