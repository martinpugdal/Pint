package dk.martinersej.pint.utils;

import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VoidGenerator extends ChunkGenerator {

    @Override
    public ChunkData generateChunkData(World world, Random random, int chunk_x, int chunk_z, BiomeGrid biomeGrid) {
        ChunkData chunk = this.createChunkData(world);
        for (int x = 0; x < 16; x++)
            for (int z = 0; z < 16; z++) biomeGrid.setBiome(x, z, Biome.JUNGLE);
        return chunk;
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return new ArrayList<>();
    }
}
