# ADR-0001: Separate Foundation, Components and Calendar

Status: Accepted

## Context

A single UI module made dependency exposure, reuse and publication harder to reason about.

## Decision

Use independent artifacts for foundation, components, calendar core, calendar UI, an aggregate artifact, and a version-alignment BOM.

## Consequences

Consumers can minimize dependency surface. Public module boundaries become release boundaries and require compatibility discipline.
