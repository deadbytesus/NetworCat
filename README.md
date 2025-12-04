<h1 align="center">
<img src="./app/src/main/res/mipmap-xxxhdpi/ic_launcher.webp" alt="NetworCat logo. Literally just a cat mask." width="200"/>
</h1>

# NetworCat: SIM Spoofing Tool

NetworCat is simple Xposed (primarily LSPatch) module that spoofs SIM card and network data.  
This is useful for bypassing regional restrictions in apps like Pixel Thermometer or TikTok.

## How It Works

Xposed API enables hooking of system functions. This module intercepts TelephonyManager calls and returns fake SIM/network information:
- MCC/MNC codes
- Country ISO
- Operator name

## Usage

1. Install [LSPatch](https://github.com/JingMatrix/LSPatch/releases/) (non-root) or [LSPosed](https://github.com/LSPosed/LSPosed/releases) (root)
2. Install NetworCat APK from [releases](https://github.com/deadbytesus/NetworCat/releases)
3. Configure region settings in NetworCat app (Country ISO, MCC/MNC, Operator)
4. Enable NetworCat module in LSPosed/LSPatch Manager
5. Select target apps (TikTok, etc.)
6. Reboot device

## Configuration Examples

| Country | MCC/MNC | Operator | ISO |
|---------|---------|----------|-----|
| USA     | 310260  | T-Mobile | US  |
| Poland  | 26001   | Orange   | PL  |
| Germany | 26202   | Telekom  | DE  |

[Other countries(Google Sheets)](https://docs.google.com/spreadsheets/d/1PyU7-mht3HX99NkSKoj6aMDvo_xRZE5d-ehdGZxHGbE/edit?usp=sharing)

## Supported Hooks

```kt
getNetworkOperator()
getNetworkOperatorName()
getNetworkCountryIso()
getSimCountryIso()
getSimOperator()
getSimOperatorName()
```

## Inspiration

This project was inspired by [Carrier Vanity Name](https://github.com/nullbytepl/CarrierVanityName), 
but uses XPosed for more functionality

## License

NetworCat is under Apache License 2.0. All rights reserved.

## Disclaimer

Apps using LSPosed, LSPatch, or root access can potentially harm your device. Use at your own risk.
