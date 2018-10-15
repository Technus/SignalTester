package com.github.technus.signalTester.utility;

public class NirCmd {
    public NirCmd() {}

    private static final String NIR_CMD_EXE ="nircmd/nircmd.exe ";
    private static final Runtime rt=Runtime.getRuntime();

    public void runCommand(String cmd) throws Exception{
        rt.exec(NIR_CMD_EXE +cmd);
    }

    public void muteSysytemMasterVolume() throws Exception{
        rt.exec(NIR_CMD_EXE +"setsysvolume 0");
    }

    public void maxSysytemMasterVolume() throws Exception{
        rt.exec(NIR_CMD_EXE +"setsysvolume 65535");
    }

    public void setSysytemMasterVolumeFloat(float volume) throws Exception{
        rt.exec(NIR_CMD_EXE +"setsysvolume "+(int)(65535f*volume));
    }

    public void setSysytemMasterVolumePercent(float volume) throws Exception{
        rt.exec(NIR_CMD_EXE +"setsysvolume "+(int)(655.35f*volume));
    }

    public void setSysytemMasterVolumeInt(int volume) throws Exception{
        rt.exec(NIR_CMD_EXE +"setsysvolume "+volume);
    }
}
