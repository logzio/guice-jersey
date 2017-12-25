package io.logz.guice.jersey.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.function.Consumer;


@ClientEndpoint
@ServerEndpoint(value = "/ws/test")
public class TestSocket {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestSocket.class);
    private final SocketCallback callback;

    @Inject
    TestSocket(SocketCallback callback){
        this.callback = callback;
    }

    @OnOpen
    public void onWebSocketConnect(Session session)
    {
        LOGGER.info("Socket Connected: " + session);
    }

    @OnMessage
    public void onWebSocketText(String message)
    {
        LOGGER.info("Received TEXT message: " + message);
        callback.accept(message);
    }

    @OnClose
    public void onWebSocketClose(CloseReason reason)
    {
        LOGGER.info("Socket Closed: " + reason);
    }

    @OnError
    public void onWebSocketError(Throwable cause)
    {
        LOGGER.info("catching: ", cause);
    }

    public interface SocketCallback extends Consumer<String> {

    }

}
