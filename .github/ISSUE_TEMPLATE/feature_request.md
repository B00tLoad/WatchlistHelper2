---
name: Feature request
about: Suggest an idea for this project
title: "[FR] "
labels: New feature, triage
assignees: B00tLoad
body:
- type: textarea
  id: idea
  attributes:
    label: Feature Description
    description: "Please describe the feature you envision. Please precisely outline what you wish the Watchlist Helper to be able to do."
    placeholder: "Feature description here"
  validations:
    required: true
- type: checkboxes
  id: 2.0.0
  attributes:
    options:
      - label: I understand, that new feature will be implemented for v2.0.0 at the earliest. WLH v1 is EOL and will not receive further development efforts except for severe security concerns.
        required: true
---

