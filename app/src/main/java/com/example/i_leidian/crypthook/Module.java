package com.example.i_leidian.crypthook;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static android.content.Context.MODE_PRIVATE;
import static de.robv.android.xposed.XposedHelpers.findAndHookConstructor;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by i-leidian on 2017/6/28.
 */

public class Module implements IXposedHookLoadPackage{
    private String packagename;
    private static final int BYTE_MSK = 0xFF;
    private static final int HEX_DIGIT_MASK = 0xF;
    private static final int HEX_DIGIT_BITS = 4;
    private static final String HEX_DIGITS = "0123456789abcdef";
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        SharedPreferences shared=GlobalApplication.getInstance().getSharedPreferences("shared_pref",MODE_PRIVATE);
        packagename=shared.getString("packagename","");
        Log.d("packagename",packagename);
        if (loadPackageParam.packageName.equals("com.example.i_leidian.encrypt"))
        {
            XposedHelpers.findAndHookConstructor(SecretKeySpec.class, byte[].class, String.class, new XC_MethodHook() {
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    StringBuffer sb = new StringBuffer();
                    sb.append("SecretKeySpec(" + byteArrayToString((byte[]) param.args[0]) + "," + (String) param.args[1] + ")");

                }
            });
            XposedHelpers.findAndHookMethod(Cipher.class, "doFinal", byte[].class, new XC_MethodHook() {
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Log.d("加密原文", byteArrayToString((byte[]) param.args[0]));
                    Log.d("密文", byteArrayToString((byte[]) param.getResult()));
                }
            });
            XposedHelpers.findAndHookMethod(Cipher.class, "getIV", new XC_MethodHook() {
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Log.d("IV", (String) param.getResult());
                }
            });
            XposedHelpers.findAndHookConstructor(IvParameterSpec.class, byte[].class, new XC_MethodHook() {
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Log.d("IV", byteArrayToString((byte[]) param.args[0]));
                }
            });
            XposedHelpers.findAndHookMethod(SecureRandom.class, "setSeed", byte[].class, new XC_MethodHook() {
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Log.d("Seed", byteArrayToString((byte[]) param.args[0]));
                }
            });
            XposedHelpers.findAndHookMethod(Cipher.class, "getInstance", String.class, new XC_MethodHook() {
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Log.d("加密类型", (String) param.args[0]);
                }
            });
            XposedHelpers.findAndHookConstructor(PBEKeySpec.class, char[].class, byte[].class, int.class, int.class, new XC_MethodHook() {
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    //StringBuffer sb = new StringBuffer();
                    //sb.append("[PBEKeySpec] - Password: " + String.valueOf((char[])param.args[0]) + " || Salt: " +  byteArrayToString((byte[])param.args[1]));
                }
            });
            XposedHelpers.findAndHookMethod(MessageDigest.class, "getInstance", String.class, new XC_MethodHook() {

                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Log.d("哈希算法", (String) param.args[0]);
                }
            });
            XposedHelpers.findAndHookMethod(MessageDigest.class, "update", byte[].class, new XC_MethodHook() {

                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Log.d("哈希原文", byteArrayToString((byte[]) param.args[0]));
                }

            });

            XposedHelpers.findAndHookMethod(MessageDigest.class, "update", byte[].class, "int", "int", new XC_MethodHook() {

                protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                    Log.d("哈希原文", byteArrayToString((byte[]) param.args[0]));
                }

            });

            XposedHelpers.findAndHookMethod(MessageDigest.class, "update", ByteBuffer.class, new XC_MethodHook() {

                protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                    ByteBuffer bb = (ByteBuffer) param.args[0];

                    Log.d("哈希原文", byteArrayToString(bb.array()));
                }
            });

            //the computed one way hash value
            XposedHelpers.findAndHookMethod(MessageDigest.class, "digest", new XC_MethodHook() {

                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Log.d("digest", toHexString((byte[]) param.getResult()));
                }
            });
        }
    }
    public static String toHexString(final byte[] byteArray) {
        StringBuilder sb = new StringBuilder(byteArray.length * 2);
        for (int i = 0; i < byteArray.length; i++) {
            int b = byteArray[i] & BYTE_MSK;
            sb.append(HEX_DIGITS.charAt(b >>> HEX_DIGIT_BITS)).append(
                    HEX_DIGITS.charAt(b & HEX_DIGIT_MASK));
        }
        return sb.toString();
    }
    public static String byteArrayToString(byte[] input) {
        if(input==null)
            return "";
        String out = new String(input);
        int tmp = 0;
        for (int i = 0; i < out.length(); i++) {
            int c = out.charAt(i);
            if (c >= 32 && c < 127) {
                tmp++;
            }
        }
        if (tmp > (out.length() * 0.60)) {
            StringBuilder sb = new StringBuilder();
            for (byte b : input) {
                if (b >= 32 && b < 127)
                    sb.append(String.format("%c", b));
                else
                    sb.append('.');
            }
            out = sb.toString();

        } else {
            out = Base64.encodeToString(input, Base64.NO_WRAP);
        }
        return out;
    }

}
