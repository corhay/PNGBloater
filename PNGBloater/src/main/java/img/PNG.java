package main.java.img;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class PNG implements Image<PNG.Chunk> {

    private final static long SIGNATURE = 0x89504E470D0A1A0AL;
    private final static int CHUNK_INITIAL_SIZE = 12;

    private ArrayList<Chunk> content;


    private PNG(){
        content = new ArrayList<Chunk>();
    }

    public static PNG parse(byte[] data) throws IOException {
        PNG new_png = new PNG();

        //wrong signature, not a PNG
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
        if (in.readLong() != SIGNATURE)  // with the long constant
            throw new IOException("Not a valid PNG file");

        //start reading chunks
        Chunk ch = new Chunk();
        do {
            int len = in.readInt();
            ch.setLength(len);
            byte[] type_bytes = new byte[4];
            in.readFully(type_bytes);
            ch.setType(Chunk.ChunkType.valueOf(new String(type_bytes, StandardCharsets.US_ASCII)));
            ch.setData(in.readNBytes(len));
            ch.setCRC(in.readInt());
            new_png.content.add(ch);
        } while(!ch.getType().equals(Chunk.ChunkType.IEND));
        return new_png;
    }

    @Override
    public Chunk ReadHeader() {
        return content.getFirst();
    }

    @Override
    public void WriteHeader(Chunk chk) {
        content.set(0,chk);
    }

    @Override
    public boolean passesIntegrityCheck() {
        return false;
    }

    @Override
    public void ReadImageContent() {
    }

    @Override
    public void WriteImageContent() {
    }




    public static class Chunk {

         private int length;
         private ChunkType type;
         private byte[] data;
         private int CRC;

         public Chunk(int l, String t, byte[] d, int c) {
             length = l;
             type = ChunkType.valueOf(t);
             data = d;
             CRC = c;

         }

         public Chunk() {
         }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public ChunkType getType() {
            return type;
        }

        public void setType(ChunkType type) {
            this.type = type;
        }

        public byte[] getData() {
            return data;
        }

        public void setData(byte[] data) {
            this.data = data;
        }

        public int getCRC() {
            return CRC;
        }

        public void setCRC(int CRC) {
            this.CRC = CRC;
        }

        private enum ChunkType {
            IHDR,
            PLTE,
            IDAT,
            IEND

        }
    }


}
