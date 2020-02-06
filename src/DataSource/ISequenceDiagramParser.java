package DataSource;

import java.io.IOException;
import java.util.List;

import Domain.SequenceDiagramArrow;

public interface ISequenceDiagramParser {

	public List<SequenceDiagramArrow> parse(String name) throws IOException;
	
}
