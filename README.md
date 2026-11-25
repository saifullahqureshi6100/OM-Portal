# ICMA OM Study Portal

Installable, offline-ready PWA for ICMA Pakistan Managerial Level-1 M3 Organizational Management.

## Current exam-first scope
- OM syllabus organized by Parts A–D.
- Part A Operations Management prioritized at 35%.
- Topic checklist with local progress storage.
- Quick MCQ test.
- Tutor workflow entry point.
- PWA manifest + service worker for Android installation/offline use.
- No API keys are embedded in the frontend.

## Run
This is a static PWA. It can be hosted on GitHub Pages, Vercel, Netlify, or any HTTPS static host.

## Install on Android
Open the HTTPS deployment in Chrome, then choose **Install app** or **Add to Home screen**.

## Next safe upgrade
Connect the Tutor workflow to a server-side API/proxy (OpenRouter/OpenAI/etc.) so secrets never ship in browser JavaScript.