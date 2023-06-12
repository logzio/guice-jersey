package io.logz.guice.jersey.resources;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;
import java.io.IOException;

import static java.util.Objects.requireNonNull;

@Path(SseResource.PATH)
public class SseResource {

    public static final String PATH = "sse";

    private static volatile SseEventSink eventSink = null;

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void getMessageQueue(@Context SseEventSink sink) {
        eventSink = requireNonNull(sink);
    }

    @POST
    public void addMessage(final String message, @Context Sse sse) throws IOException {
        final SseEventSink localSink = eventSink;
        if (localSink != null) {
            localSink.send(sse.newEventBuilder().name("custom-message").data(String.class, message).build());
        }
    }

    @DELETE
    public void close() throws IOException {
        final SseEventSink localSink = eventSink;
        if (localSink != null) {
            eventSink.close();
        }
        eventSink = null;
    }

}
