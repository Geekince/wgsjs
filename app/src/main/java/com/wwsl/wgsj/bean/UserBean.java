package com.wwsl.wgsj.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created by cxf on 2017/8/14.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class UserBean implements Parcelable {

    protected String id;
    @JSONField(name = "user_nicename")
    protected String username;
    protected String mobile;
    protected String avatar;
    @JSONField(name = "avatar_thumb")
    protected String avatarThumb;
    protected int sex;
    protected String signature;

    /**
     * 座右铭
     */
    protected String coin;
    protected String votes;
    protected String consumption;
    protected String votestotal;
    protected String province;
    protected String city;
    protected String birthday;
    protected int level;
    @JSONField(name = "level_anchor")
    protected int levelAnchor;
    /**
     * 直播数量
     */
    protected int lives;
    @JSONField(name = "dznums")
    protected int dzNum;

    @JSONField(name = "myvideonums")
    protected int videoNum;

    @JSONField(name = "myvideolikenums")
    protected int likeVideoNum;
    /**
     * 关注数量
     */
    protected int follows;
    /**
     * 是否已关注
     */
    protected int follow;


    protected int fans;

    @JSONField(name = "haoyounums")
    protected int friendNum;//好友数量
    /**
     * 粉丝数量
     */
    protected int vip;
    @JSONField(name = "liang")
    protected String specialAccount;
    protected Car car;
    protected int circle;
    @JSONField(name = "parentinfo")
    protected ParentUser parentInfo;

    //是否直播已认证
    protected int auth;

    @JSONField(name = "live_thumb")
    protected String liveThumb;

    @JSONField(name = "tg_code")
    protected String tgCode;
    //大于0时，正在直播
    protected String islive;

    @JSONField(name = "living")
    protected LiveBean liveBean;

    @JSONField(name = "list")
    protected List<List<UserItemBean>> itemList;//个人中心功能列表

    @JSONField(name = "is_have_code")
    protected String isHaveCode;

    protected String age;

    protected int workCount;
    /**
     * 作品数量
     */

    protected int dynamicCount;
    /**
     * 动态数量
     */

    protected int likeCount;
    /**
     * 喜欢数量
     */

    @JSONField(name = "coin_password")
    protected String coinPassword;

    @JSONField(name = "money_rate")
    protected float moneyRate;
    @JSONField(name = "tips")
    protected String tips;

    @JSONField(name = "is_vip")
    protected int isVip;

    @JSONField(name = "is_eye_phone")
    protected int isPhonePublic;//是否允许他人查看手机号

    @JSONField(name = "is_up_video")
    protected int canUploadVideo;//是否可以上传视频

    @JSONField(name = "is_comment")
    protected int canComment;//是否可以评论

    @JSONField(name = "is_chuchuang")
    protected int isHaveShowWindow;//是否有橱窗功能


    @JSONField(name = "is_auth")
    protected int isIdIdentify;//是否实名认证

    @JSONField(name = "partner_id")
    protected String partnerId;//合伙人id

    @JSONField(name = "master_level")
    private int masterLevel;
    @JSONField(name = "task_nums")
    private int taskNum;
    @JSONField(name = "wechat")
    private String wxName;
    private String pid;//上级id
    @JSONField(name = "maodou")
    protected String maodou;//令牌数量
    @JSONField(name = "is_transfer_maodou")
    protected int canTransMd;//是否有好友转换
    @JSONField(name = "transfer_maodou_num")
    protected String maxTransNum;//最大令牌


    @JSONField(name = "mdsjsc_order")
    protected String marketUrl;//商城url

    @JSONField(name = "servicecharge")
    protected String depositRate;//提现比例
    @JSONField(name = "welfare_money_rate")
    protected String welfareDepositRate;//福利元宝提现比例

    @JSONField(name = "commission")
    protected String commission;//福利元宝


    @JSONField(name = "is_wechat")
    protected String isWxAuth;//微信是否授权

    @JSONField(name = "is_high_grade_auth")
    protected String isCanPubYxYp;//是否能发布一乡一品视频

    @JSONField(name = "zn_register_url")
    protected String znUrl;//助农url




    public int getLevel() {
        if (level == 0) {
            level = 1;
        }
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * 显示靓号
     */
    public String getSpecialNameTip() {
        if (this.specialAccount != null) {
            if (!TextUtils.isEmpty(specialAccount) && !"0".equals(specialAccount)) {
//                return WordUtil.getString(R.string.live_liang) + ":" + liangName;
                return specialAccount;
            }
        }
        return "" + this.id;
    }

    /**
     * 获取靓号
     */
    public String getGoodName() {
        if (this.specialAccount != null) {
            return this.specialAccount;
        }
        return "0";
    }


    protected UserBean(Parcel in) {
        this.id = in.readString();
        this.username = in.readString();
        this.age = in.readString();
        this.avatar = in.readString();
        this.avatarThumb = in.readString();
        this.signature = in.readString();
        this.coin = in.readString();
        this.coinPassword = in.readString();
        this.votes = in.readString();
        this.consumption = in.readString();
        this.votestotal = in.readString();
        this.province = in.readString();
        this.city = in.readString();
        this.birthday = in.readString();
        this.islive = in.readString();
        this.marketUrl = in.readString();
        this.isHaveCode = in.readString();
        this.partnerId = in.readString();
        this.tgCode = in.readString();
        this.tips = in.readString();
        this.maodou = in.readString();
        this.maxTransNum = in.readString();
        this.liveThumb = in.readString();
        this.wxName = in.readString();
        this.pid = in.readString();
        this.mobile = in.readString();
        this.specialAccount = in.readString();
        this.depositRate = in.readString();
        this.welfareDepositRate = in.readString();
        this.commission = in.readString();
        this.isWxAuth = in.readString();
        this.znUrl = in.readString();

        this.auth = in.readInt();
        this.canComment = in.readInt();
        this.canTransMd = in.readInt();
        this.canUploadVideo = in.readInt();
        this.circle = in.readInt();
        this.sex = in.readInt();
        this.dynamicCount = in.readInt();
        this.dzNum = in.readInt();
        this.level = in.readInt();
        this.levelAnchor = in.readInt();
        this.lives = in.readInt();
        this.follows = in.readInt();
        this.follow = in.readInt();
        this.friendNum = in.readInt();
        this.masterLevel = in.readInt();
        this.isHaveShowWindow = in.readInt();
        this.isIdIdentify = in.readInt();
        this.isPhonePublic = in.readInt();
        this.isVip = in.readInt();
        this.likeCount = in.readInt();
        this.likeVideoNum = in.readInt();
        this.taskNum = in.readInt();
        this.fans = in.readInt();
        this.vip = in.readInt();
        this.videoNum = in.readInt();
        this.workCount = in.readInt();
        this.moneyRate = in.readFloat();
        this.car = in.readParcelable(Car.class.getClassLoader());
        this.liveBean = in.readParcelable(LiveBean.class.getClassLoader());
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.username);
        dest.writeString(this.age);
        dest.writeString(this.avatar);
        dest.writeString(this.avatarThumb);
        dest.writeString(this.signature);
        dest.writeString(this.coin);
        dest.writeString(this.coinPassword);
        dest.writeString(this.votes);
        dest.writeString(this.consumption);
        dest.writeString(this.votestotal);
        dest.writeString(this.province);
        dest.writeString(this.city);
        dest.writeString(this.birthday);
        dest.writeString(this.islive);
        dest.writeString(this.marketUrl);
        dest.writeString(this.isHaveCode);
        dest.writeString(this.tgCode);
        dest.writeString(this.tips);
        dest.writeString(this.maodou);
        dest.writeString(this.maxTransNum);
        dest.writeString(this.liveThumb);
        dest.writeString(this.wxName);
        dest.writeString(this.partnerId);
        dest.writeString(this.pid);
        dest.writeString(this.mobile);
        dest.writeString(this.specialAccount);
        dest.writeString(this.depositRate);
        dest.writeString(this.welfareDepositRate);
        dest.writeString(this.commission);
        dest.writeString(this.isWxAuth);
        dest.writeString(this.znUrl);

        dest.writeInt(this.auth);
        dest.writeInt(this.canComment);
        dest.writeInt(this.canTransMd);
        dest.writeInt(this.canUploadVideo);
        dest.writeInt(this.circle);
        dest.writeInt(this.sex);
        dest.writeInt(this.dynamicCount);
        dest.writeInt(this.dzNum);
        dest.writeInt(this.level);
        dest.writeInt(this.levelAnchor);
        dest.writeInt(this.lives);
        dest.writeInt(this.follows);
        dest.writeInt(this.follow);
        dest.writeInt(this.friendNum);
        dest.writeInt(this.masterLevel);
        dest.writeInt(this.isHaveShowWindow);
        dest.writeInt(this.isIdIdentify);
        dest.writeInt(this.isPhonePublic);
        dest.writeInt(this.isVip);
        dest.writeInt(this.likeCount);
        dest.writeInt(this.likeVideoNum);
        dest.writeInt(this.taskNum);
        dest.writeInt(this.fans);
        dest.writeInt(this.vip);
        dest.writeInt(this.videoNum);
        dest.writeInt(this.workCount);
        dest.writeFloat(this.moneyRate);
        dest.writeParcelable(this.car, flags);
        dest.writeParcelable(this.liveBean, flags);
    }

    public static final Creator<UserBean> CREATOR = new Creator<UserBean>() {
        @Override
        public UserBean[] newArray(int size) {
            return new UserBean[size];
        }

        @Override
        public UserBean createFromParcel(Parcel in) {
            return new UserBean(in);
        }
    };


    public static class Vip implements Parcelable {
        protected int type;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public Vip() {

        }

        public Vip(Parcel in) {
            this.type = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.type);
        }

        public static final Creator<Vip> CREATOR = new Creator<Vip>() {
            @Override
            public Vip[] newArray(int size) {
                return new Vip[size];
            }

            @Override
            public Vip createFromParcel(Parcel in) {
                return new Vip(in);
            }
        };
    }

    public static class SpecialAccount implements Parcelable {
        protected String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public SpecialAccount() {

        }

        public SpecialAccount(Parcel in) {
            this.name = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.name);
        }

        public static final Creator<SpecialAccount> CREATOR = new Creator<SpecialAccount>() {

            @Override
            public SpecialAccount createFromParcel(Parcel in) {
                return new SpecialAccount(in);
            }

            @Override
            public SpecialAccount[] newArray(int size) {
                return new SpecialAccount[size];
            }
        };

    }


    public static class ParentUser implements Parcelable {


        /**
         * id : 10000000
         * user_nicename : 刘犇
         * avatar : http://mdsj.wenzuxz.com//public/images/headimg/6.jpg
         */

        private String id;
        @JSONField(name = "user_nicename")
        private String username;
        private String avatar;
        private String mobile;


        public ParentUser() {

        }

        protected ParentUser(Parcel in) {
            id = in.readString();
            username = in.readString();
            avatar = in.readString();
            mobile = in.readString();
        }

        public static final Creator<ParentUser> CREATOR = new Creator<ParentUser>() {
            @Override
            public ParentUser createFromParcel(Parcel in) {
                return new ParentUser(in);
            }

            @Override
            public ParentUser[] newArray(int size) {
                return new ParentUser[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(username);
            dest.writeString(avatar);
            dest.writeString(mobile);
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }

    public static class Car implements Parcelable {
        protected int id;
        protected String swf;
        protected float swftime;
        protected String words;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getSwf() {
            return swf;
        }

        public void setSwf(String swf) {
            this.swf = swf;
        }

        public float getSwftime() {
            return swftime;
        }

        public void setSwftime(float swftime) {
            this.swftime = swftime;
        }

        public String getWords() {
            return words;
        }

        public void setWords(String words) {
            this.words = words;
        }

        public Car() {

        }

        public Car(Parcel in) {
            this.id = in.readInt();
            this.swf = in.readString();
            this.swftime = in.readFloat();
            this.words = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.id);
            dest.writeString(this.swf);
            dest.writeFloat(this.swftime);
            dest.writeString(this.words);
        }


        public static final Creator<Car> CREATOR = new Creator<Car>() {
            @Override
            public Car[] newArray(int size) {
                return new Car[size];
            }

            @Override
            public Car createFromParcel(Parcel in) {
                return new Car(in);
            }
        };
    }

}
