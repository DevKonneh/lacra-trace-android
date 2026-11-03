"""Generate every Android raster asset from the transparent logo master.

Source of truth : branding-source/lacra-logo-1024-transparent.png  (made by make_master.py from the
                  supplied JPEG; replace with a real vector/hi-res export when LACRA supplies one)
Run (repo root) : python3 branding-source/generate_assets.py

Sizes follow the Android / Google Play specs:
  * Adaptive icon layers  108x108 dp, important content inside the 66 dp safe circle
  * Splash icon (Android 12+ & androidx.core-splashscreen), no icon background: 288x288 dp canvas,
    visible inside a 192 dp circle
  * Notification small icon 24x24 dp (art ~20 dp, 2 dp padding), alpha-only silhouette
  * Play Store icon 512x512 px 32-bit PNG, full-bleed square (the store applies its own mask)
"""
import os
import numpy as np
from PIL import Image, ImageDraw, ImageFont, ImageChops
from scipy import ndimage as ndi

MASTER = 'branding-source/lacra-logo-1024-transparent.png'
RES = 'app/src/main/res'
DBG = 'app/src/debug/res'
PLAY = 'docs/play-store'
DENS = {'mdpi': 1.0, 'hdpi': 1.5, 'xhdpi': 2.0, 'xxhdpi': 3.0, 'xxxhdpi': 4.0}

GREEN = (0x22, 0x6C, 0x13, 255)       # keep in sync with res/values/colors.xml (brand_green)
GREEN_DARK = (0x14, 0x40, 0x0B, 255)
FONT_B = '/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf'

master = Image.open(MASTER).convert('RGBA')
CONTENT = 0.991   # the circular emblem fills ~99% of the master canvas


def logo(diameter_px):
    """Logo scaled so its circular emblem has the given diameter (square RGBA, transparent)."""
    side = max(1, round(diameter_px / CONTENT))
    return master.resize((side, side), Image.LANCZOS)


def disc(size, diameter, fill=(255, 255, 255, 255)):
    """Anti-aliased filled circle centred on a transparent size x size canvas."""
    ss = 4
    big = Image.new('RGBA', (size * ss, size * ss), (0, 0, 0, 0))
    d = ImageDraw.Draw(big)
    c = size * ss / 2
    r = diameter * ss / 2
    d.ellipse((c - r, c - r, c + r, c + r), fill=fill)
    return big.resize((size, size), Image.LANCZOS)


