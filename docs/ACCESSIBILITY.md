# Accessibility Contract

## Global requirements

- Interactive targets are at least 48dp.
- Icon-only controls require a non-empty `contentDescription`.
- Disabled, selected, checked, error and progress states are represented semantically.
- Status banners and loading states use polite live regions when appropriate.
- Components use `start`/`end` behavior and inherit layout direction.
- Color is never the sole signal for date selection, disabled state or validation.
- Reduced motion has a real non-animated rendering path.

## Calendar

Each visible day exposes:

- full Persian date description
- selected state
- range start/end/in-range state
- unavailable state
- today state
- adjacent-month state
- indicator count and optional caller description

Weekday headers expose full names even when short labels are rendered. Previous/next month actions receive explicit labels and direction-aware icons.

## Validation procedure

Static semantics are not sufficient for final approval. Before a stable release:

1. Run Compose UI semantics tests.
2. Test TalkBack in Persian and English.
3. Test font scales 1.0, 1.3 and 2.0.
4. Test dark theme and high-contrast system settings.
5. Test RTL and LTR.
6. Test Switch Access or keyboard navigation where product requirements include it.
7. Run Android Accessibility Scanner against the catalog.

Until those runtime checks pass, accessibility status is **implemented but not fully verified**.
