package de.fabmax.physxjni;

import physx.PlatformChecks;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Loader {
    private static final String version = "2.7.2";
    private static final AtomicBoolean isLoaded = new AtomicBoolean(false);
    static List<String> libraryPaths = null;

    /**
     * Forces the library loaded to load the native libraries at the given paths instead of the builtin ones.
     * Paths must be absolute. Must be called before any PhysX function is called.
     */
    public static void setLoadLibraryPaths(List<String> paths) {
        if (isLoaded.get()) {
            throw new IllegalStateException("Library path cannot be set after library is loaded");
        }
        libraryPaths = paths;
    }

    public static void load() {
        if (!isLoaded.getAndSet(true)) {
            Platform preferredPlatform = Platform.getPlatform();
            NativeLib lib = null;
            try {
                // Pre‑check native library resource existence
                boolean libResourceExists = Platform.checkNativeLibResourceExists(preferredPlatform);
                if (!libResourceExists) {
                    throw new UnsatisfiedLinkError("Native binary resource not found");
                }
                lib = preferredPlatform.getLib();
                lib.load();
            } catch (Throwable t) {
                System.err.println("[PhysX‑JNI ERROR] This device is not compatible with current physx‑jni, no libPhysXJniBindings found for platform "
                        + preferredPlatform.getPlatformId() + " (" + preferredPlatform.getResourceDir() + ")");
                System.err.println("[PhysX‑JNI INFO] Will try UNKNOWN fallback platform now.");
                try {
                    lib = Platform.UNKNOWN.getLib();
                    lib.load();
                    System.err.println("[PhysX‑JNI INFO] Succeeded to load via UNKNOWN fallback platform.");
                } catch (Throwable fallbackErr) {
                    throw new IllegalStateException("PhysX‑JNI fatal error: UNKNOWN fallback platform also failed, cannot continue.", fallbackErr);
                }
            }

            Platform actualPlatform = Platform.findPlatformByMetaClassName(lib.getClass().getName());
            if (actualPlatform != null) {
                OsType osType = actualPlatform.getOsType();
                switch (osType) {
                    case LINUX:
                        PlatformChecks.setPlatformBit(PlatformChecks.PLATFORM_LINUX);
                        break;
                    case WINDOWS:
                        PlatformChecks.setPlatformBit(PlatformChecks.PLATFORM_WINDOWS);
                        break;
                    case MACOS:
                        PlatformChecks.setPlatformBit(PlatformChecks.PLATFORM_MACOS);
                        break;
                    case ANDROID:
                        // No PLATFORM_ANDROID bit defined in PlatformChecks
                        break;
                    case UNKNOWN:
                        PlatformChecks.setPlatformBit(PlatformChecks.PLATFORM_LINUX);
                        break;
                    // NOTE: FreeBSD / Solaris / QNX etc have no corresponding PlatformChecks bit, skip setting
                    default:
                        break;
                }
            } else {
                System.err.println("[PhysX‑JNI WARNING] Loaded from custom user‑defined platform, cannot set platform check bits.");
            }

            if (!version.equals(lib.getVersion())) {
                throw new IllegalStateException("Native lib version " + lib.getVersion() +
                        " differs from main version " + version);
            }
        }
    }
}

