package com.songoda.ultimateclaims.member;

public enum ClaimRole {

    VISITOR(1), MEMBER(2), OWNER(3);

    private int index;

    ClaimRole(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    public static ClaimRole fromIndex(int index) {
        for (ClaimRole role : values())
            if (role.getIndex() == index)
                return role;
        return null;
    }
}
