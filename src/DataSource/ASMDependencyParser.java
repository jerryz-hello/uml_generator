package DataSource;

import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.TreeSet;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import Domain.ClassDiagramArrow;

public class ASMDependencyParser extends IASMRelationshipParser {

	public ASMDependencyParser(Queue<String> classNames, List<String> visited, List<ClassDiagramArrow> arrows,
			boolean parseExternal) {
		super(classNames, visited, arrows, parseExternal);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void parse(ClassNode node) {
		HashMap<String, Integer> singles = new HashMap<>();
		TreeSet<String> multiples = new TreeSet<>();
		TreeSet<String> definiteSingles = new TreeSet<>();
		for (MethodNode method : node.methods) {
			String returnType = Type.getReturnType(method.desc).getClassName();
			ASMSignatureVisitor returnTypeVisitor = new ASMSignatureVisitor(Opcodes.ASM7);
			SignatureReader returnTypeReader = new SignatureReader(method.desc);
			returnTypeReader.accept(returnTypeVisitor);
			for (TypeData t : returnTypeVisitor.getTypes()) {
				if (t.isMany) {
					multiples.add(t.name);
				} else if (singles.containsKey(t.name)) {
					singles.replace(t.name, singles.get(t.name) + 1);
				} else {
					singles.put(t.name, 1);
				}
			}
			InsnList instructions = method.instructions;
			for (int i = 0; i < instructions.size(); i++) {
				AbstractInsnNode insn = instructions.get(i);
				if (insn.getType() == AbstractInsnNode.METHOD_INSN) {
					ASMClassMethodVisitor methodVisitor = new ASMClassMethodVisitor(Opcodes.ASM7);
					insn.accept(methodVisitor);
					for (String s : methodVisitor.getTypes()) {
						definiteSingles.add(s);
					}
				}
			}
		}
		for (String s : multiples) {
			definiteSingles.remove(s);
			String name = s.replace('/', '.');
			if (!parseExternal && (name.startsWith(Constants.JAVA) || name.startsWith(Constants.ASM)
					|| name.startsWith(Constants.PLANTUML))) {
				continue;
			}
			classNames.add(name);
			ClassDiagramArrow newArrow = new ClassDiagramArrow(Constants.DEPENDENCY, node.name.replace('/', '.'), name, Constants.MANY);
			if (!containAssociation(newArrow)) {
				arrows.add(newArrow);
			}
		}
		for (String s : singles.keySet()) {
			if (!multiples.contains(s)) {
				definiteSingles.remove(s);
				String name = s.replace('/', '.');
				if (!parseExternal && (name.startsWith(Constants.JAVA) || name.startsWith(Constants.ASM)
						|| name.startsWith(Constants.PLANTUML))) {
					continue;
				}
				classNames.add(name);
				ClassDiagramArrow newArrow = new ClassDiagramArrow(Constants.DEPENDENCY, node.name.replace('/', '.'), name,
						singles.get(s).toString());
				if (!containAssociation(newArrow)) {
					arrows.add(newArrow);
				}
			}
		}
		for (String s : definiteSingles) {
			String name = s.replace('/', '.');
			if (!parseExternal && (name.startsWith(Constants.JAVA) || name.startsWith(Constants.ASM)
					|| name.startsWith(Constants.PLANTUML))) {
				continue;
			}
			classNames.add(name);
			ClassDiagramArrow newArrow = new ClassDiagramArrow(Constants.DEPENDENCY, node.name.replace('/', '.'), name);
			if (!containAssociation(newArrow)) {
				arrows.add(newArrow);
			}
		}
	}

	private boolean containAssociation(ClassDiagramArrow in) {
		ClassDiagramArrow testing = new ClassDiagramArrow("association", in.pointFrom, in.pointTo, in.cardinalityTo);
		testing.type = "association";
		for (ClassDiagramArrow origin : arrows) {
			if (origin.equals(testing)) {
				return true;
			}
		}
		return false;
	}

}
