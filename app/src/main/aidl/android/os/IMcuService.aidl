package android.os;

/** {@hide} */
interface IMcuService
{
    int heartbeat();
    int setUptime(int time);
    int getUptime();
    int openWatchdog();
    int closeWatchdog();
    int setWatchdogDuration(int duration);
    int getWatchdogDuration();
    boolean watchdogIsOpen();
}