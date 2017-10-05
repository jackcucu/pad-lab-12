package md.jack.broker;

import md.jack.dto.MessageDto;
import md.jack.marshalling.JsonMarshaller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.BlockingQueue;

class AsyncWriter implements Runnable
{
    private BlockingQueue<MessageDto> channel;
    private List<Client> subscribers;

    AsyncWriter(BlockingQueue<MessageDto> channel, List<Client> subscribers)
    {
        this.channel = channel;
        this.subscribers = subscribers;
    }

    @Override
    public void run()
    {
        while (true)
        {
            while (!subscribers.isEmpty())
            {
                while (!channel.isEmpty())
                {
                    for (Client subscriber : subscribers)
                    {
                        try
                        {
                            final PrintWriter writer = new PrintWriter(subscriber.getSocket()
                                    .getOutputStream(), true);

                            final MessageDto message = channel.peek();
                            final String marshall = new JsonMarshaller().marshall(message);
                            System.out.println("asdasd");
                            writer.println(marshall);
                            writer.flush();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    channel.poll();
                }
            }
        }
    }
}