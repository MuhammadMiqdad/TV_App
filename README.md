# TV App

A simple TV show browser built with Jetpack Compose, using the [TVMaze API](https://www.tvmaze.com/api).

Video walkthrough: *(....)*

---

## Features

- **List screen**: browse ~250 shows with poster, title, and rating (handles a
  missing rating gracefully).
- **Detail screen**: larger poster, title, premiere date, and summary (HTML
  tags from the API stripped to plain text).
- **Bonus**: season/episode count and cast, fetched via TVMaze's
  `?embed[]=cast&embed[]=episodes` in the same request (no extra API calls).
- **Search**: a search field on the List screen filters the already-fetched page of shows by title, client-side (no extra network call).
- **Share action**: shares the show's title, summary, and URL via Android's
  share sheet.
- Three explicit UI states everywhere data is fetched: Loading, Error (with
  Retry), and Success.

---

## How to run

1. Open the project root folder (`MyApplication/`) in Android Studio.
2. Let Gradle sync, it will download Retrofit, OkHttp, Coil, and Navigation
   Compose on first sync.
3. Run the `app` configuration on an emulator or physical device (minSdk 24).
4. No API key or config needed, TVMaze's API is public.

To run the unit tests: right-click `app/src/test` in Android Studio and choose
**Run Tests**, or from the command line:

```
./gradlew test
```

---

## Architecture

**MVVM + Repository pattern**, split into three layers:

```
data/
  model/       Show, Rating, ShowImage, Episode, CastMember, Person, Character, Embedded
  network/      TvMazeApiService (Retrofit), NetworkModule (Retrofit/OkHttp singleton)
  repository/  ShowRepository (interface), ShowRepositoryImpl
ui/
  list/        ShowListScreen, ShowListItem, ShowListViewModel, ShowListUiState
  detail/      ShowDetailScreen, ShowDetailViewModel, ShowDetailUiState, CastRow
  common/      LoadingState, ErrorState, InfoChip (shared across screens)
util/          stripHtml, buildShareText, seasonEpisodeSummary, filterShows
```

A few decisions worth explaining:

- **`ShowRepository` is an interface**, not just a class, so ViewModels depend
  on an abstraction instead of Retrofit directly. This is what makes them
  testable with a fake repository instead of hitting the network.
- **UI state is a sealed interface** (`Loading` / `Success` / `Error`) per
  screen, rather than separate boolean flags. This makes invalid combinations
  (like "loading" and "error" both true at once) impossible to represent.
- **HTML stripping uses plain regex, not `android.text.Html`**. The Android
  `Html` class isn't available in plain JVM unit tests without Robolectric,
  and a regex-based stripper is simple enough for TVMaze's summary markup
  while staying testable with plain JUnit.
- **`ShowDetailViewModel` takes `showId` through a manual
  `viewModelFactory { initializer { ... } }`**, rather than `SavedStateHandle`.
  It works, but it's the pattern I'm least confident about in this project
  (see `REFLECTION.md`, question 1).
- **No dependency injection framework** (no Hilt/Koin). Repositories and
  ViewModels use default constructor parameters pointing at a real
  implementation, and tests override that parameter with a fake. This keeps
  things simple for a project this size, but wouldn't scale well to a larger
  app with more shared dependencies.
- **Icons are Unicode text (`←`, `⤄`, `★`) instead of `material-icons-core`**,
  to avoid adding an icon library dependency for just a couple of glyphs. A
  production app would likely just add the library instead.
- **Search filters the already-fetched page locally**, instead of calling TVMaze's separate /search/shows endpoint. That endpoint searches the whole catalog rather than the single loaded page, and wiring it in would've meant a second network path, a fourth UI state, and debounced network-triggered queries — out of scope for what this task asks for. A local filter (util/filterShows) over ShowListUiState.Success keeps the three required states untouched and is trivial to unit test as a pure function.

---

## Testing

5 unit tests covering:
- `ShowListViewModel` and `ShowDetailViewModel` (success, error, and retry
  paths, using fake repositories)

## What I'd improve with more time

- Replace the manual `viewModelFactory` pattern with `SavedStateHandle`, or
  introduce Hilt if the app grew beyond this scope.
- Preserve `<b>`/`<i>` formatting in the summary as an `AnnotatedString`
  instead of stripping all HTML to plain text.
- Add pagination on the List screen instead of loading a single page of
  ~250 shows.
- Add instrumented UI tests (not just ViewModel/unit tests) for the actual
  Compose screens.
- Verify the `_embedded.cast` / `_embedded.episodes` response shape more
  thoroughly against edge cases (shows with zero episodes, missing cast
  photos), since my model assumptions were based on TVMaze's docs rather than
  exhaustive live testing.
- Swap the Unicode icon characters for `material-icons-core` for more
  consistent rendering across devices.
- Add a unit test for `util/filterShows` (the search filter) — it's a pure function with no coroutines or state involved, so it'd be a quick, cheap addition I just haven't gotten to yet.

See `AI_LOG.md` for where AI helped (and where it didn't get things fully
right) throughout this project, and `CODE_REVIEW.md` / `REFLECTION.md` for
the rest of the take-home submission.