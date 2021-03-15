package com.songoda.ultimateclaims.claim;

import java.util.HashSet;
import java.util.Set;

public class ClaimSettings {

    private final Set<ClaimSetting> settings = new HashSet<>();

    public ClaimSettings setEnabled(ClaimSetting setting, boolean enabled) {
        if (enabled)
            settings.add(setting);
        else
            settings.remove(setting);
        return this;
    }

    public boolean isEnabled(ClaimSetting setting) {
        return settings.contains(setting);
    }

}
