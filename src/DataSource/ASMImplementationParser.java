package DataSource;

import java.util.List;
import java.util.Queue;

import org.objectweb.asm.tree.ClassNode;

import Domain.ClassDiagramArrow;

public class ASMImplementationParser extends IASMRelationshipParser {

	public ASMImplementationParser(Queue<String> classNames, List<String> visited, List<ClassDiagramArrow> arrows,
			boolean parseExternal) {
		super(classNames, visited, arrows, parseExternal);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void parse(ClassNode node) {
		if (node.interfaces.size() != 0) {
			for (String interfaceName : node.interfaces) {
				String name = interfaceName.replace('/', '.');
				if (!parseExternal && (name.startsWith(Constants.JAVA) || name.startsWith(Constants.ASM)
						|| name.startsWith(Constants.PLANTUML))) {
					continue;
				}
				arrows.add(new ClassDiagramArrow(Constants.IMPLEMENTATION, node.name.replace('/', '.'), name));
				classNames.add(name);
			}
		}
	}

}
