# Smart Daily Expense Tracker - Android App

**Developer:** Abhilash Kumar Jha  
**Technology Stack:** Android, Jetpack Compose, Kotlin, MVVM Architecture

## 📱 App Overview

A comprehensive expense tracking application designed for small business owners, featuring real-time expense management, intelligent analytics, and offline-first architecture. The app provides an intuitive interface for tracking daily expenses, generating detailed reports, and gaining business insights through AI-powered analytics.

## 🤖 AI Usage Summary

This project was developed using **AI-native development practices** with extensive collaboration with AI assistants. Key AI usage included:
- **Architecture Design**: AI helped design Clean Architecture with MVVM pattern, implementing proper separation of concerns
- **Code Generation**: AI assisted in generating boilerplate code, UI components, and business logic implementations
- **Problem Solving**: AI helped debug issues, optimize performance, and implement complex features like real-time updates
- **Testing Strategy**: AI guided the creation of comprehensive unit tests for all layers (Domain, Data, Presentation)

## 📝 Prompt Logs

### Key Prompts Used:

**1. Initial Project Setup:**
```
"Build a full-featured Smart Daily Expense Tracker module for small business owners using Jetpack Compose with a clean MVVM architecture. Include Expense Entry Screen, Expense List Screen, Expense Report Screen, State Management & Data Layer, and Bonus Challenges like theme switcher, persist data locally, animation on add, duplicate detection, validation, offline-first sync, reusable UI components."
```

**2. Architecture Implementation:**
```
"Complete missing Clean Architecture components - implement Domain layer, UseCases, and proper Dependency Injection"
```

**3. Feature Implementation:**
```
"Go ahead with Phase 2 implementation" - Implemented theme switcher, enhanced animations, advanced search & filtering, enhanced charts & analytics, offline sync, intelligent insights, and performance optimization
```

**4. Testing Implementation:**
```
"Write unit test for each layer" and "Write unit test for other layer also domain, data presentation"
```

**5. CI/CD Integration:**
```
"Integrate CI/CD pipeline" and "Integrate SonarQube for code quality checking"
```

**6. Real-time Updates Fix:**
```
"The item which I am adding is showing in list screen only after second time app launch, but I want it to update immediately"
```

##  Checklist of Features Implemented

### **Phase 1: Core Features** 
- [x] **Expense Entry Screen**
  - Title, Amount, Category selection (Staff, Travel, Food, Utility)
  - Optional Notes (max 100 chars)
  - Optional Receipt Image upload
  - Real-time validation
  - Submit with success feedback
  - Total Spent Today display

- [x] **Expense List Screen**
  - View expenses for Today (default)
  - Previous dates via calendar/filter
  - Group by category or time (toggle)
  - Total count and amount display
  - Empty state handling
  - Real-time updates

- [x] **Expense Report Screen**
  - Last 7 days report
  - Daily totals
  - Category-wise totals
  - Mock charts (Bar/Line)
  - PDF/CSV export simulation

- [x] **State Management & Data Layer**
  - ViewModel + StateFlow
  - MVVM architecture
  - Repository pattern
  - Room database integration

### **Phase 2: Advanced Features** ✅
- [x] **Theme Switcher**
  - Light/Dark theme toggle
  - System theme detection
  - Theme consistency checker

- [x] **Enhanced Animations & Transitions**
  - Smooth screen transitions
  - Animated expense cards
  - Loading animations
  - Success/error feedback

- [x] **Advanced Search & Filtering**
  - Text search with debouncing
  - Date range filtering
  - Amount range filtering
  - Category filtering
  - Sort options (Date, Amount, Category)

- [x] **Enhanced Charts & Analytics**
  - Category breakdown charts
  - Spending trend analysis
  - Business intelligence insights
  - Interactive chart components

- [x] **Offline Sync & Data Persistence**
  - Room database
  - Data encryption
  - Background sync simulation
  - Conflict resolution

- [x] **Advanced Analytics & Intelligent Insights**
  - Spending pattern analysis
  - Predictive insights
  - Business recommendations
  - Performance metrics

- [x] **Performance Optimization**
  - Lazy loading
  - Pagination
  - Memory optimization
  - Smooth scrolling

