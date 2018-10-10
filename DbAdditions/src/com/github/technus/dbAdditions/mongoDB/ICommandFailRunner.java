package com.github.technus.dbAdditions.mongoDB;

import com.mongodb.event.CommandFailedEvent;

public interface ICommandFailRunner {
    void run(CommandFailedEvent commandFailedEvent);
}
