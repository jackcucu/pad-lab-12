package md.jack.client;

import md.jack.marshalling.JsonMarshaller;
import md.jack.model.MessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static java.lang.Runtime.getRuntime;
import static md.jack.model.ClientType.PUBLISHER;

class Publisher
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Publisher.class);
    private Socket socket;

    Publisher(final Socket socket)
    {
        this.socket = socket;
    }

    public void run()
    {
        LOGGER.info("Hi Publisher");
        try
        {
            if (socket.isConnected())
            {
                LOGGER.info("Client connected to {} on port {}",
                        socket.getInetAddress(),
                        socket.getPort());

                final PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

                final MessageDto message = new MessageDto();
                message.setClientType(PUBLISHER);
                message.setTopic("md.jack.topic");
                message.setClosing(true);
                getRuntime().addShutdownHook(new ProcessorHook(socket, message));

                final String marshall = new JsonMarshaller().marshall(message);
                writer.println(marshall);
                writer.flush();

                while (socket.isConnected())
                {
                    System.out.println("Type your message to send to server..type 'EXIT' to exit");

                    final MessageDto payload = new MessageDto();
                    payload.setName("Eugene");
                    payload.setClientType(PUBLISHER);
                    payload.setTopic("md.jack.topic");
                    payload.setPayload(reader.readLine());

                    final String send = new JsonMarshaller().marshall(payload);
                    writer.println(send);
                    writer.flush();

                    if (payload.getPayload().equals("EXIT"))
                    {
                        break;
                    }
                }
                socket.close();
            }
        }
        catch (Exception exception)
        {
            LOGGER.error("Error {} with cause {}", exception.getMessage(), exception.getCause());
        }
    }
}
