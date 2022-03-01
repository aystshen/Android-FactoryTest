package com.ayst.factorytest.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class WiegandParam implements Serializable {
    public static final int FORMAT_26 = 26;
    public static final int FORMAT_34 = 34;

    @SerializedName("format")
    private Integer format;
    @SerializedName("value")
    private Integer value;

    public Integer getFormat() {
        return format;
    }

    public void setFormat(Integer format) {
        this.format = format;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public boolean isSupport() {
        return (format == FORMAT_26) || (format == FORMAT_34);
    }

    @Override
    public String toString() {
        return "WiegandItem{" +
                "format=" + format +
                ", value=" + value +
                '}';
    }
}
