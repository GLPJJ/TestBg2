package simple.bean;

/**
 * Created by glp on 2016/9/27.
 */

public class BannerEy {// 过渡版本
    public String id; //banner ID
    public String title;//标题
    public String url; //跳转地址
    public boolean local = false;//是否本地图片
    public String image; //图片，url地址图片
    public int resId = 0;//本地图片
    public String content; //提示文字

    //本地
    public BannerEy(String title, String url, int resId) {
        this.title = title;
        this.local = true;
        this.url = url;
        this.resId = resId;
    }

    public BannerEy(BannerEntity data) {
        id = data.id;
        title = data.title;
        url = data.url;
        local = false;
        image = data.image;
        content = data.content;
    }

    public BannerEy(String title, String id, String url, String image) {
        this.id = id;
        this.title = title;
        this.local = false;
        this.url = url;
        this.image = image;
    }
}
