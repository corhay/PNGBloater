package main.java.img;

public interface Image<H> {
    H ReadHeader();
    void WriteHeader(H header);
    boolean passesIntegrityCheck();
    void ReadImageContent();
    void WriteImageContent();


}
