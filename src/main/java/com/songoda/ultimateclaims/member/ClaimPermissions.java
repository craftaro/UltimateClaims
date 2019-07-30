package com.songoda.ultimateclaims.member;

public class ClaimPermissions {

    private boolean canInteract = false;
    private boolean canBreak = false;
    private boolean canPlace = false;
    private boolean canMobKill = false;

    public void setCanInteract(boolean canInteract) {
        this.canInteract = canInteract;
    }

    public void setCanBreak(boolean canBreak) {
        this.canBreak = canBreak;
    }

    public void setCanPlace(boolean canPlace) {
        this.canPlace = canPlace;
    }

    public void setCanMobKill(boolean canMobKill) {
        this.canMobKill = canMobKill;
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
