package DataSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.signature.SignatureVisitor;
import org.objectweb.asm.tree.ClassNode;

public class ASMSignatureVisitor extends SignatureVisitor {

	private boolean nextIsMany;
	private List<TypeData> types;

	public ASMSignatureVisitor(int api) {
		super(api);
		nextIsMany = false;
		types = new ArrayList<>();
	}

	public SignatureVisitor visitArrayType() {
		nextIsMany = true;
		return this;
	}

	public void visitClassType(String name) {
		try {
			if (isCollection(name.replace('/', '.'))) {
				types.add(new TypeData(name, nextIsMany));
				nextIsMany = true;
			} else {
				types.add(new TypeData(name, nextIsMany));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<TypeData> getTypes() {
		return types;
	}

	public static boolean isCollection(String name) throws IOException {
		ClassReader reader = new ClassReader(name);
		ClassNode classNode = new ClassNode();
		reader.accept(classNode, ClassReader.EXPAND_FRAMES);
		if (classNode.superName != null) {
			if (classNode.superName.replace('/', '.').equals(Constants.COLLECTION)
					|| classNode.superName.replace('/', '.').equals(Constants.MAP)
					|| isCollection(classNode.superName.replace('/', '.'))) {
				return true;
			}
		}
		for (String s : classNode.interfaces) {
			if (s.replace('/', '.').equals(Constants.MAP) || s.replace('/', '.').equals(Constants.COLLECTION)
					|| isCollection(s.replace('/', '.'))) {
				return true;
			}
		}
		return false;
	}

}
