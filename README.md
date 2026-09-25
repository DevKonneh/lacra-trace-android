# 🌍 LACRA App

Mobile and web application designed to help supply chain actors collect and transfer geolocation data of commodity production areas to support compliance with the **EU Regulation on Deforestation-Free Products (EUDR)**.

---

## 🧩 Overview

The **EUDR**, effective from the end of 2025, requires companies importing commodities (e.g., coffee, cocoa, etc.) into the EU to prove that their products are:
- **Deforestation-free** — produced on land not deforested after 2020
- **Legally sourced** — compliant with local production laws

Many supply chain actors lack reliable **geolocation data** to meet these due diligence requirements.  
**LACRA App** addresses this gap by providing a simple, secure, and interoperable solution for collecting, storing, and sharing geolocation data across conventional supply chains.

---

## 🎯 Purpose

LACRA enables:
- **Data collection** — farmers, cooperatives, and traders can easily record land coordinates
- **Data transfer** — geolocation and transaction data are securely shared across the supply chain
- **EUDR readiness** — helps all actors comply with EU due diligence requirements without adding complexity

The solution focuses on **accessibility, privacy, and interoperability**, allowing smaller supply chain actors to participate in traceability systems.

---

## 🚀 Key Features

- Secure user authentication and role-based access
- Geolocation data capture (GPS-based)
- Recording of supply chain transactions
- Encrypted data transfer with user consent
- Offline functionality and local caching
- Interoperability with existing traceability systems
- Basic communication module for connected actors

---

## 🧱 Architecture

The project follows a layered **MVVM + Interactor** architecture with clear separation of concerns:

| Layer | Description |
|-------|--------------|
| **View** | Jetpack Compose screens (functions with suffix `Screen`) responsible for UI rendering and user interaction. |
| **Contract** | Defines screen bindings and communication contracts:<br>• `data class Binding` – UI state container<br>• `sealed class Event` – user events<br>• `sealed class Effect` – one-time side effects |
| **ViewModel** | Handles state management, event processing, and triggers domain logic via Interactors. |
| **Interactor** | Contains business logic and coordinates data operations from repositories. |
| **Repository** | Responsible for data access (local via Room and remote via Retrofit). |

This structure improves testability, readability, and maintainability.

---

## 🛠 Tech Stack

- **Language:** Kotlin  
- **UI:** Jetpack Compose  
- **Architecture:** MVVM + Interactor + Repository  
- **Networking:** Retrofit  
- **Database:** Room  
- **Dependency Injection:** Koin  
- **Cloud Services:** Firebase (Analytics, Crashlytics)

---

## 🚀 Getting Started

### Requirements
- Android Studio **Ladybug** (or newer)  
- JDK **17** (ensure correct version in *Project Structure*)  
- Minimum SDK: `28`

---

### 1️⃣ Clone the project
```bash
git clone https://github.com/<your-username>/<your-repo>.git
```

### 2️⃣ Prepare `config` folder

In the project root, create a folder named **`config/`** and add the following files:

#### 🗝 `signing.properties`
```properties
release_storeFile=path/to/your-release.jks
release_storePassword=your_password
release_keyAlias=your_alias
release_keyPassword=your_password

debug_storeFile=path/to/your-debug.jks
debug_storePassword=your_password
debug_keyAlias=your_alias
debug_keyPassword=your_password
```

#### 🔐 `secrets.properties`
```properties
MAPS_API_KEY=your_Google_Maps_API_key
GOOGLE_AUTH_CLIENT_ID=your_Google_Sign-In_client_ID
```

#### 🔐 `build.properties`
```properties
release_base_url="https://api_url"
release_default_country_code="CM"
release_default_location_latitude=4.106729
release_default_location_longitude=9.3972812

debug_base_url="https://api_url"
debug_default_country_code="CM"
debug_default_location_latitude=4.106729
debug_default_location_longitude=9.3972812
```

> ⚠️ These files are required for the project to build successfully.  
> They are already excluded in `.gitignore` — do **not** commit them.

---

### 3️⃣ Open and run
1. Open the project in **Android Studio**  
2. Let Gradle sync automatically  
3. Build and run the app on an emulator or physical device

---

## 🧪 Development Notes

- Clean and modular architecture with Compose UI separation  
- Offline caching handled via Room  
- Network sync via Retrofit  
- Dependency injection using Koin

---

## 📄 License
This project is licensed under the [MIT License](LICENSE).
