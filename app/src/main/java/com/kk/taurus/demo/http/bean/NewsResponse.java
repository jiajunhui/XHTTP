package com.kk.taurus.demo.http.bean;

import com.kk.taurus.http_helper.bean.AbsResponse;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Taurus on 2017/4/10.
 */

public class NewsResponse extends AbsResponse<NewsResponse.NewsData>{

    public static class NewsData{
        public int code;
        public List<New> news;
    }

    public static class New implements Serializable{
        public String title;
        public String link;
    }

}
