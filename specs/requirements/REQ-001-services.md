# REQ-001: Services Catalog

## Status
Implemented

## Summary
Display all beauty services offered by the parlour with pricing and duration. Prices are in USD.

## Requirements

### Functional
- [x] Show a list of services grouped by category (Hair, Skin, Nails, Makeup).
- [x] Each service shows: name, description, price, duration (minutes).
- [x] Services can be filtered by category.
- [x] Admin can create and deactivate services (soft delete). Update via PUT also available.
- [x] Admin can add and remove services from the frontend admin panel.

### Non-Functional
- [x] Services list is publicly accessible (no login required).
- [ ] Page load < 2s — not formally measured; acceptable in local dev.

## Out of Scope
- Online payment for services (covered in bookings).

## Implementation Notes
- 16 seed services across all 4 categories pre-loaded via `DataInitializer`.
- All prices denominated in USD (e.g. Haircut $45, Bridal Makeup $350).
