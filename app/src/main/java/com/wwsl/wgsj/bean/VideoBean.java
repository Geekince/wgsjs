package com.wwsl.wgsj.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;
import com.wwsl.wgsj.bean.net.VideoMusicBean;

import androidx.annotation.Nullable;


/**
 * Created by cxf on 2017/10/25.
 */
public class VideoBean implements Parcelable {

    private String id;
    private String uid;
    private String title;//视频文案内容
    @JSONField(name = "thumb")
    private String coverUrl;//封面
    @JSONField(name = "thumb_s")
    private String thumbs;
    @JSONField(name = "href")
    private String videoUrl;//视频播放资源
    @JSONField(name = "likes")
    private String likeNum;//点赞数
    @JSONField(name = "views")
    private String viewNum;//视频点击 观看数
    @JSONField(name = "comments")
    private String commentNum;//评论数
    @JSONField(name = "steps")
    private String stepNum;//踩数
    @JSONField(name = "shares")
    private String shareNum;//转发数
    private String addtime;
    private String lat;
    private String lng;
    private String city;
    @JSONField(name = "isdel")
    private String isDel;
    private String status;
    @JSONField(name = "music_id")
    private String musicId;
    @JSONField(name = "xiajia_reason")
    private String xjReason;
    @JSONField(name = "show_val")
    private String showVal;
    @JSONField(name = "nopass_time")
    private String nopass_time;
    @JSONField(name = "watch_ok")
    private String watch_ok;
    @JSONField(name = "is_ad")
    private String isAd;
    @JSONField(name = "ad_endtime")
    private String adEndTime;
    @JSONField(name = "ad_url")
    private String adUrl;
    @JSONField(name = "orderno")
    private String orderNum;
    private String images;
    private String type;
    @JSONField(name = "video_time")
    private String videoTime;
    private String price;
    @JSONField(name = "video_type")
    private String videoType;
    @JSONField(name = "votes")
    private String votes;
    @JSONField(name = "goods_id")
    private String goodsId;
    @JSONField(name = "update_time")
    private String updateTime;
    @JSONField(name = "city_id")
    private int cityId;
    @JSONField(name = "file_id")
    private String fileId;
    @JSONField(name = "at_user")
    private String atUser;
    @JSONField(name = "is_public")
    private String isPublic;
    @JSONField(name = "is_zn")
    private String isZn;
    private String tag;
    @JSONField(name = "userinfo")
    private UserBean userBean;//作者

    private String datetime;

    @JSONField(name = "islike")
    private int like;//是否已点赞
    @JSONField(name = "isstep")
    private int step;//是否踩过
    @JSONField(name = "isattent")
    private int attent;//是否已关注

    private int follow = 0;


    private String distance;//与视频发布距离

    @JSONField(name = "music")
    private VideoMusicBean musicInfo;
    @JSONField(name = "goods")
    private GoodsBean goods;

    @JSONField(name = "watch_ok")
    private String viewOkNum;//视频播放完 观看数

    public VideoBean(String id, String uid, String title, String coverUrl, String thumbs, String videoUrl, String likeNum, String viewNum, String commentNum, String stepNum, String shareNum, String addtime, String lat, String lng, String city, String isDel, String status, String musicId, String xjReason, String showVal, String nopass_time, String watch_ok, String isAd, String adEndTime, String adUrl, String orderNum, String images, String type, String videoTime, String price, String videoType, String votes, String goodsId, String updateTime, int cityId, String fileId, String atUser, String isPublic, String isZn, String tag, UserBean userBean, String datetime, int like, int step, int attent, int follow, String distance, VideoMusicBean musicInfo, GoodsBean goods, String viewOkNum) {
        this.id = id;
        this.uid = uid;
        this.title = title;
        this.coverUrl = coverUrl;
        this.thumbs = thumbs;
        this.videoUrl = videoUrl;
        this.likeNum = likeNum;
        this.viewNum = viewNum;
        this.commentNum = commentNum;
        this.stepNum = stepNum;
        this.shareNum = shareNum;
        this.addtime = addtime;
        this.lat = lat;
        this.lng = lng;
        this.city = city;
        this.isDel = isDel;
        this.status = status;
        this.musicId = musicId;
        this.xjReason = xjReason;
        this.showVal = showVal;
        this.nopass_time = nopass_time;
        this.watch_ok = watch_ok;
        this.isAd = isAd;
        this.adEndTime = adEndTime;
        this.adUrl = adUrl;
        this.orderNum = orderNum;
        this.images = images;
        this.type = type;
        this.videoTime = videoTime;
        this.price = price;
        this.videoType = videoType;
        this.votes = votes;
        this.goodsId = goodsId;
        this.updateTime = updateTime;
        this.cityId = cityId;
        this.fileId = fileId;
        this.atUser = atUser;
        this.isPublic = isPublic;
        this.isZn = isZn;
        this.tag = tag;
        this.userBean = userBean;
        this.datetime = datetime;
        this.like = like;
        this.step = step;
        this.attent = attent;
        this.follow = follow;
        this.distance = distance;
        this.musicInfo = musicInfo;
        this.goods = goods;
        this.viewOkNum = viewOkNum;
    }

