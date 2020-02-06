package Presentation;

import java.io.IOException;
import java.util.List;

import Domain.SequenceDiagramArrow;

public interface IUMLSequenceDiagramGenerator {

	public void generate(List<SequenceDiagramArrow> arrows) throws IOException;

}
