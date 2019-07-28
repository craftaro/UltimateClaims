package com.songoda.ultimateclaims.member;

public enum ClaimRole {

    VISITOR(1), MEMBER(2), OWNER(3);

    private int index;

    ClaimRole(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