    public VideoBean() {
    }

    private static int $default$follow() {
        return 0;
    }

    public static VideoBeanBuilder builder() {
        return new VideoBeanBuilder();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.uid);
        dest.writeString(this.title);
        dest.writeString(this.coverUrl);
        dest.writeString(this.thumbs);
        dest.writeString(this.videoUrl);
        dest.writeString(this.likeNum);
        dest.writeString(this.viewNum);
        dest.writeString(this.commentNum);
        dest.writeString(this.stepNum);
        dest.writeString(this.shareNum);
        dest.writeString(this.addtime);
        dest.writeString(this.lat);
        dest.writeString(this.lng);
        dest.writeString(this.city);
        dest.writeString(this.isDel);
        dest.writeString(this.status);
        dest.writeString(this.musicId);
        dest.writeString(this.xjReason);
        dest.writeString(this.showVal);
        dest.writeString(this.nopass_time);
        dest.writeString(this.watch_ok);
        dest.writeString(this.isAd);
        dest.writeString(this.adEndTime);
        dest.writeString(this.adUrl);
        dest.writeString(this.orderNum);
        dest.writeString(this.images);
        dest.writeString(this.type);
        dest.writeString(this.videoTime);
        dest.writeString(this.price);
        dest.writeString(this.videoType);
        dest.writeString(this.votes);
        dest.writeString(this.goodsId);
        dest.writeString(this.updateTime);
        dest.writeInt(this.cityId);
        dest.writeString(this.fileId);
        dest.writeString(this.atUser);
        dest.writeString(this.isPublic);
        dest.writeString(this.isZn);
        dest.writeString(this.tag);
        dest.writeParcelable(this.userBean, flags);
        dest.writeString(this.datetime);
        dest.writeInt(this.like);
        dest.writeInt(this.step);
        dest.writeInt(this.attent);
        dest.writeInt(this.follow);
        dest.writeString(this.distance);
        dest.writeString(this.viewOkNum);
        dest.writeParcelable(this.musicInfo, flags);
        dest.writeParcelable(this.goods, flags);
    }


    public VideoBean(Parcel in) {
        this.id = in.readString();
        this.uid = in.readString();
        this.title = in.readString();
        this.coverUrl = in.readString();
        this.thumbs = in.readString();
        this.videoUrl = in.readString();
        this.likeNum = in.readString();
        this.viewNum = in.readString();
        this.commentNum = in.readString();
        this.stepNum = in.readString();
        this.shareNum = in.readString();
        this.addtime = in.readString();
        this.lat = in.readString();
        this.lng = in.readString();
        this.city = in.readString();
        this.isDel = in.readString();
        this.status = in.readString();
        this.musicId = in.readString();
        this.xjReason = in.readString();
        this.showVal = in.readString();
        this.nopass_time = in.readString();
        this.watch_ok = in.readString();
        this.isAd = in.readString();
        this.adEndTime = in.readString();
        this.adUrl = in.readString();
        this.orderNum = in.readString();
        this.images = in.readString();
        this.type = in.readString();
        this.videoTime = in.readString();
        this.price = in.readString();
        this.videoType = in.readString();
        this.votes = in.readString();
        this.goodsId = in.readString();
        this.updateTime = in.readString();
        this.cityId = in.readInt();
        this.fileId = in.readString();
        this.atUser = in.readString();
        this.isPublic = in.readString();
        this.isZn = in.readString();
        this.tag = in.readString();
        this.userBean = in.readParcelable(UserBean.class.getClassLoader());
        this.datetime = in.readString();
        this.like = in.readInt();
        this.step = in.readInt();
        this.attent = in.readInt();
        this.follow = in.readInt();
        this.distance = in.readString();
        this.viewOkNum = in.readString();
        this.musicInfo = in.readParcelable(VideoMusicBean.class.getClassLoader());
        this.goods = in.readParcelable(GoodsBean.class.getClassLoader());
    }


    public static final Creator<VideoBean> CREATOR = new Creator<VideoBean>() {
        @Override
        public VideoBean[] newArray(int size) {
            return new VideoBean[size];
        }

        @Override
        public VideoBean createFromParcel(Parcel in) {
            return new VideoBean(in);
        }
    };

    public String getTag() {
        return "VideoBean" + this.getId() + this.hashCode();
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof VideoBean)) return false;
        VideoBean bean = (VideoBean) obj;
        return bean.getId().equals(id);

    }

    public String getId() {
        return this.id;
    }

    public String getUid() {
        return this.uid;
    }

    public String getTitle() {
        return this.title;
    }

    public String getCoverUrl() {
        return this.coverUrl;
    }

    public String getThumbs() {
        return this.thumbs;
    }

    public String getVideoUrl() {
        return this.videoUrl;
    }

    public String getLikeNum() {
        return this.likeNum;
    }

    public String getViewNum() {
        return this.viewNum;
    }

    public String getCommentNum() {
        return this.commentNum;
    }

    public String getStepNum() {
        return this.stepNum;
    }

    public String getShareNum() {
        return this.shareNum;
    }

    public String getAddtime() {
        return this.addtime;
    }

    public String getLat() {
        return this.lat;
    }

    public String getLng() {
        return this.lng;
    }

    public String getCity() {
        return this.city;
    }

    public String getIsDel() {
        return this.isDel;
    }

    public String getStatus() {
        return this.status;
    }

    public String getMusicId() {
        return this.musicId;
    }

    public String getXjReason() {
        return this.xjReason;
    }

    public String getShowVal() {
        return this.showVal;
    }

    public String getNopass_time() {
        return this.nopass_time;
    }

    public String getWatch_ok() {
        return this.watch_ok;
    }

    public String getIsAd() {
        return this.isAd;
    }

    public String getAdEndTime() {
        return this.adEndTime;
    }

    public String getAdUrl() {
        return this.adUrl;
    }

    public String getOrderNum() {
        return this.orderNum;
    }

    public String getImages() {
        return this.images;
    }

    public String getType() {
        return this.type;
    }

    public String getVideoTime() {
        return this.videoTime;
    }

    public String getVideoType() {
        return this.videoType;
    }

    public String getVotes() {
        return this.votes;
    }

    public String getGoodsId() {
        return this.goodsId;
    }

    public String getUpdateTime() {
        return this.updateTime;
    }

    public int getCityId() {
        return this.cityId;
    }

    public String getFileId() {
        return this.fileId;
    }

    public String getAtUser() {
        return this.atUser;
    }

    public String getIsPublic() {
        return this.isPublic;
    }

    public String getIsZn() {
        return this.isZn;
    }

    public UserBean getUserBean() {
        return this.userBean;
    }

    public String getDatetime() {
        return this.datetime;
    }

    public int getLike() {
        return this.like;
    }

    public int getStep() {
        return this.step;
    }

    public int getAttent() {
        return this.attent;
    }

    public int getFollow() {
        return this.follow;
    }

    public String getDistance() {
        return this.distance;
    }

    public VideoMusicBean getMusicInfo() {
        return this.musicInfo;
    }

    public GoodsBean getGoods() {
        return this.goods;
    }

    public String getViewOkNum() {
        return this.viewOkNum;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public void setThumbs(String thumbs) {
        this.thumbs = thumbs;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public void setLikeNum(String likeNum) {
        this.likeNum = likeNum;
    }

    public void setViewNum(String viewNum) {
        this.viewNum = viewNum;
    }

    public void setCommentNum(String commentNum) {
        this.commentNum = commentNum;
    }

    public void setStepNum(String stepNum) {
        this.stepNum = stepNum;
    }

    public void setShareNum(String shareNum) {
        this.shareNum = shareNum;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setIsDel(String isDel) {
        this.isDel = isDel;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMusicId(String musicId) {
        this.musicId = musicId;
    }

    public void setXjReason(String xjReason) {
        this.xjReason = xjReason;
    }

    public void setShowVal(String showVal) {
        this.showVal = showVal;
    }

    public void setNopass_time(String nopass_time) {
        this.nopass_time = nopass_time;
    }

    public void setWatch_ok(String watch_ok) {
        this.watch_ok = watch_ok;
    }

    public void setIsAd(String isAd) {
        this.isAd = isAd;
    }

    public void setAdEndTime(String adEndTime) {
        this.adEndTime = adEndTime;
    }

    public void setAdUrl(String adUrl) {
        this.adUrl = adUrl;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setVideoTime(String videoTime) {
        this.videoTime = videoTime;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    public void setVotes(String votes) {
        this.votes = votes;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public void setAtUser(String atUser) {
        this.atUser = atUser;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }

    public void setIsZn(String isZn) {
        this.isZn = isZn;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public void setAttent(int attent) {
        this.attent = attent;
    }

    public void setFollow(int follow) {
        this.follow = follow;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setMusicInfo(VideoMusicBean musicInfo) {
        this.musicInfo = musicInfo;
    }

    public void setGoods(GoodsBean goods) {
        this.goods = goods;
    }

    public void setViewOkNum(String viewOkNum) {
        this.viewOkNum = viewOkNum;
    }

    public String toString() {
        return "VideoBean(id=" + this.getId() + ", uid=" + this.getUid() + ", title=" + this.getTitle() + ", coverUrl=" + this.getCoverUrl() + ", thumbs=" + this.getThumbs() + ", videoUrl=" + this.getVideoUrl() + ", likeNum=" + this.getLikeNum() + ", viewNum=" + this.getViewNum() + ", commentNum=" + this.getCommentNum() + ", stepNum=" + this.getStepNum() + ", shareNum=" + this.getShareNum() + ", addtime=" + this.getAddtime() + ", lat=" + this.getLat() + ", lng=" + this.getLng() + ", city=" + this.getCity() + ", isDel=" + this.getIsDel() + ", status=" + this.getStatus() + ", musicId=" + this.getMusicId() + ", xjReason=" + this.getXjReason() + ", showVal=" + this.getShowVal() + ", nopass_time=" + this.getNopass_time() + ", watch_ok=" + this.getWatch_ok() + ", isAd=" + this.getIsAd() + ", adEndTime=" + this.getAdEndTime() + ", adUrl=" + this.getAdUrl() + ", orderNum=" + this.getOrderNum() + ", images=" + this.getImages() + ", type=" + this.getType() + ", videoTime=" + this.getVideoTime() + ", price=" + this.getPrice() + ", videoType=" + this.getVideoType() + ", votes=" + this.getVotes() + ", goodsId=" + this.getGoodsId() + ", updateTime=" + this.getUpdateTime() + ", cityId=" + this.getCityId() + ", fileId=" + this.getFileId() + ", atUser=" + this.getAtUser() + ", isPublic=" + this.getIsPublic() + ", isZn=" + this.getIsZn() + ", tag=" + this.getTag() + ", userBean=" + this.getUserBean() + ", datetime=" + this.getDatetime() + ", like=" + this.getLike() + ", step=" + this.getStep() + ", attent=" + this.getAttent() + ", follow=" + this.getFollow() + ", distance=" + this.getDistance() + ", musicInfo=" + this.getMusicInfo() + ", goods=" + this.getGoods() + ", viewOkNum=" + this.getViewOkNum() + ")";
    }

    public static class VideoBeanBuilder {
        private String id;
        private String uid;
        private String title;
        private String coverUrl;
        private String thumbs;
        private String videoUrl;
        private String likeNum;
        private String viewNum;
        private String commentNum;
        private String stepNum;
        private String shareNum;
        private String addtime;
        private String lat;
        private String lng;
        private String city;
        private String isDel;
        private String status;
        private String musicId;
        private String xjReason;
        private String showVal;
        private String nopass_time;
        private String watch_ok;
        private String isAd;
        private String adEndTime;
        private String adUrl;
        private String orderNum;
        private String images;
        private String type;
        private String videoTime;
        private String price;
        private String videoType;
        private String votes;
        private String goodsId;
        private String updateTime;
        private int cityId;
        private String fileId;
        private String atUser;
        private String isPublic;
        private String isZn;
        private String tag;
        private UserBean userBean;
        private String datetime;
        private int like;
        private int step;
        private int attent;
        private int follow$value;
        private boolean follow$set;
        private String distance;
        private VideoMusicBean musicInfo;
        private GoodsBean goods;
        private String viewOkNum;

        VideoBeanBuilder() {
        }

        public VideoBean.VideoBeanBuilder id(String id) {
            this.id = id;
            return this;
        }

        public VideoBean.VideoBeanBuilder uid(String uid) {
            this.uid = uid;
            return this;
        }

        public VideoBean.VideoBeanBuilder title(String title) {
            this.title = title;
            return this;
        }

        public VideoBean.VideoBeanBuilder coverUrl(String coverUrl) {
            this.coverUrl = coverUrl;
            return this;
        }

        public VideoBean.VideoBeanBuilder thumbs(String thumbs) {
            this.thumbs = thumbs;
            return this;
        }

        public VideoBean.VideoBeanBuilder videoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
            return this;
        }

        public VideoBean.VideoBeanBuilder likeNum(String likeNum) {
            this.likeNum = likeNum;
            return this;
        }

        public VideoBean.VideoBeanBuilder viewNum(String viewNum) {
            this.viewNum = viewNum;
            return this;
        }

        public VideoBean.VideoBeanBuilder commentNum(String commentNum) {
            this.commentNum = commentNum;
            return this;
        }

        public VideoBean.VideoBeanBuilder stepNum(String stepNum) {
            this.stepNum = stepNum;
            return this;
        }

        public VideoBean.VideoBeanBuilder shareNum(String shareNum) {
            this.shareNum = shareNum;
            return this;
        }

        public VideoBean.VideoBeanBuilder addtime(String addtime) {
            this.addtime = addtime;
            return this;
        }

        public VideoBean.VideoBeanBuilder lat(String lat) {
            this.lat = lat;
            return this;
        }

        public VideoBean.VideoBeanBuilder lng(String lng) {
            this.lng = lng;
            return this;
        }

        public VideoBean.VideoBeanBuilder city(String city) {
            this.city = city;
            return this;
        }

        public VideoBean.VideoBeanBuilder isDel(String isDel) {
            this.isDel = isDel;
            return this;
        }

        public VideoBean.VideoBeanBuilder status(String status) {
            this.status = status;
            return this;
        }

        public VideoBean.VideoBeanBuilder musicId(String musicId) {
            this.musicId = musicId;
            return this;
        }

        public VideoBean.VideoBeanBuilder xjReason(String xjReason) {
            this.xjReason = xjReason;
            return this;
        }

        public VideoBean.VideoBeanBuilder showVal(String showVal) {
            this.showVal = showVal;
            return this;
        }

        public VideoBean.VideoBeanBuilder nopass_time(String nopass_time) {
            this.nopass_time = nopass_time;
            return this;
        }

        public VideoBean.VideoBeanBuilder watch_ok(String watch_ok) {
            this.watch_ok = watch_ok;
            return this;
        }

        public VideoBean.VideoBeanBuilder isAd(String isAd) {
            this.isAd = isAd;
            return this;
        }

        public VideoBean.VideoBeanBuilder adEndTime(String adEndTime) {
            this.adEndTime = adEndTime;
            return this;
        }

        public VideoBean.VideoBeanBuilder adUrl(String adUrl) {
            this.adUrl = adUrl;
            return this;
        }

        public VideoBean.VideoBeanBuilder orderNum(String orderNum) {
            this.orderNum = orderNum;
            return this;
        }

        public VideoBean.VideoBeanBuilder images(String images) {
            this.images = images;
            return this;
        }

        public VideoBean.VideoBeanBuilder type(String type) {
            this.type = type;
            return this;
        }

        public VideoBean.VideoBeanBuilder videoTime(String videoTime) {
            this.videoTime = videoTime;
            return this;
        }

        public VideoBean.VideoBeanBuilder price(String price) {
            this.price = price;
            return this;
        }

        public VideoBean.VideoBeanBuilder videoType(String videoType) {
            this.videoType = videoType;
            return this;
        }

        public VideoBean.VideoBeanBuilder votes(String votes) {
            this.votes = votes;
            return this;
        }

        public VideoBean.VideoBeanBuilder goodsId(String goodsId) {
            this.goodsId = goodsId;
            return this;
        }

        public VideoBean.VideoBeanBuilder updateTime(String updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        public VideoBean.VideoBeanBuilder cityId(int cityId) {
            this.cityId = cityId;
            return this;
        }

        public VideoBean.VideoBeanBuilder fileId(String fileId) {
            this.fileId = fileId;
            return this;
        }

        public VideoBean.VideoBeanBuilder atUser(String atUser) {
            this.atUser = atUser;
            return this;
        }

        public VideoBean.VideoBeanBuilder isPublic(String isPublic) {
            this.isPublic = isPublic;
            return this;
        }

        public VideoBean.VideoBeanBuilder isZn(String isZn) {
            this.isZn = isZn;
            return this;
        }

        public VideoBean.VideoBeanBuilder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public VideoBean.VideoBeanBuilder userBean(UserBean userBean) {
            this.userBean = userBean;
            return this;
        }

        public VideoBean.VideoBeanBuilder datetime(String datetime) {
            this.datetime = datetime;
            return this;
        }

        public VideoBean.VideoBeanBuilder like(int like) {
            this.like = like;
            return this;
        }

        public VideoBean.VideoBeanBuilder step(int step) {
            this.step = step;
            return this;
        }

        public VideoBean.VideoBeanBuilder attent(int attent) {
            this.attent = attent;
            return this;
        }

        public VideoBean.VideoBeanBuilder follow(int follow) {
            this.follow$value = follow;
            this.follow$set = true;
            return this;
        }

        public VideoBean.VideoBeanBuilder distance(String distance) {
            this.distance = distance;
            return this;
        }

        public VideoBean.VideoBeanBuilder musicInfo(VideoMusicBean musicInfo) {
            this.musicInfo = musicInfo;
            return this;
        }

        public VideoBean.VideoBeanBuilder goods(GoodsBean goods) {
            this.goods = goods;
            return this;
        }

        public VideoBean.VideoBeanBuilder viewOkNum(String viewOkNum) {
            this.viewOkNum = viewOkNum;
            return this;
        }

        public VideoBean build() {
            int follow$value = this.follow$value;
            if (!this.follow$set) {
                follow$value = VideoBean.$default$follow();
            }
            return new VideoBean(id, uid, title, coverUrl, thumbs, videoUrl, likeNum, viewNum, commentNum, stepNum, shareNum, addtime, lat, lng, city, isDel, status, musicId, xjReason, showVal, nopass_time, watch_ok, isAd, adEndTime, adUrl, orderNum, images, type, videoTime, price, videoType, votes, goodsId, updateTime, cityId, fileId, atUser, isPublic, isZn, tag, userBean, datetime, like, step, attent, follow$value, distance, musicInfo, goods, viewOkNum);
        }

        public String toString() {
            return "VideoBean.VideoBeanBuilder(id=" + this.id + ", uid=" + this.uid + ", title=" + this.title + ", coverUrl=" + this.coverUrl + ", thumbs=" + this.thumbs + ", videoUrl=" + this.videoUrl + ", likeNum=" + this.likeNum + ", viewNum=" + this.viewNum + ", commentNum=" + this.commentNum + ", stepNum=" + this.stepNum + ", shareNum=" + this.shareNum + ", addtime=" + this.addtime + ", lat=" + this.lat + ", lng=" + this.lng + ", city=" + this.city + ", isDel=" + this.isDel + ", status=" + this.status + ", musicId=" + this.musicId + ", xjReason=" + this.xjReason + ", showVal=" + this.showVal + ", nopass_time=" + this.nopass_time + ", watch_ok=" + this.watch_ok + ", isAd=" + this.isAd + ", adEndTime=" + this.adEndTime + ", adUrl=" + this.adUrl + ", orderNum=" + this.orderNum + ", images=" + this.images + ", type=" + this.type + ", videoTime=" + this.videoTime + ", price=" + this.price + ", videoType=" + this.videoType + ", votes=" + this.votes + ", goodsId=" + this.goodsId + ", updateTime=" + this.updateTime + ", cityId=" + this.cityId + ", fileId=" + this.fileId + ", atUser=" + this.atUser + ", isPublic=" + this.isPublic + ", isZn=" + this.isZn + ", tag=" + this.tag + ", userBean=" + this.userBean + ", datetime=" + this.datetime + ", like=" + this.like + ", step=" + this.step + ", attent=" + this.attent + ", follow$value=" + this.follow$value + ", distance=" + this.distance + ", musicInfo=" + this.musicInfo + ", goods=" + this.goods + ", viewOkNum=" + this.viewOkNum + ")";
        }
    }
}
