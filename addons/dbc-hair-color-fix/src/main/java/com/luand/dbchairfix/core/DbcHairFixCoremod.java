package com.luand.dbchairfix.core;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import java.util.Map;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

@IFMLLoadingPlugin.Name("DBC Hair Color Fix")
@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.SortingIndex(1100)
public class DbcHairFixCoremod implements IFMLLoadingPlugin {
    public DbcHairFixCoremod() {
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.dbchairfix.json");
        MixinEnvironment.getDefaultEnvironment().setSide(MixinEnvironment.Side.CLIENT);
        System.out.println("[DBC Hair Fix] Mixin bootstrap initialized");
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        // No-op
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}

