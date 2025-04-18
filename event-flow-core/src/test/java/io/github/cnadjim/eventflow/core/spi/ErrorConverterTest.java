package io.github.cnadjim.eventflow.core.spi;

import io.github.cnadjim.eventflow.core.domain.error.Error;
import io.github.cnadjim.eventflow.core.domain.error.InternalServerError;
import io.github.cnadjim.eventflow.core.domain.exception.EventFlowException;
import io.github.cnadjim.eventflow.core.stub.DefaultErrorConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ErrorConverterTest {

    @Spy
    private DefaultErrorConverter errorConverter;

    @Test
    void convert_should_use_tryConvert_when_successful() {
        // Arrange
        Throwable throwable = new RuntimeException("Test exception");
        Error expectedError = new InternalServerError(Instant.now(), "Converted error", null);
        doReturn(Optional.of(expectedError)).when(errorConverter).tryConvert(throwable);

        // Act
        Error result = errorConverter.convert(throwable);

        // Assert
        assertSame(expectedError, result);
        verify(errorConverter).tryConvert(throwable);
        verify(errorConverter, never()).defaultConvert(any());
    }

    @Test
    void convert_should_use_defaultConvert_when_tryConvert_returns_empty() {
        // Arrange
        Throwable throwable = new RuntimeException("Test exception");
        Error expectedError = new InternalServerError(Instant.now(), "Default error", null);
        doReturn(Optional.empty()).when(errorConverter).tryConvert(throwable);
        doReturn(expectedError).when(errorConverter).defaultConvert(throwable);

        // Act
        Error result = errorConverter.convert(throwable);

        // Assert
        assertSame(expectedError, result);
        verify(errorConverter).tryConvert(throwable);
        verify(errorConverter).defaultConvert(throwable);
    }

    @Test
    void convert_should_use_defaultConvert_when_tryConvert_throws_exception() {
        // Arrange
        Throwable throwable = new RuntimeException("Test exception");
        Error expectedError = new InternalServerError(Instant.now(), "Default error", null);
        doThrow(new RuntimeException("Conversion error")).when(errorConverter).tryConvert(throwable);
        doReturn(expectedError).when(errorConverter).defaultConvert(throwable);

        // Act
        Error result = errorConverter.convert(throwable);

        // Assert
        assertSame(expectedError, result);
        verify(errorConverter).tryConvert(throwable);
        verify(errorConverter).defaultConvert(throwable);
    }

    @Test
    void convert_should_handle_null_throwable() {
        // Arrange
        // No need to stub tryConvert as it won't be called for null

        // Act
        Error result = errorConverter.convert(null);

        // Assert
        assertNull(result);
        // tryConvert is not called for null due to Optional.ofNullable().flatMap() behavior
        verify(errorConverter, never()).tryConvert(null);
        verify(errorConverter).defaultConvert(null);
    }

    @Test
    void defaultConvert_should_call_fromThrowable() {
        // Arrange
        Throwable throwable = new RuntimeException("Test exception");

        // Act
        Error result = errorConverter.defaultConvert(throwable);

        // Assert
        assertNotNull(result);
        assertEquals(500, result.status());
        assertEquals("Internal Server Error", result.reasonPhrase());
        assertTrue(result.message().contains("Test exception"));
    }

    @Test
    void fromThrowable_should_return_null_for_null_throwable() {
        // Act
        Error result = ErrorConverter.fromThrowable(null);

        // Assert
        assertNull(result);
    }

    @Test
    void fromThrowable_should_return_error_from_eventflow_exception() {
        // Arrange
        Error originalError = new InternalServerError(Instant.now(), "Original error", null);
        EventFlowException exception = new EventFlowException(originalError);

        // Act
        Error result = ErrorConverter.fromThrowable(exception);

        // Assert
        assertSame(originalError, result);
    }

    @Test
    void fromThrowable_should_create_internal_server_error_for_regular_exception() {
        // Arrange
        RuntimeException exception = new RuntimeException("Test exception");

        // Act
        Error result = ErrorConverter.fromThrowable(exception);

        // Assert
        assertNotNull(result);
        assertInstanceOf(InternalServerError.class, result);
        assertEquals(500, result.status());
        assertEquals("Internal Server Error", result.reasonPhrase());
        assertTrue(result.message().contains("Test exception"));
        assertNotNull(result.details());
        assertInstanceOf(List.class, result.details());
    }

    @Test
    void fromThrowable_should_handle_nested_exceptions() {
        // Arrange
        IllegalArgumentException rootCause = new IllegalArgumentException("Root cause");
        RuntimeException wrapper = new RuntimeException("Wrapper", rootCause);

        // Act
        Error result = ErrorConverter.fromThrowable(wrapper);

        // Assert
        assertNotNull(result);
        assertInstanceOf(InternalServerError.class, result);
        assertTrue(result.message().contains("Root cause"));
    }

    @Test
    void fromThrowable_should_handle_eventflow_exception_without_error() {
        // Arrange
        RuntimeException cause = new RuntimeException("Cause");
        EventFlowException exception = new EventFlowException(cause);

        // Act
        Error result = ErrorConverter.fromThrowable(exception);

        // Assert
        assertNotNull(result);
        assertInstanceOf(InternalServerError.class, result);
        assertTrue(result.message().contains("Cause"));
    }
}
