A real time Fluid simulation built with Smoother Particles Hydrodynamics, this is running inside the "core/base" I have made one day for my LibGdx projects (hence there are so many files, the fluid sim is actually only a single file).

The core simulation logic is heavily inspired by Sebastian Lague's fluid simulation video and Unity implementation - this is effectively his approach ported to LibGdx.

<p align="center" width="100%">
<video src="https://github.com/user-attachments/assets/628b8b74-d22a-450d-b00e-eca66b9f86c4" width="80" controls></video>
</p>

Features:
- SPH-based fluid behavior: density, pressure, near-pressure (anti-clustering), and viscosity forces
- Spatial hashing for fast neighbor lookups (grid-cell based, sorted spatial lookup + start-index table)
- Interactive mouse controls
- Toggleable gravity
- No memory leaks (apart from resize related leak but that's fault of the way I designed the "core" of this "framework/engine", it's unrelated to the actual fluid sim)
- Garbage collector friendly design (reusing variables)

Known limitations:
- Single threaded
- Particle count, target density, and kernel radius are tuned together, so changing one without the others can destabilize the simulation

Credits:
- https://lospec.com/gallery/skeddles/crisp-mountain-air
- https://github.com/mattdesl/lwjgl-basics/wiki/OpenGL-ES-Blurs
- https://jason.today/falling-sand
- https://www.youtube.com/watch?v=rSKMYc1CQHE