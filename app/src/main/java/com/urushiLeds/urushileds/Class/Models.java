package com.urushiLeds.urushileds.Class;

public class Models {
    private String modelName,channel1,channel2,channel3,channel4;
    int channel;

    public Models() {
    }

    public Models(String modelName, String channel1, String channel2, String channel3, String channel4, int channel) {
        this.modelName = modelName;
        this.channel1 = channel1;
        this.channel2 = channel2;
        this.channel3 = channel3;
        this.channel4 = channel4;
        this.channel = channel;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getChannel1() {
        return channel1;
    }

    public void setChannel1(String channel1) {
        this.channel1 = channel1;
    }

    public String getChannel2() {
        return channel2;
    }

    public void setChannel2(String channel2) {
        this.channel2 = channel2;
    }

    public String getChannel3() {
        return channel3;
    }

    public void setChannel3(String channel3) {
        this.channel3 = channel3;
    }

    public String getChannel4() {
        return channel4;
    }

    public void setChannel4(String channel4) {
        this.channel4 = channel4;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }
}
