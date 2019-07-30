package com.songoda.ultimateclaims.member;

public class ClaimPermissions {

    private boolean canInteract = false;
    private boolean canBreak = false;
    private boolean canPlace = false;
    private boolean canMobKill = false;

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
            default:
                return false;
        }
    }
}
