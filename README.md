# Handoff: Cafe Journal — full app redesign

Target codebase: **jordanprepos/cafe-journal-2** (Android, Kotlin, Jetpack Compose + Material 3, Firestore).

## Overview

A complete end-to-end design for Cafe Journal: a private journal of cafe visits. Seven screens — Journal (home feed, two view modes), Cafe Detail, Add/Edit Entry, Places, Stats, Profile, and 2026 Wrapped.

Two things drove the design:
1. **The emotional center is rereading old entries**, not logging them. Notes are set in a serif italic and given real space; the album view is built to be flipped through like a photo album.
2. **The feed is photo-first.** Photos are the primary content of a card, not a decoration.

## Fidelity

**High-fidelity.** Colors, typography, spacing, radii and interactions are final. Recreate pixel-accurately, substituting Compose equivalents. Two deliberate exceptions:

- **Photos are striped placeholders.** Every place a photo goes is a tinted box with a diagonal-stripe pattern and a monospace caption. Wire these to `experience.photoUri` with Coil.
- **Tab bar icons are grey rounded squares.** Keep the existing `Icons.Default.Home / LocationOn / BarChart / Person`.

## Required data model changes

`data/CafeExperience.kt` currently has `rating: Float`. The design uses **four rating axes**:

```kotlin
data class CafeRating(
    val coffee: Float = 0f,
    val vibe: Float = 0f,
    val wifi: Float = 0f,
    val seating: Float = 0f,
)

data class CafeExperience(
    @DocumentId val id: String = "",
    val cafeName: String = "",
    val location: String = "",          // shown as "area", e.g. "Kemang"
    val rating: CafeRating = CafeRating(),
    val coffeeRecommendation: String = "",  // "what you drank", e.g. "Flat white, oat"
    val priceRange: String = "",        // free text, e.g. "Rp 35K"
    val facilitiesTags: List<String> = emptyList(),
    val notes: String = "",
    val photoUri: String = "",
    val timestamp: Timestamp = Timestamp.now(),
)
```

Overall rating is always **derived**, never stored: `avg = (coffee + vibe + wifi + seating) / 4`, displayed to one decimal.

Migration: read a legacy `Float` `rating` and map it into all four axes, or treat it as `coffee` and leave the rest at 0.

Facility tags are a fixed list of 14, in this order:
`WiFi`, `Power Outlets`, `Parking`, `Air Conditioning`, `Outdoor Seating`, `Pet Friendly`, `Restroom`, `Prayer Room`, `Laptop Friendly`, `Open Late`, `Card / QRIS`, `Halal`, `Full Food Menu`, `Wheelchair Accessible`

## Design tokens

### Colors

- `primary`: `#C05A3B`
- `onPrimary`: `#FFFFFF`
- `primaryContainer`: `#FFDBD1`
- `onPrimaryContainer`: `#8F3F27`
- `background`: `#F8F5F0`
- `albumBackground`: `#F3EEE6`
- `surface`: `#FFFFFF`
- `onSurface` / `onBackground`: `#2E241E`
- `surfaceVariant`: `#EBE6DF`
- `onSurfaceVariant`: `#50443D`
- `outline`: `#DED7CD`
- `hairline`: `#EFEAE2`
- `tertiaryContainer`: `#F3E2A7`
- `onTertiaryContainer`: `#211B00`
- `tertiary`: `#6A5E2F`
- `error`: `#BA1A1A`
- `errorContainer`: `#FFDAD6`

Dark scheme (Wrapped screen, and dark theme):
- `#231A16` background
- `#2C221D` surface
- `#EDE0DB` onSurface
- `#FFB59D` primary
- `#5C1900` onPrimary
- `#D6C68D` tertiary

### Typography

Three families:
- **DM Sans**: UI: titles, labels, buttons, body. 400 / 500 / 700
- **Newsreader**: Serif, italic 400 / 500 / 600
- **IBM Plex Mono**: 400 / 500: eyebrows, metadata strips, rating axis abbreviations, numeric readouts

## Prerequisites Order
1. Change CafeExperience.rating from Float to a four-axis CafeRating(coffee, vibe, wifi, seating), with a migration path for existing docs.
2. Add DM Sans, Newsreader and IBM Plex Mono as res/font resources and wire them through Type.kt.
3. Add the wrapped route to AppNavigation.kt.
4. Add a journal_view (grid | album) preference to ThemeRepository.
