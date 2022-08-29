#include "DolphinApi.h"
#include "DolphinProcess/DolphinAccessor.h"
#include "Common/CommonUtils.h"

static DolphinComm::DolphinAccessor* accessor = nullptr;

void init()
{
    if (accessor == nullptr) {
        accessor = new DolphinComm::DolphinAccessor();
    }
}

void hook()
{
    init();
    if (accessor == nullptr) { return; }
    accessor->hook();
}

void unhook()
{
    if (accessor == nullptr) { return; }
    accessor->unHook();

}

DolphinComm::DolphinStatus getStatus() {
    return accessor->getStatus();
}

int getPID() {
    if (accessor == nullptr) { return -1; }
    return accessor->getPID();
}

bool readFromRAM(const u32 consoleAddress, char* buffer, const size_t size, const bool withBSwap)
{
    if (accessor == nullptr) { return false; }
    u32 offset = Common::dolphinAddrToOffset(consoleAddress, accessor->getMEM1ToMEM2Distance());
    return accessor->readFromRAM(offset, buffer, size, withBSwap);
}

bool writeToRAM(const u32 consoleAddress, const char* buffer, const size_t size, const bool withBSwap)
{
    if (accessor == nullptr) { return false; }
    u32 offset = Common::dolphinAddrToOffset(consoleAddress, accessor->getMEM1ToMEM2Distance());
    return accessor->writeToRAM(offset, buffer, size, withBSwap);

}
