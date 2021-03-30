package com.songoda.ultimateclaims.member;

import java.util.HashSet;
import java.util.Set;

public class ClaimPermissions {

    private final Set<ClaimPerm> permissions = new HashSet<>();

    public ClaimPermissions setAllowed(ClaimPerm perm, boolean allowed) {
        if (allowed)
            permissions.add(perm);
        else
            permissions.remove(perm);
        return this;
    }

    public boolean hasPermission(ClaimPerm perm) {
        return permissions.contains(perm);
    }
}
