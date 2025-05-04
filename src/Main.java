import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class Main {
    public static final Set<String> zipEndings = Set.of("zip", "7z", "tar");
    public static final String sevenZipLocation = "C:\\Program Files\\7-Zip\\7z.exe";
    public static void main(String[] args) {
        if (!System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            throw new RuntimeException("Only windows supported!");
        }

        Path downloadsPath = Path.of("C:/Users/Owen/Downloads");
        File[] fileArray = downloadsPath.toFile().listFiles();
        if (fileArray == null || fileArray.length == 0) { // todo dir might be empty?
            throw new RuntimeException("Cannot find files!");
        }

        List<File> files = Arrays.asList(fileArray);
        File latestFile = files.stream().max(Comparator.comparingLong(File::lastModified)).get();

        extractAndShow(latestFile);
    }

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
            if (res != 0) {
                throw new RuntimeException("Command gave non-zero exit value " + command);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Exception running command" + command);
        }
    }
}