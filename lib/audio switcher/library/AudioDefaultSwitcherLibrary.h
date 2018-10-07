#pragma once

#include "DeviceRole.h"
#include "DeviceType.h"
#include "ComDefinitions.h"
DEFINE_PROPERTYKEY(PKEY_DeviceInterface_FriendlyName, 0x026e516e, 0xb814, 0x414b, 0x83, 0xcd, 0x85, 0x6d, 0x6f, 0xef, 0x48, 0x22, 2);
DEFINE_PROPERTYKEY(PKEY_Device_DeviceDesc, 0xa45c254e, 0xdf1c, 0x4efd, 0x80, 0x20, 0x67, 0xd1, 0x46, 0xa8, 0x50, 0xe0, 2);
DEFINE_PROPERTYKEY(PKEY_Device_FriendlyName, 0xa45c254e, 0xdf1c, 0x4efd, 0x80, 0x20, 0x67, 0xd1, 0x46, 0xa8, 0x50, 0xe0, 14);

// This class is exported from the AudioEndPointLibrary.dll
namespace audio_default {    class AUDIODEFAULTSWITCHER_API CSwitcher {
    public:    
		static CSwitcher& get_instance();
		bool switch_to(__in PCWSTR deviceId, __in const DeviceRole role) const;
		bool is_default(__in PCWSTR deviceId, __in const DeviceType type, const DeviceRole role) const;
    

    private:
        CSwitcher(void);
		~CSwitcher();
        CSwitcher(CSwitcher const&) = delete;
		void operator=(CSwitcher const&) = delete;
    };

	extern "C" __declspec(dllexport) CSwitcher& get_instance();

	extern "C" __declspec(dllexport) bool switch_to(__in PCWSTR deviceId, __in const DeviceRole role);

	extern "C" __declspec(dllexport) bool is_default(CSwitcher* me, const PCWSTR deviceId, const DeviceType type, const DeviceRole role);

	extern "C" __declspec(dllexport) bool get_device_count(__out UINT* count, const DeviceType type, DWORD state);

	extern "C" __declspec(dllexport) bool get_device_collection(__out LPCWSTR* data, __in const DeviceType type, __in const  DWORD state);
}
