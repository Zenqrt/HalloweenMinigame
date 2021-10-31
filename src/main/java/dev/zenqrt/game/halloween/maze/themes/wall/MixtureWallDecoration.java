package dev.zenqrt.game.halloween.maze.themes.wall;

import dev.zenqrt.game.halloween.maze.themes.MixtureDecoration;
import kotlin.Pair;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.batch.AbsoluteBlockBatch;
import net.minestom.server.instance.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MixtureWallDecoration implements MazeWallDecoration, MixtureDecoration {

    private final List<Pair<Block, Float>> blocks = new ArrayList<>();
    private final int length,width,height;

    public MixtureWallDecoration(List<Pair<Block, Float>> blocks, int length, int width, int height) {
        this.blocks.addAll(blocks);
        this.length = length;
        this.width = width;
        this.height = height;
    }

    @Override
    public void createRightVerticalWall(AbsoluteBlockBatch batch, Point pos) {
        createVerticalWall(batch, pos);
    }

    @Override
    public void createLeftVerticalWall(AbsoluteBlockBatch batch, Point pos) {
        createVerticalWall(batch, pos.add(length - width, 0, 0));
    }

    private void createVerticalWall(AbsoluteBlockBatch batch, Point pos) {
        var random = ThreadLocalRandom.current();
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < length; z++) {
                for (int y = 0; y < height; y++) {
                    batch.setBlock(pos.add(x, y, z), chooseBlock(random));
                }
            }
        }
    }

    @Override
    public void createBottomHorizontalWall(AbsoluteBlockBatch batch, Point pos) {
        createHorizontalWall(batch, pos);
    }

    @Override
    public void createTopHorizontalWall(AbsoluteBlockBatch batch, Point pos) {
        createHorizontalWall(batch, pos.add(0, 0, length - width));
    }

    private void createHorizontalWall(AbsoluteBlockBatch batch, Point pos) {
        var random = ThreadLocalRandom.current();
        for (int x = 0; x < length; x++) {
            for (int z = 0; z < width; z++) {
                for (int y = 0; y < height; y++) {
                    batch.setBlock(pos.add(x, y, z), chooseBlock(random));
                }
            }
        }
    }

    @Override
    public List<Pair<Block, Float>> getBlocks() {
        return blocks;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

}
