package com.songoda.ultimateclaims.claim;

import com.songoda.ultimateclaims.settings.Settings;

public class ClaimSettings {

    private boolean hostileMobSpawning = Settings.DEFAULT_CLAIM_HOSTILE_MOB_SPAWN.getBoolean();
    private boolean fireSpread = Settings.DEFAULT_CLAIM_FIRE_SPREAD.getBoolean();
    private boolean mobGriefing = Settings.DEFAULT_CLAIM_MOB_GRIEFING.getBoolean();
    private boolean leafDecay = Settings.DEFAULT_CLAIM_LEAF_DECAY.getBoolean();
    private boolean pvp = Settings.DEFAULT_CLAIM_PVP.getBoolean();
    private boolean tnt = Settings.DEFAULT_CLAIM_TNT.getBoolean();

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

    public boolean isMobGriefingAllowed() {
        return mobGriefing;
    }

    public ClaimSettings setMobGriefingAllowed(boolean mobGriefing) {
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

    public boolean isTnt() {
        return tnt;
    }

    public ClaimSettings setTnt(boolean tnt) {
        this.tnt = tnt;
        return this;
    }
}
