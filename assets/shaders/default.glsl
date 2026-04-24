#type vertex
#version 330 core
layout(location = 0) in vec2 aPos;
layout(location = 1) in vec4 aColor;
layout(location = 2) in vec2 aTexCoords;
layout(location = 3) in float aTexId;

uniform mat4 uProj;
uniform mat4 uView;

out vec4 fColor;
out vec2 fTexCoords;
out float fTexId;

void main() {
    fColor     = aColor;
    fTexCoords = aTexCoords;
    fTexId     = aTexId;
    gl_Position = uProj * uView * vec4(aPos, 0.0, 1.0);
}

#type fragment
#version 330 core
in vec4  fColor;
in vec2  fTexCoords;
in float fTexId;

uniform sampler2D uTextures[8];

out vec4 color;

vec4 getColor() {
    int id = int(fTexId);
    switch (id) {
        case 0:  return fColor;
        case 1:  return fColor * texture(uTextures[0], fTexCoords);
        case 2:  return fColor * texture(uTextures[1], fTexCoords);
        case 3:  return fColor * texture(uTextures[2], fTexCoords);
        case 4:  return fColor * texture(uTextures[3], fTexCoords);
        case 5:  return fColor * texture(uTextures[4], fTexCoords);
        case 6:  return fColor * texture(uTextures[5], fTexCoords);
        case 7:  return fColor * texture(uTextures[6], fTexCoords);
        case 8:  return fColor * texture(uTextures[7], fTexCoords);
        default: return fColor;
    }
}

void main() {
    color = getColor();
}
