Sand simulation and separate SPH based Fluid Simulation built in Java and LibGdx.

This project got split into two branches: (this is where the code is)
- Blur behind UI elements like buttons and updated sand sim: https://github.com/docray5/sandbox/tree/bluries
- Real time SPH based fluid sim (inspired by Sebastian Lague): https://github.com/docray5/sandbox/tree/fluid

The aim of this project was purely to experiment with fun programming concepts and physics. I never really found time to finish this and merge into a single branch.

## Fluid sim showcase video
<p align="center" width="100%">
<video src="https://github.com/user-attachments/assets/628b8b74-d22a-450d-b00e-eca66b9f86c4" width="80" controls></video>
</p>

## Sand sim showcase video
<p align="center" width="100%">
<video src="https://github.com/user-attachments/assets/baa7c0a6-c2d5-45c8-89aa-1d334f1b5e05" width="80" controls></video>
</p>

## Fluid sim readme
A real time Fluid simulation built with Smoother Particles Hydrodynamics, this is running inside the "core/base" I have made one day for my LibGdx projects (hence there are so many files, the fluid sim is actually only a single file).

The core simulation logic is heavily inspired by Sebastian Lague's fluid simulation video and Unity implementation - this is effectively his approach ported to LibGdx.

**Features:**
- SPH-based fluid behavior: density, pressure, near-pressure (anti-clustering), and viscosity forces
- Spatial hashing for fast neighbor lookups (grid-cell based, sorted spatial lookup + start-index table)
- Interactive mouse controls
- Toggleable gravity
- No memory leaks (apart from resize related leak but that's fault of the way I designed the "core" of this "framework/engine", it's unrelated to the actual fluid sim)
- Garbage collector friendly design (reusing variables)

**Known limitations:**
- Single threaded
- Particle count, target density, and kernel radius are tuned together, so changing one without the others can destabilize the simulation

## Sand sim readme

A cellular-automata style falling-sand simulation, running inside the same "core/base" as the fluid sim. The world is stored as a 2D grid of entities (one per occupied cell), rendered directly into a `Pixmap`/`Texture` at one pixel per cell.

**Features:**
- Grid-based particle simulation
- Water, sand and solid behavior
- Velocity-driven sub-stepping, so particles don't fall with the same speed
- Randomized scan direction per row, per frame, to avoid a visible left/right bias in how elements settle
- Continuous brush strokes. Mouse movement between the last and current frame's position is interpolated so fast mouse movement doesn't leave gaps in what you paint
- Multiple brush shapes (circle, square, single pixel) with optional randomized/sparse placement for a less uniform look
- Eraser mode, using the same brush/line-interpolation logic as placing elements
- Dynamic world/gird resizing tied to window size
- Pausable simulation

**Known limitations:**
- Single-threaded, full grid scan every update
- Shares the same resize-related memory caveat noted in the fluid sim, coming from the surrounding "core/base" rather than the sand sim itself

A note on the resize memory leak: I am pretty sure that this is caused by creating, and deleting FBO objects to make blur and pixel based rendering possible.

## Info about the "base/core"
- Entity-Component-System (Ashley)
- Command pattern for UI elements like buttons, as well as for input
- Observer pattern for events between systems
- Beautiful animations for buttons, that can be configured (on click down, on hover, on clicking up and the action of the button).
- You can also make any transparent UI entity have blur behind for making beautiful UI.
- Loading screen and scene management

## Credits
- https://lospec.com/gallery/skeddles/crisp-mountain-air
- https://github.com/mattdesl/lwjgl-basics/wiki/OpenGL-ES-Blurs
- https://jason.today/falling-sand
- https://www.youtube.com/watch?v=rSKMYc1CQHE