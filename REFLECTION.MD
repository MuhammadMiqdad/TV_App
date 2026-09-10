# Reflection

### 1. Which part of your submission are you least confident about, and why?

The `ShowDetailViewModel` wiring, specifically how I pass `showId` into it via
`viewModelFactory { initializer { ... } }`. It works, but I only understand it
well enough to make it work, not well enough to explain why it's better or
worse than the more common `SavedStateHandle` approach. If asked to justify
it, I'd struggle.

---

### 2. Describe a moment during this project where you got completely stuck. What did you do, step by step?

Restyling the Share button, my build suddenly failed with "Unresolved
reference 'TextButtonDefaults'" plus an error mentioning Wear OS's Compose
library, even though I never touched anything Wear-related.

1. Read the error slowly instead of panicking, noticed the Wear OS package
   name in it.
2. Checked the import statement and found a same-named class exists in both
   regular Compose and Wear Compose, one had resolved to the wrong one.
3. Skipped debugging why the import broke, and just removed the dependency
   on `TextButtonDefaults` entirely (set the color on `Text` instead).
4. Rebuilt to confirm the error, and two unrelated-looking errors that came
   with it, all disappeared.

---

### 3. Imagine: it's Thursday, your task is due Friday, and you realize you misunderstood the requirement, half your work is wrong. What are you doing now?

Figure out what's actually salvageable first, then message whoever assigned
it right away so they have visibility early, not after the deadline. I'd
rather ship something smaller but correct by Friday than something large but
wrong, and I'd be upfront about what's done and what was rushed.

---

### 4. Your mentor asks you to change an approach you believe is worse. What do you do?

I'd voice my reasoning once, clearly and with the actual tradeoff in mind
rather than just "I think mine is better," and genuinely listen to theirs,
since a mentor often has context or constraints I can't see yet. If they
still prefer their approach afterward, I'd go with it and commit to it fully,
not just comply reluctantly, since second-guessing it halfway through helps
no one. The one exception is if it's a real correctness, security, or ethical
problem, not just a style preference, in that case I'd raise it again clearly.

---

### 5. What's something technical you taught yourself recently outside of class/work, and how did you learn it?

React.js. Most of my coursework projects have been Android-based, and my
previous internship used PHP + Vue for web development, so React was new territory for me. I got
curious about it after hearing how its component system, small, encapsulated,
reusable pieces, tends to keep code cleaner and easier to reason about
compared to what I was used to. I learned it on my own through YouTube
tutorials, building small practice components along the way instead of just
watching passively.