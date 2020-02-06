package Presentation;

import java.io.IOException;

import Domain.Project;

public interface IUMLClassDiagramGenerator {

	public void generate(Project p) throws IOException;

}
