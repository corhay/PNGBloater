import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "checksum", mixinStandardHelpOptions = true, version = "checksum 4.0",
        description = "Prints the checksum (SHA-256 by default) of a file to STDOUT.")
public class Main implements Callable<Integer> {

    @Parameters(index = "0", description = "The file whose checksum to calculate.")
    private File file;

    @Option(names = {"-a", "--algorithm"}, description = "MD5, SHA-1, SHA-256, ...")
    private String algorithm = "SHA-256";

    @Override
    public Integer call() throws Exception { // your business logic goes here...
        byte[] fileContents = Files.readAllBytes(file.toPath());
        byte[] digest = MessageDigest.getInstance(algorithm).digest(fileContents);
        System.out.printf("%0" + (digest.length*2) + "x%n", new BigInteger(1, digest));
        return 0;
    }

    // this example implements Callable, so parsing, error handling and handling user
    // requests for usage help or version help can be done with one line of code.
    public static void main(String... args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    public InputStream readFile(String filePath){
        try{
            InputStream input = new FileInputStream(filePath); 
            return new BufferedInputStream (input);
        }
        catch(IOException e){
            System.out.println("The file you just submitted does not exist");
        }
        return null;
    }

    public void writeFile(FileInputStream input, int size){
        try{
            byte[] buffer = new byte[size];
            BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream("output.png")); ;
            int n;
            while((n = input.read(buffer)) != -1){
                output.write(buffer,0,n);
            }
        }
        catch(IOException e){
            System.out.println("An error has occured during the writing of the OutputStream to a new PNG file");
        }
    }
}