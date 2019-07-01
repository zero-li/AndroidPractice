package com.zhhli.sip.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;


public class SipUtils {

    @SuppressLint("StaticFieldLeak")
    private static Context context;


    /**
     * 初始化工具类
     *
     * @param context 应用
     */
    public static void init(@NonNull final Context context) {
        SipUtils.context = context.getApplicationContext();
    }

    /**
     * 获取Application
     *
     * @return Application
     */
    public static Context getContext() {
        if (context != null){
            return context;
        }
        throw new NullPointerException("u should init first");
    }


    public static String getNumber(String remoteUri){
        String number = "";

        if(remoteUri.contains(":")){
            int index1= remoteUri.indexOf(":");

            int index2 = remoteUri.indexOf("@",index1);

            number = remoteUri.substring(index1+1,index2);
        }

        return number;
    }



}
