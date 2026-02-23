# 📋 Checklist Manager App

Aplikasi Android untuk manajemen checklist berbasis card dengan fitur notifikasi, drag-drop reorder, dan UI yang indah.

---

## 🎯 Fitur Utama

✅ **Core Features**
- Create, Read, Update, Delete (CRUD) untuk Cards, Items, dan Categories
- Checklist items dengan status checked/unchecked
- Progress bar otomatis berdasarkan completed items
- 10 color presets pastel yang beautiful

✅ **Notifications**
- Notifikasi reminder 30 menit sebelum deadline
- Notifikasi pada waktu deadline (realtime)
- AlarmManager untuk scheduling
- Custom minutes input (15, 30, 45 menit)

✅ **UX/UI**
- Search global (card name & item name)
- Filter by category & status
- Drag-drop reorder untuk cards dan items
- Dark mode support
- Confetti animation saat item completed
- Skeleton loading indicator
- Empty state dengan ilustrasi

✅ **Architecture**
- MVVM + Clean Architecture
- SOLID Principles
- Hilt Dependency Injection
- Room Database
- StateFlow & Flow untuk reactive updates

---

## 🏗️ Architecture Overview

```
Presentation Layer (UI)
├── Screens (HomeScreen, CardDetailScreen)
├── ViewModels (HomeVM, CardDetailVM, CategoryVM)
├── UI State (HomeUiState, CardDetailUiState)
├── Components (CardComponent, ChecklistItemComponent, dll)
└── Theme (Colors, Typography)

Domain Layer (Business Logic)
├── Models (Card, ChecklistItem, Category)
├── Repository Interfaces
└── UseCase (optional)

Data Layer (Persistence)
├── Database (Room)
├── DAO (Data Access Objects)
├── Entities (CardEntity, ChecklistItemEntity, CategoryEntity)
├── Repositories (Implementations)
└── Mappers (Entity ↔ Domain)

Notification Layer
├── AlarmScheduler
├── NotificationManager
└── AlarmReceiver

DI Layer (Hilt)
├── DatabaseModule
├── RepositoryModule
├── NotificationModule
└── AppModule
```

---

## 📂 Project Structure

```
app/src/main/java/com/example/checklistapp/
├── MainActivity.kt                          # App Entry Point
├── presentation/
│   ├── screen/
│   │   ├── HomeScreen.kt                   # Main screen dengan list cards
│   │   └── CardDetailScreen.kt             # Detail view dengan items
│   ├── viewmodel/
│   │   ├── HomeViewModel.kt                # State management home
│   │   ├── CardDetailViewModel.kt          # State management detail
│   │   └── CategoryViewModel.kt            # State management categories
│   ├── uistate/
│   │   └── *UiState.kt                     # UI state & event classes
│   ├── components/
│   │   └── UIComponents.kt                 # Reusable Compose components
│   ├── theme/
│   │   ├── Color.kt                        # Material 3 color scheme
│   │   ├── Type.kt                         # Typography
│   │   ├── Theme.kt                        # Theme setup
│   │   └── colorpreset/
│   │       └── ColorPresets.kt             # 10 pastel colors
│   └── navigation/
│       └── Navigation.kt                   # Navigation graph
├── domain/
│   ├── model/
│   │   └── *.kt                            # Domain models
│   └── repository/
│       └── *Repository.kt                  # Repository interfaces
├── data/
│   ├── local/
│   │   ├── database/
│   │   │   ├── AppDatabase.kt
│   │   │   └── dao/
│   │   │       ├── CardDao.kt
│   │   │       ├── ChecklistItemDao.kt
│   │   │       └── CategoryDao.kt
│   │   ├── entity/
│   │   │   └── *.kt                        # Room entities
│   │   └── mapper/
│   │       └── *.kt                        # Entity mappers
│   └── repository/
│       └── *RepositoryImpl.kt               # Repository implementations
├── notification/
│   ├── AlarmScheduler.kt                   # Schedule alarms
│   ├── NotificationManager.kt              # Handle notifications
│   └── AlarmReceiver.kt                    # BroadcastReceiver
├── di/
│   ├── DatabaseModule.kt
│   ├── RepositoryModule.kt
│   ├── NotificationModule.kt
│   └── AppModule.kt
└── utils/
    ├── Constants.kt
    ├── DateTimeUtils.kt
    ├── DateTimeConverters.kt
    └── Extensions.kt
```

