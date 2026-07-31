# Support Policy

## Supported runtime

- Android API 26 and newer.
- JDK/JVM 17 for development and compilation.
- Material 3 and Jetpack Compose versions aligned by the documented BOM/toolchain.

## Versioning

- Major: source or binary breaking public API change.
- Minor: backward-compatible component or capability.
- Patch: backward-compatible defect, documentation or metadata fix.

## Deprecation

Public APIs should be deprecated before removal. Removal normally occurs in a later major version and must include a replacement and migration example.

## Security

Security reports follow `SECURITY.md`. The library does not collect analytics, access the network, request permissions or persist consumer data by default.

## Compatibility statement

Only combinations verified by CI and documented in the release notes are supported. A README claim alone does not activate or verify a target.
