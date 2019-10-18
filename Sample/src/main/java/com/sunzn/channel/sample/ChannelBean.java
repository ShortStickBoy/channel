package com.sunzn.channel.sample;

import com.sunzn.channel.library.ChannelBase;

public class ChannelBean extends ChannelBase {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getValue() {
        return name;
    }

}
