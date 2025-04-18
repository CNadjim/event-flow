package io.github.cnadjim.eventflow.core.domain.message;

import io.github.cnadjim.eventflow.core.domain.error.Error;
import io.github.cnadjim.eventflow.core.domain.topic.MessageResultTopic;
import io.github.cnadjim.eventflow.core.domain.topic.Topic;

import static java.util.Objects.isNull;

/**
 * {@code MessageResult} is an interface representing the result of a message processing operation.
 * It extends the {@link Message} interface and provides methods to access the result status and error information.
 * <p>
 * A message result can represent either a successful or failed operation. If the operation was successful,
 * the {@link #error()} method will return null. If the operation failed, the {@link #error()} method will
 * return an {@link Error} object containing details about the failure.
 * <p>
 * The generic parameter {@code MESSAGE} represents the type of the original message that this result is for.
 *
 * @param <MESSAGE> the type of the original message
 */
public interface MessageResult<MESSAGE extends Message> extends Message {

    /**
     * Returns the error associated with this result, if any.
     * <p>
     * If the operation was successful, this method will return null.
     * If the operation failed, this method will return an {@link Error} object
     * containing details about the failure.
     *
     * @return the error, or null if the operation was successful
     */
    Error error();

    /**
     * Returns the topic associated with this result.
     * <p>
     * The result topic is typically derived from the original message's topic
     * with a suffix indicating it's a result.
     *
     * @return the result topic
     */
    MessageResultTopic resultTopic();

    /**
     * Returns the topic associated with this message.
     * <p>
     * For message results, this is the same as {@link #resultTopic()}.
     *
     * @return the message topic
     */
    @Override
    default Topic topic(){
        return resultTopic();
    }

    /**
     * Checks if this result represents a successful operation.
     * <p>
     * A result is considered successful if the {@link #error()} method returns null.
     *
     * @return true if the operation was successful, false otherwise
     */
    default boolean isSuccess(){
        return isNull(error());
    }

    /**
     * Checks if this result represents a failed operation.
     * <p>
     * A result is considered failed if the {@link #error()} method returns a non-null value.
     *
     * @return true if the operation failed, false otherwise
     */
    default boolean isFailure(){
        return !isSuccess();
    }
}