### **Phase 3: Quality & DevOps** ✅
- [x] **Comprehensive Testing**
  - Unit tests for all layers
  - Domain layer tests
  - Data layer tests
  - Presentation layer tests
  - Mockito integration

- [x] **CI/CD Pipeline**
  - GitHub Actions workflow
  - Automated testing
  - Code coverage reporting
  - SonarQube integration
  - Quality gates

- [x] **Code Quality**
  - SonarQube analysis
  - Detekt static analysis
  - JaCoCo coverage
  - Lint checks

## 📥 APK Download Link

**APK File:** `app-debug.apk` (included in submission package)

**Installation Instructions:**
1. Enable "Install from Unknown Sources" in Android settings
2. Download and install the APK file
3. Launch the app and start tracking expenses

## 📸 Screenshots

**Note:** Screenshots are included in the `screenshots/` folder showing:
- Expense Entry Screen
- Expense List Screen
- Expense Report Screen
- Theme Switcher
- Search & Filtering
- Charts & Analytics
<img width="1080" height="2160" alt="Expense Entry Screen" src="https://github.com/user-attachments/assets/31b54ec2-84a1-452e-a5bb-5ba8473451d2" />

<img width="1080" height="2160" alt="Expense List Screen" src="https://github.com/user-attachments/assets/1f918c6e-0250-4c81-9dfb-3b4da33ac5ac" />

<img width="1080" height="2160" alt="Expense Report Screen" src="https://github.com/user-attachments/assets/486a91d0-3497-4895-a1e6-b70987002469" />

<img width="1080" height="2160" alt="Theme Switcher" src="https://github.com/user-attachments/assets/f1e87df6-cbde-4646-bee2-454a6b096551" />

<img width="1080" height="2160" alt="Charts   Analytics" src="https://github.com/user-attachments/assets/2e0b263a-ecbd-400c-ae0d-22008ec97a6e" />

-  

## 👨‍💻 Developer Information

**Name:** Abhilash Kumar Jha  
**Email:** abhilashjha264@gmail.com
**GitHub:** https://github.com/jhaji246
**LinkedIn:** https://www.linkedin.com/in/abhilash-jhaji/

## 🏗️ Technical Architecture

### **Clean Architecture Implementation:**
- **Domain Layer**: Entities, Use Cases, Repository Interfaces
- **Data Layer**: Data Models, DAO, Database, Repository Implementation
- **Presentation Layer**: ViewModels, UI Components, Navigation

### **Key Technologies:**
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM + Clean Architecture
- **Database**: Room with SQLite
- **State Management**: StateFlow, LiveData
- **Dependency Injection**: Manual DI (can be upgraded to Hilt)
- **Testing**: JUnit, Mockito, Coroutines Test
- **CI/CD**: GitHub Actions, SonarQube, Detekt

### **Project Structure:**
```
app/src/main/java/com/avi/smartdailyexpensetracker/
├── domain/           # Business logic, entities, use cases
├── data/            # Data layer, database, repository
├── ui/              # Presentation layer, screens, components
└── di/              # Dependency injection setup
```

## 🚀 Getting Started

### **Prerequisites:**
- Android Studio Arctic Fox or later
- Android SDK 24+
- Kotlin 1.9+

### **Build Instructions:**
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Build and run on device/emulator

### **Run Tests:**
```bash
./gradlew test                    # Unit tests
./gradlew connectedAndroidTest    # Instrumented tests
./gradlew jacocoTestReport        # Coverage report
```

## 📊 Code Quality Metrics

- **Code Coverage**: >80% (JaCoCo)
- **SonarQube Quality Gate**: PASSED
- **Detekt Analysis**: PASSED
- **Lint Checks**: PASSED

## 🔄 CI/CD Pipeline

**GitHub Actions Workflow:**
- Automated testing on every commit
- Code quality analysis with SonarQube
- Static analysis with Detekt
- Coverage reporting with JaCoCo
- Quality gate enforcement

## 📝 License

This project is created for educational and assignment purposes.

---

**Submission Package:** `Abhilash_Kumar_Jha_Assignment.zip`  
**Total Development Time:** 30 hours of Time
**AI Collaboration:** Extensive use of AI for architecture, coding, testing, and problem-solving
