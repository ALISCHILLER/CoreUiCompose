# Release Checklist

## Version and compatibility

- [ ] Replace `-SNAPSHOT` with the release version.
- [ ] Update catalog `versionCode` / `versionName`.
- [ ] Review public API diff and deprecations.
- [ ] Generate/review the ABI baseline before the first stable publication.
- [ ] Confirm changelog, migration and rollback notes.
- [ ] Confirm no unsupported Kotlin/AGP/Compose combination.

## Build and tests

- [ ] `./scripts/static-audit.sh` passes.

- [ ] Fresh-clone dependency resolution succeeds.
- [ ] `:core-ui-calendar-core:test` passes.
- [ ] `:core-ui-calendar:testDebugUnitTest` passes.
- [ ] All library `lintRelease` tasks pass.
- [ ] `consumer-smoke` release assembly passes.
- [ ] Catalog debug and minified release assemblies pass.
- [ ] Connected component and calendar UI tests pass.
- [ ] Catalog is manually smoke-tested in light, dark, RTL, LTR and large font.

## Publication

- [ ] Publish all modules to the temporary repository.
- [ ] `maven-consumer:assembleRelease` resolves only Maven coordinates.
- [ ] Inspect POM dependency scopes and versions.
- [ ] Inspect sources JAR, JVM JAR and AAR contents.
- [ ] Generate and inspect Dokka output.
- [ ] Run an external consumer outside the repository.

## Security and legal

- [ ] CodeQL and dependency review pass.
- [ ] Secret scan is clean.
- [ ] CI permissions remain least-privilege.
- [ ] `LICENSE`, `NOTICE`, `PRIVACY.md` and `THIRD_PARTY_NOTICES.md` are reviewed.
- [ ] No third-party attribution was replaced by project branding.

## Release

- [ ] Artifacts are traceable to an immutable commit/tag.
- [ ] Rollback version and consumer migration are documented.
- [ ] Tag only after every required gate passes.

## PRD and traceability gate

- [ ] `python3 scripts/verify-prd-traceability.py` passes.
- [ ] Every `FR-*` requirement has module, evidence, gate and current status.
- [ ] `release/release-gates.json` reflects the actual executed verification state.
- [ ] `sourceRevision` and release checksums are recorded before publishing.
- [ ] No stable or production-ready status is declared while a required gate is blocked, partial or not executed.
