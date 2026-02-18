package tao.dong.dataconjurer.shell.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tao.dong.dataconjurer.engine.database.model.EntityQueryOutput;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {FileOutputService.class})
@ExtendWith(MockitoExtension.class)
class FileOutputServiceTest {
    @Mock
    private EntityQueryOutput eqo1;

    @Mock
    private EntityQueryOutput eqo2;

    @Autowired
    @SuppressWarnings("unused")
    private FileOutputService fileOutputService;

    @BeforeEach
    public void setUp() {
        // Set default behavior for mocks
        when(eqo1.getOrder()).thenReturn(1);
        when(eqo1.getEntity()).thenReturn("Product");
        when(eqo2.getOrder()).thenReturn(2);
        when(eqo2.getEntity()).thenReturn("Customer");
        lenient().when(eqo1.getQueries()).thenReturn(new StringBuilder("eqo1"));
        lenient().when(eqo2.getQueries()).thenReturn(new StringBuilder("eqo2"));
    }

    @Test
    void testGenerateSQLFiles() {
        var results = List.of(eqo1, eqo2);

        Path mockPath = mock(Path.class);
        try (MockedStatic<Files> files = mockStatic(Files.class)) {
            files.when(() -> Files.writeString(any(Path.class), anyString(), any(Charset.class), any(OpenOption[].class))).thenReturn(mockPath);
            fileOutputService.generateSQLFiles(results);
            verify(eqo1, times(1)).getQueries();
            verify(eqo2, times(1)).getQueries();
        }
    }

    @Test
    void testGetOutputFilePath() {
        assertEquals(Path.of("1_Product.sql"), fileOutputService.getOutputFilePath(eqo1));
        assertEquals(Path.of("2_Customer.sql"), fileOutputService.getOutputFilePath(eqo2));
    }
}
