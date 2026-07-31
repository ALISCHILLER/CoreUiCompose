# Privacy

MSA Core UI is an Android UI library. The library itself does not:

- make network requests
- collect analytics
- read contacts, location, files or device identifiers
- persist user selections
- transmit calendar values
- request Android permissions

The Calendar APIs receive dates, configuration and callbacks from the consuming application. The consuming application owns storage, telemetry, backend transmission, consent, retention, deletion and privacy disclosures.

The Catalog application is a local demonstration target. Its manifest disables Android backup and cleartext traffic and it contains no production endpoint or analytics integration.

Consumers must review their own data flow, logging, crash reporting, backup behavior and privacy policy before release.
