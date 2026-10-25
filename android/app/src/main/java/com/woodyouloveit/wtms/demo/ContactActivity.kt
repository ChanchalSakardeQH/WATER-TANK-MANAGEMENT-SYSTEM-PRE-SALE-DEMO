/*
 * Water Tanks Monitor - Demo app
 * woodyouloveit.com
 */
package com.woodyouloveit.wtms.demo

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ContactActivity : AppCompatActivity() {

    private lateinit var scroll: NestedScrollView
    private lateinit var consent: MaterialCheckBox
    private lateinit var consentError: TextView
    private lateinit var summary: TextView
    private lateinit var role: MaterialAutoCompleteTextView
    private lateinit var callTime: MaterialAutoCompleteTextView

    /** Text fields by the same keys Enquiry.problems() uses. */
    private val fields = linkedMapOf<String, Pair<TextInputLayout, TextInputEditText>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }
        toolbar.inflateMenu(R.menu.contact_menu)
        toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.action_clear) { confirmClear(); true } else false
        }

        scroll = findViewById(R.id.scroll)
        consent = findViewById(R.id.consent)
        consentError = findViewById(R.id.consentError)
        summary = findViewById(R.id.summary)
        role = findViewById(R.id.role)
        callTime = findViewById(R.id.time)
        role.setSimpleItems(R.array.roles)
        callTime.setSimpleItems(R.array.call_times)

        fun add(key: String, layoutId: Int, editId: Int) {
            fields[key] = findViewById<TextInputLayout>(layoutId) to findViewById<TextInputEditText>(editId)
        }
        add("society", R.id.l_society, R.id.society)
        add("address", R.id.l_address, R.id.address)
        add("city", R.id.l_city, R.id.city)
        add("pin", R.id.l_pin, R.id.pin)
        add("person", R.id.l_person, R.id.person)
        add("mobile", R.id.l_mobile, R.id.mobile)
        add("email", R.id.l_email, R.id.email)
        add("buildings", R.id.l_buildings, R.id.buildings)
        add("tanks", R.id.l_tanks, R.id.tanks)
        add("sumps", R.id.l_sumps, R.id.sumps)
        add("flats", R.id.l_flats, R.id.flats)
        add("message", R.id.l_message, R.id.message)

        fill(EnquiryDraft.load(this))

        // Clear a field's error as soon as it is edited, and keep the flats total up to date
        fields.values.forEach { (layout, edit) ->
            edit.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) { layout.error = null; updateSummary() }
            })
        }
        consent.setOnCheckedChangeListener { _, _ -> consentError.visibility = View.GONE }
        updateSummary()

        findViewById<MaterialButton>(R.id.sendWhatsapp).setOnClickListener { send(viaWhatsApp = true) }
        findViewById<MaterialButton>(R.id.sendOther).setOnClickListener { send(viaWhatsApp = false) }
    }

    override fun onPause() {
        EnquiryDraft.save(this, read())
        super.onPause()
    }

    private fun text(key: String) = fields[key]?.second?.text?.toString().orEmpty()

    private fun read() = Enquiry(
        society = text("society"), address = text("address"), city = text("city"), pin = text("pin"),
        person = text("person"), role = role.text?.toString().orEmpty(), mobile = text("mobile"),
        email = text("email"), callTime = callTime.text?.toString().orEmpty(),
        buildings = text("buildings"), tanks = text("tanks"), sumps = text("sumps"), flats = text("flats"),
        message = text("message"), consent = consent.isChecked
    )

    private fun fill(e: Enquiry) {
        val values = mapOf(
            "society" to e.society, "address" to e.address, "city" to e.city, "pin" to e.pin, "person" to e.person,
            "mobile" to e.mobile, "email" to e.email, "buildings" to e.buildings, "tanks" to e.tanks,
            "sumps" to e.sumps, "flats" to e.flats, "message" to e.message
        )
        values.forEach { (k, v) -> fields[k]?.second?.setText(v); fields[k]?.first?.error = null }
        role.setText(e.role, false)
        callTime.setText(e.callTime, false)
        consent.isChecked = e.consent
        consentError.visibility = View.GONE
    }

    private fun updateSummary() {
        val total = read().totalFlats()
        summary.text = getString(R.string.f_tanks_help) + if (total != null) ". About $total flats in total." else "."
    }

    private fun send(viaWhatsApp: Boolean) {
        val e = read()
        val problems = e.problems()
        if (problems.isNotEmpty()) { showProblems(problems); return }
        EnquiryDraft.save(this, e)
        val msg = e.messageText(BuildConfig.VERSION_NAME)
        if (viaWhatsApp) sendWhatsApp(msg) else shareOther(msg, e)
    }

    private fun showProblems(problems: Map<String, String>) {
        var first: View? = null
        for ((key, msg) in problems) {
            if (key == "consent") {
                consentError.text = msg; consentError.visibility = View.VISIBLE
                if (first == null) first = consent
            } else fields[key]?.let { (layout, edit) ->
                layout.error = msg
                if (first == null) first = edit
            }
        }
        first?.let { v ->
            scroll.post {
                val r = Rect()
                v.getDrawingRect(r)
                scroll.offsetDescendantRectToMyCoords(v, r)
                scroll.smoothScrollTo(0, (r.top - (48 * resources.displayMetrics.density).toInt()).coerceAtLeast(0))
            }
            v.requestFocus()
        }
        Toast.makeText(this, "Please check the highlighted fields", Toast.LENGTH_SHORT).show()
    }

    private fun sendWhatsApp(msg: String) {
        val uri = Uri.parse("https://wa.me/${getString(R.string.contact_whatsapp)}?text=" + Uri.encode(msg))
        for (pkg in listOf("com.whatsapp", "com.whatsapp.w4b")) {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, uri).setPackage(pkg))
                afterSend(true)
                return
            } catch (_: ActivityNotFoundException) {}
        }
        // No WhatsApp app: wa.me opens WhatsApp Web or offers the download
        try { startActivity(Intent(Intent.ACTION_VIEW, uri)); afterSend(true) }
        catch (_: ActivityNotFoundException) {
            Toast.makeText(this, "WhatsApp is not installed. Use \"Share another way\".", Toast.LENGTH_LONG).show()
        }
    }

    private fun shareOther(msg: String, e: Enquiry) {
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Water Tanks Monitor enquiry: ${e.society.trim()}")
            putExtra(Intent.EXTRA_TEXT, msg)
        }
        try { startActivity(Intent.createChooser(send, getString(R.string.send_other))); afterSend(false) }
        catch (_: ActivityNotFoundException) { Toast.makeText(this, "No app can share this", Toast.LENGTH_LONG).show() }
    }

    /** WhatsApp only fills in the message; the user still taps Send there. */
    private fun afterSend(whatsapp: Boolean) {
        EnquiryDraft.markSent(this)
        val note = if (whatsapp) "Your details are ready in WhatsApp. Tap Send there to reach us. We usually reply within a working day."
            else "Choose the app and send the message to ${getString(R.string.contact_whatsapp_display)} or your woodyouloveit contact."
        scroll.postDelayed({
            if (isFinishing) return@postDelayed
            MaterialAlertDialogBuilder(this)
                .setTitle(if (whatsapp) "Almost done" else "Share your details")
                .setMessage(note)
                .setNegativeButton("Keep the form") { _, _ -> }
                .setPositiveButton("Clear the form") { _, _ -> EnquiryDraft.clear(this); fill(Enquiry()) }
                .show()
        }, 600)
    }

    private fun confirmClear() {
        MaterialAlertDialogBuilder(this)
            .setMessage("Clear everything you’ve typed in this form?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton(R.string.clear_form) { _, _ -> EnquiryDraft.clear(this); fill(Enquiry()) }
            .show()
    }
}
