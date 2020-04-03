package com.coronosafe.approval.util;

import org.apache.commons.lang3.RandomStringUtils;

public class DigiUtils {
    public static String getSecureString(){
        return RandomStringUtils.randomAlphanumeric(10);
    }
}
