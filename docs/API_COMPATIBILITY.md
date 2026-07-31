# Public API Compatibility

## Contract

The compatibility surface consists of declarations in public packages of:

- `core-ui-foundation`
- `core-ui-components`
- `core-ui-calendar-core`
- `core-ui-calendar`
- the aggregate `core-ui` artifact

Anything with Kotlin `internal` visibility or under an `.internal` package is not a compatibility contract.

## Rules

1. Public removals and incompatible signature changes require a major release.
2. Renames use deprecation and `ReplaceWith` where a safe source migration exists.
3. New optional Compose parameters are appended and receive defaults.
4. Public collections are immutable or defensively copied.
5. Types exposed by public signatures must be published through `api`, not hidden behind `implementation`.
6. Experimental upstream APIs are not exposed unless the Core UI API is explicitly experimental too.
7. Maven metadata is verified by the independent `maven-consumer` module.

## Current enforcement

- `core-ui-calendar-core` uses Kotlin explicit API mode and warnings-as-errors.
- Source and Maven consumers compile against the aggregate artifact.
- Migration notes and semantic versioning are required by the PR/release checklist.

A generated binary ABI dump is intentionally not claimed as active because it could not be generated and verified in the delivery environment. Before the first stable publication, enable Kotlin ABI validation or another maintained binary-compatibility gate, generate the baseline from a successful build, review it, and commit it in a dedicated change.
