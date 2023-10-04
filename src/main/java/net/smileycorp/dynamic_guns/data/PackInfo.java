package net.smileycorp.dynamic_guns.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class PackInfo {

    private final String name, version;
    private final Collection<String> authors;
    private final Path path;
    private final boolean from_mod;

    private PackInfo(String name, String version, Collection<String> authors, Path path, boolean from_mod) {
        this.name = name;
        this.version = version;
        this.authors = authors;
        this.path = path;
        this.from_mod = from_mod;
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

    public boolean isFromMod() {
        return from_mod;
    }
    public Path getPath() {
        return path;
    }

    @Override
    public String toString() {
        return name + "@" + version + " by " + authors;
    }

    static PackInfo deserialize(JsonObject obj, Path path, boolean from_mod) throws Exception {
        String name = obj.get("name").getAsString();
        String version = obj.get("version").getAsString();
        List<String> authors = Lists.newArrayList();
        for (JsonElement element : obj.get("authors").getAsJsonArray()) authors.add(element.getAsString());
        return new PackInfo(name, version, authors, path, from_mod);
    }

}
