package com.example.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import java.util.UUID

data class VoiceUiState(
    val isReady: Boolean = false,
    val hasVoices: Boolean = true,
    val isSpeaking: Boolean = false,
    val activeLabel: String = "",
    val selectedVoiceName: String = "Default Voice",
    val errorMessage: String? = null
)

class VoiceTutorManager(context: Context) {
    private val appContext = context.applicationContext
    private var tts: TextToSpeech? = null

    private val _state = MutableStateFlow(VoiceUiState())
    val state: StateFlow<VoiceUiState> = _state.asStateFlow()

    init {
        tts = TextToSpeech(appContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val engine = tts
                if (engine != null) {
                    val langResult = engine.setLanguage(Locale.US)
                    val availableVoices = try {
                        engine.voices?.toList().orEmpty()
                    } catch (_: Exception) {
                        emptyList()
                    }
                    if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                        _state.value = _state.value.copy(
                            isReady = false,
                            hasVoices = false,
                            errorMessage = "Text-to-speech language pack is unavailable on this device."
                        )
                    } else {
                        val preferredVoice = selectNaturalMaleVoice(availableVoices)
                        if (preferredVoice != null) {
                            engine.voice = preferredVoice
                        }
                        engine.setPitch(0.95f)
                        engine.setSpeechRate(0.98f)
                        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                            override fun onStart(utteranceId: String?) {
                                _state.value = _state.value.copy(isSpeaking = true, errorMessage = null)
                            }

                            override fun onDone(utteranceId: String?) {
                                _state.value = _state.value.copy(isSpeaking = false, activeLabel = "")
                            }

                            @Deprecated("Deprecated in Java")
                            override fun onError(utteranceId: String?) {
                                _state.value = _state.value.copy(
                                    isSpeaking = false,
                                    activeLabel = "",
                                    errorMessage = "Speech playback encountered an error."
                                )
                            }
                        })
                        _state.value = _state.value.copy(
                            isReady = true,
                            hasVoices = availableVoices.isNotEmpty() || langResult >= TextToSpeech.LANG_AVAILABLE,
                            selectedVoiceName = preferredVoice?.name ?: "System English Voice",
                            errorMessage = null
                        )
                    }
                }
            } else {
                _state.value = _state.value.copy(
                    isReady = false,
                    hasVoices = false,
                    errorMessage = "No text-to-speech voices available on this device."
                )
            }
        }
    }

    private fun selectNaturalMaleVoice(voices: List<Voice>): Voice? {
        val englishVoices = voices.filter { it.locale.language.equals("en", ignoreCase = true) }
        val maleTokens = listOf("male", "-iom", "-iol", "-tpd", "-tpf", "en-us-x-tpd", "en-gb-x-gbd", "daniel", "arthur", "oliver", "ryan")
        return englishVoices.firstOrNull { voice ->
            val lower = voice.name.lowercase(Locale.ROOT)
            !voice.isNetworkConnectionRequired && maleTokens.any { token -> lower.contains(token) }
        } ?: englishVoices.firstOrNull { voice ->
            val lower = voice.name.lowercase(Locale.ROOT)
            maleTokens.any { token -> lower.contains(token) }
        } ?: englishVoices.firstOrNull { !it.isNetworkConnectionRequired }
            ?: englishVoices.firstOrNull()
    }

    fun speak(label: String, text: String) {
        val engine = tts
        val cleanText = text
            .replace("•", ". ")
            .replace("*", "")
            .replace("#", "")
            .trim()
        if (engine == null || !_state.value.isReady || !_state.value.hasVoices) {
            _state.value = _state.value.copy(
                errorMessage = "Voice Tutor unavailable: no speech synthesis voice installed."
            )
            return
        }
        if (cleanText.isBlank()) return
        val limitedText = cleanText.take(3600)
        _state.value = _state.value.copy(isSpeaking = true, activeLabel = label, errorMessage = null)
        engine.speak(limitedText, TextToSpeech.QUEUE_FLUSH, null, UUID.randomUUID().toString())
    }

    fun stop() {
        tts?.stop()
        _state.value = _state.value.copy(isSpeaking = false, activeLabel = "")
    }

    fun toggle(label: String, text: String) {
        if (_state.value.isSpeaking && _state.value.activeLabel == label) {
            stop()
        } else {
            speak(label, text)
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
