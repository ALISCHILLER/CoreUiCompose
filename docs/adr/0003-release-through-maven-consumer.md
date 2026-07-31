# ADR-0003: Verify Releases Through Maven Metadata

Status: Accepted

## Context

Project dependencies can hide missing transitive metadata and `api`/`implementation` mistakes.

## Decision

Every release publishes to a temporary Maven repository and compiles a consumer that has no `project(...)` dependency.

## Consequences

Release time increases slightly, but generated POM, BOM constraints and dependency exposure are validated before promotion.