---

## 🚀 Setup & Installation

### Prerequisites
- Android Studio Hedgehog atau lebih baru
- Android SDK 24+
- Java 17+

### Step 1: Clone & Setup
```bash
# Clone repository
git clone <repo_url>
cd checklist-app

# Buka di Android Studio dan sync gradle
```

### Step 2: Build & Run
```bash
# Debug build
./gradlew assembleDebug

# Run on emulator
./gradlew installDebug

# Atau langsung dari Android Studio: Run (Shift+F10)
```

### Step 3: Grant Permissions
Saat pertama buka app, ijinkan permission untuk notifications:
- POST_NOTIFICATIONS
- SCHEDULE_EXACT_ALARM

---

## 📚 Database Schema

### Categories Table
```sql
CREATE TABLE categories (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL UNIQUE,
    createdAt TEXT NOT NULL
);
```

### Cards Table
```sql
CREATE TABLE cards (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    categoryId TEXT NOT NULL,
    colorPresetId INTEGER,
    position INTEGER,
    createdAt TEXT NOT NULL,
    updatedAt TEXT NOT NULL,
    FOREIGN KEY(categoryId) REFERENCES categories(id) ON DELETE CASCADE
);
```

### Checklist Items Table
```sql
CREATE TABLE checklist_items (
    id TEXT PRIMARY KEY,
    cardId TEXT NOT NULL,
    itemName TEXT NOT NULL,
    isChecked INTEGER DEFAULT 0,
    deadline TEXT,
    notificationTime TEXT,
    notificationMinutesBefore INTEGER DEFAULT 30,
    isNotificationEnabled INTEGER DEFAULT 0,
    position INTEGER,
    createdAt TEXT NOT NULL,
    FOREIGN KEY(cardId) REFERENCES cards(id) ON DELETE CASCADE
);
```

---

## 🎨 Color Presets (10 Pastel Colors)

1. **Pastel Pink** - Primary: #FFB3D9
2. **Pastel Blue** - Primary: #B3E5FC
3. **Pastel Green** - Primary: #C8E6C9
4. **Pastel Yellow** - Primary: #FFF9C4
5. **Pastel Purple** - Primary: #E1BEE7
6. **Pastel Peach** - Primary: #FFCCBC
7. **Pastel Mint** - Primary: #B2DFDB
8. **Pastel Lavender** - Primary: #DDA0DD
9. **Pastel Coral** - Primary: #FFAB91
10. **Pastel Cyan** - Primary: #80DEEA

Setiap preset memiliki: primary, background, text, accent, border colors.

---

## 🔔 Notification Flow

```
User adds item dengan deadline & notification
        ↓
CardDetailViewModel → scheduleItemNotifications()
        ↓
AlarmScheduler → scheduleAlarm() [2x: 30min before + deadline]
        ↓
AlarmManager set alarm untuk trigger waktu tertentu
        ↓
[Pada waktu trigger]
AlarmReceiver broadcast trigger
        ↓
NotificationManager → showNotification()
        ↓
User lihat notif di notification tray
```

---

## 🔄 User Flow

### Home Screen
1. User buka app → lihat list cards
2. Search/filter cards
3. Tap card → navigate ke detail
4. FAB → create card baru

### Card Detail
1. Lihat semua items dalam card
2. Check/uncheck items → update progress
3. Tap + FAB → add item baru
4. Long-press item → edit/delete
5. Drag-drop items → reorder
6. Tap edit icon → edit card (name, color, category)

