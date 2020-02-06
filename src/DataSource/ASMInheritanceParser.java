package DataSource;

import java.util.List;
import java.util.Queue;

import org.objectweb.asm.tree.ClassNode;

import Domain.ClassDiagramArrow;

public class ASMInheritanceParser extends IASMRelationshipParser {

	public ASMInheritanceParser(Queue<String> classNames, List<String> visited, List<ClassDiagramArrow> arrows,
			boolean parseExternal) {
		super(classNames, visited, arrows, parseExternal);
	}

	@Override
	public void parse(ClassNode node) {
		if (node.superName != null) {
			String name = node.superName.replace('/', '.');
			if (!parseExternal && (name.startsWith(Constants.JAVA) || name.startsWith(Constants.ASM)
					|| name.startsWith(Constants.PLANTUML))) {
				return;
			}
			this.arrows.add(new ClassDiagramArrow(Constants.INHERITANCE, node.name.replace('/', '.'), name));
			classNames.add(name);
		}
	}

}
