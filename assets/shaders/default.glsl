#type vertex
#version 330 core

layout(location = 0) in vec3 aPos; //a=attribute
layout(location = 1) in vec4 aColor;

uniform mat4 uProj;
uniform mat4 uView;

out vec4 fColor;

void main() {
    gl_Position = uProj * uView * vec4(aPos, 1.0);
    fColor = aColor;
}

#type fragment
#version 330 core

in vec4 fColor;
out vec4 FragColor;

void main() {
    FragColor = fColor;
}