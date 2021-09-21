package com.romoufer.takewater.settings;

public class SettingsApp {

    int id;
    String switchDoAll;
    String switchOnlyNotify;
    String switchDoNothing;
    int timeInterval;
    int valueML;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSwitchDoAll() {
        return switchDoAll;
    }

    public void setSwitchDoAll(String switchDoAll) {
        this.switchDoAll = switchDoAll;
    }

    public String getSwitchOnlyNotify() {
        return switchOnlyNotify;
    }

    public void setSwitchOnlyNotify(String switchOnlyNotify) {
        this.switchOnlyNotify = switchOnlyNotify;
    }

    public String getSwitchDoNothing() {
        return switchDoNothing;
    }

    public void setSwitchDoNothing(String switchDoNothing) {
        this.switchDoNothing = switchDoNothing;
    }

    public int getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(int timeInterval) {
        this.timeInterval = timeInterval;
    }

    public int getValueML() {
        return valueML;
    }

    public void setValueML(int valueML) {
        this.valueML = valueML;
    }
}
