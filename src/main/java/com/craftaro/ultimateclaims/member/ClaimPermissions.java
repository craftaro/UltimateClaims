package com.craftaro.ultimateclaims.member;

import com.craftaro.core.locale.Locale;
import com.craftaro.ultimateclaims.UltimateClaims;

import java.util.HashSet;
import java.util.Set;

public class ClaimPermissions {
    private final Set<ClaimPerm> permissions = new HashSet<>();

    public ClaimPermissions setAllowed(ClaimPerm perm, boolean allowed) {
        if (allowed) {
            this.permissions.add(perm);
        } else {
            this.permissions.remove(perm);
        }
        return this;
    }

    public boolean hasPermission(ClaimPerm perm) {
        return this.permissions.contains(perm);
    }

    public String getStatus(ClaimPerm perm) {
        Locale locale = UltimateClaims.getInstance().getLocale();
        return hasPermission(perm) ? locale.getMessage("general.status.true").getMessage() : locale.getMessage("general.status.false").getMessage();
    }
}
