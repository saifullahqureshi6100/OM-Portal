const FREE_OPENROUTER_MODELS = new Set([
  "openrouter/free",
  "nvidia/nemotron-3-ultra:free",
  "baidu/ernie-5.5-flash-preview:free",
  "nvidia/nemotron-3.5-super-vl:free"
]);
const FREE_GEMINI_MODELS = new Set(["gemini-3.8-flash", "gemini-2.5-flash"]);
const requestTimes = new Map();

function send(res, status, payload) {
  res.statusCode = status;
  res.setHeader("Content-Type", "application/json; charset=utf-8");
  res.setHeader("Cache-Control", "no-store, max-age=0");
  res.end(JSON.stringify(payload));
}

function allowedByRateLimit(req) {
  const ip = (req.headers["x-forwarded-for"] || "").toString().split(",")[0].trim() || "unknown";
  const now = Date.now();
  const recent = (requestTimes.get(ip) || []).filter(function (t) { return now - t < 60000; });
  if (recent.length >= 8) return false;
  recent.push(now);
  requestTimes.set(ip, recent);
  return true;
}

export default async function handler(req, res) {
  if (req.method === "GET" && req.query && req.query.status === "1") {
    return send(res, 200, {
      openrouter: Boolean(process.env.OPENROUTER_API_KEY),
      gemini: Boolean(process.env.GEMINI_API_KEY)
    });
  }
  if (req.method !== "POST") {
    res.setHeader("Allow", "GET, POST");
    return send(res, 405, { error: "Method not allowed." });
  }
  if (!allowedByRateLimit(req)) return send(res, 429, { error: "Please wait a minute before sending another tutor request." });

  try {
    const body = req.body || {};
    const provider = body.provider === "gemini" ? "gemini" : "openrouter";
    const requestedModel = typeof body.model === "string" ? body.model : "";
    const prompt = typeof body.prompt === "string" ? body.prompt.slice(0, 12000) : "";
    if (!prompt.trim()) return send(res, 400, { error: "A study question is required." });

    if (provider === "openrouter") {
      const key = process.env.OPENROUTER_API_KEY;
      if (!key) return send(res, 503, { error: "OPENROUTER_API_KEY is not configured on the Vercel server." });
      const model = FREE_OPENROUTER_MODELS.has(requestedModel) ? requestedModel : "openrouter/free";
      const response = await fetch("https://openrouter.ai/api/v1/chat/completions", {
        method: "POST",
        headers: {
          "Authorization": "Bearer " + key,
          "Content-Type": "application/json",
          "HTTP-Referer": process.env.APP_URL || "https://om-portal.vercel.app",
          "X-Title": "ICMA OM Study Portal"
        },
        body: JSON.stringify({
          model: model,
          temperature: 0.35,
          max_tokens: 1000,
          messages: [
            { role: "system", content: "You are a careful ICMA Pakistan M3 Organizational Management tutor. Answer in simple, natural Urdu script unless the student explicitly asks otherwise. Use only the supplied topic and question context. Do not invent official syllabus wording, citations or past-paper questions. Explain clearly for a beginner and distinguish facts from examples." },
            { role: "user", content: prompt }
          ]
        })
      });
      const data = await response.json().catch(function () { return {}; });
      if (!response.ok) return send(res, response.status === 429 ? 429 : 502, { error: data.error && data.error.message ? data.error.message : "OpenRouter request failed. Check your free-tier model quota and key." });
      const answer = data.choices && data.choices[0] && data.choices[0].message ? data.choices[0].message.content : "";
      if (!answer) return send(res, 502, { error: "The selected model returned an empty response." });
      return send(res, 200, { answer: answer, provider: "openrouter", model: model });
    }

    const geminiKey = process.env.GEMINI_API_KEY;
    if (!geminiKey) return send(res, 503, { error: "GEMINI_API_KEY is not configured on the Vercel server." });
    const geminiModel = FREE_GEMINI_MODELS.has(requestedModel) ? requestedModel : "gemini-3.8-flash";
    const geminiUrl = "https://generativelanguage.googleapis.com/v1beta/models/" + encodeURIComponent(geminiModel) + ":generateContent?key=" + encodeURIComponent(geminiKey);
    const geminiResponse = await fetch(geminiUrl, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        systemInstruction: { parts: [{ text: "You are a careful ICMA Pakistan M3 Organizational Management tutor. Answer in simple, natural Urdu script unless the student explicitly asks otherwise. Do not invent official syllabus wording, citations or past-paper questions. Explain for a beginner with a practical example and exam-focused revision points." }] },
        contents: [{ role: "user", parts: [{ text: prompt }] }],
        generationConfig: { temperature: 0.35, maxOutputTokens: 1000 }
      })
    });
    const geminiData = await geminiResponse.json().catch(function () { return {}; });
    if (!geminiResponse.ok) return send(res, geminiResponse.status === 429 ? 429 : 502, { error: geminiData.error && geminiData.error.message ? geminiData.error.message : "Gemini request failed. Check the model and free-tier quota." });
    const answer = geminiData.candidates && geminiData.candidates[0] && geminiData.candidates[0].content
      ? (geminiData.candidates[0].content.parts || []).map(function (part) { return part.text || ""; }).join("\n")
      : "";
    if (!answer) return send(res, 502, { error: "Gemini returned an empty response." });
    return send(res, 200, { answer: answer, provider: "gemini", model: geminiModel });
  } catch (error) {
    return send(res, 500, { error: "Tutor service error. Please retry after a moment." });
  }
}