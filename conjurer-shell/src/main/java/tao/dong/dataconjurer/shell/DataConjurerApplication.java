package tao.dong.dataconjurer.shell;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;
import tao.dong.dataconjurer.shell.command.ConjurerCommand;

@SpringBootApplication
public class DataConjurerApplication implements CommandLineRunner, ExitCodeGenerator {

	public static void main(String[] args) {
		System.exit(SpringApplication.exit(SpringApplication.run(DataConjurerApplication.class, args)));
	}

	private final IFactory commandLineFactory;
	private final ConjurerCommand conjurerCommand;
	private int exitCode;

	public DataConjurerApplication(IFactory commandLineFactory, ConjurerCommand conjurerCommand) {
		this.commandLineFactory = commandLineFactory;
		this.conjurerCommand = conjurerCommand;
	}

	@Override
	public void run(String... args) {
		var commandLine = new CommandLine(conjurerCommand, commandLineFactory);
		exitCode = commandLine.execute(args);
	}

	@Override
	public int getExitCode() {
		return exitCode;
	}
}
