$input v_color0, v_texcoord0

#include "common.sh"
SAMPLER2D(u_texColor, 0);

// Quotient of the primitive size and the texture tile
uniform vec2 primCoefficient;

// The offset of the tile in the texture map
uniform vec2 tilePosition;

// The size of the tile in the texture map
uniform vec2 tileSize;

// The size to scale tiles to (1 = texture map size)
uniform vec2 tileScale;

void main()
{
	float sampleX = (fract(v_texcoord0.x * (primCoefficient.x * tileScale.x)) * tileSize.x) + tilePosition.x;
	float sampleY = (fract(v_texcoord0.y * (primCoefficient.y * tileScale.y)) * tileSize.y) + tilePosition.y;
	vec2 tiledCoord = vec2(sampleX, sampleY);
	gl_FragColor = texture2D(u_texColor, tiledCoord) * v_color0;
}
