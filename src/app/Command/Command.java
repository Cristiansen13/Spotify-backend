package app.Command;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface Command {
    /**
     * Execute object node.
     *
     * @return the object node
     */
    ObjectNode execute();
}
