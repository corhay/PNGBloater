import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.concurrent.Callable;

@Command(name = "pngbloater", mixinStandardHelpOptions = true, version = "pngbloater 1.0",
        description = "Multiplies or reduces the file size of a PNG file without affecting the image itself.")
public class Main implements Callable<Integer> {

    @Parameters(index = "0", description = "The file to bloat/debloat.")
    private File file;

    @Parameters(index = "1", description = "The output file path.")
    private String output;

    @Option(names = {"-f", "--factor"}, description = "The factor by which to multiply the file size. Only used when bloating.")
    private String factor = "2";

    @Option(names = {"-d", "--debloat"}, description = "Use this option to reduce file size rather than increase.")
    private boolean debloat;

    @Override
    public Integer call() throws Exception { // your business logic goes here...
        byte[] fileContents = Files.readAllBytes(file.toPath());
        System.out.printf("File path: %s\nOutput file: %s\nFactor: %s\nDebloat: %s", file, output, factor, (debloat ? "True" : "False"));
        return 0;
    }

    // this example implements Callable, so parsing, error handling and handling user
    // requests for usage help or version help can be done with one line of code.
    public static void main(String... args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
}