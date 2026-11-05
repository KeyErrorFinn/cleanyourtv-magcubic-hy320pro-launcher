"""Verify final helper-free APK boundaries and publisher signatures; never contacts a device."""
from pathlib import Path
import hashlib
import json
import os
import subprocess
import zipfile

ROOT = Path(__file__).resolve().parents[1]
DIST = ROOT/'dist/helper-free'
SDK = Path(os.environ['ANDROID_HOME'])/'build-tools/34.0.0'
def main():
    metadata = json.loads((DIST/'release.json').read_text())
    reports = []
    for edition in ['Basic', 'Advanced']:
        apk = DIST/f'CleanYourTV-HY320Pro-{edition}.apk'
        digest = hashlib.sha256(apk.read_bytes()).hexdigest()
        assert digest == metadata['artifacts'][apk.name]
        assert digest == Path(str(apk)+'.sha256').read_text().split()[0]
        permissions = subprocess.check_output([str(SDK/'aapt2.exe'), 'dump', 'permissions', str(apk)], text=True)
        manifest = subprocess.check_output([str(SDK/'aapt2.exe'), 'dump', 'xmltree', str(apk), '--file', 'AndroidManifest.xml'], text=True)
        for token in ['android.permission.INTERNET', 'android.permission.CHANGE_CONFIGURATION',
                      'com.gathin.tvhome.settings-session', 'com.gathin.tvhome.starter-status',
                      'com.gathin.tvhome.projection-recovery', 'com.gathin.tvhome.permission.SETTINGS_STARTER']:
            assert token not in permissions+manifest, (apk.name, token)
        if edition == 'Basic':
            for token in ['WRITE_SECURE_SETTINGS', 'WRITE_SETTINGS', 'BLUETOOTH_SCAN', 'BLUETOOTH_CONNECT']:
                assert token not in permissions, (apk.name, token)
        with zipfile.ZipFile(apk) as archive:
            assert not any(name.startswith('lib/') for name in archive.namelist()), apk.name
            for name in archive.namelist():
                if not name.endswith('.dex'): continue
                data = archive.read(name)
                for token in [b'adb_wifi_enabled', b'adb_enabled', b'local.hy320.helperstarter',
                              b'ShellSettingsSession', b'_adb-tls-connect._tcp', b'_adb-tls-pairing._tcp']:
                    assert token not in data, (apk.name, token)
        signature = subprocess.check_output([str(SDK/'apksigner.bat'), 'verify', '--print-certs', str(apk)], text=True)
        assert metadata['certificate_sha256'] in signature, apk.name
        reports.append({'edition': edition, 'bytes': apk.stat().st_size, 'sha256': digest,
                        'signature_verified': True, 'helper_free_boundaries': 'pass'})
    print(json.dumps(reports, indent=2))
    return reports

if __name__ == '__main__': main()
