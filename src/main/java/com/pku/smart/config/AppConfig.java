package com.pku.smart.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class AppConfig {
    /**
     * 实例演示开关
     */
    @Value("${smart.demoEnabled}")
    private boolean demoEnabled;

    /**
     * 上传路径
     */
    @Value("${smart.profile}")
    private static String profile;

    /**
     * 获取地址开关
     */
    @Value("${smart.addressEnabled}")
    private static boolean addressEnabled;

    public boolean isDemoEnabled() {
        return demoEnabled;
    }

    public void setDemoEnabled(boolean demoEnabled) {
        this.demoEnabled = demoEnabled;
    }

    public static String getProfile() {
        return profile;
    }

    public static void setProfile(String profile) {
        AppConfig.profile = profile;
    }

    public static boolean isAddressEnabled() {
        return addressEnabled;
    }

    public static void setAddressEnabled(boolean addressEnabled) {
        AppConfig.addressEnabled = addressEnabled;
    }

    /**
     * 获取头像上传路径
     */
    public static String getAvatarPath() {
        return getProfile() + "/avatar";
    }

    /**
     * 获取下载路径
     */
    public static String getDownloadPath() {
        return getProfile() + "/download/";
    }

    /**
     * 获取上传路径
     */
    public static String getUploadPath() {
        return getProfile() + "/upload";
    }
}
