# AI Usage Log

Tool used: Claude, as a pair-programmer throughout this project. I
reviewed and ran everything myself in Android Studio before committing.

---

### 1. Unit testing a ViewModel that fetches data in `init{}`

**What I asked:** My `ShowListViewModel` loads data as soon as it's created, and I
didn't know how to test that with coroutines/`StateFlow`, first time writing this
kind of test.

**What it gave me:** A `MainDispatcherRule` using `UnconfinedTestDispatcher` so the
`init{}` coroutine finishes immediately instead of me having to manually advance
time in every test.

**What I did:** Accepted it, but asked why it picked `Unconfined` over `Standard`
dispatcher since that's the one I'd seen in tutorials before.

**Something I verified myself:** I ran `./gradlew test` locally to confirm the
tests actually pass, I couldn't just trust the explanation without seeing it
green in my own terminal.

---

### 2. Stripping HTML tags from the `summary` field

**What I asked:** The API's `summary` field contains HTML tags like `<p>`, `<b>`,
and I wasn't sure what the "proper" way to clean that up in Android is.

**What it gave me:** A simple regex-based stripper instead of Android's built-in
`Html.fromHtml()`.

**What I did:** Accepted it, I didn't realize `Html.fromHtml()` doesn't work in a
plain unit test without extra setup (Robolectric), so this was genuinely useful
context I didn't have before.

**Something I verified myself:** I manually checked a summary with nested tags
(`<i><b>...</b></i>`) from the sample API data to make sure stripping didn't leave
weird leftover characters.

---

### 3. Passing data (showId) from Navigation into a ViewModel

**What I asked:** How to get the `showId` from the nav route into
`ShowDetailViewModel`'s constructor, I only knew the plain `viewModel()` call with
no arguments.

**What it gave me:** A `viewModelFactory { initializer { ... } }` block to
construct the ViewModel manually with the `showId`.

**What I did:** Accepted it since it worked, but this is honestly the part of the
project I understand the least, I'd want to read more about `SavedStateHandle` as
the "standard" alternative before reusing this pattern elsewhere.

---

### 4. Build failure: `TextButtonDefaults`

**What I asked:** To restyle the Share/Back buttons using `TextButtonDefaults`.

**What it gave me:** Code importing `TextButtonDefaults` from
`androidx.compose.material3` and passing `colors = TextButtonDefaults.textButtonColors(...)`
into the `TextButton`.

**What happened:** The build failed in Android Studio, "Unresolved reference
'TextButtonDefaults'" plus a type error mentioning
`androidx.wear.compose.material3.TextButtonColors`. There's a class with the exact
same name in the Wear OS Compose library, and something resolved to the wrong one.

**What I did:** Instead of chasing the import, I just dropped `TextButtonDefaults`
entirely and set the color directly on the `Text` inside the button:

```kotlin
TextButton(
    onClick = { shareShow(context, successState.show) }
) {
    Text(
        text = "⤄ Share",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.primary
    )
}
```

**One thing the AI got wrong (or at least didn't catch):** The code looked correct
on paper, but it still broke my build. Rather than trust the AI's suggested fix for
the ambiguous import, I found a simpler workaround myself that avoids the
problematic API altogether, a reminder that AI-generated code isn't "done" just
because it looks right; it still has to actually compile in my project.

---

### 5. Decision: Unicode characters instead of an icon library

**What I asked:** To add a share icon and a back arrow to the Detail screen's top
bar.

**What it gave me:** Instead of adding the `material-icons-core` dependency for
`Icons.Filled.Share` / `Icons.Filled.ArrowBack`, it suggested plain Unicode
characters (`←`, `⤄`, `★`) as text, to avoid pulling in an extra library for just
a couple of icons.

**What I did:** Accepted it for this project's scope, it's a 1-day take-home, and
adding a whole icon library for 2-3 icons felt unnecessary.

**Something I verified myself:** I ran the app on my emulator to make sure the
arrow and star characters actually render as intended, Unicode symbol support
varies by device/font, and some of the less common glyphs (like `←`) could easily
show up as a blank box on an older Android version instead of the actual arrow.

---

### 6. Sending a repeated query parameter with Retrofit (`embed[]=cast&embed[]=episodes`)

**What I asked:** For the bonus feature (season/episode count + cast), TVMaze's docs
show the URL as `?embed[]=cast&embed[]=episodes`, same query key repeated twice
with different values. I'd only ever written single-value `@Query` params before and
didn't know Retrofit could handle this.

**What it gave me:** Declaring the parameter as `@Query("embed[]") embed: List<String>`
with a default value of `listOf("cast", "episodes")`, so Retrofit automatically
repeats the key for each list item.

**What I did:** Accepted it, this wasn't something I could've guessed on my own
without reading deep into Retrofit's docs.

**One thing the AI got wrong:** It didn't mention that Retrofit URL-encodes the
`@Query` key by default, so `"embed[]"` actually gets sent over the wire as
`embed%5B%5D=cast&embed%5B%5D=episodes`, not the literal brackets shown in
TVMaze's docs. I had to check this myself (by logging the actual request URL via
the OkHttp logging interceptor already in the project) to confirm the server still
parses the percent-encoded brackets correctly, it does, but the AI never flagged
that this could've been a problem in the first place.