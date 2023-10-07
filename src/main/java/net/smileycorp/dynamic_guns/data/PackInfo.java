package net.smileycorp.dynamic_guns.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraftforge.common.util.MavenVersionStringHelper;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.locating.IModFile;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class PackInfo {

    private final String name, version;
    private final Collection<String> authors;
    private final Path path;
    private final boolean is_mod;
    private final boolean is_archive;

    private PackInfo(String name, String version, Collection<String> authors, Path path, boolean is_mod, boolean is_archive) {
        this.name = name;
        this.version = version;
        this.authors = authors;
        this.path = path;
        this.is_mod = is_mod;
        this.is_archive = is_archive;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public Collection<String> getAuthors() {
        return authors;
    }

    public boolean isMod() {
        return is_mod;
    }

    public boolean isArchive() {
        return is_archive;
    }

    public Path getPath() {
        return path;
    }

    @Override
    public String toString() {
        return name + "@" + version + " by " + authors;
    }

    static PackInfo deserialize(JsonObject obj, Path path, boolean isArchive) throws Exception {
        String name = obj.get("name").getAsString();
        String version = obj.get("version").getAsString();
        List<String> authors = Lists.newArrayList();
        for (JsonElement element : obj.get("authors").getAsJsonArray()) authors.add(element.getAsString());
        return new PackInfo(name, version, authors, path, false, isArchive);
    }

    public static PackInfo forMod(IModFile file) {
        IModInfo info = file.getModInfos().get(0);
        String version = MavenVersionStringHelper.artifactVersionToString(info.getVersion());
        Collection<String> authors = Lists.newArrayList(((String)info.getConfig().getConfigElement("authors").get()).split(", "));
        return new PackInfo(info.getModId(), version, authors, file.getFilePath(), true, true);
    }

}
