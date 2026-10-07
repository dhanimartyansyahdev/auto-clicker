# Auto Clicker Android

Project Android native untuk aplikasi auto clicker sederhana yang memungkinkan user menentukan koordinat target dan interval klik otomatis.

## Fitur
- Input koordinat X dan Y
- Input interval klik dalam milidetik
- Start/Stop otomatis
- Integrasi Accessibility Service untuk simulasi klik di layar
- Instruksi pengaktifan layanan aksesibilitas di Android

## Cara menjalankan
1. Buka project ini di Android Studio.
2. Sync Gradle.
3. Jalankan aplikasi ke emulator atau device.
4. Buka menu "Accessibility" pada aplikasi atau masuk ke Settings > Accessibility > Auto Clicker.
5. Aktifkan Auto Clicker.
6. Masukkan X, Y, dan interval.
7. Tekan Start.

## Catatan
Aplikasi ini menggunakan Accessibility Service agar bisa mengirim event tap ke layar. Karena itu, user harus mengaktifkan layanan aksesibilitas dari Android setelah install.

## Struktur utama
- `app/src/main/java/com/example/autoclicker/MainActivity.kt` - UI utama
- `app/src/main/java/com/example/autoclicker/AutoClickService.kt` - logic auto click
- `app/src/main/res/xml/accessibility_service_config.xml` - konfigurasi Accessibility Service
