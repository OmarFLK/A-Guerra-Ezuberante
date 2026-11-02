package io.github.omarflk.guerraezuberante;

import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.platform.NativeImage;
import io.github.omarflk.guerraezuberante.faction.Faction;
import io.github.omarflk.guerraezuberante.faction.Factions;
import io.github.omarflk.guerraezuberante.registry.ModVillagers;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.*;

/** Real egg use, conversion, renderer/resource assertions and four views of EVERY profession texture. Test-only. */
@Mod.EventBusSubscriber(modid=GuerraEzuberanteMod.MOD_ID,value=Dist.CLIENT)
public final class EightSkinVisualChecks {
    private record Look(String group, int index, VillagerProfession job) {
        String key() { return index < 0 ? group : group+"_"+index; }
        String texture() { return "textures/entity/"+(index < 0 ? group : group+"/"+key())+".png"; }
    }
    private static final List<Look> LOOKS = new ArrayList<>();
    static {
        add("comunista", VillagerProfession.FARMER,VillagerProfession.NITWIT,VillagerProfession.ARMORER,VillagerProfession.LIBRARIAN);
        add("bolsonarista", VillagerProfession.FARMER,VillagerProfession.LIBRARIAN,VillagerProfession.ARMORER,VillagerProfession.BUTCHER,VillagerProfession.NONE);
        add("direitista", VillagerProfession.LIBRARIAN,VillagerProfession.FARMER,VillagerProfession.ARMORER,VillagerProfession.NONE,VillagerProfession.NITWIT);
        add("petista", VillagerProfession.FARMER,VillagerProfession.LIBRARIAN,VillagerProfession.ARMORER,VillagerProfession.BUTCHER,VillagerProfession.MASON,VillagerProfession.NITWIT);
        for (String name : List.of("ciro_gomes","eneias","romeu_zema","guilherme_boulos")) LOOKS.add(new Look(name,-1,null));
    }
    private static void add(String group,VillagerProfession... jobs) {
        for(int i=0;i<jobs.length;i++) LOOKS.add(new Look(group,i,jobs[i]));
    }
    private static final float[] YAWS = {180,0,90,270};
    private static final String[] VIEWS = {"frente","costas","lado_direito","lado_esquerdo"};
    private static int stage,ticks,total,look,view;
    private static boolean finished;
    private static BlockPos base;
    private static volatile Mob posing;
    private static volatile String serverError;
    private static final Map<String,Object> result = new LinkedHashMap<>();
    private static final List<String> errors = new ArrayList<>();

    private static boolean enabled() {
        return Boolean.getBoolean("guerra.skinsEightTest") && Minecraft.getInstance().gameDirectory.toPath().toAbsolutePath().normalize()
                .endsWith(Path.of(".tools","run-skins-eight"));
    }
    private static ServerPlayer player() {
        var mc=Minecraft.getInstance(); return mc.getSingleplayerServer().getPlayerList().getPlayer(mc.player.getUUID());
    }
    private static void server(Runnable task) {
        Minecraft.getInstance().getSingleplayerServer().execute(() -> {
            try { task.run(); } catch(Exception e) { serverError=e.toString(); }
        });
    }
    private static void next(int n) { stage=n;ticks=0; }
    private static void clear(ServerLevel level) { level.getEntitiesOfClass(Mob.class,new AABB(base).inflate(32)).forEach(Entity::discard); }

    private static Mob egg(ServerLevel level,Look item,double x,double z,float yaw) {
        var at=new BlockPos((int)Math.floor(x),base.getY(),(int)Math.floor(z));
        var id=new ResourceLocation(GuerraEzuberanteMod.MOD_ID,item.group()+"_spawn_egg");
        var egg=BuiltInRegistries.ITEM.get(id);
        if(!(egg instanceof SpawnEggItem)) throw new IllegalStateException("Not a spawn egg: "+id);
        var stack=new ItemStack(egg);
        var tag=new CompoundTag(); tag.putBoolean("NoAI",true);tag.putBoolean("Invulnerable",true);tag.putBoolean("PersistenceRequired",true);
        stack.getOrCreateTag().put("EntityTag",tag);
        var actor=player();actor.setItemInHand(InteractionHand.MAIN_HAND,stack);
        var hit=new BlockHitResult(Vec3.atCenterOf(at.below()).add(0,.5,0),Direction.UP,at.below(),false);
        if(!egg.useOn(new UseOnContext(actor,InteractionHand.MAIN_HAND,hit)).consumesAction()) throw new IllegalStateException("Egg refused: "+id);
        Mob mob=level.getEntitiesOfClass(Mob.class,new AABB(at).inflate(1.2)).stream()
                .min(Comparator.comparingDouble(m->m.distanceToSqr(x,base.getY(),z))).orElseThrow();
        mob.moveTo(x,base.getY(),z,yaw,0);mob.setNoAi(true);mob.setYHeadRot(yaw);mob.setYBodyRot(yaw);
        return mob;
    }

