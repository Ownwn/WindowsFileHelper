import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    public static final Set<String> zipEndings = Set.of("zip", "7z", "tar", "jar");
    public static final String sevenZipLocation = "C:\\Program Files\\7-Zip\\7z.exe";

    public static final Path downloadsPath = Paths.get(System.getProperty("user.home"), "Downloads");

    public static void main(String[] args) {
        if (!System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            throw new RuntimeException("Only windows supported!");
        }

        HotkeyListener.init(() -> {
            new ChooserDialog(Map.of("Show latest download", Main::showLatestDownloadsFile));
        });

    }

    public static void showLatestDownloadsFile() {
        File[] fileArray = downloadsPath.toFile().listFiles();
        if (fileArray == null) {
            Toolkit.getDefaultToolkit().beep();
            throw new RuntimeException("Cannot find files!");
        }

        Optional<File> latestFile = getLatestFile(fileArray);
        if (latestFile.isEmpty()) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        showInExplorer(latestFile.get());
    }

    public static Optional<File> getLatestFile(File[] files) {
        return Optional.ofNullable(files)
                .map(Arrays::stream)
                .flatMap(stream -> stream.max(Comparator.comparingLong(File::lastModified)));
    }

    /**
     * Warning: will also open on hidden desktop.ini file
     */
    public static void showInExplorer(File file) {
        String[] openInExplorer = {"explorer", "/select,", file.getAbsolutePath()};
        runCommand(openInExplorer);
    }

    public static void extractAndShow(File file) {
        if (zipEndings.stream().noneMatch(ending -> file.getName().endsWith(ending))) {
            throw new RuntimeException("Invalid folder to be unzipped!");
        }

        File newFolder = file.getParentFile().toPath().resolve("extract").toFile();
        if (newFolder.exists()) {
            throw new RuntimeException("Extraction folder already exists!");
        }

        String[] unzip = {sevenZipLocation, "e", file.getAbsolutePath(), "-o" + newFolder.getAbsolutePath()};
        runCommand(unzip);

        showInExplorer(newFolder);
    }

    public static void runCommand(String[] command) {
        try {
            int res = new ProcessBuilder(command).start().waitFor();
            if (res != 0 && res != 1) { // gives exit code of 1 on success for some reason
                throw new RuntimeException("Command gave non-zero exit value: " + getCommandFriendly(command));
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Exception running command" + getCommandFriendly(command));
        }
    }

    public static String getCommandFriendly(String[] command) {
        return String.join(" ", command);
    }
}