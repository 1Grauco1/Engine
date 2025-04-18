#type vertex
#version 330 core

layout(location= 0) in vec3 aPos; //a=attribute
layout(location= 1) in vec4 aColor;
layout(location= 2) in vec2 aTexCoords;

uniform mat4 uProj;
uniform mat4 uView;

out vec4 fColor;
out vec2 fTexCoords;

void main() {
    gl_Position = uProj * uView * vec4(aPos, 1.0);
    fColor = aColor;
    fTexCoords= aTexCoords;
}

#type fragment
#version 330 core

uniform float uTime;
uniform sampler2D TEX_SAMPLE;

in vec4 fColor;
in vec2 fTexCoords;

out vec4 FragColor;

void main() {
    FragColor = texture(TEX_SAMPLE, fTexCoords);
}