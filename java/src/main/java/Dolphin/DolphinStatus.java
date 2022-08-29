package Dolphin;

import lombok.Getter;

@Getter
public enum DolphinStatus {
    HOOKED(0),
    NOT_RUNNING(1),
    NO_EMU(2),
    UN_HOOKED(3);
    private final int rawVal;

    DolphinStatus(Integer rawVal) {
        this.rawVal = rawVal;
    }

    static DolphinStatus fromVal(Integer val) {
        return switch (val) {
            case 0 -> HOOKED;
            case 1 -> NOT_RUNNING;
            case 2 -> NO_EMU;
            case 3 -> UN_HOOKED;
            default -> throw new IllegalArgumentException("There is no Emu of this value");
        };

    }
}
