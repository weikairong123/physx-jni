package de.fabmax.physxjni;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

enum OsType {
    LINUX,
    WINDOWS,
    MACOS,
    ANDROID,
    FREEBSD,
    OPENBSD,
    NETBSD,
    SOLARIS,
    AIX,
    QNX,
    UNKNOWN;

    public String toLower() {
        return this.name().toLowerCase();
    }
}

enum CpuArch {
    X86_64("x86_64"),
    X86("x86"),
    AARCH64("aarch64"),
    ARM32("arm32"),
    RISCV64("riscv64"),
    PPC64("ppc64"),
    PPC("ppc"),
    S390X("s390x"),
    S390("s390"),
    SPARC64("sparc64"),
    SPARC("sparc"),
    UNKNOWN("unknown");

    public final String id;
    CpuArch(String id) {
        this.id = id;
    }

    public static CpuArch fromId(String id) {
        for (CpuArch a : values()) {
            if (Objects.equals(a.id, id)) return a;
        }
        return UNKNOWN;
    }
}

/**
 * 平台模型
 * 资源目录格式: /{osLower}/{archLower}
 * Native实现类统一命名为 NativeLib
 */
public final class Platform {
    public final OsType osType;
    public final CpuArch cpuArch;
    /**大写标识符 OS_ARCH*/
    public final String platformId;
    /**类全限定名，统一以 NativeLib 作为类名 */
    public final String metaClassName;
    public final boolean isFallback;
    /**资源子目录 /os/arch 无末尾斜杠 */
    public final String resourceDir;

    /**
     * 构造，支持自定义UNKNOWN扩展，方便用户自行添加自定义架构
     * @param osType 系统类型
     * @param cpuArch cpu架构
     * @param isFallback 是否回退平台
     * @param metaClassName NativeLib实现全类名（类名统一为NativeLib）
     */
    public Platform(OsType osType, CpuArch cpuArch, boolean isFallback, String metaClassName) {
        this.osType = osType;
        this.cpuArch = cpuArch;
        this.platformId = osType.name() + "_" + cpuArch.name();
        this.metaClassName = metaClassName;
        this.isFallback = isFallback;
        this.resourceDir = "/" + osType.toLower() + "/" + cpuArch.id;
    }

    private static final List<Platform> SUPPORTED_PLATFORMS;

    /**
     * 可扩展UNKNOWN回退平台，用户可自行new Platform()传入自定义类做扩展
     * 默认fallback指向 linux/x86_64 NativeLib
     */
    public static final Platform UNKNOWN = new Platform(
            OsType.UNKNOWN,
            CpuArch.UNKNOWN,
            true,
            "de.fabmax.physxjni.unknow.NativeLib"
    );

    static {
        SUPPORTED_PLATFORMS = new ArrayList<>();

        // ========== Linux 全架构 ==========
        addPlatform(OsType.LINUX, CpuArch.X86_64, false, "de.fabmax.physxjni.linux.x86_64.NativeLib");
        addPlatform(OsType.LINUX, CpuArch.X86, false, "de.fabmax.physxjni.linux.x86.NativeLib");
        addPlatform(OsType.LINUX, CpuArch.AARCH64, false, "de.fabmax.physxjni.linux.aarch64.NativeLib");
        addPlatform(OsType.LINUX, CpuArch.ARM32, false, "de.fabmax.physxjni.linux.arm32.NativeLib");
        addPlatform(OsType.LINUX, CpuArch.RISCV64, false, "de.fabmax.physxjni.linux.riscv64.NativeLib");
        addPlatform(OsType.LINUX, CpuArch.PPC64, false, "de.fabmax.physxjni.linux.ppc64.NativeLib");
        addPlatform(OsType.LINUX, CpuArch.PPC, false, "de.fabmax.physxjni.linux.ppc.NativeLib");
        addPlatform(OsType.LINUX, CpuArch.S390X, false, "de.fabmax.physxjni.linux.s390x.NativeLib");
        addPlatform(OsType.LINUX, CpuArch.S390, false, "de.fabmax.physxjni.linux.s390.NativeLib");
        addPlatform(OsType.LINUX, CpuArch.SPARC64, false, "de.fabmax.physxjni.linux.sparc64.NativeLib");
        addPlatform(OsType.LINUX, CpuArch.SPARC, false, "de.fabmax.physxjni.linux.sparc.NativeLib");

        // ========== Windows ==========
        addPlatform(OsType.WINDOWS, CpuArch.X86_64, false, "de.fabmax.physxjni.windows.x86_64.NativeLib");
        addPlatform(OsType.WINDOWS, CpuArch.X86, false, "de.fabmax.physxjni.windows.x86.NativeLib");

        // ========== macOS ==========
        addPlatform(OsType.MACOS, CpuArch.X86_64, false, "de.fabmax.physxjni.macos.x86_64.NativeLib");
        addPlatform(OsType.MACOS, CpuArch.AARCH64, false, "de.fabmax.physxjni.macos.aarch64.NativeLib");

        // ========== Android ==========
        addPlatform(OsType.ANDROID, CpuArch.ARM32, false, "de.fabmax.physxjni.android.arm32.NativeLib");
        addPlatform(OsType.ANDROID, CpuArch.AARCH64, false, "de.fabmax.physxjni.android.aarch64.NativeLib");
        addPlatform(OsType.ANDROID, CpuArch.X86, false, "de.fabmax.physxjni.android.x86.NativeLib");
        addPlatform(OsType.ANDROID, CpuArch.X86_64, false, "de.fabmax.physxjni.android.x86_64.NativeLib");

        // ========== BSD家族占位，可后续补充原生库 ==========
        addPlatform(OsType.FREEBSD, CpuArch.X86_64, false, "de.fabmax.physxjni.freebsd.x86_64.NativeLib");
        addPlatform(OsType.OPENBSD, CpuArch.X86_64, false, "de.fabmax.physxjni.openbsd.x86_64.NativeLib");
        addPlatform(OsType.NETBSD, CpuArch.X86_64, false, "de.fabmax.physxjni.netbsd.x86_64.NativeLib");

        // ========== 其他商业系统占位 ==========
        addPlatform(OsType.SOLARIS, CpuArch.X86_64, false, "de.fabmax.physxjni.solaris.x86_64.NativeLib");
        addPlatform(OsType.AIX, CpuArch.PPC64, false, "de.fabmax.physxjni.aix.ppc64.NativeLib");
        addPlatform(OsType.QNX, CpuArch.AARCH64, false, "de.fabmax.physxjni.qnx.aarch64.NativeLib");
    }

