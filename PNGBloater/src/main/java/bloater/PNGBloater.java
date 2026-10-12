package bloater;

import java.io.*;
import java.util.ArrayList;

import img.PNG;

public class PNGBloater implements ImageBloater<PNG> {

    @Override
    public PNG bloat(PNG img, int size) {
        ArrayList<PNG.Chunk> chunks = new ArrayList<>(size / 12);
        for(int i = 0; i < (size / 12); i++) {
            chunks.add(PNG.CreateChunk(0, "prIV", new byte[0]));
        }

        img.AppendImageContent(chunks);
        return img;
    }

    @Override
    public PNG debloat(PNG img) {
        img.RemoveChunksByType(PNG.Chunk.ChunkType.prIV);
        return img;
    }
}
