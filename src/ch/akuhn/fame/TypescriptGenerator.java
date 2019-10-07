package ch.akuhn.fame;

import static ch.akuhn.util.Out.puts;

import ch.akuhn.fame.codegen.CodeGeneration;
import ch.akuhn.fame.parser.Importer;
import ch.akuhn.fame.parser.InputSource;

public class TypescriptGenerator {

	public static void main(String[] args){ 
		InputSource input = InputSource.fromResource("ch/unibe/fame/resources/FAMIX30.fm3.mse");
        MetaRepository fm3 = MetaRepository.createFM3();
        Importer builder = new Importer(fm3);
        builder.readFrom(input);
        Repository famix = builder.getResult();
        CodeGeneration gen = new CodeGeneration("", args[0], "");
        gen.accept(famix);
        puts("done");
    } 
}