    private static void addPlatform(OsType os, CpuArch arch, boolean fallback, String metaClass) {
        SUPPORTED_PLATFORMS.add(new Platform(os, arch, fallback, metaClass));
    }

    public OsType getOsType() {
        return osType;
    }
    public CpuArch getCpuArch() {
        return cpuArch;
    }
    public String getPlatformId() {
        return platformId;
    }
    public boolean isFallback() {
        return isFallback;
    }
    public String getMetaClassName() {
        return metaClassName;
    }
    public String getResourceDir() {
        return resourceDir;
    }

    public NativeLib getLib() throws ReflectiveOperationException {
        try {
            Class<?> libImpl = Loader.class.getClassLoader().loadClass(metaClassName);
            return (NativeLib) libImpl.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            printNativeLibMissingWarning(this);
            throw e;
        }
    }

    private static void printNativeLibMissingWarning(Platform platform) {
        System.err.println("[PhysX‑JNI WARNING] Failed to load NativeLib implementation class.");
        System.err.printf("  Platform ID      : %s%n", platform.platformId);
        System.err.printf("  Resource dir     : %s%n", platform.resourceDir);
        System.err.printf("  Target class     : %s%n", platform.metaClassName);
        System.err.printf("  os.name          : %s%n", getRawOsName());
        System.err.printf("  os.arch          : %s%n", getRawArch());
        System.err.println("  Check whether the corresponding physx‑jni native‑jar is included in classpath.");
    }

    /**
     * Pre‑check whether native lib resource exists in classpath
     */
    public static boolean checkNativeLibResourceExists(Platform platform) {
        String libName;
        OsType os = platform.osType;
        if(os == OsType.WINDOWS) {
            libName = "PhysXJniBindings.dll";
        } else if(os == OsType.MACOS) {
            libName = "libPhysXJniBindings.dylib";
        } else {
            libName = "libPhysXJniBindings.so";
        }
        String fullResourcePath = platform.getResourceDir() + "/" + libName;
        return Platform.class.getResource(fullResourcePath) != null;
    }

