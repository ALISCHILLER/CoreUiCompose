## Scope

- What changed?
- Why is the change required?

## Compatibility

- [ ] Public API impact reviewed
- [ ] Maven consumer impact reviewed
- [ ] Migration or deprecation documented when required
- [ ] No package, namespace, artifact, minSdk, or license change without an explicit plan

## Verification

- [ ] `./scripts/static-audit.sh`
- [ ] Relevant unit tests
- [ ] Relevant Compose UI tests
- [ ] Library lint
- [ ] Consumer smoke compile
- [ ] Minified catalog release compile when UI or resources changed

## Security and legal

- [ ] No secret, credential, private endpoint, or signing material added
- [ ] Third-party attribution preserved
- [ ] New dependency and license reviewed

## Rollback

Describe the smallest safe rollback for this change.
