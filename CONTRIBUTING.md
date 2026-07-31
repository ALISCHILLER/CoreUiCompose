# Contributing

1. Create a focused branch.
2. Keep dependency direction aligned with `docs/ARCHITECTURE.md`.
3. Add or update tests for behavioral changes.
4. Run unit tests, lint, consumer-smoke and catalog assembly.
5. Document public API changes in `CHANGELOG.md` and `MIGRATION.md`.
6. Do not remove copyright or third-party attribution.

## Public API checklist

- Is the type meant to be public?
- Does it expose an internal module type?
- Is `Modifier` supported where appropriate?
- Is state hoisted and saveable?
- Are callbacks explicit?
- Is RTL and accessibility behavior covered?
- Does the change require deprecation or migration guidance?
