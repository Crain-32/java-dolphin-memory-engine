package org.crain.dme4j.core;

import org.crain.dme4j.core.context.ConnectionDetails;
import org.crain.dme4j.core.exception.DmeCoreModuleException;
import org.crain.dme4j.core.exception.EngineConnectionException;
import org.crain.dme4j.core.exception.EngineStateException;
import org.crain.dme4j.core.types.MemoryType;
import org.crain.dme4j.core.types.PointerType;

import java.io.Closeable;

/**
 * This interface provides functionality to read and or write from a Gamecube Memory space and the JVM.
 * Implementations should be annotated with {@link Support}, and follow the requirements listed there.
 * It is not required for Implementations to be registered with the {@link java.util.ServiceLoader ServiceLoader},
 * but still recommended.
 * <br>
 * It is required that any read/write operations are cancelled if {@link Closeable#close()} is called in order to
 * follow that contract, and attempted read/write operations will no longer be supported.
 * <br>
 * If a non-zero Parameter constructor can be used,then {@link GamecubeMemoryEngine#connect(ConnectionDetails)}
 * should mirror the ability of that constructor. It is not required that Constructors delegate to the function.
 */
public interface GamecubeMemoryEngine extends Closeable {

    /**
     * This function servers two purposes,
     * <ul>
     *     <li>Provide a "Constructor" when created with the {@link java.util.ServiceLoader ServiceLoader} abstraction.</li>
     *     <li>Provide a clear point where we can know the Engine is connected to Gamecube Memory Space</li>
     * </ul>
     * Repeated calls to this function are not required to create new connections, but implementations may decide
     * to so. As a non-zero parameter Constructor in an implementation <em>can</em> mirror the functionality, calling
     * this function after constructing the instance may do nothing.
     * If {@code connectionDetails == null} then either a reasonable default should be connected to, or an exception
     * thrown explaining the problem.
     * <br>
     * It is not required to reconnect if {@link Closeable#close()} has been called.
     * @param connectionDetails Nullable object that may define an implementation-specific location to connect to.
     * @throws EngineConnectionException if {@link Closeable#close() close()} has been called and this doesn't support
     * reconnecting. If a connection attempted failed, or if {@code connectionDetails} was provided and not usable.
     */
    void connect(ConnectionDetails connectionDetails) throws EngineConnectionException;

    /**
     * <em>Not finalized</em>
     * <br>
     * Simple check for the state of the Connection. Implementations can never throw an exception.
     * The following is the expectations for the Connection lifecycle, which by extension covers this function.
     * <pre>
     * {@code
     * var engine = new MyEngine();
     * engine.getStatus(); // false
     * engine.connect(null);
     * engine.getStatus(); // true
     * engine.close();
     * engine.getStatus(); // false
     * }
     * This function should also return false if calls to {@link GamecubeMemoryEngine#readFromRAM(long, int)}
     * or {@link GamecubeMemoryEngine#writeToRAM(long, byte[])} will throw an Exception.
     * </pre>
     * @return The status of the underlying connection this {@link GamecubeMemoryEngine} uses.
     */
    @SuppressWarnings("unused")
    boolean getStatus();

    /**
     * Returns an array of {@code size} bytes from the memory address from the Console Memory at {@code consoleAddress}.
     * @param consoleAddress inclusive starting location to read from memory.
     * @param size positive number of bytes to read.
     * @return a non-null byte[size] array that maps the Gamecube Memory 1:1
     * @throws EngineStateException if the Engine is in an invalid state to read from.
     * @throws EngineConnectionException if the Engine is not connected to any memory space.
     * @throws DmeCoreModuleException if the Implementation decides that the other two don't work.
     * @throws IllegalArgumentException if the Console Address is negative.
     */
    byte[] readFromRAM(final long consoleAddress, final int size) throws DmeCoreModuleException;

    /**
     * Returns true if the Implementation can read the Gamecube memory, regardless of
     * what {@link GamecubeMemoryEngine#getStatus()} returns.
     */
    boolean canRead();

    /**
     * Returns true if the Implementation can write to Gamecube memory, regardless of
     * what {@link GamecubeMemoryEngine#getStatus()} returns.
     */
    boolean canWrite();

    /**
     * Writes {@code val.length} bytes to the memory address starting at the {@code consoleAddress}. Implementations
     * must correct for any potential Endian issues.
     * @param consoleAddress Zero / Positive Memory Address to start writing to.
     * @param val non-empty array of bytes to write.
     * @return true if no fatal issues occurred during the write process, otherwise false.
     * @throws EngineStateException if the Engine is in an invalid state to write to.
     * @throws EngineConnectionException if the Engine is not connected to any memory space.
     * @throws DmeCoreModuleException if the Implementation decides that the other two don't work.
     * @throws IllegalArgumentException if the Console Address is negative, or {@code val.length == 0}
     */
    boolean writeToRAM(final long consoleAddress, byte[] val) throws DmeCoreModuleException;

    /**
     * Mirrors the expectations of {@link GamecubeMemoryEngine#readFromRAM(long, int)}, but handles the
     * mapping of Gamecube Memory into the provided {@link MemoryType}.
     * Implementations must follow the outlined behavior of {@link MemoryType}, and may throw different
     * {@link DmeCoreModuleException} types if appropriate.
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean readIntoMemoryType(final long consoleAddress, MemoryType memoryType) throws DmeCoreModuleException;

    /**
     * Mirrors the expectations of {@link GamecubeMemoryEngine#readIntoMemoryType(long, MemoryType)}. Just provides
     * a nice handler for {@link PointerType} as they already provide a {@code consoleAddress} to handle.
     */
    @SuppressWarnings("unused")
    default void readIntoPointerType(PointerType pointerType) throws DmeCoreModuleException {
        // Wait does this need the underlying type the PointerType handles or something else.
        readIntoMemoryType(pointerType.getPointerAddress(), pointerType);
        pointerType.clearFollow();
    }

    /**
     * Mirrors the expectations of {@link GamecubeMemoryEngine#writeToRAM(long, byte[])}, but handles the
     * mapping of {@link MemoryType} into bytes.
     * Implementations must follow the outlined behavior of {@link MemoryType}, and may throw different
     * {@link DmeCoreModuleException} types if appropriate.
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean writeFromMemoryType(final long consoleAddress, MemoryType memoryType) throws DmeCoreModuleException;

    /**
     * Mirrors the expectations of {@link GamecubeMemoryEngine#writeFromMemoryType(long, MemoryType)}. Just provides
     * a nice handler for {@link PointerType} as they already provide a {@code consoleAddress} to handle.
     */
    @SuppressWarnings("unused")
    default void writeIntoPointerType(PointerType pointerType) throws DmeCoreModuleException {
        writeFromMemoryType(pointerType.getPointerAddress(), pointerType);
        pointerType.clearFollow();
    }
}
