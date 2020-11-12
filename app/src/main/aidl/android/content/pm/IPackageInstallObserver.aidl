package android.content.pm;

interface IPackageInstallObserver  {

    void packageInstalled(String packageName, int returnCode);
}
