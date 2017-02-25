
from PIL import Image 
im = Image.open('ic_launcher.png')
pixels = im.load()

for i in range (0,im.size[0]):
  for j in range (0,im.size[1]):
    if (34 <= i <= 86 and 22 <= j <= 74): continue
    if (135 <= i <= 144 and 28 <= j <= 65): continue
    if (55 <= i <= 75 and 142 <= j <= 162): continue
    if (121 <= i <= 141 and 142 <= j <= 162): continue
    sum = pixels[i, j][0] + pixels[i, j][1] + pixels[i, j][2]
    if (sum > 500):
      pixels[i, j] = (255, 128, 255)

im.save("ic_launcher_new.png", "PNG")

