package com.kk.taurus.demo.http;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Taurus on 2017/2/4.
 */

public class WxArticleRsp implements Serializable {

    public int error_code;
    public String reason;
    public WxArticleList result;

    public static class WxArticleList{
        public int totalPage;
        public int ps;
        public int pno;
        public List<WxArticle> list;
    }

    public static class WxArticle implements Serializable{
        public String id;
        public String title;
        public String source;
        public String firstImg;
        public String mark;
        public String url;
    }

}
