package DataSource;

import java.util.List;
import java.util.Queue;

import org.objectweb.asm.tree.ClassNode;

import Domain.ClassDiagramArrow;

public abstract class IASMRelationshipParser {

	protected Queue<String> classNames;
	protected List<ClassDiagramArrow> arrows;
	protected List<String> visited;
	protected boolean parseExternal;

	public IASMRelationshipParser(Queue<String> classNames, List<String> visited, List<ClassDiagramArrow> arrows,
			boolean parseExternal) {
		this.classNames = classNames;
		this.arrows = arrows;
		this.visited = visited;
		this.parseExternal = parseExternal;
	}

	public abstract void parse(ClassNode node);
}
