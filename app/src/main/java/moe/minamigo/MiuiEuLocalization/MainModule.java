package moe.minamigo.MiuiEuLocalization;

import android.widget.Toast;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;

public class MainModule implements IXposedHookLoadPackage {
    List<String> XSPACE_INTRODUCE_APPS = new ArrayList();

    public MainModule() {
        this.XSPACE_INTRODUCE_APPS.add("com.tencent.mm");
        this.XSPACE_INTRODUCE_APPS.add("com.tencent.mobileqq");
        this.XSPACE_INTRODUCE_APPS.add("com.sina.weibo");
        this.XSPACE_INTRODUCE_APPS.add("com.whatsapp");
        this.XSPACE_INTRODUCE_APPS.add("com.facebook.katana");
        this.XSPACE_INTRODUCE_APPS.add("com.instagram.android");
    }

    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        String pkg = lpparam.packageName;

        switch (pkg){
            case "moe.minamigo.MiuiEuLocalization":
                try{
                    handleSelf(lpparam);
                }catch(Exception e) {
                    XposedBridge.log("Hooked " + pkg + " Error: " + e.toString());
                }
                break;
            case "com.miui.securitycore":
                try{
                    handleSecuritycore(lpparam);
                }catch(Exception e) {
                    XposedBridge.log("Hooked " + pkg + " Error: " + e.toString());
                }
                break;
            case "com.miui.home":
                try{
                    handleMiuiHome(lpparam);
                }catch(Exception e) {
                    XposedBridge.log("Hooked " + pkg + " Error: " + e.toString());
                }
                break;
            case "com.android.systemui":
                try{
                    handleSystemUI(lpparam);
                }catch(Exception e) {
                    XposedBridge.log("Hooked " + pkg + " Error: " + e.toString());
                }
                break;
            case "android":
            case "com.miui.powerkeeper":
            case "com.xiaomi.powerchecker":
            case "com.miui.core":
                try{
                    handleInternational(lpparam);
                }catch(Exception e) {
                    XposedBridge.log("Hooked " + pkg + " Error: " + e.toString());
                }
                break;
        }
    }

    private void handleSystemUI(final XC_LoadPackage.LoadPackageParam lpparam) {
        final Class<?> notificationUtilClass = XposedHelpers.findClassIfExists("com.android.systemui.statusbar.notification.NotificationUtil", lpparam.classLoader);
        if(notificationUtilClass != null){
            final Method[] notificationUtilClassDeclaredMethods = notificationUtilClass.getDeclaredMethods();
            Method shouldSubstituteSmallIconMethod = null;
            for (Method method : notificationUtilClassDeclaredMethods) {
                if (method.getName().equals("shouldSubstituteSmallIcon")) {
                    shouldSubstituteSmallIconMethod = method;
                    break;
                }
            }
            if (shouldSubstituteSmallIconMethod != null) {
                XposedBridge.hookMethod(shouldSubstituteSmallIconMethod, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                        final Class<?> classBuild = XposedHelpers.findClass("miui.os.Build", lpparam.classLoader);
                        XposedHelpers.setStaticBooleanField(classBuild, "IS_INTERNATIONAL_BUILD", true);
                        final Class<?> xClassBuild = XposedHelpers.findClass("miuix.os.Build", lpparam.classLoader);
                        XposedHelpers.setStaticBooleanField(xClassBuild, "IS_INTERNATIONAL_BUILD", true);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                        final Class<?> classBuild = XposedHelpers.findClass("miui.os.Build", lpparam.classLoader);
                        XposedHelpers.setStaticBooleanField(classBuild, "IS_INTERNATIONAL_BUILD", false);
                        final Class<?> xClassBuild = XposedHelpers.findClass("miuix.os.Build", lpparam.classLoader);
                        XposedHelpers.setStaticBooleanField(xClassBuild, "IS_INTERNATIONAL_BUILD", false);
                    }
                });
            } else{
                XposedBridge.log("Method not found: com.android.systemui.statusbar.notification.NotificationUtil.shouldSubstituteSmallIcon");
            }
        }else{
            XposedBridge.log("Class not found: com.android.systemui.statusbar.notification.NotificationUtil");
        }
    }

    private void handleMiuiHome(XC_LoadPackage.LoadPackageParam lpparam) {
        handleInternational(lpparam);

        Class<?> miuiWidgetUtilClass = XposedHelpers.findClassIfExists("com.miui.home.launcher.MIUIWidgetUtil", lpparam.classLoader);
        if(miuiWidgetUtilClass != null){
            final Method[] MiuiWidgetUtilClassDeclaredMethods = miuiWidgetUtilClass.getDeclaredMethods();
            Method isMiuiWidgetSupportMethod = null;
            for (Method method : MiuiWidgetUtilClassDeclaredMethods) {
                if (method.getName().equals("isMIUIWidgetSupport")) {
                    isMiuiWidgetSupportMethod = method;
                    break;
                }
            }
            if (isMiuiWidgetSupportMethod != null) {
                XposedBridge.hookMethod(isMiuiWidgetSupportMethod, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                        handleChina(lpparam);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                        handleInternational(lpparam);
                    }
                });
            } else{
                XposedBridge.log("Method not found: com.miui.home.launcher.MIUIWidgetUtil.isMIUIWidgetSupport");
            }
        }else{
            XposedBridge.log("Class not found: com.miui.home.launcher.MIUIWidgetUtil");
        }
    }

    private void handleSecuritycore(XC_LoadPackage.LoadPackageParam lpparam) {
        handleInternational(lpparam);
        XposedHelpers.findAndHookMethod("miui.securityspace.XSpaceUserHandle", lpparam.classLoader, "isAppInXSpace",Context.class,String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                Context context=(Context)methodHookParam.args[0];
                if(XSPACE_INTRODUCE_APPS.contains(context.getOpPackageName())){
                    methodHookParam.setResult(true);
                }
            }

        });

    }

    private void handleSelf(XC_LoadPackage.LoadPackageParam lpparam) {
        final Class<?> clazz = XposedHelpers.findClass("moe.minamigo.MiuiEuLocalization.MainActivity", lpparam.classLoader);
        XposedHelpers.setStaticBooleanField(clazz, "isXposedModuleEnable", true);
    }

    private void handleInternational(XC_LoadPackage.LoadPackageParam lpparam) {
        final Class<?> classBuild = XposedHelpers.findClass("miui.os.Build", lpparam.classLoader);
        XposedHelpers.setStaticBooleanField(classBuild, "IS_INTERNATIONAL_BUILD", true);
        XposedHelpers.setStaticBooleanField(classBuild, "IS_GLOBAL_BUILD", true);
    }
    private void handleChina(XC_LoadPackage.LoadPackageParam lpparam) {
        final Class<?> classBuild = XposedHelpers.findClass("miui.os.Build", lpparam.classLoader);
        XposedHelpers.setStaticBooleanField(classBuild, "IS_INTERNATIONAL_BUILD", false);
        XposedHelpers.setStaticBooleanField(classBuild, "IS_GLOBAL_BUILD", false);
    }
}
