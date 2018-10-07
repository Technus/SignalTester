package java.com.github.technus.signalTester.db.mongoDB;

import com.mongodb.event.CommandFailedEvent;

public interface ICommandFailRunner {
    void run(CommandFailedEvent commandFailedEvent);
}
