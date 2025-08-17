#version 330 core

uniform sampler2D texMap;

in vec4 vTex;

out vec4 fColor;
uniform vec4 mainColor;

void main(){
	vec4 tex = texture2D(texMap, vTex.st);
	// Throw away pixels that are almost full transparent, should be good enough for simple textures with fully opaque or fully transparent pixels
	if(tex.a < 0.01) discard;
	fColor = vec4(tex.rgb, tex.a * mainColor.a);
}