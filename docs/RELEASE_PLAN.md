# Release Plan — MSA Core UI 5.0

## Release train

1. `5.7.0-SNAPSHOT`: active integration.
2. `5.7.0-rc.1`: all automated build and test gates pass.
3. `5.7.0-rc.2`: pilot feedback and compatibility fixes only.
4. `5.7.0`: no unresolved Critical/High issue and runtime smoke passed.

## Required artifacts

- `core-ui-bom`
- `core-ui-calendar-core`
- `core-ui-foundation`
- `core-ui-components`
- `core-ui-calendar`
- `core-ui`
- sources artifacts
- Dokka HTML
- Catalog release APK
- Maven repository bundle
- checksums
- release manifest

## Promotion policy

A build can only be promoted when the exact commit passes the release-candidate workflow. Artifact files must be produced by that workflow, not rebuilt manually after approval.

## Rollback

Consumers pin the previous stable version. Published releases are immutable. Deprecated APIs remain available for the documented migration window.
