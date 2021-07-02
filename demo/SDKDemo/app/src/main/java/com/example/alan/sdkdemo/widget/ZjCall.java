package com.example.alan.sdkdemo.widget;


import java.io.Serializable;

/**
 * Created by wangzhen on 2017/11/7.
 */

public class ZjCall implements Serializable {

    private String displayName; //显示名称
    private String account;     //账号
    private String name;        //名称
    private String address;     //地址
    private String sipkey;      //短号
    private long date;          //时间
    private String pwd;         //密码
    private int isDot;          //点对点
    private int inCome;         //呼入
    private int isChair;        //主持人
    private int missed;         //未接通
    private String conference;  //被呼叫后进入临时会议室
    private String token;       //被呼叫入会token
    private String time;        //被呼叫入会时间
    private String bssKey;      //bssKey
    private String msgJson;     //被呼收到的json
    private boolean joinMuteAudio;//入会时静音
    private boolean joinMuteVideo;
    private String theme;
    private String channel;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    private String sipkeyServer;//临时保存短号和服务器地址拼接形式的呼叫地址
    private boolean isShitongPlatform;//是否对接视通平台，如果设置只对本次入会起作用
    private String apiServer;   //服务器地址，如果设置只对本次入会起作用


    private boolean isTurnOnTime = false; // 专属云通讯录定时器是否开启

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public boolean isTurnOnTime() {
        return isTurnOnTime;
    }

    public void setTurnOnTime(boolean turnOnTime) {
        isTurnOnTime = turnOnTime;
    }

    public ZjCall() {
    }

    public ZjCall(String name, String address, String sipkey, long date, String pwd, int isDot, int inCome, int isChair, int missed) {
        this.name = name;
        this.address = address;
        this.sipkey = sipkey;
        this.date = date;
        this.pwd = pwd;
        this.isDot = isDot;
        this.inCome = inCome;
        this.isChair = isChair;
        this.missed = missed;
    }

    public int isMissed() {
        return missed;
    }

    public void setMissed() {
        this.missed = 1;
    }

    public int isChair() {
        return isChair;
    }

    public void setIsChair() {
        this.isChair = 1;
    }

    public void setIsNotChair() {
        this.isChair = 0;
    }

    public int isDot() {
        return isDot;
    }

    public void setIsDot() {
        this.isDot = 1;
    }

    public int inCome() {
        return inCome;
    }

    public void setInCome(boolean inCome) {
        this.inCome = inCome ? 1 : 0;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAccount() {
        if (account == null)
            return "";
        else
            return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        if (name == null)
            return "";
        else
            return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        if (address == null)
            return "";
        else
            return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getPwd() {
        if (pwd == null)
            return "";
        else
            return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getSipkey() {
        if (sipkey == null)
            return "";
        else
            return sipkey;
    }

    public void setSipkey(String sipkey) {
        this.sipkey = sipkey;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBssKey() {
        return bssKey;
    }

    public void setBssKey(String bssKey) {
        this.bssKey = bssKey;
    }

    public String getConference() {
        return conference;
    }

    public void setConference(String conference) {
        this.conference = conference;
    }

    public String getMsgJson() {
        if (msgJson == null) {
            return "";
        } else {
            return msgJson;
        }
    }

    public void setMsgJson(String msgJson) {
        this.msgJson = msgJson;
    }

    public boolean isJoinMuteAudio() {
        return joinMuteAudio;
    }

    public void setJoinMuteAudio(boolean joinMuteAudio) {
        this.joinMuteAudio = joinMuteAudio;
    }

    public String getSipkeyServer() {
        return sipkeyServer;
    }

    public void setSipkeyServer(String sipkeyServer) {
        this.sipkeyServer = sipkeyServer;
    }

    public boolean isShitongPlatform() {
        return isShitongPlatform;
    }

    public void setShitongPlatform(boolean shitongPlatform) {
        isShitongPlatform = shitongPlatform;
    }

    public String getApiServer() {
        return apiServer;
    }

    public void setApiServer(String apiServer) {
        this.apiServer = apiServer;
    }

    public boolean isJoinMuteVideo() {
        return joinMuteVideo;
    }

    public void setJoinMuteVideo(boolean joinMuteVideo) {
        this.joinMuteVideo = joinMuteVideo;
    }
}
