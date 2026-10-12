package main.java.img;

import java.util.List;

public interface Image<H> {
    H ReadHeader();
    void WriteHeader(H header);
    boolean passesIntegrityCheck();
    List<H> ReadImageContent();
    void WriteImageContent(List<H> c);
    void AppendImageContent(List<H> c);
    byte[] toByteArray();


}
