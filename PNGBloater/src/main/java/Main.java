import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.concurrent.Callable;

// Inspired by https://picocli.info/quick-guide.html#_subcommands_example and Claude.

@Command(name = "pngbloater",
        mixinStandardHelpOptions = true,
        version = "pngbloater 1.0",
        description = "Multiplies or reduces the file size of a PNG file without affecting the image itself.",
        subcommands = {Main.Bloat.class, Main.Debloat.class})
public class Main implements Runnable {

    @Override
    public void run() {
        // Invoked when no subcommand is given: show usage instead of doing nothing.
        new CommandLine(this).usage(System.out);
    }

    @Command(name = "bloat", mixinStandardHelpOptions = true, version = "pngbloater 1.0",
            description = "Multiplies the file size of a PNG file without affecting the image itself.")
    static class Bloat implements Callable<Integer> {

        @Spec CommandSpec spec;

        @Parameters(index = "0", description = "The file to bloat.")
        private File file;

        @Parameters(index = "1", description = "The output file path.")
        private String output;

        @Option(names = {"-f", "--factor"}, description = "The factor by which to multiply the file size. Only used when bloating.")
        private int factor = 2;

        @Override
        public Integer call() throws Exception {
            if (!file.exists()) {
                throw new ParameterException(spec.commandLine(), "Given file: '" + file + "' does not exist.");
            }
            if (!FilenameUtils.getExtension(file.toPath().toString()).equals("png")) {
                throw new ParameterException(spec.commandLine(), "Given file: '" + file + "' must be a png image.");
            }
            if(factor < 1) {
                throw new ParameterException(spec.commandLine(), "Factor must be larger than 0, given: " + factor);
            }
            System.out.printf("File path: %s\nOutput file: %s\nFactor: %d\nMode: bloat", file, output, factor);
            return 0;
        }
    }

    @Command(name = "debloat", mixinStandardHelpOptions = true, version = "pngbloater 1.0",
            description = "Reduces a bloated PNG file without affecting the image itself.")
    static class Debloat implements Callable<Integer> {

        @Spec CommandSpec spec;

        @Parameters(index = "0", description = "The file to debloat.")
        private File file;

        @Parameters(index = "1", description = "The output file path.")
        private String output;

        @Override
        public Integer call() throws Exception {
            //byte[] fileContents = Files.readAllBytes(file.toPath());
            if (!file.exists()) {
                throw new ParameterException(spec.commandLine(), "Given file: '" + file + "' does not exist.");
            }
            if (FilenameUtils.getExtension(file.toPath().toString()) != "png") {
                throw new ParameterException(spec.commandLine(), "Given file: '" + file + "' must be a png image.");
            }
            System.out.printf("File path: %s\nOutput file: %s\nMode: debloat", file, output);
            return 0;
        }
    }

    public static void main(String... args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
}