### Item Management
1. Add item: input name, deadline, notification time
2. Notification: spinner pilih 15/30/45 menit
3. Save → schedule alarm
4. Update item → reschedule alarm
5. Delete item → cancel alarm

---

## 🧪 Testing Checklist

### UI Testing
- [ ] Home screen loads correctly
- [ ] Cards display dengan progress bar
- [ ] Search filters work
- [ ] Add card bottom sheet works
- [ ] Card detail screen loads items
- [ ] Check/uncheck item works
- [ ] Progress updates real-time

### Database Testing
- [ ] CRUD operations untuk cards
- [ ] CRUD operations untuk items
- [ ] CRUD operations untuk categories
- [ ] Cascade delete works
- [ ] Drag-drop position saved

### Notification Testing
- [ ] Alarm schedule correctly
- [ ] Notification appears at right time
- [ ] Notification has right title/message
- [ ] Multiple notifications work
- [ ] Cancel notification works

### Dark Mode Testing
- [ ] Light mode colors correct
- [ ] Dark mode colors correct
- [ ] Toggle switches correctly
- [ ] All text readable both modes

---

## 📝 Code Style & Conventions

### Naming
- Classes: PascalCase (CardDetailViewModel)
- Functions: camelCase (calculateProgress)
- Constants: UPPER_SNAKE_CASE (DEFAULT_MINUTES)
- Composables: PascalCase (CardComponent)

### Architecture
- ViewModel per screen
- UI State dengan sealed classes
- Repository untuk data access
- Dependency injection dengan Hilt
- Coroutines untuk async operations

### Compose
- Components reusable
- State lifting ke parent
- State hoisting untuk shared state
- Proper lambdas untuk callbacks

---

## 🐛 Known Issues & TODO

### Current Limitations
- [ ] Recurrence (belum support task yang berulang)
- [ ] Cloud sync (hanya local database)
- [ ] Categories: belum bisa set otomatis untuk card
- [ ] Export data (belum ada feature backup)

### Future Improvements
- [ ] Dark mode toggle di settings
- [ ] Sound setting untuk notifikasi
- [ ] Recurring tasks
- [ ] Cloud backup
- [ ] Widget support
- [ ] Share card feature

---

## 📦 Dependencies

Key dependencies:
- **Jetpack Compose** - UI framework
- **Room** - Local database
- **Hilt** - Dependency injection
- **Coroutines** - Async programming
- **Navigation Compose** - Navigation
- **Material 3** - Design system
- **WorkManager** - Background tasks

---

## 📄 License

Project ini sudah tayang di Play Store. dilarang diperjual belikan.
Project ini adalah demo untuk pembelajaran. Bebas digunakan & dimodifikasi.

---

## 👨‍💻 Development Tips

### Hot Tips
1. Gunakan `Preview` Composable untuk test UI cepat
2. Hilt auto-inject, jangan hardcode dependencies
3. StateFlow untuk hot flows, Flow untuk cold flows
4. Test DAO dengan `@get:Rule val instantExecutorRule`
5. Gunakan sealed class untuk type-safe states

### Debug Logging
```kotlin
// Extensions di Utils.kt
item.debugLog("HomeScreen", "Loaded: ")
item.errorLog("HomeScreen", throwable)
```

### Testing Database
```kotlin
@get:Rule
val instantExecutorRule = InstantTaskExecutorRule()

@Test
fun testCardDao() {
    val card = CardEntity(id = "1", name = "Test", ...)
    cardDao.insert(card)
    
    val loaded = cardDao.getCardById("1")
    assertEquals(loaded?.name, "Test")
}
```

---

## 📞 Support & Questions

Jika ada issues:
1. Check README ini dulu
2. Baca error message dengan teliti
3. Check logs di Logcat
4. Cek apakah dependencies semua installed

---

**Happy Coding! 🚀**
