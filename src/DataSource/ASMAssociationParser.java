package DataSource;

import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.TreeSet;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import Domain.ClassDiagramArrow;

public class ASMAssociationParser extends IASMRelationshipParser {

	public ASMAssociationParser(Queue<String> classNames, List<String> visited, List<ClassDiagramArrow> arrows,
			boolean parseExternal) {
		super(classNames, visited, arrows, parseExternal);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void parse(ClassNode node) {
		HashMap<String, Integer> singles = new HashMap<>();
		TreeSet<String> multiples = new TreeSet<>();
		for (FieldNode field : node.fields) {
			ASMSignatureVisitor visitor = new ASMSignatureVisitor(Opcodes.ASM7);
			SignatureReader reader;
			if (field.signature != null) {
				reader = new SignatureReader(field.signature);
			} else {
				reader = new SignatureReader(field.desc);
			}
			reader.acceptType(visitor);
			for (TypeData t : visitor.getTypes()) {
				if (t.isMany) {
					multiples.add(t.name);
				} else if (singles.containsKey(t.name)) {
					singles.replace(t.name, singles.get(t.name) + 1);
				} else {
					singles.put(t.name, 1);
				}
			}
		}
		for (String s : multiples) {
			String name = s.replace('/', '.');
			if (!parseExternal && (name.startsWith(Constants.JAVA) || name.startsWith(Constants.ASM)
					|| name.startsWith(Constants.PLANTUML))) {
				continue;
			}
			arrows.add(new ClassDiagramArrow(Constants.ASSOCIATION, node.name.replace('/', '.'), name, Constants.MANY));
			classNames.add(s.replace('/', '.'));
		}
		for (String s : singles.keySet()) {
			if (!multiples.contains(s)) {
				String name = s.replace('/', '.');
				if (!parseExternal && (name.startsWith(Constants.JAVA) || name.startsWith(Constants.ASM)
						|| name.startsWith(Constants.PLANTUML))) {
					continue;
				}
				arrows.add(
						new ClassDiagramArrow(Constants.ASSOCIATION, node.name.replace('/', '.'), name, singles.get(s).toString()));
				classNames.add(s.replace('/', '.'));
			}
		}
	}
}
