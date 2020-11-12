package android.content.pm;

interface IPackageDeleteObserver {
    void packageDeleted(String packageName, int returnCode);
}
