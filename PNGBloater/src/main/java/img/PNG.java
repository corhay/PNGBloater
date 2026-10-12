package main.java.img;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.zip.CRC32;

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
            try {
                ch.setType(Chunk.ChunkType.valueOf(new String(type_bytes, StandardCharsets.US_ASCII)));
            } catch (IllegalArgumentException e) {
                ch.setType(Chunk.ChunkType.UNKN);
            }
            ch.setData(in.readNBytes(len));
            ch.setCRC(in.readInt());
            new_png.content.add(ch);
        } while(!ch.getType().equals(Chunk.ChunkType.IEND));

        return new_png;
    }

    /**
     * This version creates a chunk with an already provided CRC
     * @param length chunk data length in bytes
     * @param type its type
     * @param data the raw data
     * @param CRC the detecting code
     * @return
     */
    public static Chunk CreateChunk(int length, String type, byte[] data, int CRC) {
        Chunk.ChunkType chunkType;
        try {
            chunkType = Chunk.ChunkType.valueOf(type);
        } catch (IllegalArgumentException e) {
            chunkType = Chunk.ChunkType.UNKN;
        }
        return new Chunk(length, chunkType, data, CRC);
    }

    /**
     * This version creates a chunk and computes the CRC
     * @param length chunk data length in bytes
     * @param type its type
     * @param data the raw data
     * @return
     */
    public static Chunk CreateChunk(int length, String type, byte[] data) {
        Chunk.ChunkType chunkType;
        try {
            chunkType = Chunk.ChunkType.valueOf(type);
        } catch (IllegalArgumentException e) {
            chunkType = Chunk.ChunkType.UNKN;
        }
        Chunk c =  new Chunk(length, chunkType, data, 0);
        c.computeCRC();
        return c;
    }

    @Override
    public Chunk ReadHeader() {
        return content.getFirst();
    }

    @Override
    public void WriteHeader(Chunk chk) {
        content.set(0,chk);
    }
    //unused
    @Override
    public boolean passesIntegrityCheck() {
        return true;
    }

    @Override
    public List<Chunk> ReadImageContent() {
        return content.stream().filter(chunk -> chunk.getType() == Chunk.ChunkType.IDAT).toList();
    }

    @Override
    public void WriteImageContent(List<Chunk> c) {
        content = (ArrayList<Chunk>) c;
    }
    @Override
    public void AppendImageContent(List<Chunk> c) {
        content.addAll(content.size()-1, c);
    }

    public void AppendSingleChunk(Chunk c) {
        content.add(content.size()-1, c);
    }

    /**
     * Removes all the chunks of a given ChunkType.
     * WARNING: removing certain types can yield an unreadble PNG file.
     * @param chunkType the type of chunks you want to remove
     */
    public void RemoveChunksByType(Chunk.ChunkType chunkType) {
        content = (ArrayList<Chunk>) content.stream().filter(chunk -> chunk.getType() != chunkType).toList();
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bos);
        try {
            out.writeLong(SIGNATURE);
            for(Chunk c : content) {
                out.write(c.length);
                out.writeBytes(c.type.toString());
                out.write(c.data);
                out.write(c.CRC);
            }
        } catch (IOException e) {
            return new byte[0];
        }

        return bos.toByteArray();
    }




    public static class Chunk {

         private int length;
         private ChunkType type;
         private byte[] data;
         private int CRC;

         public Chunk(int l, ChunkType t, byte[] d, int c) {
             length = l;
             type = t;
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

        public void computeCRC() {
             CRC32 crc = new CRC32();
             crc.update(this.type.toString().getBytes(StandardCharsets.US_ASCII));
             crc.update(data);
             this.CRC = (int)crc.getValue();
        }

        private enum ChunkType {
            //main ones
            IHDR, PLTE, IDAT, IEND,
            //custom ones : PRIV for private chunks, UNKN for unknowns
            PRIV,UNKN
        }
    }


}
