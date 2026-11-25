# ICMA OM Study Portal

Installable, offline-ready PWA for ICMA Pakistan Managerial Level-1 M3 Organizational Management.

## Current features
- PWA install support and offline caching for the portal shell.
- Syllabus tracker using the supplied M3 part weightings (A 35%, B 20%, C 25%, D 20%).
- Study dashboard with syllabus completion, focus time, streak and best quiz score.
- 1-day, 3-day, 7-day and 14-day preparation plans with daily study-hour options.
- Urdu explanations for the listed topics, including a local fallback when no AI key is configured.
- Urdu text-to-speech through the device/browser voice engine, with full-screen reading mode.
- Video Clips mode with topic-specific YouTube search and an embedded player for a user-supplied clip URL.
- MCQ practice and locally saved progress.

## Deploy to Vercel
1. Import this GitHub repository into Vercel, or create a Git-linked project.
2. Keep the project root at the repository root. No build command is needed for the static PWA.
3. The api/tutor.js serverless function supports OpenRouter and Gemini.
4. Add the provider key under Vercel Project → Settings → Environment Variables:
   - OPENROUTER_API_KEY for OpenRouter
   - GEMINI_API_KEY for Google Gemini
5. Redeploy after changing environment variables.

## AI provider notes
- OpenRouter's openrouter/free route selects an available free model. Free model availability, limits and quotas can change.
- Gemini model selection is restricted to models listed with a free tier in the provider pricing documentation at the time this portal was configured. Quotas and availability are controlled by Google.
- API keys are private credentials tied to the account that creates them. Do not put keys in app.js, HTML, screenshots or Git.
- The portal does not ship with a public/shared API key. Live AI is enabled only after the owner configures their own key as a Vercel environment variable.

## Urdu voice
The portal uses the device's native Web Speech text-to-speech service with the ur-PK language hint. Actual voice availability depends on the Android/browser TTS engine and installed Urdu voice packages. Add an Urdu voice in Android Text-to-speech settings if the device does not read Urdu.

## Install on Android
Open the HTTPS deployment in Chrome → menu ⋮ → Install app (or Add to Home screen). Load the site once while online so the app shell is cached.

## Data and privacy
Topic completion, study timer, plans and quiz results are stored in local browser storage on the current device. The tutor endpoint only reads credentials from server environment variables. Tutor prompts are sent to the selected AI provider when a key is configured.
