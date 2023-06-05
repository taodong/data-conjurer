package tao.dong.dataconjurer.shell.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.Callable;

@Component
@Command(name = "conjure", mixinStandardHelpOptions = true, version = "checksum 4.0",
                     description = "Command to generate data")
@Slf4j
public class ConjureCommand  implements Callable<Integer> {

    @Parameters(index = "0", description = "Data schema file")
    private File schema;
    @Parameters(index = "1", description = "Data generation plan")
    private File plan;

    @Override
    public Integer call() throws Exception {
        var schema = Files.readString(this.schema.toPath(), StandardCharsets.UTF_8);
        System.out.println(schema);

        var dataPlan = Files.readString(this.plan.toPath(), StandardCharsets.UTF_8);
        System.out.println(dataPlan);
        return 0;
    }
}
