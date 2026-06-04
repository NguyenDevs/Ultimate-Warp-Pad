# Changelog

## 1.0.2 (2026-06-02)

### Bug Fixes
- Replace fragile title-based GUI detection with InventoryHolder pattern – previously relied on configurable inventory titles (messages.yml) for click/drag handling; now uses Paper's `InventoryHolder` interface for reliable inventory identification
- Fix group collision – disable entity pushing when `collision: true` to prevent players being pushed out of the warp pad during group teleport
- Updated messages.yml: refined GUI message texts and display strings

## 1.0.1
- Initial release
