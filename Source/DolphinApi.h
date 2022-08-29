// MathLibrary.h - Contains declarations of math functions
#pragma once
#include "./Common/CommonTypes.h"
#include "./DolphinProcess/DolphinAccessor.h"

#ifdef DOLPHIN_ENGINE_EXPORTS
#define DOLPHIN_ENGINE_API __declspec(dllexport)
#else
#define DOLPHIN_ENGINE_API __declspec(dllimport)
#endif


extern "C" DOLPHIN_ENGINE_API void init();

extern "C" DOLPHIN_ENGINE_API void hook();

extern "C" DOLPHIN_ENGINE_API void unhook();

extern "C" DOLPHIN_ENGINE_API int getPID();

extern "C" DOLPHIN_ENGINE_API DolphinComm::DolphinStatus getStatus();

extern "C" DOLPHIN_ENGINE_API bool readFromRAM(const u32 offset, char* buffer, const size_t size, const bool withBSwap);

extern "C" DOLPHIN_ENGINE_API bool writeToRAM(const u32 offset, const char* buffer, const size_t size, const bool withBSwap);