def centred(base, layer):
    base.alpha_composite(layer, ((base.width - layer.width) // 2, (base.height - layer.height) // 2))
    return base


def mono_mask(img, gamma=0.6):
    """Single-colour layer: opaque where the artwork is dark/saturated, transparent where white."""
    rgb = np.asarray(img.convert('RGB')).astype(np.float32)
    a = np.asarray(img.getchannel('A')).astype(np.float32) / 255
    dark = (1 - rgb.min(axis=2) / 255) ** gamma
    return Image.fromarray((np.clip(dark * a, 0, 1) * 255).astype(np.uint8), 'L')


def save(img, path):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    img.save(path, optimize=True)
    print(f'{path} {img.size}')


def adaptive_foreground(px, dp_scale, ribbon=False):
    """108 dp layer: white disc = 66 dp safe circle, logo inside it."""
    canvas = Image.new('RGBA', (px, px), (0, 0, 0, 0))
    canvas.alpha_composite(disc(px, 66 * dp_scale))
    centred(canvas, logo(62 * dp_scale))
    if ribbon:                                  # debug "DEV" ribbon, kept inside the 66 dp safe zone
        d = ImageDraw.Draw(canvas)
        bw, bh = 32 * dp_scale, 12 * dp_scale
        x0, y0 = (px - bw) / 2, 70 * dp_scale
        d.rounded_rectangle((x0, y0, x0 + bw, y0 + bh), radius=4 * dp_scale, fill=(230, 81, 0, 255))
        d.text((px / 2, y0 + bh / 2), 'DEV', font=ImageFont.truetype(FONT_B, round(9 * dp_scale)),
               fill=(255, 255, 255, 255), anchor='mm')
    return canvas


def adaptive_monochrome(px, dp_scale):
    canvas = Image.new('RGBA', (px, px), (0, 0, 0, 0))
    centred(canvas, logo(62 * dp_scale))
    out = Image.new('RGBA', canvas.size, (0, 0, 0, 0))
    out.putalpha(mono_mask(canvas))
    return out


def notification_icon(px):
    """Alpha-only silhouette of the inner emblem (ring text is illegible at 24 dp, so it is dropped)."""
    work = 8
    big = logo(24 * work * 4)                        # hi-res working copy
    n = big.width
    yy, xx = np.mgrid[0:n, 0:n]
    r = np.hypot(xx - n / 2, yy - n / 2) / (n / 2)
    rgb = np.asarray(big.convert('RGB')).astype(np.float32)
    a = np.asarray(big.getchannel('A')).astype(np.float32)
    # Coloured artwork inside the ring. A strict threshold plus a small erosion keeps the thin white
    # outlines between leaf, pod and beans open, so the glyph keeps internal detail instead of a blob.
    solid = (a > 128) & (rgb.min(axis=2) < 110) & (r < 0.80)
    solid = ndi.binary_opening(solid, iterations=2)
    solid = ndi.binary_erosion(solid, iterations=4)
    solid = ndi.binary_opening(solid, iterations=3)
    lab, nlab = ndi.label(solid)                      # drop speckle: keep components >= 1.5% of the largest
    if nlab:
        sizes = ndi.sum(solid, lab, range(1, nlab + 1))
        keep = [i + 1 for i, z in enumerate(sizes) if z >= 0.015 * sizes.max()]
        solid = np.isin(lab, keep)
    sil = Image.fromarray((solid * 255).astype(np.uint8), 'L')
    bbox = sil.getbbox()
    sil = sil.crop(bbox)
    side = max(sil.size)
    sq = Image.new('L', (side, side), 0)
    sq.paste(sil, ((side - sil.width) // 2, (side - sil.height) // 2))
    target = round(px * 20 / 24)                     # 20 dp art inside 24 dp icon
    sq = sq.resize((target, target), Image.LANCZOS)
    out = Image.new('RGBA', (px, px), (255, 255, 255, 0))
    mask = Image.new('L', (px, px), 0)
    mask.paste(sq, ((px - target) // 2, (px - target) // 2))
    out.putalpha(mask)
    return out


# ---- launcher (adaptive) layers -------------------------------------------------------------
for d, s in DENS.items():
    px = round(108 * s)
    save(adaptive_foreground(px, s), f'{RES}/mipmap-{d}/ic_launcher_foreground.png')
    save(adaptive_monochrome(px, s), f'{RES}/mipmap-{d}/ic_launcher_monochrome.png')
    save(adaptive_foreground(px, s, ribbon=True), f'{DBG}/mipmap-{d}/ic_launcher_dev_foreground.png')

# ---- system splash icon -------------------------------------------------------------------------
# Android 12+ / androidx.core-splashscreen, icon WITHOUT icon background: 288x288 dp canvas, only the
# central 192 dp circle is visible. The logo's brown ring text is unreadable on the green splash, so a
# white disc (188 dp, just inside the mask) is baked in and the emblem (172 dp) sits on it.
for d, s in DENS.items():
    px = round(288 * s)
    canvas = Image.new('RGBA', (px, px), (0, 0, 0, 0))
    canvas.alpha_composite(disc(px, 188 * s))
    save(centred(canvas, logo(172 * s)), f'{RES}/drawable-{d}/ic_splash_logo.png')

# ---- notification small icon: 24 dp --------------------------------------------------------------
for d, s in DENS.items():
    save(notification_icon(round(24 * s)), f'{RES}/drawable-{d}/ic_stat_notification.png')

# ---- in-app logo (header + splash composable): one 768 px density-independent bitmap -----------
save(master.resize((768, 768), Image.LANCZOS), f'{RES}/drawable-nodpi/ic_logo.png')

# ---- Google Play listing icon: 512x512, full bleed, 32-bit PNG -----------------------------------
play = Image.new('RGBA', (512, 512), GREEN)
play.alpha_composite(disc(512, 420))
centred(play, logo(396))
save(play, f'{PLAY}/ic_launcher_512.png')
