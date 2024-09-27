#ifdef GL_ES
#define PRECISION mediump
precision PRECISION float;
precision PRECISION int;
#else
#define PRECISION;
#endif

varying vec4 vColor;
varying vec2 vTexCoord;

uniform sampler2D u_texture;

uniform vec3 COLOR;

void main() {
    vec4 texColor = texture2D(u_texture, vTexCoord);

    if (texColor.r == 0 && texColor.g == 0 && texColor.b == 0) discard;
    else texColor.rgb = COLOR;

    gl_FragColor = texColor;
}
