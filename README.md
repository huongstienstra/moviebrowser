# Movie Browser - Clean Architecture Guide

A modern Android application demonstrating **Clean Architecture** principles. This README serves as a learning guide for understanding Clean Architecture concepts.

---

## Table of Contents
- [What is Clean Architecture?](#what-is-clean-architecture)
- [Why Use Clean Architecture?](#why-use-clean-architecture)
- [The Dependency Rule](#the-dependency-rule)
- [The Three Layers](#the-three-layers)
- [Data Flow](#data-flow)
- [Key Patterns](#key-patterns)
- [Project Structure](#project-structure)
- [Tech Stack](#tech-stack)
- [Setup](#setup)

---

## What is Clean Architecture?

Clean Architecture is a software design philosophy introduced by **Robert C. Martin (Uncle Bob)** in 2012. It separates code into layers with clear responsibilities, making the codebase:

- **Testable** - Business logic can be tested without UI or database
- **Independent of frameworks** - The architecture doesn't depend on libraries
- **Independent of UI** - UI can change without affecting business logic
- **Independent of database** - You can swap databases easily
- **Independent of external services** - Business rules don't know about the outside world

### The Core Idea

> "The center of your application is not the database. Nor is it one or more of the frameworks you may be using. **The center of your application is the use cases of your application.**"
> — Robert C. Martin

---

## Why Use Clean Architecture?

### Problems It Solves

| Problem | How Clean Architecture Helps |
|---------|------------------------------|
| **Spaghetti Code** | Clear separation makes code organized |
| **Hard to Test** | Business logic is isolated and testable |
| **Tightly Coupled** | Layers communicate through interfaces |
| **Hard to Change** | Changes in one layer don't affect others |
| **Framework Lock-in** | Core business logic is framework-independent |

### When to Use It

✅ **Use when:**
- Building medium to large applications
- Working in a team
- App will be maintained long-term
- Need high test coverage

❌ **Avoid when:**
- Building small/simple apps (over-engineering)
- Rapid prototyping
- One-time projects

---

## The Dependency Rule

This is the **most important rule** in Clean Architecture:

> **Source code dependencies must point only inward, toward higher-level policies.**

```
┌─────────────────────────────────────────────────────────────┐
│                                                             │
│    ┌─────────────────────────────────────────────────┐     │
│    │                                                 │     │
│    │    ┌─────────────────────────────────────┐     │     │
│    │    │                                     │     │     │
│    │    │           DOMAIN LAYER              │     │     │
│    │    │         (Business Logic)            │     │     │
│    │    │                                     │     │     │
│    │    │   • Entities (Models)               │     │     │
│    │    │   • Use Cases                       │     │     │
│    │    │   • Repository Interfaces           │     │     │
│    │    │                                     │     │     │
│    │    └─────────────────────────────────────┘     │     │
│    │                                                 │     │
│    │                 DATA LAYER                      │     │
│    │              (Data Management)                  │     │
│    │                                                 │     │
│    │   • Repository Implementation                   │     │
│    │   • API Service (Retrofit)                      │     │
│    │   • Database (Room)                             │     │
│    │   • DTOs & Mappers                              │     │
│    │                                                 │     │
│    └─────────────────────────────────────────────────┘     │
│                                                             │
│                    PRESENTATION LAYER                       │
│                    (UI & User Input)                        │
│                                                             │
│   • Activities/Fragments/Composables                        │
│   • ViewModels                                              │
│   • UI State                                                │
│                                                             │
└─────────────────────────────────────────────────────────────┘

        ← ← ← ← DEPENDENCIES POINT INWARD ← ← ← ←
```

### What This Means

- **Domain Layer** knows nothing about Data or Presentation
- **Data Layer** knows about Domain (implements its interfaces)
- **Presentation Layer** knows about Domain (uses its use cases)
- **Inner layers define interfaces, outer layers implement them**

---

## The Three Layers

### 1. Domain Layer (Innermost - Pure Kotlin)

The **heart** of the application. Contains business logic and rules.

#### What It Contains

| Component | Purpose | Example |
|-----------|---------|---------|
| **Entities/Models** | Core business objects | `Movie`, `TvShow`, `Favorite` |
| **Use Cases** | Single business actions | `GetPopularMoviesUseCase` |
| **Repository Interfaces** | Contracts for data operations | `MovieRepository` |

#### Key Characteristics
- ✅ Pure Kotlin (no Android dependencies)
- ✅ No knowledge of database or network
- ✅ Highly testable
- ✅ Most stable layer (changes rarely)

#### Code Example

```kotlin
// Domain Model - Pure Kotlin, no annotations
data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val voteAverage: Double
)

// Repository Interface - Defines WHAT, not HOW
interface MovieRepository {
    suspend fun getPopularMovies(): Result<List<Movie>>
    suspend fun searchMovies(query: String): Result<List<Movie>>
}

// Use Case - Single responsibility, one action
class GetPopularMoviesUseCase(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(): Result<List<Movie>> {
        return repository.getPopularMovies()
    }
}
```

#### Why Use Cases Matter

| Without Use Cases | With Use Cases |
|-------------------|----------------|
| ViewModel calls repository directly | ViewModel calls use case |
| Business logic scattered in ViewModels | Business logic centralized |
| Hard to reuse logic | Easy to reuse across ViewModels |
| Hard to test | Easy to test in isolation |

---

### 2. Data Layer (Middle)

Responsible for **data management** - fetching, caching, and storing data.

#### What It Contains

| Component | Purpose | Example |
|-----------|---------|---------|
| **Repository Impl** | Implements domain interfaces | `MovieRepositoryImpl` |
| **Remote Data Source** | API calls | `TMDBApi` (Retrofit) |
| **Local Data Source** | Database operations | `FavoriteDao` (Room) |
| **DTOs** | API response models | `MovieDto` |
| **Mappers** | Convert DTO ↔ Domain | `MovieDto.toDomain()` |

#### Code Example

```kotlin
// DTO - Matches API response structure
@JsonClass(generateAdapter = true)
data class MovieDto(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String,
    @Json(name = "overview") val overview: String?,
    @Json(name = "vote_average") val voteAverage: Double
)

// Mapper - DTO to Domain Model
fun MovieDto.toDomain(): Movie {
    return Movie(
        id = id,
        title = title,
        overview = overview ?: "",
        voteAverage = voteAverage
    )
}

// Repository Implementation
class MovieRepositoryImpl(
    private val api: TMDBApi,
    private val database: MovieDatabase
) : MovieRepository {

    override suspend fun getPopularMovies(): Result<List<Movie>> {
        return runCatching {
            api.getPopularMovies()
                .results
                .map { it.toDomain() }  // Convert DTO → Domain
        }
    }
}
```

#### Why Separate DTOs and Domain Models?

| DTO (Data Layer) | Domain Model |
|------------------|--------------|
| Matches API structure | Matches business needs |
| Has JSON annotations | Pure Kotlin |
| May have nullable fields | Clean, validated data |
| Can change with API | Stable, rarely changes |

**Example:** If TMDB API changes `vote_average` to `rating`, you only update the DTO and mapper - Domain layer stays unchanged.

---

### 3. Presentation Layer (Outermost)

Handles **UI and user interaction**.

#### What It Contains

| Component | Purpose | Example |
|-----------|---------|---------|
| **UI Components** | Display data | `HomeScreen` (Composable) |
| **ViewModels** | Hold UI state, handle actions | `HomeViewModel` |
| **UI State** | Immutable screen state | `HomeUiState` |

#### Code Example

```kotlin
// UI State - Immutable, represents screen state
data class HomeUiState(
    val isLoading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val error: String? = null
)

// ViewModel - Connects UI with Domain
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadMovies()
    }

    fun loadMovies() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            getPopularMoviesUseCase()
                .onSuccess { movies ->
                    _uiState.update {
                        it.copy(isLoading = false, movies = movies)
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, error = error.message)
                    }
                }
        }
    }
}

// Composable - Observes state, renders UI
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.isLoading -> LoadingIndicator()
        uiState.error != null -> ErrorMessage(uiState.error)
        else -> MovieList(uiState.movies)
    }
}
```

---

## Data Flow

### User Action Flow (UI → Data)

```
┌──────────┐    ┌───────────┐    ┌──────────┐    ┌────────────┐    ┌──────────┐
│   User   │───▶│  Screen   │───▶│ ViewModel│───▶│  Use Case  │───▶│Repository│
│  clicks  │    │(Composable)    │          │    │            │    │          │
└──────────┘    └───────────┘    └──────────┘    └────────────┘    └──────────┘
```

### Data Response Flow (Data → UI)

```
┌──────────┐    ┌──────────┐    ┌──────────┐    ┌───────────┐    ┌──────────┐
│   API    │───▶│Repository│───▶│ Use Case │───▶│ ViewModel │───▶│  Screen  │
│          │    │ (maps to │    │ (returns │    │ (updates  │    │(observes │
│          │    │  domain) │    │  domain) │    │   state)  │    │  state)  │
└──────────┘    └──────────┘    └──────────┘    └───────────┘    └──────────┘
```

### Complete Example: Search Flow

```
1. User types "Batman" in search field
                    │
                    ▼
2. SearchScreen calls viewModel.search("Batman")
                    │
                    ▼
3. SearchViewModel calls searchMoviesUseCase("Batman")
                    │
                    ▼
4. SearchMoviesUseCase calls repository.searchMovies("Batman")
                    │
                    ▼
5. MovieRepositoryImpl:
   - Calls api.searchMovies("Batman")
   - Receives List<MovieDto> from API
   - Maps to List<Movie> using toDomain()
   - Returns Result<List<Movie>>
                    │
                    ▼
6. SearchViewModel:
   - Receives Result<List<Movie>>
   - Updates _uiState with movies
                    │
                    ▼
7. SearchScreen:
   - Observes uiState via collectAsState()
   - Recomposes with new movie list
```

---

## Key Patterns

### 1. Repository Pattern

**Purpose:** Abstract data sources from the rest of the app. The domain layer doesn't know if data comes from API, database, or cache.

```kotlin
// Interface in Domain layer - defines WHAT
interface MovieRepository {
    suspend fun getMovies(): Result<List<Movie>>
}

// Implementation in Data layer - defines HOW
class MovieRepositoryImpl(
    private val remoteDataSource: TMDBApi,
    private val localDataSource: MovieDao
) : MovieRepository {

    override suspend fun getMovies(): Result<List<Movie>> {
        return try {
            // Try remote first
            val movies = remoteDataSource.getMovies()
            localDataSource.cacheMovies(movies)  // Cache locally
            Result.success(movies.map { it.toDomain() })
        } catch (e: Exception) {
            // Fallback to cache if network fails
            val cached = localDataSource.getMovies()
            if (cached.isNotEmpty()) {
                Result.success(cached.map { it.toDomain() })
            } else {
                Result.failure(e)
            }
        }
    }
}
```

**Benefits:**
- Swap data sources without changing domain logic
- Add caching strategies in one place
- Easy to test with fake implementations

---

### 2. Use Case Pattern

**Purpose:** Encapsulate a single business action. Each use case does ONE thing.

```kotlin
// One use case = one action
class GetPopularMoviesUseCase(private val repo: MovieRepository) {
    suspend operator fun invoke() = repo.getPopularMovies()
}

class SearchMoviesUseCase(private val repo: MovieRepository) {
    suspend operator fun invoke(query: String) = repo.searchMovies(query)
}

class AddToFavoritesUseCase(private val repo: MovieRepository) {
    suspend operator fun invoke(movie: Movie) = repo.addFavorite(movie)
}

// Use case with business logic
class GetFilteredMoviesUseCase(private val repo: MovieRepository) {
    suspend operator fun invoke(minRating: Double): Result<List<Movie>> {
        return repo.getPopularMovies().map { movies ->
            movies.filter { it.voteAverage >= minRating }
        }
    }
}
```

**Benefits:**
- Single Responsibility - easy to understand
- Reusable across multiple ViewModels
- Business logic stays in domain layer
- Easy to unit test

---

### 3. Dependency Injection (Hilt)

**Purpose:** Provide dependencies from outside. Classes don't create their own dependencies.

```kotlin
// Module - defines HOW to create dependencies
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.TMDB_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): TMDBApi {
        return retrofit.create(TMDBApi::class.java)
    }
}

// Bind interface to implementation
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRepository(impl: MovieRepositoryImpl): MovieRepository
}

// ViewModel receives dependencies automatically
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase
) : ViewModel()
```

**Benefits:**
- Easy to swap implementations (testing)
- Manages object lifecycle
- Reduces boilerplate
- Makes dependencies explicit

---

## Dependency Inversion Principle

This is how Clean Architecture achieves layer independence:

```
WITHOUT Dependency Inversion:
┌────────────┐         ┌────────────┐
│  Domain    │────────▶│    Data    │  Domain DEPENDS on Data ❌
└────────────┘         └────────────┘

WITH Dependency Inversion:
┌────────────┐         ┌────────────┐
│  Domain    │◀────────│    Data    │  Data DEPENDS on Domain ✅
│            │         │            │
│ interface  │         │ implements │
│ Repository │         │ Repository │
└────────────┘         └────────────┘
```

**How it works:**
1. Domain defines `interface MovieRepository`
2. Data creates `class MovieRepositoryImpl : MovieRepository`
3. Hilt injects `MovieRepositoryImpl` where `MovieRepository` is needed
4. Domain never knows about `MovieRepositoryImpl`

---

## Project Structure

```
app/src/main/java/com/vyvienne/moviebrowser/
│
├── data/                           # DATA LAYER
│   ├── remote/
│   │   ├── api/
│   │   │   └── TMDBApi.kt          # Retrofit interface
│   │   └── dto/
│   │       ├── MovieDto.kt         # API response models
│   │       └── MovieResponse.kt
│   ├── local/
│   │   ├── dao/
│   │   │   └── FavoriteDao.kt      # Room DAO
│   │   ├── entity/
│   │   │   └── FavoriteEntity.kt   # Room Entity
│   │   └── MovieDatabase.kt
│   └── repository/
│       └── MovieRepositoryImpl.kt  # Repository implementation
│
├── domain/                         # DOMAIN LAYER (Pure Kotlin)
│   ├── model/
│   │   ├── Movie.kt                # Domain models
│   │   ├── TvShow.kt
│   │   └── Favorite.kt
│   ├── repository/
│   │   └── MovieRepository.kt      # Repository interface
│   └── usecase/
│       ├── GetPopularMoviesUseCase.kt
│       ├── SearchMoviesUseCase.kt
│       └── ToggleFavoriteUseCase.kt
│
├── presentation/                   # PRESENTATION LAYER
│   ├── theme/
│   ├── components/
│   ├── home/
│   │   ├── HomeScreen.kt
│   │   └── HomeViewModel.kt
│   ├── search/
│   ├── detail/
│   ├── favorites/
│   └── navigation/
│
├── di/                             # Dependency Injection
│   ├── NetworkModule.kt
│   ├── DatabaseModule.kt
│   └── RepositoryModule.kt
│
└── MovieBrowserApp.kt
```

---

## Tech Stack

| Category | Technology |
|----------|------------|
| **UI** | Jetpack Compose, Material 3 |
| **Architecture** | Clean Architecture, MVVM |
| **DI** | Hilt |
| **Networking** | Retrofit, OkHttp, Moshi |
| **Database** | Room |
| **Async** | Kotlin Coroutines, StateFlow |
| **Image Loading** | Coil |
| **Navigation** | Navigation Compose |

---

## Setup

1. Clone the repository
2. Get a TMDB API key from [themoviedb.org](https://www.themoviedb.org/settings/api)
3. Add to `local.properties`:
   ```
   TMDB_API_KEY=your_api_key_here
   ```
4. Build and run

---

## Resources

- [Clean Architecture by Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Android Guide to App Architecture](https://developer.android.com/topic/architecture)
- [Now in Android - Official Sample](https://github.com/android/nowinandroid)

---

## Author

Built by [vyvienne](https://github.com/huongstienstra)