    // ==================== 架构归一化 ====================
    private static CpuArch normalizeArch(String rawArch) {
        if (rawArch == null) return CpuArch.UNKNOWN;
        String arch = rawArch.toLowerCase().trim();
        if ("amd64".equals(arch) || "x86_64".equals(arch)) return CpuArch.X86_64;
        else if ("aarch64".equals(arch) || "arm64".equals(arch)) return CpuArch.AARCH64;
        else if ("x86".equals(arch) || "i386".equals(arch) || "i486".equals(arch)
                || "i586".equals(arch) || "i686".equals(arch)) return CpuArch.X86;
        else if ("arm".equals(arch) || "armv7".equals(arch) || "armv7l".equals(arch)) return CpuArch.ARM32;
        else if ("riscv64".equals(arch)) return CpuArch.RISCV64;
        else if ("ppc64".equals(arch) || "powerpc64".equals(arch)) return CpuArch.PPC64;
        else if ("ppc".equals(arch) || "powerpc".equals(arch)) return CpuArch.PPC;
        else if ("s390x".equals(arch)) return CpuArch.S390X;
        else if ("s390".equals(arch)) return CpuArch.S390;
        else if ("sparcv9".equals(arch) || "sparc64".equals(arch)) return CpuArch.SPARC64;
        else if ("sparc".equals(arch)) return CpuArch.SPARC;
        return CpuArch.UNKNOWN;
    }

    // ==================== 工具方法 ====================
    public static String getRawOsName() {
        return System.getProperty("os.name", "unknown");
    }
    public static String getRawArch() {
        return System.getProperty("os.arch", "unknown");
    }
    public static String getJavaVendor() {
        return System.getProperty("java.vendor", "unknown");
    }

    public static Platform findPlatformByMetaClassName(String metaClassName) {
        for (Platform p : SUPPORTED_PLATFORMS) {
            if (Objects.equals(p.getMetaClassName(), metaClassName)) {
                return p;
            }
        }
        if (Objects.equals(UNKNOWN.getMetaClassName(), metaClassName)) {
            return UNKNOWN;
        }
        return null;
    }

    private static Platform findMatch(OsType osType, CpuArch arch) {
        for (Platform p : SUPPORTED_PLATFORMS) {
            if (p.osType == osType && p.cpuArch == arch) {
                return p;
            }
        }
        return UNKNOWN;
    }

    public static Platform getPlatform() {
        String vendor = getJavaVendor().toLowerCase();
        String osRaw = getRawOsName().toLowerCase();
        String rawArch = getRawArch();
        CpuArch arch = normalizeArch(rawArch);

        boolean isAndroid = vendor.contains("android") || osRaw.contains("android");
        if (isAndroid) {
            return resolveAndroidPlatform(arch);
        }

        Platform platform;
        if (osRaw.contains("windows")) {
            platform = resolveWindowsPlatform(arch);
        } else if (osRaw.contains("linux")) {
            platform = resolveLinuxPlatform(arch);
        } else if (osRaw.contains("mac os x") || osRaw.contains("darwin") || osRaw.contains("osx")) {
            platform = resolveMacOsPlatform(arch);
        } else if (osRaw.contains("freebsd")) {
            platform = findMatch(OsType.FREEBSD, arch);
        } else if (osRaw.contains("openbsd")) {
            platform = findMatch(OsType.OPENBSD, arch);
        } else if (osRaw.contains("netbsd")) {
            platform = findMatch(OsType.NETBSD, arch);
        } else if (osRaw.contains("sunos") || osRaw.contains("solaris")) {
            platform = findMatch(OsType.SOLARIS, arch);
        } else if (osRaw.contains("aix")) {
            platform = findMatch(OsType.AIX, arch);
        } else if (osRaw.contains("qnx")) {
            platform = findMatch(OsType.QNX, arch);
        } else {
            platform = UNKNOWN;
        }

        if(platform == UNKNOWN) {
            printFallbackWarning(arch);
        }
        return platform;
    }

    private static void printFallbackWarning(CpuArch normalizedArch) {
        String os = getRawOsName();
        String archRaw = getRawArch();
        String vendor = getJavaVendor();
        System.err.println("[PhysX‑JNI WARNING] Platform recognized but no native implementation available.");
        System.err.printf("  os.name    : %s%n", os);
        System.err.printf("  os.arch    : %s%n", archRaw);
        System.err.printf("  java.vendor: %s%n", vendor);
        System.err.println("  Will attempt fallback: load UNKNOWN platform.");
        if (!CpuArch.X86_64.equals(normalizedArch)) {
            System.err.println("[PhysX‑JNI CRITICAL WARNING] Current CPU architecture is NOT x86_64!");
            System.err.println("  Binary instruction set mismatch, fallback native library WILL DEFINITELY fail.");
        }
    }

    private static Platform resolveAndroidPlatform(CpuArch arch) {
        return findMatch(OsType.ANDROID, arch);
    }
    private static Platform resolveWindowsPlatform(CpuArch arch) {
        return findMatch(OsType.WINDOWS, arch);
    }
    private static Platform resolveLinuxPlatform(CpuArch arch) {
        return findMatch(OsType.LINUX, arch);
    }
    private static Platform resolveMacOsPlatform(CpuArch arch) {
        return findMatch(OsType.MACOS, arch);
    }
}

