package com.ayst.factorytest.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NarParam implements Serializable {

    @SerializedName("card")
    private Integer card;
    @SerializedName("device")
    private Integer device;
    @SerializedName("channels")
    private Integer channels;
    @SerializedName("rate")
    private Integer rate;
    @SerializedName("bits")
    private Integer bits;
    @SerializedName("period_size")
    private Integer periodSize;
    @SerializedName("period_cnt")
    private Integer periodCnt;
    @SerializedName("play_channel")
    private Integer playChannel;

    public Integer getCard() {
        return card;
    }

    public void setCard(Integer card) {
        this.card = card;
    }

    public Integer getDevice() {
        return device;
    }

    public void setDevice(Integer device) {
        this.device = device;
    }

    public Integer getChannels() {
        return channels;
    }

    public void setChannels(Integer channels) {
        this.channels = channels;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public Integer getBits() {
        return bits;
    }

    public void setBits(Integer bits) {
        this.bits = bits;
    }

    public Integer getPeriodSize() {
        return periodSize;
    }

    public void setPeriodSize(Integer periodSize) {
        this.periodSize = periodSize;
    }

    public Integer getPeriodCnt() {
        return periodCnt;
    }

    public void setPeriodCnt(Integer periodCnt) {
        this.periodCnt = periodCnt;
    }

    public Integer getPlayChannel() {
        return playChannel;
    }

    public void setPlayChannel(Integer playChannel) {
        this.playChannel = playChannel;
    }

    @Override
    public String toString() {
        return "NarParam{" +
                "card=" + card +
                ", device=" + device +
                ", channels=" + channels +
                ", rate=" + rate +
                ", bits=" + bits +
                ", periodSize=" + periodSize +
                ", periodCnt=" + periodCnt +
                ", playChannel=" + playChannel +
                '}';
    }
}
