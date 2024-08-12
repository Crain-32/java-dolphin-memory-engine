# Java Dolphin Memory Engine

A Java Plugin designed to read and write the emulated memory of [the Dolphin emulator](https://github.com/dolphin-emu/dolphin) during runtime. 

Based on the Popular [py-dolphin-memory-engine](https://github.com/henriquegemignani/py-dolphin-memory-engine) by henriquegemignani

## Runtime Requirements

This library requires at minimum Java 22 to run. As it utilizes the new Foreign Function and Memory API [JEP](https://openjdk.org/jeps/454)

At this point in time we utilize the fact that any unnamed module can utilize the API without additional CLI args. This
is subject to change.

## License
This program is licensed under the MIT license which grants you the permission to do anything you wish to with the
software, as long as you preserve all copyright notices. (See the file LICENSE for the legal text.)


## Documentation

For brevity, you find the documentation at the following [Index](./docs/docs_index.md)