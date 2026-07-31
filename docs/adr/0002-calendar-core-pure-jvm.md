# ADR-0002: Keep Calendar Core Pure JVM

Status: Accepted

## Context

Calendar conversion and validation must be deterministic and testable without Android or Compose.

## Decision

`core-ui-calendar-core` is a Kotlin/JVM library. Android UI depends on it; it never depends on Android UI.

## Consequences

Conversion can be tested quickly and may be considered for future multiplatform extraction without committing the current product to KMP.
