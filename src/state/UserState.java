package state;

import java.io.ObjectOutputStream;

public interface UserState {
    boolean wake(ObjectOutputStream outputStream) throws Exception;
    boolean register(ObjectOutputStream outputStream) throws Exception;
    boolean login(ObjectOutputStream outputStream) throws Exception;
    String echo(ObjectOutputStream outputStream, String message) throws Exception;
    boolean broadcast(ObjectOutputStream outputStream) throws Exception;
    boolean logout(ObjectOutputStream outputStream) throws Exception;
    boolean sleep(ObjectOutputStream outputStream) throws Exception;
}
