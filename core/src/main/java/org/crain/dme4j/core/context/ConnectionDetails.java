package org.crain.dme4j.core.context;

/**
 * Container for the required details to connect from the JVM to the Gamecube Memory Space.
 * You should extend this if you wish support a custom connection when using the
 * {@link org.crain.dme4j.core.GamecubeMemoryEngine GamecubeMemoryEngine}.
 * Otherwise, you can just use the {@link SimpleConnectionDetails}
 */
public interface ConnectionDetails {

    /**
     * A not blank String that defines the location to connect to. Implementations of
     * {@link org.crain.dme4j.core.GamecubeMemoryEngine GamecubeMemoryEngine} should define their full expectations
     * in terms of formating, but the following are some examples for inspiration.
     * <ul>
     *     <li>"file://my/snapshot/location/snapshot.txt"</li>
     *     <li>"dolphin://search"</li>
     *     <li>"dolphin://pid/1234"</li>
     *     <li>"console://127.0.0.5:7878</li>
     * </ul>
     */
    @SuppressWarnings("unused")
    String location();

}
