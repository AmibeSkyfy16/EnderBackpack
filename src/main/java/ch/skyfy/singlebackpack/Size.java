package ch.skyfy.singlebackpack;

public final class Size {
    public byte rows;
    public byte columns;
    public String texturePath;

    public Size(byte rows, byte columns, String texturePath) {
        this.rows = rows;
        this.columns = columns;
        this.texturePath = texturePath;
    }
}
