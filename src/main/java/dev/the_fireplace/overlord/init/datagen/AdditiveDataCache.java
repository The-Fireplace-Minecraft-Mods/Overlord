package dev.the_fireplace.overlord.init.datagen;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import net.minecraft.data.DataCache;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AdditiveDataCache extends DataCache {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Path root;
    private final Path recordFile;
    private int unchanged;
    private final Map<Path, String> oldSha1 = Maps.newHashMap();
    private final Map<Path, String> newSha1 = Maps.newHashMap();

    public AdditiveDataCache(Path path, String string) throws IOException {
        super(path, string);
        this.root = path;
        Path path2 = path.resolve(".cache");
        Files.createDirectories(path2);
        this.recordFile = path2.resolve(string);
        this.files().forEach((pathx) -> this.oldSha1.put(pathx, ""));
        if (Files.isReadable(this.recordFile)) {
            IOUtils.readLines(Files.newInputStream(this.recordFile), Charsets.UTF_8).forEach((stringx) -> {
                int i = stringx.indexOf(32);
                this.oldSha1.put(path.resolve(stringx.substring(i + 1)), stringx.substring(0, i));
            });
        }
    }

    @Override
    public void write() throws IOException {
        BufferedWriter writer2;
        try {
            writer2 = Files.newBufferedWriter(this.recordFile);
        } catch (IOException var3) {
            LOGGER.warn("Unable write cachefile {}: {}", this.recordFile, var3.toString());
            return;
        }

        IOUtils.writeLines(this.newSha1.entrySet().stream().map((entry) -> entry.getValue() + ' ' + this.root.relativize(entry.getKey()))
            .collect(Collectors.toList()), System.lineSeparator(), writer2);
        writer2.close();
        LOGGER.debug("Caching: cache hits: {}, created: {} removed: {}", this.unchanged, this.newSha1.size() - this.unchanged, this.oldSha1.size());
    }

    @Override
    public String getOldSha1(Path path) {
        return this.oldSha1.get(path);
    }

    @Override
    public void updateSha1(Path path, String string) {
        this.newSha1.put(path, string);
        if (Objects.equals(this.oldSha1.remove(path), string)) {
            ++this.unchanged;
        }
    }

    @Override
    public boolean contains(Path path) {
        return this.oldSha1.containsKey(path);
    }

    private Stream<Path> files() throws IOException {
        return Files.walk(this.root).filter((path) -> !Objects.equals(this.recordFile, path) && !Files.isDirectory(path));
    }
}
