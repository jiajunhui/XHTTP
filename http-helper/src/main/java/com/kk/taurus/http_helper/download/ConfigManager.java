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

package com.kk.taurus.http_helper.download;

import android.text.TextUtils;

import com.kk.taurus.http_helper.utils.GeneratorMD5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Taurus on 2017/2/6.
 */

public class ConfigManager {

    public static DownloadConfig loadConfig(DownloadRequest downloadRequest){
        File file = new File(downloadRequest.getDesDir(),downloadRequest.getRename());
        File config = getConfigFile(downloadRequest);
        if(config.exists() && file.exists()){
            Map<String, String> configMap = readConfig(config);
            if(configMap!=null && configMap.size()>0){
                return mapTransObject(file,configMap);
            }
        }
        return null;
    }

    public static boolean deleteConfig(DownloadRequest downloadRequest){
        File config = getConfigFile(downloadRequest);
        if(config.exists())
            return config.delete();
        return false;
    }

    private static File getConfigFile(DownloadRequest downloadRequest){
        return new File(downloadRequest.getDesDir(), md5FileName(downloadRequest.getUrl()));
    }

    private static String md5FileName(String url){
        return GeneratorMD5.md5(url);
    }

    private static Map<String, String> readConfig(File file){
        BufferedReader bufferedReader = null;
        try {
            Map<String,String> result = new HashMap<>();
            bufferedReader = new BufferedReader(new FileReader(file));
            String line = null;
            while((line=bufferedReader.readLine())!=null){
                String[] keyV = line.split(":");
                if(keyV!=null && keyV.length==2){
                    result.put(keyV[0],keyV[1]);
                }
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if(bufferedReader!=null){
                    bufferedReader.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void writeConfig(DownloadRequest downloadRequest,DownloadConfig downloadConfig){
        Map<String,String> result = objectTransMap(downloadConfig);
        if(result!=null && result.size()>0){
            StringBuilder sb = new StringBuilder();
            for(String key : result.keySet()){
                sb.append(key).append(":").append(result.get(key)).append("\n");
            }
            File configFile = new File(downloadRequest.getDesDir(), md5FileName(downloadRequest.getUrl()));
            String info = sb.toString();
            writeToFile(configFile.getAbsolutePath(),info,false);
        }
    }

    public static DownloadConfig mapTransObject(File file, Map<String,String> map){
        DownloadConfig downloadConfig = new DownloadConfig();
        downloadConfig.setCurrSize(file.length());
        downloadConfig.setUpdateTime(Long.parseLong(map.get(DownloadConfig.KEY_UPDATE_TIME)));
        downloadConfig.setTotalSize(Long.parseLong(map.get(DownloadConfig.KEY_TOTAL_SIZE)));
        return downloadConfig;
    }

    public static Map<String,String> objectTransMap(DownloadConfig downloadConfig){
        Map<String,String> result = new HashMap<>();
        result.put(DownloadConfig.KEY_UPDATE_TIME,String.valueOf(System.currentTimeMillis()));
        result.put(DownloadConfig.KEY_TOTAL_SIZE,String.valueOf(downloadConfig.getTotalSize()));
        return result;
    }

    private static void writeToFile(String fileName, String content, boolean append){
        FileOutputStream outputStream = null;
        try {
            if(TextUtils.isEmpty(content) || TextUtils.isEmpty(fileName))
                return;
            outputStream = new FileOutputStream(fileName,append);
            outputStream.write(content.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(outputStream!=null){
                try {
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class DownloadConfig{

        public static final String KEY_UPDATE_TIME = "update_time";
        public static final String KEY_TOTAL_SIZE = "total_size";

        private long updateTime;
        private long currSize;
        private long totalSize;

        public long getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
        }

        public long getCurrSize() {
            return currSize;
        }

        public void setCurrSize(long currSize) {
            this.currSize = currSize;
        }

        public long getTotalSize() {
            return totalSize;
        }

        public void setTotalSize(long totalSize) {
            this.totalSize = totalSize;
        }
    }

}
