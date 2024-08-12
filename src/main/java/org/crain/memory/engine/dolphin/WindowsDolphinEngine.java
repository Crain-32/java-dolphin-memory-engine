package org.crain.memory.engine.dolphin;

import org.crain.memory.engine.dolphin.platform.windows.WindowsKernel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.crain.memory.engine.dolphin.platform.windows.WindowsKernel.*;
import static org.crain.memory.engine.dolphin.platform.windows.WindowsProcessMemory.*;

class WindowsDolphinEngine extends AbstractDolphinMemoryEngine {

    static final Logger LOGGER = LoggerFactory.getLogger(WindowsDolphinEngine.class);

    WindowsDolphinEngine() {
        super();
    }

    private int m_hDolphin = -1;
    private static final List<String> DOLPHIN_EXE_NAMES = List.of("Dolphin.exe", "DolphinQt2.exe", "DolphinWx.exe");

    @Override
    public boolean connect() {
        if (m_PID <= 0 && getPId() <= 0) {
            throw new IllegalStateException("Failed to find a PID, is Dolphin Running?");
        }
        m_hDolphin = WindowsKernel.OpenProcess(m_PID,
                PROCESS_QUERY_INFORMATION
                | PROCESS_VM_OPERATION
                | PROCESS_VM_READ
                | PROCESS_VM_WRITE
        );
        if (m_hDolphin > 0) return obtainEmuRAMInformation();
        return false;
    }

    private Long getPId() {
        int handle = WindowsKernel.CreateToolhelp32Snapshot();
        if (handle <= 0) {
            throw new IllegalStateException("Failed to get a Windows Handle");
        }
        WindowsKernel.PROCESSENTRY32 entry = WindowsKernel.Process32First(handle);
        this.m_PID = -1;
        while (entry != null) {
            var exeName = new String(entry.szExeFile);
            if (DOLPHIN_EXE_NAMES.stream().anyMatch(name -> name.equalsIgnoreCase(exeName))) {
                m_PID = entry.th32ProcessID;
                break;
            }
            entry = WindowsKernel.Process32Next(handle);
        }
        WindowsKernel.CloseHandle(handle);
        return (long) m_PID;
    }

    @Override
    public boolean getStatus() {
        return false;
    }

    @Override
    public byte[] readFromRAM(final long consoleAddress, final int size) {
        return ReadProcessMemory(m_hDolphin, getRAMAddress(consoleAddress), size);
    }

    @Override
    public boolean writeToRAM(final long consoleAddress, byte[] val) {
        return WriteProcessMemory(m_hDolphin, getRAMAddress(consoleAddress), val);
    }

    long getRAMAddress(final long consoleAddress) {
        final long strippedAddress = consoleAddress & Constants.MEM1_STRIP_START;

        long RAMAddress = m_emuRAMAddressStart + strippedAddress;
        LOGGER.atError().setMessage("WHAT? 0x{}")
                .addArgument(() -> Long.toHexString(RAMAddress))
                .log();
//        if (m_ARAMAccessible) {
//            if (strippedAddress >= Constants.ARAM_FAKESIZE) {
//                RAMAddress = m_emuRAMAddressStart + strippedAddress - Constants.ARAM_FAKESIZE;
//            } else {
//                RAMAddress = m_emuARAMAddressStart + strippedAddress;
//            }
//        } else if (strippedAddress >= (Constants.MEM2_START - Constants.MEM1_START)) {
//            RAMAddress = m_MEM2AddressStart + strippedAddress - (Constants.MEM2_START - Constants.MEM1_START);
//        }
        return RAMAddress;
    }

    private static final int MEM_MAPPED = 0x40000;

    private boolean obtainEmuRAMInformation() {
        boolean MEM1Found = false;
        long virtualQueryBaseAddress = 0;
        var basicInfo = VirtualQueryEx(m_hDolphin, virtualQueryBaseAddress);
        while (basicInfo != null) {
            if (!m_MEM2Present && basicInfo.RegionSize() == Constants.MEM2_SIZE) {
                long regionBaseAddress = basicInfo.BaseAddress();
                if (MEM1Found && regionBaseAddress > m_emuRAMAddressStart + 0x10000000) {
                    break;
                }
                PSAPI_WORKING_SET_EX_INFORMATION wsInfo = QueryWorkingSetEx(m_hDolphin, basicInfo.BaseAddress());
                if (wsInfo != null && wsInfo.Valid()) {
                    m_MEM2AddressStart = regionBaseAddress;
                    m_MEM2Present = true;
                }
            } else if (basicInfo.RegionSize() == Constants.MEM1_SIZE && basicInfo.Type() == MEM_MAPPED) {
                PSAPI_WORKING_SET_EX_INFORMATION wsInfo = QueryWorkingSetEx(m_hDolphin, basicInfo.BaseAddress());
                if (wsInfo != null && wsInfo.Valid()) {
                    if (!MEM1Found) {
                        System.out.printf("Basic Info %s, Region Size %s%n",wsInfo.getVirtualAddress(),basicInfo.RegionSize());
                        m_emuRAMAddressStart = basicInfo.BaseAddress();
                        MEM1Found = true;
                    } else {
                        long aramCandidate = basicInfo.BaseAddress();
                        if (aramCandidate == m_emuARAMAddressStart + Constants.MEM1_SIZE) {
                            m_emuRAMAddressStart = aramCandidate;
                            m_ARAMAccessible = true;
                        }
                    }
                }
            }
            virtualQueryBaseAddress += basicInfo.RegionSize();
            basicInfo = VirtualQueryEx(m_hDolphin, virtualQueryBaseAddress);
        }
        if (m_MEM2Present) {
            m_emuARAMAddressStart = 0;
            m_ARAMAccessible = false;
        }
        LOGGER.atDebug()
                .setMessage("""
                        AbstractDolphinMemoryEngineValues
                                        m_PID : 0x{}
                         m_emuRAMAddressStart : 0x{}
                        m_emuARAMAddressStart : 0x{}
                           m_MEM2AddressStart : 0x{}
                             m_ARAMAccessible : {}
                                m_MEM2Present : {}
                        """)
                .addArgument(m_PID)
                .addArgument(() -> Long.toHexString(m_emuRAMAddressStart))
                .addArgument(() -> Long.toHexString(m_emuARAMAddressStart))
                .addArgument(() -> Long.toHexString(m_MEM2AddressStart))
                .addArgument(m_ARAMAccessible)
                .addArgument(m_MEM2Present)
                .log();
        return m_emuRAMAddressStart != 0;
    }
}
