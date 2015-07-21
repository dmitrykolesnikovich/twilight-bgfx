package twilight.bgfx;

public enum TextureFormat {
    BC1, // DXT1
    BC2, // DXT3
    BC3, // DXT5
    BC4, // LATC1/ATI1
    BC5, // LATC2/ATI2
    BC6H, // BC6H
    BC7, // BC7
    ETC1, // ETC1 RGB8
    ETC2, // ETC2 RGB8
    ETC2A, // ETC2 RGBA8
    ETC2A1, // ETC2 RGB8A1
    PTC12, // PVRTC1 RGB 2BPP
    PTC14, // PVRTC1 RGB 4BPP
    PTC12A, // PVRTC1 RGBA 2BPP
    PTC14A, // PVRTC1 RGBA 4BPP
    PTC22, // PVRTC2 RGBA 2BPP
    PTC24, // PVRTC2 RGBA 4BPP

    Unknown, // compressed formats above

    R1, R8, R16, R16F, R32, R32F, RG8, RG16, RG16F, RG32, RG32F, BGRA8, RGBA16, RGBA16F, RGBA32, RGBA32F, R5G6B5, RGBA4, RGB5A1, RGB10A2, R11G11B10F,

    UnknownDepth, // depth formats below

    D16, D24, D24S8, D32, D16F, D24F, D32F, D0S8,

    Count;
}