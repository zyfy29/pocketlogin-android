package com.dx.mobile.captcha.demo;

import android.content.Context;
import android.util.Log;

import com.dx.mobile.captcha.DXCaptchaView;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Profiles {
    private static final String TAG = "PROFILES";
    public static List<HashMap<String, Object>> profiles = new ArrayList<>();
    static HashMap<String, Object> defaultProfile;

    public static void initDefaultProfileInto(DXCaptchaView captcha) {
        initProfileInto(defaultProfile, captcha);
    }

    private static void initProfileInto(HashMap<String, Object> profile, DXCaptchaView captcha) {
        if (profile == null) {
            return;
        }
        captcha.init((String) profile.get("appId"));
        captcha.initConfig(profile);
        if (profile.containsKey("PRIVATE_CLEAR_TOKEN")) {
            HashMap<String, String> tokeConfig = new HashMap<>();
            tokeConfig.put("PRIVATE_CLEAR_TOKEN", (String) profile.get("PRIVATE_CLEAR_TOKEN"));
            captcha.initTokenConfig(tokeConfig);
        }
    }


    /**
     * @param context appContext
     */
    //如果客户有自己的配置放在assets下，客户调用加载自己的profile.json
    public static void load(Context context) {
        try {
            File cacheFile = new File(context.getApplicationInfo().dataDir, "captcha-profile.json");
            String content;
            if (cacheFile.exists()) {
                try (InputStream is = new FileInputStream(cacheFile)) {
                    content = IOUtils.toString(is, "UTF-8");
                }
            } else {
                try (InputStream is = context.getAssets().open("captcha-profile.json")) {
                    content = IOUtils.toString(is, "UTF-8");
                }
            }

            JSONArray list = (JSONArray) new JSONTokener(content).nextValue();
            if (list.length() > 0) {
                for (int i = 0; i < list.length(); i++) {
                    JSONObject profile = list.optJSONObject(i);
                    if (profile != null) {
                        add(profile);
                    }
                }
                defaultProfile = toMap(list.optJSONObject(0));
            }
        } catch (IOException | JSONException e) {
            Log.w(TAG, "fail to load profile", e);
        }
        if (defaultProfile == null) {
            defaultProfile = new HashMap<>();
        }
    }

    static String add(JSONObject jprofile) throws JSONException {
        String profileName = (String) jprofile.optString("profileName");
        if (profileName == null || profileName.length() == 0) {
            profileName = "P" + profiles.size();
            jprofile.putOpt("profileName", profileName);
        }
        HashMap<String, Object> profile = toMap(jprofile);

        HashMap<String, Object> exists = find(profileName);
        if (exists != null) {
            exists.clear();
            exists.putAll(profile);
        } else {
            profiles.add(profile);
        }
        return profileName;
    }


    private static HashMap<String, Object> toMap(JSONObject profile) {
        if (profile == null) {
            return null;
        }
        HashMap<String, Object> out = new HashMap<>();
        for (Iterator<String> it = profile.keys(); it.hasNext(); ) {
            String key = it.next();
            Object value = profile.opt(key);
            if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            } else if (value instanceof JSONArray) {
                throw new RuntimeException("not supported JSONArray");
            }
            out.put(key, value);
        }
        return out;
    }

    public static void store(Context context) {
        try {
            File cacheFile = new File(context.getApplicationInfo().dataDir, "cache.json");
            String json = new JSONArray(profiles).toString();

            try (OutputStream os = new FileOutputStream(cacheFile);) {
                IOUtils.write(json, os, "UTF-8");
            }
        } catch (IOException e) {
            Log.w(TAG, "fail to save profile", e);
        }
    }

    public static HashMap<String, Object> find(String profileName) {
        for (HashMap<String, Object> p : profiles) {
            if (profileName.equals(p.get("profileName"))) {
                return p;
            }
        }
        return null;
    }
}
