package com.luand.dbchairfix.mixin;

import com.luand.dbchairfix.config.HairFixConfig;
import JinRyuu.JRMCore.JRMCoreH;
import java.lang.reflect.Field;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "JinRyuu.JRMCore.JRMCoreHDBC", remap = false)
public abstract class MixinJRMCoreHDBCGetPlayerColor {
    private static volatile boolean jbraDnsFieldInitialized;
    private static volatile Field jbraDnsField;

    @Inject(method = "getPlayerColor", at = @At("HEAD"), cancellable = true, remap = false)
    private static void dbchairfix$preferDnsHairColor(
            int type,
            int def,
            int powerType,
            int race,
            int state,
            boolean divine,
            boolean y,
            boolean ui,
            boolean ui2,
            boolean gd,
            CallbackInfoReturnable<Integer> cir
    ) {
        HairFixConfig.ensureLoaded();
        if (!HairFixConfig.enabled) {
            return;
        }
        // This fix is for Ki power type rendering.
        if (powerType != 1) {
            return;
        }
        // Saiyan and Half-Saiyan.
        if (race != 1 && race != 2) {
            return;
        }
        if (state <= 0) {
            return;
        }
        if (!HairFixConfig.includeSsj4 && state == 14) {
            return;
        }
        if (HairFixConfig.onlyState >= 0 && state != HairFixConfig.onlyState) {
            return;
        }

        // Keep scope narrow: only replace the exact hair request path from JBRA/DBC.
        // type=0 is base hair tint and def=0 is where DBC hardcodes transformed color.
        if (type != 0 || (def & 0xFFFFFF) != 0) {
            return;
        }
        Integer baseColor = resolveHairBaseColor();
        if (baseColor == null) {
            return;
        }
        cir.setReturnValue(baseColor);
    }

    private static Integer resolveHairBaseColor() {
        try {
            String dns = resolveActiveDns();
            if (dns != null && dns.length() > 12) {
                int dnsColor = JRMCoreH.dnsHairC(dns);
                if (dnsColor > 0) {
                    return Integer.valueOf(dnsColor & 0xFFFFFF);
                }
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    private static String resolveActiveDns() {
        String dns = readJbraRenderDns();
        if (dns != null && dns.length() > 12) {
            return dns;
        }
        try {
            if (JRMCoreH.dns != null && JRMCoreH.dns.length() > 12) {
                return JRMCoreH.dns;
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    private static String readJbraRenderDns() {
        if (!jbraDnsFieldInitialized) {
            synchronized (MixinJRMCoreHDBCGetPlayerColor.class) {
                if (!jbraDnsFieldInitialized) {
                    try {
                        Class<?> cls = Class.forName("JinRyuu.JBRA.RenderPlayerJBRA");
                        Field field = cls.getDeclaredField("dns");
                        field.setAccessible(true);
                        jbraDnsField = field;
                    } catch (Throwable ignored) {
                        jbraDnsField = null;
                    }
                    jbraDnsFieldInitialized = true;
                }
            }
        }
        Field field = jbraDnsField;
        if (field == null) {
            return null;
        }
        try {
            Object value = field.get(null);
            if (value instanceof String) {
                return (String) value;
            }
        } catch (Throwable ignored) {
        }
        return null;
    }
}
