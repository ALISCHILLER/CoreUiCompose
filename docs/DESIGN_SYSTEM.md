# Design System

## Foundation tokens

### Color

Material 3 roles are extended with semantic `success`, `warning` and `info` roles. Dynamic color is opt-in to preserve brand consistency.

### Typography and shapes

`CoreUiTypography` and `CoreUiShapes` are defaults, not hard requirements. Products may inject branded Material 3 typography and shapes through `CoreUiTheme`.

### Spacing

```text
none 0 · xxs 2 · xs 4 · sm 8 · md 12 · lg 16 · xl 24 · xxl 32 · xxxl 48 dp
```

### Elevation

```text
level0 0 · level1 1 · level2 3 · level3 6 · level4 8 · level5 12 dp
```

### Sizing

- Touch target: 48dp
- Standard button: 48dp
- Compact visual button: 40dp, while Material interaction sizing remains accessible
- Field minimum: 56dp
- Icon scale: 16 / 24 / 32dp
- Content maximum: 840dp
- Dialog maximum: 560 × 760dp

### Alpha and stroke

Disabled, muted and outline opacity comes from `CoreUiAlpha`. Hairline, emphasis and strong borders come from `CoreUiStroke`; these values are not reused as visual-size tokens.

### Shared strings

`CoreUiStrings` owns fallback and accessibility labels shared by primitives. Product copy still belongs to application string resources and should be injected at `CoreUiTheme`.

### Breakpoints

```text
Compact: <= 599dp
Medium: 600–839dp
Expanded: >= 840dp
```

`CoreContentContainer` centers bounded content and provides the active `CoreUiWindowSizeClass`.

### Calendar dimensions

`PersianDatePickerDimensions` controls date content size, indicator size, adaptive month/year cell widths, selector heights and the compact-grid padding breakpoint.

### Motion

`CoreUiMotion.animationsEnabled` is the product-level reduced-motion switch. Compose also continues to respect platform animation-scale behavior. Components must provide a non-animated code path where motion is not essential.

## Component rules

Every public visual component should:

1. Accept `modifier: Modifier = Modifier`.
2. Put required arguments before optional arguments.
3. Hoist meaningful state.
4. Use explicit callbacks rather than ambiguous booleans.
5. Use semantic status roles rather than hard-coded feature colors.
6. Preserve a 48dp interactive target.
7. Require labels for icon-only actions.
8. Work in LTR and RTL without forcing global layout direction.
9. Avoid screen-size constants; use bounded specs and adaptive containers.
10. Expose selected, disabled, error and progress state through semantics.
11. Respect the Core UI motion policy.
12. Have a unit, Compose UI or consumer verification path.
