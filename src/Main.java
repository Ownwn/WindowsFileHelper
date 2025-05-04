import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Main {
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

        try {
            showInExplorer(latestFile);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error opening file!", e);
        }
    }

    public static void showInExplorer(File file) throws IOException, InterruptedException {
        String[] openInExplorer = {"explorer", "/select,", file.getAbsolutePath()};
        new ProcessBuilder(openInExplorer).start().waitFor();
    }
}