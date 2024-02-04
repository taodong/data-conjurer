package tao.dong.dataconjurer.shell;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import picocli.CommandLine;
import tao.dong.dataconjurer.shell.command.ConjureCommand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataConjurerApplicationTest {

    @Mock
    @SuppressWarnings("unused")
    private CommandLine.IFactory commandLineFactory;
    @Mock
    @SuppressWarnings("unused")
    private ConjureCommand conjureCommand;
    @InjectMocks
    private DataConjurerApplication dataConjurerApplication;

    @Test
    void testRunWithArgs() {
        String[] args = new String[]{"arg1", "arg2"};
        try(@SuppressWarnings("unused") MockedConstruction<CommandLine> mockedCommandLine =
                    mockConstruction(CommandLine.class, (mock, context) -> when(mock.execute("arg1", "arg2")).thenReturn(0))) {
            dataConjurerApplication.run(args);
            assertEquals(0, dataConjurerApplication.getExitCode());
        }
    }
}
