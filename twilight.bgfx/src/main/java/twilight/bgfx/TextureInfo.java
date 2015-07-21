package twilight.bgfx;

public class TextureInfo {
    TextureFormat format;
    long storageSize;
    int width;
    int height;
    int depth;
    short numMips;
    short bitsPerPixel;

    public int getFormatOrdinal() {
        return format.ordinal();
    }

    public TextureFormat getFormat() {
        return format;
    }

    public void setFormat(TextureFormat format) {
        this.format = format;
    }

    public long getStorageSize() {
        return storageSize;
    }

    public void setStorageSize(long storageSize) {
        this.storageSize = storageSize;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public short getNumMips() {
        return numMips;
    }

    public void setNumMips(short numMips) {
        this.numMips = numMips;
    }

    public short getBitsPerPixel() {
        return bitsPerPixel;
    }

    public void setBitsPerPixel(short bitsPerPixel) {
        this.bitsPerPixel = bitsPerPixel;
    }

}