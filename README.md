# Kartownik 💳

Aplikacja mobilna na system Android do przechowywania i skanowania kart lojalnościowych, klubowych oraz sklepowych w jednym miejscu.

**Autor:** Erakles  
**Wersja:** 1.0.0  

---

## 🚀 Główne funkcje

1. **Błyskawiczne skanowanie kodów aparatem:**
   * Obsługa Google ML Kit Barcode Scanning w czasie rzeczywistym.
   * Wsparcie dla popularnych formatów: CODE 128, EAN-13, EAN-8, QR Code, CODE 39, Aztec, PDF417.
2. **Generowanie czytelnych kodów kreskowych i QR:**
   * Silnik ZXing renderujący kody w wysokiej rozdzielczości.
   * **Automatyczne rozjaśnianie ekranu telefonu do 100%** po wejściu w kartę – eliminuje problemy z odczytem przez skanery kasowe w marketach.
3. **Katalog gotowych sklepów (Presety):**
   * Gotowe kolory i konfiguracje dla: Biedronka, Lidl, Rossmann, Hebe, Żabka, Carrefour, Auchan, IKEA Family, Kaufland, Castorama, Leroy Merlin, Media Expert, RTV Euro AGD, Empik, CCC, Decathlon, Action, Pepco, Shell, Orlen i opcja własnej karty.
4. **Wyszukiwanie i Ulubione:**
   * Dynamiczne przeszukiwanie portfela po nazwie sklepu lub notatce.
   * Oznaczanie kart gwiazdką (Ulubione) i filtrowanie jednym kliknięciem.
5. **Widget na pulpit Androida:**
   * Widget wyświetlający status portfela, liczbę zapisanych kart i szybkie przejście do aplikacji z ekranu głównego.
6. **100% Prywatności (Offline-First):**
   * Wszystkie dane są zapisywane wyłącznie w lokalnej bazie danych SQLite / Room na Twoim telefonie.
   * Brak wymogu logowania, brak śledzenia, brak wysyłania danych do chmury.

---

## 🛠️ Architektura i Technologie

* **Język:** Kotlin
* **UI:** Jetpack Compose + Material 3
* **Architektura:** MVVM (Model-View-ViewModel) + Coroutines & Flow
* **Baza danych:** Room Database
* **Skanowanie:** CameraX + Google ML Kit Vision Barcode Scanning
* **Generowanie kodów:** ZXing Core
* **Widget:** Android AppWidgetProvider
* **Nawigacja:** Jetpack Navigation Compose

---

## 📱 Jak uruchomić projekt

1. Pobierz lub otwórz folder projektu w **Android Studio** (wersja Hedgehog, Iguana lub Jellyfish).
2. Poczekaj na automatyczną synchronizację Gradle (`Sync Project with Gradle Files`).
3. Podłącz telefon z Androidem (włączone debugowanie USB) lub uruchom emulator (np. Pixel 7 / Android 14).
4. Kliknij zielony przycisk **Run ('app')** (Shift + F10).
