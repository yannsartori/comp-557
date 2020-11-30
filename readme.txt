Yann Sartori 260822776
To implement the anti-aliasing and sampling, I implemented a simple grid approach. However, one thing I assume
is that the number of samples is the square of some number. If it isn't, I round it up to the next one (so 12 
samples becomes 16). For the jittering, I just sampled from a uniform distribution over the subpixel square.

The extra goodies I decided to implement are as follows:
Quadrics (0.5): I used the same approach as that Professor Kry posted on the slides, along with this useful
link (https://www.cs.uaf.edu/2012/spring/cs481/section/0/lecture/01_26_ray_intersections.html). In other words,
I solved the transpose(x)Qx = 0 equation for intersection, and set the normal to (transpose(x))A - 2b.
The image "quadric.png" shows an elliptic hyperboloid of one sheet with its axis aligned with the x axis.

Area lights (1): To implement this, I added some extra fields to Light.java: shadowSamples, radius, and n.
I then modified my ray tracer to shoot shadowSamples ray at positions distributed by a uniform distribution on
points inside the ball of radius r around a light, and then projected this result down on the plane defined by 
the normal n. To simplify things, I made the assumption that all lights were hence just closed disks (which is
why I calculated it in this manner). I then counted the number of hits, and then scaled down my colour to a value
directly proportional to the ratio of shadow hits.
The image "AreaLights.png" is TwoSpheresPlane, but with soft shadows to demonstrate this.

Mirror Reflection (0.5): For this, I added another field to material called "shiny". This was to distinguish between using
Blinn Phong reflection (to make the past images look like the Professor's) and following-the-ray reflection. For this, I first
factored out my ray colour calculations to a seperate method, and also added a specular field. This specular field initially
gets set to <1,1,1,1>-- it is meant to be the culmination of mirror reflections to represent the drop-off. Then, I just
determined the reflection direction, saved my results, culminated the specular, and recursively shot another ray. If my specular
was too small (or we hit a non-reflective surface), this recursion terminates.
The image "TwoSpheresPlaneReflective.png" shows this effect on TwoSpheresPlane, but with a shiny floor.

Boolean operations for Constructive Solid Geometry (2): For this, I created a new class called "CompositeGeom". For simplicity,
I made it so you could only do an operation on two objects, where if one wanted to create, for instance A \cap B \cap C, they
create a node (A \cap B), and the create another intesection node with this, and C. I supported union, intersection, and set
difference. To actually perform these operations, I calculated the points of entry and exit (if they existed) for both objects.
Then, with the interval defined by this entry and exit points, I perform the operation on the intervals. I then take the minimum
point of the interval, and determine which material to display, and which normal to display. One small modification I had to make
was supporting being inside a box, since I treated all my objects as hollow. To achieve this, I just added a check where if the
minimum intersection was smaller than the maximum intersection (so it was a valid intersection), the minimum negative, and the
maximum positive, to set the intersection to the maximum instead of the minimum. This made it possible to have an exit coordinate
from inside a box (which is needed for the difference operation).
The image "CompositeGeom.png" demonstrates this (where the image I decided to display is that on this page: 
https://www.wikiwand.com/en/Constructive_solid_geometry)

My final image can be found under "Lemons.png". It is a still life of a bowl of lemons, which was inspired the
previous tenants of my apartment for some reason leaving such a painting. To make the lemon model, I looked up
an image of a low poly lemon, put the lemon in Desmos, found the coordinates of the vertices along one line, and
then rotated this line across an axis (using Matlab). This has all the goodies I implemented-- the bowl is an
elliptic hyperboloid of two sheets intersected with a box (to get only one sheet), then intersected with a nested
hyperboloid (to make it not-solid). There are soft lights and the plane has a slight reflection. However, since
I decided to really deck it out, and I didn't make any attempts at optimization, it is incredibly slow. Disabling
the soft shadows aides tremendously in those regards.