package pl.v3bc.platform.utils.id;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * @Author: v3bc_
 * @Date: 8/22/26
 * @Project: astra-platform
 */

public record PlayerId(UUID id) implements Serializable {
    private static final long serialVersionUID = 1L;

    public PlayerId {
        Objects.requireNonNull(id, "PlayerId value cannot be null");
    }
}
