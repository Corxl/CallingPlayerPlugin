package me.corxl.CorxlTeleport.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class UpdateChecker {
    private final String currentVersion;
    private final String latestVersion;
    private final int resourceId;
    private final boolean isUpToDate;

    public UpdateChecker(String currentVersion, int resourceId) throws IOException {
        this.currentVersion = currentVersion;
        this.resourceId = resourceId;
        InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream();
        Scanner scanner = new Scanner(inputStream);
        this.latestVersion = scanner.next();
        this.isUpToDate = this.latestVersion.equals(this.currentVersion);
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public int getResourceId() {
        return resourceId;
    }

    public boolean isUpToDate() {
        return isUpToDate;
    }
}
