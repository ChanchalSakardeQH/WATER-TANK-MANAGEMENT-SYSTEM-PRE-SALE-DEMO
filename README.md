<p align="center">
  <img src="docs/woodyouloveit-logo.png" alt="woodyouloveit" width="300">
</p>

<h1 align="center">Water Tanks Monitor System: Demo app</h1>

<p align="center">
  <img src="docs/mascot.png" alt="Smiling water drop mascot" width="120"><br>
  <b>Every drop counts.</b> Save precious water, and electricity.<br>
  A branded Android app that shows housing societies how the Water Tanks Monitor works, using sample data.
</p>

<p align="center">
  <img alt="Android 8.0+" src="https://img.shields.io/badge/Android-8.0%2B-0B7379">
  <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-2.0-0B7379">
  <img alt="Works offline" src="https://img.shields.io/badge/works-offline-D23A6E">
  <img alt="For cooperative housing societies" src="https://img.shields.io/badge/for-housing%20societies-D23A6E">
</p>

---

## Contents

- [About the Water Tanks Monitor System](#about-the-water-tanks-monitor-system)
- [How it works](#how-it-works)
- [Features](#features)
- [From survey to savings](#from-survey-to-savings)
- [The demo app](#the-demo-app)
- [Screenshots](#screenshots)
- [Building on Windows](#building-on-windows)
- [Project layout](#project-layout)
- [Brochure](#brochure)
- [Get a quote](#get-a-quote)

## About the Water Tanks Monitor System

Live tank levels on every phone, so tanks stop overflowing and running dry, the motor runs only as long as needed,
and the committee gets clear reports.

### The water worries every society knows

| Problem | What happens today |
|---|---|
| **Tanks overflow** | Water, and the electricity that pumped it, go down the drain. |
| **Tanks run dry** | Residents find out when the taps stop. |
| **Climbing to check** | Someone goes up to the roof or opens the sump. |
| **Motor left on** | Nobody knows how long it ran. |
| **Hidden leaks** | A slow loss at night goes unnoticed for weeks. |
| **No numbers** | The committee has complaints, not data. |

### Save precious water, and the electricity that pumps it

| 💧 Precious water | ⚡ Electricity |
|---|---|
| See the level before a tank overflows | Every overflow also wastes the power that pumped it up |
| The night leak check flags hidden losses early | See when a tank is nearly full, and switch the motor off on time |
| Daily use per tank, so waste stands out | Every motor run and its minutes are recorded |

- **Save effort:** no roof climbing, everyone checks on their phone.
- **Decide with numbers:** daily use, fills and PDF reports for meetings.

## How it works

```mermaid
flowchart LR
    S["<b>Sensor</b><br/>Above the water in each<br/>tank or compartment"] --> C["<b>Controller</b><br/>Works out level and litres;<br/>keeps months of history"]
    C --> L["<b>Level bar</b><br/>Colour LED bar<br/>where people pass"]
    C --> P["<b>Phones</b><br/>Live in the app<br/>or a browser"]
```

<p align="center">
  <img src="docs/phone-live-level.png" alt="Live level of a two-compartment overhead tank on a phone" width="260">
</p>

## Features

| Feature | Details |
|---|---|
| **Live level and litres** | For every tank and compartment |
| **Alerts** | Low water, sensor faults and filling |
| **Night leak check** | Flags a level that drops while nobody is using water |
| **Motor run time** | Every fill, per day |
| **Water used per day** | And the busiest hour |
| **Reports** | PDF, data export, WhatsApp sharing |
| **Self-healing** | With a built-in sensor test |
| **Updates over Wi-Fi** | No site visit needed |
| **Your data stays with you** | No cloud account |

## From survey to savings

1. **Site survey:** tanks, sumps, power and Wi-Fi.
2. **Installation:** tanks stay in use; no emptying.
3. **Set-up and training:** for the committee, in one session.
4. **Support:** updates and service visits.

## The demo app

This repository is the **WTMS Demo** Android app. It runs the full dashboard with three sample tanks, completely offline
from the app itself: no website, hosting or tank device is needed. Committees can try every feature before a site survey.

| Screen | What it does |
|---|---|
| Start-up | Teal splash with the save-water drop, then the demo |
| Demo | The full dashboard: Overview, Analytics, Setup, Log and Service. The **Try it** bar switches the motor on, opens a tap, starts a leak or disconnects a sensor, and the dashboard reacts like a real tank |
| Contact us | A form for the society's details. **Send on WhatsApp** opens WhatsApp with everything filled in. **Share another way** offers email, SMS and other apps |
| Water report | "Print or save as PDF" in Analytics opens Android's print screen |
| About | Version, what the product does, woodyouloveit.com |

**Sample tanks**

| Tank | Shows |
|---|---|
| Wing A overhead tank | Two compartments, each with its own sensor and LED bar, and a possible leak last night |
| Home loft tank | A 500 L loft tank whose calibration Smart calibration can correct |
| Underground sump | A 40,000 L sump with a single night-time drop that the leak check recognises as water use |

**Demo sign-ins:** Admin password `demo-admin` (settings and calibration), Service key `demo-service` (firmware update over Wi-Fi).

### Contact us form

| Section | Fields |
|---|---|
| Your society | Society name, address, city or area, PIN code |
| Point of contact | Contact person, role in the society, mobile, email, best time to call |
| Buildings and tanks | Buildings or wings, overhead tanks, underground sumps, average flats per building (the app shows the total flats) |
| Anything else | Message, and consent to be contacted |

Mobile (10-digit Indian number), PIN code, email and counts are checked before sending. A half-filled form is kept on
the phone until it's sent or cleared. Nothing is stored online by the app.

To change where enquiries go, edit `contact_whatsapp` and `contact_whatsapp_display` in
`android/app/src/main/res/values/strings.xml`.

The app ID is `com.woodyouloveit.wtms.demo`, so it installs next to the real Water Tanks Monitor app.

## Screenshots

| Overview | Filling | Analytics | Night leak check |
|:---:|:---:|:---:|:---:|
| <img src="docs/demo-overview.png" alt="Overview with a two-compartment tank and LED bars" width="200"> | <img src="docs/demo-filling.png" alt="Tank filling with the motor on" width="200"> | <img src="docs/demo-analytics.png" alt="Analytics summary tiles" width="200"> | <img src="docs/demo-leak-check.png" alt="Night leak check table" width="200"> |

## Building on Windows

The app uses the same self-setting-up build script as the Water Tanks Monitor app.

1. Put this folder next to the Water Tanks Monitor project, for example `D:\Android\Projects\WTMS-DEMO-APP`.
2. Double-click **`build-android.bat`**. The first time, it copies `tools\build-android.ps1` from the Water Tanks Monitor
   project and adapts it for the demo (app ID, file names, titles). Run `tools\get-build-script.bat` again whenever you
   update that script.
3. Choose **2** for a debug build installed on your phone, or build a release:

```bat
build-android.bat -Task Debug -Install -Launch
build-android.bat -Task Release -VersionName 1.0.0 -VersionCode 1
```

| Output | Location |
|---|---|
| APK and AAB | `release\android\<version>\woodyouloveit-wtms-demo-<version>-release.apk` and `.aab` |
| Signing key | Same as the real app: `%USERPROFILE%\.woodyouloveit\wtms-release.jks` |
| Tools | Java, the Android SDK and Gradle 8.14.3, shared with your other projects in `D:\Android` |

The `android\` folder also opens directly in Android Studio.

## Project layout

```
build-android.bat                  menu and build launcher
tools/get-build-script.ps1         copies and adapts the Water Tanks Monitor build script
docs/                              images used in this README
android/
  app/src/main/assets/demo/        the demo page (index.html) and its fonts, bundled for offline use
  app/src/main/java/com/woodyouloveit/wtms/demo/
    MainActivity.kt                branded toolbar, the demo in a WebView, printing, back button
    DemoBridge.kt                  window.WTMSDemo: Contact us and printing from inside the demo
    ContactActivity.kt             Contact us form, WhatsApp and sharing
    Enquiry.kt                     form fields, checks, message text, saved draft
  app/src/main/res/                colours, splash, launcher icon, form layout, texts
```

## Brochure

The two-page A4 society brochure, for printing or sharing with committees.

| Front | Back |
|:---:|:---:|
| <a href="docs/brochure-front.png"><img src="docs/brochure-front.png" alt="Brochure front: Every drop counts" width="360"></a> | <a href="docs/brochure-back.png"><img src="docs/brochure-back.png" alt="Brochure back: How it works, features and quote" width="360"></a> |

## Get a quote

**Get a quote for your society.** Book a site survey: we look at your tanks and send a quote.

- WhatsApp: [+91 92848 17975](https://wa.me/919284817975)
- Website: [woodyouloveit.com](https://woodyouloveit.com)

---

<sub>Fonts: Baloo 2 and Hind, SIL Open Font License (licence files in `android/app/src/main/assets/demo/fonts`).
© 2026 woodyouloveit.com</sub>
