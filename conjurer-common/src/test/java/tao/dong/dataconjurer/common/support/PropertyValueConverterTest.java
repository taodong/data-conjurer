package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import tao.dong.dataconjurer.common.model.PropertyType;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static tao.dong.dataconjurer.common.model.PropertyType.SEQUENCE;
import static tao.dong.dataconjurer.common.model.PropertyType.TEXT;

class PropertyValueConverterTest {
    @SuppressWarnings("unchecked")
    @Test
    void testCovertEntityProperties_NonMatchSize() {
        PropertyValueConverter<String> converter = new PropertyValueConverter<String>(mock(BiFunction.class)) {
        };
        List<Object> values = List.of("abc", "efg");
        var types = List.of(TEXT);
        assertThrows(IllegalArgumentException.class, () -> converter.convertEntityProperties(values, types));
    }

    @Test
    void testConvertEntityProperties() {
        var converter = new PropertyValueConverter<>((o, type) -> type == TEXT ? (String) o : String.valueOf(o)){};
        List<Object> val = List.of("abc", 1L);
        List<PropertyType> types = List.of(TEXT, SEQUENCE);
        var result = converter.convertEntityProperties(val, types);
        assertEquals("abc", result.get(0));
        assertEquals("1", result.get(1));
    }

    @Test
    void testConvertRecords() {
        var converter = new PropertyValueConverter<>((o, type) -> type == TEXT ? (String) o : String.valueOf(o)){};
        List<List<Object>> rows = List.of(List.of("abc", 1L), List.of("eft", 2), List.of("hls", 3));
        List<PropertyType> types = List.of(TEXT, SEQUENCE);
        var result = converter.convertRecords(rows, types);
        assertEquals(3, result.size());
    }

    private static Stream<Arguments> testConvertRecords_Exception() {
        return Stream.of(
                Arguments.of(mock(ExecutionException.class)),
                Arguments.of(mock(InterruptedException.class))
        );
    }

    @ParameterizedTest
    @MethodSource
    @SuppressWarnings("unchecked")
    void testConvertRecords_Exception(Exception ex) throws ExecutionException, InterruptedException {
        var converter = new PropertyValueConverter<>((o, type) -> type == TEXT ? (String) o : String.valueOf(o)){};
        List<List<Object>> rows = List.of(List.of("abc"), List.of("eft"));
        List<PropertyType> types = List.of(TEXT);
        Future<List<List<String>>> future = mock(Future.class);
        try (MockedStatic<Executors> executors = mockStatic(Executors.class);
             var executorService = mock(ExecutorService.class)) {
            executors.when(Executors::newVirtualThreadPerTaskExecutor).thenReturn(executorService);
            when(executorService.submit(any(Callable.class))).thenReturn(future);
            when(future.get()).thenThrow(ex);
            assertThrows(DataGenerateException.class, () -> converter.convertRecords(rows, types));
        }
    }


}
