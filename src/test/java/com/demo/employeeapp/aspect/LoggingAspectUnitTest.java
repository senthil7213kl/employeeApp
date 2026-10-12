package com.demo.employeeapp.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoggingAspectUnitTest {

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private Signature signature;

    @InjectMocks
    private LoggingAspect loggingAspect;

    @BeforeEach
    void setUp() {
        when(signature.getName()).thenReturn("testMethod");
        when(signature.getDeclaringTypeName()).thenReturn("com.demo.employeeapp.controller.TestController");
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"arg1", "arg2"});
    }

    @Nested
    @DisplayName("Successful Method Execution")
    class SuccessfulMethodExecution {

        @Test
        @DisplayName("Should return result from proceeding join point")
        void shouldReturnResult() throws Throwable {
            Object expectedResult = "success";
            when(joinPoint.proceed()).thenReturn(expectedResult);

            Object result = loggingAspect.logMethodExecution(joinPoint);

            assertEquals(expectedResult, result);
            verify(joinPoint).proceed();
        }

        @Test
        @DisplayName("Should log method entry and exit")
        void shouldLogEntryAndExit() throws Throwable {
            when(joinPoint.proceed()).thenReturn("result");

            loggingAspect.logMethodExecution(joinPoint);

            verify(joinPoint, times(2)).getSignature();
            verify(joinPoint).getArgs();
            verify(joinPoint).proceed();
        }

        @Test
        @DisplayName("Should handle null arguments")
        void shouldHandleNullArguments() throws Throwable {
            when(joinPoint.getArgs()).thenReturn(null);
            when(joinPoint.proceed()).thenReturn("result");

            Object result = loggingAspect.logMethodExecution(joinPoint);

            assertEquals("result", result);
        }

        @Test
        @DisplayName("Should handle null result")
        void shouldHandleNullResult() throws Throwable {
            when(joinPoint.proceed()).thenReturn(null);

            Object result = loggingAspect.logMethodExecution(joinPoint);

            assertNull(result);
        }
    }

    @Nested
    @DisplayName("Exception Handling")
    class ExceptionHandling {

        @Test
        @DisplayName("Should rethrow exception from proceeding join point")
        void shouldRethrowException() throws Throwable {
            RuntimeException expectedException = new RuntimeException("Test error");
            when(joinPoint.proceed()).thenThrow(expectedException);

            RuntimeException thrown = assertThrows(RuntimeException.class, () -> loggingAspect.logMethodExecution(joinPoint));

            assertSame(expectedException, thrown);
        }

        @Test
        @DisplayName("Should log exception message")
        void shouldLogExceptionMessage() throws Throwable {
            RuntimeException expectedException = new RuntimeException("Something went wrong");
            when(joinPoint.proceed()).thenThrow(expectedException);

            assertThrows(RuntimeException.class, () -> loggingAspect.logMethodExecution(joinPoint));

            verify(joinPoint).proceed();
        }

        @Test
        @DisplayName("Should handle different exception types")
        void shouldHandleDifferentExceptionTypes() throws Throwable {
            IllegalArgumentException expectedException = new IllegalArgumentException("Invalid argument");
            when(joinPoint.proceed()).thenThrow(expectedException);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> loggingAspect.logMethodExecution(joinPoint));

            assertSame(expectedException, thrown);
        }
    }

    @Nested
    @DisplayName("Method Signature")
    class MethodSignature {

        @Test
        @DisplayName("Should capture method name from signature")
        void shouldCaptureMethodName() throws Throwable {
            when(signature.getName()).thenReturn("getAllEmployees");
            when(joinPoint.proceed()).thenReturn("result");

            loggingAspect.logMethodExecution(joinPoint);

            verify(signature).getName();
        }

        @Test
        @DisplayName("Should capture class name from signature")
        void shouldCaptureClassName() throws Throwable {
            when(signature.getDeclaringTypeName()).thenReturn("com.demo.employeeapp.service.EmployeeService");
            when(joinPoint.proceed()).thenReturn("result");

            loggingAspect.logMethodExecution(joinPoint);

            verify(signature).getDeclaringTypeName();
        }
    }
}
