# QR codes

One QR code per place it's used. Scan any of them with a phone camera to test.

| File | Opens | Used on |
|---|---|---|
| `whatsapp-brochure` | WhatsApp chat to +91 92848 17975, *Ref: Brochure* | Society brochure, back page |
| `whatsapp-poster` | WhatsApp chat, *Ref: Notice board* | A4 notice-board poster |
| `whatsapp-flyer` | WhatsApp chat, *Ref: Flyer* | A6 flyers |
| `whatsapp-sticker` | WhatsApp chat, *Ref: Sticker* | Lift, pump room, tank room, meter room |
| `whatsapp-card` | WhatsApp chat, *Ref: Visiting card* | Visiting cards |
| `whatsapp-website` | WhatsApp chat, *Ref: Website* | Live demo page, README, slides |
| `whatsapp-email` | WhatsApp chat, *Ref: Email* | Promotional email (`../email/`) |
| `live-demo` | https://chanchalsakardeqh.github.io/WATER-TANK-MANAGEMENT-SYSTEM-PRE-SALE-DEMO/ | Brochure front, poster, flyers |
| `contact-form` | The live demo's Contact us form (`#contact`) | Anywhere society details should be filled in |

The WhatsApp message arrives already typed: *"Hi, please send a quote for the Water Tanks Monitor. (Ref: …)"*.
The Ref tells you which printed piece brought the enquiry. `qr-codes.json` lists every link in full.

## Which file to use

- **SVG** for print shops, Canva, CorelDRAW, Illustrator and Word: it stays sharp at any size.
- **PNG** (1200 px) for WhatsApp status, slides, and anywhere SVG isn't accepted.

## Printing tips

- **Size:** WhatsApp codes at least **25 mm** wide, the live demo and contact form codes at least **20 mm**.
  For a notice board seen from a distance, go much bigger: the poster uses 82 mm.
- **Keep the white border** around the code. Don't crop it or place it over a photo or pattern.
- **Dark on light only.** Don't print the code in light colours or invert it.
- **Matte is better than glossy** for stickers in bright places such as a terrace or pump room.
- **Print at 100 %** (actual size), never "fit to page", and **scan a printed proof** before a large print run.

## Changing the number or the web address

Edit `NUMBER` or `DEMO_URL` at the top of `../tools/make-qr.py`, then:

```bat
pip install segno
python tools\make-qr.py
```

All codes here are remade. The brochure, the print kit and the live demo page have the codes built in, so ask for those
to be updated too after a change.
