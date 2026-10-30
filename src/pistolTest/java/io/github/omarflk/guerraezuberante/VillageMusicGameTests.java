package io.github.omarflk.guerraezuberante;

import com.mojang.authlib.GameProfile;
import io.github.omarflk.guerraezuberante.faction.Faction;
import io.github.omarflk.guerraezuberante.village.VillageMusic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;

@GameTestHolder(GuerraEzuberanteMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class VillageMusicGameTests {
    private record Fixture(StructureStart start, BoundingBox box) {}

    private static Fixture village(GameTestHelper helper, Faction faction, ChunkPos chunk) {
        ServerLevel level = helper.getLevel();
        var structure = level.registryAccess().registryOrThrow(Registries.STRUCTURE)
                .get(new ResourceLocation(GuerraEzuberanteMod.MOD_ID, "vila_" + faction.id()));
        var generator = level.getChunkSource().getGenerator();
        StructureStart start = structure.generate(level.registryAccess(), generator, generator.getBiomeSource(),
                level.getChunkSource().randomState(), level.getStructureManager(), level.getSeed(), chunk, 0, level,
                biome -> true);
        helper.assertTrue(start.isValid(), "Could not generate village metadata");
        BoundingBox box = start.getBoundingBox();
        level.getChunk(chunk.x, chunk.z).setStartForStructure(structure, start);
        ChunkPos.rangeClosed(new ChunkPos(box.minX() >> 4, box.minZ() >> 4),
                        new ChunkPos(box.maxX() >> 4, box.maxZ() >> 4))
                .forEach(pos -> level.getChunk(pos.x, pos.z).addReferenceForStructure(structure, chunk.toLong()));
        ChunkPos.rangeClosed(new ChunkPos((box.minX() - 16) >> 4, (box.minZ() - 16) >> 4),
                        new ChunkPos((box.maxX() + 16) >> 4, (box.maxZ() + 16) >> 4))
                .forEach(pos -> level.getChunk(pos.x, pos.z));
        return new Fixture(start, box);
    }

    private static FakePlayer player(ServerLevel level, BlockPos position) {
        FakePlayer player = new FakePlayer(level, new GameProfile(UUID.randomUUID(), "MusicTest"));
        player.setGameMode(GameType.SURVIVAL);
        player.setPos(position.getX() + .5, position.getY(), position.getZ() + .5);
        return player;
    }

    private static int villageAt(ServerPlayer player, int previous) throws Exception {
        Method method = VillageMusic.class.getDeclaredMethod("villageAt", ServerPlayer.class, int.class);
        method.setAccessible(true);
        return (int) method.invoke(null, player, previous);
    }

    @GameTest(template = "empty", batch = "village_music", timeoutTicks = 400)
    public static void missionDetectionUsesStructureMetadata(GameTestHelper helper) throws Exception {
        Fixture mission = village(helper, Faction.MISSAO, new ChunkPos(600, 600));
        FakePlayer player = player(helper.getLevel(), mission.box().getCenter());
        helper.assertTrue(villageAt(player, -1) == Faction.MISSAO.ordinal(), "Mission village not recognized");

        Fixture other = village(helper, Faction.LULISTA, new ChunkPos(625, 600));
        player.setPos(other.box().getCenter().getX(), 80, other.box().getCenter().getZ());
        helper.assertTrue(villageAt(player, -1) == -1, "Village without a track started Mission music");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "village_music", timeoutTicks = 400)
    public static void exitMarginDoesNotExpandEntry(GameTestHelper helper) throws Exception {
        Fixture fixture = village(helper, Faction.MISSAO, new ChunkPos(650, 600));
        BoundingBox box = fixture.box();
        FakePlayer player = player(helper.getLevel(), new BlockPos(box.minX() - 8, 80, box.getCenter().getZ()));
        helper.assertTrue(villageAt(player, Faction.MISSAO.ordinal()) == Faction.MISSAO.ordinal(),
                "Eight-block exit margin failed");
        helper.assertTrue(villageAt(player, -1) == -1, "Exit margin expanded the entry area");
        player.setPos(box.minX() - 9, 80, box.getCenter().getZ());
        helper.assertTrue(villageAt(player, Faction.MISSAO.ordinal()) == -1, "Music survived outside exit margin");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "village_music", timeoutTicks = 400)
    public static void invalidPlayerStatesClearMusic(GameTestHelper helper) throws Exception {
        Fixture fixture = village(helper, Faction.MISSAO, new ChunkPos(675, 600));
        FakePlayer player = player(helper.getLevel(), fixture.box().getCenter());
        player.setGameMode(GameType.SPECTATOR);
        helper.assertTrue(villageAt(player, Faction.MISSAO.ordinal()) == -1, "Spectator retained music");
        player.setGameMode(GameType.SURVIVAL);
        player.setHealth(0);
        helper.assertTrue(villageAt(player, Faction.MISSAO.ordinal()) == -1, "Dead player retained music");
        ServerLevel nether = helper.getLevel().getServer().getLevel(Level.NETHER);
        helper.assertTrue(nether != null, "Nether fixture missing");
        helper.assertTrue(villageAt(player(nether, fixture.box().getCenter()), Faction.MISSAO.ordinal()) == -1,
                "Player in another dimension retained music");
        helper.succeed();
    }

    @SuppressWarnings("unchecked")
    @GameTest(template = "empty", batch = "village_music", timeoutTicks = 400)
    public static void cachedStateIsIndependentAndClearedOnLogout(GameTestHelper helper) throws Exception {
        Fixture fixture = village(helper, Faction.MISSAO, new ChunkPos(700, 600));
        FakePlayer first = player(helper.getLevel(), fixture.box().getCenter());
        FakePlayer second = player(helper.getLevel(), fixture.box().getCenter());
        var field = VillageMusic.class.getDeclaredField("SENT");
        field.setAccessible(true);
        Map<UUID, Integer> state = (Map<UUID, Integer>) field.get(null);
        try {
            first.tickCount = 20;
            second.tickCount = 20;
            VillageMusic.playerTick(new TickEvent.PlayerTickEvent(TickEvent.Phase.END, first));
            VillageMusic.playerTick(new TickEvent.PlayerTickEvent(TickEvent.Phase.END, second));
            helper.assertTrue(state.get(first.getUUID()) == Faction.MISSAO.ordinal()
                    && state.get(second.getUUID()) == Faction.MISSAO.ordinal(), "Player state was not independent");

            first.setPos(fixture.box().maxX() + 32, 80, fixture.box().getCenter().getZ());
            first.tickCount = 40;
            VillageMusic.playerTick(new TickEvent.PlayerTickEvent(TickEvent.Phase.END, first));
            helper.assertTrue(state.get(first.getUUID()) == -1
                    && state.get(second.getUUID()) == Faction.MISSAO.ordinal(),
                    "One player leaving changed another player's state");
            VillageMusic.loggedOut(new PlayerEvent.PlayerLoggedOutEvent(second));
            helper.assertTrue(!state.containsKey(second.getUUID()), "Logout retained cached state");
        } finally {
            state.remove(first.getUUID());
            state.remove(second.getUUID());
        }
        helper.succeed();
    }

    private VillageMusicGameTests() {}
}
