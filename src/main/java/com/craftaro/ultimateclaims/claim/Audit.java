package com.craftaro.ultimateclaims.claim;

import java.util.UUID;

public class Audit {

    private UUID who;
    private long when;

    public Audit(UUID who, long when) {
        this.who = who;
        this.when = when;
    }

    public UUID getWho() {
        return who;
    }

    public void setWho(UUID who) {
        this.who = who;
    }

    public long getWhen() {
        return when;
    }

    public void setWhen(long when) {
        this.when = when;
    }
}
