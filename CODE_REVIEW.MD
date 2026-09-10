# Code Review: `MovieViewModel`

Reviewing this as a PR before merge. Code under review:

```kotlin
class MovieViewModel : ViewModel() {
    var movies: List<Movie> = emptyList()
    fun loadMovies() {
        val url = URL("https://api.example.com/movies")
        val data = url.readText()
        movies = parseMovies(data)
    }
}
```

This won't compile against real usage without crashing or silently failing to
update the UI. Below are the problems I'd flag, roughly ordered from
"will crash in production" to "works but is bad practice."

---

## 1. Network call runs on the main thread, will crash

`url.readText()` is a blocking, synchronous call. If `loadMovies()` is invoked
from the UI (which is the only place a ViewModel method gets called from),
Android will throw `NetworkOnMainThreadException` and crash the app outright.
This isn't a style nitpick, it simply doesn't work.

**Fix:** Move the network call off the main thread using a coroutine, launched
in `viewModelScope` so it's automatically cancelled if the ViewModel is cleared
mid-request:

```kotlin
fun loadMovies() {
    viewModelScope.launch(Dispatchers.IO) {
        // network + parsing here
    }
}
```

(Better yet, see point 6, use a suspend Retrofit call instead of raw `URL`,
which already dispatches off the main thread.)

---

## 2. No error handling, any failure crashes the app

There's no `try/catch` around either the network call or `parseMovies(data)`.
No internet connection, a timeout, a non-200 response, or malformed JSON will
all throw an unhandled exception that crashes the app instead of showing the
user a retry option.

**Fix:** Wrap the work in a `try/catch` and expose the outcome as state (see
point 4) instead of letting the ViewModel throw:

```kotlin
try {
    val movies = repository.getMovies()
    _uiState.value = MovieUiState.Success(movies)
} catch (e: IOException) {
    _uiState.value = MovieUiState.Error("No internet connection.")
} catch (e: Exception) {
    _uiState.value = MovieUiState.Error("Something went wrong.")
}
```

---

## 3. `movies` isn't observable, the UI will never update

`var movies: List<Movie>` is a plain mutable property. Reassigning it doesn't
notify anything: Compose won't recompose (it's not a `State`/`Flow`), and the
View system won't get an update either (it's not `LiveData`). Functionally,
`loadMovies()` fetches data that nothing can ever observe.

**Fix:** Back it with a `MutableStateFlow` and expose an immutable
`StateFlow`, which is what the UI actually collects:

```kotlin
private val _uiState = MutableStateFlow<MovieUiState>(MovieUiState.Loading)
val uiState: StateFlow<MovieUiState> = _uiState.asStateFlow()
```

---

## 4. No Loading / Error state, only the happy path exists

Even once observability is fixed, there's no way for the UI to show a loading
spinner or an error message with retry. The ViewModel only models "I have a
list" (which starts empty and silently becomes populated at some point).

**Fix:** Model the three states explicitly, same pattern as this project's
`ShowListUiState`:

```kotlin
sealed interface MovieUiState {
    data object Loading : MovieUiState
    data class Success(val movies: List<Movie>) : MovieUiState
    data class Error(val message: String) : MovieUiState
}
```

---

## 5. Networking logic lives directly inside the ViewModel

The ViewModel constructs a `URL`, reads from it, and parses the response
itself. This mixes networking/data concerns into the presentation layer,
which:
- makes the ViewModel untestable without hitting a real network or heavily
  mocking `java.net.URL`
- means the same fetch-and-parse logic can't be reused elsewhere
- makes it harder to reason about what the ViewModel is actually responsible
  for

**Fix:** Extract networking into a `MovieRepository` (interface +
implementation) and inject it into the ViewModel constructor. This also
makes the ViewModel trivially testable with a fake repository:

```kotlin
class MovieViewModel(
    private val repository: MovieRepository = MovieRepositoryImpl(...)
) : ViewModel() { ... }
```

---

## 6. Raw `java.net.URL` instead of Retrofit/OkHttp

Using `URL(...).readText()` directly means no request timeouts (a hanging
server blocks forever), no connection pooling, no built-in JSON
deserialization, no easy way to add headers/auth later, and no cancellation
support tied to the coroutine.

**Fix:** Use Retrofit (as already set up elsewhere in this project) with a
suspend function and a Gson/Moshi converter:

```kotlin
interface MovieApiService {
    @GET("movies")
    suspend fun getMovies(): List<Movie>
}
```

---

## 7. Hardcoded URL string

`"https://api.example.com/movies"` is hardcoded inline in the ViewModel. If
the base URL ever needs to change (dev/staging/prod environments, or just a
domain change), this has to be hunted down instead of changed in one place.

**Fix:** Centralize it in the Retrofit client setup (base URL), the same way
this project's `NetworkModule` does it, so the ViewModel/Repository never
hardcodes it.

---

## Summary: what a fixed version looks like

Putting all of the above together:

```kotlin
sealed interface MovieUiState {
    data object Loading : MovieUiState
    data class Success(val movies: List<Movie>) : MovieUiState
    data class Error(val message: String) : MovieUiState
}

class MovieViewModel(
    private val repository: MovieRepository = MovieRepositoryImpl(NetworkModule.movieApi)
) : ViewModel() {

    private val _uiState = MutableStateFlow<MovieUiState>(MovieUiState.Loading)
    val uiState: StateFlow<MovieUiState> = _uiState.asStateFlow()

    init {
        loadMovies()
    }

    fun loadMovies() {
        viewModelScope.launch {
            _uiState.value = MovieUiState.Loading
            try {
                val movies = repository.getMovies()
                _uiState.value = MovieUiState.Success(movies)
            } catch (e: IOException) {
                _uiState.value = MovieUiState.Error("No internet connection.")
            } catch (e: Exception) {
                _uiState.value = MovieUiState.Error(e.message ?: "Failed to load movies.")
            }
        }
    }
}
```

This is essentially the same pattern already used in this repo's
`ShowListViewModel` / `ShowRepository`. I'd point to that as the reference
implementation in an actual PR comment rather than writing it all out again.