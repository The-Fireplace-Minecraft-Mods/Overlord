package dev.the_fireplace.overlord.init.datagen;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AdditiveDataGenerator extends DataGenerator {
    private static final Logger LOGGER = LogManager.getLogger("Additive Data Generator");
    private final List<DataProvider> providers = Lists.newArrayList();

    public AdditiveDataGenerator(Path output, Collection<Path> collection) {
        super(output, collection);
    }

    @Override
    public void run() throws IOException {
        DataCache dataCache = new AdditiveDataCache(this.getOutput(), "cache");
        dataCache.ignore(this.getOutput().resolve("version.json"));
        Stopwatch stopwatch = Stopwatch.createStarted();
        Stopwatch stopwatch2 = Stopwatch.createUnstarted();

        for (DataProvider dataProvider : this.providers) {
            LOGGER.info("Starting provider: {}", dataProvider.getName());
            stopwatch2.start();
            dataProvider.run(dataCache);
            stopwatch2.stop();
            LOGGER.info("{} finished after {} ms", dataProvider.getName(), stopwatch2.elapsed(TimeUnit.MILLISECONDS));
            stopwatch2.reset();
        }

        LOGGER.info("All providers took: {} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
        dataCache.write();
    }

    @Override
    public void install(DataProvider dataProvider) {
        this.providers.add(dataProvider);
    }
}