    private static void hold() {
        var item=LOOKS.get(look); var level=player().serverLevel();
        double x=base.getX()+.5,z=base.getZ()+3.5;
        if(posing==null || posing.isRemoved()) posing=level.getEntitiesOfClass(Mob.class,new AABB(base).inflate(7)).stream()
                .min(Comparator.comparingDouble(m->m.distanceToSqr(x,base.getY(),z))).orElseThrow();
        if(item.job()!=null) {
            if(!(posing instanceof Villager)) throw new IllegalStateException("Egg did not become villager: "+item.key());
            if(posing.getType()!=net.minecraft.world.entity.EntityType.VILLAGER) return; // First-tick conversion still pending.
            var villager=(Villager)posing;
            boolean correct=switch(item.group()) {
                case "comunista" -> ModVillagers.isComunista(villager);
                case "petista" -> ModVillagers.isPetista(villager);
                case "bolsonarista" -> ModVillagers.isBolsonarista(villager);
                default -> ModVillagers.isDireitista(villager);
            };
            if(!correct) throw new IllegalStateException("Wrong faction conversion: "+item.key());
            villager.setVillagerData(villager.getVillagerData().setProfession(item.job()));
            if(item.job()!=VillagerProfession.NITWIT && item.job()!=VillagerProfession.NONE) villager.setVillagerXp(1);
        }
        posing.moveTo(x,base.getY(),z,YAWS[view],0);posing.setYHeadRot(YAWS[view]);posing.setYBodyRot(YAWS[view]);
        posing.setDeltaMovement(Vec3.ZERO);posing.setNoAi(true);
    }

    private static void holdGroup() {
        var level=player().serverLevel();
        int[] indices={1,8,12,18};
        for(int i=0;i<4;i++) {
            var item=LOOKS.get(indices[i]);double x=base.getX()+.5+(3.5-i)*1.15,z=base.getZ()+8.5;
            level.getEntitiesOfClass(Villager.class,new AABB(base).inflate(16),v->v.getType()==net.minecraft.world.entity.EntityType.VILLAGER)
                    .stream().min(Comparator.comparingDouble(v->v.distanceToSqr(x,base.getY(),z))).ifPresent(v->{
                        v.setVillagerData(v.getVillagerData().setProfession(item.job()));
                        v.moveTo(x,base.getY(),z,180,0);v.setYHeadRot(180);v.setYBodyRot(180);v.setNoAi(true);v.setDeltaMovement(Vec3.ZERO);
                    });
        }
    }

    private static void setup() {
        var actor=player();var level=actor.serverLevel();base=new BlockPos(actor.getBlockX(),220,actor.getBlockZ());
        for(int x=-14;x<=14;x++) for(int z=-6;z<=17;z++) {
            level.setBlockAndUpdate(base.offset(x,-1,z),Blocks.GRASS_BLOCK.defaultBlockState());
            for(int y=0;y<6;y++) level.setBlockAndUpdate(base.offset(x,y,z),Blocks.AIR.defaultBlockState());
        }
        level.setDayTime(6000);level.setWeatherParameters(6000,0,false,false);
        Factions.assign(actor,Faction.CENTRO);actor.setGameMode(GameType.SPECTATOR);
        actor.teleportTo(level,base.getX()+.5,base.getY(),base.getZ()+.5,0,8);actor.setYHeadRot(0);
        clear(level);
    }

