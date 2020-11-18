Yann Sartori 260822776
One thing I added was the ability to specify a surface by supplying a spline on either the xz or
the yz plane. Then there is a line supplying the number of surfaces each curve composing the spline
should possess. The code then takes the points and revolves it around the z-axis, generating the
control points for each tensor product patch. From there, the code functions identically.
Using this modification, I supplied the control points for the curve of a side profile of a vase
which resembles those optical illusions with the face.

I could improve the code to allow for arbitrary axis rotation. This would simply involve parsing
another input line and then creating the matrix representing the revolution about that axis.
However, since I only created one surface, I didn't see the need for this.

Another thing I added was the ability to create a rational bezier surface. To do so, first, in the
data file, the first line is "rational" to specify a rational surface. Specifically, it is a
rational, quadratic surface. The rest of the file is the same, save the 4th coordinate.
To implement this in the code, I first opted to create a separate field called "rationalPatch",
which, similar to "coordinatePatch", is a 2D array with dimensions numPatch and four of Matrix3d.
The Matrix3d is since each patch is specified by 3 quadratic curves, and four since we have four
coordinates. Depending on if we have a cubic, non-homogenous surface versus a quadratic, homogenous
surface, we set one of the two fields. Based on this, I check these fields to determine which bezier
bases to evaluate the curve at, and then call overloaded methods to handle both cases. The only
difference in the overloaded methods is, besides the changes in dimensions, that I have to divide
by w if we are evaluating a point.