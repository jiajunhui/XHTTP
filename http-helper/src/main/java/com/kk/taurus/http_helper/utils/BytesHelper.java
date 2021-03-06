/*
 * Copyright 2017 jiajunhui
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.kk.taurus.http_helper.utils;

import java.math.BigDecimal;

/**
 * Created by Taurus on 2016/12/23.
 */

public class BytesHelper {
    private static final String UNIT_KB = "KB";
    private static final String UNIT_MB = "MB";
    private static final String UNIT_GB = "GB";

    private static final int DEFAULT_DECIMAL_DIGITS = 1;

    private static final long VALUE_KB_BYTES = 1024;
    private static final long VALUE_MB_BYTES = 1024*1024;
    private static final long VALUE_GB_BYTES = 1024*1024*1024;

    public static String formatBytes(long bytes){
        if(bytes<VALUE_MB_BYTES){
            float KB = (float) (bytes*1.0/VALUE_KB_BYTES);
            return getDecimalPrice(KB,DEFAULT_DECIMAL_DIGITS) + UNIT_KB;
        }else if(bytes<VALUE_GB_BYTES){
            float MB = (float) (bytes*1.0/VALUE_MB_BYTES);
            return getDecimalPrice(MB,DEFAULT_DECIMAL_DIGITS) + UNIT_MB;
        }else{
            float GB = (float) (bytes*1.0/VALUE_GB_BYTES);
            return getDecimalPrice(GB,DEFAULT_DECIMAL_DIGITS) + UNIT_GB;
        }
    }

    public static String formatBytes(long bytes, int decimalDigits){
        if(bytes<VALUE_MB_BYTES){
            float KB = (float) (bytes*1.0/VALUE_KB_BYTES);
            return getDecimalPrice(KB,decimalDigits) + UNIT_KB;
        }else if(bytes<VALUE_GB_BYTES){
            float MB = (float) (bytes*1.0/VALUE_MB_BYTES);
            return getDecimalPrice(MB,decimalDigits) + UNIT_MB;
        }else{
            float GB = (float) (bytes*1.0/VALUE_GB_BYTES);
            return getDecimalPrice(GB,decimalDigits) + UNIT_GB;
        }
    }

    public static String getDecimalPrice(float price, int decimalDigits){
        BigDecimal bigDecimal = new BigDecimal(price);
        return bigDecimal.setScale(decimalDigits, BigDecimal.ROUND_DOWN).toString();
    }
}
