/*
 * Water Tanks Monitor - Demo app
 * woodyouloveit.com
 */
package com.woodyouloveit.wtms.demo

import android.content.Context
import org.json.JSONObject

/** What a society sends from the Contact us page. All values are kept as typed. */
data class Enquiry(
    val society: String = "",
    val address: String = "",
    val city: String = "",
    val pin: String = "",
    val person: String = "",
    val role: String = "",
    val mobile: String = "",
    val email: String = "",
    val callTime: String = "",
    val buildings: String = "",
    val tanks: String = "",
    val sumps: String = "",
    val flats: String = "",
    val message: String = "",
    val consent: Boolean = false
) {
    /** Field id -> problem. Empty map means the enquiry can be sent. */
    fun problems(): Map<String, String> {
        val p = linkedMapOf<String, String>()
        if (society.isBlank()) p["society"] = "Enter the society name"
        if (address.isBlank()) p["address"] = "Enter the society address"
        if (city.isBlank()) p["city"] = "Enter the city or area"
        if (pin.isNotBlank() && !Regex("[1-9][0-9]{5}").matches(pin.trim())) p["pin"] = "PIN code has 6 digits"
        if (person.isBlank()) p["person"] = "Enter who we should talk to"
        if (mobile.isBlank()) p["mobile"] = "Enter a mobile number"
        else if (normalisedMobile() == null) p["mobile"] = "Enter a 10-digit Indian mobile number"
        if (email.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) p["email"] = "Check the email address"
        checkCount(p, "buildings", buildings, 1, 300, "Enter the number of buildings or wings")
        checkCount(p, "tanks", tanks, 1, 999, "Enter the number of overhead tanks")
        if (sumps.isNotBlank()) checkCount(p, "sumps", sumps, 0, 99, "")
        checkCount(p, "flats", flats, 1, 999, "Enter the average flats per building")
        if (!consent) p["consent"] = "Tick this so we may call or message you"
        return p
    }

    private fun checkCount(p: MutableMap<String, String>, key: String, v: String, min: Int, max: Int, emptyMsg: String) {
        val n = v.trim().toIntOrNull()
        when {
            v.isBlank() -> p[key] = emptyMsg
            n == null || n < min || n > max -> p[key] = "Use a number from $min to $max"
        }
    }

    /** "98765 43210", "+91 98765-43210" or "098765 43210" -> "9876543210"; null if not a valid Indian mobile. */
    fun normalisedMobile(): String? {
        var d = mobile.filter { it.isDigit() }
        if (d.length == 12 && d.startsWith("91")) d = d.substring(2)
        if (d.length == 11 && d.startsWith("0")) d = d.substring(1)
        return if (Regex("[6-9][0-9]{9}").matches(d)) d else null
    }

    fun totalFlats(): Int? {
        val b = buildings.trim().toIntOrNull() ?: return null
        val f = flats.trim().toIntOrNull() ?: return null
        return if (b > 0 && f > 0) b * f else null
    }

    /** The text sent on WhatsApp or shared. WhatsApp shows *text* in bold. */
    fun messageText(appVersion: String): String = buildString {
        appendLine("*Water Tanks Monitor enquiry*")
        appendLine()
        appendLine("*Society:* ${society.trim()}")
        appendLine("*Address:* ${address.trim().replace('\n', ' ')}")
        appendLine("*City / area:* ${city.trim()}" + if (pin.isNotBlank()) ", ${pin.trim()}" else "")
        appendLine()
        appendLine("*Contact:* ${person.trim()}" + if (role.isNotBlank()) " ($role)" else "")
        val m = normalisedMobile()
        appendLine("*Mobile:* " + if (m != null) "+91 ${m.substring(0, 5)} ${m.substring(5)}" else mobile.trim())
        if (email.isNotBlank()) appendLine("*Email:* ${email.trim()}")
        if (callTime.isNotBlank()) appendLine("*Best time to call:* $callTime")
        appendLine()
        appendLine("*Buildings / wings:* ${buildings.trim()}")
        appendLine("*Overhead tanks:* ${tanks.trim()}")
        if (sumps.isNotBlank()) appendLine("*Underground sumps:* ${sumps.trim()}")
        val total = totalFlats()
        appendLine("*Average flats per building:* ${flats.trim()}" + if (total != null) " (about $total flats)" else "")
        if (message.isNotBlank()) { appendLine(); appendLine("*Message:* ${message.trim()}") }
        appendLine()
        append("Sent from the WTMS Demo app $appVersion")
    }

    fun toJson(): JSONObject = JSONObject()
        .put("society", society).put("address", address).put("city", city).put("pin", pin)
        .put("person", person).put("role", role).put("mobile", mobile).put("email", email).put("callTime", callTime)
        .put("buildings", buildings).put("tanks", tanks).put("sumps", sumps).put("flats", flats)
        .put("message", message).put("consent", consent)

    companion object {
        fun fromJson(o: JSONObject) = Enquiry(
            o.optString("society"), o.optString("address"), o.optString("city"), o.optString("pin"),
            o.optString("person"), o.optString("role"), o.optString("mobile"), o.optString("email"), o.optString("callTime"),
            o.optString("buildings"), o.optString("tanks"), o.optString("sumps"), o.optString("flats"),
            o.optString("message"), o.optBoolean("consent", false)
        )
    }
}

/** Keeps the half-filled form on this phone only, so nothing is lost if the user switches apps. */
object EnquiryDraft {
    private const val PREFS = "contact"
    private fun prefs(c: Context) = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun load(c: Context): Enquiry =
        try { Enquiry.fromJson(JSONObject(prefs(c).getString("draft", "{}") ?: "{}")) } catch (e: Exception) { Enquiry() }

    fun save(c: Context, e: Enquiry) { prefs(c).edit().putString("draft", e.toJson().toString()).apply() }

    fun clear(c: Context) { prefs(c).edit().remove("draft").apply() }

    fun markSent(c: Context) { prefs(c).edit().putLong("lastSent", System.currentTimeMillis()).apply() }

    fun lastSent(c: Context): Long = prefs(c).getLong("lastSent", 0L)
}
