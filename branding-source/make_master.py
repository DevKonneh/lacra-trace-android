"""Build a transparent 1024px master from the supplied 512px JPEG.

HONEST NOTE: this is an UPSCALE of a 512px JPEG. It removes the white background (real alpha) and
gives a 1024px canvas, but it cannot add detail that is not in the source. Replace
lacra-logo-official.jpg with a true high-res / vector export when LACRA supplies one, then re-run.
Run from repo root: python3 branding-source/make_master.py
"""
import numpy as np
from PIL import Image, ImageFilter
from scipy import ndimage as ndi

SRC = 'branding-source/lacra-logo-official.jpg'
OUT = 'branding-source/lacra-logo-1024-transparent.png'
S = 2  # upscale factor -> 1024

im = Image.open(SRC).convert('RGB')
im = im.resize((im.width * S, im.height * S), Image.LANCZOS).filter(ImageFilter.UnsharpMask(2, 55, 3))
a = np.asarray(im).astype(np.float32)
mn = a.min(axis=2)

# 1. background = near-white pixels connected to the image border (keeps enclosed cream highlights)
near_white = mn > 236
lab, _ = ndi.label(near_white)
border = np.unique(np.concatenate([lab[0], lab[-1], lab[:, 0], lab[:, -1]]))
bg = np.isin(lab, border[border > 0])

# 2. soft edge: estimate coverage against white, un-premultiply colour near the boundary
zone = ndi.binary_dilation(bg, iterations=4) & ~ndi.binary_erosion(~bg, iterations=0)
fg_mn = ndi.minimum_filter(mn, size=7)
cov = np.clip((255 - mn) / np.maximum(255 - fg_mn, 24), 0, 1)
alpha = np.where(bg, 0.0, 1.0)
edge = zone & ~bg
alpha[edge] = np.maximum(cov[edge], 0.0)
# pixels that are not bg but are in the zone and very pale get low alpha automatically
alpha = ndi.gaussian_filter(alpha, 0.6)
alpha[bg & ~ndi.binary_dilation(~bg, iterations=2)] = 0

al = np.clip(alpha, 1e-3, 1)[..., None]
rgb = np.where(edge[..., None], (a - (1 - al) * 255) / al, a)
rgb = np.clip(rgb, 0, 255)
out = np.dstack([rgb, np.clip(alpha, 0, 1) * 255]).astype(np.uint8)
img = Image.fromarray(out, 'RGBA')

# 3. crop to content, centre on a square canvas with 2% padding
bbox = img.getchannel('A').point(lambda v: 255 if v > 24 else 0).getbbox()
img = img.crop(bbox)
side = int(max(img.size) * 1.04)
sq = Image.new('RGBA', (side, side), (0, 0, 0, 0))
sq.paste(img, ((side - img.width) // 2, (side - img.height) // 2), img)
sq = sq.resize((1024, 1024), Image.LANCZOS)
sq.save(OUT, optimize=True)

al = np.asarray(sq.getchannel('A'))
ys, xs = np.nonzero(al > 128)
cy, cx = (ys.min() + ys.max()) / 2, (xs.min() + xs.max()) / 2
r = np.sqrt((xs - cx) ** 2 + (ys - cy) ** 2).max()
print('saved', OUT, sq.size, 'content bbox', xs.min(), xs.max(), ys.min(), ys.max())
print('circumscribed radius px (of 1024):', round(float(r), 1), '-> diameter fraction', round(float(2 * r / 1024), 3))
