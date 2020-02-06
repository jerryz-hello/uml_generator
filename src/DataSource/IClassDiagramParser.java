package DataSource;

import java.io.IOException;

import Domain.Project;

public interface IClassDiagramParser {

	public Project parse(String[] args) throws IOException;

}
