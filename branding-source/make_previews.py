#!/usr/bin/env python3
"""Composite previews (NOT device screenshots) of the launcher icon and splash.
Run from repo root: python3 branding-source/make_previews.py"""
from PIL import Image, ImageDraw, ImageFont
import os
R = "app/src/main/res"
GREEN = (0x22, 0x6C, 0x13); GOLD = (0xF8, 0xD9, 0x2E)
FD = "/usr/share/fonts/truetype/liberation/"
def font(sz, style="Sans-Bold"): return ImageFont.truetype(FD + f"Liberation{style}.ttf", sz)

# ---- splash mock-up (1080x2340 px = 360x780dp @3x) ----
S = 3
W, H = 360 * S, 780 * S
im = Image.new("RGB", (W, H), GREEN)
d = ImageDraw.Draw(im)
logo = Image.open(f"{R}/drawable-xxxhdpi/ic_splash_logo.png").convert("RGBA")
logo = logo.resize((288 * S, 288 * S), Image.LANCZOS)
im.paste(logo, ((W - 288 * S) // 2, (H - 288 * S) // 2), logo)
cy = H // 2 + 126 * S
def centred(text, y, f, fill):
    w = d.textlength(text, font=f); d.text(((W - w) / 2, y), text, font=f, fill=fill)
centred("LACRA Trace", cy, font(40 * S, "Sans-Bold"), "white")
d.rounded_rectangle([(W - 56 * S) // 2, cy + 56 * S, (W + 56 * S) // 2, cy + 59 * S], 2 * S, fill=GOLD)
f2 = font(16 * S, "Sans-Regular")
lines = ["Liberia Agriculture Commodity", "Regulatory Authority"]
for i, t in enumerate(lines): centred(t, cy + 70 * S + i * 22 * S, f2, "white")
centred("From Seed to Table: Regulating for Excellence", H - 96 * S, font(14 * S, "Sans-Italic"), GOLD)
im.resize((W // 2, H // 2), Image.LANCZOS).save("docs/previews/splash-mockup.png", optimize=True)

# ---- icon preview: circle / squircle / square masks + Play icon + notification ----
def compose(fg_path, size=432, mono=False):
    bg = Image.new("RGBA", (size, size), GREEN + (255,))
    fg = Image.open(fg_path).convert("RGBA").resize((size, size), Image.LANCZOS)
    bg.alpha_composite(fg); return bg
def mask(im, shape):
    s = im.size[0]; vis = int(s * 72 / 108)   # mask area 72dp of 108dp
    o = (s - vis) // 2; c = im.crop((o, o, o + vis, o + vis))
    m = Image.new("L", c.size, 0); md = ImageDraw.Draw(m)
    if shape == "circle": md.ellipse([0, 0, *c.size], fill=255)
    elif shape == "squircle": md.rounded_rectangle([0, 0, *c.size], c.size[0] // 3.2, fill=255)
    else: md.rounded_rectangle([0, 0, *c.size], c.size[0] // 12, fill=255)
    out = Image.new("RGBA", c.size, (0, 0, 0, 0)); out.paste(c, (0, 0), m); return out
rel = compose(f"{R}/mipmap-xxxhdpi/ic_launcher_foreground.png")
dev = compose("app/src/debug/res/mipmap-xxxhdpi/ic_launcher_dev_foreground.png")
mono = Image.open(f"{R}/mipmap-xxxhdpi/ic_launcher_monochrome.png").convert("RGBA").resize((432, 432))
themed = Image.new("RGBA", (432, 432), (0xCF, 0xE8, 0xC8, 255))
tint = Image.new("RGBA", (432, 432), (0x1B, 0x3A, 0x14, 255)); tint.putalpha(mono.split()[3])
themed.alpha_composite(tint)
tiles = [("Release circle", mask(rel, "circle")), ("Release squircle", mask(rel, "squircle")),
         ("Debug (DEV)", mask(dev, "circle")), ("Themed (A13+)", mask(themed, "circle"))]
cw, ch = 330, 400
sheet = Image.new("RGB", (cw * len(tiles), ch + 340), (245, 245, 245)); sd = ImageDraw.Draw(sheet)
for i, (t, ic) in enumerate(tiles):
    ic = ic.resize((240, 240), Image.LANCZOS); sheet.paste(ic, (i * cw + 45, 40), ic)
    w = sd.textlength(t, font=font(20)); sd.text((i * cw + (cw - w) / 2, 300), t, font=font(20), fill=(30, 30, 30))
play = Image.open("docs/play-store/ic_launcher_512.png").convert("RGB").resize((240, 240), Image.LANCZOS)
sheet.paste(play, (45, 420)); sd.text((45, 670), "Play 512px (source)", font=font(20), fill=(30, 30, 30))
n = Image.open(f"{R}/drawable-xxxhdpi/ic_stat_notification.png").convert("RGBA")
nb = Image.new("RGBA", (96 * 2, 96 * 2), (60, 60, 60, 255))
nw = Image.new("RGBA", n.size, (255, 255, 255, 255)); nw.putalpha(n.split()[3])
nn = nw.resize((192, 192), Image.LANCZOS); nb.alpha_composite(nn)
sheet.paste(nb.convert("RGB"), (cw + 45, 420)); sd.text((cw + 45, 630), "Status-bar icon (24dp)", font=font(20), fill=(30, 30, 30))
sd.text((2 * cw + 45, 440), "Composite by script.\nNot a device screenshot.", font=font(20, "Sans-Regular"), fill=(90, 90, 90))
sheet.save("docs/previews/icons-preview.png", optimize=True)
print("ok")
