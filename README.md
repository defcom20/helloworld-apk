# Hello World: Minimal Kotin APK Template

**Compile Android APKs in 2 minutes - ZERO installation required!**

![kotlinapktemplate_screenshot](https://github.com/user-attachments/assets/4c8cf552-da53-46a3-85a1-0be9b1c02577)


> **Just click "Use this template" and start coding - no Android Studio, no SDK, no JDK needed!**

## 🚀 How It Works

This template uses **GitHub Actions** to compile your APK automatically. You only need a GitHub account - no development tools required!

### 3-Step Process:
1. **Click "Use this template"** → Create your repository
2. **Edit code directly on GitHub**
3. **Download APK from Actions tab**

Your APK compiles automatically on every code change!

## 🎯 Get Started Now

### Step 1: Create Your Project
- Click the **"Use this template"** button above
- Name your new repository
- Create it

### Step 2: Customize Your App
Edit these files directly on GitHub:

| File | What to Change |
|------|----------------|
| `app/src/main/res/values/strings.xml` | App name & text |
| `app/src/main/res/layout/activity_main.xml` | UI layout |
| `app/src/main/res/values/colors.xml` | Color scheme |
| `app/src/main/java/.../MainActivity.kt` | App logic |

### Step 3: Get Your APK
1. After saving changes, go to **Actions** tab
2. Click the latest workflow run
3. Download the `app-debug-apk` artifact
4. Install on your Android device!

## 📁 Project Structure - What Each File Does

Here's what you need to know about the project files:

### 🎨 App Resources (Edit These)

**`app/src/main/res/values/strings.xml`**
- Contains all text strings for your app
- **Change**: App name, button texts, messages

**`app/src/main/res/values/colors.xml`**
- Defines your app's color scheme
- **Change**: Primary color, background colors, text colors

**`app/src/main/res/layout/activity_main.xml`**
- Your app's user interface layout
- **Change**: Add buttons, text views, images

**`app/src/main/java/com/example/helloworld/MainActivity.kt`**
- Your app's main logic
- **Change**: Add functionality, handle button clicks

### 🖼️ App Icons (Replace These)

**Icon files to replace in `mipmap-*` folders:**
- `mipmap-hdpi/ic_launcher.png` (72x72 pixels)
- `mipmap-mdpi/ic_launcher.png` (48x48 pixels)
- `mipmap-xhdpi/ic_launcher.png` (96x96 pixels)
- `mipmap-xxhdpi/ic_launcher.png` (144x144 pixels)
- `mipmap-xxxhdpi/ic_launcher.png` (192x192 pixels)

**Replace all these PNG files** with your own app icon at the correct sizes.

### ⚙️ Configuration Files (Usually Don't Edit)

**`app/build.gradle`** - App dependencies and build settings
**`build.gradle`** - Project-wide Gradle configuration
**`AndroidManifest.xml`** - App permissions and components
**`.github/workflows/build.yml`** - GitHub Actions auto-build script

### 🔐 Signing Scripts (For Advanced Use)

**`bash/utils-generate-keystore`** - Helps create signing keys for Play Store
**`bash/utils-compile-apk`** - Local compilation script

## 🛠️ Quick Customization Guide

### Change App Name
Edit `app/src/main/res/values/strings.xml`:
```xml
<string name="app_name">My Awesome App</string>
```

### Change Colors
Edit `app/src/main/res/values/colors.xml`:
```xml
<color name="colorPrimary">#FF3F51B5</color>
```

### Change Layout
Edit `app/src/main/res/layout/activity_main.xml` to add buttons, text, or images.

### Add Functionality
Edit `app/src/main/java/com/example/helloworld/MainActivity.kt` to make your app interactive.

## 🌟 Why This Template?

- ✅ **Zero installation** - code in browser, get APK
- ✅ **Instant setup** - 2 minutes from template to APK
- ✅ **Clean structure** - easy to understand and expand
- ✅ **Auto-compilation** - APK built on every code change

---

**Ready to start? Click "Use this template" above!** 🚀

### Need Signed APK for Play Store?
Run the signing script when you're ready to publish:
```bash
cd ./bash
./utils-generate-keystore
```
Then create a GitHub release to get your signed APK.
