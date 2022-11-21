#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
in vec2 oneTexel;

uniform vec2 InSize;

uniform float BrightnessAdjust;

out vec4 fragColor;

void main()
{
    vec4 color = texture(DiffuseSampler, texCoord);
	
    fragColor = color * (BrightnessAdjust + 1.0);
}
