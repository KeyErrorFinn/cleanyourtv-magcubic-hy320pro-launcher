"""Public edition boundary/installer tests. No device access."""
import importlib.util
from pathlib import Path
import unittest
from unittest.mock import patch
import xml.etree.ElementTree as ET

ROOT=Path(__file__).resolve().parents[1]
ANDROID='{http://schemas.android.com/apk/res/android}'
spec=importlib.util.spec_from_file_location('device',ROOT/'scripts/device.py')
device=importlib.util.module_from_spec(spec);spec.loader.exec_module(device)

class ReleaseTests(unittest.TestCase):
    def test_basic_has_no_advanced_permissions_or_providers(self):
        main=ET.parse(ROOT/'app/src/main/AndroidManifest.xml').getroot()
        permissions={x.get(ANDROID+'name') for x in main.findall('uses-permission')}
        for permission in ['WRITE_SECURE_SETTINGS','WRITE_SETTINGS','CHANGE_CONFIGURATION','BLUETOOTH_CONNECT','BLUETOOTH_SCAN']:
            self.assertNotIn('android.permission.'+permission,permissions)
        providers={x.get(ANDROID+'name') for x in main.find('application').findall('provider')}
        self.assertFalse({'.SettingsSessionProvider','.StarterStatusProvider','.ProjectionRecoveryProvider'} & providers)

    def test_no_helper_pairing_or_native_writer_in_default_branch(self):
        # Switching from Ultimate can leave ignored compiler output; it must not be mistaken for
        # tracked companion source, or become part of a main-branch build.
        for relative in ['settings-starter/AndroidManifest.xml', 'settings-starter/build.py',
                         'settings-starter/src/local/hy320/helperstarter/StarterRunner.java',
                         'app/src/advanced/cpp/CMakeLists.txt']:
            self.assertFalse((ROOT/relative).exists(), relative)
        forbidden = ['SettingsSessionClient', 'ShellSettingsSession', 'StarterStatusProvider',
                     'SettingsSessionProvider', 'NativePairing', 'ManualCornerBackend', 'local.hy320.helperstarter']
        for directory in ['main', 'basic', 'advanced']:
            source = ROOT/'app/src'/directory
            for path in source.rglob('*.java'):
                text = path.read_text(encoding='utf-8')
                for name in forbidden:
                    self.assertNotIn(name, text, str(path))
                for key in ['adb_wifi_enabled', 'adb_enabled']:
                    self.assertNotIn('"'+key+'"', text, str(path))

    def test_advanced_has_no_helper_providers_or_network_permission(self):
        root=ET.parse(ROOT/'app/src/advanced/AndroidManifest.xml').getroot()
        self.assertIsNone(root.find('permission'))
        self.assertFalse(root.findall('.//provider'))
        for manifest in ['main', 'advanced']:
            tree=ET.parse(ROOT/'app/src'/manifest/'AndroidManifest.xml').getroot()
            permissions={x.get(ANDROID+'name') for x in tree.findall('uses-permission')}
            self.assertNotIn('android.permission.INTERNET',permissions)
            self.assertNotIn('android.permission.CHANGE_CONFIGURATION',permissions)

    def test_every_adb_call_selects_device(self):
        result=type('Result',(),{'returncode':0,'stdout':b'ok','stderr':b''})()
        with patch.object(device.subprocess,'run',return_value=result) as run:
            selected=device.Device('confirmed-projector:12345')
            selected.shell('getprop ro.build.id')
        command=run.call_args.args[0]
        self.assertEqual(command[1:4],['-s','confirmed-projector:12345','shell'])

    def test_guard_rejects_firmware_mismatch_before_mutation(self):
        selected=device.Device('confirmed-projector:12345')
        with patch.object(selected,'shell',return_value='different firmware') as shell:
            with self.assertRaises(RuntimeError): selected.guard()
        self.assertEqual(shell.call_count,1)

    def test_unknown_recovery_state_blocks_update(self):
        selected=device.Device('confirmed-projector:12345')
        with patch.object(selected,'installed',return_value=True), patch.object(selected,'shell',side_effect=[
                'com.gathin.tvhome.projection-recovery','busy=0']):
            with self.assertRaises(RuntimeError): selected.quiescent()

    def test_active_helper_blocks_update(self):
        selected=device.Device('confirmed-projector:12345')
        status=' '.join(x+'=0' for x in ['orientation_pending','manual_pending','reset_pending','scaling_pending','automatic_pending','busy'])
        with patch.object(selected,'installed',return_value=True), patch.object(selected,'shell',side_effect=[
                'com.gathin.tvhome.projection-recovery',status,'connected=true']):
            with self.assertRaises(RuntimeError): selected.quiescent()

if __name__=='__main__': unittest.main()
