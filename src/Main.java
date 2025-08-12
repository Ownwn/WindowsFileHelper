import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class Main {
    public static final Set<String> zipEndings = Set.of("zip", "7z", "tar", "jar");
    public static final String sevenZipLocation = "C:\\Program Files\\7-Zip\\7z.exe";

    public static final Path downloadsPath = Paths.get(System.getProperty("user.home"), "Downloads");
    public static final Path randomPath = Path.of("C:\\Home\\Area\\Random"); // todo customisable

    public static void main(String[] args) {
        if (!System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            throw new RuntimeException("Only windows supported!");
        }

        HotkeyListener.init(() -> {
            Map<String, Runnable> buttons = new LinkedHashMap<>();
            buttons.put("Show latest download", Main::showLatestDownloadsFile);
            buttons.put("Move download to Random", Main::moveLatestDownloadToRandom);
            buttons.put("Open cmd in Random", Main::openCmd);
            buttons.put("Trash latest Download", Main::trashLatestDownload);
            new ChooserDialog(buttons);
        });

    }

    public static void trashLatestDownload() {
        getLatestDownload().ifPresentOrElse(Main::trashFile, () -> {
            Toolkit.getDefaultToolkit().beep();
            throw new RuntimeException("Latest download not found!");
        });
    }

    public static void trashFile(File file) {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.MOVE_TO_TRASH)) {
                desktop.moveToTrash(file);
            }
        }
    }

    public static Optional<File> getLatestDownload() {
        File[] fileArray = downloadsPath.toFile().listFiles();
        if (fileArray == null) {
            Toolkit.getDefaultToolkit().beep();
            throw new RuntimeException("Cannot find files!");
        }
        return getLatestFile(fileArray);
    }

    public static void showLatestDownloadsFile() {
        Optional<File> latestFile = getLatestDownload();
        if (latestFile.isEmpty()) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        showInExplorer(latestFile.get());
    }

    public static void openCmd() {
        CommandCall cmd = new CommandCall("cmd.exe", "/c", "start", "/D", randomPath.toString());
        runCommand(cmd);
    }

    public static void moveLatestDownloadToRandom() {
        File latestFile = getLatestDownload().orElseThrow(() -> {
            Toolkit.getDefaultToolkit().beep();
            return new RuntimeException("Missing file");
        });

        File randomFiles = randomPath.toFile();
        if (!randomFiles.exists() || !randomFiles.isDirectory()) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        try {
            Path newFilePath = randomPath.resolve(latestFile.getName());
            if (newFilePath.toFile().exists()) {
                throw new UnsupportedOperationException("Duplicate file!");
            }
            Files.move(latestFile.toPath(),newFilePath, StandardCopyOption.ATOMIC_MOVE);
            showInExplorer(newFilePath.toFile());
        } catch (IOException | UnsupportedOperationException e) {
            Toolkit.getDefaultToolkit().beep();
            throw new RuntimeException(e);
        }
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
        CommandCall openInExplorer = new CommandCall("explorer", "/select,", file.getAbsolutePath());
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

        CommandCall unzip = new CommandCall(sevenZipLocation, "e", file.getAbsolutePath(), "-o", newFolder.getAbsolutePath());
        runCommand(unzip);

        showInExplorer(newFolder);
    }

    public static void runCommand(CommandCall command) {
        try {
            int res = new ProcessBuilder(command.getCommand()).start().waitFor();
            if (res != 0 && res != 1) { // gives exit code of 1 on success for some reason
                throw new RuntimeException("Command gave non-zero exit value: " + command);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Exception running command" + command);
        }
    }

    record CommandCall(String name, String... args) {
        public String[] getCommand() {
            String[] cmd = new String[args.length + 1];
            System.arraycopy(args, 0, cmd, 1, args.length);
            cmd[0] = name;
            return cmd;
        }

        @Override
        public String toString() {
            return name + " " + String.join(" ", args);
        }
    }
}