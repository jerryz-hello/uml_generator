package Domain;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import DataSource.ASMClassDiagramParser;
import DataSource.ASMSequenceDiagramParser;
import DataSource.Constants;
import DataSource.IClassDiagramParser;
import DataSource.ISequenceDiagramParser;
import Presentation.IUMLClassDiagramGenerator;
import Presentation.IUMLSequenceDiagramGenerator;
import Presentation.PlantUMLClassDiagramGenerator;
import Presentation.PlantUMLSequenceDiagramGenerator;

public class MainLogic {

	private String filter;
	private String filename;
	private String[] classNames;
	private String methodName;
	private int depth;
	private boolean parseExternal;
	private boolean isClassDiagram;

	public MainLogic(String[] args, String filename) {
		this.filename = filename;
		switch(args[0]) {
		case Constants.OPT_CLASSDIAGRAM:
			this.isClassDiagram = true;
			this.filter = args[args.length - 2];
			this.parseExternal = Boolean.parseBoolean(args[args.length - 1]);
			this.classNames = Arrays.copyOfRange(args, 1, args.length - 2);
			break;
		case Constants.OPT_SEQUENCEDIAGRAM:
			this.isClassDiagram = false;
			this.depth = Integer.parseInt(args[1]);
			this.methodName = args[2];
			break;
		}
		
	}

	public void run() throws IOException {
		if(isClassDiagram) {
			Set<String> arrowTypes = new TreeSet<>(Arrays.asList(Constants.INHERITANCE, Constants.IMPLEMENTATION,
					Constants.ASSOCIATION, Constants.DEPENDENCY));
			IClassDiagramParser parser = new ASMClassDiagramParser(filter, arrowTypes, parseExternal);
			Project p = parser.parse(classNames);
			IUMLClassDiagramGenerator umlGenerator = new PlantUMLClassDiagramGenerator(this.filename);
			umlGenerator.generate(p);
		}else {
			ISequenceDiagramParser parser = new ASMSequenceDiagramParser(depth);
			List<SequenceDiagramArrow> arrows = parser.parse(methodName);
			IUMLSequenceDiagramGenerator sequenceGenerator = new PlantUMLSequenceDiagramGenerator(this.filename);
			sequenceGenerator.generate(arrows);
		}
	}

}
