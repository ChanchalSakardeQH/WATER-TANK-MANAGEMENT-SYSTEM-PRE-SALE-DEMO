#!/usr/bin/env python3
"""
Water Tanks Monitor - QR codes for print and the web (woodyouloveit.com)

Makes one QR code per place it will be used, in qr/ next to this repo's README:
  <name>.svg  vector, for print shops and design tools (scales to any size)
  <name>.png  1200 px, for Word, WhatsApp status, slides
The WhatsApp codes open a chat with the message filled in, ending with "(Ref: <place>)",
so each enquiry shows where the person scanned it.

Change NUMBER or DEMO_URL below, run   python tools/make-qr.py   (needs: pip install segno)
and reprint. Always test-scan a printed proof before a large print run.
"""
import json, pathlib, urllib.parse
import segno

NUMBER   = "919284817975"     # WhatsApp number with country code, digits only
DEMO_URL = "https://chanchalsakardeqh.github.io/WATER-TANK-MANAGEMENT-SYSTEM-PRE-SALE-DEMO/"
DARK, LIGHT = "#0B3B39", "#FFFFFF"   # brand dark teal on white: high contrast, scans like black

def whatsapp(ref):
    text = f"Hi, please send a quote for the Water Tanks Monitor. (Ref: {ref})"
    return f"https://wa.me/{NUMBER}?text=" + urllib.parse.quote(text)

CODES = {
    # name               : (link, what it opens, where it is used)
    "whatsapp-brochure"  : (whatsapp("Brochure"),     "WhatsApp chat", "Society brochure, back page"),
    "whatsapp-poster"    : (whatsapp("Notice board"), "WhatsApp chat", "A4 notice-board poster"),
    "whatsapp-flyer"     : (whatsapp("Flyer"),        "WhatsApp chat", "A6 flyers, letterbox drops"),
    "whatsapp-sticker"   : (whatsapp("Sticker"),      "WhatsApp chat", "Stickers: lift, pump room, tank room, meter room"),
    "whatsapp-card"      : (whatsapp("Visiting card"),"WhatsApp chat", "Visiting cards"),
    "whatsapp-website"   : (whatsapp("Website"),      "WhatsApp chat", "Live demo page, shown on computers and projectors"),
    "live-demo"          : (DEMO_URL,                 "Live demo",     "Brochure front, poster, flyers, slides"),
    "contact-form"       : (DEMO_URL + "#contact",    "Contact us form on the live demo", "Anywhere people should fill in society details"),
}

def main():
    root = pathlib.Path(__file__).resolve().parent.parent
    out = root / "qr"; out.mkdir(exist_ok=True)
    index = {}
    for name, (link, opens, where) in CODES.items():
        qr = segno.make(link, error="m", micro=False)
        qr.save(out / f"{name}.svg", scale=10, border=4, dark=DARK, light=LIGHT, xmldecl=True, title=f"{opens}: {where}")
        size = qr.symbol_size(scale=1, border=4)[0]
        qr.save(out / f"{name}.png", scale=max(1, 1200 // size), border=4, dark=DARK, light=LIGHT)
        index[name] = {"link": link, "opens": opens, "where": where, "version": qr.version,
                       "min_print_mm": max(20, round(qr.symbol_size(scale=1, border=0)[0] * 0.5))}
        print(f"{name:18} v{qr.version:<2} {index[name]['min_print_mm']} mm min  {where}")
    (out / "qr-codes.json").write_text(json.dumps(index, indent=2), encoding="utf-8")

if __name__ == "__main__":
    main()