    private static void capture(String key) {
        var mc=Minecraft.getInstance();
        if(mc.screen!=null) throw new IllegalStateException("UI covers "+key);
        Screenshot.grab(mc.gameDirectory,"skin8-"+key+".png",mc.getMainRenderTarget(),ignored->{});
        result.put(key,"captured");
    }
    private static void verifyResource() throws Exception {
        var mc=Minecraft.getInstance();var item=LOOKS.get(look);
        Mob mob=mc.level.getEntitiesOfClass(Mob.class,mc.player.getBoundingBox().inflate(8)).stream()
                .min(Comparator.comparingDouble(m->m.distanceToSqr(mc.player))).orElseThrow();
        var renderer=mc.getEntityRenderDispatcher().getRenderer(mob);
        var texture=renderer.getTextureLocation(mob);
        if(!texture.getPath().equals(item.texture())) throw new IllegalStateException("Renderer bound "+texture+" instead of "+item.texture());
        var resource=mc.getResourceManager().getResource(texture).orElseThrow();
        byte[] bytes;
        try(var stream=resource.open()) { bytes=stream.readAllBytes(); }
        var root=mc.gameDirectory.toPath().toAbsolutePath().normalize().getParent().getParent();
        byte[] source=Files.readAllBytes(root.resolve("assets-entrada/skins/refatoracao-8/"+item.key()+"_skin_64x64.png"));
        var digest=MessageDigest.getInstance("SHA-256");
        if(!Arrays.equals(digest.digest(bytes),digest.digest(source))) throw new IllegalStateException("Client loaded stale pixels: "+item.key());
        try(var image=NativeImage.read(new java.io.ByteArrayInputStream(bytes))) {
            if(image.getWidth()!=64 || image.getHeight()!=64) throw new IllegalStateException("Not 64x64: "+texture);
        }
        result.put(item.key()+"-binding",texture.toString());
    }

    @SubscribeEvent public static void tick(TickEvent.ClientTickEvent event) {
        if(!enabled() || finished || event.phase!=TickEvent.Phase.END) return;
        var mc=Minecraft.getInstance();ticks++;
        if(++total>12000) { finish("Timed out at stage "+stage);return; }
        try {
            if(serverError!=null) throw new IllegalStateException(serverError);
            if(mc.getOverlay()!=null || mc.level==null || mc.player==null || mc.getSingleplayerServer()==null) return;
            switch(stage) {
                case 0 -> {
                    if(ticks<40)return;
                    if(mc.options.getSoundSourceVolume(SoundSource.MASTER)!=0) throw new IllegalStateException("Client must be muted");
                    if(mc.screen!=null)mc.setScreen(null);
                    mc.options.setCameraType(CameraType.FIRST_PERSON);mc.options.hideGui=true;mc.options.pauseOnLostFocus=false;
                    org.lwjgl.glfw.GLFW.glfwSetCursorPos(mc.getWindow().getWindow(),0,0);
                    server(EightSkinVisualChecks::setup);next(1);
                }
                case 1 -> { if(ticks>=60) next(2); }
                case 2 -> {
                    server(()->{var level=player().serverLevel();clear(level);posing=egg(level,LOOKS.get(look),base.getX()+.5,base.getZ()+3.5,YAWS[view]);});
                    next(3);
                }
                case 3 -> {
                    if(mc.screen!=null)mc.setScreen(null);
                    server(EightSkinVisualChecks::hold);
                    if(ticks<30)return;
                    verifyResource();capture(LOOKS.get(look).key()+"-"+VIEWS[view]);next(4);
                }
                case 4 -> {
                    if(ticks<8)return;
                    if(++view>=4){view=0;look++;}
                    if(look<LOOKS.size())next(2);
                    else {server(()->{
                        var level=player().serverLevel();posing=null;clear(level);
                        for(int i=0;i<8;i++) {
                            int index=new int[]{1,8,12,18,20,21,22,23}[i];
                            var item=LOOKS.get(index);var mob=egg(level,item,base.getX()+.5+(3.5-i)*1.15,base.getZ()+8.5,180);
                            if(mob instanceof Villager v && item.job()!=null)v.setVillagerData(v.getVillagerData().setProfession(item.job()));
                        }
                    });next(5);}
                }
                case 5 -> {if(mc.screen!=null)mc.setScreen(null);server(EightSkinVisualChecks::holdGroup);if(ticks>=50){capture("grupo-oito");next(6);}}
                case 6 -> {if(ticks>=10)finish(null);}
                default -> {}
            }
        } catch(Exception e) {finish(e.toString());}
    }
    private static void finish(String error) {
        if(error!=null)errors.add(error);finished=true;result.put("passed",errors.isEmpty());result.put("errors",errors);
        result.put("textures",24);result.put("eggIds",8);result.put("masterVolume",0);
        try{Files.writeString(Minecraft.getInstance().gameDirectory.toPath().resolve("skins-eight-result.json"),new GsonBuilder().setPrettyPrinting().create().toJson(result));}
        catch(Exception e){throw new RuntimeException(e);}
        Minecraft.getInstance().stop();
    }
}
