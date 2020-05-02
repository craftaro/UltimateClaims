package com.songoda.ultimateclaims.member;

public class ClaimPermissions {

    private boolean canInteract = false;
    private boolean canBreak = false;
    private boolean canPlace = false;
    private boolean canMobKill = false;
    private boolean canRedstone = false;
    private boolean canDoors = false;
    private boolean canTrade = false;

    public ClaimPermissions setCanTrade(boolean canTrade) {
        this.canTrade = canTrade;
        return this;
    }

    public ClaimPermissions setCanInteract(boolean canInteract) {
        this.canInteract = canInteract;
        return this;
    }

    public ClaimPermissions setCanBreak(boolean canBreak) {
        this.canBreak = canBreak;
        return this;
    }

    public ClaimPermissions setCanPlace(boolean canPlace) {
        this.canPlace = canPlace;
        return this;
    }

    public ClaimPermissions setCanMobKill(boolean canMobKill) {
        this.canMobKill = canMobKill;
        return this;
    }

    public ClaimPermissions setCanRedstone(boolean canRedstone) {
        this.canRedstone = canRedstone;
        return this;
    }

    public ClaimPermissions setCanDoors(boolean canDoors) {
        this.canDoors = canDoors;
        return this;
    }

    public boolean hasPermission(ClaimPerm perm) {
        switch (perm) {
            case BREAK:
                return canBreak;
            case PLACE:
                return canPlace;
            case INTERACT:
                return canInteract;
            case MOB_KILLING:
                return canMobKill;
            case REDSTONE:
                return canRedstone;
            case DOORS:
                return canDoors;
            case TRADING:
                return canTrade;
            default:
                return false;
        }
    }
}
