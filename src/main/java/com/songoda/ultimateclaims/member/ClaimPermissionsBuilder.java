package com.songoda.ultimateclaims.member;

public class ClaimPermissionsBuilder {

    private ClaimPermissions claimPermissions;

    public ClaimPermissionsBuilder() {
        this.claimPermissions = new ClaimPermissions();
    }

    public ClaimPermissionsBuilder setCanInteract(boolean option) {
        this.claimPermissions.setCanInteract(option);
        return this;
    }

    public ClaimPermissionsBuilder setCanBuild(boolean option) {
        this.claimPermissions.setCanBreak(option);
        return this;
    }

    public ClaimPermissionsBuilder setCanPlace(boolean option) {
        this.claimPermissions.setCanPlace(option);
        return this;
    }

    public ClaimPermissions build() {
        return claimPermissions;
    }
}
