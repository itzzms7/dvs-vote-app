# Website Companion Android App

Build a native Android companion application for an existing brand/website, translating web services, content, and brand identity into an ergonomic, responsive Jetpack Compose mobile experience.

## User Review & Critical Decisions

> [!IMPORTANT]
> The app architecture is structured as a native companion app. We are ready to ingest the website zip archive or chat specifications to tailor features, data models, and brand colors.

- **Companion App Scope**: Configured to translate website content, services, and workflows into native mobile screens and interactions.
- **Aesthetic Direction**: User will specify the exact visual design and palette (or derive directly from the uploaded website assets).
- **Awaiting Website Input**: User will upload the website ZIP archive or paste key details and feature objectives.

---

## 1. Overview & Core Concept

- **What It Does**: Delivers an Android client tailored to the website's brand identity and core user flows, offering smooth mobile navigation, offline-friendly caching, and native touch interactions.
- **Target Audience / Persona**: Existing customers and mobile visitors who want fast, native mobile access to the website's services, catalog, content, or account capabilities.
- **Key Value**: Native mobile responsiveness, fluid transitions, touch-optimized layouts, edge-to-edge Material 3 styling, and mobile-specific perks (instant loading, push notifications readiness, device ergonomics).

---

## 2. User Experience & Visual Design

- **Key User Flows**:
  1. **Splash & Welcome / Brand Overview**: Smooth launch with brand identity, core value proposition, and quick access.
  2. **Main Hub / Dashboard**: Primary navigation showcasing top website sections, featured offerings, recent updates, or key services.
  3. **Detail & Interaction Views**: Clean, focused presentation for content, items, or user actions with edge-to-edge scrolling.
  4. **Settings & Preferences**: Theme toggles, notifications, and brand links.

- **Visual Identity & Theme**:
  - *Aesthetic Direction*: Refined, modern Material 3 interface aligned with the brand aesthetic the user provides.
  - *Color Palette & Tokens*: Semantic M3 tokens in `Theme.kt` (`primary`, `onPrimary`, `surface`, `onSurface`, `container` tiers).
  - *Typography & Hierarchy*: Clear typography scale with distinct display titles and legible body text.
  - *Component Styling*: Elevated M3 cards, rounded action pills, and clear visual hierarchy.

- **Interactive Feedback & Motion**:
  - Material ripple on all clickable elements with at least 48dp touch targets.
  - Smooth animated transitions between destinations and content state changes.
  - Polished empty states and loading progress indicators.

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Native Jetpack Compose Architecture**
  - *Chosen Approach*: Pure Jetpack Compose with Material 3 and Single-Activity architecture.
  - *Why*: Delivers high performance, smooth animations, dynamic window size responsiveness, and clean code maintainability.
  - *Alternatives Considered*: WebView wrapper (rejected because native Compose gives superior speed, offline capability, and tactile user experience).

- **Decision 2: Layered State Management & Extensible Data Layer**
  - *Chosen Approach*: Clean Architecture (UI Layer -> ViewModel with `StateFlow` -> Repository layer).
  - *Why*: Allows easy swapping or binding to local mock data, local caching (Room), or remote REST/Firestore APIs once website assets and backend endpoints are revealed.

---

## 4. Technical Architecture & Data Strategy

### System Architecture & Data Flow

```
┌────────────────────────────────────────────────────────┐
│                   MainActivity                         │
│            (Edge-to-Edge, Theme Setup)                 │
└──────────────────────────┬─────────────────────────────┘
                           │
┌──────────────────────────▼─────────────────────────────┐
│                 Navigation & Screens                   │
│  ┌─────────────────┐  ┌──────────────────────────────┐ │
│  │   Home / Hub    │  │ Detail & Action Screens      │ │
│  └────────┬────────┘  └──────────────┬───────────────┘ │
└───────────┼──────────────────────────┼─────────────────┘
            │                          │
┌───────────▼──────────────────────────▼─────────────────┐
│              ViewModels & UI State                     │
│         (StateFlow, Sealed UI States)                  │
└──────────────────────────┬─────────────────────────────┘
                           │
┌──────────────────────────▼─────────────────────────────┐
│                 Repository Layer                       │
│    (Brand Data, Content Models, Sync / Caching)        │
└────────────────────────────────────────────────────────┘
```

### Data Model & State Strategy
- **AppUiState**: Sealed interface representing `Loading`, `Success(data)`, and `Error(message)` states for each feature.
- **Content & Service Models**: Data classes modeled around the website's entities (articles, products, services, or account info).
- **Navigation State**: Type-safe screen routing with system back-press handling via `BackHandler`.
