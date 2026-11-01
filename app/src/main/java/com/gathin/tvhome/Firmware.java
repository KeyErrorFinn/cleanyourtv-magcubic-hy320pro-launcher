package com.gathin.tvhome;

/** The HY320 Pro firmware builds the projector-specific controls have been run on. */
final class Firmware {
    static final String[] VERIFIED = {"Projector_20260713.153921", "Projector_20261014.163241"};

    private Firmware() { }

    static boolean verified(String buildId) {
        for (String id : VERIFIED) if (id.equals(buildId)) return true;
        return false;
    }
}
