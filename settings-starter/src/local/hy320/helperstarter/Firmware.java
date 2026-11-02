package local.hy320.helperstarter;

/** The HY320 Pro firmware builds this app has been run on; the same list as the launcher's Firmware class. */
final class Firmware {
    static final String[] VERIFIED = {"Projector_20260713.153921", "Projector_20261014.163241"};

    private Firmware() { }

    static boolean verified(String buildId) {
        for (String id : VERIFIED) if (id.equals(buildId)) return true;
        return false;
    }
}
