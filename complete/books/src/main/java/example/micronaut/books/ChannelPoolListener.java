package example.micronaut.books;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import io.micronaut.configuration.rabbitmq.connect.ChannelPool;
import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;

import javax.inject.Singleton;
import java.io.IOException;

@Singleton
public class ChannelPoolListener implements BeanCreatedEventListener<ChannelPool> {

    @Override
    public ChannelPool onCreated(BeanCreatedEvent<ChannelPool> event) {
        ChannelPool pool = event.getBean();

        Channel channel = null;
        try {
            channel = pool.getChannel();
        } catch (IOException e) {
            // The channel couldn't be retrieved
        }

        if (channel != null) {
            try {
                channel.exchangeDeclare("micronaut", BuiltinExchangeType.DIRECT, true); // <1>

                channel.queueDeclare("analytics", true, false, false, null); // <2>
                channel.queueBind("analytics", "micronaut", "analytics"); // <3>
            } catch (IOException e) {
                // no-op
            } finally {
                pool.returnChannel(channel);
            }
        }

        return pool;
    }
}
