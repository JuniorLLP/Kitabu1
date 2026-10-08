Kitabu 📚
> *Your university library in your pocket.*
Kitabu (Swahili for "book") is a native Android app that lets students and library members browse a book catalogue, reserve titles, and manage the full rental lifecycle (renew, return, cancel) with return deadlines tracked in real time. It works fully offline using a local Room database.
Built for MADB372 – Mobile Application Development B (SF1), STADIO School of Information Technology, for the fictional company StadioLinks.
Student: `<Your Name> – <Student Number>`
Module: MADB372 – Mobile Application Development B
Prototype: Figma Make prototype
---
Screenshots
Home	Library	My Books
![Home](docs/screenshots/home.png)	![Library](docs/screenshots/library.png)	![My Books](docs/screenshots/my-books.png)

Book Detail (Available)	Book Detail (Borrowed)	Renew Rental
![Detail](docs/screenshots/detail-available.png)	![Borrowed](docs/screenshots/detail-borrowed.png)	![Renew](docs/screenshots/renew-modal.png)
CRUD in action
Action	Screenshot / GIF
Create: reserve a book	![Reserve](docs/screenshots/crud-reserve.gif)
Read: search and filter catalogue	![Search](docs/screenshots/crud-search.gif)
Update: renew a rental	![Renew](docs/screenshots/crud-renew.gif)
Update: return a book (badge flips to Available)	![Return](docs/screenshots/crud-return.gif)
Delete: cancel a pending reservation	![Cancel](docs/screenshots/crud-cancel.gif)
---
Features
Book catalogue: responsive grid (`LazyVerticalGrid`) with title, author, category and a live availability badge (green Available / red Borrowed)
Search and filter: filter by title or author (SQL `LIKE %query%`) and by All / Available / Borrowed
Reserve: tap an available book to choose a rental duration and reserve it
My Books dashboard: active rentals with reservation date, return deadline and calculated days remaining
Lifecycle actions: Renew (extend deadline), Return (frees the book), Cancel (removes a pending reservation)
Reactive UI: badges update instantly when a book is reserved or returned
Offline first: all data persists locally; the database is pre-populated with sample books on first launch
---
Tech Stack
Area	Technology
Language	Kotlin
UI	Jetpack Compose (Material 3)
Navigation	Navigation Compose (single activity)
Local database	Room (SQLite)
Annotation processing	KSP
Async	Kotlin Coroutines
Reactive state	Kotlin Flow / StateFlow
Architecture	MVVM
Design	Figma
---
Architecture
```
┌──────────────────────────┐
│  UI Layer (Compose)      │  Screens observe state with collectAsStateWithLifecycle()
└────────────┬─────────────┘
             │ events ↑   ↓ StateFlow
┌────────────▼─────────────┐
│  ViewModel               │  Holds UI state, launches work in viewModelScope
└────────────┬─────────────┘
             │
┌────────────▼─────────────┐
│  Repository (domain)     │  Business logic abstraction over the DAOs
└────────────┬─────────────┘
             │
┌────────────▼─────────────┐
│  Data Layer              │  Room: Entities, DAOs, TypeConverters, Database
└──────────────────────────┘
```
Key decisions
Room DAOs return `Flow<List<...>>`; ViewModels convert them to `StateFlow` with `.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), initialValue)`.
All writes (insert/update/delete) run inside `viewModelScope.launch`, so no database work happens on the Main thread.
Reserving or returning a book updates `BookEntity.isAvailable` and the `BookingEntity` in a single transaction so the catalogue badge and dashboard never disagree.
Package structure
```
app/src/main/java/.../kitabu/
├── ui/          Compose screens, components, navigation, theme
├── viewmodel/   ViewModels and UI state
├── domain/      Repository (business logic)
├── data/        Entities, DAOs, TypeConverters, KitabuDatabase
└── di/          Singleton database / repository / ViewModel factory provision
```
---
Database Schema
BookEntity
Field	Type	Notes
`bookId`	Int	Auto-generated primary key
`title`	String	
`author`	String	
`category`	String	
`isAvailable`	Boolean	Default `true`
BookingEntity
Field	Type	Notes
`bookingId`	Int	Auto-generated primary key
`bookOwnerId`	Int	Foreign key → `BookEntity.bookId`, CASCADE delete
`userName`	String	
`bookingDate`	Long	Timestamp
`returnDeadline`	Long	Timestamp
`status`	Enum	`PENDING`, `ACTIVE`, `RETURNED` (via `@TypeConverters`)
```
BookEntity 1 ──────< BookingEntity
 (bookId)              (bookOwnerId)
```
---
Getting Started
Prerequisites
Android Studio (Ladybug or newer)
JDK 17+
Android emulator or device running API 26+
Git
Run the app
```bash
git clone https://github.com/<your-username>/Kitabu_MADB372_SF1.git
cd Kitabu_MADB372_SF1
```
Open the folder in Android Studio.
Let Gradle sync finish (KSP generates the Room code).
Select an emulator/device (API 26+) and press Run ▶.
The database is created and pre-populated with sample books on first launch. To reset it, uninstall the app or clear its storage.
---
Design
The UI follows the Kitabu design system (navy `#1B2A41` headers, blue `#3B5FE0` primary actions, soft-tint status pills). Full colour, button and navigation-flow notes are in `docs/design-spec.md`.
---
Project Milestones
Milestone	Date	Focus
Monitor Day 1	28/08/2026	Compose UI and navigation (≥ 50%)
Monitor Day 2	11/09/2026	Room DB, DAOs, ViewModels (≥ 50%)
Final submission	End of term	Complete project and README
---
Scope
In scope: Compose UI, Room (two related entities), Coroutines and Flow, search, reserve, renew, return, cancel, single-activity navigation.
Out of scope: Firebase / REST APIs / cloud databases, authentication or multi-user support, payment processing for late fees.
---
AI Use Declaration
AI assistance was used for project planning, design documentation and feedback in line with the module's AI Traffic Light Matrix (Task 1: Amber). Prompts and outputs are provided as evidence in `ai-evidence/`.
---
References
Kotlin. Kotlin Documentation. https://kotlinlang.org/docs/home.html
Android Developers. Jetpack Compose documentation. https://developer.android.com/jetpack/compose
Android Developers. Room persistence library. https://developer.android.com/training/data-storage/room
Android Developers. Navigation Compose. https://developer.android.com/develop/ui/compose/navigation
SQLite. SQLite Documentation. https://www.sqlite.org/docs.html
Figma. Figma Help Center. https://help.figma.com
> Replace/extend with STADIO Referencing Guide formatting before submission.
---
License
Created for educational purposes as part of MADB372 at STADIO. All rights reserved by the author.
