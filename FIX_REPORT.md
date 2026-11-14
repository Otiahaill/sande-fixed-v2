# sande: build fix report (second pass)

## What was wrong
The previous FIX_REPORT claimed many fixes, but the UI layer in the zip still had them unfixed.
Compile blockers found and removed by rewriting the UI layer against Material 3 1.2.1:
- Screens redefined `Box`, `Column`, `Row`, `Spacer`, shadowing the real ones
- Material 2 `BottomNavigation` API, `Card(elevation = Dp)`, `floatingActionButton = Button(...)`
- Invented theme APIs (`letterHeight`, `CornerBasedShape(...)`, `surfaceDisabled`)
- Icons missing from material-icons-core (Inventory, Storage, ChevronRight, ShoppingBag, DeleteOutline)
- `StockScreen` used `viewModel.stockByProduct`, which does not exist (ViewModel exposes `rows`)
- `SalesScreen` used `viewModel.itemCount` / `subtotalCents` as properties; they did not exist
- `Thread.sleep` inside a LaunchedEffect (froze the UI)
- Missing imports everywhere; `SandeTheme` never imported in several screens

## Other changes
- minSdk 24 -> 26 (launcher icon only exists for API 26+)
- gradlew made executable
- Navigation is a simple state machine (Splash -> SignIn -> Shell with bottom tabs)
- Stock screen: tap a product to receive stock (writes a RECEIVE movement)

## Not verified
Still NOT compiled: no Gradle, Android SDK or network where this was done. The data layer was
read line by line and looks valid. Push and read the CI result; remaining errors, if any, should be small.
