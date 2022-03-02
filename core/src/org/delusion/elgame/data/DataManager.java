package org.delusion.elgame.data;


import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.SystemUtils;
import org.delusion.elgame.utils.Utils;

import java.io.*;
import java.nio.file.Path;
import java.util.Objects;

public class DataManager {
    public static final Path ROOT_STORAGE_PATH = Utils.evaluate(() -> {
        if (SystemUtils.IS_OS_WINDOWS) {
            return Path.of(System.getenv("APPDATA"), "ElGame");
        } else {
            throw new NotImplementedException("Support for " + SystemUtils.OS_NAME + " is not yet implemented.");
        }
    });
    private static boolean canSave = true;

    public static void destroySaveData() {
        canSave = false;

        File ps = ROOT_STORAGE_PATH.resolve("playerdata.json").toFile();
        if (ps.exists()) {
            ps.delete();
        }

        File[] fs = ROOT_STORAGE_PATH.resolve("chunks/").toFile().listFiles();
        for (File file : Objects.requireNonNull(fs)) {
            System.out.println("Deleting Chunk " + file.getName());
            file.delete();
        }
        System.out.println("Destroyed Save Data");
    }


    public static void unlockSaves() {
        canSave = true;
    }

    public static boolean canSave() {
        return canSave;
    }

    public static void setCanSave(boolean canSave) {
        DataManager.canSave = canSave;
    }

    public static void writeTextData(String datapath, String content) {
        File fp = ROOT_STORAGE_PATH.resolve(datapath).toFile();
        try {
            if (!fp.exists()) {
                fp.createNewFile();
            }
            FileWriter writer = new FileWriter(fp);
            writer.write(content);
            writer.close();
            System.out.println("Wrote data to file '" + datapath + "'");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FileReader fileReader(String fp) throws FileNotFoundException {
        File f = ROOT_STORAGE_PATH.resolve(fp).toFile();
        if (!f.exists()) {
            return null;
        }
        return new FileReader(f);
    }
}
