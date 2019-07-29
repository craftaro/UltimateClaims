package com.songoda.ultimateclaims.claim;

public class ClaimSettings {

    private boolean hostileMobSpawning = true;
    private boolean fireSpread = true;
    private boolean mobGriefing = true;
    private boolean leafDecay = true;
    private boolean pvp = true;


    public boolean isHostileMobSpawning() {
        return this.hostileMobSpawning;
    }

    public ClaimSettings setHostileMobSpawning(boolean hostileMobSpawning) {
        this.hostileMobSpawning = hostileMobSpawning;
        return this;
    }

    public boolean isFireSpread() {
        return fireSpread;
    }

    public ClaimSettings setFireSpread(boolean fireSpread) {
        this.fireSpread = fireSpread;
        return this;
    }

    public boolean isMobGriefing() {
        return mobGriefing;
    }

    public ClaimSettings setMobGriefing(boolean mobGriefing) {
        this.mobGriefing = mobGriefing;
        return this;
    }

    public boolean isLeafDecay() {
        return leafDecay;
    }

    public ClaimSettings setLeafDecay(boolean leafDecay) {
        this.leafDecay = leafDecay;
        return this;
    }

    public boolean isPvp() {
        return pvp;
    }

    public ClaimSettings setPvp(boolean pvp) {
        this.pvp = pvp;
        return this;
    }
}
